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

package harmotab.desktop;


import java.awt.*;


public class SmoothLayout implements LayoutManager2 {
	private Component northComponent = null;
	private Component southComponent = null;
	private Component eastComponent = null;
	private Component westComponent = null;
	
	public static final String NORTH = "North";
	public static final String SOUTH = "South";
	public static final String EAST = "East";
	public static final String WEST = "West";
	public static String lastPositionning = null;
	

	public SmoothLayout() {
	}

	@Override
	public void addLayoutComponent(String string, Component component) {
		if (string.equals(NORTH))
			northComponent = component;
		else if (string.equals(SOUTH))
			southComponent = component;
		else if (string.equals(EAST))
			eastComponent = component;
		else if (string.equals(WEST))
			westComponent = component;
		lastPositionning = string;
	}

	@Override
	public void layoutContainer(Container container) {
		if (northComponent != null) {
			Dimension dim = northComponent.getPreferredSize();
			northComponent.setBounds(0, 0, container.getWidth(), dim.height);
		}
		else if (southComponent != null) {
			Dimension dim = southComponent.getPreferredSize();
			southComponent.setBounds(0, container.getHeight()-dim.height, container.getWidth(), dim.height);
		}
		else if (westComponent != null) {
			Dimension dim = westComponent.getPreferredSize();
			westComponent.setBounds(0, 0, dim.width, container.getHeight());
		}
		else if (eastComponent != null) {
			Dimension dim = eastComponent.getPreferredSize();
			eastComponent.setBounds(container.getWidth()-dim.width, 0, dim.width, container.getHeight());
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container container) {
		return new Dimension(0, 0);
	}

	@Override
	public Dimension preferredLayoutSize(Container container) {
		return new Dimension(0, 0);
	}

	@Override
	public void removeLayoutComponent(Component component) {
		if (component == northComponent)
			northComponent = null;
		else if (component == westComponent)
			westComponent = null;
		else if (component == southComponent)
			southComponent = null;
		else if (component == eastComponent)
			eastComponent = null;
	}

	@Override
	public void addLayoutComponent(Component component, Object object) {
		if (object != null)
			addLayoutComponent((String)object, component);
	}

	@Override
	public float getLayoutAlignmentX(Container container) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container container) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container container) {
	}

	@Override
	public Dimension maximumLayoutSize(Container container) {
		return null;
	}
	
}
