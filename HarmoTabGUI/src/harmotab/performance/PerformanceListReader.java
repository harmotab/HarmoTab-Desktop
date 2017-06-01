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

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;


public class PerformanceListReader {
	
	public void read(PerformancesList list, InputStream stream) throws IOException {
		try {
		    SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser parser = factory.newSAXParser();
		    parser.parse(stream, new PerformanceListXMLHandler(list));
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e.getMessage());
		}
		catch (SAXException e) {
			throw new IOException(e.getMessage());
		}
	}
	
}
