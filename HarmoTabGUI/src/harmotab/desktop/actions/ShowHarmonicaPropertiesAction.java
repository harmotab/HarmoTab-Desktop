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
import harmotab.desktop.setupdialog.ScoreSetupDialog;
import harmotab.desktop.tools.ToolIcon;
import harmotab.track.Track;


/**
 * Action d'affichage des propti�t�s de la l'harmonica de la piste courante.
 */
public class ShowHarmonicaPropertiesAction extends UserAction {
	private static final long serialVersionUID = 1L;

	public ShowHarmonicaPropertiesAction() {
		super(
			Localizer.get(i18n.ET_HARMONICA_SETUP), 
			ToolIcon.getIcon(ToolIcon.TUNE)
		);
	}

	@Override
	public void run() {
		DesktopController dsk = DesktopController.getInstance();
		Track track = dsk.getCurrentSelection().getTrack();
		UndoManager.getInstance().addUndoCommand(track.createRestoreCommand(), i18n.ET_HARMONICA_SETUP);
		ScoreSetupDialog dlg = new ScoreSetupDialog(dsk.getGuiWindow(), dsk.getScoreController());
		dlg.setSelectedTab(1);
		dlg.setVisible(true);
	}
}

