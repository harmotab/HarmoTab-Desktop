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
import harmotab.desktop.ErrorMessenger;
import harmotab.desktop.components.HarmonicaTunningChooser;
import harmotab.performance.Performance;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.Window;


/**
 * Boite de dialogue de modification des attributs d'une intepr�tation
 */
public class PerformanceSetupDialog extends SetupDialog {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public PerformanceSetupDialog(Window parent, Performance performance) {
		super(parent, Localizer.get(i18n.N_RECORDING));
		m_performance = performance;
		
		// Initialisation des composants graphiques
		m_recordNameField = new JTextField(m_performance.getName());
		m_harmonicaName = new JTextField(m_performance.getHarmonica().getName());
		m_harmonicaTunningCombo = new HarmonicaTunningChooser(m_performance.getHarmonica().getTunning());
		
		// Ajout des composants � l'interface
		SetupCategory performanceSetupCategory = new SetupCategory(Localizer.get(i18n.N_RECORDING));
		JPanel performanceSetupPane = performanceSetupCategory.getPanel();
		performanceSetupPane.add(createSetupSeparator(Localizer.get(i18n.N_RECORDING)));
		performanceSetupPane.add(createSetupField(Localizer.get(i18n.N_NAME), m_recordNameField));
		performanceSetupPane.add(createSetupSeparator(Localizer.get(i18n.N_HARMONICA)));
		performanceSetupPane.add(createSetupField(Localizer.get(i18n.N_NAME), m_harmonicaName));
		performanceSetupPane.add(createSetupField(Localizer.get(i18n.N_TONALITY), m_harmonicaTunningCombo));
		
		// Enregistrement des listeners
		
		
		// Affichage de la fen�tre
		addSetupCategory(performanceSetupCategory);
		
	}
	
	
	//
	// Getters / setters
	//
	
	public Performance getPerformance() {
		return m_performance;
	}

	
	//
	// M�thodes
	//
	
	@Override
	protected boolean save() {
		String name = m_recordNameField.getText().trim();
		if (name.equals("")) {
			ErrorMessenger.showErrorMessage(this, Localizer.get(i18n.M_NO_NAME_ERROR));
			m_recordNameField.requestFocus();
			return false;
		}
		m_performance.setName(name);
		m_performance.getHarmonica().setName(m_harmonicaName.getText());
		m_performance.getHarmonica().setTunning(m_harmonicaTunningCombo.getSelectedTunning());
		return true;
	}

	
	@Override
	protected void discard() {
	}
	
	
	//
	// Attributs
	//

	protected Performance m_performance = null;
	
	protected JTextField m_recordNameField = null;
	protected JTextField m_harmonicaName = null;
	protected HarmonicaTunningChooser m_harmonicaTunningCombo = null;
	
}
