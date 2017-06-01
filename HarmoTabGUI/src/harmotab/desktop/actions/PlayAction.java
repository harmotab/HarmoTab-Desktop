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

package harmotab.desktop.actions;

import harmotab.core.GlobalPreferences;
import harmotab.core.Localizer;
import harmotab.core.Score;
import harmotab.core.i18n;
import harmotab.desktop.DesktopController;
import harmotab.desktop.tools.ToolIcon;
import harmotab.sound.MidiCountDown;
import harmotab.sound.ScorePlayer;
import harmotab.sound.ScorePlayerController;
import harmotab.sound.SoundCountdown;
import harmotab.sound.SoundCountdownListener;


/**
 * Action "Lecture"
 */
public class PlayAction extends UserAction {
	private static final long serialVersionUID = 1L;

	public PlayAction() {
		super(
			Localizer.get(i18n.TT_PLAY),
			Localizer.get(i18n.TT_PLAY),
			ToolIcon.getIcon(ToolIcon.PLAY)
		);
		setLittleIcon(ToolIcon.getIcon(ToolIcon.PLAY_LITTLE));
	}

	@Override
	public void run() {
		// Joue le compte � rebours si besoin et attend sa fin pour lancer la lecture
		if (GlobalPreferences.getPlaybackCountdownEnabled()) {
			try {
				Score score = DesktopController.getInstance().getScoreController().getScore();
				MidiCountDown midiCountDown = new MidiCountDown(score.getFirstTimeSignature(), score.getTempo());
				midiCountDown.addSoundCountdownListener(new SoundCountdownObserver());
				midiCountDown.start();
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// Sinon lance la lecture immédiatement
		doPlay();
	}
	
	
	/**
	 * Lance effectivement la lecture
	 */
	private void doPlay() {
		Score score = DesktopController.getInstance().getScoreController().getScore();
		ScorePlayer player = DesktopController.getInstance().getScoreController().getScorePlayer();
		ScorePlayerController playerController = new ScorePlayerController(player, score);
		playerController.preparePlayer();
		player.play();
	}
	
	
	/**
	 * Classe permettant de lancer la lecture à la fin du compte à rebours
	 */
	private class SoundCountdownObserver implements SoundCountdownListener {

		@Override
		public void onSoundCountdownStopped(SoundCountdown soundCountDown, boolean cancelled) {
			if (!cancelled) {
				doPlay();
			}
		}
		
		@Override public void onSoundCountdownStarted(SoundCountdown soundCountDown) {}
	}
	
}

