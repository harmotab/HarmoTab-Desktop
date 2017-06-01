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

import harmotab.core.GlobalPreferences;
import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.desktop.ErrorMessenger;
import javax.sound.midi.*;


/**
 * Joue les �l�ments sonores d'une partition avec un synth�tiseur MIDI.
 */
public class MidiScorePlayer extends ScorePlayer implements MetaEventListener {
	
	/**
	 * Retourne l'instance du lecteur midi
	 */
	public static synchronized MidiScorePlayer getInstance() {
		if (m_instance == null) {
			m_instance = new MidiScorePlayer();
		}
		return m_instance;
	}
	
	
	/**
	 * Constructeur.
	 * Récupération des objets correspondant aux ports midi et initialisation.
	 */
	private MidiScorePlayer() {
		m_opened = false;
		m_volume = GlobalPreferences.getGlobalVolume();
		m_observer = new MidiPlayerObserver(this);
		m_countdownOffset = 0;
	}
	
	
	/**
	 * Destructeur
	 */
	protected void finalize() {
		if (m_opened) {
			close();
		}
	}
	
	
	/**
	 * Ouverture du port midi.
	 * 
	 * @warning	Les erreurs sont signalées via les listeners, la méthode ne
	 * 			pas d'erreur que le lecteur soit ouvert ou non en sortie.
	 */
	@Override
	public synchronized void open() {
		if (m_opened == true)
			return;
		
		m_sounds = null;
		m_midiTrack = null;
		
		try {
			
			if ((m_midiSynthesizer = MidiSystem.getSynthesizer()) == null) {
				ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_NO_DEFAULT_MIDI_OUTPUT_ERROR));
				return;
			}
			
			m_midiSynthesizer.open();
			m_midiSequencer = MidiSystem.getSequencer();
			m_midiSequencer.addMetaEventListener(this);
			m_midiSequencer.setTempoInBPM(MidiConstants.BEATS_PER_MINUTE);
			m_midiSequence = new Sequence(Sequence.PPQ, MidiConstants.TICKS_PER_BEAT);
			
