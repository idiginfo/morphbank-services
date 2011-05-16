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
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

public class GetRDF {

	static String URL = "http://www.morphbank.net/142837";

	public static void main (String[] args) throws Exception {
		GetRDF getRdf = new GetRDF();
		getRdf.post(URL);
	}

	public void post(String strURL) throws Exception {
		// Prepare HTTP post
		GetMethod get = new GetMethod(strURL);
		get.setRequestHeader("Accept", "application/rdf+xml");
		// Request content will be retrieved directly
		HttpClient httpclient = new HttpClient();
		// Execute request
		try {
			System.out.println("Trying post");
			int result = httpclient.executeMethod(get);
			// Display status code
			System.out.println("Response status code: " + result);
			// Display response
			System.out.println("Response body: ");
			InputStream response = get.getResponseBodyAsStream();
			//int j = response.read(); System.out.write(j);
			for (int i = response.read(); i!= -1; i = response.read()){
				System.out.write(i);
			}
			//System.out.flush();
		} finally {
			// Release current connection to the connection pool once you are
			// done
			get.releaseConnection();
		}
	}
}
