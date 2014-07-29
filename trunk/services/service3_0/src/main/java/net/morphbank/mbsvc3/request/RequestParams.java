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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.text.*;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.*;
import net.morphbank.mbsvc3.request.*;

import net.morphbank.object.Group;
import net.morphbank.object.User;
import net.morphbank.object.UserGroupKey;
import net.morphbank.search.SearchParams;

import java.net.URLEncoder;

/**
 * Class to hold a service request
 * 
 * @author riccardi
 * 
 */
public class RequestParams extends SearchParams{

	// Parameters names
	public static final String PARAM_ID = "id";
	public static final String PARAM_METHOD = "method";
	public static final String PARAM_FORMAT = "format";
	public static final String PARAM_KEYWORDS = "keywords";
	public static final String PARAM_OBJECTTYPE = "objecttype";
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_TAXON_NAME = "taxonName";
	public static final String PARAM_FIRST_RESULT = "firstResult";
	public static final String PARAM_KEY_STRING = "keyString";
	public static final String PARAM_USER = "user";
	public static final String PARAM_GROUP = "group";
	public static final String PARAM_PASSWORD = "password";
	public static final String PARAM_CHANGE_DATE = "changeDate";
	public static final String PARAM_LAST_CHANGE_DATE = "lastChangeDate";
	public static final String PARAM_NUM_CHANGE_DAYS = "numChangeDays";
	public static final String PARAM_GEOLOCATED = "geolocated";
	public static final String PARAM_IS_PUBLIC= "isPublic";
	public static final String PARAM_IS_EXACT= "isExact";
	public static final String PARAM_HOST= "host";

	// methods
	public static final String METHOD_SEARCH = "search";
	public static final String METHOD_ID = "id";
	public static final String METHOD_TAXON = "taxon";
	public static final String METHOD_EXTERNALID = "externalId";
	public static final String METHOD_CHANGES = "changes";
	public static final String METHOD_HERBIS = "herbis";
	public static final String METHOD_OCR = "ocr";
	public static final String METHOD_USERGROUP = "usergroup";
	public static final String METHOD_EOL = "eol";
	public static final String METHOD_REMOTE = "remote";
	public static final String METHOD_SHOW = "show";
	public static final String METHOD_EXTREF = "extref";
	public static final String METHOD_MORPHSTER = "morphster";
	public static final String METHOD_FIX_UUID = "fixuuid";

	public static final String[] METHODS = { METHOD_SEARCH, METHOD_ID, METHOD_TAXON,
			METHOD_EXTERNALID, METHOD_CHANGES, METHOD_HERBIS, METHOD_OCR, METHOD_USERGROUP,
			METHOD_EOL, METHOD_REMOTE, METHOD_SHOW, METHOD_EXTREF , METHOD_MORPHSTER};

	// formats
	public static final String FORMAT_XML = "xml";
	public static final String FORMAT_RSS = "rss";
	public static final String FORMAT_ID = "id";
	public static final String FORMAT_SVC = "svc";
	public static final String FORMAT_RDF = "rdf";
	public static final String FORMAT_THUMB = "thumb";
	public static final String FORMAT_REMOTE_THUMB = "remote";
	public static final String FORMAT_JSON = "json";

	public RequestParams() {

	}

	public RequestParams(HttpServletRequest req) {
		getSearchParams(req);
	}

	public RequestParams(Query req) {
		getSearchParams(req);
	}

	// parameter names match field names exactly
	String method;
	String format;



