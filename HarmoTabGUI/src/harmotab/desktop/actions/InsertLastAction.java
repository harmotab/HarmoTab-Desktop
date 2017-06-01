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

import harmotab.core.Localizer;
import harmotab.core.ScoreViewSelection;
import harmotab.core.i18n;
import harmotab.desktop.AddElementMenu;
import harmotab.desktop.DesktopController;
import harmotab.desktop.ActionIcon;
import harmotab.track.Track;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;


/**
 * Action "Ins�rer � la fin"
 */
public class InsertLastAction extends UserAction {
	private static final long serialVersionUID = 1L;
	
	public InsertLastAction() {
		super(
			Localizer.get(i18n.MENU_INSERT_LAST),
			ActionIcon.getIcon(ActionIcon.ADD_LAST)
		);
		setLittleIcon(ActionIcon.getIcon(ActionIcon.ADD_LAST_LITTLE));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		m_displayX = 0;
		m_displayY = ((JComponent) e.getSource()).getHeight();
		m_source = (Component) e.getSource();
		super.actionPerformed(e);
	}

	@Override
	public void run() {
		ScoreViewSelection selection = DesktopController.getInstance().getCurrentSelection();
		if (selection != null) {
			Track track = selection.getTrack();
			AddElementMenu menu = AddElementMenu.createInsertLast(track);
			menu.populate();
			JPopupMenu popup = menu.getPopupMenu();
			popup.show(m_source, m_displayX, m_displayY);
		}
	}
	
	private int m_displayX = 0;
	private int m_displayY = 0;
	private Component m_source = null;
}

