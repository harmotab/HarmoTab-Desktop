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

package harmotab.track.layout;

import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.renderer.renderingelements.*;
import harmotab.track.*;


/**
 * Positionnement des �l�ments d'une piste de type LyricsTrack.
 */
public class LyricsTrackLayout extends TrackLayout {

	//
	// Constructeur
	//
	
	public LyricsTrackLayout(Track track) {
		super(track);
		m_lyricsTrack = (LyricsTrack) track;
	}
	
	
	//
	// Surcharges des méthodes de TrackLayout
	//

	@Override
	public int getTrackHeight() {
		return LYRICS_TRACK_HEIGHT;
	}
	
	@Override
	public int getLayoutRound() {
		return m_lyricsTrack.getLinkedTrack().getTrackLayout().getLayoutRound() + 1;
	}
		
	
	//
	// Positionnement des éléments de la piste
	//

	/**
	 * Effectue le positionnement des �l�ments de la piste
	 */
	@Override
	public void processElementsPositionning(LocationList locations, int areaWidth, float scoreDuration) {
		LocationList localLocations = new LocationList();
		int linkedTrackId = m_track.getScore().getTrackId(m_lyricsTrack.getLinkedTrack());
		TimeScale timeScale = new TimeScale(locations, linkedTrackId);
		
		int trackId = getTrackId();
		float currentTime = 0;
		int currentLine = 1;
		
		// Parcours les éléments de la piste et les positionne en fonction de leur temps
		for (Element element : m_track) {
			// Recherche de la position
			TimeScale.TimePoint startTimePoint = timeScale.getLastPointAt(currentTime);
			TimeScale.TimePoint endTimePoint = timeScale.getFirstPointAt(currentTime + element.getDuration());
			
			if (startTimePoint != null) {
				int startTimeLine = startTimePoint.getLine();
				int endTimeLine = endTimePoint != null ? endTimePoint.getLine() : startTimeLine;
				// Largeur temporaire, déterminée en fonction de la position 
				// de l'élément ou de la barre de mesure suivante
				int x1 = startTimePoint.getX();
				int width = areaWidth - x1;
				if (endTimePoint != null && startTimeLine == endTimeLine)
					width = endTimePoint.getX() - x1;

				// Création et ajout du LocationItem du texte
				LocationItem lyricsItem = LocationItem.newFromArea(element, 
					x1, 0, width, getTrackHeight(),			// x, y, width, height
					trackId, startTimeLine, currentTime,	// trackId, line, time
					0);										// extra
				localLocations.add(lyricsItem);
				
				// Ajout d'items temporaires si l'�l�ment est sur plusieurs lignes
				if (endTimePoint != null) {
					for (int line = startTimeLine+1; line <= endTimeLine; line++) {
						width = (line == endTimeLine ? endTimePoint.getX() : areaWidth);
						LocationItem tempItem = LocationItem.newFromArea(element, 
								0, 0, width, getTrackHeight(),		// x, y, width, height
								trackId, line, currentTime,			// trackId, line, time
								0);									// extra
						tempItem.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
						localLocations.add(tempItem);
					}
				}
				
				// Prise en compte de la ligne de fin d'affichage de l'�l�ment comme ligne courante
				currentLine = endTimeLine;
				
			}
			else {
				System.err.println("LyricsTrackLayout::processElementsPositionning: no location found for " + element);
			}
			
			// Prend en compte l'élément ajouté pour le passage à l'élément suivant
			currentTime += element.getDuration();
			
		}
		
		// Ajout du EmptyArea pour terminer la ligne cournate
		TimeScale.TimePoint endPoint = timeScale.getFirstPointAt(currentTime);
		int emptyAreaX = (endPoint != null ? endPoint.getX() : 0);
		LocationItem emptyArea = LocationItem.newFromArea(new EmptyArea(m_track), 
				emptyAreaX, 0, areaWidth - emptyAreaX, getTrackHeight(),	// x, y, width, height 
				trackId, currentLine,	currentTime,						// trackId, line, time
				0);															// extra
		emptyArea.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
		localLocations.add(emptyArea);
		
		// Ajout des EmptyArea pour couvrir les lignes suppl�mentaires
		int maxLine = timeScale.getNumberOfLines();
		for (currentLine++ ; currentLine <= maxLine; currentLine++) {
			currentTime = timeScale.getLineStart(currentLine).getTime();
			emptyArea = LocationItem.newFromArea(new EmptyArea(m_track), 
					0, 0, areaWidth, getTrackHeight(),	// x, y, width, height 
					trackId, currentLine, currentTime,	// trackId, line, time
					0);									// extra
			emptyArea.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, true);
			localLocations.add(emptyArea);	
		}
		
		// Ajout des éléments de la piste aux éléments de la partition
		locations.add(localLocations);
		
	}
	
	
	//
	// Attributs
	//
	
	protected static final int LYRICS_TRACK_HEIGHT = 25;
	private LyricsTrack m_lyricsTrack = null;

}
