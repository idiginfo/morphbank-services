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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;

public class XmlRequestTest {

	public static final int RICCARDI_ID = 78271;
	public static final int RICCARDI_GROUP_ID = 78272;
//	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_LOCALHOST;
	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBDEV;
//	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBPROD;
//	public static final String PERSISTENCE = "ala";
	static String UPLOAD_PKG = "net.morphbank.services";
	static String REQ_PKG = "net.morphbank.services";
	// static String UPLOAD_FILE = "C:/dev/morphbank/specimeninsert.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/spider/ramirez0000.xml";
	//static String UPLOAD_FILE = "c:/dev/morphbank/ala/ala_gvc_01.xml";
	//static String UPLOAD_FILE = "c:/dev/morphbank/scamit/scamit01.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/specifyimport/Specify-000039894_1b.xml";
//	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/toprocess/ANICSATSCANv2.xml";
//	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/toprocess/TTRS_v8.xml";
//	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/TTRS_18Feb_subsetTestViewInsert.xml";
	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/USMS/test/troymbsubmission_0001-test.xml";
//	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/CTOLOct270000.xml";
//	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/toprocess/CinclosomaV4.xml";
//	static String UPLOAD_FILE = "/home/gjimenez/downloads/fsuherbxml/fixed/req453.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/specifyimport/req322.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/specifyimport/req349.xml";
	////static String UPLOAD_FILE = "C:/dev/morphbank/ctol/fishsample.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/CToL/xmlfiles/ctolJune0000.xml";
	// static String UPLOAD_FILE = "C:/dev/morphbank/spiderfiles/spidersupdate0000.xml";
	// static String UPLOAD_FILE = "C:/dev/morphbank/spidermbsvc.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/querysample1.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/spider/xmlfiles/newSpider0002.xml";
	//static String XML_OUTPUT_FILE = "C:/dev/morphbank/CToL/xmlfiles/ctolJuneResp.xml";
	//static final String XML_OUTPUT_FILE =  "C:/dev/morphbank/spider/ramirezresp0000.xml";
	//static final String XML_OUTPUT_FILE =  "C:/dev/morphbank/spider/xmlfiles/new0070response.xml";
//	static final String XML_OUTPUT_FILE =  "/home/gjimenez/downloads/response453.xml";
//	static final String XML_OUTPUT_FILE =  "/home/gjimenez/downloads/CinclosomaV2.xml";
	static final String XML_OUTPUT_FILE =  UPLOAD_FILE.substring(0, UPLOAD_FILE.length()-4) + "-output.xml";
//	static final String XML_OUTPUT_FILE =  "C:/dev/morphbank/fsuherb/xmlfiles/response.xml";
	//static final String XML_OUTPUT_FILE =  "C:/dev/morphbank/specifyimport/resp322.xml";

	// static String UPLOAD_FILE = "C:/dev/morphbank/insertdoc.xml";
	// static final String XML_OUTPUT_FILE = "C:/dev/morphbank/insertdocresponse.xml";
	static String UPLOAD_URL = "http://morphbank.net/schema/reqsample.xml";
	static boolean FILE = true;
	// static final String XML_OUTPUT_FILE =
	// "c:/dev/morphbank/spiderprod0results.xml";
	static final String REPORT_FILE = "/usr/local/dev/morphbank/spiderviewreport.txt";

	Request request = null;
	Responses responses = new Responses();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MorphbankConfig.setPersistenceUnit(PERSISTENCE);
		MorphbankConfig.init();

		XmlRequestTest requestTest = new XmlRequestTest(args);
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

	public XmlRequestTest(String[] args) {
	}

	public void run(boolean file, String requestName, String xmlOutputFile) {
		// createXML();
		try {
			System.out.println("running for file: "+requestName);
			InputStream in = new FileInputStream(requestName);
			Object xmlObject = XmlUtils.getXmlObject(in);
			request = (Request) xmlObject;
			ProcessRequest process = new ProcessRequest(request);
			Responses responses = process.processRequest();
			FileWriter outFile = new FileWriter(xmlOutputFile);
			PrintWriter out = new PrintWriter(outFile);
			XmlUtils.printXml(out, responses);
			outFile.close();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
