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

import harmotab.core.undo.RestoreCommand;
import harmotab.io.ObjectSerializer;
import harmotab.io.SerializedObject;
import harmotab.throwables.UnhandledCaseError;
import java.awt.Font;


/**
 * Mod�le d'un �l�ment de texte de la partition 
 */
public class TextElement extends Element {
	
	public static final String TEXT_ATTR = "text";
	public static final String FONT_ATTR = "font";
	public static final String ALIGNMENT_ATTR = "alignment";
	
	public static final String FONT_FAMILY_ATTR = "fontFamily";
	public static final String FONT_SIZE_ATTR = "fontSize";
	public static final String FONT_STYLE_ATTR = "fontStyle";
	
	public final static String LEFT = "left";
	public final static String CENTER = "center";
	public final static String RIGHT = "right";
	
	public final static String DEFAULT_ALIGNMENT = LEFT;
		
	
	//
	// Constructeurs
	//
	
	public TextElement() {
		super(Element.TEXT_ELEMENT);
		setText(null);
		setFont(null);
		setAlignment(DEFAULT_ALIGNMENT);
	}
	
	public TextElement(String text) {
		super(Element.TEXT_ELEMENT);
		setText(text);
		setFont(null);
		setAlignment(DEFAULT_ALIGNMENT);
	}
	
	public TextElement(String text, Font font, String alignment) {
		super(Element.TEXT_ELEMENT);
		setText(text);
		setFont(font);
		setAlignment(alignment);
	}
	
	
	@Override
	public Object clone() {
		TextElement text = (TextElement) super.clone();
		return text;
	}
	
	
	@Override
	public RestoreCommand createRestoreCommand() {
		return new TextElementRestoreCommand(this);
	}
	
	
	//
	// Getters / setters
	//
	
	public void setText(String text) {
		m_text = text;
		fireObjectChanged(TEXT_ATTR);
	}
	
	public String getText() {
		return m_text;
	}
	
	
	public void setFont(Font font) {
		if (font == null)
			m_font = new Font("Sans-serif", Font.PLAIN, 12);
		else
			m_font = font;
		
		fireObjectChanged(FONT_ATTR);
	}
	
	public Font getFont() {
		return m_font;
	}
	
	
	public void setAlignment(String alignment) {
		if( alignment.equals(LEFT) || 
			alignment.equals(CENTER) ||
			alignment.equals(RIGHT) ) {
				m_alignment = alignment;
		}
		else
			throw new UnhandledCaseError("Invalid alignment !");
		
		fireObjectChanged(ALIGNMENT_ATTR);
	}
	
	public String getAlignment() {
		return m_alignment;
	}	
	
	
	//
	// S�rialisation / d�serialisation
	//
	
	public SerializedObject serialize(ObjectSerializer serializer) {
		SerializedObject object = super.serialize(serializer);
		object.setAttribute(TEXT_ATTR, getText());
		if (getFont() != null) {
			object.setAttribute(FONT_ATTR, "");
			object.setAttribute(FONT_FAMILY_ATTR, getFont().getFamily());
			object.setAttribute(FONT_SIZE_ATTR, getFont().getSize()+"");
			object.setAttribute(FONT_STYLE_ATTR, getFont().getStyle()+"");
		}
		if (getAlignment() != DEFAULT_ALIGNMENT)
			object.setAttribute(ALIGNMENT_ATTR, getAlignment());
		return object;
	}
	
	public void deserialize(ObjectSerializer serializer, SerializedObject object) {
		setText(object.getAttribute(TEXT_ATTR));
		if (object.hasAttribute(FONT_ATTR)) {
			Font font = new Font(
				object.getAttribute(FONT_ATTR),
				Integer.parseInt(object.getAttribute(FONT_STYLE_ATTR)),
				Integer.parseInt(object.getAttribute(FONT_SIZE_ATTR))
				);
			setFont(font);
		}
		if (object.hasAttribute(ALIGNMENT_ATTR))
			setAlignment(object.getAttribute(ALIGNMENT_ATTR));
	}
	
	
	
	//
	// Attributs
	//
	
	protected String m_text = null;
	protected Font m_font = null;
	protected String m_alignment = LEFT;

}


/**
 * Commande d'annulation des modifications d'un �l�ment de texte de la partition
 */
class TextElementRestoreCommand extends TextElement implements RestoreCommand {
	
	public TextElementRestoreCommand(TextElement saved) {
		m_saved = saved;
		m_text = m_saved.m_text;
		m_font = m_saved.m_font;
		m_alignment = m_saved.m_alignment;
	}
	
	@Override
	public void execute() {
		if (m_text != m_saved.m_text)
			m_saved.setText(m_text);
		if (m_font != m_saved.m_font)
			m_saved.setFont(m_font);
		if (m_alignment != m_saved.m_alignment)
			m_saved.setAlignment(m_alignment);
	}
	
	@Override
	public RestoreCommand getInvertCommand() {
		return new TextElementRestoreCommand(m_saved);
	}
	
	private TextElement m_saved;
}

