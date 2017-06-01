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

import harmotab.core.GlobalPreferences;
import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.element.*;
import java.awt.*;
import javax.swing.*;
import res.ResourceLoader;


/**
 * Composant de s�lection d'une direction (aspir� / souffl�, avec bends et 
 * overblows).
 */
public class DirectionChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	private static final int UP_FULL_BEND_ID = 0;
	private static final int UP_HALF_BEND_ID = 1;
	private static final int UP_NO_BEND_ID = 2;
	private static final int UNDEFINED_ID = 3;
	private static final int DOWN_NO_BEND_ID = 4;
	private static final int DOWN_HALF_BEND_ID = 5;
	private static final int DOWN_FULL_BEND_ID = 6;
	
	
	//
	// Chargement static des images
	//
	
	private static ImageIcon[] m_icons = null;
	
	static {
		ResourceLoader loader = ResourceLoader.getInstance();
		
		m_icons = new ImageIcon[7];
		m_icons[UP_FULL_BEND_ID] = new ImageIcon(loader.loadImage("/res/controllers/up-full-bend.png"));
		m_icons[UP_HALF_BEND_ID] = new ImageIcon(loader.loadImage("/res/controllers/up-half-bend.png"));
		m_icons[UP_NO_BEND_ID] = new ImageIcon(loader.loadImage("/res/controllers/up-no-bend.png"));
		m_icons[UNDEFINED_ID] = new ImageIcon(loader.loadImage("/res/controllers/undefined.png"));
		m_icons[DOWN_NO_BEND_ID] = new ImageIcon(loader.loadImage("/res/controllers/down-no-bend.png"));
		m_icons[DOWN_HALF_BEND_ID] = new ImageIcon(loader.loadImage("/res/controllers/down-half-bend.png"));
		m_icons[DOWN_FULL_BEND_ID] = new ImageIcon(loader.loadImage("/res/controllers/down-full-bend.png"));
	}
	
	
	//
	// Constructeur
	// 
	
	public DirectionChooser(Tab tab) {
		m_instance = this;
		m_blowUp = (GlobalPreferences.getTabBlowDirection() == GlobalPreferences.BLOW_UP);
		
		m_labels = new String[7];
		if (m_blowUp) {
			m_labels[UP_FULL_BEND_ID] = Localizer.get(i18n.N_FULL_OVERBLOW);
			m_labels[UP_HALF_BEND_ID] = Localizer.get(i18n.N_HALF_OVERBLOW);
			m_labels[UP_NO_BEND_ID] = Localizer.get(i18n.N_BLOW);
			m_labels[UNDEFINED_ID] = "";
			m_labels[DOWN_NO_BEND_ID] = Localizer.get(i18n.N_DRAW);
			m_labels[DOWN_HALF_BEND_ID] = Localizer.get(i18n.N_HALF_BEND);
			m_labels[DOWN_FULL_BEND_ID] = Localizer.get(i18n.N_FULL_BEND);	
		}
		else {
			m_labels[UP_FULL_BEND_ID] = Localizer.get(i18n.N_FULL_BEND);
			m_labels[UP_HALF_BEND_ID] = Localizer.get(i18n.N_HALF_BEND);
			m_labels[UP_NO_BEND_ID] = Localizer.get(i18n.N_DRAW);
			m_labels[UNDEFINED_ID] = "";
			m_labels[DOWN_NO_BEND_ID] = Localizer.get(i18n.N_BLOW);
			m_labels[DOWN_HALF_BEND_ID] = Localizer.get(i18n.N_HALF_OVERBLOW);
			m_labels[DOWN_FULL_BEND_ID] = Localizer.get(i18n.N_FULL_OVERBLOW);
		}
		
		setOpaque(true);
		setRenderer(new DirectionLabelRenderer());

    	addItem(UP_FULL_BEND_ID + "");
    	addItem(UP_HALF_BEND_ID + "");
    	addItem(UP_NO_BEND_ID + "");
    	addItem(UNDEFINED_ID + "");
    	addItem(DOWN_NO_BEND_ID + "");
    	addItem(DOWN_HALF_BEND_ID + "");
    	addItem(DOWN_FULL_BEND_ID + "");
    	
    	setTab(tab);
	}
	
	public DirectionChooser() {
		this(null);
	}
	
	
	//
	// Getters / setters
	//
	
	public Tab getTab(int hole) {
		int selected = getSelectedIndex();
		
		byte direction = Tab.UNDEFINED;
		if (selected > UNDEFINED_ID) {
			direction = m_blowUp ? Tab.DRAW : Tab.BLOW;
		}
		if (selected < UNDEFINED_ID) {
			direction = m_blowUp ? Tab.BLOW : Tab.DRAW;
		}
		
		byte bend = Tab.NONE;
		if (selected == DOWN_HALF_BEND_ID || selected == UP_HALF_BEND_ID) {
			bend = Tab.HALF_BEND;
		}
		if (selected == DOWN_FULL_BEND_ID || selected == UP_FULL_BEND_ID) {
			bend = Tab.FULL_BEND;
		}
		
		return new Tab(hole, direction, bend);
	}
	
	
	public void setTab(Tab tab) {
		if (tab == null) {
			setSelectedIndex(UNDEFINED_ID);
			return ;
		}
		
	    switch (tab.getDirection()) {
	    	case Tab.UNDEFINED:
	    		setSelectedIndex(UNDEFINED_ID);
	    		break;
	    	case Tab.BLOW:
	    		if (tab.getBend() == Tab.NONE)
	    			setSelectedIndex(m_blowUp ? UP_NO_BEND_ID : DOWN_NO_BEND_ID);
	    		else if (tab.getBend() == Tab.HALF_BEND)
	    			setSelectedIndex(m_blowUp ? UP_HALF_BEND_ID : DOWN_HALF_BEND_ID);
	    		else if (tab.getBend() == Tab.FULL_BEND)
	    			setSelectedIndex(m_blowUp ? UP_FULL_BEND_ID : DOWN_FULL_BEND_ID);
	    		break;
	    	case Tab.DRAW:	
	    		if (tab.getBend() == Tab.NONE)
	    			setSelectedIndex(m_blowUp ? DOWN_NO_BEND_ID : UP_NO_BEND_ID);
	    		else if (tab.getBend() == Tab.HALF_BEND)
	    			setSelectedIndex(m_blowUp ? DOWN_HALF_BEND_ID : UP_HALF_BEND_ID);
	    		else if (tab.getBend() == Tab.FULL_BEND)
	    			setSelectedIndex(m_blowUp ? DOWN_FULL_BEND_ID : UP_FULL_BEND_ID);
	    		break;
	    }
	}
	
	
	//
	// Renderer
	//
	
	class DirectionLabelRenderer extends JPanel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public DirectionLabelRenderer() {
			m_iconLabel = new JLabel();
			m_iconLabel.setHorizontalAlignment(JLabel.CENTER);
			m_iconLabel.setVerticalAlignment(JLabel.CENTER);
			m_stringLabel = new JLabel();
			
			setLayout(new BorderLayout(5, 5));
			add(m_iconLabel, BorderLayout.WEST);
			add(m_stringLabel, BorderLayout.CENTER);
			setBackground(Color.WHITE);
			setPreferredSize(new Dimension(170, 25));
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

			setToolTipText(m_labels[selectedIndex]);
			m_iconLabel.setIcon(m_icons[selectedIndex]);
			m_stringLabel.setText(m_labels[selectedIndex]);
			return this;
		}
		
		private JLabel m_iconLabel = null;
		private JLabel m_stringLabel = null;
	}
	
	
	//
	// Attributs
	//
	
	private JComboBox m_instance;
	private boolean m_blowUp = true;
	private String[] m_labels = null;
	
}
