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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.element.*;
import harmotab.io.*;
import harmotab.track.layout.*;
import harmotab.track.soundlayout.*;


/**
 * Mod�le d'une piste d'accompagnement
 */
public class AccompanimentTrack extends Track {
	
	private static final int ACCOMPANIMENT_DEFAULT_VOLUME = 90;
	public static String ACCOMPANIMENT_TRACK_TYPESTR = "accompanimentTrack";
	

	//
	// Constructeur
	//
	
	protected AccompanimentTrack() {
	}
	
	public AccompanimentTrack(Score score, Track linkedTrack) {
		super(score);		
		setLinkedTrack(linkedTrack);
		setTrackLayout(new AccompanimentTrackLayout(this));
		setSoundLayout(new AccompanimentTrackSoundLayout(this));
		setVolume(ACCOMPANIMENT_DEFAULT_VOLUME);
		setName(Localizer.get(i18n.N_ACCOMPANIMENT_TRACK));
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new AccompanimentTrackRestoreCommand(this);
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	@Override
	public Collection<TrackElement> getAddableElements() {
		ArrayList<TrackElement> list = new ArrayList<TrackElement>();
		list.add(new Accompaniment(new Chord()));
		list.add(new Silence());
		return list;
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute("type", ACCOMPANIMENT_TRACK_TYPESTR);
		return object;
	}
	
}


/**
 * Commande d'annulation des modifications d'une interpr�tation
 */
class AccompanimentTrackRestoreCommand extends AccompanimentTrack implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public AccompanimentTrackRestoreCommand(AccompanimentTrack saved) {
		m_saved = saved;
		m_name = m_saved.m_name;
		m_instrument = m_saved.m_instrument;
		m_comment = m_saved.m_comment;
		m_volumePercentage = m_saved.m_volumePercentage;
		m_elements = (LinkedList<Element>) m_saved.m_elements.clone();
	}
	
	@Override
	public void execute() {
		if (m_saved.m_name != m_name)
			m_saved.setName(m_name);
		if (m_saved.m_instrument != m_instrument)
			m_saved.setInstrument(m_instrument);
		if (m_saved.m_comment != m_comment)
			m_saved.setComment(m_comment);
		if (m_saved.m_volumePercentage != m_volumePercentage)
			m_saved.setVolume(m_volumePercentage);
		if (m_saved.m_elements != m_elements)
			m_saved.m_elements = m_elements;
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new AccompanimentTrackRestoreCommand(m_saved);
	}
	
	private AccompanimentTrack m_saved;
}

