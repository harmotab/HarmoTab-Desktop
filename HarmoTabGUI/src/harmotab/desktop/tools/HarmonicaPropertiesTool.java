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

import harmotab.core.*;
import harmotab.desktop.actions.*;
import harmotab.renderer.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Boite d'outils de modifications des propri�t�s d'un harmonica
 */
public class HarmonicaPropertiesTool extends Tool {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public HarmonicaPropertiesTool(Container container, Score score, LocationItem item) {
		super(container, score, item);

		// Construction des composants
		ToolButton harmoPropertiesButton = new ToolButton(new ShowHarmonicaPropertiesAction());
		harmoPropertiesButton.setWide(true);
		
		// Ajout des composants � l'interface
		add(harmoPropertiesButton);
		
	}
	
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	//
	// Attributs
	//
	
	
}
