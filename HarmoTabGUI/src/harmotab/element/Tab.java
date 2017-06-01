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
import harmotab.harmonica.HarmonicaModel;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.*;


/**
 * Mod�le d'une tablature
 */
public class Tab extends TrackElement {
	
	public static final String HOLE_ATTR = "hole";
	public static final String DIRECTION_ATTR = "direction";
	public static final String BEND_ATTR = "bend";
	public static final String EFFECT_ATTR = "effect";
	public static final String PUSHED_ATTR = "pushed";
	
	public static final byte UNDEFINED = 0;
	public static final String UNDEFINED_STR = "";

	public static final byte BLOW = 1;
	public static final byte DRAW = 2;
	
	public static final String BLOW_STR = "blow";
	public static final String DRAW_STR = "draw";
	
	public static final byte NONE = 0;
	public static final byte HALF_BEND = 1;
	public static final byte FULL_BEND = 2;
	
	public static final String NONE_STR = "none";
	public static final String HALF_BEND_STR = "half";
	public static final String FULL_BEND_STR = "full";
	
	private static final byte DEFAULT_DIRECTION = UNDEFINED;
	private static final byte DEFAULT_BEND = NONE;
	private static final int DEFAULT_HOLE = UNDEFINED;
	private static final boolean DEFAULT_PUSHED = false;
	
	
	private final static String FULL_OVERBLOW_STRING = "full overblow";
	private final static String HALF_OVERBLOW_STRING = "half overblow";
	private final static String BLOW_STRING = "blow";
	private final static String DRAW_STRING = "draw";
	private final static String HALF_BEND_STRING = "half bend";
	private final static String FULL_BEND_STRING = "full bend";

	
	
	//
	// Constructeurs
	//
	
	public Tab(int hole, byte direction, byte bend, boolean pushed) {
		super(Element.TAB);
		setHole(hole);
		setDirection(direction);
		setBend(bend);
		setEffect(new Effect());
		setPushed(pushed);
	}
	
	public Tab(int hole, byte direction, byte bend) {
		this(hole, direction, bend, DEFAULT_PUSHED);
	}
	
	public Tab() {
		this(DEFAULT_HOLE, DEFAULT_DIRECTION, DEFAULT_BEND);
	}
	
	public Tab(int hole) {
		this(hole, DEFAULT_DIRECTION, DEFAULT_BEND);		
	}
	
	public Tab(byte direction) {
		this(DEFAULT_HOLE, direction, DEFAULT_BEND);	
	}
	
	public Tab(Tab tab) {
		super(Element.TAB);
		set(tab);
	}
	
	
	public void set(Tab tab) {
		setHole(tab.getHole());
		setDirection(tab.getDirection());
		setBend(tab.getBend());	
		setEffect((Effect) tab.getEffect().clone());
		setPushed(tab.isPushed());
	}

	
	@Override
	public Object clone() {
		Tab tab = (Tab) super.clone();
		tab.setEffect((Effect) getEffect().clone());
		return tab;
	}
	
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (!(object instanceof Tab)) {
			return false;
		}
		Tab tab = (Tab) object;
		return 	(m_hole == tab.m_hole) && 
				(m_direction == tab.m_direction) &&
				(m_bend == tab.m_bend) &&
				(m_effect.getType() == tab.getEffect().getType()) &&
				m_pushed == tab.m_pushed;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new TabRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public int getHole() {
		return m_hole;
	}
	
	public void setHole(int hole) throws OutOfBoundsError {
		if ((hole < HarmonicaModel.MIN_HOLE_VALUE || hole > HarmonicaModel.MAX_HOLE_VALUE) && hole != UNDEFINED)
			throw new OutOfBoundsError("Invalid hole number (" + hole + ") !");
		
		m_hole = hole;
		fireObjectChanged(HOLE_ATTR);
	}
	
	
	public byte getDirection() {
		return m_direction;
	}
	
	public String getDirectionStr() {
		switch (m_direction) {
			case DRAW:		return DRAW_STR;
			case BLOW:		return BLOW_STR;
			case UNDEFINED:	return UNDEFINED_STR;
			default:		throw new UnhandledCaseError("Unhandled direction '#" + m_direction + "'");
		}
	}
	
	public void setDirection(byte direction) {
		if (direction != BLOW && direction != DRAW && direction != UNDEFINED)
			throw new OutOfSpecificationError("Invalid direction '#" + direction + "'");
		
		m_direction = direction;
		fireObjectChanged(DIRECTION_ATTR);
	}
	
	public void setDirection(String direction) {
		if (direction == null)
			throw new NullPointerException();
		if (direction.equals(DRAW_STR))
			setDirection(DRAW);
		else if (direction.equals(BLOW_STR))
			setDirection(BLOW);
		else if (direction.equals(UNDEFINED_STR))
			setDirection(UNDEFINED);
		else
			throw new IllegalArgumentException("Unknown direction '" + direction + "'");
	}
	
	
	public byte getBend() {
		return m_bend;
	}
	
	public String getBendStr() {
		switch (m_bend) {
			case NONE:			return NONE_STR;
			case HALF_BEND:		return HALF_BEND_STR;
			case FULL_BEND:		return FULL_BEND_STR;
			default:
				throw new UnhandledCaseError("Unhandled ben '#" + m_bend + "'");
		}
	}
	
	public boolean isBended() {
		return m_bend != NONE;
	}
	
	public void setBend(byte bend) throws OutOfSpecificationError {
		if (bend != NONE && bend != HALF_BEND && bend != FULL_BEND)
			throw new OutOfSpecificationError("Invalid bend !");
		
		m_bend = bend;
		fireObjectChanged(BEND_ATTR);
	}
	
