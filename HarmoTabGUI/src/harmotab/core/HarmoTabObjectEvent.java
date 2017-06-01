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

public class HarmoTabObjectEvent {
	
	public static final String UNDEFINED_PROPERTY = "undefined";
	

	//
	// Constructeurs
	//
	
	public HarmoTabObjectEvent(HarmoTabObject source, String property, HarmoTabObjectEvent parent) {
		m_source = source;
		m_property = property;
		m_parent = parent;
	}
	
	public HarmoTabObjectEvent(HarmoTabObject source, String property) {
		this(source, property, null);
	}

	public HarmoTabObjectEvent(HarmoTabObject source) {
		this(source, UNDEFINED_PROPERTY);
	}

	
	//
	// Getters / setters
	//
	
	public HarmoTabObject getSource() {
		return m_source;
	}
	
	public String getProperty() {
		return m_property;
	}
	
	public HarmoTabObjectEvent getParent() {
		return m_parent;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	public boolean propertyIs(String property) {
		return m_property.equals(property);
	}
	
	public boolean hierarchyContains(String property) {
		HarmoTabObjectEvent event = this;
		while (event != null) {
			if (event.propertyIs(property))
				return true;
			event = event.getParent();
		}
		return false;
	}
	
	public HarmoTabObjectEvent getHierarchyEvent(String property) {
		HarmoTabObjectEvent event = this;
		while (event != null) {
			if (event.propertyIs(property))
				return event;
			event = event.getParent();
		}
		return null;
	}
	
	public void printStackTrace() {
		HarmoTabObjectEvent event = this;
		String indent = "\t";
		System.out.println("Event " + event);
		while (event != null) {
			System.out.println(indent + "Source = " + event.getSource());
			System.out.println(indent + "Property = " + event.getProperty());
			System.out.println(indent + "Parent = " + event.getParent());
			indent += "\t";
			event = event.getParent();
		}
	}
	
	
	//
	// Attributs
	//
	
	private HarmoTabObject m_source = null;
	private String m_property = null;
	private HarmoTabObjectEvent m_parent = null;
	
}
