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

import harmotab.core.Localizer;
import harmotab.core.Score;
import harmotab.core.i18n;
import harmotab.core.undo.UndoManager;
import harmotab.element.Tempo;
import harmotab.renderer.LocationItem;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Boite � outils de modification du tempo
 */
public class TempoTool extends Tool implements ChangeListener {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeur / destructeur
	//
	
	public TempoTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_tempo = (Tempo) item.getElement();
		
		// Construction des composants
		m_tempoSpinner = new JSpinner(new SpinnerNumberModel(m_tempo.getValue(), Tempo.MIN_TEMPO_VALUE, Tempo.MAX_TEMPO_VALUE, 1));
		
		// Ajout des composants � la barre d'outils
		add(m_tempoSpinner);
		
		// Enregistrement des listeners
		m_tempoSpinner.addChangeListener(this);

	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//

	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == m_tempoSpinner) {
			UndoManager.getInstance().addUndoCommand(m_tempo.createRestoreCommand(), Localizer.get(i18n.N_TEMPO));
			m_tempo.setValue((Integer) m_tempoSpinner.getValue());
		}
	}
	
	
	// 
	// Attributs
	// 

	private Tempo m_tempo;
	private JSpinner m_tempoSpinner;
	
}
