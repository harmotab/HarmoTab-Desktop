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

package harmotab.core;

import harmotab.element.ElementFactory;
import harmotab.harmonica.*;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;


public class HarmoTabObjectFactory {

	public static HarmoTabObject create(ObjectSerializer serializer, SerializedObject object) {
		HarmoTabObject result = null;
	
		String type = object.getObjectType();
		
		if      (type.equals("#text"))						return null;
		else if (type.equals(Duration.DURATION_TYPESTR))	result = new Duration();
		else if (type.equals(Figure.FIGURE_TYPESTR))		result = new Figure();
		else if (type.equals(Height.HEIGHT_TYPESTR))		result = new Height();
		else if (type.equals(Effect.EFFECT_TYPESTR))		result = new Effect();
		else if (type.equals(RepeatAttribute.REPEAT_ATTRIBUTE_TYPESTR))	result = new RepeatAttribute();
		else if (type.equals(Harmonica.HARMONICA_TYPESTR))	result = new Harmonica();
		else if (type.equals(TabModel.TAB_MODEL_TYPESTR))	result = new TabModel();
		else {
			result = ElementFactory.create(serializer, object);
		}
		
		if (result != null) {
			result.deserialize(serializer, object);
		}
		else {
			throw new IllegalArgumentException("Unhandled object type (" + type + ")");
		}
		
		return result;
	}
	
}
