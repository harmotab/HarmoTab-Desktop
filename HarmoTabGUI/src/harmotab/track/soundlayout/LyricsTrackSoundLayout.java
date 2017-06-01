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

import harmotab.element.*;
import harmotab.sound.*;
import harmotab.track.*;


public class LyricsTrackSoundLayout extends SoundLayout {
	
	//
	// Constructeur
	//
	
	public LyricsTrackSoundLayout(Track track) {
		super(track);
	}
	
	
	//
	// Ajout des sons
	//
	
	@Override
	public void processSoundsPositionning(SoundSequence sounds) {
		float currentTime = 0.0f;
		int trackId = getTrackId();
		float tempo = (float) getTempo();
		
		// Parcours de tous les élément
		for (Element element : getTrack()) {
			float startTime = currentTime;
			float durationTime = (60.0f / tempo) * element.getDuration();
			
			SoundItem item = new SoundItem(element, trackId, SoundItem.NO_SOUND, startTime, durationTime);
			sounds.add(item);
			currentTime += durationTime;
		}

	}
		
}
