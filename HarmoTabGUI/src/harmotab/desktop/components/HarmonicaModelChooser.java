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

package harmotab.desktop.components;

import harmotab.core.*;
import harmotab.harmonica.*;
import harmotab.io.harmonica.*;
import java.io.File;
import java.util.Vector;
import javax.swing.JComboBox;
import rvt.util.gui.FileUtilities;


/**
 * Composant de s�lection d'un mod�le d'harmonica.
 * Choix parmis les mod�les contenus dans le r�pertoire des mod�les.
 */
public class HarmonicaModelChooser extends JComboBox {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//

	public HarmonicaModelChooser() {
		super(getModelsList());
		if (getModel().getSize() > 1) {
			setSelectedIndex(1);
		}
	}
	
	
	public static Vector<String> getModelsList() {
		File modelsFolder = new File(GlobalPreferences.getModelsFolder());

		m_modelsName.add("");
		m_modelsPath.add(null);
		
		try {
			if (modelsFolder.exists() && modelsFolder.isDirectory()) {
				for (String filename : modelsFolder.list()) {
					if (filename.endsWith(HarmonicaModelReader.HARMOTAB_3_MODEL_FILE_EXTENSION)) {
						m_modelsName.add(FileUtilities.getNameWithoutExtension(filename));
						m_modelsPath.add(modelsFolder.getAbsolutePath() + File.separatorChar + filename);
					}
				}
			}
		}
		catch (NullPointerException e) {
			System.err.println("Model folder: " + modelsFolder);
			e.printStackTrace();
		}
		return m_modelsName;
	}

	
	//
	// Getters / setters
	//
	
	public String getSelectedModelPath() {
		int index = getSelectedIndex();
		if (index == -1)
			return null;
		return m_modelsPath.get(index);
	}
	
	public HarmonicaModel getSelectedModel() {
		HarmonicaModelController controller = new HarmonicaModelController(new HarmonicaModel());
		String modelPath = getSelectedModelPath();
		if (modelPath != null) {
			controller.open(new File(modelPath));
		}
		return controller.getModel();
	}
	
	
	//
	// Attributs
	//
	 
	private static Vector<String> m_modelsName = new Vector<String>();
	private static Vector<String> m_modelsPath = new Vector<String>();
}
