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
import harmotab.desktop.actions.UserAction;
import harmotab.desktop.components.*;
import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.throwables.*;
import harmotab.track.Track;
import java.awt.*;
import java.awt.event.*;
import java.util.ListIterator;
import javax.swing.*;


/**
 * Boite d'outils graphique de modification des propri�t�s d'une note.
 */
public class NoteTool extends Tool implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public NoteTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_note = ((Note)m_locationItem.getElement());
		
		//
		// Création des composants
		
		m_upButton = new ToolButton(new MoveUpAction());
		m_downButton = new ToolButton(new MoveDownAction());
		m_figureChooser = new FigureChooser(m_note.getFigure());
		m_figureChooser.setSize(60, 30);
		m_alterationChooser = new AlterationChooser(m_note.getHeight().getAlteration());
		m_restButton = new ToolToggleButton(new ToggleRestStateAction());
		m_dottedButton = new ToolToggleButton(new ToggleDottedStateAction());
		m_tiedButton = new ToolToggleButton(new ToggleTiedStateAction());
		m_tripletButton = new ToolToggleButton(new ChangeTrippletStateAction());
		
		//
		// Initialisation des composants
			
		if (m_note.isRest()) {
			m_restButton.setSelected(true);
			m_figureChooser.setDisplayRests(true);
			m_alterationChooser.setEnabled(false);
			m_upButton.setEnabled(false);
			m_downButton.setEnabled(false);
			m_tiedButton.setEnabled(false);
		}
		else {
			m_alterationChooser.setSelectedAlteration(m_note.getHeight().getAlteration());
			m_restButton.setSelected(false);
		}
		
		if (m_note.isTied()) {
			m_tiedButton.setSelected(true);
			m_upButton.setEnabled(false);
			m_downButton.setEnabled(false);
			m_alterationChooser.setEnabled(false);
			m_restButton.setEnabled(false);
		}
		else {
			m_tiedButton.setSelected(false);
		}
		
		m_dottedButton.setSelected(m_note.getFigure().isDotted());
		m_tripletButton.setSelected(m_note.getFigure().isTriplet());
		
		//
		// Ajout des composants à la barre d'outils
		
		add(m_upButton);
		add(m_downButton);
		addSeparator();
		add(m_figureChooser);
		add(m_alterationChooser);
		addSeparator();
		add(m_restButton);
		add(m_dottedButton);
		add(m_tiedButton);
		add(m_tripletButton);

		// Enregistrement des listeners
		m_figureChooser.addActionListener(this);
		m_alterationChooser.addActionListener(this);
		
	}
	
	
	//
	// Action sur un composant graphique
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		UserAction action = null;

		// Modification de la figure
		if (event.getSource() == m_figureChooser) {
			Boolean dotted = m_note.getFigure().isDotted();
			Figure figure = m_figureChooser.getSelectedFigure();
			figure.setDotted(dotted);
			action = new ChangeFigureAction(figure);
		}
		// Modification de l'alt�ration
		else if (event.getSource() == m_alterationChooser) {
			Height height = m_note.getHeight();
			UndoManager.getInstance().addUndoCommand(height.createRestoreCommand(), Localizer.get(i18n.N_ALTERATION));
			height.setAlteration(m_alterationChooser.getSelectedAlteration());
			tieCheck();
		}
		
		if (action != null) {
			SwingUtilities.invokeLater(action);
		}
	}
	
	
	//
	// Frappe d'une touche du clavier
	//

	@Override
	public void keyTyped(KeyEvent event) {
		UserAction action = null;
		
		switch (event.getKeyCode()) {
			case KeyEvent.VK_UP:	action = new MoveUpAction();		break;
			case KeyEvent.VK_DOWN:	action = new MoveDownAction();		break;
		}
		
		switch (event.getKeyChar()) {
			case '.': {
				Figure figure = m_note.getFigure(); 
				UndoManager.getInstance().addUndoCommand(figure.createRestoreCommand(), Localizer.get(i18n.TT_DOTTED));
				figure.setDotted(!m_note.getFigure().isDotted());
			} break;
			case '+':
				try {
					Figure figure = m_note.getFigure();
					UndoManager.getInstance().addUndoCommand(figure.createRestoreCommand(), Localizer.get(i18n.N_FIGURE));
					figure.setType((byte)(m_note.getFigure().getType()-1));
				} catch (Throwable error) {}	// Ne fait rien si la durée est déjà la plus petite
				break;
			case '-':
				try {
					Figure figure = m_note.getFigure();
					UndoManager.getInstance().addUndoCommand(figure.createRestoreCommand(), Localizer.get(i18n.N_FIGURE));
					figure.setType((byte)(m_note.getFigure().getType()+1));
				} catch (Throwable error) {}	// Ne fait rient si la durée est déjà la plus petite
				break;
			case '#': {
				Height height = m_note.getHeight();
				UndoManager.getInstance().addUndoCommand(height.createRestoreCommand(), Localizer.get(i18n.N_SHARP));
				height.setAlteration(Height.SHARP);
				tieCheck();
			} break;
			case 'b': {
				Height height = m_note.getHeight();
				UndoManager.getInstance().addUndoCommand(height.createRestoreCommand(), Localizer.get(i18n.N_FLAT));
				height.setAlteration(Height.FLAT);
				tieCheck();
			} break;
			case 'n':
				Height height = m_note.getHeight();
				UndoManager.getInstance().addUndoCommand(height.createRestoreCommand(), Localizer.get(i18n.N_NATURAL));
				height.setAlteration(Height.NATURAL);
				tieCheck();
				break;
		}
		
		if (action != null) {
			SwingUtilities.invokeLater(action);
		}
	}
	
	
	//
	// Actions
	//
	
	/**
	 * Modidification de la hauteur de la note vers le haut
	 */
	private class MoveUpAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public MoveUpAction() {
			super(null, Localizer.get(i18n.TT_MOVE_UP), ToolIcon.getIcon(ToolIcon.UP));
		}

		@Override
		public void run() {
			Height height = m_note.getHeight();
			UndoManager.getInstance().addUndoCommand(height.createRestoreCommand(), Localizer.get(i18n.N_NOTE));
			height.moveUp();
			tieCheck();
		}
	}
	
	/**
	 * Modification de la hauteur de la note vers le bas
	 */
	private class MoveDownAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public MoveDownAction() {
			super(null, Localizer.get(i18n.TT_MOVE_DOWN), ToolIcon.getIcon(ToolIcon.DOWN));
		}

		@Override
		public void run() {
			Height height = m_note.getHeight();
			UndoManager.getInstance().addUndoCommand(height.createRestoreCommand(), Localizer.get(i18n.N_NOTE));
			height.moveDown();
			tieCheck();
		}
	}
	
	
	/**
	 * Modification de la figure
	 */
	private class ChangeFigureAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public ChangeFigureAction(Figure figure) {
			super(null, null);
			m_newFigure = figure;
		}

		@Override
		public void run() {
			UndoManager.getInstance().addUndoCommand(m_note.createRestoreCommand(), Localizer.get(i18n.N_FIGURE));
			m_note.setFigure(m_newFigure);
		}
		
		private Figure m_newFigure = null;
	}
	
	/**
	 * Modification de l'état silence de la note
	 */
	private class ToggleRestStateAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public ToggleRestStateAction() {
			super(null, Localizer.get(i18n.TT_REST), ToolIcon.getIcon(ToolIcon.REST));
		}

		@Override
		public void run() {
			UndoManager.getInstance().addUndoCommand(m_note.createRestoreCommand(), Localizer.get(i18n.TT_REST));
			m_note.setRest(!m_note.isRest());
			tieCheck();
		}
	}
	
	/**
	 * Modification de l'�tat point� de la note
	 */
	private class ToggleDottedStateAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public ToggleDottedStateAction() {
			super(null, Localizer.get(i18n.TT_DOTTED), ToolIcon.getIcon(ToolIcon.DOT));
		}

		@Override
		public void run() {
			Figure figure = m_note.getFigure();
			UndoManager.getInstance().addUndoCommand(figure.createRestoreCommand(), Localizer.get(i18n.TT_DOTTED));
			figure.setDotted(!figure.isDotted());
		}
	}
	
	/**
	 * Modification de l'�tat li�e de la note
	 */
	private class ToggleTiedStateAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public ToggleTiedStateAction() {
			super(null, Localizer.get(i18n.TT_TIED), ToolIcon.getIcon(ToolIcon.LINK));
		}

		@Override
		public void run() {
			UndoManager.getInstance().addUndoCommand(m_note.createRestoreCommand(), Localizer.get(i18n.TT_TIED));
			m_note.setTied(!m_note.isTied());
			tieCheck();
		}
	}
	
	/**
	 * Modification de l'�tat triolet de la note
	 */
	private class ChangeTrippletStateAction extends UserAction {
		private static final long serialVersionUID = 1L;
		
		public ChangeTrippletStateAction() {
			super(null, Localizer.get(i18n.TT_TRIPLET), ToolIcon.getIcon(ToolIcon.TRIPLET));
		}

		@Override
		public void run() {
			Figure figure = m_note.getFigure();
			UndoManager.getInstance().addUndoCommand(figure.createRestoreCommand(), Localizer.get(i18n.TT_TRIPLET));
			figure.setTriplet(m_tripletButton.isSelected());
		}
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	/**
	 * S'assure de la coh�rence des liaisons.
	 */
	private void tieCheck() {
		try {
			ListIterator<Element> iterator = getTrack().listIterator(m_note);
			Element contiguousElement = iterator.next();
			boolean found = false;
			Note first = m_note;
						
			// Remonte � la premi�re note non li�e de la s�rie
			while (!found && iterator.hasPrevious()) {
				contiguousElement = iterator.previous();
				if (contiguousElement instanceof Note) {
					first = (Note) contiguousElement;
					if (!first.isTied()) {
						found = true;
					}
				}
			}
			
			// Parcours toutes les notes li�es de la s�rie et leur affecte la m�me hauteur
			// que la note courante
			// Délie la note suivant si la note courante est un silence
			boolean prevIsRest = false;
			if (iterator.hasNext()) {
				do {
					contiguousElement = iterator.next();
					if (contiguousElement instanceof Note) {
						Note contiguousNote = (Note) contiguousElement;
						if (contiguousNote.isTied() || contiguousNote == first) {
							// Délie la note si la précédente est un silence est silence
							if (prevIsRest) {
								UndoManager.getInstance().appendToLastUndoCommand(contiguousNote.createRestoreCommand());
								contiguousNote.setTied(false);
								break;
							}
							prevIsRest = contiguousNote.isRest();
							// Met la note à la même hauteur si besoin
							if (contiguousNote.getHeight().getSoundId() != first.getHeight().getSoundId()) {
								UndoManager.getInstance().appendToLastUndoCommand(contiguousNote.createRestoreCommand());
								contiguousNote.setHeight(new Height(first.getHeight()));
							}
						}
						else {
							//iterator = null;
							break;
						}
					}
				} while (/*iterator != null && */iterator.hasNext());
			}
			
		}
		catch (ObjectNotFoundError e) {
			// Se produit en cas de suppression de la note courante : la liaison 
			// de la note suivante doit �tre cass�e
			int index = m_locationItem.getElementIndex();
			Track track = getTrack();
			while (index >= 0 && index < track.size()) {
				if (track.get(index) instanceof Note) {
					Note note = (Note) track.get(index);
					if (note.isTied()) {
						UndoManager.getInstance().appendToLastUndoCommand(note.createRestoreCommand());
						note.setTied(false);
					}
					index = -2;
				}
				index++;
			}
		}	
	}
	
	
	//
	// Attributs
	//
	
	private JButton m_upButton = null;
	private JButton m_downButton = null;
	private JToggleButton m_restButton = null;
	private JToggleButton m_dottedButton = null;
	private JToggleButton m_tiedButton = null;
	private JToggleButton m_tripletButton = null;
	private FigureChooser m_figureChooser = null;
	private AlterationChooser m_alterationChooser = null;
	
	private Note m_note = null;

}
