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

import java.io.*;
import java.util.*;
import java.util.zip.*;
import rvt.util.gui.FileUtilities;
import harmotab.core.Score;
import harmotab.io.score.HT3ScoreReader;
import harmotab.io.score.ScoreReader;
import harmotab.throwables.FileFormatException;


/**
 * Lecture de fichiers ht3x.
 * Les fichiers contenus dans le ht3x sont extaits dans le r�pertoire temporaire
 * du syst�me.
 */
public class Ht3XScoreReader extends ScoreReader {

	//
	// Constructeur
	//
	
	public Ht3XScoreReader(Score score, String path) {
		super(score, path);
		m_performanceList = null;
	}
	
	
	//
	// Getters / setters
	//
	
	@Override
	public boolean isExportedScore() {
		return true;
	}
	
	@Override
	public PerformancesList getPerformancesList() {
		return m_performanceList;
	}
	
	protected void setPerformancesList(PerformancesList list) {
		m_performanceList = list;
	}
	
	
	//
	// M�thodes
	//

	@Override
	protected void read(Score score, File file) throws IOException, FileFormatException {
		FileInputStream input = new FileInputStream(file);
		Map<String, File> oldNamesMapping = new HashMap<String, File>();
		m_performanceList = new PerformancesList();
		
		try  {
			ZipInputStream zin = new ZipInputStream(input);
			ZipEntry ze = null;
			
			// Parcours des fichiers contenus dans l'archive et prise en compte 
			// individuelle de chaque fichier
			while ((ze = zin.getNextEntry()) != null) {
				String entryName = ze.getName();

				// Partition
				if (entryName.endsWith(".ht3")) {
					File scoreFile = writeAsTemporaryFile(zin, FileUtilities.getNameWithoutExtension(ze.getName()), ".ht3");
					HT3ScoreReader reader = new HT3ScoreReader(score, scoreFile.getAbsolutePath());
					reader.open();
				}
				// Fichier midi
				else if (entryName.endsWith(".mid")) {
					/* NON LU */;
				}
				// Fichier image
				else if (entryName.endsWith(".png")) {
					/* NON LU */;
				}
				// Mapping temps / �l�ments
				else if (entryName.endsWith(".smap")) {
					/* NON LU */;
				}
				// Liste des interpr�tations
				else if (entryName.endsWith(".perfslist")) {
					// Passage par un fichier temporaire car il semble que le 
					// parseur SAX cause une fermeture du flux zip ??? 
					File tempFile = writeAsTemporaryFile(zin, "ht_", ".xml");
					InputStream inStream = new FileInputStream(tempFile);
					PerformanceListReader reader = new PerformanceListReader();
					reader.read(m_performanceList, inStream);
					inStream.close();
					tempFile.delete();
				}
				// Fichier d'interpr�tation
				else if (entryName.endsWith(".pcm")) {
					File newPcmFile = writeAsTemporaryFile(zin, "perf_", ".pcm");
					oldNamesMapping.put(ze.getName(), newPcmFile);
				}
			}
			
			// Archive end
			try {
				zin.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			// R�affecte les bon fichiers son au niveau de la liste des interpr�tations
			for (Performance perf : m_performanceList) {
				File newFile = oldNamesMapping.get(perf.getFile().getName());
				if (newFile != null) {
					perf.setFile(newFile);
				}
				else {
					System.err.println("HT3XScoreReader: " + "Extracted sound file for performance " + perf.getName() + " not found !");
				}
			}
			
		} 
		catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	
	/**
	 * Ecrit le flux en entr�e dans un fichier temporaire
	 */
	protected File writeAsTemporaryFile(InputStream is, String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		FileOutputStream os = new FileOutputStream(file);
		int read = 0;
		byte[] buffer = new byte[4096];
		while ((read = is.read(buffer, 0, buffer.length)) != -1) {
			os.write(buffer, 0, read);
		}
		return file;
	}
	
	
	//
	// Attributs
	//
	
	protected PerformancesList m_performanceList = null;

}

