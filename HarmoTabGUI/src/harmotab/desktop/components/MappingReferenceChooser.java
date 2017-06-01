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

import java.awt.*;
import java.util.ArrayList;

import harmotab.core.*;
import harmotab.element.Tab;
import harmotab.harmonica.*;
import javax.swing.*;
import javax.swing.event.*;


public class MappingReferenceChooser extends JPanel {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//

	public MappingReferenceChooser(Harmonica harmonica) {
		m_harmonica = harmonica;
		
		// Cr�ation des composants
		m_tabs = new ArrayList<Tab>();
		m_heightChooser = new HeightChooser(new Height());
		m_possibilitiesCombo = new JComboBox();
		
		// Ajout des composants � l'interface
		setLayout(new BorderLayout(10, 10));
		add(m_heightChooser, BorderLayout.WEST);
		add(m_possibilitiesCombo, BorderLayout.CENTER);
		
		// Enregistrement des listeners
		ChangesObserver listener = new ChangesObserver();
		m_heightChooser.addChangeListener(listener);
		m_harmonica.addObjectListener(listener);
		
		// Affichage du composant
		setOpaque(false);
		
	}
	
	
	//
	// Getters
	//
	
	public Tab getSelectedTab() {
		int index = m_possibilitiesCombo.getSelectedIndex();
		if (index != -1)
			return m_tabs.get(index);
		return null;
	}
	
	public Height getSelectedHeight() {
		return m_heightChooser.getSelectedHeight();
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	private class ChangesObserver implements ChangeListener, HarmoTabObjectListener {
		
		// Modification de la hauteur de la note de r�f�rence
		@Override
		public void stateChanged(ChangeEvent event) {
			updateReferencePossibilities();
		}

		// Modification de la hauteur de la 
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			updateReferencePossibilities();
		}
	}
	
	
	//
	// Actions
	//
	
	private void updateReferencePossibilities() {
		DefaultComboBoxModel model = (DefaultComboBoxModel) m_possibilitiesCombo.getModel();
		
		// Suppression des anciens �l�ments
		model.removeAllElements();
		m_tabs.clear();
		
		// Ajout des nouveaux �l�ments
		for (Tab tab : m_harmonica.getTabPossibilities(m_heightChooser.getSelectedHeight())) {
			model.addElement(tab.getHole() + " - " + tab.getBreathName());
			m_tabs.add(tab);
		}
		
		// S�lectionne la valeur centrale
		int index = model.getSize();
		if (index > 0)
			m_possibilitiesCombo.setSelectedIndex(index / 2);
			
	}
	
	
	//
	// Attributs
	//
	
	private Harmonica m_harmonica = null;
	private HeightChooser m_heightChooser = null;
	private JComboBox m_possibilitiesCombo = null;
	private ArrayList<Tab> m_tabs = null;
	
}

