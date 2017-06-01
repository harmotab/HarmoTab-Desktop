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
import harmotab.core.Score;
import harmotab.io.XmlObjectSerializer;
import harmotab.io.XmlSerializedObject;

import java.io.File;
import java.io.IOException;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class HT3ScoreWriter extends ScoreWriter {

	
	public HT3ScoreWriter(Score score, String path) {
		super(score, path);
	}

	
	@Override
	protected void write(Score score, File file) throws IOException {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();

			Document doc = builder.newDocument();
		
			Element format = doc.createElement("harmotab");
			format.setAttribute("file-format-version", "3.0");
			format.setAttribute("file-type", "score");
			format.setAttribute("harmotab-version", HarmoTabConstants.getVersionString());
	
			XmlObjectSerializer xmlSerializer = new XmlObjectSerializer(doc);
			XmlSerializedObject serializedScore = 
				(XmlSerializedObject) score.serialize(xmlSerializer);
			format.appendChild(serializedScore.getXmlNode());
			
			doc.appendChild(format);
			Source source = new DOMSource(doc);
			Result resultat = new StreamResult(file);
	
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

}
