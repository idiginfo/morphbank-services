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

import java.io.IOException;
import java.util.List;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.*;
import net.morphbank.mbsvc3.request.*;
import net.morphbank.object.Group;
import net.morphbank.object.User;
import net.morphbank.search.KeywordSearch;
import net.morphbank.search.Search;
import net.morphbank.search.SearchParams;

public class ProcessQuery {

	public static final String KEYWORDS_QUERY = "keywords";
	public static final String ID_QUERY = "id";
	//KeywordSearch search = new KeywordSearch();

	private Query theQuery = null;

	public ProcessQuery() {

	}

	public ProcessQuery(Query query) {
		theQuery = query;
	}

	public Object processQuery(Credentials requestCredentials) throws IOException {
		return processQuery(requestCredentials, theQuery);
	}

	protected MapObjectToResponse responseMapper = new MapObjectToResponse("query",
			"query from xml");

	public Object processQuery(Credentials requestCredentials, Query query) throws IOException {
		RequestParams params = new RequestParams(query);
		params.setFormat(RequestParams.FORMAT_SVC); // TODO only legal format
													// for now
		// check for appropriate format (rss, xml, svc)
		if (RequestParams.FORMAT_RSS.equals(params.getFormat())) {
			// TODO rss request from xml
			return null;
		} else if (RequestParams.FORMAT_SVC.equals(params.getFormat())) {
			// mbsvc request
			MBCredentials credentials = new MBCredentials(requestCredentials);
			if (query == null) {
				return responseMapper.createResponse(credentials, "query", "no query provided");
			}
			RequestProcessor requestProcessor = new RequestProcessor(params, null);
			Object response = requestProcessor.processQuery();
			return response;
		} else if (params.getFormat().equals(RequestParams.FORMAT_RDF)) {
			// TODO handle rdf request from xml
			return null;
		}
		return null;
	}

	public Response doXmlSearch(MBCredentials credentials, String keywords, int limit,
			int firstResult, List<String> objectTypeList, boolean geolocated) {
		// lookup credentials via keyString
		// get number of results and object ids for search
		User user = null;
		Group group = null;
		if (credentials != null) {
			user = credentials.getUser();
			group = credentials.getGroup();
		}
		String[] objectTypes = null;
		if (objectTypeList != null) {
			objectTypes = new String[objectTypeList.size()];
			for (int i = 0; i < objectTypes.length; i++) {
				objectTypes[i] = objectTypeList.get(i);
			}
		}
		SearchParams params = new SearchParams();
		params.setKeywords(keywords);
		params.setObjectTypes(objectTypes);
		params.setUser(user);
		params.setGroup(group);
		params.setGeolocated(geolocated);
		KeywordSearch search = new KeywordSearch(params);

		int numResults = search.getNumResults();
		List objectIds = search.getResultIds();
		System.err.println("<!-- persistence: " + MorphbankConfig.getPersistenceUnit() + " -->");
		Response response = XmlServices.createResponse("query", "Search for keywords: " + keywords,
				numResults, objectIds.size(), firstResult, objectIds);
		return response;
	}

}
