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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * Spinner dont la valeur est suivie d'un label pouvant contenir par exemple 
 * l'unit� de la valeur.
 */
public class LabelledSpinner extends JSpinner {
	private static final long serialVersionUID = 1L;
	
	public static final int COLUMNS_FIT_CONTENT = 0;
	
	
	//
	// Constructeurs
	//
	
	public LabelledSpinner(String label) {
		m_labelledSpinnerInstance = this;
		m_labelledSpinnerEditor = new LabelledSpinnerEditor(label);		
		setEditor(m_labelledSpinnerEditor);
		m_labelledSpinnerEditor.updateView();
	}
	
	public LabelledSpinner(String label, SpinnerModel model) {
		m_labelledSpinnerInstance = this;
		m_labelledSpinnerEditor = new LabelledSpinnerEditor(label);		
		setEditor(m_labelledSpinnerEditor);
		setModel(model);
		m_labelledSpinnerEditor.updateView();
	}
	
	public LabelledSpinner(String label, SpinnerModel model, int editorColumns) {
		m_labelledSpinnerInstance = this;
		m_editorColumns = editorColumns;
		m_labelledSpinnerEditor = new LabelledSpinnerEditor(label);		
		setEditor(m_labelledSpinnerEditor);
		setModel(model);
		m_labelledSpinnerEditor.updateView();
	}
			
	
	//
	// Attributs
	//
	
	protected LabelledSpinnerEditor m_labelledSpinnerEditor = null;
	protected LabelledSpinner m_labelledSpinnerInstance = null;
	protected int m_editorColumns = 5;


	//
	// Inner classes
	//
	
	/**
	 * Editeur du spinner comprenant une zone pour afficher le label
	 */
	protected class LabelledSpinnerEditor extends JSpinner.DefaultEditor implements ChangeListener, ActionListener, DocumentListener {
		private static final long serialVersionUID = 1L;
		
		protected JLabel m_label = null;
		protected JTextField m_textField = null;
		protected String m_labelText = null;
		
		public LabelledSpinnerEditor(String label) {
			super(m_labelledSpinnerInstance);
			m_labelText = label;
			
			m_textField = new JTextField(m_editorColumns);
			m_label = new JLabel(" " + m_labelText + " ");
			
			setBorder(m_textField.getBorder());
			setBackground(m_textField.getBackground());
			setForeground(m_textField.getForeground());
			
			m_label.setOpaque(false);
			m_label.setFont(m_textField.getFont());
						
			m_textField.setOpaque(false);
			m_textField.setBorder(new EmptyBorder(0, 0, 0, 0));
			m_textField.setHorizontalAlignment(JTextField.RIGHT);
			
			BorderLayout layout = new BorderLayout();
			layout.setHgap(0);
			layout.setVgap(0);
			setLayout(layout);
			
			add(m_textField, BorderLayout.CENTER);
			add(m_label, BorderLayout.EAST);
			
			updateView();
			m_textField.requestFocus();
			
			m_labelledSpinnerInstance.addChangeListener(this);
			m_textField.addActionListener(this);
			m_textField.getDocument().addDocumentListener(this);
			m_labelledSpinnerInstance.addPropertyChangeListener(this);

		}
		
		
		public void updateModel() {
			try {
				Object value = m_labelledSpinnerInstance.getModel().getValue();
				if (value instanceof Integer) {
					m_labelledSpinnerInstance.setValue(new Integer(m_textField.getText()));
				}
				else if (value instanceof Float) {
					m_labelledSpinnerInstance.setValue(new Float(m_textField.getText()));
				}
				else if (value instanceof Double) {
					m_labelledSpinnerInstance.setValue(new Double(m_textField.getText()));
				}
			}
			catch (Throwable t) {}
		}
		
		public void updateView() {
			String valueString = String.valueOf(m_labelledSpinnerInstance.getValue());
//			if ((valueString.equals("1") || valueString.equals("1.0")) && m_labelText.endsWith("s")) {
//				m_label.setText(" " + m_labelText.substring(0, m_labelText.length()-1) + " ");
//			}
//			else {
//				m_label.setText(" " + m_labelText + " ");
//			}
			m_textField.setText(valueString);
		}
		
		
		@Override
		public void stateChanged(ChangeEvent event) {
			updateView();
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			updateModel();
		}

		@Override
		public void changedUpdate(DocumentEvent event) {
			updateModel();
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			updateModel();
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			updateModel();
		}
		
	}

	
}
