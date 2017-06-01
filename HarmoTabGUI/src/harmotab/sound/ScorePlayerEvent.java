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


public class ScorePlayerEvent {
	
	//
	// Constructeurs
	//

	public ScorePlayerEvent(ScorePlayer player) {
		m_player = player;
		m_played = player.getPlayedItem();
		m_position = player.getPosition();
		m_state = player.getState();

	}
	
	public ScorePlayerEvent(ScorePlayer player, byte state, SoundItem played, float position) {
		m_player = player;
		m_played = played;
		m_position = position;
		m_state = state;
	}
	
	
	// 
	// Getters
	// 
	
	public ScorePlayer getScorePlayer() {
		return m_player;
	}
	
	public SoundItem getPlayedItem() {
		return m_played;
	}
	
	public float getPosition() {
		return m_position;
	}
	
	public byte getState() {
		return m_state;
	}
	
	
	//
	// Attributs
	//
	
	protected ScorePlayer m_player = null;
	protected SoundItem m_played;
	protected float m_position = 0;
	protected byte m_state = 0;
	
}
