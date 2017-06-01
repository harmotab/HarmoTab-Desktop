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

package harmotab.sound;

import java.util.ArrayList;


/**
 * 
 */
public abstract class Recorder {

	public abstract void open() throws RecorderException;
	public abstract void close() throws RecorderException;
	public abstract void start() throws RecorderException;
	public abstract void startMonitoring() throws RecorderException;
	public abstract void stop() throws RecorderException;
	
	public abstract void save(String path) throws RecorderException;
	
	public abstract float getLevel() throws RecorderException;
	public abstract float getPositionSec() throws RecorderException;
	public abstract boolean isOpenned() throws RecorderException;
	public abstract boolean isRunning() throws RecorderException;
	

	//
	// Gestion des �v�nements
	//
	
	protected void fireRecorderOpened() {
		for (RecorderListener listener : m_listeners) {
			listener.onRecorderOpenned();
		}		
	}
	
	protected void fireRecorderClosed() {
		for (RecorderListener listener : m_listeners) {
			listener.onRecorderClosed();
		}
	}
	
	protected void fireRecordingStarted() {
		for (RecorderListener listener : m_listeners) {
			listener.onRecordingStarted();
		}
	}
	
	protected void fireRecordingStopped() {
		for (RecorderListener listener : m_listeners) {
			listener.onRecordingStopped();
		}
	}
	
	protected void fireMonitoringStarted() {
		for (RecorderListener listener : m_listeners) {
			listener.onMonitoringStarted();
		}		
	}
	
	protected void fireMonitoringStopped() {
		for (RecorderListener listener : m_listeners) {
			listener.onMonitoringStopped();
		}
	}
		
	public void addRecorderListener(RecorderListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeRecorderListener(RecorderListener listener) {
		m_listeners.remove(listener);
	}
	
	
	//
	// Attributs
	//
	
	protected final ArrayList<RecorderListener> m_listeners = new ArrayList<RecorderListener>();
	
}
