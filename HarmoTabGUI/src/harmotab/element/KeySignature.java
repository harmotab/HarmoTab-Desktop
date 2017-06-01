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
import harmotab.core.undo.RestoreCommand;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.*;


/**
 * Mod�le d'une signature
 */
public class KeySignature extends TrackElement {
	public static final String INDEX_ATTR = "index";

	public static final byte MAX_KEY_SIGNATURE = Height.NUMBER_OF_NOTES_PER_OCTAVE-1;
	public static final byte MIN_KEY_SIGNATURE = -MAX_KEY_SIGNATURE;
	
	public static final byte G_FLAT_MAJOR = -6;
	public static final byte D_FLAT_MAJOR = -5;
	public static final byte A_FLAT_MAJOR = -4;
	public static final byte E_FLAT_MAJOR = -3;
	public static final byte B_FLAT_MAJOR = -2;
	public static final byte F_MAJOR = -1;	
	public static final byte C_MAJOR = 0;
	public static final byte G_MAJOR = 1;
	public static final byte D_MAJOR = 2;
	public static final byte A_MAJOR = 3;
	public static final byte E_MAJOR = 4;
	public static final byte B_MAJOR = 5;
	public static final byte F_SHARP_MAJOR = 6;
	
	public static final byte E_FLAT_MINOR = -6;
	public static final byte B_FLAT_MINOR = -5;
	public static final byte F_MINOR = -4;
	public static final byte C_MINOR = -3;
	public static final byte G_MINOR = -2;
	public static final byte D_MINOR = -1;
	public static final byte A_MINOR = 0;
	public static final byte E_MINOR = 1;
	public static final byte B_MINOR = 2;
	public static final byte F_SHARP_MINOR = 3;
	public static final byte C_SHARP_MINOR = 4;
	public static final byte G_SHARP_MINOR = 5;
	public static final byte D_SHARP_MINOR = 6;
	
	
	public static final Height[] SHARP_ORDER = { 
		null,
		new Height(Height.F, 5),
		new Height(Height.C, 5),
		new Height(Height.G, 5),
		new Height(Height.D, 5),
		new Height(Height.A, 4),
		new Height(Height.E, 5),
		new Height(Height.B, 4)
	};
	
	public static final Height[] FLAT_ORDER = {
		null,
		new Height(Height.B, 4),
		new Height(Height.E, 5),
		new Height(Height.A, 4),
		new Height(Height.D, 5),
		new Height(Height.G, 4),
		new Height(Height.C, 5),
		new Height(Height.F, 4)
	}; 
	
	public static final byte DEFAULT_KEY_SIGNATURE = C_MAJOR;
	
	
	//
	// Constructeurs
	//
	
	public KeySignature() {
		super(Element.KEY_SIGNATURE);
		setIndex(DEFAULT_KEY_SIGNATURE);
	}
	
	public KeySignature(byte index) throws OutOfBoundsError {
		super(Element.KEY_SIGNATURE);
		setIndex(index);
	}
	
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new KeySignatureRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public byte getValue() {
		return m_keySignature;
	}
	
