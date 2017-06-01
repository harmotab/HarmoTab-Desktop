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

package harmotab.io.score;

import harmotab.core.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;


public class ScorePrintable implements Printable {
	
	private static final float m_zoom = 1.5f;
	
	//
	// Constructeurs
	//
	
	public ScorePrintable(Score score) {
		if (score == null)
			throw new NullPointerException();
		m_score = score;
	}
	
	
	//
	// M�thode d'impression 
	//

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		Graphics2D g2d = (Graphics2D) g;
		
		// R�cup�ration des informations sur le format de l'impression
		int width = (int) pageFormat.getWidth();
		int height = (int) pageFormat.getHeight();
		
		// Cr�er la liste des images au premier appel
		if (m_images == null) {
			ImagesListView view = new ImagesListView(m_score, (int) (width * m_zoom), (int) (height * m_zoom));
			view.setPageNumberVisible(true);
			view.setFixedPageHeight(true);
			m_images = view.createImages();
		}
		
		// Si toutes les pages ont d�j� �t� imprim�es, fin de l'impression
		if (pageIndex >= m_images.size()) {
			m_images = null;
			return Printable.NO_SUCH_PAGE;
		}
		
		BufferedImage img = m_images.get(pageIndex);
		g2d.drawImage(img, 0, 0, width, height, null);

		// Indique qu'une page a �t� �crite
		return Printable.PAGE_EXISTS;
	}
	
	//
	// Attributs
	//
	
	private Score m_score  = null;
	private ArrayList<BufferedImage> m_images = null;

}
