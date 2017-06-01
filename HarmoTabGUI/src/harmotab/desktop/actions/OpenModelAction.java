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
import harmotab.desktop.ActionIcon;
import harmotab.desktop.modeleditor.HarmonicaModelEditor;
import harmotab.harmonica.HarmonicaModel;
import harmotab.harmonica.HarmonicaModelController;
import java.io.File;


/**
 * Action d'ouverture d'un mod�le d'harmonica
 */
public class OpenModelAction extends UserAction {
	private static final long serialVersionUID = 1L;

	//
	// Constructeurs
	//
	
	public OpenModelAction(String path) {
		super(
			Localizer.get(i18n.MENU_OPEN_MODEL),
			ActionIcon.getIcon(ActionIcon.MODEL)
		);
		m_modelFilePath = path;
	}
	
	
	//
	// Action
	//
	
	@Override
	public void run() {
		HarmonicaModel model = new HarmonicaModel();
		HarmonicaModelController controller = new HarmonicaModelController(model);
		controller.open(new File(m_modelFilePath));
		HarmonicaModelEditor editor = new HarmonicaModelEditor(
				DesktopController.getInstance().getGuiWindow(), model);
		editor.setVisible(true);
	}
	
	
	//
	// Attributs
	//
	
	private String m_modelFilePath = null;

}
