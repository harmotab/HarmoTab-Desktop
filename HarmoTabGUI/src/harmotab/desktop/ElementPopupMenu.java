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

package harmotab.desktop;

import java.awt.*;
import javax.swing.*;
import harmotab.core.*;
import harmotab.desktop.actions.*;
import harmotab.renderer.*;
import harmotab.track.*;


/**
 * Menu contextuel d'édition d'un élément de la partition
 */
public class ElementPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public ElementPopupMenu(Component parent, Point mouseLocation, Score score, LocationItem item) {
		m_score = score;
		m_item = item;
		
		// Construction des composants
		Track track =  m_score.getTrack(m_item.getTrackId());
		m_insertBeforeMenu = AddElementMenu.createInsertBefore(track, item.getRootElement());
		m_insertAfterMenu = AddElementMenu.createInsertAfter(track, item.getRootElement());
		m_deleteMenu = new ActionMenuItem(new DeleteAction());
		
		// Ajout des composants
		add(m_insertBeforeMenu);
		add(m_insertAfterMenu);
		addSeparator();
		add(m_deleteMenu);

		// Affichage du menu
		show(parent, mouseLocation.x, mouseLocation.y);
	}
	
	
	//
	// Attributs
	//
	
	private Score m_score = null;
	private LocationItem m_item = null;
	
	private JMenu m_insertBeforeMenu = null;
	private JMenu m_insertAfterMenu = null;
	private JMenuItem m_deleteMenu = null;
	
}

