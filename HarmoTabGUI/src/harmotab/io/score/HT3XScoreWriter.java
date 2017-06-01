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

package harmotab.io.score;

import harmotab.HarmoTabConstants;
import harmotab.core.*;
import harmotab.element.*;
import harmotab.renderer.*;
import harmotab.sound.*;
import harmotab.track.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.*;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * 
 */
public class HT3XScoreWriter extends ScoreWriter {
	private final static int BUFFER_SIZE = 2048;
	
	private final static int SOURCE_FILE = 0;
	private final static int IMAGE_FILE = 1;
	private final static int SOUND_FILE = 2;
	private final static int MAPPING_FILE = 3;
	
	
	//
	// Constructeur
	//
	
	public HT3XScoreWriter(Score score, String path) {
		super(score, path);
		m_temporaryFilesPaths = new ArrayList<File>(4);
		m_temporaryFilesPaths.add(null);	// SOURCE_FILE
		m_temporaryFilesPaths.add(null);	// IMAGE_FILE
		m_temporaryFilesPaths.add(null);	// SOUND_FILE
		m_temporaryFilesPaths.add(null);	// MAPPING_FILE
	}

	
	//
	// M�thode publique
	//
	
	@Override
	protected void write(Score score, File file) throws IOException {
		try {
			// Cr�ation des diff�rents fichiers de l'archive
			createTemporaryFiles();
			
			// Ajout des fichiers temporaires dans l'archive
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(file);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER_SIZE];

			for (int i = 0; i < m_temporaryFilesPaths.size(); i++) {
				File currentFile = m_temporaryFilesPaths.get(i);
				FileInputStream fi = new FileInputStream(currentFile);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				ZipEntry entry = new ZipEntry(m_temporaryFilesPaths.get(i).getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
			
			// Suppression des fichiers temporaires cr��s
			deleteTemporaryFiles();
		}
		catch (Throwable exception) {
			deleteTemporaryFiles();
			throw new IOException(exception);
		}
	}
	
	
	//
	// M�thodes priv�es
	//
	
	protected void createTemporaryFiles() throws IOException {
		ScoreController controller = new ScoreController(m_score);
		
		// HT3
		File ht3File = File.createTempFile("src_", ".ht3");
		ht3File.deleteOnExit();
		m_temporaryFilesPaths.set(SOURCE_FILE, ht3File);
		controller.saveScoreAs(m_temporaryFilesPaths.get(SOURCE_FILE).getAbsolutePath());
		
		// PNG
		File pngFile = File.createTempFile("img_", ".png");
		pngFile.deleteOnExit();
		m_temporaryFilesPaths.set(IMAGE_FILE, pngFile);
		LocationList locations = createImage(m_temporaryFilesPaths.get(IMAGE_FILE).getAbsolutePath());
		
		// MID
		File midFile = File.createTempFile("snd_", ".mid");
		midFile.deleteOnExit();
		m_temporaryFilesPaths.set(SOUND_FILE, midFile);
		controller.exportAsMidi(m_temporaryFilesPaths.get(SOUND_FILE).getAbsolutePath());
		
		// SMAP
		File smapFile = File.createTempFile("mapping_", ".smap");
		smapFile.deleteOnExit();
		m_temporaryFilesPaths.set(MAPPING_FILE, smapFile);
		ScorePlayerController scorePlayerController = new ScorePlayerController(null, m_score);
		createSmapFile(
				locations, 
				scorePlayerController.createSoundSequence(), 
				m_temporaryFilesPaths.get(MAPPING_FILE).getAbsolutePath());
		
	}
	

	/**
	 * Suppression de tous les fichiers temporaires cr��s
	 */
	protected void deleteTemporaryFiles() {
		for (File file : m_temporaryFilesPaths) {
			if (file != null) {
				if (file.delete() == false)
					System.err.println("Error deleting file " + file.getAbsolutePath());
			}
		}
	}

	
	/**
	 * Export en format image
	 */
	private LocationList createImage(String path) throws IOException {
		// Cr�ation de l'image
		ScoreRenderer renderer = new ScoreRenderer(m_score);
		renderer.setElementRenderer(new AwtPrintingElementRendererBundle());
		int width = HarmoTabConstants.DEFAULT_SCORE_WIDTH;
		int height = Integer.MAX_VALUE;
		
		// Layout de la partition
		renderer.setPageSize(width, height);
		renderer.setMultiline(true);
		LocationList locations = new LocationList();
		renderer.layout(locations);
		height = locations.getBottomOrdinate() + renderer.getInterlineHeight();

		GraphicsConfiguration graphicsConfiguration = 
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		BufferedImage image = graphicsConfiguration.createCompatibleImage(width, height/*, Transparency.BITMASK*/);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);	
		renderer.paint(g2d, locations, new Point(0, 0));
		
		// Ecriture des fichiers en sortie
		ImageIO.write(image, "PNG", new File(path));
		
		// Retourne la localisation des diff�rents �l�ments
		return locations;
	}
	
	
	/**
	 * Cr�ation du fichier smap.
	 * Contient un map entre
	 *  - les �l�ments de la partition
	 *  - leur position et dur�e de lecture
	 *  - leur position sur l'image
	 */
	private void createSmapFile(LocationList locations, SoundSequence sounds, String path) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			
			org.w3c.dom.Document doc = builder.newDocument();
			org.w3c.dom.Element root = doc.createElement("harmotab");
			root.setAttribute("file-format-version", "3.0");
			root.setAttribute("file-type", "score-export-mapping");
			root.setAttribute("harmotab-version", "3.0");
	
			for (Track track : m_score) {
				int trackId = track.getTrackIndex();
				for (Element element : track) {
					SoundItem soundItem = sounds.get(element);
					LocationItem locationItem = locations.get(element);
					if (soundItem != null && locationItem != null) {
						org.w3c.dom.Element item = doc.createElement("element");
						item.setAttribute("trackId", Integer.toString(trackId));
						item.setAttribute("elementId", "#" + Integer.toHexString(element.hashCode()));
						item.setAttribute("startTime", Float.toString(soundItem.m_startTime));
						item.setAttribute("duration", Float.toString(soundItem.m_durationTime));
						item.setAttribute("x", Integer.toString(locationItem.getX1()));
						item.setAttribute("y", Integer.toString(locationItem.getY1()));
						item.setAttribute("width", Integer.toString(locationItem.getWidth()));
						item.setAttribute("height", Integer.toString(locationItem.getHeight()));
						root.appendChild(item);
					}
				}
			}
			
			//root.appendChild(serializedScore.getXmlNode());
			doc.appendChild(root);
			Source source = new DOMSource(doc);
			Result resultat = new StreamResult(path);
	
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.transform(source, resultat);
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IOException(e);
		} 
		catch (TransformerException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	
	
	//
	// Attributs
	//
	
	protected ArrayList<File> m_temporaryFilesPaths;
	
}