			if (m_midiSynthesizer.getDefaultSoundbank() != null) {
				m_instruments = m_midiSynthesizer.getDefaultSoundbank().getInstruments();
				m_midiSynthesizer.loadInstrument(m_instruments[0]);
			}
			else {
				throw new Exception("No sound bank for the default midi synthesizer.");
			}
			
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			m_observer.fireScorePlayerError(new ScorePlayerEvent(this), throwable);
			return;
		}
		
		// Signale l'ouverture
		m_opened = true;
		m_observer.fireScorePlayerStateChanged(new ScorePlayerEvent(this));
		
		// Joue une s�quence vide pour pallier � un bug qui fait que le tempo 
		// n'est pas pris en compte � la premi�re lecture
		setSounds(new SoundSequence());
		m_observer.setDispatchingEnabled(false);
		play();
		stop();
		m_observer.setDispatchingEnabled(true);
		
	}

	
	/**
	 * Libère les ressources midi.
	 * 
	 * @warning	Les erreurs sont signalées via les listeners, la méthode ne
	 * 			pas d'erreur que le lecteur soit ouvert ou non en sortie.
	 */
	@Override
	public synchronized void close() {
		if (m_opened == false)
			return;
		
		// Fermeture
		try {
			m_opened = false;
			m_midiSynthesizer.close();
			m_midiSequencer.close();
		} catch (Throwable throwable) {
			m_observer.fireScorePlayerError(new ScorePlayerEvent(this), throwable);
			return;
		}
		
		// Signale la fermeture
		m_observer.fireScorePlayerStateChanged(new ScorePlayerEvent(this));
	}
	
	
	/**
	 * Indique si le port midi est ouvert
	 */
	@Override
	public byte getState() {
		return m_opened ? OPENED : CLOSED;
	}
	
	
	/**
	 * Affectation de la séquence de sons à jouer
	 */
	@Override
	public void setSounds(SoundSequence sounds) {
		super.setSounds(sounds);
		updateSequence();
	}
	
	
	/**
	 * Affecte l'instrument d'une piste
	 */
	@Override
	public void setInstrument(int channel, int instrument) {
		m_midiSynthesizer.loadInstrument(m_instruments[instrument]);
		addEvent(channel, 0, MidiConstants.PROGRAM, instrument);
	}
	
	
	/**
	 * Affecte le volume global
	 */
	@Override
	public void setGlobalVolume(int volume) {
		m_volume = volume;

		MidiChannel[] channels = m_midiSynthesizer.getChannels();
		float gain = ((float) volume / 100.0f);
		for (int i=0; i<channels.length; i++) {
			channels[i].controlChange(7, (int) (gain * 127.0));
		}
	}
	
	
	/**
	 * Affecte le volume d'une piste
	 */
	@Override
	public void setTrackVolume(int channel, int volume) {
		float value = ((float) volume / 100.0f) * (float) MidiConstants.MAX_VOLUME;
		try {
			ShortMessage volMessage = new ShortMessage();
			volMessage.setMessage(ShortMessage.CONTROL_CHANGE, channel, 7, (int) value);
			m_midiTrack.add(new MidiEvent(volMessage, 0));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Démarrage de la lecture.
	 */
	@Override
	public void play() {
		if (m_opened == false) {
			throw new SoundDeviceNotOpenedError("Midi device not opened !");
		}
		try {
			m_midiSequencer.open();
			m_midiSequencer.setSequence(m_midiSequence);
			setGlobalVolume(m_volume);
			m_midiSequencer.start();
			m_observer.firePlaybackStarted(new ScorePlayerEvent(this));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Arrêt de la lecture.
	 */
	@Override
	public void stop() {
		if (m_opened == false)
			throw new SoundDeviceNotOpenedError("Midi device not opened !");
		m_midiSequencer.stop();
		m_midiSequencer.close();
		m_observer.firePlaybackStopped(new ScorePlayerEvent(this), false);
	}
	
	
	/**
	 * Indique si la lecture est en cours.
	 */
	@Override
	public boolean isPlaying() {
		if (m_opened == false)
			return false;
		return m_midiSequencer.isRunning();
	}
	
	
	/**
	 * Mise en pause de la lecture.
	 */
	@Override
	public void pause() {
		if (m_opened == false)
			throw new SoundDeviceNotOpenedError("Midi device not opened !");
		m_midiSequencer.stop();
		m_observer.firePlaybackPaused(new ScorePlayerEvent(this));
	}
	
	/**
	 * Indique si la lecture est en pause.
	 */
	@Override
	public boolean isPaused() {
		if (m_opened == false)
			return false;
		return (!m_midiSequencer.isRunning() && m_midiSequencer.getMicrosecondPosition() != 0);
	}

	
	/**
	 * Retourne la durée de la séquence en secondes.
	 */
	@Override
	public float getDuration() {
		if (m_opened == false) {
			return 0.0f;
		}
		return (m_midiSequencer.getMicrosecondLength() / 1000000.0f) + m_countdownOffset;
	}
	
	
	/**
	 * Retourne la position de lecture courante en secondes.
	 */
	@Override
	public float getPosition() {
		if (m_opened == false)
			return 0.0f;
		return m_midiSequencer.getMicrosecondPosition() / 1000000.0f;
	}


	/**
	 * Affecte la position de lecture au début de la lecture de l'item en paramètre.
	 */
	@Override
	public void setPosition(SoundItem item) {
		if (m_opened == false)
			throw new SoundDeviceNotOpenedError("Midi device not opened !");
		if (item == null)
			throw new NullPointerException();
		m_midiSequencer.setMicrosecondPosition((long) (item.getStartTime() * 1000000.0f));
	}
	
	
	/**
	 * Gestion des listeners
	 */
	@Override
	public void addSoundPlayerListener(ScorePlayerListener listener) {
		m_observer.addScorePlayerListener(listener);		
	}
	
	@Override
	public void removeSoundPlayerListener(ScorePlayerListener listener) {
		m_observer.removeScorePlayerListener(listener);
	}
	
	
	//
	// Gestion de la s�quence
	//
	
	/**
	 * Création de la séquence, i.e. l'objet contenant les items midi à jouer.
	 */
	private void updateSequence() {
		// Recréation de la track midi
		if (m_midiTrack != null)
			m_midiSequence.deleteTrack(m_midiTrack);
		m_midiTrack = m_midiSequence.createTrack();
		
		// Insertion des éléments sonnores
		for (SoundItem sound : m_sounds) {
			if (sound.getSoundId() != SoundItem.NO_SOUND) {
				addEvent(sound.m_trackId, sound.m_startTime, MidiConstants.NOTEON, sound.m_soundId);
				addEvent(sound.m_trackId, sound.m_endTime, MidiConstants.NOTEOFF, sound.m_soundId);
			}
		}
	}
	
	
	/**
	 * 
	 * given 120 bpm: (120 bpm) / (60 seconds per minute) = 2 beats per second 2
	 * / 1000 beats per millisecond (2 * resolution) ticks per second (2 *
	 * resolution)/1000 ticks per millisecond, or (resolution / 500) ticks per
	 * millisecond ticks = milliseconds * resolution / 500
	 */
	private void addEvent(int channel, float time, int type, int num) {
		try {
			long ticks = (long) (time * m_midiSequence.getResolution() * 2);
			ShortMessage message = new ShortMessage();
			message.setMessage(type + channel, num, MidiConstants.DEFAULT_VELOCITY);
			MidiEvent event = new MidiEvent(message, ticks);			
			m_midiTrack.add(event);
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	
	/**
	 * Evènement reçu du séquenceur midi.
	 * Implémentation de l'interface MetaEventListener
	 */
	@Override
	public void meta(MetaMessage message) {
		if (message.getType() == MidiConstants.END_OF_TRACK) {
			m_midiSequencer.setMicrosecondPosition(0);
			m_observer.firePlaybackStopped(new ScorePlayerEvent(this), true);
		}
		else {
			System.out.println("Midi meta event : #" + message.getType());
		}
	}

	
	//
	// Attributs
	//
	
	private static MidiScorePlayer m_instance = null;
		
	private Sequencer m_midiSequencer;
	private Synthesizer m_midiSynthesizer;
	private Instrument m_instruments[];

	private Sequence m_midiSequence;
	private Track m_midiTrack;
	
	private MidiPlayerObserver m_observer;
	private boolean m_opened;
	private int m_volume;
	
	private float m_countdownOffset;

}

