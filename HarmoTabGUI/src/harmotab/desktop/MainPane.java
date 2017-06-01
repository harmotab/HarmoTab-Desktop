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
import javax.swing.border.*;
import harmotab.core.*;


/**
 * Zone contenant la zone d'affichage et d'�dition de la partition et les boite
 * d'outils d'�dition de la partition et de controle du playback.
 */
public class MainPane extends JPanel implements ScoreViewListener {
	private static final long serialVersionUID = 1L;

	public MainPane(ScoreController controller) {
		m_hasTopBorder = false;
		m_hasBottomBorder = false;
		m_hasRightBorder = true;
		
		m_scorePane = new ScorePane(controller);
		ScoreView scoreView = m_scorePane.getScoreView();
		ScoreScrollBar scrollBar = new ScoreScrollBar(scoreView);
		EditionToolBar scoreEditionToolBar = new EditionToolBar(controller);
		ScorePlaybackToolBar scorePlaybackToolBar = new ScorePlaybackToolBar(controller, scoreView);
		
		m_corePane = new JPanel(new BorderLayout());
		m_corePane.add(m_scorePane, BorderLayout.CENTER);
		m_corePane.setBackground(Color.DARK_GRAY);
		
		setLayout(new BorderLayout());
		
		add(m_corePane, BorderLayout.CENTER);
		add(scrollBar, BorderLayout.EAST);
		add(scoreEditionToolBar, BorderLayout.NORTH);
		add(scorePlaybackToolBar, BorderLayout.SOUTH);

		scoreView.addScoreViewListener(this);
	}


	/**
	 * Ajoute une bordure en haut ou en bas de la partition pour simuler les 
	 * bordures d'une page.
	 * Enl�ve �galement la bordure � droite si la zone d'affichage est trop 
	 * etroite pour l'affichage de la partition.
	 * RMQ: pas de modification de la marge en bas
	 */
	@Override
	public void onScoreViewChanged(ScoreView scoreView) {
		boolean resetBorder = false;
		
		//FIXME: la 2em condition est un hack pour éviter à la partition de 
		// "sauter" si l'affichage est à la limite
		if (scoreView.getViewOffset() == 0 && m_scorePane.getSoundPlayer().isPlaying() == false) { 
			if (m_hasTopBorder == false)
				resetBorder = true;
			m_hasTopBorder = true;
		}
		else {
			if (m_hasTopBorder == true)
				resetBorder = true;
			m_hasTopBorder = false;
		}
		
		//if (scoreView.getScoreRenderer().getPageWidth() > m_scorePane.getWidth()) {
		if (scoreView.getViewWidth() > m_scorePane.getWidth()) {
			if (m_hasRightBorder == true)
				resetBorder = true;
			m_hasRightBorder = false;
		}
		else {
			if (m_hasRightBorder == false)
				resetBorder = true;
			m_hasRightBorder = true;
		}

		if (resetBorder == true) {
			m_corePane.setBorder(
				new EmptyBorder(m_hasTopBorder ? 30 : 0, 
				30, 
				m_hasBottomBorder ? 30 : 0, 
				m_hasRightBorder ? 30 : 0));
		}
	}
	
	
	//
	// Attributs
	//
	
	private JPanel m_corePane = null;
	private ScorePane m_scorePane = null;
	private boolean m_hasTopBorder;
	private boolean m_hasBottomBorder;
	private boolean m_hasRightBorder;
}
