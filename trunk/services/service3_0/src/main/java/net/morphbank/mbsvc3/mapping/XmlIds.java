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
package net.morphbank.mbsvc3.mapping;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
/**
 * Class to produce list of morphbank ids as an XML document 
 * @author riccardi
 *
 */
public class XmlIds {

	public XmlIds() {
	}

	public void printNumResults(PrintWriter out, int numResults, int numResultsReturned,
			int firstResult) {
		out.println("\t<numResults>" + numResults + "</numResults>");
		out.println("\t<numResultsReturned>" + numResultsReturned + "</numResultsReturned>");
		out.println("\t<firstResult>" + firstResult + "</firstResult>");
	}

	public void getXmlIds(PrintWriter out, int numResults, int firstResult, List objectIds) {
		int numObjectIds = 0;
		if (numResults != 0 && objectIds != null) {
			numObjectIds = objectIds.size();
		}
		printNumResults(out, numResults, numObjectIds, firstResult);
		if (numResults == 0) {
			return;
		}
		if (objectIds == null) {
			return;
		}
		Iterator iter = objectIds.iterator();
		while (iter.hasNext()) {
			out.print("\t<id>");
			Object obj = iter.next();
			int id;
			if (obj instanceof BaseObject) {
				id = ((BaseObject) obj).getId();
			} else {
				id = MorphbankConfig.getIntFromQuery(obj);
			}
			out.print(id);
			out.println("</id>");
		}
	}

	public void printHeader(PrintWriter out, String keywords, int limit, String[] objectTypes,
			Date changeDate, int numChangeDays, boolean isGeolocated) {
		out.print(getIdHeader(keywords, limit, objectTypes, changeDate, numChangeDays, isGeolocated));
	}

	public String getIdHeader(String keywords, int limit, String[] objectTypes,
			Date changeDate, int numChangeDays, boolean isGeolocated) {
		StringBuffer out = new StringBuffer();
		out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.append("<mbresponse>\n");
		if (keywords!=null && keywords.length()>0){
			// print keywords info
			out.append("\t<keywords>").append(keywords).append("</keywords>\n");
		}
		if(changeDate!=null) {
			//print change info
			out.append("\t<changeDate>").append(changeDate).append("</changeDate>\n");
		}
		if (numChangeDays>0){
			out.append("\t\t<numChangeDays>").append(numChangeDays).append("</numChangeDays>\n");
		}
		if (objectTypes != null && objectTypes.length > 0) {
			out.append("\t<objecttypes>");
			String space = "";
			for (int i = 0; i < objectTypes.length; i++) {
				out.append(space);
				out.append(objectTypes[i]);
				space =" ";
			}
			out.append("</objecttypes>\n");
		}
		out.append("\t<limit>").append(limit).append("</limit>\n");
		if (isGeolocated){
			out.append("<geolocated/>");
		}
		return out.toString();
	}

	public void printFooter(PrintWriter out) {
		out.print("</mbresponse>\n");
	}

}
