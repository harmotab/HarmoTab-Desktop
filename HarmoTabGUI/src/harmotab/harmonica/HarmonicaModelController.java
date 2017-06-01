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

package harmotab.harmonica;

import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.io.harmonica.*;
import rvt.util.gui.FileUtilities;

import java.io.File;
import java.io.IOException;
import javax.swing.*;


/**
 * Controlleur d'un modèle d'harmonica.
 */
public class HarmonicaModelController {

	//
	// Constructeur
	//
	
	public HarmonicaModelController(HarmonicaModel model) {
		m_model = model;
		m_writer = null;
		m_modelHasChanged = false;
		
		m_modelObserver = new ModelObserver();
		m_model.addObjectListener(m_modelObserver);
	}
	
	public void finalize() {
		m_model.removeObjectListener(m_modelObserver);
	}
	
	
	//
	// Getters / setters
	//
	
	public HarmonicaModel getModel() {
		return m_model;
	}
	
	public boolean hasHarmonicaModelChanged() {
		return m_modelHasChanged;
	}
	
	
	//
	// M�thodes
	//
	
	// Nouveau modèle
	public boolean createNew() {
		if (close()) {
			m_writer = null;
			m_model.resetModel();
			m_modelHasChanged = true;
			return true;
		}
		return false;
	}
	
	
	// Fermeture du mod�le courante
	public boolean close() {
		if (m_modelHasChanged) {
			int response = JOptionPane.showConfirmDialog(null, 
					Localizer.get(i18n.M_SAVE_MODEL_CHANGES_QUESTION), 
					Localizer.get(i18n.ET_HARMONICA_MODEL_CHANGED), 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (response == JOptionPane.CANCEL_OPTION) {
				return false;
			}
			if (response == JOptionPane.YES_OPTION) {
				return save();
			}
			return true;
		}
		return true;
	}
	
	
	// Ouverture d'un modèle
	public boolean open() {
		if (close()) {
			JFileChooser fileChooser = new JFileChooser();
			HarmonicaModelFileFilter filter = new HarmonicaModelFileFilter(true);
			fileChooser.setFileFilter(filter);
			fileChooser.setCurrentDirectory(new File(GlobalPreferences.getModelsFolder()));
			int ret = fileChooser.showOpenDialog(null);
			
			if (ret == JFileChooser.APPROVE_OPTION) {
				return open(fileChooser.getSelectedFile());
			}
		}
		return false;
	}
	
	public boolean open(File modelFile) {
		HarmonicaModelReader reader = HarmonicaModelReader.createReader(m_model, modelFile);
		try {
			reader.read(modelFile);
		}
		catch (IOException e) {
			ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_FILE_READ_ERROR).replace("%FILE%", modelFile.getName()));
			return false;
		}
		m_writer = HarmonicaModelWriter.createWriter(modelFile);
		// Si c'est un fichier autre que .hmd, ne conserve pas en mémoire le nom
		// pour ne pas l'écraser avec un format .hmd en cas de sauvegarde
		if (!HarmonicaModelFileFilter.isHarmotab3HarmonicaModelExtension(FileUtilities.getExtension(modelFile)))
			m_writer = null;
		m_modelHasChanged = false;
		return true;
	}

	
	// Enregistrement du modèle dans le fichier en cours
	public boolean save() {
		if (m_writer != null) {
			try {
				m_writer.writeFile(m_model);
				m_modelHasChanged = false;
				return true;
			}
			catch (IOException e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage("Error saving file.");
				return false;
			}
		}
		else {
			return saveAs();
		}
	}
	
	// Enregistrement du modèle dans un fichier demandé à l'utilisateur
	public boolean saveAs() {
		JFileChooser fileChooser = new JFileChooser();
		HarmonicaModelFileFilter filter = new HarmonicaModelFileFilter();
		fileChooser.setFileFilter(filter);
		int ret = fileChooser.showSaveDialog(null);
		
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			// Ajout de l'extension si elle n'est pas .hmd
			if (!HarmonicaModelFileFilter.isHarmotab3HarmonicaModelExtension(FileUtilities.getExtension(file))) {
				file = new File(FileUtilities.getNameWithoutExtension(file.getAbsolutePath())
						+ "." + HarmonicaModelFileFilter.HARMOTAB_3_HARMONICA_MODEL_EXTENSION);
			}
			// Si le fichier existe d�j� demande confirmation
			if (file.exists()) {
				int res = JOptionPane.showConfirmDialog(null,
					Localizer.get(i18n.M_FILE_ALREADY_EXISTS_QUESTION).replace("%FILE%", file.getPath()),
					Localizer.get(i18n.MENU_SAVE_AS), 
					JOptionPane.YES_NO_OPTION);
				if (res != JOptionPane.YES_OPTION)
					return false;
			}
			// Enregistrement du fichier
			try {
				m_writer = HarmonicaModelWriter.createWriter(file);
				m_writer.writeFile(m_model);
				m_modelHasChanged = false;
				return true;
			}
			catch (IOException e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage("Error saving file.");
			}
		}
		return false;
	}
	
	
	//
	// Observation des changements du mod�le
	//
	
	private class ModelObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			m_modelHasChanged = true;
		}
	}
	
	
	//
	// Attributs
	//
	
	private HarmonicaModel m_model = null;
	private HarmonicaModelWriter m_writer = null;
	private boolean m_modelHasChanged = false;
	
	private ModelObserver m_modelObserver = null;
	
}

