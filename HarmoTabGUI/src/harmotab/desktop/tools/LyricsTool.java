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

package harmotab.desktop.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.components.*;
import harmotab.element.*;
import harmotab.renderer.*;


/**
 * Boite � outil de modification d'un �l�ment textuel
 */
public class LyricsTool extends Tool implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//
	
	public LyricsTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_lyrics = (Lyrics) item.getElement();

		// Cr�ation des composants
		m_durationChooser = new DurationChooser(m_lyrics.getDurationObject());
		
		// Ajout des composants
		add(m_durationChooser);
		
		// Ajout du champ d'�dition du texte
		m_textField = new JTextField();
		m_container.add(m_textField);
		m_textField.setBounds(m_locationItem.getX1(), m_locationItem.getY1(), m_locationItem.getWidth(), m_locationItem.getHeight());
		
		// Initialisation des composants
		m_textField.setText(m_lyrics.getText());

		// Enregistrement des listeners
		m_textField.addActionListener(this);
		m_durationChooser.addChangeListener(new DurationChangesObserver());

		// S�lection du texte apr�s l'affichage du composant
		SwingUtilities.invokeLater(
		new Runnable() {
			public void run() {
				m_textField.setSelectionStart(0);
				m_textField.setSelectionEnd(m_textField.getText().length());
				m_textField.requestFocus();
			}
		});
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			m_container.remove(m_textField);
			
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				validateText();
				}
			});
		}
	}
	
	
	private void validateText() {
		if (!m_textField.getText().equals(m_lyrics.getText())) {
			UndoManager.getInstance().addUndoCommand(m_lyrics.createRestoreCommand(), Localizer.get(i18n.N_LYRICS));
			m_lyrics.setText(m_textField.getText());
		}
	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// "Entrer" sur le composant d'�dition du texte
		if (e.getSource() == m_textField) {
			setVisible(false);
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	private class DurationChangesObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			// Modification de la dur�e
			if (event.getSource() == m_durationChooser) {
				m_lyrics.setDispachEvents(false, null);
				validateText();
				m_lyrics.setDispachEvents(true, null);
				UndoManager.getInstance().addUndoCommand(m_lyrics.createRestoreCommand(), Localizer.get(i18n.N_DURATION));
				m_lyrics.setDurationObject(new Duration(m_durationChooser.getDurationValue()));
			}
		}
	}
	
	
	//
	// Attributs
	//

	private Lyrics m_lyrics;
	
	private JTextField m_textField;
	private DurationChooser m_durationChooser;

}
