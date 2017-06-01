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

package harmotab.io;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import harmotab.HarmoTabConstants;
import harmotab.core.HarmoTabObject;
import harmotab.core.HarmoTabObjectFactory;


public class XmlSerializedObject implements SerializedObject {
	
	//
	// Constructeurs
	//
	
	XmlSerializedObject(XmlObjectSerializer serializer, String objectName) {
		m_serializer = serializer;
		m_element = (Element) serializer.m_document.createElement(objectName);
	}
	
	public XmlSerializedObject(XmlObjectSerializer serializer, Node xmlNode) {
		m_serializer = serializer;
		m_element = (Element) xmlNode;
	}
	
	
	//
	// Getters / setters
	//	
	
	public Node getXmlNode() {
		return m_element;
	}
	
	
	//
	// Impl�mentation de l'interface SerializedObject
	//
	
	
	@Override
	public String getObjectType() {
		return m_element.getNodeName();
	}
	

	@Override
	public boolean hasAttribute(String attribute) {
		return m_element.hasAttribute(attribute);
	}

	
	
	@Override
	public String getAttribute(String attribute) {
		return m_element.getAttribute(attribute);
	}

	@Override
	public void setAttribute(String attribute, String value) {
		m_element.setAttribute(attribute, value);
	}
	
	
	
	@Override
	public HarmoTabObject getElementAttribute(String attribute) {
		int objectNodeId = Integer.decode(m_element.getAttribute(attribute));
		
		NodeList nodeList = m_element.getChildNodes();
		int length = nodeList.getLength();
		for (int i = 0; i < length; i++) {
			Node node = nodeList.item(i);
			NamedNodeMap attributes = node.getAttributes();
			if (attributes != null) {
				Element curEle = (Element) node;
				if (curEle.hasAttribute(HarmoTabConstants.SERIALIZATION_ID_ATTR)) {
					int curId = Integer.decode(curEle.getAttribute(HarmoTabConstants.SERIALIZATION_ID_ATTR));
					if (curId == objectNodeId) {
						return HarmoTabObjectFactory.create(m_serializer, new XmlSerializedObject(m_serializer, node));
					}
				}
			}
		}
		
		return null;
	}

	@Override
	public void setElementAttribute(String attribute, HarmoTabObject object) {
		Node objectNode = ((XmlSerializedObject) object.serialize(m_serializer)).getXmlNode();
		m_element.setAttribute(
				attribute, 
				objectNode.getAttributes().getNamedItem(HarmoTabConstants.SERIALIZATION_ID_ATTR).getNodeValue());		
		m_element.appendChild(objectNode);
	}
	

	@Override
	public void addChild(SerializedObject object) {
		m_element.appendChild(((XmlSerializedObject) object).getXmlNode());
	}
	
	@Override
	public int getChildsNumber() {
		return m_element.getChildNodes().getLength();
	}
	
	@Override
	public SerializedObject getChild(int index) {
		Node node = m_element.getChildNodes().item(index);
		if (node.getNodeType() == Node.ELEMENT_NODE)
			return new XmlSerializedObject(m_serializer, node);
		else
			return null;
	}
	
	
	//
	// Attributes
	//
	
	protected Element m_element;
	protected XmlObjectSerializer m_serializer;
	
}
