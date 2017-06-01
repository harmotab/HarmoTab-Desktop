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
import harmotab.desktop.components.*;
import harmotab.element.*;
import harmotab.renderer.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;


/**
 * Boite d'outils de mofication d'un silence
 */
public class SilenceTool extends Tool {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeur / desctructeur
	//
	
	public SilenceTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_silence = (Silence) item.getElement();
		
		// Construction des composant
		m_durationChooser = new DurationChooser(m_silence.getDuration());

		// Ajout des composants
		add(m_durationChooser);

		// Enregistrement des listeners
		m_durationChooser.addChangeListener(new DurationChangeAction());

	}
	
	
	//
	// Actions de l'utilisateur
	//

	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	private class DurationChangeAction implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			UndoManager.getInstance().addUndoCommand(m_silence.createRestoreCommand(), Localizer.get(i18n.N_DURATION));
			m_silence.setDuration(m_durationChooser.getDurationValue());
		}
	}
	
	
	//
	// Attributs
	//
	
	private Silence m_silence = null;
	private DurationChooser m_durationChooser = null;

}
