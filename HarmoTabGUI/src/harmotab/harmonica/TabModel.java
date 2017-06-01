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

package harmotab.harmonica;

import java.util.Vector;
import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.element.*;
import harmotab.io.*;
import harmotab.throwables.*;


/**
 * Contient le mapping entre les hauteurs de son et les tablatures
 */
public class TabModel extends HarmoTabObject {
	
	public final static String TAB_MODEL_TYPESTR = "tabModel";
	public final static String MAPPING_ATTR = "mapping";
	
	
	//
	// Constructeur
	//

	public TabModel() {
		m_model = new Vector<Tab>(Height.MAX_VALUE);
		reset();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new TabModelRestoreCommand(this);
	}
	
	
	@Override
	public Object clone() {
		TabModel model = (TabModel) super.clone();
		
		model.m_model = new Vector<Tab>(0);
		for (Tab tab : m_model) {
			model.m_model.add(tab != null ? (Tab) tab.clone() : null);
		}
		
		return model;
	}
	
	
	//
	// Getters / setters
	//
	
	/**
	 * Retourne la tab associé à une hauteur de son
	 */
	public Tab getTab(Height height) {
		Tab tab = m_model.elementAt(height.getSoundId());
		if (tab != null)
			return new Tab(tab);
		return null;
	}
	
	/**
	 * Affecte la tab associé à une hauteur de son
	 */
	public void setTab(Height height, Tab tab) {
		if (tab != null && (tab.getHole() == Tab.UNDEFINED || tab.getDirection() == Tab.UNDEFINED))
			return;
		Tab addTab = new Tab(tab);
		// N'enregistre pas l'effet avec la tablature
		addTab.setEffect(new Effect());
		m_model.setElementAt(addTab, height.getSoundId());
		fireObjectChanged(MAPPING_ATTR);
	}
	
	
	/**
	 * Retourne la hauteur de son associée à une tab
	 */
	public Height getHeight(Tab tab) {
		if (tab == null || tab.getHole() == Tab.UNDEFINED || tab.getDirection() == Tab.UNDEFINED) {
			return null;
		}
		int size = m_model.size();
		for (int i = 0; i < size; i++) {
			if (tab.equals(m_model.elementAt(i))) {
				try {
					return new Height(i);
				}
				catch (OutOfBoundsError e) {
					throw new BrokenImplementationError("Out of bounds note height (" + i + ") !");
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Supprime l'association de la tab à la hauteur de son spécifié
	 */
	public void deleteTab(Height height) {
		m_model.setElementAt(null, height.getSoundId());
		fireObjectChanged(MAPPING_ATTR);
	}
	
	/**
	 * Mise à zéro du mapping hauteur de son / tab
	 */
	public void reset() {
		m_model.clear();
		for (int i = 0; i <= Height.MAX_VALUE+1; i++)
			m_model.add(i, null);
		fireObjectChanged(MAPPING_ATTR);
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(TAB_MODEL_TYPESTR, hashCode());

		// Ajout d'une tab pour toutes les hauteurs renseign�es
		for (int i = Height.MIN_VALUE; i < Height.MAX_VALUE; i++) {
			Height height = new Height(i);
			Tab tab = getTab(height);
			if (tab != null && tab.isDefined()) {
				SerializedObject heightObject = height.serialize(serializer);
				heightObject.setElementAttribute(Tab.TAB_TYPESTR, tab);
				object.addChild(heightObject);
			}
		}
		
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		int numberOfChild = object.getChildsNumber();
		for (int i = 0; i < numberOfChild; i++) {
			SerializedObject child = object.getChild(i);
			if (child != null) {
				HarmoTabObject childObject = HarmoTabObjectFactory.create(serializer, child);
				if (childObject instanceof Height) {
					Height height = (Height) childObject;
					Tab tab = (Tab) child.getElementAttribute(Tab.TAB_TYPESTR);
					setTab(height, tab);
				}
			}
		}
	}
	
	
	//
	// Méthode de débug
	//
	
	public void print() {
		System.out.print(this + ":" );
		for (Tab tab : m_model) {
			System.out.print("-" + (tab != null ? tab.getHole() : " "));
		}
		System.out.println("");
	}
	
	
	//
	// Attributs
	//
	
	protected Vector<Tab> m_model;
}


/**
 * Commande d'annulation des modifications d'un harmonica
 */
class TabModelRestoreCommand extends TabModel implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public TabModelRestoreCommand(TabModel saved) {
		m_saved = saved;
		m_model = (Vector<Tab>) m_saved.m_model.clone();
//		m_model = (ArrayList<Tab>) m_saved.m_model.clone();
	}
	
	@Override
	public void execute() {
		if (m_saved.m_model != m_model)
			m_saved.m_model = m_model;
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new TabModelRestoreCommand(m_saved);
	}
	
	private TabModel m_saved;
}

