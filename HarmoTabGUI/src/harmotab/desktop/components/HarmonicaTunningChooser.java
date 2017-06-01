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

import harmotab.core.Height;
import javax.swing.JComboBox;


/**
 * Composant de s�lection de la tonalit� d'un harmonica.
 */
public class HarmonicaTunningChooser extends JComboBox {
	private static final long serialVersionUID = 1L;

	// 
	// Constructeurs
	// 
	
	public HarmonicaTunningChooser() {
		super(Height.getNotesName());
	}

	public HarmonicaTunningChooser(Height tunning) {
		this();
		setSelectedTunning(tunning);
	}
	
	
	// 
	// M�thodes
	// 
	
	public void setSelectedTunning(Height tunning) {
		setSelectedItem(tunning.getNoteName());
	}
	
	public Height getSelectedTunning() {
		return new Height((String) getSelectedItem());
	}
	
}