	public void setIndex(byte keySignature) throws OutOfBoundsError {
		if (keySignature < MIN_KEY_SIGNATURE || keySignature > MAX_KEY_SIGNATURE)
			throw new OutOfBoundsError("Invalid key signature index (" + keySignature + ") !");
		
		m_keySignature = keySignature;
		fireObjectChanged(INDEX_ATTR);
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_KEY_SIGNATURE);
	}
	
	public boolean isSharp(Height height) {
		switch (height.getNote()) {
			case Height.F:	return m_keySignature > 0;
			case Height.C:	return m_keySignature > 1;
			case Height.G:	return m_keySignature > 2;
			case Height.D:	return m_keySignature > 3;
			case Height.A:	return m_keySignature > 4;
			case Height.E:	return m_keySignature > 5;
			case Height.B:	return m_keySignature > 6;
			default: throw new UnhandledCaseError("Invalid note height !");
		}
	}
	
	public boolean isFlat(Height height) {
		switch (height.getNote()) {
			case Height.B:	return m_keySignature < 0;
			case Height.E:	return m_keySignature < -1;
			case Height.A:	return m_keySignature < -2;
			case Height.D:	return m_keySignature < -3;
			case Height.G:	return m_keySignature < -4;
			case Height.C:	return m_keySignature < -5;
			case Height.F:	return m_keySignature < -6;
			default: throw new UnhandledCaseError("Invalid note height !");
		}
	}
	
	public static Height getHeight(byte index) {
		if (index > 0)
			return SHARP_ORDER[index];
		if (index < 0)
			return FLAT_ORDER[-index];
		return null;
	}
	
	
	@Override 
	public float getWidthUnit() {
		float width = getValue() * 0.30f;
		return (width >= 0 ? width : -width) + 0.25f;
	}
	
	
	public static String getTonalityName(int value) {
		switch (value) {
			case -6:	return "Gb " + Localizer.get(i18n.N_MAJOR) + " / Eb " + Localizer.get(i18n.N_MINOR) + "";
			case -5:	return "Db " + Localizer.get(i18n.N_MAJOR) + " / Bb " + Localizer.get(i18n.N_MINOR) + "";
			case -4:	return "Ab " + Localizer.get(i18n.N_MAJOR) + " / F " + Localizer.get(i18n.N_MINOR) + "";
			case -3:	return "Eb " + Localizer.get(i18n.N_MAJOR) + " / C " + Localizer.get(i18n.N_MINOR) + "";
			case -2:	return "Bb " + Localizer.get(i18n.N_MAJOR) + " / G " + Localizer.get(i18n.N_MINOR) + "";
			case -1:	return "F " + Localizer.get(i18n.N_MAJOR) + " / D " + Localizer.get(i18n.N_MINOR) + "";
			case 0:		return "C " + Localizer.get(i18n.N_MAJOR) + " / A " + Localizer.get(i18n.N_MINOR) + "";
			case 1:		return "G " + Localizer.get(i18n.N_MAJOR) + " / E " + Localizer.get(i18n.N_MINOR) + "";
			case 2:		return "D " + Localizer.get(i18n.N_MAJOR) + " / B " + Localizer.get(i18n.N_MINOR) + "";
			case 3:		return "A " + Localizer.get(i18n.N_MAJOR) + " / F# " + Localizer.get(i18n.N_MINOR) + "";
			case 4:		return "E " + Localizer.get(i18n.N_MAJOR) + " / C# " + Localizer.get(i18n.N_MINOR) + "";
			case 5:		return "B " + Localizer.get(i18n.N_MAJOR) + " / G# " + Localizer.get(i18n.N_MINOR) + "";
			case 6:		return "F# " + Localizer.get(i18n.N_MAJOR) + " / D# " + Localizer.get(i18n.N_MINOR) + "";
			default:	return "ERROR";//throw new UnhandledCaseError("Invalid tonality value (" + value + ")");
		}
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(INDEX_ATTR, getValue()+"");
		return object;
	}
	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setIndex(object.hasAttribute(INDEX_ATTR) ?
			Byte.decode(object.getAttribute(INDEX_ATTR)) :
			DEFAULT_KEY_SIGNATURE);
	}
	

	//
	// Attributs
	//
	
	protected byte m_keySignature;
	
}


/**
 * Commande d'annulation des modifications d'une signature
 */
class KeySignatureRestoreCommand extends KeySignature implements RestoreCommand {
	
	public KeySignatureRestoreCommand(KeySignature saved) {
		m_saved = saved;
		m_keySignature = m_saved.m_keySignature;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_keySignature != m_keySignature)
			m_saved.setIndex(m_keySignature);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new KeySignatureRestoreCommand(m_saved);
	}
	
	private KeySignature m_saved;
}
