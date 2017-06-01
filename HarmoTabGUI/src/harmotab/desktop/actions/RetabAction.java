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
import harmotab.core.i18n;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.DesktopController;
import harmotab.desktop.ActionIcon;
import harmotab.desktop.setupdialog.TabModelWizard;
import harmotab.harmonica.TabModel;
import harmotab.harmonica.TabModelController;
import harmotab.track.HarmoTabTrack;


/**
 * Retab de la p pour un autre h en passant par l'utilisation
 * d'un mod�le
 */
public class RetabAction extends UserAction {
	private static final long serialVersionUID = 1L;

	public RetabAction() {
		super(
			Localizer.get(i18n.MENU_RETAB),
			ActionIcon.getIcon(ActionIcon.WIZARD)
		);
	}

	@Override
	public void run() {
		TabModelWizard tabModelWizzard = new TabModelWizard(DesktopController.getInstance().getGuiWindow());
		tabModelWizzard.setModal(true);
		tabModelWizzard.setVisible(true);
		
		TabModel tabModel = tabModelWizzard.getTabModel();
		if (tabModel != null) {
			HarmoTabTrack htTrack = (HarmoTabTrack) DesktopController.getInstance().getScoreController().getScore().getTrack(HarmoTabTrack.class, 0);
			UndoManager.getInstance().addUndoCommand(htTrack.createRestoreCommand(), Localizer.get(i18n.MENU_RETAB));
			htTrack.setTabModel(tabModel);
			htTrack.setHarmonica(tabModelWizzard.getHarmonica());
			TabModelController tabModelController = new TabModelController(tabModel);
			tabModelController.updateTabs(htTrack);
		}
	}

}
