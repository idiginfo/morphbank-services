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
package net.morphbank.mbsvc3.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userprop", propOrder = { "property", "value", "namespaceURI" })
public class Userprop {

	@XmlAttribute(required = true)
	protected String property;
	@XmlAttribute(required = true)
	protected String value;
	@XmlAttribute(required = true)
	protected String namespaceURI;

	public Userprop() {

	}

	public Userprop(String property, String value) {
		this(property, value, null);
	}

	public Userprop(String property, String value, String namespaceURI) {
		this.property = property;
		this.value = value;
		this.namespaceURI= namespaceURI;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String value) {
		this.property = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

}
