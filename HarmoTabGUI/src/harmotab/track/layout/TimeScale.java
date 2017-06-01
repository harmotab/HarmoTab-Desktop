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

package harmotab.track.layout;

import java.util.LinkedList;
import java.util.Iterator;

import harmotab.element.Bar;
import harmotab.element.Element;
import harmotab.element.Note;
import harmotab.renderer.LocationList;
import harmotab.renderer.LocationItem;


public class TimeScale {

	//
	// Constructeurs
	//
	
	public TimeScale(LocationList locations, int trackId) {
		constructTimeScale(locations, trackId);
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	/**
	 * Construit l'echelle de temps à partir du positionnement des éléments d'une piste
	 */
	public void constructTimeScale(LocationList locations, int trackId) {
		float currentTime = 0.0f;
		int currentLine = 0;
		
		// Initialisation de l'instance
		m_timePoints.clear();
		m_timeIterator = null;
		m_timeIteratorCurrent = null;
		
		// Parcours de tous les items correspondant à la piste de référence
		Iterator<LocationItem> locationsIterator = locations.getIterator();
		while (locationsIterator.hasNext()) {
			LocationItem item = locationsIterator.next();
			if (item.getTrackId() == trackId) {
				Element element = item.getElement();
				
				// Si l'élément est un temps...
				if (element instanceof Note) {
					// Ajout d'un point de début de ligne si c'est la première note de la ligne
					if (item.m_line > currentLine) {
						currentLine = item.m_line;
						m_timePoints.add(new TimePoint(TimePoint.LINE_STARTS, currentTime, currentLine, item.m_x1));
					}
					// Ajout d'un vecteur temporel
					currentTime = item.m_time + element.getDuration();
					m_timePoints.add(new TimeVector(TimePoint.TIME, item.m_time, currentTime, item.m_line, item.m_x1, item.m_x2));
				}
				// Si l'élément est une barre de mesure...
				else if (element instanceof Bar) {
					// Ajout un point de positionnement de barre de mesure
					m_timePoints.add(new TimePoint(TimePoint.BAR, currentTime, currentLine, item.m_poiX));
				}
				
			}
		}
	}
	
	
	/**
	 * Récupération d'un point à partir d'un temps.
	 * Retourne le premier point trouv�.
	 */	
	public TimePoint getFirstPointAt(float time) {
		TimePoint result = null;
		// RAZ de l'itérateur si pas initialisé ou temps recherché dépassé
		if (m_timeIteratorCurrent == null || m_timeIteratorCurrent.m_endTime > time) {
			m_timeIterator = m_timePoints.iterator();
			iterateTimeIterator();
		}
		// Retourne nul si rien � it�rer
		if (m_timeIteratorCurrent == null)
			return null;
		
		// Recherche du temps demand�
		do {
			result = m_timeIteratorCurrent.getPoint(time);
			if (result == null)
				iterateTimeIterator();
		}
		while (result == null && m_timeIteratorCurrent != null);
		
		// Retourne le r�sultat
		return result;
	}
	
	
	/**
	 * R�cup�ration d'un point � partir d'un temps.
	 * Retourne le dernier point trouv�.
	 */
	public TimePoint getLastPointAt(float time) {
		TimePoint result = null;
		// RAZ de l'itérateur si pas initialisé ou temps recherché dépassé
		if (m_timeIteratorCurrent == null || m_timeIteratorCurrent.m_endTime > time) {
			m_timeIterator = m_timePoints.iterator();
			iterateTimeIterator();
		}
		// Retourne nul si rien � it�rer
		if (m_timeIteratorCurrent == null)
			return null;
		
		// Recherche du temps demand� (s�lectionne le premier trouv�)
		do {
			result = m_timeIteratorCurrent.getPoint(time);
			if (result == null)
				iterateTimeIterator();
		}
		while (result == null && m_timeIteratorCurrent != null);
		
		// Continue l'it�ration tant que la point contient le temps voulu
		TimePoint after = null;
		do {
			after = m_timeIteratorCurrent.getPoint(time);
			if (after != null) {
				result = after;
				iterateTimeIterator();
			}
		}
		while (after != null && m_timeIteratorCurrent != null);
		
		// Retourne le r�sultat
		return result;
	}
	
	
	/**
	 * It�ration qui ne s'arr�te que sur les instances de TimeVector
	 */
	private TimeVector iterateTimeIterator() {
		TimePoint point = null;
		do {
			point = m_timeIterator.hasNext() ? m_timeIterator.next() : null;
		}
		while (point != null && !(point instanceof TimeVector));
		
		m_timeIteratorCurrent = (TimeVector) point;
		return m_timeIteratorCurrent;
	}
	

	/**
	 * Récupération d'un temps à partir d'une position
	 */
	public Float getTime(int line, int x) {
		for (TimePoint point : m_timePoints) {
			if (point instanceof TimeVector) {
				TimeVector vector = (TimeVector) point;
				Float res = vector.getTime(line, x);
				if (res != null)
					return res;
			}
		}
		return null;
	}
	
	/**
	 * Récupération du dernier temps de l'echelle
	 */
	public float getEndTime() {
		float endTime = 0.0f;
		for (TimePoint point : m_timePoints) {
			endTime = Math.max(endTime, point.getEndTime());
		}
		return endTime;
	}
	
	/**
	 * Récupération de la position du début de la ligne
	 */
	public TimePoint getLineStart(int line) {
		for (TimePoint point : m_timePoints) {
			if (point.m_type == TimePoint.LINE_STARTS && point.m_line == line)
				return new TimePoint(point.m_type, point.m_time, point.m_line, point.m_x);
		}
		return null;
	}
	
	/**
	 * R�cup�ration du num�ro de ligne maximal
	 */
	public int getNumberOfLines() {
		int max = 0;
		for (TimePoint point : m_timePoints) {
			max = Math.max(max, point.getLine());
		}
		return max;
	}
	
	
	//
	// Attributs
	//
	
	protected LinkedList<TimePoint> m_timePoints = new LinkedList<TimePoint>();
	protected Iterator<TimePoint> m_timeIterator;
	protected TimeVector m_timeIteratorCurrent;
	
	
	//
	// Inner class
	//
	
	public class TimePoint {
		
		public static final byte LINE_STARTS = 1;
		public static final byte LINE_ENDS = 2;
		public static final byte BAR = 3;
		public static final byte TIME = 4;
		
		public TimePoint(byte type, float time, int line, int x) {
			m_type = type;
			m_time = time;
			m_line = line;
			m_x = x;
		}
		
		public float getTime() {
			return m_time;
		}
		
		public int getLine() {
			return m_line;
		}
		
		public int getX() {
			return m_x;
		}
		
		public float getEndTime() {
			return m_time;
		}
		
		protected byte m_type;
		protected float m_time;
		protected int m_line;
		protected int m_x;
		
	}
	
	
	public class TimeVector extends TimePoint {

		public TimeVector(byte type, float startTime, float endTime, int line, int startX, int endX) {
			super(type, startTime, line, startX);
			m_endTime = endTime;
			m_endX = endX;
		}
		
		public TimePoint getPoint(float time) {
			if (time >= m_time && time <= m_endTime) {
				int posX = (int) (m_x + (m_endX - m_x) * ((time - m_time) / (m_endTime - m_time)));
				return new TimePoint(TimePoint.TIME, time, m_line, posX);
			}
			return null;
		}
		
		public Float getTime(int line, int x) {
			if (line == m_line && x >= m_x && x <= m_endX) {
				float time = (m_time + (m_endTime - m_time) * ((x - m_x) / (m_endX - m_x)));
				return new Float(time);
			}
			return null;
		}

		@Override
		public float getEndTime() {
			return m_endTime;
		}
		
		protected float m_endTime;
		protected int m_endX;
		
	}
	
}
