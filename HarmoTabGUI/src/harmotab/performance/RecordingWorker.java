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

package harmotab.performance;

import java.io.File;
import java.util.ArrayList;
import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.desktop.components.CountDownDialog;
import harmotab.element.*;
import harmotab.sound.*;


/**
 * Classe de gestion de la phase d'enrgistrement
 */
public class RecordingWorker extends Thread implements ScorePlayerListener {

	//
	// Constructeur
	//
	
	public RecordingWorker(ScoreController controller, Recorder recorder, Performance performance) throws RecorderException {
		if (controller.getPerformancesList() == null) {
			throw new IllegalArgumentException("Cannot create a RecordingWorker for a non exported score.");
		}
		
		m_scoreController = controller;
		m_soundPlayer = controller.getScorePlayer();
		m_recorder = recorder;
		m_performance = performance;
		
	}
		
	
	//
	// Getters / setters
	//
	
	public boolean hasBeenAborted() {
		return m_aborted;
	}
	
	public boolean hasAbortedOnError() {
		return m_errorOccured;
	}
	
	
	//
	// Worker
	//
	
	@Override
	public void run() {
		m_recording = true;
		m_aborted = false;
		m_errorOccured = false;
		
		ScorePlayer player = null;
		Score score = m_scoreController.getScore();
		TimeSignature timeSignature = score.getFirstTimeSignature();
				
		// Lancement du compte � rebours
		SoundCountdown soundCountDown = null;
		try {
			soundCountDown = new MidiCountDown(score.getFirstTimeSignature(), score.getTempo());
			if (GlobalPreferences.getPlaybackCountdownEnabled()) {
				((MidiCountDown) soundCountDown).start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			soundCountDown = new SoundCountdown(timeSignature, score.getTempo());
		}
		
		// Initialisation de l'enregistreur et du lecteur
		try {
			// Lecteur
			m_scoreController.setDefaultSoundPlayer();
			player = m_scoreController.getScorePlayer();
			ScorePlayerController playerController = new ScorePlayerController(player, score);
			playerController.preparePlayer();
			// Enregistreur
			m_recorder.open();
		} 
		catch (RecorderException e) {
			e.printStackTrace();
			m_recording = false;
			m_aborted = true;
			m_errorOccured = true;
			return;
		}

		
		// Affichage de la fenêtre de compte à rebours
		CountDownDialog countDownDialog = new CountDownDialog(
				Localizer.get(i18n.N_RECORDING), Localizer.get(i18n.M_RECORDING_COUNTDOWN));
		float countDownDuration = soundCountDown.getCountdownSequence().getLastTime();		
		if (!countDownDialog.countDown(countDownDuration, 1)) {
			try {
				player.stop();
				m_recorder.close();
			}
			catch (RecorderException e) {}
			
			ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_RECORDING_ABORTED_ERR));
			return;
		}
		
		// D�marrage de la lecture et de l'enregistrement
		fireRecordingStarting();
		try {
			// Démarrage de la lecture
			player.addSoundPlayerListener(this);
			player.play();
			
			// Démarrage de l'enregistrement
			m_recorder.start();
			
			// Phase d'enregistrement
			while (m_recording == true && m_aborted == false) {
				yield();
			}
			m_recorder.stop();
			
			// Finalisation de l'enregistrement
			if (m_aborted == false) {
				PerformancesList perfs = m_scoreController.getPerformancesList();
				File outFile = File.createTempFile("perf_", ".pcm");
				outFile.deleteOnExit();
				m_recorder.save(outFile.getAbsolutePath());
				m_performance.setFile(outFile);
				perfs.add(m_performance);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			m_errorOccured = true;
		}
		finally {
			m_scoreController.getScorePlayer().removeSoundPlayerListener(this);
			try {
				m_recorder.close();
			}
			catch (RecorderException e) {
				e.printStackTrace();
			}
			
			if (hasAbortedOnError()) {
				ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_RECORDING_ERROR_ERR));
			}
			else if (hasBeenAborted()) {
				ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_RECORDING_ABORTED_ERR));
			}
			fireRecordingStopped();
		}
		
	}
	
	
	//
	// Ecoute des �v�nements de lecture
	//
	
	@Override
	public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		m_recording = false;
		m_aborted = !endOfPlayback;
	}
	
	@Override
	public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {
		m_aborted = true;
		m_errorOccured = true;
	}

	@Override
	public void onPlaybackPaused(ScorePlayerEvent event) {
		m_aborted = true;
		m_errorOccured = true;
	}
	
	@Override public void onScorePlayerStateChanged(ScorePlayerEvent event) {}
	@Override public void onPlaybackStarted(ScorePlayerEvent event) {}
	@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}

	
	//
	// Gestion des listeners
	//
	
	public void addRecordingListener(RecordingListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeRecordingListener(RecordingListener listener) {
		m_listeners.remove(listener);
	}
	
	
	public void fireRecordingStarting() {
		for (RecordingListener listener : m_listeners) {
			listener.onRecordingStarted(this);
		}
	}
	
	public void fireRecordingStopped() {
		for (RecordingListener listener : m_listeners) {
			listener.onRecordingStopped(this);
		}
	}
	
	
	//
	// Attributs
	//
	
	protected ScoreController m_scoreController = null;
	protected ScorePlayer m_soundPlayer = null;
	protected Recorder m_recorder = null;
	protected Performance m_performance = null;
	
	protected boolean m_recording = false;
	protected boolean m_aborted = false;
	protected boolean m_errorOccured = false;
	
	protected final ArrayList<RecordingListener> m_listeners = new ArrayList<RecordingListener>();
	
}
