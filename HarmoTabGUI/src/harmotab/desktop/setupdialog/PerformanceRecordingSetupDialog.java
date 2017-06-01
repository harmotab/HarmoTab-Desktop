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

import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.desktop.components.InputLevelViewer;
import harmotab.performance.Performance;
import harmotab.sound.PcmRecorder;
import harmotab.sound.Recorder;

import javax.swing.JPanel;
import java.awt.Window;


/**
 * Boite de dialogue permettant de lancer l'enregistrement d'une intepr�tation
 */
public class PerformanceRecordingSetupDialog extends PerformanceSetupDialog {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeur
	//
	
	public PerformanceRecordingSetupDialog(Window parent) {
		super(parent, new Performance());
		
		m_recorder = new PcmRecorder();
		
		// Initialisation des composants graphiques
		m_levelViewer = new InputLevelViewer(m_recorder);
		
		// Ajout des composants � l'interface
		SetupCategory recordSetupCategory = new SetupCategory(Localizer.get(i18n.N_RECORDING));
		JPanel recordSetupPane = recordSetupCategory.getPanel();
		recordSetupPane.add(createSetupSeparator(Localizer.get(i18n.N_RECORDING_PARAMETERS)));
		recordSetupPane.add(createSetupField(Localizer.get(i18n.N_RECORDING_INPUT), m_levelViewer));
		
		// Enregistrement des listeners
		
		
		// Affichage de la fen�tre
		addSetupCategory(recordSetupCategory);
		m_levelViewer.start();
		
	}
	
	
	//
	// Getters / setters
	//
	
	public Recorder getRecorder() {
		return m_recorder;
	}
	
	public Performance getPerformance() {
		return m_performance;
	}

	
	//
	// M�thodes
	//
	
	@Override
	protected boolean save() {
		if (super.save()) {
			m_levelViewer.stop();
			return true;
		}
		return false;
	}

	
	@Override
	protected void discard() {
		m_levelViewer.stop();
		m_recorder = null;
		m_performance = null;
		super.discard();
	}
	
	
	//
	// Attributs
	//
	
	protected InputLevelViewer m_levelViewer = null;
	protected Recorder m_recorder = null;
	
}
