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
package net.morphbank.mbsvc3.morphster.rdf;

import java.io.PrintWriter;

public class RDFUtils {

	/**
	 * Header of the html document
	 * Includes 1st line of the table (headers)
	 * @param pw
	 */
	public static void writeHTMLHeader(PrintWriter pw) {
		pw.println("<html>");
		pw.println("<head>");
		pw.println("<title></title>");
		pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"/>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<table>");
		pw.println("<tr><th>Name</th><th>Label</th><th>Property</th></tr>");
	}
	
	/**
	 * 
	 * @param resource
	 * @param full is true for a full name or false for just the TAO or PATO id
	 * @return
	 */
	public static String trimResourceName(String resource, boolean full) {
		String response = resource.substring(resource.lastIndexOf('#') + 1);
		if (full) {
			if (response.substring(0, 3).equalsIgnoreCase("TAO")) {
				response = new String ("Teleost Anatomy Ontology ").concat(response);
			}
			else if (response.substring(0, 4).equalsIgnoreCase("PATO")) {
				response = new String ("Phenotypic Quality ").concat(response);
			}
		}
		return response;
	}

	/**
	 * Type expected HasEntity or HasQuality
	 * @param id
	 * @return
	 */
	public static String bioportalUrl(HasEntityOrQuality id) {
		String url = RDFUtils.trimResourceName(id.getResource(), false);;
		if (id instanceof HasEntity) {
			return new String("http://bioportal.bioontology.org/virtual/1110/").concat(url);
		}
		else if(id instanceof HasQuality) {
			return new String("http://bioportal.bioontology.org/visualize/45301/?conceptid=").concat(url);
		}
		else return null;
	}

	
	
	
}
