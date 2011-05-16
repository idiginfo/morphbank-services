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
package net.morphbank.mbsvc3.morphster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import net.morphbank.mbsvc3.morphster.rdf.AnatomicalEntity;
import net.morphbank.mbsvc3.morphster.rdf.Annotation;
import net.morphbank.mbsvc3.morphster.rdf.CharacterAbstract;
import net.morphbank.mbsvc3.morphster.rdf.HasEntity;
import net.morphbank.mbsvc3.morphster.rdf.HasEntityOrQuality;
import net.morphbank.mbsvc3.morphster.rdf.HasPart;
import net.morphbank.mbsvc3.morphster.rdf.HasQuality;
import net.morphbank.mbsvc3.morphster.rdf.MorphsterAnnotation;
import net.morphbank.mbsvc3.morphster.rdf.PhenotypicQuality;
import net.morphbank.mbsvc3.morphster.rdf.RDF;
import net.morphbank.mbsvc3.morphster.rdf.RDFUtils;
import net.morphbank.mbsvc3.request.RequestParams;

public class MorphsterRequestProcess {
	PrintWriter pw;
	MorphsterAnnotation mbAnnotation;

	/**
	 * Other presentation of the html table
	 */
	PrintWriter createHTMLAlternative(PrintWriter out, StringBuffer in) {
		RDF rdf = new RDF();
		Unmarshaller unmarshaller;
		JAXBContext jc;
		pw = new PrintWriter(out);
		RDFUtils.writeHTMLHeader(pw);

		try {
			jc = JAXBContext.newInstance("net.morphbank.mbsvc3.morphster.rdf");
			unmarshaller = jc.createUnmarshaller();
			rdf= (RDF) unmarshaller.unmarshal(new StringReader(in.toString()));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		mbAnnotation = new MorphsterAnnotation(rdf);
		List<Annotation> annotations = mbAnnotation.getAnnotations();
		Iterator<Annotation> itAnn = annotations.iterator();
		Annotation annotation;

		while(itAnn.hasNext()) {
			annotation = (Annotation) itAnn.next();
			pw.println("<tr><td colspan=\"3\">" + "Annotated on: <a href=\"" + annotation.getAnnotatedOn().getResource() + "\" target=\"_blank\">"
					+ annotation.getAnnotatedOn().getResource() + "</a></td></tr>");
			iterateParts(annotation.getHasPart());
			pw.println("<tr><td>" + "Comments: </td><td colspan=\"2\">"	+ annotation.getComment() + "</td></tr>");
		}
		pw.println("</table>");
		pw.println("</body>");
		pw.println("</html>");
		pw.close();
		return pw;
	}

	void iterateParts(List<HasPart> hasParts) {
		HasPart hasPart;
		Iterator<HasPart> itPart = hasParts.iterator();
		while(itPart.hasNext()) {
			hasPart = itPart.next();
			CharacterAbstract chAbst = mbAnnotation.getCharacterbyID(hasPart.getResource());
			if (chAbst == null){ //if resource is not a Character or CharacterState
				pw.println("<tr><td>" + "Has Part: " + "</td><td><a href=" + hasPart.getResource() + " target=\"_blank\"</td>"
						+ hasPart.getResource() + "</a></tr>");
			}
			else { //goes through lists of hasEntity and hasQuality
				writeQualitiesEntities(chAbst);
			}
		}
	}
	
	/**
	 * Gets answer back from morphster via an URL and generates a StringBuffer
	 * @param url
	 * @return rdf StringBuffer
	 * @throws IOException
	 */
	StringBuffer getRdfFromUrl(URL url) throws IOException{
		StringBuffer rdf = new StringBuffer();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(url.openStream()));
		String content = "";
		while ((content = bufferReader.readLine()) != null) {
			System.out.println(content);
			rdf.append(content);
		}
		return rdf;
	}

	/**
	 * Creates the html ouput from the morphbank id
	 * @param out
	 * @param params
	 */
	public void getHTML(PrintWriter out, RequestParams params) {
		StringBuffer rdf = new StringBuffer();
		int id = params.getId();
		try {
			rdf = getRdfFromUrl(new URL("http://tata.csres.utexas.edu:8081/cgi-bin/ann.pl?bankid=" + id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createHTMLAlternative(out, rdf);
	}

	/**
	 * Writes Character and CharacterStare properties: HasEntity and HasQuality in the PrintWriter
	 * @param chAbst
	 */
	void writeQualitiesEntities(CharacterAbstract chAbst) {
		Iterator<HasEntityOrQuality> itEntityorQuality;
		List<HasEntityOrQuality> hasEntitiesOrQualities = chAbst.getHasEntityOrHasQuality();
		pw.println("<tr><td>Character State: </td><td colspan=\"2\">" +	chAbst.getLabel() + "</td><td></td></tr>");
		itEntityorQuality = hasEntitiesOrQualities.iterator();
		while(itEntityorQuality.hasNext()) {
			HasEntityOrQuality hasEntityorQuality = itEntityorQuality.next();
			writeRow(hasEntityorQuality);
		}
	}

	void writeHasEntity(HasEntity hasEntity) {
		AnatomicalEntity anatomical = 
			mbAnnotation.getAnatomicalById(hasEntity.getResource());
		pw.print("Has Entity: " + "</td><td>" + anatomical.getLabel());
	}
	
	void writeHasQuality(HasQuality hasQuality) {
		PhenotypicQuality phenotypic = 
			mbAnnotation.getPhenotypicById(hasQuality.getResource());
		pw.print("Has Quality: " + "</td><td>" + phenotypic.getLabel());
	}
	
	void writeRow(HasEntityOrQuality hasEntityorQuality) {
		pw.print("<tr><td>");
		if (hasEntityorQuality instanceof HasEntity) {
			writeHasEntity((HasEntity) hasEntityorQuality);
		}
		else {
			writeHasQuality((HasQuality) hasEntityorQuality);
		}
		pw.println("</td><td><a href=\"" + RDFUtils.bioportalUrl(hasEntityorQuality) + "\" target=\"_blank\">"
				+ RDFUtils.trimResourceName(hasEntityorQuality.getResource(), true) + "</a></td></tr>");
	}

}
