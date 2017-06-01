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

package harmotab.desktop;

import harmotab.element.*;
import java.util.Vector;
import javax.swing.ImageIcon;
import res.ResourceLoader;


public class ElementIcon {
	
	public final static byte NOTE = 0;
	public final static byte BAR = 1;
	public final static byte ACCOMPANIMENT = 2;
	public final static byte SILENCE = 3;
	public final static byte LYRICS = 4;
	public final static byte NUMBER_OF_ICONS = 5;
	
	private static Vector<ImageIcon> m_icons = new Vector<ImageIcon>();
	
	
	// Chargement des images
	static {
		m_icons.setSize(NUMBER_OF_ICONS);
		addIcon(NOTE,			"/res/elements/note-icon.png");
		addIcon(BAR,			"/res/elements/bar-icon.png");
		addIcon(ACCOMPANIMENT,	"/res/elements/accompaniment-icon.png");
		addIcon(SILENCE,		"/res/elements/silence-icon.png");
		addIcon(LYRICS,	"/res/elements/lyrics-icon.png");
	}
	
	private static void addIcon(byte id, String path) {
		ResourceLoader loader = ResourceLoader.getInstance();
		m_icons.setElementAt(new ImageIcon(loader.loadImage(path)), id);
	}
	
	
	public static ImageIcon getIcon(Element element) {
		if (element instanceof Note)			return m_icons.elementAt(NOTE);
		if (element instanceof Bar)				return m_icons.elementAt(BAR);
		if (element instanceof Accompaniment)	return m_icons.elementAt(ACCOMPANIMENT);
		if (element instanceof Silence)			return m_icons.elementAt(SILENCE);
		if (element instanceof Lyrics)			return m_icons.elementAt(LYRICS);
		return null;
	}
	
}
