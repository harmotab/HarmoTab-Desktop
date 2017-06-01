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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;


/**
 * Composant d'afficheur de compte � rebours
 */
public class CountdownPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 80;
	private static final int HEIGHT = 80;

	
	//
	// Constructeur
	//
	
	public CountdownPanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.WHITE);
	}
	
	
	//
	// Getter / setters
	//
	
	/**
	 * Modification de l'afficheur.
	 * 0 <= fraction <= 1.0
	 */
	public void setValue(String label, int value, float fraction) {
		m_label = label;
		m_value = value;
		m_fraction = fraction;
		repaint();
	}
	
	
	//
	// Affichage du composant
	//
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
//		boolean even = ((int) m_value % 2 == 0);
		boolean even = false;
		int angle = (int) ((even ? -1 : 0) - m_fraction * 360);
		int width = getWidth();
		int height = getHeight();

		// Affichage du fond
		g2d.setColor(even ? COLOR1 : COLOR2);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		// Affichage de la portion de cercle en fonction de l'avanc�e
		g2d.setColor(even ? COLOR2 : COLOR1);
		g2d.fillArc(-width/2, -height/2, width*2, height*2, 90, angle);

		// Affichage du compte � rebours
		float fontSize = (float) (width < height ? width : height) - 30.0f;
		fontSize = Math.max(fontSize, 48);
		g2d.setFont(NUMBER_FONT.deriveFont(fontSize));
		g2d.setColor(Color.BLACK);
		int x = (width / 2) - (g2d.getFontMetrics().stringWidth(m_label) / 2);
		int y = (height / 2) + (g2d.getFontMetrics().getHeight() / 4);
		g2d.drawString(m_label, x, y);
	}
	
	
	//
	// Attributs
	//
	
	protected String m_label = "";
	protected int m_value = 0;
	protected float m_fraction = 0;
	
	private final Font NUMBER_FONT = new Font("Sans-serif", Font.PLAIN, 96);
	private final Color COLOR1 = Color.GRAY;
	private final Color COLOR2 = Color.WHITE;
	
}

