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

package harmotab.track;

import harmotab.core.Score;
import harmotab.io.*;


/**
 * Constructeur de pistes
 */
public class TrackFactory {

	/**
	 * Créer une piste à partir d'un flux sérialisé
	 */
	public static Track create(Score score, ObjectSerializer serializer, SerializedObject object) {
		Track track = null;
	
		if (!object.getObjectType().equals(Track.TRACK_TYPESTR))
			throw new IllegalArgumentException("Object type '" + object.getObjectType() + "' is not a track.");
		
		String type = object.getAttribute("type");
		if (type == null || type.equals(""))
			throw new IllegalArgumentException();
		
		if      (type.equals(StaffTrack.STAFF_TRACK_TYPESTR))					track = new StaffTrack(score);
		else if	(type.equals(HarmoTabTrack.HARMOTAB_TRACK_TYPESTR))				track = new HarmoTabTrack(score);
		else if (type.equals(AccompanimentTrack.ACCOMPANIMENT_TRACK_TYPESTR))	track = new AccompanimentTrack(score, null);
		else if (type.equals(LyricsTrack.LYRICS_TRACK_TYPESTR))					track = new LyricsTrack(score, null);
		else
			System.err.println("TrackFactory::create: Unhandled track type ("+ type +")");
		
		if (track != null)
			track.deserialize(serializer, object);
		
		return track;
	}
	
}
