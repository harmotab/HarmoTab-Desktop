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

package harmotab.desktop.tools;

import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.setupdialog.*;
import harmotab.renderer.*;
import harmotab.track.Track;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * Boite � outils de modification d'une cl�
 */
public class KeyTool extends Tool {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public KeyTool(Container container, Score score, LocationItem item) {
		super(container, score, item);

		// Construction des composants
		m_trackPropertiesButton = new ToolButton(
				Localizer.get(i18n.ET_TRACK_SETUP), 
				ToolIcon.TUNE, 
				Localizer.get(i18n.ET_TRACK_SETUP));
		
		// Ajout des composants � l'interface
		add(m_trackPropertiesButton);
		
		// Enregistrement des listeners
		UserActionObserver listener = new UserActionObserver();
		m_trackPropertiesButton.addActionListener(listener);
		
	}
	
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	private class UserActionObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// Bouton d'accès à la configuration de la partition
			if (event.getSource() == m_trackPropertiesButton) {
				Track track = getTrack();
				UndoManager.getInstance().addUndoCommand(track.createRestoreCommand(), Localizer.get(i18n.ET_TRACK_SETUP));
				TrackSetupDialog.create(null, track).setVisible(true);
			}
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	//
	// Attributs
	//
	
	private JButton m_trackPropertiesButton = null;
	
}

