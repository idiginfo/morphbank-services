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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.MapObjectToResponse;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.ocr.HerbisAnalysis;
import net.morphbank.mbsvc3.ocr.MorphbankOcr;
import net.morphbank.mbsvc3.ocr.Ocr;
import net.morphbank.mbsvc3.rss.TRss;
import net.morphbank.mbsvc3.rss.TRssChannel;
import net.morphbank.mbsvc3.rssmapping.RssServices;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.mbsvc3.mapping.XmlIds;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.object.ExternalLinkObject;
import net.morphbank.object.Specimen;
import net.morphbank.rdf.RdfOntologies;
import net.morphbank.rdfutils.RdfUtils;
import net.morphbank.search.ChangeSearch;
import net.morphbank.search.EolSearch;
import net.morphbank.search.ExtRefSearch;
import net.morphbank.search.KeywordSearch;
import net.morphbank.search.Search;
import net.morphbank.search.TaxonSearch;

/**
 * @author riccardi
 * 
 *         Main class for query services for Morphbank Queries can be created
 *         from mbsvc.xsd or from an HTTP request with parameters RequestParams
 *         carries the parameters A param object can be created from a Query
 *         (net.morphbank.mbservices.Query) see
 *         net.morphbank.mbobjservices.ProcessQuery
 */
public class SearchRequestProcessor {
	static final long serialVersionUID = 1;

	// static final String CONFIG = MorphbankConfig.PERSISTENCE_MBDEV;
	static final String CONFIG_PARAM = "persistence";
	static final String XML_CONTENTTYPE = "text/xml";
	static final String HTML_CONTENTTYPE = "text/html";
	static final String RSS_CONTENTTYPE = "application/rss+xml";
	static final String RDF_CONTENTTYPE = "application/rdf+xml";
	static final int DEPTH = 1;
	static final String BAD_FORMAT = "<html>bad format</html>";

	public static final String REMOTE_SERVICE = "http://services.morphbank.net/mb3/request?";

	HttpServletRequest req = null;
	HttpServletResponse resp = null;

	// TODO add parameter for central service
	public SearchRequestProcessor(HttpServletRequest req, HttpServletResponse resp,
			RequestParams params, PrintWriter out, MorphbankPage page) {
		this.req = req;
		this.resp = resp;
		this.params = params;
		this.out = out;
		this.page = page;
	}

	// Search search = null;

	PrintWriter out = null;
	MorphbankPage page = null;
	private XmlIds xmlIds = new XmlIds();
	// RdfUtils rdfUtils = new RdfUtils();

	// ocr parameters
	List<Integer> ocrIds;

	// parameters as an object
	RequestParams params = null;

	public SearchRequestProcessor() {
	}

	void getOcrParams(RequestParams params) {
		ocrIds = params.getIds();
	}

	public Object doXmlMBIdResponse(RequestParams params, PrintWriter out) {
		return doXmlMBIdResponse(params.getIds(), params, out);
	}

	public Object doXmlMBIdResponse(int id, RequestParams params, PrintWriter out) {
		List<Integer> ids = new Vector<Integer>();
		ids.add(id);
		return doXmlMBIdResponse(ids, params, out);
	}

	public Object doXmlMBIdResponse(List<Integer> ids, RequestParams params, PrintWriter out) {
		boolean addRelatedObjs = params.getLimit() > 1;
		StringBuffer descBuffer = new StringBuffer("Request for id(s) ");
		for (int i = 0; i < ids.size(); i++) {
			int id = ids.get(i);
			descBuffer.append(id).append(" ").append(addRelatedObjs ? " and related objects" : "");
		}
		String desc = descBuffer.toString();
		return doXmlIdResponse(desc, ids, params, out);
	}

	public Object doXmlIdResponse(String desc, List<Integer> ids, RequestParams params,
			PrintWriter out) {
		// TODO manage multiple ids
		this.params = params;
		int id;
		Response response = null;
		MapObjectToResponse mapper = new MapObjectToResponse("id", desc);
		BaseObject obj = null;
		boolean addRelatedObjs = params.getLimit() > 1;
		List objectIds = new Vector();
		for (int i = 0; i < ids.size(); i++) {
			id = ids.get(i);
			objectIds.add(id);
			obj = BaseObject.getEJB3Object(id);
			List relatedObjects = obj.getRelatedIdList();
			objectIds.addAll(relatedObjects);
		}
		return produceOutput(resp, out, desc, desc, objectIds.size(), 1, objectIds);
	}

