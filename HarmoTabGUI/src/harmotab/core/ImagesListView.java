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

package harmotab.core;

import harmotab.renderer.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;


public class ImagesListView {

	//
	// Constructeur
	//
	
	public ImagesListView(Score score, int width, int height) {
		m_score = score;
		m_width = width;
		m_height = height;
		
		m_pageNumberVisible = false;
		m_fixedPageHeight = false;
	}
	
	
	//
	// Getters / setters
	//
	
	public void setPageNumberVisible(boolean visible) {
		m_pageNumberVisible = visible;
	}
	
	public boolean getPageNumberVisible() {
		return m_pageNumberVisible;
	}
	
	
	public void setFixedPageHeight(boolean fixed) {
		m_fixedPageHeight = fixed;
	}
	
	public boolean getFixedPageHeight() {
		return m_fixedPageHeight;
	}
	
	
	//
	// M�thodes
	//
	
	public ArrayList<BufferedImage> createImages() {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		
		ScoreRenderer renderer = new ScoreRenderer(m_score);
		renderer.setElementRenderer(new AwtPrintingElementRendererBundle());
		int width = m_width;
		int height = m_height;
		
		// Layout de la partition
		renderer.setPageSize(width, height);
		renderer.setMultiline(true);
		LocationList locations = new LocationList();
		renderer.layout(locations);
		
		// Adapte la largeur et la hauteur de l'image en sortie
		if (width == Integer.MAX_VALUE) {
			renderer.setHeaderDrawingEnabled(false);
			width = locations.getRightOrdinate() + 150;
			height = locations.getBottomOrdinate();
			renderer.setPageSize(width, height);
			locations.reset();
			renderer.layout(locations);
		}
		if (height == Integer.MAX_VALUE)
			height = locations.getBottomOrdinate() + renderer.getInterlineHeight();
		
		// R�cup�ration des informations d'affichage
		int lineHeight = renderer.getLineHeight();
		int totalHeight = locations.getBottomOrdinate();
		int pageOffset = 0;
		
		// Rendu page pas page
		int localY = renderer.getHeaderHeight() + renderer.getInterlineHeight();
		int page = 1;
		while (pageOffset < totalHeight && totalHeight - pageOffset > lineHeight) {
			while (localY <= height)
				localY += lineHeight;
			localY -= lineHeight;

			// Création de l'image vide et configuration du Graphics
			BufferedImage img = createBlankImage(width, m_fixedPageHeight ? m_height : localY);
			Graphics2D g2d = (Graphics2D) img.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			
			// Dessin de la page
			renderer.setPageSize(width, localY);
			renderer.paint(g2d, locations, new Point(0, 0));
			
			// Affiche le numéro de page si nécessaire
			if (m_pageNumberVisible == true) {
				String pageString = "- " + page + " -";
				g2d.drawString(pageString, width/2 - g2d.getFontMetrics().stringWidth(pageString)/2, m_height - 15);
			}
			
			// Enregistre l'image et passe à la suivante
			images.add(img);
			locations.addVerticalScrolling(localY);
			pageOffset += localY;
			localY = 0;
			page++;
		}
		
		// Retourne une liste contenant l'ensemble des images g�n�r�es
		return images;
	}
	
	
	// 
	// M�thodes utilitaires
	// 
	
	private BufferedImage createBlankImage(int width, int height) {
		BufferedImage image = graphicsConfiguration.createCompatibleImage(width, height/*, Transparency.BITMASK*/);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		return image;
	}

	
	//
	// Attributs
	//
	
	private static GraphicsConfiguration graphicsConfiguration = 
		GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	
	private Score m_score;
	private int m_width;
	private int m_height;
	
	private boolean m_pageNumberVisible;
	private boolean m_fixedPageHeight;
	
}
