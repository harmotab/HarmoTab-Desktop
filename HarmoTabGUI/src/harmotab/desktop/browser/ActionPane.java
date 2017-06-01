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

package harmotab.desktop.browser;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import rvt.util.gui.VerticalLayout;
import harmotab.core.*;
import harmotab.desktop.*;
import harmotab.desktop.actions.*;
import harmotab.sound.ScorePlayer;


/**
 * Boite permettant l'acc�s aux actions du logiciel
 */
public class ActionPane extends JToolBar implements ScoreControllerListener {
	private static final long serialVersionUID = 1L;
	
	// 
	// Constructeur
	// 

	public ActionPane(ScoreController controller) {
		super(JToolBar.VERTICAL);
		m_scoreController = controller;
		
		//
		// Cr�ation des boutons d'action des sous-menus
		//
		
		JPopupMenu openSubMenu = new RecentFilesPopulatedPopupMenu();
		openSubMenu.add(new ActionItem(new OpenScoreAction()));
		openSubMenu.add(new ActionItem(new OpenFolderAction()));
		openSubMenu.addSeparator();
		
		JPopupMenu exportSubMenu = new JPopupMenu();
		exportSubMenu.add(new ActionItem(new ExportAsMidiAction()));
		exportSubMenu.add(new ActionItem(new ExportAsImageAction()));
		if (GlobalPreferences.getPerformancesFeatureEnabled()) {
			exportSubMenu.add(new ActionItem(new ExportAsHt3xAction()));
		}

		JPopupMenu helpSubMenu = new JPopupMenu();
		helpSubMenu.add(new ActionItem(new ShowHelpContentAction()));
		helpSubMenu.addSeparator();
		helpSubMenu.add(new ActionItem(new ShowAboutAction()));
		
		
		//
		// Cr�ation des boutons d'action principaux
		//
				
		m_newButton = new ActionButton(new NewScoreAction(), KeyEvent.VK_N);
		m_openButton = new ActionButton(new OpenScoreAction(), openSubMenu, KeyEvent.VK_O);
		m_closeButton = new ActionButton(new CloseScoreAction(), KeyEvent.VK_W);
		m_saveButton = new ActionButton(new SaveAction(), KeyEvent.VK_S);
		m_saveAsButton = new ActionButton(new SaveScoreAsAction());
		m_exportButton = new ActionButton(new ExportAsImageAction(), exportSubMenu);
		m_exportButton.setText(Localizer.get(i18n.MENU_EXPORT));
		m_recordButton = new ActionButton(new RecordPerformanceFromHt3Action());
		m_printButton = new ActionButton(new PrintScoreAction(), KeyEvent.VK_P);
		m_modelEditorButton = new ActionButton(	new ShowModelEditorAction());
		m_preferencesButton = new ActionButton(new ShowPreferencesAction());
		m_helpButton = new ActionButton( new ShowHelpContentAction(), helpSubMenu, KeyEvent.VK_F1);
				
		//
		// Ajout des composants � l'interface
		//
		
		setLayout(new VerticalLayout(0, VerticalLayout.BOTH, VerticalLayout.TOP));
		addSeparator();
		add(m_newButton);
		add(m_openButton);
		add(m_closeButton);
		add(m_saveButton);
		add(m_saveAsButton);
		addSeparator();
		add(m_printButton);
		if (GlobalPreferences.getPerformancesFeatureEnabled()) {
			add(m_exportButton);
			add(m_recordButton);
		}
		addSeparator();
		add(m_modelEditorButton);
		add(m_preferencesButton);
		addSeparator();
		add(m_helpButton);
		addSeparator();
		
		// Enregistrement des listeners
		m_scoreController.addScoreControllerListener(this);
		onControlledScoreChanged(m_scoreController, m_scoreController.getScore());
		
		// Affichage de composant
		setFloatable(false);
		setOpaque(false);
		setBorder(new EmptyBorder(5, 0, 10, 0));
		
	}
	
	
	//
	// R�action aux �v�nements
	//
	