	public void doOcr(RequestParams params, PrintWriter out) {
		MorphbankOcr ocrSvc = new MorphbankOcr();
		getOcrParams(params);
		Ocr ocr = null;
		int ocrId = ocrIds.get(params.getIds().get(0));
		ocr = ocrSvc.getOcr(ocrId);
		// int objectId = Integer.parseInt(ocrId);
		Specimen specimen = Specimen.getSpecimen(ocrId);
		int sId = specimen.getId();
		int iId = specimen.getStandardImage().getId();
		if (ocr == null) {// no Ocr in database, analyze image
			ocr = ocrSvc.getNewOcrFromObjectId(ocrId);
			if (ocr == null) {
				page.printErrorPage(out, "No ocr returned for specimen " + sId, null);
				return;
			}
			ocrSvc.updateSpecimen(ocrId, ocr);
		}
		page.printOcrPage(out, iId, sId, ocr);
	}

	public Object doXmlSearch(PrintWriter out) {
		KeywordSearch search = new KeywordSearch(params);
		String title;
		String subtitle;
		MorphbankConfig.SYSTEM_LOGGER.info("selected params in doXmlSearch");
		MorphbankConfig.SYSTEM_LOGGER.info("format: " + params.getFormat());
		MorphbankConfig.SYSTEM_LOGGER.info("method: " + params.getMethod());
		// get number of results and object ids for search
		int numResults = search.getNumResults();
		List objectIds = search.getResultIds();
		// return produceOutput(out, title, subtitle, numResults, firstResult,
		// objectIds);
		if (params.getKeywords() != null && params.getKeywords().length() > 0) {
			title = "Keyword query";
			subtitle = getSearchSubtitle("keyword search for ", params.getKeywords(), numResults,
					objectIds.size(), params.isGeolocated(), params.isPublic());
		} else { // no .getKeywords()
			title = "Search";
			subtitle = getSearchSubtitle(null, null, numResults, objectIds.size(), params
					.isGeolocated(), params.isPublic());
		}
		// adjust title and subtitle as required for format
		if (RequestParams.FORMAT_SVC.equals(params.format)) {// user
			// mbsvc.xsd
			System.err
					.println("<!-- persistence: " + MorphbankConfig.getPersistenceUnit() + " -->");
			title = "query";
		} else if (RequestParams.FORMAT_THUMB.equals(params.format)) {
			title = "Morphbank: Results from Keyword Search";
			subtitle += "': Click for Details";
		}

		return produceOutput(resp, out, title, subtitle, numResults, params.getFirstResult(),
				objectIds);
	}

	public Object doTaxonSearch(PrintWriter out) {
		// getSearchParams(req);
		TaxonSearch taxonSearch = new TaxonSearch();
		int numResults = 0;
		List objectIds = null;
		String title;
		String subtitle;
		String taxonName = params.getTaxonName();
		if (params.getObjectTypes() != null && params.getObjectTypes().length > 0
				&& params.getObjectTypes()[0].equals("Image")) {
			numResults = taxonSearch.getNumImages(taxonName);
			objectIds = taxonSearch
					.getImages(taxonName, params.getLimit(), params.getFirstResult());
		} else { // "Specimen"
			numResults = taxonSearch.getNumSpecimens(taxonName);
			objectIds = taxonSearch.getSpecimens(taxonName, params.getLimit(), params
					.getFirstResult());
		}
		title = "query";
		subtitle = getSearchSubtitle("taxon", taxonName, numResults, objectIds.size(), params
				.isGeolocated(), params.isPublic());
		// adjust title and subtitle as appropriate
		if (RequestParams.FORMAT_THUMB.equals(params.format)) {
			title = "Morphbank: Results from Taxon Search";
			subtitle += "': Click for Details";
		}
		return produceOutput(resp, out, title, subtitle, numResults, params.getFirstResult(),
				objectIds);
	}

