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

package harmotab.track.soundlayout;

import harmotab.core.Figure;
import harmotab.core.RepeatAttribute;
import harmotab.element.*;
import harmotab.sound.*;
import harmotab.track.*;


public class StaffTrackSoundLayout extends SoundLayout {
	
	private final static float NO_CURRENT_PHRASE = -1.0f;
	public final static float APPOGIATURE_DURATION = 0.1f;
	
	
	//
	// Constructeur
	//
	
	public StaffTrackSoundLayout(Track track) {
		super(track);
	}
	

	/**
	 * Positionnement temporel des notes de la partition.
	 * Parcours tous les éléments de la partitions et ajoute un SoundItem pour 
	 * chaque son devant être joué.
	 */
	@Override
	public void processSoundsPositionning(SoundSequence sounds) {
		m_sounds = sounds;
		
		// Initialisation des attributs
		m_currentTime = 0;
		m_trackId = getTrackId();
		m_phraseStartTime = NO_CURRENT_PHRASE;
		m_tempo = (float) getTempo();
		m_previousSound = new SoundItemGroup();
		m_appogiatureRetractTime = 0;
		
		// Parcours des éléments de la piste
		for (Element element : getTrack()) {		
			if (element instanceof Note) {
				add((Note) element);
			}
			else if (element instanceof Bar) {
				manageBar((Bar) element);
			}
		}
		
	}
	
	
	/**
	 * Ajoute une note à la liste des son à produire.
	 */
	private void add(Note note) {
		// Calcul de la durée de la note en fonction du tempo
		float duration = 0.0f;
		
		// Prise en compte des appogiatures
		if (note.getFigure().getType() == Figure.APPOGIATURE) {
			duration = (60.0f / m_tempo) * APPOGIATURE_DURATION;
			m_appogiatureRetractTime += duration;
		}
		else {
			duration = ((60.0f / m_tempo) * note.getDuration()) - m_appogiatureRetractTime;
			m_appogiatureRetractTime = 0;
		}
		
		// La note est la 2em note d'une liaison
		if (note.isTied() && !m_previousSound.isEmpty()) {
			// Prolonge la durée de la note précédente
			m_previousSound.extend(duration);
			// Ajoute un item de son pour indiquer la lecture de la note suivante de la liaison
			SoundItem followItem = new SoundItem(note, m_trackId, SoundItem.NO_SOUND, m_currentTime, duration);
			m_sounds.add(followItem);
			m_previousSound.add(followItem);
			
		}
		// Note sans attribut particulier
		else {
			int soundId = note.getHeight().getSoundId();
			
			// Gestion des silences, ajoute quand meme un item de son pour savoir
			// quand l'�l�ment est jou�
			if (note.isRest())
				soundId = SoundItem.NO_SOUND;
			
			SoundItem noteSound = new SoundItem(note, m_trackId, soundId, m_currentTime, duration);
			m_sounds.add(noteSound);
			m_previousSound.set(noteSound);
		}
		
		m_currentTime += duration;
	}
	
	
	/**
	 * Gestion des reprises
	 */
	private void manageBar(Bar bar) {
		RepeatAttribute repeat = bar.getRepeatAttribute();
		if (repeat != null) {
			// Fin de phrase
			if (repeat.isEnd()) {
				int times = repeat.getRepeatTimes();
				// Si il y � une r�p�tition
				if (times > 1) {
					if (m_phraseStartTime != NO_CURRENT_PHRASE) {
						m_sounds.addRepeat(new RepeatItem(times, m_phraseStartTime, m_currentTime));
					}
					else {
						System.err.println("StaffTrackSoundLayout:manageBar: No phrase starting found.");
					}
				}
				// Sort de la phrase courante
				m_phraseStartTime = NO_CURRENT_PHRASE;
			}
			
			// D�buts de phrase
			if (repeat.isBeginning())
				m_phraseStartTime = m_currentTime;
		}
	}
	
	
	//
	// Attributs
	//
	
	private SoundSequence m_sounds;
	private float m_currentTime;
	private int m_trackId;
	private float m_phraseStartTime;
	private float m_tempo;
	private SoundItemGroup m_previousSound;
	private float m_appogiatureRetractTime;

}
