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

import harmotab.core.undo.RestoreCommand;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.OutOfBoundsError;


/**
 * Mod�le d'un tempo
 */
public class Tempo extends Element {
	
	public static final String VALUE_ATTR = "value";
	
	public static final int MIN_TEMPO_VALUE = 10;
	public static final int MAX_TEMPO_VALUE = 500;
	public static final int DEFAULT_TEMPO_VALUE = 120;
	

	//
	// Constructeurs
	//
	
	public Tempo() {
		super(Element.TEMPO);
		setValue(DEFAULT_TEMPO_VALUE);
	}
	
	public Tempo(int tempo) {
		super(Element.TEMPO);
		setValue(tempo);
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new TempoRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public int getValue() {
		return m_tempo;
	}
	
	public void setValue(int tempo) {
		if (tempo < MIN_TEMPO_VALUE || tempo > MAX_TEMPO_VALUE)
			throw new OutOfBoundsError("Bad tempo value '" + tempo + "'.");
		m_tempo = tempo;
		fireObjectChanged(VALUE_ATTR);
	}
	
	
	//
	// M�thodes objet
	//
	
	/**
	 * Retourne le temps entre 2 battements (ou dur�e d'un temps)
	 */
	public float getBeatPeriodInSeconds() {
		return 60.0f / ((float) m_tempo);
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(VALUE_ATTR, getValue()+"");
		return object;
	}
	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setValue(Integer.parseInt(object.getAttribute(VALUE_ATTR)));
	}
		
	
	//
	// Attributs
	//
	
	protected int m_tempo;

}


/**
 * Commande d'annulation des modifications d'un tempo
 */
class TempoRestoreCommand extends Tempo implements RestoreCommand {
	
	public TempoRestoreCommand(Tempo saved) {
		m_saved = saved;
		m_tempo = m_saved.m_tempo;
	}
	
	@Override
	public void execute() {
		if (m_tempo != m_saved.m_tempo)
			m_saved.setValue(m_tempo);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new TempoRestoreCommand(m_saved);
	}
	
	private Tempo m_saved;
}

