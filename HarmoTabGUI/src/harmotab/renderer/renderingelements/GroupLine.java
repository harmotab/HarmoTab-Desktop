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

package harmotab.renderer.renderingelements;


public class GroupLine {

	//
	// Constructeur
	//
	
	public GroupLine( float m, float p, int direction, int x1, int x2 ) {
		m_lineCoefficient = m;
		m_originOrdinate = p;
		m_direction = direction;
		m_x1 = x1;
		m_x2 = x2;
	}
	
	
	//
	// Getters / setters
	//
	
	public float getLineCoefficient() {
		return m_lineCoefficient;
	}
	
	public float getOriginOrdinate() {
		return m_originOrdinate;
	}
	
	public int getDirection() {
		return m_direction;
	}
	
	public int getX1() {
		return m_x1;
	}
	
	public int getX2() {
		return m_x2;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	public int getY(int x) {
		return (int) (m_lineCoefficient * (float) x + m_originOrdinate);
	}
	
	public int getX(int y) {
		return (int) (((float) y - m_originOrdinate) / m_lineCoefficient);
	}
	
	
	//
	// Attributs
	//
	
	private float m_lineCoefficient;
	private float m_originOrdinate;
	private int m_direction;
	private int m_x1;
	private int m_x2;
	
}
