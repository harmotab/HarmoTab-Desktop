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

import harmotab.core.Effect;
import harmotab.core.GlobalPreferences;
import harmotab.element.Element;
import harmotab.element.Tab;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.LocationItem;
import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Dessin d'une tablature sous forme de chiffre/fl�che.
 */
public class AwtArrowTabRenderer extends ElementRenderer {
	
	public AwtArrowTabRenderer() {
		m_blowUp = (GlobalPreferences.getTabBlowDirection() == GlobalPreferences.BLOW_UP);
	}
	

	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		Tab tab = (Tab) element;
		int x = item.getX1();
		int y = item.getY2() - 30;
		int direction = tab.getDirection();
		
		// Affichage du cercle si il y a un bend
		int bend = tab.getBend();
		if (bend != Tab.NONE) {
			// Rempli le cercle si c'est un bend d'1 ton
			if (bend == Tab.FULL_BEND) {
				Color color = g.getColor();
				g.setColor(Color.LIGHT_GRAY);
				g.fillOval(x, y-AwtRenderersResources.BEND_WIDTH+5, AwtRenderersResources.BEND_WIDTH, AwtRenderersResources.BEND_WIDTH);
				g.setColor(color);
			}
			// Affichage du contour du cercle
			g.drawOval(x, y-AwtRenderersResources.BEND_WIDTH+5, AwtRenderersResources.BEND_WIDTH, AwtRenderersResources.BEND_WIDTH);
		}
		
		// Affichage du chiffre
		int hole = tab.getHole();
		if (hole != Tab.UNDEFINED) {
			g.drawString(hole+"", x + (hole > 9 ? 4 : 7), y);
		}
		
		// Affichage de la direction
		if (direction != Tab.UNDEFINED) {
			int yIndex = -1;
			if (m_blowUp) {
				yIndex = (direction == Tab.BLOW ? 1 : 0);
			}
			else {
				yIndex = (direction == Tab.BLOW ? 0 : 1);
			}
			int dirY = y + 5;
			int dirX = x + 2;
			g.drawImage(AwtRenderersResources.m_breathImage, 
					dirX, dirY, dirX + AwtRenderersResources.BREATH_WIDTH, dirY + AwtRenderersResources.BREATH_HEIGHT,
					0, yIndex * AwtRenderersResources.BREATH_HEIGHT, AwtRenderersResources.BREATH_WIDTH, (yIndex+1) * AwtRenderersResources.BREATH_HEIGHT,
					null);
		}
		
		// Affichage des effets
		int effectX = x + (hole < 10 ? 15 : 19);
		
		switch (tab.getEffect().getType()) {
			case Effect.SLIDE:
				g.drawLine(effectX, y - 3, effectX + 10, y - 7);
				break;
			case Effect.WAHWAH:
				g.drawLine(effectX + 0,	y - 3,	effectX + 2,		y - 7);
				g.drawLine(effectX + 2,	y - 7,	effectX + 4,		y - 3);
				g.drawLine(effectX + 4,	y - 3,	effectX + 6,		y - 7);
				g.drawLine(effectX + 6,	y - 7,	effectX + 8,		y - 3);
				g.drawLine(effectX + 8,	y - 3,	effectX + 10,		y - 7);
				break;
		}
		
		// Affichage de la barre au dessus du chiffre en cas de tir�
		if (tab.isPushed()) {
			g.drawLine(x + 3, y - 12, x + 15, y - 12);
		}
		
	}
	
	
	private boolean m_blowUp = true;

}
