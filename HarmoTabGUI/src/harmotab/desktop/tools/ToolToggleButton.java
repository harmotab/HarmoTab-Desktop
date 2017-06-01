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

import harmotab.desktop.actions.UserAction;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;


/**
 * Classe m�re des boutons ON/OFF utilis�s dans les barres d'outils de 
 * modification des �l�ments affich�s sur la partition
 */
public class ToolToggleButton extends JToggleButton implements MouseListener {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WIDTH = 32;
	private static final int DEFAULT_HEIGHT = 32;

	//
	// Constructeur
	//
	
	public ToolToggleButton(String text, byte icon) {
		setIcon(ToolIcon.getIcon(icon));
		setToolTipText(text);
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setFocusable(false);
		setBorderPainted(true);
		setContentAreaFilled(false);
		addMouseListener(this);
	}
	
	public ToolToggleButton(UserAction action) {
		setIcon(action.getIcon());
		setToolTipText(action.getDescription());
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setFocusable(false);
		setBorderPainted(true);
		setContentAreaFilled(false);
		addMouseListener(this);
		addActionListener(action);
	}
	

	/**
	 * Affichage du fond d'une couleur diff�rente en fonction de la s�lection
	 */
	@Override
	public void setSelected(boolean selected) {
		if (selected) {
			setContentAreaFilled(true);
		}
		super.setSelected(selected);
	}
		
	
	//
	// Impl�mentation de MouseListener
	//
	
	@Override
	public void mouseEntered(MouseEvent e) {
		setContentAreaFilled(isEnabled());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setContentAreaFilled(isEnabled() && isSelected());
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	
}

