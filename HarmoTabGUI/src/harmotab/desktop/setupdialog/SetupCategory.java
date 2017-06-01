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

package harmotab.desktop.setupdialog;

import javax.swing.*;
import javax.swing.border.*;
import rvt.util.gui.VerticalLayout;


/**
 * Onglet d'une SetupDialog
 */
public class SetupCategory {
	
	//
	// Constructeur
	//

	public SetupCategory(String title) {
		m_title = title;
		m_pane = new JPanel(new VerticalLayout(10, VerticalLayout.BOTH));
		m_pane.setBorder(new EmptyBorder(10, 10, 10, 10));
		m_pane.setOpaque(false);
	}
	
	
	//
	// Getters / setters
	//
	
	public String getTitle() {
		return m_title;
	}

	public JPanel getPanel() {
		return m_pane;
	}
	
	public void setPanel(JPanel panel) {
		m_pane = panel;
	}
	
	
	//
	// Attributs
	//
	
	private String m_title = null;
	private JPanel m_pane = null;
	
}
