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

package harmotab.renderer;

import java.awt.*;
import java.util.ConcurrentModificationException;

import harmotab.throwables.*;
import harmotab.track.*;
import harmotab.track.layout.*;
import harmotab.core.*;


public class ScoreRenderer {
	
	public static final int MIN_PAGE_WIDTH = 400;
	public static final int MIN_PAGE_HEIGHT = 300;
	
	
	//
	// Constructeur
	//
	
	public ScoreRenderer(Score score) {
		m_score = score;
		m_elementRendererBundle = new AwtEditorElementRendererBundle();
		m_headerLayout = new HeaderLayout(score); 
	}
	
	
	//
	// Getters / setters
	//
	
	public void setElementRenderer(ElementRendererBundle renderer) {
		if (renderer == null)
			throw new NullPointerException();
		m_elementRendererBundle = renderer;
	}
	
	public ElementRendererBundle getElementRenderer() {
		return m_elementRendererBundle;
	}
	
	public int getPageWidth() {
		return m_pageWidth;
	}
	
	public int getPageHeight() {
		return m_pageHeight;
	}
	
	public void setPageSize(int width, int height) throws OutOfSpecificationError {
		if (width < 0 || height < 0)
			throw new OutOfSpecificationError("Invalid page size (" + width + " x " + height + ") !");

		m_pageWidth = Math.max(MIN_PAGE_WIDTH, width);
		m_pageHeight = Math.max(MIN_PAGE_HEIGHT, height);
	}

	
	public boolean isMultiline() {
		return m_multiline;
	}
	
	public void setMultiline(boolean multiline) {
		m_multiline = multiline;
	}
	
	
	public boolean isHeaderDrawingEnabled() {
		return m_headerDrawingEnabled;
	}
	
	public void setHeaderDrawingEnabled(boolean enabled) {
		m_headerDrawingEnabled = enabled;
	}
	
	
	public int getLineHeight() {
		int value = 0;
		for (Track track : m_score)
			value += track.getTrackLayout().getTrackHeight();
		value += m_interlineSpace;
		return value;
	}
	
	public int getHeaderHeight() {
		if (isHeaderDrawingEnabled() == false)
			return 0;
		return m_headerLayout.getHeight();
	}
	
	public int getInterlineHeight() {
		return m_interlineSpace;
	}
	
	public int getLineOffset(int line) {
		return getHeaderHeight() + ((line-1) * getLineHeight());
	}
	
	
	//
	// Gestion des fonctionnalités de la partition
	//	
	

	/**
	 * Positionne les éléments de la partition dans la zone en paramètre.
	 */
	public void layout(LocationList locations) {
		
		// Calcul de la position des éléments de l'entête
		if (isHeaderDrawingEnabled() == true)
			m_headerLayout.processHeaderPositionning(locations, m_pageWidth - m_verticalMargin*2);
		
		// Calcul du positionnement des éléments de toutes les pistes.
		// Toutes les pistes ne sont pas positionnées au même "round", voir TrackLayout.getLayoutRound.
		int round = 0;
		int tracksStillNotLayedOut = m_score.getTracksCount();
		float scoreDuration = m_score.getDuration();
		
		while (tracksStillNotLayedOut > 0) {
			for (Track track : m_score) {
				TrackLayout layout = track.getTrackLayout();
				if (layout.getLayoutRound() == round) {
					layout.processElementsPositionning(locations, m_pageWidth - m_verticalMargin*2, scoreDuration);
					tracksStillNotLayedOut--;
				}
			}
			round++;
		}
				
		// Parcours de la liste des éléments pour
		// - passer à un positionnement relatif à l'ecran en fonction
		//		- du scroll d'affichage
		//		- des marges à gauche et en haut
		//		- de la position de la ligne de l'élément
		locations.addOffset(m_verticalMargin, m_horizontalMargin);
		
		int lineHeight = getLineHeight();
		int trackOffset = getHeaderHeight();
		int trackId = 0;
		for (Track track : m_score) {
			locations.addVerticalOffset(trackId, lineHeight, trackOffset);
			trackOffset += track.getTrackLayout().getTrackHeight();
			trackId++;
		}
				
	}
	
	/**
	 * Affichage de la partition
	 */
	public void paint(Graphics2D g, LocationList locations, Point offset) {
		try {
			for (LocationItem item : locations) {
				if (item.m_x2 >= 0 && item.m_x1 <= m_pageWidth && 
						item.m_y2 >= 0 && item.m_y1 <= m_pageHeight) {
					m_elementRendererBundle.paintElement(g, item);
				}
			}
		}
		catch (ConcurrentModificationException e) {}
	}
	
	
	//
	// Attributs
	//
	
	private int m_pageWidth = 600;
	private int m_pageHeight = 1000;
	private boolean m_multiline = true;
	private boolean m_headerDrawingEnabled = true;
	
	private Score m_score = null;
	private HeaderLayout m_headerLayout = null;
	private ElementRendererBundle m_elementRendererBundle = null;
	
	private int m_verticalMargin = 50;
	private int m_horizontalMargin = 50;
	private int m_interlineSpace = 30;
	
}
