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

package harmotab.desktop.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;


/**
 * Action accessible depuis l'interface utilisateur
 */
public abstract class UserAction extends AbstractAction implements Runnable {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeurs
	//
	
	public UserAction(String label, String description, ImageIcon icon) {
		setLabel(label);
		setDescription(description);
		setIcon(icon);
		setLittleIcon(icon);
	}
	
	public UserAction(String label, ImageIcon icon) {
		this(label, label, icon);
	}
	
	
	//
	// Getters / setters
	//
	
	protected void setLabel(String label) {
		m_actionLabel = label;
	}
	
	public String getLabel() {
		return m_actionLabel;
	}
	
	protected void setDescription(String description) {
		m_actionDescription = description;
	}
	
	public String getDescription() {
		return m_actionDescription;
	}
	
	protected void setIcon(ImageIcon icon) {
		m_actionIcon = icon;
	}
	
	public ImageIcon getIcon() {
		return m_actionIcon;
	}
	
	public void setLittleIcon(ImageIcon icon) {
		m_actionLittleIcon = icon;
	}
	
	public ImageIcon getLittleIcon() {
		return m_actionLittleIcon;
	}
	
	
	//
	// Implémentation AbstractAction
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		//run();
		SwingUtilities.invokeLater(this);
	}
	
	
	//
	// Attributs
	//
	
	protected String m_actionLabel = null;
	protected String m_actionDescription = null;
	protected ImageIcon m_actionIcon = null;
	protected ImageIcon m_actionLittleIcon = null;
}
