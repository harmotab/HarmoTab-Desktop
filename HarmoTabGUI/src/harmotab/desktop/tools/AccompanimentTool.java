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

import harmotab.renderer.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.setupdialog.*;
import harmotab.element.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import rvt.util.gui.LabelledSpinner;


/**
 * Boite d'outils de modification d'un accompagnement
 */
public class AccompanimentTool extends Tool {
	private static final long serialVersionUID = 1L;
	
	//
	// Constructeur
	//
	
	public AccompanimentTool(Container container, Score score, LocationItem item) {
		super(container, score, item);
		m_accompaniment = (Accompaniment) item.getElement();
		
		// Cr�ation des composants		
		m_chordEditorButton = new ToolButton(Localizer.get(i18n.TT_CHORD_SELECTION), ToolIcon.TUNE, "");
		
		if (m_accompaniment.isOneFigureRepeated()) {
			m_durationSpinnerModel = new SpinnerNumberModel(
					m_accompaniment.getRepeatTime(), 
					Accompaniment.MIN_REPEAT_NUMBER, 
					Accompaniment.MAX_REPEAT_NUMBER, 
					1);
			m_durationSpinner = new LabelledSpinner(
					"x " + m_accompaniment.getRepeatedFigure().getLocalizedName(),
					m_durationSpinnerModel);
		}
		else {
			float value = m_accompaniment.getDuration();
			m_durationSpinnerModel = new SpinnerNumberModel(
					value, 
					Duration.MIN_DURATION_VALUE, 
					Duration.MAX_DURATION_VALUE,
					Duration.DURATION_GRANULARITY);
			m_durationSpinner = new LabelledSpinner(
					Localizer.get(value > 1.0f ? i18n.W_BEATS : i18n.W_BEAT),
					m_durationSpinnerModel);
		}
		
		// Initialisation des composants
		m_chordEditorButton.setText(" " + m_accompaniment.getChord().getName() + " ");
		
		// Ajout des composants � la barre d'outils
		add(m_chordEditorButton);
		addSeparator();
		add(m_durationSpinner);

		// Enregistrement des listeners
		m_chordEditorButton.addActionListener(new DisplaySetupAction());
		m_durationSpinnerModel.addChangeListener(new DurationChangeAction());
				
		// Affiche la boite de dialogue de s�lection d'accord si il n'est pas défini
		if (!m_accompaniment.getChord().isDefined())
			new AccompanimentSetupDialog(null, m_accompaniment).setVisible(true);

	}


	//
	// Actions de l'utilisateur
	//

	/**
	 * Affichage de la fen�tre d'edition des accompagnements
	 */
	private class DisplaySetupAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == m_chordEditorButton) {
				UndoManager.getInstance().addUndoCommand(m_accompaniment.createRestoreCommand(), Localizer.get(i18n.N_ACCOMPANIMENT));
				new AccompanimentSetupDialog(null, m_accompaniment).setVisible(true);
			}
		}
	}
	
	/**
	 * Modification de la dur�e de l'accompagnement
	 */
	private class DurationChangeAction implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			// Modification de la dur�e d'un accompagnement compos� d'accords r�p�t�s
			if (m_accompaniment.isOneFigureRepeated()) {
				try {
					int duration = m_durationSpinnerModel.getNumber().intValue();
					UndoManager.getInstance().addUndoCommand(m_accompaniment.createRestoreCommand(), Localizer.get(i18n.N_DURATION));
					m_accompaniment.setRhythmic(m_accompaniment.getRepeatedFigure(), duration);
				}
				catch (IllegalArgumentException e) {
					m_durationSpinnerModel.setValue(m_accompaniment.getRepeatTime());
				}
			}
			// Modification de la dur�e d'un accompagnement compos� d'un unique accord
			else {
				try {
					float duration = m_durationSpinnerModel.getNumber().floatValue();
					UndoManager.getInstance().addUndoCommand(m_accompaniment.createRestoreCommand(), Localizer.get(i18n.N_DURATION));
					m_accompaniment.setCustomDuration(duration);
				}
				catch (IllegalArgumentException e) {
					m_durationSpinnerModel.setValue(m_accompaniment.getDuration());
				}
			}
		}
	}

	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	
	//
	// Attributs
	//
	
	private Accompaniment m_accompaniment;
	private ToolButton m_chordEditorButton;
	private JSpinner m_durationSpinner;
	private SpinnerNumberModel m_durationSpinnerModel;
	
}
