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
import harmotab.throwables.*;


/**
 * Mod�le d'une signature temporelle
 */
public class TimeSignature extends TrackElement {
	
	public static final String NUMBER_ATTR = "number";
	public static final String REFERENCE_ATTR = "reference";

	public static final byte MIN_NUMBER = 1;
	public static final byte MAX_NUMBER = 16;
	public static final byte DEFAULT_NUMBER = 4;
	
	public static final byte MIN_REFERENCE = 1;
	public static final byte MAX_REFERENCE = 16;
	public static final byte DEFAULT_REFERENCE = 4;
	
	
	//
	// Constructeurs
	//
	
	public TimeSignature() {
		super(Element.TIME_SIGNATURE);
	}
	
	public TimeSignature(byte number, byte reference) {
		super(Element.TIME_SIGNATURE);
		setNumber(number);
		setReference(number);
	}
	
	
	@Override
	public Object clone() {
		return super.clone();
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new TimeSignatureRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public byte getNumber() {
		return m_number;
	}
	
	public void setNumber(byte number) throws OutOfBoundsError {
		if (number < MIN_NUMBER || number > MAX_NUMBER)
			throw new OutOfBoundsError("Invalid time signature number !");
		
		m_number = number;
		fireObjectChanged(NUMBER_ATTR);
	}
	
	
	public byte getReference() {
		return m_reference;
	}
	
	
	public void setReference(byte reference) throws OutOfBoundsError, OutOfSpecificationError {
		if (reference < MIN_NUMBER || reference > MAX_NUMBER)
			throw new OutOfBoundsError("Invalid time signature reference !");
		if ((reference & (reference - 1)) != 0)	// Vérifie que le nombre est une puissance de 2 
			throw new OutOfSpecificationError("Time signature reference must be 1, 2, 4, 8, 16 or 32");
		
		m_reference = reference;
		fireObjectChanged(REFERENCE_ATTR);
	}

	
	//
	// Méthodes utilitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_TIME_SIGNATURE);
	}
	
	
	public static boolean isReferenceValid(byte reference) {
		if (reference < MIN_NUMBER || reference > MAX_NUMBER)
			return false;
		if ((reference & (reference - 1)) == 0)	// Vérifie que le nombre est une puissance de 2 
			return true;
		return false;
	}
	
	
	/**
	 * Retourne le nombre de temps par mesure
	 */
	public float getTimesPerBar() {
		float factor = 0;
		switch (m_reference) {
			case 1:		factor = 4.0f;		break;
			case 2:		factor = 2.0f;		break;
			case 4:		factor = 1.0f;		break;
			case 8:		factor = 0.5f;		break;
			case 16:	factor = 0.25f;		break;
		}
		return factor * m_number;
	}
	
	/**
	 * Retourne le nombre de bats par mes
	 */
	public int getBeatsPerBar() {
		return (int) (getTimesPerBar() / getTimesPerBeat());
	}
	
	/**
	 * Indique si le temps est un temps fort
	 */
	public boolean isStrongBeat(float time) {
		return (time - (float)(Math.floor(time) * getTimesPerBeat()) == 0.0f);
	}
	
	/**
	 * Retourne le nombre de temps � chaque battement
	 */
	public float getTimesPerBeat() {
		switch (m_reference) {
			case 1:	case 2: case 4:
				return 1.0f;
			case 8: case 12: case 16:
				return 1.5f;
			default:
				throw new UnhandledCaseError("TimeSignature::getBeatPeriod: Reference not handled !");
		}	
	}
	
	
	@Override
	public float getWidthUnit() {
		return 0.5f;
	}
	
	
	//
	// Serialisation / déserialisation xml
	//

	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(REFERENCE_ATTR, getReference()+"");
		object.setAttribute(NUMBER_ATTR, getNumber()+"");
		return object;	
	}
	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setReference(object.hasAttribute(REFERENCE_ATTR) ?
			Byte.decode(object.getAttribute(REFERENCE_ATTR)) :
			DEFAULT_REFERENCE);
		setNumber(object.hasAttribute(NUMBER_ATTR) ?
			Byte.decode(object.getAttribute(NUMBER_ATTR)) :
			DEFAULT_NUMBER);
	}
	

	//
	// Attributs
	//
	
	protected byte m_number = DEFAULT_NUMBER;
	protected byte m_reference = DEFAULT_REFERENCE;
	
}


/**
 * Commande d'annulation des modifications d'une signature temporelle
 */
class TimeSignatureRestoreCommand extends TimeSignature implements RestoreCommand {
	
	public TimeSignatureRestoreCommand(TimeSignature saved) {
		m_saved = saved;
		m_number = m_saved.m_number;
		m_reference = m_saved.m_reference;
	}
	
	@Override
	public void execute() {
		if (m_number != m_saved.m_number)
			m_saved.setNumber(m_number);
		if (m_reference != m_saved.m_reference)
			m_saved.setReference(m_reference);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new TimeSignatureRestoreCommand(m_saved);
	}
	
	private TimeSignature m_saved;
}

