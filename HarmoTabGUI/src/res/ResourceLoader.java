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

package res;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;


public class ResourceLoader {

	//
	// Constructeur
	//
	
	private ResourceLoader() {
		m_cache = new HashMap<String, Image>();
	}
	
	public static synchronized ResourceLoader getInstance() {
		if (m_instance == null)
			m_instance = new ResourceLoader();
		return m_instance;
	}
	
	
	//
	// Méthodes publiques
	//
	
	public Image loadImage(String path) {
		Image image = null;
		
		// Retourne l'image si elle est pr�sente dans le cache
		if (m_cache.containsKey(path)) {
			return m_cache.get(path);
		}
		
		// Chargement de l'image � partir d'un fichier
		try {
			java.net.URL url = getClass().getResource(path);
			if (url == null)
				throw new IOException();
			image = ImageIO.read(url);
		} catch (IOException e) {
			System.err.println("Cannot read file " + path + ".");
			e.printStackTrace();
		}
		
		// Ajoute l'image au cache
		if (image != null) {
			m_cache.put(path, image);
		}
		
		// Retourne l'image charg�e
		return image;
	}
	
	
	//
	// Attributs
	//
	
	private static ResourceLoader m_instance = null;
	private static Map<String, Image> m_cache = null;
	
}
