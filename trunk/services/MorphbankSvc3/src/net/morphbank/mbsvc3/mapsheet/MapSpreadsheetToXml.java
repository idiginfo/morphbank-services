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
package net.morphbank.mbsvc3.mapsheet;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jxl.Cell;
import jxl.DateCell;
import jxl.Sheet;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.Credentials;
import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.Insert;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.mbsvc3.xml.XmlTaxonNameUtilities;

public class MapSpreadsheetToXml {

	// protected MapToResponse mapResponse;
	protected Credentials submitter;
	protected Credentials contributor;
	protected Request request;
	FieldMapper fieldMapper;
	int localId = 0;
	public static final String SPECIMEN_ID_FIELD = "Specimen External id";
	public static final String IMAGE_ID_FIELD = "Image External id";
	public static final String SPECIMEN_ID_PREFIX_FIELD = "Specimen External id Prefix";
	public static final String IMAGE_ID_PREFIX_FIELD = "Image External id Prefix";
	public static final String VIEW_ID_FIELD = "View External id";
	public static final String VIEW_ID_PREFIX_FIELD = "View External id Prefix";
	public static final String VIEW_MORPHBANK_ID_FIELD = "View Morphbank id";
	public static final String SPECIMEN_STANDARD_IMAGE_ID_FIELD = "is standard";

	static XmlBaseObject xmlView;
	static XmlBaseObject xmlImage;

	String userName;
	int userId = 0;
	String submitterName;
	int submitterId = 0;
	String groupName;
	int groupId = 0;

	public static final int DAYS_TO_PUBLISH = -1;

	Date dateToPublish = null;


