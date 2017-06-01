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


public class RepeatItem implements Cloneable, Comparable<RepeatItem> {

	//
	// Constructeurs
	//
	
	public RepeatItem(int iterations, float phraseStartTime, float phraseEndTime) {
		m_iterationsNumber = iterations;
		m_phraseStartTime = phraseStartTime;
		m_phraseEndTime = phraseEndTime;
		m_iterator = m_iterationsNumber;
	}
	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	//
	// Getters / setters
	//
	
	public int getIterationsNumber() {
		return m_iterationsNumber;
	}
	
	public float getPhraseStartTime() {
		return m_phraseStartTime;
	}
	
	public float getPhraseEndTime() {
		return m_phraseEndTime;
	}
	
	
	//
	// Gestion d'un "it�rateur"
	//
	
	public void resetIterator() {
		m_iterator = m_iterationsNumber;
	}
	
	public boolean mustIterate() {
		return m_iterator > 1;
	}
	
	public void iterate() {
		m_iterator--;
	}
	
	
	// 
	// M�thodes utilitaires
	// 
	
	@Override
	public int compareTo(RepeatItem object) {
		float diff = m_phraseStartTime - object.m_phraseStartTime;
		if (diff == 0)
			return 0;
		return (diff < 0 ? -1 : 1);
	}
	
	
	public void timeshift(float time) {
		m_phraseStartTime += time;
		m_phraseEndTime += time;
	}
	
	
	//
	// Attributs
	//
	
	private int m_iterationsNumber;
	private float m_phraseStartTime;
	private float m_phraseEndTime;
	private int m_iterator;
	
}
