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

package harmotab.io.score;

import harmotab.core.Score;
import harmotab.sound.MidiConstants;
import harmotab.sound.SoundItem;
import harmotab.sound.SoundSequence;
import harmotab.track.Track;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;


public class MidiScoreWriter extends ScoreWriter {

	public MidiScoreWriter(Score score, String path) {
		super(score, path);
	}

	@Override
	protected void write(Score score, File file) throws IOException {
		try {
			// Cr�ation de la s�quence midi
			Sequence sequence = new Sequence(Sequence.PPQ, MidiConstants.TICKS_PER_BEAT);

			// Cr�ation de la liste de sons
			SoundSequence sounds = new SoundSequence();
			for (Track track : score)
				track.getSoundLayout().processSoundsPositionning(sounds);
			
			// Ajout de la liste de sons � la s�quence midi
			sounds = sounds.mergeRepeats();
			javax.sound.midi.Track midiTrack = sequence.createTrack();
			for (SoundItem sound : sounds) {
				if (sound.getSoundId() != SoundItem.NO_SOUND) {
					addEvent(sequence, midiTrack, sound.m_trackId, sound.m_startTime, MidiConstants.NOTEON, sound.m_soundId);
					addEvent(sequence, midiTrack, sound.m_trackId, sound.m_endTime, MidiConstants.NOTEOFF, sound.m_soundId);
				}
			}

			// Affecte les informations de volume et d'instrument
			for (Track track : score) {
				int channel = score.getTrackId(track);
				// Affectation de l'instrument
				setInstrument(sequence, midiTrack, channel, track.getInstrument());
				// Affectation du volume
				setTrackVolume(sequence, midiTrack, channel, track.getVolume());
			}
			
			// Cr�er le fichier midi
			int[] fileTypes = MidiSystem.getMidiFileTypes(sequence);
			if (fileTypes.length == 0) {
				throw new Exception("Cannot get midi file types for the sequence.");
			} else {
				if (MidiSystem.write(sequence, fileTypes[0], file) == -1) {
					throw new Exception("An error occured writing midi file.");
				}
			}

		}
		// En cas d'erreur affiche un message
		catch (Exception e) {
			throw new IOException(e);
		}		
	}
	
	

	private void addEvent(Sequence sequence, javax.sound.midi.Track midiTrack, int channel, float time, int type, int num) {
		try {
			long ticks = (long) (time * sequence.getResolution() * 2);
			ShortMessage message = new ShortMessage();
			message.setMessage(type + channel, num, MidiConstants.DEFAULT_VELOCITY);
			MidiEvent event = new MidiEvent(message, ticks);			
			midiTrack.add(event);
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	private void setInstrument(Sequence sequence, javax.sound.midi.Track midiTrack, int channel, int instrument) {
		addEvent(sequence, midiTrack, channel, 0, MidiConstants.PROGRAM, instrument);
	}
	
	private void setTrackVolume(Sequence sequence, javax.sound.midi.Track midiTrack, int channel, int volume) {
		float value = ((float) volume / 100.0f) * (float) MidiConstants.MAX_VOLUME;
		try {
			ShortMessage volMessage = new ShortMessage();
			volMessage.setMessage(ShortMessage.CONTROL_CHANGE, channel, 7, (int) value);
			midiTrack.add(new MidiEvent(volMessage, 0));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
}

