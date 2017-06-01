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

package harmotab.renderer;

import java.awt.*;
import java.util.Iterator;

import harmotab.core.*;
import harmotab.desktop.components.TabStyleChooser;
import harmotab.element.*;
import harmotab.renderer.awtrenderers.*;
import harmotab.renderer.renderingelements.*;



/**
 * ElementRenderer pour la g�n�ration d'images de partitions pour �diteur.
 * Affiche des informations utiles � l'�dition.
 */
public class AwtEditorElementRendererBundle extends ElementRendererBundle {	
	
	//
	// Construction
	//
	
	public AwtEditorElementRendererBundle() {
		setMode(RenderingMode.EDIT_MODE);
		initRenderers();
	}
	
	@Override
	public void reset() {
		initRenderers();
	}
	
	public void initRenderers() {
		// Note
		m_noteRenderer = new AwtNoteRenderer();
		// HarmoTabElement
		m_harmoTabElementRenderer = new AwtHarmoTabElementRenderer();
		// Tab
		m_tabRenderer = TabStyleChooser.getRenderer(GlobalPreferences.getTabStyle());
		if (m_tabRenderer == null) {
			m_tabRenderer = new AwtArrowTabRenderer();
		}
		// Bar
		if (m_tabRenderer instanceof AwtTabularTabRenderer) {
			m_barRenderer = new AwtTabularBarRenderer();
		} 
		else {
			m_barRenderer = new AwtArrowBarRenderer();
		}
		// Tempo
		m_tempoRenderer = new AwtTempoRenderer();
		// KeySignature)
		m_keySignatureRenderer = new AwtKeySignatureRenderer();
		// TabArea
		if (m_tabRenderer instanceof AwtTabularTabRenderer) {
			m_tabAreaRenderer = new AwtTabularTabAreaRenderer();
		}
		else {
			m_tabAreaRenderer = new AwtArrowTabAreaRenderer();
		}
	}
	
	
	// 
	// M�thodes d'affichage des �l�ments
	// 
	
