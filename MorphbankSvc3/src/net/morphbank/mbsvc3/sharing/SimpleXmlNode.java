/*******************************************************************************
 * Copyright (c) 2011 Greg Riccardi, Guillaume Jimenez.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  	Greg Riccardi - initial API and implementation
 * 	Guillaume Jimenez - initial API and implementation
 ******************************************************************************/
package net.morphbank.mbsvc3.sharing;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.morphbank.MorphbankConfig;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleXmlNode {
	private static Element rootElement(String filename, String rootName) {
		InputStream fileInputStream = null;
		try {
			URL remoteUrl = new URL(filename);
			URLConnection connection = remoteUrl.openConnection(MorphbankConfig.getProxy());
			fileInputStream = connection.getInputStream();
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(fileInputStream);
			Element rootElement = document.getDocumentElement();
			if (!rootElement.getNodeName().equals(rootName))
				throw new RuntimeException("Could not find root node: " + rootName);
			return rootElement;
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (Exception exception) {
					throw new RuntimeException(exception);
				}
			}
		}
	}

	public SimpleXmlNode(String filename, String rootName) {
		this(rootElement(filename, rootName));
	}

	private SimpleXmlNode(Element element) {
		this.name = element.getNodeName();
		this.content = element.getTextContent();
		NamedNodeMap namedNodeMap = element.getAttributes();
		int n = namedNodeMap.getLength();
		for (int i = 0; i < n; i++) {
			Node node = namedNodeMap.item(i);
			String name = node.getNodeName();
			addAttribute(name, node.getNodeValue());
		}
		NodeList nodes = element.getChildNodes();
		n = nodes.getLength();
		for (int i = 0; i < n; i++) {
			Node node = nodes.item(i);
			int type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) addChild(node.getNodeName(), new SimpleXmlNode((Element) node));
		}
	}

	private void addAttribute(String name, String value) {
		nameAttributes.put(name, value);
	}

	private void addChild(String name, SimpleXmlNode child) {
		List<SimpleXmlNode> children = nameChildren.get(name);
		if (children == null) {
			children = new ArrayList<SimpleXmlNode>();
			nameChildren.put(name, children);
		}
		children.add(child);
	}

	public String name() {
		return name;
	}

	public String content() {
		return content;
	}

	public SimpleXmlNode child(String name) {
		List<SimpleXmlNode> children = children(name);
		if (children.size() != 1)
			throw new RuntimeException("Could not find individual child node: " + name);
		return children.get(0);
	}

	public List<SimpleXmlNode> children(String name) {
		List<SimpleXmlNode> children = nameChildren.get(name);
		return children == null ? new ArrayList<SimpleXmlNode>() : children;
	}

	public String string(String name) {
		String value = nameAttributes.get(name);
		if (value == null)
			throw new RuntimeException("Could not find attribute: " + name + ", in node: "
					+ this.name);
		return value;
	}

	public int integer(String name) {
		return Integer.parseInt(string(name));
	}

	private String name;
	private String content;
	private Map<String, String> nameAttributes = new HashMap<String, String>();
	private Map<String, List<SimpleXmlNode>> nameChildren = new HashMap<String, List<SimpleXmlNode>>();
}
