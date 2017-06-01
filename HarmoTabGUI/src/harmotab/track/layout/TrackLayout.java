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

import harmotab.core.Score;
import harmotab.renderer.LocationList;
import harmotab.track.*;


/**
 * Positionne graphiquement les �l�ments d'une piste.
 */
public abstract class TrackLayout {
	
	//
	// Constructeur
	//
	
	public TrackLayout(Track track) {
		setTrack(track);
	}
	
	
	//
	// Getters / setters
	//
	
	public Track getTrack() {
		return m_track;
	}
	
	public void setTrack(Track track) {
		if (track == null)
			throw new NullPointerException();
		m_track = track;
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	public Score getScore() {
		return m_track.getScore();
	}
	
	public int getTrackId() {
		if (getScore() != null)
			return getScore().getTrackId(m_track);
		return -1;
	}
	
	
	/**
	 * Indique à quelle phase du positionnement des pistes les éléments de cette
	 * piste doivent être positionnés : par défaut 0, s'il s'agit d'une piste liée
	 * à une autre piste on doit s'assurer que la piste liée est déjà positionnée,
	 * retourne la valeur 1, si la piste liée est elle même liée, retourne 2, etc.
	 */
	public int getLayoutRound() {
		return 0;
	}

	
	//
	// Méthodes abstraites
	//
	
	/**
	 * Positionne les éléments de la piste.
	 * Les positionnements sont ajoutés à <locations>.
	 */
	abstract public void processElementsPositionning(LocationList locations, int areaWidth, float scoreDuration);
	
	/**
	 * Retourne la hauteur de la piste en pixels.
	 */
	abstract public int getTrackHeight();
	
	
	//
	// Attributs
	//
	
	protected Track m_track;

}