	/**
	 * Affichage d'un élément
	 */
	@Override
	public void paintElement(Graphics2D g, LocationItem item) {
		Element element = item.getElement();		
		if (element == null)
			return ;
		
		// Affecte la couleur et la police par d�faut
		g.setColor(AwtRenderersResources.m_defaultForeground);
		g.setFont(AwtRenderersResources.m_defaultFont);
		
		// Affichage d'un symbole en cas d'erreur signal�e sur un �l�ment
		if (item.getFlag(LocationItemFlag.ERRORNOUS_ITEM)) {
			paintWarning(g, item);
			g.setColor(Color.RED);
		}
		
		// Dessin de l'�l�ment
		switch (element.getType()) {
		
			case Element.EMPTY_AREA:			paintEmptyArea(g, (EmptyArea) element, item);						break;
			case Element.TEXT_ELEMENT:			paintTextElement(g, (TextElement) element, item);					break;
			case Element.STAFF:					paintStaff(g, (Staff) element, item);								break;
			
			case Element.BAR:
				if (m_barRenderer != null)
					m_barRenderer.paint(g, element, item);
				drawEditingSurroundingHelper(g, item);
				break;
				
			case Element.KEY:					paintKey(g, (Key) element, item);									break;
			
			case Element.KEY_SIGNATURE:
				if (m_keySignatureRenderer != null)
					m_keySignatureRenderer.paint(g, element, item);
				drawEditingSurroundingHelper(g, item);
				break;
				
			case Element.TIME_SIGNATURE:		paintTimeSignature(g, (TimeSignature) element, item);				break;
			
			case Element.NOTE:
				if (m_noteRenderer != null)
					m_noteRenderer.paint(g, element, item);
				drawEditingSurroundingHelper(g, item);
				break;
				
			case Element.HARMOTAB:
				if (m_harmoTabElementRenderer != null)
					m_harmoTabElementRenderer.paint(g, element, item);
				drawEditingSurroundingHelper(g, item);
				break;
				
			case Element.TAB:
				if (m_tabRenderer != null)
					m_tabRenderer.paint(g, element, item);
				drawEditingSurroundingHelper(g, item);
				break;
				
			case Element.HOOCKED_NOTES:			paintHoockedNoteGroup(g, (HoockedNoteGroup) element, item);		break;
			case Element.CHORD:					paintChord(g, (Chord) element, item);								break;
			case Element.ACCOMPANIMENT:			paintAccompaniment(g, (Accompaniment) element, item);				break;
			
			case Element.TEMPO:
				if (m_tempoRenderer != null)
					m_tempoRenderer.paint(g, element, item);
				drawEditingSurroundingHelper(g, item);
				break;
				
			case Element.TIED_NOTES:			paintTiedNoteGroup(g, (TiedNoteGroup) element, item);				break;
			case Element.TRIPLET_GROUP:			paintTripletGroup(g, (TripletGroup) element, item);				break;
			case Element.SILENCE:				paintSilence(g, (Silence) element, item);							break;
			case Element.LYRICS:				paintLyrics(g, (Lyrics) element, item);							break;
			case Element.HARMONICA_PROPERTIES:	paintHarmonicaProperties(g, (HarmonicaProperties) element, item);	break;
			
			case Element.TAB_AREA:
				if (m_tabAreaRenderer != null) 
					m_tabAreaRenderer.paint(g, element, item);
				break;
				
			default:	System.err.println("AwtElementRenderer::render: No paint method for element " + element.getType());
		}
		
		// Dessins de debug
		if (item.m_flag >= 1 << LocationItemFlag.DEBUG_MARK) {
			if (item.getFlag(LocationItemFlag.GREEN_MARK))
				g.setColor(Color.GREEN);
			if (item.getFlag(LocationItemFlag.RED_MARK))
				g.setColor(Color.RED);
			g.drawLine(item.m_x1, item.m_y1, item.m_x2, item.m_y2);
			g.drawLine(item.m_x1, item.m_y2, item.m_x2, item.m_y1);
			g.setColor(Color.BLACK);
		}

	}
	
	
	/**
	 * Signalisation des �l�ments signal�s erron�s
	 */
	protected void paintWarning(Graphics2D g, LocationItem item) {
		if (getDrawEditingWarnings()) {
			g.drawImage(AwtRenderersResources.m_warningImage, item.getX1() + 2, item.getY1() + 2, null);
		}
	}
	
	
	/**
	 * Affichage d'un EmptyArea
	 */
	protected void paintEmptyArea(Graphics2D g, EmptyArea emptyArea, LocationItem item) {
		if (getDrawEditingWarnings()) {
			int square = 16;
			int x = item.getX1() + 10;
			int y = item.getY1() + (item.getHeight() - square) / 2;
			g.drawImage(AwtRenderersResources.m_emptyAreaImage, x, y, null);
		}
	}
	

