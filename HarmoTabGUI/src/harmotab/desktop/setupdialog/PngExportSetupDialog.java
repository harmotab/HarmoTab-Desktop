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

package harmotab.desktop.setupdialog;

import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.io.score.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.*;
import rvt.util.gui.*;


public class PngExportSetupDialog extends SetupDialog {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public PngExportSetupDialog(Window parent, Score score) {
		super(parent, "Image export");
		m_score = score;
		
		// Cr�ation des composants
		ButtonGroup pagingOptionsGroup = new ButtonGroup();
		m_onePageButton = new JRadioButton(Localizer.get(i18n.ET_ONE_FILE_WHOLE_SCORE), true);
		m_onePageButton.setOpaque(false);
		m_severalPagesButton = new JRadioButton(Localizer.get(i18n.ET_ONE_FILE_PER_PAGE), false);
		m_severalPagesButton.setOpaque(false);
		
		pagingOptionsGroup = new ButtonGroup();		
		pagingOptionsGroup.add(m_onePageButton);
		pagingOptionsGroup.add(m_severalPagesButton);
		
		JPanel pagingPane = new JPanel();
		pagingPane.setOpaque(false);
		pagingPane.setLayout(new BoxLayout(pagingPane, BoxLayout.PAGE_AXIS));
		pagingPane.add(m_onePageButton);
		pagingPane.add(m_severalPagesButton);
		
		m_widthSpinner = new LabelledSpinner("px", new SpinnerNumberModel(900, 200, Integer.MAX_VALUE, 10));
		m_widthSpinner.setOpaque(false);
		m_heightSpinner = new LabelledSpinner("px", new SpinnerNumberModel(1200, 200, Integer.MAX_VALUE, 10));
		m_heightSpinner.setOpaque(false);
		m_heightSpinner.setEnabled(false);
		
		JPanel sizePanel = new JPanel(new GridLayout(2, 3));
		sizePanel.setOpaque(false);
		sizePanel.add(new JLabel(Localizer.get(i18n.ET_WIDTH_LABEL)));
		sizePanel.add(m_widthSpinner);
		sizePanel.add(new JLabel(" "));
		sizePanel.add(new JLabel(Localizer.get(i18n.ET_HEIGHT_LABEL)));
		sizePanel.add(m_heightSpinner);
		sizePanel.add(new JLabel(" "));
		
		m_outputFolderField = new FileField(GlobalPreferences.getUserDefaultDirectory(), true);
		m_outputFileNameField = new FileField(m_score.getScoreName() + ".png", false);
		
		// Ajout des composants � l'interface
		SetupCategory exportSetupCategory = new SetupCategory(Localizer.get(i18n.ET_EXPORT_PNG_IMAGE_SETUP_CATEGORY));
		JPanel pane = exportSetupCategory.getPanel();
		pane.add(createSetupField(Localizer.get(i18n.ET_PAGES), pagingPane));
		pane.add(createSetupSeparator(Localizer.get(i18n.ET_PAGE_FORMAT)));
		pane.add(createSetupField(Localizer.get(i18n.ET_SIZE), sizePanel));
		pane.add(createSetupSeparator(Localizer.get(i18n.ET_OUTPUT_FILE)));
		pane.add(createSetupField(Localizer.get(i18n.ET_OUTPUT_FOLDER), m_outputFolderField));
		pane.add(createSetupField(Localizer.get(i18n.ET_FILENAME), m_outputFileNameField));
		addSetupCategory(exportSetupCategory);
		
		// Enregistrement des listeners
		RadioToggleObserver radioToggleListener = new RadioToggleObserver();
		m_onePageButton.addActionListener(radioToggleListener);
		m_severalPagesButton.addActionListener(radioToggleListener);
		m_outputFileNameField.addChangeListener(new FileNameChangedListener());
		
	}

	
	//
	// Gestion des actions utilisateur
	//

	@Override
	protected boolean save() {
		String path = "";

		// V�rification du nom de fichier indiqu�
		if (m_outputFileNameField.getFile() == null) {
			ErrorMessenger.showErrorMessage(getWindow(), Localizer.get(i18n.M_NO_FILENAME_ERROR));
			return false;
		}
		
		// V�rification du nom de r�pertoire indiqu�
		if (m_outputFolderField.getFile() == null || !m_outputFolderField.getFile().exists()
				|| !m_outputFolderField.getFile().isDirectory()) {
			ErrorMessenger.showErrorMessage(getWindow(), Localizer.get(i18n.M_NO_FOLDER_NAME_ERROR));
			return false;			
		}
			
		// R�cup�ration du nom de fichier
		path = 
			m_outputFolderField.getFile().getAbsolutePath() +
			File.separator + 
			m_outputFileNameField.getFile().getName();
		if (path.toUpperCase().endsWith(".PNG"))
			path = path.substring(0, path.length()-4);
		
		// R�cup�ration du format d�sir�
		int width = ((Number) m_widthSpinner.getValue()).intValue();
		int height = ((Number) m_heightSpinner.getValue()).intValue();
		
		if (m_onePageButton.isSelected() == true)
			height = Integer.MAX_VALUE;
		
		// Cr�ation des images
		ScoreWriter writer = new PngFileWriter(m_score, path, width, height);
		try {
			writer.save();
		} catch (IOException e) {
			e.printStackTrace();
			ErrorMessenger.showErrorMessage(getWindow(), Localizer.get(i18n.M_FILE_WRITE_ERROR)
					.replace("%FILE%", path));
		}

		return true;
	}
	
	@Override
	protected void discard() {
	}
	
	
	private class RadioToggleObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			m_heightSpinner.setEnabled(m_severalPagesButton.isSelected());
		}
	}
	
	private class FileNameChangedListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			File file = m_outputFileNameField.getFile();
			if (file != null) {
				if (file.getParent() != null && !file.getParent().equals(""))
					m_outputFolderField.setFile(file.getParent());
				m_outputFileNameField.setFile(file.getName());
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	private Score m_score = null;
	
	private JRadioButton m_onePageButton = null;
	private JRadioButton m_severalPagesButton = null;
	private JSpinner m_widthSpinner = null;
	private JSpinner m_heightSpinner = null;
	private FileField m_outputFolderField = null;
	private FileField m_outputFileNameField = null;

}
