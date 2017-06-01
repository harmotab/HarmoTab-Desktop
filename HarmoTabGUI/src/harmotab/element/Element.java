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

package harmotab.element;

import harmotab.core.*;
import harmotab.io.*;
import harmotab.throwables.*;


public abstract class Element extends HarmoTabObject implements Cloneable {	

	//
	// Identifiants des différents types d'éléments
	//
	
	public final static byte EMPTY_AREA = -1;
	public final static byte ELEMENT = 0;
	public final static byte TEXT_ELEMENT = 1;
	public final static byte NOTE = 2;
	public final static byte TAB = 3;
	public final static byte HARMOTAB = 4;
	public final static byte BAR = 5;
	public final static byte STAFF = 6;
	public final static byte KEY = 7;
	public final static byte KEY_SIGNATURE = 8;
	public final static byte TIME_SIGNATURE = 9;
	public final static byte GROUP = 10;
	public final static byte HOOCKED_NOTES = 11;
	public final static byte CHORD = 12;
	public final static byte ACCOMPANIMENT = 13;
	public final static byte TEMPO = 14;
	public final static byte TIED_NOTES = 15;
	public final static byte TRIPLET_GROUP = 16;
	public final static byte SILENCE = 17;
	public final static byte LYRICS = 18;
	public final static byte HARMONICA_PROPERTIES = 19;
	public final static byte TAB_AREA = 20;

	public final static String EMPTY_AREA_TYPESTR = "emptyArea";
	public final static String ELEMENT_TYPESTR = "element";
	public final static String TEXT_ELEMENT_TYPESTR = "textElement";
	public final static String NOTE_TYPESTR = "note";
	public final static String TAB_TYPESTR = "tab";
	public final static String HARMOTAB_TYPESTR = "harmotab";
	public final static String BAR_TYPESTR = "bar";
	public final static String STAFF_TYPESTR = "staff";
	public final static String KEY_TYPESTR = "key";
	public final static String KEY_SIGNATURE_TYPESTR = "keySignature";
	public final static String TIME_SIGNATURE_TYPESTR = "timeSignature";
	public final static String GROUP_TYPESTR = "group";
	public final static String HOOCKED_NOTES_TYPESTR = "hoockedNotes";
	public final static String CHORD_TYPESTR = "chord";
	public final static String ACCOMPANIMENT_TYPESTR = "accompaniment";
	public final static String TEMPO_TYPESTR = "tempo";
	public final static String TIED_NOTES_TYPESTR = "tiedNotes";
	public final static String TRIPLET_GROUP_TYPESTR = "tripletGroup";
	public final static String SILENCE_TYPESTR = "silence";
	public final static String LYRICS_TYPESTR = "lyrics";
	
	
	//
	// Constructeurs
	//
	
	public Element(byte type) {
		m_type = type;
	}
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	public byte getType() {
		return m_type;
	}
	
	public float getWidthUnit() {
		return 1.0f;
	}
	
	public float getDuration() {
		return 0;
	}
	
	
	public static String getTypeName(int type) {
		switch (type) {
			case EMPTY_AREA:		return EMPTY_AREA_TYPESTR;
			case ELEMENT:			return ELEMENT_TYPESTR;
			case TEXT_ELEMENT:		return TEXT_ELEMENT_TYPESTR;
			case NOTE:				return NOTE_TYPESTR;
			case TAB:				return TAB_TYPESTR;
			case HARMOTAB:			return HARMOTAB_TYPESTR;
			case BAR:				return BAR_TYPESTR;
			case STAFF:				return STAFF_TYPESTR;
			case KEY:				return KEY_TYPESTR;
			case KEY_SIGNATURE:		return KEY_SIGNATURE_TYPESTR;
			case TIME_SIGNATURE:	return TIME_SIGNATURE_TYPESTR;
			case GROUP:				return GROUP_TYPESTR;
			case HOOCKED_NOTES:		return HOOCKED_NOTES_TYPESTR;
			case CHORD:				return CHORD_TYPESTR;
			case ACCOMPANIMENT:		return ACCOMPANIMENT_TYPESTR;
			case TEMPO:				return TEMPO_TYPESTR;
			case SILENCE:			return SILENCE_TYPESTR;
			case LYRICS:			return LYRICS_TYPESTR;
			default:
				throw new UnhandledCaseError("Cannot retrieve element type name (#" + type + ")");
		}
	}
	
	public String getTypeName() {
		return Element.getTypeName(m_type);
	}
	
	
	//
	// Gestion des sous-�l�ment
	//
	
	public boolean contains(Element subElement) {
		return false;
	}
	
	public boolean delete(Element subElement) {
		return false;
	}

	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(getTypeName(), hashCode());
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		System.err.println("Element::deserializer: Method should be overloaded for " + this);
	}
	

	//
	// Attributs
	//
	
	protected byte m_type = ELEMENT;
	
}
