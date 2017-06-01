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

package harmotab.desktop.modeleditor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import harmotab.core.Localizer;
import harmotab.core.i18n;
import harmotab.desktop.*;
import javax.swing.*;
import javax.swing.border.*;


/**
 * Interface graphique correspondant à une partie d'un harmonica.
 */
class HarmonicaBodyLabel extends JLabel implements MouseListener {
	private static final long serialVersionUID = 1L;
	
	public static final int START = -1;
	public static final int END_DIATO = -2;
	public static final int END_CHROMA_NATURAL = -3;
	public static final int END_CHROMA_PUSHED = -4;
	

	//
	// Constructeur
	//
	
	public HarmonicaBodyLabel(int hole) {
		m_hole = hole;
		m_listener = null;
				
		switch (m_hole) {
			case START:
				setIcon(GuiIcon.getIcon(GuiIcon.HARMONICA_START));
				break;
			case END_DIATO:
				setIcon(GuiIcon.getIcon(GuiIcon.HARMONICA_END_DIATO));
				break;
			case END_CHROMA_NATURAL:
				setIcon(GuiIcon.getIcon(GuiIcon.HARMONICA_END_CHROMA_NATURAL));
				setText(Localizer.get(i18n.ET_PISTON_RELEASED) + "   ");
				break;
			case END_CHROMA_PUSHED:
				setIcon(GuiIcon.getIcon(GuiIcon.HARMONICA_END_CHROMA_PUSHED));
				setText(Localizer.get(i18n.ET_PISTON_PUSHED) + "   ");
				break;
			default:
				setIcon(GuiIcon.getIcon(GuiIcon.HARMONICA_HOLE));
		}
		
		addMouseListener(this);
	}
	
	
	//
	// Gestion des �v�nement
	//
	
	public void setActionListener(ActionListener listener) {
		m_listener = listener;
		
		if (listener != null) {
			setBorder(new LineBorder(Color.GRAY));
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else {
			setBorder(null);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	

	@Override
	public void mouseClicked(MouseEvent event) {
		if (m_listener != null) {
			m_listener.actionPerformed(new ActionEvent(this, 0, ""));
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		if (m_listener != null) {
			setBorder(new LineBorder(Color.BLACK));
		}
	}
	
	@Override public void mouseExited(MouseEvent event) {
		if (m_listener != null) {
			setBorder(new LineBorder(Color.GRAY));
		}
	}
	
	@Override public void mousePressed(MouseEvent event) {}
	@Override public void mouseReleased(MouseEvent event) {}
	
	
	//
	// Attributs
	//
	
	private int m_hole;
	private ActionListener m_listener;
	
}
