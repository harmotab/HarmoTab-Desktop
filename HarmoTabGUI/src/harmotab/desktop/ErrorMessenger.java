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

import java.awt.*;
import javax.swing.*;


/**
 * Gestion de la notification des erreurs de l'application � l'utilisateur
 */
public class ErrorMessenger {
	
	//
	// M�thodes de configuration de la sortie d'erreur
	//
	
	/**
	 * Place l'application en mode graphique
	 */
	public static void setGuiMode() {
		m_guiMode = true;
	}
	
	/**
	 * Place l'application en mode console
	 */
	public static void setConsoleMode() {
		m_guiMode = false;
	}
	
		
	//
	// M�thodes de signalisation d'erreurs
	//

	/**
	 * Affichage d'un message d'erreur.
	 * Ouvre une boite de dialogue d'erreur si l'application est en mode 
	 * graphique, sinon affiche le message sur la sortie d'erreur.
	 */
    public static final void showErrorMessage(String msg){
    	if (m_guiMode == true) {
    		showErrorMessageDialog(DesktopController.getInstance().getGuiWindow(), msg);
    	}
    	else {
    		showConsoleErrorMessage(msg);
    	}
    }

	/**
	 * Affichage d'un message d'erreur.
	 * Ouvre une boite de dialogue d'erreur si l'application est en mode 
	 * graphique, sinon affiche le message sur la sortie d'erreur.
	 */
    public static final void showErrorMessage(Component parent, String msg){
    	showErrorMessageDialog(DesktopController.getInstance().getGuiWindow(), msg);
    }


    //
    // M�thodes priv�es
    //
	
    private static final void showErrorMessageDialog(Component parent, String msg){
    	JOptionPane.showMessageDialog(
    		parent,
    		msg,
    		"HarmoTab",
    		JOptionPane.ERROR_MESSAGE 
    	);
    }
    
    private static final void showConsoleErrorMessage(String msg){
    	System.err.println(msg);
    }
    
    
    //
    // Attributs
    //
    
	private static boolean m_guiMode = true;

}

