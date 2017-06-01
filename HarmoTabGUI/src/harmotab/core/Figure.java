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
import harmotab.io.*;
import harmotab.throwables.*;


/**
 * Mod�le d'une figure de note (donne une caract�ristique de dur�e)
 */
public class Figure extends Duration implements Cloneable {
	
	public final static String FIGURE_TYPESTR = "figure";
	public final static String TYPE_ATTR = "type";
	public final static String DOTTED_ATTR = "dotted";
	public final static String TRIPLET_ATTR = "triplet";
		
	public final static byte WHOLE = 1;				// Ronde
	public final static byte HALF = 2;				// Blanche
	public final static byte QUARTER = 3;			// Noire
	public final static byte EIGHTH = 4;			// Croche
	public final static byte SIXTEENTH = 5;			// Double croche
	public final static byte APPOGIATURE = 6;		// Appogiature

	public final static byte MIN_FIGURE_ID = 1;
	public final static byte MAX_FIGURE_ID = 6;
	public final static byte FIGURES_NUMBER = 6;
	
	private final static String WHOLE_STRING = "whole";
	private final static String HALF_STRING = "half";
	private final static String QUARTER_STRING = "quarter";
	private final static String EIGHTH_STRING = "eighth";
	private final static String SIXTEENTH_STRING = "sixteenth";
	private final static String APPOGIATURE_STRING = "appogiature";
	
	private final static byte DEFAULT_TYPE = QUARTER;
	public final static boolean DEFAULT_DOTTED = false;
	public final static boolean DEFAULT_TRIPLET = false;
	
	public final float TRIPLET_FACTOR = ((1.0f / 3.0f) * 2.0f) + 0.000001f;
	public final float DOT_FACTOR = 1.5f;
	
	
	//
	// Constructeurs
	//
	
