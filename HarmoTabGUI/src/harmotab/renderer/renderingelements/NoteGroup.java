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

import harmotab.core.Figure;
import harmotab.element.Note;
import harmotab.renderer.*;
import harmotab.renderer.awtrenderers.AwtNoteRenderer;

import java.util.*;


public abstract class NoteGroup extends RenderingElement implements List<LocationItem> {
	
	public final static int UP = 1;
	public final static int DOWN = -1;
	public final static int NOTE_ROUND_WIDTH = 8;
	
	
	//
	// Constructeurs
	//

	public NoteGroup(byte type) {
		super(type);
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	/**
	 * Retourne la dur�e du groupe
	 */
	public float getGroupDuration() {
		float duration = 0.0f;
		for (LocationItem item : this)
			duration += item.getElement().getDuration();
		return duration;
	}
	
	
	/**
	 * Retourne la figure des notes du groupe s'ils ont tous la m�me figure
	 */
	public Figure getGroupFigure() {
		if (m_list.size() < 1)
			return null;
		
		byte figureType = ((Note) m_list.get(0).getElement()).getFigure().getType();
		for (LocationItem item : m_list) {
			int itemType = ((Note) item.getElement()).getFigure().getType();
			if (itemType != figureType)
				return null;
		}
		
		return new Figure(figureType);
	}
	
	
	/**
	 * Retourne la hauteur moyenne des notes du groupement
	 */
	public int getAverageHeight() {
		int sum = 0;
		ListIterator<LocationItem> it = listIterator();
		while (it.hasNext())
			sum += ((Note)it.next().getElement()).getHeight().getOrdinate();
		return sum / size();
	}

	
	/**
	 * Retourne le sens de la queue des notes du groupe
	 */
	public int getAverageDirection() {
		return getAverageHeight() >= AwtNoteRenderer.REVERSE_QUEUE_ORDINATE ? UP : DOWN;	
	}
	
	
	/**
	 * Indique si toutes les notes ont la m�me direction
	 */
	public boolean allHaveSameDirection() {
		if (m_list.size() < 1)
			return true;
		
		int direction = ((Note) m_list.get(0).getElement()).getHeight().getOrdinate() 
				>= AwtNoteRenderer.REVERSE_QUEUE_ORDINATE ? UP : DOWN;
		for (LocationItem item : m_list) {
			int itemDirection = ((Note) item.getElement()).getHeight().getOrdinate() 
				>= AwtNoteRenderer.REVERSE_QUEUE_ORDINATE ? UP : DOWN;
			if (itemDirection != direction)
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Retourne l'abscisse du d�but du groupe
	 */
	public int getX1() {
		if (size() > 1)
			return get(0).getPointOfInterestX();
		else
			return get(0).getPointOfInterestX() - NOTE_ROUND_WIDTH;
	}
	
	/**
	 * Retourne l'abcisse de la fin du groupe
	 */
	public int getX2() {
		if (size() > 1)
			return get(size() - 1).getPointOfInterestX();
		else
			return get(size() - 1).getPointOfInterestX() + NOTE_ROUND_WIDTH;
	}
	
	
	/**
	 * Retourne la ligne correspondant au groupe de note
	 */
	public GroupLine getGroupLine() {
		final int LINE_SIZE = 22;
		final float MAX_SLOPE = 0.25f;
		
		// Calcul le coefficient de la droite entre la première et la dernière note du groupe -> y = mx + p
		LocationItem first = get(0);
		LocationItem last = get(size()-1);
		
		// Calcul des extremit�s du groupement
		int x1 = getX1();
		int x2 = getX2();
		int y1 = first.getPointOfInterestY() - LINE_SIZE;
		int y2 = last.getPointOfInterestY() - LINE_SIZE;
		
		// Calcul du coefficient de la pente entre les extr�mit�s
		float m = (float) (y2 - y1) / (float) (x2 - x1);
		if (m > MAX_SLOPE) m = MAX_SLOPE;
		if (m < -MAX_SLOPE) m = -MAX_SLOPE;
		
		// Calcul la direction des �l�ments
		int direction = 0;
		// Si toutes les notes ont la m�me figure et qu'elles sont accrochables,
		// prend en compte la direction de l'ensemble
		Figure groupFigure = getGroupFigure();
		if (groupFigure != null && groupFigure.isHookable()) {
			direction = getAverageDirection();
		}
		// Sinon...
		else {
			// si elles ont toutes la direction BAS, la direction est BAS 
			if (allHaveSameDirection() && getAverageDirection() == DOWN) {
				direction = DOWN;
			}
			// sinon le direction est HAUT
			else {
				direction = UP;
			}
		}	
		
		// Ajuste p pour avoir une distance minimale entre chaque note et la barre
		Iterator<LocationItem> i = iterator();
		int p = i.next().getPointOfInterestY() - LINE_SIZE * direction;
		int y = 0;
		while (i.hasNext()) {
			LocationItem item = i.next();
			y = (int) (m * (item.getPointOfInterestX() - (float) x1) + p);
			if (direction == UP) {
				if (y + LINE_SIZE * direction > item.getPointOfInterestY())
					p -= (y + LINE_SIZE) - item.getPointOfInterestY() ;
			} else {
				if (y + LINE_SIZE * direction < item.getPointOfInterestY())
					p -= (y + LINE_SIZE*direction) - item.getPointOfInterestY() ;					
			}
		}
		
		// Décale x1 et x2 de l'autre coté du rond de la note si les queues sont vers le bas
		if (direction == DOWN) {
			x1 -= 5;//NOTE_ROUND_WIDTH;
			x2 -= 5;//NOTE_ROUND_WIDTH;
		}
		else {
			x1 += 3;
			x2 += 3;			
		}
		
		// Cr�er la ligne
		return new GroupLine( m, p, direction, x1, x2 );
	}
	
	
	
	//
	// Implementation de l'interface List
	//

	@Override
	public boolean add(LocationItem item) {
		return m_list.add(item);
	}

	@Override
	public void add(int index, LocationItem item) {
		m_list.add(index, item);
	}

	@Override
	public boolean addAll(Collection<? extends LocationItem> collection) {
		return m_list.addAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends LocationItem> collection) {
		return m_list.addAll(index, collection);
	}

	@Override
	public void clear() {
		m_list.clear();
	}

	@Override
	public boolean contains(Object element) {
		return m_list.contains(element);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return m_list.containsAll(collection);
	}

	@Override
	public LocationItem get(int index) {
		return m_list.get(index);
	}

	@Override
	public int indexOf(Object element) {
		return m_list.indexOf(element);
	}

	@Override
	public boolean isEmpty() {
		return m_list.isEmpty();
	}

	@Override
	public Iterator<LocationItem> iterator() {
		return m_list.iterator();
	}

	@Override
	public int lastIndexOf(Object element) {
		return m_list.lastIndexOf(element);
	}

	@Override
	public ListIterator<LocationItem> listIterator() {
		return m_list.listIterator();
	}

	@Override
	public ListIterator<LocationItem> listIterator(int index) {
		return m_list.listIterator(index);
	}

	@Override
	public boolean remove(Object element) {
		return m_list.remove(element);
	}

	@Override
	public LocationItem remove(int index) {
		return m_list.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return m_list.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return m_list.retainAll(collection);
	}

	@Override
	public LocationItem set(int index,LocationItem item) {
		return m_list.set(index, item);
	}

	@Override
	public int size() {
		return m_list.size();
	}

	@Override
	public List<LocationItem> subList(int fromIndex, int toIndex) {
		return m_list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return m_list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return m_list.toArray(array);
	}
	
	
	//
	// Attributs
	//
	
	private LinkedList<LocationItem> m_list = new LinkedList<LocationItem>();

}
