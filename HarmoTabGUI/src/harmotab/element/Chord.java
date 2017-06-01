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

import java.util.*;
import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.io.*;
import harmotab.throwables.*;


/**
 * Accord
 * Ensemble de notes jouées simultanément décrit par un nom.
 */
public class Chord extends TrackElement implements Comparable<Chord> {
	
	public static final String NAME_ATTR = "name";
	public static final String FIGURE_ATTR = "figure";
	public static final String HEIGHTS_ATTR = "heights";
	
	public final static String UNDEFINED = "?";
	
	public static final String[] m_heightNames = {
		new String("C"), new String("D"), new String("E"), new String("F"), new String("G"), new String("A"), new String("B") };
	
	public static final String[] m_alterationsNames = {
		new String(""), new String("#"), new String("b") };
	
	public static final String[] m_typesNames = {
		new String("7"), new String("7M"), new String("6"), new String("m"), new String("m7"), new String("m6"),
		new String("sus4"), new String("dim"), new String("aug"), new String("9"), new String("M9"), new String("m9"),
		new String("13") };
	
	public static final int DEFAULT_CHORD_OCTAVE = 4;
	public static final int DEFAULT_CHORD_BASS_OCTAVE = DEFAULT_CHORD_OCTAVE-1;
	
	
	//
	// Constructeur
	//
	
	public Chord() {
		super(Element.CHORD);
		instanciateEmptyHeights();
		setChord(new Height(), null, null);
		setFigure(null);
	}
	
	public Chord(String name) {
		super(Element.CHORD);
		setName(name);
		setFigure(new Figure());
		instanciateEmptyHeights();
	}
		
	public Chord(String name, Figure figure) {
		super(Element.CHORD);
		setName(name);
		setFigure(figure);
		instanciateEmptyHeights();
	}
	
