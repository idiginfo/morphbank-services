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
import java.text.*;

import net.morphbank.mbsvc3.fsuherb.*;
import net.morphbank.mbsvc3.xml.*;

public class ProcessFsuHerbFile {

	//static final String INPUT_FILE = "C:/dev/morphbank/fsuherb/FSUherb20MarchNotRestricted.txt";
	static final String INPUT_FILE = "C:/dev/morphbank/fsuherb/FSUherb20MarchRestricted.xls";
	static final String FILE_PATH = "C:/dev/morphbank/fsuherb/";
	static final String XML_OUTPUT_FILE_PREFIX = FILE_PATH + "xmlfiles/fsuherbN";
	//static final String XML_OUTPUT_FILE_PREFIX = FILE_PATH + "xmlfiles/fsuherbNot";
	static final String XML_OUTPUT_FILE = "c:/dev/morphbank/fsuherb/sample.xml";
	static final String REPORT_FILE = "c:/dev/morphbank/fsuherb/sample.txt";
	static final String REPORT_FILE_PREFIX = FILE_PATH + "reports";
	static final int OWNER_ID = 77409;
	static final int SUBMITTER_ID = 77354;
	static final int GROUP_ID = 475742;

	MapFsuHerbSpreadsheetToXml mapper = new MapFsuHerbSpreadsheetToXml();

	public static void main(String args[]) {
		ProcessFsuHerbFile tester = new ProcessFsuHerbFile();
		int numLinesPerFile = 100;
		String outputFile = XML_OUTPUT_FILE_PREFIX;
		if (args.length > 0) {
			outputFile = FILE_PATH + args[0];
		}
		if (args.length > 1) {
			numLinesPerFile = Integer.valueOf(args[1]);
		}
		tester.run(outputFile, numLinesPerFile);
	}

	XlsFieldMapper fieldMapper = null;

	static final NumberFormat intFormat = new DecimalFormat("0000");

	public void run(String outputFilePrefix, int numLinesPerFile) {
		try {
			Credentials submitter = new Credentials(SUBMITTER_ID, GROUP_ID, null);
			Credentials owner = new Credentials(OWNER_ID, GROUP_ID, null);
			FileWriter reportFile = new FileWriter(REPORT_FILE_PREFIX);
			PrintWriter report = new PrintWriter(reportFile);
			int firstLine = 1;
			for (int fileNum = 0; true; fileNum++) {
				int startLine = numLinesPerFile * fileNum + 1;
				String outputFile = outputFilePrefix
						+ intFormat.format(fileNum) + ".xml";
				Request request = mapper.createRequestFromFile(INPUT_FILE,
						submitter, owner, report, numLinesPerFile, startLine);
				// TODO add credentials to request
				if (request == null) {
					break;
				}
				FileWriter outFile = new FileWriter(outputFile);
				PrintWriter out = new PrintWriter(outFile);
				XmlUtils.printXml(out, request);
				out.close();
				outFile.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
