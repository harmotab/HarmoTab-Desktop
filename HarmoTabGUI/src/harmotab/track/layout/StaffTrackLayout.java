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

package harmotab.track.layout;

import java.util.*;

import harmotab.track.*;
import harmotab.renderer.*;
import harmotab.renderer.renderingelements.*;
import harmotab.core.*;
import harmotab.element.*;


/**
 * Positionne graphiquement les �l�ments d'une piste de type StaffTrackLayout.
 */
public class StaffTrackLayout extends TrackLayout {
	
	private final static int DEFAULT_TRACK_HEIGHT = 110;
	
	private final static int SILENCE_ORDINATE = new Height(Height.G, 4).getOrdinate();
	private final static int STAFF_ORDINATE = new Height(Height.F, 5).getOrdinate();
	private final static int KEY_SIGNATURE_ORDINATE = new Height(Height.G).getOrdinate();
	private final static int TIME_SIGNATURE_ORDINATE = new Height(Height.B, 4).getOrdinate();
	private final static int BAR_ORDINATE = new Height(Height.F, 5).getOrdinate();
	
	
	// 
	// Constructeur
	// 

	public StaffTrackLayout(Track track) {
		super(track);
	}
	
	
	/**
	 * Positionne les éléments de la piste.
	 * Le positionnement est enregistré dans la liste de positions en paramètre. 
	 */
	@Override
	public void processElementsPositionning(LocationList locations, int areaWidth, float scoreDuration) {
		
		// Initialisation
		m_locations.reset();
		m_trackId = getTrackId();
		m_areaWidth = areaWidth;
		m_currentBar = null;
		m_currentKey = null;
		m_currentKeySignature = null;
		m_currentTimeSignature = null;
		m_currentLine = 0;
		m_currentX = areaWidth;
		m_widthUnitFactor = 40;
		m_staffHeight = getTrackHeight();
		m_spacing = 4;
		m_shifting = (10*Height.NUMBER_OF_NOTES_PER_OCTAVE*m_spacing)+(0*m_spacing);
		m_notesOnLineCount = 0;
		m_barCurrentTime = 0;
		m_currentTime = 0;
		m_currentHoockedGroup = null;
		m_currentTripletGroup = null;
		m_previousNote = null;
		m_barNumber = -1;
		m_currentElementIndex = 0;
		m_implicitAlteration = new byte[Height.MAX_VALUE];
		m_staffY = STAFF_ORDINATE * m_spacing - m_shifting;
				
		// Ajout des éléments de la piste
		ListIterator<Element> i = m_track.listIterator();
		while (i.hasNext()) {
			m_currentElementIndex = i.nextIndex();
			TrackElement trackElement = (TrackElement) i.next();
									
			// Ajout de l'élément
			if (trackElement instanceof Note)
				addNote((Note) trackElement);
			else if (trackElement instanceof Bar)
				addBar((Bar) trackElement);
			else if (trackElement instanceof KeySignature)
				addKeySignature((KeySignature) trackElement);
			else
				addElement(trackElement);
			
			// Ajoute la durée de l'élément au temps courant
			m_currentTime += trackElement.getDuration();
			
			// Saut de ligne si nécessaire
			if (m_currentX + trackElement.getWidthUnit() * m_widthUnitFactor > m_areaWidth)
				lineFeed(false);
			
		}
		
		// Ajout de silences pour atteindre la fin de la partition
		fillTrackWithSilences(scoreDuration);
		
		// Finalise la dernière ligne
		lineFeed(true);
							
		// Ajout des éléments à la liste en paramètre
		locations.add(m_locations);

	}
	
	
	/**
	 * Retourne la hauteur d'une ligne de la piste.
	 */
	@Override
	public int getTrackHeight() {
		return DEFAULT_TRACK_HEIGHT;
	}
	
	
	/**
	 * Effectue les opérations réalisées lorsqu'une ligne est pleine.
	 * - Passage à la ligne suivante de tous les éléments de la mesure en cours,
	 * - ajout de la clé, de la signature et de la signature temporelle,
	 * - repositionnement des éléments de la ligne précédente pour qu'ils occupent toute la largeur de la ligne.
	 */
	private void lineFeed(boolean trackEnds) {
		int previousLineLastElementIndex = -1;
	
		//
		// Finalisation de la portée
		//
		LocationList lineChangeElements = new LocationList();
		
		if (m_currentLine > 0) {
			// Marque les éléments de la mesure courante pour qu'ils passent sur la nouvelle ligne
			if (m_locations.getSize() > 1) {
				ListIterator<LocationItem> it = m_locations.getListIterator(m_locations.getSize() - 1);
				LocationItem sameBarElement = it.next();
				if (sameBarElement.getElement() instanceof Bar) {
					// Le dernier élément de la ligne est déjà une barre de mesure
					previousLineLastElementIndex = it.previousIndex();
				} 
				else {
					// Recherche de début de la mesure (barre de mesure)
					while (!(sameBarElement.getElement() instanceof Bar) && it.hasPrevious())
						sameBarElement = it.previous();
					previousLineLastElementIndex = it.nextIndex();
					// Indique pour tous les éléments avant la barre qu'ils doivent être placés sur la nouvelle ligne
					if (sameBarElement.getElement() instanceof Bar) {
						it.next();
						while (it.hasNext())
							lineChangeElements.add(it.next());
					}
				}
			}

			// Pas encore de barre de mesure
			if( previousLineLastElementIndex == 0 )
				previousLineLastElementIndex = m_locations.getSize();
					
			// Ajout de la portée
			if (!trackEnds) {
				LocationItem staffLoc = new LocationItem(new Staff(), 
						m_areaWidth, m_staffY, 0, 0,							// poiX, poiY, X, Y 
						m_areaWidth, m_staffHeight,								// width, height
						m_trackId, m_currentLine, m_currentTime, m_spacing );	// track, line, time, extra
				m_locations.add(++previousLineLastElementIndex, staffLoc);
				staffLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			}
			
		}
				
		
		//
		// Ajout des éléments de début de ligne suivante si la partition n'est pas terminée
		//
		if (!trackEnds) {
			// Initialisation de la ligne
			m_currentLine++;
			m_currentX = 0;
			m_notesOnLineCount = 0;
					
			// Ajout de la clé de début de ligne
			if (m_currentKey != null) {
				LocationItem keyLoc = addElement(++previousLineLastElementIndex, m_currentKey,
						m_currentX, 0,										// x, y
						m_currentKey.getWidthUnit(), m_staffHeight,			// width, height
						m_currentKey.getOrdinate()*m_spacing - m_shifting,	// ordinate
						0);													// extra
				keyLoc.setParent(m_currentBar);
				keyLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			}
			
			// Ajout de l'armure de début de ligne
			if (m_currentKeySignature != null) {
				LocationItem keySignatureLoc = addElement(++previousLineLastElementIndex, m_currentKeySignature,
						m_currentX,	0,							// x, y
						m_currentKeySignature.getWidthUnit(),	// width 
						m_staffHeight, 							// height
						KEY_SIGNATURE_ORDINATE * m_spacing - m_shifting,	// ordinate
						m_spacing								// extra -> spacing
						);
				keySignatureLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			}
			
			// Ajout de la signature temporelle de début de ligne 
			if (m_currentTimeSignature != null) {
				int timeY = TIME_SIGNATURE_ORDINATE * m_spacing - m_shifting;
				if (m_currentLine == 1) {
					LocationItem timeSignatureLoc = addElement(++previousLineLastElementIndex, m_currentTimeSignature, 
							m_currentX, 0,											// x, y
							m_currentTimeSignature.getWidthUnit(), m_staffHeight,	// width, height 
							timeY,													// ordinate
							0);														// extra
					timeSignatureLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
				}
			}
			
			// Ajout d'une barre de mesure si la mesure courante est un d�but de phrase
			if (m_currentBar.getRepeatAttribute().isBeginning()) {
				// Indique que l'affichage pr�c�dent de la barre ne doit pas 
				// comprendre le d�but de phrase
				LocationItem prevBarItem = m_locations.get(m_currentBar);
				prevBarItem.setFlag(LocationItemFlag.IMPLICIT_PHRASE_START, true);
				// Ajoute une copie de la barre sur la nouvelle ligne
				LocationItem barLoc = addElement(m_currentBar,
						m_currentX, 0,								// X, Y
						m_currentBar.getWidthUnit(), m_staffHeight,	// width, height 
						BAR_ORDINATE * m_spacing - m_shifting,	// ordinate
						m_barNumber);								// extra -> num�ro de mesure
				barLoc.setFlag(LocationItemFlag.IMPLICIT_PHRASE_END, true);
			}
			
			// Ajout des éléments de la mesure courante à la nouvelle ligne pour ne pas 
			// qu'une mesure ne soit coupée par un saut de ligne
			if (lineChangeElements.getSize() > 0) {
				Iterator<LocationItem> it = lineChangeElements.getIterator();
				do {
					LocationItem item = it.next();
					item.m_line += 1;
					item.moveToX(m_currentX);
					m_currentX = item.m_x2;
				} while (it.hasNext());
			}

		}
		
		
		//
		// Ajustement de la position des éléments de la ligne précédente pour occuper tout l'espace en largeur
		//
		
		if (m_currentLine > 1 && !trackEnds) {
			ListIterator<LocationItem> it = m_locations.getListIterator(m_locations.getSize() - 1);
			// Recherche du dernier élément de la ligne
			LocationItem last = it.next();
			while (it.hasPrevious() && (last.getLine() >= m_currentLine || !(last.getElement() instanceof Bar)))
				last = it.previous();
			// Recherche du premier élément de la ligne
			LocationItem first = last;
			while (it.hasPrevious() && (!(first.getElement() instanceof Key)))
				first = it.previous();
			// Calcul du facteur de positionnement
			double firstX = first.m_x1;
			double lastX = last.m_poiX;
			double factor = ((double)m_areaWidth-firstX) / (lastX-firstX);
			// Repositionnement des éléments
			LocationItem item = null;
			while (it.hasNext() && item != last) {
				item = it.next();
				if (! (item.getElement() instanceof Staff)) {	//FIXME: patch pas très clean pour ne pas modifier le positionnement des mesures
					item.moveToX((int)((((double) item.m_x1 - firstX) * factor) + firstX));
					item.resize((int)((double) item.m_width*factor), item.m_height);
				}
			}
			// Calcul du point d'affichage de la barre de mesure de fin de ligne 
			// pour qu'il coincide exactement avec la fin de la portée
			if (last.getElement() instanceof Bar)
				last.m_poiX = last.m_x1 + last.m_width/2 + 1;
			else
				System.err.println("StaffTrackLayout::lineFeed: last.getElement() instanceof Bar failed (" + last.getElement() + ")");
		}
				
		
		//
		// Ajout d'une barre de mesure à la fin de la partition et réduit la taille de la mesure
		// si la dernière mesure est complète
		//
		if (trackEnds && m_locations.getSize() > 0 && m_currentTimeSignature != null) {
			LocationItem lastBarItem = null;
			
			// Effectue l'ajout uniquement si l'avant dernier élément est une note
			// (le dernier élément est la portée et l'avant dernier l'élément de zone vide)
			ListIterator<LocationItem> it = m_locations.getListIterator(m_locations.getSize() - 1);
			LocationItem last = it.next();
			if (last.getElement() instanceof Bar)
				lastBarItem = last;
			// et que la mesure courant est finie
			if (last.getElement() instanceof Note && m_barCurrentTime >= m_currentTimeSignature.getTimesPerBar()) {
				// Ajoute une barre de fin de partition
				m_currentElementIndex++;
				lastBarItem = addBar(new Bar());
				lastBarItem.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			}
			
			// Ajout d'un item pour indiquer la fin de la partition
			if (trackEnds) {
				// L'item n'est ajout� que s'il n'y a pas d�j� de EmptyArea dans les items
				if (! m_locations.hasElementOfType(Element.EMPTY_AREA)) {
					LocationItem emptyAreaLoc = new LocationItem( new EmptyArea(m_track),
							0, m_staffY, m_currentX,	0,					// poiX, poiY, X, Y
							m_areaWidth-m_currentX, m_staffHeight,			// width, height
							m_trackId, m_currentLine, m_currentTime, 0 );	// track, line, time, extra
					m_locations.add( emptyAreaLoc );
					emptyAreaLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
				}
			}
					
			// Ajout de la portée
			LocationItem staffLoc = new LocationItem(new Staff(), 
					lastBarItem != null ? lastBarItem.getPointOfInterestX() : m_areaWidth, m_staffY, 	// poiX, poiY 
					0, 0,	// X, Y 
					m_areaWidth, m_staffHeight,	// width, height
					m_trackId, m_currentLine, m_currentTime, m_spacing );	// track, line, extra
			m_locations.add(staffLoc);
			staffLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
		}
		
	}
	
	
	/**
	 * Ajoute des silences jusqu'à atteindre la fin de la piste.
	 */
	private void fillTrackWithSilences(float scoreDuration) {
		LocationItem emptyAreaLoc = null;
		
		// Ajoute des silences tant que la partition n'est pas remplie
		while (m_currentTime < scoreDuration) {
			
			// Ajoute un EmptyArea pour couvrir les notes temporaires ajout�es
			// si c'est le premier �l�ment temporaire de la ligne
			if (emptyAreaLoc == null) {
				emptyAreaLoc = new LocationItem(new EmptyArea(m_track),
						0, m_staffY, m_currentX, 0,						// poiX, poiY, X, Y 
						m_areaWidth-m_currentX, m_staffHeight,			// width, height
						m_trackId, m_currentLine, m_currentTime, 0 );	// track, line, time, extra
				m_locations.add(emptyAreaLoc);
				emptyAreaLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			}
			
			// Calcul le temps du prochain silence en fonction du temps restant
			// dans la mesure courant
			float barEndTime = m_currentTimeSignature.getTimesPerBar();
			float remainingTime = barEndTime - m_barCurrentTime;
			if (remainingTime == 0.0f)
				remainingTime = barEndTime;
			if (m_currentTime + remainingTime > scoreDuration)
				remainingTime = scoreDuration - m_currentTime;

			// Ajoute le silence
			Note rest = new Note(new Figure(remainingTime));
			rest.setRest(true);
			LocationItem item = addNote(rest);
			item.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			
			// Saut de ligne si nécessaire
			if (m_currentX + rest.getWidthUnit() * m_widthUnitFactor > m_areaWidth) {
				// Finalisation de la ligne
				lineFeed(false);
				// Ajout d'un EmptyArea pour couvrir la ligne
				// -3 pour que le EmptyArea soit avant les �l�ments ajout�s en d�but de ligne
				int index = m_locations.getSize() - 3;  
				emptyAreaLoc = new LocationItem(new EmptyArea(m_track),
						0, m_staffY, 0, 0,								// poiX, poiY, X, Y 
						m_areaWidth, m_staffHeight,						// width, height
						m_trackId, m_currentLine, m_currentTime, 0 );	// track, line, time, extra
				m_locations.add(index, emptyAreaLoc);
				emptyAreaLoc.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			}
			
			m_currentTime += rest.getDuration();
			
		}
	}
	
	
	/**
	 * Enregistre le positionnement d'un élément en prenant en compte sa position et ses dimensions.
	 */
	private LocationItem addElement(Element e, int x, int y, float widthUnit, int height, int extra) {
		int width = (int)(widthUnit * m_widthUnitFactor);
		LocationItem newItem = LocationItem.newFromArea(e, x, y, width, height, m_trackId, m_currentLine, m_currentTime, extra);
		newItem.m_elementIndex = m_currentElementIndex;
		m_locations.add(newItem);
		m_currentX = x + width;
		return newItem;
	}
	
