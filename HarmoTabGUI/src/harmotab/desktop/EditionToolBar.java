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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import harmotab.core.*;
import harmotab.core.undo.*;
import harmotab.desktop.actions.*;
import harmotab.element.TrackElement;
import harmotab.renderer.LocationItem;
import harmotab.renderer.LocationItemFlag;
import harmotab.sound.ScorePlayer;
import harmotab.sound.ScorePlayerListener;
import harmotab.sound.ScorePlayerEvent;


/**
 * Boite d'outils donnant acc�s aux fonctionnalit�s d'�dition de la partition
 * (annuler/r�peter, ins�rer, supprimer...)
 */
public class EditionToolBar extends JToolBar implements ScoreControllerListener, SelectionListener, ScorePlayerListener {
	private static final long serialVersionUID = 1L;

	// 
	// Constructeur
	// 
	
	public EditionToolBar(ScoreController scoreController) {
		m_scoreController = scoreController;
		
		// Cr�ation des composants
		m_undoButton = new ActionButton(new UndoAction(), KeyEvent.VK_Z, false);
		m_redoButton = new ActionButton(new RedoAction(), KeyEvent.VK_Y, false);
		m_deleteButton = new ActionButton(new DeleteAction(), false);
		m_insertBeforeButton = new ActionButton(new InsertBeforeAction(), false);
		m_insertAfterButton = new ActionButton(new InsertAfterAction(), false);
		m_insertEndButton = new ActionButton(new InsertLastAction(), false);
		m_scorePropertiesButton = new ActionButton(new ShowScorePropertiesAction(), KeyEvent.VK_F4, false);
		m_retabButton = new ActionButton(new RetabAction(), KeyEvent.VK_R, false);
		
		// Ajout des composants
		add(m_undoButton);
		add(m_redoButton);
		addSeparator();
		add(m_insertBeforeButton);
		add(m_insertAfterButton);
		add(m_insertEndButton);
		add(m_deleteButton);
		addSeparator();
		add(Box.createHorizontalGlue());
		addSeparator();
		add(m_scorePropertiesButton);
		add(m_retabButton);
		addSeparator();
		
		// Enregistrement des listeners
		m_scoreController.addScoreControllerListener(this);
		if (m_scoreController.hasScore())
			m_scoreController.getScore().addObjectListener(new ScoreChangesObserver());
		DesktopController.getInstance().addSelectionListener(this);
		
	}
	
	
	//
	// Ecoute de �v�nnements
	//
	
