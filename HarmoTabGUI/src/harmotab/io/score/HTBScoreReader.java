/**
 * This file is part of HarmoTab.
 *
 * @copyright Copyright (c) 2011 HarmoTab
 * @license GPL-3.0
 * 
 * HarmoTab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *   
 * HarmoTab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with HarmoTab.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author E. Revert (erevert@harmotab.com)
 */

package harmotab.io.score;

import harmotab.core.*;
import harmotab.element.*;
import harmotab.harmonica.Harmonica;
import harmotab.harmonica.TabModelController;
import harmotab.throwables.*;
import harmotab.track.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;


/**
 * Lecteur de fichier HTB
 */
public class HTBScoreReader extends ScoreReader {

	//
	// Constructeur
	//
	
	public HTBScoreReader(Score score, String path) {
		super(score, path);
	}

	
	//
	// Lecture
	//
	
	private static final int SCORE_TITLE = 0;
	private static final int SCORE_AUTHOR = 1;
	private static final int SCORE_COMMENT = 2;
	private static final int SCORE_HARMO = 3;
	private static final int SCORE_INFO_SUP = 4;
	private static final int SCORE_TEMPO = 5;
	private static final int SCORE_X_WIDTH = 6;
	
	private static final int NOTE_TYPE = 0;
	private static final int NOTE_HEIGHT = 1;
	private static final int NOTE_HOLE = 2;
	private static final int NOTE_DIRECTION = 3;
	private static final int NOTE_ARM = 4;
	private static final int NOTE_DOTED = 5;
	private static final int NOTE_REST = 6;
	private static final int NOTE_TRIPLET = 7;
	private static final int NOTE_TXT = 8; 
	private static final int NOTE_EFFECT = 9;
	private static final int NOTE_BAR = 10;
	private static final int NOTE_CHORD = 11;
	private static final int NOTE_LINKED = 12;
	
	private static final int KEY_TYPE = 0;	// = N_T => 10
	private static final int KEY_NUMBER = 1;
	private static final int KEY_REFERENCE = 2; 
	private static final int KEY_ARM = 3;
	
	private static final int FIGURE_WHOLE = 0;
	private static final int FIGURE_HALF = 1;
	private static final int FIGURE_QUARTER = 2;
	private static final int FIGURE_EIGHTH = 3;
	private static final int FIGURE_SIXTEENTH = 4;
	private static final int FIGURE_APOGIATURE = 5;
	private static final int FIGURE_SIGN = 10;
	
	private static final int DIRECTION_UNDEFINED = 0;
	private static final int DIRECTION_BLOW = 1;
	private static final int DIRECTION_DRAW = 2;
	
	private static final int ARM_NONE = 0;
	private static final int ARM_SHARP = 1;
	private static final int ARM_FLAT = 2;
	private static final int ARM_NAT = 3;
	
	private static final int EFFECT_NONE = 0;
	private static final int EFFECT_BEND = 1;
	private static final int EFFECT_WAHWAH = 2;
	private static final int EFFECT_SLIDE = 3;
	
