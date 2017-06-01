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


import java.util.*;
import javax.swing.*;

import res.ResourceLoader;


public class ToolIcon {

	public final static byte NO_ICON = 0;
	public final static byte ALIGN_LEFT = 1;
	public final static byte ALIGN_CENTER = 2;
	public final static byte ALIGN_RIGHT = 3;
	public final static byte ADD = 4;
	public final static byte UP = 5;
	public final static byte DOWN = 6;
	public final static byte REST = 7;
	public final static byte DOT = 8;
	public final static byte PLAY = 9;
	public final static byte PLAY_LITTLE = 10;
	public final static byte PAUSE = 11;
	public final static byte PAUSE_LITTLE = 12;
	public final static byte STOP = 13;
	public final static byte STOP_LITTLE = 14;
	public final static byte DELETE = 15;
	public final static byte TUNE = 16;
	public final static byte VALID = 17;
	public final static byte CANCEL = 18;
	public final static byte LINK = 19;
	public final static byte TRIPLET = 20;
	public final static byte PHRASE_START = 21;
	public final static byte PHRASE_END = 22;
	public final static byte PLAY_FROM = 23;
	public final static byte PLAY_FROM_LITTLE = 24;
	public final static byte ADD_BAR = 25;
	public final static byte START_RECORD = 26;
	public final static byte STOP_RECORD = 27;
	public final static byte EDITABLE = 28;
	public final static byte EDIT = 29;
	public final static byte NUMBER_OF_ICONS = 30;
	
	private static Vector<ImageIcon> m_icons = new Vector<ImageIcon>();
	
	
	// Chargement des images
	static {
		m_icons.setSize(NUMBER_OF_ICONS);
		addIcon(NO_ICON,			"/res/icons/unknown.png");
		addIcon(ALIGN_LEFT,			"/res/icons/align-horizontal-left.png");
		addIcon(ALIGN_CENTER,		"/res/icons/align-horizontal-center.png");
		addIcon(ALIGN_RIGHT,		"/res/icons/align-horizontal-right.png");
		addIcon(ADD,				"/res/icons/list-add.png");
		addIcon(UP,					"/res/icons/arrow-up.png");
		addIcon(DOWN,				"/res/icons/arrow-down.png");
		addIcon(REST,				"/res/icons/ht-rest.png");
		addIcon(DOT,				"/res/icons/ht-dot.png");
		addIcon(PLAY,				"/res/icons/media-playback-start.png");
		addIcon(PLAY_LITTLE,		"/res/icons/media-playback-start-16.png");
		addIcon(PAUSE,				"/res/icons/media-playback-pause.png");
		addIcon(PAUSE_LITTLE,		"/res/icons/media-playback-pause-16.png");
		addIcon(STOP,				"/res/icons/media-playback-stop.png");
		addIcon(STOP_LITTLE,		"/res/icons/media-playback-stop-16.png");
		addIcon(DELETE,				"/res/icons/dialog-close.png");
		addIcon(TUNE,				"/res/icons/kmix-master.png");
		addIcon(VALID,				"/res/icons/dialog-ok-apply.png");
		addIcon(CANCEL,				"/res/icons/edit-delete.png");
		addIcon(LINK, 				"/res/icons/ht-link.png");
		addIcon(TRIPLET,			"/res/icons/ht-triplet.png");
		addIcon(PHRASE_START,		"/res/icons/ht-phrase-start.png");
		addIcon(PHRASE_END,			"/res/icons/ht-phrase-end.png");
		addIcon(PLAY_FROM,			"/res/icons/media-playback-start.png");
		addIcon(PLAY_FROM_LITTLE,	"/res/icons/media-playback-start-16.png");
		addIcon(ADD_BAR, 			"/res/icons/add-bar.png");
		addIcon(START_RECORD	,	"/res/icons/media-record.png");
		addIcon(STOP_RECORD,		"/res/icons/media-record-stop.png");
		addIcon(EDITABLE,			"/res/icons/draw-path.png");
		addIcon(EDIT,				"/res/icons/document-edit.png");
	}
	
	private static void addIcon(byte id, String path) {
		m_icons.setElementAt(new ImageIcon(ResourceLoader.getInstance().loadImage(path)), id);
	}
	
	
	public static ImageIcon getIcon(byte icon) {
		return m_icons.elementAt(icon);
	}
	
	
}
