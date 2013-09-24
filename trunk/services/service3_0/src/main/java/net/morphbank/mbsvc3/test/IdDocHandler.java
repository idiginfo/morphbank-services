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

import java.util.Iterator;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.sharing.UpdateRemote;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sample document
 * 
 * <mbresponse> <keywords>Alaska</keywords> <limit>2</limit>
 * <firstResult>0</firstResult> <numResults>49558</numResults>
 * <numResultsReturned>2</numResultsReturned> <firstResult>0</firstResult>
 * <id>514466</id> <id>514470</id> </mbresponse>
 * 
 * @author riccardi
 * 
 */
public class IdDocHandler extends DefaultHandler {

	String tempVal; // holds the most recent character block
	int id;
	int numResults;
	int numResultsReturned;
	int firstResult;
	String baseServiceUrl;
	
	UpdateRemote updater = new UpdateRemote("http://services.morphbank.net/mb3");

	public IdDocHandler(String baseServiceUrl) {
		this.baseServiceUrl = baseServiceUrl;
		updater = new UpdateRemote(baseServiceUrl);
	}

	// Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		// reset
		if (qName.equalsIgnoreCase("keywords")) {
		} else if (qName.equalsIgnoreCase("changeDate")) {
		} else if (qName.equalsIgnoreCase("numResults")) {
		} else if (qName.equalsIgnoreCase("numChangeDays")) {
		} else if (qName.equalsIgnoreCase("objecttypes")) {
		} else if (qName.equalsIgnoreCase("limit")) {
		} else if (qName.equalsIgnoreCase("numResults")) {
		} else if (qName.equalsIgnoreCase("numResultsReturned")) {
		} else if (qName.equalsIgnoreCase("firstResult")) {
		} else if (qName.equalsIgnoreCase("id")) {
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("keywords")) {
		} else if (qName.equalsIgnoreCase("changeDate")) {
			MorphbankConfig.SYSTEM_LOGGER.info("changeDate: " + tempVal);
		} else if (qName.equalsIgnoreCase("numChangeDays")) {
		} else if (qName.equalsIgnoreCase("objecttypes")) {
		} else if (qName.equalsIgnoreCase("limit")) {
		} else if (qName.equalsIgnoreCase("numResults")) {
			numResults = getInt(tempVal);
			MorphbankConfig.SYSTEM_LOGGER.info("numResults: " + numResults);
		} else if (qName.equalsIgnoreCase("numResultsReturned")) {
			numResultsReturned = getInt(tempVal);
			MorphbankConfig.SYSTEM_LOGGER.info("numResultsReturned: " + numResultsReturned);
		} else if (qName.equalsIgnoreCase("firstResult")) {
			firstResult = getInt(tempVal);
			MorphbankConfig.SYSTEM_LOGGER.info("firstResult: " + firstResult);
		} else if (qName.equalsIgnoreCase("id")) {
			id = getInt(tempVal);
			MorphbankConfig.SYSTEM_LOGGER.info("id: " + id);
			Response result = updateObject(id);
			reportStatus(result);
			// update the object id
		}
	}

	private void reportStatus(Response result) {
		// TODO Auto-generated method stub
		String status = result.getStatus();

		MorphbankConfig.SYSTEM_LOGGER.info ("Updates processed for id "+id+" yielded { "+status+"}");
		Iterator<Object> objects = result.getObjects().iterator();
		while(objects.hasNext()){
			XmlBaseObject xmlObj = (XmlBaseObject) objects.next();
			MorphbankConfig.SYSTEM_LOGGER.info("Object "+xmlObj.getMorphbankId()+" status: "+xmlObj.getStatus());
		}
		XmlUtils.printXml(result);
	}

	protected Response updateObject(int id){
		Response result = updater.updateObject(id);
		//XmlFileProcessor.printXml(result);
		return result;
	}
	
	int getInt(String val) {
		try {
			int intVal = Integer.parseInt(val);
			return intVal;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
