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

import java.awt.Window;
import java.awt.event.*;
import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.desktop.components.*;
import harmotab.desktop.modeleditor.*;
import harmotab.element.*;
import harmotab.harmonica.*;

import javax.swing.*;


/**
 * Boite de dialogue de cr�ation d'un mod�le de tablature � partir d'un mod�le
 * d'harmonica
 */
public class TabModelWizard extends SetupDialog {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeurs
	//
	
	public TabModelWizard(Window parent) {
		super(parent, Localizer.get(i18n.ET_TAB_MAPPING_WIZARD));
		
		m_harmonica = new Harmonica();
		m_tabModel = null;
		
		// Construction des composants
		m_harmonicaModelChooser = new HarmonicaModelChooser();
		m_modelEditorButton = new JButton(Localizer.get(i18n.ET_OPEN_MODEL_EDITOR), ActionIcon.getIcon(ActionIcon.WIZARD));
		m_tonalityCombo = new JComboBox(Height.getNotesName());
		m_mappingReferenceChooser = new MappingReferenceChooser(m_harmonica);
		m_harmonicaNameLabel = new JLabel("<html><i>" + Localizer.get(i18n.ET_NO_HARMONICA_MODEL_LOADED) + "</i></html>");
		
		// Ajout des composants � l'interface
		Box buttonsBox = new Box(BoxLayout.X_AXIS);
		buttonsBox.add(m_harmonicaModelChooser);
		buttonsBox.add(Box.createHorizontalGlue());
		buttonsBox.add(m_modelEditorButton);
		
		SetupCategory setupCategory = new SetupCategory(Localizer.get(i18n.ET_CREATE_TAB_MODEL));
		addSetupCategory(setupCategory);
		JPanel panel = setupCategory.getPanel();
		panel.add(createSetupSeparator(Localizer.get(i18n.N_HARMONICA_MODEL)));
		panel.add(getSetupField(null, new JLabel(), Localizer.get(i18n.M_HARMONICA_MODEL_DESC)));
		panel.add(createSetupField(Localizer.get(i18n.N_HARMONICA_MODEL_FILE), buttonsBox));
		panel.add(createSetupField("", m_harmonicaNameLabel));
		panel.add(createSetupField(Localizer.get(i18n.N_TONALITY), m_tonalityCombo));	
		panel.add(createSetupSeparator(Localizer.get(i18n.N_TAB_NOTE_MAPPING)));
		panel.add(getSetupField(null, new JLabel(), Localizer.get(i18n.M_REFERENCE_DESC)));
		panel.add(createSetupField(Localizer.get(i18n.N_REFERENCE_NOTE), m_mappingReferenceChooser));
		
		// Enregistrement des listeners
		UserActionObserver listener = new UserActionObserver();
		m_harmonicaModelChooser.addActionListener(listener);
		m_modelEditorButton.addActionListener(listener);
		m_tonalityCombo.addActionListener(listener);
		
		// Chargement du mod�le par d�faut
		HarmonicaModel defaultModel = m_harmonicaModelChooser.getSelectedModel();
		if (defaultModel != null) {
			m_harmonica.setModel(defaultModel);
			listener.fireModelUpdated();
		}
		
	}
	
	
	//
	// Gestion des donn�es
	//
	
	@Override
	protected boolean save() {
		if (m_tabModel == null)
			m_tabModel = new TabModel();
				
		Height referenceHeight = m_mappingReferenceChooser.getSelectedHeight();
		Tab referenceTab = m_mappingReferenceChooser.getSelectedTab();
		
		// Message d'erreur si aucune r�f�rence n'est s�lectionn�e
		if (referenceHeight == null || referenceTab == null) {
			ErrorMessenger.showErrorMessage(getWindow(), Localizer.get(i18n.M_NO_REFERENCE_ERROR));
			return false;
		}

		TabModelController controller = new TabModelController(m_tabModel);
		controller.populateFromHarmonicaModel(m_harmonica, referenceHeight, referenceTab);
		
		return true;
	}
	

	@Override
	protected void discard() {
		m_tabModel = null;
		m_harmonica = null;
	}
	
	
	public void setTabModel(TabModel model) {
		m_tabModel = model;
	}
	
	public TabModel getTabModel() {
		return m_tabModel;
	}
	
	public Harmonica getHarmonica() {
		return m_harmonica;
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	private class UserActionObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// Action sur le bouton de chargement d'un mod�le
			if (event.getSource() == m_harmonicaModelChooser) {
				fireModelUpdated();
			}
			// Action sur le bouton d'ouverture de l'�diteur de mod�les
			else if (event.getSource() == m_modelEditorButton) {
				HarmonicaModel model = m_harmonica.getModel();
				HarmonicaModelEditor editor = new HarmonicaModelEditor(null, model);
				editor.setVisible(true);
				m_harmonica.setName(m_harmonica.getModel().getName());
				m_harmonicaNameLabel.setText(m_harmonica.getName() + ", " + model.getNumberOfHoles() + " " + Localizer.get(i18n.N_HOLES) + ".");
				m_tonalityCombo.setSelectedItem(editor.getHarmonica().getTunning().getNoteName());
			}
			// Action sur le composant de s�lection de la tonalit� de l'harmonica
			else if (event.getSource() == m_tonalityCombo) {
				m_harmonica.setTunning(new Height((String) m_tonalityCombo.getSelectedItem()));
			}
		}
		
		public void fireModelUpdated() {
			HarmonicaModel model = m_harmonicaModelChooser.getSelectedModel();
			m_harmonica.setModel(model);
			m_harmonica.setName(model.getName());
			m_harmonicaNameLabel.setText(model.getName() + ", " + model.getNumberOfHoles() + " " + Localizer.get(i18n.N_HOLES) + ".");
		}
	}
	
	
	//
	// Attributs
	//

	private Harmonica m_harmonica = null;
	private TabModel m_tabModel = null;
	
	private HarmonicaModelChooser m_harmonicaModelChooser = null;
	private JButton m_modelEditorButton = null;
	private JLabel m_harmonicaNameLabel = null;
	private JComboBox m_tonalityCombo = null;
	private MappingReferenceChooser m_mappingReferenceChooser = null;
	
}
