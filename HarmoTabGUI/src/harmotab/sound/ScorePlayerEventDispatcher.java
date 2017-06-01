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

package harmotab.sound;

import javax.swing.event.EventListenerList;


/**
 * Distribue les �v�nements relatifs � un lecteur audio
 */
public class ScorePlayerEventDispatcher {
	
	//
	// Constructeur
	//
	
	public ScorePlayerEventDispatcher(ScorePlayer player) {
		m_player = player;
		m_dispatchingEnabled = true;
	}
	
	
	//
	// Getters / setters
	//
	
	public boolean isDispatchingEnabled() {
		return m_dispatchingEnabled;
	}
	
	public void setDispatchingEnabled(boolean enabled) {
		m_dispatchingEnabled = enabled;
	}
	
	
	//
	// Gestion des listeners
	//
	
	public void addScorePlayerListener(ScorePlayerListener listener) {
		m_listeners.add(ScorePlayerListener.class, listener);
	}
	
	public void removeScorePlayerListener(ScorePlayerListener listener) {
		m_listeners.remove(ScorePlayerListener.class, listener);
	}

	
	public void fireScorePlayerStateChanged(ScorePlayerEvent event) {
		if (m_dispatchingEnabled == true) {
			for (ScorePlayerListener listener : m_listeners.getListeners(ScorePlayerListener.class)) {
				listener.onScorePlayerStateChanged(event);
			}
		}
	}
	
	public void fireScorePlayerError(ScorePlayerEvent event, Throwable error) {
		if (m_dispatchingEnabled == true) {
			for (ScorePlayerListener listener : m_listeners.getListeners(ScorePlayerListener.class)) {
				listener.onScorePlayerError(event, error);
			}
		}
	}
	
	public void firePlaybackStarted(ScorePlayerEvent event) {
		if (m_dispatchingEnabled == true) {
			for (ScorePlayerListener listener : m_listeners.getListeners(ScorePlayerListener.class)) {
				listener.onPlaybackStarted(event);
			}
		}
	}
	
	public void firePlaybackPaused(ScorePlayerEvent event) {
		if (m_dispatchingEnabled == true) {
			for (ScorePlayerListener listener : m_listeners.getListeners(ScorePlayerListener.class)) {
				listener.onPlaybackPaused(event);
			}
		}
	}
	
	public void firePlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		if (m_dispatchingEnabled == true) {
			for (ScorePlayerListener listener : m_listeners.getListeners(ScorePlayerListener.class)) {
				listener.onPlaybackStopped(event, endOfPlayback);
			}
		}
	}
	
	public void firePlayedSoundItemChanged(ScorePlayerEvent event) {
		if (m_dispatchingEnabled == true) {
			for (ScorePlayerListener listener : m_listeners.getListeners(ScorePlayerListener.class)) {
				listener.onPlayedSoundItemChanged(event);
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	protected ScorePlayer m_player;
	protected boolean m_dispatchingEnabled;
	protected final EventListenerList m_listeners = new EventListenerList();
	
}

