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

import harmotab.HarmoTabConstants;
import harmotab.core.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;


/**
 * Affichage de la fen�tre "A propos" du logiciel.
 */
public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public AboutDialog () {
		super((Window) null, Localizer.get(i18n.ET_ABOUT));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		m_window = this;
		
		// Cr�ation des composants
		JPanel descriptionPane = new JPanel();
		descriptionPane.setLayout(new BoxLayout(descriptionPane, BoxLayout.PAGE_AXIS));
		JLabel descriptionLabel = new JLabel(
				"<html>" +
				"	<h2>" + Localizer.get(i18n.M_ABOUT_SOFTWARE_TITLE).replace("%VERSION%", HarmoTabConstants.getVersionString()) + "</h2>" +
				"	<p>" + Localizer.get(i18n.M_ABOUT_SOFTWARE_DESC) + "</p>" +
				"	<p>&nbsp;</p>" +
				"<html>");
		JLabel linkLabel = new JLabel(
				"<html>" +
				"	<p>" + Localizer.get(i18n.M_ABOUT_WEBSITE_DESC) + "<br>" +
				"	<a href=\"" + Localizer.get(i18n.URL_WEBSITE) + "\">" + Localizer.get(i18n.URL_WEBSITE) + "</a></p>" +
				"<html>");
		descriptionPane.add(descriptionLabel);
		descriptionPane.add(linkLabel);
		
		JLabel iconLabel = new JLabel(GuiIcon.getIcon(GuiIcon.HARMOTAB_ICON_64));
		JPanel iconPane = new JPanel(new FlowLayout(FlowLayout.LEADING));
		iconPane.add(iconLabel);
		
		JButton closeButton = new JButton(Localizer.get(i18n.ET_CLOSE_DIALOG));
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonPane.add(closeButton);

		// Ajout des composant � la fen�tre
		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout(20, 20));
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		contentPane.add(iconPane, BorderLayout.WEST);
		contentPane.add(descriptionPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		
		// Enregistrement des listeners
		closeButton.addActionListener(new CloseAction());
		linkLabel.addMouseListener(new WebSiteAction());
	
		// Affichage de la fen�tre
		pack();
		setLocationRelativeTo(getParent());
		setVisible(true);
	}
	
	
	//
	// Attributs
	//
	
	private Window m_window = null;
	
	
	//
	// Actions
	//
	
	// Action du bouton "Close"
	private class CloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			dispose();
		}	
	}
	
	// Action sur le label de liens vers le site web
	private class WebSiteAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent event) {
			try {
				Desktop.getDesktop().browse(new URI(Localizer.get(i18n.URL_WEBSITE)));
			} catch (IOException e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage(m_window, Localizer.get(i18n.M_NO_DEFAULT_WEB_BROWSER));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void mouseEntered(MouseEvent event) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
		@Override
		public void mouseExited(MouseEvent event) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		@Override public void mousePressed(MouseEvent event) {}
		@Override public void mouseReleased(MouseEvent event) {}
		
	}
	
}
