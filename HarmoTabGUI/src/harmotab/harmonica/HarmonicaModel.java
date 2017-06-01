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

import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.element.Tab;
import harmotab.io.*;
import harmotab.throwables.*;
import java.util.*;


/**
 * Modèle d'accordage d'harmonica.
 * Contient pour chanque trou d'un harmonica les note qu'il produit en fonction
 * de l'aspiration ou du souffle.
 * <!> Les trous de l'harmonica sont numérotés de 1 à n.<!>
 * @internal Les valeurs sont stockées de 0 à n-1
 */
public class HarmonicaModel extends HarmoTabObject {
	
	public final static String HARMONICA_MODEL_TYPESTR = "harmonicaModel";
	public final static String NAME_ATTR = "name";
	public final static String NUMBER_OF_HOLES_ATTR = "numberOfHoles";
	public final static String CONTENT_ATTR = "content";
	public final static String HARMONICA_TYPE_ATTR = "harmonicaType";
	
		
	public final static int MIN_HOLE_VALUE = 1;
	public final static int MAX_HOLE_VALUE = 50;
	
	public final static int MIN_NUMBER_OF_HOLES = 4;
	public final static int MAX_NUMBER_OF_HOLES = 50;

	private final static String DEFAULT_NAME = "";
	private final static int DEFAULT_NUMBER_OF_HOLES = 10;
	private final static HarmonicaType DEFAULT_HARMONICA_TYPE = HarmonicaType.DIATONIC;

	private final static byte NUMBER_OF_BREATH_PER_HOLE = 6;
	
	
	//
	// Constructeur
	//
	
	public HarmonicaModel(String name, int numberOfHoles) {
		initModel();
		setName(name);
		setNumberOfHoles(numberOfHoles);
		setHarmonicaType(DEFAULT_HARMONICA_TYPE);
	}
	
	public HarmonicaModel() {
		this(DEFAULT_NAME, DEFAULT_NUMBER_OF_HOLES);
	}
	
	private void initModel() {
		m_naturalModel = new Vector<Vector<Height>>(DEFAULT_NUMBER_OF_HOLES);
		m_pushedModel = new Vector<Vector<Height>>(DEFAULT_NUMBER_OF_HOLES);
	}
	
	
	public void resetModel() {
		initModel();
		setNumberOfHoles(DEFAULT_NUMBER_OF_HOLES);
		setName(DEFAULT_NAME);
		fireObjectChanged(CONTENT_ATTR);
	}

	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new HarmonicaModelRestoreCommand(this);
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
	
	
	public int getNumberOfHoles() {
		return m_numberOfHoles;
	}
	
	public void setNumberOfHoles(int numberOfHoles) throws OutOfBoundsError {
		if (numberOfHoles < MIN_NUMBER_OF_HOLES || numberOfHoles > MAX_NUMBER_OF_HOLES)
			throw new OutOfBoundsError("Invalid number of holes (" + numberOfHoles + ") !");
		
		m_numberOfHoles = numberOfHoles;
		// Ajustement de la taille des tableaux contenant 
		m_naturalModel.setSize(NUMBER_OF_BREATH_PER_HOLE);
		m_pushedModel.setSize(NUMBER_OF_BREATH_PER_HOLE);
		
		for (int i = m_naturalModel.size()-1; i >=0 ; i--) {
			if (m_naturalModel.elementAt(i) == null) {
				m_naturalModel.set(i, new Vector<Height>(DEFAULT_NUMBER_OF_HOLES));
				m_pushedModel.set(i, new Vector<Height>(DEFAULT_NUMBER_OF_HOLES));
			}
		}
		
		for (int i = 0; i < NUMBER_OF_BREATH_PER_HOLE; i++) {
			m_naturalModel.elementAt(i).setSize(numberOfHoles);
			m_pushedModel.elementAt(i).setSize(numberOfHoles);
		}
		
		fireObjectChanged(NUMBER_OF_HOLES_ATTR);
	}
	
	
	public HarmonicaType getHarmonicaType() {
		return m_harmonicaType;
	}
	
