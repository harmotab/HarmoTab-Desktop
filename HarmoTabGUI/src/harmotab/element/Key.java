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
import harmotab.io.*;
import harmotab.throwables.*;


/**
 * Mod�le d'une clef
 */
public class Key extends TrackElement {
	public final static String KEY_ATTR = "value";
	
	//
	// Identidfiants des clés
	//
	
	public final static byte G2 = 0;
	public final static byte F4 = 1;
	public final static byte C3 = 2;
	public final static byte C4 = 3;
	
	public final static String G2_STR = "G2";
	public final static String F4_STR = "F4";
	public final static String C3_STR = "C3";
	public final static String C4_STR = "C4";
	
	private final static String DEFAULT_KEY = "G2";
	
	
	//
	// Constructeurs
	//
	
	public Key() {
		super(Element.KEY);
		setValue(DEFAULT_KEY);
	}
	
	public Key(byte key) throws OutOfBoundsError {
		super(Element.KEY);
		setValue(key);
	}
	
	
	public void setValue(String key) {
		if (key.equals(G2_STR))
			setValue(G2);
		else if (key.equals(F4_STR))
			setValue(F4);
		else if (key.equals(C3_STR))
			setValue(C3);
		else if (key.equals(C4_STR))
			setValue(C4);
		else
			throw new UnhandledCaseError("Invalid key '" + key + "'");
	}
	
	
	public String getValueStr() {
		switch (m_key) {
			case G2:	return G2_STR;
			case F4:	return F4_STR;
			case C3:	return C3_STR;
			case C4:	return C4_STR;
			default: throw new UnhandledCaseError("Unhandled key conversion for '" + m_key + "'");
		}
	}

	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new KeyRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setValue(byte key) throws OutOfBoundsError {
		switch (key) {
			case G2:	m_ordinate = (new Height(Height.G, 4)).getOrdinate();	break;
			case F4:	m_ordinate = (new Height(Height.F, 2)).getOrdinate();	break;
			case C3:	m_ordinate = (new Height(Height.C, 3)).getOrdinate();	break;
			case C4:	m_ordinate = (new Height(Height.C, 3)).getOrdinate();	break;
			default:	throw new OutOfBoundsError("Invalid key value (" + key + ") !");
		}
		
		m_key = key;
		fireObjectChanged(KEY_ATTR);
	}
	
	public byte getValue() {
		return m_key;
	}
		
	
	//
	// Méthodes utilitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_KEY);
	}
	
	public int getOrdinate() {
		return m_ordinate;
	}
	
	public float getWidthUnit() {
		return 0.75f;
	}
	
	
	//
	// Serialisation / déserialisation xml
	//

	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(KEY_ATTR, getValueStr());
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setValue(object.hasAttribute(KEY_ATTR) ?
			object.getAttribute(KEY_ATTR) :
			DEFAULT_KEY);
	}

	
	//
	// Attributs
	//
	
	protected byte m_key;
	protected int m_ordinate;
		
}


/**
 * Commande d'annulation des modifications d'une clef
 */
class KeyRestoreCommand extends Key implements RestoreCommand {
	
	public KeyRestoreCommand(Key saved) {
		m_saved = saved;
		m_key = m_saved.m_key;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_key != m_key)
			m_saved.setValue(m_key);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new KeyRestoreCommand(m_saved);
	}
	
	private Key m_saved;
}

