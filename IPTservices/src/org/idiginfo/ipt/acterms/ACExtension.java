package org.idiginfo.ipt.acterms;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.idiginfo.ipt.extension.Extension;
import org.idiginfo.ipt.extension.Extension.Property;

/**
 * Class to handle the details of making an IPT extension for Audubon Core
 * Selecting and ordering fields, adding extra fields as necessary, and
 * otherwise tailoring the contents of the extention
 * 
 * @author griccardi
 * 
 */
public class ACExtension {

	static JAXBContext jc = null;
	static Marshaller marshaller = null;
	static Unmarshaller unmarshaller = null;
	// private static String termFileName =
	// "C:/dev/morphbank/audubon core IPT/acterms list.txt";
	private static String termFileName = "C:/dev/morphbank/audubon core IPT/include list.txt";
	public static final String REQ_PKG = "org.idiginfo.ipt.extension";
	public static final String SCHEMA_LOCATION = "http://rs.gbif.org/extension/ http://rs.gbif.org/schema/extension.xsd";

	static final Set<String> omittedTermSet;
	static final String[] omittedTerms = { "dcterms:type", "ac:subtypeLiteral",
			"ac:metadataLanguageLiteral", "ac:commenter",
			"ac:commenterLiteral", "ac:reviewerLiteral", "ac:dcterms:rights",
			"ac:fundingAttribution", "ac:dcterms:source", "ac:providerLiteral",
			"ac:metadataProviderLiteral", "ac:dcterms:language", "ac:temporal",
			"ac:CreateDate", "ac:timeOfDay", "ac:scientificNameID",
			"ac:otherScientificName" };
	static final Set<String> omittedAccessTermSet;
	static final String[] omittedAccessTerms = { "ac:variantLiteral",
			"dcterms:format", "ac:variant", "ac:hashFunction", "ac:hashValue",
			"exif:PixelXDimension", "exif:PixelYDimension", "mix:fileSize" };
	static {
		omittedTermSet = new HashSet<String>();
		for (String term : omittedTerms) {
			omittedTermSet.add(term);
		}
		omittedAccessTermSet = new HashSet<String>();
		for (String term : omittedAccessTerms) {
			omittedAccessTermSet.add(term);
			omittedTermSet.add(term);
		}

	}

	public static final String DESC = "The Audubon Core is a set of vocabularies designed to represent metadata"
			+ " for biodiversity multimedia resources and collections. "
			+ "These vocabularies aim to represent information that will "
			+ "help to determine whether a particular resource or collection will be fit "
			+ "for some particular biodiversity science application before acquiring the media. "
			+ "Among others, the vocabularies address such concerns as the management of the media "
			+ "and collections, descriptions of their content, their taxonomic, geographic, and "
			+ "temporal coverage, and the appropriate ways to retrieve, attribute and reproduce them. "
			+ "This document contains a list of attributes of each Audubon Core term, including a "
			+ "documentation name, a specified URI, a recommended English label for user interfaces, "
			+ "a definition, and some ancillary commentary.";

	public Extension createExtension(ACTerms acTerms) {
		Extension extension = new Extension();
		extension.setName("Multimedia");
		extension.setNamespace("http://rs.tdwg.org/ac/terms/");
		extension.setRowType("http://rs.tdwg.org/ac/terms/multimedia");
		extension.setDescription(DESC);
		extension.setTitle("Audubon Media Description");
		extension
				.setRelation("http://species-id.net/wiki/Audubon_Core_Term_List");

		List<Property> properties = extension.getProperty();
		// Collection<ACTerm> terms = acTerms.values();
		generateTerms(properties, acTerms);
		return extension;
	}

