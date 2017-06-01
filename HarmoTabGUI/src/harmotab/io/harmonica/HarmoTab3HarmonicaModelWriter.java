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
import harmotab.harmonica.*;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;


public class HarmoTab3HarmonicaModelWriter extends HarmonicaModelWriter {

	public HarmoTab3HarmonicaModelWriter(File file) {
		super(file);
	}
	
	
	/**
	 *
	 *	<harmonica name="[.*]" holes="[1;50] type="{diatonic|chromatic|other}">
	 * 		<hole number="[1;50]">
	 * 			<alt type="[half bend]">
	 * 				[C|C#|D...]
	 * 			</alt>
	 * 			[<alt type="[half bend]" piston="{released|pushed}">
	 * 				[C|C#|D...]
	 * 			</alt>]
	 * 		</hole>
	 *	</harmonica>
	 */
	public void writeFile(HarmonicaModel model) throws IOException {
		try {
			
			int numberOfHoles = model.getNumberOfHoles();
			boolean hasPiston = model.getHarmonicaType().hasPiston();
			
			//
			// Création de l'arbre XML correspondant au modèle
			//
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			
			Element format = doc.createElement("harmotab");
			format.setAttribute("file-format-version", "3.0");
			format.setAttribute("file-type", "harmonica-model");
			format.setAttribute("harmotab-version", "3.0");

			Element harmonica = doc.createElement("harmonica");
			harmonica.setAttribute("name", model.getName());
			harmonica.setAttribute("holes", model.getNumberOfHoles() + "");
			harmonica.setAttribute("type", model.getHarmonicaType().toString());
			
			for (int i = 1; i <= numberOfHoles; i++) {
				Element hole = doc.createElement("hole");
				hole.setAttribute("number", String.valueOf(i));
				for (byte j = 0; j < 6; j++) {
					// Tab piston relaché
					Tab tab = HarmonicaModel.createTab(i, j, false);
					if (model.isSet(tab)) {
						Element alt = doc.createElement("alt");
						alt.setAttribute("type", tab.getBreathName());
						if (hasPiston == true)
							alt.setAttribute("piston", "released");
						Height height = model.getHeight(tab);
						alt.setTextContent(height.getNoteName() + height.getOctave());
						hole.appendChild(alt);
					}
					// Tab piston poussé
					if (hasPiston) {
						tab = HarmonicaModel.createTab(i, j, true);
						if (model.isSet(tab)) {
							Element alt = doc.createElement("alt");
							alt.setAttribute("type", tab.getBreathName());
							if (hasPiston == true)
								alt.setAttribute("piston", "pushed");
							Height height = model.getHeight(tab);
							alt.setTextContent(height.getNoteName() + height.getOctave());
							hole.appendChild(alt);
						}
					}
				}
				harmonica.appendChild(hole);
			}
			
			format.appendChild(harmonica);
			doc.appendChild(format);
			Source source = new DOMSource(doc);
			Result resultat = new StreamResult(m_file);

			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.transform(source, resultat);
			
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		} catch (TransformerConfigurationException e) {
			throw new IOException(e);
		} catch (TransformerException e) {
			throw new IOException(e);
		}
	}
		
}
