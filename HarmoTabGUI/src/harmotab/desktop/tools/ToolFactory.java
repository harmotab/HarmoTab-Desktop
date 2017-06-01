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

import harmotab.core.*;
import harmotab.element.*;
import harmotab.renderer.*;


/**
 * Constructeur de boite d'outils de modification des �l�ments affich�s sur la 
 * partition
 */
public class ToolFactory {

	/**
	 * Retourne l'instance d'un nouveau controlleur pour l'élément contenu dans <location>.
	 */
	public static Tool createController(Container container, Score score, LocationItem location) {
		switch (location.getElement().getType()) {
			case Element.TEXT_ELEMENT:			return new TextTool(container, score, location);
			case Element.NOTE:					return new NoteTool(container, score, location);
			case Element.TIME_SIGNATURE:		return new TimeSignatureTool(container, score, location);
			case Element.HARMOTAB:				return new HarmoTabTool(container, score, location);
			case Element.TAB:					return new TabTool(container, score, location);
			case Element.BAR:					return new BarTool(container, score, location);
			case Element.KEY_SIGNATURE:			return new KeySignatureTool(container, score, location);
			case Element.KEY:					return new KeyTool(container, score, location);
			case Element.CHORD:					return new ChordTool(container, score, location);
			case Element.ACCOMPANIMENT:			return new AccompanimentTool(container, score, location);
			case Element.TEMPO:					return new TempoTool(container, score, location);
			case Element.SILENCE:				return new SilenceTool(container, score, location);
			case Element.LYRICS:				return new LyricsTool(container, score, location);
			case Element.HARMONICA_PROPERTIES:	return new HarmonicaPropertiesTool(container, score, location);
		}
		System.err.println("ControllerFactory::createController: Cannot set controller: unhandled element type (#" + 
				location.getElement().getType() + ").");
		return null;
	}
	
}