	public Figure() {
		try {
			setType(DEFAULT_TYPE);
			setDotted(DEFAULT_DOTTED);
			setTriplet(DEFAULT_TRIPLET);
		} catch (OutOfBoundsError e) {
			throw new BrokenImplementationError("Bad figure default type !");
		}
	}
	
	
	public Figure(byte type) throws OutOfBoundsError {
		setType(type);
		setDotted(DEFAULT_DOTTED);
		setTriplet(DEFAULT_TRIPLET);
	}
	
	
	public Figure(float time) {
		if (time >= 4.0f) {
			setType(WHOLE);
		}
		else if (time >= 3.0f) {
			setType(HALF);
			setDotted(true);
		}
		else if (time >= 2.0f) {
			setType(HALF);
		}
		else if (time >= 1.5f) {
			setType(QUARTER);
			setDotted(true);
		}
		else if (time >= 1.0f) {
			setType(QUARTER);
		}
		else if (time >= 0.75f) {
			setType(EIGHTH);
			setDotted(true);
		}
		else if (time >= 0.5f) {
			setType(EIGHTH);
		}
		else if (time >= 0.375f) {
			setType(SIXTEENTH);
			setDotted(true);
		}
		else {// if (time >= 0.25f)
			setType(SIXTEENTH);
		}
//		else { 
//			setType(APPOGIATURE);
//		}
	}
	
	
	public Figure(String type) {
		setType(type);
		setDotted(DEFAULT_DOTTED);
		setTriplet(DEFAULT_TRIPLET);
	}
	
	
	@Override
	public Object clone() {
		return super.clone();
	}

	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new FigureRestoreCommand(this);
	}

		
	//
	// Getters / setters
	//
	
	public byte getType() {
		return m_type;
	}
	
	public String getFigureTypeStr() {
		switch (m_type) {
			case WHOLE:			return WHOLE_STRING;
			case HALF:			return HALF_STRING;
			case QUARTER:		return QUARTER_STRING;
			case EIGHTH:		return EIGHTH_STRING;
			case SIXTEENTH:		return SIXTEENTH_STRING;
			case APPOGIATURE:	return APPOGIATURE_STRING;
			default: throw new UnhandledCaseError("Cannot convert figure as a string !");
		}
	}

	
	
	public void setType(byte type) throws OutOfBoundsError {
		if (type < MIN_FIGURE_ID || type > MAX_FIGURE_ID)
			throw new OutOfBoundsError("Out of bounds figure's type identifier !");
		
		m_type = type;
		switch (m_type) {
			case WHOLE:			setDuration(4.0f);		break;
			case HALF:			setDuration(2.0f);		break;
			case QUARTER:		setDuration(1.0f);		break;
			case EIGHTH:		setDuration(0.5f);		break;
			case SIXTEENTH:		setDuration(0.25f);		break;
			case APPOGIATURE:	setDuration(0.0f);		break;
			default: throw new UnhandledCaseError("Cannot get figure's duration !");
		}
		fireObjectChanged(TYPE_ATTR);
	}
	
	public void setType(String string) throws OutOfSpecificationError {
		try {
			if (string.equals(WHOLE_STRING))
				setType(WHOLE);
			else if (string.equals(HALF_STRING))
				setType(HALF);
			else if (string.equals(QUARTER_STRING))
				setType(QUARTER);
			else if (string.equals(EIGHTH_STRING))
				setType(EIGHTH);
			else if (string.equals(SIXTEENTH_STRING))
				setType(SIXTEENTH);
			else if (string.equals(APPOGIATURE_STRING))
				setType(APPOGIATURE);
			else
				throw new OutOfSpecificationError("Invalid type string identifier !");
		} catch (OutOfBoundsError e) {
			throw new BrokenImplementationError("Hard coded note type does not exists !");
		}
	}
	
	
	public boolean isDotted() {
		return m_isDotted;
	}
	
	public void setDotted(boolean dotted) {
		m_isDotted = dotted;
		fireObjectChanged(DOTTED_ATTR);
	}
	
	
	public boolean isTriplet() {
		return m_isTriplet;
	}
	
	public void setTriplet(boolean triplet) {
		m_isTriplet = triplet;
		fireObjectChanged(TRIPLET_ATTR);
	}
	
	
	@Override 
	public float getDuration() {
		float duration = super.getDuration();
		
		if (m_isDotted)
			duration *= DOT_FACTOR;
		if (m_isTriplet)
			duration *= TRIPLET_FACTOR;
		
		return duration;
	}
	
	
	//
	// Méthodes utilitaires
	//	

	public String getLocalizedName() {
		switch (m_type) {
			case WHOLE:			return Localizer.get(i18n.N_NOTE_WHOLE);
			case HALF:			return Localizer.get(i18n.N_NOTE_HALF);
			case QUARTER:		return Localizer.get(i18n.N_NOTE_QUARTER);
			case EIGHTH:		return Localizer.get(i18n.N_NOTE_EIGHTH);
			case SIXTEENTH:		return Localizer.get(i18n.N_NOTE_SIXTEENTH);
			case APPOGIATURE:	return Localizer.get(i18n.N_NOTE_APPOGIATURE);
			default: throw new UnhandledCaseError("Cannot convert figure as a name !");
		}
	}

	
	public float getWidth() {
		float width = 0;
		switch (m_type) {
			case WHOLE:			width = 3.0f;		break;
			case HALF:			width = 1.5f;		break;
			case QUARTER:		width = 1.0f;		break;
			case EIGHTH:		width = 0.5f;		break;
			case SIXTEENTH:		width = 0.5f;		break;
			case APPOGIATURE:	width = 0.3f;		break;
			default: throw new UnhandledCaseError("Cannot get figure's duration !");
		}
//		if (isDotted() && m_type < SIXTEENTH)
//			width *= DOT_FACTOR;
//		if (isTriplet() && m_type < SIXTEENTH)
//			width *= TRIPLET_FACTOR;
		return width;
	}
	
	
	/**
	 * Indique si la note est accrochable (croche ou double croche)
	 */
	public boolean isHookable() {
		return m_type > Figure.QUARTER && m_type != Figure.APPOGIATURE;
	}
	
	
	//
	// Serialisation / d�serialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = serializer.createSerializedObject(FIGURE_TYPESTR, hashCode());
		object.setAttribute(TYPE_ATTR, getFigureTypeStr());
		if (isDotted() != DEFAULT_DOTTED)
			object.setAttribute(DOTTED_ATTR, isDotted()+"");
		if (isTriplet() != DEFAULT_TRIPLET)
			object.setAttribute(TRIPLET_ATTR, isTriplet()+"");
		return object;
	}

	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setType(object.getAttribute(TYPE_ATTR));
		setDotted(object.hasAttribute(DOTTED_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(DOTTED_ATTR)) :
			DEFAULT_DOTTED);
		setTriplet(object.hasAttribute(TRIPLET_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(TRIPLET_ATTR)) :
			DEFAULT_TRIPLET);
	}	
	
				
	//
	// Attributs
	//
	
	protected byte m_type;
	protected boolean m_isDotted;
	protected boolean m_isTriplet;
	
}


//
//Commande d'annulation
//

class FigureRestoreCommand extends Figure implements RestoreCommand {
	
	public FigureRestoreCommand(Figure saved) {
		m_saved = saved;
		m_type = saved.m_type;
		m_isDotted = saved.m_isDotted;
		m_isTriplet = saved.m_isTriplet;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_type != m_type)
			m_saved.setType(m_type);
		if (m_saved.m_isDotted != m_isDotted)
			m_saved.setDotted(m_isDotted);
		if (m_saved.m_isTriplet != m_isTriplet)
			m_saved.setTriplet(m_isTriplet);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new FigureRestoreCommand(m_saved);
	}
	
	private Figure m_saved;
}
