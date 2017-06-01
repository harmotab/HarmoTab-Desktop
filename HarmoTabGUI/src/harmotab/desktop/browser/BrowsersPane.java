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

package harmotab.desktop.browser;

import harmotab.core.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


/**
 * Panneau � onglet situ�sur la partie gauche de l'interface graphique.
 */
public class BrowsersPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public BrowsersPane(ScoreController controller) {
		
		// Initialisation de l'apparence
		setBorder( new EmptyBorder(5, 5, 5, 5));
		
		// Cr�ation des composants
		m_homeBrowser = new HomeBrowser(controller);
		m_localBrowser = new LocalBrowser(controller);
		
		// Ajout des composants � l'interface
		addTab(Localizer.get(i18n.ET_HOME_BROWSER), m_homeBrowser);
		addTab(Localizer.get(i18n.ET_LOCAL_CONTENT), m_localBrowser);
		
		// Enregistrement des listeners
		
		
	}
	
	
	//
	// Getters / setters
	//
	
	public void setSelectedTab(Browser browser) {
		setSelectedComponent(browser);
	}
	
	
	public Browser getHomeBrowser() {
		return m_homeBrowser;
	}
	
	public Browser getLocalBrowser() {
		return m_localBrowser;
	}
	
	
	//
	// Attributs
	//
	
	private HomeBrowser m_homeBrowser = null;
	private LocalBrowser m_localBrowser = null;
	
}

