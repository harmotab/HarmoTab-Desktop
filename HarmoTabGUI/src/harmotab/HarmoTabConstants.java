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

package harmotab;

import harmotab.core.GlobalPreferences;


/**
 * Constantes de configuration du logiciel
 */
public class HarmoTabConstants {
	
	public static final float HT_VERSION = 3.1f;
	public static final String HT_WELCOME_PAGE = 
		"http://www.harmotab.com/infos.php" +
			"?lang=" + GlobalPreferences.getLanguage() + 
			"&vers=" + getVersionString();
	
	public static final String LOCALIZATION_FILE_NAME = "localization";
	public static final String LOCALIZATION_FOLDER = "res.i18n.";
	
	public static final int PLAYER_OBSERVER_REFRESH_PERIOD_MS = 40;
	
	public static final String 	SERIALIZATION_ID_ATTR = "id";
	
	public static final int DEFAULT_SCORE_WIDTH = 900;
	
	public static String getVersionString() {
		return String.valueOf(HT_VERSION);
	}

}