	private static final int BAR_NONE = 0;
	private static final int BAR_DEFAULT = 1;
	private static final int BAR_START = 2;
	private static final int BAR_END = 3;
	private static final int BAR_START_WITH_REP = 4;
	private static final int BAR_START_END = 5;
	
	
	/**
	 * Lancement de la lecture
	 * @throws IOException 
	 */
	@Override
	protected void read(Score score, File file) throws IOException {
		try {
			// Préparation
			m_htTrack = new HarmoTabTrack(score);
			m_accTrack = new AccompanimentTrack(score, m_htTrack);
			m_lyricsTrack = new LyricsTrack(score, m_htTrack);
			
			m_currentTime = 0;
			m_prevChordStartTime = 0;
			m_prevChord = null;
			m_prevLyricsStartTime = 0;
			m_prevLyrics = null;
			m_implicitAlteration = new byte[Height.MAX_VALUE];
			m_currentKeySignature = null;
			m_currentTimeSignature = null;
			m_barToAdd = null;
			
			// Ouverture du fichier
			InputStream input = new FileInputStream(file);
			Reader reader = new InputStreamReader(input);
			
			// Lecture du fichier, découpage élément par élément
			Scanner scanner = new Scanner(reader);
			scanner.useDelimiter("\'a7");
						
			int index = 0;
			while (scanner.hasNext()) {
				try {
					// Le premier élément est le header de la partition
					if (index == 0)	{
						extractScoreProperties(scanner.next());
					}
					// Tous les autres éléments sont des notes
					else {
						extractNote(scanner.next());
						manageBars();
					}
					index++;
				}
				catch (Throwable exception) {
					exception.printStackTrace();
				}
			}
			
			// Dernier accord
			if (m_prevChord != null)
				m_prevChord.setCustomDuration(m_currentTime - m_prevChordStartTime);
			// Dernier texte
			if (m_prevLyrics != null)
				m_prevLyrics.setDurationObject(new Duration(m_currentTime - m_prevLyricsStartTime));
						
			// Fermeture du fichier
			input.close();
			
			// Compl�te le mod�le de tablatures de la piste HarmoTab
			TabModelController tabModelController = new TabModelController(m_htTrack.getTabModel());
			tabModelController.populateFromHarmoTabTrack(m_htTrack);
			
			// Insertion des pistes dans la partition
			m_score.addTrack(m_accTrack);
			m_score.addTrack(m_htTrack);
			m_score.addTrack(m_lyricsTrack);
			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	
	/**
	 * Extraction des propriétés de la partition
	 */
	private void extractScoreProperties(String input) {
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter("\'a4");
		
		int index = 0;		
		while (scanner.hasNext()) {
			String field = scanner.next();
			field = field.substring(0, field.length()-1);
			
			switch (index) {
				case SCORE_TITLE:		
					m_score.setTitle(decodeRtfString(field.split("fs17 ")[1]));
					break;
				case SCORE_AUTHOR:
					m_score.setSongwriter(decodeRtfString(field));
					break;
				case SCORE_COMMENT:
					m_score.setComment(decodeRtfString(field));
					break;
				case SCORE_HARMO:
					m_htTrack.setHarmonica(new Harmonica());
					break;
				case SCORE_INFO_SUP:
					m_score.setDescription(decodeRtfString(field));
					break;
				case SCORE_TEMPO:
					m_score.setTempo(Integer.parseInt(field));
					break;
				case SCORE_X_WIDTH:
					/* Pas de prise en compte de LargeurX */
					break;
			}
			index++;
		}
	}
		
	
	/**
	 * Extraction d'un élément
	 */
	private boolean extractNote(String input) {
		HarmoTabElement htElement = null;
		
		if (input.startsWith(FIGURE_SIGN + ""))
			return extractKey(input);
		
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter("\'a4");

		try {
			int index = 0;
			while (scanner.hasNext()) {
				String field = scanner.next();
				field = field.substring(0, field.length()-1);
							
				if (field.length() > 0) {
					switch (index) {
					
						case NOTE_TYPE: {
							int type = Integer.parseInt(field);
							htElement = new HarmoTabElement();
							Figure figure = null;
							switch (type) {
								case FIGURE_WHOLE:		figure = new Figure(Figure.WHOLE);			break;
								case FIGURE_HALF:		figure = new Figure(Figure.HALF);			break;
								case FIGURE_QUARTER:	figure = new Figure(Figure.QUARTER);		break;
								case FIGURE_EIGHTH:		figure = new Figure(Figure.EIGHTH);			break;
								case FIGURE_SIXTEENTH:	figure = new Figure(Figure.SIXTEENTH);		break;
								case FIGURE_APOGIATURE:	figure = new Figure(Figure.APPOGIATURE);	break;
								default: 			throw new UnhandledCaseError("Unhandled note type '#" + type + "'");
							}
							htElement.setFigure(figure);
						} break;
						
						case NOTE_HEIGHT: {
							int h = 23 - Integer.parseInt(field);
							byte note = (byte) (h % Height.NUMBER_OF_NOTES_PER_OCTAVE);
							int octave = (h / Height.NUMBER_OF_NOTES_PER_OCTAVE) + 3;
							htElement.setHeight(new Height(note, octave));
						} break;
						
						case NOTE_HOLE: {
							int hole = Integer.parseInt(field);
							Tab tab = new Tab(hole);
							htElement.setTab(tab);
						} break;
						
						case NOTE_DIRECTION: {
							Tab tab = htElement.getTab();
							int direction = Integer.parseInt(field);
							switch (direction) {
								case DIRECTION_BLOW:		tab.setDirection(Tab.BLOW);			break;
								case DIRECTION_DRAW:		tab.setDirection(Tab.DRAW);			break;
								case DIRECTION_UNDEFINED:	tab.setDirection(Tab.UNDEFINED);	break;
								default:	throw new UnhandledCaseError("Unhandled direction '#" + direction + "'");
							}
							htElement.setTab(tab);
						} break;
						
						case NOTE_ARM: {
							int arm = Integer.parseInt(field);
							switch (arm) {
								case ARM_NONE:		break;
								case ARM_SHARP:		htElement.getHeight().setAlteration(Height.SHARP);		break;
								case ARM_FLAT:		htElement.getHeight().setAlteration(Height.FLAT);		break;
								case ARM_NAT:		htElement.getHeight().setAlteration(Height.NATURAL);		break;
								default:			throw new UnhandledCaseError("Unhandled arm '#" + arm + "'");
							}
						} break;
							
						case NOTE_DOTED: {
							htElement.getFigure().setDotted(parseBoolean(field));
						} break;
							
						case NOTE_REST: {
							htElement.setRest(parseBoolean(field));
						} break;
							
						case NOTE_TRIPLET: {
							htElement.getFigure().setTriplet(parseBoolean(field));
						} break;
							
						case NOTE_TXT: {
							String text = decodeRtfString(field.trim());
							if (!text.equals("")) {
								Lyrics lyrics = new Lyrics(text);
								
								// Texte pr�c�dent, met � jour sa dur�e
								if (m_prevLyrics != null) {
									m_prevLyrics.setDurationObject(new Duration(m_currentTime - m_prevLyricsStartTime));
								}
								else {
									// Pas de texte pr�c�dent, ajoute un silence en d�but de piste si besoins
									if (m_currentTime != 0)
										m_lyricsTrack.add(new Silence(m_currentTime));
								}
								// Prend en compte le texte courant
								m_lyricsTrack.add(lyrics);
								m_prevLyricsStartTime = m_currentTime;
								m_prevLyrics = lyrics;
							}
														
						} break;
						
						case NOTE_EFFECT: {
							int effect = Integer.parseInt(field);
							switch (effect) {
								case EFFECT_NONE:	break;
								case EFFECT_BEND:	htElement.getTab().setBend(Tab.HALF_BEND);					break;
								case EFFECT_WAHWAH:	htElement.getTab().setEffect(new Effect(Effect.WAHWAH)); 	break;
								case EFFECT_SLIDE:	htElement.getTab().setEffect(new Effect(Effect.SLIDE)); 	break;
								default:	throw new UnhandledCaseError("Unhandled effect '#" + effect + "'");
							}
						} break;
						
						case NOTE_BAR: {
							Bar barElement = new Bar();
							int bar = Integer.parseInt(field) % 10;
							int repeats = (Integer.parseInt(field) / 10) + 1;
							if (bar != BAR_NONE) {
								m_barToAdd = barElement;
								switch (bar) {
									case BAR_DEFAULT:
										break;
									case BAR_START:
									case BAR_START_WITH_REP:
										m_barToAdd.setRepeatAttribute(new RepeatAttribute(true, false));
										break;
									case BAR_END:
										m_barToAdd.setRepeatAttribute(new RepeatAttribute(false, true, repeats));
										break;
									case BAR_START_END:
										m_barToAdd.setRepeatAttribute(new RepeatAttribute(true, true, repeats));
										break;
									default:
										throw new UnhandledCaseError("Unhandled bar type '#" + bar + "'");
								}
								resetImplicitAlterations();
							}
						} break;
						
						case NOTE_CHORD: {
							Accompaniment acc = extractChord(field);
							if (acc != null) {
								// Accord pr�c�dent, met � jour sa dur�e
								if (m_prevChord != null) {
									m_prevChord.setCustomDuration(m_currentTime - m_prevChordStartTime);
								}
								else {
								// Pas d'accord pr�c�dent, ajoute un silence en d�but de piste si besoins 
									if (m_currentTime != 0) {
										m_accTrack.add(0, new Silence(m_currentTime));
									}
								}
								// Prend en compte l'accord courant
								m_prevChordStartTime = m_currentTime;
								m_prevChord = acc;
							}
						} break;
						
						case NOTE_LINKED: {
							htElement.setTied(parseBoolean(field));
						} break;
						
					} // switch
				} // if
				
				index++;
				
			} // while
			
			if (index <= 1)
				return false;
			
			// Gestion des altérations implicites
			byte alteration = htElement.getHeight().getAlteration();
			int noteSoundId = htElement.getHeight().getUnalteredSoundId();
			byte implicitAlteration = m_implicitAlteration[noteSoundId];
			if (alteration == Height.NATURAL && implicitAlteration != Height.NATURAL)
				htElement.getHeight().setAlteration(implicitAlteration);
			else
				m_implicitAlteration[noteSoundId] = alteration;
			
			// Ajout de l'élément à la piste
			m_htTrack.add(htElement);
						
		} // try
		catch (NumberFormatException e) {
			return false;
		}
		
		m_currentTime += htElement.getDuration();
		m_barCurrentTime += htElement.getDuration();
		return true;
	}
	
	
	/**
	 * Extraction d'un acc
	 */
	private Accompaniment extractChord(String input) {
		Accompaniment acc = new Accompaniment();
		
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter("%");
		
		try {
			int index = 0;
			while (scanner.hasNext()) {
				String field = scanner.next();
	
				// Index 0 -> nom de l'acc
				if (index == 0)
					acc.setChord(new Chord(field));
				// Notes
				else if (field.length() > 0) {
					int nid = Integer.parseInt(field, 0x10);
					acc.getChord().addHeight(new Height(nid));
				}
				index++;
			}
			
			acc.setCustomDuration(new Duration(1.0f));
			m_accTrack.add(acc);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		return acc;
	}
	
	
	/**
	 * Extraction d'une clé.
	 */
	private boolean extractKey(String input) {
		Key key = new Key();
		TimeSignature timeSignature = new TimeSignature();
		KeySignature keySignature = new KeySignature();
		
		Scanner scanner = new Scanner(input);
		scanner.useDelimiter("\'a4");
		
		int index = 0;
		while (scanner.hasNext()) {
			String field = scanner.next();
			field = field.substring(0, field.length()-1);
			
			switch (index) {
				case KEY_TYPE:
					break;
				case KEY_NUMBER:
					timeSignature.setNumber(Byte.parseByte(field));
					break;
				case KEY_REFERENCE:
					timeSignature.setReference(Byte.parseByte(field));
					break; 
				case KEY_ARM:
					byte arm = 0;
					if (field.length() == 7) {
						while (arm < 7 && field.charAt(arm) == '1')
							arm++;
						if (arm == 0)
							while (arm > -7 && field.charAt(-arm) == '2')
								arm--;
					}
					keySignature.setIndex(arm);
					break;
			}
			index++;
		}

		Bar bar = new Bar(key, keySignature, timeSignature, new RepeatAttribute());
		m_currentKeySignature = keySignature;
		m_currentTimeSignature = timeSignature;
		m_htTrack.add(bar);
		resetImplicitAlterations();
		return true;
	}
	
	
	/**
	 * Gestion des altérations
	 */
	private void resetImplicitAlterations() {
		// Initialisation des alt�rations en fonction de l'armure
		if (m_currentKeySignature != null) {
			int keySignatureValue = m_currentKeySignature.getValue();
			// Remise à zéro
			for (int n = 0; n < Height.MAX_VALUE; n++)
				m_implicitAlteration[n] = Height.NATURAL;
			// Ajout de l'armure
			for (int i = 1; i < KeySignature.MAX_KEY_SIGNATURE; i++) {
				// Dièses
				if (i <= keySignatureValue) {
					byte armNote = KeySignature.SHARP_ORDER[i].getNote();
					for (int n = Height.MIN_VALUE; n < Height.MAX_VALUE; n++) {
						Height height = new Height(n);
						if (height.getAlteration() == Height.NATURAL && height.getNote() == armNote) {
							m_implicitAlteration[n] = Height.SHARP;
						}
					}
				}
				// Bémols
				else if (-i >= keySignatureValue) {
					byte armNote = KeySignature.FLAT_ORDER[i].getNote();
					for (int n = Height.MIN_VALUE; n < Height.MAX_VALUE; n++) {
						Height height = new Height(n);
						if (height.getAlteration() == Height.NATURAL && height.getNote() == armNote) {
							m_implicitAlteration[n] = Height.FLAT;
						}
					}					
				}
			}
		}
	}
	
	
	/**
	 * Gestion des barres de mesures
	 */
	private void manageBars() {
		// Ajout des barre de mesure explicites
		if (m_barToAdd != null) {
			m_htTrack.add(m_barToAdd);
			m_barCurrentTime = 0;
			m_barToAdd = null;
		}
		
		// Ajout des barres de mesure automatiques
		if (m_currentTimeSignature != null && m_barCurrentTime >= m_currentTimeSignature.getTimesPerBar()) {
			m_barCurrentTime = 0;
			resetImplicitAlterations();
		}

	}

	
	/**
	 * Lecture de la valeur d'un booléen.
	 */
	private boolean parseBoolean(String str) {
		if (Boolean.parseBoolean(str))
			return true;
		if (str.equals("Vrai") || str.equals("vrai"))
			return true;
		return false;
	}
	
	
	/**
	 * Remplace les caract�res sp�ciaux encod�s RTF de la cha�ne en param�tre 
	 * par les caract�res correspondant.
	 * Les caract�res sp�ciaux sont encod�s sous la forma \'xx avec xx le code
	 * du carac�re en hexa.
	 */
	private String decodeRtfString(String str) {
		String result = new String(str);
		int pos = result.indexOf("\\'", 0);
		while (pos != -1) {
			String valStr = result.substring(pos + 2, pos + 4);
			char val = (char) Integer.parseInt(valStr, 0x10);
			result = result.replaceAll("\\\\'" + valStr, val + "");
			pos = result.indexOf("\\'", pos + 1);
		}
		return result;
	}
	
	
	// 
	// Attributs
	// 
	
	private HarmoTabTrack m_htTrack = null;
	private AccompanimentTrack m_accTrack = null;
	private LyricsTrack m_lyricsTrack = null;
	
	private float m_currentTime = 0;
	private float m_prevChordStartTime = 0;
	private Accompaniment m_prevChord = null;
	private float m_prevLyricsStartTime = 0;
	private Lyrics m_prevLyrics = null;
	private byte[] m_implicitAlteration;
	private KeySignature m_currentKeySignature = null;
	private TimeSignature m_currentTimeSignature = null;
	private float m_barCurrentTime = 0;
	private Bar m_barToAdd = null;
	
}
