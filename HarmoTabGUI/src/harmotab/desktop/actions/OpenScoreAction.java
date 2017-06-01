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
import harmotab.core.ScoreController;
import harmotab.core.i18n;
import harmotab.desktop.DesktopController;
import harmotab.desktop.ErrorMessenger;
import harmotab.desktop.ActionIcon;
import harmotab.desktop.RecentFilesManager;
import harmotab.throwables.FileFormatException;
import harmotab.throwables.ScoreIoException;


/**
 *  Ouverture d'une partition
 */
public class OpenScoreAction extends UserAction {
	private static final long serialVersionUID = 1L;

	public OpenScoreAction() {
		super(
			Localizer.get(i18n.MENU_OPEN_SCORE),
			ActionIcon.getIcon(ActionIcon.OPEN)
		);
	}
	
	public OpenScoreAction(String path) {
		this();
		m_filepath = path;
	}
	
	@Override
	public void run() {
		ScoreController controller = DesktopController.getInstance().getScoreController();
		try {
			boolean openned = false;
			if (m_filepath != null) {
				openned = controller.open(m_filepath); 
			}
			else {
				openned = controller.open();
			}
			if (openned == true) {
				RecentFilesManager.getInstance().addRecentFile(controller.getCurrentScoreWriter().getFile().getAbsolutePath());
			}
		}
		catch (ScoreIoException exception) {
			if (exception.getCause() != null && exception.getCause() instanceof FileFormatException) {
				ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_FILE_FORMAT_ERROR));
			}
			else {
				ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_FILE_OPENING_ERROR).replace("%FILE%", exception.getFilePath()));
			}
		}
	}
	
	private String m_filepath = null;
}

