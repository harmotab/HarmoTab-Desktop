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

package harmotab.core;

import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileSystemView;


/**
 * Gestion des pr�f�rences du logiciel.
 * Il s'agit des param�tres de configuration et des sauvegarde des pr�f�rences 
 * de l'utilisateur.
 */
public class GlobalPreferences {
		
	//
	// Valeurs par d�faut des pr�f�rences
	//
	
	//
	// Pr�f�rences r�gl�es par l'utilisateur
	
	private static final String		LANGUAGE = "HT_LANGUAGE";
	private static final String		USE_SYSTEM_APPEARANCE = "HT_USE_SYSTEM_APPEARANCE";
	private static final String		AUTO_TAB_ENABLED = "HT_AUTO_TAB_ENABLED";
	private static final String		AUTO_NOTE_ENABLED = "HT_AUTO_NOTE_ENABLED";
	private static final String		TAB_MAPPING_COMPLETION_ENABLED = "HT_TAB_MAPPING_COMPLETION_ENABLED";
	private static final String		BAR_NUMBERS_DISPLAYED = "HT_BAR_NUMBERS_DISPLAYED";
	private static final String		EDITING_HELPERS_DISPLAYED = "HT_EDITING_HELPERS_DISPLAYED";
	private static final String		TAB_STYLE = "HT_TAB_STYLE";
	private static final String		TAB_BLOW_DIRECTION = "HT_TAB_BLOW_DIRECTION";
	private static final String		MODELS_FOLDER = "HT_MODELS_FOLDER";
	private static final String		MIDI_OUTPUT = "HT_MIDI_OUTPUT";
	private static final String		GLOBAL_VOLUME = "HT_GLOBAL_VOLUME";
	private static final String		PLAYBACK_COUNTDOWN_ENABLED = "PLAYBACK_COUNTDOWN_ENABLED";
	private static final String		NETWORK_ENABLED = "HT_NETWORK_ENABLED";
	private static final String		PERFORMANCES_FEATURE_ENABLED = "HT_PERFORMANCES_FEATURE_ENABLED";
	private static final String		METRONOME_FEATURE_ENABLED = "HT_METRONOME_FEATURE_ENABLED";
	
	public static final String		DEFAULT_LANGUAGE = getDefaultLanguage();
	private static final boolean	DEFAULT_USE_SYSTEM_APPEARANCE = true;
	private static final boolean 	DEFAULT_AUTO_TAB_ENABLED = true;
	private static final boolean 	DEFAULT_AUTO_NOTE_ENABLED = true;
	private static final boolean	DEFAULT_TAB_MAPPING_COMPLETION_ENABLED = true;
	private static final boolean	DEFAULT_BAR_NUMBERS_DISPLAYED = false;
	private static final boolean	DEFAULT_EDITING_HELPERS_DISPLAYED = true;
	private static final int		DEFAULT_TAB_STYLE = 0;
	private static final int		DEFAULT_TAB_BLOW_DIRECTION = 0;
	private static final String		DEFAULT_MODELS_FOLDER = getDefaultModelsDirectory();
	private static final String		DEFAULT_MIDI_OUTPUT = "";
	private static final int		DEFAULT_GLOBAL_VOLUME = 100;
	private static final boolean	DEFAULT_NETWORK_ENABLED = true;
	private static final boolean 	DEFAULT_BETA_FEATURE_ENABLED = false;
	private static final boolean	DEFAULT_PLAYBACK_COUNTDOWN_ENABLED = false;
	
	//
	// Pr�f�rences automatiques
	
	private static final String 	WINDOW_WIDTH = "HT_WINDOW_WIDTH";
	private static final String		WINDOW_HEIGHT = "HT_WINDOW_HEIGHT";
	private static final String		WINDOW_MAXIMIZED = "HT_WINDOW_MAXIMIZED";
	private static final String		SCORES_BROWSING_FOLDER = "HT_SCORES_BROWSING_FOLDER";
	private static final String		METRONOME_ENABLED = "HT_METRONOME_ENABLED";

	private static final int	 	DEFAULT_WINDOW_WIDTH = 1024;
	private static final int		DEFAULT_WINDOW_HEIGHT = 768;
	private static final boolean	DEFAULT_WINDOW_MAXIMIZED = true;
	private static final String		DEFAULT_SCORES_BROWSING_FOLDER = getSamplesDirectory();
	private static final boolean	DEFAULT_METRONOME_ENABLED = false;
	
	
	//
	// Constantes pour les valeurs �num�r�es
	
	// TAB_BLOW_DIRECTION
	public static final int			BLOW_UP = 1;
	public static final int 		BLOW_DOWN = 2;


	//
	// Constructeurs
	//
	
