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

package harmotab.desktop.components;

import harmotab.core.Score;
import harmotab.core.ScoreController;
import harmotab.core.ScoreControllerListener;
import harmotab.sound.ScorePlayer;
import harmotab.sound.ScorePlayerEvent;
import harmotab.sound.ScorePlayerListener;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Composant de réglage d'un volume
 */
public class VolumeControl extends JPanel implements ScoreControllerListener, ScorePlayerListener {
	private static final long serialVersionUID = 1L;
	
	private final static int DEFAULT_WIDTH = 110;
	
	
	//
	// Constructeurs
	//

	public VolumeControl(int value) {
		m_instance = this;
		
		m_slider = new JSlider(0, 100, value);
		m_slider.setOpaque(false);
		m_valueLabel = new JLabel(value+" %");
		
		setLayout(new BorderLayout());
		add(m_slider, BorderLayout.CENTER);
		
		// Affecte la taille d�sir�e
		Dimension volumeSliderSize = getPreferredSize();
		volumeSliderSize.width = DEFAULT_WIDTH;
		setPreferredSize(volumeSliderSize);
		setMaximumSize(volumeSliderSize);
		setSize(volumeSliderSize);
		
		m_slider.addChangeListener(new SliderValueObserver());
		setOpaque(false);
	}
	
	
	public VolumeControl(ScoreController controller, int value) {
		this(value);		
		setScorePlayer(controller.getScorePlayer());
		controller.addScoreControllerListener(this);
		updateEnabledStatus();
	}
	
	
	//
	// Getters / setters
	//
	
	public int getValue() {
		return m_slider.getValue();
	}
	
	public void setValue(int value) {
		m_slider.setValue(value);
	}
	
	
	@Override
	public void setEnabled(boolean enabled) {
		m_slider.setEnabled(enabled);
	}
	
	

	private void setScorePlayer(ScorePlayer player) {
		if (m_scorePlayer != null) {
			m_scorePlayer.removeSoundPlayerListener(this);
		}
		m_scorePlayer = player;
		if (m_scorePlayer != null) {
			m_scorePlayer.addSoundPlayerListener(this);
		}
		updateEnabledStatus();
	}
	
	private void updateEnabledStatus() {
		setEnabled(m_scorePlayer != null && m_scorePlayer.isPlaying() == false);
	}
	
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	public void addChangeListener(ChangeListener listener) {
		m_listeners.add(ChangeListener.class, listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		m_listeners.remove(ChangeListener.class, listener);
	}
	
	
	private class SliderValueObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			m_valueLabel.setText(getValue()+" %");
			for (ChangeListener listener : m_listeners.getListeners(ChangeListener.class))
				listener.stateChanged(new ChangeEvent(m_instance));
		}	
	}
	
	
	
	//
	// Implémentation de ScoreControllerListener
	//
	

	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
	}

	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer scorePlayer) {
		setScorePlayer(scorePlayer);
	}

	
	//
	// Implémentation de ScorePlayerListener
	//
	
	@Override
	public void onScorePlayerStateChanged(ScorePlayerEvent event) {
		updateEnabledStatus();
	}

	@Override 
	public void onPlaybackStarted(ScorePlayerEvent event) {
		updateEnabledStatus();
	}
	
	@Override
	public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		updateEnabledStatus();
	}

	@Override public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {}
	@Override public void onPlaybackPaused(ScorePlayerEvent event) {}
	@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}
	
	
	//
	// Attributs
	//
	
	private EventListenerList m_listeners = new EventListenerList();
	private JSlider m_slider = null;
	private JLabel m_valueLabel = null;
	private VolumeControl m_instance = null;
	private ScorePlayer m_scorePlayer = null;
	
}
