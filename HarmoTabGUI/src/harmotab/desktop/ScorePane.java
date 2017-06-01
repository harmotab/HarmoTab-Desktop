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
import java.util.*;
import javax.swing.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.renderer.*;
import harmotab.renderer.renderingelements.*;
import harmotab.sound.*;
import harmotab.desktop.tools.*;
import harmotab.track.*;
import harmotab.element.*;


/**
 * Panneau d'affichage d'une partition
 */
public class ScorePane extends JPanel 
	implements 
		ScoreViewListener, 
		ComponentListener, 
		MouseListener, 
		MouseMotionListener,
		MouseWheelListener,
		ScorePlayerListener,
		ScoreControllerListener,
		KeyListener {
	
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeur / destructeur
	//
	
	public ScorePane(ScoreController controller) {
		// Affectation des attributs
		m_player = null;
		m_desktopController = DesktopController.getInstance();
		m_scoreView = new ImageScoreView(controller);
		DesktopController.getInstance().setScoreView(m_scoreView);
		setSoundPlayer(controller.getScorePlayer());
		setScore(controller.getScore());
		
		// Réglage des attributs de l'interface
		setLayout(new SmoothLayout());
		setFocusable(true);
		
		// Enregistrement des listener des composants graphiques
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		
		// Enregistrement des listener de partition
		m_scoreView.addScoreViewListener(this);
		controller.addScoreControllerListener(this);
		
		// Actions d'ouverture
		m_scoreView.refresh();
	}

	
	//
	// Getters
	//
	
	protected void setScore(Score score) {
		m_score = score;
		m_locations = new LocationList();
		unselect();
		
		if (m_score != null) {
			m_score.addObjectListener(new ScoreChangesObserver());
			m_allowEdition = m_scoreView.getScoreController().isScoreEditable();
		}
		else {
			m_allowEdition = false;
			setSelected((LocationItem) null, false);
			m_scoreView.refresh();
		}
		
		updateRenderingMode();
		m_scoreView.refresh();
	}
	
	public Score getScore() {
		return m_score;
	}

	public ScoreView getScoreView() {
		return m_scoreView;
	}
	
	/**
	 * Affecte le SoundPlayer qui est utilisé pour lire la partition
	 */
	protected void setSoundPlayer(ScorePlayer player) {
		if (m_player != null) {
			m_player.removeSoundPlayerListener(this);
		}
		m_player = player;
		m_player.addSoundPlayerListener(this);
	}
	
	/**
	 * Retourne le SoundPlayer en cours d'utilisation
	 */
	public ScorePlayer getSoundPlayer() {
		return m_player;
	}
		
	
	//
	// Ecoute des composants graphiques
	//
	
	/**
	 * Gestion de la fenêtre
	 */
	@Override 
	public void componentResized(ComponentEvent e) {
		m_scoreView.setViewSize(getWidth(), getHeight());
		m_scoreView.refresh();
	}
	
	
	@Override public void componentHidden(ComponentEvent event) {}
	@Override public void componentMoved(ComponentEvent event) {}
	@Override public void componentShown(ComponentEvent event) {}
	
	
	/**
	 * Gestion de la souris
	 */
	
	@Override
	public void mouseMoved(MouseEvent event) {
		if (updateOverItem()) {
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		LocationItem selected = m_locations.at(event.getX(), event.getY());
		setSelected(selected, true);
		if (event.isPopupTrigger()) {
			showPopupMenu(selected, event.getPoint());
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		m_scoreView.translateView(event.getWheelRotation()* ScoreScrollBar.INCREMENT_VALUE);
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		if (event.isPopupTrigger()) {
			LocationItem selected = m_locations.at(event.getX(), event.getY());
			showPopupMenu(selected, event.getPoint());
		}
	}
	
	
	@Override public void mouseClicked(MouseEvent event) {}
	@Override public void mouseEntered(MouseEvent event) {}	
	@Override public void mouseExited(MouseEvent event) {}
	@Override public void mouseDragged(MouseEvent event) {}
	
	
	/**
	 * Gestion du clavier
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		if (m_selection != null) {
			
			switch (event.getKeyChar()) {
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_ENTER:
					unselect();
					return;

				default:
					if (m_allowEdition) {
						m_selection.getTool().keyTyped(event);
					}
					break;
			}
			
			switch (event.getKeyCode()) {
				// Navigation parmis les �l�ments
				case KeyEvent.VK_RIGHT: 
					selectNext();
					break;
				case KeyEvent.VK_LEFT:
					selectPrevious();
					break;
				// Suppression d'un �l�ment
				case KeyEvent.VK_DELETE:
				case KeyEvent.VK_BACK_SPACE:
					if (	m_selection != null && 
							m_selection.getElement() instanceof TrackElement &&
							!(m_selection.getLocationItem().getFlag(LocationItemFlag.TEMPORARY_ELEMENT))) {
						m_selection.getTrack().remove(m_selection.getElement());
					}
					break;
			}
		}

	}
		
	@Override public void keyReleased(KeyEvent event) {}
	@Override public void keyTyped(KeyEvent event) {}
	
	
	/**
	 * Modification de la vue
	 */
	@Override
	public void onScoreViewChanged(ScoreView scoreView) {
		updateRenderingMode();
		
		m_locations = m_scoreView.getLocations();
		// La liste des location items à été mise à jour : affecte la sélection
		// au LocationItem correspondant à l'élément sélectionné
		if (m_selection != null) {
			int prevSelectionIndex = m_selection.getElementIndex();
			// Si ce n'est pas un élément temporaire et que l'élément fait partie d'une piste
			// recherche le LocationItem en fonction de l'indice de l'élément
			if (m_selection.getLocationItem().getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == false && prevSelectionIndex != -1) {
				if (prevSelectionIndex > m_selection.getTrack().size() - 1)
					prevSelectionIndex--;
				if (prevSelectionIndex >= 0) {
					Element correspondingElement = m_selection.getTrack().get(prevSelectionIndex);
					setSelected((LocationItem) m_locations.get(correspondingElement), false);
				}
			}
			// Si c'est un élément temporaire ou un élément du header, recheche 
			// le LocationItem en fonction de sa position
			else {
				int selIndex = m_selection.getLocationItemIndex();
				setSelected(
					selIndex != -1 && selIndex < m_locations.getSize() ? 
					m_locations.get(selIndex) : null, false);
			}
		}
		
		// Met à jour la position du controller
		if (m_selection != null)
			m_selection.getTool().updateLocation();
		// Met à jour la position de la note jouée
		if (m_played != null)
			m_played = m_locations.get(m_played.getElement());
		
		repaint();
	}
	
	
	/**
	 * Dessin du composant
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		// Affichage du fond
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		// Affichage du fond de l'élément sélectionné
		if (m_selection != null) {
			LocationItem selected = m_selection.getLocationItem();
			// Affichage du fond de la piste
			g2d.setColor(STAFF_COLOR);
			g2d.fillRect(0, selected.getY1(), getWidth(), selected.getHeight());
			// Affichage du fond de l'élément
			Color color = ELEMENT_COLOR;
			if (selected.getFlag(LocationItemFlag.TEMPORARY_ELEMENT))
				color = TEMPORARY_ELEMENT_COLOR;
			if (m_allowEdition == false)
				color = NO_ACTION_ELEMENT_COLOR;
			g2d.setColor(color);
			g2d.fillRect(selected.getX1(), selected.getY1(), selected.getWidth(), selected.getHeight());
		}
		
		// Affichage du survol de la souris ou de la position de lecture
		updateOverItem();
		if (m_played != null) {
			g2d.setColor(PLAYED_ELEMENT_COLOR);
			g2d.fillRect(m_played.getX1(), m_played.getY1(), m_played.getWidth(), m_played.getHeight());
		}
		if (m_mouseOver != null && !m_player.isPlaying()) {
			if (m_selection == null || m_mouseOver != m_selection.getLocationItem()) {
				g2d.setColor(OVER_COLOR);
				g2d.fillRect(m_mouseOver.getX1(), m_mouseOver.getY1(), m_mouseOver.getWidth(), m_mouseOver.getHeight());
			}
		}
		
		// Affichage de la partition
		g2d.drawImage(m_scoreView.getImage(), 0, 0, this);
		
	}
	
	
	/**
	 * Réactions à la lecture
	 */
	
	@Override
	public void onScorePlayerStateChanged(ScorePlayerEvent event) {
	}
	
	@Override
	public void onScorePlayerError(ScorePlayerEvent event, Throwable error) {
		ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_SOUND_OUTPUT_OPEN_ERROR));
		error.printStackTrace();
	}
	
	@Override
	public void onPlayedSoundItemChanged(ScorePlayerEvent event) {
		SoundItem item = event.getPlayedItem();
		// item peut être nul si aucun son en cours de lecture sur la piste
		if (item != null) {
			m_played = m_locations.get(event.getPlayedItem().getElement());
			goTo(m_played, true);
		}
		else {
			m_played = null;
		}
		repaint();
	}
	
	@Override
	public void onPlaybackStarted(ScorePlayerEvent event) {
		m_played = null;
		setSelected((LocationItem) null, true);
		updateRenderingMode();
		m_scoreView.updateScoreView();
		repaint();
	}
	
	@Override
	public void onPlaybackPaused(ScorePlayerEvent event) {
		repaint();
	}
	
	@Override
	public void onPlaybackStopped(ScorePlayerEvent event, boolean endOfPlayback) {
		m_played = null;
		updateRenderingMode();
		m_scoreView.updateScoreView();
		repaint();
	}
	
	
	private void updateRenderingMode() {
		ScoreRenderer renderer = m_scoreView.getScoreRenderer();
		if (renderer != null) {
			boolean onlyView = !m_allowEdition || m_player.isPlaying();
			renderer.getElementRenderer().setMode(onlyView ? RenderingMode.VIEW_MODE : RenderingMode.EDIT_MODE);
		}
	}
	
	
	//
	// Gestion de la sélection
	//
	
	private void unselect() {
		// Ferme le controlleur de la sélection précédente s'il existe
		if (m_selection != null) {
			m_selection.getTool().setVisible(false);
			m_selection.getLocationItem().m_isSelection = false;
		}
		m_selection = null;
	}
		
	/**
	 * Modification de la selection
	 */
	public void setSelected(LocationItem selected, boolean scrollOnSelection) {
		// Ferme le controlleur de la sélection précédente s'il existe
		unselect();
		
		// Ne fait rien si la lecture est en cours
		if (m_player.isPlaying())
			return;
		
		// Affichage de l'outil pour l'item sélectionné s'il existe
		if (selected != null) {
			// Si c'est un EmptyArea, affiche le menu d'ajout d'�l�ments
			if (selected.getElement() instanceof EmptyArea) {
				if (m_allowEdition) {
					JPopupMenu menu = new JPopupMenu();
					Track track = m_score.getTrack(selected.getTrackId());
					menu.add(AddElementMenu.createInsertLast(track));
					Point p = getMousePosition();
					if (p != null) {
						menu.show(this, p.x, p.y);
					}
				}
			}
			// Pour tous les autres �l�ments cr�er un �l�ment de s�lection et affiche le controlleur
			else {
				Tool controller = ToolFactory.createController(this, m_score, selected);
				if (controller != null) {
					m_selection = new ScoreViewSelection(controller, m_locations.getItemIndex(selected));
					selected.m_isSelection = true;
					// Affiche l'outil si la partition est éditable
					controller.setVisible(m_allowEdition);
					if (scrollOnSelection) {
						goTo(selected);
					}
				}
			}
		}
		
		// Indique au player la piste qu'il doit suivre et l'�l�ment s�lectionn�
		if (m_selection != null) {
			m_player.setHighlightedTrack(m_selection.getTrackId());
			m_player.setPlayFromElement(m_selection.getElement());
		}
		else {
			m_player.setHighlightedTrack(ScorePlayer.NO_TRACK_HIGHLIGHTED);
			m_player.setPlayFromElement(null);
		}
		
		// Transmet l'�v�nement de changement de s�lection
		m_desktopController.fireSelectionChanged(m_selection);
		
		// Rafraîchissement de la fenêtre
		requestFocus();
		repaint();
		
	}
	
	
	public void setSelected(Element element, boolean scrollOnSelection) {
		if (element == null) {
			setSelected((LocationItem) null, scrollOnSelection);
		}
		else {
			setSelected(m_locations.get(element), scrollOnSelection);
		}
	}
	
	/**
	 * Sélection de l'élément suivant
	 */
	public void selectNext() {
		// Si la sélection courante n'est pas un élément temporaire, sélectionne 
		// l'élement non temporaire suivant
		if (m_selection.getLocationItem().getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == false) {
			Track track = m_selection.getTrack();
			if (track != null) {
				ListIterator<Element> iterator = track.listIterator(m_selection.getLocationItem());
				// S�lectionne l'�l�ment suivant s'il existe ...
				if (iterator != null) { 
					iterator.next();
					if (iterator.hasNext()) {
						setSelected(iterator.next(), true);
					}
					// ou cr�er un nouvel �l�ment si c'est le dernier de la piste
					else {
						if (m_allowEdition == true) {
							Element newElement = (Element) m_selection.getLocationItem().getRootElement().clone();
							UndoManager.getInstance().addUndoCommand(track.createRestoreCommand(), Localizer.get(i18n.ACT_ADD_ELEMENT));
							track.add(newElement);
							//Appel récursif permettant de sélectionner l'élément nouvellement créé
							selectNext();
						}
					}
				}
				return;
			}
		}
		// Si la sélection est un élément temporaire ou un élément du titre
		int selectionIndex = m_selection.getLocationItemIndex();
		if (selectionIndex != -1 && selectionIndex < m_locations.getSize()-1)
			setSelected(m_locations.get(selectionIndex+1), true);
	}
	
	/**
	 * Sélection de l'élément précedent
	 */
	public void selectPrevious() {
		// Si la sélection courante n'est pas un élément temporaire, sélectionne 
		// l'élement non temporaire précédent
		if (m_selection.getLocationItem().getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == false) {
			Track track = m_selection.getTrack();
			if (track != null) {
				ListIterator<Element> iterator = track.listIterator(m_selection.getLocationItem());
				if (iterator != null) {
					if (iterator.hasPrevious()) {
						setSelected(iterator.previous(), true);
					} 
					return;
				}
			}
		}
		// Si la sélection est un élément temporaire ou un élément du titre
		int selectionIndex = m_selection.getLocationItemIndex();
		if (selectionIndex > 0)
			setSelected(m_locations.get(selectionIndex-1), true);
	}
	
	
	/**
	 * Déplace la vue de la partition (scroll) à l'élément indiqué si l'élément 
	 * n'est pas entièrement visible.
	 */
	public void goTo(LocationItem item) {
		goTo(item, false);
	}
	
	public void goTo(LocationItem item, boolean centered) {
		if (item == null)
			return;
		
		int lineStart = m_scoreView.getLineOffset(item.m_line);
		int lineEnd =  m_scoreView.getLineOffset(item.m_line + 1);
		
		// L'�l�ment � atteindre doit �tre affich� au milieu de l'�cran
		if (centered) {
			m_scoreView.translateView(lineStart);
		}
		// L'�l�ment � atteindre doit �tre affich� en premi�re ligne visible s'il
		// esy trop haut ou en derni�re ligne visible s'il est trop bas.
		else {
			// Si l'item s�lectionn� est top haut par rapport � la visualisation courante,
			// remonte la visualisation
			if (lineStart < 0) {
				m_scoreView.translateView(lineStart);
			}
			// Si l'item s�lectionn� est trop bas par rapport � la visualisation courante,
			// baisse la visualisation
			if (lineEnd > m_scoreView.getViewHeight()) {
				m_scoreView.translateView(lineEnd - m_scoreView.getViewHeight() + 35);
			}
		}
	}
	
	
	/**
	 * Met � jour l'item sur lequel est la souris
	 * @return Vrai si l'item survol� � chang�
	 */
	private boolean updateOverItem() {
		LocationItem previous = m_mouseOver; 
		Point mousePosition = getMousePosition();
		if (mousePosition != null) {
			m_mouseOver = m_locations.at(mousePosition.x, mousePosition.y);
			if (m_mouseOver != null && m_mouseOver.getFlag(LocationItemFlag.ERRORNOUS_ITEM) == true)
				setToolTipText(LocationItemFlag.getErrorFlagToolTip(m_mouseOver));
			else
				setToolTipText(null);
			updateCursor();
		}
		else {
			return false;
		}
		return (m_mouseOver != previous);
	}
	

	/**
	 * Retourne la sélection courante
	 */
	public ScoreViewSelection getScorePaneSelection() {
		return m_selection;
	}
	
	
	/**
	 * Gestion de l'affichage du menu contextuel
	 */
	public void showPopupMenu(LocationItem item, Point mousePosition) {
		if (m_allowEdition == false)
			return;
		if (item == null)
			return;
		if (item.getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == true)
			return;
		if (!(item.getElement() instanceof TrackElement))
			return;
		
		new ElementPopupMenu(this, mousePosition, m_score, item);
	}
	
	
	/**
	 * Affecte le curseur de la souris en fonction de l'�l�ment survol�
	 */
	private void updateCursor() {
		if (m_mouseOver != null) {
			setCursor(ELEMENT_CURSOR);
		}
		else {
			setCursor(DEFAULT_CURSOR);
		}

	}
	
	
	//
	// Gestion des modifications de la partition
	//
	
	private class ScoreChangesObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			// Si la liste d'�l�ments d'une piste a chang�e ...
			if (event.hierarchyContains(Track.ELEMENT_LIST_CHANGED_EVENT)) {
				// S�lectionne l'�l�ment ajout�
				HarmoTabObjectEvent elementEvent = event.getHierarchyEvent(Track.ELEMENT_LIST_CHANGED_EVENT).getParent();
				Element element = (Element) elementEvent.getSource();
				m_element = element;
				
				SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						setSelected(m_element, true);
					}
				});
			}
		}
		private Element m_element = null;
	}
	
	
	//
	// Modification de l'environnement
	//

	@Override
	public void onControlledScoreChanged(ScoreController controller, Score scoreControlled) {
		setScore(scoreControlled);
	}
	
	@Override
	public void onScorePlayerChanged(ScoreController controller, ScorePlayer soundPlayer) {
		setSoundPlayer(soundPlayer);
	}
	
	
	//
	// Attributs
	//
	
	private Score m_score = null;
	private ImageScoreView m_scoreView = null;
	private ScorePlayer m_player = null;
	private LocationList m_locations = null;
	private ScoreViewSelection m_selection = null;
	private LocationItem m_played = null;
	private LocationItem m_mouseOver = null;
	private DesktopController m_desktopController = null;
	private boolean m_allowEdition = true;
	
	private static Color ELEMENT_COLOR = new Color(0xFFFFC0);
	private static Color NO_ACTION_ELEMENT_COLOR = new Color(0x9EC3DF);
	private static Color STAFF_COLOR = new Color(0xF8F8F8);
	private static Color TEMPORARY_ELEMENT_COLOR = new Color(0xC0FFC0);
	private static Color OVER_COLOR = new Color(0xF0F0F0);
	private static Color PLAYED_ELEMENT_COLOR = new Color(0xBEE3FF);
	
	private static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private static Cursor ELEMENT_CURSOR = new Cursor(Cursor.HAND_CURSOR);

}
