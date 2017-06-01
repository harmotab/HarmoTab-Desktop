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

package harmotab.track.layout;

import java.util.Iterator;

import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.renderer.renderingelements.EmptyArea;
import harmotab.renderer.renderingelements.HarmonicaProperties;
import harmotab.renderer.renderingelements.Staff;
import harmotab.renderer.renderingelements.TabArea;
import harmotab.track.*;


/**
 * Gestion du positionnement des éléments d'une piste de type HarmoTabTrack.
 */
public class HarmoTabTrackLayout extends StaffTrackLayout {

	private static final int TAB_TRACK_HEIGHT = 45;
	private static final int TAB_AREA_HEADER_WIDTH = 30;
	
	
	public HarmoTabTrackLayout(Track track) {
		super(track);
	}
	
	
	@Override
	public void processElementsPositionning(LocationList locations, int areaWidth, float scoreDuration) {
		super.processElementsPositionning(locations, areaWidth, scoreDuration);
		LocationList localHeadLocations = new LocationList();
		LocationList localTailLocations = new LocationList();
		
		// R�cup�re la premi�re port�e pour ajouter le type d'harmonica
		Iterator<LocationItem> itemIterator = locations.getIterator();
		LocationItem staffLocationItem = null;
		while (itemIterator.hasNext() && 
				(staffLocationItem == null || !(staffLocationItem.getElement() instanceof Staff)))
			staffLocationItem = itemIterator.next();
		if (staffLocationItem != null) {
			HarmonicaProperties harmoProps = new HarmonicaProperties(((HarmoTabTrack) m_track).getHarmonica());
			LocationItem harmoPropsItem = new LocationItem(staffLocationItem);
			harmoPropsItem.translate(0, harmoPropsItem.m_height - 40);
			harmoPropsItem.resize(60, 50);
			harmoPropsItem.m_element = harmoProps;
			localHeadLocations.add(harmoPropsItem);
		}
		else {
			System.err.println("HarmoTabTrackLayout::processElementsPositionning: First staff not found !");
		}
		
		// Parcours tous les éléments pour ajouter les items nécessaires
		int staffTrackHeight = super.getTrackHeight();
		LocationItem firstNoteOfTheLine = null;
		
		for (LocationItem item : locations) {
			Element element = item.getElement();
			// Ajoute un item de tab pour chacune des tabs
			if (element instanceof HarmoTabElement) {
				HarmoTabElement htElement = (HarmoTabElement) element;
				if (firstNoteOfTheLine == null) {
					firstNoteOfTheLine = item;
				}
				if (htElement.canHaveTab() == true) {
					// R�duction de la taille de l'item de la note
					item.resize(item.m_width, staffTrackHeight);
					// Cr�ation d'un LocationItem pour la Tab
					LocationItem newItem = (LocationItem) item.clone();
					newItem.m_element = ((HarmoTabElement) element).getTab();
					newItem.resize(item.m_width, TAB_TRACK_HEIGHT);
					newItem.translate(0, staffTrackHeight);
					newItem.setParent(element);
					localHeadLocations.add(newItem);
				}
			}
			// Ajoute un item de TabArea pour chacune des lignes
			else if (element instanceof Staff) {
				LocationItem newItem = (LocationItem) item.clone();
				// Calcul de la position de début de l'item
				int dx = firstNoteOfTheLine != null ? firstNoteOfTheLine.getX1() : item.getX1();
				int delta = dx - item.getX1() - TAB_AREA_HEADER_WIDTH;
				int newWidth = item.m_width - delta;
				// Façonage de l'item pour le TabArea
				newItem.m_element = new TabArea();
				newItem.m_poiX -= item.m_width - newWidth;
				newItem.resize(newWidth, TAB_TRACK_HEIGHT);
				newItem.translate(delta, staffTrackHeight);
				newItem.setParent(null);
				localTailLocations.add(newItem);
				firstNoteOfTheLine = null;
			}
			// Ajoute un item de barre de mesure pour chacune des barres
			else if (element instanceof Bar) {
				LocationItem newItem = (LocationItem) item.clone();
				newItem.resize(0, TAB_TRACK_HEIGHT);
				newItem.translate(0, staffTrackHeight - 42);
				newItem.setParent(element);
				newItem.m_flag = 0;
				localTailLocations.add(newItem);
				// Prend en compte l'item comme positionnement possible du premier élément de la ligne
				if (firstNoteOfTheLine == null)
					firstNoteOfTheLine = item;
			}
			// Prend en compte les EmptyArea comme positionnement possible du premier élément de la ligne
			else if (element instanceof EmptyArea) {
				if (firstNoteOfTheLine == null)
					firstNoteOfTheLine = item;
			}
		}
		
		// Ajout de la liste des locations de Tabs
		locations.add(0, localHeadLocations);
		locations.add(localTailLocations);
	}
	
	
	@Override
	public int getTrackHeight() {
		return super.getTrackHeight() + TAB_TRACK_HEIGHT;
	}
	
}
