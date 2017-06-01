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

import harmotab.HarmoTabConstants;


public class MidiPlayerObserver extends ScorePlayerEventDispatcher {

	public MidiPlayerObserver(MidiScorePlayer player) {
		super(player);
	}
	
	
	@Override
	public void firePlaybackStarted(ScorePlayerEvent event) {
		super.firePlaybackStarted(event);
		m_observer = new ObserverThread();
		m_observer.start();
	}
	
	@Override
	public void firePlaybackPaused(ScorePlayerEvent event) {
		m_observer.haveToStop = true;
		super.firePlaybackPaused(event);
	}
	
	@Override
	public void firePlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		m_observer.haveToStop = true;
		super.firePlaybackStopped(event, endOfPlayback);
	}
	
	
	private ObserverThread m_observer;
	
	
	private class ObserverThread extends Thread {
		public boolean haveToStop;
		
		public ObserverThread() {
			haveToStop = false;
		}
		
		@Override
		public void run() {
			SoundItem currentPlayed = null;
			SoundItem lastPlayed = null;
			while (!haveToStop) {
				try {
					currentPlayed = m_player.getPlayedItem();
					// Notification uniquement aux changements de note
					if (currentPlayed != lastPlayed)
						firePlayedSoundItemChanged(
							new ScorePlayerEvent(
									m_player,
									m_player.getState(), 
									currentPlayed,
									m_player.getPosition()
									)
							);
					lastPlayed = currentPlayed;	
					sleep(HarmoTabConstants.PLAYER_OBSERVER_REFRESH_PERIOD_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

}
