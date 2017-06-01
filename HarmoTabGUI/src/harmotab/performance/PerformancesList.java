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

package harmotab.performance;

import harmotab.core.HarmoTabObjectEvent;
import harmotab.core.HarmoTabObjectListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Liste d'interpr�tation sonore d'une partition.
 */
public class PerformancesList implements Iterable<Performance>, HarmoTabObjectListener {

	//
	// Constructeur
	//
	
	public PerformancesList() {
		setDefaultPerformance(-1);
	}
		
	
	//
	// Proxy de ArrayList
	//
	
	public int size() {
		return m_list.size();
	}
	
	public void add(Performance perf) {
		m_list.add(perf);
		perf.addObjectListener(this);
		firePerformanceListChanged();
	}
	
	public boolean remove(Performance perf) {
		boolean res = m_list.remove(perf);
		if (res == true) {
			perf.removeObjectListener(this);
			firePerformanceListChanged();
		}
		return res;
	}
	
	@Override
	public Iterator<Performance> iterator() {
		return m_list.iterator();
	}
	
	public int indexOf(Performance performance) {
		return m_list.indexOf(performance);
	}
	
	public Performance get(int index) {
		return m_list.get(index);
	}
	
	
	// 
	// Getter / setter
	// 
	
	public int getDefaultPerformance() {
		return size() > 0 ? m_defaultIndex : -1;
	}
	
	public void setDefaultPerformance(int index) {
		m_defaultIndex = index;
		fireDefaultPerformanceChanged();
	}

	
	//
	// Impl�mentation de HarmoTabObjectListener
	//
	
	/**
	 * R�action aux modifications des interpr�tations de la liste
	 */
	@Override
	public void onObjectChanged(HarmoTabObjectEvent event) {
		firePerformanceListItemChanged((Performance) event.getSource());
	}

	
	//
	// Gestion des listeners
	//
	
	public void addPerformanceListListener(PerformanceListListener listener) {
		m_listeners.add(listener);
	}
	
	public void removePerformanceListListener(PerformanceListListener listener) {
		m_listeners.remove(listener);
	}
	
	
	protected void firePerformanceListChanged() {
		for (PerformanceListListener listener : m_listeners) {
			listener.onPerformanceListChanged(this);
		}
	}
	
	protected void fireDefaultPerformanceChanged() {
		for (PerformanceListListener listener : m_listeners) {
			listener.onDefaultPerformanceChanged(this);
		}
	}
	
	protected void firePerformanceListItemChanged(Performance perf) {
		for (PerformanceListListener listener : m_listeners) {
			listener.onPerformanceListItemChanged(this, perf);
		}
	}
	
	
	//
	// Attributs
	//
	
	protected int m_defaultIndex;
	private final LinkedList<Performance> m_list = new LinkedList<Performance>();
	private final ArrayList<PerformanceListListener> m_listeners = new ArrayList<PerformanceListListener>();
	
}

