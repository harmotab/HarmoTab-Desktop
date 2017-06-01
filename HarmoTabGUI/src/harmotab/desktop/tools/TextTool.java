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
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.element.*;
import harmotab.renderer.*;


public class TextTool extends Tool implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//
	
	public TextTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_textElement = (TextElement)item.getElement();

		// Cr�ation des composants
//		m_fontFamilyChooser = new FontFamilyChooser(m_textElement.getFont().getFamily());
//		m_leftButton = new ToolButton(
//				Localizer.get(i18n.TT_ALIGN_LEFT),
//				ToolIcon.ALIGN_LEFT);
//		m_centerButton = new ToolButton(
//				Localizer.get(i18n.TT_ALIGN_CENTER),
//				ToolIcon.ALIGN_CENTER);
//		m_rightButton = new ToolButton(
//				Localizer.get(i18n.TT_ALIGN_RIGHT),
//				ToolIcon.ALIGN_RIGHT);
		
		// Ajout des composants
//		add(m_fontFamilyChooser);
//		addSeparator();
//		add(m_leftButton);
//		add(m_centerButton);
//		add(m_rightButton);
//		addSeparator();
		
		// Ajout du champ d'�dition du texte
		m_textField = new JTextField();
		m_container.add(m_textField);
		m_textField.setBounds(m_locationItem.getX1(), m_locationItem.getY1(), m_locationItem.getWidth(), m_locationItem.getHeight());
		//m_textField.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		// Initialisation des composants
		m_textField.setText(m_textElement.getText());
		m_textField.setFont(m_textElement.getFont());

		String align = m_textElement.getAlignment();
		if (align.equals(TextElement.LEFT))
			m_textField.setHorizontalAlignment(JTextField.LEFT);
		else if (align.equals(TextElement.CENTER))
			m_textField.setHorizontalAlignment(JTextField.CENTER);
		else if (align.equals(TextElement.RIGHT))
			m_textField.setHorizontalAlignment(JTextField.RIGHT);

		// Enregistrement des listeners
//		m_fontFamilyChooser.addActionListener(this);
//		m_leftButton.addActionListener(this);
//		m_centerButton.addActionListener(this);
//		m_rightButton.addActionListener(this);
//		m_textField.addActionListener(this);

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
		if (visible == false) {
			SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					m_container.remove(m_textField);
					if (!m_textField.getText().equals(m_textElement.getText())) {
						UndoManager.getInstance().addUndoCommand(m_textElement.createRestoreCommand(), Localizer.get(i18n.N_SCORE_PROPERTES));
						m_textElement.setText(m_textField.getText());
					}
				}
			});
		}
	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		// Bouton d'alignement � gauche
//		if (e.getSource() == m_leftButton) {
//			m_textField.setHorizontalAlignment(JTextField.LEFT);
//			m_textElement.setAlignment(TextElement.LEFT);
//		}
//		// Bouton d'alignement au centre
//		else if (e.getSource() == m_centerButton) {
//			m_textField.setHorizontalAlignment(JTextField.CENTER);
//			m_textElement.setAlignment(TextElement.CENTER);
//		}
//		// Bouton d'alignement � droite
//		else if (e.getSource() == m_rightButton) {
//			m_textField.setHorizontalAlignment(JTextField.RIGHT);
//			m_textElement.setAlignment(TextElement.RIGHT);
//		}
//		// Bouton de s�lection de la police
//		else if (e.getSource() == m_fontFamilyChooser) {
//			m_textField.setFont(new Font(
//					m_fontFamilyChooser.getSelectedFontFamily(), 
//					m_textElement.getFont().getStyle(), 
//					m_textElement.getFont().getSize())
//					);
//			m_textElement.setFont(m_textField.getFont());
//		}
		// "Entrer" sur le composant d'�dition du texte
		/*else*/ if (e.getSource() == m_textField) {
			setVisible(false);
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	//
	// Attributs
	//

	private TextElement m_textElement = null;
	private JTextField m_textField = null;
//	private FontFamilyChooser m_fontFamilyChooser = null;
//	private JButton m_leftButton = null;
//	private JButton m_centerButton = null;
//	private JButton m_rightButton = null;

}
