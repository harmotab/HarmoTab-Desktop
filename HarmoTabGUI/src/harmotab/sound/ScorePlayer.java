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

import harmotab.element.Element;


/**
 * Lecteur de partition.
 * Joue des donn�es audio en rapport avec une partition.
 */
public abstract class ScorePlayer {
	
	public static final byte CLOSED = 1;
	public static final byte OPENED = 2;
	public static final int NO_TRACK_HIGHLIGHTED = -1;
	
	
	//
	// Constructeur
	//
	
	public ScorePlayer() {
		m_sounds = null;
		setHighlightedTrack(NO_TRACK_HIGHLIGHTED);
		setPlayFromElement(null);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setHighlightedTrack(int trackId) {
		m_highlightedTrack = trackId;
	}
	
	public int getHighlightedTrack() {
		return m_highlightedTrack;
	}
	
	public void setPlayFromElement(Element element) {
		m_playFromElement = element;
	}
	
	public Element getPlayFromElement() {
		return m_playFromElement;
	}
	
	public void setSounds(SoundSequence sounds) {
		m_sounds = sounds;
	}
	
	public SoundSequence getSounds() {
		return m_sounds;
	}

	
	
	//
	// Méthodes abstraites
	//
	
	abstract public void setInstrument(int channel, int instrument);
	abstract public void setTrackVolume(int channel, int volume);
	
	/**
	 * Modification du volume de la ligne (toutes pistes comprises)
	 * 0 <= volume <= 100
	 */
	abstract public void setGlobalVolume(int volume);
		
	abstract public void open();
	abstract public void close();
	abstract public byte getState();
	
	abstract public void play();
	abstract public void pause();
	abstract public void stop();
	
	abstract public boolean isPlaying();
	abstract public boolean isPaused();
	
	/**
	 * Retourne la position lue en secondes.
	 */
	abstract public float getPosition();
	
	/**
	 * Modifie la position lue en affectant le d�but de l'item en param�tre
	 */
	abstract public void setPosition(SoundItem item);
	
	/**
	 * Retourne la dur�e totale de la lecture
	 */
	abstract public float getDuration();
	
	
	abstract public void addSoundPlayerListener(ScorePlayerListener listener);	
	abstract public void removeSoundPlayerListener(ScorePlayerListener listener);
	
	
	//
	// Méthodes utilitaires
	//
	
	/**
	 * Ouverture du lecteur dans un thread
	 */
	public synchronized void asynchronousOpen() {
		new Thread() {
			@Override
			public void run() {
				open();		
			}
		}.start();
	}
	
	/**
	 * Fermeture de lecteur dans un thread
	 */
	public synchronized void asynchronousClose() {
		new Thread() {
			@Override
			public void run() {
				close();		
			}
		}.start();		
	}
	
	
	/**
	 * Lance la lecture depuis l'�l�ment courant
	 */
	public void playFrom() {
		if (m_playFromElement != null) {
			SoundItem item = m_sounds.get(m_playFromElement);
			if (item != null) {
				// Item correspondant � l'�l�ment trouv�, lecture � son d�but
				play();
				setPosition(item);
				return;
			}
		}
		// Element nul ou non trouv�, lecture du d�but
		play();
	}
	
	
	/**
	 * Indique si le lecteur est ouvert
	 */
	
	public boolean isOpenned() {
		return (getState() == ScorePlayer.OPENED);
	}
	

	/**
	 * Retourne le SoundItem le plus proche avant la position de lecture courante.
	 * RMQ: Il serai possible de Conserver l'itérateur entre 2 appels pour ne 
	 * 		pas parcourir la liste à chaque fois. 
	 */
	public SoundItem getPlayedItem() {
		// V�rifie que le lecteur est ouvert
		if (!isOpenned() || m_sounds == null)
			return null;
		
		// Retourne l'item sur la piste s�lectionn�e le plus proche du temps courant
		if (getHighlightedTrack() != ScorePlayer.NO_TRACK_HIGHLIGHTED)
			return m_sounds.at(getHighlightedTrack(), getPosition());
		
		// Si il n'y a pas de piste s�lectionn�e, prend la piste 1 (2em piste)
		// comme r�f�rence
		return m_sounds.at(1, getPosition());
	}
	
	
	//
	// Attributs
	//
	
	private int m_highlightedTrack;
	private Element m_playFromElement = null;
	
	protected SoundSequence m_sounds;
	
}
