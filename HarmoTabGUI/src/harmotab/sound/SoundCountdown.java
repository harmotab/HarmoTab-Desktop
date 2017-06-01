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

import java.util.ArrayList;

import harmotab.element.Tempo;
import harmotab.element.TimeSignature;


/**
 * Compte à rebours sonore.
 * Correspond aux battements indiquant le début du démarrage d'une partition.
 */
public class SoundCountdown {
	
	//
	// Constructeur
	//
	
	public SoundCountdown(TimeSignature timeSignature, Tempo tempo) {
		setTimeSignature(timeSignature);
		setTempo(tempo);
	}

	
	//
	// Getters / setters
	//
	
	public void setTimeSignature(TimeSignature timeSignature) {
		m_timeSignature = timeSignature;
	}
	
	public TimeSignature getTimeSignature() {
		return m_timeSignature;
	}
	
	
	public void setTempo(Tempo tempo) {
		m_tempo = tempo;
	}
	
	public Tempo getTempo() {
		return m_tempo;
	}
	
	
	//
	// M�thodes m�tier
	//
	
	/**
	 * Cr�er une s�quence de sons correspondant � un compte � rebours.
	 * La s�quence prend le temps d'une mesure et comporte un son par
	 * battement (temps fort) et un son faible tous les 0.5 temps.
	 */
	public SoundSequence getCountdownSequence() {
		SoundSequence sounds = new SoundSequence();
		float beatTimeSec = m_tempo.getBeatPeriodInSeconds();
		float timesPerBar = m_timeSignature.getTimesPerBar();
		float inc = m_timeSignature.getTimesPerBeat() == 1.0f ? 1.0f : 0.5f;
		for (float time = 0; time < timesPerBar; time += inc) {
			SoundItem sound = new SoundItem(
					null, MidiConstants.PERCUSSION_CHANNEL,			// element, track (channel)
					m_timeSignature.isStrongBeat(time % ((int) timesPerBar)) ?  			// soundId
							MidiConstants.STRONG_BEAT_SOUNDID : MidiConstants.LOW_BEAT_SOUNDID,
					time * beatTimeSec, beatTimeSec * inc);						// startTime, duration
			sounds.add(sound);
		}
		return sounds;
	}
	
	
	/**
	 * 
	 */
	public void start() {
		fireCountdownStarted();
		try {
			Thread.sleep((long) getCountdownSequence().getLastTime() * 1000);
		}
		catch (InterruptedException e) {}
		fireCountdownStopped(false);
	}
	
	
	//
	// Evènements
	//
	
	public void addSoundCountdownListener(SoundCountdownListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeSoundCountdownListener(SoundCountdownListener listener) {
		m_listeners.remove(listener);
	}
	
	
	protected void fireCountdownStarted() {
		for (SoundCountdownListener listener : m_listeners) {
			listener.onSoundCountdownStarted(this);
		}
	}
	
	protected void fireCountdownStopped(boolean cancelled) {
		for (SoundCountdownListener listener : m_listeners) {
			listener.onSoundCountdownStopped(this, cancelled);
		}
	}
	
		
	//
	// Attributs
	//
	
	protected TimeSignature m_timeSignature = null;
	protected Tempo m_tempo = null;
	
	private final ArrayList<SoundCountdownListener> m_listeners = new ArrayList<SoundCountdownListener>();
	
}

