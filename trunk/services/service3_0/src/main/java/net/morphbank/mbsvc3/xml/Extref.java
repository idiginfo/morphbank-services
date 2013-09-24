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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extref", propOrder = { "label", "urlData", "description", "externalId" })
public class Extref {

 	@XmlElement(required = true)
	protected String label;
	@XmlElement(required = true)
	protected String urlData;
	@XmlElement(required = true)
	protected String description;
	@XmlElement(required = true)
	protected String externalId;
	@XmlAttribute
	protected String type;
	
	public Extref(){
		
	}
	
	public Extref(String type, String label, String urlData, String description, String externalId){
		this.type = type;
		this.label = label;
		this.urlData = urlData;
		this.description = description;
		this.externalId = externalId;
	}
	
	public Extref(String externalId, int linkTypeId){
		this.externalId = externalId;
		this.type = XmlUtils.getExternalType(linkTypeId);
	}
	
	public Extref(String externalId, String type){
		this.externalId = externalId;
		this.type = type;
	}
	
	public void addToDescription(String desc){
		if (getDescription()==null){
			setDescription(desc);
		} else {
			setDescription(getDescription()+" "+desc);
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrlData() {
		return urlData;
	}

	public void setUrlData(String urlData) {
		this.urlData = urlData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getType() {
		return type;
	}

	public void setType(String value) {
		this.type = value;
	}

}
