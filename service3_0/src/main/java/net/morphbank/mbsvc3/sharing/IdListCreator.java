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
package net.morphbank.mbsvc3.sharing;

import java.util.List;
import java.util.Vector;

import net.morphbank.MorphbankConfig;

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
public class IdListCreator extends DefaultHandler {

	protected String tempVal; // holds the most recent character block
	protected int numResults;
	protected int numResultsReturned;
	protected int firstResult;
	protected String baseServiceUrl;
	protected List<Integer> idList = new Vector<Integer>();
	StringBuffer message = new StringBuffer();
	boolean isInt = false;
	String tempInt = null;

	protected UpdateRemote updater = new UpdateRemote("http://services.morphbank.net/mb3");

	public IdListCreator(String baseServiceUrl) {
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
			startInt();
		} else if (qName.equalsIgnoreCase("numChangeDays")) {
			startInt();
		} else if (qName.equalsIgnoreCase("objecttypes")) {
		} else if (qName.equalsIgnoreCase("limit")) {
			startInt();
		} else if (qName.equalsIgnoreCase("numResults")) {
			startInt();
		} else if (qName.equalsIgnoreCase("numResultsReturned")) {
			startInt();
		} else if (qName.equalsIgnoreCase("firstResult")) {
			startInt();
		} else if (qName.equalsIgnoreCase("id")) {
			startInt();
		}
	}
	
	void startInt(){
		isInt = true;
		tempInt = "";
	}
	
	int endInt(){
		int val = getInt(tempInt);
		isInt = false;
		//MorphbankConfig.SYSTEM_LOGGER.info("integer "+val+" from string '"+tempInt+"'");
		return val;
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
		if (isInt) tempInt = tempInt + tempVal;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("keywords")) {
		} else if (qName.equalsIgnoreCase("changeDate")) {
			message.append("changeDate: ").append(tempVal);
		} else if (qName.equalsIgnoreCase("numChangeDays")) {
		} else if (qName.equalsIgnoreCase("objecttypes")) {
		} else if (qName.equalsIgnoreCase("limit")) {
		} else if (qName.equalsIgnoreCase("numResults")) {
			numResults = endInt();
			message.append(" numResults: ").append(numResults);
		} else if (qName.equalsIgnoreCase("numResultsReturned")) {
			numResultsReturned = endInt();
			message.append(" numResultsReturned: ").append(numResultsReturned);
		} else if (qName.equalsIgnoreCase("firstResult")) {
			firstResult = endInt();
			message.append(" firstResult: ").append(firstResult);
		} else if (qName.equalsIgnoreCase("id")) {
			int id = endInt();
			message.append(" id: ").append(id);
			idList.add(id);
			isInt = false;
			// reportStatus(result);
			// update the object id
		}
	}

	int getInt(String val) {
		try {
			int intVal = Integer.parseInt(val);
			return intVal;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public List<Integer> getIdList() {
		MorphbankConfig.SYSTEM_LOGGER.info(message.toString());
		return idList;
	}

	public int getNumResults() {
		return numResults;
	}

	public int getNumResultsReturned() {
		return numResultsReturned;
	}

	public int getFirstResult() {
		return firstResult;
	}
}
