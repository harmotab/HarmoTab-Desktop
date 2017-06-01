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

import harmotab.core.*;
import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.track.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Affichage d'une port�e.
 */
class TrackPane extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 2048;
	private static final int HEIGHT = 180;
	private static final int XOFFSET = 15;
	
	//
	// Constructeur
	//
	
	public TrackPane(Track track, ElementRendererBundle renderer) {
		m_locations = new LocationList();
		m_over = null;
		m_track = track;
		m_elementRenderer = renderer;
		m_popupTriggerAction = null;
		
		// Enregistrement des listeners
		MouseObserver mouseObserver = new MouseObserver();
		addMouseListener(mouseObserver);
		addMouseMotionListener(mouseObserver);
		m_track.addObjectListener(new TrackChangesObserver());
		
		// Affichage du composant
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		
		// Premier calcul du positionnement des �l�ments
		m_track.getTrackLayout().processElementsPositionning(m_locations, WIDTH, m_track.getDuration());
	}
	
	
	//
	// Getters / setters
	//
	
	public Track getTrack() {
		return m_track;
	}
	
	public Element getSelectedItem() {
		if (m_selected == null)
			return null;
		return m_selected.getElement();
	}
	
	public LocationList getCurrentLocations() {
		return m_locations;
	}
	
	
	public void setPopupTriggerAction(Action action) {
		m_popupTriggerAction = action;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	public void setSelectedItem(LocationItem item) {
		m_selected = item;
		repaint();
	}
	
	public void setSelectedElement(Element element) {
		m_selected = m_locations.get(m_requestedElementSelection);
		repaint();
	}
	
	
	//
	// Gestion de l'affichage
	//
	
	@Override
	public void paint(Graphics g) {
		// Affichage du fond
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		
		// Affichage de la s�lection
		if (m_selected != null) {
			g.setColor(new Color(0xFFFFC0));
			g.fillRect(m_selected.getX1()-XOFFSET, 0, m_selected.getWidth(), HEIGHT);
		}
		
		// Affichage de la piste
		m_locations.reset();
		m_track.getTrackLayout().processElementsPositionning(m_locations, WIDTH, m_track.getDuration());
		for (LocationItem item : m_locations) {
			m_elementRenderer.paintElement((Graphics2D) g, item);
		}

		// Affichage du survol de la souris
		if (m_over != null && m_over != m_selected) {
			g.setColor(Color.GRAY);
			g.drawRect(m_over.getX1()-XOFFSET, 0, m_over.getWidth(), HEIGHT);
		}
	}
	
	
	//
	// Gestion des actions utilisateur
	//
	
	class MouseObserver implements MouseListener, MouseMotionListener {
		
		public void updateSelection(int mouseX, int mouseY) {
			LocationItem selected = m_locations.at(mouseX + XOFFSET, mouseY);
			if (selected != null && selected.getElement() instanceof Note) {
				m_selected = selected;
			}
			else {
				m_selected = null;
			}
			repaint();
		}
		
		@Override
		public void mouseMoved(MouseEvent event) {
			LocationItem selected = m_locations.at(event.getX()+XOFFSET, event.getY());
			if (selected != null && selected.getElement() instanceof Note) {
				m_over = selected;
			}
			else {
				m_over = null;
			}
			repaint();
		}
		
		@Override 
		public void mouseEntered(MouseEvent event) {
			mouseMoved(event);
		}
		
		
		@Override
		public void mouseExited(MouseEvent event) {
			m_over = null;
			repaint();
		}
				
		@Override
		public void mousePressed(MouseEvent event) {
			if (event.isPopupTrigger() && m_popupTriggerAction != null) {
				updateSelection(event.getX(), event.getY());
				m_popupTriggerAction.actionPerformed(
					new ActionEvent(event.getSource(), event.getID(), ""));
				trigeringPopupOnPressed = true;
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent event) {
			updateSelection(event.getX(), event.getY());
			
			if (event.isPopupTrigger() && m_popupTriggerAction != null) {
				m_popupTriggerAction.actionPerformed(
					new ActionEvent(event.getSource(), event.getID(), ""));
			}
			else {
				if (!trigeringPopupOnPressed) {
					fireActionPerformed();
				}
			}
			trigeringPopupOnPressed = false;
		}
		
		@Override public void mouseClicked(MouseEvent event) {}
		@Override public void mouseDragged(MouseEvent event) {}
	
		// Permet de ne déclencher une action que si le clic en cours n'est pas 
		// le bouton de la souris permettant d'activer les menu contextuel.
		// Utilisé car bouton droit released sur windows et pressed sur linux.
		private boolean trigeringPopupOnPressed = false;
	}
	
	class TrackChangesObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			repaint();
		}
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	public void addActionListener(ActionListener listener) {
		m_listeners.add(ActionListener.class, listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		m_listeners.remove(ActionListener.class, listener);
	}
	
	
	private void fireActionPerformed() {
		for (ActionListener listener : m_listeners.getListeners(ActionListener.class))
			listener.actionPerformed(new ActionEvent(this, 0, ""));
	}
	
	
	//
	// Attributs
	//
	
	private final EventListenerList m_listeners = new EventListenerList();

	private Action m_popupTriggerAction = null;
	private Track m_track = null;
	private ElementRendererBundle m_elementRenderer = null;
	private LocationList m_locations = null;
	private LocationItem m_over = null;
	private LocationItem m_selected = null;
	private Element m_requestedElementSelection = null;

}