	public void doThumbnailResponse(MorphbankPage page, RequestParams params, String title,
			String subtitle, int numResults, int firstResult, List objectIds) {
		// based on .getKeywords(), objectTypes and limit
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

	public void doHerbisAnalysis(PrintWriter out) {
		// getSearchParams(req);
		String herbisResults = HerbisAnalysis.getHerbisAnalysis(params.getId());
		out.print(herbisResults);
		out.close();
	}

	public Object doExternalIdSearch(PrintWriter out) {
		// getSearchParams(req);
		String extId = params.getKeywords();
		List<Integer> ids = new Vector<Integer>();
		MorphbankConfig.SYSTEM_LOGGER.info("Ext search extid: " + params.getKeywords());
		BaseObject obj = BaseObject.getObjectByExternalId(extId);
		if (obj == null) {
			return doXmlIdResponse("externalid not found: " + extId, ids, params, out);
		}
		boolean addRelatedObjs = params.getLimit() > 1;
		String desc = "Request for external id " + extId
				+ (addRelatedObjs ? " and related objects" : "");
		ids.add(obj.getId());
		return doXmlIdResponse(desc, ids, params, out);
	}

	public Object doExtRefSearch(PrintWriter out) {
		// getSearchParams(req);
		ExtRefSearch extRefSearch = new ExtRefSearch(params);

		String extId = params.getKeywords();
		List<Integer> ids = new Vector<Integer>();
		MorphbankConfig.SYSTEM_LOGGER.info("Ext search extid: " + params.getKeywords());
		int numResults = extRefSearch.getNumResults();
		List objectIds = extRefSearch.getResultIds();
		String title = "Search by external reference";
		String description = "Objects with external reference to "+params.getKeywords();
		return produceOutput(resp, out, title, description, numResults, params.getFirstResult(),
				objectIds);
	}

	public Object eolSearch(PrintWriter out) {
		EolSearch eolSearcher = new EolSearch();
		int numResults = eolSearcher.getNumEolIds();
		List objectIds = eolSearcher.getEolIds(params.getLimit(), params.getFirstResult());
		String title = "EOL id query";
		if (params.getNumChangeDays() > 0) {
			title = getChangeTitle(params.getNumChangeDays(), numResults, objectIds.size());
		}
		String description = "List of image IDs that are included in the EOL site";
		// if (RequestParams.FORMAT_THUMB.equals(format)) {
		// title = "Morphbank: Changes";
		// }
		return produceOutput(resp, out, title, description, numResults, params.getFirstResult(),
				objectIds);
	}

	String getChangeTitle(int numChanges, int numReturned) {
		StringBuffer title = new StringBuffer("Morphbank changes after ");
		title.append(RequestParams.DATE_FORMATTER.format(params.getChangeDate()));
		if (params.getLastChangeDate() != null) {
			title.append(" and before ").append(
					RequestParams.DATE_FORMATTER.format(params.getLastChangeDate()));
		}
		if (params.getUser() != null) {
			title.append(" for user ").append(params.getUser().getUin());
		}
		if (params.getGroup() != null) {
			title.append(" for group ").append(params.getGroup().getGroupName());
		}
		if (params.getObjectTypes() != null && params.getObjectTypes().length > 0) {
			title.append(" for object types");
			for (int i = 0; i < params.getObjectTypes().length; i++) {
				title.append(" ").append(params.getObjectTypes()[i]);
			}
		}
		title.append(" number changed ").append(numChanges).append(" number returned ").append(
				numReturned);
		return title.toString();
	}

	String getChangeTitle(int numDays, int numChanges, int numReturned) {
		StringBuffer title = new StringBuffer("Morphbank changes in past ");
		title.append(numDays).append(" days");
		if (params.getUser() != null) {
			title.append(" for user ").append(params.getUser().getUin());
		}
		if (params.getGroup() != null) {
			title.append(" for group ").append(params.getGroup().getGroupName());
		}
		if (params.getObjectTypes() != null && params.getObjectTypes().length > 0) {
			title.append(" for object types");
			for (int i = 0; i < params.getObjectTypes().length; i++) {
				title.append(" ").append(params.getObjectTypes()[i]);
			}
		}
		return title.toString();
	}

	Date getChangeDate(int numChangeDays) {
		if (params.getChangeDate() == null) {
			// no change date: search for past 24 hours
			Calendar startDate = getThisMorning();
			int numDays = (numChangeDays < 1 ? 1 : numChangeDays);
			startDate.add(Calendar.DAY_OF_YEAR, 1 - numDays);
			params.setChangeDate(startDate.getTime());
			params.setLastChangeDate(null);
		}
		return params.getChangeDate();
	}

	Calendar getThisMorning() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today;
	}

