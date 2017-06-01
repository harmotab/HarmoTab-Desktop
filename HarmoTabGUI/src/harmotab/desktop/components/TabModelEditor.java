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

package harmotab.desktop.components;

import java.awt.*;
import java.awt.event.*;
import java.util.ConcurrentModificationException;
import javax.swing.*;
import rvt.util.gui.VerticalLayout;
import harmotab.core.*;
import harmotab.desktop.tools.ToolIcon;
import harmotab.element.*;
import harmotab.harmonica.*;
import harmotab.renderer.*;
import harmotab.renderer.renderingelements.*;
import harmotab.track.*;


/**
 * Composant de modification d'un mod�le de tablature
 */
public class TabModelEditor extends JPanel {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public TabModelEditor(TabModel tabModel, boolean showChromaButton) {
		m_tabModel = tabModel;
		m_allowChromaButton = showChromaButton;
		m_modelChanged = false;

		// Cr�ation des composants
		Score voidScore = new Score();
		m_track = new HarmoTabTrack(voidScore);
		resetTrack();

		m_staffPane = new TrackPane(m_track, new CustomElementRenderer());
		
		// Ajout des composants � l'interface
		m_scrollPane = new JScrollPane(m_staffPane);
		setLayout(new BorderLayout());
		add(m_scrollPane, BorderLayout.CENTER);
		
		// Enregistrement des listeners
		m_staffPane.addActionListener(new HeightChoiceAction());
		m_tabModel.addObjectListener(new TabModelObserver());
		m_staffPane.setPopupTriggerAction(new TabPopupMenuAction());
		
		// Affichage
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setPreferredSize(new Dimension(150, 200));
		
	}
	
	
	private void resetTrack() {
		m_track.clear();
		m_track.add(new Bar(new Key(), new KeySignature(), new CustomTimeSignature(), new RepeatAttribute()));	
		for (int i = Height.MIN_VALUE; i < Height.MAX_VALUE; i++) {
			Height height = new Height(i);
			Tab tab = m_tabModel.getTab(height);
			if (tab == null)
				tab = new Tab();
			HarmoTabElement element = new HarmoTabElement(height, new Figure());
			m_track.add(element);
			element.setTab(tab);
		}
	}
	
	
	//
	// Getters / setters
	//
	
	/**
	 * Retourne le mod�le de tablatures
	 */
	public TabModel getTabModel() {
		return m_tabModel;
	}
	
	/**
	 * Indique si le mod�le de tablatures � eu des modifications.
	 */
	public boolean hasTabModelChanged() {
		return m_modelChanged;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	/**
	 * D�place l'affichage � la note corr�spondant � la hauteur en param�tre
	 */
	public void goTo(Height height) {
		LocationList locations = m_staffPane.getCurrentLocations();
		if (locations == null || locations.getSize() == 0) {
			System.err.println("TabModelEditor:goTo: No locations computed.");
			return;
		}
		try {
			for (LocationItem item : locations) {
				Element element = item.getElement();
				if (element instanceof HarmoTabElement) {
					HarmoTabElement htElement = (HarmoTabElement) element;
					if (htElement.getHeight().equals(height)) {
						m_staffPane.setSelectedItem(item);
						SwingUtilities.invokeLater(new ScrollLaterWoker(item.getX1() - 100));
					}
				}
			}
		}
		catch (ConcurrentModificationException e) {}
	}
		
	
	/**
	 * Effectue un scroll � la position d�sir�e de mani�re asynchrone
	 */
	private class ScrollLaterWoker implements Runnable {
		int m_scrollXRequested;
		
		public ScrollLaterWoker(int x) {
			m_scrollXRequested = x;
		}
		
		@Override
		public void run() {
			m_scrollPane.getHorizontalScrollBar().setValue(m_scrollXRequested);
		}
	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	/**
	 * Action de modification d'une tab
	 */
	private class HeightChoiceAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			Element selected = m_staffPane.getSelectedItem();
			if (selected != null && selected instanceof HarmoTabElement) {
				HarmoTabElement selectedHt = (HarmoTabElement) selected;
				TabChooser chooser = new TabChooser(selectedHt.getTab(), m_allowChromaButton);
				JPanel panel = new JPanel(new VerticalLayout(10, 10));
				panel.add(new JLabel("Veuillez choisir la tablature correspondante � la note s�lectionn�e :"));
				panel.add(chooser);
				
				int res = JOptionPane.showConfirmDialog(
						TabModelEditor.this, 
						panel,
						"Tab model editor",
						JOptionPane.OK_CANCEL_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					Tab tab = chooser.getTab();
					selectedHt.setTab(tab);
					m_tabModel.setTab(selectedHt.getHeight(), tab);
					m_modelChanged = true;
				}
			}
		}
	}
	
	
	/**
	 * Action de suppression d'une tab
	 */
	private class DeleteTabAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		public DeleteTabAction() {
			putValue(SMALL_ICON, ToolIcon.getIcon(ToolIcon.DELETE));
			putValue(NAME, Localizer.get(i18n.MENU_DELETE_TAB));
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Element selected = m_staffPane.getSelectedItem();
			if (selected != null && selected instanceof HarmoTabElement) {
				HarmoTabElement selectedHt = (HarmoTabElement) selected;
				selectedHt.setTab(new Tab());
				m_tabModel.deleteTab(selectedHt.getHeight());
				m_modelChanged = true;
			}
		}
	}
	
	
	/**
	 * Menu associ� � une tab
	 */
	private class TabPopupMenuAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void showPopupMenu(int x, int y) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new DeleteTabAction());
			menu.show(TabModelEditor.this, x, y);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			Point mousePosition = getMousePosition();
			if (mousePosition != null) {
				showPopupMenu((int) mousePosition.getX(), (int) mousePosition.getY());
			}
		}
	}
	
	
	/**
	 * R�action aux modifications du mod�le de tab
	 */
	private class TabModelObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			resetTrack();
		}
	}
	
	
	//
	// Classes sp�cifiques pour l'affichage de la piste
	//

	/**
	 * Signature temporelle pour ne pas que le layout de la partition n'ajoute de 
	 * barres de mesure.
	 */
	private class CustomTimeSignature extends TimeSignature {
		@Override
		public float getTimesPerBar() {
			return Float.MAX_VALUE;
		}
	}

	/**
	 * ElementRenderer n'affichant pas les signatures ni les EmptyArea.
	 */
	private class CustomElementRenderer extends AwtPrintingElementRendererBundle {
		@Override protected void paintTimeSignature(Graphics2D g, TimeSignature ts, LocationItem l) {}
		@Override protected void paintHarmonicaProperties(Graphics2D g, HarmonicaProperties harmoProps, LocationItem item) {}
	}
	
	
	//
	// Classes internes
	//
	
	
	
	
	//
	// Attributs
	//
	
	private Track m_track = null;
	private TabModel m_tabModel = null;
	private TrackPane m_staffPane = null;
	private JScrollPane m_scrollPane = null;
	private boolean m_allowChromaButton = false;
	private boolean m_modelChanged = false;

}

