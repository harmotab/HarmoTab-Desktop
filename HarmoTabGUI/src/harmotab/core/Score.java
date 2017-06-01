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

package harmotab.core;

import harmotab.core.undo.RestoreCommand;
import harmotab.element.*;
import harmotab.io.*;
import harmotab.throwables.*;
import harmotab.track.*;

import java.awt.Font;
import java.util.*;


/**
 * Mod�le d'une partition
 */
public class Score extends HarmoTabObject implements Iterable<Track> {
	public static final String SCORE_TYPESTR = "score";
		
	public static final String TITLE_ATTR = "title";
	public static final String SONGWRITER_ATTR = "songwriter";
	public static final String COMMENT_ATTR = "comment";
	public static final String TEMPO_ATTR = "tempo";
	public static final String DESCRIPTION_ATTR = "description";
	
	public static final String PROPERTIES_CHANGED_EVENT = "propertiesChanged";
	public static final String TRACK_CHANGED_EVENT = "trackChanged";
	public static final String TRACK_LIST_CHANGED_EVENT = "trackListChanged";
	

	//
	// Constructeur
	//
	
	public Score() {
		reset();	
	}
	
	
	public void reset() {
		setTitle(new TextElement(
				""/*Localizer.get(i18n.N_SCORE_TITLE)*/, 
				new Font("DejaVu Sans", Font.BOLD, 26), 
				TextElement.CENTER));
		setSongwriter(new TextElement(
				""/*Localizer.get(i18n.N_SCORE_SONGWRITER)*/,
				new Font("DejaVu Sans", Font.PLAIN, 20), 
				TextElement.CENTER));
		setComment(new TextElement(
				""/*Localizer.get(i18n.N_SCORE_COMMENT)*/, 
				new Font("DejaVu Sans", Font.PLAIN, 14), 
				TextElement.RIGHT));
		setTempo(new Tempo());
		setDescription("");
		m_tracks.clear();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new ScoreRestoreCommand(this);
	}
	
	
	// 
	// Getters / setters
	// 
	
	public String getTitleString() {
		return m_title.getText();
	}
	
	public TextElement getTitle() {
		return m_title;
	}
	
	public void setTitle(String title) {
		m_title.setText(title);
	}
	
	public void setTitle(TextElement title) {
		if (title == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_title, TITLE_ATTR);
		m_title = title;
		addAttributeChangesObserver(m_title, TITLE_ATTR);
		fireObjectChanged(TITLE_ATTR);
	}
	
	
	public String getSongwriterString() {
		return m_songwriter.getText();
	}
	
	public TextElement getSongwriter() {
		return m_songwriter;
	}
	
	public void setSongwriter(String songwriter) {
		m_songwriter.setText(songwriter);
	}
	
	public void setSongwriter(TextElement songwriter) {
		if (songwriter == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_songwriter, SONGWRITER_ATTR);
		m_songwriter = songwriter;
		addAttributeChangesObserver(m_songwriter, SONGWRITER_ATTR);
		fireObjectChanged(SONGWRITER_ATTR);
	}
	
	
	public int getTempoValue() {
		return m_tempo.getValue();
	}
	
	public Tempo getTempo() {
		return m_tempo;
	}
	
	public String getTempoString() {
		return Localizer.get(i18n.ET_TEMPO_EQUALS) + m_tempo;
	}
	
	public void setTempo(int tempo) {
		setTempo(new Tempo(tempo));
	}
	
	public void setTempo(Tempo tempo) {
		if (tempo == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_tempo, TEMPO_ATTR);
		m_tempo = tempo;
		addAttributeChangesObserver(m_tempo, TEMPO_ATTR);
		fireObjectChanged(TEMPO_ATTR);
	}
	
	
	public String getCommentString() {
		return m_comment.getText();
	}
	
	public TextElement getComment() {
		return m_comment;
	}
	
	public void setComment(String comment) {
		m_comment.setText(comment);
	}
	
	public void setComment(TextElement comment) {
		if (comment == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_comment, COMMENT_ATTR);
		m_comment = comment;
		addAttributeChangesObserver(m_comment, COMMENT_ATTR);
		fireObjectChanged(COMMENT_ATTR);
	}
	
	
	public void setDescription(String description) {
		if (description == null)
			throw new NullPointerException();
		m_description = description;
		fireObjectChanged(DESCRIPTION_ATTR);
	}
	
	public String getDescription() {
		return m_description;
	}
	
	
	//
	// Gestion des pistes
	//
	
