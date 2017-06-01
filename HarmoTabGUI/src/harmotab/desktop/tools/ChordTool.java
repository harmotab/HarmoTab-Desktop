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

import harmotab.renderer.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.components.*;
import harmotab.desktop.setupdialog.*;
import harmotab.element.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Boite d'outils de modification d'un accord
 */
public class ChordTool extends Tool implements ActionListener {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public ChordTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_chord = (Chord) item.getElement();
		
		m_chordEditorButton = new ToolButton(
				Localizer.get(i18n.TT_CHORD_SELECTION), 
				ToolIcon.TUNE);
		m_chordEditorButton.addActionListener(this);
		add(m_chordEditorButton);
		
		addSeparator();
		
		m_figureChooser = new FigureChooser(m_chord.getFigure());
		m_figureChooser.setSize(60, 30);
		m_figureChooser.addActionListener(this);
		add(m_figureChooser);

	}

	
	//
	// Gestion des actions de l'utilisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// Action sur le bouton de modification de l'accord
		if (event.getSource() == m_chordEditorButton) {
			UndoManager.getInstance().addUndoCommand(m_chord.createRestoreCommand(), i18n.TT_CHORD_SELECTION);
			new ChordSetupDialog(null, m_chord).setVisible(true);
		}
		// Action sur le bouton de s�lection de la figure
		else if (event.getSource() == m_figureChooser) {
			UndoManager.getInstance().addUndoCommand(m_chord.createRestoreCommand(), Localizer.get(i18n.N_FIGURE));
			m_chord.setFigure(m_figureChooser.getSelectedFigure());
		}
	}

	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	//
	// Attributs
	//
	
	private Chord m_chord;
	private ToolButton m_chordEditorButton;
	private FigureChooser m_figureChooser;
	
}
