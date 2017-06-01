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

package harmotab.desktop.components;

import java.awt.*;
import java.util.*;
import javax.swing.*;


/**
 * ComboBox de séléction de police de caractères
 */
public class FontFamilyChooser extends JComboBox {
	private static final long serialVersionUID = 1L;
	static public boolean USE_LIGHT_VERSION = false;

	static private final int FONT_SIZE = 12;
	static private final Font DEFAULT_FONT = new Font("Sans-serif", Font.PLAIN, FONT_SIZE);
	static private final String DEFAULT_FONT_FAMILY = DEFAULT_FONT.getFamily();
	static private final Dimension ITEM_SIZE = new Dimension(180, 30);
	static private final Dimension DEFAULT_SIZE = new Dimension(200, 30);
	
	static private final String[] m_fontsName = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	static private final ArrayList<FontItem> m_fontLabels = new ArrayList<FontItem>(0);

	
	public FontFamilyChooser() {
		this(DEFAULT_FONT_FAMILY);
	}
	
	public FontFamilyChooser(String fontFamilyName) {
		boolean fontFamilyExists = false;
		int defaultFontIndex = 0;

		setEditable(false);
		setRenderer(new ComboBoxRenderer());
		
		for (String fontName : m_fontsName) {
			FontItem item = new FontFamilyChooser.FontItem(fontName);
			m_fontLabels.add(item);
			addItem(item);
			if (fontName.equals(fontFamilyName)) {
				setSelectedItem(item);
				fontFamilyExists = true;
			}
			if (fontName.equals(DEFAULT_FONT_FAMILY))
				defaultFontIndex = getItemCount()-1;
		}
		
		if (!fontFamilyExists)
			setSelectedIndex(defaultFontIndex);

	}
	
	public FontFamilyChooser(String fontFamilyName, int x, int y) {
		this(fontFamilyName);
		setBounds(x, y, DEFAULT_SIZE.width, DEFAULT_SIZE.height);
	}
	
	public String getSelectedFontFamily() {
		int selected = getSelectedIndex();
		if (selected == -1)
			return DEFAULT_FONT_FAMILY;
		return m_fontsName[selected];
	}

	
	class FontItem {

		public FontItem(String fontFamily) {
			m_fontFamily = fontFamily;
			
			if (USE_LIGHT_VERSION) {
				m_font = DEFAULT_FONT;
			} else {
				m_font = new Font(fontFamily, Font.PLAIN, FONT_SIZE);
				if (!m_font.canDisplay(m_fontFamily.charAt(0)))
					m_font = DEFAULT_FONT;
			}
		}
		
		public String getFontFamily() {
			return m_fontFamily;
		}
		
		public Font getFont() {
			return m_font;
		}
		
		private Font m_font;
		private String m_fontFamily;

	}
	
	
	/**
	 * Renderer des éléments de la combo box.
	 * Label affichant le nom de la police avec sa propre police.
	 */
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;
	
		public Component getListCellRendererComponent(
				JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			int id = index;
			if (id == -1)
				id = list.getSelectedIndex();
			if (id == -1)
				id = 0;
			
			FontItem fontItem = m_fontLabels.get(id);
			setText(fontItem.getFontFamily());
			setFont(fontItem.getFont());
			setOpaque(true);
			setPreferredSize(ITEM_SIZE);

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(Color.WHITE);
				setForeground(list.getForeground());
			}
			
			return this;
		}
		
	}

}