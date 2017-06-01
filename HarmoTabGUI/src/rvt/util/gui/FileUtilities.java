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

package rvt.util.gui;

import java.io.File;


public class FileUtilities {

	public static String getExtension(File file) {
		if (file != null) {
			return getExtension(file.getName());
		}
		return null;
	}
	
	public static String getExtension(String filename) {
		int extPos = 0;
		for (extPos = filename.length()-1; extPos > 0; extPos--) {
			char cur = filename.charAt(extPos);
			if (cur == '.') {
				return filename.substring(extPos+1).toLowerCase();
			}
			else if (cur == '/' || cur == '\\') {
				return null;
			}
		}
		return null;
	}
	
	
	public static String getNameWithoutExtension(String filename) {
		String ext = getExtension(filename);
		if (ext == null)
			return filename;
		return filename.substring(0, filename.length() - ext.length() - 1);
	}
	
}
