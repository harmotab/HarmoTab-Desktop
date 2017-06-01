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

import harmotab.core.Figure;
import java.awt.*;
import javax.swing.*;
import res.ResourceLoader;



public class FigureChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Chargement des images
	//

	private static ImageIcon[] m_figureIcons = null;
	private static ImageIcon[] m_restIcons = null;
	
	static {
		ResourceLoader loader = ResourceLoader.getInstance();
		m_figureIcons = new ImageIcon[Figure.FIGURES_NUMBER];
		m_restIcons = new ImageIcon[Figure.FIGURES_NUMBER];

		// Images de figures normales
		m_figureIcons[0] = new ImageIcon(loader.loadImage("/res/controllers/whole.png"));
		m_figureIcons[1] = new ImageIcon(loader.loadImage("/res/controllers/half.png"));
		m_figureIcons[2] = new ImageIcon(loader.loadImage("/res/controllers/quarter.png"));
		m_figureIcons[3] = new ImageIcon(loader.loadImage("/res/controllers/eighth.png"));
		m_figureIcons[4] = new ImageIcon(loader.loadImage("/res/controllers/sixteenth.png"));
		m_figureIcons[5] = new ImageIcon(loader.loadImage("/res/controllers/appogiature.png"));

		// Images de figures en "silence"
		m_restIcons[0] = new ImageIcon(loader.loadImage("/res/controllers/rest-whole.png"));
		m_restIcons[1] = new ImageIcon(loader.loadImage("/res/controllers/rest-half.png"));
		m_restIcons[2] = new ImageIcon(loader.loadImage("/res/controllers/rest-quarter.png"));
		m_restIcons[3] = new ImageIcon(loader.loadImage("/res/controllers/rest-eighth.png"));
		m_restIcons[4] = new ImageIcon(loader.loadImage("/res/controllers/rest-sixteenth.png"));
		m_restIcons[5] = new ImageIcon(loader.loadImage("/res/controllers/rest-appogiature.png"));
	}
	
	//
	// Constructeur
	//
	
	public FigureChooser(Figure figure) {
		
		m_instance = this;
		setDisplayRests(false);
		
		setOpaque(true);
		setRenderer(new FigureLabelRenderer());
		
	    for (int i = Figure.MIN_FIGURE_ID; i <= Figure.MAX_FIGURE_ID; i++)
	    	addItem(i+"");
	    
	    switch (figure.getType()) {
	    	case Figure.WHOLE:			setSelectedIndex(0);		break;
	    	case Figure.HALF:			setSelectedIndex(1);		break;
	    	case Figure.QUARTER:		setSelectedIndex(2);		break;
	    	case Figure.EIGHTH:			setSelectedIndex(3);		break;
	    	case Figure.SIXTEENTH:		setSelectedIndex(4);		break;
	    	case Figure.APPOGIATURE:	setSelectedIndex(5);		break;
	    }

	}
	
	
	//
	// Setters / getters
	//
	
	public void setDisplayRests(boolean displayRests) {
		m_displayRests = displayRests;
	}
	
	public boolean getDisplayRests() {
		return m_displayRests;
	}
	
	
	public Figure getSelectedFigure() {
		switch (getSelectedIndex()) {
			case 0:	return new Figure(Figure.WHOLE);
			case 1:	return new Figure(Figure.HALF);
			case 2:	return new Figure(Figure.QUARTER);
			case 3: return new Figure(Figure.EIGHTH);
			case 4:	return new Figure(Figure.SIXTEENTH);
			case 5:	return new Figure(Figure.APPOGIATURE);
			default: return null;
		}
	}
	
	
	//
	// Attributs
	//
	
	private JComboBox m_instance;
	private boolean m_displayRests;

	
	//
	// Renderer du combo
	//
	
	class FigureLabelRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public FigureLabelRenderer() {
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

			if (m_displayRests == false)
				setIcon(m_figureIcons[selectedIndex]);
			else
				setIcon(m_restIcons[selectedIndex]);

			return this;
		}
		
	}
	
}
