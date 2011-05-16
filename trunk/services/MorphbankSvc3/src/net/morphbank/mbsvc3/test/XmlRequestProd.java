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

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLConnection;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;

public class XmlRequestProd {

	public static final int RICCARDI_ID = 78271;
	public static final int RICCARDI_GROUP_ID = 78272;
	// public static final String PERSISTENCE =
	// MorphbankConfig.PERSISTENCE_LOCALHOST;
	// public static final String PERSISTENCE =
	// MorphbankConfig.PERSISTENCE_MBDEV;
	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBPROD;
	static String REQ_PKG = "net.morphbank.mbservices";
	// static String UPLOAD_FILE = "C:/dev/morphbank/specimeninsert.xml";
	// static String UPLOAD_FILE =
	// "C:/dev/morphbank/spider/fullspiderviews.xml";
	// static String UPLOAD_FILE = "C:/dev/morphbank/ctol/fishsample.xml";
	static String UPLOAD_FILE = "C:/dev/morphbank/CToL/xmlfiles/ctol0000.xml";
	// static String UPLOAD_FILE =
	// "C:/dev/morphbank/spiderfiles/spidersupdate0000.xml";
	// static String UPLOAD_FILE = "C:/dev/morphbank/spidermbsvc.xml";
	// static String UPLOAD_FILE = "C:/dev/morphbank/querysample.xml";
	// static String UPLOAD_FILE =
	// "C:/dev/morphbank/spider/xmlfiles/newSpider0002.xml";
	static String XML_OUTPUT_FILE = "C:/dev/morphbank/CToL/xmlfiles/ctol0000resp.xml";
	// static final String XML_OUTPUT_FILE =
	// "C:/dev/morphbank/querysampleresponse.xml";
	// static final String XML_OUTPUT_FILE =
	// "C:/dev/morphbank/spider/xmlfiles/new0002response.xml";
	// static String UPLOAD_FILE = "C:/dev/morphbank/insertdoc.xml";
	// static final String XML_OUTPUT_FILE =
	// "C:/dev/morphbank/insertdocresponse.xml";
	static String UPLOAD_URL = "http://morphbank.net/schema/reqsample.xml";
	static boolean FILE = true;
	// static final String XML_OUTPUT_FILE =
	// "c:/dev/morphbank/spiderprod0results.xml";
	static final String REPORT_FILE = "c:/dev/morphbank/spiderviewreport.txt";

	Request request = null;
	Responses responses = new Responses();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MorphbankConfig.setPersistenceUnit(PERSISTENCE);
		MorphbankConfig.init();

		XmlRequestProd requestTest = new XmlRequestProd(args);
		String xmlOutputFile = XML_OUTPUT_FILE;
		String requestFile = UPLOAD_FILE;
		boolean file = FILE;
		if (!file) {
			requestFile = UPLOAD_URL;
		}
		if (args.length > 0) {
			requestFile = args[0];
		}
		if (args.length > 1) {
			file = args[1].equals("true");
		}
		if (args.length > 2) {
			xmlOutputFile = args[2];
		}
		requestTest.run(file, requestFile, xmlOutputFile);
	}

	public XmlRequestProd(String[] args) {
	}

	public void run(boolean file, String requestName, String xmlOutputFile) {
		// createXML();
		try {
			Object xmlObject = getXml(file, requestName);
			request = (Request) xmlObject;
			ProcessRequest process = new ProcessRequest(request);
			Responses responses = process.processRequest();
			FileWriter outFile = new FileWriter(xmlOutputFile);
			PrintWriter out = new PrintWriter(outFile);
			outFile.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Request getRequest(boolean file, String requestName) {
		Object obj = getXml(file, requestName);
		if (obj instanceof Request) {
			return (Request) request;
		} else {
			return null;
		}
	}

	public Request getRequest(boolean file, String requestName, Unmarshaller unmarshaller)
			throws JAXBException {

		Object obj = getXml(file, requestName, unmarshaller);
		if (obj instanceof Request) {
			return (Request) request;
		} else {
			return null;
		}
	}

	public Object getXml(boolean isFile, String requestName) {
		Unmarshaller unmarshaller = XmlUtils.getUnmarshaller();
		try {
			Object obj = getXml(isFile, requestName, unmarshaller);
			return obj;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getXml(boolean isFile, String requestName, Unmarshaller unmarshaller)
			throws JAXBException {
		if (!isFile) { // read sample xml from URL
			java.net.URL requestAccess = null;
			try {
				requestAccess = new java.net.URL(requestName);
				URLConnection connection = requestAccess.openConnection(MorphbankConfig.getProxy());
				InputStream in = connection.getInputStream();
				Request request = (Request) unmarshaller.unmarshal(in);
				in.close();
				return request;
			} catch (Exception e) {
				// URL did not produce proper xml, use file instead
			}
		}
		// read sample xml from file
		Object request = unmarshaller.unmarshal(new File(requestName));
		return request;
	}
}
