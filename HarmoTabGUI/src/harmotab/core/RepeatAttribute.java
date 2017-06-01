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
 * Mod�le d'une r�p�tition
 */
public class RepeatAttribute extends HarmoTabObject implements Cloneable {
	public static final String REPEAT_ATTRIBUTE_TYPESTR = "repeatAttribute";
	
	public static final String BEGINNING_ATTR = "beginning";
	public static final String ENDING_ATTR = "ending";
	public static final String ALTERNATE_ENDING_ATTR = "alternateEnding";
	public static final String REPEAT_TIMES_ATTR = "repeats";
	
	public static final int MIN_ALTERNATE_ENDING = 0;
	public static final int MAX_ALTERNATE_ENDING = 32;
	
	public static final int MIN_REPEAT_TIMES = 1;
	public static final int MAX_REPEAT_TIMES = 32;
	
	public static final byte DEFAULT_ALTERNAT_ENDING = 0;
	public static final boolean DEFAULT_ENDING = false;
	public static final boolean DEFAULT_BEGINNING = false;
	public static final int DEFAULT_REPEAT_TIMES = 1;
	

	//
	// Constructeurs
	//

	public RepeatAttribute(boolean isBeginning, boolean isEnd, int repeatTimes, byte alternateEnding) {
		setBeginning(isBeginning);
		setEnd(isEnd);
		setAlternateEnding(alternateEnding);
		setRepeatTimes(repeatTimes);		
	}
	
	public RepeatAttribute(boolean isBeginning, boolean isEnd, int repeatTimes) {
		this(isBeginning, isEnd, repeatTimes, DEFAULT_ALTERNAT_ENDING);		
	}
	
	public RepeatAttribute(boolean isBeginning, boolean isEnd) {
		this(isBeginning, isEnd, DEFAULT_REPEAT_TIMES);
	}
	
	public RepeatAttribute() {
		this(DEFAULT_BEGINNING, DEFAULT_ENDING, DEFAULT_REPEAT_TIMES, DEFAULT_ALTERNAT_ENDING);
	}
		
	public RepeatAttribute(byte alternateEnding) {
		this(DEFAULT_BEGINNING, DEFAULT_ENDING, DEFAULT_REPEAT_TIMES, alternateEnding);
	}
	
	public RepeatAttribute(int repeatTimes) {
		this(DEFAULT_BEGINNING, true, repeatTimes);
	}

	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new RepeatAttributeRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//	
	
	public boolean isBeginning() {
		return m_isBeginning;
	}
	
	public void setBeginning(boolean isBeginning) {
		m_isBeginning = isBeginning;
		fireObjectChanged(BEGINNING_ATTR);
	}
	
	
	public boolean isEnd() {
		return m_isEnd;
	}
	
	public void setEnd(boolean isEnd) {
		m_isEnd = isEnd;
		if (!m_isEnd)
			setRepeatTimes(DEFAULT_REPEAT_TIMES);
		fireObjectChanged(ENDING_ATTR);
	}
	
	
	public boolean isAlternateEnding() {
		return m_alternateEnding > 0;
	}
	
	public byte getAlternateEnding() {
		return m_alternateEnding;
	}
	
	public void setAlternateEnding(byte alternateEnding) throws OutOfBoundsError {
		if (alternateEnding < MIN_ALTERNATE_ENDING || alternateEnding > MAX_ALTERNATE_ENDING)
			throw new OutOfBoundsError("Invalid alternate ending value !");
		m_alternateEnding = alternateEnding;
		fireObjectChanged(ALTERNATE_ENDING_ATTR);
	}
	
	
	public int getRepeatTimes() {
		return m_repeatTimes;
	}
	
	public void setRepeatTimes(int repeatTimes) {
		if (repeatTimes < MIN_ALTERNATE_ENDING || repeatTimes > MAX_ALTERNATE_ENDING)
			throw new OutOfBoundsError("Invalid repeat times value (" + repeatTimes + ")");
		m_repeatTimes = repeatTimes;
		fireObjectChanged(REPEAT_TIMES_ATTR);
	}
	
	
	
	//
	// M�thodes utilitaires
	//

	public boolean isSingle() {
		return !m_isBeginning && !m_isEnd;
	}
		
	
	//
	// Serialisation / d�serialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(REPEAT_ATTRIBUTE_TYPESTR, hashCode());
		
		if (getAlternateEnding() != DEFAULT_ALTERNAT_ENDING)
			object.setAttribute(ALTERNATE_ENDING_ATTR, getAlternateEnding()+"");
		if (isBeginning() != DEFAULT_BEGINNING)
			object.setAttribute(BEGINNING_ATTR, isBeginning()+"");
		if (isEnd() != DEFAULT_ENDING)
			object.setAttribute(ENDING_ATTR, isEnd()+"");
		if (getRepeatTimes() != DEFAULT_REPEAT_TIMES)
			object.setAttribute(REPEAT_TIMES_ATTR, getRepeatTimes()+"");
		
		return object;
	}
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setAlternateEnding(object.hasAttribute(ALTERNATE_ENDING_ATTR) ?
			Byte.decode(object.getAttribute(ALTERNATE_ENDING_ATTR)) :
			DEFAULT_ALTERNAT_ENDING);
		setBeginning(object.hasAttribute(BEGINNING_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(BEGINNING_ATTR)) :
			DEFAULT_BEGINNING);
		setEnd(object.hasAttribute(ENDING_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(ENDING_ATTR)) :
			DEFAULT_ENDING);
		setRepeatTimes(object.hasAttribute(REPEAT_TIMES_ATTR) ?
			Integer.parseInt(object.getAttribute(REPEAT_TIMES_ATTR)) :
			DEFAULT_REPEAT_TIMES);
	}
	
	
	//
	// Attributs
	//
	
	protected byte m_alternateEnding = 0;
	protected boolean m_isBeginning = false;
	protected boolean m_isEnd = false;
	protected int m_repeatTimes = 0;
	
}


/**
 * Commande d'annulation des modifications d'un attribut de r�p�tition
 */
class RepeatAttributeRestoreCommand extends RepeatAttribute implements RestoreCommand {
	
	public RepeatAttributeRestoreCommand(RepeatAttribute saved) {
		m_saved = saved;
		m_alternateEnding = m_saved.m_alternateEnding;
		m_isBeginning = m_saved.m_isBeginning;
		m_isEnd = m_saved.m_isEnd;
		m_repeatTimes = m_saved.m_repeatTimes;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_alternateEnding != m_alternateEnding)
			m_saved.setAlternateEnding(m_alternateEnding);
		if (m_saved.m_isBeginning != m_isBeginning)
			m_saved.setBeginning(m_isBeginning);
		if (m_saved.m_isEnd != m_isEnd)
			m_saved.setEnd(m_isEnd);
		if (m_saved.m_repeatTimes != m_repeatTimes)
			m_saved.setRepeatTimes(m_repeatTimes);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new RepeatAttributeRestoreCommand(m_saved);
	}
	
	private RepeatAttribute m_saved;
}
