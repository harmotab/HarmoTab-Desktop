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

package harmotab.sound;

import harmotab.element.Element;


public class SoundItem implements Cloneable, Comparable<SoundItem> {
	
	public static final int NO_SOUND = -1;
	
	
	//
	// Constructeurs
	//

	public SoundItem(Element element, int trackId, int soundId, float startTime, float durationTime) {
		m_element = element;
		m_trackId = trackId;
		m_soundId = soundId;
		m_startTime = startTime;
		m_endTime = startTime + durationTime;
		m_durationTime = durationTime;
	}
	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Comporaison de deux sound item en fonction de leur temp de d�but
	 */
	@Override
	public int compareTo(SoundItem object) {
		float diff = m_startTime - object.m_startTime;
		if (diff == 0) {
			diff = m_endTime - object.m_endTime;
			if (diff == 0) {
				return 0;
			}
		}
		return (diff < 0 ? -1 : 1);
	}
	
	
	//
	// Getters
	//
	
	public Element getElement() {
		return m_element;
	}
	
	public float getStartTime() {
		return m_startTime;
	}
	
	public float getEndTime() {
		return m_endTime;
	}
	
	public float getDurationTime() {
		return m_durationTime;
	}
	
	public int getTrackId() {
		return m_trackId;
	}
	
	public int getSoundId() {
		return m_soundId;
	}
	
	public boolean isSilence() {
		return (m_soundId == NO_SOUND);
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	
	/**
	 * Décale le son dans le temps
	 */
	public void timeshift(float time) {
		m_startTime += time;
		m_endTime += time;
	}
	
	/**
	 * Allonge la durée du son
	 */
	public void extend(float time) {
		m_endTime += time;
		m_durationTime += time;
	}
	
	
	//
	// Attributs
	//
	
	public Element m_element;
	public float m_startTime;
	public float m_endTime;
	public float m_durationTime;
	public int m_trackId;
	public int m_soundId;
	public int m_type;
	
}
