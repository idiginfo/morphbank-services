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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.02.11 at 11:38:16 AM EST 
//

package net.morphbank.mbsvc3.xml;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlId", propOrder = { "morphbank", "uri", "local", "external" })
public class XmlId {
	@XmlAttribute
	protected String role;
	@XmlAttribute
	protected Integer index;
	@XmlAttribute
	protected String title;
	@XmlAttribute
	protected String objectType;
	@XmlAttribute
	protected Boolean source;
	@XmlElement
	protected Integer morphbank;
	@XmlElement(name = "URI")
	protected List<String> uri;
	@XmlElement
	protected String local;
	@XmlElement
	protected List<String> external;

	public XmlId() {

	}

	public XmlId(int morphbankId) {
		setMorphbank(morphbankId);
	}

	// TODO add constructor with directional attribute and no morpbhbank id

	public XmlId(String role, String objectType) {
		setRole(role);
		setObjectType(objectType);
	}

	public XmlId(String role, String objectType, int mbId) {
		this(role, objectType);
		setMorphbank(mbId);
	}

	public XmlId(String role, String objectType, String extId) {
		this(role, objectType);
		addExternal(extId);
	}

	public XmlId(String role, String objectType, int mbId, String extId) {
		this(role, objectType, mbId);
		addExternal(extId);
	}

	public XmlId(String role, String objectType, String extId, boolean source) {
		this(role, objectType, extId);
		setSource(source);
	}

	/**
	 * 2 XmlId objects are equal if any field matches
	 * 
	 * @param id
	 * @return
	 */
	public boolean matches(XmlId id) {
		if (id == null) return false;
		if (this.hasExternal(id.getExternal())) return true;
		if (this.getLocal() != null && this.getLocal().equals(id.getLocal()))
			return true;
		if (this.getMorphbank() != 0
				&& this.getMorphbank() == id.getMorphbank()) return true;
		if (hasURI(id.getURI())) return true;
		return false;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public boolean hasExternal(String id) {
		Iterator<String> extIds = getExternal().iterator();
		while (extIds.hasNext()) {
			String extId = extIds.next();
			if (extId.equals(id)) return true;
		}
		return false;

	}

	public boolean hasExternal(List<String> idList) {
		if (idList == null) return false;
		Iterator<String> ids = idList.iterator();
		while (ids.hasNext()) {
			if (hasExternal(ids.next())) return true;
		}
		return false;
	}

	public boolean hasURI(String id) {
		Iterator<String> uris = getURI().iterator();
		while (uris.hasNext()) {
			String uri = uris.next();
			if (uri.equals(id)) return true;
		}
		return false;
	}

	public boolean hasURI(List<String> idList) {
		if (idList == null) return false;
		Iterator<String> ids = idList.iterator();
		while (ids.hasNext()) {
			if (hasURI(ids.next())) return true;
		}
		return false;
	}

	public void addExternal(String id) {
		if (!hasExternal(id)) {
			getExternal().add(id);
		}
	}

	public int getMorphbank() {
		if (morphbank == null) return 0;
		return morphbank;
	}

	public void setMorphbank(Integer value) {
		this.morphbank = value;
	}

	public List<String> getURI() {
		if (uri == null) {
			uri = new ArrayList<String>();
		}
		return uri;
	}

	public String getFirstURI() {
		if (getURI().size() > 0) return uri.get(0);
		return null;
	}

	public String getURI(int index) {
		if (getURI().size() > index) return uri.get(index);
		return null;
	}

	public void addURI(String value) {
		this.getURI().add(value);
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String value) {
		this.local = value;
	}

	public List<String> getExternal() {
		if (external == null) {
			external = new ArrayList<String>();
		}
		return external;
	}

	public String getFirstExternal() {
		List<String> externals = getExternal();
		if (externals != null && externals.size() > 0) { return getExternal()
				.get(0); }
		return null;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getSource() {
		return source;
	}

	public void setSource(Boolean source) {
		this.source = source;
	}

	public String toString() {
		StringBuffer out = new StringBuffer();
		out.append("XmlId: ");
		if (getMorphbank() != 0) {
			out.append(" morphbank: " + getMorphbank());
		}
		if (getLocal() != null) {
			out.append(" local: " + getLocal());
		}
		if (getURI() != null) {
			int numURI = getURI().size();
			for (int i = 0; i < numURI; i++) {
				out.append(" URI: " + getURI().get(i));
			}
		}
		Iterator<String> extIds = getExternal().iterator();
		while (extIds.hasNext()) {
			String extId = extIds.next();
			out.append(" external: " + extId);
		}
		return out.toString();
	}

}
