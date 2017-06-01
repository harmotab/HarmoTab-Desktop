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

package harmotab.desktop.browser;

import harmotab.HarmoTabConstants;
import harmotab.core.*;
import harmotab.desktop.ErrorMessenger;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.event.*;


public class SoftwarePane extends Browser {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//

	public SoftwarePane() {
		
		// Construction des composants
		m_htmlContent = new JEditorPane();
		m_htmlContent.setOpaque(false);
		m_htmlContent.setEditable(false);
		
		// Ajout des composants � l'interface
		setLayout(new BorderLayout());
		add(m_htmlContent, BorderLayout.CENTER);
	
		// Enregistrement des listeners
		m_htmlContent.addHyperlinkListener(new LinkActionObserver());
		
		// Affichage de l'interface
		setOpaque(false);
		m_htmlContent.setText(Localizer.get(i18n.ET_LOADING));
		m_htmlContent.setPreferredSize(new Dimension(150, 300));
		new GetHtmlContentAction().start();
	}
	
	
	//
	// R�cup�re le contenu d'internet
	//
	
	private class GetHtmlContentAction extends Thread {
		@Override
		public void run() {
			try {
				m_htmlContent.setPage(HarmoTabConstants.HT_WELCOME_PAGE);
			} catch (IOException e) {
				remove(m_htmlContent);
				repaint();
			}
		}
	}
	
	
	private class LinkActionObserver implements HyperlinkListener {
		@Override
		public void hyperlinkUpdate(HyperlinkEvent event) {
			HyperlinkEvent.EventType eventType = event.getEventType();
			if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					Desktop.getDesktop().browse(new URI(event.getURL().toString()));
				} catch (IOException e) {
					e.printStackTrace();
					ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_NO_DEFAULT_WEB_BROWSER));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	private JEditorPane m_htmlContent = null;
	
}
