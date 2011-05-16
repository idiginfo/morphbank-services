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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;
/**
 * Process a collection of XML request files 
 * The names of the request files follow a pattern of a prefix plus an index of fixed length
 *  e.g. req0000.xml, req0002.xml 
 *  
 * @author riccardi
 *
 *Main method expects at least 4 parameters
 *[0] request directory
 *[1] request prefix
 *[2] number of digits in the numeric sequence
 *[3] number of files
 *optional parameters
 *[4] index of first file: default is 0
 *[5] JPA persistence context: default is "morphbank" 
 *[6] response prefix: default is [request prefix] + "resp"
 */
public class ProcessRequestFiles {

	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBPROD;
	static String REQ_PKG = "net.morphbank.mbservices";

	Request request = null;
	Responses responses = new Responses();

	/**
	 * @param args
	 *            args[0] directory including terminal '/' 
	 *            args[1] prefix of  request files 
	 *            args[2] number of digits in file index value
	 *            args[3] number of files 
	 *            args[4] index of first file (default 0) 
	 *            args[5] database configuration (default localhost)
	 *            args[6] prefix of response files (default args[1] + "Resp"
	 */
	public static void main(String[] args) {

		String zeros = "0000000";

		if (args.length < 4) {
			MorphbankConfig.SYSTEM_LOGGER.info("Too few parameters");
		} else {
			try {
				// get parameters
				String reqDir = args[0];
				String reqPrefix = args[1];
				int numDigits = Integer.valueOf(args[2]);
				if (numDigits > zeros.length()) numDigits = zeros.length();
				NumberFormat intFormat = new DecimalFormat(zeros.substring(0,
						numDigits));
				int numFiles = Integer.valueOf(args[3]);
				int firstFile = 0;

				if (args.length > 4) {
					firstFile = Integer.valueOf(args[4]);
				}
				int lastFile = firstFile + numFiles - 1;
				String persistence = PERSISTENCE;
				if (args.length > 5) persistence = args[5];
				String respPrefix = reqPrefix + "Resp";
				if (args.length > 6) respPrefix = args[6];

				MorphbankConfig.setPersistenceUnit(persistence);
				MorphbankConfig.init();
				// process files
				for (int i = firstFile; i <= lastFile; i++) {
					ProcessRequestFiles requestTest = new ProcessRequestFiles();
					String xmlOutputFile = null;
					String requestFile = reqDir + reqPrefix
							+ intFormat.format(i) + ".xml";
					MorphbankConfig.SYSTEM_LOGGER.info("Persistence: "+ persistence);
					System.out
							.println("Processing request file " + requestFile);
					String responseFile = reqDir + respPrefix
							+ intFormat.format(i) + ".xml";
					MorphbankConfig.SYSTEM_LOGGER.info("Response file " + responseFile);
					requestTest.run(true, requestFile, responseFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public ProcessRequestFiles() {
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
			XmlUtils.printXml(out, responses);
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

	public Request getRequest(boolean file, String requestName,
			Unmarshaller unmarshaller) throws JAXBException {

		Object obj = getXml(file, requestName, unmarshaller);
		if (obj instanceof Request) {
			return (Request) request;
		} else {
			return null;
		}
	}

	public Object getXml(boolean file, String requestName) {
		Unmarshaller unmarshaller = XmlUtils.getUnmarshaller();
		try {
			Object obj = getXml(file, requestName, unmarshaller);
			return obj;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getXml(boolean isFile, String requestName,
			Unmarshaller unmarshaller) throws JAXBException {
		if (!isFile) { // read sample xml from URL
			java.net.URL requestAccess = null;
			try {
				requestAccess = new java.net.URL(requestName);
				URLConnection connection = requestAccess.openConnection(MorphbankConfig.getProxy());
				InputStream doc = connection.getInputStream();
				Request request = (Request) unmarshaller.unmarshal(doc);
				doc.close();
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
