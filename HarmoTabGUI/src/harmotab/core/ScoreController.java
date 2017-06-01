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

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.*;
import harmotab.HarmoTabConstants;
import harmotab.core.undo.UndoManager;
import harmotab.desktop.*;
import harmotab.desktop.setupdialog.*;
import harmotab.element.*;
import harmotab.io.score.*;
import harmotab.performance.*;
import harmotab.sound.*;
import harmotab.throwables.ScoreIoException;
import harmotab.track.*;


/**
 * Gestion des fonctionnalités associées à une partition.
 */
public class ScoreController implements PerformanceListListener {
	
	public final static String SCORE_OPENNED_EVENT = "scoreOpenned";
	public final static String NEW_SCORE_EVENT = "newScore";
	public final static String SCORE_SAVED_EVENT = "scoreSaved";
	public final static String PERFORMANCE_LIST_CHANGED_EVENT = "performanceList";
	
	
	//
	// Constructeur
	//
	
	public ScoreController(Score score) {
		m_listeners = new ArrayList<ScoreControllerListener>();
		m_performancesList = null;
		setScore(score);
	}
	
	public ScoreController() {
		this(null);
	}
	
	
	//
	// Getters / setters
	//
	
	protected void setScore(Score score) {
		m_score = score;

		if (m_score != null) {
			m_score.addObjectListener(new ScoreObserver());
		}
		else {
		}

		setScorePlayer(null);
		m_currentScoreWriter = null;
		m_scoreChanged = false;
		
		fireScoreControlledChanged();
	}
	
	public Score getScore() {
		return m_score;
	}
	
	public boolean hasScore() {
		return (m_score != null);
	}
	
	public boolean isScoreEditable() {
		return !m_exportedScore;
	}
	
	public boolean isExportedScore() {
		return m_exportedScore;
	}
	
	public boolean hasScoreChanged() {
		return m_scoreChanged;
	}
	
	
	/**
	 * Retourne le lecteur utilisé pour lire la partition
	 */
	public ScorePlayer getScorePlayer() {
		return m_scorePlayer;
	}
	
	/**
	 * Affecte le lecteur utilisé pour lire la partition.
	 * Si le lecteur est fermé, l'ouvre.
	 */
	public void setScorePlayer(ScorePlayer player) {
		ScorePlayer midiScorePlayer = MidiScorePlayer.getInstance();
		
		// Fermeture de l'ancien SoundPlayer
		if (m_scorePlayer != null) {
			// Ferme le lecteur sauf s'il s'agit du lecteur par défaut qui doit rester ouvert 
			if (m_scorePlayer.isOpenned() && m_scorePlayer != midiScorePlayer) {
				m_scorePlayer.close();
			}
		}
		
		// Affectation du nouveau SoundPlayer
		m_scorePlayer = player != null ? player : midiScorePlayer;
		
		// Ouverture du nouveau SoundPlayer
		if (!m_scorePlayer.isOpenned()) {
			// Ouverture asynchrone pour le lecteur midi (ouvert au démarrage de l'application)
			if (m_scorePlayer == midiScorePlayer) {
				m_scorePlayer.asynchronousOpen();
			}
			// Ouverture synchrone pour les autres lecteurs
			else {
				m_scorePlayer.open();
			}
		}
		
		// Signalement de la modification
		fireScorePlayerChanged();
	}
	
	/**
	 * Affecte le lecteur par défaut (lecteur MIDI)
	 */
	public void setDefaultSoundPlayer() {
		setScorePlayer(null);
	}
	
	public PerformancesList getPerformancesList() {
		return m_performancesList;
	}
	
	public void setPerformancesList(PerformancesList perfsList) {
		if (m_performancesList != null) {
			m_performancesList.removePerformanceListListener(this);
		}
		m_performancesList = perfsList;
		if (m_performancesList != null) {
			m_performancesList.addPerformanceListListener(this);
		}
	}
	
	public ScoreWriter getCurrentScoreWriter() {
		return m_currentScoreWriter;
	}
	
	
	//
	// Ouverture / enregistrement / export / impression...
	//
	
