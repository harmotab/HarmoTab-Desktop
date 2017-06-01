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

package harmotab.desktop.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import harmotab.core.*;
import harmotab.sound.*;
import harmotab.desktop.actions.*;
import harmotab.desktop.tools.*;


/**
 * Composant de gestion de la lecture en utilisant le SoundPlayer du controlleur 
 * d'une partition.
 */
public class PlayerControlPane extends JToolBar implements ScoreControllerListener, ScorePlayerListener, ActionListener {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public PlayerControlPane(ScoreController controller) {
		m_scoreController = controller;
		
		setFloatable(false);
		
		// Cr�ation des composants
		m_playButton = new ToolButton(new PlayAction());
		m_playFromButton = new ToolButton(new PlayFromAction());
		m_pauseButton = new ToolToggleButton(new PauseAction());
		m_stopButton = new ToolButton(new StopAction());
		m_metronomeCheckBox = new JCheckBox(Localizer.get(i18n.N_METRONOME), GlobalPreferences.getMetronomeEnabled());

		// Initialisation des composants
		m_playButton.setText(null);
		m_pauseButton.setText(null);
		m_stopButton.setText(null);
		m_playFromButton.setText(null);

		m_playButton.setEnabled(false);
		m_pauseButton.setEnabled(false);
		m_stopButton.setEnabled(false);
		m_playFromButton.setEnabled(false);
		m_metronomeCheckBox.setEnabled(false);
		
		m_playButton.setWide(true);
		m_playFromButton.setWide(true);
		m_pauseButton.setPreferredSize(null);
		m_stopButton.setWide(true);
		m_metronomeCheckBox.setOpaque(false);
						
		// Ajout des composants � la barre d'outils
		add(m_playButton);
		add(m_playFromButton);
		add(m_pauseButton);
		add(m_stopButton);
		if (GlobalPreferences.getMetronomeFeatureEnabled()) {
			add(m_metronomeCheckBox);
		}
		
		// Enregistrement des listeners
		m_scoreController.addScoreControllerListener(this);
		m_metronomeCheckBox.addActionListener(this);
		setSoundPlayer(m_scoreController.getScorePlayer());
	}

	
	protected void updateButtonsStates() {
		boolean hasScore = m_scoreController.hasScore();
		boolean opened = m_soundPlayer.getState() == ScorePlayer.OPENED;
		boolean playing = m_soundPlayer.isPlaying();
		boolean paused = m_soundPlayer.isPaused();
		m_playButton.setEnabled(hasScore && opened && !playing && !paused);
		m_playFromButton.setEnabled(hasScore && opened && !playing && !paused);
		m_pauseButton.setEnabled(opened && (playing || paused));
		m_stopButton.setEnabled(opened && (playing || paused));
		m_pauseButton.setSelected(hasScore && paused);
		m_metronomeCheckBox.setEnabled(hasScore && opened && !playing && !paused);
	}
	
	
	//
	// Getters / setters
	//
	
	protected void setSoundPlayer(ScorePlayer player) {
		if (m_soundPlayer != null) {
			m_soundPlayer.removeSoundPlayerListener(this);
		}
		m_soundPlayer = player;
		m_soundPlayer.addSoundPlayerListener(this);
	}
	
	
	//
	// Actions de l'utilisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// Action sur la case � cocher du m�tronome
		if (event.getSource() == m_metronomeCheckBox) {
			GlobalPreferences.setMetronomeEnabled(m_metronomeCheckBox.isSelected());
		}
	}

	
	//
	// Ecoute du controller de la partition
	//
	
	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		updateButtonsStates();
	}
	
	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
		setSoundPlayer(soundPlayer);
		updateButtonsStates();
	}
	
	
	//
	// Ecoute du lecteur
	//
	
	@Override
	public void onScorePlayerStateChanged(ScorePlayerEvent event) {
		updateButtonsStates();
	}
	
	@Override
	public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {
		error.printStackTrace();
		updateButtonsStates();
	}
	
	@Override
	public void onPlaybackStarted(ScorePlayerEvent event) {
		updateButtonsStates();
	}

	@Override
	public void onPlaybackPaused(ScorePlayerEvent event) {
		updateButtonsStates();
	}

	@Override
	public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		updateButtonsStates();
	}

	@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}

	
	//
	// Attributs
	//
	
	protected ScoreController m_scoreController = null;
	protected ScorePlayer m_soundPlayer = null;
	
	private ToolButton m_playButton = null;
	private ToolButton m_playFromButton = null;
	private ToolToggleButton m_pauseButton = null;
	private ToolButton m_stopButton = null;
	private JCheckBox m_metronomeCheckBox = null;
	
}
