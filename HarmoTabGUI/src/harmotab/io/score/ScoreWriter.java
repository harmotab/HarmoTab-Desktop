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

import harmotab.core.Localizer;
import harmotab.core.Score;
import harmotab.core.i18n;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;


/**
 * Classe m�re des objets de d'ecriture de partitions
 */
public abstract class ScoreWriter extends ScoreIO {

	public ScoreWriter(Score score, String path) {
		super(score, path);
	}


	abstract protected void write(Score score, File file) throws IOException;
	
	
	public void saveAs(String path) throws IOException {
		ScoreWriter writer = (ScoreWriter) clone();
		File outputFile = new File(path);
		if (outputFile.exists()) {
			int res = JOptionPane.showConfirmDialog(
					null, 
					Localizer.get(i18n.M_FILE_ALREADY_EXISTS_QUESTION).replace("%FILE%", path),
					Localizer.get(i18n.MENU_SAVE_AS),
					JOptionPane.YES_NO_OPTION);
			if (res != JOptionPane.YES_OPTION)
				return;
		}
		writer.setFile(outputFile);
		writer.save();
	}
	
	public void save() throws IOException {
		write(m_score, m_file);
	}

}
