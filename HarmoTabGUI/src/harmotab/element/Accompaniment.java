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
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.OutOfBoundsError;
import java.util.Collection;
import java.util.Vector;


/**
 * Mod�le d'un accompagnement compos� d'accords
 */
public class Accompaniment extends TrackElement implements Cloneable, Comparable<Accompaniment> {
	
	public final static String CHORD_ATTR = "chord";
	public final static String RHYTHMIC_ATTR = "rhythmic";
	public final static String DURATION_ATTR = "duration";
	
	public final static int MIN_REPEAT_NUMBER = 1;
	public final static int MAX_REPEAT_NUMBER = 100;
	

	//
	// Constructeurs
	//
	
	public Accompaniment() {
		super(Element.ACCOMPANIMENT);
		setChord(new Chord());
		setCustomDuration(new Duration());
	}

	public Accompaniment(Chord chord) {
		super(Element.ACCOMPANIMENT);
		setChord(chord);
		setCustomDuration(new Duration());
	}
	
	public Accompaniment(Chord chord, Figure figure, int repeat) {
		super(Element.ACCOMPANIMENT);
		setChord(chord);
		setRhythmic(figure, repeat);
	}
	
	public Accompaniment(Chord chord, Duration customDuration) {
		super(Element.ACCOMPANIMENT);
		setChord(chord);
		setCustomDuration(customDuration);
	}

	
	@Override
	public int compareTo(Accompaniment acc) {
		if (acc != null) {
			// Chord
			if (! m_chord.equals(acc.m_chord))
				return m_chord.compareTo(acc.m_chord);
			// Rhythmic
			if (m_rhythmic.size() != acc.m_rhythmic.size())
				return m_rhythmic.size() - acc.m_rhythmic.size();
			for (int i = 0; i < m_rhythmic.size(); i++) {
				Duration duration = m_rhythmic.get(i);
				Duration accDuration = acc.m_rhythmic.get(i);
				if (duration.getDuration() != accDuration.getDuration())
					return duration.getDuration() > accDuration.getDuration() ? 1 : -1;
			}
			return 0;
		}
		return 1;
	}
	
	
	@Override
	public Object clone() {
		Accompaniment acc = (Accompaniment) super.clone();
		
		Chord c = (Chord) m_chord.clone();
		acc.m_chord = null;
		acc.setChord(c);
		
		acc.m_rhythmic = new Vector<Duration>();
		for (Duration d : m_rhythmic)
			acc.m_rhythmic.add((Duration) d.clone());
		
		return acc;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new AccompanimentRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setChord(Chord chord) {
		if (chord == null)
			throw new NullPointerException();
		removeAttributeChangesObserver(m_chord, CHORD_ATTR);
		m_chord = chord;
		addAttributeChangesObserver(m_chord, CHORD_ATTR);
		fireObjectChanged(CHORD_ATTR);
	}
	
	public Chord getChord() {
		return m_chord;
	}
	
	
	public Collection<Duration> getRhythmic() {
		return m_rhythmic;
	}
	
	public void setRhythmic(Figure figure, int repeat) {
		if (repeat < MIN_REPEAT_NUMBER || repeat > MAX_REPEAT_NUMBER)
			throw new OutOfBoundsError("Bad repeats time value (" + repeat + ")");
		m_rhythmic.clear();
		while (repeat-- > 0)
			m_rhythmic.add((Figure) figure.clone());
		m_hasCustomDuration = false;
		fireObjectChanged(RHYTHMIC_ATTR);
	}
	
	public void appendRhythmicFigure(Figure figure) {
		m_rhythmic.add((Figure) figure.clone());
		fireObjectChanged(RHYTHMIC_ATTR);
	}
	
	
	public void setCustomDuration(Duration duration) {
		if (duration == null)
			throw new NullPointerException();
		m_rhythmic.clear();
		m_hasCustomDuration = true;
		m_rhythmic.add(duration);
		fireObjectChanged(DURATION_ATTR);
	}
	
	public void setCustomDuration(float duration) {
		setCustomDuration(new Duration(duration));
	}

	
	//
	// Méthodes utilitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_ACCOMPANIMENT);
	}
	
