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

package rvt.util.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;


public class GridBagUtilities {
		
	public static GridBagConstraints getConstraints(int gridx, int gridy) {
		GridBagConstraints c = createNewConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		return c;
	}
	
	public static GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth, int gridheight) {
		GridBagConstraints c = createNewConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		return c;
	}
	
	public static GridBagConstraints getConstraints(int gridx, int gridy, double weightx, double weighty) {
		GridBagConstraints c = createNewConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.weightx = weightx;
		c.weighty = weighty;	
		return c;
	}
	
	public static GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
		GridBagConstraints c = createNewConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.weightx = weightx;
		c.weighty = weighty;	
		return c;
	}

	
	public static void setDefaultPositionning(int anchor, int fill) {
		m_constraints.anchor = anchor;
		m_constraints.fill = fill;
	}
	
	public static void setDefaultPadding(Insets insets, int ipadx, int ipady) {
		m_constraints.insets = insets;
		m_constraints.ipadx = ipadx;
		m_constraints.ipady = ipady;
	}
		
	public static void setDefaultWeight(double weightx, double weighty) {
		m_constraints.weightx = weightx;
		m_constraints.weighty = weighty;		
	}
	
	
	private static GridBagConstraints createNewConstraints() {
		return (GridBagConstraints) m_constraints.clone();
	}
	
	private static GridBagConstraints m_constraints = new GridBagConstraints();
}
