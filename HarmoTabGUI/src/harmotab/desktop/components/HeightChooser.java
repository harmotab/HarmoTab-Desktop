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
import javax.swing.*;
import javax.swing.event.*;

import harmotab.core.*;
import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.desktop.tools.*;
import harmotab.track.*;


public class HeightChooser extends JPanel {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public HeightChooser(Height height) {
		m_height = new Height(height);

		// Construction des composants
		Track track = new StaffTrack(new Score());
		track.add(new Bar(new Key(), new KeySignature(), new TimeSignature(), new RepeatAttribute()));
		track.add(new Note(m_height));
		m_trackPane = new TrackPane(track, new CustomElementRenderer());
		
		m_upButton = new ToolButton(Localizer.get(i18n.TT_MOVE_UP), ToolIcon.UP);
		m_downButton = new ToolButton(Localizer.get(i18n.TT_MOVE_DOWN), ToolIcon.DOWN);
		m_alterationChooser = new AlterationChooser(m_height.getAlteration());
		
		// Ajout des composants � l'interface
		Box buttonsBox = new Box(BoxLayout.Y_AXIS);
		buttonsBox.add(m_upButton);
		buttonsBox.add(m_downButton);
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.setOpaque(false);
		controlPanel.add(buttonsBox, BorderLayout.CENTER);
		controlPanel.add(m_alterationChooser, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout(10, 10));
		add(m_trackPane, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.EAST);
		
		// Enregistrement des listeners
		UserActionObserver listener = new UserActionObserver();
		m_alterationChooser.addActionListener(listener);
		m_upButton.addActionListener(listener);
		m_downButton.addActionListener(listener);
		m_height.addObjectListener(new HeightChangesObserver());
		
		// Affichage du composant
		setOpaque(false);
		setPreferredSize(new Dimension(190, 110));
		
	}
	
	
	//
	// Getters / setters
	//
	
	public Height getSelectedHeight() {
		return new Height(m_height);
	}
	
	
	//
	// Classes sp�cifiques pour l'affichage de la piste
	//
	
	/**
	 * ElementRenderer n'affichant pas les signatures ni les EmptyArea.
	 */
	class CustomElementRenderer extends AwtPrintingElementRendererBundle {
		@Override protected void paintTimeSignature(Graphics2D g, TimeSignature ts, LocationItem l) {}
	}

	
	//
	// Gestion des �v�nements
	//
	
	private class HeightChangesObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			fireHeightChanged();
		}
	}
	
	private class UserActionObserver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == m_upButton) {
				m_height.moveUp();
			}
			else if (event.getSource() == m_downButton) {
				m_height.moveDown();
			}
			else if (event.getSource() == m_alterationChooser) {
				m_height.setAlteration(m_alterationChooser.getSelectedAlteration());
			}
		}
	}
	
	
	public void addChangeListener(ChangeListener listener) {
		m_listeners.add(ChangeListener.class, listener);
	}
	
	public void removeChangeListener(ChangeListener listener) {
		m_listeners.remove(ChangeListener.class, listener);
	}
	
	
	protected void fireHeightChanged() {
		for (ChangeListener listener : m_listeners.getListeners(ChangeListener.class))
			listener.stateChanged(new ChangeEvent(this));
	}
	
	
	//
	// Attributs
	//
	
	private TrackPane  m_trackPane = null;
	private JButton m_upButton = null;
	private JButton m_downButton = null;
	private AlterationChooser m_alterationChooser = null;
	private Height m_height = null;

	private EventListenerList m_listeners = new EventListenerList();
	
}

