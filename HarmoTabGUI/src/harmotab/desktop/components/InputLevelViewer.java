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

import harmotab.sound.Recorder;
import harmotab.sound.RecorderException;

import javax.swing.JProgressBar;


/**
 * Composant de visualisation du niveau d'une source sonore.
 */
public class InputLevelViewer extends JProgressBar {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//
	
	public InputLevelViewer(Recorder recorder) {
		super(JProgressBar.HORIZONTAL, 0, 100);
		m_recorder = recorder;
		m_run = false;
	}
	
	
	//
	// Activation / d�sactivation du contr�le
	//
	
	Thread inputLevelViewerThread;
	
	public void start() {
		m_run = true;
		try {
			m_recorder.open();
			m_recorder.startMonitoring();
			
			inputLevelViewerThread = new InputLevelViewerThread();
			inputLevelViewerThread.start();
		}
		catch (RecorderException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		m_run = false;
		try {
			inputLevelViewerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			m_recorder.stop();
			m_recorder.close();
		}
		catch (RecorderException e) {
			e.printStackTrace();
		}
	}
	
	
	//
	// Thread de lecture du volume en entr�e
	//
	
	private class InputLevelViewerThread extends Thread {
		@Override
		public void run() {
			try {
				while (m_run && m_recorder.isOpenned() && m_recorder.isRunning()) {
					int value = (int) (m_recorder.getLevel() * 100);
					setValue(value);
					try {
						Thread.sleep(20);
					} 
					catch (InterruptedException e) {}
				}
			}
			catch (RecorderException e) {
				e.printStackTrace();
			}
		}
	}
		
	
	//
	// Attributs
	//
	
	protected Recorder m_recorder;
	protected boolean m_run;
	
}
