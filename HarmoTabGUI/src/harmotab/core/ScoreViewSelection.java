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

import harmotab.element.Element;
import harmotab.renderer.LocationItem;
import harmotab.desktop.tools.Tool;
import harmotab.track.Track;


public class ScoreViewSelection {
	
	//
	// Constructeur
	//
	
	public ScoreViewSelection(Tool tool, int itemIndex) {
		m_tool = tool;
		m_locationItem = m_tool.getLocationItem();
		m_track = tool.getTrack();
		m_element = m_locationItem.getElement();
		if (m_track != null)
			m_elementIndex = m_track.indexOf(m_element);
		else 
			m_elementIndex = -1;
		m_locationItemIndex = itemIndex;
		
		if (itemIndex == -1)
			System.err.println("ScoreViewSelection::ScoreViewSelection: itemIndex == -1");
	}
	
	
	//
	// Getters
	//
	
	public Tool getTool() {
		return m_tool;
	}
	
	public LocationItem getLocationItem() {
		return m_locationItem;
	}
	
	public Track getTrack() {
		return m_track;
	}
	
	public Element getElement() {
		return m_element;
	}
	
	public int getElementIndex() {
		return m_elementIndex;
	}
	
	public int getLocationItemIndex() {
		return m_locationItemIndex;
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	public int getTrackId() {
		return m_locationItem.getTrackId();
	}
	
	
	//
	// Attributs
	//

	private Tool m_tool;
	private LocationItem m_locationItem;
	private Track m_track;
	private Element m_element;
	private int m_elementIndex;
	private int m_locationItemIndex;
	
}
