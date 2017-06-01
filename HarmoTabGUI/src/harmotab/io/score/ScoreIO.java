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

import java.io.File;
import harmotab.core.Score;


/**
 * Classe m�re des objets de lecture/ecriture de partitions
 */
public abstract class ScoreIO implements Cloneable {
	
	//
	// Constructeurs
	//
	
	public ScoreIO(Score score, String path) {
		setScore(score);
		setFile(new File(path));
	}
	
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	//
	// Getters / setters
	//
	
	public void setFile(File file) {
		if (file == null)
			throw new NullPointerException();
		m_file = file;
	}
	
	public File getFile() {
		return m_file;
	}
	
	
	public void setScore(Score score) {
		if (score == null)
			throw new NullPointerException();
		m_score = score;
	}
	
	public Score getScore() {
		return m_score;
	}
	
	
	//
	// Attributs
	//
	
	protected Score m_score;
	protected File m_file;
	
}
