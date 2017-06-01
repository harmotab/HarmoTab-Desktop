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

import java.util.*;

import harmotab.core.*;
import harmotab.element.*;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializableObject;
import harmotab.io.SerializedObject;
import harmotab.renderer.LocationItem;
import harmotab.throwables.*;
import harmotab.track.layout.*;
import harmotab.track.soundlayout.*;


/**
 * Mod�le d'une piste
 */
public abstract class Track extends HarmoTabObject implements SerializableObject, Iterable<Element> {
	
	public static final String TRACK_TYPESTR = "track";
	
	public static final String NAME_ATTR = "name";
	public static final String INSTRUMENT_ATTR = "instrument";
	public static final String COMMENT_ATTR = "comment";
	public static final String VOLUME_ATTR = "volume";
	public static final String TRACK_INDEX_ATTR = "index";
	
	public static final String ELEMENT_CHANGED_EVENT = "elementChanged";
	public static final String ELEMENT_LIST_CHANGED_EVENT = "elementListChanged";
	
	public static final String DEFAULT_NAME = "";
	public static final int DEFAULT_INSTRUMENT = 0;
	public static final String DEFAULT_COMMENT = "";
	public static final int DEFAULT_VOLUME = 100; 
	public static final int DEFAULT_TRACK_INDEX = 0;
	
	
	//
	// Constructeur
	//
	
	protected Track() {
	}
	
