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
import java.awt.image.*;
import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.renderer.awtrenderers.AwtKeySignatureRenderer;
import javax.swing.*;


/**
 * ComboBox de s�lection d'un tonalit� (signature).
 */
public class TonalityChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	private JComboBox m_instance = null;
	
	
	//
	// Constructeur
	//

	public TonalityChooser(byte tonality) {
		m_instance = this;
		
		setRenderer(new TonalityRenderer());
		for (int i = KeySignature.MIN_KEY_SIGNATURE; i <= KeySignature.MAX_KEY_SIGNATURE; i++)
			addItem(i);
		setTonality(tonality);
		
	}
	
	
	// 
	// Getters / setters
	// 
	
	public void setTonality(byte tonality) {
		int value = tonality - KeySignature.MIN_KEY_SIGNATURE;
		setSelectedIndex(value);
	}
	
	public byte getTonality() {
		int value = (byte) getSelectedIndex() + KeySignature.MIN_KEY_SIGNATURE;
		return (byte) value;
	}

	
	//
	// Cell renderer
	//
	
	
	/**
	 * Label d'affichage de l'altération
	 */
	class TonalityRenderer extends JPanel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
		
		private final static int IMAGE_WIDTH = 90;
		private final static int IMAGE_HEIGHT = 40;

		public TonalityRenderer() {
			m_alterationsLabel = new JLabel();
			Dimension size = new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
			m_alterationsLabel.setPreferredSize(size);
			m_alterationsLabel.setOpaque(false);
			
			m_tonalityNameLabel = new JLabel();
			m_tonalityNameLabel.setOpaque(false);

			setLayout(new BorderLayout());
			add(m_tonalityNameLabel, BorderLayout.CENTER);
			add(m_alterationsLabel, BorderLayout.EAST);
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			int selectedIndex = index;

			if (selectedIndex == -1) {
				selectedIndex = m_instance.getSelectedIndex();
			}
			if (selectedIndex == -1) {
				selectedIndex = 0;
			}

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			if (selectedIndex >= 0) {
				int tonality = selectedIndex + KeySignature.MIN_KEY_SIGNATURE;
				m_tonalityNameLabel.setText(KeySignature.getTonalityName(tonality));
				
				KeySignature signature = new KeySignature((byte) tonality);
				LocationItem item = new LocationItem(signature, 0, 15, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, -1, 0, 0, 2);
				
				BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
				new AwtKeySignatureRenderer().paint((Graphics2D) image.getGraphics(), signature, item);
				m_alterationsLabel.setIcon(new ImageIcon(image));
			}			
			return this;
		}
		
		private JLabel m_alterationsLabel = null;
		private JLabel m_tonalityNameLabel = null;
	}
	
	
	//
	// Attributs
	//
	
}