	/**
	 * Modification de l'�l�ment s�lectionn�
	 */
	private class ScoreChangesObserver implements HarmoTabObjectListener {
		public ScoreChangesObserver() {
		}
		
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						m_saveButton.setEnabled(m_scoreController.hasScoreChanged());
					}
				}
			);
		}
		
	}
	
	
	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		Score score = m_scoreController.getScore();
		if (score != null) {
			boolean scoreChanged = m_scoreController.hasScoreChanged();
			m_closeButton.setEnabled(true);
			m_saveButton.setEnabled(scoreChanged);
			m_saveAsButton.setEnabled(true);
			m_printButton.setEnabled(true);
			m_exportButton.setEnabled(true);
			m_recordButton.setEnabled(m_scoreController.getPerformancesList() == null);
			score.addObjectListener(new ScoreChangesObserver());
		}
		else {
			m_closeButton.setEnabled(false);
			m_saveButton.setEnabled(false);
			m_saveAsButton.setEnabled(false);
			m_printButton.setEnabled(false);
			m_exportButton.setEnabled(false);
			m_recordButton.setEnabled(false);
		}
	}
	
	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
	}
	
	
	//
	// Classe de bouton
	//
	
	/**
	 * Bouton d'action principal.
	 * Est compos� d'une image et d'un texte et peut comporter un sous-menu.
	 */
	private class ActionButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;

		public ActionButton(UserAction action) {
			this(action, null);
		}
		
		public ActionButton(UserAction action, int accelerator) {
			this(action, null);
			setAccelerator(accelerator);
		}
		
		public ActionButton(UserAction action, JPopupMenu subMenu, int accelerator) {
			this(action, subMenu);
			setAccelerator(accelerator);
		}
		
		public ActionButton(UserAction action, JPopupMenu subMenu) {
			super(action.getLabel(), action.getIcon());
			
			m_action = action;
			m_subMenu = subMenu;			
			setLayout(new BorderLayout());
			setFloatable(false);
			setOpaque(false);
			setBorder(new EmptyBorder(0, 0, 0, 0));
			setFocusable(false);
			
			// Bouton d'action principal
			setOpaque(false);
			setBorder(new EmptyBorder(5, 5, 5, 5));
			setHorizontalAlignment(SwingConstants.LEADING);
			addActionListener(action);
						
			// Bouton affichant le sous menu
			m_subMenuButton = null;
			if (subMenu != null) {
				m_subMenuButton = new JButton(ActionIcon.getIcon(ActionIcon.DROP_DOWN));
				m_subMenuButton.setOpaque(false);
				m_subMenuButton.setBorder(new EmptyBorder(5, 10, 5, 10));
				m_subMenuButton.setHorizontalAlignment(SwingConstants.TRAILING);
				m_subMenuButton.addActionListener(this);
				add(m_subMenuButton, BorderLayout.EAST);
			}
		}
		
		public void showSubMenu() {
			if (m_subMenu != null) {
				m_subMenu.show(m_subMenuButton, 0, m_subMenuButton.getHeight());
			}
		}
		
		public void setAccelerator(int accelerator) {
			KeyStroke keyBtn = KeyStroke.getKeyStroke(accelerator, InputEvent.CTRL_DOWN_MASK);
			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyBtn, "ctrl" + accelerator);
			getActionMap().put("ctrl" + accelerator, m_action);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			showSubMenu();
		}
				
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			if (m_subMenuButton != null) {
				m_subMenuButton.setEnabled(enabled);
			}
		}
		
		private JButton m_subMenuButton;
		private JPopupMenu m_subMenu;
		private Action m_action;
		
	}
	
	
	/**
	 * Item d'un sous-menu d'action.
	 * Est compos� d'une image et d'un texte
	 */
	private class ActionItem extends JMenuItem {
		private static final long serialVersionUID = 1L;
		
		public ActionItem(UserAction action) {
			super(action.getLabel(), action.getIcon());
			addActionListener(action);
			setBorder(new EmptyBorder(5, 0, 5, 5));
		}

	}
	
	
	/**
	 * Sous-menu sp�cifique pour le menu "Ouverture" qui se rempli avec la liste
	 * des fichiers r�cemment ouverts lors de son affichage
	 */
	private class RecentFilesPopulatedPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public RecentFilesPopulatedPopupMenu() {
			m_items = new ArrayList<JMenuItem>();
		}
		
		@Override
		public void show(Component invoker, int x, int y) {
			// Ajoute les entr�es pour les ouvertures de fichiers r�cents
			for (String filepath : RecentFilesManager.getInstance().getRecentFiles()) {
				File file = new File(filepath);
				JMenuItem item = new ActionItem(new OpenScoreAction(file.getAbsolutePath()));
				item.setText(m_items.size()+1 + ". " + file.getName());
				item.setToolTipText(file.getAbsolutePath());
				item.setIcon(null);
				m_items.add(item);
			}
			
			// Affichage du menu
			for (JMenuItem item : m_items) {
				add(item);
			}
			validate();
			super.show(invoker, x, y);
			
		}
		
		@Override
		protected void firePopupMenuWillBecomeInvisible() {
			// Supprime les entr�es correspondant aux fichiers r�cents
			for (JMenuItem item : m_items) {
				remove(item);
			}
			m_items.clear();
			// Appel de la m�thode parente
			super.firePopupMenuWillBecomeInvisible();
		}
		
		private ArrayList<JMenuItem> m_items = null;
	}
	
	
	//
	// Attributs
	//
	
	private ScoreController m_scoreController = null;
	
	private ActionButton m_newButton = null;
	private ActionButton m_openButton = null;
	private ActionButton m_closeButton = null;
	private ActionButton m_saveButton = null;
	private ActionButton m_saveAsButton = null;
	private ActionButton m_printButton = null;
	private ActionButton m_exportButton = null;
	private ActionButton m_recordButton = null;
	private ActionButton m_modelEditorButton = null;
	private ActionButton m_preferencesButton = null;
	private ActionButton m_helpButton = null;

}

