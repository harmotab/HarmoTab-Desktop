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

import java.awt.*;
import java.awt.event.*;

import harmotab.core.Localizer;
import harmotab.core.Score;
import harmotab.core.i18n;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.components.*;
import harmotab.element.*;
import harmotab.renderer.LocationItem;


/**
 * Boite d'outils de modification d'une signature temporelle
 */
public class KeySignatureTool extends Tool {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeurs / destructeurs
	//

	public KeySignatureTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_keySignature = (KeySignature) item.getElement();

		// Création des composants
		m_tonalityChooser = new TonalityChooser(m_keySignature.getValue());
		
		// Ajout des composants à la barre d'outils
		add(m_tonalityChooser);
		
		// Enregistrement des listeners
		m_tonalityChooser.addActionListener(new TonalityChangesObserver());
		
	}


	//
	// Actions sur les composants
	//

	@Override
	public void keyTyped(KeyEvent event) {
	}

	
	private class TonalityChangesObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			UndoManager.getInstance().addUndoCommand(m_keySignature.createRestoreCommand(), Localizer.get(i18n.N_KEY_SIGNATURE));
			m_keySignature.setIndex(m_tonalityChooser.getTonality());
		}
	}

	
	//
	// Attributs
	//
	
	private KeySignature m_keySignature = null;
	private TonalityChooser m_tonalityChooser = null;
	
}
