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

package harmotab.desktop.tools;

import harmotab.core.*;
import harmotab.element.*;
import harmotab.renderer.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Boite d'outils de modification d'un �l�ment de type HarmoTab
 */
public class HarmoTabTool extends NoteTool {
	private static final long serialVersionUID = 1L;

	//
	// Gestion du controlleur
	//

	public HarmoTabTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_harmoTabElement = (HarmoTabElement) item.getElement();		
		m_tab = m_harmoTabElement.getTab();
		
		LocationItem tabLocationItem = (LocationItem) item.clone();
		tabLocationItem.m_element = m_tab;
		m_tabController = new TabTool(container, score, tabLocationItem);
	}

	
	//
	// Gestion des actions utilisateur
	//
	
	@Override
	public void keyTyped(KeyEvent event) {
		super.keyTyped(event);
		// Répercuter l'évènement sur la tablature si l'objet peut avoir une tablature
		if (!m_harmoTabElement.isRest()) {
			m_tabController.keyTyped(event);
		}
	}

	
	//
	// Attributs
	//
	
	private HarmoTabElement m_harmoTabElement = null;
	private Tab m_tab = null;
	private TabTool m_tabController = null;
	
}