	/**
	 * Retourne une piste en fonction de sa position
	 */
	public Track getTrack(int index) {
		if (index < 0 || index >= m_tracks.size())
			throw new OutOfBoundsError("Out of bound track index " + index);
		return m_tracks.get(index);
	}
	
	/**
	 * Retourne une piste en fonction de son type et de sa position
	 * Retourn null si aucune piste ne correspond.
	 * ex: getTrack(instanceDeStaffTrack, 1) retournera la 2em StaffTrack de 
	 * la partition.
	 */
	public Track getTrack(Class<?> trackClass, int index) {
		for (Track track : m_tracks) {
			if (track.getClass().equals(trackClass)) {
				if (index == 0) {
					return track;
				}
				index--;
			}
		}
		return null;
	}
	

	/**
	 * Ajoute une piste en derni�re position
	 */
	public void addTrack(Track track) {
		m_tracks.add(track);
		addAttributeChangesObserver(track, TRACK_CHANGED_EVENT);
		dispatchEvent(new HarmoTabObjectEvent(this, TRACK_LIST_CHANGED_EVENT, new HarmoTabObjectEvent(track)));
	}

	/**
	 * Retire une piste
	 */
	public void removeTrack(Track track) {
		m_tracks.remove(track);
		removeAttributeChangesObserver(track, TRACK_CHANGED_EVENT);
		dispatchEvent(new HarmoTabObjectEvent(this, TRACK_LIST_CHANGED_EVENT, new HarmoTabObjectEvent(track)));
	}	
	
	/**
	 * Retourne le nombre de pistes de la partition
	 */
	public int getTracksCount() {
		return m_tracks.size();
	}
	
	/**
	 * Retourne la position de la piste dans la position.
	 * Retourne -1 si la piste ne fait pas partie de la partition.
	 */
	public int getTrackId(Track track) {
		return m_tracks.indexOf(track);
	}
	
	/**
	 * Retourne un it�rateur sur les pistes de la partition
	 */
	@Override
	public Iterator<Track> iterator() {
		return m_tracks.iterator();
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	/**
	 * Retourne la durée de la partition.
	 * Il s'agit de la durée de la piste la plus longue.
	 */
	public float getDuration() {
		float scoreDuration = 0.0f;
		for (Track track : m_tracks) {
			float trackDuration = track.getDuration();
			if (trackDuration > scoreDuration)
				scoreDuration = trackDuration;
		}
		return scoreDuration;
	}
	
	
	/**
	 * Met � jour les index des pistes en fonction de leur position.
	 * Cette m�thode n'est appel�e automatiquement qu'avant l'enregistrement de 
	 * la partition. Les index ne sont pas mis � jour en cas d'ajout ou de 
	 * suppression de pistes.
	 */
	public void updateTracksIndex() {
		int index = 0;
		for (Track track : m_tracks) {
			track.setTrackIndex(index);
			index++;
		}
	}
	
	
	/**
	 * Tri les pistes en fonction de leur index.
	 * Cette m�thode n'est appel�e automatiquement qu'apr�s ouverture d'une 
	 * partition. 
	 */
	public void sortTracksUsingIndex() {
		Collections.sort(m_tracks, new TrackIndexComparator());
	}
	
	public class TrackIndexComparator implements Comparator<Track> {
		@Override
		public int compare(Track track0, Track track1) {
			return track0.getTrackIndex() - track1.getTrackIndex();
		}
	}

	
	/**
	 * Retourne une nom g�n�rique pour la partition
	 */
	public String getScoreName() {
		String sw = getSongwriterString();
		String ttl = getTitleString();
	
		if (!sw.isEmpty() && !ttl.isEmpty())
			return  sw + " - " + ttl;
		if (!sw.isEmpty())
			return sw;
		if (!ttl.isEmpty())
			return ttl;
		return "HarmoTab score";
	}
	
	
	/**
	 * Retourne la piste principale de la partition
	 */
	public Track getMainTrack() {
		return getTrack(HarmoTabTrack.class, 0);
	}
	
	
	/**
	 * Retourne la signature temporelle de la premi�re mesure
	 */
	public TimeSignature getFirstTimeSignature() {
		Track mainTrack = getMainTrack();
		Bar firstBar = (Bar) mainTrack.get(Bar.class, 0);
		return firstBar.getTimeSignature();
	}
	
		
	//
	// S�rialisation / d�serialisation xml
	//

	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(SCORE_TYPESTR, hashCode());
		
		updateTracksIndex();

		object.setElementAttribute(TITLE_ATTR, getTitle());
		object.setElementAttribute(SONGWRITER_ATTR, getSongwriter());
		object.setElementAttribute(TEMPO_ATTR, getTempo());
		object.setElementAttribute(COMMENT_ATTR, getComment());
		object.setAttribute(DESCRIPTION_ATTR, getDescription());

		int round = 0;
		int remainingTracks = m_tracks.size();
		
		HashMap<Track, SerializedObject> trackObjectsMap = new HashMap<Track, SerializedObject>(m_tracks.size());
		
		
		while (remainingTracks > 0) {
			for (Track track : m_tracks) {
				if (track.getTrackLayout().getLayoutRound() == round) {
					SerializedObject trackSerialized = track.serialize(serializer);
					trackObjectsMap.put(track, trackSerialized);
					if (track.getLinkedTrack() == null) {
						object.addChild(trackSerialized);
					}
					else {
						SerializedObject linkedObject = trackObjectsMap.get(track.getLinkedTrack());
						if (linkedObject != null) {
							linkedObject.addChild(trackSerialized);
						}
						else {
							throw new NullPointerException("Linked track not yet serialized.");
						}
					}
					remainingTracks--;
				}
			}
			round++;
		}

		return object;
	}
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		reset();

