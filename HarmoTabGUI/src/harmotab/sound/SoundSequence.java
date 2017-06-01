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

package harmotab.sound;

import harmotab.element.Element;

import java.util.*;


public class SoundSequence extends LinkedList<SoundItem> implements Cloneable {
	private static final long serialVersionUID = 1L;

	//
	// Constructeur
	//
	
	public SoundSequence() {
		m_repeats = new Vector<RepeatItem>();
	}	
	
	
	public Object clone() {
		SoundSequence soundList = new SoundSequence();
		
		// Clonage des �l�ments de la liste
		for (SoundItem item : this)
			soundList.add((SoundItem) item.clone());
		
		// Clonage des r�p�titions
		soundList.m_repeats = new Vector<RepeatItem>();
		for (RepeatItem item : m_repeats)
			soundList.m_repeats.add((RepeatItem) item.clone());
		
		return soundList;
	}
	
	
	//
	// Gestion du contenu
	//
	
	/**
	 * Retourne l'item au temps sp�cifi� de n'importe quelle piste.
	 * Si plusieurs items correspondent, celui dont le d�but est le plus proche
	 * est retourn�.
	 */
	public SoundItem at(float time) {
		SoundItem applicant = null;
		
		// Parcours tous les items
		for (SoundItem item : this) {
			// Si le temps sp�cifi� est dans l'item en cours 
			if (item.m_startTime <= time && item.m_endTime >= time) {
				if (applicant != null) {
					applicant = item.m_startTime >= applicant.m_startTime ? item : applicant;
				}
				else {
					applicant = item;
				}
			}
		}
		return applicant;
	}
	
	/**
	 * Retourne l'item au temps pass� en param�tre de la piste sp�cifi�e.
	 * Si plusieurs items correspondent, celui dont le d�but est le plus proche
	 * est retourn�.
	 */
	public SoundItem at(int trackId, float time) {
		SoundItem applicant = null;
		
		// Parcours tous les items
		for (SoundItem item : this) {
			// Si le temps sp�cifi� est dans l'item en cours 
			if (item.m_trackId == trackId && item.m_startTime <= time && item.m_endTime >= time) {
				if (applicant != null) {
					applicant = item.m_startTime >= applicant.m_startTime ? item : applicant;
				}
				else {
					applicant = item;
				}
			}
		}
		return applicant;
	}
	
	/**
	 * Retourne l'item qui correspond � l'�l�ment sp�cifi�
	 */
	public SoundItem get(Element element) {
		for (SoundItem item : this)
			if (item.getElement() == element)
				return item;
		return null;
	}
	
	
	/**
	 * Retourne le temps maximal de la s�quence
	 */
	public float getLastTime() {
		float lastTime = 0;
		for (SoundItem item : this) {
			if (item.m_endTime > lastTime) {
				lastTime = item.m_endTime;
			}
		}
		return lastTime;
	}
		
	
	//
	// Gestion des r�p�tition
	//
	
	public void addRepeat(RepeatItem item) {
		m_repeats.add(item);
	}
	
	public void removeRepeat(RepeatItem item) {
		m_repeats.remove(item);
	}
	
	public void clearRepeats() {
		m_repeats.clear();
	}
	
