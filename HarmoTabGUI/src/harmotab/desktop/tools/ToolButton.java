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
import java.awt.event.*;
import javax.swing.*;


/**
 * Classe m�re des boutons affich� dans les boites d'outils de moficiation des
 * �l�ments affich�s sur la partition 
 */
public class ToolButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 1L;

	//
	// Constructeurs
	//

	public ToolButton(String toolTip, String text) {
		setText(text);
		setToolTipText(toolTip);
		setFocusable(false);
		setBorderPainted(true);
		setContentAreaFilled(false);
		addMouseListener(this);
		Dimension dim = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setPreferredSize(dim);
	}
	
	public ToolButton(String toolTip, byte icon, String text) {
		this(toolTip, text);
		setIcon(ToolIcon.getIcon(icon));
		setWide(true);
	}
	
	public ToolButton(String toolTip, byte icon) {
		this(toolTip, "");
		setIcon(ToolIcon.getIcon(icon));
	}

	public ToolButton(UserAction action) {
		this(action.getDescription(), action.getLabel());
		setIcon(action.getIcon());
		addActionListener(action);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setWide(boolean wide) {
		if (wide) {
			setPreferredSize(null);
		}
		else {
			Dimension dim = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
			setPreferredSize(dim);
		}
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
		setContentAreaFilled(false);
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	
	
	//
	// Attributs
	//
	
	private static final int DEFAULT_WIDTH = 32;
	private static final int DEFAULT_HEIGHT = 32;
	
}
