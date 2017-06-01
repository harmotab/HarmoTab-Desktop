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

import javax.swing.event.EventListenerList;
import harmotab.core.ScoreController;
import harmotab.core.ScoreView;
import harmotab.core.ScoreViewSelection;
import harmotab.desktop.browser.BrowsersPane;


/**
 * Fournit un acc�s aux �l�ments principaux de l'interface graphique
 */
public class DesktopController {
	
	//
	// Constructeur
	//
	
	private DesktopController() {
		m_listeners = new EventListenerList();
	}
	
	public static synchronized DesktopController getInstance() {
		if (m_instance == null)
			m_instance = new DesktopController();			
		return m_instance;
	}
	
	
	//
	// Gestion des �tats de l'application
	//

	public ScoreViewSelection getCurrentSelection() {
		return m_scoreViewSelection;
	}
	
	
	public void setGuiWindow(Gui gui) {
		m_guiWindow = gui;
	}
	
	public Gui getGuiWindow() {
		return m_guiWindow;
	}
	
	
	public ScoreController getScoreController() {
		return m_scoreController;
	}

	public void setScoreController(ScoreController scoreController) {
		m_scoreController = scoreController;
	}
	
	
	public ScoreView getScoreView() {
		return m_scoreView;
	}
	
	public void setScoreView(ScoreView scoreView) {
		m_scoreView = scoreView;
	}
	
	
	public void setBrowsersPane(BrowsersPane pane) {
		m_browsersPane = pane;
	}
	
	public BrowsersPane getBrowsersPane() {
		return m_browsersPane;
	}

	
	//
	// Gestion des �v�nements de changement de l'�l�ment s�lectionn�
	//
	
	public void addSelectionListener(SelectionListener listener) {
		m_listeners.add(SelectionListener.class, listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		m_listeners.remove(SelectionListener.class, listener);
	}
	
	public void fireSelectionChanged(ScoreViewSelection selection) {
		m_scoreViewSelection = selection;
		for (SelectionListener listener : m_listeners.getListeners(SelectionListener.class))
			listener.onSelectionChanged(selection);
	}
	
	
	//
	// Attributs
	//
	
	private static DesktopController m_instance = null;
	private EventListenerList m_listeners = null;
	private ScoreViewSelection m_scoreViewSelection = null;
	private ScoreController m_scoreController = null;
	private ScoreView m_scoreView = null;
	private BrowsersPane m_browsersPane = null;
	private Gui m_guiWindow = null;
	
}

