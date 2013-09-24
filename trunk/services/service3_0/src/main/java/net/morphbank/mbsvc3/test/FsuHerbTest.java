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

import net.morphbank.mbsvc3.fsuherb.MapFsuHerbSpreadsheetToXml;
import net.morphbank.mbsvc3.fsuherb.XlsFieldMapper;
import net.morphbank.mbsvc3.xml.*;

public class FsuHerbTest {

	// static final String INPUT_FILE =
	// "C:/dev/morphbank/ctol/CToL_Images_with_TAO_Peter_copyright.xls";
	// static final String INPUT_FILE =
	// "C:/dev/morphbank/ctol/Mabee_CToL_Images.xls";
	//static final String INPUT_FILE = "C:/dev/morphbank/fsuherb/FSUHerbRestAccessN_100records.csv";
	static final String INPUT_FILE = "/usr/local/dev/morphbank/upload/FSUherbNewSet22Feb2011.xls";
//	static final String INPUT_FILE = "C:/dev/morphbank/fsuherb/FSU18MarYesNoRecords.xls";
	static final String FILE_PATH = "/usr/local/dev/morphbank/upload/";
	static final String XML_OUTPUT_FILE = "/usr/local/dev/morphbank/upload/FSUherbNewSet22Feb2011.xml";
	static final String REPORT_FILE = "/usr/local/dev/morphbank/upload/FSUherbNewSet22Feb2011.txt";
	static final int OWNER_ID = 77409;
	static final int SUBMITTER_ID = 77354;
	static final int GROUP_ID = 475742;

	MapFsuHerbSpreadsheetToXml mapper = new MapFsuHerbSpreadsheetToXml();

	public static void main(String args[]) {
		FsuHerbTest tester = new FsuHerbTest();
		int numLines = 700;
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
