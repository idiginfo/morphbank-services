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
package net.morphbank.mbsvc3.request;

import java.io.PrintWriter;
import java.net.URLEncoder;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.ocr.Ocr;
import net.morphbank.mbsvc3.xml.ObjectList;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Locality;

public class MorphbankPage {

	public MorphbankPage(PrintWriter out) {
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
		out.println("<!DOCTYPE html >");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">");
		out.println("<head>");
		out.print("<title>");
		out.print(title);
		out.println("</title>");
		out.println("<link rel=\"stylesheet\" title=\"Default\" "
				+ "href=\"http://www.morphbank.net/style/morphbank2.css\" "
				+ "type=\"text/css\" media=\"screen\" />");
		out.println("<link rel=\"shortcut icon\" "
				+ "href=\"http://www.morphbank.net/style/webImages/mLogo16.ico\" />");
		if (rssFeed != null) {
			out.print("<link rel=\"alternate\" href=\"");
			out.print(encode(rssFeed));
			out.println("\"");
			out.println("type=\"application/rss+xml\""
					+ " title=\"\" id=\"morphbankimages\" />");
		}
		out.println("</head>");
		out.println("<body>");
		out.println("<div id=\"main\">");
		out.println("<div class=\"mainHeader\">");
		out.println("<div class=\"mainHeaderLogo\"> "
				+ "<a href=\"http://www.morphbank.net/index.php\"><img "
				+ "src=\"http://www.morphbank.net/style/webImages/mbLogoHeader.png\" "
				+ "alt=\"logo\" /></a> </div>");
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

	public static String encodeAmp(String str) {
		String outStr = null;
		try {
			
			outStr = str.replace("&", "&amp;");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return outStr;
	}

	static final String POPUP_STYLE = "<style type=\"text/css\" scoped>#boxes a span {display: none;}\n"
			+ "#boxes a:hover span{\n"
			+ "display: block;\n"
			+ "position: absolute;\n"
			+ "top: 100px;\n"
			+ "left: 0px;\n"
			+ "width:150px;\n"
			+ "margin: 0px;\n"
			+ "padding: 10px;\n"
			+ "color: #335500;\n"
			+ "font-weight: normal;\n"
			+ "background: #e5e5e5;\n"
			+ "text-align: left;\n"
			+ "border: 1px solid #666;\n" + "z-index:1000;\n" + "}</style> ";

	public void printThumbHeader(String title, String subtitle, int numResults,
			int numResultsReturned, int firstResult, String rssFeed) {
		printHeader(title, subtitle, rssFeed);
		out.println(POPUP_STYLE);
		// TODO print numresults, etc.
		out.println("<h3> Number of Results: " + numResults + "</h3>");
		out.println("<h3> Number of Results Returned: " + numResultsReturned
				+ "</h3>");
		out.println("<h3> First Result: " + firstResult + "</h3>");
		out.print("<h2><a target=\"googlemap\" href=\"http://maps.google.com?q=");
		out.print(encode(rssFeed));
		out.println("\">Click for Google Map display of geolocated items</a></h2>");
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
			out.println("No object available");
			return;
		}
		String showUrl = obj.getUrl();
		String objectType = obj.getClassName();
		String thumbUrl = null;
		if (obj.getThumbURL() == null) {
			thumbUrl = "http://www.morphbank.net/style/webImages/defaultThumbNailNotPub.png";
		} else {
			thumbUrl = encodeAmp(obj.getFullThumbURL());
		}
		String popupHtml = getPopupHtml(obj);
		double latitude = 0.0;
		double longitude = 0.0;
		boolean isGeolocated = obj.getGeolocated();
		if (isGeolocated) {
			Locality locality = obj.getLocalityObject();
			if (locality != null) {
				latitude = locality.getLatitude();
				longitude = locality.getLongitude();
			}
		}
		printThumbnail(showUrl, thumbUrl, objectType, id, popupHtml,
				isGeolocated, latitude, longitude);
	}

	public void printThumbnail(String showUrl, String thumbUrl,
			String objectType, int id, String popupHtml, boolean isGeolocated,
			double latitude, double longitude) {
		out.print("<li style=\"height: 180px\"><a href=\"");
		out.print(showUrl);
		out.println("\">");
		out.print(getImgThumbTag(thumbUrl, id));
		out.println("<br/>" + objectType + ": " + id);
		out.println(popupHtml);
		out.println("</a>");
		// make google map link
		if (isGeolocated) {
			out.print("<br/><a target=\"google\" href=\""
					+ "http://maps.google.com/maps?z=8&amp;q=" + latitude + ","
					+ longitude);
			out.println("\">Click for Google Map</a>");
		}
		out.println("</li>");
	}

