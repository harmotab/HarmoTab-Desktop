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

package harmotab.core.undo;

import java.util.LinkedList;


/**
 * Gestion des annulations/r�stauration sur l'ensemble du logiciel
 */
public class UndoManager {
	
	public final static int UNDO_MAX_LEVEL = 20;
	

	//
	// Constructeurs / singleton
	//
	
	private UndoManager() {
		m_undoCommands = new LinkedList<RestoreCommand>();
		m_undoLabels = new LinkedList<String>();
		m_redoCommands = new LinkedList<RestoreCommand>();
		m_redoLabels = new LinkedList<String>();
	}
	
	
	public static synchronized UndoManager getInstance() {
		if (m_instance == null) {
			m_instance = new UndoManager();
		}
		return m_instance;
	}
	
	
	//
	// Getters / setters
	//
	
	/**
	 * Indique si des commande d'annulation sont stock�es
	 */
	public boolean hasUndoCommands() {
		return !m_undoCommands.isEmpty();
	}
	
	/**
	 * Retourne la description de la premi�re commande d'annulation
	 */
	public String getTopUndoLabel() {
		return m_undoLabels.peek();
	}
	
	/**
	 * Indique si des commandes de restauration sont stock�es
	 */
	public boolean hasRedoCommands() {
		return !m_redoCommands.isEmpty();
	}
	
	/**
	 * Retourne la description de la premi�re commande de restauration
	 */
	public String getTopRedoLabel() {
		return m_undoLabels.peek();
	}
	
	
	//
	// M�thodes pour annuler / refaire
	//
	
	/**
	 * Remise � z�ro des stocks de commandes d'annulations et de r�stauration
	 */
	public void reset() {
		m_undoCommands.clear();
		m_undoCommands.clear();
		m_redoCommands.clear();
		m_redoLabels.clear();
	}
	
	/**
	 * Ex�cute une commande d'annulation 
	 */
	public void undo() {
		if (m_undoCommands.size() > 0) {
			RestoreCommand command = m_undoCommands.pop();
			String label = m_undoLabels.pop();
			addRedoCommand(command.getInvertCommand(), label);
			command.execute();
		}
	}

	/**
	 * Ex�cute une commande de r�stauration
	 */
	public void redo() {
		if (m_redoCommands.size() > 0) {
			RestoreCommand command = m_redoCommands.pop();
			String label = m_redoLabels.pop();
			addUndoCommandKeepingRedoStack(command.getInvertCommand(), label);
			command.execute();
		}
	}
	
	
	//
	// M�thodes pour ajouter des actions
	//
	
	/**
	 * Ajout d'une commande d'annulation.
	 * Cette m�thode vide le stock de commandes de restaurations
	 */
	public void addUndoCommand(RestoreCommand command, String label) {
		m_redoCommands.clear();
		m_redoLabels.clear();
		addUndoCommandKeepingRedoStack(command, label);
	}
	
	
	/**
	 * Ajoute une commande � la derni�re commande undo enregistr�e.
	 */
	public void appendToLastUndoCommand(RestoreCommand command) {
		// R�cup�re la derni�re commande enregistr�e
		RestoreCommand lastCommand = m_undoCommands.pop();
		// Cr�er un groupe de commande avec la derni�re commande si ce n'est 
		// pas d�j� un groupe
		if (!(lastCommand instanceof RestoreCommandGroup)) {
			RestoreCommandGroup commandGroup = new RestoreCommandGroup();
			commandGroup.add(lastCommand);
			lastCommand = commandGroup;
		}
		// Y ajoute la commande sp�cifi�e
		RestoreCommandGroup commandGroup = (RestoreCommandGroup) lastCommand;
		commandGroup.add(command);
		// Replace la commande dans la pile de commandes
		m_undoCommands.push(commandGroup);
	}
	
	
	/**
	 * Ajout d'une commande d'annulation.
	 * Cette m�thode ne vide pas le stock de commandes de restauration
	 */
	private void addUndoCommandKeepingRedoStack(RestoreCommand command, String label) {
		m_undoCommands.push(command);
		m_undoLabels.push(label);
		if (m_undoCommands.size() > UNDO_MAX_LEVEL) {
			m_undoCommands.removeLast();
			m_undoLabels.removeLast();
		}
	}
	
	/**
	 * Ajout d'une commande de r�stauration
	 */
	private void addRedoCommand(RestoreCommand command, String label) {
		m_redoCommands.push(command);
		m_redoLabels.push(label);
		if (m_redoCommands.size() > UNDO_MAX_LEVEL) {
			m_redoCommands.removeLast();
			m_redoLabels.removeLast();
		}
	}
	
	
	
	// 
	// Méthode de débug
	// 
	
	public void printStackTrace() {
		System.out.println("-----------");
		System.out.println("Undo stack:");
		int index = 0;
		for (RestoreCommand command : m_undoCommands) {
			System.out.println(index + ". " + m_undoLabels.get(index) + " \t" + command);
			index++;
		}
		System.out.println("Redo stack:");
		index = 0;
		for (RestoreCommand command : m_redoCommands) {
			System.out.println(index + ". " + m_redoLabels.get(index) + " \t" + command);
			index++;
		}
	}
	
	
	//
	// Attributs
	//
	
	private static UndoManager m_instance = null;
	
	private LinkedList<RestoreCommand> m_undoCommands = null;
	private LinkedList<String> m_undoLabels = null;
	private LinkedList<RestoreCommand> m_redoCommands = null;
	private LinkedList<String> m_redoLabels = null;
	
}

