package org.idiginfo.ipt.acterms;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.idiginfo.ipt.extension.Extension;
import org.idiginfo.ipt.extension.Extension.Property;

public class ACTerms extends HashMap<String, ACTerm> {

	private static final long serialVersionUID = 1L;

	public Extension createExtension() {

		Extension extension = new Extension();
		ACTerm term = get("dcterms:identifier");
		Set<String> keys = this.keySet();
		for (String key : keys){
			System.out.println(key);
		}
		Property property = term.createExtensionProperty();
		
		extension.getProperty().add(property);
		return extension;
	}

	public static ACTerms processACTerms(File in) throws IOException {
		// File acTermsFile = new File(ACTERMS_FILE);
		LineIterator lines = FileUtils.lineIterator(in);
		ACTerms terms = new ACTerms();
		while (lines.hasNext()) {
			Map<String, String> map = getItemMap(lines);
			if (map.size() == 0)
				break;
			ACTerm acTerm = new ACTerm(map, "");
			terms.put(acTerm.getSimpleUri(), acTerm);
			System.out.println("label: " + acTerm.getLabel());
			Property property = acTerm.createExtensionProperty();
			System.out.println("qual name: " + property.getQualName()
					+ " simple name: " + property.getName());
		}
		System.out.println("Number of terms: " + terms.size());
		return terms;
	}

	// get the next term from the file
	static Map<String, String> getItemMap(LineIterator lines) {
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
					addNextTerm(outMap, line);
				}
			}
		}
		return outMap;
	}

	// // String regex = "^(.*)=(.*)$";
	// String regex = "\\{\\{NS2 (\\S*) \\| (\\S*) (\\}\\})";
	// Pattern pattern = Pattern.compile(regex);

	static final String TERM_REGEX = "^\\s*\\|(.*)=(.*)$";
	static Pattern termPattern = Pattern.compile(TERM_REGEX);
	// example: | Defined by = {{NS XMP2Doc | 3.4.2}}

	static final String URI_REGEX = "\\{\\{NS2\\s+(\\S*)\\s*\\|\\s*(\\S*)\\s*\\}\\}";
	static Pattern uriPattern = Pattern.compile(URI_REGEX);

	//
	static void  addNextTerm(Map<String, String> outMap, String line) {
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
