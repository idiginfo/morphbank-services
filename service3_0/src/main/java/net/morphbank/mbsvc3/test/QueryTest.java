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
package net.morphbank.mbsvc3.test;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.mbsvc3.request.RequestProcessor;

public class QueryTest {
	static String UPLOAD_PKG = "net.morphbank.services";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		MorphbankConfig
		// .setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBPROD);
//				 .setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBDEV);
				 .setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
				//.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
		MorphbankConfig.init();

		QueryTest request = new QueryTest(args);
		request.run();
	}

	public QueryTest(String[] args) {
	}

	RequestParams getMorphster(int id) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_MORPHSTER);
		params.setId(id);
//		params.setFormat(RequestParams.F)
		return params;
	}
	
	RequestParams getImageRequest(String keywords) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_SEARCH);
		params.setFormat(RequestParams.FORMAT_RDF);
		params.setLimit(5);
		params.setKeywords(keywords);
		// params.setUserId("PlantCollections");
		params.setGeolocated(true);
		//params.setPublic(true);
		// params.setGroupId("227020");
		// params.setUserGroup();
		String[] objectTypes = new String[] { "Specimen" };
		params.setObjectTypes(objectTypes);
		return params;
	}

	RequestParams getRemoteRequest(String keywords) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_REMOTE);
		params.setFormat(RequestParams.FORMAT_REMOTE_THUMB);
		params.setKeywords(keywords);
		// params.setUserId("PlantCollections");
		params.setGeolocated(false);
		params.setPublic(true);
		// params.setGroupId("227020");
		// params.setUserGroup();
		String[] objectTypes = new String[] { "Image" };
		params.setObjectTypes(objectTypes);
		return params;
	}

	RequestParams getRemoteShowRequest(int id) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_SHOW);
		params.setId(id);
		return params;
	}

	RequestParams getTaxonRequest(String taxonName) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_TAXON);
		params.setFormat(RequestParams.FORMAT_SVC);
		params.setTaxonName(taxonName);
		String[] objectTypes = new String[] { "Specimen" };
		params.setObjectTypes(objectTypes);
		return params;
	}

	RequestParams getIdRequest(int id) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_ID);
		params.setFormat(RequestParams.FORMAT_SVC);
		params.addId(id);
		return params;
	}

	RequestParams getExtIdRequest(String extId) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_EXTERNALID);
		params.setFormat(RequestParams.FORMAT_ID);
		params.setKeywords(extId);
		return params;
	}

	RequestParams getShowIdRequest(int id) {
		MorphbankConfig.setWebServer("http://localhost/www");
		MorphbankConfig.setImageServer("http://localhost/ImageServer/");
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_SHOW);
		params.setFormat(RequestParams.FORMAT_SVC);
		params.addId(id);
		return params;
	}

	RequestParams getIdRequest(String[] idStrs) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_ID);
		params.setFormat(RequestParams.FORMAT_RDF);
		params.setLimit(-1);
		params.addIds(idStrs);
		return params;
	}

	RequestParams getChangeRequest(Date changeDate, Date lastChangeDate, String format) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_CHANGES);
		params.setFormat(format);
		String[] objectTypes = { "Image", "Specimen" };
		params.setObjectTypes(objectTypes);
		if (changeDate != null) {
			params.setChangeDate(changeDate);
		}
		if (lastChangeDate != null) {
			params.setLastChangeDate(lastChangeDate);
		}
		params.setNumChangeDays(2);
		params.setLimit(10);
		params.setFirstResult(0);
		params.setHostServer("localhost");
		return params;
	}

	RequestParams getChangeRequest(int numChangeDays, String format) {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_CHANGES);
		params.setFormat(format);
		// params.setGroupId("2");
		params.setNumChangeDays(numChangeDays);
		params.setPublic(false);
		// String[] objectTypes = {"Image", "Specimen"};
		// params.setObjectTypes(objectTypes);
		params.setLimit(10);
		params.setFirstResult(0);
		return params;
	}

	static Calendar now = Calendar.getInstance();
	public static final DateFormat DATE_FORMATTER = DateFormat.getDateInstance(DateFormat.SHORT);

	Calendar getThisMorning() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today;
	}

	RequestParams getEolRequest() {
		RequestParams params = new RequestParams();
		params.setMethod(RequestParams.METHOD_EOL);
		params.setFormat(RequestParams.FORMAT_ID);
		params.setLimit(10);
		params.setFirstResult(0);
		return params;
	}

	public void run() throws Exception {
		// String xmlOutputFile = XML_OUTPUT_FILE;
		PrintWriter out = new PrintWriter(System.out);
		RequestParams params = null;
//		params = getImageRequest("alaska");
		params = getTaxonRequest("Lycopodiella alopecuroides");
		//params = getMorphster(579700);
		// params = getRemoteRequest("ramirez");
		// params = getRemoteShowRequest(514513);
		// params = getImageRequest("");
		String[] idStrs = {
//				"2003509"
		// "480578",
				// "140443",
//				"1", // user
//				// "2", // group
				"110001", // image
//				// "102993" , //annotation
//				// "365833", // determination annotation
//				// "227103", //taxonconcept
				"109991", // specimen
		// "109949", // view
		// "135712", //publication
		// "64010" //locality
		};
		 //params = getIdRequest(idStrs);
		//params = getIdRequest(idStrs);
		// params = getExtIdRequest("SBCD");
		// params = getShowIdRequest(480711);
		// params = getIdRequest(1);
		// params = getIdRequest(216481);
		// params = getIdRequest(102990);
		// params = getTaxonRequest(null);
		// Date lastWeek = DATE_FORMATTER.parse("2/12/2008");
		// Date earlier = DATE_FORMATTER.parse("2/13/2008");
		// params = getChangeRequest(lastWeek, earlier,
		// RequestParams.FORMAT_ID);
		Date thisMorning = RequestParams.getDate("6/13/2008");
//		 params = getChangeRequest(thisMorning, null,
//		 RequestParams.FORMAT_ID);
		// params = getEolRequest();
		// params = getChangeRequest(10, RequestParams.FORMAT_ID);

		RequestProcessor requestProcessor = new RequestProcessor(params, out);
		requestProcessor.processQuery();
		System.out.println();
		System.out.println(out);
		out.close();
	}
}
