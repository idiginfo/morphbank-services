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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import net.morphbank.mbsvc3.webservices.RestService;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;


public class ProcessFiles {

	static String URL = "http://services.morphbank.net/mbd/restful";
	static String FILE_IN_PATH = "c:/data/scratch/filenum.txt";

	RestService restService = new RestService();

	/**
	 * @param args
	 *            args[0] directory including terminal '/' 
	 *            args[1] prefix of request files 
	 *            args[2] number of digits in file index value
	 *            args[3] number of files 
	 *            args[4] index of first file (default 0) 
	 *            args[5] prefix of service 
	 *            args[6] prefix of response files (default args[1] + "Resp")
	 */
	public static void main(String[] args) {
		ProcessFiles fileProcessor = new ProcessFiles();
		// restTest.processRequest(URL, UPLOAD_FILE);

		String zeros = "0000000";

		if (args.length < 4) {
			System.out.println("Too few parameters");
		} else {
			try {
				// get parameters
				String reqDir = args[0];
				String reqPrefix = args[1];
				int numDigits = Integer.valueOf(args[2]);
				if (numDigits > zeros.length()) numDigits = zeros.length();
				NumberFormat intFormat = new DecimalFormat(zeros.substring(0, numDigits));
				int numFiles = Integer.valueOf(args[3]);
				int firstFile = 0;

				BufferedReader fileIn = new BufferedReader(new FileReader(FILE_IN_PATH));
				String line = fileIn.readLine();
				fileIn.close();
				firstFile = Integer.valueOf(line);
				// firstFile = 189;
				// numFiles = 1;
				int lastFile = firstFile + numFiles - 1;
				String url = URL;
				String respPrefix = reqPrefix + "Resp";
				if (args.length > 5) respPrefix = args[5];
				if (args.length > 6) url = args[6];

				// process files
				for (int i = firstFile; i <= lastFile; i++) {
					String xmlOutputFile = null;
					String requestFile = reqDir + reqPrefix + intFormat.format(i) + ".xml";
					System.out.println("Processing request file " + requestFile);
					String responseFile = reqDir + respPrefix + intFormat.format(i) + ".xml";
					System.out.println("Response file " + responseFile);
					// restTest.processRequest(URL, UPLOAD_FILE);
					fileProcessor.processRequest(url, requestFile, responseFile);
					Writer fileOut = new FileWriter(FILE_IN_PATH, false);
					fileOut.append(Integer.toString(i+1));
					fileOut.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void processRequest(String url, String xmlFilename, String responseFilename)
			throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter(responseFilename));
		InputStream response = post(url, xmlFilename, out);
		out.close();
	}

	public InputStream post(String strURL, String strXMLFilename, PrintWriter out) throws Exception {
		InputStream response = null;
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
			int result = httpclient.executeMethod(post);
			// Display status code
			System.out.println("Response status code: " + result);
			// Display response
			response = post.getResponseBodyAsStream();
			for (int i = response.read(); i != -1; i = response.read()) {
				out.write(i);
			}
		} finally {
			// Release current connection to the connection pool once you are
			// done
			post.releaseConnection();
		}
		return response;
	}
}
