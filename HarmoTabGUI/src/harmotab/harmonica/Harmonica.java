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

package harmotab.harmonica;

import java.util.Collection;
import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.element.Tab;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.OutOfBoundsError;


/**
 * Mod�le d'un harmonica
 */
public class Harmonica extends HarmoTabObject {
	
	public final static String HARMONICA_TYPESTR = "harmonica";
	
	public final static String NAME_ATTR = "name";
	public final static String MODEL_ATTR = "model";
	public final static String TUNNING_ATTR = "tunning";
	public final static String NUMBER_OF_HOLES_ATTR = "numberOfHoles";
	public final static String HARMONICA_TYPE_ATTR = "harmonicaType";
	
	
	//
	// Constructeur
	//

	public Harmonica(String name, HarmonicaModel model, Height tunning) {
		setName(name);
		setModel(model);
		setTunning(tunning);
	}
	
	public Harmonica(String name) {
		this(name, new HarmonicaModel(), new Height());
	}
	
	public Harmonica(HarmonicaModel model) {
		this(model.getName(), model, new Height());
	}
	
	public Harmonica() {
		this("");
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new HarmonicaRestoreCommand(this);
	}

	
	//
	// Getters / setters
	//
	
	public HarmonicaModel getModel() {
		return m_model;
	}
	
	public void setModel(HarmonicaModel model) {
		if (model == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_model, MODEL_ATTR);
		m_model = model;
		addAttributeChangesObserver(m_model, MODEL_ATTR);
		fireObjectChanged(MODEL_ATTR);
	}
	
	
	public Height getTunning() {
		return m_tunning;
	}
	
	public void setTunning(Height tunning) {
		if (tunning == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_tunning, TUNNING_ATTR);
		m_tunning = tunning;
		m_tunningOffset = m_tunning.getAlteredNoteId();
		addAttributeChangesObserver(m_tunning, TUNNING_ATTR);
		fireObjectChanged(TUNNING_ATTR);
	}
	
	
	public String getName() {
		return m_name;
	}
	
	public void setName(String name) {
		if (name == null)
			throw new NullPointerException();
		m_name = name;
		fireObjectChanged(NAME_ATTR);
	}
	
	
	//
	// R�cup�ration / affectation des �l�ments du mapping
	//
	
	public Height getHeight(Tab tab) {
		Height height = m_model.getHeight(tab);
		if (height == null)
			return null;
		try {
			return new Height(height.getSoundId() + m_tunningOffset);
		}
		catch (OutOfBoundsError e) {
			return null;
		}
	}
	
	public void setHeight(Tab tab, Height h) {
		Height height = new Height(h.getSoundId() - m_tunningOffset);
		m_model.setHeight(tab, height);
	}
	
	
	public Tab getTab(Height h) {
		Height height = new Height(h.getSoundId() - m_tunningOffset);
		return m_model.getTab(height);
	}
	
	public Collection<Tab> getTabPossibilities(Height h) {
		Height height = new Height(h.getSoundId() - m_tunningOffset);
		return m_model.getTabPossibilities(height);
	}
	
	
	public boolean isSet(Tab tab) {
		return m_model.isSet(tab);
	}
	
	
	//
	// S�rialisation / d�serialisation
	// RMQ: le mod�le d'harmonica est s�rialis� en partie ici.
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(HARMONICA_TYPESTR, hashCode());
		object.setAttribute(NAME_ATTR, getName());
		object.setElementAttribute(TUNNING_ATTR, m_tunning);
		object.setAttribute(NUMBER_OF_HOLES_ATTR, String.valueOf(m_model.getNumberOfHoles()));
		object.setAttribute(HARMONICA_TYPE_ATTR, m_model.getHarmonicaType().toString());
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setName(object.getAttribute(NAME_ATTR));
		setTunning((Height) object.getElementAttribute(TUNNING_ATTR));
		if (object.hasAttribute(NUMBER_OF_HOLES_ATTR))
			m_model.setNumberOfHoles(Integer.parseInt(object.getAttribute(NUMBER_OF_HOLES_ATTR)));
		if (object.hasAttribute(HARMONICA_TYPE_ATTR))
			m_model.setHarmonicaType(HarmonicaType.parseHarmonicaType(object.getAttribute(HARMONICA_TYPE_ATTR)));
	}
	
	
	//
	// Attributs
	//
	
	protected String m_name = null;
	protected HarmonicaModel m_model = null;
	protected Height m_tunning = null;
	protected int m_tunningOffset;
	
}


/**
 * Commande d'annulation des modifications d'un harmonica
 */
class HarmonicaRestoreCommand extends Harmonica implements RestoreCommand {
	
	public HarmonicaRestoreCommand(Harmonica saved) {
		m_saved = saved;
		m_name = m_saved.m_name;
		m_model = m_saved.m_model;
		m_tunning = m_saved.m_tunning;
		m_tunningOffset = m_saved.m_tunningOffset;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_name != m_name)
			m_saved.setName(m_name);
		if (m_saved.m_model != m_model)
			m_saved.setModel(m_model);
		if (m_saved.m_tunning != m_tunning)
			m_saved.setTunning(m_tunning);
		if (m_saved.m_tunningOffset != m_tunningOffset)
			m_saved.m_tunningOffset = m_tunningOffset;
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new HarmonicaRestoreCommand(m_saved);
	}
	
	private Harmonica m_saved;
}
