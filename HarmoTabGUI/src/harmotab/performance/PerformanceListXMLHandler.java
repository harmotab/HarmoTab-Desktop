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

import java.io.File;

import harmotab.core.Height;
import harmotab.harmonica.Harmonica;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Handler XML SAX pour r�cr�er une liste d'interpr�tations � partir d'un flux 
 * XML.
 */
public class PerformanceListXMLHandler extends DefaultHandler {
	
	public PerformanceListXMLHandler(PerformancesList list) {
		mList = list;
	}
	
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equalsIgnoreCase("harmotab")){
        	//RAF
        }
        // Nouvelle entr�e d'interpr�tation
        if(name.equalsIgnoreCase("performance")){
        	String perfFile = "";
        	String perfName = "";
        	Harmonica perfHarmonica = new Harmonica();
        	
        	// Extraction des valeurs des attributs
        	for (int i = 0; i < attributes.getLength(); i++) {
        		String attr = attributes.getLocalName(i);
        		String value = attributes.getValue(i);
        		
        		if (attr.equals("file")) {
        			perfFile = value;
        		}
        		else if (attr.equals("name")) {
        			perfName = value;
        		}
        		else if (attr.equals("harmonica")) {
        			perfHarmonica.setName(value);
        		}
        		else if (attr.equals("tunning")) {
        			perfHarmonica.setTunning(new Height(value));
        		}
        	}
        	
        	// Enregistrement de l'interpr�tation
        	Performance perf = new Performance(new File(perfFile), perfName, perfHarmonica);
        	mList.add(perf);
        }
    }

	private PerformancesList mList = null;

}