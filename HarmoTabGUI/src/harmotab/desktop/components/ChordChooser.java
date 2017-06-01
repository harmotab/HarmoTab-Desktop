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

package harmotab.desktop.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import harmotab.core.*;
import harmotab.element.*;


public class ChordChooser extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	
	//
	// Constructeur
	//
	
	public ChordChooser() {
		setChord(null);
		create();
	}
	
	public ChordChooser(Chord chord) {
		setChord(chord);
		create();
	}
	
	
	private void create() {
		setOpaque(false);
		
		// Initialisation des composants
		m_fundamentalCombo = createCombo(Chord.m_heightNames, false);
		m_fundamentalAlterationCombo = createCombo(Chord.m_alterationsNames, false);
		m_typeCombo = createCombo(Chord.m_typesNames, true);
		m_bassCombo = createCombo(Chord.m_heightNames, true);
		m_bassAlterationCombo = createCombo(Chord.m_alterationsNames, false);

		// Construction de l'IHM
		add(m_fundamentalCombo);
		add(m_fundamentalAlterationCombo);
		add(m_typeCombo);
		add(new JLabel(" / "));
		add(m_bassCombo);
		add(m_bassAlterationCombo);
		
		// Enregistrement des listeners
		m_fundamentalCombo.addActionListener(this);
		m_fundamentalAlterationCombo.addActionListener(this);
		m_typeCombo.addActionListener(this);
		m_bassCombo.addActionListener(this);
		m_bassAlterationCombo.addActionListener(this);

		// Affichage de l'accord courant
		updateChordView();
	}
	
	
	private void updateChordView() {
		Height fundamentalHeight = m_chord.extractNoteHeight();
		String chordType = m_chord.extractType();
		Height bassHeight = m_chord.extractBassHeight();

		m_fundamentalCombo.setSelectedItem(fundamentalHeight.getNoteChar());
		m_fundamentalAlterationCombo.setSelectedItem(fundamentalHeight.getAlterationChar());
		if (chordType == null)
			m_typeCombo.setSelectedIndex(0);
		else
			m_typeCombo.setSelectedItem(chordType);
		m_bassCombo.setSelectedItem(bassHeight != null ? bassHeight.getNoteChar() : " ");
		m_bassAlterationCombo.setSelectedItem(bassHeight != null ? bassHeight.getAlterationChar() : " ");
	}
	
	private void updateChord() {
		Height fundamentalHeight = new Height(
				(String) m_fundamentalCombo.getSelectedItem() +
				((String) m_fundamentalAlterationCombo.getSelectedItem()).trim());
		fundamentalHeight.setOctave(Chord.DEFAULT_CHORD_OCTAVE);
		
		String type = ((String) m_typeCombo.getSelectedItem()).trim();

		Height bassHeight = null;
		if (!((String) m_bassCombo.getSelectedItem()).trim().equals("")) {
			bassHeight = new Height(
					((String) m_bassCombo.getSelectedItem()).trim() +
					((String) m_bassAlterationCombo.getSelectedItem()).trim());
			bassHeight.setOctave(Chord.DEFAULT_CHORD_BASS_OCTAVE);
		}
		
		m_chord = new Chord(fundamentalHeight, type, bassHeight);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setChord(Chord chord) {
		if (chord == null || !chord.isDefined())
			m_chord = new Chord();
		else
			m_chord = (Chord) chord.clone();
	}
	
	public Chord getChord() {
		return m_chord;
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	@Override
	public void actionPerformed(ActionEvent event) {	
		updateChord();
		fireActionPerformed(this, event.getID(), event.getActionCommand());
	}
	
	
	public void addActionListener(ActionListener listener) {
		m_listeners.add(ActionListener.class, listener);
	}
	
	private void fireActionPerformed(Object source, int id, String command) {
		ActionEvent event = new ActionEvent(source, id, command);
		for (ActionListener listener : m_listeners.getListeners(ActionListener.class))
			listener.actionPerformed(event);
	}
	
	
	//
	// M�thodes utilitaires
	//
	
	private JComboBox createCombo(String[] values, boolean addEmptyChoice) {
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		if (addEmptyChoice == true)
			model.addElement(new String(" "));
		for (String value : values)
			model.addElement(value);
		JComboBox combo = new JComboBox(model);
		Dimension size = combo.getPreferredSize();
		size.width = 60;
		combo.setPreferredSize(size);
		combo.setOpaque(false);
		return combo;
	}
	
	
	//
	// Attributs
	//
	
	private Chord m_chord = null;
	
	private JComboBox m_fundamentalCombo = null;
	private JComboBox m_fundamentalAlterationCombo = null;
	private JComboBox m_typeCombo = null;
	private JComboBox m_bassCombo = null;
	private JComboBox m_bassAlterationCombo = null;
	
	private final EventListenerList m_listeners = new EventListenerList();

}

