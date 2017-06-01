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

package harmotab.track.layout;

import harmotab.core.*;
import harmotab.renderer.*;


public class HeaderLayout {
	
	private static final int HEADER_HEIGHT = 180;

	
	//
	// Constructeur
	//
	
	public HeaderLayout(Score score) {
		m_score = score;
	}
	
	
	/**
	 * Calcule la position d'affichage des éléments de l'entête 
	 */
	public void processHeaderPositionning(LocationList locations, int areaWidth) {
		
		// Titre
		locations.add(LocationItem.newFromArea(m_score.getTitle(),
				0, 0, areaWidth, 40,	// x, y, width, height
				-1, 0, -1, 0)			// track, line, time, extra
			);
		
		// Auteur
		locations.add(LocationItem.newFromArea(m_score.getSongwriter(),
				0, 40, areaWidth, 30,	// x, y, width, height
				-1, 0, -1, 0)			// track, line, time, extra
			);
		
		// Tempo
		locations.add(LocationItem.newFromArea(m_score.getTempo(),
				0, 100, areaWidth/2, 25,	// x, y, width, height
				-1, 0, -1, 0)				// track, line, time, extra
			);		
		
		// Commentaires
		locations.add(LocationItem.newFromArea(m_score.getComment(),
				areaWidth/2, 100, areaWidth/2, 25,	// x, y, width, height
				-1, 0, -1, 0)						// track, line, time, extra
			);
		
	}
	
	
	/**
	 * Retourne la hauteur de l'entête en pixels
	 */
	public int getHeight() {
		return HEADER_HEIGHT;
	}
	
	
	//
	// Attributs
	//
	
	private Score m_score;
	
}
