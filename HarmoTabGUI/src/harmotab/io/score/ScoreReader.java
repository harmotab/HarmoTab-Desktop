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

package harmotab.io.score;

import harmotab.core.GlobalPreferences;
import harmotab.core.Score;
import harmotab.performance.PerformancesList;
import harmotab.throwables.FileFormatException;
import java.io.File;
import java.io.IOException;


/**
 * Classe m�re des objets de lecture de partitions
 */
public abstract class ScoreReader extends ScoreIO {

	public ScoreReader(Score score, String path) {
		super(score, path);
	}

	
	abstract protected void read(Score score, File file) throws IOException, FileFormatException;

	
	public void open() throws IOException, FileFormatException {
		IOException exception = null;
		
		// D�sactivation temporaire de l'auto-tab
		boolean autoTabEnabled = GlobalPreferences.isAutoTabEnabled();
		GlobalPreferences.setAutoTabEnabled(false);
		
		// Lecture du fichier
		try {
			read(m_score, m_file);
		}
		catch (IOException x) {
			exception = new IOException(x);
		}
		
		// R�activation de l'auto-tab
		GlobalPreferences.setAutoTabEnabled(autoTabEnabled);
		
		// Signalement des erreurs
		if (exception != null)
			throw exception;
	}
	
	
	/**
	 * Indique si la partition lue est extraite d'un fichier htx3
	 */
	public boolean isExportedScore() {
		return false;
	}

	public PerformancesList getPerformancesList() {
		return null;
	}

}
