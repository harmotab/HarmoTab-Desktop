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

package harmotab.desktop;

import java.util.ArrayList;
import java.util.prefs.Preferences;


/**
 * Gestion des "fichiers r�cents" affich�s dans le menu d'ouverture de 
 * partitions
 */
public class RecentFilesManager {
	
	/**
	 * Nombre maximal de fichiers r�cents enregistr�s dans la liste.
	 */
	public static final int MAX_NUMBER_OF_RECENT_FILES = 10;
	
	/**
	 * Nom sous lequel sont enregistr�s les fichiers r�cents dans les 
	 * pr�f�rences
	 */
	private static final String RECENT_FILE = "HT_RECENT_FILE_";

	

	//
	// Constructeur (singleton)
	//
	
	private RecentFilesManager() {
		m_list = new ArrayList<String>();
		load();
	}
	
	public static synchronized RecentFilesManager getInstance() {
		if (m_instance == null) {
			m_instance = new RecentFilesManager();
		}
		return m_instance;
	}
	
	
	//
	// M�thodes de gestion des fichiers r�cents
	//
	
	/**
	 * Ajoute un fichier � la liste des fichiers r�cemment ouverts.
	 * Si le fichier y est d�j�, le postionne en premier.
	 */
	public void addRecentFile(String filepath) {
		if (m_list.contains(filepath)) {
			m_list.remove(filepath);
		}
		m_list.add(0, filepath);
		if (m_list.size() > MAX_NUMBER_OF_RECENT_FILES) {
			m_list.remove(m_list.size()-1);
		}
		save();
	}
	
	/**
	 * Retourne la liste des fichiers r�cemment ouverts ordonn� de telle sorte 
	 * que les plus r�cemment ouverts soient en premier.
	 */
	public ArrayList<String> getRecentFiles() {
		return m_list;
	}
	
	
	//
	// Enregistrement / lecture des donn�es
	//	
	
	/**
	 * Sauvegarde la liste des fichiers r�cemment ouverts
	 */
	private void save() {
		Preferences prefs = Preferences.userRoot();
		
		int fileIndex = 0;
		for (String path : m_list) {
			prefs.put(RECENT_FILE + fileIndex, path);
			fileIndex++;
		}
	}
	
	/**
	 * Cr�er la liste des fichiers r�cemment ouverts depuis la derni�re 
	 * sauvegarde.
	 */
	private void load() {
		Preferences prefs = Preferences.userRoot();
		m_list.clear();
		
		for (int i = 0; i < MAX_NUMBER_OF_RECENT_FILES; i++) {
			String current = prefs.get(RECENT_FILE + i,  null);
			if (current != null) {
				m_list.add(current);
			}
		}
	}
	
	/**
	 * Remise � z�ro de la liste des fichiers ouverts r�cemment
	 */
	public void reset() {
		Preferences prefs = Preferences.userRoot();
		for (int i = 0; i < MAX_NUMBER_OF_RECENT_FILES; i++) {
			String current = prefs.get(RECENT_FILE + i,  null);
			prefs.remove(current);
		}
		m_list.clear();

	}
	
	
	//
	// Attributs
	//
	
	private static RecentFilesManager m_instance = null;
	private ArrayList<String> m_list = null;
	
}
