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

package harmotab.desktop.setupdialog;

import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.desktop.components.*;
import harmotab.element.*;
import harmotab.sound.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ChordSetupDialog extends SetupDialog {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//

	public ChordSetupDialog(Window parent, Chord chord) {
		super(parent, Localizer.get(i18n.ET_CHORD_SETUP));
		m_chord = chord;
		
		// Initialisation des composants graphiques
		m_chordChooser = new ChordChooser(m_chord);
		m_playButton = new JButton(ActionIcon.getIcon(ActionIcon.PLAY_CHORD));
		m_compositionLabel = new JLabel("");
		
		// Onglet de configuration de l'accord
		
		SetupCategory chordSetupCategory = new SetupCategory(Localizer.get(i18n.N_CHORD));
		JPanel chordSetupPane = chordSetupCategory.getPanel();
		
		chordSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_CHORD_SETUP)));
		chordSetupPane.add(createSetupField(Localizer.get(i18n.N_CHORD), m_chordChooser));
		chordSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_PREVIEW)));

		JPanel playPanel = new JPanel(new BorderLayout(30, 10));
		playPanel.setOpaque(false);
		playPanel.add(m_playButton, BorderLayout.WEST);
		playPanel.add(m_compositionLabel, BorderLayout.CENTER);
		chordSetupPane.add(createSetupField(Localizer.get(i18n.ET_LISTEN), playPanel));
		
		addSetupCategory(chordSetupCategory);

		// Enregistrement des listeners
		m_chordChooser.addActionListener(new ChordChangedAction());
		m_playButton.addActionListener(new PlayChordAction());
		
		// Affichage
		update();
		displayCategory(chordSetupCategory);
		
	}

	
	//
	// Gestion du contenu
	//
	
	private void update() {
		// Mise � jour de l'accord en fonction de ce que l'utilisateur a sélectionné
		Chord chord = m_chordChooser.getChord();
		String composition = "";
		boolean addPeriod = false;
		for (Height height : chord.getHeights()) {
			composition += (addPeriod ? ", " : "") + height.getNoteName();
			addPeriod = true;
		}
		m_compositionLabel.setText("<html><h2>" + chord.getName() + "</h2>" + composition + "</html>");
	}
	

	@Override
	protected void discard() {
	}

	@Override
	protected boolean save() {
		m_chord.set(m_chordChooser.getChord());
		return true;
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	/**
	 * Action de lecture de l'accord en cours d'�dition
	 */
	private class PlayChordAction extends AbstractAction implements ScorePlayerListener {
		private static final long serialVersionUID = 1L;
		private static final float PLAY_DURATION = 2.0f;
		private static final float HEIGHT_SEPARATION_DELAY = 0.1f;
		
		ScorePlayer player = MidiScorePlayer.getInstance();

		@Override 
		public void actionPerformed(ActionEvent event) {
			final int trackId = 0;
			if (player.getState() == ScorePlayer.OPENED && !player.isPlaying()) {
				SoundSequence sounds = new SoundSequence();
				float start = 0;
				float end = PLAY_DURATION;
				for (Height height : m_chordChooser.getChord().getHeights()) {
					sounds.add(new SoundItem(null, trackId, height.getSoundId(), start, end));
					start += HEIGHT_SEPARATION_DELAY;
					end -= HEIGHT_SEPARATION_DELAY;
				}
				player.setSounds(sounds);
				player.setInstrument(trackId, 0);
				player.setTrackVolume(trackId, 100);
				player.addSoundPlayerListener(this);
				player.play();
			}
			else {
				ErrorMessenger.showErrorMessage(getWindow(), Localizer.get(i18n.M_MIDI_OUTPUT_ERROR));
			}
		}

		@Override 
		public void onPlaybackStarted(ScorePlayerEvent event) {
			m_playButton.setEnabled(false);
		}
		
		@Override 
		public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
			m_playButton.setEnabled(true);
			player.removeSoundPlayerListener(this);
		}

		@Override public void onPlaybackPaused(ScorePlayerEvent event) {}
		@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}
		@Override public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {}
		@Override public void onScorePlayerStateChanged(ScorePlayerEvent event) {}
	}
	
	
	/**
	 * Modification de l'accord
	 */
	private class ChordChangedAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			update();
		}
	}
	
	
	//
	// Attributs
	//

	private Chord m_chord = null;
	
	private ChordChooser m_chordChooser = null;
	private JLabel m_compositionLabel = null;
	private JButton m_playButton = null;
	
}
