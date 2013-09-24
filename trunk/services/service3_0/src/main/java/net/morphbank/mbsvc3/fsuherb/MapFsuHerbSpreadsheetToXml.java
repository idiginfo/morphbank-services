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
package net.morphbank.mbsvc3.fsuherb;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.morphbank.mbsvc3.xml.*;

public class MapFsuHerbSpreadsheetToXml {

	// protected MapToResponse mapResponse;
	protected Credentials submitter;
	protected Request request;
	FieldMapper fieldMapper;
	int localId = 0;
	public static final String SPECIMEN_ID_FIELD = "Specimen_externalId";
	public static final String IMAGE_ID_FIELD = "Catalog Number";
	public static final String FSU_IMAGE_ID = "FSU-I:";
	
	public static final int DAYS_TO_PUBLISH = 10;
	
	Date dateToPublish = null;

	public MapFsuHerbSpreadsheetToXml() {
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.DAY_OF_YEAR, DAYS_TO_PUBLISH);
		dateToPublish = nextYear.getTime();
	}

	public Request createRequestFromFile(String fileName,
			Credentials submitter, Credentials owner, PrintWriter report) {
		return createRequestFromFile(fileName, submitter, owner, report, -1, -1);
	}

	public Request createRequestFromFile(String fileName,
			Credentials submitter, Credentials owner, PrintWriter report,
			int numLines, int firstLine) {
		String extension = fileName.substring(fileName.indexOf('.')+1);
		if ("xls".equals(extension)){
			fieldMapper = new XlsFieldMapper(fileName);
		} else {
			fieldMapper = new TextFieldMapper(fileName);
		}
		fieldMapper.moveToLine(firstLine - 1);
		request = new Request();
		request.setSubmitter(submitter);
		Insert insert = new Insert();
		insert.setSubmitter(submitter);
		request.getInsert().add(insert);
		Set<String> specimenIds = new HashSet<String>();// keep track of
		// specimen ids
		int lineNumber = 0;
		while (fieldMapper.hasNext()) {
			int spreadSheetLineNumber = firstLine + lineNumber + 1;
			fieldMapper.getNextLine();
			XmlBaseObject xmlSpecimen = createXmlSpecimen(spreadSheetLineNumber);
			xmlSpecimen.setOwner(owner);
			insert.getXmlObjectList().add(xmlSpecimen);
			XmlBaseObject xmlImage = createXmlImage(spreadSheetLineNumber);
			xmlImage.setOwner(owner);
			insert.getXmlObjectList().add(xmlImage);
			lineNumber++;
			if (numLines >= 1 && lineNumber >= numLines) break;
		}
		if (0 == lineNumber) {
			// no lines!
			return null;
		}
		System.out.println("Lines: " + firstLine + " to line "
				+ (firstLine + lineNumber - 1) + " processed");
		return request;
	}

	/**
	 * Keep track of local id within spreadsheet
	 * 
	 * @param specimenId
	 * @return
	 */
	private void addLocalId(XmlBaseObject xmlObj) {
		String localIdStr = "local:" + (localId++);
		xmlObj.getSourceId().setLocal(localIdStr);
	}

	public XmlBaseObject createXmlSpecimen(int lineNumber) {
		MapFsuHerbSpecimen specimenMapper = new MapFsuHerbSpecimen(fieldMapper);
		XmlBaseObject xmlSpecimen = new XmlBaseObject("Specimen");
		xmlSpecimen.addDescription("From spreadsheet line " + lineNumber);
		xmlSpecimen.setDateToPublish(dateToPublish);

		specimenMapper.setXmlSpecimenFields(xmlSpecimen);
		addLocalId(xmlSpecimen);
		return xmlSpecimen;
	}

	public XmlBaseObject createXmlImage(int lineNumber) {
		MapFsuHerbImage imageMapper = new MapFsuHerbImage(fieldMapper);
		XmlBaseObject xmlImage = new XmlBaseObject("Image");
		xmlImage.setDateToPublish(dateToPublish);
		imageMapper.setXmlImageFields(xmlImage);
		xmlImage.addDescription(" From spreadsheet line " + lineNumber);
		addLocalId(xmlImage);
		return xmlImage;
	}

	public XmlBaseObject createXmlView(int lineNumber) {
		MapFsuHerbView imageMapper = new MapFsuHerbView(fieldMapper);
		XmlBaseObject xmlView = new XmlBaseObject("View");
		imageMapper.setXmlViewFields(xmlView);
		xmlView.addDescription(xmlView.getDescription()
				+ " From spreadsheet line " + lineNumber);
		addLocalId(xmlView);
		return xmlView;
	}

	// constants used to create GUIDs for the fsuherb ATOL objects
	// note: duplicated in MorphbankConfig
	// TODO decide on prefixes
	static final String ID_PREFIX = "FSU";
	static final String SPECIMEN_PREFIX = ID_PREFIX+":";
	static final String IMAGE_PREFIX = ID_PREFIX+"-I:";
	static final String VIEW_PREFIX = ID_PREFIX+"-V:";

	static final int NUM_LENGTH = 9;
	static final String ZEROS = "0000000";

	public static String numString(String number) {
		int value;
		try {
			Integer.valueOf(number);
			return ZEROS.substring(0, NUM_LENGTH - number.length()) + number;
		} catch (NumberFormatException e) {
			return number;
		}
	}

	public static String getSpecimenExtId(String id) {
		return SPECIMEN_PREFIX + numString(id);
	}

	public static String getImageExtId(String id) {
		return IMAGE_PREFIX + numString(id);
	}

	public static String getViewExtId(String id) {
		return VIEW_PREFIX + numString(id);
	}

	public static String getSpecimenIdString(FieldMapper specimen) {
		String idStr = specimen.getValue(SPECIMEN_ID_FIELD);
		if (idStr.toLowerCase().indexOf("n/a") > -1) {
			return "";
		} else {
			return idStr;
		}
	}

	public static String getImageIdString(FieldMapper image) {
		String idStr = FSU_IMAGE_ID + image.getValue(IMAGE_ID_FIELD);
		return idStr;
	}

	static final String[] viewIdFields = { "Imaging Technique",
			"Imaging Preparation Technique", "Specimen Part", "View Angle",
			"Developmental Stage", "Sex", "Form", "View Applicable to Taxon" };

	public static String getViewIdStr(FieldMapper view) {
		StringBuffer viewId = new StringBuffer();
		String minus = "";
		for (int i = 0; i < viewIdFields.length; i++) {
			String field = view.getValue(viewIdFields[i]);
			viewId.append(minus).append(field);
			minus = "-";
		}
		return viewId.toString();
	}

	/**
	 * Get the XmlId of this row to be used as a sourceId
	 * 
	 * @param specimen
	 * @return
	 */
	public static XmlId getXmlExternalId(String idStr) {
		if (idStr != null && idStr.trim().length() > 0) {
			XmlId id = new XmlId();
			id.addExternal(idStr);
			return id;
		}
		return null;
	}

	public static XmlId getSpecimenId(FieldMapper specimen) {
		String idStr = getSpecimenIdString(specimen);
		return getXmlExternalId(idStr);
	}

	public static XmlId getImageId(FieldMapper image) {
		String idStr = getImageIdString(image);
		if (idStr == null || idStr.length() == 0) return null;
		return getXmlExternalId(idStr);
	}

	public static XmlId getViewId(FieldMapper image) {
		String viewIdStr = getViewIdStr(image);
		XmlId id = new XmlId(77407);
		return id;
	}

	public static XmlId getTaxonId(FieldMapper specimen) {
		String tsn = specimen.getValue("tsn");
		return getXmlExternalId("ITIS:"+tsn);
	}

	public static XmlId getTaxonId(String taxonName) {
		return getXmlExternalId(XmlTaxonNameUtilities.getTaxonExtId(taxonName));
	}

	static Calendar calendar = Calendar.getInstance();
	static DateFormat dateFormatSlash = DateFormat.getDateInstance(DateFormat.SHORT);
	static DateFormat dateFormatDash = new SimpleDateFormat("yyyy-MM-dd");

	public static Date createDate(String dateStr) {
		// TODO make the date format correct
		try{
			Date date = dateFormatSlash.parse(dateStr);
			return date;
		} catch(Exception e){
		}
		try{
			Date date = dateFormatDash.parse(dateStr);
			return date;
		} catch(Exception e){
			//e.printStackTrace();
			return null;
		}

	}

	public static Date createDate(String yearStr, String monthStr, String dayStr) {
		try {
			int commaPosition = yearStr.indexOf(",");
			if (commaPosition > -1) {// get rid of comma in year
				yearStr = yearStr.substring(0, commaPosition)
						+ yearStr.substring(commaPosition + 1);
			}
			int year = Integer.parseInt(yearStr);
			int month = Integer.parseInt(monthStr);
			int day = Integer.parseInt(dayStr);
			calendar.set(year, month, day);
			return calendar.getTime();
		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}
	}

	public void setId(XmlBaseObject xmlObj) {
		// initialize Id object
	}

	public void setBaseObjectFields(XmlBaseObject xmlObj) {
	}

	public Credentials getSubmitter() {
		return submitter;
	}

	public void setSubmitter(Credentials submitter) {
		this.submitter = submitter;
	}

	public void setSubmitter() {
		submitter = new Credentials();
	}
}
