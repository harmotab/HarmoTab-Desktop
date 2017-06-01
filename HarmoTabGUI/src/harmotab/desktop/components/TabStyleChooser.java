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

import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.awtrenderers.AwtArrowTabRenderer;
import harmotab.renderer.awtrenderers.AwtTabularTabRenderer;
import javax.swing.JComboBox;


/**
 * Composant de sélection du style de tablature
 */
public class TabStyleChooser extends JComboBox {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeur
	//
	
	public TabStyleChooser(int styleIndex)	{
		super(m_labels);
		setSelectedIndex(styleIndex);
	}
	
	
	//
	// Getters / setters
	//
		
	public static ElementRenderer getRenderer(int styleIndex) {
		switch (styleIndex) {
			case 0:	return new AwtArrowTabRenderer();
			case 1: return new AwtTabularTabRenderer();
		}
		return null;
	}
	
	
	//
	// Attributs
	//
	
	public static String[] m_labels = {Localizer.get(i18n.ET_ARROW_TAB_STYLE), Localizer.get(i18n.ET_TABULAR_TAB_STYLE)};
	
}

