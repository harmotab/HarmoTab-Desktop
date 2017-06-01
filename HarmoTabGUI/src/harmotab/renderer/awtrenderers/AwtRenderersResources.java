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

package harmotab.renderer.awtrenderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import res.ResourceLoader;


public class AwtRenderersResources {
	
	public final static int BREATH_WIDTH = 15;
	public final static int BREATH_HEIGHT = 20;
	public final static int BEND_WIDTH = 20;
	
	
	/**
	 * Chargement des images
	 */
	static {
		ResourceLoader loader = ResourceLoader.getInstance();
		m_keyCImage 		= loader.loadImage("/res/elements/key-c.png");
		m_sharpImage 		= loader.loadImage("/res/elements/sharp.png");
		m_flatImage 		= loader.loadImage("/res/elements/flat.png");
		m_naturalImage		= loader.loadImage("/res/elements/natural.png");
		m_digitsImage		= loader.loadImage("/res/elements/digits.png");
		m_notesImage		= loader.loadImage("/res/elements/notes.png");
		m_breathImage 		= loader.loadImage("/res/elements/breath.png");
		m_emptyAreaImage	= loader.loadImage("/res/elements/empty-area.png");
		m_warningImage		= loader.loadImage("/res/elements/warning.png");
		m_tempoImage		= loader.loadImage("/res/controllers/quarter.png");
	}

	//
	// Attributs
	//
		
	static public Image m_keyCImage;
	static public Image m_sharpImage;
	static public Image m_flatImage;
	static public Image m_naturalImage;
	static public Image m_digitsImage;
	static public Image m_notesImage;
	static public Image m_breathImage;
	static public Image m_emptyAreaImage;
	static public Image m_tempoImage;
	static public Image m_warningImage;
	
	static public Font m_defaultFont = new Font("Sans-serif", Font.PLAIN, 12);
	static public Color m_defaultForeground = Color.BLACK;
	
	static public Font m_barNumberFont = new Font("Sans-serif", Font.PLAIN, 10);
	static public Color m_barNumberColor = Color.GRAY;
	
	static public Font m_lyricsFont = new Font("Sans-serif", Font.ITALIC, 11);
	static public Color m_lyricsForeground = Color.BLACK;
	
	static public Font m_harmonicaNameFont = new Font("Sans-serif", Font.PLAIN, 10);
	static public Font m_harmonicaKeyFont = new Font("Sans-serif", Font.BOLD, 15);
	static public Color m_harmonicaForeground = Color.BLACK;
	
}
