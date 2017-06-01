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

import java.awt.Window;
import java.awt.event.ActionEvent;
import harmotab.core.*;
import harmotab.desktop.RecentFilesManager;
import harmotab.desktop.components.*;
import javax.swing.*;

import rvt.util.gui.FileField;


/**
 * Boite de dialogue de configuration des pr�f�rences
 */
public class PreferencesSetupDialog extends SetupDialog {
	private static final long serialVersionUID = 1L;
	
	
	//
	// Constructeur
	//

	public PreferencesSetupDialog(Window parent) {
		super(parent, "Software preferences");
		
		//
		// Initialisation des composants graphiques
		
		// Global
		m_languageCombo = new LanguageChooser(GlobalPreferences.getLanguage());
		m_systemAppearanceBox = new JCheckBox(Localizer.get(i18n.ET_USE_SYSTEM_APPEARANCE));
		m_systemAppearanceBox.setSelected(GlobalPreferences.useSystemAppearance());
		m_restoreDefaultsButton = new JButton(Localizer.get(i18n.ET_RESTORE_BUTTON));
		m_systemAppearanceBox.setOpaque(false);

		// Editeur
		m_autoTabBox = new JCheckBox(Localizer.get(i18n.ET_AUTO_TAB));
		m_autoTabBox.setSelected(GlobalPreferences.isAutoTabEnabled());
		m_autoTabBox.setOpaque(false);
		m_autoCompleteModelBox = new JCheckBox(Localizer.get(i18n.ET_AUTOMATICS_MODEL_COMPLETION));
		m_autoCompleteModelBox.setSelected(GlobalPreferences.isTabMappingCompletionEnabled());
		m_autoCompleteModelBox.setOpaque(false);
		m_displayBarNumbersBox = new JCheckBox(Localizer.get(i18n.ET_DISPLAY_BAR_NUMBERS));
		m_displayBarNumbersBox.setSelected(GlobalPreferences.isBarNumbersDisplayed());
		m_displayBarNumbersBox.setOpaque(false);
		m_displayEditingHelpersBox = new JCheckBox(Localizer.get(i18n.ET_DISPLAY_EDITING_HELPERS));
		m_displayEditingHelpersBox.setSelected(GlobalPreferences.isEditingHelpersDisplayed());
		m_displayEditingHelpersBox.setOpaque(false);
		m_tabStyleChooser = new TabStyleChooser(GlobalPreferences.getTabStyle());
		m_tabBlowDirectionChooser = new TabBlowDirectionChooser(GlobalPreferences.getTabBlowDirection());
		
		// R�pertoires
		m_scoresFolder = new FileField(GlobalPreferences.getScoresBrowsingFolder(), true);
		m_modelsFolder = new FileField(GlobalPreferences.getModelsFolder(), true);
		
		// R�seau
		m_networkEnabledBox = new JCheckBox(Localizer.get(i18n.ET_NETWORK_ENABLED));
		m_networkEnabledBox.setSelected(GlobalPreferences.isNetworkEnabled());
		m_networkEnabledBox.setOpaque(false);
		
		// Avanc�
		m_enablePerformancesFeatures = new JCheckBox(Localizer.get(i18n.ET_PERFORMANCES_FEATURE), GlobalPreferences.getPerformancesFeatureEnabled());
		m_enablePerformancesFeatures.setOpaque(false);
		m_enableMetronomeFeature = new JCheckBox(Localizer.get(i18n.ET_METRONOME_FEATURE), GlobalPreferences.getMetronomeFeatureEnabled());
		m_enableMetronomeFeature.setOpaque(false);
		m_countDownCheckBox = new JCheckBox(Localizer.get(i18n.ET_COUNTDOWN_FEATURE), GlobalPreferences.getPlaybackCountdownEnabled());
		m_countDownCheckBox.setOpaque(false);
		
		
		//
		// Configuration de l'IHM
		
		// Global
		SetupCategory globalSetupCategory = new SetupCategory(Localizer.get(i18n.ET_GLOBAL));
		JPanel globalSetupPane = globalSetupCategory.getPanel();
		globalSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_LOCALIZATION)));
		globalSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_LANGUAGE), */null, m_languageCombo, Localizer.get(i18n.M_LANGUAGE_DESC)));
		globalSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_APPEARANCE)));
		globalSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_SYSTEM_APPEARANCE),*/null, m_systemAppearanceBox, Localizer.get(i18n.M_SYSTEM_APPEARANCE_DESC)));
		globalSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_RESTORE)));		
		globalSetupPane.add(getSetupField(/*""*/null, m_restoreDefaultsButton, Localizer.get(i18n.M_RESTORE_DEFAULTS_DESC)));		
		
		// R�pertoires
		SetupCategory foldersSetupCategory = new SetupCategory(Localizer.get(i18n.ET_FOLDERS));
		JPanel foldersSetupPane = foldersSetupCategory.getPanel();
		foldersSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_SCORE_FOLDERS)));
		foldersSetupPane.add(getSetupField(null, m_scoresFolder, Localizer.get(i18n.M_SCORE_FOLDERS_DESC)));
		foldersSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_MODELS_FOLDER)));
		foldersSetupPane.add(getSetupField(null, m_modelsFolder, Localizer.get(i18n.M_MODELS_FOLDER_DESC)));

		// Editeur
		SetupCategory editorSetupCategory = new SetupCategory(Localizer.get(i18n.ET_EDITOR_SETUP_CATEGORY));
		JPanel editorSetupPane = editorSetupCategory.getPanel();
		editorSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_SCORE_EDITOR)));
		editorSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_AUTOMATIONS)*/null, m_autoTabBox, Localizer.get(i18n.M_AUTO_TAB_DESC)));
		editorSetupPane.add(getSetupField(/*""*/null, m_autoCompleteModelBox, Localizer.get(i18n.M_MODEL_AUTO_COMPLETION_DESC)));
		editorSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_SCORE_DRAWING)));
		editorSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_BAR_NUMBERS)*/null, m_displayBarNumbersBox, Localizer.get(i18n.M_BAR_NUMBERS_DESC)));
		editorSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_EDITING_HELPERS)*/null, m_displayEditingHelpersBox, Localizer.get(i18n.M_EDITING_HELPERS_DESC)));
		editorSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_TAB_STYLE)));
		editorSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_TAB_STYLE)*/null, m_tabStyleChooser, Localizer.get(i18n.M_TAB_STYLE_DESC)));
		editorSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_TAB_BLOW_DIRECTION)*/null, m_tabBlowDirectionChooser, Localizer.get(i18n.M_TAB_BLOW_DIRECTION)));
				
		// R�seau
		SetupCategory networkSetupCategory = new SetupCategory(Localizer.get(i18n.ET_NETWORK_CATEGORY));
		JPanel networkSetupPane = networkSetupCategory.getPanel();
		networkSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_NETWORK_ACTIVATION)));
		networkSetupPane.add(getSetupField(/*Localizer.get(i18n.ET_NETWORD_ACTIVATED)*/null, m_networkEnabledBox, Localizer.get(i18n.M_NETWORK_DESC)));
		
		// Avanc�s
		SetupCategory advancedSetupCategory = new SetupCategory(Localizer.get(i18n.ET_ADVANCED_SETUP));
		JPanel advancedSetupPane = advancedSetupCategory.getPanel();
		advancedSetupPane.add(createSetupSeparator(Localizer.get(i18n.ET_BETA_FEATURES_SETUP)));
		advancedSetupPane.add(getSetupField(/*Localizer.get(i18n.N_PERFORMANCES)*/null, m_enablePerformancesFeatures, Localizer.get(i18n.M_PERFORMANCES_FEATURE_DESC)));
		advancedSetupPane.add(getSetupField(/*Localizer.get(i18n.N_METRONOME)*/null, m_enableMetronomeFeature, Localizer.get(i18n.M_METRONOME_FEATURE_DESC)));
		advancedSetupPane.add(getSetupField(/*Localizer.get(i18n.N_COUNTDOWN)*/null, m_countDownCheckBox, Localizer.get(i18n.M_COUNTDOWN_FEATURE_DESC)));
		
		//
		// Enregistrement des listeners
		m_restoreDefaultsButton.addActionListener(new RestoreDefaultsAction());
		
		//
		// Affichage de la fen�tre
		addSetupCategory(globalSetupCategory);
		addSetupCategory(editorSetupCategory);
		addSetupCategory(foldersSetupCategory);
		addSetupCategory(networkSetupCategory);
		addSetupCategory(advancedSetupCategory);

	}
	
	
	//
	// Gestion des actions de l'utilisateur
	//
	
	/**
	 * Affectation des valeurs par d�faut des param�tres
	 * R�initialise �galement la liste des fichiers ouverts r�cemment
	 */
	private class RestoreDefaultsAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent event) {
			GlobalPreferences.restoreDefaultPreferences();
			RecentFilesManager.getInstance().reset();
			JOptionPane.showMessageDialog(getWindow(), Localizer.get(i18n.M_PREFERENCE_NEED_RESTART));
			dispose();
		}
	}

	
	//
	// Gestion des donn�es
	//

	@Override
	protected void discard() {
	}

	@Override
	protected boolean save() {
		boolean showRebootMessage = 
			!m_languageCombo.getSelectedLanguageIdentifier().equals(GlobalPreferences.getLanguage()) ||
			m_systemAppearanceBox.isSelected() != GlobalPreferences.useSystemAppearance() ||
			m_enablePerformancesFeatures.isSelected() != GlobalPreferences.getPerformancesFeatureEnabled() ||
			m_enableMetronomeFeature.isSelected() != GlobalPreferences.getMetronomeFeatureEnabled();
		
		// Global
		GlobalPreferences.setLanguage(m_languageCombo.getSelectedLanguageIdentifier());
		GlobalPreferences.useSystemAppearance(m_systemAppearanceBox.isSelected());
		
		// R�pertoires
		GlobalPreferences.setScoresBrowsingFolder(m_scoresFolder.getFile().getAbsolutePath());
		GlobalPreferences.setModelsFolder(m_modelsFolder.getFile().getAbsolutePath());
		
		// Editeur
		GlobalPreferences.setAutoTabEnabled(m_autoTabBox.isSelected());
		GlobalPreferences.setTabMappingCompletionEnabled(m_autoCompleteModelBox.isSelected());
		GlobalPreferences.setBarNumbersDisplayed(m_displayBarNumbersBox.isSelected());
		GlobalPreferences.setEditingHelpersDisplayed(m_displayEditingHelpersBox.isSelected());
		GlobalPreferences.setTabStyle(m_tabStyleChooser.getSelectedIndex());
		GlobalPreferences.setTabBlowDirection(m_tabBlowDirectionChooser.getSelectedBlowDirection());
		
		// R�seau
		GlobalPreferences.setNetworkEnabled(m_networkEnabledBox.isSelected());
		
		// Avanc�
		GlobalPreferences.setPerformancesFeatureEnabled(m_enablePerformancesFeatures.isSelected());
		GlobalPreferences.setMetronomeFeatureEnabled(m_enableMetronomeFeature.isSelected());
		GlobalPreferences.setPlaybackCountdownEnabeld(m_countDownCheckBox.isSelected());
		
		// Sauvegarde des modifications
		GlobalPreferences.save();
		
		// Si des param�tres de configurations n�cessitant un red�marrage du 
		// logiciel ont �t� modifi�s, affiche un message
		if (showRebootMessage) {
			JOptionPane.showMessageDialog(getWindow(), Localizer.get(i18n.M_PREFERENCE_NEED_RESTART));
		}
		
		return true;
	}
	
	
	//
	// Attributs
	//

	// Global
	private LanguageChooser m_languageCombo = null;
	private JCheckBox m_systemAppearanceBox = null;
	private JButton m_restoreDefaultsButton = null;
		
	// R�pertoires
	private FileField m_scoresFolder = null;
	private FileField m_modelsFolder = null;
	
	// Editeur
	private JCheckBox m_autoTabBox = null;
	private JCheckBox m_autoCompleteModelBox = null;
	private JCheckBox m_displayBarNumbersBox = null;
	private JCheckBox m_displayEditingHelpersBox = null;
	private TabStyleChooser m_tabStyleChooser = null;
	private TabBlowDirectionChooser m_tabBlowDirectionChooser = null;
		
	// R�seau
	private JCheckBox m_networkEnabledBox = null;
	
	// Avanc�
	private JCheckBox m_enablePerformancesFeatures = null;
	private JCheckBox m_enableMetronomeFeature = null;
	private JCheckBox m_countDownCheckBox = null;

}