		// R�cup�ration des attributs de la partitions
		if (object.hasAttribute(TITLE_ATTR))
			setTitle((TextElement) object.getElementAttribute(TITLE_ATTR));
		if (object.hasAttribute(SONGWRITER_ATTR))
			setSongwriter((TextElement) object.getElementAttribute(SONGWRITER_ATTR));
		if (object.hasAttribute(TEMPO_ATTR))
			setTempo((Tempo) object.getElementAttribute(TEMPO_ATTR));
		if (object.hasAttribute(COMMENT_ATTR))
			setComment((TextElement) object.getElementAttribute(COMMENT_ATTR));
		if (object.hasAttribute(DESCRIPTION_ATTR))
			setDescription(object.getAttribute(DESCRIPTION_ATTR));
		
		// R�cup�ration des pistes
		for (int i = 0; i < object.getChildsNumber(); i++) {
			SerializedObject serialized = object.getChild(i);
			if (serialized != null && serialized.getObjectType().equals("track")) {
				Track track = TrackFactory.create(this, serializer, serialized);
				addTrack(track);
			}
		}
		
		// Repositionne les piste en fonction de leur index
		sortTracksUsingIndex();
		
	}
	
	
	//
	// Attributs
	//
	
	protected ArrayList<Track> m_tracks = new ArrayList<Track>(0);
	protected TextElement m_title;
	protected TextElement m_songwriter;
	protected Tempo m_tempo;
	protected TextElement m_comment;
	protected String m_description;
	
}


/**
 * Commande d'annulation des modifications d'une partition
 */
class ScoreRestoreCommand extends Score implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public ScoreRestoreCommand(Score saved) {
		m_saved = saved;
		m_tracks = (ArrayList<Track>) m_saved.m_tracks.clone();
		m_title = m_saved.m_title;
		m_songwriter = m_saved.m_songwriter;
		m_tempo = m_saved.m_tempo;
		m_comment = m_saved.m_comment;
		m_description = m_saved.m_description;
		
		m_attributesRestoreCommands = new LinkedList<RestoreCommand>();
		m_attributesRestoreCommands.add(m_title.createRestoreCommand());
		m_attributesRestoreCommands.add(m_songwriter.createRestoreCommand());
		m_attributesRestoreCommands.add(m_tempo.createRestoreCommand());
		m_attributesRestoreCommands.add(m_comment.createRestoreCommand());
	}
	
	@Override
	public void execute() {
		for (RestoreCommand command : m_attributesRestoreCommands) {
			command.execute();
		}
		if (m_saved.m_tracks != m_tracks)
			m_saved.m_tracks = m_tracks;
		if (m_saved.m_title != m_title)
			m_saved.setTitle(m_title);
		if (m_saved.m_songwriter != m_songwriter)
			m_saved.setSongwriter(m_songwriter);
		if (m_saved.m_tempo != m_tempo)
			m_saved.setTempo(m_tempo);
		if (m_saved.m_comment != m_comment)
			m_saved.setComment(m_comment);
		if (m_saved.m_description != m_description)
			m_saved.setDescription(m_description);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new ScoreRestoreCommand(m_saved);
	}
	
	private Score m_saved;
	private LinkedList<RestoreCommand> m_attributesRestoreCommands = null;
}
