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
 * Piste compos�e d'une ligne de texte
 */
public class LyricsTrack extends Track {
	
	public final static String LYRICS_TRACK_TYPESTR = "lyricsTrack";
	

	//
	// Constructeur
	//
	
	protected LyricsTrack() {}
	
	public LyricsTrack(Score score, Track linkedTrack) {
		super(score);		
		setLinkedTrack(linkedTrack);
		setTrackLayout(new LyricsTrackLayout(this));
		setSoundLayout(new LyricsTrackSoundLayout(this));
		setName(Localizer.get(i18n.N_LYRICS_TRACK));
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new LyricsTrackRestoreCommand(this);
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	@Override
	public Collection<TrackElement> getAddableElements() {
		ArrayList<TrackElement> list = new ArrayList<TrackElement>();
		list.add(new Lyrics());
		list.add(new Silence());
		return list;
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute("type", LYRICS_TRACK_TYPESTR);
		return object;
	}
	
}


/**
 * Commande d'annulation des modifications d'une piste de texte
 */
class LyricsTrackRestoreCommand extends LyricsTrack implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public LyricsTrackRestoreCommand(LyricsTrack saved) {
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
		return new LyricsTrackRestoreCommand(m_saved);
	}
	
	private LyricsTrack m_saved;
}