	public void setHarmonicaType(HarmonicaType type) {
		m_harmonicaType = type;
		fireObjectChanged(HARMONICA_TYPE_ATTR);
	}
	
	
	public Height getHeight(Tab tab) {
		if (tab == null)
			throw new NullPointerException();
		int hole = tab.getHole();
		if (hole == Tab.UNDEFINED || tab.getDirection() == Tab.UNDEFINED)
			throw new IllegalArgumentException("Undefined tab.");
		if (hole < MIN_HOLE_VALUE || hole > m_numberOfHoles)
			throw new IllegalArgumentException("Invalid hole number (" + tab.getHole() + ") !");
		
		Height res = null;
		if (tab.isPushed()) {
			res = m_pushedModel.elementAt(getBreathIndex(tab)).elementAt(hole-1);
		}
		else {
			res = m_naturalModel.elementAt(getBreathIndex(tab)).elementAt(hole-1);
		}

		return (res != null ? new Height(res) : null);
	}
	
	public void setHeight(Tab tab, Height height) {
		if (tab == null || height == null)
			throw new NullPointerException();
		int hole = tab.getHole();
		if (hole == Tab.UNDEFINED || tab.getDirection() == Tab.UNDEFINED)
			throw new IllegalArgumentException("Undefined tab.");
		if (hole < MIN_HOLE_VALUE || hole > m_numberOfHoles)
			throw new IllegalArgumentException("Invalid hole number (" + hole + ").");
		
		if (tab.isPushed()) {
			m_pushedModel.elementAt(getBreathIndex(tab)).set(hole-1, new Height(height));
		}
		else {
			m_naturalModel.elementAt(getBreathIndex(tab)).set(hole-1, new Height(height));
		}
		
		fireObjectChanged(CONTENT_ATTR);
	}

	
	public boolean isSet(Tab tab) {
		if (tab == null)
			throw new NullPointerException();
		int hole = tab.getHole();
		if (hole == Tab.UNDEFINED || tab.getDirection() == Tab.UNDEFINED)
			throw new IllegalArgumentException("Undefined tab.");
		if (hole < MIN_HOLE_VALUE || hole > m_numberOfHoles)
			throw new IllegalArgumentException("Invalid hole number (" + hole + ").");
		
		if (tab.isPushed()) {
			return m_pushedModel.elementAt(getBreathIndex(tab)).get(hole-1) != null;
		}
		else {
			return m_naturalModel.elementAt(getBreathIndex(tab)).get(hole-1) != null;
		}
	}
	
