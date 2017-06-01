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

import harmotab.core.*;
import harmotab.desktop.actions.OpenModelAction;
import harmotab.desktop.actions.OpenScoreAction;
import harmotab.desktop.actions.UserAction;
import harmotab.desktop.browser.*;
import harmotab.io.harmonica.HarmonicaModelReader;
import harmotab.io.score.ScoreIOUtilities;
import harmotab.io.score.ScoreWriter;
import harmotab.sound.ScorePlayer;
import harmotab.throwables.ScoreIoException;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Fen�tre principale et point d'entr�e du logiciel.
 * La fen�tre est compos�e du menu, de la zone d'd'�dition de la partition et
 * du panneau � gauche. 
 */
public class Gui extends JFrame implements WindowListener, ComponentListener, ScoreControllerListener {
	private static final long serialVersionUID = 1;


	/**
	 * Display the HarmoTab Desktop GUI
	 */
	Gui() {
		
		// Configuration de la fen�tre principale
		DesktopController.getInstance().setGuiWindow(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(Localizer.get(i18n.MAIN_FRAME_TITLE) + " - HarmoTab");
		setIconImage(GuiIcon.getIcon(GuiIcon.HARMOTAB_ICON_48).getImage());
		setSize(GlobalPreferences.getWindowWidth(), GlobalPreferences.getWindowHeight());
		setExtendedState(GlobalPreferences.getWindowMaximized() ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
		
		// Cr�ation des composants de l'interface
		m_scoreController = new ScoreController();
		DesktopController.getInstance().setScoreController(m_scoreController);
		m_mainPane = new MainPane(m_scoreController);
		m_browsersPane = new BrowsersPane(m_scoreController);
		DesktopController.getInstance().setBrowsersPane(m_browsersPane);
		
		m_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_browsersPane, m_mainPane);
		m_splitPane.setOneTouchExpandable(true);
		m_splitPane.setDividerLocation(240);
		
		// Ajout des composants
		setJMenuBar(new HarmoTabMenu());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(m_splitPane, BorderLayout.CENTER);
				
		// Enregistrement des listeners 
		addWindowListener(this);
		GlobalPreferences.addChangeListener(new PreferencesObserver());
		addComponentListener(this);
		
		// Affichage de la fen�tre
		setNavigationPanelVisible(true);
		setLocationRelativeTo(null);
		setVisible(true);
		
		// Positionnement du s�parateur
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					m_splitPane.setDividerLocation(240);
				}
			}
		);
	}
	
	
	//
	// Getters / setters
	//
	
	/**
	 * Indique si le panneau de navigation/actions � gauche de l'interface est
	 * visible ou masqu�e.
	 */
	public boolean isBrowsersPaneVisible() {
		return m_isBrowsersPaneVisible;
	}
	
	
	//
	// Modification du titre de la fen�tre en fonction du fichier
	//
	
	private class FrameTitleSetter implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			String propertyChanged = event.getProperty();
			if (propertyChanged.equals(ScoreController.NEW_SCORE_EVENT) ||
					propertyChanged.equals(ScoreController.SCORE_OPENNED_EVENT) ||
					propertyChanged.equals(ScoreController.SCORE_SAVED_EVENT)) {
				// Modification du nom de la fen�tre en fonction du fichier dans
				// lequel est enregistr� la partition courante
				ScoreWriter writer = m_scoreController.getCurrentScoreWriter();
				if (writer != null) {
					setTitle(writer.getFile().getName() + " - HarmoTab");
				}
				else {
					setTitle(Localizer.get(i18n.MAIN_FRAME_TITLE) + " - HarmoTab");
				}
			}
		}
	}
	
	
	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		if (scoreControlled != null) {
			scoreControlled.addObjectListener(new FrameTitleSetter());
		}
	}

	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
	}
	
	
	//
	// Modification de l'apparence de la fen�tre
	//
	
	public void setNavigationPanelVisible(boolean visible) {
		m_isBrowsersPaneVisible = visible;
		if (visible) {
			m_splitPane.setLeftComponent(m_browsersPane);
			if (m_splitPane.getDividerSize() < 7) {
				m_splitPane.setDividerSize(7);
			}
		}
		else {
			m_splitPane.setLeftComponent(null);
			m_splitPane.setDividerSize(0);		
		}
	}
	
	
	//
	// Gestion des �v�nements de la fen�tre
	//

	@Override
	public void windowClosing(WindowEvent event) {
		if (m_scoreController.close())
			System.exit(0);
	}

	@Override public void windowActivated(WindowEvent event) {}
	@Override public void windowClosed(WindowEvent event) {}
	@Override public void windowDeactivated(WindowEvent event) {}
	@Override public void windowDeiconified(WindowEvent event) {}
	@Override public void windowIconified(WindowEvent event) {}
	@Override public void windowOpened(WindowEvent event) {}
	
	
	@Override
	public void componentResized(ComponentEvent event) {
		if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
			GlobalPreferences.setWindowMaximized(true);
		}
		else {
			GlobalPreferences.setWindowSize(getWidth(), getHeight());
			GlobalPreferences.setWindowMaximized(false);
		}
	}	

	@Override public void componentHidden(ComponentEvent event) {}
	@Override public void componentMoved(ComponentEvent event) {}
	@Override public void componentShown(ComponentEvent event) {}

	
	private class PreferencesObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			setLookAndFeel();
		}
	}
	
	private static void setLookAndFeel() {
		try {
			if (GlobalPreferences.useSystemAppearance() == true)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {}
	    catch (ClassNotFoundException e) {} 
	    catch (InstantiationException e) {}
	    catch (IllegalAccessException e) {}
	}	
	
	
	/**
	 * Point d'entr�e du logiciel.
	 * @param args
	 */
	public static void main(String[] args) {
		boolean displayUsage = false;
		
		// Aucun argument, affichage de la fen�tre principale
		if (args.length == 0) {
			displayGUI(null);
		}
		// Commande en argument, execution de la commande
		else if (args.length > 1 && args[0].startsWith("--")) {
			ErrorMessenger.setConsoleMode();
			String command = args[0];
			ScoreController scoreController = new ScoreController();
			
			// Enregistrement de la partition en entr�e dans le fichier ht3 de sortie
			if (command.equals("--ht3-output")) {
				try {
					scoreController.open(args[2]);
				} catch (ScoreIoException e) {
					e.printStackTrace();
				}
				if (! scoreController.saveScoreAs(args[1]))
					ErrorMessenger.showErrorMessage("Cannot create output file.");
			}
			// Export de la partition pour lecteur mobile
			else if (command.equals("--ht3x-output")) {
			    try {
					scoreController.open(args[2]);
			    	scoreController.exportAsExportedScore(args[1]);
				} catch (IOException e) {
					ErrorMessenger.showErrorMessage("Cannot create output file.");
				}
			}
			// Affichage des propri�t�s de la partition
			else if (command.equals("--score-properties")) {
				try {
					scoreController.open(args[1]);
					Score score = scoreController.getScore();
					System.out.println(
						score.getSongwriterString() + "\n" +
						score.getTitleString() 		+ "\n" +
						score.getTempo().getValue() + "\n" +
						score.getCommentString() 	+ "\n" +
						score.getDescription() 		+ "\n"
						);
				} catch (IOException e) {
					ErrorMessenger.showErrorMessage("Cannot create output file.");
				}
			}
			// Arguments invalides, affichage de l'"usage"
			else {
				displayUsage = true;
			}
		}
		// Un seul argument, ouverture en prenant le nom de l'argument comme 
		// nom de fichier � ouvrir
		else {
			displayGUI(args[0]);
		}
		
		// Affichage de l'usage si demand�
		if (displayUsage == true) {
			System.err.println("Usage :\n" +
				"   ./HarmoTab [<score-file>|<model-file>]\n" +
				"   ./HarmoTab --ht3-output <output-file> <input-file>\n" +
				"   ./HarmoTab --ht3x-output <output-file> <input-file>\n" +
				"   ./HarmoTab --score-properties <input-file>\n");
		}
		
	}
	
	
	/**
	 * Affichage de la fen�tre principale et ouverture de la partiition en 
	 * param�tre si il est non nul.
	 */
	private static void displayGUI(String path) {
		// Cr�ation de l'interface
		setLookAndFeel();
		Gui gui = new Gui();
		// Ouverture du fichier en entr�e s'il existe
		if (path != null) {
			path = path.trim();
			if (!path.equals("")) {
				// Ouverture d'une partition
				if (new ScoreIOUtilities.ReadableScoreFileFilter().accept(new File(path))) {
					UserAction action = new OpenScoreAction(path);
					action.actionPerformed(new ActionEvent(gui, 0, ""));
				}
				// Ouverture d'un mod�le d'harmonica
				else if (new HarmonicaModelReader.ReadableModelFileFilter().accept(new File(path))) {
					UserAction action = new OpenModelAction(path);
					action.actionPerformed(new ActionEvent(gui, 0, ""));
				}
				// Type de fichier non reconnu
				else {
					ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_UNKNOWN_FILE_TYPE_ERROR));
				}
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	private ScoreController m_scoreController = null;
	
	private JSplitPane m_splitPane = null;
	private MainPane m_mainPane = null;
	private BrowsersPane m_browsersPane = null;
	private boolean m_isBrowsersPaneVisible = true;

}
