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
import harmotab.desktop.DesktopController;
import harmotab.desktop.components.*;
import harmotab.desktop.setupdialog.ScoreSetupDialog;
import harmotab.element.*;
import harmotab.harmonica.HarmonicaType;
import harmotab.renderer.*;
import harmotab.throwables.*;
import harmotab.track.HarmoTabTrack;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.event.*;


/**
 * Boite d'outils de modification d'une tablature
 */
public class TabTool extends Tool {
	private static final long serialVersionUID = 1L;

	//
	// Gestion du controlleur
	//

	public TabTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_tab = (Tab) item.getElement();
		
		// N'affiche la case "tir�" que si c'est une tab d'harmonica chromatique
		boolean showPushChooser = 
			(((HarmoTabTrack) getTrack()).getHarmonica().getModel().getHarmonicaType() 
					== HarmonicaType.CHROMATIC);
		
		// Cr�ation des composants
		m_tabChooser = new TabChooser(m_tab, showPushChooser);
		m_effectChooser = new EffectChooser(m_tab.getEffect());
		m_modelSetupButton = new ToolButton(Localizer.get(i18n.N_TAB_NOTE_MAPPING), ToolIcon.TUNE);
		
		// Ajout des composants � la barre d'outils
		add(m_modelSetupButton);
		addSeparator();
		add(m_tabChooser);
		addSeparator();
		add(m_effectChooser);
		
		// Enregistrement des listeners
		UserActionObserver listener = new UserActionObserver();
		m_modelSetupButton.addActionListener(listener);
		m_tabChooser.addChangeListener(listener);
		m_effectChooser.addActionListener(listener);
		
	}

	
	//
	// Gestion des actions utilisateur
	//
	
	@Override
	public void keyTyped(KeyEvent event) {		
		switch (event.getKeyChar()) {
		
			// Modification du chiffre
			case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
				String value = event.getKeyChar() + "";
				int hole = Integer.parseInt((m_tab != null ? m_tab.getHole() + "" : "") + value);
				
				if (m_tab == null)
					m_tab = new Tab();

				try {
					UndoManager.getInstance().addUndoCommand(m_tab.createRestoreCommand(), Localizer.get(i18n.N_HOLE));
					m_tab.setHole(hole);
				}
				catch (OutOfBoundsError e) {
					m_tab.setHole(Tab.UNDEFINED);
				}
				break;
				
			// Modification de la direction
			case ' ':
				if (m_tab == null) {
					m_tab = new Tab(Tab.BLOW);
				}
				else {
					UndoManager.getInstance().addUndoCommand(m_tab.createRestoreCommand(), Localizer.get(i18n.N_BLOW));
					m_tab.toggleDirection();
				}
				break;
				
			// Modification de "tir�"
			case '/':
				if (m_tab != null) {
					UndoManager.getInstance().addUndoCommand(m_tab.createRestoreCommand(), Localizer.get(i18n.N_PUSHED));
					m_tab.setPushed(!m_tab.isPushed());
				}
				break;
		}
	}
	
	
	class UserActionObserver implements ChangeListener, ActionListener {
		
		@Override
		public void stateChanged(ChangeEvent event) {
			// Modification de la tablature
			if (event.getSource() == m_tabChooser) {
				UndoManager.getInstance().addUndoCommand(m_tab.createRestoreCommand(), Localizer.get(i18n.N_TAB));
				m_tab.set(m_tabChooser.getTab());
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// Action sur le bouton d'acc�s � la configuration du mod�le de tablature
			if (event.getSource() == m_modelSetupButton) {
				ScoreController controller = DesktopController.getInstance().getScoreController();
				UndoManager.getInstance().addUndoCommand(controller.getScore().createRestoreCommand(), Localizer.get(i18n.ET_SCORE_SETUP));
				ScoreSetupDialog dlg = new ScoreSetupDialog( null, controller);
				dlg.setSelectedTabMappingHeight(((HarmoTabElement) m_locationItem.getParent()).getHeight());
				dlg.setSelectedTab(ScoreSetupDialog.HARMONICA_PROPERTIES_TAB);
				dlg.setVisible(true);
			}
			// Modification de l'effet
			else if (event.getSource() == m_effectChooser) {
				UndoManager.getInstance().addUndoCommand(m_tab.createRestoreCommand(), Localizer.get(i18n.N_EFFECT));
				m_tab.setEffect(m_effectChooser.getEffect());
			}
		}
		
	}

	
	//
	// Attributs
	//
	
	private Tab m_tab = null;
	
	private JButton m_modelSetupButton = null;
	private TabChooser m_tabChooser = null;
	private EffectChooser m_effectChooser = null;
	
}