	public MapSpreadsheetToXml() {
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.DAY_OF_YEAR, DAYS_TO_PUBLISH);
		dateToPublish = nextYear.getTime();
		//dateToPublish = this.dateToPublish();
	}

	private Date dateToPublish() {
		if (fieldMapper instanceof XlsFieldMapper) {
			Sheet credentials = ((XlsFieldMapper) fieldMapper).getCredentialSheet();
			Cell date = credentials.getCell(1, 7);
			DateCell dateCell = (DateCell) date; //get GMT time so there is a 5 hours difference with the excel file
			return dateCell.getDate();
		}
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.DAY_OF_YEAR, DAYS_TO_PUBLISH);
		return nextYear.getTime();
	}

	public Request createRequestFromFile(String fileName, Credentials submitter, Credentials owner,
			PrintWriter report) {
		return createRequestFromFile(fileName, submitter, owner, report, -1, -1);
	}

	public Request createRequestFromFile(String fileName, Credentials submitter, Credentials owner,
			PrintWriter report, int numLines, int firstLine) {
		String extension = fileName.substring(fileName.indexOf('.') + 1);
		if ("xls".equals(extension)) {
			XlsFieldMapper xlsFieldMapper = new XlsFieldMapper(fileName);
			getCredentials(xlsFieldMapper);
			this.submitter = getSubmitter(submitter);
			this.contributor = getContributor(owner);
			fieldMapper = xlsFieldMapper;
		} else {
			fieldMapper = new TextFieldMapper(fileName);
		}
		fieldMapper.moveToLine(firstLine - 1);
		request = new Request();
		request.setSubmitter(this.submitter);
		Insert insert = new Insert();
		insert.setSubmitter(this.submitter);
		request.getInsert().add(insert);
		Set<String> specimenIds = new HashSet<String>();// keep track of
		// specimen ids
		int lineNumber = 0;
		while (fieldMapper.hasNext()) {
			int spreadSheetLineNumber = firstLine + lineNumber + 1;
			fieldMapper.getNextLine();
			String origFileName = fieldMapper.getValue("Original File Name");
			if (origFileName.length() > 0) {
				if (fieldMapper.getValue(VIEW_MORPHBANK_ID_FIELD).equalsIgnoreCase("")){
					XmlBaseObject xmlView = createXmlView(spreadSheetLineNumber);
					if (xmlView != null) {
						xmlView.setOwner(this.contributor);
						insert.getXmlObjectList().add(xmlView);
					}
				}
				//switched image and specimen order
				XmlBaseObject xmlImage = createXmlImage(spreadSheetLineNumber);
				xmlImage.setOwner(this.contributor);
				insert.getXmlObjectList().add(xmlImage);
				XmlBaseObject xmlSpecimen = createXmlSpecimen(spreadSheetLineNumber);
				xmlSpecimen.setOwner(this.contributor);
				insert.getXmlObjectList().add(xmlSpecimen);

			}
			lineNumber++;
			if (numLines >= 1 && lineNumber >= numLines) break;
		}
		if (0 == lineNumber) {
			// no lines!
			return null;
		}
		System.out.println("Lines: " + firstLine + " to line " + (firstLine + lineNumber - 1)
				+ " processed");
		return request;
	}

	private Credentials getContributor(Credentials owner) {
		if (userId > 0 && groupId > 0) return new Credentials(userId, groupId);
		if (userName.length() > 0 && groupName.length() > 0)
			return new Credentials(userName, groupName);
		return owner;
	}

	private int getInteger(String value) {
		try {
			int val = Integer.parseInt(value);
			return val;
		} catch (Exception e) {
			return 0;
		}
	}

	//	private Credentials getSubmitter(Credentials submitter) {
	//		if (submitterId > 0 && groupId > 0) return new Credentials(userId, groupId);
	//		if (submitterName.length() > 0 && groupName.length() > 0)
	//			return new Credentials(userName, groupName);
	//		return submitter;
	//	}
	private Credentials getSubmitter(Credentials submitter) {
		if (submitterId > 0 && groupId > 0) return new Credentials(submitterId, groupId);
		if (submitterName.length() > 0 && groupName.length() > 0)
			return new Credentials(submitterName, groupName);
		return submitter;
	}

	private void getCredentials(XlsFieldMapper fieldMapper) {
		userName = fieldMapper.getCredentialSheet().getCell(1, 1).getContents();
		userId = getInteger(fieldMapper.getCredentialSheet().getCell(1, 2).getContents());
		submitterName = fieldMapper.getCredentialSheet().getCell(1, 3).getContents();
		submitterId = getInteger(fieldMapper.getCredentialSheet().getCell(1, 4).getContents());
		groupName = fieldMapper.getCredentialSheet().getCell(1, 5).getContents();
		groupId = getInteger(fieldMapper.getCredentialSheet().getCell(1, 6).getContents());
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
		MapSheetSpecimen specimenMapper = new MapSheetSpecimen(fieldMapper);
		XmlBaseObject xmlSpecimen = new XmlBaseObject("Specimen");
		xmlSpecimen.addDescription("From spreadsheet line " + lineNumber);
		xmlSpecimen.setDateToPublish(dateToPublish());
		specimenMapper.setXmlSpecimenFields(xmlSpecimen);
		addLocalId(xmlSpecimen);
		return xmlSpecimen;
	}

	public XmlBaseObject createXmlImage(int lineNumber) {
		MapSheetImage imageMapper = new MapSheetImage(fieldMapper);
		xmlImage = new XmlBaseObject("Image");
		xmlImage.setDateToPublish(dateToPublish()); 
		imageMapper.setXmlImageFields(xmlImage);
		xmlImage.addDescription(" From spreadsheet line " + lineNumber);
		addLocalId(xmlImage);
		return xmlImage;
	}

	public XmlBaseObject createXmlView(int lineNumber) {
		MapSheetView imageMapper = new MapSheetView(fieldMapper);
		xmlView = new XmlBaseObject("View");
		imageMapper.setXmlViewFields(xmlView);
		if (xmlView.getName().equalsIgnoreCase("//////")) return null;
		// xmlView.addDescription(xmlView.getDescription() +
		// " From spreadsheet line " + lineNumber);
		addLocalId(xmlView);
		return xmlView;
	}

	// constants used to create GUIDs for the fsuherb ATOL objects
	// note: duplicated in MorphbankConfig
	// TODO decide on prefixes
	static final String ID_PREFIX = "SCAMITCSD";
	static final String SPECIMEN_PREFIX = ID_PREFIX + ":";
	static final String IMAGE_PREFIX = ID_PREFIX + "-I:";
	static final String VIEW_PREFIX = ID_PREFIX + "-V:";

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
		String idStr = specimen.getValue(SPECIMEN_ID_PREFIX_FIELD) + ":"
		+ specimen.getValue(SPECIMEN_ID_FIELD);
		if (idStr.toLowerCase().indexOf("n/a") > -1) {
			return "";
		}
		return idStr;
	}

	public static String getImageIdString(FieldMapper image) {
		String idStr = image.getValue(IMAGE_ID_PREFIX_FIELD) + ":" + image.getValue(IMAGE_ID_FIELD);
		return idStr;
	}

	static final String[] viewIdFields = { "Specimen Part", "View Angle", "Imaging Technique",
		"Imaging Preparation Technique", "View Developmental Stage", "View Sex", "View Form", };

	// specimenPart/ViewAngle/imagingTechnique/ImagingPreparationTechnique
	// DevelopmentalStage/Sex/Form

	public static String getViewIdStr(FieldMapper view) {
		StringBuffer viewId = new StringBuffer();
		String separator = "";
		for (int i = 0; i < viewIdFields.length; i++) {
			// int fieldId =
			String field = view.getValue(viewIdFields[i]);
			viewId.append(separator).append(field);
			separator = "/";
		}
		return viewId.toString();
	}

	public static String getViewIdString(FieldMapper view) {
		String idStr = view.getValue(VIEW_ID_PREFIX_FIELD) + ":" + view.getValue(VIEW_ID_FIELD);
		if (!idStr.equalsIgnoreCase(":")) {
			return idStr;
		}
		return null;
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

	/*3 possibilities : 
	 * there is a morphbank id for the view
	 * there is an external id
	 * there is a local id
	 */
	public static XmlId getViewId(FieldMapper view) {
		String idStr;
		if (!(idStr = view.getValue(VIEW_MORPHBANK_ID_FIELD)).equalsIgnoreCase("")) {
			int i = Integer.parseInt(idStr);
			return new XmlId(i);
		}
		idStr = getViewIdString(view);
		if (idStr != null) {
			return getXmlExternalId(idStr); 
		}
		if (xmlView != null) { // use localId if external not found
			return xmlView.getSourceId();
		}
		return null;
	}

	public static XmlId getTaxonId(FieldMapper specimen) {
		String tsn = specimen.getValue("tsn");
		XmlId taxonId = new XmlId();
		if (tsn != null && tsn.length() > 0) taxonId.addExternal("ITIS:" + tsn);
		String sciName = specimen.getValue("Determination Scientific Name");
		if (sciName != null && sciName.length() > 0)
			taxonId.addExternal(XmlTaxonNameUtilities.getTaxonExtId(sciName));
		return taxonId;
	}

	public static XmlId getTaxonId(String taxonName) {
		return getXmlExternalId(XmlTaxonNameUtilities.getTaxonExtId(taxonName));
	}

	static Calendar calendar = Calendar.getInstance();
	static DateFormat dateFormatSlash = DateFormat.getDateInstance(DateFormat.SHORT);
	static DateFormat dateFormatDash = new SimpleDateFormat("yyyy-MM-dd");

	//		public static Date createDate(String dateStr) {
	//			// TODO make the date format correct
	//			try {
	//				Date date = dateFormatSlash.parse(dateStr);
	//				return date;
	//			} catch (Exception e) {
	//			}
	//			try {
	//				Date date = dateFormatDash.parse(dateStr);
	//				return date;
	//			} catch (Exception e) {
	//				// e.printStackTrace();
	//				return null;
	//			}

	//		}

	public static Date createDate(Cell cell) {
		if (cell != null) {
			try {
				DateCell dateCell = (DateCell) cell;
				return dateCell.getDate();
			}
			catch(Exception e) { //show the cell coordinates
				String error = "Date format ambiguous at row " + (cell.getRow() + 1) + 
				" col " + getColumnId(cell.getColumn()) + ". Cell content: " + cell.getContents();
				MorphbankConfig.SYSTEM_LOGGER.info(error);
				return parseDate(cell.getContents());
			}
		}
		return null;
	}

	private static Date parseDate(String date) {
		calendar.clear();

		if (date.length() == 4) {
			date += "-01-01";

		}
		else if (date.length() == 10) {
			date = date.replaceAll("-00", "-01");
		}
		else {
			String error = "Impossible to parse this date";
			MorphbankConfig.SYSTEM_LOGGER.info(error);
			return null;
		}
		try {
			calendar.setTime(dateFormatDash.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
			String error = "Impossible to parse this date";
			MorphbankConfig.SYSTEM_LOGGER.info(error);
		}
		String info = "Date Changed to:" + dateFormatDash.format(calendar.getTime());
		MorphbankConfig.SYSTEM_LOGGER.info(info);
		return calendar.getTime();

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

	public static Extref getExternalLink(FieldMapper mapper, String objectType) {
		String urlData = mapper.getValue(objectType + " External Link URL");
		String label = mapper.getValue(objectType + " External Link Label");
		String type = mapper.getValue(objectType + " External Link Type");
		if (urlData.length() == 0) return null;
		return new Extref(type, label, urlData, null, null);
	}

	//convert Cell.getColumn() int into a String to match Excel column numbering (AA,AB,AC..., BC,BD...)
	public static String getColumnId(int i) {
		String columnId = "";

		if (i > 26) {
			int alphaBase = i / 26 - 1;
			int asciiBase = alphaBase + 65;
			int afterZ = i % 26; 
			int asciiCode = afterZ + 65;
			columnId = Character.toString((char) asciiBase) + Character.toString((char) asciiCode);
		}
		else {
			int asciiBase = i + 65;
			columnId = Character.toString((char) asciiBase);
		}


		return columnId;
	}

	public static XmlId getStandarImageId(FieldMapper specimen) {
		if (specimen.getValue(SPECIMEN_STANDARD_IMAGE_ID_FIELD) != null && specimen.getValue(SPECIMEN_STANDARD_IMAGE_ID_FIELD).equalsIgnoreCase("yes")){
			if (xmlImage!= null) {
				//return new XmlId(123456);
				int standardId = xmlImage.getMorphbankId();
				if (standardId != 0) {
					return new XmlId(standardId);
				}
				return xmlImage.getSourceId();
			}
		}
		return null;
	}
}
