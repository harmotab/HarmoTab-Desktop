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

import javax.swing.JComboBox;


/**
 * Composant de s�lection
 */
public class TabBlowDirectionChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	public final static int BLOW_UP = GlobalPreferences.BLOW_UP;
	public final static int BLOW_DOWN = GlobalPreferences.BLOW_DOWN;
	private static String[] m_choices = {
		Localizer.get(i18n.ET_TAB_BLOW_UP), 
		Localizer.get(i18n.ET_TAB_BLOW_DOWN) };
	
	
	//
	// Constructeurs
	//
	
	public TabBlowDirectionChooser(int direction) {
		super(m_choices);
		setSelectedBlowDirection(direction);		
	}

	public TabBlowDirectionChooser() {
		this(BLOW_UP);
	}
	
	
	//
	// Getters / setters
	//
	
	public int getSelectedBlowDirection() {
		return getSelectedIndex() + 1;
	}
	
	public void setSelectedBlowDirection(int direction) {
		setSelectedIndex(direction - 1);
	}
	
}

