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

import java.io.*;
import java.nio.channels.*;
import javax.sound.sampled.*;


/**
 * Enregistrement d'un sample au format PCM.
 */
public class PcmRecorder extends Recorder {
	
	//
	// Constructeur
	//
	
	public PcmRecorder() {
		m_running = false;
		m_openned = false;
		m_lastLevel = 0;
		
//		m_audioFormat = new AudioFormat(
//				44100, 	// Sample Rate
//				16, 	// Sample Size In Bits
//				2, 		// Channels
//				true, 	// Signed
//				true	// Big Endian
//			);
		
		m_audioFormat = new AudioFormat(
				22050, 	// Sample Rate
				16,	 	// Sample Size In Bits
				1, 		// Channels
				true, 	// Signed
				true	// Big Endian
			);
		
	}
	
	
	//
	// Getters / setters
	//
	
	@Override
	public boolean isRunning() throws RecorderException {
		return m_running;
	}
	
	@Override
	public boolean isOpenned() throws RecorderException {
		return m_openned;
	}
	
	
	//
	// Impl�mentation de l'interface Recorder
	//
	
	/**
	 * Pr�paration de l'entr�e
	 */
	@Override
	public void open() throws RecorderException {
		if (m_openned == true) {
			throw new IllegalStateException("Recorder already openned.");
		}
	
		// On cr�e un descripteur de ligne en pr�cisant le type de ligne qu'on 
		// veut (on enregistre donc TargetDataLine) et le format audio construit 
		// pr�c�demment. 
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, m_audioFormat);
		// On teste �galement si le syst�me supporte le type de ligne que l'on 
		// souhaite obtenir. 
		if (!AudioSystem.isLineSupported(info)) {
		  System.err.println("Audio Format specified is not supported");
		  return;
		}
		
		// On r�cup�re le DataLine ad�quat
		try {
			m_line = (TargetDataLine) AudioSystem.getLine(info);
		}
		catch (LineUnavailableException e) { 
			e.printStackTrace();
			return;
		}

		// Indique l'enregistreur comme �tant ouvert
		m_openned = true;
		m_running = false;
		fireRecorderOpened();
	}
	
	
	/**
	 * D�marrage de l'enregistrement
	 */
	@Override
	public void start() throws RecorderException {
		if (m_openned == false)
			throw new IllegalStateException("Recorder not openned.");
		if (m_running == true)
			throw new IllegalStateException("Recorder already running.");
		
		try {
			createNewCacheFile();
		}
		catch (IOException e) {
			throw new RecorderException(e);
		}
		
		// Ouverture de la ligne avec le format audio sp�cifi�
		try {
			m_line.open(m_audioFormat);
		} 
		catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}

		// D�marrage de l'enregistrement
		m_line.flush();
		m_line.start();
		new RecordingThread().start();
	}


	/**
	 * D�marrage de l'enregistrement
	 */
	@Override
	public void startMonitoring() throws RecorderException {
		if (m_openned == false)
			throw new IllegalStateException("Recorder not openned.");
		if (m_running == true)
			throw new IllegalStateException("Recorder already running.");
		
		// Ouverture de la ligne avec le format audio sp�cifi�
		try {
			m_line.open(m_audioFormat);
		}
		catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}

		// D�marrage du monitoring
		m_line.flush();
		m_line.start();
		new MonitoringThread().start();
	}

	
	/**
	 * Arr�t de l'enregistrement ou du monitoring
	 */
	@Override
	public void stop() {
		m_line.stop();
		m_line.drain();
		m_line.close();
		m_running = false;
		
		try {
			m_runningThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Sauvegrade le flux enregistr� dans un fichier
	 */
	@Override
	public void save(String path) {
		if (m_running == true)
			throw new IllegalStateException("Recorder currently recording");
		if (m_cacheFile == null)
			throw new IllegalStateException("No sample recorded.");

		// Copie du fichier
		FileChannel in = null;
		FileChannel out = null;
		
		try {
			in = new FileInputStream(m_cacheFile).getChannel();
			out = new FileOutputStream(path).getChannel();

			// Copie depuis le in vers le out
			in.transferTo(0, in.size(), out);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	
	/**
	 * Lib�re les ressources mat�rielles
	 */
	@Override
	public void close() {
		if (m_openned == false)
			throw new IllegalStateException("Recorder not openned.");
		
		if (m_running) {
			stop();
		}
		if (m_cacheFile != null) {
			m_cacheFile.delete();
		}
		m_openned = false;
		m_running = false;
		fireRecorderClosed();
	}
	
	
	/**
	 * Indique le niveau de l'entr�e lorsque le monitoring est activ�.
	 * Valeur entre -1.0 et 1.0
	 */
	@Override
	public float getLevel() throws RecorderException {
		if (!m_running)
			throw new IllegalStateException("Monitoring not running !");
		return m_lastLevel;
	}
	
	
	/**
	 * Retourne la position courante d'enregistrement en secondes
	 */
	@Override
	public float getPositionSec() throws RecorderException {
		return m_line.getMicrosecondPosition() * 1000000.0f;
	}
	
	
	//
	// Thread d'enregistrement
	//
	
	/**
	 * Enregistrement
	 */
	private class RecordingThread extends Thread {
		@Override
		public void run() {
			m_running = true;
			m_runningThread = this;
			fireRecordingStarted();
			
			AudioFileFormat.Type targetType = AudioFileFormat.Type.AU;
			AudioInputStream audioInputStream = new AudioInputStream(m_line);
			OutputStream output = null;
			try {
				output = new FileOutputStream(m_cacheFile);
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
			try {
				AudioSystem.write(audioInputStream, targetType, output);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			finally {
				try {
					audioInputStream.close();
					output.close();
				}
				catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			
			m_running = false;
			fireRecordingStopped();
		}
	}
	

	//
	// Thread de lecture du niveau d'entr�e
	//

	/**
	 * Calcul du niveau d'entr�e
	 */
	private class MonitoringThread extends Thread {
		private static final int BUFFER_SIZE = 2048;
		
		@Override
		public void run() {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = 0;

			m_running = true;
			m_runningThread = this;
			fireMonitoringStarted();
			
			AudioInputStream audioInputStream = new AudioInputStream(m_line);
			try {
				while ((read = audioInputStream.read(buffer, 0, BUFFER_SIZE)) > 0) {
					// Le niveau est pris comme la moyenne des valeur absolues des valeurs
					float sum = 0;
					for (int i = 1; i < read; i += 2) {
						sum += (float) Math.abs(buffer[i]);
					}
					m_lastLevel = (127f - (sum / (read / 2))) / 127f;
					// Saute les derni�res donn�es acquises pour �tre "temp r�el"
					audioInputStream.skip(audioInputStream.available());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			m_running = false;
			fireMonitoringStopped();
		}
	}
	
	
	//
	// M�thodes priv�es
	//
	
	private void createNewCacheFile() throws IOException {
	    if (m_cacheFile != null) {
        	m_cacheFile.delete();
	    }
	    m_cacheFile = File.createTempFile("ht3-tmp-", ".pcm");
	    m_cacheFile.deleteOnExit();
	}
	
	
	//
	// Attributs
	//
	
	protected AudioFormat m_audioFormat = null;
	protected TargetDataLine m_line = null;
	protected File m_cacheFile = null;
	protected Thread m_runningThread = null;

	protected boolean m_running = false;
	protected boolean m_openned = false;
	protected float m_lastLevel = 0;

}
