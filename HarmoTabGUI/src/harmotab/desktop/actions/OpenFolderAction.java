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

import harmotab.core.GlobalPreferences;
import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.desktop.DesktopController;
import harmotab.desktop.ActionIcon;
import harmotab.desktop.browser.BrowsersPane;
import harmotab.desktop.browser.LocalBrowser;
import javax.swing.JFileChooser;


/**
 * Ouverture d'un r�pertoire
 */
public class OpenFolderAction extends UserAction {
	private static final long serialVersionUID = 1L;
	
	public OpenFolderAction() {
		super(
			Localizer.get(i18n.MENU_OPEN_FOLDER),
			ActionIcon.getIcon(ActionIcon.OPEN)
		);
	}

	@Override
	public void run() {
		JFileChooser chooser = new JFileChooser(GlobalPreferences.getScoresBrowsingFolder());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String folder = chooser.getSelectedFile().getAbsolutePath();
			BrowsersPane bp = DesktopController.getInstance().getBrowsersPane();
			((LocalBrowser) bp.getLocalBrowser()).setFolder(folder);
			bp.setSelectedComponent(bp.getLocalBrowser());
		}
	}
}