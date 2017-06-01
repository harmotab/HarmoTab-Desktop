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
import harmotab.element.Tab;
import harmotab.harmonica.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;


class HarmonicaModelDirectionLabel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final Dimension m_size = new Dimension(25, 20);
	
	
	// 
	// Constructeur
	// 

	public HarmonicaModelDirectionLabel(Harmonica harmonica, Tab tab) {
		m_harmonica = harmonica;
		m_tab = tab;
		
		// Cr�ation des composants
		m_label = new JLabel();
		m_label.setOpaque(true);
		m_label.setPreferredSize(m_size);
		m_label.setHorizontalAlignment(JLabel.CENTER);
		Height h = harmonica.getHeight(m_tab);
		m_label.setText(h != null ? h.getNoteName() + h.getOctave() : "");
		m_label.setBackground(Color.WHITE);
		m_label.setToolTipText(Localizer.get(i18n.N_HOLE) + " " + m_tab.getHole() + ", " + m_tab.getBreathName());
		m_label.setFont(m_label.getFont().deriveFont(10.0f));
		m_label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// Ajout des composants � l'IHM
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		add(m_label);
		
		// Enregistrement des listeners
		MouseObserver mouseObserver = new MouseObserver();
		addMouseListener(mouseObserver);
		m_label.addMouseListener(mouseObserver);
		
		// Affichage du composant
		setPreferredSize(m_size);
		setOpaque(false);
		setBackground(Color.WHITE);
		
	}

	
	//
	// Gestion des actions de l'utilisateur
	//
	
	public void updateContent() {
		Height h = m_harmonica.getHeight(m_tab);
		m_label.setText(h != null ? h.getNoteName() + h.getOctave() : "");
	}
	
	private class MouseObserver implements MouseListener {
	
		@Override
		public void mouseEntered(MouseEvent event) {
			if (m_harmonica.isSet(m_tab)) {
				m_label.setBackground(Color.YELLOW);
			}
			else {
				m_label.setIcon(GuiIcon.getIcon(GuiIcon.ADD));
			}
		}
	
		@Override
		public void mouseExited(MouseEvent event) {
			m_label.setBackground(Color.WHITE);
			m_label.setIcon(null);
		}
	
		
		@Override
		public void mousePressed(MouseEvent event) {
			Height height = m_harmonica.getHeight(m_tab);
			if (height == null)
				height = new Height();

			// Click gauche, modification
			if (event.getButton() == MouseEvent.BUTTON1) {
				String choice = (String)JOptionPane.showInputDialog(
						null,
						Localizer.get(i18n.M_MODEL_NOTE_SELECTION)
							.replace("%HOLE%", m_tab.getHole()+"")
							.replace("%TYPE%", m_tab.getBendStr()),
						Localizer.get(i18n.ET_NOTE_SELECTION),
						JOptionPane.OK_CANCEL_OPTION | JOptionPane.QUESTION_MESSAGE,
						null,
						getAllNoteNames(),
						height.getNoteName() + height.getOctave()
					);
				
				if (choice != null) {
					m_harmonica.setHeight(m_tab, new Height(choice));
				}
			}
			// Click droit, suppression de la hauteur enregistr�es
			else if (event.getButton() == MouseEvent.BUTTON3) {
				m_harmonica.getModel().unset(m_tab);
			}
		}
		
	
		@Override public void mouseClicked(MouseEvent event) {}
		@Override public void mouseReleased(MouseEvent event) {}
		
	}
	
	
	private Object[] getAllNoteNames() {
		ArrayList<Object> result = new ArrayList<Object>();
		for (int octave = Height.MIN_OCTAVE; octave <= Height.MAX_OCTAVE; octave++) {
			for (String noteName : Height.getNotesName()) {
				result.add(noteName + octave);
			}
		}
		return result.toArray();
	}
	
	
	//
	// Attributs
	//

	private JLabel m_label = null;
	private Harmonica m_harmonica = null;
	private Tab m_tab = null;

}

