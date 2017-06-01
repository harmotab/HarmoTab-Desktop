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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import harmotab.renderer.*;
import harmotab.track.*;
import harmotab.core.*;


/**
 * Boite d'outils de modification d'un �l�ment affich� sur la partition
 */
public abstract class Tool extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private static final int BORDER_SIZE = 5;
	private static final int GAP_SIZE = 0;
	
	//
	// Constructeur
	//
	
	public Tool(Container container, Score score, LocationItem item) {
		m_locationItem = item;
		m_score = score;
		m_container = container;

		setLayout(new FlowLayout(FlowLayout.LEFT, GAP_SIZE, GAP_SIZE));
		setBorder(new EmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
		setOpaque(false);
		setCursor(DEFAULT_CURSOR);
		setMinimumSize(new Dimension(Short.MIN_VALUE, 40));
		setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					revalidate();
				}
			}
		);
	}
	
	
	
	//
	// Gestion de l'affichage du composant
	//
	
	/**
	 * Affiche le fond de la boite d'outils
	 */
	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
		super.paint(g);
	}
	
	
	//
	// Getters
	//
	
	public LocationItem getLocationItem() {
		return m_locationItem;
	}
	
	public Track getTrack() {
		int trackId = m_locationItem.getTrackId();
		if (trackId == -1)
			return null;
		return m_score.getTrack(m_locationItem.getTrackId());
	}
	
	public Score getScore() {
		return m_score;
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	public void updateLocation() {
		// Positionnement par défaut : démarrage au dessus de l'élément aligné à gauche
		int x = m_locationItem.getX1();
		int y = m_locationItem.m_y1 - 45;
		
		// Positionne en dessous de l'élément si non visible au dessus
		if (m_locationItem.m_y1 < 45)
			y = m_locationItem.m_y2 + 5;
		
		// Alignement à droite si pas assez de place à droite
		if (x + getWidth() > m_container.getWidth())
			x = m_container.getWidth() - getWidth() -  10;
		
		// Affectation du positionnement
		setLocation(x, y);
	}
	

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		
		// Affichage de la barre d'outils
		if (visible) {
			int width = 0;
			int height = 0;
			
			// Calcul de la largeur et de la hauteur de la barre d'outils
			for (Component comp : getComponents()) {
				Dimension compSize = comp.getPreferredSize();
				width += compSize.width + GAP_SIZE;
				if (compSize.height > height) {
					height = compSize.height;
				}
			}
			
			// Affecte la taille � la barre d'outils
			Dimension size = new Dimension(width + (BORDER_SIZE*2), height + (BORDER_SIZE*2)); 
			setSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setPreferredSize(size);

			// Affecte la hauteur maximale � tous les composants de la barre
			for (Component comp : getComponents()) {
				Dimension compSize = comp.getPreferredSize();
				compSize.height = height;
				comp.setPreferredSize(compSize);
				comp.setSize(compSize);
				comp.setMinimumSize(compSize);
				comp.setMaximumSize(compSize);
			}
			
			// Affiche la barre
			m_container.add(this);
		}
		// D�saffichage de la barre d'outils
		else {
			super.setVisible(false);
			// Retire la barre
			m_container.remove(this);
		}
		
		// Mise à jour de la position à l'affichage
		if (visible == true) {
			updateLocation();
		}		
	}
	
	
	/**
	 * Ajout d'un s�parateur
	 */
	public void addSeparator(){
		add(new JLabel(" "));
	}

	
	//
	// Méthodes abstraites
	//
	
	abstract public void keyTyped(KeyEvent event);
	
	
	//
	// Attributs
	//
	
	protected LocationItem m_locationItem;
	protected Container m_container;
	protected Score m_score;
	
}
