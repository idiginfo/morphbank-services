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
package net.morphbank.webclient;

import java.io.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

public class PostXML {

	//static String UPLOAD_FILE = "C:/dev/morphbank/spiderfiles/spiders2sample.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/CToL/xmlfiles/ctol0007.xml";
	//static String UPLOAD_FILE = "C:/dev/morphbank/specifyimport/Specify-000030850.xml";
	static String UPLOAD_FILE = "/usr/local/dev/morphbank/upload/CTOLOct270000.xml";
	//static String UPLOAD_FILE = "c:/dev/morphbank/scamit/testinsert.xml";
	static String URL = "http://localhost:8080/mbd/restful";
//	static String URL = "http://services.morphbank.net/mbd/restful";

	public static void main (String[] args) throws Exception {
		PostXML postXML = new PostXML();
		String uploadFile = UPLOAD_FILE;
		if (args.length>0) uploadFile = args[0];
		else {
			//System.out.println("Usage: java net.morphbank.webclient.PostXML [filepath] ");
		}
		postXML.post(URL, uploadFile);
	}

	public void post(String strURL, String strXMLFilename) throws Exception {
		File input = new File(strXMLFilename);
		// Prepare HTTP post
		PostMethod post = new PostMethod(strURL);
		// Request content will be retrieved directly
		// from the input stream Part[] parts = {
		Part[] parts = { new FilePart("uploadFile", strXMLFilename, input) };
		RequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		// RequestEntity entity = new FileRequestEntity(input,
		// "text/xml;charset=utf-8");
		post.setRequestEntity(entity);
		// Get HTTP client
		HttpClient httpclient = new HttpClient();
		// Execute request
		try {
			System.out.println("Trying post");
			int result = httpclient.executeMethod(post);
			// Display status code
			System.out.println("Response status code: " + result);
			// Display response
			System.out.println("Response body: ");
			InputStream response = post.getResponseBodyAsStream();
			//int j = response.read(); System.out.write(j);
			for (int i = response.read(); i!= -1; i = response.read()){
				System.out.write(i);
			}
			//System.out.flush();
		} finally {
			// Release current connection to the connection pool once you are
			// done
			post.releaseConnection();
		}
	}
}