	public void printOcrThumb(BaseObject obj, int imageId, int specId,
			String label) {
		out.print("<li style=\"width: 120px; height: 180px\">");
		String url = "http://morphbank2.scs.fsu.edu:8080/mb/request?id="
				+ obj.getId() + "&amp;method=ocr";
		out.print("<a href=\"" + url + "\">");
		if (obj.getThumbURL() == null) {
			out.println("No image available");
		} else {
			out.print(getImgThumbTag(obj.getFullThumbURL(), imageId));
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
		out.println("   <div class=\"mainGenericContainer\" style=\"width:760px\"> ");
		out.println(title);
		if (excpt != null) {
			out.println("<br /><pre>");
			excpt.printStackTrace(out);
			out.println("</pre>");
		}
		out.println("</div>");
		printOcrFooter();
	}

	public String getImgThumbTag(String thumbUrl, int imageId) {
		return getImgTag(thumbUrl, imageId, "thumbnail image of " + imageId);
	}

	public String getImgTag(String thumbUrl, int imageId, String altText) {
		String imgTag = "<img src=\"" + thumbUrl + "\" alt=\"" + altText
				+ "\"/>";
		return imgTag;
	}

	public void printOcrPage(PrintWriter out, int imageId, int specimenId,
			Ocr ocr) {
		printOcrHeader("OCR Results");
		out.println("   <div class=\"mainGenericContainer\" style=\"width:760px\"> ");
		out.println("<div><table><tr><td>");
		out.print(getImgThumbTag("http://morphbank.net?imgType=thumb&amp;id="
				+ imageId, imageId));
		out.println("</td><td>");
		out.print("<h2>Image Id is: ");
		out.print(imageId);
		out.print(", Specimen Id is: ");
		out.print(specimenId);
		out.println("</h2>\n<br /><a href=\"http://morphbank.net?id=");
		out.print(imageId);
		out.println("\">Morphbank page for image</a> ");
		out.print("<br /><a href=\""
				+ getImgThumbTag("http://morphbank.net?id=" + imageId
						+ "&amp;imgType=jpeg", imageId)
				+ "Jpeg file for image</a> ");
		out.print("</a>\n<br /><a href=\"http://morphbank.net/Show/imageViewer?id=");
		out.print(imageId);
		out.println("\" target=\"_blank\">Detail view of image</a>" + "");
		out.print("<br /><a href=\"?id=");
		out.print(specimenId);
		out.println("\" target=\"_blank\">Specimen metadata</a>");
		out.print("<br /><a href=\"" + "?method=herbis&amp;id=");
		out.print(specimenId);
		out.println("\" target=\"_blank\">Herbis analysis</a>");
		out.println("<p>barCode is: ");
		out.println(ocr.getBarCode());
		out.println("</p></td></tr></div>\n<div>OCR is: <div><pre>");
		out.println(ocr.getOcr());
		out.println("</pre></div></div></div></div>");

		printOcrFooter();
		// out.close();
	}

	public void printOcrHeader(String title) {
		out.println("<html><head><meta charset=\"utf-8\"/><title>");
		out.print(title);
		out.print("</title><link rel=\"stylesheet\" ");
		out.print("href=\"http://www.morphbank.net/style/morphbank2.css\" ");
		out.println("type=\"text/css\" media=\"screen\" />");
		out.print("<link rel=\"shortcut icon\" ");
		out.println("href=\"http://www.morphbank.net/style/webImages/mLogo16.ico\" />");
		out.println("</head><body>");
		out.println(" <div id=\"main\"> ");
		out.println("   <div class=\"mainHeader\"> ");
		out.println("     <div class=\"mainHeaderLogo\"> ");
		out.println("<a href=\"http://www.morphbank.net/index.php\"> ");
		out.print(" <img ");
		out.print("src=\"http://www.morphbank.net/style/webImages/mbLogoHeader.png\"");
		out.println(" alt=\"logo\"/>");
		out.println("</a></div> ");
		out.print(" <div class=\"mainHeaderTitle\">");
		out.print(title);
		out.println("  </div></div> ");
		out.println("   <div class=\"mainRibbon\"></div> ");
	}

	public void printOcrFooter() {
		out.println("<div style=\"clear: both;\"/>");
		out.println("</div><div id=\"footer\" style=\"position: static; visibility: visible;\">");
		out.println("<a href=\"http://morphbank.net\">Morphbank Home</a></div>");
		out.println("</div></body></html>");
	}

	public void printThumbnail(XmlBaseObject xmlObj, ObjectList list) {
		if (xmlObj == null) {
			// TODO handle tsn results!
			out.println("No object available");
			return;
		}
		int id = xmlObj.getMorphbankId();
		String showUrl = getRemoteShowUrl(id);
		String objectType = xmlObj.getObjectTypeId();
		String thumbUrl = xmlObj.getThumbUrl();
		String popupHtml = getPopupHtml(xmlObj, list);
		double latitude = 0.0;
		double longitude = 0.0;
		printThumbnail(showUrl, thumbUrl, objectType, id, popupHtml, false,
				latitude, longitude);
	}

	String getRemoteShowUrl(int id) {
		String showRemote = MorphbankConfig.getServicePrefix() + "method="
				+ RequestParams.METHOD_SHOW + "&amp;id=" + id;
		return showRemote;
	}

	private String getPopupHtml(BaseObject obj) {
		return "<span>" + getPopupText(obj) + "</span>";
	}

	private String getPopupText(BaseObject obj) {
		return "popup text should be here";
	}

	private String getPopupHtml(XmlBaseObject xmlObj, ObjectList list) {
		return "<span>" + xmlObj.getHtmlDesc(list) + "</span>";
	}

}
