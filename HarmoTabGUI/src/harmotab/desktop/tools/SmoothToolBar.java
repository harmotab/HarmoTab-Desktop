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
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class SmoothToolBar extends /*JToolBar*/JPanel {
	private static final long serialVersionUID = 1L;
	
	private Container m_container;
	
	
	// 
	// Constructeur
	// 
	
	public void addSeparator(){}
	
	public SmoothToolBar(Container container) {
		m_container = container;
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		setBorder(new EmptyBorder(0, 0, 0, 0));
//		setFloatable(false);
		
		addSeparator();

		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					revalidate();
				}
			}
		);
		
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	@Override
	public void setVisible(boolean visible) {
		// Affichage de la barre d'outils
		if (visible) {
			int width = 0;
			int height = 0;
			
			// Calcul de la largeur et de la hauteur de la barre d'outils
			for (Component comp : getComponents()) {
				Dimension compSize = comp.getPreferredSize();
				width += compSize.width;
				if (compSize.height > height)
					height = compSize.height;
			}
			
			// Affecte la taille � la barre d'outils
			Dimension size = new Dimension(width + 20, height); 
			setSize(size);
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
	}
	
}

