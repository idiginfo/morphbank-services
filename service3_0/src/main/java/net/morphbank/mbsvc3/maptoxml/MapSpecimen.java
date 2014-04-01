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
package net.morphbank.mbsvc3.maptoxml;

import net.morphbank.mbsvc3.xml.XmlBaseObject;

public abstract class MapSpecimen {

	SourceIterator specimen;
	Fields fields;

	public abstract boolean addRelationshipProperty(XmlBaseObject xmlSpecimen,
			String fieldName, String value);

	public abstract boolean addUserProperty(XmlBaseObject xmlSpecimen,
			String fieldName, String value);

	public abstract boolean setXmlField(XmlBaseObject xmlSpecimen,
			String value, String value2);

	public abstract String[][] getUserProperties();


	public MapSpecimen(SourceIterator specimen, Fields fields) {
		this.specimen = specimen;
		this.fields = fields;
	}

	public void setXmlSpecimenFields(XmlBaseObject xmlSpecimen,
			SourceIterator source) {
		// iterate through fields of source
		String[] headers = source.getHeaders();
		for (String fieldName : headers) {
			String value = source.getValue(fieldName);
			boolean isXmlField = setXmlField(xmlSpecimen, fieldName, value);
			if (isXmlField)
				continue;
			boolean isUserProperty = addUserProperty(xmlSpecimen, fieldName,
					value);
			if (isUserProperty)
				continue;
			boolean isRelationshipProperty = addRelationshipProperty(
					xmlSpecimen, fieldName, value);
			if (isRelationshipProperty)
				continue;
			// doesn't match any property
		}
	}


}
