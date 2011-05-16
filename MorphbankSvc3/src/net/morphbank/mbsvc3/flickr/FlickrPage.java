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
package net.morphbank.mbsvc3.flickr;

import java.io.PrintWriter;
import java.net.URLEncoder;

import net.morphbank.object.BaseObject;
import net.morphbank.object.Locality;
import net.morphbank.mbsvc3.ocr.Ocr;

public class FlickrPage {

	public FlickrPage(PrintWriter out) {
		this.out = out;
	}

	String title, subtitle;
	PrintWriter out = null;

	public void printHeader(String title, String subtitle) {
		printHeader(title, subtitle, null);
	}

	public void printHeader(String title, String subtitle, String rssFeed) {
		this.title = title;
		this.subtitle = subtitle;
		out
				.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
		out.println("<head>");
		out.print("<title>");
		out.print(title);
		out.println("</title>");
		out.println("<link rel=\"stylesheet\" title=\"Default\" "
				+ "href=\"http://www.morphbank.net/style/morphbank2.css\" "
				+ "type=\"text/css\" media=\"screen\" />");
		out
				.println("<link rel=\"shortcut icon\" "
						+ "href=\"http://www.morphbank.net/style/webImages/mLogo16.ico\" />");
		if (rssFeed != null) {
			out.print("<link rel=\"alternate\" href=\"");
			out.print(rssFeed);
			out.println("\"");
			out.println("type=\"application/rss+xml\""
					+ " title=\"\" id=\"morphbank images\" \\>");
		}
		out.println("</head>");
		out.println("<body>");
		out.println("<div id=\"main\">");
		out.println("<div class=\"mainHeader\">");
		out
				.println("<div class=\"mainHeaderLogo\"> "
						+ "<a href=\"http://www.morphbank.net/index.php\"><img border=\"0\" "
						+ "src=\"http://www.morphbank.net/style/webImages/mbLogoHeader.png\" "
						+ "alt=\"logo\" /></a>&nbsp; </div>");
		out.println("<div class=\"mainHeaderTitle\">");
		out.println(title);
		out.println("</div>");
		out.println("</div>");
		out.println("<div class=\"mainRibbon\"></div>");
		out.println("<div class=\"mainGenericContainer\" ><h1>");
		out.println(subtitle);
		out.println("</h1><div>");
	}

	public void printFooter() {
		out.println("</div>");
		out.println("</div> <!-- end div main -->");
		out.println("</body>");
		out.println("</html>");
	}

	int thumbRowCount = 0;
	int thumbRowMax = 8;

