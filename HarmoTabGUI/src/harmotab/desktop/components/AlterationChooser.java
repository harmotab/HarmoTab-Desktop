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

import harmotab.core.*;
import harmotab.desktop.*;

import java.awt.*;
import javax.swing.*;


/**
 * ComboBox de s�lection d'une alt�ration (b�car, di�se ou b�mol).
 */
public class AlterationChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	private JComboBox m_instance;

	
	/**
	 * Construction
	 */
	public AlterationChooser(byte alteration) {
		m_instance = this;
				
		setOpaque(true);
		setRenderer(new AlterationLabelRenderer());
		
	    for (int i = 0; i < Height.ALTERATIONS_NUMBER; i++)
	    	addItem(i+"");
	    setSelectedAlteration(Height.NATURAL);
	    
	}
		
	
	/**
	 * Retourne l'alt�ration s�lectionn�e
	 */
	public byte getSelectedAlteration() {
		switch (getSelectedIndex()) {
			case 0:	return Height.NATURAL;
			case 1:	return Height.SHARP;
			case 2:	return Height.FLAT;
		}
		return Height.NATURAL;
	}
	
	
	/**
	 * Indique l'altération sélectionnée
	 */
	public void setSelectedAlteration(byte alteration) {
	    switch (alteration) {
    		case Height.NATURAL:	setSelectedIndex(0);	break;
    		case Height.SHARP:		setSelectedIndex(1);	break;
    		case Height.FLAT:		setSelectedIndex(2);	break;
    		default: throw new IllegalArgumentException("Unhandled alteration '#" + alteration + "'");
	    }
	}
	
	
	/**
	 * Label d'affichage de l'altération
	 */
	class AlterationLabelRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public AlterationLabelRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
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

		    switch (selectedIndex) {
	    		case Height.NATURAL:	setIcon(GuiIcon.getIcon(GuiIcon.ALTERATION_NATURAL));	break;
	    		case Height.SHARP:		setIcon(GuiIcon.getIcon(GuiIcon.ALTERATION_SHARP));		break;
	    		case Height.FLAT:		setIcon(GuiIcon.getIcon(GuiIcon.ALTERATION_FLAT));		break;
		    }
			return this;
		}
	}
	
}
