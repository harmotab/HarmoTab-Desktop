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

import harmotab.core.Height;
import harmotab.element.Element;
import harmotab.element.KeySignature;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.LocationItem;


/**
 * Dessin d'une armure
 */
public class AwtKeySignatureRenderer extends ElementRenderer {

	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		KeySignature ks = (KeySignature) element;

		int x = item.getX1();
		int y = item.getPointOfInterestY();
		int refOrdinate = new Height(Height.G).getOrdinate();
		int spacing = item.getExtra();
		int shifting = (refOrdinate * spacing) - item.getPointOfInterestY();
		
		// Dièses
 		for (byte i = 1; i <= ks.getValue(); i++) {
 			y = KeySignature.getHeight(i).getOrdinate() * spacing - shifting - 9;
			g.drawImage(AwtRenderersResources.m_sharpImage, x+5, y, null);
			x += 12;
		}
 		
 		// Bémols
		for (byte i = -1; i >= ks.getValue(); i--) {
			y = KeySignature.getHeight(i).getOrdinate() * spacing - shifting - 15;
			g.drawImage(AwtRenderersResources.m_flatImage, x+5, y, null);
			x += 12;
		}	}

}
