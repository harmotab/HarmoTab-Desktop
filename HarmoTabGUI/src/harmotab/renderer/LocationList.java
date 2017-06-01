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

import java.util.*;


public class LocationList implements Iterable<LocationItem> {
	
	public LocationList() {	
	}
	
	
	public void add(LocationItem item) {
		m_list.add(item);
	}
	
	public void add(LocationList list) {
		m_list.addAll(list.m_list);
	}
	
	public void add(int index, LocationList list) {
		m_list.addAll(index, list.m_list);
	}
	
	public void add(int index, LocationItem item) {
		m_list.add(index, item);
	}
	
	public void addBefore(LocationItem beforeThis, LocationItem item) {
		if (beforeThis == null)
			throw new NullPointerException();
		int index = m_list.indexOf(beforeThis);
		if (index == -1)
			throw new IllegalArgumentException("Element not found");
		m_list.add(index, item);
	}
	
	
	public LocationItem at(int x, int y) {
		try {
			Iterator<LocationItem> i = m_list.iterator();
			while (i.hasNext()) {
				LocationItem e = i.next();
				if (e.m_y1 <= y && e.m_y2 >= y && e.m_x1 <= x && e.m_x2 >= x)
					return e;
			}
		}
		catch (ConcurrentModificationException e) {
			System.err.println("LocationItem.at(x, y): ConcurrentModificationException, return null.");
			e.printStackTrace();
		}
		return null;
	}
	
	
	public LocationItem get(Element element) {
		try {
			Iterator<LocationItem> i = m_list.iterator();
			while (i.hasNext()) {
				LocationItem e = i.next();
				if (e.getElement() == element)
					return e;
			}
		}
		catch (ConcurrentModificationException e) {
			System.err.println("LocationItem.get(element): ConcurrentModificationException, return null.");
			e.printStackTrace();
		}
		return null;
	}
	
	
	public LocationItem get(int index) {
		return m_list.get(index);
	}
	
	
	public void addVerticalOffset(int trackId, int lineHeight, int trackOffset) {
		Iterator<LocationItem> i = m_list.iterator();
		int verticalOffset; 
		while (i.hasNext()) {
			LocationItem e = i.next();
			if (e.m_trackId == trackId) {
				verticalOffset = trackOffset + (e.m_line-1) * lineHeight;
				e.m_y1 += verticalOffset;
				e.m_y2 += verticalOffset;
				e.m_poiY += verticalOffset;
			}
		}		
	}
	
	
	public void addOffset(int marginX, int marginY) {
		Iterator<LocationItem> i = m_list.iterator();
		while (i.hasNext()) {
			LocationItem e = i.next();
			e.m_x1 += marginX;
			e.m_x2 += marginX;
			e.m_y1 += marginY;
			e.m_y2 += marginY;
			e.m_poiX += marginX;
			e.m_poiY += marginY;
		}
	}
	
	
	public void addVerticalScrolling(int scollY) {
		Iterator<LocationItem> i = m_list.iterator();
		while (i.hasNext()) {
			LocationItem e = i.next();
			e.m_y1 -= scollY;
			e.m_y2 -= scollY;
			e.m_poiY -= scollY;
		}
	}
	
	
	public int getBottomOrdinate() {
		int maxY = 0;
		for (LocationItem item : m_list)
			if (item.m_y2 > maxY)
				maxY = item.m_y2;
		return maxY;
	}
	
	public int getRightOrdinate() {
		int maxX1 = 0;
		LocationItem maxX1Item = null;
		for (LocationItem item : m_list)
			if (item.m_x2 > maxX1 && item.m_x2 < Integer.MAX_VALUE - 1000) {
				maxX1Item = item;
				maxX1 = item.m_x1;
			}
		return maxX1Item.m_x2;
	}
	
	
	public void reset() {
		m_list.clear();
	}
	
	
	public boolean hasElementOfType(byte type) {
		for (LocationItem item : m_list) {
			if (item.getElement().getType() == type) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public Iterator<LocationItem> iterator() {
		return m_list.iterator();
	}
	
	public Iterator<LocationItem> getIterator() {
		return m_list.iterator();
	}
	
	public ListIterator<LocationItem> getListIterator() {
		return m_list.listIterator();
	}

	public ListIterator<LocationItem> getListIterator(int index) {
		return m_list.listIterator(index);
	}
	
	@SuppressWarnings("rawtypes")
	public ElementLocationIterator getElementLocationIterator(Class elementClass) {
		return new ElementLocationIterator(getListIterator(), elementClass);
	}
	
	@SuppressWarnings("rawtypes")
	public ElementLocationIterator getElementLocationIterator(int index, Class elementClass) {
		return new ElementLocationIterator(getListIterator(index), elementClass);
	}

	
	public int getItemIndex(LocationItem item) {
		return m_list.indexOf(item);
	}

	
	public int getSize() {
		return m_list.size();
	}
	
	
	public void printStackTrace() {
		Iterator<LocationItem> it = getIterator();
		System.out.println("Location list (" + m_list.size() + " items) : " + this);
		int index = 0;
		while (it.hasNext()) {
			LocationItem item = it.next();
			System.out.println(index + ". " + item.getElement() + "\t\t" + 
					"t=" + item.m_trackId + ", l=" + item.m_line +
					", x1=" + item.m_x1 + ", y1=" + item.m_y1 + 
					", w=" + item.m_width + ", h=" + item.m_height);
			index++;
		}
	}
	
	
	private Vector<LocationItem> m_list = new Vector<LocationItem>();

}
