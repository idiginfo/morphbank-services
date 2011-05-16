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
package net.morphbank.mbsvc3.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.morphbank.MorphbankConfig;

/**
 * Package of static methods for manipulating XML documents and values Includes:
 * print/read methods and white space and empty string methods
 * 
 * @author riccardi
 * 
 */
public class XmlUtils {

	public static final String REQ_PKG = "net.morphbank.mbsvc3.xml";
	public static final String SCHEMA_LOCATION = "http://www.morphbank.net/mbsvc3/ http://www.morphbank.net/schema/mbsvc3.xsd";

	public static String WEB_SERVER = "http://localhost/";
	public static String MORPHBANK_ID_SERVER = WEB_SERVER + "?id=";
	public static String IMAGE_SERVER = "http://images.morphbank.net/";

	// constants used to create GUIDs for the Spider ATOL objects
	public static final String ID_PREFIX = "SPD";
	public static final String SPECIMEN_PREFIX = ID_PREFIX + "-S:";
	public static final String IMAGE_PREFIX = ID_PREFIX + "-I:";
	public static final String VIEW_PREFIX = ID_PREFIX + "-SV:";
	public static final String SMALLER_REGION_PREFIX = ID_PREFIX + ":";

	// literals for taxon identification
	public static final String ITIS_PREFIX = "ITIS:";
	public static final String SCI_NAME_PREFIX = "SCI-NAME:";
	public static final String SCI_NAME_AUTHOR_PREFIX = "SCI-NAME-AUTHOR:";
	public static final String SCI_NAME_AUTHOR_SEPARATOR = "|";
	public static final String SCI_NAME_AUTHOR_SPLIT = "\\|";

	public static String getImageURL(int localId) {
		return getImageURL(localId, "thumb");
	}

	public static String getImageURL(int localId, String imageType) {
		return getImageURL(Integer.toString(localId), imageType);
	}

	public static String getImageURL(String localId, String imageType) {
		// return MORPHBANK_SERVER + imageType + "/" + localId + ".jpg";
		return IMAGE_SERVER + "?id=" + localId + "&imgType=" + imageType;
	}

	static Map<Integer, String> linkTypes = new HashMap<Integer, String>();

	static {
		linkTypes.put(1, "GenBank");
		linkTypes.put(2, "Institution");
		linkTypes.put(3, "Publication");
		linkTypes.put(4, "External Unique Reference");
		linkTypes.put(5, "Other");
		linkTypes.put(6, "Google Maps");
		linkTypes.put(10, "Project");
		linkTypes.put(12, "Morphbank");
		linkTypes.put(13, "Ontology");
		linkTypes.put(14, "Specimen Database");
		linkTypes.put(15, "Determination Resource");
	}

	public static String getExternalType(int type) {
		String value = linkTypes.get(type);
		if (value == null) return "Other";
		return value;
	}

	public static void printXml(Object xmlObj) {
		PrintWriter out = new PrintWriter(System.out);
		printXml(out, xmlObj);
		// out.close();
	}

	public static void printXml(PrintWriter out, Object xmlDoc) {
		if (xmlDoc == null) return;
		// JAXBContext jc = null;
		Marshaller marshaller = getMarshaller();
		try {
			marshaller.marshal(xmlDoc, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static JAXBContext jc = null;
	static Marshaller marshaller = null;
	static Unmarshaller unmarshaller = null;

	public static Marshaller getMarshaller() {
		if (marshaller != null) {
			return marshaller;
		}
		try {
			if (jc == null) {
				jc = JAXBContext.newInstance(REQ_PKG);
			}
			marshaller = jc.createMarshaller();
			// TODO use UTF-8? why not? Problems with Java classes
//			 marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
//			marshaller.setProperty(Marshaller.JAXB_ENCODING, "WINDOWS-1252");
			// MorphbankConfig.SYSTEM_LOGGER.info("ISO");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION);
			try {
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
						new NamespacePrefixMapperImpl());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return marshaller;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Unmarshaller getUnmarshaller() {
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

	public static Object getXmlObject(InputStream in) {
		Unmarshaller unmarshaller = getUnmarshaller();
		if (unmarshaller == null) {
			return null;
		}
		try {
//			 unmarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			Object xmlObject = getXmlObject(in, unmarshaller);
			return xmlObject;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getXmlObject(InputStream in, Unmarshaller unmarshaller)
			throws JAXBException {
		Object xmlObject = unmarshaller.unmarshal(in);
		return xmlObject;
	}

	public static ObjectList getXmlObjectList(URL in) {
		//System.out.println("getXmlObject url: " + in);
		// if (true) return null;
		Unmarshaller unmarshaller = getUnmarshaller();
		if (unmarshaller == null) {
			return null;
		}
		try {
			Object xmlObject = getXmlObject(in, unmarshaller);
			return (ObjectList) xmlObject;
		} catch (JAXBException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	public static Object getXmlObject(URL remoteUrl, Unmarshaller unmarshaller)
			throws JAXBException {
		try {
			URLConnection connection = remoteUrl.openConnection(MorphbankConfig.getProxy());
			InputStream in = connection.getInputStream();
			Object xmlObject = unmarshaller.unmarshal(in);
			in.close();
			return xmlObject;
		} catch (IOException e) {
			return null;
		}
	}

	public static Request getRequest(InputStream in) {
		Object xmlObject = getXmlObject(in);
		if (xmlObject instanceof Request) return (Request) xmlObject;
		return null;
	}

	// Returns a version of the input where all contiguous
	// whitespace characters are replaced with a single
	// space. Line terminators are treated like whitespace.
	public static String removeDuplicateWhitespace(String inputStr) {
		String patternStr = "\\s+";
		String replaceStr = " ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.replaceAll(replaceStr);
	}

	public static boolean notEmptyString(Object obj) {
		if (!(obj instanceof String)) {
			return false;
		}
		String str = (String) obj;
		return str.trim().length() > 0;
	}

	public static String getNonEmptyString(Object obj) {
		if (!(obj instanceof String)) {
			return null;
		}
		String str = (String) obj;
		return (str.trim().length() > 0 ? str : null);
	}

}
