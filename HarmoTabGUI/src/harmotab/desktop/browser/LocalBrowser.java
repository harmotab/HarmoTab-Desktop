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

package harmotab.desktop.browser;

import harmotab.core.*;
import harmotab.desktop.actions.OpenScoreAction;
import harmotab.io.score.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import rvt.util.gui.FileField;


/**
 * Panneau de navigation parmis les partitions d'un r�pertoire local.
 */
public class LocalBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public LocalBrowser(ScoreController controller) {
//		m_scoreController = controller;
		m_currentPath = GlobalPreferences.getScoresBrowsingFolder();
		
		// Cr�ation des composants
		m_fileField = new FileField(null, true);
		m_scoreList = new JList(new DefaultListModel());
		m_listScrollPane = new JScrollPane(m_scoreList);
		m_listScrollPane.setOpaque(false);
		m_listScrollPane.getViewport().setOpaque(false);
		m_noScorePane = new JPanel(new BorderLayout());
		m_noScorePane.add(new JLabel(
				"<html><i>" + Localizer.get(i18n.ET_NO_SCORE_IN_FOLDER) + "</i></html>",
				JLabel.CENTER), BorderLayout.CENTER);
		m_noScorePane.setOpaque(false);
		
		// Ajout des composant � l'interface
		setLayout(new BorderLayout(5, 5));
		add(m_fileField, BorderLayout.NORTH);
		add(m_listScrollPane, BorderLayout.CENTER);
		
		// Enregistrement des listeners
		m_fileField.addChangeListener(new PathValidationObserver());
		m_scoreList.addListSelectionListener(new ScoreSelectionObserver());
		m_fileField.addKeyListener(new PathChangeObserver());
		
		// Affichage du composant
		setBorder(new EmptyBorder(5, 5, 5, 5));

		setFolder(m_currentPath);
		
	}
	
	
	//
	// Getters / setters
	//
	
	public void setFolder(String folder) {
		m_fileField.setFile(folder);
		setWaitMode(true);
		new FileListUpdater().start();
	}
	
	
	
	//
	// Gestion des action utilisateur
	//
	
	private class PathValidationObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			setWaitMode(true);
			new FileListUpdater().start();
		}
	}
	
	private class PathChangeObserver implements KeyListener {	
		@Override
		public void keyReleased(KeyEvent event) {
			if (m_fileField.getFile().isDirectory()) {
				setWaitMode(true);
				new FileListUpdater().start();
			}
		}
		
		@Override public void keyTyped(KeyEvent event) {}
		@Override public void keyPressed(KeyEvent event) {}
	}
	
	private class ScoreSelectionObserver implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent event) {
			if (!event.getValueIsAdjusting()) {
				if (m_scoreList.getSelectedValue() != null) {
					setWaitMode(true);
					new ScoreLoader().start();
				}
			}
		}
	}

	
	//
	// M�thodes utilitaires
	//
	
	/**
	 * Met � jour la liste des fichiers du r�pertoire saisi
	 */
	private class FileListUpdater extends Thread {
		@Override
		public void run() {
			// R�initialisation de la liste
			ArrayList<String> files = new ArrayList<String>();
			DefaultListModel model = (DefaultListModel) m_scoreList.getModel();
			model.clear();
			
			// Parcours les fichiers du repertoire � la recherche des partitions
			File folder = m_fileField.getFile();
			m_currentPath = folder.getAbsolutePath();
			if (folder != null && folder.isDirectory()) {
				for (File file : folder.listFiles(new ScoreIOUtilities.ReadableScoreFileFilter())) {
					files.add(file.getName());
				}
			}
			// 
			if (files.size() > 0) {
				Collections.sort(files);
				for (String file : files)
					model.addElement(file);
				
				m_listScrollPane.setViewportView(m_scoreList);
				GlobalPreferences.setScoresBrowsingFolder(folder.getAbsolutePath());
			}
			else {
				m_listScrollPane.setViewportView(m_noScorePane);
			}
			
			setWaitMode(false);
			m_listScrollPane.repaint();
			m_scoreList.repaint();
			m_fileField.requestFocus();
		}
	}
	
	
	/**
	 * Effectue le chargement du fichier s�lectionn�
	 */
	private class ScoreLoader extends Thread {
		@Override
		public void run() {
			String fileName = (String) m_scoreList.getSelectedValue();
			if (fileName != null) {
				String filePath = m_currentPath + File.separator + fileName;
				//m_scoreController.open(filePath);
				OpenScoreAction action = new OpenScoreAction(filePath);
				action.actionPerformed(new ActionEvent(LocalBrowser.this, 0, ""));
			}
			setWaitMode(false);
		}
	}
	
	
	/**
	 * Passage � l'�tat d'attente (composants d�sactiv�s et curseur en sablier)
	 * ou � l'�tat de fonctionnement (composants activ�s et curseur normal).
	 */
	private void setWaitMode(boolean wait) {
		if (wait == true) {
			m_fileField.setEnabled(false);
			m_scoreList.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		else {
			m_fileField.setEnabled(true);
			m_scoreList.setEnabled(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	
	//
	// Attributs
	//
	
//	private ScoreController m_scoreController = null;
	private FileField m_fileField = null;
	private JScrollPane m_listScrollPane = null;
	private JList m_scoreList = null;
	private JPanel m_noScorePane = null;
	private String m_currentPath = null;
	
}