	private static synchronized GlobalPreferences getInstance() {
		if (m_instance == null)
			m_instance = new GlobalPreferences();
		return m_instance;
	}
	
	private GlobalPreferences() {
		read();
	}
	
	
	//
	// Getters / setters des pr�f�rences r�gl�es par l'utilisateur
	//	
	
	/**
	 * Affecte la valeur par d�faut de chacune des pr�f�rences
	 */
	public static void restoreDefaultPreferences() {
		GlobalPreferences prefs = getInstance();
		prefs.m_language = DEFAULT_LANGUAGE;
		prefs.m_useSystemAppearance = DEFAULT_USE_SYSTEM_APPEARANCE;
		prefs.m_autoTabElabeld = DEFAULT_AUTO_TAB_ENABLED;
		prefs.m_autoNoteEnabled = DEFAULT_AUTO_NOTE_ENABLED;
		prefs.m_tabMappingCompletionEnabled = DEFAULT_TAB_MAPPING_COMPLETION_ENABLED;
		prefs.m_barNumbersDisplayed = DEFAULT_BAR_NUMBERS_DISPLAYED;
		prefs.m_tabStyle = DEFAULT_TAB_STYLE;
		prefs.m_midiOutput = DEFAULT_MIDI_OUTPUT;
		prefs.m_globalVolume = DEFAULT_GLOBAL_VOLUME;
		prefs.m_playbackCountdownEnabed = DEFAULT_PLAYBACK_COUNTDOWN_ENABLED;
		prefs.m_modelsFolder = DEFAULT_MODELS_FOLDER;
		prefs.m_networkEnabled = DEFAULT_NETWORK_ENABLED;
		prefs.m_performancesFeatureEnabled = DEFAULT_BETA_FEATURE_ENABLED;
		prefs.m_metronomeFeatureEnabled = DEFAULT_BETA_FEATURE_ENABLED;
		GlobalPreferences.save();
	}
		
	
	public static String getLanguage() {
		return getInstance().m_language;
	}
	
	public static void setLanguage(String language) {
		getInstance().m_language = language;
	}
	
	public static boolean useSystemAppearance() {
		return getInstance().m_useSystemAppearance;
	}
	
	public static void useSystemAppearance(boolean useIt) {
		getInstance().m_useSystemAppearance = useIt;
	}
	
	public static boolean isAutoTabEnabled() {
		return getInstance().m_autoTabElabeld;
	}
	
	public static void setAutoTabEnabled(boolean enabled) {
		getInstance().m_autoTabElabeld = enabled;
	}
	
	public static boolean isAutoNoteEnabled() {
		return getInstance().m_autoNoteEnabled;
	}
	
	public static void setAutoNoteEnabled(boolean enabled) {
		getInstance().m_autoNoteEnabled = enabled;
	}
	
	public static boolean isTabMappingCompletionEnabled() {
		return getInstance().m_tabMappingCompletionEnabled;
	}
	
	public static void setTabMappingCompletionEnabled(boolean enabled) {
		getInstance().m_tabMappingCompletionEnabled = enabled;
	}
	
	public static boolean isBarNumbersDisplayed() {
		return getInstance().m_barNumbersDisplayed;
	}
	
	public static void setBarNumbersDisplayed(boolean displayed) {
		getInstance().m_barNumbersDisplayed = displayed;
	}
	
	public static boolean isEditingHelpersDisplayed() {
		return getInstance().m_editingHelpersDisplayed;
	}
	
	public static void setEditingHelpersDisplayed(boolean displayed) {
		getInstance().m_editingHelpersDisplayed = displayed;
	}
	
	public static int getTabStyle() {
		return getInstance().m_tabStyle;
	}
	
	public static void setTabStyle(int tabStyle) {
		getInstance().m_tabStyle = tabStyle;
	}
	
	public static int getTabBlowDirection() {
		return getInstance().m_tabBlowDirection;
	}
	
	public static void setTabBlowDirection(int direction) {
		getInstance().m_tabBlowDirection = direction;
	}
	
	public static String getMidiOutput() {
		return getInstance().m_midiOutput;
	}

	public static void setMidiOutput(String output) {
		getInstance().m_midiOutput = output;
	}
	
	public static int getGlobalVolume() {
		return getInstance().m_globalVolume;
	}
	
	public static void setMidiGlobalVolume(int volume) {
		getInstance().m_globalVolume = volume;
	}
	
	public static boolean getPlaybackCountdownEnabled() {
		return getInstance().m_playbackCountdownEnabed;
	}
	
	public static void setPlaybackCountdownEnabeld(boolean enabled) {
		getInstance().m_playbackCountdownEnabed = enabled;
	}
	
	public static String getModelsFolder() {
		return getInstance().m_modelsFolder;
	}
	
	public static void setModelsFolder(String path) {
		getInstance().m_modelsFolder = path;
	}
	
