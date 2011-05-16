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
package net.morphbank.mbsvc3.sharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.spi.TransactionalWriter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.MapXmlToObject;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.mbsvc3.xml.ObjectList;
import net.morphbank.mbsvc3.xml.RequestSummary;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.object.BaseObject;
import net.morphbank.object.CollectionObject;
import net.morphbank.object.Image;
import net.morphbank.object.Locality;
import net.morphbank.object.MissingLink;
import net.morphbank.object.Specimen;
import net.morphbank.object.View;

import org.xml.sax.SAXException;

/**
 * 
 * @author riccardi
 * 
 */
public class UpdateRemote {

	String remoteServer;
	String remoteChangesUrlBase;
	String remoteDetailsUrlBase;
	HttpServletRequest request;
	HttpServletResponse response;
	int numDays = 10;
	int limit = 100;
	int firstResult = 0;

	//extra params
	int user;
	int group;
	String[] keywords;
	String[] objectTypes;

	PrintWriter out = null;
	MapXmlToObject xmlMapper = null;
	Responses responses = null;

	public UpdateRemote(String remoteServer) {
		this.remoteServer = remoteServer;
		MorphbankConfig.setRemoteServer(remoteServer);
		this.remoteChangesUrlBase = remoteServer
		+ MorphbankConfig.MB_CHANGES_REQUEST;
		this.remoteDetailsUrlBase = remoteServer
		+ MorphbankConfig.MB_DETAILS_REQUEST;
		xmlMapper = new MapXmlToObject(remoteDetailsUrlBase);
	}

	public UpdateRemote(HttpServletRequest request, HttpServletResponse response) {
		this(MorphbankConfig.getRemoteServer());
		this.request = request;
		this.response = response;
		String numDaysString = request
		.getParameter(RequestParams.PARAM_NUM_CHANGE_DAYS);
		if (numDaysString != null)
			numDays = Integer.valueOf(numDaysString);
		String limitString = request.getParameter(RequestParams.PARAM_LIMIT);
		if (limitString != null)
			limit = Integer.valueOf(limitString);
		try {
			out = response.getWriter();
		} catch (IOException e) {
			out = new PrintWriter(System.out);
		}
	}

	/**
	 * Update the local database from an XML update document
	 * 
	 * @param id
	 * @return
	 */
	protected Response updateObjects(ObjectList objects) {
		ProcessRequest processRequest = new ProcessRequest(null);
		Response resp = processRequest.processUpdate(null, objects);
		return resp;
	}

	public Response updateObject(int id) {
		return updateObject(id, remoteDetailsUrlBase + id);
	}

	public Response updateObject(int id, String remoteDetailsUrl) {
		// prepare URL for object
		try {
			URL getObjectUrl = new URL(remoteDetailsUrl);
			// get response object for URL
			ObjectList objectInfo = XmlUtils.getXmlObjectList(getObjectUrl);
			if (objectInfo == null) {
				// no object information returned for id
				xmlMapper.recordMissingLink(id, id, "object");
				MorphbankConfig.SYSTEM_LOGGER
				.info("No information provided for id " + id
						+ " stored in MissingLinks");
				return null;
			}
			// send the document for update/insert
			Response resp = updateObjects(objectInfo);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected List<Integer> getRemoteChanges(int numDays, int limit) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Writer out = new OutputStreamWriter(System.out, "UTF8");
			SAXParser saxParser = factory.newSAXParser();
			// InputStream doc = new FileInputStream(REQ_FILE);
			URL changeUrl = new URL(getChangeUrl(numDays, limit, firstResult));
			MorphbankConfig.SYSTEM_LOGGER.info(changeUrl.toString());
			URLConnection connection = changeUrl.openConnection(MorphbankConfig
					.getProxy());
			InputStream doc = connection.getInputStream();
			IdListCreator idListCreator = new IdListCreator(remoteServer);
			saxParser.parse(doc, idListCreator);
			return idListCreator.getIdList();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return null;
	}

	//guillaume
	protected List<Integer> getRemoteChangesMore() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Writer out = new OutputStreamWriter(System.out, "UTF8");
			SAXParser saxParser = factory.newSAXParser();
			// InputStream doc = new FileInputStream(REQ_FILE);
			URL changeUrl = new URL(getChangeUrl());
			MorphbankConfig.SYSTEM_LOGGER.info(changeUrl.toString());
			URLConnection connection = changeUrl.openConnection(MorphbankConfig
					.getProxy());
			InputStream doc = connection.getInputStream();
			IdListCreator idListCreator = new IdListCreator(remoteServer);
			saxParser.parse(doc, idListCreator);
			return idListCreator.getIdList();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return null;
	}


