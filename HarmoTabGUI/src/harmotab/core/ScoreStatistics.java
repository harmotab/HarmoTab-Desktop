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

package harmotab.core;

import harmotab.desktop.DesktopController;
import harmotab.element.Bar;
import harmotab.renderer.LocationItem;
import harmotab.renderer.LocationList;
import harmotab.sound.SoundSequence;
import harmotab.track.HarmoTabTrack;
import harmotab.track.Track;


/**
 * Calcul de statitistiques
 */
public class ScoreStatistics {

	//
	// Constructeur
	//
	
	public ScoreStatistics(Score score) {
		m_score = score;
		m_soundSequence = new SoundSequence();
		
		for (Track track : m_score) {
			track.getSoundLayout().processSoundsPositionning(m_soundSequence);
		}
		m_soundSequence.mergeRepeats();
		
		m_locations = new LocationList();
		DesktopController.getInstance().getScoreView().getScoreRenderer().layout(m_locations);
	}
	
	
	//
	// M�thodes de calcul de statistiques
	//
	
	public int getTracksCount() {
		return m_score.getTracksCount();
	}
	
	public float getPlaybackDurationSec() {
		return m_soundSequence.getLastTime();
	}
	
	public int getBarsCount() {
		int result = 0;
		Track track = m_score.getTrack(HarmoTabTrack.class, 0);
		int trackIndex = track.getTrackIndex();
		for (LocationItem item : m_locations) {
			if (item.getTrackId() == trackIndex && item.getElement() instanceof Bar) {
				result = Math.max(result, item.getExtra());
			}
		}
		return result;
	}
	
	public int getItemsCount() {
		int result = 0;
		for (Track track : m_score) {
			result += track.size();
		}
		return result;
	}
	
	public int getDisplayedItemsCount() {
		return m_locations.getSize();
	}
	
	
	//
	// Attributs
	//
	
	private Score m_score = null;
	private SoundSequence m_soundSequence = null;
	private LocationList m_locations = null;
	
}