	/**
	 * Création d'une partition vièrge
	 * @return	VRAI si une partition vièrge est créée, FAUX si l'action est 
	 * 			annulée par l'utilisateur
	 */
	public boolean createNewDefaultScore() {
		if (close()) {
			setScore(new Score());
			m_score.setDispachEvents(false, null);
			m_exportedScore = false;
			try {
				StaffTrack harmoTabTrack = new HarmoTabTrack(m_score);
				harmoTabTrack.add(new Bar(new Key(), new KeySignature(), new TimeSignature(), new RepeatAttribute()));
				Track accompanimentTrack = new AccompanimentTrack(m_score, harmoTabTrack);
				Track lyricsTrack = new LyricsTrack(m_score, harmoTabTrack);
				
				m_score.addTrack(accompanimentTrack);
				m_score.addTrack(harmoTabTrack);
				m_score.addTrack(lyricsTrack);
			}
			catch (Throwable e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_NEW_SCORE_ERROR));
			}
			m_score.setDispachEvents(true, NEW_SCORE_EVENT);
			UndoManager.getInstance().reset();
			fireScoreControlledChanged();
			return true;
		}
		return false;
	}
	
	
	/**
	 * Ouverture d'un fichier.
	 * @throws IOException 
	 */
	public boolean open() throws ScoreIoException {	
		// S�lection du fichier
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(Localizer.get(i18n.ET_HARMOTAB_SCORE), "ht3", "htb", "ht3x"));
		
		// Si un fichier est s�lectionn� pour ouverture
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			// Ouverture du fichier
			String path = chooser.getSelectedFile().getPath();
			boolean openned = open(path);
			return openned;
		}
		return false;
	}
	
	public boolean open(String filePath) throws ScoreIoException {
		// Fermeture du fichier courant
		if(!close())
			return false;

		// Lecture du fichier
		try {
			if (!hasScore()) {
				setScore(new Score());
			}
			
			m_score.setDispachEvents(false, null);
			
			// Lecture du fichier en param�tre
			ScoreReader reader = ScoreIOUtilities.createScoreReader(m_score, filePath);
			reader.open();
			m_exportedScore = reader.isExportedScore();
			setPerformancesList(reader.getPerformancesList());

			// Prise en compte de l'ouverture
			m_currentScoreWriter = ScoreIOUtilities.createScoreWriter(m_score, filePath, m_performancesList);
 
			try {
				m_score.setDispachEvents(true, SCORE_OPENNED_EVENT);
			}
			catch( Throwable e )  {
				System.err.println("ScoreController::open()");
				e.printStackTrace();
			}

			UndoManager.getInstance().reset();
			fireScoreControlledChanged();
		} 
		catch (Throwable e) {
			close();
			throw new ScoreIoException(e, filePath);
		}
		
		return true;
	}
	
	
	/**
	 * Enregistrement de la partition dans un fichier.
	 * L'enregistrement est effectu� dans le fichier courant s'il existe.
	 */
	public boolean saveScore() {
		m_currentScoreWriter = ScoreIOUtilities.saveScore(null, this, m_currentScoreWriter);
		
		if (m_currentScoreWriter != null) {
			m_score.setDispachEvents(true, SCORE_SAVED_EVENT);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Enregistrement de la partition dans un fichier choisit par l'utilisateur.
	 */
	public boolean saveScoreAs() {
		m_currentScoreWriter = ScoreIOUtilities.saveScore(null, this, null);
		
		if (m_currentScoreWriter != null) {
			m_score.setDispachEvents(true, SCORE_SAVED_EVENT);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Enregistrement de la partition dans le fichier pass� en param�tre
	 */
	public boolean saveScoreAs(String path) {
		ScoreWriter writer = ScoreIOUtilities.createScoreWriter(m_score, path);
		if (writer != null) {
			m_currentScoreWriter = ScoreIOUtilities.saveScore(null, this, writer);
			
			if (m_currentScoreWriter != null) {
				m_score.setDispachEvents(true, SCORE_SAVED_EVENT);
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Enregistrement du fichier courant dans un fichier sp�cifi�
	 */
	public void saveAs(String path) throws ScoreIoException {
		ScoreWriter writer = ScoreIOUtilities.createScoreWriter(m_score, path, m_performancesList);
		if (writer != null) {
			m_currentScoreWriter = ScoreIOUtilities.saveScore(null, this, writer);
			
			if (m_currentScoreWriter != null) {
				m_score.setDispachEvents(true, SCORE_SAVED_EVENT);
			}
		}
		else {
			throw new ScoreIoException(null, path);
		}
	}
	
	/**
	 * Enregistrement du fichier courant
	 */
	public void save() throws ScoreIoException {
		if (m_currentScoreWriter != null) {
			saveAs(m_currentScoreWriter.getFile().getAbsolutePath());
		}
		else {
			saveScoreAs();
		}
	}
	
	
	/**
	 * Fermeture de la partition courante.
	 */
	public boolean close() {
		if (!hasScore()) {
			return true;
		}
		
		// Si des changements sont d�tect�s, demande � l'utilisateur s'il veut 
		// enregistrer les changements
		if (m_scoreChanged) {
			int res = JOptionPane.showConfirmDialog(
					null, 
					Localizer.get(i18n.M_SAVE_BEFORE_CLOSING_QUESTION), 
					"HarmoTab", 
					JOptionPane.YES_NO_CANCEL_OPTION);
			// Annuler -> annule la fermeture
			if (res == JOptionPane.CANCEL_OPTION)
				return false;
			// Oui -> effectue la sauvegarde
			if (res == JOptionPane.YES_OPTION) {
				m_currentScoreWriter = ScoreIOUtilities.saveScore(null, this, m_currentScoreWriter);
				// Si la sauvegare demand�e est annul�e, annule la fermeture
				if (m_currentScoreWriter == null)
					return false;
			}
			// Non -> ferme sans sauvegarder
		}
		
		// Effectue la fermeture
		m_currentScoreWriter = null;
		m_performancesList = null;
		UndoManager.getInstance().reset();
		
		// Affecte une partition nulle (avec transmission de l'évènement l'indiquant aux listeners)
		setScore(null);
		return true;
	}
	
	
	/**
	 * Export de la partition dans un fichier midi.
	 */
	public boolean exportAsMidi() {
		
		// S�l�ction du fichier en sortie
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(Localizer.get(i18n.ET_MIDI_FILE), "mid", "midi"));
		chooser.setSelectedFile(new File(m_score.getScoreName() + ".mid"));
		
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile().exists()) {
				int res = JOptionPane.showConfirmDialog(
					null,
					Localizer.get(i18n.M_FILE_ALREADY_EXISTS_QUESTION).replace("%FILE%", chooser.getSelectedFile().getPath()),
					Localizer.get(i18n.ET_EXPORT_AS_MIDI_FILE), 
					JOptionPane.YES_NO_OPTION);
				if (res != JOptionPane.YES_OPTION)
					return false;
			}
			
			String outputFile = chooser.getSelectedFile().getPath();
			if (!outputFile.endsWith(".mid") && !outputFile.endsWith(".midi"))
				outputFile += ".mid";
			MidiScoreWriter writer = new MidiScoreWriter(m_score, outputFile);
			try {
				writer.save();
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage(Localizer
						.get(i18n.M_ERROR_CREATING_FILE)
						.replace("%FILE%", chooser.getSelectedFile().getPath()));
			}
		}
		return false;
	}
	
	public boolean exportAsMidi(String path) throws IOException {
		MidiScoreWriter writer = new MidiScoreWriter(m_score, path);
		writer.save();
		return true;
	}
	
	
	/**
	 * Export de la partition dans un fichier ht3x.
	 * Retourne le fichier dans lequel est export� la partition, NULL si 
	 * l'export n'a pas �t� effectu�.
	 */
	public File exportAsExportedScore() {
		
		// S�l�ction du fichier en sortie
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter(Localizer.get(i18n.ET_HT3X_FILE), "ht3x"));
		chooser.setSelectedFile(new File(m_score.getScoreName() + ".ht3x"));
		
		if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile().exists()) {
				int res = JOptionPane.showConfirmDialog(
					null,
					Localizer.get(i18n.M_FILE_ALREADY_EXISTS_QUESTION).replace("%FILE%", chooser.getSelectedFile().getPath()),
					Localizer.get(i18n.ET_EXPORT_AS_HT3X_FILE), 
					JOptionPane.YES_NO_OPTION);
				if (res != JOptionPane.YES_OPTION) {
					return null;
				}
			}
			
			String outputFile = chooser.getSelectedFile().getPath();
			if (!outputFile.endsWith(".ht3x")) {
				outputFile += ".ht3x";
			}
			
			HT3XScoreWriter ht3xWriter = new HT3XScoreWriter(m_score, outputFile);
			try {
		    	ht3xWriter.save();
				return new File(outputFile);
			}
			catch (Exception e) {
				e.printStackTrace();
				ErrorMessenger.showErrorMessage(Localizer
						.get(i18n.M_ERROR_CREATING_FILE)
						.replace("%FILE%", chooser.getSelectedFile().getPath()));
			}
		}
		return null;
	}
	
	public boolean exportAsExportedScore(String path) throws IOException {
		HT3XScoreWriter ht3xWriter = new HT3XScoreWriter(m_score, path);
    	ht3xWriter.save();
		return true;
	}
	
	
	/**
	 * Export de la partition en format image
	 */
	public void exportAsImage() {
		new PngExportSetupDialog(null, m_score).setVisible(true);
	}
	
	public void exportAsImage(String path) throws IOException {
		// R�cup�ration du format d�sir�
		int width = HarmoTabConstants.DEFAULT_SCORE_WIDTH;
		int height = Integer.MAX_VALUE;
		
		// Cr�ation des images
		ScoreWriter writer = new PngFileWriter(m_score, path, width, height);
		writer.save();
	}
	
	
	/**
	 * Impression
	 */
	public void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
	      job.setPrintable(new ScorePrintable(m_score));
	      job.setJobName(m_score.getScoreName());
	      if (job.printDialog()){
	         try {
	            job.print();
	         } catch (PrinterException ex) {
	            ex.printStackTrace();
	            ErrorMessenger.showErrorMessage(Localizer.get(i18n.M_PRINTING_ERROR));
	         }
	      }
	}
	
	
	//
	// Ecoute des diff�rents �v�nemets du logiciel
	//
	
	private class ScoreObserver implements HarmoTabObjectListener {
		@Override
		public void onObjectChanged(HarmoTabObjectEvent event) {
			// Prend note des modifications de la partition
			if (event.propertyIs(SCORE_OPENNED_EVENT) || 
					event.propertyIs(SCORE_SAVED_EVENT) ||
					event.propertyIs(NEW_SCORE_EVENT)) {
				m_scoreChanged = false;
			}
			else { 
				m_scoreChanged = true;
			}
			
			// En cas de modification, si la lecture de la partition est en 
			// cours, l'arr�te
			if (m_scorePlayer != null && m_scorePlayer.isPlaying()) {
				m_scorePlayer.stop();
			}
		}
	}
	
	
	@Override
	public void onPerformanceListChanged(PerformancesList list) {
		m_score.fireObjectChanged(PERFORMANCE_LIST_CHANGED_EVENT);
	}

	@Override
	public void onDefaultPerformanceChanged(PerformancesList list) {
		m_score.fireObjectChanged(PERFORMANCE_LIST_CHANGED_EVENT);
	}
	
	@Override
	public void onPerformanceListItemChanged(PerformancesList list, Performance perf) {
		m_score.fireObjectChanged(PERFORMANCE_LIST_CHANGED_EVENT);
	}
	
	
	//
	// Gestion des listeners
	//
	
	public void addScoreControllerListener(ScoreControllerListener listener) {
		m_listeners.add(listener);
	}
	
	public void removeScoreControllerListener(ScoreControllerListener listener) {
		m_listeners.remove(listener);
	}
	
	
	protected void fireScoreControlledChanged() {
		for (ScoreControllerListener listener : m_listeners) {
			listener.onControlledScoreChanged(this, m_score);
		}
	}
	
	protected void fireScorePlayerChanged() {
		for (ScoreControllerListener listener : m_listeners) {
			listener.onScorePlayerChanged(this, m_scorePlayer);
		}
	}
	
	
	// 
	// Attributs
	// 
	
	private Score m_score = null;
	private ScorePlayer m_scorePlayer = null;
	
	private ScoreWriter m_currentScoreWriter = null;
	private boolean m_scoreChanged = false;
	private boolean m_exportedScore = false;
	private PerformancesList m_performancesList = null;
	
	private ArrayList<ScoreControllerListener> m_listeners = null;

}

