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

package harmotab.renderer.renderingelements;

import harmotab.core.undo.RestoreCommand;
import harmotab.element.*;


/**
 * Classe m�re des �l�ments qui ne servent que pour l'affichage d'une partition.
 * Ces �l�ments ne sont pas enregistr�s et sont temporaires.
 */
public class RenderingElement extends Element {

	public RenderingElement(byte type) {
		super(type);
	}
	
	@Override
	public RestoreCommand createRestoreCommand() {
		System.err.println("Cannot undo action on a RenderingElement");
		return null;
	}
	
}
