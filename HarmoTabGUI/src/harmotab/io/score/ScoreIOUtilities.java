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

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import harmotab.core.Localizer;
import harmotab.core.Score;
import harmotab.core.ScoreController;
import harmotab.core.i18n;
import harmotab.desktop.ErrorMessenger;
import harmotab.performance.Ht3XPerformanceWriter;
import harmotab.performance.Ht3XScoreReader;
import harmotab.performance.PerformancesList;


public class ScoreIOUtilities {
	
	//
	// Bo�tes de dialogue d'ouverture / enregistrement
	//
	
	
	/**
	 * Affiche la bo�te de dialogue d'enregistrement d'une partition et effectue
	 * l'enregistrement du fichier.
	 * @return Le ScoreWriter utilis� pour l'enregistrement s'il a �t� effectu�,
	 *  sinon null. 
	 */
	public static ScoreWriter saveScore(Component parent, ScoreController controller, ScoreWriter writer) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("HarmoTab3 score", "ht3"));
		chooser.setSelectedFile(new File(controller.getScore().getScoreName()));
		
		if (writer == null) {
			if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (!file.getPath().endsWith(".ht3"))
					file = new File(file.getPath() + ".ht3");
				if (file.exists()) {
					int res = JOptionPane.showConfirmDialog(parent,
						Localizer.get(i18n.M_FILE_ALREADY_EXISTS_QUESTION).replace("%FILE%", file.getPath()),
						Localizer.get(i18n.MENU_SAVE_AS), 
						JOptionPane.YES_NO_OPTION);
					if (res != JOptionPane.YES_OPTION)
						return null;
				}
				writer = new HT3ScoreWriter(controller.getScore(), file.getPath());
			}
		}
		
		if (writer != null) {
			try {
				writer.save();
				return writer;
			}
			catch (Exception e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage(
					Localizer.get(i18n.M_ERROR_CREATING_FILE).replace("%FILE%", writer.getFile().getPath()));
			}
		}
		return null;
	}
	
	
	//
	// Cr�ation des ScoreReader et ScoreWriter
	//

	/**
	 * Retourne un ScoreReader permettant de lire le fichier en param�tre
	 */
	public static ScoreReader createScoreReader(Score score, String path) {
		// Fichier .htb (HarmoTab 2.x) 
		if (path.endsWith(".htb")) {
			return new HTBScoreReader(score, path);
		}
		// Fichier .ht3 (HarmoTab 3.x)
		else if (path.endsWith(".ht3")) {
			return new HT3ScoreReader(score, path);
		}
		// Fichier .ht3x (HarmoTab3 Export)
		else if (path.endsWith(".ht3x")) {
			return new Ht3XScoreReader(score, path);
		}
		// Fichier sans extension ou d'extension inconnue, consid�re qu'il s'agit 
		// de la derni�re version du format de fichier
		else {
			return new HT3ScoreReader(score, path);
		}
	}
	
	
	/**
	 * Retourne un FileFilter filtrant tous les fichiers lisible par l'application
	 */
	public static class ReadableScoreFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			String name = file.getName();
			if (name.endsWith(".ht3"))
				return true;
			if (name.endsWith(".htb"))
				return true;
			if (name.endsWith(".ht3x"))
				return true;
			return false;
		}
		
	}
	
	
	/**
	 * Retourne un ScoreWriter permettant d'ecrire le fichier en param�tre
	 */
	public static ScoreWriter createScoreWriter(Score score, String path) {
		// Fichier .ht3 (HarmoTab 3.x)
		if (path.endsWith(".ht3")) {
			return new HT3ScoreWriter(score, path);
		}
		// Fichier sans extension ou d'extension inconnue, retourne null
		else {
			return null;
		}
	}
	
	public static ScoreWriter createScoreWriter(Score score, String path, PerformancesList perfs) {
		if (path.endsWith(".ht3x") && perfs != null) {
			return new Ht3XPerformanceWriter(score, path, perfs);
		}
		// Fichier sans extension ou d'extension inconnue, retourne null
		else {
			return createScoreWriter(score, path);
		}
	}
	
	
}
