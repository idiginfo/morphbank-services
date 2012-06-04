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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.*;

import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.rdf.RdfOntologies;
import net.morphbank.rdfutils.RdfUtils;
import net.morphbank.mbsvc3.rss.*;
import net.morphbank.mbsvc3.rssmapping.*;
import net.morphbank.mbsvc3.xml.*;

/**
 * @author riccardi
 * 
 *         Main class for query services for Morphbank Queries can be created
 *         from mbsvc.xsd or from an HTTP request with parameters RequestParams
 *         carries the parameters A param object can be created from a Query
 *         (net.morphbank.mbservices.Query) see
 *         net.morphbank.mbobjservices.ProcessQuery
 */
public class FlickrRequestProcessor {
	static final long serialVersionUID = 1;

	static final String CONFIG = MorphbankConfig.PERSISTENCE_MBDEV;
	static final String CONFIG_PARAM = "persistence";
	static final String XML_CONTENTTYPE = "text/xml";
	static final String HTML_CONTENTTYPE = "text/html";
	static final String RSS_CONTENTTYPE = "application/rss+xml";
	static final String RDF_CONTENTTYPE = "application/rdf+xml";
	static final int DEPTH = 2;
	static final String BAD_FORMAT = "<html>bad format</html>";

	// Search search = null;

	PrintWriter out = null;
	FlickrPage page = null;
	XmlIds xmlIds = new XmlIds();
	RdfUtils rdfUtils;
	// ocr parameters
	List<Integer> ocrIds;

	// parameters as an object
	FlickrRequestParams params = null;
	HttpServletRequest req;
	
	public FlickrRequestProcessor(FlickrRequestParams params) {
		this.params = params;
		rdfUtils = new RdfUtils(params);
	}

	// request types:
	// get Flickr info for objects
	// upload objects to flickr

	public FlickrRequestProcessor(HttpServletRequest req) {
		// TODO Auto-generated constructor stub
		this.req = req;
		params = new FlickrRequestParams(req);
	}

	public void doGet(HttpServletResponse resp) {
		doPost(resp);
	}

