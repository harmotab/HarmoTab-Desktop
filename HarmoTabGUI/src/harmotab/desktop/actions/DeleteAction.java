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
import harmotab.core.undo.UndoManager;
import harmotab.desktop.DesktopController;
import harmotab.desktop.ActionIcon;
import harmotab.element.Element;
import harmotab.track.Track;


/**
 * Action "Supprimer"
 */
public class DeleteAction extends UserAction {
	private static final long serialVersionUID = 1L;
	
	public DeleteAction() {
		super(
			Localizer.get(i18n.MENU_DELETE_ELEMENT),
			ActionIcon.getIcon(ActionIcon.DELETE)
		);
		setLittleIcon(ActionIcon.getIcon(ActionIcon.DELETE_LITTLE));
	}
	
	@Override
	public void run() {
		ScoreViewSelection selection = DesktopController.getInstance().getCurrentSelection();
		if (selection != null) {
			Element element = selection.getLocationItem().getRootElement();
			Track track = selection.getTrack();
			UndoManager.getInstance().addUndoCommand(track.createRestoreCommand(), Localizer.get(i18n.MENU_DELETE_ELEMENT));
			track.remove(element);
		}
	}
}

