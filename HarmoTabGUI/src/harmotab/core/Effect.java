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


/**
 * Mod�le d'un effet
 */
public class Effect extends HarmoTabObject {

	public static final String EFFECT_TYPESTR = "effect";
	public static final String EFFECT_ATTR = "effect";
	
	public static final byte NONE = 0;
	public static final byte WAHWAH = 1;
	public static final byte SLIDE = 2;
	public static final byte NUMBER_OF_EFFETS = 3;
	
	public static final String NONE_STR = "none";
	public static final String WAHWAH_STR = "wahwah";
	public static final String SLIDE_STR = "slide";
	
	
	//
	// Constructeur
	//
	
	public Effect(byte effect) {
		setType(effect);
	}
	
	public Effect(String effect) {
		setType(effect);
	}
	
	public Effect() {
		this(NONE);
	}
	
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new EffectRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setType(byte effect) {
		switch (effect) {
			case NONE:
			case WAHWAH:
			case SLIDE:
				m_effect = effect;
				break;
			default:
				throw new IllegalArgumentException("Unhandled effect " + effect + ".");
		}
		fireObjectChanged(EFFECT_ATTR);
	}
	
	public void setType(String effect) {
		if (effect.equals(NONE_STR))
			setType(NONE);
		else if (effect.equals(WAHWAH_STR))
			setType(WAHWAH);
		else if (effect.equals(SLIDE_STR))
			setType(SLIDE);
		else 
			throw new IllegalArgumentException("Unhandled effect '" + effect + "'.");
	}
	
	public byte getType() {
		return m_effect;
	}
	
	public String getEffectTypeStr() {
		switch (m_effect) {
			case NONE:		return NONE_STR;
			case WAHWAH:	return WAHWAH_STR;
			case SLIDE:		return SLIDE_STR;
			default:		return "UNKNOWN EFFECT";
		}
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	public static String getEffectLocalizedName(byte effect) {
		switch (effect) {
			case NONE:		return Localizer.get(i18n.N_EFFECT_NONE);
			case WAHWAH:	return Localizer.get(i18n.N_EFFECT_WAHWAH);
			case SLIDE:		return Localizer.get(i18n.N_EFFECT_SLIDE);
			default:		return "UNKNOWN EFFECT";
		}		
	}
	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(EFFECT_TYPESTR, hashCode());
		object.setAttribute(EFFECT_ATTR, getType()+"");
		return null;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setType(Byte.parseByte(object.getAttribute(EFFECT_ATTR)));
	}

	
	//
	// Attributs
	//
	
	protected byte m_effect;

}


/**
 * Commande d'annulation des modifications d'un effet
 */
class EffectRestoreCommand extends Effect implements RestoreCommand {
	
	public EffectRestoreCommand(Effect saved) {
		m_saved = saved;
		m_effect = m_saved.m_effect;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_effect != m_effect)
			m_saved.setType(m_effect);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new EffectRestoreCommand(m_saved);
	}
	
	private Effect m_saved;
}