	public void setBend(String bend) {
		if (bend == null)
			throw new NullPointerException();
		if (bend.equals(NONE_STR))
			setBend(NONE);
		else if (bend.equals(HALF_BEND_STR))
			setBend(HALF_BEND);
		else if (bend.equals(FULL_BEND_STR))
			setBend(FULL_BEND);
		else
			throw new IllegalArgumentException("Invalid bend '" + bend + "'");
	}
	
	
	public Effect getEffect() {
		return m_effect;
	}
	
	public void setEffect(Effect effect) {
		removeAttributeChangesObserver(m_effect, EFFECT_ATTR);
		m_effect = effect;
		addAttributeChangesObserver(m_effect, EFFECT_ATTR);
		fireObjectChanged(EFFECT_ATTR);
	}
	
	
	public boolean isPushed() {
		return m_pushed;
	}
	
	public void setPushed(boolean pushed) {
		m_pushed = pushed;
		fireObjectChanged(PUSHED_ATTR);
	}
	
	
	//
	// Méthodes utilitaires
	//
	
	public void print() {
		System.out.println(
				getTrackElementLocalizedName() + " " + 
				getHole() + " " +
				getDirectionStr() + " " +
				getBend() + " " +
				getEffect().getEffectTypeStr() + " " +
				isPushed()
				);
	}
	
	
	@Override
	public String getTrackElementLocalizedName() {
		return Localizer.get(i18n.N_TAB);
	}
	
	public void toggleDirection() {
		switch (getDirection()) {
			case UNDEFINED:	setDirection(BLOW);			break;
			case BLOW:		setDirection(DRAW);			break;
			case DRAW:		setDirection(UNDEFINED);	break;
		}
	}
	
	public String getBreathName() {
		switch (getDirection()) {
			case BLOW:
				switch (getBend()) {
					case NONE:			return BLOW_STRING;
					case HALF_BEND:		return HALF_OVERBLOW_STRING;
					case FULL_BEND:		return FULL_OVERBLOW_STRING;
				}
				break;
				
			case DRAW:
				switch (getBend()) {
					case NONE:			return DRAW_STRING;
					case HALF_BEND:		return HALF_BEND_STRING;
					case FULL_BEND:		return FULL_BEND_STRING;
				}
				break;
		}
		throw new IllegalArgumentException();
	}
	
	public void setBreath(String breath) {
		if (breath.equals(FULL_OVERBLOW_STRING)) {
			setDirection(BLOW);
			setBend(FULL_BEND);
		}
		else if (breath.equals(HALF_OVERBLOW_STRING)) {
			setDirection(BLOW);
			setBend(HALF_BEND);
		}
		else if (breath.equals(BLOW_STRING)) {
			setDirection(BLOW);
			setBend(NONE);
		}
		else if (breath.equals(DRAW_STRING)) {
			setDirection(DRAW);
			setBend(NONE);
		}
		else if (breath.equals(HALF_BEND_STRING)) {
			setDirection(DRAW);
			setBend(HALF_BEND);
		}
		else if (breath.equals(FULL_BEND_STRING)) {
			setDirection(DRAW);
			setBend(FULL_BEND);
		}
		else {
			throw new IllegalArgumentException("Invalid breath '" + breath + "'");
		}
	}
	
	
	public boolean isEmpty() {
		return 	m_hole == UNDEFINED &&
				m_direction == UNDEFINED &&
				m_bend == NONE;
	}
	
	public boolean isDefined() {
		return 	m_hole != UNDEFINED &&
				m_direction != UNDEFINED;
	}
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(HOLE_ATTR, getHole()+"");
		object.setAttribute(DIRECTION_ATTR, getDirectionStr());
		if (getBend() != NONE)
			object.setAttribute(BEND_ATTR, getBendStr());
		object.setAttribute(EFFECT_ATTR, m_effect.getEffectTypeStr());
		if (isPushed() != DEFAULT_PUSHED)
			object.setAttribute(PUSHED_ATTR, String.valueOf(isPushed()));
		return object;
	}
	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setHole(Integer.decode(object.getAttribute(HOLE_ATTR)));
		setDirection(object.getAttribute(DIRECTION_ATTR));
		if (object.hasAttribute(BEND_ATTR))
			setBend(object.getAttribute(BEND_ATTR));
		else
			setBend(DEFAULT_BEND);
		setEffect(new Effect(object.getAttribute(EFFECT_ATTR)));
		setPushed(
			object.hasAttribute(PUSHED_ATTR) ?
			Boolean.parseBoolean(object.getAttribute(PUSHED_ATTR)) :
			DEFAULT_PUSHED);
	}
	

	//
	// Attributs
	//
	
	protected int m_hole;
	protected byte m_direction;
	protected byte m_bend;
	protected boolean m_pushed;
	protected Effect m_effect = null;
	
}


/**
 * Commande d'annulation des modifications d'une tablature
 */
class TabRestoreCommand extends Tab implements RestoreCommand {
	
	public TabRestoreCommand(Tab saved) {
		m_saved = saved;
		m_hole = m_saved.m_hole;
		m_direction = m_saved.m_direction;
		m_bend = m_saved.m_bend;
		m_pushed = m_saved.m_pushed;
		m_effect = m_saved.m_effect;
	}
	
	@Override
	public void execute() {
		if (m_hole != m_saved.m_hole)
			m_saved.setHole(m_hole);
		if (m_direction != m_saved.m_direction)
			m_saved.setDirection(m_direction);
		if (m_bend != m_saved.m_bend)
			m_saved.setBend(m_bend);
		if (m_pushed != m_saved.m_pushed)
			m_saved.setPushed(m_pushed);
		if (m_effect != m_saved.m_effect)
			m_saved.setEffect(m_effect);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new TabRestoreCommand(m_saved);
	}
	
	private Tab m_saved;
}

