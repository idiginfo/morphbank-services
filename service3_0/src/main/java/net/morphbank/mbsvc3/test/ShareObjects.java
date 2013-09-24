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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.request.RequestParams;

import org.xml.sax.SAXException;

public class ShareObjects {

	static String SERVICE_BASE_URL = "http://services.morphbank.net/mb3";
	static String CHANGES_BASE_URL = "http://services.morphbank.net/mb3/request?method="
			+ RequestParams.METHOD_CHANGES + "&format=" + RequestParams.FORMAT_ID
			+ "&numChangeDays=";
	static String OUT_FILE = "c:/dev/morphbank/changeresult.xml";
	static String REQ_FILE = "c:/dev/morphbank/changeIds.xml";
	static String CHANGE_FILE = "c:/dev/morphbank/changeIds.xml";

	public static void main(String[] args) throws Exception {
		ShareObjects tester = new ShareObjects();
		tester.run(args);
	}

	public void run(String[] args) throws Exception {
		String persistence = MorphbankConfig.PERSISTENCE_LOCALHOST;
		if (args.length > 0) persistence = args[0];
		int numDays = 6;
		if (args.length > 1) numDays = Integer.valueOf(args[1]);
		int limit = 100;
		if (args.length > 2) limit = Integer.valueOf(args[2]);

		MorphbankConfig.setPersistenceUnit(persistence);
		MorphbankConfig.init();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Writer out = new OutputStreamWriter(System.out, "UTF8");
			SAXParser saxParser = factory.newSAXParser();
			// InputStream doc = new FileInputStream(REQ_FILE);
			URL changeUrl = new URL(getChangeUrl(numDays, limit));
			System.out.println(changeUrl.toString());
			URLConnection connection = changeUrl.openConnection(MorphbankConfig.getProxy());
			InputStream doc = connection.getInputStream();
			IdDocHandler docHandler = new IdDocHandler(SERVICE_BASE_URL);
			saxParser.parse(doc, docHandler);
			doc.close();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	String getChangeUrl(int numChangeDays, int limit){
		return CHANGES_BASE_URL + numChangeDays+"&limit="+limit;
	}
}
