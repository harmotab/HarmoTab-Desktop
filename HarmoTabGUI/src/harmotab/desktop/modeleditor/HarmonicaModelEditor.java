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

package harmotab.desktop.modeleditor;

import harmotab.core.*;
import harmotab.desktop.GuiIcon;
import harmotab.desktop.components.*;
import harmotab.harmonica.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import rvt.util.gui.*;


/**
 * Fen�tre de l'�diteur de mod�le d'harmonica.
 */
public class HarmonicaModelEditor extends JDialog {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	/**
	 * Initialisation et affichage de la fen�tre d'�dition de mod�les d'harmonica
	 */
	public HarmonicaModelEditor(Window parent, HarmonicaModel model) {
		super(parent, Localizer.get(i18n.ET_HARMONICA_MODEL));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImage(GuiIcon.getIcon(GuiIcon.HARMOTAB_ICON_16).getImage());
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		m_harmonica = new Harmonica(model);
		m_modelController = new HarmonicaModelController(model);
		
		// Cr�ation des composants
		m_modelPane = new HarmonicaModelPane(m_harmonica);
		m_modelPane.setBorder(new EmptyBorder(20, 20, 10, 20));
		m_nameTextField = new JTextField(model.getName(), 0);
		m_harmonicaTypeChooser = new HarmonicaTypeChooser(model.getHarmonicaType());
		m_numberOfHolesChooser = new NumberOfHolesChooser(model.getNumberOfHoles());
		m_tunningCombo = new JComboBox(Height.getNotesName());
		m_toolBar = new HarmonicaModelEditorToolbar(m_modelController);
		m_closeButton = new JButton(Localizer.get(i18n.ET_CLOSE_DIALOG));
		

		// Ajout des composants � l'IHM
		JPanel propertiesPane = new JPanel();
		propertiesPane.setLayout(new GridBagLayout());
		propertiesPane.setBorder(new EmptyBorder(10, 20, 20, 20));
		
		GridBagUtilities.setDefaultPadding(new Insets(5, 5, 5, 5), 0, 0);
		
		GridBagUtilities.setDefaultPositionning(GridBagConstraints.EAST, GridBagConstraints.NONE);
		propertiesPane.add(new JLabel(Localizer.get(i18n.ET_TUNNING_NAME)), GridBagUtilities.getConstraints(0, 0));
		propertiesPane.add(new JLabel(Localizer.get(i18n.ET_NUMBER_OF_HOLES)), GridBagUtilities.getConstraints(0, 1));
		propertiesPane.add(new JLabel(Localizer.get(i18n.ET_TUNNING)), GridBagUtilities.getConstraints(3, 1));
		propertiesPane.add(new JLabel(" "), GridBagUtilities.getConstraints(2, 1, 1.0, 0.0));
		
		GridBagUtilities.setDefaultPositionning(GridBagConstraints.WEST, GridBagConstraints.BOTH);
		propertiesPane.add(m_nameTextField, GridBagUtilities.getConstraints(1, 0, 4, 1, 1.0, 0.0));
		propertiesPane.add(m_numberOfHolesChooser, GridBagUtilities.getConstraints(1, 1));
		propertiesPane.add(m_harmonicaTypeChooser, GridBagUtilities.getConstraints(2, 1));
		propertiesPane.add(m_tunningCombo, GridBagUtilities.getConstraints(4, 1));
		propertiesPane.add(m_closeButton, GridBagUtilities.getConstraints(3, 2, 2, 1));
		
		add(m_toolBar, BorderLayout.NORTH);
		add(m_modelPane, BorderLayout.CENTER);
		add(propertiesPane, BorderLayout.SOUTH);

		// Enregistrement des listeners
		addWindowListener(new WindowObserver());
		UserActionOberver userActionObserver = new UserActionOberver();
		m_numberOfHolesChooser.addChangeListener(userActionObserver);
		m_harmonicaTypeChooser.addActionListener(userActionObserver);
		m_tunningCombo.addActionListener(userActionObserver);
		m_closeButton.addActionListener(userActionObserver);
		m_nameTextField.getDocument().addDocumentListener(userActionObserver);
		
		m_harmonicaListener = new HarmonicaChangeListener();
		m_harmonica.addObjectListener(m_harmonicaListener);
		
		// Affichage de la fen�tre
		setSize(550, 400);
		setLocationRelativeTo(parent);
		
	}
	
