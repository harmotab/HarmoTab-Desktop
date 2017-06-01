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
import harmotab.desktop.DesktopController;
import harmotab.desktop.ErrorMessenger;
import harmotab.desktop.ActionIcon;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 *  Affichage du contenu de l'aide
 */
public class ShowHelpContentAction extends UserAction {
	private static final long serialVersionUID = 1L;
	
	public ShowHelpContentAction() {
		super(
			Localizer.get(i18n.MENU_HELP_CONTENT),
			ActionIcon.getIcon(ActionIcon.HELP)
		);
	}

	@Override
	public void run() {
		try {
			Desktop.getDesktop().browse(new URI(Localizer.get(i18n.URL_WEBSITE)));
		} 
		catch (IOException e1) {
			e1.printStackTrace();
			ErrorMessenger.showErrorMessage(
					DesktopController.getInstance().getGuiWindow(), 
					Localizer.get(i18n.M_NO_DEFAULT_WEB_BROWSER));
		} 
		catch (URISyntaxException e2) {
		}
	}
}
