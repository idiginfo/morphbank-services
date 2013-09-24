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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.xml.Credentials;
import net.morphbank.mbsvc3.xml.ObjectList;
import net.morphbank.mbsvc3.xml.RequestSummary;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Group;
import net.morphbank.object.User;

/**
 * @author riccardi
 * 
 *         Main class for query services for Morphbank Queries can be created
 *         from mbsvc.xsd or from an HTTP request with parameters RequestParams
 *         carries the parameters A param object can be created from a Query
 *         (net.morphbank.mbservices.Query) see
 *         net.morphbank.mbobjservices.ProcessQuery
 */
public class RemoteRequestProcessor {
	static final long serialVersionUID = 1;

	static final String BAD_FORMAT = "<html>bad format</html>";
	// TODO move the remote service to a servlet parameter
	public static String REMOTE_SERVICE = MorphbankConfig.getRemoteServer() + "request?";

	HttpServletRequest req = null;
	HttpServletResponse resp = null;
	PrintWriter out = null;
	MorphbankPage page = null;
	RequestParams params = null;

	public RemoteRequestProcessor(HttpServletRequest req, HttpServletResponse resp,
			RequestParams params, PrintWriter out, MorphbankPage page) {
		this.req = req;
		this.resp = resp;
		this.params = params;
		this.out = out;
		this.page = page;
		if (out == null) this.out = new PrintWriter(System.out);
		if (page == null) this.page = new MorphbankPage(this.out);
	}

	public Object doFetchAndShow(PrintWriter out) {
		// TODO Auto-generated method stub
		// validate request
		int id = params.getId();
		if (id == 0) {// bad request

		}
		String showUrl = MorphbankConfig.getURL(id);
		// check for local object
		Object obj = ensureObjectInDatabase(id);
		if (obj != null) {
			// redirect to Web server
			if (resp != null) {
				resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
				resp.setHeader("Location", showUrl);
			} else {
				out.println(showUrl);
			}
		}
		return obj;
	}

	private boolean ensureUsersAndGroups(ObjectList objectList) {
		// TODO Auto-generated method stub\
		boolean success = false;
		success = ensureCredentials(objectList.getSubmitter());
		// loop through objects and check for all users and groups
		Iterator<Object> objects = objectList.getObjects().iterator();
		while (objects.hasNext()) {
			XmlBaseObject obj = (XmlBaseObject) objects.next();
			success &= ensureCredentials(obj.getOwner());
			success &= ensureCredentials(obj.getSubmittedBy());
		}
		return success;
	}

	private BaseObject ensureObjectInDatabase(int id) {
		BaseObject obj = BaseObject.getEJB3Object(id);
		if (obj != null) return obj;
		try {
			// fetch object
			String fetchUrlStr = getIdRequestUrl(id);
			MorphbankConfig.SYSTEM_LOGGER.info("remote fetch url: " + fetchUrlStr);
			URL fetchUrl = new URL(fetchUrlStr);
			// store object
			Object requestDoc = XmlUtils.getXmlObjectList(fetchUrl);
			// insert object
			// open transaction
			if (requestDoc instanceof Response) {
				Response response = (Response) requestDoc;
				XmlBaseObject xmlObj = (XmlBaseObject) response.getObjects().get(0);
				ProcessRequest processor = new ProcessRequest(null);
				ensureUsersAndGroups(response);
				Response results = processor.processInsert(response);
				// check on status of results
				obj = BaseObject.getEJB3Object(id);
				return obj;
			}
			// redirect to Web server
			//
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * Make sure that the user and group are in the database
	 * 
	 * @param credentials
	 * @return
	 */
	private boolean ensureCredentials(Credentials credentials) {
		if (credentials == null) return true;
		int ownerId = credentials.getUserId();
		boolean success = ensureUser(ownerId);
		int groupId = credentials.getGroupId();
		success &= ensureGroup(groupId);
		return success;
	}

	/**
	 * Check to for presence of group in database If not found, fetch and load
	 * 
	 * @param groupId
	 */
	private boolean ensureGroup(int groupId) {
		// TODO Auto-generated method stub
		BaseObject obj = BaseObject.getEJB3Object(groupId);
		if (obj != null) {
			if (obj instanceof Group) return true;
			return false;
		}
		obj = ensureObjectInDatabase(groupId);
		if (obj != null && obj instanceof Group) return true;
		return false;
	}

	/**
	 * Check to for presence of user in database If not found, fetch and load
	 * 
	 * @param ownerId
	 */
	private boolean ensureUser(int userId) {
		// TODO Auto-generated method stub
		BaseObject obj = BaseObject.getEJB3Object(userId);
		if (obj != null) {
			if (obj instanceof User) return true;
			return false;
		}
		obj = ensureObjectInDatabase(userId);
		if (obj != null && obj instanceof User) return true;
		return false;
	}

	public String getIdRequestUrl(int id) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_ID);
		params.setFormat(RequestParams.FORMAT_SVC);
		params.setId(id);
		return REMOTE_SERVICE + params.getUrlParams();
	}

