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
import java.awt.event.*;
import harmotab.element.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Composant de s�lectiond d'une tablature (trou + directino)
 */
public class TabChooser extends JPanel {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//
	
	public TabChooser(Tab tab, boolean showPushButton) {
		m_tab = (tab != null ? new Tab(tab) : new Tab());
				
		// Cr�ation des composants
		m_holeChooser = new HoleChooser(Tab.UNDEFINED);
		m_directionChooser = new DirectionChooser();
		m_pushedChooser = showPushButton ? new PushedChooser(m_tab.isPushed()) : null;

		// Initialisation des composants
		if (m_tab != null) {
			m_holeChooser.setValue(m_tab.getHole());
			m_directionChooser.setTab(m_tab);
		}
		
		// Ajout des composants � la barre d'outils
		//setLayout(new GridLayout(1, 3));
		setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
		add(m_holeChooser);
		add(m_directionChooser);
		if (m_pushedChooser != null)
			add(m_pushedChooser);
		
		// Enregistrement des listeners
		HarmoTabChangeAction listener = new HarmoTabChangeAction();
		m_holeChooser.addChangeListener(listener);
		m_directionChooser.addActionListener(listener);
		if (m_pushedChooser != null)
			m_pushedChooser.addActionListener(listener);
				
	}
	
	public TabChooser() {
		this(null, false);
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	@Override
	public void setEnabled(boolean enabled) {
		m_directionChooser.setEnabled(enabled);
		m_holeChooser.setEnabled(enabled);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setTab(Tab tab) {
		m_tab = (tab != null ? new Tab(tab) : new Tab());
		m_holeChooser.setValue(m_tab.getHole());
		m_directionChooser.setTab(m_tab);
	}
	
	public Tab getTab() {
		return new Tab(m_tab);
	}
	
	
	//
	// Gestion des actions utilisateur
	//

	/**
	 * R�action aux actions de l'utilisateur sur l'un des composants de moficiation
	 * de la tablature 
	 */
	private class HarmoTabChangeAction implements ChangeListener, ActionListener {

		/**
		 * R�action aux modifications du trou
		 */
		@Override
		public void stateChanged(ChangeEvent event) {
			updateHole();
		}

		/**
		 * R�action aux modifications de la respiration ou de la tirette
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			updateTab();
		}
		
		
		/**
		 * Prise en compte de la modification du trou
		 */
		private void updateHole() {
			int hole = (Integer) m_holeChooser.getValue();
			if (m_tab == null) {
				m_tab = new Tab(hole);
			}
			else {
				m_tab.setHole(hole);
			}
			fireTabChanged();
		}
		
		/**
		 * Prise en compte de la modification de la respiration ou de la tirette
		 */
		private void updateTab() {
			Tab selected = m_directionChooser.getTab(Tab.UNDEFINED);
			if (m_tab == null) {
				m_tab = selected;
			}
			else {
				m_tab.setDirection(selected.getDirection());
				m_tab.setBend(selected.getBend());
				m_tab.setPushed(m_pushedChooser != null ? m_pushedChooser.isSelected() : false);
			}
			fireTabChanged();
		}
		
	}
	
	
	// 
	// Gestion des �v�nements
	// 
	
	public void addChangeListener(ChangeListener listener) {
		m_listeners.add(ChangeListener.class, listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		m_listeners.remove(ChangeListener.class, listener);
	}
	
	
	public void fireTabChanged() {
		for (ChangeListener listener : m_listeners.getListeners(ChangeListener.class))
			listener.stateChanged(new ChangeEvent(this));
	}
	
	
	//
	// Attributs
	//
	
	private EventListenerList m_listeners = new EventListenerList();
	
	private Tab m_tab = null;
	private HoleChooser m_holeChooser = null;
	private DirectionChooser m_directionChooser = null;
	private PushedChooser m_pushedChooser = null;
	
}
