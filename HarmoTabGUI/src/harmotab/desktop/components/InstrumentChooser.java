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

import harmotab.core.Localizer;
import harmotab.core.i18n;

import java.util.Vector;
import javax.swing.*;


public class InstrumentChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	//
	// Initialisation de la liste des instruments
	//
	
	public static final int INSTRUMENTS_NUMBER = 128;
	private static Vector<String> m_instruments;
	
	static {
		m_instruments = new Vector<String>(INSTRUMENTS_NUMBER);
		for (int i = 0; i < INSTRUMENTS_NUMBER; i++)
			m_instruments.add(Localizer.get(i18n.MIDI_INSTRUMENT_KEY.replace("%", i+"")));
	}
	
	
	//
	// Constructeurs
	//
	
	public InstrumentChooser() {
		super(m_instruments);
	}

	public InstrumentChooser(int value) {
		super(m_instruments);
		setSelectedIndex(value);
	}
	
}
