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

import harmotab.core.*;
import harmotab.performance.*;

import javax.swing.JComboBox;


/**
 * Composant de s�lection de l'une des interpr�tation de la partition 
 * enregistr�es.
 */
public class PerformancesChooser extends JComboBox implements PerformanceListListener {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//

	public PerformancesChooser(PerformancesList perfs, int selected) {
		m_perfs = perfs;
		m_perfs.addPerformanceListListener(this);
		updateList();
	}
	
	
	private void updateList() {
		removeAllItems();
		addItem(Localizer.get(i18n.N_SYNTHETIZER));
		for (Performance perf : m_perfs) {
			addItem(perf.getName());
		}
	}
	
	
	//
	// Getters / setters
	//
	
	public void setSelectedSynthetizer() {
		setSelectedIndex(0);
	}
	
	public void setSelectedPerformanceIndex(int selected) {
		setSelectedIndex(selected + 1);
	}
	
	public void setSelectedPerformance(Performance performance) {
		setSelectedPerformanceIndex(m_perfs.indexOf(performance));
	}
	
	public Performance getSelectedPerformance() {
		int index = getSelectedIndex() - 1;
		if (index >= 0) {
			return m_perfs.get(index);
		}
		return null;
	}
	
	
	//
	// Ecoute des modifications de la liste
	//
	
	@Override
	public void onPerformanceListChanged(PerformancesList list) {
		updateList();
	}

	@Override
	public void onDefaultPerformanceChanged(PerformancesList list) {
	}
	
	@Override
	public void onPerformanceListItemChanged(PerformancesList list, Performance perf) {
		updateList();
	}

	
	//
	// Attributs
	//
	
	protected PerformancesList m_perfs = null;
	
}
