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

import java.util.*;
import harmotab.core.undo.UndoManager;
import harmotab.core.undo.Restoreable;
import harmotab.io.*;


/**
 * Classe m�re de tous les objets du logiciel faisant partie d'une partition.
 */
public abstract class HarmoTabObject implements Cloneable, SerializableObject, Restoreable {
	
	//
	// Constructeurs
	//
	
	public HarmoTabObject() {
		m_undoManager = UndoManager.getInstance();
		m_dispatchEvents = true;
		m_listeners = new LinkedList<HarmoTabObjectListener>();
	}
	
	
	@Override
	protected Object clone() {
		try {
			HarmoTabObject clone = (HarmoTabObject) super.clone();
			clone.m_listeners = new LinkedList<HarmoTabObjectListener>();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	//
	// Interface de gestion des annulations
	//
	
//	@Override
//	public void createUndoPoint() {
//		System.err.println("Cannot undo from " + this);
//	}
	
	
	//
	// Gestion des listeners
	//
	
	public void addObjectListener(HarmoTabObjectListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeObjectListener(HarmoTabObjectListener listener) {
		m_listeners.remove(listener);
	}
	
	
	protected synchronized void dispatchEvent(HarmoTabObjectEvent event) {
		if (m_dispatchEvents == true) {
			for (HarmoTabObjectListener listener : m_listeners)
				listener.onObjectChanged(event);
		}
	}
	
	protected synchronized void fireObjectChanged(String propertyName) {
		if (m_dispatchEvents == true) {
			HarmoTabObjectEvent event = new HarmoTabObjectEvent(this, propertyName);
			for (HarmoTabObjectListener listener : m_listeners) {
				listener.onObjectChanged(event);
			}
		}
	}
		
	protected void addAttributeChangesObserver(HarmoTabObject attribute, String name) {
		if (attribute != null)
			attribute.addObjectListener(new AttributeChangesObserver(this, name));
	}
	
	
	protected void removeAttributeChangesObserver(HarmoTabObject attribute, String name) {
		if (attribute != null) {
			for (HarmoTabObjectListener listener : attribute.m_listeners) {
				if (listener instanceof AttributeChangesObserver) {
					if (((AttributeChangesObserver) listener).m_attribute.equals(name)) {
						attribute.m_listeners.remove(listener);
						return;
					}
				}
			}
		}
	}
	
	
	public synchronized void setDispachEvents(boolean dispatchEvents, String notificationEvent) {
		m_dispatchEvents = dispatchEvents;
		if (notificationEvent != null)
			fireObjectChanged(notificationEvent);
	}
		
	
	//
	// Attributs
	//
	
	private LinkedList<HarmoTabObjectListener> m_listeners = null;
	private boolean m_dispatchEvents;
	protected UndoManager m_undoManager = null;
	
}


/**
 * Object permettant l'�coute de la modification des attributs.
 */
class AttributeChangesObserver implements HarmoTabObjectListener {
	protected HarmoTabObject m_owner = null;
	protected String m_attribute;
	
	public AttributeChangesObserver(HarmoTabObject owner, String attribute) {
		m_owner = owner;
		m_attribute = attribute;
	}
	
	public void onObjectChanged(HarmoTabObjectEvent event) {
		HarmoTabObjectEvent dispatchEvent = new HarmoTabObjectEvent(m_owner, m_attribute, event);
		m_owner.dispatchEvent(dispatchEvent);
	}
}