	public static boolean isNetworkEnabled() {
		return getInstance().m_networkEnabled;
	}
	
	public static void setNetworkEnabled(boolean enabled) {
		getInstance().m_networkEnabled = enabled;
	}
	
	public static boolean getPerformancesFeatureEnabled() {
		return getInstance().m_performancesFeatureEnabled;
	}
	
	public static void setPerformancesFeatureEnabled(boolean enabled) {
		getInstance().m_performancesFeatureEnabled = enabled;
	}
	
	public static boolean getMetronomeFeatureEnabled() {
		return getInstance().m_metronomeFeatureEnabled;
	}
	
	public static void setMetronomeFeatureEnabled(boolean enabled) {
		getInstance().m_metronomeFeatureEnabled = enabled;
		if (enabled == false) {
			setMetronomeEnabled(false);
		}
	}
	
	
	//
	// Getters / setters des pr�f�rences r�gl�es automatiquement
	//
	
	private static Preferences getAutoPrefs() {
		return Preferences.userRoot();
	}
	
	public static int getWindowWidth() {
		return getAutoPrefs().getInt(WINDOW_WIDTH, DEFAULT_WINDOW_WIDTH);
	}
	
	public static int getWindowHeight() {
		return getAutoPrefs().getInt(WINDOW_HEIGHT, DEFAULT_WINDOW_HEIGHT);
	}
	
	public static void setWindowSize(int width, int height) {
		Preferences prefs = getAutoPrefs(); 
		prefs.putInt(WINDOW_WIDTH, width);
		prefs.putInt(WINDOW_HEIGHT, height);
	}
		
	public static boolean getWindowMaximized() {
		return getAutoPrefs().getBoolean(WINDOW_MAXIMIZED, DEFAULT_WINDOW_MAXIMIZED);
	}
	
	public static void setWindowMaximized(boolean maximized) {
		getAutoPrefs().putBoolean(WINDOW_MAXIMIZED, maximized);
	}
	
	public static String getScoresBrowsingFolder() {
		return getAutoPrefs().get(SCORES_BROWSING_FOLDER, DEFAULT_SCORES_BROWSING_FOLDER);
	}
	
	public static void setScoresBrowsingFolder(String path) {
		getAutoPrefs().put(SCORES_BROWSING_FOLDER, path);
	}
	
	public static boolean getMetronomeEnabled() {
		return getAutoPrefs().getBoolean(METRONOME_ENABLED, DEFAULT_METRONOME_ENABLED);
	}
	
	public static void setMetronomeEnabled(boolean enabled) {
		getAutoPrefs().putBoolean(METRONOME_ENABLED, enabled);
	}
	
	
	//
	// Lecture / ecriture des pr�f�rences dans un fichier 
	//
	
	/**
	 * Lecture des pr�f�rences
	 */
	private synchronized void read() {
		Preferences prefs = Preferences.userRoot();
		m_language = prefs.get(LANGUAGE, DEFAULT_LANGUAGE);
		m_useSystemAppearance = prefs.getBoolean(USE_SYSTEM_APPEARANCE, DEFAULT_USE_SYSTEM_APPEARANCE);
		m_autoTabElabeld = prefs.getBoolean(AUTO_TAB_ENABLED, DEFAULT_AUTO_TAB_ENABLED);
		m_autoNoteEnabled = prefs.getBoolean(AUTO_NOTE_ENABLED, DEFAULT_AUTO_NOTE_ENABLED);
		m_tabMappingCompletionEnabled = prefs.getBoolean(TAB_MAPPING_COMPLETION_ENABLED, DEFAULT_TAB_MAPPING_COMPLETION_ENABLED);
		m_barNumbersDisplayed = prefs.getBoolean(BAR_NUMBERS_DISPLAYED, DEFAULT_BAR_NUMBERS_DISPLAYED);
		m_editingHelpersDisplayed = prefs.getBoolean(EDITING_HELPERS_DISPLAYED, DEFAULT_EDITING_HELPERS_DISPLAYED);
		m_tabStyle = prefs.getInt(TAB_STYLE, DEFAULT_TAB_STYLE);
		m_tabBlowDirection = prefs.getInt(TAB_BLOW_DIRECTION, DEFAULT_TAB_BLOW_DIRECTION);
		m_midiOutput = prefs.get(MIDI_OUTPUT, DEFAULT_MIDI_OUTPUT);
		m_globalVolume = prefs.getInt(GLOBAL_VOLUME, DEFAULT_GLOBAL_VOLUME);
		m_playbackCountdownEnabed = prefs.getBoolean(PLAYBACK_COUNTDOWN_ENABLED, DEFAULT_PLAYBACK_COUNTDOWN_ENABLED);
		m_modelsFolder = prefs.get(MODELS_FOLDER, DEFAULT_MODELS_FOLDER);
		m_networkEnabled = prefs.getBoolean(NETWORK_ENABLED, DEFAULT_NETWORK_ENABLED);
		m_performancesFeatureEnabled = prefs.getBoolean(PERFORMANCES_FEATURE_ENABLED, DEFAULT_BETA_FEATURE_ENABLED);
		m_metronomeFeatureEnabled = prefs.getBoolean(METRONOME_FEATURE_ENABLED, DEFAULT_BETA_FEATURE_ENABLED);
	}
	