	public Object doChangeSearch(ChangeSearch search, PrintWriter out, boolean isPublic) {
		getChangeDate(params.getNumChangeDays());
		// get number of results and object ids for search
		int numResults = search.getNumResults();
		List objectIds = search.getResultIds();
		String title = "query";
		if (params.getNumChangeDays() > 0) {
			title = getChangeTitle(params.getNumChangeDays(), numResults, objectIds.size());
		}
		String description = getChangeTitle(numResults, objectIds.size());
		// if (RequestParams.FORMAT_THUMB.equals(format)) {
		// title = "Morphbank: Changes";
		// }
		return produceOutput(resp, out, title, description, numResults, params.getFirstResult(),
				objectIds);
	}

	String getSearchSubtitle(String topic, String parameterDescription, int numIds,
			int numReturned, boolean geolocated, boolean published) {
		StringBuffer sub = new StringBuffer();
		sub.append("Search Results");
		if (topic != null) {
			sub.append(" for ").append(topic).append(" '").append(parameterDescription.trim())
					.append("'");
		}
		if (params.getGroup() != null)
			sub.append(" for Group ").append(params.getGroup().getGroupName());
		if (params.getUser() != null) sub.append(" for User ").append(params.getUser().getUin());
		if (params.getObjectTypes() != null && params.getObjectTypes().length > 0) {
			sub.append(" object types");
			for (int i = 0; i < params.getObjectTypes().length; i++) {
				sub.append(" ").append(params.getObjectTypes()[i]);
			}
		}
		sub.append(" number of matches ").append(numIds).append(" number returned ").append(
				numReturned);
		if (published) {
			sub.append(" including only public objects");
		}
		if (geolocated) {
			sub.append(" all objects are geolocated");
		}
		return sub.toString();
	}

	public Object produceOutput(HttpServletResponse resp, PrintWriter out, String title,
			String subtitle, int numResults, int firstResult, List objectIds) {
		// formats svc, rss and thumb use title and subtitle, others do not
		if (resp != null) resp.setContentType(params.getHttpResponseType());
		if (RequestParams.FORMAT_XML.equals(params.format)
				|| RequestParams.FORMAT_SVC.equals(params.format)) {// user
			// mbsvc.xsd
			// System.err.println("<!-- persistence: "
			// + MorphbankConfig.getPersistenceUnit() + " -->");
			Response xmlResp = XmlServices.createResponse(title, subtitle, numResults, objectIds
					.size(), firstResult, objectIds);
			if (out != null) XmlUtils.printXml(out, xmlResp);
			return xmlResp;
		} else if (params.format == null || RequestParams.FORMAT_ID.equals(params.format)) {
			if (out != null) {
				xmlIds
						.printHeader(out, params.getKeywords(), params.getLimit(), params
								.getObjectTypes(), params.getChangeDate(), params
								.getNumChangeDays(), false);
				xmlIds.getXmlIds(out, numResults, firstResult, objectIds);
				xmlIds.printFooter(out);
			}
			return null;
		} else if (RequestParams.FORMAT_RDF.equals(params.format)) {
			if (out != null) {
				RdfUtils rdfUtils = new RdfUtils(params);
				rdfUtils.getRdfObjects(numResults, firstResult, objectIds, DEPTH, out);
			}
			return null;
		} else if (RequestParams.FORMAT_THUMB.equals(params.format)) {
			if (out != null) {
				doThumbnailResponse(page, params, title, subtitle, numResults, firstResult,
						objectIds);
			}
			return null;
		} else if (RequestParams.FORMAT_RSS.equals(params.format)) {
			Object rss = RssServices.doRssResponse(out, title, subtitle, numResults, firstResult,
					objectIds);
			return rss;
		} else {
			if (out != null) out.println(BAD_FORMAT);
			return BAD_FORMAT;
		}
	}
}
