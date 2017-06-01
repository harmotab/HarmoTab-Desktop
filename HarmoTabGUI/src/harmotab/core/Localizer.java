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

package harmotab.core;

import harmotab.HarmoTabConstants;
import harmotab.desktop.ErrorMessenger;

import java.util.*;
import javax.swing.event.*;


public class Localizer {
	
	//
	// Constructeur
	//
	
	static {
		loadLocale();
		GlobalPreferences.addChangeListener(new PreferencesObserver());
	}
	
	
	// 
	// M�thodes utilitaires
	// 
	
	private static synchronized void loadLocale() {
		try {
			m_resourceBundle = ResourceBundle.getBundle(
					HarmoTabConstants.LOCALIZATION_FOLDER + 
					HarmoTabConstants.LOCALIZATION_FILE_NAME, 
				new Locale(GlobalPreferences.getLanguage()));
		}
		catch (Exception e1) {
			e1.printStackTrace();
			try {
				m_resourceBundle = ResourceBundle.getBundle(
						HarmoTabConstants.LOCALIZATION_FOLDER + 
						HarmoTabConstants.LOCALIZATION_FILE_NAME, 
						new Locale(GlobalPreferences.DEFAULT_LANGUAGE));				
			}
			catch (Exception e2) {
				e2.printStackTrace();
				ErrorMessenger.showErrorMessage("Cannot load resource file " +
						HarmoTabConstants.LOCALIZATION_FOLDER + 
						HarmoTabConstants.LOCALIZATION_FILE_NAME + ".");
				System.exit(1);
			}
		}
	}
	
	
	public static String get(String key) {
		if (m_resourceBundle == null)
			Localizer.loadLocale();
		try {
			return m_resourceBundle.getString(key);
		}
		catch (Exception e) {
			return "<error>";
		}
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	private static class PreferencesObserver implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			loadLocale();
		}
	}
	
	
	//
	// Attributs
	//

	private static ResourceBundle m_resourceBundle = null;
	
}