	/**
	 * Ecoute des modifications de la partition
	 */
	private class ScoreChangesObserver implements HarmoTabObjectListener {
		public ScoreChangesObserver() {
			m_undoManager = UndoManager.getInstance();
		}
		
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						// Mise � jour du bouton undo
						boolean hasUndoCommands = m_undoManager.hasUndoCommands();
						m_undoButton.setEnabled(hasUndoCommands);
						if (hasUndoCommands == true) {
							m_undoButton.setToolTipText(
									Localizer.get(i18n.ET_MODIFICATION) + " " +
									m_undoManager.getTopUndoLabel());
						}
						else {
							m_undoButton.setToolTipText("");
						}
						// Mise � jour du bouton redo
						boolean hasRedoCommand = m_undoManager.hasRedoCommands();
						m_redoButton.setEnabled(hasRedoCommand);
						if (hasRedoCommand == true) {
							m_redoButton.setToolTipText(
									Localizer.get(i18n.ET_MODIFICATION) + " " +
									m_undoManager.getTopRedoLabel());
						}
						else {
							m_redoButton.setToolTipText("");
						}
					}
				}
			);
		}
		
		private UndoManager m_undoManager = null;
	}
	
	
	/**
	 * Ecoute des modifications de la s�lection
	 */
	@Override
	public void onSelectionChanged(ScoreViewSelection selection) {
		// Met à jour le status des boutons annuler/répéter
		m_undoButton.setEnabled(UndoManager.getInstance().hasUndoCommands());
		m_redoButton.setEnabled(UndoManager.getInstance().hasRedoCommands());

		// Si aucun �l�ment n'est s�lectionn�
		if (selection == null) {
			m_deleteButton.setEnabled(false);
			m_insertBeforeButton.setEnabled(false);
			m_insertAfterButton.setEnabled(false);
			m_insertEndButton.setEnabled(false);
		}
		// Si un �l�ment est s�lectionn�
		else {
			LocationItem selected = selection.getLocationItem();
			// Boutons d'insertion et suppression actifs si élément non 
			// temporaire et élément de piste
			boolean editionEnabled = 
					m_scoreController.isScoreEditable() &&
					!selected.getFlag(LocationItemFlag.TEMPORARY_ELEMENT) &&
					selected.getElement() instanceof TrackElement;
			m_deleteButton.setEnabled(editionEnabled);
			m_insertBeforeButton.setEnabled(editionEnabled);
			m_insertAfterButton.setEnabled(editionEnabled);
			m_insertEndButton.setEnabled(editionEnabled);
		}
	}

	
	/**
	 * Ecoute du ScoreController
	 */
	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		// Une partition en cours
		if (scoreControlled != null) {
			scoreControlled.addObjectListener(new ScoreChangesObserver());
			m_scorePropertiesButton.setEnabled(true);
			m_retabButton.setEnabled(true);
		}
		// Pas de partition en cours
		else {
			m_scorePropertiesButton.setEnabled(false);
			m_retabButton.setEnabled(false);
		}
	}
	
	@Override public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
		soundPlayer.addSoundPlayerListener(this);
	}
	
	
	/**
	 * Ecoute de l'état de la lecture de la partition
	 */
	
	@Override
	public void onPlaybackStarted(ScorePlayerEvent event) {
		m_undoButton.setEnabled(false);
		m_redoButton.setEnabled(false);
		m_deleteButton.setEnabled(false);
		m_insertBeforeButton.setEnabled(false);
		m_insertAfterButton.setEnabled(false);
		m_insertEndButton.setEnabled(false);
		m_scorePropertiesButton.setEnabled(false);
		m_retabButton.setEnabled(false);
	}
	
	@Override
	public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		m_scorePropertiesButton.setEnabled(true);
		m_retabButton.setEnabled(true);
		m_undoButton.setEnabled(UndoManager.getInstance().hasUndoCommands());
		m_redoButton.setEnabled(UndoManager.getInstance().hasRedoCommands());
	}
	
	@Override
	public void onPlaybackPaused(ScorePlayerEvent event) {
		m_scorePropertiesButton.setEnabled(true);
	}

	@Override public void onScorePlayerStateChanged(ScorePlayerEvent event) {}
	@Override public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {}
	@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}

	
	// 
	// Bouton
	// 
	
	private class ActionButton extends JButton {
		private static final long serialVersionUID = 1L;
		private final Dimension BUTTON_DIMENSION = new Dimension(40, 32);
		
		public ActionButton(UserAction action) {
			super(action.getIcon());
			m_action = action;
			setSize(BUTTON_DIMENSION);
			setMinimumSize(BUTTON_DIMENSION);
			setMaximumSize(BUTTON_DIMENSION);
			setPreferredSize(BUTTON_DIMENSION);
			setFocusable(false);
			setBorder(new EmptyBorder(5, 0, 5, 5));
			setToolTipText(action.getLabel());
			addActionListener(action);
		}
		
		public ActionButton(UserAction action, int accelerator) {
			this(action);
			setAccelerator(accelerator);
		}
		
		public ActionButton(UserAction action, int accelerator, boolean enabled) {
			this(action, accelerator);
			setEnabled(enabled);
		}
		
		public ActionButton(UserAction action, boolean enabled) {
			this(action);
			setEnabled(enabled);
		}
				
		
		public void setAccelerator(int accelerator) {
			KeyStroke keyBtn = KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK);
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyBtn, "ctrl" + accelerator);
			getActionMap().put("ctrl" + accelerator, m_action);
		}
		
		private UserAction m_action = null;

	}
	
	
	//
	// Attributs
	//
	
	private ScoreController m_scoreController = null;
	
	private ActionButton m_undoButton = null;
	private ActionButton m_redoButton = null;
	private ActionButton m_deleteButton = null;
	private ActionButton m_insertBeforeButton = null;
	private ActionButton m_insertAfterButton = null;
	private ActionButton m_insertEndButton = null;
	private ActionButton m_scorePropertiesButton = null;
	private ActionButton m_retabButton = null;
	
}
