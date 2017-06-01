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

package harmotab.desktop;

import javax.swing.*;
import javax.swing.event.*;
import harmotab.core.*;
import harmotab.desktop.components.*;
import harmotab.sound.*;
import harmotab.track.*;


/**
 * Barre d'outils affich�e en bas de la partition donnant acc�s au controle de 
 * la lecture de la partition et l'enregistrement d'interpr�tations
 */
public class ScorePlaybackToolBar extends JToolBar implements SelectionListener {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeurs
	//
	
	public ScorePlaybackToolBar(ScoreController controller, ScoreView scoreView) {
		m_selectedTrack = null;
		m_scoreController = controller;
		
		// Cr�ation des composants
		m_globalVolume = new VolumeControl(controller, GlobalPreferences.getGlobalVolume());
		m_playerControlPane = new PlayerControlPane(controller);
		m_trackNameLabel = new JLabel("");
		m_trackVolumeLabel = new JLabel(Localizer.get(i18n.N_VOLUME) + " : ");
		m_perfsControlPane = new PerformancesListControlPane(controller);
		
		// Ajouts des composants � la barre d'outils
		add(m_perfsControlPane);
		add(m_playerControlPane);
		addSeparator();
		add(Box.createHorizontalGlue());
		add(m_trackNameLabel);
		add(Box.createHorizontalGlue());
		add(m_trackVolumeLabel);
		add(m_globalVolume);
		
		// Enregistrement des listeners
		m_globalVolume.addChangeListener(new UserActionObserver());
		m_globalPreferencesObserver = new GlobalPreferencesObserver();
		GlobalPreferences.addChangeListener(m_globalPreferencesObserver);
		DesktopController.getInstance().addSelectionListener(this);		
	}

	
	public void finalize() {
		GlobalPreferences.removeChangeListener(m_globalPreferencesObserver);
		DesktopController.getInstance().removeSelectionListener(this);
	}
	
	
	//
	// Actions de l'utilisateur
	//

	private class UserActionObserver implements ChangeListener {
		
		@Override
		public void stateChanged(ChangeEvent event) {
			// Modificatin du slider de volume
			if (event.getSource() == m_globalVolume) {
				int volumeValue = m_globalVolume.getValue();
				if (volumeValue != GlobalPreferences.getGlobalVolume()) {
					GlobalPreferences.setMidiGlobalVolume(m_globalVolume.getValue());
					ScorePlayer player = m_scoreController.getScorePlayer();
					if (player != null) {
						player.setGlobalVolume(volumeValue);
					}
				}
			}
		}
		
	}
	
	
	private class GlobalPreferencesObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			m_globalVolume.setValue(GlobalPreferences.getGlobalVolume());
		}
	}

	
	// 
	// Implémentation de SelectionListener
	// 

	/**
	 * R�action aux changements de l'�l�ment s�lectionn�
	 */
	@Override
	public void onSelectionChanged(ScoreViewSelection selection) {
		// Si pas de sélection, affiche le bouton d'accès aux propriétés de la partition
		if (selection == null) {
			m_selectedTrack = null;
			m_trackNameLabel.setText("");
		}
		// S'il une piste est sélectionnée, affiche le bouton d'accès aux propriétés de la piste
		else {
			m_selectedTrack = selection.getTrack();
			if (m_selectedTrack != null) {
				m_trackNameLabel.setText(m_selectedTrack.getName());
			}
		}
	}
	

	//
	// Attributs
	//
	
	private ScoreController m_scoreController = null;
	private Track m_selectedTrack = null;
	
	private JLabel m_trackNameLabel = null;
	private JLabel m_trackVolumeLabel = null;
	private VolumeControl m_globalVolume = null;
	private PlayerControlPane m_playerControlPane = null;
	private PerformancesListControlPane m_perfsControlPane = null;
	
	private GlobalPreferencesObserver m_globalPreferencesObserver = null;

}
