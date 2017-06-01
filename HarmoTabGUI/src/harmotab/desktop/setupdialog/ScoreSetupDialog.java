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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import harmotab.core.*;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.*;
import harmotab.desktop.components.*;
import harmotab.element.*;
import harmotab.harmonica.*;
import harmotab.track.*;


/**
 * Boite de dialogue de cr�ation d'une nouvelle partition.
 * La boite de dialogue est affich�e � la fin du constructeur sauf si l'action
 * de cr�ation d'une nouvelle partition est annul�e par l'utilisateur.
 */
public class ScoreSetupDialog extends SetupDialog {
	private static final long serialVersionUID = 1L;
	
	public static int SCORE_PROPERTIES_TAB = 0;
	public static int HARMONICA_PROPERTIES_TAB = 1;
	

	//
	// Constructeur
	//

	public ScoreSetupDialog(Window parent, ScoreController controller) {
		super(parent, Localizer.get(i18n.ET_SCORE_SETUP));
		m_scoreController = controller;
		m_score = m_scoreController.getScore();
		m_modelChanged = false;
		
		HarmoTabTrack htTrack = (HarmoTabTrack) m_score.getTrack(HarmoTabTrack.class, 0);
		
		//
		// Cr�ation des composants graphiques

		m_scoreSetupCategory = new SetupCategory(Localizer.get(i18n.ET_SCORE_SETUP_CATEGORY));
		m_harmonicaSetupCategory = new SetupCategory(Localizer.get(i18n.N_HARMONICA));
		m_tracksSetupCategory = new SetupCategory(Localizer.get(i18n.ET_TRACKS_SETUP_CATEGORY));
		m_statsSetupCategory = new SetupCategory(Localizer.get(i18n.ET_STATISTICS));

		//
		// Onglet "Partition"
		m_titleText = new JTextField(m_score.getTitleString());
		m_songwriterText = new JTextField(m_score.getSongwriterString());
		m_commentText = new JTextField(m_score.getCommentString());
		m_tempoSpinner = new JSpinner(new SpinnerNumberModel(
				m_score.getTempoValue(), Tempo.MIN_TEMPO_VALUE, Tempo.MAX_TEMPO_VALUE, 1));
		m_descriptionTextArea = new JTextArea(5, 20);
		
		m_descriptionTextArea.setBorder(new JTextField().getBorder());
		m_descriptionTextArea.setFont(new JTextField().getFont());
		m_descriptionTextArea.setLineWrap(true);
		m_descriptionTextArea.setText(m_score.getDescription());
		
		m_tonalityChooser = new TonalityChooser(KeySignature.DEFAULT_KEY_SIGNATURE);
		m_timeSignatureChooser = new TimeSignatureChooser();
		
		// Initialisation des composants graphiques
		Harmonica harmonica = htTrack.getHarmonica();
		m_createFromModelButton = new JButton(Localizer.get(i18n.ET_CREATE_FROM_HARMONICA_MODEL), ActionIcon.getIcon(ActionIcon.WIZARD));
		m_harmonicaNameText = new JTextField(harmonica.getName());
		m_harmonicaTunningChooser = new HarmonicaTunningChooser(harmonica.getTunning());
		m_harmonicaTypeChooser = new HarmonicaTypeChooser(harmonica.getModel().getHarmonicaType());
		m_numberOfHolesChooser = new NumberOfHolesChooser(harmonica.getModel().getNumberOfHoles());
		m_tabModelEditor = new TabModelEditor((TabModel) htTrack.getTabModel().clone(), harmonica.getModel().getHarmonicaType().hasPiston());
		
		JPanel scoreSetupPane = m_scoreSetupCategory.getPanel();
		scoreSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_SCORE_SETUP)));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_SCORE_TITLE), m_titleText));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_SCORE_SONGWRITER), m_songwriterText));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_SCORE_COMMENT), m_commentText));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_TEMPO), m_tempoSpinner));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_DESCRIPTION), m_descriptionTextArea));
		
		scoreSetupPane.add(createSetupSeparator(Localizer.get(i18n.N_STAFF_TRACK)));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_TONALITY), m_tonalityChooser));
		scoreSetupPane.add(createSetupField(Localizer.get(i18n.N_TIME_SIGNATURE), m_timeSignatureChooser));
		
		//
		// Onglet "H"
		JPanel harmonicaSetupPane = m_harmonicaSetupCategory.getPanel();
		
		harmonicaSetupPane.add(createSetupSeparator(Localizer.get(i18n.N_HARMONICA)));
		harmonicaSetupPane.add(createSetupField(null, m_createFromModelButton));
		harmonicaSetupPane.add(createSetupField(null, new JLabel("")));
		harmonicaSetupPane.add(createSetupField(Localizer.get(i18n.N_NAME), m_harmonicaNameText));
		harmonicaSetupPane.add(createSetupField(Localizer.get(i18n.N_HARMONICA_TYPE), m_harmonicaTypeChooser));
		harmonicaSetupPane.add(createSetupField(Localizer.get(i18n.N_HOLES), m_numberOfHolesChooser));
		harmonicaSetupPane.add(createSetupField(Localizer.get(i18n.N_TUNNING), m_harmonicaTunningChooser));
		harmonicaSetupPane.add(createSetupField(null, new JLabel("")));
		harmonicaSetupPane.add(createSetupField(null, m_tabModelEditor));
		
		//
		// Onglet "Pistes"
		JPanel tracksSetupPane = m_tracksSetupCategory.getPanel();
		tracksSetupPane.add(createSetupSeparator("Tracks"));
		int i = 1;
		for (Track track : m_score) {
			tracksSetupPane.add(createSetupField(Localizer.get(
					i18n.N_TRACK) + " " + i++, new TrackSetupComponent(getWindow(), track)));
		}
		if (GlobalPreferences.getMetronomeFeatureEnabled()) {
			//TODO: Implémenter MetronomeSetupComponent
			//tracksSetupPane.add(createSetupField(Localizer.get(i18n.N_TEMPO), new TempoSetupComponent(getWindow(), track)));
		}
		
		//
		// Onglet "Statistiques"
		ScoreStatistics stats = new ScoreStatistics(m_score);
		String statsString = "";
		statsString += Localizer.get(i18n.ET_TRACKS_COUNT) + ": " + stats.getTracksCount() + "<br>";
		statsString += Localizer.get(i18n.ET_PLAYBACK_DURATION) + ": " + stats.getPlaybackDurationSec() + " s<br>";
		statsString += Localizer.get(i18n.ET_BARS_COUNT) + ": " + stats.getBarsCount() + "<br>";
		statsString += Localizer.get(i18n.ET_SCORE_ITEMS_COUNT) + ": " + stats.getItemsCount() + "<br>";
		statsString += Localizer.get(i18n.ET_SCORE_DISPLAYED_ITEMS_COUNT) + ": " + stats.getDisplayedItemsCount() + "<br>";
		m_statsLabel = new JLabel("<html>" + statsString + "</html>");
		m_statsLabel.setOpaque(false);
		
		JPanel statsSetupPane = m_statsSetupCategory.getPanel();
		statsSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_STATISTICS)));
		statsSetupPane.add(createSetupField(null, m_statsLabel));
		
		//
		// Ajout des cat�gories � la fen�tre
		addSetupCategory(m_scoreSetupCategory);
		addSetupCategory(m_harmonicaSetupCategory);
		addSetupCategory(m_tracksSetupCategory);
		addSetupCategory(m_statsSetupCategory);
		
		//
		// Desactivation des composants si la partition n'est pas éditable
		boolean editable = m_scoreController.isScoreEditable();
		m_titleText.setEnabled(editable);
		m_songwriterText.setEnabled(editable);
		m_commentText.setEnabled(editable);
		m_tempoSpinner.setEnabled(editable);
		m_descriptionTextArea.setEnabled(editable);
		m_tonalityChooser.setEnabled(editable);
		m_timeSignatureChooser.setEnabled(editable);
		
		//
		// Enregistrement des listeners
		m_createFromModelButton.addActionListener(new TabMappingWizardAction());
		
		//
		// Affichage
		displayCategory(m_scoreSetupCategory);
		
	}
	
	
	public void setSelectedTabMappingHeight(Height height) {
		m_tabModelEditor.goTo(height);
	}
	
	
	// Affichage de l'assistant de cr�ation de TabModel
	private class TabMappingWizardAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			// Affichage de la fen�tre de l'assistant
			TabModelWizard wizard = new TabModelWizard(getWindow());
			wizard.setTabModel(m_tabModelEditor.getTabModel());
			wizard.setVisible(true);
			// Prise en compte des modification effectu�es
			if (wizard.getTabModel() != null) {
				Harmonica harmonica = wizard.getHarmonica();
				m_harmonicaNameText.setText(harmonica.getModel().getName());
				m_harmonicaTunningChooser.setSelectedItem(harmonica.getTunning().getNoteName());
				m_harmonicaTypeChooser.setSelectedHarmonicaType(harmonica.getModel().getHarmonicaType());
				m_numberOfHolesChooser.setValue(harmonica.getModel().getNumberOfHoles());
				m_modelChanged = true;
			}
		}
	}

	
	//
	// Gestion du contenu
	//
	
	@Override
	protected void discard() {
	}

	@Override
	protected boolean save() {
		// Ajoute une action d'annulation des modifications
		UndoManager undoManager = UndoManager.getInstance();
		undoManager.addUndoCommand(m_score.createRestoreCommand(), Localizer.get(i18n.MENU_SCORE_PROPERTIES));
		
		// D�but des modifications de la partition
		m_score.setDispachEvents(false, null);
		
		// Modifications des propri�t�s de la partition
		m_score.setTitle(m_titleText.getText());
		m_score.setSongwriter(m_songwriterText.getText());
		m_score.setComment(m_commentText.getText());
		m_score.setTempo((Integer) m_tempoSpinner.getValue());
		m_score.setDescription(m_descriptionTextArea.getText());
		
		// Modification des propri�t�s de la port�e
		HarmoTabTrack htTrack = (HarmoTabTrack) m_score.getTrack(HarmoTabTrack.class, 0);
		Bar firstBar = (Bar) htTrack.get(Bar.class, 0);
		TimeSignature timeSignature = firstBar.getTimeSignature();
		undoManager.appendToLastUndoCommand(timeSignature.createRestoreCommand());
		timeSignature.setNumber(m_timeSignatureChooser.getNumber());
		timeSignature.setReference(m_timeSignatureChooser.getReference());
		KeySignature keySignature = firstBar.getKeySignature();
		undoManager.appendToLastUndoCommand(keySignature.createRestoreCommand());
		keySignature.setIndex(m_tonalityChooser.getTonality());
		
		// Modification des propri�t�s de l'harmonica
		undoManager.appendToLastUndoCommand(htTrack.createRestoreCommand());
		htTrack.setTabModel(m_tabModelEditor.getTabModel());
		htTrack.setHarmonica(new Harmonica(m_harmonicaNameText.getText()));
		htTrack.getHarmonica().getModel().setNumberOfHoles(m_numberOfHolesChooser.getNumberOfHoles());
		htTrack.getHarmonica().getModel().setHarmonicaType(m_harmonicaTypeChooser.getSelectedHarmonicaType());
		htTrack.getHarmonica().setTunning(m_harmonicaTunningChooser.getSelectedTunning());
		
		// En cas de modification du mod�le de tablature, propose de retabler 
		// la partition si elle contient des notes avec tablature
		if (m_modelChanged || m_tabModelEditor.hasTabModelChanged() && htTrack.get(HarmoTabElement.class, 0) != null) {
			TabModel tabModel = htTrack.getTabModel();
			if (tabModel != null) {
				int res = JOptionPane.showConfirmDialog(this, Localizer.get(i18n.M_UPDATE_TAB_MAPPING_QUESTION), "HarmoTab", JOptionPane.YES_NO_OPTION);
				if (res == JOptionPane.OK_OPTION) {
					TabModelController tmc = new TabModelController(tabModel);
					tmc.updateTabs(htTrack);
				}
			}
		}
		
		// Fin des modifications de la partition
		m_score.setDispachEvents(true, Score.PROPERTIES_CHANGED_EVENT);
		
		return true;
	}
	
	
	//
	// Attributs
	//

	protected ScoreController m_scoreController = null;
	protected Score m_score = null;
	protected boolean m_modelChanged = false;
	
	private SetupCategory m_scoreSetupCategory = null;
	private JTextField m_titleText = null;
	private JTextField m_songwriterText = null;
	private JTextField m_commentText = null;
	private JSpinner m_tempoSpinner = null;
	private JTextArea m_descriptionTextArea = null;
	private TonalityChooser m_tonalityChooser = null;
	private TimeSignatureChooser m_timeSignatureChooser = null;
	
	private SetupCategory m_harmonicaSetupCategory = null;
	private JButton m_createFromModelButton = null;
	private JTextField m_harmonicaNameText = null;
	private HarmonicaTunningChooser m_harmonicaTunningChooser = null;
	private HarmonicaTypeChooser m_harmonicaTypeChooser = null;
	private NumberOfHolesChooser m_numberOfHolesChooser = null;
	private TabModelEditor m_tabModelEditor = null;
	
	private SetupCategory m_statsSetupCategory = null;
	private JLabel m_statsLabel = null;

	private SetupCategory m_tracksSetupCategory = null;
	
}