	private String getChangeUrl() {
		return remoteChangesUrlBase + numDays + "&"
		+ RequestParams.PARAM_LIMIT + "=" + limit + "&"
		+ objectTypesListParam()
		+ RequestParams.PARAM_USER + "=" + user + "&"
		+ RequestParams.PARAM_GROUP + "=" + group + "&"
		+ keywordsListParam() + "&"
		+ RequestParams.PARAM_FIRST_RESULT + "=" + firstResult;
	}

	private String objectTypesListParam() {
		String objTypes = "";
		if (objectTypes != null) {
			for (int i = 0; i < this.objectTypes.length; i++) {
				objTypes += RequestParams.PARAM_OBJECTTYPE + "=" + objectTypes[i] + "&";
			}
		}
		return objTypes;
	}

	private String keywordsListParam() {
		if (keywords != null) {
			String kwords = RequestParams.PARAM_KEYWORDS + "=";
			for (int i = 0; i < this.keywords.length; i++) {
				kwords += keywords[i] + "+";
			}
			kwords = kwords.substring(0, kwords.length() -1);
			return kwords;
		}
		return null;

	}

	protected String getChangeUrl(int numChangeDays, int limit) {
		return getChangeUrl(numChangeDays, limit, 0);
	}

	protected String getChangeUrl(int numChangeDays, int limit, int firstResult) {
		return remoteChangesUrlBase + numChangeDays + "&"
		+ RequestParams.PARAM_LIMIT + "=" + limit
		+ RequestParams.PARAM_FIRST_RESULT + "=" + firstResult;
	}

	public static void update(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		UpdateRemote updateRemote = new UpdateRemote(request, response);
		updateRemote.run();
	}

	Response getChangeResponse(int numChanges) {
		Response changeResponse = new Response();
		RequestSummary summary = new RequestSummary();
		StringBuffer description = new StringBuffer(
		"Update from remote server ");
		description.append(remoteServer).append(" number of days ").append(
				numDays).append(" limit ").append(limit).append(
				" number of changed objects ").append(numChanges);
		summary.setDescription(description.toString());
		summary.setRequestType("Remote update");
		changeResponse.setRequestSummary(summary);
		return changeResponse;
	}

	public void run() {
		responses = new Responses();
		//		List<Integer> changeIdList = getRemoteChanges(numDays, limit); //previous line
		List<Integer> changeIdList = getRemoteChangesMore();
		// XmlFileProcessor.printXml(requestOut, changeIds);
		Iterator<Integer> changeIds = changeIdList.iterator();
		Response changeResponse = getChangeResponse(changeIdList.size());
		responses.getResponse().add(changeResponse);
		while (changeIds.hasNext()) {
			int id = changeIds.next();
			Response resp = updateObject(id);
			responses.getResponse().add(resp);
		}
		// TODO check missing links
		fixLinks();
		XmlUtils.printXml(out, responses);
		out.close();
	}

	public void run(int numDays, int limit, PrintWriter out) {
		this.numDays = numDays;
		this.limit = limit;
		this.out = out;
		run();
	}


	public void run(int numDays, int limit, PrintWriter out, String[] objectTypes, int user, int group, String[] keywords) {
		this.numDays = numDays;
		this.limit = limit;
		this.out = out;
		this.objectTypes = objectTypes;
		this.user = user;
		this.group = group;
		this.keywords = keywords;
		run();
	}

	static final String getAllLinks = "select l from MissingLink l";

	public int fixLinks() {
		// open transaction
		boolean localTransaction = false;
		EntityManager em = MorphbankConfig.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			if (!tx.isActive()) {
				localTransaction = true;
				tx.begin();
			}
			int numFixed = 0;
			while (true) {
				int numFixedLoop = 0;
				Query allLinksQuery = em.createQuery(getAllLinks);
				List<?> allLinks = allLinksQuery.getResultList();
				Iterator<?> links = allLinks.iterator();
				while (links.hasNext()) {
					MissingLink link = (MissingLink) links.next();
					if (updateAndFixLink(link))
						numFixedLoop++;
				}
				if (numFixedLoop == 0)
					break;// no changes this loop
				numFixed += numFixedLoop;
			}
			// close transaction
			if (localTransaction) {
				tx.commit();
			}
			return numFixed;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Try to get the missing objects from the remote server and fix the link
	 * 
	 * @param link
	 * @return
	 */
	public boolean updateAndFixLink(MissingLink link) {
		int sourceId = link.getSourceId();
		int targetId = link.getTargetId();
		String linkType = link.getLinkType();
		String remoteDetailUrl = link.getRemoteDetailUrl();
		BaseObject source = BaseObject.getEJB3Object(sourceId);
		if (source == null && !MissingLink.OBJECT.equals(linkType))
			return false;
		BaseObject target = BaseObject.getEJB3Object(targetId);
		if (target == null) {
			// get target from the remote source
			Response response = updateObject(targetId, remoteDetailUrl);
		}
		return link.fixLink();
	}
}
