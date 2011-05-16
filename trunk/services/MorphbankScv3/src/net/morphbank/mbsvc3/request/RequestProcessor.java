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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.MapObjectToResponse;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.ocr.HerbisAnalysis;
import net.morphbank.mbsvc3.ocr.MorphbankOcr;
import net.morphbank.mbsvc3.ocr.Ocr;
import net.morphbank.mbsvc3.rssmapping.RssServices;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.mapping.XmlIds;
import net.morphbank.mbsvc3.morphster.MorphsterRequestProcess;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.object.Specimen;
import net.morphbank.rdf.RdfOntologies;
import net.morphbank.rdfutils.RdfUtils;
import net.morphbank.search.ChangeSearch;
import net.morphbank.search.EolSearch;
import net.morphbank.search.ExtRefSearch;
import net.morphbank.search.Search;
import net.morphbank.search.TaxonSearch;

/**
 * @author riccardi
 * 
 *         Main class for query services for Morphbank Queries can be created
 *         from an XML document with schema mbsvc3.xsd or from an HTTP request
 *         with parameters.
 * 
 *         RequestParams carries the parameters.
 * 
 *         A param object can be created from a Query
 *         (net.morphbank.mbservices.Query) see
 *         net.morphbank.mbobjservices.ProcessQuery
 */
public class RequestProcessor {
	static final long serialVersionUID = 1;

	// static final String CONFIG = MorphbankConfig.PERSISTENCE_MBDEV;
	static final String CONFIG_PARAM = "persistence";
	static final String XML_CONTENTTYPE = "text/xml";
	static final String HTML_CONTENTTYPE = "text/html";
	static final String RSS_CONTENTTYPE = "application/rss+xml";
	static final String RDF_CONTENTTYPE = "application/rdf+xml";
	static final int DEPTH = 2;
	static final String BAD_FORMAT = "<html>bad format</html>";

	public static final String REMOTE_SERVICE = "http://services.morphbank.net/mb3/request?";

	HttpServletRequest req = null;
	HttpServletResponse resp = null;
	// TODO add parameter for central service

	// Search search = null;

	PrintWriter out = null;
	MorphbankPage page = null;
	private XmlIds xmlIds = new XmlIds();
	// ocr parameters
	List<Integer> ocrIds;

	// parameters as an object
	RequestParams params = null;
	boolean isChangeSearchPublic;

	public RequestProcessor(boolean isChangeSearchPublic) {
		this.isChangeSearchPublic = isChangeSearchPublic;
	}

	public RequestProcessor(RequestParams params, PrintWriter out) {
		this.params = params;
		if (out != null) {
			this.out = out;
		} else {
			this.out = new PrintWriter(System.out);
		}
		page = new MorphbankPage(this.out);
	}

	void getOcrParams(RequestParams params) {
		ocrIds = params.getIds();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			this.req = req;
			this.resp = resp;
			EntityManager manager = MorphbankConfig.getEntityManager();
			if (!manager.isOpen()) {
				try {
					manager = MorphbankConfig.getEntityManager();
				} catch (Exception e) {
					MorphbankConfig.SYSTEM_LOGGER.info("error in connect to db: " + e.toString());
					resp.sendError(500, "Cannot connect to database");
					return;
				}
			}
			params = new RequestParams(req);
			this.isChangeSearchPublic = params.isPublic();
			try {
				out = resp.getWriter();
			} catch (Exception e) {
				e.printStackTrace();
			}
			page = new MorphbankPage(out);
			StringBuffer message = new StringBuffer();
			message.append("selected params ").append("format: ").append(params.getFormat())
					.append(" method: ").append(params.getMethod() + " id: ").append(params.getId());
			MorphbankConfig.SYSTEM_LOGGER.info(message.toString());
			processQuery();
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("failure in RequestProcessor::doPost: "
					+ e.toString()+e.getStackTrace());
			resp.sendError(500, "Error in service, please try again later."
					+ " If urgent, send email to mbadmin@sc.fsu.edu");
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// MorphbankConfig.SYSTEM_LOGGER
				// .info("failed to close output file in RequestProcessor::doPost");
				//e.printStackTrace();
			}
		}
	}

	public Object processQuery() throws IOException {
		String method = params.getMethod();
		SearchRequestProcessor searchProcessor = new SearchRequestProcessor(req, resp, params, out,
				page);
		MorphsterRequestProcess rdfFromMorphster = new MorphsterRequestProcess();
		if (method == null || method.equals(RequestParams.METHOD_ID)) {
			if (params.getIds().size() > 0) {
				return searchProcessor.doXmlMBIdResponse(params, out);
			} else {
				resp.sendError(404, "Improper method specified: " + method);
			}
		} else if (method.equals(RequestParams.METHOD_USERGROUP)) {
			// all objects for a group or user?
		} else if (method.equals(RequestParams.METHOD_OCR)) {
			searchProcessor.doOcr(params, out);
		} else if (method.equals("xml") || method.equals(RequestParams.METHOD_SEARCH)) {
			return searchProcessor.doXmlSearch(out);
		} else if (method.equals(RequestParams.METHOD_TAXON)) {
			return searchProcessor.doTaxonSearch(out);
		} else if (method.equals(RequestParams.METHOD_HERBIS)) {
			searchProcessor.doHerbisAnalysis(out);
		} else if (method.equals(RequestParams.METHOD_EXTERNALID)) {
			return searchProcessor.doExternalIdSearch(out);
		} else if (method.equals(RequestParams.METHOD_CHANGES)) {
			ChangeSearch changeSearch = new ChangeSearch(params);
			return searchProcessor.doChangeSearch(changeSearch, out, params.isPublic());
		} else if (method.equals(RequestParams.METHOD_EOL)) {
			return searchProcessor.eolSearch(out);
		} else if (method.equals(RequestParams.METHOD_REMOTE)) {
			RemoteRequestProcessor remote = new RemoteRequestProcessor(req, resp, params, out, page);
			return remote.doRemoteSearch(out);
		} else if (method.equals(RequestParams.METHOD_SHOW)) {
			RemoteRequestProcessor remote = new RemoteRequestProcessor(req, resp, params, out, page);
			return remote.doFetchAndShow(out);
		} else if (method.equals(RequestParams.METHOD_EXTREF)) {
			searchProcessor.doExtRefSearch(out);
		} else if (method.equals(RequestParams.METHOD_MORPHSTER)) {
			rdfFromMorphster.getHTML(out,params);//use httpresponse to get the write, params for id
		} else {// method does not match
			// TODO report correctly according to output format
			if (out != null) {
				resp.sendError(404, "Improper method specified: " + method);
			}
		}
		return null;
		// MorphbankConfig.closeEntityManagerFactory();
	}
}
