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
import javax.swing.border.*;


public class HomeBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public HomeBrowser(ScoreController controller) {
		
		// Cr�ation des composants
		m_actionPane = new ActionPane(controller);
				
		// Ajout des composants � l'interface
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(m_actionPane);
		
		// Enregistrement des listeners
		
		// Partie de l'interface affich�e uniquement si la connexion est activ�e
		if (GlobalPreferences.isNetworkEnabled() == true) {
			m_softwarePane = new SoftwarePane();
			add(m_softwarePane);
			m_softwarePane.setAlignmentX(JComponent.LEFT_ALIGNMENT);			
		}
		
		
		// Affichage du composant
		m_actionPane.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		setBorder(new EmptyBorder(10, 10, 10, 10));
		
	}
	
	
	//
	// Attributs
	//
	
	private SoftwarePane m_softwarePane = null;
	private ActionPane m_actionPane = null;
	
}