	/**
	 * Enregistre le positionnement d'un élément en prenant en compte sa position, ses dimensions et sa hauteur.
	 */
	private LocationItem addElement(Element e, int x, int y, float widthUnit, int height, int ordinate, int extra) {
		int width = (int)(widthUnit * m_widthUnitFactor);
		LocationItem newItem = LocationItem.newFromOrdinate(e, x, y, width, height, ordinate, m_trackId, m_currentLine, m_currentTime, extra);
		newItem.m_elementIndex = m_currentElementIndex;
		m_locations.add(newItem);
		m_currentX = x + width;
		return newItem;
	}

	/**
	 * Enregistre le positionnement d'un élément en prenant en compte sa position, ses dimensions et sa hauteur
	 * à une position spécifiée dans la liste.
	 */
	private LocationItem addElement(int index, Element e, int x, int y, float widthUnit, int height, int ordinate, int extra) {
		int width = (int)(widthUnit * m_widthUnitFactor);
		LocationItem newItem = LocationItem.newFromOrdinate(e, x, y, width, height, ordinate, m_trackId, m_currentLine, m_currentTime, extra);
		newItem.m_elementIndex = m_currentElementIndex;
		m_locations.add(index, newItem);
		m_currentX = x + width;
		return newItem;
	}
	
