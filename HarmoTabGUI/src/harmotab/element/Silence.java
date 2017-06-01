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
 * Mod�le d'un silence
 */
public class Silence extends TrackElement {
	
	public final static String DURATION_ATTR = "duration";
	

	//
	// Constructeur
	//
	
	public Silence(float duration) {
		super(Element.SILENCE);
		m_duration = new Duration(duration);
	}
	
	public Silence() {
		this(Duration.DEFAULT_DURATION);
	}
	
	
	@Override
	public Object clone() {
		Silence clone = (Silence) super.clone();
		clone.m_duration = (Duration) m_duration.clone();
		return clone;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new SilenceRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	@Override 
	public float getDuration() {
		return m_duration.getDuration();
	}
	
	public void setDuration(float duration) {
		m_duration.setDuration(duration);
		fireObjectChanged(DURATION_ATTR);
	}

	
	//
	// M�thodes utilitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_SILENCE);
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(DURATION_ATTR, getDuration()+"");
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setDuration(object.hasAttribute(DURATION_ATTR) ?
			Float.parseFloat(object.getAttribute(DURATION_ATTR)) :
			Duration.DEFAULT_DURATION);
	}
	
	
	//
	// Attributs
	//
	
	protected Duration m_duration;

}


/**
 * Commande d'annulation des modifications d'un silence
 */
class SilenceRestoreCommand extends Silence implements RestoreCommand {
	
	public SilenceRestoreCommand(Silence saved) {
		m_saved = saved;
		savedDuration = m_saved.m_duration.getDuration();
	}
	
	@Override
	public void execute() {
		m_saved.setDuration(savedDuration);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new SilenceRestoreCommand(m_saved);
	}
	
	private float savedDuration;
	private Silence m_saved;
}

