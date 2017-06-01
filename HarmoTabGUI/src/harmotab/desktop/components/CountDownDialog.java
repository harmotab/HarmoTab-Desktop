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

import harmotab.desktop.DesktopController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


/**
 * Affichage d'une fen�tre effectuant un compte � rebours
 */
public class CountDownDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public CountDownDialog(String title, String label) {
		setTitle(title);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(DesktopController.getInstance().getGuiWindow());
		
		m_countDownPanel = new CountdownPanel();
		m_cancelButton = new JButton("Cancel");
		m_message = "<html>" + label + "</html>";
		
		m_messageLabel = new JLabel();
		m_messageLabel.setText(m_message.replaceAll("%COUNTDOWN%", "..."));
		
		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout(10, 10));
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.add(m_countDownPanel, BorderLayout.WEST);
		contentPane.add(m_cancelButton, BorderLayout.EAST);
		contentPane.add(m_messageLabel, BorderLayout.CENTER);
		
		m_cancelButton.addActionListener(this);		
		pack();
	}	
	
	/**
	 * Affichage de la fen�tre et d�marrage du compte � rebours
	 */
	public boolean countDown(float steps, float stepDuration) {
		m_countdownSteps = steps;
		m_countdownStepDuration = stepDuration;
		m_cancelled = false;
		
		startCountDown();
		setVisible(true);
		
		return !m_cancelled;
	}

	
	//
	// Ecoute des actions de l'utilisateur
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == m_cancelButton) {
			m_cancelled = true;
		}
	}
	
	
	// 
	// Thread de compte � rebours
	// 
	
	private void startCountDown() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				setPriority(Thread.MIN_PRIORITY);
				long countdownStartTimeMs = System.currentTimeMillis();
				float countdownDurationSec = m_countdownStepDuration * m_countdownSteps;
				long countdownEndTimeMs = countdownStartTimeMs + (long)(countdownDurationSec * 1000.0f);
				float currentStep = 0;
				
				while (!m_cancelled && (currentStep < m_countdownSteps)) {
					long currentTimeMs = System.currentTimeMillis();
					long currentDurationMs = currentTimeMs - countdownStartTimeMs;
					float remainingTimeSec = (countdownEndTimeMs - currentTimeMs) / 1000.0f;
					currentStep = ((float) currentDurationMs / 1000.0f) / m_countdownStepDuration;
					//float fraction = (currentStep - ((int) currentStep));
					float fraction = remainingTimeSec / countdownDurationSec;
					int value = (int) (m_countdownSteps - currentStep) + 1;
					m_countDownPanel.setValue(String.valueOf(value), value, fraction);
					m_messageLabel.setText(m_message.replaceAll("%COUNTDOWN%", String.valueOf((int) remainingTimeSec)));
					yield();
				}
				
				setVisible(false);
			}
		};
		thread.start();
	}
	
	
	// 
	// Attributs
	// 
	
	private JButton m_cancelButton = null;
	private JLabel m_messageLabel = null;
	
	private float m_countdownSteps = 0;
	private String m_message = null;
	private float m_countdownStepDuration = 0;
	private CountdownPanel m_countDownPanel = null;
	private boolean m_cancelled = false;
	
}
