package net.morphbank.mbsvc3.mapdwca;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;



/**
 * Handler for parsing meta.xml of a Dwc-Archive
 * Populate the list of headers.
 * @author gjimenez
 *
 */
public class MetaXmlHandler extends DefaultHandler {

	private ArrayList<String> headers = new ArrayList<String>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			MetaXmlHandler handler = new MetaXmlHandler();
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			FileReader r = new FileReader("/home/gjimenez/iDigBio/Link to IPT/dwca-morphbank_test/meta.xml");
		    xr.parse(new InputSource(r));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public MetaXmlHandler() {
		super();
	}
	
	public void startDocument() {
//		System.out.println("start");
	}
	
	public void endDocument() {
//		System.out.println("end");
	}
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if(qName.equalsIgnoreCase("field")){
			headers.add(atts.getValue("term"));
			System.out.println(atts.getValue("term"));
		}
	}
	
	public void endElement(String uri, String name, String qName) {

	}
	
	public String[] getHeader() {
		return (String[]) headers.toArray();
	}

}
