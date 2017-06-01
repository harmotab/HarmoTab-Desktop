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

package harmotab.core;

import harmotab.core.undo.RestoreCommand;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.*;


/**
 * Mod�le d'une hauteur de son
 */
public class Height extends HarmoTabObject implements Cloneable {
	public static final String HEIGHT_TYPESTR = "height";
	
	public static final String VALUE_ATTR = "value";
	public static final String NOTE_ATTR = "note";
	public static final String OCTAVE_ATTR = "octave";
	public static final String ALTERATION_ATTR = "alteration";

	public final static int MIN_VALUE = 36;//40; 	// Min MIDI = 0
	public final static int MAX_VALUE = 84;//78; 	// Max MIDI = 128

	public final static byte NATURAL = 0;
	public final static byte SHARP = 1;
	public final static byte FLAT = 2;
	public final static int ALTERATIONS_NUMBER = 3;
	public final static byte DEFAULT_ALTERATION = NATURAL;
	
	public final static int MIN_OCTAVE = 3;
	public final static int MAX_OCTAVE = 6;
	public final static int OCTAVE_EXTREMUM = 10;
	public final static int DEFAULT_OCTAVE = 5;
	
	public final static byte C = 0;
	public final static byte D = 1;
	public final static byte E = 2;
	public final static byte F = 3;
	public final static byte G = 4;
	public final static byte A = 5;
	public final static byte B = 6;
	public final static byte NUMBER_OF_NOTES_PER_OCTAVE = 7;
	public final static byte NUMBER_OF_ALTERED_NOTES_PER_OCTAVE = 12;	// C - C# - D - D# - E - F - F# - G - G# - A - Bb - B

	public final static byte DEFAULT_NOTE = C;
	
	
	
	//
	// Constructeurs
	//
	
	public Height() {
		setNote(DEFAULT_NOTE);
		setOctave(DEFAULT_OCTAVE);
		setAlteration(DEFAULT_ALTERATION);
	}
	
	public Height(byte note) {
		setNote(note);
		setOctave(DEFAULT_OCTAVE);
		setAlteration(DEFAULT_ALTERATION);
	}
	
	public Height(byte note, int octave) {
		setNote(note);
		setOctave(octave);
		setAlteration(DEFAULT_ALTERATION);
	}

	public Height(byte note, int octave, byte alteration) {
		setNote(note);
		setOctave(octave);
		setAlteration(alteration);
	}
	
	public Height(int soundId) {
		if (soundId < MIN_VALUE || soundId > MAX_VALUE)
			throw new OutOfBoundsError("Invalid sound identifier (" + soundId + ") !");
		
		setOctave(soundId / NUMBER_OF_ALTERED_NOTES_PER_OCTAVE);
		
		switch (soundId % NUMBER_OF_ALTERED_NOTES_PER_OCTAVE) {
			case 0:		setNote(C);		setAlteration(NATURAL);		break;
			case 1:		setNote(C);		setAlteration(SHARP);		break;
			case 2:		setNote(D);		setAlteration(NATURAL);		break;
			case 3:		setNote(E);		setAlteration(FLAT);		break;
			case 4:		setNote(E);		setAlteration(NATURAL);		break;
			case 5:		setNote(F);		setAlteration(NATURAL);		break;
			case 6:		setNote(F);		setAlteration(SHARP);		break;
			case 7:		setNote(G);		setAlteration(NATURAL);		break;
			case 8:		setNote(G);		setAlteration(SHARP);		break;
			case 9:		setNote(A);		setAlteration(NATURAL);		break;
			case 10:	setNote(B);		setAlteration(FLAT);		break;
			case 11:	setNote(B);		setAlteration(NATURAL);		break;
		}
	}
	
	public Height(String height) {
		setHeight(height);
	}
	
