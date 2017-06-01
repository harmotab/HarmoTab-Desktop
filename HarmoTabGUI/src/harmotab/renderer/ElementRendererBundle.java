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

package harmotab.renderer;

import java.awt.*;


/**
 * Objet permettant le rendu des diff�rents �l�ments d'une partition
 */
public abstract class ElementRendererBundle {
	
	//
	// Constructeur
	//
	
	public ElementRendererBundle() {
		setMode(RenderingMode.VIEW_MODE);
	}
	
	abstract public void reset();

	
	//
	// Getters / setters
	//
	
	public void setMode(RenderingMode mode) {
		if (mode == RenderingMode.EDIT_MODE) {
			setDrawEditingHelpers(true);
			setDrawEditingWarnings(true);
		}
		else if (mode == RenderingMode.VIEW_MODE) {
			setDrawEditingHelpers(false);
			setDrawEditingWarnings(false);
		}
		else {
			throw new IllegalArgumentException("Unknown mode #" + mode);
		}
	}
	
	
	public void setDrawEditingHelpers(boolean draw) {
		m_drawEditingHelpers = draw;
	}
	
	public boolean getDrawEditingHelpers() {
		return m_drawEditingHelpers;
	}
	
	
	public void setDrawEditingWarnings(boolean draw) {
		m_drawEditingWarnings = draw;
	}
	
	public boolean getDrawEditingWarnings() {
		return m_drawEditingWarnings;
	}
	
	
	//
	// Affichage des �l�ments
	//
	
	public abstract void paintElement(Graphics2D g, LocationItem item);
	
	
	//
	// Attributs
	//

	private boolean m_drawEditingHelpers;
	private boolean m_drawEditingWarnings;
		
}
