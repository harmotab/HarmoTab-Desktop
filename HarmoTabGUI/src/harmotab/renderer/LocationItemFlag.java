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

package harmotab.renderer;

import harmotab.core.Localizer;
import harmotab.core.i18n;


public class LocationItemFlag {
	
	public static final int ERRORNOUS_ITEM		= 0;
	public static final int STAND_ALONE 			= 1;
	public static final int FORCE_QUEUE_UP			= 2;
	public static final int FORCE_QUEUE_DOWN		= 3;
	public static final int TEMPORARY_ELEMENT		= 4;
	public static final int IMPLICIT_ELEMENT		= 5;
	public static final int EXPLICIT_ALTERATION		= 6;
	public static final int IMPLICIT_PHRASE_START	= 7;
	public static final int IMPLICIT_PHRASE_END		= 8;
	public static final int DRAW_REPEAT_SYMBOL		= 9;
	public static final int NOT_FILLED_BAR			= 10;
	public static final int BAR_EXCEEDED			= 11;
	public static final int DEBUG_MARK				= 28;
	public static final int RED_MARK				= 29;
	public static final int GREEN_MARK				= 30;
	

	public static String getErrorFlagToolTip(LocationItem item) {
		if (item.getFlag(BAR_EXCEEDED))
			return Localizer.get(i18n.M_BAR_EXCEEDED);
		else if (item.getFlag(NOT_FILLED_BAR))
			return Localizer.get(i18n.M_NOT_FILLED_BAR);
		else
			return "";
	}
	
}
