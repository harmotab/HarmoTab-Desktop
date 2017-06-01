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
 * chiffres fl�ch�s
 */
public class AwtArrowTabAreaRenderer extends ElementRenderer {
	
	public AwtArrowTabAreaRenderer() {
		m_blowUp = (GlobalPreferences.getTabBlowDirection() == GlobalPreferences.BLOW_UP);
	}
	
	
	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		int y = item.m_y1 + (item.m_height / 2);
		int x = item.m_x1 - 10;
		int dirY = -1;
		int dirX = x + 8;
		int yIndex = -1;
		String str = null;

		// Affiche la symbolique sur la premi�re ligne
		if (item.getLine() == 1) {
			g.setColor(Color.DARK_GRAY);
			
			// Affichage du symbole pour les notes aspir�es
			// Affichage de la direction
			yIndex = 1;
			str = m_blowUp ? Localizer.get(i18n.S_BLOW) : Localizer.get(i18n.S_DRAW);
			dirY = y - 20;
			g.drawImage(AwtRenderersResources.m_breathImage, 
					dirX, dirY, dirX + AwtRenderersResources.BREATH_WIDTH, dirY + AwtRenderersResources.BREATH_HEIGHT,		// dest
					0, yIndex * AwtRenderersResources.BREATH_HEIGHT, AwtRenderersResources.BREATH_WIDTH, (yIndex+1) * AwtRenderersResources.BREATH_HEIGHT,	// src
					null);
			g.drawString(str, x, y - 7);
	
			// Affichage du symbole pour les notes souffl�es
			yIndex = 0;
			str = m_blowUp ? Localizer.get(i18n.S_DRAW) : Localizer.get(i18n.S_BLOW);
			dirY = y + 3;
			g.drawImage(AwtRenderersResources.m_breathImage, 
					dirX, dirY, dirX + AwtRenderersResources.BREATH_WIDTH, dirY + AwtRenderersResources.BREATH_HEIGHT,		// dest
					0, yIndex * AwtRenderersResources.BREATH_HEIGHT, AwtRenderersResources.BREATH_WIDTH, (yIndex+1) * AwtRenderersResources.BREATH_HEIGHT,	// src
					null);
			g.drawString(str, x, item.m_y2 - 7);
		}
	}
	
	
	private boolean m_blowUp = true;
	
}
