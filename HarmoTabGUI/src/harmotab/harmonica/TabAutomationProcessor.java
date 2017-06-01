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

import harmotab.core.*;
import harmotab.element.*;


/**
 * Listener des modification des �l�ments de type HarmoTabElement.
 * G�re la fonctionnalit� d'automatisation de la s�lection de la tablature
 * et de cr�ation automatique du mapping note/tab.
 */
public class TabAutomationProcessor implements HarmoTabObjectListener {
	
	//
	// Initialisation
	//
	
	public TabAutomationProcessor(TabModel tabModel) {
		setTabModel(tabModel);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setTabModel(TabModel tabModel) {
		if (tabModel == null)
			throw new NullPointerException();
		m_tabModel = tabModel;
	}
	
	public TabModel getTabModel() {
		return m_tabModel;
	}
	
	
	//
	// Ecoute des modifications de tab ou de hauteur de note
	//

	@Override
	public void onObjectChanged(HarmoTabObjectEvent event) {
		// V�rifie que l'objet est bien de type HarmoTabElement
		if (! (event.getSource() instanceof HarmoTabElement))
			System.err.println("TabAutomationProcessor: event not comming from an HarmoTabElement.");
		
		// En cas de modification de la hauteur de la note met � jour la tab
		if (event.propertyIs(Note.HEIGHT_ATTR)) {
			doAutoTab((HarmoTabElement) event.getSource());
		}
		// En cas de modification de la tablature met � jour le mod�le
		else if (event.propertyIs(HarmoTabElement.TAB_ATTR)) {
			updateTabModel((HarmoTabElement) event.getSource());
		}
	}
	
	
	//
	// Actions
	//
	
	/**
	 * Affecte la tab en fonction de la note
	 */
	public void doAutoTab(HarmoTabElement htElement) {
		// Si l'auto-tab est actif
		if (GlobalPreferences.isAutoTabEnabled()) {
			Tab modelTab = m_tabModel.getTab(htElement.getHeight());
			Tab elementTab = htElement.getTab();
			
			// Mise � jour de la tablature en fonction du mod�le
			// si le mod�le est renseign� et que la tab de l'�l�ment est diff�rente de celle du mod�le
			htElement.setDispachEvents(false, null);
			if (modelTab != null && modelTab.isDefined() && !modelTab.equals(elementTab)) {
				modelTab.setEffect(elementTab.getEffect());
				htElement.setTab(modelTab);
			}
			else if (modelTab == null && elementTab.isDefined()) {
				Tab tab = new Tab();
				tab.setEffect(elementTab.getEffect());
				htElement.setTab(tab);
			}
			htElement.setDispachEvents(true, null);
		}
	}
	
	/**
	 * Met � jour le mod�le de tablatures en affectant la la tab courante 
	 * � la note courante
	 */
	public void updateTabModel(HarmoTabElement htElement) {
		// Mise � jour du mod�le de tablature si la compl�tion automatique du mod�le est activ�e
		if (GlobalPreferences.isTabMappingCompletionEnabled()) {
			Tab modelTab = m_tabModel.getTab(htElement.getHeight());
			Tab elementTab = htElement.getTab();
			
			// et que la tab de l'�l�ment est non nulle
			// et soit le mod�le n'est pas renseign� pour cette note ou que la tab renseign�e est diff�rente
			if (elementTab.isDefined() && (modelTab == null || !modelTab.equals(elementTab))) {
				m_tabModel.setTab(htElement.getHeight(), elementTab);
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	private TabModel m_tabModel = null;
	
}
