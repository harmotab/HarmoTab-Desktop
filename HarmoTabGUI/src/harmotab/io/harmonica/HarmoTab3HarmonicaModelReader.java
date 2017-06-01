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

package harmotab.io.harmonica;

import harmotab.core.Height;
import harmotab.element.Tab;
import harmotab.harmonica.HarmonicaModel;
import harmotab.harmonica.HarmonicaType;
import harmotab.throwables.FileFormatError;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


public class HarmoTab3HarmonicaModelReader extends HarmonicaModelReader {
	
	public HarmoTab3HarmonicaModelReader(HarmonicaModel model) {
		super(model);
	}
	
		

	public void read(File file) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList childs = doc.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node node = childs.item(i);
				if (node.getNodeName() == "harmotab")
					parseHarmoTab((Element) node);
				else
					System.out.println("Root node different of 'harmotab' found !");
			}
		} catch (ParserConfigurationException pce) {
			throw new IOException(pce);
		} catch (SAXException se) {
			throw new IOException(se);
		} catch (IOException ioe) {
			throw new IOException(ioe);
		}
	}

	
	/**
	 * Extraction des informations contenues dans la balise <harmotab>
	 */
	private void parseHarmoTab(Element harmotab) {

		// Extraction des attributs
	
		if (harmotab.hasAttribute("file-type")) {
			if (!harmotab.getAttribute("file-type").equals("harmonica-model"))
				throw new FileFormatError("Not harmo tab 3 model file !");
		}
		else {
			System.err.println("File type not found !");
		}
		
		if (harmotab.hasAttribute("file-format-version")) {
			float version = Float.parseFloat(harmotab.getAttribute("file-format-version"));
			if (version < 3.0)
				throw new FileFormatError("Not harmo tab 3 model file !");
			else if (version > 3.0) 
				System.out.println("File version higher than 3.0 !");
		}
		else {
			System.err.println("File type not found !");
		}
		
		// Extraction des noeuds enfants
		NodeList childs = harmotab.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node node = childs.item(i);
			if (node.getNodeName().equals("harmonica"))
				parseHarmonica((Element) node);
			else if (node.getNodeName() != "#text" )
				System.out.println("Harmotab node different of 'harmonica' found ! (" + node.getNodeName() + ")");
		}	
	}


	/**
	 * Extraction des informations contenues dans la balise <harmonica>
	 */
	private void parseHarmonica(Element harmonica) {
		
		// Extraction des attributs
		if (harmonica.hasAttribute("holes")) {
			int holes = Integer.parseInt( harmonica.getAttribute("holes"));
			m_model.setNumberOfHoles(holes);
		}
		else {
			System.err.println("Attribute 'holes' not found !");
		}
		
		if (harmonica.hasAttribute("name")) {
			m_model.setName(harmonica.getAttribute("name"));
		}
		else {
			System.err.println("Attribute 'name' not found !");
		}
		
		if (harmonica.hasAttribute("type")) {
			m_model.setHarmonicaType(
					HarmonicaType.parseHarmonicaType(harmonica.getAttribute("type"))
				);
		}

		// Extraction des noeuds enfants
		NodeList childs = harmonica.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node node = childs.item(i);
			if (node.getNodeName().equals("hole"))
				parseHole((Element) node);
			else if (node.getNodeName() != "#text" )
				System.out.println("Harmonica node different of 'hole' found ! (" + node.getNodeName() + ")");
		}
	}
	
	
	/**
	 * Extraction des informations contenues dans la balise <hole>
	 */
	private void parseHole(Element hole) {
		int holeNumber;
		
		// Extraction des attributs
		if (hole.hasAttribute("number")) {
			holeNumber = Integer.parseInt(hole.getAttribute("number"));
		}
		else {
			throw new FileFormatError("Attribute 'number' not found !");
		}
		
		// Extraction des noeuds enfants
		NodeList childs = hole.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node node = childs.item(i);
			if (node.getNodeName().equals("alt"))
				parseAlt((Element) node, holeNumber);
			else if (node.getNodeName() != "#text" )
				System.out.println("Harmonica node different of 'alt' found ! (" + node.getNodeName() + ")");
		}
	}

	
	/**
	 * Extraction des informations contenues dans la balise <hole>
	 */
	private void parseAlt(Element alt, int hole) {

		// Extraction des attributs
		if (alt.hasAttribute("type")) {
			String type = alt.getAttribute("type");
			String value = alt.getTextContent();
			Tab tab = new Tab(hole);
			tab.setBreath(type);
			tab.setPushed((alt.hasAttribute("piston") && alt.getAttribute("piston").equals("pushed")));
			m_model.setHeight(tab, new Height(value));
		}
		else {
			throw new FileFormatError("Attribute 'type' not found !");
		}
	}

}