	/**
	 * Call the central service for objects
	 * 
	 * @param out
	 * @return
	 */
	public Object doRemoteSearch(PrintWriter out) {
		// make new parameters from the original request 
		RequestParams requestParams = new RequestParams(req);
		requestParams.setMethod(RequestParams.METHOD_SEARCH);
		requestParams.setFormat(RequestParams.FORMAT_SVC);
		String remoteReqUrl = REMOTE_SERVICE + requestParams.getUrlParams();
		requestParams.setFormat(RequestParams.FORMAT_RSS);
		String rssFeed = REMOTE_SERVICE + requestParams.getUrlParams();
		MorphbankConfig.SYSTEM_LOGGER.info("Remote url is " + remoteReqUrl);
		Response remoteResponseDoc = null;
		try {
			URL remoteRequest = new URL(remoteReqUrl);
			// test
			// InputStream in = remoteRequest.openStream();
			// //out.println(in);
			// char c;
			// while ((c = (char) in.read()) >= 0) {
			// if (c <= 0) break;
			// out.append(c);
			// }

			Object remoteResponse = XmlUtils.getXmlObjectList(remoteRequest);
			if (remoteResponse instanceof Response) {
				remoteResponseDoc = (Response) remoteResponse;
				doThumbnailResponse(remoteResponseDoc, rssFeed);
				return remoteResponseDoc;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void doThumbnailResponse(Response remoteResponseDoc, String rssFeed) {
		RequestSummary summary = remoteResponseDoc.getRequestSummary();
		String desc = summary.getDescription();
		Integer numMatchesInt = remoteResponseDoc.getNumMatches();
		int numMatches = (numMatchesInt != null ? numMatchesInt : 0);
		Integer numReturnedInt = remoteResponseDoc.getNumReturned();
		int numReturned = (numReturnedInt != null ? numReturnedInt : 0);
		Integer firstReturnedInt = remoteResponseDoc.getFirstReturned();
		int firstReturned = (firstReturnedInt != null ? firstReturnedInt : 0);
		MorphbankConfig.SYSTEM_LOGGER.info("response type for remote: " + params.getHttpResponseType());
		if (resp != null) resp.setContentType(params.getHttpResponseType());
		page.printThumbHeader("Search", desc, numMatches, numReturned, firstReturned, rssFeed);
		// print objects(baseUrl, baseImageUrl)
		XmlBaseObject xmlObj = null;
		Iterator objects = remoteResponseDoc.getObjects().iterator();
		while (objects.hasNext()) {
			xmlObj = (XmlBaseObject) objects.next();
			page.printThumbnail(xmlObj, remoteResponseDoc);
		}
		page.printThumbFooter();
	}

}