	public void finalize() {
		m_harmonica.removeObjectListener(m_harmonicaListener);
	}
	
	
	//
	// Getters / setters
	//
	
	public Harmonica getHarmonica() {
		return m_harmonica;
	}
	
	
	//
	// Gestion des �v�nement
	//
	
	/**
	 * R�action aux �v�nements de la fen�tre
	 */
	private class WindowObserver implements WindowListener {
		
		@Override
		public void windowClosing(WindowEvent event) {
			if (m_modelController.close()) {
				dispose();
			}
		}
	
		@Override public void windowActivated(WindowEvent event) {}
		@Override public void windowClosed(WindowEvent event) {}
		@Override public void windowDeactivated(WindowEvent event) {}
		@Override public void windowDeiconified(WindowEvent event) {}
		@Override public void windowIconified(WindowEvent event) {}
		@Override public void windowOpened(WindowEvent event) {}
	}
	
	
	/**
	 * R�action � la modification de l'harmonica
	 */
	private class HarmonicaChangeListener implements HarmoTabObjectListener {

		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			HarmonicaModel model = m_harmonica.getModel();
			
			// Met � jour le nombre de trous affich�
			if (model.getNumberOfHoles() != (Integer) m_numberOfHolesChooser.getValue()) {
				m_numberOfHolesChooser.setValue(model.getNumberOfHoles());
			}
			// Met � jour le nom de l'harmonica
			if (!model.getName().equals(m_nameTextField.getText())) {
				m_nameTextField.setText(model.getName());
			}
			// Met � jour le type d'harmonica
			if (model.getHarmonicaType() != m_harmonicaTypeChooser.getSelectedHarmonicaType()) {
				m_harmonicaTypeChooser.setSelectedHarmonicaType(model.getHarmonicaType());
			}
		}
		
	}
	
	
	/**
	 * Action sur l'un des composants de la fen�tre
	 */
	private class UserActionOberver implements ChangeListener, ActionListener, DocumentListener {

		@Override
		public void stateChanged(ChangeEvent event) {
			// Modification du nombre de trous
			if (event.getSource() == m_numberOfHolesChooser) {
				int numberOfHoles = (Integer) m_numberOfHolesChooser.getValue();
				if (numberOfHoles != m_harmonica.getModel().getNumberOfHoles())
					m_harmonica.getModel().setNumberOfHoles(numberOfHoles);
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// Modification de la tonalit� de l'harmonica
			if (event.getSource() == m_tunningCombo) {
				m_harmonica.setTunning(new Height((String) m_tunningCombo.getSelectedItem())); 
			}
			// Modification du type d'harmonica
			else if (event.getSource() == m_harmonicaTypeChooser) {
				m_harmonica.getModel().setHarmonicaType(m_harmonicaTypeChooser.getSelectedHarmonicaType());
			}
			// Click sur le bouton de fermeture de la fen�tre
			else if (event.getSource() == m_closeButton) {
				if (m_modelController.close()) {
					dispose();
				}
			}
		}
		
		private void modelNameChanged() {
			// Modification du nom de l'harmonica
			m_harmonica.getModel().setName(m_nameTextField.getText());
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
			modelNameChanged();
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			modelNameChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			modelNameChanged();
		}
		
	}
	
		
	//
	// Attributs
	//
	
	private Harmonica m_harmonica = null;
	private HarmonicaModelController m_modelController = null;
	private HarmonicaChangeListener m_harmonicaListener = null;
	
	private JTextField m_nameTextField = null;
	
	private NumberOfHolesChooser m_numberOfHolesChooser = null;
	private HarmonicaTypeChooser m_harmonicaTypeChooser = null;
	private JComboBox m_tunningCombo = null;
	private JToolBar m_toolBar = null;
	private HarmonicaModelPane m_modelPane = null;
	private JButton m_closeButton = null;
		
}