	public Track(Score score) {
		setScore(score);
		m_trackLayout = null;
		
		setLinkedTrack(null);
		setName(DEFAULT_NAME);
		setInstrument(DEFAULT_INSTRUMENT);
		setComment(DEFAULT_COMMENT);
		setVolume(DEFAULT_VOLUME);
		setTrackIndex(DEFAULT_TRACK_INDEX);
	}
	
	
	//
	// Getters / setters
	//
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		m_name = name;
		fireObjectChanged(NAME_ATTR);
	}
	
	
	public int getInstrument() {
		return m_instrument;
	}
	
	public void setInstrument(int instrument) {
		m_instrument = instrument;
		fireObjectChanged(INSTRUMENT_ATTR);
	}
	
	
	public String getComment() {
		return m_comment;
	}
	
	public void setComment(String comment) {
		m_comment = comment;
		fireObjectChanged(COMMENT_ATTR);
	}
	
	
	public int getVolume() {
		return m_volumePercentage;
	}
	
	public void setVolume(int volumePercentage) {
		if (volumePercentage < 0 || volumePercentage > 100)
			throw new IllegalArgumentException("Invalid volume percentage (" + volumePercentage + ").");
			
		m_volumePercentage = volumePercentage;
		fireObjectChanged(VOLUME_ATTR);
	}
	
	
	public Score getScore() {
		return m_score;
	}
	
	public void setScore(Score score) {
		m_score = score;
	}
	
	
	public void setTrackLayout(TrackLayout trackLayout) {
		if (trackLayout == null)
			throw new NullPointerException();
		m_trackLayout = trackLayout;
	}
	
	public TrackLayout getTrackLayout() {
		return m_trackLayout;
	}
	
	
	public void setSoundLayout(SoundLayout layout) {
		if (layout == null)
			throw new NullPointerException();
		m_soundLayout = layout;
	}
	
	public SoundLayout getSoundLayout() {
		return m_soundLayout;
	}

	
	public Track getLinkedTrack() {
		return m_linkedTrack;
	}
	
	public void setLinkedTrack(Track track) {
		m_linkedTrack = track;
	}
	
	
	public int getTrackIndex() {
		return m_trackIndex;
	}
	
	public void setTrackIndex(int index) {
		m_trackIndex = index;
	}
	
	
	//
	// Gestion de la liste d'�l�ments
	//
	
	public void add(Element element) {
		m_elements.add(element);
		addAttributeChangesObserver(element, ELEMENT_CHANGED_EVENT);
		dispatchEvent(new HarmoTabObjectEvent(this, ELEMENT_LIST_CHANGED_EVENT, new HarmoTabObjectEvent(element)));
	}
	
	public void add(int index, Element element) {
		m_elements.add(index, element);
		addAttributeChangesObserver(element, ELEMENT_CHANGED_EVENT);
		dispatchEvent(new HarmoTabObjectEvent(this, ELEMENT_LIST_CHANGED_EVENT, new HarmoTabObjectEvent(element)));
	}
	
	public void remove(Element element) {
		// Supprime l'�l�ment si c'est un �l�ment de la piste
		if (m_elements.remove(element) == false) {
			// Sinon cherche si c'est un sous �l�ment pour le supprimer
			boolean deleted = false;
			for (Element cur : m_elements) {
				if (cur.delete(element))
					deleted = true;
			}
			if (!deleted)
				System.err.println("Track.remove: Element " + element + " not found.");
		}
		removeAttributeChangesObserver(element, ELEMENT_CHANGED_EVENT);
		dispatchEvent(new HarmoTabObjectEvent(this, ELEMENT_LIST_CHANGED_EVENT, new HarmoTabObjectEvent(element)));
	}
	
	public Element get(int index) {
		return m_elements.get(index);
	}
	
	/**
	 * Retourne un �l�ment en fonction de son type et de sa position
	 * Retourn null si aucun �l�ment ne correspond.
	 * ex: get(instanceDeElement, 1) retournera la 2em Element de la piste.
	 */
	public Element get(Class<?> elementClass, int index) {
		for (Element element : this) {
			if (element.getClass().equals(elementClass)) {
				if (index == 0) {
					return element;
				}
				index--;
			}
		}
		return null;
	}
	
	public int size() {
		return m_elements.size();
	}
	
	public int indexOf(Element element) {
		return m_elements.indexOf(element);
	}
	
	public Iterator<Element> iterator() {
		return m_elements.iterator();
	}
	
	public ListIterator<Element> listIterator() {
		return m_elements.listIterator();
	}
	
	public ListIterator<Element> listIterator(Element element) {
		int index = m_elements.indexOf(element);
		if (index == -1)
			throw new ObjectNotFoundError("Cannot create iterator on element #" + element + " !");
		return m_elements.listIterator(index);
	}
	
	public ListIterator<Element> listIterator(LocationItem item) {
		int index = m_elements.indexOf(item.getElement());
		if (index == -1) {
			index = m_elements.indexOf(item.getParent());
			if (index == -1)
				throw new ObjectNotFoundError("Cannot create iterator on item #" + item + " !");
		}
		return m_elements.listIterator(index);
	}

	
	public void clear() {
		for (Element element : m_elements)
			removeAttributeChangesObserver(element, ELEMENT_CHANGED_EVENT);
		m_elements.clear();
		dispatchEvent(new HarmoTabObjectEvent(this, ELEMENT_LIST_CHANGED_EVENT));
	}
	
	
	//
	// M�thodes utilitaires
	//

	/**
	 * Retourne la durée de la piste
	 */
	public float getDuration() {
		float duration = 0.0f;
		for (Element element : m_elements)
			duration += element.getDuration();
		return duration;
	}
	
	
	/**
	 * Retourne un list d'�l�ments que l'utilisateur peut ajouter � la piste.
	 */
	abstract public Collection<TrackElement> getAddableElements();
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(TRACK_TYPESTR, hashCode());

		object.setAttribute(TRACK_INDEX_ATTR, getTrackIndex()+"");
		object.setAttribute(NAME_ATTR, getName());
		object.setAttribute(INSTRUMENT_ATTR, getInstrument()+"");
		object.setAttribute(VOLUME_ATTR, getVolume()+"");
		object.setAttribute(COMMENT_ATTR, getComment());
		
		for (Element element : m_elements)
			object.addChild(element.serialize(serializer));

		return object;
	}

	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {

		setTrackIndex(object.hasAttribute(TRACK_INDEX_ATTR) ?
			Integer.parseInt(object.getAttribute(TRACK_INDEX_ATTR)) :
			DEFAULT_TRACK_INDEX);
		setName(object.hasAttribute(NAME_ATTR) ?
			object.getAttribute(NAME_ATTR) :
			DEFAULT_NAME);
		setInstrument(object.hasAttribute(INSTRUMENT_ATTR) ?
			Integer.parseInt(object.getAttribute(INSTRUMENT_ATTR)) :
			DEFAULT_INSTRUMENT);
		setVolume(object.hasAttribute(VOLUME_ATTR) ?
			Integer.parseInt(object.getAttribute(VOLUME_ATTR)) :
			DEFAULT_VOLUME);
		setComment(object.hasAttribute(COMMENT_ATTR) ?
			object.getAttribute(COMMENT_ATTR) :
			DEFAULT_COMMENT);
	
		// Eléments
		for (int i = 0; i < object.getChildsNumber(); i++) {
			SerializedObject child = object.getChild(i);
			if (child != null) {
				// Piste liée
				if (child.getObjectType().equals(Track.TRACK_TYPESTR)) {
					Track track = TrackFactory.create(m_score, serializer, object.getChild(i));
					track.setLinkedTrack(this);
					m_score.addTrack(track);
				}
				// Eléments
				else {
					HarmoTabObject htObject = HarmoTabObjectFactory.create(serializer, child);
					if (htObject instanceof Element) {
						add((Element) htObject);
					}
				}
			}
		}

	}

		
	//
	// Attributs
	//
	
	public void printTrace() {
		System.out.println("Track " + m_name + ", " + m_elements.size() + " elements :");
		int index = 0;
		for (Element e : m_elements) {
			System.out.println(index + ". " + e);
			index++;
		}
	}
	
	
	protected String m_name;
	protected int m_instrument;
	protected String m_comment;
	protected int m_volumePercentage;
	protected LinkedList<Element> m_elements = new LinkedList<Element>();

	private Score m_score = null;
	private TrackLayout m_trackLayout = null;
	private SoundLayout m_soundLayout = null;
	private Track m_linkedTrack = null;
	private int m_trackIndex = 0;
		
}