	/**
	 * Affichage d'un texte
	 */
	protected void paintTextElement(Graphics2D g, TextElement e, LocationItem item) {
		Font oldFont = g.getFont();
		g.setFont(e.getFont());
		
		String text = e.getText();
		int x = item.getX1();
		int y = item.getY1() + g.getFontMetrics().getHeight();
		int width = g.getFontMetrics().stringWidth(text);
		
		String align = e.getAlignment();
		if (align.equals(TextElement.LEFT))
			;
		else if (align.equals(TextElement.RIGHT))
			x = item.getX2() - width;	
		else if (align.equals(TextElement.CENTER))
			x += item.getWidth()/2 - width/2;
		else
			System.err.println("ElementRenderer::paintElement(TextElement): Alignment not handled (#" + e.getAlignment() + ") !");
		
		g.drawString(text, x, y);
		g.setFont(oldFont);
		
		drawEditingSurroundingHelper(g, item);
	}
	
	
	/**
	 * Affichage d'une portée vierge
	 */
	protected void paintStaff(Graphics2D g, Staff staff, LocationItem item) {
		int x1 = item.getX1();
		int x2 = (getDrawEditingHelpers() ? item.getX2() : item.getPointOfInterestX());
		int y = item.getPointOfInterestY();
		int spacing = item.getExtra();
		g.drawLine(x1, y, x1, y + spacing * 8);
		for (int i = 0; i < 5; i++) {
			g.drawLine(x1, y, x2, y);
			y += spacing*2;
		}
	}
	
	
	/**
	 * Affichage des informations sur l'harmonica utilis�
	 */
	protected void paintHarmonicaProperties(Graphics2D g, HarmonicaProperties harmoProps, LocationItem item) {
		int x = item.getX1();
		int y = item.getY1();
		g.setFont(AwtRenderersResources.m_harmonicaNameFont);
		g.setColor(AwtRenderersResources.m_harmonicaForeground);
		g.drawString(harmoProps.getHarmonica().getName(), x, y + 45);
		g.setFont(AwtRenderersResources.m_harmonicaKeyFont);
		g.drawString(harmoProps.getHarmonica().getTunning().getNoteName(), x, y + 20);
		drawEditingSurroundingHelper(g, item);
	}
	
	
	/**
	 * Affichage d'une clé
	 */
	protected void paintKey(Graphics2D g, Key key, LocationItem l) {
		int x = l.getPointOfInterestX() - 10;
		int y = l.getPointOfInterestY() - 30;
		
		switch (key.getValue()) {
			case Key.G2:
				g.drawImage(AwtRenderersResources.m_keyCImage, x, y, null);
				break;
			default:
				System.err.println("ElementRenderer::paintElement(Key): Key not handled (#" + key.getValue() + ") !");
		}
	}
	
	
	/**
	 * Affichage d'un signature temporelle
	 */
	protected void paintTimeSignature(Graphics2D g, TimeSignature ts, LocationItem item) {
		final int DIGIT_WIDTH = 19;
		final int DIGIT_HEIGHT = 15;
		
		int x = item.getPointOfInterestX() - 15;
		int y = item.getPointOfInterestY() - 15;
		int number = ts.getNumber();
		int reference = ts.getReference();
		
		g.drawImage(AwtRenderersResources.m_digitsImage, x, y, x + DIGIT_WIDTH, y + DIGIT_HEIGHT,
				(number-1) * DIGIT_WIDTH, 0, number * DIGIT_WIDTH, DIGIT_HEIGHT,
				null);
		
		y += DIGIT_HEIGHT;
		g.drawImage(AwtRenderersResources.m_digitsImage, x, y, x + DIGIT_WIDTH, y + DIGIT_HEIGHT,
				(reference-1) * DIGIT_WIDTH, 0, reference * DIGIT_WIDTH, DIGIT_HEIGHT,
				null);

		drawEditingSurroundingHelper(g, item);
	}
	
	
	/**
	 * Affichage d'un groupement de croches
	 */
	private void paintHoockedNoteGroup(Graphics2D g, HoockedNoteGroup hoocked, LocationItem l) {
		final int LINE_PART_WIDTH = 7;
		final int SECOND_LINE_OFFSET = 6;
		
		int notesCount = hoocked.size();
		Iterator<LocationItem> i;

		// Si une seule croche, affiche sa queue
		if( notesCount < 2 ) {
			hoocked.get(0).setFlag(LocationItemFlag.STAND_ALONE, true);
		}
		// Plusieurs croches, affiche une barre
		else {
			// Calcul la droite entre la première et la dernière note du groupe
			GroupLine line = hoocked.getGroupLine();
			int x1 = line.getX1();
			int x2 = line.getX2();
			int y1 = (int) line.getOriginOrdinate();
			int y2 = line.getY(x2 - x1);
			int direction = line.getDirection();
			
			// Affiche la barre principale
			g.fillPolygon(new int[] { x1, x1, x2+1, x2+1 }, new int[] { y1-2, y1+2, y2+2, y2-2 }, 4);
			
			// Affiche les autres éléments
			i = hoocked.iterator();
			boolean prevIsSixteenth = false;
			int prevX = 0;
			while (i.hasNext()) {
				LocationItem item = i.next();
				Note note = (Note)item.getElement();
				// Affichage des queues
				item.setFlag(LocationItemFlag.FORCE_QUEUE_UP, (direction == NoteGroup.UP));
				item.setFlag(LocationItemFlag.FORCE_QUEUE_DOWN, (direction == NoteGroup.DOWN));
				int x = item.getPointOfInterestX() + (direction == NoteGroup.DOWN ? -5 : 3);
				int y = line.getY(x - x1);
				if (!note.isRest()) {
					g.drawLine(x, y, x, item.getPointOfInterestY());
				}
				// Affichage des barres secondaires
				if (note.getFigure().getType() == Figure.SIXTEENTH) {
					// Si la précédente était une double, continue la barre sur la gauche
					if (prevIsSixteenth) {
						y1 = line.getY(prevX - x1) + (SECOND_LINE_OFFSET * direction);
						y2 = line.getY(x - x1) + (SECOND_LINE_OFFSET * direction);
						g.fillPolygon(new int[] { prevX, prevX, x+1, x+1 }, new int[] { y1-2, y1+2, y2+2, y2-2 }, 4);
					}
					// Si la précédente était une simple
					else {
						// Si ce n'est pas la dernière du groupe, trace un trait à droite
						if (i.hasNext()) {
							y1 = line.getY(x - x1) + (SECOND_LINE_OFFSET * direction);
							y2 = line.getY(x + LINE_PART_WIDTH - x1) + (SECOND_LINE_OFFSET * direction);
							g.fillPolygon(new int[] { x, x, x+LINE_PART_WIDTH+1, x+LINE_PART_WIDTH+1 }, new int[] { y1-2, y1+2, y2+2, y2-2 }, 4);
						}
						// Si c'est la dernière du groupe trace un trait à gauche
						else {
							y1 = line.getY(x - LINE_PART_WIDTH - x1) + (SECOND_LINE_OFFSET * direction);
							y2 = line.getY(x - x1) + (SECOND_LINE_OFFSET * direction);
							g.fillPolygon(new int[] { x-LINE_PART_WIDTH, x-LINE_PART_WIDTH, x+1, x+1 }, new int[] { y1-2, y1+2, y2+2, y2-2 }, 4);
						}
						
					}
					prevIsSixteenth = true;
					prevX = x;
				}
				else {
					prevIsSixteenth = false;
				}
			}
		}
	}

	
	/**
	 * Affichage d'une liaison
	 */
	protected void paintTiedNoteGroup(Graphics2D g, TiedNoteGroup tiedNotes, LocationItem item) {
		final int TIE_HEIGHT = 8;
		
		LocationItem firstNote = tiedNotes.get(0);
		LocationItem secondNote = tiedNotes.get(1);
		
		// Les deux notes sont sur la m�me ligne
		if (firstNote.getLine() == secondNote.getLine()) {
			int width = secondNote.getX1() - firstNote.getX1() - 15;
			int x = firstNote.getX1() + 15;
			int y = firstNote.getPointOfInterestY() - (TIE_HEIGHT / 2) + 5;
			g.drawArc(x, y, width, TIE_HEIGHT, 0, -180);
		}
		// Les deux notes sont sur des lignes diff�rentes
		else {
			int width = 30;
			int x = firstNote.getX1() + 15;
			int y = firstNote.getPointOfInterestY() - (TIE_HEIGHT / 2) + 5;
			// Demi liaison apr�s la premi�re note
			g.drawArc(x, y, width, TIE_HEIGHT, 270, -90);
			x = secondNote.getX1() - 30;
			y = secondNote.getPointOfInterestY() - (TIE_HEIGHT / 2) + 5;
			// Demi liaison avant la seconde note
			g.drawArc(x, y, width, TIE_HEIGHT, 270, 90);
		}
	}
	
	
	/**
	 * Affichage du groupement de triolets
	 */
	protected void paintTripletGroup(Graphics2D g, TripletGroup tripletGroup, LocationItem item) {
		final int TRIPLET_SIGN_GAP = 10;
		final int BRACE_HEIGHT = 4;
		
		GroupLine line = tripletGroup.getGroupLine();
		int x1 = line.getX1() - 8;
		int x2 = line.getX2();
		
		// Affiche le signe de triolet ("3") pour les notes déjà groupées
		if (tripletGroup.getTripletFigure().isHookable()) {
			int direction = line.getDirection();
			int x = (x2 - x1) / 2;
			int y = line.getY(x) - (TRIPLET_SIGN_GAP*direction) + 4;
			g.drawString("3", x + x1, y);
		}
		// Notes non accrochables, affiche en plus une ligne de groupement
		else {
			int direction = line.getDirection();
			int x = (x2 - x1) / 2;
			int y = line.getY(x) - (TRIPLET_SIGN_GAP*2*direction) + 4;
			g.drawString("3", x + x1, y);
			
			x1 -= BRACE_HEIGHT;
			x2 += BRACE_HEIGHT;
			int y1 = (int) line.getOriginOrdinate() - (TRIPLET_SIGN_GAP*direction);
			int y2 = line.getY(x2 - x1) - (TRIPLET_SIGN_GAP*direction);
			
			// Affiche la barre de groupement
			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x1, y1, x1, y1 + BRACE_HEIGHT*direction);
			g.drawLine(x2, y2, x2, y2 + BRACE_HEIGHT*direction);
		}
	}
	
	
	/**
	 * Affichage d'un accord
	 */
	protected void paintChord(Graphics2D g, Chord chord, LocationItem item) {
		if (item.getFlag(LocationItemFlag.IMPLICIT_ELEMENT) == false &&
				item.getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == false) {
			g.drawString(chord.getName(), item.m_x1, item.m_y1 + g.getFontMetrics().getHeight());
		}
	}
	
	
	/**
	 * Affichage d'un élément d'accompagnement
	 */
	protected void paintAccompaniment(Graphics2D g, Accompaniment acc, LocationItem item) {
		paintChord(g, acc.getChord(), item);
		drawEditingSurroundingHelper(g, item);
	}
	
	
	/**
	 * Affichage d'un silence
	 */
	protected void paintSilence(Graphics2D g, Silence silence, LocationItem item) {
		drawEditingSurroundingHelper(g, item);
	}
	
	
	/**
	 * Affichage d'un �l�ment de paroles
	 */
	protected void paintLyrics(Graphics2D g, Lyrics lyrics, LocationItem item) {
		if (item.getFlag(LocationItemFlag.TEMPORARY_ELEMENT) == false) {
			g.setFont(AwtRenderersResources.m_lyricsFont);
			g.setColor(AwtRenderersResources.m_lyricsForeground);
			// Texte centr� verticalement
			int y = item.getY2() - (g.getFontMetrics().getHeight() / 2);
			g.drawString(lyrics.getText(), item.getX1(), y);
			// Affichage de la zone de texte en mode d'�dition
			drawEditingSurroundingHelper(g, item);
		}
	}
	
	
	protected void drawEditingSurroundingHelper(Graphics2D g, LocationItem item) {
		if (getDrawEditingHelpers() && GlobalPreferences.isEditingHelpersDisplayed()) {
			float dash[] = { 5.0f };
			Stroke prevStroke = g.getStroke();
		    g.setStroke(new BasicStroke(0.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash, 0.0f));
		    g.setColor(Color.DARK_GRAY);
		    g.drawRect(item.getX1(), item.getY1(), item.getWidth()-1, item.getHeight()-1);
		    g.setStroke(prevStroke);
		}
	}

	
	//
	// Attributs
	//
	
	protected ElementRenderer m_noteRenderer = null;
	protected ElementRenderer m_harmoTabElementRenderer = null;
	protected ElementRenderer m_barRenderer = null;
	protected ElementRenderer m_tabRenderer = null;
	protected ElementRenderer m_tempoRenderer = null;
	protected ElementRenderer m_keySignatureRenderer = null;
	protected ElementRenderer m_tabAreaRenderer = null;
	
}
