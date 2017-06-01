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
import harmotab.sound.ScorePlayer;

import javax.swing.event.*;


public abstract class ScoreView implements ScoreControllerListener {
	
	public static final int MIN_VIEW_HEIGHT = 50;
	public static final int MIN_VIEW_WIDTH = 50;
	

	//
	// Constructeur
	//
	
	public ScoreView(ScoreController controller) {
		m_renderer = null;
		m_scoreController = controller;
		setScore(controller.getScore());
		m_scoreWidth = 500;
		m_scoreHeight = 0;
		m_viewWidth = 500;
		m_viewHeight = 300;
		m_viewOffset = 0;
		controller.addScoreControllerListener(this);
		GlobalPreferences.addChangeListener(new PreferencesObserver());
	}

	private void updateLocations() {
		m_locations.reset();
		if (m_renderer != null) {
			m_renderer.setPageSize(m_viewWidth, m_viewHeight);
			m_renderer.layout(m_locations);
			m_scoreWidth = m_viewWidth;
			m_scoreHeight = m_locations.getBottomOrdinate();
			m_locations.addVerticalScrolling(m_viewOffset);
		}
	}
	
	protected abstract void updateScoreView();
	
	
	/**
	 * Force le rafraîchissement de la vue de la partition avec recalcul du 
	 * positionnement des éléments.
	 */
	public void refresh() {
		updateLocations();
		updateScoreView();
		notifyScoreViewChanged();
	}
	
	
	//
	// Getters / setters
	//
	
	protected void setScore(Score score) {
		if (score != null) {
			score.addObjectListener(new ScoreObserver());
			m_renderer = new ScoreRenderer(score);
		}
		else {
			m_renderer = null;
		}
	}
	
	public ScoreController getScoreController() {
		return m_scoreController;
	}
	
	public int getViewHeight() {
		return m_viewHeight;
	}
	
	public int getViewWidth() {
		return m_viewWidth;
	}

	public void setViewSize(int width, int height) {
		m_viewHeight = height;		
		m_viewWidth = width;
		
		if (m_viewHeight < MIN_VIEW_HEIGHT)
			m_viewHeight = MIN_VIEW_HEIGHT;
		if (m_viewWidth < MIN_VIEW_WIDTH)
			m_viewWidth = MIN_VIEW_WIDTH;
	}
	
	
	public int getViewOffset() {
		return m_viewOffset;
	}
	
	public void setViewOffset(int offset) {
		m_viewOffset = offset;
	}

	public int getMaxOffset() {
		int max = m_scoreHeight - m_viewHeight;
		return (max >= 0 ? max : 0);
	}
	
	
	public int getScoreWidth() {
		return m_scoreWidth;
	}
	
	public int getScoreHeight() {
		return m_scoreHeight;
	}
	
	public void setScoreSize(int width, int height) {
		m_scoreWidth = width;
		m_scoreHeight = height;
	}
	
	
	public ScoreRenderer getScoreRenderer() {
		return m_renderer;
	}
	
//	public void setScoreRenderer(ScoreRenderer renderer) {
//		m_renderer = renderer;
//	}
	
	
	public LocationList getLocations() {
		return m_locations;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	public void translateView(int dOffset) {
		int oldOffset = getViewOffset();
		int newOffset = oldOffset + dOffset;
		int maxOffset = getMaxOffset();
		
		if (newOffset < 0)
			newOffset = 0;
		if (newOffset > maxOffset)
			newOffset = maxOffset;
		
		if (oldOffset != newOffset) {
			int deltaY = newOffset - oldOffset;
			m_locations.addVerticalScrolling(deltaY);
			
			setViewOffset(newOffset);
			updateScoreView();
			notifyScoreViewChanged();
		}
	}
	
	public int getLineOffset(int line) {
		return m_renderer.getLineOffset(line) - getViewOffset();
	}
	
	
	//
	// R�actions aux �v�nements
	//

	private class ScoreObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			refresh();
		}
	}

	private class PreferencesObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			// Prise en compte des modifications des préférences d'affichage
			setScore(m_scoreController.getScore());
			refresh();
		}
	}
	
	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		setScore(scoreControlled);
	}
	
	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
	}
	
	
	//
	// Gestion des listeners
	//

	public void addScoreViewListener(ScoreViewListener listener) {
		m_listeners.add(ScoreViewListener.class, listener);
	}	
	
	protected void notifyScoreViewChanged() {
		for (ScoreViewListener listener : m_listeners.getListeners(ScoreViewListener.class))
            listener.onScoreViewChanged(this);
	}
	
	
	//
	// Attributs
	//	
	
	private EventListenerList m_listeners = new EventListenerList();
	protected LocationList m_locations = new LocationList();
	
	protected ScoreController m_scoreController;
	protected ScoreRenderer m_renderer;
	protected int m_scoreWidth;
	protected int m_scoreHeight;
	protected int m_viewWidth;
	protected int m_viewHeight;
	protected int m_viewOffset;
	
}
