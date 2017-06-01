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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import harmotab.core.GlobalPreferences;
import harmotab.core.Score;
import harmotab.performance.Performance;


/**
 * Lecteur d'interprétations sous la forme de ScorePlayer
 */
public class PerformanceScorePlayer extends ScorePlayer implements LineListener {
	
	//
	// Constructeur
	//
	
	public PerformanceScorePlayer(Performance performance, Score score) {
		m_score = score;
		m_filePath = new File(performance.getFile().getAbsolutePath());
		m_openned = false;
		m_clip = null;
		m_volume = GlobalPreferences.getGlobalVolume();;
	}
	
	
	//
	// Getters / setters
	//
	
	@Override
	public byte getState() {
		if (m_clip == null)
			return CLOSED;
		return m_clip.isOpen() ? OPENED : CLOSED;
	}

	@Override
	public void setInstrument(int channel, int instrument) {
		// Pas d'impl�mentation pour ce lecteur
	}

	@Override
	public void setTrackVolume(int channel, int volume) {
		// Pas d'impl�mentation pour ce lecteur
	}

	@Override
	public void setGlobalVolume(int volume) {
		m_volume = volume;
		if (m_openned == true) {
			try {
				float value = (float) m_volume / 100.0f;
				FloatControl volCtrl = (FloatControl) m_clip.getControl(FloatControl.Type.MASTER_GAIN);
				volCtrl.setValue(value);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isPlaying() {
		return m_clip.isRunning();
	}

	@Override
	public boolean isPaused() {
		return false;
	}

	@Override
	public float getPosition() {
		return (float) m_clip.getMicrosecondPosition() / 1000000.0f;
	}

	@Override
	public void setPosition(SoundItem item) {
		m_clip.setMicrosecondPosition((long) (item.getStartTime() * 1000000.0f));
	}

	@Override
	public float getDuration() {
		return m_clip.getMicrosecondLength() / 1000000.0f;
	}

	
	//
	// M�thodes de gestion de la ligne audio
	//

	/**
	 * Ouverture de la sortie audio
	 */
	@Override
	public void open() {
		if (m_openned == true)
			throw new IllegalStateException("Reader already openned");
		
		try {
			m_audioInputStream = AudioSystem.getAudioInputStream(m_filePath);
			AudioFormat audioFormat = m_audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
			m_clip = (Clip) AudioSystem.getLine(info);
			m_clip.open(m_audioInputStream);
			m_openned = true;
			setGlobalVolume(m_volume);
			m_clip.addLineListener(this);
		}
		catch (Exception e) {
			fireSoundPlayerError(e);
		}
		
		fireStateChanged();
	}

	/**
	 * Fermeture de la sortie audio
	 */
	@Override
	public void close() {
		if (m_openned == false)
			throw new IllegalStateException("Reader not openned");
		
		try {
			m_clip.close();
			m_audioInputStream.close();
		}
		catch (IOException e) {
			fireSoundPlayerError(e);
		}
		
		m_openned = false;
		fireStateChanged();
	}

	
	/**
	 * D�marrage de la lecture
	 */
	@Override
	public void play() {
		// V�rification de l'�tat de la lecture
		if (m_openned == false)
			throw new IllegalStateException("Reader not openned");
		if (m_clip.isActive())
			return ;
		
		// D�marrage de la lecture
		m_stoppedByUser = false;
		setGlobalVolume(m_volume);
		
		// D�marrage de la lecture
		m_clip.start();
		
		// D�marrage de l'observation de l'avanc�e de la lecture
		new ReadingObserver().start();
		
		// Signalement du d�marrage de la lecture
		firePlaybackStarted();
	}
	

	/**
	 * Mise en pause de la lecture
	 */
	@Override
	public void pause() {
		m_clip.stop();
		firePlaybackPaused();
	}

	
	/**
	 * Arr�t de la lecture
	 */
	@Override
	public void stop() {
		if (m_clip.isActive() == false)
			throw new IllegalStateException("Reader not reading");
		m_stoppedByUser = true;
		m_clip.stop();
		m_clip.setFramePosition(0);
		//Ev�nement d'arr�t transmis par l'observeur
	}

	
	//
	// Gestion des listeners
	//
	
	@Override
	public void addSoundPlayerListener(ScorePlayerListener listener) {
		m_listeners.add(listener);
	}

	@Override
	public void removeSoundPlayerListener(ScorePlayerListener listener) {
		m_listeners.remove(listener);
	}
	
	
	private void fireStateChanged() {
		ScorePlayerEvent event = new ScorePlayerEvent(this);
		
		for (ScorePlayerListener listener : m_listeners) {
			listener.onScorePlayerStateChanged(event);
		}
	}
	
	private void firePlaybackStarted() {
		ScorePlayerEvent event = new ScorePlayerEvent(this);
		
		for (ScorePlayerListener listener : m_listeners) {
			listener.onPlaybackStarted(event);
		}
	}
	
	private void firePlaybackStopped(boolean endOfPlayback) {
		ScorePlayerEvent event = new ScorePlayerEvent(this);
		
		for (ScorePlayerListener listener : m_listeners) {
			listener.onPlaybackStopped(event, endOfPlayback);
		}
	}
	
	private void firePlaybackPaused() {
		ScorePlayerEvent event = new ScorePlayerEvent(this);
		
		for (ScorePlayerListener listener : m_listeners) {
			listener.onPlaybackPaused(event);
		}		
	}
	
	private void fireSoundItemChanged() {
		ScorePlayerEvent event = new ScorePlayerEvent(this);
		
		for (ScorePlayerListener listener : m_listeners) {
			listener.onPlayedSoundItemChanged(event);
		}
	}
	
	private void fireSoundPlayerError(Throwable error) {
		ScorePlayerEvent event = new ScorePlayerEvent(this);
		
		for (ScorePlayerListener listener : m_listeners) {
			listener.onScorePlayerError(event, error);
		}
	}
	
	
	//
	// Observeur de la lecture
	//
	
	private class ReadingObserver extends Thread {
		@Override
		public void run() {
			while (m_clip.isActive()) {
				fireSoundItemChanged();
				try {
					Thread.sleep(40);
				}
				catch (InterruptedException e) {
				}
			}
		}
	}
	
	
	//
	// Impl�mentation de LineListener
	//
	
	@Override
	public void update(LineEvent event) {
		if (event.getType() == LineEvent.Type.STOP) {
			firePlaybackStopped(!m_stoppedByUser);
			m_clip.setFramePosition(0);
		}
	}
	
	
	//
	// Attributs
	//
	
	protected Score m_score = null;
	protected Clip m_clip = null;
	protected AudioInputStream m_audioInputStream = null;
	protected boolean m_openned = false;
	protected File m_filePath = null;
	protected boolean m_stoppedByUser = false;
	protected int m_volume = 100;
	
	protected final ArrayList<ScorePlayerListener> m_listeners = new ArrayList<ScorePlayerListener>();

}