	protected String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("encoding exception for value: '" + value + "'");
			e.printStackTrace();
			return value;
		}
	}

	protected void appendParam(StringBuffer params, String name, String value) {
		if (value != null && value.length() > 0) {
			params.append('&').append(name).append('=').append(encode(value));
		}
	}

	protected void appendParam(StringBuffer params, String name, int value) {
		if (value > 0) {
			params.append('&').append(name).append('=').append(value);
		}
	}

	protected void appendParam(StringBuffer params, String name, boolean value) {
		if (value) {
			params.append('&').append(name).append('=').append("true");
		}
	}

	protected void appendParam(StringBuffer params, String name, Date value) {
		if (value != null) {
			params.append('&').append(name).append('=')
					.append(encode(DATE_FORMATTER.format(value)));
		}
	}

	protected void appendObjectTypes(StringBuffer params, String[] value) {
		if (value != null) {
			for (int i = 0; i < value.length; i++) {
				appendParam(params, "objecttype", value[i]);
			}
		}
	}

	public String getUrlParams() {
		StringBuffer params = new StringBuffer();
		params.append(PARAM_METHOD).append("=").append(method);
		appendParam(params, PARAM_FORMAT, format);
		appendParam(params, PARAM_KEYWORDS, keywords);
		appendParam(params, PARAM_LIMIT, limit);
		if (ids.size() < 1) {
			appendParam(params, PARAM_ID, id);
		} else {
			for (int i = 0; i < ids.size(); i++) {
				appendParam(params, PARAM_ID, ids.get(i));
			}
		}
		appendObjectTypes(params, objectTypes);
		appendParam(params, PARAM_TAXON_NAME, taxonName);
		appendParam(params, PARAM_FIRST_RESULT, firstResult);
		appendParam(params, PARAM_KEY_STRING, keyString);
		appendParam(params, PARAM_USER, userId);
		appendParam(params, PARAM_GROUP, groupId);
		appendParam(params, PARAM_PASSWORD, password);
		appendParam(params, PARAM_CHANGE_DATE, changeDate);
		appendParam(params, PARAM_LAST_CHANGE_DATE, lastChangeDate);
		appendParam(params, PARAM_NUM_CHANGE_DAYS, numChangeDays);
		appendParam(params, PARAM_GEOLOCATED, geolocated);
		appendParam(params, PARAM_IS_PUBLIC, isPublic);
		appendParam(params, PARAM_IS_EXACT, isExact);
		appendParam(params,PARAM_HOST, hostServer);
		// TODO urlencode!
		return params.toString();
	}

	protected static final String DATE_STR = "05/06/2008";
	public static final DateFormat DATE_PARSER = DateFormat.getDateInstance(DateFormat.SHORT);
	public static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance();
	static final String XML_CONTENTTYPE = "text/xml";
	static final String HTML_CONTENTTYPE = "text/html";
	static final String RSS_CONTENTTYPE = "application/rss+xml";
	static final String RDF_CONTENTTYPE = "application/rdf+xml";
	static final String JSON_CONTENTTYPE = "application/json";

	public String getTaxonName() {
		return taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	protected void getSearchParams(HttpServletRequest req) {
		method = req.getParameter(PARAM_METHOD);
		setKeywords(req.getParameter(PARAM_KEYWORDS));
		keyString = req.getParameter(PARAM_KEY_STRING);
		userId = req.getParameter(PARAM_USER);
		groupId = req.getParameter(PARAM_GROUP);
		setUserGroup();
		objectTypes = req.getParameterValues(PARAM_OBJECTTYPE);
		addIds(req.getParameterValues(PARAM_ID));
		if (ids.size() > 0) id = ids.get(0); // first id
		limit = getInt(req.getParameter(PARAM_LIMIT));
		format = req.getParameter(PARAM_FORMAT);
		taxonName = req.getParameter(PARAM_TAXON_NAME);
		firstResult = getInt(req.getParameter(PARAM_FIRST_RESULT));
		changeDate = getDate(req.getParameter(PARAM_CHANGE_DATE));
		lastChangeDate = getDate(req.getParameter(PARAM_LAST_CHANGE_DATE));
		numChangeDays = getInt(req.getParameter(PARAM_NUM_CHANGE_DAYS));
		geolocated = getBoolean(req.getParameter(PARAM_GEOLOCATED));
		isPublic = getBoolean(req.getParameter(PARAM_IS_PUBLIC));
		isExact = getBoolean(req.getParameter(PARAM_IS_EXACT));
		hostServer = req.getParameter(PARAM_HOST);
	}
	
	protected int getInt(String intString) {
		try {
			return Integer.parseInt(intString);
		} catch (Exception e) {
			return 0;
		}
	}

	protected boolean getBoolean(String boolString) {
		try {
			boolean value = Boolean.parseBoolean(boolString);
			// MorphbankConfig.SYSTEM_LOGGER.info("Boolean conversion for '" +
			// boolString + "' "
			// + value);
			return value;
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER
					.info("Boolean conversion failed for '" + boolString + "'");
			e.printStackTrace();
			return false;
		}
	}

	public static Date getDate(String dateString) {
		if (dateString == null || dateString.length() < 1) {
			return null;
		}
		try {
			return DATE_PARSER.parse(dateString);
		} catch (Exception e) {
			return null;
		}
	}

	protected int getInt(Integer temp) {
		if (temp == null) return 0;
		return temp.intValue();
	}

	protected boolean getBoolean(Boolean temp) {
		if (temp == null) return false;
		return temp.booleanValue();
	}

	protected void getSearchParams(Query req) {
		method = req.getMethod();
		setKeywords(req.getKeywords());
		if (req.getSubmitter() != null) {
			keyString = req.getSubmitter().getKeyString();
			userId = Integer.toString(req.getSubmitter().getUserId());
			groupId = Integer.toString(req.getSubmitter().getGroupId());
		} else {
			keyString = null;
			userId = null;
			groupId = null;
		}
		setUserGroup();
		objectTypes = req.getObjectTypes();
		addId(req.getId());
		limit = getInt(req.getLimit());
		format = req.getFormat();
		taxonName = req.getTaxonName();
		firstResult = getInt(req.getFirstResult());
		changeDate = req.getChangeDate();
		// TODO add last change date and num change days
		lastChangeDate = req.getLastChangeDate();
		numChangeDays = getInt(req.getNumChangeDays());
		geolocated = getBoolean(req.getGeolocated());
		isPublic = req.isPublic();
		isExact = req.isExact();
		hostServer = req.getHostServer();
	}

	public String getHttpResponseType() {
		// special cases of remote requests that don't use format parameter
		if (METHOD_REMOTE.equals(method)) return HTML_CONTENTTYPE;
		if (METHOD_SHOW.equals(method)) return HTML_CONTENTTYPE;

		if (format == null) return XML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_ID)) return XML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_RDF)) return RDF_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_RSS)) return RSS_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_SVC)) return XML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_THUMB)) return HTML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_REMOTE_THUMB)) return HTML_CONTENTTYPE;
		if (format.equals(RequestParams.FORMAT_JSON)) return JSON_CONTENTTYPE;

		return HTML_CONTENTTYPE;
	}

	// initialization parameters
	static final String CONFIG = MorphbankConfig.PERSISTENCE_MBPROD;
	static final String CONFIGx = MorphbankConfig.PERSISTENCE_MBDEV;
	static final String FILEPATH = "c:/dev/morphbank/websvcfiles/";
	static final String SERVICE = MorphbankConfig.SERVICES;
	static final String CONFIG_PARAM = "persistence";
	static final String METHOD = "method";
	static final String UPDATE_METHOD = "update";
	static final String FILEPATH_PARAM = "filepath";
	static final String SERVICE_PARAM = "serviceprefix";
	static final String WEB_SERVER_PARAM = "webserver";
	static final String IMAGE_SERVER_PARAM = "imageserver";
	static final String REMOTE_SERVER_PARAM = "remoteserver";
	static final String PROXY_SERVER_PARAM = "proxyserver";
	static final String PROXY_PORT_PARAM = "proxyport";
	static final String LOG_FILE_PARAM = "logfile";

	public static void initService(ServletConfig config) throws ServletException {
		// setup persistence unit from parameter, if available
		String persistence = config.getInitParameter(CONFIG_PARAM);
		MorphbankConfig.SYSTEM_LOGGER.info("Persistence: " + persistence);
		if (persistence != null) {
			MorphbankConfig.setPersistenceUnit(persistence);
		} else {
			MorphbankConfig.setPersistenceUnit(CONFIG);
		}
		String servicePrefix = config.getInitParameter(SERVICE_PARAM);
		if (servicePrefix != null) {
			MorphbankConfig.setServicePrefix(servicePrefix);
		} else {
			MorphbankConfig.setServicePrefix(SERVICE);
		}
		String webServerPrefix = config.getInitParameter(WEB_SERVER_PARAM);
		if (webServerPrefix != null) {
			MorphbankConfig.setWebServer(webServerPrefix);
		}
		String imageServerPrefix = config.getInitParameter(IMAGE_SERVER_PARAM);
		if (imageServerPrefix != null) {
			MorphbankConfig.setImageServer(imageServerPrefix);
		}
		String remoteServerPrefix = config.getInitParameter(REMOTE_SERVER_PARAM);
		if (remoteServerPrefix != null) {
			MorphbankConfig.setRemoteServer(remoteServerPrefix);
		}
		String proxyServer = config.getInitParameter(PROXY_SERVER_PARAM);
		String proxyPort = config.getInitParameter(PROXY_PORT_PARAM);
		if (proxyServer != null) {
			MorphbankConfig.setupProxy(proxyServer, proxyPort);
		}
		String filepath = config.getInitParameter(FILEPATH_PARAM);
		if (filepath != null) {
			MorphbankConfig.setFILEPATH(filepath);
		}
		String logFileName = config.getInitParameter(LOG_FILE_PARAM);
		MorphbankConfig.createLogHandler(logFileName);

		MorphbankConfig.renewEntityManagerFactory();
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}


}
