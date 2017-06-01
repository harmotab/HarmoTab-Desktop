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

import harmotab.core.*;
import harmotab.desktop.ActionIcon;
import harmotab.harmonica.*;
import javax.swing.*;
import java.awt.event.*;


public class HarmonicaModelEditorToolbar extends JToolBar {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//
	
	public HarmonicaModelEditorToolbar(HarmonicaModelController controller) {
		m_controller = controller;
		
		// Cr�ation des boutons
		m_new = new JButton(ActionIcon.getIcon(ActionIcon.NEW));
		m_new.setToolTipText(Localizer.get(i18n.TT_NEW_MODEL));
		m_open = new JButton(ActionIcon.getIcon(ActionIcon.OPEN));
		m_open.setToolTipText(Localizer.get(i18n.TT_OPEN_MODEL));
		m_save = new JButton(ActionIcon.getIcon(ActionIcon.SAVE));
		m_save.setToolTipText(Localizer.get(i18n.TT_SAVE_MODEL));
		m_saveAs = new JButton(ActionIcon.getIcon(ActionIcon.SAVE_AS));
		m_saveAs.setToolTipText(Localizer.get(i18n.TT_SAVE_MODEL_AS));
		
		// Ajout des boutons � la barre
		add(m_new);
		add(m_open);
		add(m_save);
		add(m_saveAs);
		
		// Enregistrement des listeners
		ButtonsOberver listener = new ButtonsOberver();
		m_new.addActionListener(listener);
		m_open.addActionListener(listener);
		m_save.addActionListener(listener);
		m_saveAs.addActionListener(listener);
				
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	private class ButtonsOberver implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == m_new) {
				m_controller.createNew();
			}
			else if (event.getSource() == m_open) {
				m_controller.open();
			}
			else if (event.getSource() == m_save) {
				m_controller.save();
			}
			else if (event.getSource() == m_saveAs) {
				m_controller.saveAs();
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	private HarmonicaModelController m_controller = null;
	
	private JButton m_new = null;
	private JButton m_open = null;
	private JButton m_save = null;
	private JButton m_saveAs = null;

}

