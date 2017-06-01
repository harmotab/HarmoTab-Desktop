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

import harmotab.core.*;
import harmotab.element.*;
import harmotab.track.*;


/**
 * Permet de faire le lien entre une partition et un lecteur.
 */
public class ScorePlayerController {

	//
	// Constructeur
	//
	
	public ScorePlayerController(ScorePlayer player, Score score) {
		m_scorePlayer = player;
		m_score = score;
	}

	
	//
	// M�thodes de controle
	//

	/**
	 * Configure le lecteur pour la lecture
	 */
	public void preparePlayer() {
		TimeSignature timeSignature = m_score.getFirstTimeSignature();
		Tempo tempo = m_score.getTempo();
		
		// Cr�ation de la liste de sons
		SoundSequence sounds = createSoundSequence();
		
		// Ajoute les battements de m�tronome
		if (GlobalPreferences.getMetronomeEnabled()) {
			float duration = sounds.getLastTime();
			float beatDuration = tempo.getBeatPeriodInSeconds() * timeSignature.getTimesPerBeat();
			float beat = 0; 
			while (beat < duration) {
				sounds.add(
					new SoundItem(null, 
						MidiConstants.PERCUSSION_CHANNEL,
						MidiConstants.STRONG_BEAT_SOUNDID, 
						beat, 0.5f)					
					);
				beat += beatDuration;
			}
		}
		
		// Gestion des r�p�titions
		sounds = sounds.mergeRepeats();
		
		// Affectation des sons au lecteur
		m_scorePlayer.setSounds(sounds);
		
		// Affectation des propri�t�s de lecture des pistes
		float globalVolumeFactor = ((float) GlobalPreferences.getGlobalVolume()) / 100.0f;
		for (Track track : m_score) {
			int channel = m_score.getTrackId(track);
			m_scorePlayer.setInstrument(channel, track.getInstrument());
			m_scorePlayer.setTrackVolume(channel, (int) (track.getVolume() * globalVolumeFactor));
		}
	}
	
	
	/**
	 * Cr�er la s�quence de sons
	 */
	public SoundSequence createSoundSequence() {
		SoundSequence sounds = new SoundSequence();
		
		// Cr�ation de la liste de sons � produire
		for (Track track : m_score) {
			track.getSoundLayout().processSoundsPositionning(sounds);
		}
		
		return sounds;
	}
	
	
	//
	// Attributs
	//
	
	private ScorePlayer m_scorePlayer = null;
	private Score m_score = null;
	
}

