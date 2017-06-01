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

package harmotab.io.harmonica;

import harmotab.harmonica.*;
import java.io.*;


public abstract class HarmonicaModelReader {
	
	public static String HARMOTAB_3_MODEL_FILE_EXTENSION = ".hmd";
	public static String HARMOTAB_2_MODEL_FILE_EXTENSION = ".md";	
	

	public static HarmonicaModelReader createReader(HarmonicaModel model, File file) {
		
		if (file.getName().endsWith(HARMOTAB_2_MODEL_FILE_EXTENSION)) {
			return new HarmoTab2HarmonicaModelReader(model);
		}
		else {
			return new HarmoTab3HarmonicaModelReader(model);
		}
	}
	
	
	public abstract void read(File file) throws IOException;
	
	
	public HarmonicaModelReader(HarmonicaModel model) {
		m_model = model;
	}
	
	
	/**
	 * Retourne un FileFilter filtrant tous les fichiers lisible par l'application
	 */
	public static class ReadableModelFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			String name = file.getName();
			if (name.endsWith(HARMOTAB_3_MODEL_FILE_EXTENSION))
				return true;
			if (name.endsWith(HARMOTAB_2_MODEL_FILE_EXTENSION))
				return true;
			return false;
		}
		
	}
	
	
	//
	// Attributs
	//
	
	protected HarmonicaModel m_model = null;

}