	public void unset(Tab tab) {
		if (tab == null)
			throw new NullPointerException();
		int hole = tab.getHole();
		if (hole == Tab.UNDEFINED || tab.getDirection() == Tab.UNDEFINED)
			throw new IllegalArgumentException("Undefined tab.");
		if (hole < MIN_HOLE_VALUE || hole > m_numberOfHoles)
			throw new IllegalArgumentException("Invalid hole number (" + hole + ").");
		
		if (tab.isPushed()) {
			m_pushedModel.elementAt(getBreathIndex(tab)).set(hole-1, null);
		}
		else {
			m_naturalModel.elementAt(getBreathIndex(tab)).set(hole-1, null);
		}
		fireObjectChanged(CONTENT_ATTR);
	}
	
	
	public Tab getTab(Height height) {
		if (height == null)
			throw new NullPointerException();
		
		int holesNumber = getNumberOfHoles();
		for (int hole = 0; hole < holesNumber; hole++) {
			for (byte j = 0; j < NUMBER_OF_BREATH_PER_HOLE; j++) {
				// Recherche dans le mod�le tirette non activ�e
				Height naturalModelHeight = m_naturalModel.elementAt(j).get(hole);
				if (naturalModelHeight != null && naturalModelHeight.getSoundId() == height.getSoundId())
					return createTab(hole+1, j);
				// Recherche dans le mod�le tirette activ�e
				Height pushedModelHeight = m_pushedModel.elementAt(j).get(hole);
				if (pushedModelHeight != null && pushedModelHeight.getSoundId() == height.getSoundId())
					return createTab(hole+1, j, true);
			}
		}
		return null;
	}
	
	
	public Collection<Tab> getTabPossibilities(Height height) {
		if (height == null)
			throw new NullPointerException();
		
		ArrayList<Tab> list = new ArrayList<Tab>();
		int holesNumber = getNumberOfHoles();
		for (int hole = 0; hole < holesNumber; hole++) {
			for (byte j = 0; j < NUMBER_OF_BREATH_PER_HOLE; j++) {
				// Recherche dans le mod�le tirette non activ�e
				Height naturalModelHeight = m_naturalModel.elementAt(j).get(hole);
				if (naturalModelHeight != null && naturalModelHeight.getAlteredNoteId() == height.getAlteredNoteId())
					list.add(createTab(hole+1, j));
				// Recherche dans le mid�le tirette activ�e
				Height pushedModelHeight = m_pushedModel.elementAt(j).get(hole);
				if (pushedModelHeight != null && pushedModelHeight.getAlteredNoteId() == height.getAlteredNoteId())
					list.add(createTab(hole+1, j));				
			}
		}
		return list;
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	private static int getBreathIndex(Tab tab) {
		if (tab.getDirection() == Tab.BLOW) {
			switch (tab.getBend()) {
				case Tab.NONE:			return 2;
				case Tab.HALF_BEND:		return 1;
				case Tab.FULL_BEND:		return 0;
			}
		}
		else if (tab.getDirection() == Tab.DRAW) {
			switch (tab.getBend()) {
			case Tab.NONE:			return 3;
			case Tab.HALF_BEND:		return 4;
			case Tab.FULL_BEND:		return 5;
			}
		}
		throw new IllegalArgumentException("Direction not defined.");
	}
	
	
	public static Tab createTab(int hole, byte type) {
		switch (type) {
			case 0:	return new Tab(hole, Tab.BLOW, Tab.FULL_BEND);
			case 1:	return new Tab(hole, Tab.BLOW, Tab.HALF_BEND);
			case 2:	return new Tab(hole, Tab.BLOW, Tab.NONE);
			case 3:	return new Tab(hole, Tab.DRAW, Tab.NONE);
			case 4:	return new Tab(hole, Tab.DRAW, Tab.HALF_BEND);
			case 5:	return new Tab(hole, Tab.DRAW, Tab.FULL_BEND);
		}
		return null;
	}
	
	public static Tab createTab(int hole, byte type, boolean pushed) {
		Tab tab = createTab(hole, type);
		if (tab != null)
			tab.setPushed(pushed);
		return tab;
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		throw new NotImplementedError("HarmonicaModel serialization/deserialization not implemented.");
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		throw new NotImplementedError("HarmonicaModel serialization/deserialization not implemented.");
	}
	
	
	//
	// Attributs
	//
	
	protected String m_name;
	protected int m_numberOfHoles;
	protected HarmonicaType m_harmonicaType;
	
	protected Vector<Vector<Height>> m_naturalModel = null;
	protected Vector<Vector<Height>> m_pushedModel = null;
	
}



/**
 * Commande d'annulation des modifications d'un harmonica
 */
class HarmonicaModelRestoreCommand extends HarmonicaModel implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public HarmonicaModelRestoreCommand(HarmonicaModel saved) {
		m_saved = saved;
		m_name = m_saved.m_name;
		m_numberOfHoles = m_saved.m_numberOfHoles;
		m_harmonicaType = m_saved.m_harmonicaType;
		m_naturalModel = (Vector<Vector<Height>>) m_saved.m_naturalModel.clone();
		m_pushedModel = (Vector<Vector<Height>>) m_saved.m_pushedModel.clone();
	}
	
	@Override
	public void execute() {
		if (m_saved.m_name != m_name)
			m_saved.setName(m_name);
		if (m_saved.m_numberOfHoles != m_numberOfHoles)
			m_saved.setNumberOfHoles(m_numberOfHoles);
		if (m_saved.m_harmonicaType != m_harmonicaType)
			m_saved.setHarmonicaType(m_harmonicaType);
		if (m_saved.m_naturalModel != m_naturalModel)
			m_saved.m_naturalModel = m_naturalModel;
		if (m_saved.m_pushedModel != m_pushedModel)
			m_saved.m_pushedModel = m_pushedModel;
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new HarmonicaModelRestoreCommand(m_saved);
	}
	
	private HarmonicaModel m_saved;
}
