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

import java.awt.*;
import harmotab.core.*;
import javax.swing.*;


/**
 * Composant de s�lection d'un effet
 */
public class EffectChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	private JComboBox m_instance = null;
	
	
	//
	// Constructeur
	//

	public EffectChooser(Effect effect) {
		m_instance = this;
		
		setRenderer(new EffectRenderer());
		
		DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();
		for (int i = 0; i < Effect.NUMBER_OF_EFFETS; i++)
			model.addElement(i);
		setEffect(effect);
	}
	
	
	// 
	// Getters / setters
	// 
	
	public void setEffect(Effect effect) {
		byte value = effect.getType();
		setSelectedIndex(value);
	}
	
	public Effect getEffect() {
		byte value = (byte) getSelectedIndex();
		return new Effect(value);
	}

	
	//
	// Cell renderer
	//
	
	
	/**
	 * Label d'affichage de l'altération
	 */
	class EffectRenderer extends JPanel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		
		private final static int IMAGE_WIDTH = 40;
		private final static int IMAGE_HEIGHT = 25;

		public EffectRenderer() {
			m_effectLabel = new JLabel();
			Dimension size = new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
			m_effectLabel.setPreferredSize(size);
			m_effectLabel.setOpaque(false);
			
			m_effectNameLabel = new JLabel();
			m_effectNameLabel.setOpaque(false);

			setLayout(new BorderLayout());
			add(m_effectNameLabel, BorderLayout.CENTER);
			add(m_effectLabel, BorderLayout.EAST);
			
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			int selectedIndex = index;

			if (selectedIndex == -1)
				selectedIndex = m_instance.getSelectedIndex();
			if (selectedIndex == -1)
				selectedIndex = 0;

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			if (selectedIndex >= 0) {
				byte effectId = (byte) selectedIndex;
				m_effectNameLabel.setText(Effect.getEffectLocalizedName(effectId));
			}			
			return this;
		}
		
		private JLabel m_effectLabel = null;
		private JLabel m_effectNameLabel = null;
	}
	
	
	//
	// Attributs
	//
	
}

