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


/**
 * Mod�le d'une barre de mesure
 */
public class Bar extends TrackElement {
	public static final String KEY_ATTR = "key";
	public static final String KEY_SIGNATURE_ATTR = "keySignature";
	public static final String TIME_SIGNATURE_ATTR = "timeSignature";
	public static final String REPEAT_ATTRIBUTE_ATTR = "repeatAttribute";
	
	//
	// Constructeurs
	// 
	
	public Bar() {
		super(Element.BAR);
		setKey(null);
		setKeySignature(null);
		setTimeSignature(null);
		setRepeatAttribute(new RepeatAttribute());	
	}
	
	public Bar(Key key, KeySignature ks, TimeSignature ts, RepeatAttribute repeat) {
		super(Element.BAR);
		setKey(key);
		setKeySignature(ks);
		setTimeSignature(ts);
		setRepeatAttribute(repeat);
	}

	
	@Override
	public Object clone() {
		Bar bar = (Bar)super.clone();
		
		Key key = m_key != null ? (Key) m_key.clone() : null;
		bar.m_key = null;
		bar.setKey(key);
		
		KeySignature ks = m_keySignature != null ? (KeySignature) m_keySignature.clone() : null;
		bar.m_keySignature = null;
		bar.setKeySignature(ks);
		
		TimeSignature ts = m_timeSignature != null ? (TimeSignature) m_timeSignature.clone() : null;
		bar.m_timeSignature = null;
		bar.setTimeSignature(ts);
		
		RepeatAttribute ra = m_repeatAttribute != null ? (RepeatAttribute) m_repeatAttribute.clone() : null;
		bar.m_repeatAttribute = null;
		bar.setRepeatAttribute(ra);
		
		return bar;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new BarRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public Key getKey() {
		return m_key;
	}
	
	public void setKey(Key key) {
		removeAttributeChangesObserver(m_key, KEY_ATTR);
		m_key = key;
		addAttributeChangesObserver(m_key, KEY_ATTR);
		fireObjectChanged(KEY_ATTR);
	}
	
	
	public KeySignature getKeySignature() {
		return m_keySignature;
	}
	
	public void setKeySignature(KeySignature keySignature) {
		removeAttributeChangesObserver(m_keySignature, KEY_SIGNATURE_ATTR);
		m_keySignature = keySignature;
		addAttributeChangesObserver(m_keySignature, KEY_SIGNATURE_ATTR);
		fireObjectChanged(KEY_SIGNATURE_ATTR);
	}
	
	
	public TimeSignature getTimeSignature() {
		return m_timeSignature;
	}
	
	public void setTimeSignature(TimeSignature timeSignature) {
		removeAttributeChangesObserver(m_timeSignature, TIME_SIGNATURE_ATTR);
		m_timeSignature = timeSignature;
		addAttributeChangesObserver(m_timeSignature, TIME_SIGNATURE_ATTR);
		fireObjectChanged(TIME_SIGNATURE_ATTR);
	}
	
	
	public RepeatAttribute getRepeatAttribute() {
		return m_repeatAttribute; 
	}
	
	public void setRepeatAttribute(RepeatAttribute repeatAttribute) {
		if (repeatAttribute == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_repeatAttribute, REPEAT_ATTRIBUTE_ATTR);
		m_repeatAttribute = repeatAttribute;
		addAttributeChangesObserver(m_repeatAttribute, REPEAT_ATTRIBUTE_ATTR);
		fireObjectChanged(REPEAT_ATTRIBUTE_ATTR);
	}
	
	
	//
	// Méthodes utilitaires
	//	
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_BAR);
	}
	
	@Override public float getWidthUnit() {
		return 1.0f;
	}
	
	
	//
	// Gestion des sous-�l�ment
	//
	
	@Override
	public boolean contains(Element subElement) {
		return
			subElement == m_key ||
			subElement == m_keySignature ||
			subElement == m_timeSignature;
	}
	
	@Override
	public boolean delete(Element subElement) {
		if (subElement == m_key)
			setKey(null);
		else if (subElement == m_keySignature)
			setKeySignature(null);
		else if (subElement == m_timeSignature)
			setTimeSignature(null);
		else
			return false;
		return true;
	}

	
	//
	// Serialisation / d�serialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		if (getKey() != null)
			object.setElementAttribute(KEY_ATTR, getKey());
		if (getKeySignature() != null)
			object.setElementAttribute(KEY_SIGNATURE_ATTR, getKeySignature());
		if (getTimeSignature() != null)
			object.setElementAttribute(TIME_SIGNATURE_ATTR, getTimeSignature());
		if (getRepeatAttribute() != null)
			object.setElementAttribute(REPEAT_ATTRIBUTE_ATTR, getRepeatAttribute());
		return object;
	}
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setKey(object.hasAttribute(KEY_ATTR) ?
			(Key) object.getElementAttribute(KEY_ATTR) :
			null);
		setKeySignature(object.hasAttribute(KEY_SIGNATURE_ATTR) ?
			(KeySignature) object.getElementAttribute(KEY_SIGNATURE_ATTR) :
			null);
		setTimeSignature(object.hasAttribute(TIME_SIGNATURE_ATTR) ?
			(TimeSignature) object.getElementAttribute(TIME_SIGNATURE_ATTR) :
			null);
		setRepeatAttribute(object.hasAttribute(REPEAT_ATTRIBUTE_ATTR) ?
			(RepeatAttribute) object.getElementAttribute(REPEAT_ATTRIBUTE_ATTR) :
			null);
	}

	
	//
	// Attributs
	//
	
	protected Key m_key = null;
	protected KeySignature m_keySignature = null;
	protected TimeSignature m_timeSignature = null;
	protected RepeatAttribute m_repeatAttribute = null;
	
}


/**
 * Commande d'annulation des modifications d'une barre de mesure
 */
class BarRestoreCommand extends Bar implements RestoreCommand {
	
	public BarRestoreCommand(Bar saved) {
		m_saved = saved;
		m_key = m_saved.m_key;
		m_keySignature = m_saved.m_keySignature;
		m_timeSignature = m_saved.m_timeSignature;
		m_repeatAttribute = m_saved.m_repeatAttribute;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_key != m_key)
			m_saved.setKey(m_key);
		if (m_saved.m_keySignature != m_keySignature)
			m_saved.setKeySignature(m_keySignature);
		if (m_saved.m_timeSignature != m_timeSignature)
			m_saved.setTimeSignature(m_timeSignature);
		if (m_saved.m_repeatAttribute != m_repeatAttribute)
			m_saved.setRepeatAttribute(m_repeatAttribute);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new BarRestoreCommand(m_saved);
	}
	
	private Bar m_saved;
}

