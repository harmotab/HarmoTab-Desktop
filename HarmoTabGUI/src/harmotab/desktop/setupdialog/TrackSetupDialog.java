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

import java.awt.Window;

import harmotab.core.*;
import harmotab.desktop.DesktopController;
import harmotab.desktop.components.*;
import harmotab.track.*;
import javax.swing.*;


public class TrackSetupDialog extends SetupDialog {
	private static final long serialVersionUID = 1L;
	

	//
	// Constructeur
	//
	
	public static TrackSetupDialog create(Window parent, Track track) {
			return new TrackSetupDialog(parent, track);
	}
	

	protected TrackSetupDialog(Window parent, Track track) {
		super(parent, Localizer.get(i18n.ET_TRACK_SETUP));
		m_track = track;
		m_trackSetupCategory = new SetupCategory(Localizer.get(i18n.ET_TRACK_SETUP_CATEGORY));
		
		// Initialisation des composants graphiques
		m_trackNameText = new JTextField(m_track.getName());
		m_instrumentChooser = new InstrumentChooser(m_track.getInstrument());
		m_commentsArea = new JTextArea(m_track.getComment());
		m_commentsArea.setBorder(m_trackNameText.getBorder());
		m_volumeSlider = new JSlider(0, 100, m_track.getVolume());
		m_volumeSlider.setMinorTickSpacing(5);
		m_volumeSlider.setMajorTickSpacing(10);
		m_volumeSlider.setPaintLabels(true);
		m_volumeSlider.setPaintTicks(true);
		m_volumeSlider.setOpaque(false);
		
		// Configuration de l'IHM
		JPanel trackSetupPane = m_trackSetupCategory.getPanel();
		
		trackSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_TRACK_SETUP)));
		trackSetupPane.add(createSetupField(Localizer.get(i18n.N_NAME), m_trackNameText));
		trackSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_INSTRUMENT_SETUP)));
		trackSetupPane.add(createSetupField(Localizer.get(i18n.N_INSTRUMENT), m_instrumentChooser));
		trackSetupPane.add(createSetupField(Localizer.get(i18n.N_VOLUME), m_volumeSlider));
		trackSetupPane.add(createSetupSeparator(Localizer.get(i18n.N_COMMENTS)));
		trackSetupPane.add(createSetupField(Localizer.get(i18n.N_COMMENTS), m_commentsArea));
		
		// Ajout de la cat�gorie � la fen�tre de configuration
		addSetupCategory(m_trackSetupCategory);
		
		// Enregistrement des listeners
		
		
		// Affichage
		boolean scoreEditable = DesktopController.getInstance().getScoreController().isScoreEditable();
		m_trackNameText.setEnabled(scoreEditable);
		m_commentsArea.setEnabled(scoreEditable);
		
		displayCategory(m_trackSetupCategory);
	}

	
	//
	// Getters / setters
	//
	
	public Track getTrack() {
		return m_track;
	}
	
	
	//
	// Gestion du contenu
	//
	
	@Override
	protected void discard() {
	}

	@Override
	protected boolean save() {
		m_track.setName(m_trackNameText.getText());
		m_track.setInstrument(m_instrumentChooser.getSelectedIndex());
		m_track.setVolume(m_volumeSlider.getValue());
		m_track.setComment(m_commentsArea.getText());
		return true;
	}
	
	
	//
	// Attributs
	//
	
	private Track m_track = null;
	private SetupCategory m_trackSetupCategory = null;	
	
	private JTextField m_trackNameText = null;
	private InstrumentChooser m_instrumentChooser = null;
	private JSlider m_volumeSlider = null;
	private JTextArea m_commentsArea = null;

}