	public void doPost(HttpServletResponse resp) {
		try {
			EntityManager manager = MorphbankConfig.getEntityManager();
			if (!manager.isOpen()) {
				try {
					manager = MorphbankConfig.getEntityManager();
				} catch (Exception e) {
					resp.setContentType(HTML_CONTENTTYPE);
					page.printErrorPage(out, "failed to open session", e);
					return;
				}
			}
			resp.setContentType(responseType(params.getFormat()));
			try {
				out = resp.getWriter();
			} catch (Exception e) {
				e.printStackTrace();
			}
			page = new FlickrPage(out);
			MorphbankConfig.SYSTEM_LOGGER.info("selected params");
			MorphbankConfig.SYSTEM_LOGGER.info("format: " + params.getFormat() + " local: " + params.getFormat());
			MorphbankConfig.SYSTEM_LOGGER.info("getLimit(): " + params.getMethod() + " local: "
					+ params.getMethod());
			processQuery(params, out);
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("failure in RequestProcessor::doPost");
			e.printStackTrace();
			out.println("Error in service, please try again later."
					+ " If urgent, send email to mbadmin@sc.fsu.edu");
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				MorphbankConfig.SYSTEM_LOGGER.info("failed to close output file in RequestProcessor::doPost");
				e.printStackTrace();
			}
		}

	}

	public String responseType(String format) {
		if (format == null) return XML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_ID)) return XML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_RDF)) return RDF_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_RSS)) return RSS_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_SVC)) return XML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_THUMB))
			return HTML_CONTENTTYPE;
		else
			return XML_CONTENTTYPE;
	}

	public Object processQuery(FlickrRequestParams params, PrintWriter out) {
		this.params = params;
		if (page == null) {
			if (out == null)
				page = new FlickrPage(new PrintWriter(System.out));
			else
				page = new FlickrPage(out);
		}
		if (params.getMethod().equals(FlickrRequestParams.METHOD_UPLOAD)) {
			return doFlickrUpload(params, out);
		} else if (params.getMethod().equals(FlickrRequestParams.METHOD_FLICKRQUERY)) {
			return doFlickrQuery(params, out);
		} else {// method does not match
			if (out != null) {
				page.printErrorPage(out, "Improper method specified " + params.getMethod(), null);
			}
		}
		return null;
		// MorphbankConfig.closeEntityManagerFactory();
	}

	public Object doFlickrUpload(FlickrRequestParams params, PrintWriter out) {
		return doFlickrUpload(params.getIds(), params, out);
	}

	public Object doFlickrUpload(int id, FlickrRequestParams params, PrintWriter out) {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		return doFlickrUpload(ids, params, out);
	}

	public Object doFlickrUpload(List<Integer> ids, FlickrRequestParams params, PrintWriter out) {
		// TODO manage multiple ids
		this.params = params;
		Response response = null;
		FlickrUpload flickrUpload = new FlickrUpload();
		boolean addRelatedObjs = params.getLimit() > 1;
		StringBuffer descBuffer = new StringBuffer("Upload request for id(s) ");
		for (int i = 0; i < ids.size(); i++) {
			int id = ids.get(i);
			descBuffer.append("Request for id ").append(id).append(" ").append(
					addRelatedObjs ? "and related objects" : "");
		}
		String desc = descBuffer.toString();
		MapObjectToResponse mapper = new MapObjectToResponse("id", desc);
		boolean success;
		for (int i = 0; i < ids.size(); i++) {
			int id = ids.get(i);
			BaseObject obj = BaseObject.getEJB3Object(id);
			if (obj instanceof Collection) {
				// desc = "Request for objects of collection " + id;
				success = flickrUpload.upload((Collection) obj);
			} else {
				success = flickrUpload.upload(id);
			}
		}
		return response;
	}

	public Object doFlickrQuery(FlickrRequestParams params, PrintWriter out) {
		return doFlickrQuery(params.getIds(), params, out);
	}

	public Object doFlickrQuery(int id, FlickrRequestParams params, PrintWriter out) {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id);
		return doFlickrQuery(ids, params, out);
	}

	public Object doFlickrQuery(List<Integer> ids, FlickrRequestParams params, PrintWriter out) {
		// TODO manage multiple ids
		this.params = params;
		Response response = null;
		return response;
	}

	public void doThumbnailResponse(FlickrPage page, RequestParams params, String title,
			String subtitle, int numResults, int firstResult, List objectIds) {
		// based on .getKeywords(), objectTypes and getChangeDate()
		String rssFeed = RssServices.getRssUrl(params);
		page.printThumbHeader(title, subtitle, numResults, objectIds.size(), firstResult, rssFeed);
		Iterator iter = objectIds.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			int id;
			if (obj instanceof BaseObject) {
				id = ((BaseObject) obj).getId();
			} else {
				id = MorphbankConfig.getIntFromQuery(obj);
			}
			page.printThumbnail(id);
		}
		page.printThumbFooter();
	}

	public Object produceOutput(PrintWriter out, String title, String subtitle, int numResults,
			int firstResult, List objectIds) {
		// formats svc, rss and thumb use title and subtitle, others do not
		if (RequestParams.FORMAT_XML.equals(params.getFormat())
				|| RequestParams.FORMAT_SVC.equals(params.getFormat())) {// user
			// mbsvc.xsd
			// System.err.println("<!-- persistence: "
			// + MorphbankConfig.getPersistenceUnit() + " -->");
			Response xmlResp = XmlServices.createResponse(title, subtitle, numResults, objectIds
					.size(), firstResult, objectIds);
			if (out != null) XmlUtils.printXml(out, xmlResp);
			return xmlResp;
		} else if (params.getFormat() == null || RequestParams.FORMAT_ID.equals(params.getFormat())) {
			if (out != null) {
				xmlIds.printHeader(out, params.getKeywords(), params.getLimit(), params.getObjectTypes(),
						null, -1, false);
				xmlIds.getXmlIds(out, numResults, firstResult, objectIds);
				xmlIds.printFooter(out);
			}
			return null;
		} else if (RequestParams.FORMAT_RDF.equals(params.getFormat())) {
			if (out != null) {
				out.println("<rdfobjects>");
				rdfUtils.getRdfObjects(numResults, firstResult, objectIds, DEPTH, out);
				out.println("</rdfobjects>");
			}
			return null;
		} else if (RequestParams.FORMAT_THUMB.equals(params.getFormat())) {
			if (out != null) {
				doThumbnailResponse(page, params, title, subtitle, numResults, firstResult,
						objectIds);
			}
			return null;
		} else if (RequestParams.FORMAT_RSS.equals(params.getFormat())) {
			Object rss = RssServices.doRssResponse(out, title, subtitle, numResults, firstResult,
					objectIds);
			return rss;
		} else {
			if (out != null) out.println(BAD_FORMAT);
			return BAD_FORMAT;
		}
	}
}
