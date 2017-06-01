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

package harmotab.harmonica;

import harmotab.core.Height;
import harmotab.core.undo.UndoManager;
import harmotab.element.*;
import harmotab.throwables.*;
import harmotab.track.HarmoTabTrack;

public class TabModelController {
	
	//
	// Constructeurs
	//
	
	public TabModelController(TabModel tabModel) {
		m_tabModel = tabModel;
	}
	
	
	//
	// Getters / setters
	//
	
	public TabModel getTabModel() {
		return m_tabModel;
	}
	
	
	//
	// M�thodes de controles
	//
	
	/**
	 * Cr�er le mod�le de tablature en utilisant un mod�le d'harmonica.
	 */
	public void populateFromHarmonicaModel(Harmonica harmonica, Height referenceHeight, Tab referenceTab) {
		// D�but du traitement
		m_tabModel.setDispachEvents(false, null);
		
		// Calcul l'offset � utiliser pour la prise en compte de la note de r�f�rence
		int numberOfHoles = harmonica.getModel().getNumberOfHoles();
		m_tabModel.reset();
		
		Height standardHeight = harmonica.getHeight(referenceTab);
		int offset = referenceHeight.getOctave() - standardHeight.getOctave();
		
		// Affecte une tab � chaque hauteur
		for (int hole = 1; hole <= numberOfHoles; hole++) {
			for (byte i = 0; i < 6; i++) {
				Tab tab = HarmonicaModel.createTab(hole, i);
				Height height = harmonica.getHeight(tab);
				if (height != null) {
					try {
						height.setOctave(height.getOctave() + offset);
						m_tabModel.setTab(height, tab);
					}
					catch (OutOfBoundsError e) {}
				}
			}
		}
		
		// Fin du traitement
		m_tabModel.setDispachEvents(true, TabModel.MAPPING_ATTR);
	}
	
	
	/**
	 * Cr�er un mod�le de tablatures en utilisant les tablatures indiqu�es dans
	 * une piste.
	 */
	public void populateFromHarmoTabTrack(HarmoTabTrack htTrack) {
		// D�but du traitement
		m_tabModel.setDispachEvents(false, null);
		
		TabAutomationProcessor tabAutoProc = new TabAutomationProcessor(m_tabModel);
		
		// Parcours tous les HarmoTabElement et met � jour le mod�le avec leur 
		// tablature
		for (Element element : htTrack) {
			if (element instanceof HarmoTabElement) {
				HarmoTabElement htElement = (HarmoTabElement) element;
				tabAutoProc.updateTabModel(htElement);
			}
		}
		
		// Fin du traitement
		m_tabModel.setDispachEvents(true, TabModel.MAPPING_ATTR);	
	}
	
	
	/**
	 * Met � jour toutes les tablatures d'une piste en utilisant le mod�le de 
	 * tablatures.
	 * Les actions d'annulation correspondant sont ajoutées à la dernière 
	 * commande d'annulation.
	 */
	public void updateTabs(HarmoTabTrack track) {
		track.setDispachEvents(false, null);
		UndoManager undo = UndoManager.getInstance();
		
		for (Element element : track) {
			if (element instanceof HarmoTabElement) {
				HarmoTabElement htElement = (HarmoTabElement) element;
				Tab tab = m_tabModel.getTab(htElement.getHeight());
				if (tab != null) {
					undo.appendToLastUndoCommand(htElement.createRestoreCommand());
					htElement.setTab(tab);
				}
			}
		}
		
		track.setDispachEvents(true, HarmoTabTrack.ELEMENT_CHANGED_EVENT);
	}
	
	
	// 
	// Attributs
	// 
	
	private TabModel m_tabModel = null;
	
}
