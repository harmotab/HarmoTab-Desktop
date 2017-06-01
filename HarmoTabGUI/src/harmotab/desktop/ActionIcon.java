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
 * Contient et propose les icones associées aux différentes actions du logiciel
 */
public class ActionIcon {

	public final static byte OPEN = 0;
	public final static byte CLOSE = 1;
	public final static byte SAVE = 2;
	public final static byte SAVE_AS = 3;
	public final static byte NEW = 4;
	public final static byte WIZARD = 5;
	public final static byte PLAY_CHORD = 6;
	public final static byte CONFIGURE = 7;
	public final static byte CONFIGURE_LITTLE = 8;
	public final static byte PRINT = 9;
	public final static byte HELP = 10;
	public final static byte ABOUT = 11;
	public final static byte ADD_BEFORE = 12;
	public final static byte ADD_BEFORE_LITTLE = 13;
	public final static byte ADD_AFTER = 14;
	public final static byte ADD_AFTER_LITTLE = 15;
	public final static byte ADD_LAST = 16;
	public final static byte ADD_LAST_LITTLE = 17;
	public final static byte DELETE = 18;
	public final static byte DELETE_LITTLE = 19;
	public final static byte INSERT = 20;
	public final static byte INSERT_LITTLE = 21;
	public final static byte SCORE_PROPERTIES = 22;
	public final static byte SCORE_PROPERTIES_LITTLE = 23;
	public final static byte EXPORT = 24;
	public final static byte EXPORT_SOUND = 25;
	public final static byte EXPORT_IMAGE = 26;
	public final static byte DROP_DOWN = 27;
	public final static byte RECORD = 28;
	public final static byte EDIT = 29;
	public final static byte UNDO = 30;
	public final static byte UNDO_LITTLE = 31;
	public final static byte REDO = 32;
	public final static byte REDO_LITTLE = 33;
	public final static byte MODEL = 34;
	public final static byte MODEL_LITTLE = 35;
	public final static byte OK = 36;
	public final static byte CANCEL = 37;
	public final static byte NUMBER_OF_ICONS = 38;
	
	private static Vector<ImageIcon> m_icons = new Vector<ImageIcon>();
	
	
	// Chargement des images
	static {
		m_icons.setSize(NUMBER_OF_ICONS);
		addIcon(OPEN,						"/res/toolbar/document-open.png");
		addIcon(CLOSE,						"/res/toolbar/dialog-close.png");
		addIcon(SAVE,						"/res/toolbar/document-save.png");
		addIcon(SAVE_AS,					"/res/toolbar/document-save-as.png");
		addIcon(NEW,						"/res/toolbar/document-new.png");
		addIcon(WIZARD,						"/res/toolbar/tools-wizard.png");
		addIcon(PLAY_CHORD,					"/res/toolbar/play-chord.png");
		addIcon(CONFIGURE,					"/res/toolbar/configure.png");
		addIcon(CONFIGURE_LITTLE,			"/res/toolbar/configure-16.png");
		addIcon(PRINT,						"/res/toolbar/document-print.png");
		addIcon(HELP,						"/res/toolbar/help-contents.png");
		addIcon(ABOUT,						"/res/toolbar/help-about.png");
		addIcon(ADD_BEFORE,					"/res/toolbar/edit-table-insert-column-left.png");
		addIcon(ADD_AFTER,					"/res/toolbar/edit-table-insert-column-right.png");
		addIcon(ADD_LAST, 					"/res/toolbar/edit-table-insert-row-under.png");
		addIcon(ADD_BEFORE_LITTLE,			"/res/toolbar/edit-table-insert-column-left-16.png");
		addIcon(ADD_AFTER_LITTLE,			"/res/toolbar/edit-table-insert-column-right-16.png");
		addIcon(ADD_LAST_LITTLE,			"/res/toolbar/edit-table-insert-row-under-16.png");
		addIcon(DELETE,						"/res/toolbar/edit-delete.png");
		addIcon(DELETE_LITTLE,				"/res/toolbar/edit-delete-16.png");
		addIcon(INSERT, 					"/res/toolbar/insert.png");
		addIcon(SCORE_PROPERTIES,			"/res/toolbar/view-table-of-contents-ltr.png");
		addIcon(SCORE_PROPERTIES_LITTLE,	"/res/toolbar/view-table-of-contents-ltr-16.png");
		addIcon(EXPORT,						"/res/toolbar/export.png");
		addIcon(EXPORT_SOUND,				"/res/toolbar/export-sound.png");
		addIcon(EXPORT_IMAGE,				"/res/toolbar/export-image.png");
		addIcon(DROP_DOWN,					"/res/toolbar/arrow-down.png");
		addIcon(RECORD,						"/res/toolbar/media-record.png");
		addIcon(EDIT,						"/res/toolbar/document-edit.png");
		addIcon(UNDO,						"/res/toolbar/edit-undo.png");
		addIcon(UNDO_LITTLE,				"/res/toolbar/edit-undo-16.png");
		addIcon(REDO,						"/res/toolbar/edit-redo.png");
		addIcon(REDO_LITTLE,				"/res/toolbar/edit-redo-16.png");
		addIcon(MODEL,						"/res/toolbar/tools-wizard.png");		//TODO: Icone à faire
		addIcon(MODEL_LITTLE,				"/res/toolbar/tools-wizard-16.png");	//TODO: Icone à faire
		addIcon(OK,							"/res/toolbar/dialog-ok-apply.png");
		addIcon(CANCEL,						"/res/toolbar/dialog-cancel.png");
	}
	
	private static void addIcon(byte id, String path) {
		ResourceLoader loader = ResourceLoader.getInstance();
		m_icons.setElementAt(new ImageIcon(loader.loadImage(path)), id);
	}
	
	
	public static ImageIcon getIcon(byte icon) {
		return m_icons.elementAt(icon);
	}

}
