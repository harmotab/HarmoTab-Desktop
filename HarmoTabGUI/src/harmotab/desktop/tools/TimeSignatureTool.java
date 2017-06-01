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
import harmotab.desktop.components.TimeSignatureChooser.TimeSignatureListener;
import harmotab.element.*;
import harmotab.renderer.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Boite d'outils de modification d'une signature temporelle.
 */
public class TimeSignatureTool extends Tool implements TimeSignatureListener {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur / destructeur
	//
	
	public TimeSignatureTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_timeSignature = (TimeSignature) m_locationItem.getElement();
		m_timeSignatureChooser = new TimeSignatureChooser(m_timeSignature);
		m_timeSignatureChooser.addTimeSignatureListener(this);
		add(m_timeSignatureChooser);
	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	@Override
	public void onNumberChanged(byte number) {
		UndoManager.getInstance().addUndoCommand(m_timeSignature.createRestoreCommand(), Localizer.get(i18n.N_TIME_SIGNATURE));
		m_timeSignature.setNumber(number);
	}

	@Override
	public void onReferenceChanged(byte reference) {
		UndoManager.getInstance().addUndoCommand(m_timeSignature.createRestoreCommand(), Localizer.get(i18n.N_TIME_SIGNATURE));
		m_timeSignature.setReference(reference);
	}

	
	//
	// Attributs
	//
	
	private TimeSignature m_timeSignature = null;
	private TimeSignatureChooser m_timeSignatureChooser = null;
	
}
