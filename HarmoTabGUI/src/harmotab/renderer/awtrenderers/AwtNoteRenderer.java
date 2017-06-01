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
import harmotab.element.Note;
import harmotab.renderer.ElementRenderer;
import harmotab.renderer.LocationItem;
import harmotab.renderer.LocationItemFlag;


/**
 * Classe de dessin d'une note en utilisant les m�thodes de dessins fournies 
 * par AWT.
 */
public class AwtNoteRenderer extends ElementRenderer {
	
	public static final int REVERSE_QUEUE_ORDINATE = 85;
	

	@Override
	public void paint(Graphics2D g, Element element, LocationItem item) {
		Note note = (Note) element;
		
		final int NOTE_WIDTH = 20;
		final int NOTE_HEIGHT = 30;
		final int BLACK_BODY_INDEX = 6;
		
		int x = item.getX1();
		int y = item.getPointOfInterestY() - 24;
		int xIndex = note.getFigure().getType() - 1;
		int yIndex = 0;
		int ordinate = note.getHeight().getOrdinate();
		int spacing = item.getExtra();
		int shifting = (ordinate * spacing) - item.getPointOfInterestY();
		boolean isRest = note.isRest();
		
		// Force le sens de la queue si nécessaire
		if (isRest || item.getFlag(LocationItemFlag.FORCE_QUEUE_UP))
			yIndex = 0;
		// Inversion de la queue si nécessaire
		else if (ordinate < REVERSE_QUEUE_ORDINATE || item.getFlag(LocationItemFlag.FORCE_QUEUE_UP)) {
			yIndex = 1;
			y += 18;
		}
				
		// Silences
		if (isRest) {
			yIndex = 2;
			ordinate = 85;
		}
		
		// Croche accrochée : n'affiche que le corps
		if (note.isHookable() && !note.isRest()) {
			if (!item.getFlag(LocationItemFlag.STAND_ALONE))
				xIndex = BLACK_BODY_INDEX;
		}
		
		// Affichage de traits horizontaux supplémentaires si besoin
		final int EXTRA_LINE_WIDTH = 16;
		// Traits en bas
		if (ordinate > 91) {
			int lineY = 92 * spacing - shifting;
			g.drawLine(x+2, lineY, x + EXTRA_LINE_WIDTH, lineY);
			if (ordinate > 93) {
				lineY = 94 * spacing - shifting;
				g.drawLine(x+2, lineY, x + EXTRA_LINE_WIDTH, lineY);
				if (ordinate > 95) {
					lineY = 96 * spacing - shifting;
					g.drawLine(x+2, lineY, x + EXTRA_LINE_WIDTH, lineY);
					if (ordinate > 97) {
						lineY = 98 * spacing - shifting;
						g.drawLine(x+2, lineY, x + EXTRA_LINE_WIDTH, lineY);
					}
				}
			}
		}
		// Traits en haut
		if (ordinate < 81) {
			int lineY = 80 * spacing - shifting;
			g.drawLine(x, lineY, x + EXTRA_LINE_WIDTH, lineY);
			if (ordinate < 79) {
				lineY = 78 * spacing - shifting;
				g.drawLine(x, lineY, x + EXTRA_LINE_WIDTH, lineY);
				if (ordinate < 77) {
					lineY = 76 * spacing - shifting;
					g.drawLine(x, lineY, x + EXTRA_LINE_WIDTH, lineY);
					if (ordinate < 75) {
						lineY = 74 * spacing - shifting;
						g.drawLine(x, lineY, x + EXTRA_LINE_WIDTH, lineY);
						if (ordinate < 73) {
							lineY = 72 * spacing - shifting;
							g.drawLine(x, lineY, x + EXTRA_LINE_WIDTH, lineY);
						}
					}
				}
			}			
		}
		
		// Affichage de la note
		g.drawImage(AwtRenderersResources.m_notesImage, x, y, x + NOTE_WIDTH, y + NOTE_HEIGHT,
				xIndex * NOTE_WIDTH, yIndex * NOTE_HEIGHT, (xIndex+1) * NOTE_WIDTH, (yIndex+1) * NOTE_HEIGHT,
				null);
		
		// Affichage d'un point si besoin
		if (note.getFigure().isDotted())
			g.fillArc(x + 15, item.getPointOfInterestY() + 0, 4, 3, 0, 360);
		
		// Affichage d'une atlération si besoin
		if (item.getFlag(LocationItemFlag.EXPLICIT_ALTERATION) == true) {
			switch (note.getHeight().getAlteration()) {
				case Height.NATURAL:
					if (yIndex == 0)
						g.drawImage(AwtRenderersResources.m_naturalImage, x - 5, y + 15, null);
					else if (yIndex == 1)
						g.drawImage(AwtRenderersResources.m_naturalImage, x - 5, y - 3, null);
					break;
				case Height.SHARP:
					if (yIndex == 0)
						g.drawImage(AwtRenderersResources.m_sharpImage, x - 5, y + 15, null);
					else if (yIndex == 1)
						g.drawImage(AwtRenderersResources.m_sharpImage, x - 5, y - 3, null);
					break;
				case Height.FLAT:
					if (yIndex == 0)
						g.drawImage(AwtRenderersResources.m_flatImage, x - 5, y + 10, null);
					else if (yIndex == 1)
						g.drawImage(AwtRenderersResources.m_flatImage, x - 5, y - 9, null);
					break;
			}
		}
	}
	
}
