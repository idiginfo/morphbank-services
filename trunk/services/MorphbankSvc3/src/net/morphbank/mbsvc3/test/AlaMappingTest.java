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

import java.io.*;

import net.morphbank.mbsvc3.mapsheet.*;
import net.morphbank.mbsvc3.xml.*;

public class AlaMappingTest {

	// static final String INPUT_FILE =
	// "C:/dev/morphbank/ala/ALA_GVC_01_debEdited.xls";
	// static final String FILE_PATH = "C:/dev/morphbank/ala";
	// static final String XML_OUTPUT_FILE =
	// "c:/dev/morphbank/ala/ala_gvc_01.xml";
	// static final String REPORT_FILE = "c:/dev/morphbank/ala/ala_gvc_01.txt";
	static final int OWNER_ID = 477719;
	static final int SUBMITTER_ID = 477719;
	static final int GROUP_ID = 2505490;

	// scamit values C:\dev\morphbank\scamit
//		static final String INPUT_FILE = "/usr/local/dev/morphbank/upload/ANICSATSCANv2.xls";
//		static final String INPUT_FILE = "/usr/local/dev/morphbank/upload/TTRS_v8.xls";
//	static final String INPUT_FILE =  "/usr/local/dev/morphbank/upload/CinclosomaV4.xls";
//	static final String INPUT_FILE =  "/usr/local/dev/morphbank/upload/USMSTest.xls";
	static final String INPUT_FILE =  "/usr/local/dev/morphbank/upload/customWorkbook_TTRS_Birds-0213.xls";
//	static final String INPUT_FILE =  "/usr/local/dev/morphbank/upload/TTRS_testCharset.xls";
	static final String FILE_PATH = "/usr/local/dev/morphbank/upload/";
	//	static final String XML_OUTPUT_FILE = "/usr/local/dev/morphbank/upload/CinclosomaV3.xml";
	static final String XML_OUTPUT_FILE = INPUT_FILE.substring(0, INPUT_FILE.length()-4) + ".xml";
	static final String REPORT_FILE = INPUT_FILE.substring(0, INPUT_FILE.length()-4) + "-report.txt";
	//static final int OWNER_ID = 199389; // Kevin Barwick
	//static final int SUBMITTER_ID = 199389;
	//static final int GROUP_ID = 463950;

	MapSpreadsheetToXml mapper = new MapSpreadsheetToXml();

	public static void main(String args[]) {
		AlaMappingTest tester = new AlaMappingTest();
		int numLines = 700; //original 200
		int firstLine = 1;
		String outputFile = XML_OUTPUT_FILE;
		if (args.length > 0) {
			outputFile = FILE_PATH + args[0];
		}
		if (args.length > 1) {
			numLines = Integer.valueOf(args[1]);
		}
		if (args.length > 2) {
			firstLine = Integer.valueOf(args[2]);
		}
		tester.run(outputFile, numLines, firstLine);
	}

	XlsFieldMapper fieldMapper = null;

	public void run(String outputFile, int numLines, int firstline) {
		try {
			Credentials submitter = new Credentials(SUBMITTER_ID, GROUP_ID, null);
			Credentials owner = new Credentials(OWNER_ID, GROUP_ID, null);
			FileWriter reportFile = new FileWriter(REPORT_FILE);
			PrintWriter report = new PrintWriter(reportFile);
			// Request request = mapper.createRequestFromFile(INPUT_FILE,
			Request request = mapper.createRequestFromFile(INPUT_FILE, submitter, owner, report,
					numLines, firstline);
			// TODO add credentials to request
			FileWriter outFile = new FileWriter(outputFile);
			PrintWriter out = new PrintWriter(outFile);
			XmlUtils.printXml(out, request);
			report.close();
			reportFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
