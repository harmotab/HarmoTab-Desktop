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

package harmotab.harmonica;

import harmotab.core.Localizer;
import harmotab.core.i18n;


/**
 * Type d'harmonica (diatonique ou chromatique)
 */
public class HarmonicaType {

	public final static String DIATONIC_STR = "diatonic";
	public final static String CHROMATIC_STR = "chromatic";
	public final static String OTHER_STR = "other";

	
	//
	// Type d'harmonica possibles
	//
	public static final HarmonicaType DIATONIC = new HarmonicaType(DIATONIC_STR);
	public static final HarmonicaType CHROMATIC = new HarmonicaType(CHROMATIC_STR);
	public static final HarmonicaType OTHER = new HarmonicaType(OTHER_STR);
	
	
	//
	// M�thodes statiques
	//

	/**
	 * Retourne le type d'harmonica correspondant au nom traduit dans la langue 
	 * courante
	 */
	public static HarmonicaType parseLocalizedName(String localizedName) {
		if (localizedName.equals(DIATONIC.getLocalizedName()))
			return DIATONIC;
		else if (localizedName.equals(CHROMATIC.getLocalizedName()))
			return CHROMATIC;
		else if (localizedName.equals(OTHER.getLocalizedName()))
			return OTHER;
		return null;
	}
	
	/**
	 * Retourne le type d'harmonica correspondant � l'identifiant du type 
	 * d'harmonica.
	 */
	public static HarmonicaType parseHarmonicaType(String id) {
		if (id.equals(DIATONIC.toString()))
			return DIATONIC;
		else if (id.equals(CHROMATIC.toString()))
			return CHROMATIC;
		else if (id.equals(OTHER.toString()))
			return OTHER;
		return null;
	}
	
	/**
	 * Retourne la liste de tous les types d'harmonica traduits dans la langue 
	 * courante
	 */
	public static String[] getLocalizedNamesList() {
		return new String[] {
			DIATONIC.getLocalizedName(),
			CHROMATIC.getLocalizedName(),
			OTHER.getLocalizedName()
		};
	}
	
	
	//
	// Constructeur
	//
	
	private HarmonicaType(String typeString) {
		m_typeString = typeString;
		
		if (typeString.equals(DIATONIC_STR)) {
			m_localizedName = Localizer.get(i18n.N_DIATONIC);
		}
		else if (typeString.equals(CHROMATIC_STR)) {
			m_localizedName = Localizer.get(i18n.N_CHROMATIC);
		}
		else if (typeString.equals(OTHER_STR)) {
			m_localizedName = Localizer.get(i18n.N_OTHER);
		}
	}
	
	
	//
	// M�thodes d'instance
	//
	
	@Override
	public String toString() {
		return m_typeString;
	}
	
	public String getLocalizedName() {
		return m_localizedName;
	}
	
	public boolean hasPiston() {
		return (this == CHROMATIC);
	}
	
	
	//
	// Attributs
	//
	
	private String m_typeString = null;
	private String m_localizedName = null;
	
}