	public static String encode(String str) {
		String outStr = null;
		try {
			outStr = URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return outStr;
	}

	public void printThumbHeader(String title, String subtitle, int numResults,
			int numResultsReturned, int firstResult, String rssFeed) {
		printHeader(title, subtitle, rssFeed);
		// TODO print numresults, etc.
		out.println("<h3> Number of Results: " + numResults + "</h3>");
		out.println("<h3> Number of Results Returned: " + numResultsReturned
				+ "</h3>");
		out.println("<h3> First Result: " + firstResult + "</h3>");
		out
				.print("<p><h2><a target=\"googlemap\" href=\"http://maps.google.com?q=");
		out.print(encode(rssFeed));
		out
				.println("\"/>Click for Google Map display of geolocated items</a></h2></p>");
		out.println("<ul id=\"boxes\">");
		thumbRowCount = 0;
	}
	public void printThumbFooter() {
		out.println("</ul></table>");
		printFooter();
	}

	public void printThumbnail(int id) {
		BaseObject obj = BaseObject.getEJB3Object(id);
		if (obj == null) {
			// TODO handle tsn results!
			out.println("No image available");
			return;
		}
		out.print("<li style=\"height: 180px\"><a href=\"");
		out.print(obj.getUrl());
		out.println("\">");
		String thumbURL = null;
		if (obj.getThumbURL() == null) {
			thumbURL = "http://www.morphbank.net/style/webImages/defaultThumbNailNotPub.png";
		} else {
			thumbURL = obj.getFullThumbURL();
		}
		out.print("<img src=\"");
		out.print(thumbURL);
		out.println("\">");

		out.println("<br/>" + obj.getClassName() + ": " + obj.getId());
		out.println("</a>");
		// make google map link
		if (obj.isGeolocated()) {
			Locality locality = obj.getLocalityObject();
			if (locality != null) {
				out.print("<br/><a target=\"google\" href=\"http://maps.google.com/maps?z=8&q=");
				out.print(locality.getLatitude());
				out.print(",");
				out.print(locality.getLongitude());
				out.println("\">Click for Google Map</a>");
			}
		}
		out.println("</li>");
	}

	public void printOcrThumb(BaseObject obj, int imageId, int specId,
			String label) {
		out.print("<li style=\"width: 120px; height: 180px\">");
		out.print("<a href=\"http://morphbank2.scs.fsu.edu:"
				+ "8080/mb/request?id=");
		out.print(obj.getId());
		out.println("&method=ocr\">");
		if (obj.getThumbURL() == null) {
			out.println("No image available");
		} else {
			out.print("<img src=\"");
			out.print(obj.getFullThumbURL());
			out.println("\">");
		}
		out.println("</a>");
		// print identifying info
		out.println("<br />Image: " + imageId);
		out.print("<br />Specimen: " + specId);
		out.print("<br />" + label);
		out.println("</li>");
	}

	public void printErrorPage(PrintWriter out, String title, Exception excpt) {
		printOcrHeader("System error: unable to process request");
		out
				.println("   <div class=\"mainGenericContainer\" style=\"width:760px\"> ");
		out.println(title);
		if (excpt != null) {
			out.println("<br /><pre>");
			excpt.printStackTrace(out);
			out.println("</pre>");
		}
		out.println("</div>");
		printOcrFooter();
	}

	public void printOcrPage(PrintWriter out, int imageId, int specimenId,
			Ocr ocr) {
		printOcrHeader("OCR Results");
		out
				.println("   <div class=\"mainGenericContainer\" style=\"width:760px\"> ");
		out.println("<div><table><tr><td>");
		out.print("<img src=\"http://morphbank.net?imgType=thumb&id=");
		out.print(imageId);
		out.println("\"></td><td>");
		out.print("<h2>Image Id is: ");
		out.print(imageId);
		out.print(", Specimen Id is: ");
		out.print(specimenId);
		out.println("</h2>\n<br /><a href=\"http://morphbank.net?id=");
		out.print(imageId);
		out.println("\">Morphbank page for image</a> ");
		out.print("<br /><a href=\"http://morphbank.net?id=");
		out.print(imageId);
		out.print("&imgType=jpeg\">Jpeg file for image</a> ");
		out
				.print("</a>\n<br /><a href=\"http://morphbank.net/Show/imageViewer?id=");
		out.print(imageId);
		out.println("\" target=\"_blank\">Detail view of image</a>" + "");
		out.print("<br /><a href=\"?id=");
		out.print(specimenId);
		out.println("\" target=\"_blank\">Specimen metadata</a>");
		out.print("<br /><a href=\"?method=herbis&id=");
		out.print(specimenId);
		out.println("\" target=\"_blank\">Herbis analysis</a>");
		out.println("<p>barCode is: ");
		out.println(ocr.getBarCode());
		out.println("</p></td></tr></table>\n<div>OCR is: <div><pre>");
		out.println(ocr.getOcr());
		out.println("</pre></div></div></div></div>");

		printOcrFooter();
		//out.close();
	}

	public void printOcrHeader(String title) {
		out.println("<html><head><title>");
		out.print(title);
		out.print("</title><link rel=\"stylesheet\" ");
		out.print("href=\"http://www.morphbank.net/style/morphbank2.css\" ");
		out.println("type=\"text/css\" media=\"screen\" />");
		out.print("<link rel=\"shortcut icon\" ");
		out
				.println("href=\"http://www.morphbank.net/style/webImages/mLogo16.ico\" />");
		out.println("</head><body>");
		out.println(" <div id=\"main\"> ");
		out.println("   <div class=\"mainHeader\"> ");
		out.println("     <div class=\"mainHeaderLogo\"> ");
		out.println("<a href=\"http://www.morphbank.net/index.php\"> ");
		out.print(" <img border=\"0\"  ");
		out
				.print("src=\"http://www.morphbank.net/style/webImages/mbLogoHeader.png\"");
		out.println(" alt=\"logo\"/>");
		out.println("</a></div> ");
		out.print(" <div class=\"mainHeaderTitle\">");
		out.print(title);
		out.println("  </div></div> ");
		out.println("   <div class=\"mainRibbon\"></div> ");
	}

	public void printOcrFooter() {
		out.println("<div style=\"clear: both;\"/>");
		out
				.println("</div><div id=\"footer\" style=\"position: static; visibility: visible;\">");
		out
				.println("<a href=\"http://morphbank.net\">Morphbank Home</a></div>");
		out.println("</div></body></html>");
	}

}
