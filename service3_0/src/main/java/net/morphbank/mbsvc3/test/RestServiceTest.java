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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import net.morphbank.mbsvc3.webservices.RestService;

public class RestServiceTest {

	//static String UPLOAD_FILE = "C:/dev/morphbank/spiderfiles/spiders2sample.xml";
	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/CTOLOct270000.xml";
	static String URL = "http://localhost:8080/mbd/restful";
//	static String URL = "http://services.morphbank.net/mbd/restful";
	
	RestService restService = new RestService();

	public static void main (String[] args) throws Exception {
		RestServiceTest restTest= new RestServiceTest();
		restTest.processRequest(URL, UPLOAD_FILE);
	}

	public void processRequest(String strURL, String strXMLFilename) throws Exception {
		InputStream in = new FileInputStream(strXMLFilename);
		//TODO fix this service to initialize properly
		//restService.initObject();
		System.out.println(restService.getNextReqFileNumber());
		System.out.println(restService.saveRequestXmlAsFile(null));
		PrintWriter out = new PrintWriter(System.out);
		//restService.processRequest(in, out, strXMLFilename);
		in.close();
		out.close();
	}
}
