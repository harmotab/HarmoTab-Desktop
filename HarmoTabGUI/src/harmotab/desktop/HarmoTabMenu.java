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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.actions.*;
import harmotab.element.Element;
import harmotab.element.TrackElement;
import harmotab.renderer.LocationItem;
import harmotab.renderer.LocationItemFlag;
import harmotab.sound.*;
import harmotab.track.Track;


/**
 * Menu principal de l'application
 */
public class HarmoTabMenu extends JMenuBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Construction du menu
	 */
	public HarmoTabMenu() {
		
		m_scoreController = DesktopController.getInstance().getScoreController();
		
		//
		// Création des menus
		//
		m_fileMenu = new JMenu(Localizer.get(i18n.MENU_FILE));
		m_newMenu = new ActionMenuItem(new NewScoreAction());
		m_openMenu = new ActionMenuItem(new OpenScoreAction());		
		m_saveMenu = new ActionMenuItem(new SaveAction());		
		m_saveAsMenu = new ActionMenuItem(new SaveScoreAsAction());
		m_exportMenu = new JMenu(Localizer.get(i18n.MENU_EXPORT));
		m_exportMenu.setIcon(ActionIcon.getIcon(ActionIcon.EXPORT));
		m_exportImageMenu = new ActionMenuItem(new ExportAsImageAction());
		m_exportHt3xMenu = new ActionMenuItem(new ExportAsHt3xAction());
		m_exportMidiMenu = new ActionMenuItem(new ExportAsMidiAction());
		m_printMenu = new ActionMenuItem(new PrintScoreAction());
		m_closeMenu = new ActionMenuItem(new CloseScoreAction());
		m_recentFilesMenu = new RecentFilesMenu();
		m_quitMenu = new ActionMenuItem(new QuitAction());
		
		m_editMenu = new JMenu(Localizer.get(i18n.MENU_EDIT));
		m_undoMenu = new ActionMenuItem(new UndoAction());
		m_redoMenu = new ActionMenuItem(new RedoAction());
		m_insertBeforeMenu = AddElementMenu.createInsertBefore(null, null);
		m_insertAfterMenu = AddElementMenu.createInsertAfter(null, null);
		m_insertLastMenu = AddElementMenu.createInsertLast(null);
		m_deleteMenu = new ActionMenuItem(new DeleteAction());
		
		m_playbackMenu = new JMenu(Localizer.get(i18n.MENU_PLAYBACK));
		m_playAllMenu = new ActionMenuItem(new PlayAction());
		m_playCurrentMenu = new ActionMenuItem(new PlayFromAction());
		m_pauseMenu = new ActionMenuItem(new PauseAction());
		m_stopMenu = new ActionMenuItem(new StopAction());
		
		m_viewMenu = new JMenu(Localizer.get(i18n.MENU_VIEW));
		m_navigationPanelVisible = new JCheckBoxMenuItem(
				Localizer.get(i18n.MENU_SHOW_NAVIGATION_PANEL), 
				DesktopController.getInstance().getGuiWindow().isBrowsersPaneVisible());
		m_navigationPanelVisible.addActionListener(new BrowsersPaneCheckboxToggledAction());
		m_scorePropertiesMenu = new ActionMenuItem(new ShowScorePropertiesAction());
		m_modelEditorMenu = new ActionMenuItem(new ShowModelEditorAction());
		m_retabMenu = new ActionMenuItem(new RetabAction());
		m_preferencesMenu = new ActionMenuItem(new ShowPreferencesAction());
		
		m_helpMenu = new JMenu(Localizer.get(i18n.MENU_HELP));
		m_helpContentMenu = new ActionMenuItem(new ShowHelpContentAction());
		m_aboutMenu = new ActionMenuItem(new ShowAboutAction());
		
		//
		// Initialisation des composants
		//
		
		m_saveMenu.setEnabled(false);		
		m_saveAsMenu.setEnabled(false);
		m_exportMenu.setEnabled(false);
		m_exportMenu.setEnabled(false);
		m_printMenu.setEnabled(false);
		m_closeMenu.setEnabled(false);
		
		m_undoMenu.setEnabled(false);
		m_redoMenu.setEnabled(false);
		m_insertBeforeMenu.setEnabled(false);
		m_insertAfterMenu.setEnabled(false);
		m_insertLastMenu.setEnabled(false);
		m_deleteMenu.setEnabled(false);
		
		m_playAllMenu.setEnabled(false);
		m_playCurrentMenu.setEnabled(false);
		m_pauseMenu.setEnabled(false);
		m_stopMenu.setEnabled(false);

		m_scorePropertiesMenu.setEnabled(false);
		m_retabMenu.setEnabled(false);
		
		//
		// Composition du menu
		//
		m_fileMenu.add(m_newMenu);
		m_fileMenu.add(m_openMenu);
		m_fileMenu.addSeparator();
		m_fileMenu.add(m_closeMenu);
		m_fileMenu.addSeparator();
		m_fileMenu.add(m_saveMenu);
		m_fileMenu.add(m_saveAsMenu);
		m_fileMenu.addSeparator();
		m_fileMenu.add(m_exportMenu);
		m_exportMenu.add(m_exportImageMenu);
		m_exportMenu.add(m_exportMidiMenu);
		if (GlobalPreferences.getPerformancesFeatureEnabled()) {
			m_exportMenu.add(m_exportHt3xMenu);
		}
		m_fileMenu.addSeparator();
		m_fileMenu.add(m_printMenu);
		m_fileMenu.addSeparator();
		m_fileMenu.add(m_recentFilesMenu);
		m_fileMenu.addSeparator();
		m_fileMenu.add(m_quitMenu);
		add(m_fileMenu);
		
		m_editMenu.add(m_undoMenu);
		m_editMenu.add(m_redoMenu);
		m_editMenu.addSeparator();
		m_editMenu.add(m_insertBeforeMenu);
		m_editMenu.add(m_insertAfterMenu);
		m_editMenu.add(m_insertLastMenu);
		m_editMenu.add(m_deleteMenu);
		add(m_editMenu);

		m_playbackMenu.add(m_playAllMenu);
		m_playbackMenu.add(m_playCurrentMenu);
		m_playbackMenu.add(m_pauseMenu);
		m_playbackMenu.add(m_stopMenu);
		add(m_playbackMenu);
		
		m_viewMenu.add(m_navigationPanelVisible);
		m_viewMenu.addSeparator();
		m_viewMenu.add(m_scorePropertiesMenu);
		m_viewMenu.add(m_preferencesMenu);
		m_viewMenu.addSeparator();
		m_viewMenu.add(m_modelEditorMenu);
		m_viewMenu.add(m_retabMenu);
		add(m_viewMenu);
		
		m_helpMenu.add(m_helpContentMenu);
		m_helpMenu.addSeparator();
		m_helpMenu.add(m_aboutMenu);
		add(m_helpMenu);
		
		//
		// Enregistrement des listeners
		//
		m_scoreController.addScoreControllerListener(new ScoreControllerObserver());
		DesktopController.getInstance().addSelectionListener(new SelectionObserver());
		
	}
	

	//
	// Ecoute des diff�rents �v�nements du logiciel
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
			// Mise à jour de l'activation du menu "Enregistrer"
			SwingUtilities.invokeLater(
					new Runnable() {
						public void run() {
							m_saveMenu.setEnabled(DesktopController.getInstance().getScoreController().hasScoreChanged());
						}
					}
				);
			// Mise à jour de l'état et du tooltip des menus "Annuler" et "Refaire"
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						// Mise � jour du bouton undo
						boolean hasUndoCommands = m_undoManager.hasUndoCommands();
						m_undoMenu.setEnabled(hasUndoCommands);
						if (hasUndoCommands == true) {
							m_undoMenu.setToolTipText(
									Localizer.get(i18n.ET_MODIFICATION) + " " +
									m_undoManager.getTopUndoLabel());
						}
						else {
							m_undoMenu.setToolTipText("");
						}
						// Mise � jour du bouton redo
						boolean hasRedoCommand = m_undoManager.hasRedoCommands();
						m_redoMenu.setEnabled(hasRedoCommand);
						if (hasRedoCommand == true) {
							m_redoMenu.setToolTipText(
									Localizer.get(i18n.ET_MODIFICATION) + " " +
									m_undoManager.getTopRedoLabel());
						}
						else {
							m_redoMenu.setToolTipText("");
						}
					}
				}
			);
		}
		
		private UndoManager m_undoManager = null;
	}
	
	
	/**
	 * R�actions aux �v�nements de lecture de la partition
	 */
	private class SoundPlayerObserver implements ScorePlayerListener {
		
		public SoundPlayerObserver() {
			updateEnabledMenu();
		}
		
		private void updateEnabledMenu() {
			ScorePlayer player = DesktopController.getInstance().getScoreController().getScorePlayer();
			boolean openned = (player.getState() == ScorePlayer.OPENED);
			boolean playing = player.isPlaying();
			boolean paused = player.isPaused();
			
			m_playAllMenu.setEnabled(openned && !playing && !paused);
			m_playCurrentMenu.setEnabled(openned && !playing && !paused);
			m_pauseMenu.setEnabled(openned && (playing || paused));
			m_stopMenu.setEnabled(openned && (playing || paused));
		}

		@Override
		public void onScorePlayerStateChanged(ScorePlayerEvent event) {
			updateEnabledMenu();
		}

		@Override
		public void onPlaybackPaused(ScorePlayerEvent event) {
			updateEnabledMenu();
		}

		@Override
		public void onPlaybackStarted(ScorePlayerEvent event) {
			updateEnabledMenu();
			m_undoMenu.setEnabled(false);
			m_redoMenu.setEnabled(false);
			m_deleteMenu.setEnabled(false);
			m_insertBeforeMenu.setEnabled(false);
			m_insertAfterMenu.setEnabled(false);
			m_insertLastMenu.setEnabled(false);
			m_scorePropertiesMenu.setEnabled(false);
			m_retabMenu.setEnabled(false);
		}

		@Override
		public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
			m_scorePropertiesMenu.setEnabled(true);
			m_retabMenu.setEnabled(true);
			m_undoMenu.setEnabled(UndoManager.getInstance().hasUndoCommands());
			m_redoMenu.setEnabled(UndoManager.getInstance().hasRedoCommands());
			updateEnabledMenu();
		}

		@Override public void onPlayedSoundItemChanged(ScorePlayerEvent event) {}
		@Override public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {}
		
	}
	
	
	/**
	 * R�action aux changement de partitions controll�e
	 */
	private class ScoreControllerObserver implements ScoreControllerListener {
		@Override
		public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
			// Une partition est actuellement control�e
			if (scoreControlled != null) {
				boolean scoreChanged = controller.hasScoreChanged();
				m_closeMenu.setEnabled(true);
				m_saveMenu.setEnabled(scoreChanged);
				m_saveAsMenu.setEnabled(true);
				m_printMenu.setEnabled(true);
				m_exportMenu.setEnabled(true);
				m_playAllMenu.setEnabled(true);
				m_playCurrentMenu.setEnabled(true);
				m_scorePropertiesMenu.setEnabled(true);
				m_retabMenu.setEnabled(true);
				scoreControlled.addObjectListener(new ScoreChangesObserver());
			}
			// Pas de partition actuellement control�e
			else {
				m_closeMenu.setEnabled(false);
				m_saveMenu.setEnabled(false);
				m_saveAsMenu.setEnabled(false);
				m_printMenu.setEnabled(false);
				m_exportMenu.setEnabled(false);
				m_playAllMenu.setEnabled(false);
				m_playCurrentMenu.setEnabled(false);
				m_scorePropertiesMenu.setEnabled(false);
				m_retabMenu.setEnabled(false);
			}
		}

		@Override
		public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
			if (soundPlayer != null) {
				soundPlayer.addSoundPlayerListener(new SoundPlayerObserver());
			}
		}
	}
	
	
	
	/**
	 * R�actions aux modifications de la visibilit� du panneau de navigation
	 */
	private class BrowsersPaneCheckboxToggledAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			new TogglePanelAction().actionPerformed(event);
			m_navigationPanelVisible.setSelected(
					DesktopController.getInstance().getGuiWindow().isBrowsersPaneVisible()
				);
		}
	}
	

	/**
	 * R�actions aux changements de la s�lection
	 */
	private class SelectionObserver implements SelectionListener {
		@Override
		public void onSelectionChanged(ScoreViewSelection selection) {
			// Si aucun �l�ment n'est s�lectionn�
			if (selection == null) {
				m_deleteMenu.setEnabled(false);
				m_insertBeforeMenu.setEnabled(false);
				m_insertAfterMenu.setEnabled(false);
				m_insertLastMenu.setEnabled(false);
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
				m_deleteMenu.setEnabled(editionEnabled);
				m_insertBeforeMenu.setEnabled(editionEnabled);
				m_insertAfterMenu.setEnabled(editionEnabled);
				m_insertLastMenu.setEnabled(editionEnabled);
				if (editionEnabled) {
					Track track = selection.getTrack();
					Element ref = selection.getLocationItem().getRootElement();
					m_insertBeforeMenu.setReference(track, ref);
					m_insertAfterMenu.setReference(track, ref);
					m_insertLastMenu.setReference(track, null);
				}
			}
		}
	}
	
	
	//
	// Inners classes
	//
	
	/**
	 * Sous-menu sp�cifique pour le menu "Ouverture" qui se rempli avec la liste
	 * des fichiers r�cemment ouverts lors de son affichage
	 */
	private class RecentFilesMenu extends JMenu {
		private static final long serialVersionUID = 1L;

		public RecentFilesMenu() {
			super(Localizer.get(i18n.MENU_RECENT_FILES));
		}
		
		@Override
		protected void fireMenuSelected() {
			// Ajoute les entr�es pour les ouvertures de fichiers r�cents
			removeAll();
			int i = 0;
			for (String filepath : RecentFilesManager.getInstance().getRecentFiles()) {
				i++;
				File file = new File(filepath);
				JMenuItem item = new ActionMenuItem(new OpenScoreAction(file.getAbsolutePath()));
				item.setText(i + ". " + file.getName());
				item.setToolTipText(file.getAbsolutePath());
				item.setIcon(null);
				add(item);
			}
			validate();

			super.fireMenuSelected();
		}
		
	}
	
	
	
	//
	// Attributs
	//
	
	private ScoreController m_scoreController = null;
	
	private JMenu m_fileMenu = null;
	private JMenuItem m_closeMenu = null;
	private JMenuItem m_newMenu = null;
	private JMenuItem m_openMenu = null;
	private JMenuItem m_saveMenu = null;
	private JMenuItem m_saveAsMenu = null;
	private JMenu m_exportMenu = null;
	private JMenuItem m_exportImageMenu = null;
	private JMenuItem m_exportMidiMenu = null;
	private JMenuItem m_exportHt3xMenu = null;
	private JMenuItem m_printMenu = null;
	private JMenu m_recentFilesMenu = null;
	private JMenuItem m_quitMenu = null;
	
	private JMenu m_editMenu = null;
	private JMenuItem m_undoMenu = null;
	private JMenuItem m_redoMenu = null;
	private AddElementMenu m_insertBeforeMenu = null;
	private AddElementMenu m_insertAfterMenu = null;
	private AddElementMenu m_insertLastMenu = null;
	private JMenuItem m_deleteMenu = null;
	
	private JMenu m_playbackMenu = null;
	private JMenuItem m_playAllMenu = null;
	private JMenuItem m_playCurrentMenu = null;
	private JMenuItem m_pauseMenu = null;
	private JMenuItem m_stopMenu = null;
	
	private JMenu m_viewMenu = null;
	private JCheckBoxMenuItem m_navigationPanelVisible = null;
	private JMenuItem m_scorePropertiesMenu = null;
	private JMenuItem m_modelEditorMenu = null;
	private JMenuItem m_retabMenu = null;
	private JMenuItem m_preferencesMenu = null;
	
	private JMenu m_helpMenu = null;
	private JMenuItem m_helpContentMenu = null;
	private JMenuItem m_aboutMenu = null;
		
}

