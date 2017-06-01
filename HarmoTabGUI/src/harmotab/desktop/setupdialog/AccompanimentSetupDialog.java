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

package harmotab.desktop.setupdialog;

import harmotab.core.*;
import harmotab.desktop.components.*;
import harmotab.element.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class AccompanimentSetupDialog extends ChordSetupDialog {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//
	
	public AccompanimentSetupDialog(Window parent, Accompaniment accompaniment) {
		super(parent, accompaniment.getChord());
		m_accompaniment = accompaniment;
		create(false);
	}
	
	public AccompanimentSetupDialog(Window parent, Accompaniment accompaniment, boolean showAccompanimentTab) {
		super(parent, accompaniment.getChord());
		m_accompaniment = accompaniment;
		create(showAccompanimentTab);			
	}	
	
	private void create(boolean showAccompanimentTab) {
		setTitle(Localizer.get(i18n.ET_ACCOMPANIMENT_CHORD_SETUP));
		
		// Initialisation des composants graphiques
		boolean customDuration = m_accompaniment.hasCustomDuration();
		
		m_figureChooser = new FigureChooser(customDuration ? new Figure() : m_accompaniment.getRepeatedFigure());
		m_repeatSpinner = new JSpinner(new SpinnerNumberModel(
				Accompaniment.MIN_REPEAT_NUMBER, 
				Accompaniment.MIN_REPEAT_NUMBER, 
				Accompaniment.MAX_REPEAT_NUMBER, 
				1));
		if (!customDuration) {
			m_repeatSpinner.setValue(m_accompaniment.getRepeatTime());
		}
		
		ButtonGroup group = new ButtonGroup();
		m_figureRhythmicRadio = new JRadioButton(Localizer.get(i18n.ET_FIGURE_BASED_RHYTHMIC));
		m_figureRhythmicRadio.setOpaque(false);
		group.add(m_figureRhythmicRadio);
		m_fixedDurationRhytmicRadio = new JRadioButton(Localizer.get(i18n.ET_FIXED_DURATION));
		m_fixedDurationRhytmicRadio.setOpaque(false);
		group.add(m_fixedDurationRhytmicRadio);
		
		m_fixedDurationRhytmicRadio.setSelected(customDuration);
		m_figureRhythmicRadio.setSelected(!customDuration);			
		
		m_customDurationChooser = new DurationChooser(m_accompaniment.getDuration());

		// Onglet de configuration de la rythmique
		
		SetupCategory rhythmicSetupCategory = new SetupCategory(Localizer.get(i18n.ET_RHYTHMIC_SETUP_CATEGORY));
		JPanel rhythmicSetupPane = rhythmicSetupCategory.getPanel();
		
		rhythmicSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_FIGURE_BASED_RHYTHMIC_SETUP)));
		rhythmicSetupPane.add(createSetupField("", m_figureRhythmicRadio));
		JPanel rhythmicPane = new JPanel(new FlowLayout());
		rhythmicPane.setOpaque(false);
		rhythmicPane.add(m_repeatSpinner);
		rhythmicPane.add(new JLabel(" x "));
		rhythmicPane.add(m_figureChooser);
		rhythmicSetupPane.add(createSetupField(Localizer.get(i18n.N_RHYTHMIC), rhythmicPane));
		
		rhythmicSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_DURATION_BASED_RHYTHMIC_SETUP)));
		rhythmicSetupPane.add(createSetupField("", m_fixedDurationRhytmicRadio));
		rhythmicSetupPane.add(createSetupField(Localizer.get(i18n.N_DURATION), m_customDurationChooser));
				
		addSetupCategory(rhythmicSetupCategory);
		
		// Enregistrement des listeners
		UserActionListener listener = new UserActionListener();
		m_fixedDurationRhytmicRadio.addActionListener(listener);
		m_figureRhythmicRadio.addActionListener(listener);
		
		// Affichage
		update();
		if (showAccompanimentTab)
			displayCategory(rhythmicSetupCategory);
	}

	
	//
	// Gestion du contenu
	//
	
	private void update() {
		boolean fixedDurationMode = m_fixedDurationRhytmicRadio.isSelected() == true;
		m_figureChooser.setEnabled(!fixedDurationMode);
		m_repeatSpinner.setEnabled(!fixedDurationMode);
		m_customDurationChooser.setEnabled(fixedDurationMode);
	}
	

	@Override
	protected void discard() {
		super.discard();
	}

	@Override
	protected boolean save() {
		if (super.save() == false)
			return false;
		
		if (m_figureRhythmicRadio.isSelected()) {
			m_accompaniment.setRhythmic(m_figureChooser.getSelectedFigure(), (Integer) m_repeatSpinner.getValue());
		}
		else if (m_fixedDurationRhytmicRadio.isSelected()) {
			m_accompaniment.setCustomDuration(m_customDurationChooser.getDurationValue());			
		}
		return true;
	}
	
	
	//
	// Gestion des �v�nements
	//
	
	private class UserActionListener extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override 
		public void actionPerformed(ActionEvent event) {
			// Action sur l'un des boutons radio de s�lection de la rythmique
			if (event.getSource() == m_fixedDurationRhytmicRadio || 
					event.getSource() == m_figureRhythmicRadio) {
				update();
			}
		}
	}
	
	
	//
	// Attributs
	//

	private Accompaniment m_accompaniment = null;
	
	private JRadioButton m_figureRhythmicRadio = null;
	private JRadioButton m_fixedDurationRhytmicRadio = null;
	private FigureChooser m_figureChooser = null;
	private JSpinner m_repeatSpinner = null;
	private DurationChooser m_customDurationChooser = null;

}
