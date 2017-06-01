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

import harmotab.core.*;
import harmotab.element.*;
import harmotab.sound.*;
import harmotab.track.*;


public class AccompanimentTrackSoundLayout extends SoundLayout {

	//
	// Constructeur
	//
	
	public AccompanimentTrackSoundLayout(Track track) {
		super(track);
		m_sounds = new SoundSequence();
	}

	
	//
	// Ajout des sons
	//
	
	/**
	 * Ajout des éléments sonnores de la piste
	 */
	@Override
	public void processSoundsPositionning(SoundSequence sounds) {
		// Réinitialisation des attributs
		m_sounds.clear();
		m_currentTime = 0.0f;
		m_trackId = getTrackId();
		m_tempo = (float) getTempo();
		
		// Parcours de tous les élément
		for (Element element : getTrack()) {
			// Ajout des éléments de type accompagnement
			if (element instanceof Accompaniment) {
				add((Accompaniment) element);
			}
			else if (element instanceof Silence) {
				add((Silence) element);
			}
		}

		// Ajout les éléments sonores de la piste à la liste globale
		sounds.addAll(m_sounds);
	}
	
	
	/**
	 * Ajout d'un élément d'accompagnement  
	 */
	private void add(Accompaniment acc) {
		
		for (Duration duration : acc.getRhythmic()) {
			float startTime = m_currentTime;
			float durationTime = (60.0f / m_tempo) * duration.getDuration();
			
			for (Height height : acc.getChord().getHeights()) {
				SoundItem item = new SoundItem(acc, m_trackId, height.getSoundId(), startTime, durationTime);
				m_sounds.add(item);
			}
			
			m_currentTime += durationTime;
		}
		
	}
	
	/**
	 * Ajout d'un silence
	 */
	private void add(Silence silence) {
		float durationTime = (60.0f / m_tempo) * silence.getDuration();
		m_currentTime += durationTime;
	}
	
	
	//
	// Attributs
	//
	
	private SoundSequence m_sounds;
	private float m_currentTime;
	private int m_trackId;
	private float m_tempo;

}
