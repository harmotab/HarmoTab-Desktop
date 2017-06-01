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

package harmotab.harmonica;

import harmotab.core.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import rvt.util.gui.FileUtilities;


public class HarmonicaModelFileFilter extends FileFilter {
	
	public static final String HARMOTAB_2_HARMONICA_MODEL_EXTENSION = "md";
	public static final String HARMOTAB_3_HARMONICA_MODEL_EXTENSION = "hmd";
	
	
	public HarmonicaModelFileFilter() {
		m_acceptLegacyExtensions = false;
	}
	
	public HarmonicaModelFileFilter(boolean acceptLegacyExtensions) {
		m_acceptLegacyExtensions = acceptLegacyExtensions;
	}

	
	
	public void setAcceptLegacyExtensions(boolean accept) {
		m_acceptLegacyExtensions = accept;
	}

	public boolean acceptLegacyExtensions() {
		return m_acceptLegacyExtensions;
	}

	

	@Override
	public boolean accept(File file) {
		if (file == null)
			return false;
		if (file.isDirectory())
			return true;
		if (FileUtilities.getExtension(file) == null)
			return false;
		if (isHarmonicaModelExtension(FileUtilities.getExtension(file)))
			return true;
		return false;
	}
	

	@Override
	public String getDescription() {
		return Localizer.get(i18n.ET_HARMONICA_MODEL_FILE_DESC);
	}
	
	
	public boolean isHarmonicaModelExtension(String extension) {
		if (extension == null)
			return false;
		if (acceptLegacyExtensions()) {
			if (isHarmotab2HarmonicaModelExtension(extension))
				return true;
		}
		if (isHarmotab3HarmonicaModelExtension(extension))
			return true;
		return false;
	}
	
	public static boolean isHarmotab3HarmonicaModelExtension(String extension) {
		if (extension == null)
			return false;
		if (extension.equals(HARMOTAB_3_HARMONICA_MODEL_EXTENSION))
			return true;
		return false;
	}
	
	
	public static boolean isHarmotab2HarmonicaModelExtension(String extension) {
		if (extension == null)
			return false;
		if (extension.equals(HARMOTAB_2_HARMONICA_MODEL_EXTENSION))
			return true;
		return false;
	}
	

	private boolean m_acceptLegacyExtensions;

}
