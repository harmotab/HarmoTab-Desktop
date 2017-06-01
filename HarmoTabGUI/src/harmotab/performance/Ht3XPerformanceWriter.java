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

package harmotab.performance;

import harmotab.core.Score;
import harmotab.io.score.HT3XScoreWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Permet d'ecrire les fichiers son dans les fichiers ht3x.
 */
public class Ht3XPerformanceWriter extends HT3XScoreWriter {
	
	//
	// Constructeur
	//
	
	public Ht3XPerformanceWriter(Score score, String path, PerformancesList performancesList) {
		super(score, path);
		setPerformanceList(performancesList);
	}
	
	
	//
	// Getters / setters
	//
	
	public PerformancesList getPerformanceList() {
		return m_performanceList;
	}
	
	public void setPerformanceList(PerformancesList performanceList) {
		m_performanceList = performanceList;
	}
	
	
	//
	// M�thodes m�tiers
	//
	
	@Override
	protected void createTemporaryFiles() throws IOException {
		super.createTemporaryFiles();
		
		// Cr�ation du fichier contenant la liste des fichiers son
		PerformanceListWriter writer = new PerformanceListWriter();
		File performanceListFile = File.createTempFile("perfs_", ".perfslist");
		performanceListFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(performanceListFile);
		writer.write(fos, m_performanceList);
		fos.close();
		m_temporaryFilesPaths.add(performanceListFile);
		
		// Ajout des fichiers son
		for (Performance perf : m_performanceList) {
			m_temporaryFilesPaths.add(perf.getFile());
		}
	}
	
	
	//
	// Attributs
	//
	
	protected PerformancesList m_performanceList;

}
