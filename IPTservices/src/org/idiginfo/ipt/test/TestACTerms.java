package org.idiginfo.ipt.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.LineIterator;
import org.idiginfo.ipt.acterms.ACExtension;
import org.idiginfo.ipt.acterms.ACTerms;
import org.idiginfo.ipt.extension.Extension;

public class TestACTerms {

	static final String ACTERMS_FILE = "C:/dev/morphbank/audubon core IPT/acterms.txt";
	static final String ACTERMS_OUT = "C:/dev/morphbank/audubon core IPT/acterms2.txt";
	static final String ACTERMSXML_OUT = "C:/dev/morphbank/audubon core IPT/actermsShort.xml";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TestACTerms testACTerms = new TestACTerms();
		testACTerms.run();
	}

	private void run() throws IOException {
		// ACExtension acExtension = new ACExtension();
		File acTermsFile = new File(ACTERMS_FILE);
		ACTerms terms = ACTerms.processACTerms(acTermsFile);
		System.out.println("number of terms " + terms.size());
		ACExtension acExtension = new ACExtension();
		Extension extension = acExtension.createExtension(terms);
		Marshaller marshaller = acExtension.getMarshaller();
		try {
			FileWriter out = new FileWriter(ACTERMSXML_OUT);
			marshaller.marshal(extension, out);
			out.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Number of terms: " + terms.size());

	}

	// String getItem(LineIterator lines) {
	// while (lines.hasNext()) {

	Map<String, String> getItemMap(LineIterator lines) {
		StringBuffer outLine = new StringBuffer();
		HashMap<String, String> outMap = new HashMap<String, String>();
		while (lines.hasNext()) {
			String line = lines.nextLine();
			if (line.startsWith("{{Audubon Core TermNoComments2")) {
				outLine.append(line).append('\n');
				while (lines.hasNext()) {
					line = lines.nextLine();
					if (line.startsWith("}}")) {
						return outMap;
					}
					addToMap(outMap, line);
				}
			}
		}
		return outMap;
	}

	// String regex = "^(.*)=(.*)$";
	String regex = "\\{\\{NS2 (\\S*) \\| (\\S*) (\\}\\})";
	Pattern pattern = Pattern.compile(regex);

	static final String TERM_REGEX = "^\\s*\\|(.*)=(.*)$";
	Pattern termPattern = Pattern.compile(TERM_REGEX);
	// example: | Defined by = {{NS XMP2Doc | 3.4.2}}

	static final String URI_REGEX = "\\{\\{NS2\\s+(\\S*)\\s*\\|\\s*(\\S*)\\s*\\}\\}";
	Pattern uriPattern = Pattern.compile(URI_REGEX);

	void addToMap(Map<String, String> outMap, String line) {
		Matcher termMatch = termPattern.matcher(line);
		if (!termMatch.matches())
			return;
		String key = termMatch.group(1).trim();
		String value = termMatch.group(2).trim();

		Matcher uri = uriPattern.matcher(value);
		if (uri.matches()) {
			String namespace = uri.group(1);
			String term = uri.group(2);
			// String rest = uri.group(3);
			System.out.println("matches! '" + namespace + "' term is '" + term
					+ "'");
			// value = namespace+term;

		}
		// parse line into field,value
		outMap.put(key, value);
	}
}
