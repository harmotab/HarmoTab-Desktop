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

package harmotab.element;

import harmotab.io.*;


public class ElementFactory {

	public static Element create(ObjectSerializer serializer, SerializedObject object) {
		Element element = null;
	
		String type = object.getObjectType();
		
		if (type.equals(Element.BAR_TYPESTR))					element = new Bar();
		else if (type.equals(Element.KEY_TYPESTR))				element = new Key();
		else if (type.equals(Element.KEY_SIGNATURE_TYPESTR))	element = new KeySignature();
		else if (type.equals(Element.TIME_SIGNATURE_TYPESTR))	element = new TimeSignature();
		else if (type.equals(Element.HARMOTAB_TYPESTR))			element = new HarmoTabElement();
		else if (type.equals(Element.ACCOMPANIMENT_TYPESTR))	element = new Accompaniment();
		else if (type.equals(Element.CHORD_TYPESTR))			element = new Chord();
		else if (type.equals(Element.NOTE_TYPESTR))				element = new Note();
		else if (type.equals(Element.TAB_TYPESTR))				element = new Tab();
		else if (type.equals(Element.TEXT_ELEMENT_TYPESTR))		element = new TextElement();
		else if (type.equals(Element.TEMPO_TYPESTR))			element = new Tempo();
		else if (type.equals(Element.SILENCE_TYPESTR))			element = new Silence();
		else if (type.equals(Element.LYRICS_TYPESTR))			element = new Lyrics();
		else 
			throw new IllegalArgumentException("ElementFactory::create: Unhandled element type '" + type + "'");
		
		if (element != null)
			element.deserialize(serializer, object);
		return element;
	}
	
}
