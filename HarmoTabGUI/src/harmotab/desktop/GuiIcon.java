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

import java.util.*;
import javax.swing.*;
import res.ResourceLoader;


/**
 * Fournit les images n�cessaires � l'interface graphique
 */
public class GuiIcon {

	public final static byte HARMONICA_START = 0;
	public final static byte HARMONICA_END_DIATO = 1;
	public final static byte HARMONICA_END_CHROMA_NATURAL = 2;
	public final static byte HARMONICA_END_CHROMA_PUSHED = 3;
	public final static byte HARMONICA_HOLE = 4;
	public final static byte ALTERATION_NATURAL = 5;
	public final static byte ALTERATION_SHARP = 6;
	public final static byte ALTERATION_FLAT = 7;
	public final static byte ADD = 8;
	public final static byte HARMOTAB_ICON_16 = 9;
	public final static byte HARMOTAB_ICON_32 = 10;
	public final static byte HARMOTAB_ICON_48 = 11;
	public final static byte HARMOTAB_ICON_64 = 12;
	public final static byte HARMOTAB_ICON_96 = 13;
	public final static byte HARMOTAB_ICON_128 = 14;
	public final static byte HARMOTAB_ICON_192 = 15;
	
	public final static byte NUMBER_OF_ICONS = 16;
	
	private static Vector<ImageIcon> m_icons = new Vector<ImageIcon>();
	
	
	// Chargement des images
	static {
		m_icons.setSize(NUMBER_OF_ICONS);
		addIcon(HARMONICA_START, 				"/res/gui/harmo-start.png");
		addIcon(HARMONICA_END_DIATO, 			"/res/gui/harmo-end-diato.png");
		addIcon(HARMONICA_END_CHROMA_NATURAL, 	"/res/gui/harmo-end-chroma-natural.png");
		addIcon(HARMONICA_END_CHROMA_PUSHED, 	"/res/gui/harmo-end-chroma-pushed.png");
		addIcon(HARMONICA_HOLE, 				"/res/gui/harmo-hole.png");
		addIcon(ALTERATION_NATURAL, 			"/res/controllers/natural.png");
		addIcon(ALTERATION_SHARP, 				"/res/controllers/sharp.png");
		addIcon(ALTERATION_FLAT, 				"/res/controllers/flat.png");
		addIcon(ADD, 							"/res/icons/list-add.png");
		addIcon(HARMOTAB_ICON_16,				"/res/icons/soft/icone2_16.png");
		addIcon(HARMOTAB_ICON_32,				"/res/icons/soft/icone_32.png");
		addIcon(HARMOTAB_ICON_48,				"/res/icons/soft/icone_48.png");
		addIcon(HARMOTAB_ICON_64,				"/res/icons/soft/icone_64.png");
		addIcon(HARMOTAB_ICON_96,				"/res/icons/soft/icone_96.png");
		addIcon(HARMOTAB_ICON_128,				"/res/icons/soft/icone_128.png");
		addIcon(HARMOTAB_ICON_192,				"/res/icons/soft/icone_192.png");
	}
	
	private static void addIcon(byte id, String path) {
		m_icons.setElementAt(new ImageIcon(ResourceLoader.getInstance().loadImage(path)), id);
	}

	public static ImageIcon getIcon(byte icon) {
		return m_icons.elementAt(icon);
	}
	
}
