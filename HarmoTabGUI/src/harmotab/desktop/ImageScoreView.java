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

package harmotab.desktop;

import java.awt.*;
import harmotab.core.*;


/**
 * Vue d'une partition dessin�e dans une image.
 */
public class ImageScoreView extends ScoreView {

	// Environnement graphique utilis� pour cr�er les images
	static GraphicsConfiguration graphicsConfiguration = 
		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
	
	//
	// Constructeur
	//
	
	public ImageScoreView(ScoreController controller) {
		super(controller);
		m_scoreImage = null;
	}
	
	
	//
	// Getters / setters
	//
	
	public Image getImage() {
		return m_scoreImage;
	}
	
	
	//
	// Impl�mentation des m�thodes abstraites de la classe m�re
	//
	
	@Override
	protected void updateScoreView() {
		// Cr�ation de l'image
		m_scoreImage = graphicsConfiguration.createCompatibleImage(
				m_viewWidth, m_viewHeight, Transparency.TRANSLUCENT);
		// R�cup�ration et configuration du Graphics
		Graphics2D g2d = (Graphics2D) m_scoreImage.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// Affichage des �l�ments dans l'image
		if (m_renderer != null) {
			m_renderer.paint(g2d, m_locations, new Point(0, 0));
		}
		// Affichage d'une zone vide
		else {
			g2d.setColor(Color.DARK_GRAY);
			g2d.fillRect(0, 0, m_viewWidth, m_viewHeight);
		}
		g2d.dispose();
	}
	
	
	//
	// Attributs
	//
	
	private Image m_scoreImage = null;
	
}
