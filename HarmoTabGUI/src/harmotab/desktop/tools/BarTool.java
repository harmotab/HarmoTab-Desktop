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

package harmotab.desktop.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.element.*;
import harmotab.renderer.*;


/**
 * Bo�te d'outils pour la modification des propri�t�s d'une barre de mesure.
 */
public class BarTool extends Tool implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur / destructeur
	//
	
	public BarTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_bar = (Bar) item.getElement();
			
		// Cr�ation des composants graphiques
		m_addTimeSignatureButton = new ToolButton(
				Localizer.get(i18n.TT_ADD_TIME_SIGNATURE),
				ToolIcon.ADD_BAR);
		m_beginningButton = new ToolToggleButton(
				Localizer.get(i18n.TT_BEGINNING_OF_PHRASE), 
				ToolIcon.PHRASE_START);
		m_endingButton = new ToolToggleButton(
				Localizer.get(i18n.TT_END_OF_PHRASE),
				ToolIcon.PHRASE_END);
		m_repeatsSpinner = new JSpinner(
				new SpinnerNumberModel(
						1, 
						RepeatAttribute.MIN_REPEAT_TIMES, 
						RepeatAttribute.MAX_REPEAT_TIMES, 
						1));

		// Initialisation des composants
		m_addTimeSignatureButton.setEnabled(!(m_bar.getTimeSignature() != null));
		m_beginningButton.setSelected(m_bar.getRepeatAttribute().isBeginning());
		m_endingButton.setSelected(m_bar.getRepeatAttribute().isEnd());
		m_repeatsSpinner.setValue(m_bar.getRepeatAttribute().getRepeatTimes());
		m_repeatsSpinner.setEnabled(m_bar.getRepeatAttribute().isEnd());
		
		// Ajout des composants � la barre d'outils
		add(m_addTimeSignatureButton);
		addSeparator();
		add(m_beginningButton);
		add(m_endingButton);
		addSeparator();
		add(m_repeatsSpinner);
		add(new JLabel(" times "));
		
		// Enregistrement des listeners
		m_addTimeSignatureButton.addActionListener(this);
		m_beginningButton.addActionListener(this);
		m_endingButton.addActionListener(this);
		m_repeatsSpinner.addChangeListener(this);

	}
	
	
	//
	// Gestion des actions sur les composants
	//

	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	@Override
	public void actionPerformed(ActionEvent event) {
		// Bouton d'ajout d'une signature temporelle
		if (event.getSource() == m_addTimeSignatureButton) {
			UndoManager.getInstance().addUndoCommand(m_bar.createRestoreCommand(), Localizer.get(i18n.TT_ADD_TIME_SIGNATURE));
			m_bar.setTimeSignature(new TimeSignature());
		}
		// Bouton de d�but de r�p�tition
		else if (event.getSource() == m_beginningButton) {
			UndoManager.getInstance().addUndoCommand(m_bar.createRestoreCommand(), Localizer.get(i18n.TT_BEGINNING_OF_PHRASE));
			m_bar.getRepeatAttribute().setBeginning(m_beginningButton.isSelected());
		}
		// Bouton de fin de r�p�tition
		else if (event.getSource() == m_endingButton) {
			UndoManager.getInstance().addUndoCommand(m_bar.createRestoreCommand(), Localizer.get(i18n.TT_END_OF_PHRASE));
			m_bar.getRepeatAttribute().setEnd(m_endingButton.isSelected());
		}
		validateModifications();
	}
	
	
	@Override
	public void stateChanged(ChangeEvent event) {
		// Spinner de modification du nombre de r�p�titions
		if (event.getSource() == m_repeatsSpinner) {
			RepeatAttribute repeat = m_bar.getRepeatAttribute();
			UndoManager.getInstance().addUndoCommand(repeat.createRestoreCommand(), Localizer.get(i18n.N_REPEAT_TIMES));
			repeat.setRepeatTimes((Integer) m_repeatsSpinner.getValue());
		}
		validateModifications();
	}
	
	
	private void validateModifications() {
		// Si la barre de mesure est une bare de mesure temporaire, l'insert en
		// tant que barre de mesure définitive dans la piste
		if (m_locationItem.getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == true) {
			UndoManager.getInstance().addUndoCommand(getTrack().createRestoreCommand(), Localizer.get(i18n.N_BAR));
			getTrack().add(m_locationItem.m_elementIndex, m_bar);
			m_locationItem.setFlag(LocationItemFlag.TEMPORARY_ELEMENT, false);
		}
	}

	
	//
	// Attributs
	//
	
	private Bar m_bar = null;
	private ToolButton m_addTimeSignatureButton = null;
	private ToolToggleButton m_beginningButton = null;
	private ToolToggleButton m_endingButton = null;
	private JSpinner m_repeatsSpinner = null;
	
}
