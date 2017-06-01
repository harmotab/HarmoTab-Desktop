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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import harmotab.element.TimeSignature;

import javax.swing.JComboBox;
import javax.swing.JPanel;


/**
 * Composant de s�lection de signature temporelle (ex: 4/4, 6/8...).
 */
public class TimeSignatureChooser extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	//
	// Interface des listeners du composant
	//
	public interface TimeSignatureListener {
		public void onNumberChanged(byte number);
		public void onReferenceChanged(byte reference);
	}
	
	
	//
	// Constructeurs
	//

	public TimeSignatureChooser(byte number, byte reference) {
		m_number = number;
		m_reference = reference;

		m_numberCombo = new NumberCombo();
		m_referenceCombo = new ReferenceCombo();
		
		setOpaque(false);
		setLayout(new FlowLayout());
		add(m_numberCombo);
		add(m_referenceCombo);
				
		m_numberCombo.addActionListener(this);
		m_referenceCombo.addActionListener(this);
	}
	
	public TimeSignatureChooser(TimeSignature timeSignature) {
		this(timeSignature.getNumber(), timeSignature.getReference());
	}
	
	public TimeSignatureChooser() {
		this(new TimeSignature());
	}
	
	
	//
	// Getters / setters
	//
	
	public byte getNumber() {
		return m_number;
	}
	
	public byte getReference() {
		return m_reference;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_numberCombo.setEnabled(enabled);
		m_referenceCombo.setEnabled(enabled);
	}
	
	
	//
	// Actions de l'utlisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == m_numberCombo) {
			m_number = (byte) (m_numberCombo.getSelectedIndex() + TimeSignature.MIN_NUMBER);
			fireNumberChanged();
		}
		else if (event.getSource() == m_referenceCombo) {
			m_reference = (byte) Integer.parseInt((String) m_referenceCombo.getSelectedItem());
			fireReferenceChanged();
		}
		
	}
	

	//
	// Gestion des listeners
	//
	
	public void addTimeSignatureListener(TimeSignatureListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeTimeSignatureListener(TimeSignatureListener listener) {
		m_listeners.remove(listener);
	}
	
	protected void fireNumberChanged() {
		for (TimeSignatureListener listener : m_listeners) {
			listener.onNumberChanged(m_number);
		}
	}
	
	protected void fireReferenceChanged() {
		for (TimeSignatureListener listener : m_listeners) {
			listener.onReferenceChanged(m_reference);
		}
	}
	
	
	//
	// Composants graphiques
	//

	class NumberCombo extends JComboBox {
		private static final long serialVersionUID = 1L;

		public NumberCombo() {
			int selected = 0;
			for (byte i = TimeSignature.MIN_NUMBER; i <= TimeSignature.MAX_NUMBER; i++) {
				addItem(i+"");
				if (m_number == i)
					selected = getItemCount()-1;
			}
			setSelectedIndex(selected);
		}
	}
	
	class ReferenceCombo extends JComboBox {
		private static final long serialVersionUID = 1L;
		
		public ReferenceCombo() {
			int selected = 0;
			for (byte i = TimeSignature.MIN_REFERENCE; i <= TimeSignature.MAX_REFERENCE; i++) {
				if (TimeSignature.isReferenceValid(i)) {
					addItem(i+"");
					if (m_reference == i)
						selected = getItemCount()-1;
				}
			}
			setSelectedIndex(selected);
		}
	}
	
	
	//
	// Attributs
	//
	
	private NumberCombo m_numberCombo;
	private ReferenceCombo m_referenceCombo;
	private final ArrayList<TimeSignatureListener> m_listeners = new ArrayList<TimeSignatureListener>();
	
	private byte m_number = 0;
	private byte m_reference = 0;
	
}


