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

import harmotab.core.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


public class PngFileWriter extends ScoreWriter {
	
	//
	// Constructeur
	//

	public PngFileWriter(Score score, String path, int width, int height) {
		super(score, path);
		m_width = width;
		m_height = height;
	}

	
	//
	// Ecriture
	//
	
	@Override
	protected void write(Score score, File file) throws IOException {
		
		// Cr�ation des images
		ImagesListView view = new ImagesListView(score, m_width, m_height);
		Collection<BufferedImage> images = view.createImages();

		// V�rification de l'existence des fichiers en sortie
		boolean exists = false;
		for (int i = 0; i < images.size(); i++) {
			File imageFile = getFile(file, i);
			if (imageFile.exists())
				exists = true;
		}
		
		if (exists == true) {
			int result = JOptionPane.showConfirmDialog(
				null, 
				Localizer.get(i18n.M_OVERWRITE_EXISTING_PNG_QUESTION), 
				Localizer.get(i18n.ET_PNG_EXPORT),
				JOptionPane.YES_NO_OPTION);
			if (result != JOptionPane.YES_OPTION)
				return ;
		}
		
		// Ecriture des fichiers en sortie
		int index = 0;
		for (BufferedImage img : images) {
			ImageIO.write(img, "PNG", getFile(file, index));
			index++;
		}
		
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	private File getFile(File reference, int index) {
		String path = reference.getAbsoluteFile() + (index > 0 ? "_" + index : "");
		if (! path.toLowerCase().endsWith(".png")) {
			path += ".png";
		}
		return new File(path);
	}
	
	
	//
	// Attributs
	//
	
	private int m_width;
	private int m_height;

}

 