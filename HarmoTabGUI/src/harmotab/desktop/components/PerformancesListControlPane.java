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

import java.awt.event.*;
import javax.swing.*;

import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.desktop.setupdialog.*;
import harmotab.desktop.tools.*;
import harmotab.performance.*;
import harmotab.sound.*;


/**
 * Composant de gestion des interpr�tations.
 * Permet la s�lection d'une interpr�tation, l'enregistrement d'une nouvelle
 * ou la modification d'une interpr�tation existante.
 */
public class PerformancesListControlPane extends JToolBar 
implements ActionListener, ScoreControllerListener, RecordingListener {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public PerformancesListControlPane(ScoreController controller) {
		m_scoreController = controller;
		setFloatable(false);
		setOpaque(false);
		setPerformancesList(controller.getPerformancesList());
		controller.addScoreControllerListener(this);
	}
	
	private void constructGui() {
		removeAll();
		// Interface si une liste est fournie
		if (m_perfs != null) {
			// Initialisation des composants
			m_editButton = new ToolButton(Localizer.get(i18n.ET_EDIT_PERFORMANCE), ToolIcon.EDIT);
			m_editButton.setWide(true);
			m_editButton.setEnabled(false);
			m_deleteButton = new ToolButton(Localizer.get(i18n.ET_DELETE_PERFORMANCE), ToolIcon.DELETE);
			m_deleteButton.setWide(true);
			m_deleteButton.setEnabled(false);
			m_performancesChooser = new PerformancesChooser(m_perfs, m_perfs.getDefaultPerformance());
			m_recordingButton = new ToolButton(Localizer.get(i18n.ET_START_RECORD), ToolIcon.START_RECORD);
			m_recordingButton.setWide(true);
			
			// Ajout des composants à l'interface
			add(m_recordingButton);
			addSeparator();
			add(m_performancesChooser);
			add(m_editButton);
			add(m_deleteButton);
			addSeparator();
			
			// Enregistrement des listeners
			m_editButton.addActionListener(this);
			m_deleteButton.addActionListener(this);
			m_performancesChooser.addActionListener(this);
			m_recordingButton.addActionListener(this);			
			
			// Rafraichissement de la fenêtre
			updateUI();
		}
		// Interface si aucune liste n'est fournie
		else {
			m_editButton = null;
			m_deleteButton = null;
			m_performancesChooser = null;
//			JLabel isEditableLabel = new JLabel(ToolIcon.getIcon(ToolIcon.EDITABLE));
//			add(isEditableLabel);
//			addSeparator();
		}
	}
	
	
	//
	// Getters / setters
	//
	
	/**
	 * Modification de la liste d'interprétations gérées par ce composant.
	 */
	protected void setPerformancesList(PerformancesList perfs) {
		m_perfs = perfs;
		constructGui();
	}
	
	
	//
	// Ecoute des modifications du ScoreController
	//
	
	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		setPerformancesList(controller.getPerformancesList());
	}

	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
	}
	
	
	//
	// Ecoute de l'état de l'enregistrement
	//
	
	@Override
	public void onRecordingStarted(RecordingWorker worker) {
		m_recordingButton.setEnabled(false);
		m_performancesChooser.setEnabled(false);
		m_editButton.setEnabled(false);
	}

	@Override
	public void onRecordingStopped(RecordingWorker worker) {
		m_recordingButton.setEnabled(true);
		m_performancesChooser.setEnabled(true);
		m_editButton.setEnabled(true);
	}

	
	//
	// Gestion des actions de l'utilisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		// Bouton d'enregistrement
		if (source == m_recordingButton) {
			PerformanceRecordingSetupDialog dlg = new PerformanceRecordingSetupDialog(null);
			dlg.setVisible(true);
			Recorder recorder = dlg.getRecorder();
			Performance performance = dlg.getPerformance();
			if (recorder != null && performance != null) {
				try {
					RecordingWorker worker = new RecordingWorker(m_scoreController, recorder, performance);
					worker.addRecordingListener(this);
					worker.start();
				}
				catch (Exception e) {
					ErrorMessenger.showErrorMessage("Recording error !");
				}
			}
		}
		// Bouton de modification de l'enregistrement
		else if (source == m_editButton) {
			Performance selectedPerformance = m_performancesChooser.getSelectedPerformance();
			if (selectedPerformance != null) {
				int selectedIndex = m_performancesChooser.getSelectedIndex();
				SetupDialog dlg = new PerformanceSetupDialog(null, selectedPerformance);
				dlg.setVisible(true);
				m_performancesChooser.setSelectedIndex(selectedIndex);
			}
		}
		// Liste de sélection de l'interprétation
		else if (source == m_performancesChooser) {
			Performance selectedPerformance = m_performancesChooser.getSelectedPerformance();
			// Affectaction du lecteur en fonction de l'int�rpr�tation s�lectionn�e
			ScorePlayer player = null;
			if (selectedPerformance != null) {
				Score score = DesktopController.getInstance().getScoreController().getScore();
				player = new PerformanceScorePlayer(selectedPerformance, score);
			}
			m_scoreController.setScorePlayer(player);
			// Activation / d�sactivation du bouton de modification d'un performance
			boolean selectionIsAUserPerformance = (selectedPerformance != null);
			m_editButton.setEnabled(selectionIsAUserPerformance);
			m_deleteButton.setEnabled(selectionIsAUserPerformance);
		}
		// Suppression d'une interpr�tation de la liste
		else if (source == m_deleteButton) {
			Performance selectedPerformance = m_performancesChooser.getSelectedPerformance();
			if (selectedPerformance != null) {
				int res = JOptionPane.showConfirmDialog(
						null,
						Localizer.get(i18n.M_DELETE_PERFORMANCE_CONFIRMATION).replace("%PERF%", selectedPerformance.getName()),
						Localizer.get(i18n.ET_DELETE_PERFORMANCE), 
						JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.YES_OPTION) {
					m_perfs.remove(selectedPerformance);
				}
			}
		}
	}

	
	//
	// Attributs
	//
	
	protected ScoreController m_scoreController;
	protected PerformancesList m_perfs = null;
	
	protected ToolButton m_recordingButton = null;
	protected PerformancesChooser m_performancesChooser = null;
	protected ToolButton m_editButton = null;
	protected ToolButton m_deleteButton = null;
	
}