	/**
	 * Sauvegarde des pr�f�rences
	 */
	private synchronized void write() {
		Preferences prefs = Preferences.userRoot();
		prefs.put(LANGUAGE, m_language);
		prefs.putBoolean(USE_SYSTEM_APPEARANCE, m_useSystemAppearance);
		prefs.putBoolean(AUTO_TAB_ENABLED, m_autoTabElabeld);
		prefs.putBoolean(AUTO_NOTE_ENABLED, m_autoNoteEnabled);
		prefs.putBoolean(TAB_MAPPING_COMPLETION_ENABLED, m_tabMappingCompletionEnabled);
		prefs.putBoolean(BAR_NUMBERS_DISPLAYED, m_barNumbersDisplayed);
		prefs.putBoolean(EDITING_HELPERS_DISPLAYED, m_editingHelpersDisplayed);
		prefs.putInt(TAB_STYLE, m_tabStyle);
		prefs.putInt(TAB_BLOW_DIRECTION, m_tabBlowDirection);
		prefs.put(MIDI_OUTPUT, m_midiOutput);
		prefs.putInt(GLOBAL_VOLUME, m_globalVolume);
		prefs.putBoolean(PLAYBACK_COUNTDOWN_ENABLED, m_playbackCountdownEnabed);
		prefs.put(MODELS_FOLDER, m_modelsFolder);
		prefs.putBoolean(NETWORK_ENABLED, m_networkEnabled);
		prefs.putBoolean(PERFORMANCES_FEATURE_ENABLED, m_performancesFeatureEnabled);
		prefs.putBoolean(METRONOME_FEATURE_ENABLED, m_metronomeFeatureEnabled);
		firePreferenceChange();
	}
	
	/**
	 * Enregistrement des pr�f�rences
	 */
	public static void save() {
		m_instance.write();
	}
	
	
	//
	// Ev�nements de changement des pr�f�rences
	//
	
	public static void addChangeListener(ChangeListener listener) {
		getInstance().m_listeners.add(ChangeListener.class, listener);
	}
	
	public static void removeChangeListener(ChangeListener listener) {
		getInstance().m_listeners.remove(ChangeListener.class, listener);
	}
	
	
	private void firePreferenceChange() {
		for (ChangeListener listener : m_listeners.getListeners(ChangeListener.class))
			listener.stateChanged(new ChangeEvent(this));
	}
		
		
	//
	// M�thodes utilitaires
	//
	
	/**
	 * Retourne la langue du syst�me d'exploitation si il fait parti des 
	 * langues disponibles, sinon la langue par d�faut.
	 */
	private static String getDefaultLanguage() {
		String lang = Locale.getDefault().getLanguage();
		if (lang.equals("en") || lang.equals("fr"))
			return lang;
		return "en";
	}
	
	/**
	 * Retourne le r�pertoire par d�faut des documents de l'utilsateur
	 */
	public static String getUserDefaultDirectory() {
		return FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
	}
	
	/**
	 * Retourne le r�pertoire "samples" de l'installation
	 */
	private static String getSamplesDirectory() {
		return System.getProperty("user.dir") + File.separator + "samples" + File.separator;
	}

	/**
	 * Retourne le r�pertoire "models" de l'installation
	 */
	private static String getDefaultModelsDirectory() {
		return System.getProperty("user.dir") + File.separator + "samples" + File.separator;
	}

	
	//
	// Attributs
	//
	
	private static GlobalPreferences m_instance = null;
	private EventListenerList m_listeners = new EventListenerList();
	
	private String m_language;
	private boolean m_useSystemAppearance;
	private boolean m_autoTabElabeld;
	private boolean m_autoNoteEnabled;
	private boolean m_tabMappingCompletionEnabled;
	private boolean m_barNumbersDisplayed;
	private boolean m_editingHelpersDisplayed;
	private int m_tabStyle;
	private int m_tabBlowDirection;
	private String m_midiOutput;
	private int m_globalVolume;
	private boolean m_playbackCountdownEnabed;
	private String m_modelsFolder;
	private boolean m_networkEnabled;
	private boolean m_performancesFeatureEnabled;
	private boolean m_metronomeFeatureEnabled;

}
