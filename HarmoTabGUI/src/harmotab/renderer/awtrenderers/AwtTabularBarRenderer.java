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

import java.awt.Graphics2D;
import harmotab.core.GlobalPreferences;
import harmotab.core.RepeatAttribute;
import harmotab.element.Bar;
import harmotab.element.Element;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.LocationItem;
import harmotab.renderer.LocationItemFlag;


/**
 * Classe de dessins d'�lements de partitions affichant les tablatures sous
 * forme de tableau aspir�/souffl� sous la port�e
 */
public class AwtTabularBarRenderer extends ElementRenderer {

	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		Bar bar = (Bar) element;
		final int BAR_HEIGHT = 32;
		
		RepeatAttribute repeat = bar.getRepeatAttribute();
		int x = item.getPointOfInterestX();
		int y = item.getPointOfInterestY();

		// Affichage d'une barre simple
		g.drawLine(x, y, x, y + BAR_HEIGHT);
			
		// D�but de r�p�tition
		if (repeat.isBeginning() && !item.getFlag(LocationItemFlag.IMPLICIT_PHRASE_START)) {
			g.fillRect(x - 1, y, 3, BAR_HEIGHT);
			g.drawLine(x + 4, y, x + 4, y + BAR_HEIGHT);
			if (item.getFlag(LocationItemFlag.DRAW_REPEAT_SYMBOL)) {
				g.fillOval(x + 8, y + 12, 3, 3);
				g.fillOval(x + 8, y + 20, 3, 3);
			}
		}
		
		// Fin de r�p�tition
		if (repeat.isEnd() && !item.getFlag(LocationItemFlag.IMPLICIT_PHRASE_END)) {
			g.fillRect(x - 1, y, 3, BAR_HEIGHT);
			g.drawLine(x - 5, y, x - 5, y + BAR_HEIGHT);
			if (repeat.getRepeatTimes() > 1) {
				g.fillOval(x - 12, y + 10, 4, 4);
				g.fillOval(x - 12, y + 18, 4, 4);
			}
		}
		
		// Affichage du nombre de r�p�titions
		if (repeat.getRepeatTimes() > 2 && !item.getFlag(LocationItemFlag.IMPLICIT_PHRASE_END)) {
			g.drawString(repeat.getRepeatTimes()+"x", x - 14, y - 15);
		}
		
		// Fin alt�rnative
		if (repeat.isAlternateEnding())
			System.err.println("ElementRenderer:paintElement:Bar: Alternate ending not handled.");
		
		// Affichage du num�ro de mesure
		if (GlobalPreferences.isBarNumbersDisplayed()) {
			g.setFont(AwtRenderersResources.m_barNumberFont);
			g.setColor(AwtRenderersResources.m_barNumberColor);
			g.drawString(item.getExtra()+"", x - 5, y - 3);
		}
	}

}
