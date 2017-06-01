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
import harmotab.desktop.DesktopController;
import harmotab.desktop.ActionIcon;
import harmotab.desktop.actions.ActionButton;
import harmotab.desktop.actions.UserAction;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;


/**
 * Permet de construire des boite de dialogue de configuration.
 */
public abstract class SetupDialog extends JDialog implements WindowListener {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//

	public SetupDialog(Window parent, String title) {
		super(parent != null ? parent : DesktopController.getInstance().getGuiWindow(), title);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		m_categories = new Vector<SetupCategory>();
		
		// Cr�ation des composants de l'interface
		m_tabbedPane = new JTabbedPane();

		// Insertion des composants
		JPanel dialogButtonsPane = new JPanel(new GridLayout(1, 2, 5, 5));
		dialogButtonsPane.add(new ActionButton(new CancelAction()));
		dialogButtonsPane.add(new ActionButton(new OkAction()));
		
		JPanel bottomPane = new JPanel(new BorderLayout());
		bottomPane.add(dialogButtonsPane, BorderLayout.EAST);

		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout(10, 10));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(m_tabbedPane, BorderLayout.CENTER);
		contentPane.add(bottomPane, BorderLayout.SOUTH);
		
		// Enregistrement des listeners
		addWindowListener(this);
		
		// Affichage de la fenètre
		setSize( 600, 600 );
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(getParent());
	}
	
	
	//
	// Actions Ok / Annuler
	//
	
	private class OkAction extends UserAction {
		private static final long serialVersionUID = 1L;

		public OkAction() {
			super(Localizer.get(i18n.ET_OK), ActionIcon.getIcon(ActionIcon.OK));
		}

		@Override
		public void run() {
			if (save() == true) {
				dispose();
			}
		}
	}
	
	private class CancelAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public CancelAction() {
			super(Localizer.get(i18n.ET_CANCEL), ActionIcon.getIcon(ActionIcon.CANCEL));
		}

		@Override
		public void run() {
			discard();
			dispose();
		}
		
	}
	
	
	//
	// Gestion de la fen�tre
	//
	
	/**
	 * Enregistre les donn�es entr�es dans la fen�tre et ferme.
	 * L'action d'enregistrement et de fermeture est annul�e si la m�thode 
	 * retourne false.
	 */
	protected abstract boolean save();
	
	/**
	 * Ferme la fen�tre sans enregistrer les donn�es.
	 */
	protected abstract void discard();

	
	protected Window getWindow() {
		return this;
	}
	
	
	//
	// Gestion du contenu
	//
	
	public void setSelectedTab(int index) {
		m_tabbedPane.setSelectedIndex(index);
	}
	
	protected void displayCategory(SetupCategory setupCategory) {
		m_tabbedPane.setSelectedIndex(m_categories.indexOf(setupCategory));
	}
	
	protected void addSetupCategory(SetupCategory setupCategory) {
		m_categories.add(setupCategory);
		JScrollPane scrollPane = new JScrollPane(setupCategory.getPanel());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		m_tabbedPane.addTab(setupCategory.getTitle(), scrollPane);
	}
	
	
	protected Component getSetupField(String title, JComponent component, String help) {
		JPanel fieldPanel = new JPanel(new BorderLayout());
		fieldPanel.setOpaque(false);

		// Titre de la configuration
		if (title != null) {
			JLabel label = new JLabel(title + (title.length() > 0 ? " : " : ""));
			label.setPreferredSize(new Dimension(120, 20));
			label.setOpaque(false);
			fieldPanel.add(label, BorderLayout.WEST);
		}
		
		// Composant de modification de la configuration
		fieldPanel.add(component, BorderLayout.CENTER);
		
		// Message d'aide
		if (help != null) {
			JTextArea helpLabel = new JTextArea();
			
			helpLabel.setLineWrap(true);
			helpLabel.setText(help);
			helpLabel.setFont(new JTextField().getFont());
			helpLabel.setOpaque(false);
			helpLabel.setEditable(false);
			helpLabel.setForeground(Color.GRAY);
			helpLabel.setWrapStyleWord(true);
			helpLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
			
			JPanel centerPane = new JPanel(new BorderLayout());
			centerPane.setOpaque(false);
			centerPane.add(component, BorderLayout.CENTER);
			centerPane.add(helpLabel, BorderLayout.SOUTH);
			fieldPanel.add(centerPane, BorderLayout.CENTER);
		}
		return fieldPanel;
	}
	
	protected Component createSetupField(String title, JComponent component) {
		return getSetupField(title, component, null);
	}
	
	protected Component createSetupSeparator(String title) {
		JPanel separatorPane = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel(title);
		titleLabel.setPreferredSize(new Dimension(200, 25));
		titleLabel.setBorder( new MatteBorder(0, 0, 1, 0, Color.GRAY));
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
		titleLabel.setForeground(new Color(0x6283BF));
		titleLabel.setOpaque(false);
		separatorPane.add(titleLabel, BorderLayout.CENTER);
		separatorPane.setBorder(new EmptyBorder(8, 0, 5, 0));
		separatorPane.setOpaque(false);
		return separatorPane;
	}
	
	
	//
	// Ev�nements de la fen�tre
	//
	
	@Override
	public void windowClosing(WindowEvent event) {
		save();
	}
	
	
	@Override public void windowActivated(WindowEvent event) {}
	@Override public void windowClosed(WindowEvent event) {}
	@Override public void windowDeactivated(WindowEvent event) {}
	@Override public void windowDeiconified(WindowEvent event) {}
	@Override public void windowIconified(WindowEvent event) {}
	@Override public void windowOpened(WindowEvent event) {}
		
	
	//
	// Attributs
	//
	
	private JTabbedPane m_tabbedPane = null;
	private Vector<SetupCategory> m_categories = null;
	
}
