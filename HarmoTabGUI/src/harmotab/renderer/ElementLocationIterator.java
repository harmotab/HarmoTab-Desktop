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

import java.util.ListIterator;



public class ElementLocationIterator {
	
	//
	// Constructeur
	//
	
	public ElementLocationIterator(ListIterator<LocationItem> i, Class<?> elementClass) {
		m_iterator = i;
		m_class = elementClass;
	}
	
	
	//
	// M�thodes d'it�ration
	//

	public LocationItem next() {
		while (m_iterator.hasNext()) {
			LocationItem item = m_iterator.next();
			if (m_class.isInstance(item.getElement()))
					return item;
		}
		return null;
	}

	public LocationItem previous() {
		while (m_iterator.hasPrevious()) {
			LocationItem item = m_iterator.previous();
			if (m_class.isInstance(item.getElement()))
					return item;
		}
		return null;
	}
	
	
	//
	// Attributs
	//
	
	private ListIterator<LocationItem> m_iterator = null;
	private Class<?> m_class = null;

}
