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
 * Mod�le d'une dur�e en terme de "battements"
 */
public class Duration extends HarmoTabObject implements Cloneable {

	public final static String DURATION_TYPESTR = "duration";
	public final static String DURATION_ATTR = "duration";
	
	public final static float DEFAULT_DURATION = 1.0f;
	public final static float DURATION_GRANULARITY = 0.25f;
	public final static float MIN_DURATION_VALUE = /*DURATION_GRANULARITY*/0;
	public final static float MAX_DURATION_VALUE = 100.0f;
	
	
	//
	// Constructeurs
	//
	
	public Duration() {
		setDuration(DEFAULT_DURATION);
	}
	
	public Duration(float value) {
		setDuration(value);
	}
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new DurationRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public float getDuration() {
		return m_duration;
	}
	
	public void setDuration(float value) {
		if (value < MIN_DURATION_VALUE || value > MAX_DURATION_VALUE) {
			throw new IllegalArgumentException("Bad duration value " + value);
		}
		
		m_duration = value;
		fireObjectChanged(DURATION_ATTR);
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(DURATION_TYPESTR, hashCode());
		object.setAttribute(DURATION_ATTR, getDuration()+"");
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setDuration(object.hasAttribute(DURATION_ATTR) ? 
			Float.parseFloat(object.getAttribute(DURATION_ATTR)) : 
			DEFAULT_DURATION);
	}	

	
	//
	// Attributs
	//
	
	protected float m_duration;

}


/**
 * Commande d'annulation des modifications d'une dur�e
 */
class DurationRestoreCommand extends Duration implements RestoreCommand {
	
	public DurationRestoreCommand(Duration saved) {
		m_saved = saved;
		m_duration = m_saved.m_duration;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_duration != m_duration)
			m_saved.setDuration(m_duration);
	}	
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new DurationRestoreCommand(m_saved);
	}
	
	private Duration m_saved;
	
}