	public Collection<RepeatItem> getRepeats() {
		return m_repeats;
	}
	
	
	/**
	 * Tri les �l�ments de la liste de sorte qu'ils apparaissent en ordre 
	 * croissant de leur temps d'apparition
	 */
	public void sort() {
		Collections.sort(this);
		Collections.sort(m_repeats);
	}
	
	
	/**
	 * Cr�er une SoundList compos�e des item de la liste plus une duplication
	 * des items qui se trouvent dans une r�p�tition et supprime les r�p�titions.
	 */
	public SoundSequence mergeRepeats() {
		SoundSequence soundList = (SoundSequence) this.clone();
		if (isEmpty())
			return soundList;
		// Tri les �l�ments de la liste au pr�alable
		soundList.sort();
		// 
		while (soundList.getRepeats().size() > 0) {
			// R�cup�re et retire la premi�re r�petition de la liste
			RepeatItem repeat = soundList.m_repeats.get(0);
			soundList.m_repeats.remove(0);
			// Recherche le premier item de la phrase
			Iterator<SoundItem> iterator = soundList.iterator();
			SoundItem soundItem = iterator.next();
			while (soundItem.getStartTime() < repeat.getPhraseStartTime() && iterator.hasNext()) {
				soundItem = iterator.next();
			}
			if (soundItem.getStartTime() >= repeat.getPhraseStartTime()) {
				int firstItemIndex = soundList.indexOf(soundItem);
				// Recherche du dernier item de la phrase
				int lastItemIndex = -1;
				try {
					while (soundItem.getStartTime() < repeat.getPhraseEndTime()) {
						soundItem = iterator.next();
					}
					lastItemIndex = soundList.indexOf(soundItem);
				}
				catch (NoSuchElementException e) {
					// Cas ou le dernier élément est la dernière note de la partition
					lastItemIndex = soundList.indexOf(soundItem) + 1;
				}
				// Pour chaque r�p�tition � effectuer
				SoundSequence phraseSounds = new SoundSequence();
				float phraseDuration = repeat.getPhraseEndTime() - repeat.getPhraseStartTime();
				for (int currentIteration = 1; currentIteration < repeat.getIterationsNumber(); currentIteration++) {
					// Cr�er une liste contenant un copie des items de la phrase
					for (SoundItem phraseItem : soundList.subList(firstItemIndex, lastItemIndex)) {
						SoundItem newSoundItem = (SoundItem) phraseItem.clone();
						newSoundItem.timeshift(phraseDuration * currentIteration);
						phraseSounds.add(newSoundItem);
					}
				}
				// D�cale tous les items qui se situent apr�s la phrase
				float shift = phraseDuration*(repeat.getIterationsNumber()-1);
				for (int i = lastItemIndex; i < soundList.size(); i++) {
					soundList.get(i).timeshift(shift);
				}
				// D�cale toutes les r�p�titions suivantes
				for (RepeatItem item : soundList.getRepeats()) {
					item.timeshift(shift);
				}
				// Ins�re les r�p�tition de la phrase apr�s la premier occurence de la phrase
				soundList.addAll(lastItemIndex, phraseSounds);
			}
		}
		soundList.clearRepeats();
		return soundList;
	}	
	
	
	/**
	 * Insertion d'un SoundItem en d�but de s�quence.
	 * L'item est ajout� au temps 0 et les items d�j� dans la liste sont 
	 * d�cal�s dans le temps en fonction de sa dur�e.
	 */
	public void insertFront(SoundItem item) {
		float delay = item.getDurationTime();
		item.m_startTime = 0;
		item.m_endTime = delay;
		
		// D�cale les �l�ments de la s�quence
		timeShift(delay);
		// Ajout de l'item au d�but
		add(0, item);
		
	}
	
	/**
	 * Insertion d'une liste de SoundItem en d�but de s�quence.
	 * Seule la dur�e de l'item est pris en compte, l'item est ajout� au temps 0
	 * et les items d�j� dans la liste sont d�cal�s dans le temps en fonction de
	 * sa dur�e.
	 */
	public void insertFront(SoundSequence sounds) {
		// D�cale les �l�ments de la s�quence
		timeShift(sounds.getLastTime());
		// Ajout de l'item au d�but
		addAll(0, sounds);
	}
	
	/**
	 * D�calage de tous les items de la s�quence de la dur�e en param�tre
	 */
	public void timeShift(float delay) {
		for (SoundItem i : this) {
			i.timeshift(delay);
		}
	}

	
	public void printStackTrace() {
		Iterator<SoundItem> it = iterator();
		System.out.println("Sound list (" + size() + " items) : " + this);
		int index = 0;
		while (it.hasNext()) {
			SoundItem item = it.next();
			System.out.println(
					index + ". " + item.getElement() + "\t" + 
					item.getStartTime() + " to " + item.getEndTime() + "\t" +
					"(#" + item.getSoundId() + ")" + "\t" +
					(item.isSilence() ? "SILENCE" : "\t")
			);
			index++;
		}
	}
	
	
	//
	// Attributs
	//
	
	private Vector<RepeatItem> m_repeats;
	
}