	/**
	 * Positionne une note.
	 * Gère la hauteur de la note ainsi que son éventuelle apartenance à un groupement.
	 * Insère une barre de mesure avant la note si la mesure est terminée.
	 */
	private LocationItem addNote(Note note) {
		LocationItem noteItem = null;
		int ordinate = note.getHeight().getOrdinate();
		
		// Gestion des silences
		if (note.isRest()) {
			// fixe leur ordonnée
			ordinate = SILENCE_ORDINATE;
		}
		
		// Ajout d'un barre de mesure automatique avant la note si nécessaire
		if (m_currentTimeSignature != null && m_barCurrentTime >= m_currentTimeSignature.getTimesPerBar()) {
			LocationItem barItem = addBar(new Bar());
			barItem.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
		}
		
		// Insère un nouveau groupement de croches avant la note si elle est accrochable...
		if (note.isHookable()) {
			// et qu'aucun groupement de croche n'est en cours OU
			// que la note est la premi�re d'un groupement de triolets OU
			// que la note est la denri�re d'un groupement de triolets
			if (m_currentHoockedGroup == null || 
				(m_currentTripletGroup == null && note.getFigure().isTriplet()) ||
				(m_currentTripletGroup != null && !note.getFigure().isTriplet())
				) 
			{
				m_currentHoockedGroup = new HoockedNoteGroup();
				m_locations.add(LocationItem.newFromArea(m_currentHoockedGroup, m_currentX, 0, 0, 0, m_trackId, m_currentLine, m_currentTime, 0));
			}
		}
				
		// Positionnement de la note
		noteItem = addElement(note,
				m_currentX, 0,							// x, y
				note.getWidthUnit(), getTrackHeight(),	// width, height 
				ordinate*m_spacing - m_shifting,		// ordinate
				m_spacing);								// extra
		
		// Si l'élément est accrochable, l'ajoute à un groupe de croche
		if (note.isHookable()) {
			m_currentHoockedGroup.add(noteItem);
		}
		// Sinon, ferme le groupement de croche précédent
		else {
			m_currentHoockedGroup = null;
		}
		
		// Ajout d'un groupe de liaison avec la note précédente si la note 
		// courante est marquée comme liée
		if (note.isTied()) {
			if (m_previousNote != null) {
				TiedNoteGroup tiedNotes = new TiedNoteGroup();
				tiedNotes.add(m_previousNote);
				tiedNotes.add(noteItem);
				m_locations.addBefore(noteItem, LocationItem.newFromArea(tiedNotes,
					m_previousNote.getX1(), m_previousNote.getY1(), 	// x, y
					0, 0,												// width, height, 
					noteItem.getTrackId(), m_previousNote.getLine(),	// trackId, line
					-1, 0));		// time, extra
			}
			else {
				System.err.println("StaffTrackLayout::addNote: Note tied without previous note.");
			}
		}
		
		// Gestion des groupes de triolets
		// Si la note courante est un triolet
		if (note.getFigure().isTriplet()) {
			// Si aucun triolet n'est ouvert ou si la figure de la note ne 
			// correspond pas � celle du triolet en cours...
			if (m_currentTripletGroup == null || note.getFigure().getType() != m_currentTripletGroup.getTripletFigure().getType()) {
				//... ajoute la note au groupe courant
				m_currentTripletGroup = new TripletGroup();
				m_locations.add(LocationItem.newFromArea(m_currentTripletGroup, m_currentX, 0, 0, 0, m_trackId, m_currentLine, m_currentTime, 0));
			}
			// Ajoute la note courante au groupe de triolet
			m_currentTripletGroup.add(noteItem);
		}
		// Si la note courante n'est pas un triolet
		else {
			// ferme le triolet pr�c�dent s'il existe
			m_currentTripletGroup = null;
		}
		
		// Gestion des altérations
		if (!note.isRest()) {
			byte alteration = note.getHeight().getAlteration();
			int noteSoundId = note.getHeight().getUnalteredSoundId();
			byte implicitAlteration = m_implicitAlteration[noteSoundId];
			if (alteration != implicitAlteration) {
				noteItem.setFlag(LocationItemFlag.EXPLICIT_ALTERATION, true);
				m_implicitAlteration[noteSoundId] = alteration;
			}
		}
		
		// Prise en compte des temps forts pour le découpage des croches
		float nextBarTime = m_barCurrentTime + note.getDuration();
		
		if (m_currentTimeSignature != null) {
			float beatPeriod = m_currentTimeSignature.getTimesPerBeat();
			
			if (m_currentHoockedGroup != null && m_currentTripletGroup == null) {
				if (Math.floor(nextBarTime / beatPeriod) != Math.floor(m_barCurrentTime / beatPeriod) )
					m_currentHoockedGroup = null;
			}
			
			// Prise en compte des temps forts pour le d�coupage des triolets
			if (m_currentTripletGroup != null) {
				float fullTripletBeatPeriod = beatPeriod * m_currentTripletGroup.getTripletFigure().getDuration() * 2;
				if (m_currentTripletGroup.getGroupDuration() >= fullTripletBeatPeriod) {
					m_currentTripletGroup = null;
					m_currentHoockedGroup = null;
				}
			}
		}
		
		// Effectue les opérations de prise en compte de l'ajout de la note
		m_barCurrentTime = nextBarTime;
		m_notesOnLineCount++;
		m_previousNote = noteItem;
		
		return noteItem;
	}
	
	
	/**
	 * Positionnement d'une barre de mesure.
	 */
	private LocationItem addBar(Bar bar) {
		float barCurrentTime = m_barCurrentTime;
				
		// Prend en compte la barre comme étant la barre de début de la mesure courante
		m_currentBar = bar;
		m_barCurrentTime = 0;
		m_currentHoockedGroup = null;
		m_currentTripletGroup = null;
		m_barNumber++;

		// Si la nouvelle barre est une fin de phrase avec reprise, indique � la 
		// barre de d�but de phrase pr�c�dente que c'est le d�but d'une reprise
		if (bar.getRepeatAttribute().isEnd() && bar.getRepeatAttribute().getRepeatTimes() > 1) {
			ElementLocationIterator it = m_locations.getElementLocationIterator(m_locations.getSize()-1, Bar.class);
			LocationItem prevBarItem = null;
			boolean prevBarIsPhraseStart = false;
			do {
				prevBarItem = it.previous();
				if (prevBarItem != null) {
					Bar prevBar = (Bar) prevBarItem.getElement();
					if (prevBar.getRepeatAttribute().isBeginning() == true)
						prevBarIsPhraseStart = true;
				}
			}
			while (prevBarIsPhraseStart == false && prevBarItem != null);
			
			if (prevBarIsPhraseStart == true)
				prevBarItem.setFlag(LocationItemFlag.DRAW_REPEAT_SYMBOL, true);
		}
		
		// Nouvelle clé
		if (bar.getKey() != null)
			m_currentKey = bar.getKey();
		
		// Ajout de la barre
		// N'affiche pas la première barre de mesure de la ligne
		LocationItem newBarLocation = null;
		if (!(m_currentLine == 0 && m_notesOnLineCount == 0 && bar.getRepeatAttribute().isSingle() == true)) {
			newBarLocation = addElement(bar,
					m_currentX,			// X
					0,					// Y
					bar.getWidthUnit(),	// width 
					m_staffHeight, 		// height
					BAR_ORDINATE * m_spacing - m_shifting,	// ordinate
					m_barNumber			// extra -> num�ro de mesure
					);
			
			// Signale un probl�me sur la barre si elle ferme une mesure trop t�t ou trop tard
			// avec gestion des anacrouses (la premi�re mesure peut �tre incompl�te)
			float waitedTimesPerBar = m_currentTimeSignature.getTimesPerBar();
			if (barCurrentTime > waitedTimesPerBar + 0.001f) {
				newBarLocation.setFlag(LocationItemFlag.ERRORNOUS_ITEM, true);
				newBarLocation.setFlag(LocationItemFlag.BAR_EXCEEDED, true);
			}
			if (barCurrentTime < waitedTimesPerBar - 0.001f && m_barNumber != 1) {
				newBarLocation.setFlag(LocationItemFlag.ERRORNOUS_ITEM, true);
				newBarLocation.setFlag(LocationItemFlag.NOT_FILLED_BAR, true);
			}
		}
				
		// Nouvelle armure
		if (bar.getKeySignature() != null) {
			// N'affiche pas la première signature, qui est ensuite réaffichée automatiquement
			boolean isFirstKeySignature = m_currentKeySignature == null;
			m_currentKeySignature = bar.getKeySignature();
			if (!isFirstKeySignature) {
				addElement(m_currentKeySignature,
					m_currentX,			// X
					0,					// Y
					m_currentKeySignature.getWidthUnit(),	// width 
					m_staffHeight, 		// height
					KEY_SIGNATURE_ORDINATE * m_spacing - m_shifting,	// ordinate
					m_spacing			// extra -> spacing
					);
			}
		}
		
		// Nouvelle signature temporelle
		if (bar.getTimeSignature() != null) {
			m_currentTimeSignature = bar.getTimeSignature();
			int timeY = TIME_SIGNATURE_ORDINATE * m_spacing - m_shifting;
			if (m_currentLine == 1) {
				addElement(m_currentTimeSignature, 
						m_currentX, 0,											// x, y
						m_currentTimeSignature.getWidthUnit(), m_staffHeight,	// width, height 
						timeY,													// ordinate
						0);														// extra
			}
		}
		
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
		
		return newBarLocation;		
	}
	
	
	/**
	 * Positionnement de l'armure
	 */
	private LocationItem addKeySignature(KeySignature ks) {
		m_currentKeySignature = ks;
		return null;
	}
	
	
	/**
	 * Positionnement d'un élément quelconque.
	 */
	private LocationItem addElement(Element e) {
		return addElement(e,
				m_currentX,			// X
				0,					// Y
				e.getWidthUnit(),	// width 
				m_staffHeight, 		// height
				0					// extra
				);	
	}

	
	//
	// Attributs
	//

	private int m_trackId;
	private LocationList m_locations = new LocationList();
	private Bar m_currentBar;
	private Key m_currentKey;
	private KeySignature m_currentKeySignature;
	private TimeSignature m_currentTimeSignature;
	private int m_currentLine;
	private int m_currentX;
	private int m_widthUnitFactor;
	private int m_areaWidth;
	private int m_shifting;
	private int m_staffY;
	private int m_barNumber;
	protected int m_staffHeight;
	private int m_spacing;
	private int m_notesOnLineCount;
	private float m_barCurrentTime;
	private float m_currentTime;
	private int m_currentElementIndex;
	private HoockedNoteGroup m_currentHoockedGroup;
	private TripletGroup m_currentTripletGroup;
	private LocationItem m_previousNote;
	private byte[] m_implicitAlteration;
}
