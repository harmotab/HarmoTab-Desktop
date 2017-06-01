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

import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.desktop.ErrorMessenger;
import harmotab.element.Tempo;
import harmotab.element.TimeSignature;


/**
 * Compte � rebours midi
 */
public class MidiCountDown extends SoundCountdown implements ScorePlayerListener {
	
	//
	// Constructeur
	//

	public MidiCountDown(TimeSignature timeSignature, Tempo tempo) throws Exception {
		super(timeSignature, tempo);
		player = MidiScorePlayer.getInstance();
	}
	
	protected void finalize() throws Throwable {
		player.removeSoundPlayerListener(this);
	}
	
	
	//
	// Impl�mentation de SoundCountDown
	//
	
	/**
	 * D�marrage du compte � rebours
	 */
	@Override
	public void start() {
		final int trackId = 0;
		if (player.getState() == ScorePlayer.OPENED && !player.isPlaying()) {
			player.setSounds(getCountdownSequence());
			player.setInstrument(trackId, 0);
			player.setTrackVolume(trackId, 100);
			
			player.removeSoundPlayerListener(this);
			player.addSoundPlayerListener(this);

			player.play();
			fireCountdownStarted();
		}
		else {
			ErrorMessenger.showErrorMessage(null, Localizer.get(i18n.M_MIDI_OUTPUT_ERROR));
		}
	}
	
	
	// 
	// Impl�mentation de ScorePlayerListener
	// 
	
	@Override
	public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		fireCountdownStopped(!endOfPlayback);
		player.removeSoundPlayerListener(this);
	}

	@Override public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {}
	@Override public void onPlaybackPaused(ScorePlayerEvent event) {}
	@Override public void onScorePlayerStateChanged(ScorePlayerEvent event) {}
	@Override public void onPlaybackStarted(ScorePlayerEvent event) {}
	@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}
	
	
	//
	// Attributs
	//
	
	private MidiScorePlayer player = null;

}