	public Chord(Height main, String mode, Height bass) {
		super(Element.CHORD);
		instanciateEmptyHeights();
		setChord(main, mode, bass);
	}
	
	
	private void instanciateEmptyHeights() {
		m_heights = new LinkedHashSet<Height>();
	}
	
	
	@Override
	public Object clone() {
		Chord chord = (Chord) super.clone();
		
		chord.setName(m_chordName);
		
		Figure f = m_figure != null ? (Figure) m_figure.clone() : null;
		chord.m_figure = null;
		chord.setFigure(f);
		
		instanciateEmptyHeights();
		for (Height height : m_heights) {
			chord.addHeight((Height) height.clone());
		}
		
		return chord;
	}
	
	
	public void set(Chord chord) {
		setName(chord.getName());
		setFigure((Figure) chord.getFigure().clone());
		for (Height height : chord.getHeights()) {
			addHeight((Height) height.clone());
		}
		fireObjectChanged(NAME_ATTR);
	}
	
	
	@Override
	public int compareTo(Chord chord) {
		if (chord == null)
			return 1;
		return ((Chord) chord).getName().compareTo(getName());		
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new ChordRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setName(String name) {
		m_chordName = name;
		fireObjectChanged(NAME_ATTR);
	}
	
	public String getName() {
		return m_chordName;
	}
	
	
	public void setFigure(Figure figure) {
		removeAttributeChangesObserver(m_figure, FIGURE_ATTR);
		m_figure = figure;
		addAttributeChangesObserver(m_figure, FIGURE_ATTR);
		fireObjectChanged(FIGURE_ATTR);
	}
	
	public Figure getFigure() {
		return m_figure;
	}

	
	//
	// Méthodes utlitaires
	//
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_CHORD);
	}
	
		
	public void setChord(Height main, String mode, Height bass) {
		// Initialisation des attributs
		String chordName = main.getNoteName();
		
		if (mode != null && mode.equals(""))
			mode = null;
		if (mode != null)
			chordName += mode;
		else
			mode = "M";
		
		if (bass != null)
			chordName += "/" + bass.getNoteName();
		
		setName(chordName);
		setFigure(new Figure());
		
		// Affectation des heights en fonction du type
		if (bass != null) {
			Height bassHeight = new Height(bass.getSoundId());
			//bassHeight.setOctave(Height.DEFAULT_OCTAVE - 1);
			m_heights.add(bassHeight);
		}
		
		int mainSId = main.getSoundId();
		
		if (mode.equals("M")) {
			// 0, 4, 7, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("7")) {
			// 0, 4, 7, 10, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 10));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("7M")) {
			// 0, 4, 7, 11, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 11));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("6")) {
			// 0, 4, 7, 9, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 9));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("m")) {
			// 0, 3, 7, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 3));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("m7")) {
			// 0, 3, 7, 10, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 3));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 10));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("m6")) {
			// 0, 3, 7, 9, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 3));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 9));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("sus4")) {
			// 0, 5, 7, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 5));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("dim")) {
			// 0, 3, 7, 10, 2, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 2));
			m_heights.add(new Height(mainSId + 3));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 10));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("aug")) {
			// 0, 4, 8, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 8));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("M9")) {
			// 0, 4, 7, 11, 2, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 2));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 11));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("9")) {
			// 0, 4, 7, 10, 2, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 2));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 10));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("m9")) {
			// 0, 3, 7, 10, 2, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 2));
			m_heights.add(new Height(mainSId + 3));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 10));
			m_heights.add(new Height(mainSId + 12));
		}
		else if (mode.equals("13")) {
			// 0, 4, 7, 10, 2, 9, 0
			m_heights.add(new Height(mainSId + 0));
			m_heights.add(new Height(mainSId + 2));
			m_heights.add(new Height(mainSId + 4));
			m_heights.add(new Height(mainSId + 7));
			m_heights.add(new Height(mainSId + 9));
			m_heights.add(new Height(mainSId + 10));
			m_heights.add(new Height(mainSId + 12));
		}
		else {
			throw new OutOfSpecificationError("Unknown mode type.");
		}
		
		fireObjectChanged(NAME_ATTR);
	}
	
	
	public Height extractNoteHeight() {
		String note = "";
		String alt = "";
		
		if (m_chordName.length() > 0)
			note = m_chordName.substring(0, 1);
		if (m_chordName.length() > 1)
			alt = m_chordName.substring(1, 2);
		if (!alt.equals("#") && !alt.equals("b"))
			alt = "";

		Height height = null;
		try {
			height = new Height(note + alt);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return height;
	}
	
	public Height extractBassHeight() {
		Height height = null;
		int slashPosition = m_chordName.indexOf("/");
		if (slashPosition != -1) {
			String note = m_chordName.substring(slashPosition + 1);
			try {
				height = new Height(note);
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return height;
	}
	
	public String extractType() {
		int typeStarts = 1;
		if (m_chordName.length() > 1 && (m_chordName.charAt(1) == 'b' || m_chordName.charAt(1) == '#'))
			typeStarts = 2;
		
		int typeEnds = m_chordName.indexOf("/");
		if (typeEnds < 0)
			typeEnds = m_chordName.length();
		
		if (typeStarts < typeEnds)
			return m_chordName.substring(typeStarts, typeEnds);
		return null;
	}
	
	public boolean isDefined() {
		return !getName().equals(UNDEFINED);
	}
	
	
	//
	// Surchage des méthodes
	//
	
	@Override
	public float getDuration() {
		return m_figure.getDuration();
	}

	
	//
	// Gestion des notes de l'accord
	//
	
	public Iterator<Height> getHeightsIterator() {
		return m_heights.iterator();
	}
	
	public Collection<Height> getHeights() {
		return m_heights;
	}
	
	public void addHeight(Height height) {
		m_heights.add(height);
		addAttributeChangesObserver(height, HEIGHTS_ATTR);
		fireObjectChanged(HEIGHTS_ATTR);
	}
	
	public void removeHeight(Height height) {
		m_heights.remove(height);
		removeAttributeChangesObserver(height, HEIGHTS_ATTR);
		fireObjectChanged(HEIGHTS_ATTR);		
	}
	
	public void clearHeights() {
		for (Height height : m_heights)
			removeAttributeChangesObserver(height, HEIGHTS_ATTR);
		m_heights.clear();
		fireObjectChanged(HEIGHTS_ATTR);
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(NAME_ATTR, getName());
		if (getFigure() != null)
			object.setElementAttribute(FIGURE_ATTR, getFigure());
		for (Height height : m_heights)
			object.addChild(height.serialize(serializer));
		return object;
	}
	
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setName(object.getAttribute(NAME_ATTR));
		setFigure(object.hasAttribute(FIGURE_ATTR) ?
			(Figure) object.getElementAttribute(FIGURE_ATTR) :
			null);
		
		clearHeights();
		for (int i = 0; i < object.getChildsNumber(); i++) {
			SerializedObject child = object.getChild(i);
			if (child != null) {
				HarmoTabObject htObj = HarmoTabObjectFactory.create(serializer, child);
				if (htObj instanceof Height)
					addHeight((Height) htObj); 
			}
		}
	}
	
	
	//
	// Attributs
	//
	
	protected String m_chordName = null;
	protected Figure m_figure = null;
	protected LinkedHashSet<Height> m_heights = null;
	
}


/**
 * Commande d'annulation des modifications d'un accord
 */
class ChordRestoreCommand extends Chord implements RestoreCommand {
	
	@SuppressWarnings("unchecked")
	public ChordRestoreCommand(Chord saved) {
		m_saved = saved;
		m_chordName = m_saved.m_chordName;
		m_figure = m_saved.m_figure;
		m_heights = (LinkedHashSet<Height>) m_saved.m_heights.clone();
	}
	
	@Override
	public void execute() {
		if (m_saved.m_chordName != m_chordName)
			m_saved.setName(m_chordName);
		if (m_saved.m_figure != m_figure)
			m_saved.setFigure(m_figure);
		m_saved.m_heights = m_heights;
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new ChordRestoreCommand(m_saved);
	}
	
	private Chord m_saved;
}

