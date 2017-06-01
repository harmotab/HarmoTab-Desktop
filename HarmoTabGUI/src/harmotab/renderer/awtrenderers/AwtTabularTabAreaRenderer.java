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

package harmotab.renderer.awtrenderers;

import java.awt.Color;
import java.awt.Graphics2D;

import harmotab.core.GlobalPreferences;
import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.element.Element;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.LocationItem;


/**
 * Dessin de la zone de tablatures d'une port�e avec tablatures sous forme de 
 * tableau
 */
public class AwtTabularTabAreaRenderer extends ElementRenderer {
	
	public AwtTabularTabAreaRenderer() {
		m_blowUp = (GlobalPreferences.getTabBlowDirection() == GlobalPreferences.BLOW_UP);
	}
	
	
	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		int y = item.m_y1 + (item.m_height / 2);
		int x = 0;

		// Affichage de la barre horizontale
		g.setColor(AwtRenderersResources.m_defaultForeground);
		g.setFont(AwtRenderersResources.m_defaultFont);
		g.drawLine(item.m_x1, y, item.getPointOfInterestX(), y);
		
		// Affichage du début de ligne
		if (item.getLine() == 1) {
			g.setColor(Color.DARK_GRAY);
			x = item.m_x1 + 10;
			if (m_blowUp) {
				g.drawString(Localizer.get(i18n.S_BLOW), x, y - 7);
				g.drawString(Localizer.get(i18n.S_DRAW), x, item.m_y2 - 7);
			}
			else {
				g.drawString(Localizer.get(i18n.S_DRAW), x, y - 7);
				g.drawString(Localizer.get(i18n.S_BLOW), x, item.m_y2 - 7);
			}
		}
		
	}
	
	
	private boolean m_blowUp = true;
	
}
