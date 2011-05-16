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
package net.morphbank.mbsvc3.ocr;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class HerbisAnalysis {

	static String HERBIS_BASE_URL = "http://www3.isrl.uiuc.edu/~TeleNature/Herbis/src/"
			+ "web/cgi-bin/tagger.cgi?modelno=1&inputbutton=Submit&rawlabel=";

	public static String getHerbisAnalysisResult(Ocr ocr) {
		if (ocr.getLabelData() != null && ocr.getLabelData().length() > 0) {
			return ocr.getLabelData();
		} else
			return getNewHerbisAnalysisResult(ocr.getOcr());
	}

	public static String getNewHerbisAnalysisResult(String ocr) {
		try {
			String ocrEncoded = URLEncoder.encode(ocr, "UTF-8");
			String herbisUrl = HERBIS_BASE_URL + ocrEncoded;
			URL herbisFetchUrl = new URL(herbisUrl);
			URLConnection conn = herbisFetchUrl.openConnection(MorphbankConfig.getProxy());
			// String contentType = conn.getContentType();
			// int contentLength = conn.getContentLength();
			// byte[] contents = new byte[100];
			InputStreamReader herbisReader = new InputStreamReader(
					(InputStream) conn.getContent());
			BufferedReader herbisIn = new BufferedReader(herbisReader);
			String line = herbisIn.readLine();
			StringBuffer herbisResults = new StringBuffer();
			while (line != null) {
				if (!line.startsWith("<?")) {
					herbisResults.append(line).append('\n');
				}
				line = herbisIn.readLine();
			}
			return herbisResults.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getHerbisAnalysis(String id) {
		int objectId = Integer.parseInt(id);
		return getHerbisAnalysis(objectId);
	}

	public static String getHerbisAnalysis(int objectId) {
		Ocr ocr = null;
		Specimen specimen = Specimen.getSpecimen(objectId);
		ocr = new Ocr(specimen);
		if (ocr == null) {
			return null;
		}
		return HerbisAnalysis.getHerbisAnalysisResult(ocr);
	}

}
