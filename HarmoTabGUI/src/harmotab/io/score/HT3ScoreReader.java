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

import harmotab.core.Score;
import harmotab.io.XmlObjectSerializer;
import harmotab.io.XmlSerializedObject;
import harmotab.throwables.FileFormatException;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Lecteur de fichier HT3
 */
public class HT3ScoreReader extends ScoreReader {

	public HT3ScoreReader(Score score, String path) {
		super(score, path);
	}
	
	
	// 
	// Lecture
	// 

	@Override
	protected void read(Score score, File file) throws IOException, FileFormatException {
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList childs = doc.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node node = childs.item(i);
				if (node.getNodeName().equals("harmotab")) {
					NodeList childs2 = node.getChildNodes();
					for (int j = 0; j < childs2.getLength(); j++) {
						Node node2 = node.getChildNodes().item(j);					
						if (node2.getNodeName().equals("score")) {
							XmlObjectSerializer serializer = new XmlObjectSerializer(doc);
							score.deserialize(
								serializer, 
								new XmlSerializedObject(serializer, node2));
						}
						else {
						}
					}
				}
				else {
					System.out.println("Root node different of 'harmotab' found !");
				}
			}
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			throw new FileFormatException("Invalid score file format.", pce);
		} catch (SAXException se) {
			se.printStackTrace();
			throw new FileFormatException("Invalid score file format.", se);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw new IOException(ioe);
		}
		
	}

}
