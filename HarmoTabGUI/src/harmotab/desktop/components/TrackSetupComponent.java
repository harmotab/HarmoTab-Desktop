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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import harmotab.core.*;
import harmotab.desktop.actions.ActionButton;
import harmotab.desktop.actions.UserAction;
import harmotab.desktop.setupdialog.TrackSetupDialog;
import harmotab.desktop.tools.*;
import harmotab.track.*;


/**
 * Composant de modification des propriétés d'une piste.
 * Panel contenant un bouton d'accès à la fenêtre de configuration de la piste,
 * nom de la piste et composant de réglage du volume
 */
public class TrackSetupComponent extends JPanel implements ChangeListener, HarmoTabObjectListener {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//

	public TrackSetupComponent(Window parent, Track track) {
		m_parentWindow = parent;
		m_track = track;
		
		// Cr�ation des composants
		m_trackNameLabel = new JLabel(track.getName());
		m_trackNameLabel.setOpaque(false);
		m_volumeControl = new VolumeControl(m_track.getVolume());
		ActionButton trackSetupButton = new ActionButton(new TrackSetupAction());
		trackSetupButton.setText(null);
		
		// Ajout des composants � l'interface
		setLayout(new BorderLayout(10, 10));
		add(m_trackNameLabel, BorderLayout.CENTER);
		add(trackSetupButton, BorderLayout.WEST);
		add(m_volumeControl, BorderLayout.EAST);
		
		// Enregistrement des listeners
		m_volumeControl.addChangeListener(this);
		m_track.addObjectListener(this);
		
		// Affichage du composant
		setOpaque(false);
		update();
		
	}
	
	private void update() {
		m_trackNameLabel.setText(m_track.getName());
		m_volumeControl.setValue(m_track.getVolume());
	}
	
	public void finalize() {
		m_track.removeObjectListener(this);
	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	private class TrackSetupAction extends UserAction {
		private static final long serialVersionUID = 1L;

		public TrackSetupAction() {
			super(Localizer.get(i18n.TT_SETUP), ToolIcon.getIcon(ToolIcon.TUNE));
		}

		@Override
		public void run() {
			TrackSetupDialog.create(m_parentWindow, m_track).setVisible(true);
		}
	}
	
	
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == m_volumeControl) {
			if (m_volumeControl.getValue() != m_track.getVolume())
				m_track.setVolume(m_volumeControl.getValue());
		}
	}
	
	@Override
	public void onObjectChanged(HarmoTabObjectEvent event) {
		update();
	}

	
	
	//
	// Attributs
	//
	
	private Track m_track = null;
	
	private JLabel m_trackNameLabel = null;
	private VolumeControl m_volumeControl = null;
	private Window m_parentWindow = null;
	
}
