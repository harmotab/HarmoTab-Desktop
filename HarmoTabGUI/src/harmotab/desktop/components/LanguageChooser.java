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

package harmotab.desktop.components;

import java.util.Locale;
import javax.swing.*;


public class LanguageChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public LanguageChooser(String language) {
	
		// Liste les fichiers de langue
		DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();

//RMQ: code ne fonctionnant pas quand les fichiers properties sont dans un jar
//		Pattern pattern = Pattern.compile( "^" + HarmoTabConstants.LOCALIZATION_FILE_NAME + "_(..).properties$" );
//		File folder = new File(HarmoTabConstants.LOCALIZATION_FOLDER);
//
//		for (String file : folder.list()) {
//			Matcher matcher = pattern.matcher(file);
//			if (matcher.matches()) {
//				String lang = matcher.group(1);
//				Locale locale = new Locale(lang);
//				model.addElement(lang + " - " + locale.getDisplayName());
//			}
//		}
// Passage � un listage statique
		model.addElement("en" + " - " + new Locale("en").getDisplayName());
		model.addElement("fr" + " - " + new Locale("fr").getDisplayName());		
		
		Locale locale = new Locale(language);
		setSelectedItem(language + " - " + locale.getDisplayName());
		
	}
	
	
	//
	// M�thodes utilitaires
	//

	public String getSelectedLanguageIdentifier() {
		if (getSelectedIndex() != -1) {
			String selected = (String) getSelectedItem();
			return selected.substring(0, 2);
		}
		return null;
	}
	
}

