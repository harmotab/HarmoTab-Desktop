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


import harmotab.core.*;
import java.awt.event.*;
import javax.swing.*;



public class ScoreScrollBar extends JScrollBar implements AdjustmentListener, ScoreViewListener {
	private static final long serialVersionUID = 1L;
	
	public final static int INCREMENT_VALUE = 30;


	public ScoreScrollBar(ScoreView scoreView) {
		m_scoreView = scoreView;
			
		setMinimum(0);
		setMaximum(0);
		setValue(0);
		setBlockIncrement(INCREMENT_VALUE*5);
		
		addAdjustmentListener(this);
		m_scoreView.addScoreViewListener(this);
	}

	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent event) {
		int curOffset = m_scoreView.getViewOffset();
		int newOffset = getValue() * INCREMENT_VALUE;
		
		if (curOffset != newOffset) {
			m_scoreView.translateView(newOffset - curOffset);
		}
	}

	
	@Override
	public void onScoreViewChanged(ScoreView scoreView) {
		setMaximum(scoreView.getMaxOffset()/INCREMENT_VALUE);
		setValue(scoreView.getViewOffset()/INCREMENT_VALUE);
	}
	
	
	private ScoreView m_scoreView;

}