	private void generateTerms(List<Property> properties, ACTerms acTerms) {
		// for (ACTerm term : acTerms) {
		// properties.add(term.createExtensionProperty());
		// }
		// List<String> accessPointTermNames;
		try {
			File termFile = new File(termFileName);
			LineIterator lines;
			lines = FileUtils.lineIterator(termFile);

			generateACTerms(lines, properties, acTerms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String headerPatternStr = "\\'\\'\\'(.*)\\'\\'\\'";
	static String termPatternStr = "^\\{\\{!\\}\\}\\s*\\[\\[#(.*)\\s\\|.*$";
	static Pattern headerPattern = Pattern.compile(headerPatternStr);
	static Pattern termPattern = Pattern.compile(termPatternStr);

	private void generateAccessTerms(List<Property> properties,
			ACTerms acTerms, String groupName, List<String> accessPointTerms) {
		generateAccessTerms(properties, acTerms, groupName, accessPointTerms,
				"thumbnail");
		generateAccessTerms(properties, acTerms, groupName, accessPointTerms,
				"lowerQuality");
		// generateAccessTerms(properties, acTerms, groupName, accessPointTerms,
		// "mediumQuality");
		// generateAccessTerms(properties, acTerms, groupName, accessPointTerms,
		// "goodQuality");
		// generateAccessTerms(properties, acTerms, groupName, accessPointTerms,
		// "bestQuality");
	}

	private void generateAccessTerms(List<Property> properties,
			ACTerms acTerms, String groupName, List<String> accessPointTerms,
			String quality) {
		for (String termName : accessPointTerms) {
			if (omittedAccessTermSet.contains(termName))
				continue;
			ACTerm term = createAccessTerm(acTerms.get(termName), quality);
			properties.add(term.createExtensionProperty(groupName));
		}
	}

	// strings for use in access terms
	static String uriDescription = "URI of the resource itself. If this resource can be acquired by an http request, "
			+ "its http URL should be given. If not, but it has some URI in another URI scheme, that may be given here.";
	static String relationPrefix = "http://species-id.net/wiki/Audubon_Core_Term_List#";

	private ACTerm createAccessTerm(ACTerm accessTerm, String quality) {
		ACTerm term = new ACTerm();
		term.setItemURI(accessTerm.getItemURI());
		term.setLabel(StringUtils.capitalise(quality) + " "
				+ accessTerm.getLabel());
		term.setTermName(term.getItemURI());
		term.setDefinedBy(accessTerm.getDefinedBy());
		term.setLayer(accessTerm.getLayer());
		term.setRepeatable(accessTerm.getRepeatable());
		term.setRequired("Yes".equals(accessTerm.getRequired()));
		term.setDefinition(accessTerm.getDefinition());
		term.setNotes(accessTerm.getNotes());
		term.setName(quality
				+ StringUtils.capitalise(accessTerm.getSimpleName()));

		return term;
	}

	private void generateACTerms(LineIterator lines, List<Property> properties,
			ACTerms terms) {
		String groupName = null;
		List<String> accessTermNames = new Vector<String>();
		boolean accessTerms = false;
		while (lines.hasNext()) {
			String line = lines.nextLine();
			Matcher headerMatcher = headerPattern.matcher(line);
			if (headerMatcher.matches()) {
				groupName = headerMatcher.group(1);
				// check for Service Access Point terms, which are processed
				// separately
				// accessTerms = groupName.startsWith("Service Access");
				continue;
			}
			Matcher termMatcher = termPattern.matcher(line);
			if (termMatcher.matches()) {
				String termName = termMatcher.group(1);
				System.out.println("term name:" + termName);

				if (omittedTermSet.contains(termName)) {
					continue;
				}
				ACTerm term = terms.get(termName);
				if (term == null) {
					System.out.println("no term: " + termName);
					continue;
				}
				if (!accessTerms) {
					properties.add(term.createExtensionProperty(groupName));
				} else {// put term name on list for later processing
					accessTermNames.add(term.getSimpleUri());
				}
			}
		}
		generateAccessTerms(properties, terms, groupName, accessTermNames);
	}

	List<String> getAccessPointTermNames(LineIterator lines) {

		return null;
	}

	public Marshaller getMarshaller() {
		if (marshaller != null) {
			return marshaller;
		}
		try {
			if (jc == null) {
				jc = JAXBContext.newInstance(REQ_PKG);
			}
			marshaller = jc.createMarshaller();
			// TODO use UTF-8? why not? Problems with Java classes
			// marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			// MorphbankConfig.SYSTEM_LOGGER.info("ISO");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					SCHEMA_LOCATION);
			try {
				marshaller.setProperty(
						"com.sun.xml.bind.namespacePrefixMapper",
						new NamespacesPrefixMapperImpl());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return marshaller;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Unmarshaller getUnmarshaller() {
		if (unmarshaller != null) {
			return unmarshaller;
		}
		try {
			if (jc == null) {
				jc = JAXBContext.newInstance(REQ_PKG);
			}
			unmarshaller = jc.createUnmarshaller();
			return unmarshaller;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

}
