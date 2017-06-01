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

import java.awt.Dimension;
import harmotab.element.*;
import harmotab.harmonica.*;
import javax.swing.*;
import rvt.util.gui.LabelledSpinner;


/**
 * Composant de s�lection d'un trou
 */
public class HoleChooser extends LabelledSpinner {
	private static final long serialVersionUID = 1L;
	private static final int NUMBER_OF_COLUMNS = 2;
	

	//
	// Constructeur
	//
	
	public HoleChooser(byte hole) {
		super("", 
			new SpinnerNumberModel(hole, Tab.UNDEFINED, HarmonicaModel.MAX_HOLE_VALUE, 1), 
			NUMBER_OF_COLUMNS);
		m_editor = new HoleChooserEditor("");
		setEditor(m_editor);
	}
	
	
	//
	// Inner classes
	//
	
	/**
	 * Editeur n'affichant aucun texte � la place de "0"
	 */
	class HoleChooserEditor extends LabelledSpinnerEditor {
		private static final long serialVersionUID = 1L;

		HoleChooserEditor(String label) {
			super(label);
			//Dimension dim = getPreferredSize();
			//dim.width = 40;
			setPreferredSize(new Dimension(40, 25));
		}
		
		@Override
		public void updateView() {
			super.updateView();
			Integer value = (Integer) m_labelledSpinnerInstance.getValue();
			if (value == 0) {
				m_textField.setText("");
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	private HoleChooserEditor m_editor = null;

}
