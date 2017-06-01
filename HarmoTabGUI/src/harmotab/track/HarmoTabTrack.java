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
import harmotab.harmonica.*;
import harmotab.io.*;
import harmotab.track.layout.*;


/**
 * Piste compos�e d'une port�e et d'une ligne de tablatures
 */
public class HarmoTabTrack extends StaffTrack {
	
	public static String HARMOTAB_TRACK_TYPESTR = "harmoTabTrack";
	public static String TAB_MODEL_ATTR = "tabModel";
	public static String HARMONICA_ATTR = "harmonica";
	

	//
	// Constructeur
	//
	
	protected HarmoTabTrack() {}
	

	public HarmoTabTrack(Score score) {
		super(score);
		setTrackLayout(new HarmoTabTrackLayout(this));
		setTabModel(new TabModel());
		setHarmonica(new Harmonica());
		setName(Localizer.get(i18n.N_HARMOTAB_TRACK));
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new HarmoTabTrackRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setTabModel(TabModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		
		m_tabModel = model;
		
		if (m_tabAutomationProcessor == null) {
			m_tabAutomationProcessor = new TabAutomationProcessor(m_tabModel);
		}
		else {
			m_tabAutomationProcessor.setTabModel(m_tabModel);
		}
		
		fireObjectChanged(TAB_MODEL_ATTR);
	}
		
	public TabModel getTabModel() {
		return m_tabModel;
	}
	
	
	public void setHarmonica(Harmonica harmonica) {
		if (harmonica == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_harmonica, HARMONICA_ATTR);
		m_harmonica = harmonica;
		addAttributeChangesObserver(m_harmonica, HARMONICA_ATTR);
		fireObjectChanged(HARMONICA_ATTR);
	}
	
	public Harmonica getHarmonica() {
		return m_harmonica;
	}
	
	
	//
	// Gestion des fonctionnalit� d'automatisation
	//
	
	public void add(Element element) {
		if (element instanceof HarmoTabElement) {
			element.addObjectListener(m_tabAutomationProcessor);
			m_tabAutomationProcessor.doAutoTab((HarmoTabElement) element);
		}
		super.add(element);
	}
	
	public void add(int index, Element element) {
		if (element instanceof HarmoTabElement) {
			element.addObjectListener(m_tabAutomationProcessor);
			m_tabAutomationProcessor.doAutoTab((HarmoTabElement) element);
		}
		super.add(index, element);
	}
	
	
	//
	// M�thodes de contr�le
	//
	
	@Override
	public Collection<TrackElement> getAddableElements() {
		ArrayList<TrackElement> list = new ArrayList<TrackElement>();
		list.add(new HarmoTabElement());
		list.add(new Bar());
		return list;
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute("type", HARMOTAB_TRACK_TYPESTR);

		// Enregistrement de l'harmonica et du mod�le de tablatures
		if (m_harmonica != null)
			object.setElementAttribute(HARMONICA_ATTR, m_harmonica);
		if (m_tabModel != null)
			object.setElementAttribute(TAB_MODEL_ATTR, m_tabModel);
		
		return object;
	}
	
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		// R�cup�ration de l'harmonica
		if (object.hasAttribute(HARMONICA_ATTR))
			setHarmonica((Harmonica) object.getElementAttribute(HARMONICA_ATTR));
		// R�cup�ration du mod�le de tablatures
		if (object.hasAttribute(TAB_MODEL_ATTR))
			setTabModel((TabModel) object.getElementAttribute(TAB_MODEL_ATTR));
		
		super.deserialize(serializer, object);
		
		// Compl�te le mod�le de tablatures de la piste HarmoTab
		TabModelController tabModelController = new TabModelController(m_tabModel);
		tabModelController.populateFromHarmoTabTrack(this);
	}

	
	//
	// Attributs
	//
	
	protected TabModel m_tabModel;
	protected Harmonica m_harmonica;
	
	private TabAutomationProcessor m_tabAutomationProcessor = null;
	
}



/**
 * Commande d'annulation des modifications d'une piste
 */
class HarmoTabTrackRestoreCommand extends HarmoTabTrack implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public HarmoTabTrackRestoreCommand(HarmoTabTrack saved) {
		m_saved = saved;
		m_name = m_saved.m_name;
		m_instrument = m_saved.m_instrument;
		m_comment = m_saved.m_comment;
		m_volumePercentage = m_saved.m_volumePercentage;
		m_elements = (LinkedList<Element>) m_saved.m_elements.clone();
		m_tabModel = m_saved.m_tabModel;
		m_harmonica = m_saved.m_harmonica;
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
		if (m_saved.m_tabModel != m_tabModel)
			m_saved.setTabModel(m_tabModel);
		if (m_saved.m_harmonica != m_harmonica)
			m_saved.setHarmonica(m_harmonica);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new HarmoTabTrackRestoreCommand(m_saved);
	}
	
	private HarmoTabTrack m_saved;
}

