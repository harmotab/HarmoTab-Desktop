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

import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.ElementIcon;
import harmotab.element.*;
import harmotab.throwables.NotImplementedError;
import harmotab.track.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * Menu contextuel d'ajout d'�l�ments � une piste.
 * Construit la liste d'élément ajoutable en fonction du type de piste.
 */
public class AddElementMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//

	private AddElementMenu(String label, Track track) {
		super(label);
		m_referenceElement = null;
		m_insertAfter = false;
		m_time = null;
		m_track = track;		
	}

	
	public static AddElementMenu createInsertBefore(Track track, Element beforeElement) {
		AddElementMenu menu = new AddElementMenu(Localizer.get(i18n.MENU_INSERT_BEFORE), track);
		menu.setIcon(ActionIcon.getIcon(ActionIcon.ADD_BEFORE_LITTLE));
		menu.setInsertBerfore(beforeElement);
		return menu;
	}

	public static AddElementMenu createInsertAfter(Track track, Element afterElement) {
		AddElementMenu menu = new AddElementMenu(Localizer.get(i18n.MENU_INSERT_AFTER), track);
		menu.setIcon(ActionIcon.getIcon(ActionIcon.ADD_AFTER_LITTLE));
		menu.setInsertAfter(afterElement);
		return menu;
	}
	
	public static AddElementMenu createInsertLast(Track track) {
		AddElementMenu menu = new AddElementMenu(Localizer.get(i18n.MENU_INSERT_LAST), track);
		menu.setIcon(ActionIcon.getIcon(ActionIcon.ADD_LAST_LITTLE));
		menu.setInsertLast();
		return menu;
	}
	
	
	public void setReference(Track track, Element element) {
		m_track = track;
		m_referenceElement = element;
	}


	private void insertAddMenuItems(TrackElement element) {
		// Création du menu
		JMenuItem menuItem = new JMenuItem(element.getTrackElementLocalizedName());
		menuItem.addActionListener(new AddElementAction(element));
		
		// Ajout de l'icone correspondante
		ImageIcon icon = ElementIcon.getIcon(element);
		if (icon != null)
			menuItem.setIcon(icon);
		
		// Ajout du menu
		add(menuItem);
	}
	
	
	//
	// Getters / setters
	//
	
	protected void setInsertAfter(Element element) {
		m_insertAfter = true;
		m_referenceElement = element;
		m_time = null;
	}
	
	protected void setInsertBerfore(Element element) {
		m_insertAfter = false;
		m_referenceElement = element;
		m_time = null;
	}
	
	protected void setInsertAt(Element element, float time) {
		m_insertAfter = false;
		m_referenceElement = element;
		m_time = time;
	}
	
	protected void setInsertLast() {
		m_referenceElement = null;
		m_time = null;
	}
	
	
	//
	// Peuplement du menu
	//
	
	public void populate() {
		// Ajoute les entr�es l'insertion des différents types d'éléments ajoutables
		removeAll();
		if (m_track != null) {
			for (TrackElement e : m_track.getAddableElements()) {
				insertAddMenuItems(e);
			}
		}
		validate();		
	}
	
	@Override
	protected void fireMenuSelected() {
		populate();
		super.fireMenuSelected();
	}
	
	//
	// Attributs
	//
	
	private Element m_referenceElement = null;
	private boolean m_insertAfter = false;
	private Float m_time = null;
	private Track m_track;
	
	
	//
	// Action correspondant aux diff�rents menus d'ajout
	//
	
	private class AddElementAction implements ActionListener {
		
		public AddElementAction(TrackElement element) {
			m_trackElement = element;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// Cr�ation du nouvel �l�ment
			TrackElement newElement = (TrackElement) m_trackElement.clone();
			
			// Insertion en fin de piste
			if (m_referenceElement == null) {
				UndoManager.getInstance().addUndoCommand(m_track.createRestoreCommand(), Localizer.get(i18n.ACT_ADD_ELEMENT));
				m_track.add(newElement);
			}
			else {
				// Insertion � un temps donn�
				if (m_time != null) {
					//m_track.add(m_time, newElement);
					throw new NotImplementedError( "Add to a specific time not implemented" );
				}
				// Insertion avant ou apr�s un �l�ment de la piste
				else {
					int index = m_track.indexOf(m_referenceElement);
					if (m_insertAfter == true)
						index += 1;
					UndoManager.getInstance().addUndoCommand(m_track.createRestoreCommand(), Localizer.get(i18n.ACT_INSERT_ELEMENT));
					m_track.add(index, newElement);
				}
			}
		}
		
		private TrackElement m_trackElement = null;
		
	}
	
}
