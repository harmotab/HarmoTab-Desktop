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

package rvt.util.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Composant graphique de s�lection d'un fichier
 */
public class FileField extends JPanel {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public FileField(String path, boolean folder) {
		m_onlyFolder = folder;
		
		// Cr�ation des composants
		m_pathField = new JTextField(path);
		m_browseButton = new JButton("...");
		
		// Ajout des composants � l'interface
		setLayout(new BorderLayout(5, 5));
		add(m_pathField, BorderLayout.CENTER);
		add(m_browseButton, BorderLayout.EAST);
		
		// Enregistrement des listeners
		m_browseButton.addActionListener(new BrowseAction());
		m_pathField.addActionListener(new PathChangedObserver());
		
		// Affichage du composant
		setOpaque(false);
		
	}
	
	public FileField() {
		this("", false);
	}
	
	public FileField(boolean folder) {
		this("", folder);
	}
	
	
	//
	// Getters / setters
	//
	
	public File getFile() {
		if (m_pathField.getText().trim().equals(""))
			return null;
		File file = new File(m_pathField.getText());
		return file;
	}
	
	public void setFile(String path) {
		m_pathField.setText(path);
	}
	
	public void setFile(File file) {
		setFile(file.getAbsolutePath());
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_pathField.setEnabled(enabled);
		m_browseButton.setEnabled(enabled);
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	public void addChangeListener(ChangeListener listener) {
		m_listeners.add(ChangeListener.class, listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		m_listeners.remove(ChangeListener.class, listener);
	}
	
	
	public void firePathChange() {
		for (ChangeListener listener : m_listeners.getListeners(ChangeListener.class))
			listener.stateChanged(new ChangeEvent(this));
	}
	
	@Override
	public void addKeyListener(KeyListener listener) {
		m_pathField.addKeyListener(listener);
	}
	
	
	//
	// Actions utilisateur
	//
	
	private class BrowseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			JFileChooser chooser = new JFileChooser(getFile());
			if (m_onlyFolder == true)
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				m_pathField.setText(chooser.getSelectedFile().getAbsolutePath());
				firePathChange();
			}
		}
		
	}
	
	
	private class PathChangedObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			firePathChange();
		}
	}
	
	
	@Override
	public void requestFocus() {
		m_pathField.requestFocus();
	}
	
	
	
	//
	// Attributs
	//
	
	private JTextField m_pathField = null;
	private JButton m_browseButton = null;
	private boolean m_onlyFolder = false;
	
	private EventListenerList m_listeners = new EventListenerList();
	
}
