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

package harmotab.element;

import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.io.*;


/**
 * Mod�le d'un �l�ment de texte
 */
public class Lyrics extends TrackElement {
	
	public static final String TEXT_ATTR = "text";
	public static final String DURATION_ATTR = "duration";
	
	
	//
	// Constructeur
	//
	
	public Lyrics(String text, Duration duration) {
		super(Element.LYRICS);
		setText(text);
		setDurationObject(duration);
	}

	public Lyrics(String text) {	
		this(text, new Duration());
	}
	
	public Lyrics() {
		this("");
	}
		
	
	@Override
	public Object clone() {
		Lyrics lyrics = (Lyrics) super.clone();
		lyrics.setText(getText());
		lyrics.setDurationObject((Duration) getDurationObject().clone());
		return lyrics;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new LyricsRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setText(String text) {
		m_text = text;
		fireObjectChanged(TEXT_ATTR);
	}
	
	public String getText() {
		return m_text;
	}
	
	
	public void setDurationObject(Duration duration) {
		removeAttributeChangesObserver(m_duration, DURATION_ATTR);
		m_duration = duration;
		addAttributeChangesObserver(m_duration, DURATION_ATTR);
		fireObjectChanged(DURATION_ATTR);
	}
	
	public Duration getDurationObject() {
		return m_duration;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_LYRICS);
	}

	
	//
	// Surchage des méthodes
	//
	
	@Override
	public float getDuration() {
		return m_duration.getDuration();
	}

	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(TEXT_ATTR, getText());
		object.setElementAttribute(DURATION_ATTR, getDurationObject());
		return object;
	}
	
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setText(object.getAttribute(TEXT_ATTR));
		setDurationObject((Duration) object.getElementAttribute(DURATION_ATTR));
	}
	
	
	//
	// Attributs
	//
	
	protected String m_text = null;
	protected Duration m_duration = null;
	
}


/**
 * Commande d'annulation des modifications d'un texte
 */
class LyricsRestoreCommand extends Lyrics implements RestoreCommand {
	
	public LyricsRestoreCommand(Lyrics saved) {
		m_saved = saved;
		m_text = m_saved.m_text;
		m_duration = m_saved.m_duration;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_text != m_text)
			m_saved.setText(m_text);
		if (m_saved.m_duration != m_duration)
			m_saved.setDurationObject(m_duration);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new LyricsRestoreCommand(m_saved);
	}
	
	private Lyrics m_saved;
}