	public Height(Height height) {
		setNote(height.getNote());
		setOctave(height.getOctave());
		setAlteration(height.getAlteration());
	}
	
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new HeightRestoreCommand(this);
	}
	
	
	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		Height height = (Height) object;
		return 	(m_note == height.m_note) &&
				(m_octave == height.m_octave) &&
				(m_alteration == height.m_alteration);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setNote(byte note) {
		switch (note) {
			case C:	case D:	case E:	case F: case G: case A: case B:
				m_note = note;
				break;
			default:
				throw new OutOfSpecificationError("Invalid note identifier '#" + note + "'");
		}
		fireObjectChanged(NOTE_ATTR);
	}
	
	
	/**
	 * Affecte un note � partir d'une cha�ne de caract�res.
	 * Format accept�s : C - C# - C8 - C#8 - C12 - Cb12
	 */
	public void setNote(String name) {
		if (name.length() < 1 || name.length() > 2)
			throw new OutOfSpecificationError("Invalid note name.");
		
		// Note
		switch (name.charAt(0)) {
			case 'C':	setNote(C);		break;
			case 'D':	setNote(D);		break;
			case 'E':	setNote(E);		break;
			case 'F':	setNote(F);		break;
			case 'G':	setNote(G);		break;
			case 'A':	setNote(A);		break;
			case 'B':	setNote(B);		break;
			default: throw new OutOfSpecificationError("Unknown note name.");
		}
		
	}
	
	
	public byte getNote() {
		return m_note;
	}
	
	
	public String getNoteChar() {
		switch (m_note) {
			case C:	return "C";
			case D:	return "D";
			case E:	return "E";
			case F:	return "F";
			case G:	return "G";
			case A:	return "A";
			case B:	return "B";
			default: throw new UnhandledCaseError("Note height has no name !");
		}
	}
	
	
	public String getNoteName() {
		return getNoteChar() + getAlterationChar();
	}
	
	public static String[] getNotesName() {
		final String [] notes = {
			new String("C"), new String("C#"), new String("D"), new String("D#"), new String("E"), new String("F"), 
			new String("F#"), new String("G"), new String("G#"), new String("A"), new String("Bb"), new String("B")
		};
		return notes;
	}
	
	
	public int getOctave() {
		return m_octave;
	}
	
	public void setOctave(int octave) {
		if (octave < MIN_OCTAVE || octave > MAX_OCTAVE)
			throw new OutOfBoundsError("Invalid octave (" + octave + ").");
		
		m_octave = octave;
		fireObjectChanged(OCTAVE_ATTR);
	}
	
	
	public byte getAlteration() {
		return m_alteration;
	}
	
	public String getAlterationChar() {
		switch (m_alteration) {
			case FLAT:	return "b";
			case SHARP:	return "#";
			default:	return "";
		}
	}
	
	public void setAlteration(byte alteration) {
		switch (alteration) {
			case NATURAL: case FLAT: case SHARP:
				m_alteration = alteration;
				break;
			default: throw new OutOfSpecificationError("Invalid alteration identifier !");
		}
		fireObjectChanged(ALTERATION_ATTR);
	}
	
	public void setAlteration(String alteration) {
		if (alteration == null)
			throw new NullPointerException();
		if (alteration.equals(""))
			m_alteration = NATURAL;
		else if (alteration.equals("#"))
			m_alteration = SHARP;
		else if (alteration.equals("b"))
			m_alteration = FLAT;
		else
			throw new IllegalArgumentException("Unhandled alteration '" + alteration + "'");
	}
	
	//
	// M�thodes utilitaires
	//
	
	public void print() {
		System.out.println(getNoteName() + getOctave());
	}
	
	/**
	 * Affecte un note � partir d'une cha�ne de caract�res.
	 * Format accept�s : C - C# - C8 - C#8 - C12 - Cb12
	 */
	public void setHeight(String name) {
		if (name.length() < 1 || name.length() > 4)
			throw new OutOfSpecificationError("Empty note name.");
		
		// Note
		switch (name.charAt(0)) {
			case 'C':	setNote(C);		break;
			case 'D':	setNote(D);		break;
			case 'E':	setNote(E);		break;
			case 'F':	setNote(F);		break;
			case 'G':	setNote(G);		break;
			case 'A':	setNote(A);		break;
			case 'B':	setNote(B);		break;
			default: throw new OutOfSpecificationError("Unknown note name '" + name.charAt(0) + "'");
		}
		name = name.substring(1);
		
		// Alteration
		setAlteration(DEFAULT_ALTERATION);
		if (name.length() >= 1) {
			switch (name.charAt(0)) {
				case 'b':	
					setAlteration(FLAT);
					name = name.substring(1);
					break;
				case '#':	
					setAlteration(SHARP);
					name = name.substring(1);
					break;
			}
		}
		
		// Octave
		setOctave(DEFAULT_OCTAVE);
		if (name.length() >= 1) {
			String octave = "";
			for (int i = 0; i < name.length(); i++) {
				if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
					octave += name.charAt(0);
					name = name.substring(1);
				}
			}
			if (octave.length() > 0)
				setOctave(Integer.decode(octave));
		}
		
	}
	
	public int getOrdinate() {
		return (NUMBER_OF_ALTERED_NOTES_PER_OCTAVE*OCTAVE_EXTREMUM)-(m_note+NUMBER_OF_NOTES_PER_OCTAVE*m_octave);
	}

	
	/**
	 * Retourne l'identifiant de la hauteur sans prise en compte de l'octave 
	 * et avec prise en compte de l'alt�ration.
	 * => C4 == C5
	 */
	public int getAlteredNoteId() {
		int value = 0;
		
		switch (m_note) {
			case C:		value += 0;		break;
			case D:		value += 2;		break;
			case E:		value += 4;		break;
			case F:		value += 5;		break;
			case G:		value += 7;		break;
			case A:		value += 9;		break;
			case B:		value += 11;	break;
		}
		
		switch (m_alteration) {
			case SHARP:	value += 1;	break;
			case FLAT:	value -= 1;	break;
		}
		
		if (value == -1)
			value = 11;
		if (value == 12)
			value = 0;
		
		return value;
	}
	
	
	/**
	 * Retourne l'identifiant de la hauteur tenant compte de sa note, octave et 
	 * alt�ration.
	 * => C#5 == Db5
	 */
	public int getSoundId() {
		int value = getUnalteredSoundId();
		if (m_alteration == SHARP)
			value += 1;
		if (m_alteration == FLAT)
			value -= 1;
		return value;
	}
	
	/**
	 * Retourne l'identifiant de la hauteur tenant compte de sa note et octave
	 * mais pas de son alt�ration.
	 * => C#5 == C5 != Db5
	 */
	public int getUnalteredSoundId() {
		int value = m_octave * NUMBER_OF_ALTERED_NOTES_PER_OCTAVE;
		
		switch (m_note) {
			case C:		value += 0;		break;
			case D:		value += 2;		break;
			case E:		value += 4;		break;
			case F:		value += 5;		break;
			case G:		value += 7;		break;
			case A:		value += 9;		break;
			case B:		value += 11;	break;
		}
		
		return value;
	}
	
	
	public void moveUp() {
		if (getSoundId() >= MAX_VALUE-1)
			return;
		m_note += 1;
		if (m_note >= NUMBER_OF_NOTES_PER_OCTAVE) {
			m_note = 0;
			m_octave += 1;
		}
		if (m_octave > MAX_OCTAVE) {
			m_octave -= 1;
			m_note = NUMBER_OF_NOTES_PER_OCTAVE-1;
			fireObjectChanged(OCTAVE_ATTR);
		}
		
		fireObjectChanged(NOTE_ATTR);
	}
	
	public void moveDown() {
		if (getSoundId() <= MIN_VALUE)
			return;
		m_note -= 1;
		if (m_note < 0) {
			m_octave -= 1;
			m_note = NUMBER_OF_NOTES_PER_OCTAVE-1;
		}
		if (m_octave < 0) {
			m_note = 0;
			m_octave += 1;
			fireObjectChanged(OCTAVE_ATTR);
		}
		fireObjectChanged(NOTE_ATTR);
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(HEIGHT_TYPESTR, hashCode());
		object.setAttribute(NOTE_ATTR, getNoteChar());
		object.setAttribute(OCTAVE_ATTR, getOctave()+"");
		if (getAlteration() != NATURAL)
			object.setAttribute(ALTERATION_ATTR, getAlterationChar());
		return object;
	}
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		String height = object.getAttribute(NOTE_ATTR);
		if (object.hasAttribute(ALTERATION_ATTR))
			height += object.getAttribute(ALTERATION_ATTR);
		if (object.hasAttribute(OCTAVE_ATTR))
			height += object.getAttribute(OCTAVE_ATTR);
		setHeight(height);
	}
	
	
	//
	// Attributs
	//

	protected byte m_note;
	protected int m_octave;
	protected byte m_alteration;
	
}


/**
 * Commande d'annulation de la modification d'une hauteur de son
 */
class HeightRestoreCommand extends Height implements RestoreCommand {
	
	public HeightRestoreCommand(Height saved) {
		m_saved = saved;
		m_note = saved.m_note;
		m_octave = saved.m_octave;
		m_alteration = saved.m_alteration;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_note != m_note)
			m_saved.setNote(m_note);
		if (m_saved.m_octave != m_octave)
			m_saved.setOctave(m_octave);
		if (m_saved.m_alteration != m_alteration)
			m_saved.setAlteration(m_alteration);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new HeightRestoreCommand(m_saved);
	}
	
	private Height m_saved;
}
