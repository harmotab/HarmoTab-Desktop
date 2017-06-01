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

package harmotab.renderer;

import harmotab.element.Element;

/**
 * Indication de positionnement d'un élément
 */
public class LocationItem implements Cloneable {

	//
	// Constructeurs
	//
	
	public LocationItem(Element element, int poiX, int poiY, int x, int y, int width, int height, int trackId, int line, float time, int extra) {
		m_element = element;
		m_x1 = x;
		m_y1 = y;
		m_width = width;
		m_height = height;
		m_x2 = x + width;
		m_y2 = y + height;
		m_trackId = trackId;
		m_line = line;
		m_poiX = poiX;
		m_poiY = poiY;
		m_time = time;
		m_extra = extra;
		m_isSelection = false;
		m_flag = 0;
		m_parent = null;
		m_elementIndex = -1;
	}
	
	public LocationItem(LocationItem item) {
		m_element = item.m_element;
		m_x1 = item.m_x1;
		m_y1 = item.m_y1;
		m_width = item.m_width;
		m_height = item.m_height;
		m_x2 = item.m_x1 + item.m_width;
		m_y2 = item.m_y1 + item.m_height;
		m_trackId = item.m_trackId;
		m_line = item.m_line;
		m_poiX = item.m_poiX;
		m_poiY = item.m_poiY;
		m_extra = item.m_extra;
		m_isSelection = item.m_isSelection;
		m_flag = item.m_flag;
		m_parent = item.m_parent;
		m_elementIndex = item.m_elementIndex;
	}
	
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return new LocationItem(this);
		}
	}
	
	
	static public LocationItem newFromArea(Element element, int x, int y, int width, int height, int trackId, int line, float time, int extra) {
		return new LocationItem(element, x+width/2, y+height/2, x, y, width, height, trackId, line, time, extra);
	}
	
	static public LocationItem newFromPoi(Element element, int poiX, int poiY, int width, int height, int trackId, int line, float time, int extra) {
		return new LocationItem(element, poiX, poiY, poiX-width/2, poiY-height/2, width, height, trackId, line, time, extra);
	}
	
	static public LocationItem newFromOrdinate(Element element, int x, int y, int width, int height, int ordinate, int trackId, int line, float time, int extra) {
		return new LocationItem(element, x + width / 2, ordinate, x, y, width, height, trackId, line, time, extra);
	}
	
	
	//
	// Getters / setters
	//
	
	public int getX1() {
		return m_x1;
	}
	
	public int getY1() {
		return m_y1;
	}
	
	public int getX2() {
		return m_x2;
	}
	
	public int getY2() {
		return m_y2;
	}
	
	public int getPointOfInterestX() {
		return m_poiX;
	}
	
	public int getPointOfInterestY() {
		return m_poiY;
	}
	
	public int getWidth() {
		return m_width;
	}
	
	public int getHeight() {
		return m_height;
	}
	
	public int getTrackId() {
		return m_trackId;
	}
	
	public int getLine() {
		return m_line;
	}
	
	public float getTime() {
		return m_time;
	}
	
	public Element getElement() {
		return m_element;
	}
	
	public int getExtra() {
		return m_extra;
	}
	
	
	public void setFlag(int flag, boolean value) {
		if (value)
			m_flag |= (1 << flag);
		else
			m_flag &= ~(1 << flag);
	}
	
	public boolean getFlag(int flag) {
		return (0x1 & (m_flag >> flag)) == 1;
	}
	
	
	public void setElementIndex(int index) {
		m_elementIndex = index;
	}
	
	public int getElementIndex() {
		return m_elementIndex;
	}
	
	public void setParent(Element parent) {
		m_parent = parent;
	}
	
	public Element getParent() {
		return m_parent;
	}
	
	
	public Element getRootElement() {
		return (m_parent != null ? m_parent : m_element);
	}
	
	
	//
	// M�thodes de moficiations
	//
	
	public void resize(int width, int height) {
//		m_poiX -= m_width - width;
//		m_poiY -= m_height - height;
		m_width = width;
		m_height = height;
		m_x2 = m_x1 + m_width;
		m_y2 = m_y1 + m_height;
	}
	
	public void reduceLeft(int dx) {
		m_width -= dx;
		m_x1 += dx; 
	}
	
	public void translate(int dx, int dy) {
		m_x1 += dx;
		m_x2 += dx;
		m_y1 += dy;
		m_y2 += dy;
		m_poiX += dx;
		m_poiY += dy;
	}
	
	public void moveTo(int x, int y) {
		m_poiX -= x - m_x1;
		m_poiY -= y - m_y1;
		m_x1 = x;
		m_x2 = x + m_width;
		m_y1 = y;
		m_y2 = y + m_height;		
	}
	
	public void moveToX(int x) {
		m_poiX += x - m_x1;
		m_x1 = x;
		m_x2 = x + m_width;
	}

	
	//
	// Attributs
	//
	
	public Element m_element;
	public int m_x1;
	public int m_y1;
	public int m_x2;
	public int m_y2;
	public int m_width;
	public int m_height;
	public int m_trackId;
	public int m_line;
	public int m_poiX;	// Point Of Interest
	public int m_poiY;
	public float m_time;
	public int m_extra;
	public int m_flag;
	public boolean m_isSelection;
	public Element m_parent;
	public int m_elementIndex;
	
}

