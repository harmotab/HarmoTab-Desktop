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
 * Mod�le d'une note
 */
public class Note extends TrackElement {
	
	public static final String FIGURE_ATTR = "figure";
	public static final String REST_ATTR = "rest";
	public static final String HEIGHT_ATTR = "height";
	public static final String TIED_ATTR = "tied";
	public static final String NOTE_ATTR = "note";
	
	public static final boolean DEFAULT_REST = false;
	public static final boolean DEFAULT_TIED = false;
	
	
	//
	// Constructeurs
	//
		
	public Note() {
		super(Element.NOTE);
		setFigure(new Figure());
		setHeight(new Height());
		setRest(DEFAULT_REST);
		setTied(DEFAULT_TIED);
	}
	
	public Note(Height height) {
		super(Element.NOTE);
		setHeight(height);
		setFigure(new Figure());
		setRest(DEFAULT_REST);
		setTied(DEFAULT_TIED);
	}
	
	public Note(Figure figure) {
		super(Element.NOTE);
		setFigure(figure);
		setHeight(new Height());
		setRest(DEFAULT_REST);
		setTied(DEFAULT_TIED);
	}
	
	public Note(Height height, Figure figure) {
		super(Element.NOTE);
		setFigure(figure);
		setHeight(height);
		setRest(DEFAULT_REST);
		setTied(DEFAULT_TIED);
	}
	
	
	@Override
	public Object clone() {
		Note n = (Note) super.clone();
		
		Figure f = (Figure) m_figure.clone();
		n.m_figure = null;
		n.setFigure(f);
		
		Height h = (Height) m_height.clone();
		n.m_height = null;
		n.setHeight(h);
		
		return n;
	}
	

	@Override
	public RestoreCommand createRestoreCommand() {
		return new NoteRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//	
	
	/**
	 * Gestion de la figure
	 */
	
	public Figure getFigure() {
		return m_figure;
	}
		
	public void setFigure(byte figure) {
		setFigure(new Figure(figure));
	}
	
	public void setFigure(Figure figure) {
		removeAttributeChangesObserver(m_figure, FIGURE_ATTR);
		m_figure = figure;
		addAttributeChangesObserver(m_figure, FIGURE_ATTR);
		fireObjectChanged(FIGURE_ATTR);
	}


	/**
	 * Gestion de la hauteur
	 */
	
	public Height getHeight() {
		return m_height;
	}
	
	public void setHeight(Height height) {
		removeAttributeChangesObserver(m_height, HEIGHT_ATTR);
		m_height = height;
		addAttributeChangesObserver(m_height, HEIGHT_ATTR);
		fireObjectChanged(HEIGHT_ATTR);
	}
	
	
	/**
	 * Gestion de la propri�t� de silence
	 */

	public boolean isRest() {
		return m_isRest;
	}
	
	public void setRest(boolean isRest) {
		m_isRest = isRest;
		fireObjectChanged(REST_ATTR);
	}
	
	
	/**
	 * Gestion de la propri�t� de liaison
	 */
	
	public boolean isTied() {
		return m_isTied;
	}
	
	public void setTied(boolean isTied) {
		m_isTied = isTied;
		fireObjectChanged(TIED_ATTR);
	}
	
	
	//
	// Méthodes utilitaires
	//	
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_NOTE);
	}
	
	
	@Override
	public float getDuration() {
		return m_figure.getDuration();
	}
	
	
	@Override
	public float getWidthUnit() {
		return m_figure.getWidth();
	}
	
	
	/**
	 * Indique si la note est accrochable (croche ou double croche)
	 */
	public boolean isHookable() {
		return m_figure.isHookable() && (!m_isRest || m_figure.isTriplet());
	}
	
	
	//
	// Serialisation / déserialisation xml
	//
	
	@Override
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);	
		object.setElementAttribute(FIGURE_ATTR, m_figure);
		object.setElementAttribute(HEIGHT_ATTR, m_height);
		if (isRest() != DEFAULT_REST)
			object.setAttribute(REST_ATTR, isRest()+"");
		if (m_figure.isDotted() != Figure.DEFAULT_DOTTED)
			object.setAttribute(Figure.DOTTED_ATTR, m_figure.isDotted()+"");
		if (isTied() != DEFAULT_TIED)
			object.setAttribute(TIED_ATTR, isTied()+"");
		return object;
	}
	
	@Override
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setFigure(object.hasAttribute(FIGURE_ATTR) ?
			(Figure) object.getElementAttribute(FIGURE_ATTR) :
			new Figure());
		setHeight(object.hasAttribute(HEIGHT_ATTR) ?
			(Height) object.getElementAttribute(HEIGHT_ATTR) :
			new Height());
		setRest(object.hasAttribute(REST_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(REST_ATTR)) :
			DEFAULT_REST);
		m_figure.setDotted(object.hasAttribute(Figure.DOTTED_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(Figure.DOTTED_ATTR)) :
			Figure.DEFAULT_DOTTED);
		setTied(object.hasAttribute(TIED_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(TIED_ATTR)) :
			DEFAULT_TIED);
	}
	
	
	//
	// Attributs
	//
		
	protected Figure m_figure = null;
	protected Height m_height = null;
	protected boolean m_isRest = DEFAULT_REST;
	protected boolean m_isTied = DEFAULT_TIED;
	
}


/**
 * Commande d'annulation des modifications d'une note
 */
class NoteRestoreCommand extends Note implements RestoreCommand {
	
	public NoteRestoreCommand(Note saved) {
		m_saved = saved;
		m_figure = saved.m_figure;
		m_height = saved.m_height;
		m_isRest = saved.m_isRest;
		m_isTied = saved.m_isTied;
	}
	
	@Override
	public void execute() {
		if (m_saved.m_figure != m_figure)
			m_saved.setFigure(m_figure);
		if (m_saved.m_height != m_height)
			m_saved.setHeight(m_height);
		if (m_saved.m_isRest != m_isRest)
			m_saved.setRest(m_isRest);
		if (m_saved.m_isTied != m_isTied)
			m_saved.setTied(m_isTied);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new NoteRestoreCommand(m_saved);
	}
	
	private Note m_saved;
}