	/**
	 * Retourne la dur�e totale de l'accompagnement.
	 */
	@Override
	public float getDuration() {
		float value = 0.0f;
		for( Duration duration : m_rhythmic )
			value += duration.getDuration();
		return value;
	}
	
	/**
	 * Indique si la rythmique de l'accompagnement est bas�e sur une s�quence
	 * de figure ou sur une dur�e fixe.
	 * @return Vrai si la rythmique de l'accompagnement est bas�e sur une dur�e fixe.
	 */
	public boolean hasCustomDuration() {
		 return m_hasCustomDuration;
	}
	
	/**
	 * Indique si la rythmique de l'accompagnement est bas�e sur la r�p�tition
	 * d'une figure ou sur un autre type de rythmique.
	 * @return 	Vrai si la rythmique de l'accompagnement est bas�e sur la r�p�tition
	 * 			d'une figure.
	 */
	public boolean isOneFigureRepeated() {
		if (m_hasCustomDuration)
			return false;
		Duration firstDuration = null;
		if (m_rhythmic.size() == 0)
			return false;
		for (Duration duration : m_rhythmic) {
			if (firstDuration == null)
				firstDuration = duration;
			if (duration.getDuration() != firstDuration.getDuration())
				return false;
		}
		return true;
	}
	
	/**
	 * Retourne le nombre de r�p�titions de la figure si la rythmique est bas�e
	 * sur la r�p�tition d'une figure, sinon 0.
	 * @return
	 */
	public int getRepeatTime() {
		return (isOneFigureRepeated() ? m_rhythmic.size() : 0);
	}
	
	/**
	 * Retourne la figure de la rythmique si la rythmique de l'accompagnement
	 * est bas� sur la r�petition d'une figure, sinon null.
	 * @return
	 */
	public Figure getRepeatedFigure() {
		if (! isOneFigureRepeated())
			return null;
		return (Figure) m_rhythmic.get(0);
	}
	
	
	//
	// Serialisation / d�serialisation
	//

	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		
		object.setElementAttribute(CHORD_ATTR, m_chord);
		if (m_hasCustomDuration == true) {
			object.setAttribute(DURATION_ATTR, m_rhythmic.get(0).getDuration()+"");
		}
		else {
			object.setAttribute(RHYTHMIC_ATTR, true+"");
			for (Duration duration : m_rhythmic)
				object.addChild(duration.serialize(serializer));
		}
		return object;
	}
	
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setChord((Chord) object.getElementAttribute(CHORD_ATTR));
		if (object.hasAttribute(DURATION_ATTR)) {
			setCustomDuration(new Duration(Float.parseFloat(object.getAttribute(DURATION_ATTR))));
		}
		else if (object.hasAttribute(RHYTHMIC_ATTR)) {
			m_rhythmic.clear();
			for (int i = 0; i < object.getChildsNumber(); i++) {
				SerializedObject child = object.getChild(i);
				if (child != null) {
					HarmoTabObject htObj = HarmoTabObjectFactory.create(serializer, child);
					if (htObj instanceof Figure)
						appendRhythmicFigure((Figure) htObj);
				}
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	protected Chord m_chord = null;
	protected Vector<Duration> m_rhythmic = new Vector<Duration>();
	protected boolean m_hasCustomDuration;

}


/**
 * Commande d'annulation des mofdifications d'un accompagnement
 */
class AccompanimentRestoreCommand extends Accompaniment implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public AccompanimentRestoreCommand(Accompaniment saved) {
		m_saved = saved;
		m_chord = m_saved.m_chord;
		m_rhythmic = (Vector<Duration>) m_saved.m_rhythmic.clone();
		m_hasCustomDuration = m_saved.m_hasCustomDuration;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_chord != m_chord)
			m_saved.setChord(m_chord);
		m_saved.m_rhythmic = m_rhythmic;
		m_saved.m_hasCustomDuration = m_hasCustomDuration;
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new AccompanimentRestoreCommand(m_saved);
	}
	
	private Accompaniment m_saved;
}
