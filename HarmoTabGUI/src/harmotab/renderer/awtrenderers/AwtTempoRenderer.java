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
import harmotab.element.Element;
import harmotab.element.Tempo;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.LocationItem;


public class AwtTempoRenderer extends ElementRenderer {

	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		Tempo tempo = (Tempo) element;
		g.drawImage(AwtRenderersResources.m_tempoImage, item.getX1(), item.getY1() + 3, null);
		g.drawString(" = " + tempo.getValue(), item.getX1() + 12, item.getY2() - 4);
	}

}
