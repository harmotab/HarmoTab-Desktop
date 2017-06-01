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

package harmotab.performance;

import harmotab.HarmoTabConstants;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Ecrit un fichier contenant une liste d'interpr�tations
 */
public class PerformanceListWriter {

	public void write(OutputStream output, PerformancesList performanceList) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			
			org.w3c.dom.Document doc = builder.newDocument();
			org.w3c.dom.Element root = doc.createElement("harmotab");
			root.setAttribute("file-format-version", "3.1");
			root.setAttribute("file-type", "performance-list");
			root.setAttribute("harmotab-version", String.valueOf(HarmoTabConstants.HT_VERSION));
	
			for (Performance performance : performanceList) {
				org.w3c.dom.Element item = doc.createElement("performance");
				item.setAttribute("file", performance.getFile().getName());
				item.setAttribute("name", performance.getName());
				item.setAttribute("harmonica", performance.getHarmonica().getName());
				item.setAttribute("tunning", performance.getHarmonica().getTunning().getNoteName());
				root.appendChild(item);
			}
			
			// Ecriture du fichier
			doc.appendChild(root);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.transform(new DOMSource(doc), new StreamResult(output));
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
