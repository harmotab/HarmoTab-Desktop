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

package harmotab.element;

import harmotab.core.*;
import harmotab.core.undo.RestoreCommand;
import harmotab.io.*;


/**
 * Mod�le de l'�l�ment d'une mesure compos� d'une note et de la tablature 
 * correspondante
 */
public class HarmoTabElement extends Note {
	
	public static final String TAB_ATTR = "tab";
	
	
	//
	// Constructeurs
	//

	public HarmoTabElement() {
		m_type = Element.HARMOTAB;
		setTab(new Tab());
	}
	
	public HarmoTabElement(Figure figure) {
		super(figure);
		m_type = Element.HARMOTAB;
		setTab(new Tab());
	}
	
	public HarmoTabElement(Height height, Figure figure, Tab tab) {
		super(height, figure);
		m_type = Element.HARMOTAB;
		setTab(tab);
	}
	
	public HarmoTabElement(Height height, Figure figure) {
		super(height, figure);
		m_type = Element.HARMOTAB;
		setTab(new Tab());
	}

	
	@Override
	public Object clone() {
		HarmoTabElement e = (HarmoTabElement) super.clone();
		
		Tab t = (Tab) m_tab.clone();
		e.setTab(t);
		
		return e;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new HarmoTabElementRestoreCommand(this, super.createRestoreCommand());
	}

	
	//
	// Getters / setters
	//
	
	public Tab getTab() {
		return m_tab;
	}
	
	public void setTab(Tab tab) {
		if (tab == null)
			throw new NullPointerException();
		
		removeAttributeChangesObserver(m_tab, TAB_ATTR);
		m_tab = tab;
		addAttributeChangesObserver(m_tab, TAB_ATTR);
		fireObjectChanged(TAB_ATTR);
	}
	
	
	//
	// Surcharges de m�thodes de la classe Note
	//
	
	@Override
	public void setRest(boolean isRest) {
		if (isRest == true)
			m_tab = new Tab();
		super.setRest(isRest);
	}
	
	@Override
	public void setTied(boolean tied) {
		if (tied == true)
			m_tab = new Tab();
		super.setTied(tied);
	}
	
	
	// 
	// M�thodes utilitaires
	// 
	
	public boolean canHaveTab() {
		return !isRest() && !isTied();
	}
	
	
	//
	// Gestion des sous-�l�ment
	//
	
	@Override
	public boolean contains(Element subElement) {
		return subElement == m_tab;
	}
	
	@Override
	public boolean delete(Element subElement) {
		if (subElement == m_tab)
			setTab(new Tab());
		else
			return false;
		return true;
	}
	
	
	//
	// Serialisation / d�serialisation
	//
	
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		if (getTab() != null)
			object.setElementAttribute(TAB_ATTR, getTab());
		return object;
	}
	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		super.deserialize(serializer, object);
		setTab(object.hasAttribute(TAB_ATTR) ?
			(Tab) object.getElementAttribute(TAB_ATTR) :
			/*null*/new Tab());
	}
	

	//
	// Attributs
	//
	
	protected Tab m_tab = null;
				
}


/**
 * Commande d'annulation des modifications d'un HarmoTabElement
 */
class HarmoTabElementRestoreCommand extends HarmoTabElement implements RestoreCommand {
	
	public HarmoTabElementRestoreCommand(HarmoTabElement saved, RestoreCommand superRestoreCommand) {
		m_saved = saved;
		m_superRestoreCommand = superRestoreCommand;
		m_tab = m_saved.m_tab;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_tab != m_tab)
			m_saved.setTab(m_tab);
		m_superRestoreCommand.execute();
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new HarmoTabElementRestoreCommand(m_saved, m_superRestoreCommand.getInvertCommand());
	}
	
	private HarmoTabElement m_saved;
	private RestoreCommand m_superRestoreCommand = null;
}


