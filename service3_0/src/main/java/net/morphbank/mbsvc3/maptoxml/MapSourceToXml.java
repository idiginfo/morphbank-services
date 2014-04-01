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
package net.morphbank.mbsvc3.maptoxml;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapdwca.DwcaSourceIterator;
import net.morphbank.mbsvc3.mapdwca.DwcaFields;
import net.morphbank.mbsvc3.mapdwca.MapDwcaToXml;
import net.morphbank.mbsvc3.xml.Credentials;
import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.Insert;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.mbsvc3.xml.XmlTaxonNameUtilities;

public class MapSourceToXml {

	// protected MapToResponse mapResponse;
	protected Credentials submitter;
	protected Credentials contributor;
	protected Request request;
	SourceIterator fieldMapper;
	Fields fields;
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

	public MapSourceToXml() {
		// dateToPublish = this.dateToPublish();
	}

	public Request createRequestFromSource(SourceIterator source) {
		// alternative FieldMapper implements Iterator<SourceObjectL>
		request = new Request();
		request.setSubmitter(this.submitter);
		Insert insert = new Insert();
		insert.setSubmitter(this.submitter);
		request.getInsert().add(insert);
		Set<String> specimenIds = new HashSet<String>();// keep track of
		// specimen ids
		int lineNumber = 0;

		// iterate through objects
		while (source.hasNext()) {
			SourceObject sourceObj = source.next();
			
			XmlBaseObject xmlObject = new XmlBaseObject(sourceObj.getSourceType());
			setFields(xmlObject, sourceObj);
			insert.getXmlObjectList().add(xmlObject);
		}

		return request;

	}

	private void setFields(XmlBaseObject xmlObject, SourceObject sourceObj) {
		while (sourceObj.hasNext()){
			Field field = sourceObj.next();
			// set the field of the xmlObject
			boolean success = field.setXmlValue(xmlObject);
			if(!success){
				field.addUserProperty(xmlObject);
			}
		}
		
	}

	private Date dateToPublish() {
		// TODO fix this
		// if (fieldMapper instanceof XlsFieldMapper) {
		// Sheet credentials = ((XlsFieldMapper) fieldMapper)
		// .getCredentialSheet();
		//
		// return credentials.getRow(7).getCell(1).getDateCellValue();
		// }
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.DAY_OF_YEAR, DAYS_TO_PUBLISH);
		return nextYear.getTime();
	}


	private Credentials getContributor(Credentials owner) {
		if (userId > 0 && groupId > 0)
			return new Credentials(userId, groupId);
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

	// private Credentials getSubmitter(Credentials submitter) {
	// if (submitterId > 0 && groupId > 0) return new Credentials(userId,
	// groupId);
	// if (submitterName.length() > 0 && groupName.length() > 0)
	// return new Credentials(userName, groupName);
	// return submitter;
	// }
	private Credentials getSubmitter(Credentials submitter) {
		if (submitterId > 0 && groupId > 0)
			return new Credentials(submitterId, groupId);
		if (submitterName.length() > 0 && groupName.length() > 0)
			return new Credentials(submitterName, groupName);
		return submitter;
	}

	// private void getCredentials(XlsFieldMapper fieldMapper) {
	//
	// userName = fieldMapper.getCredentialSheet().getRow(1).getCell(1)
	// .getStringCellValue();
	// userId = getInteger(fieldMapper.getCredentialSheet().getRow(2)
	// .getCell(1).getStringCellValue());
	// submitterName = fieldMapper.getCredentialSheet().getRow(3).getCell(1)
	// .getStringCellValue();
	// submitterId = (int) fieldMapper.getCredentialSheet().getRow(4)
	// .getCell(1).getNumericCellValue();
	// groupName = fieldMapper.getCredentialSheet().getRow(5).getCell(1)
	// .getStringCellValue();
	// groupId = getInteger(fieldMapper.getCredentialSheet().getRow(6)
	// .getCell(1).getStringCellValue());
	// }

//	private void getCredentials(DwcaFieldMapper fieldMapper) {
//		userName = fieldMapper.getUserName();
//		userId = fieldMapper.getUserId();
//		submitterName = fieldMapper.getSubmitterName();
//		submitterId = fieldMapper.getSubmitterId();
//		groupName = fieldMapper.getGroupName();
//		groupId = fieldMapper.groupId();
//	}

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
		// MapSpecimen specimenMapper = new MapSpecimen(fieldMapper,
		// fields);
		XmlBaseObject xmlSpecimen = new XmlBaseObject("Specimen");
		// xmlSpecimen.addDescription("From spreadsheet line " + lineNumber);
		// xmlSpecimen.setDateToPublish(dateToPublish());
		// specimenMapper.setXmlSpecimenFields(xmlSpecimen);
		// addLocalId(xmlSpecimen);
		return xmlSpecimen;
	}

	public XmlBaseObject createXmlImage(int lineNumber) {
		MapImage imageMapper = new MapImage(fieldMapper, fields);
		xmlImage = new XmlBaseObject("Image");
		xmlImage.setDateToPublish(dateToPublish());
		imageMapper.setXmlImageFields(xmlImage);
		xmlImage.addDescription(" From spreadsheet line " + lineNumber);
		addLocalId(xmlImage);
		return xmlImage;
	}

	public XmlBaseObject createXmlView(int lineNumber) {
		MapView imageMapper = new MapView(fieldMapper, fields);
		xmlView = new XmlBaseObject("View");
		imageMapper.setXmlViewFields(xmlView);
		if (xmlView.getName().equalsIgnoreCase("//////"))
			return null;
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

	public static String getSpecimenIdString(SourceIterator specimen) {
		String idStr = specimen.getValue(SPECIMEN_ID_PREFIX_FIELD) + ":"
				+ specimen.getValue(SPECIMEN_ID_FIELD);
		if (idStr.toLowerCase().indexOf("n/a") > -1) {
			return "";
		}
		return idStr;
	}

	public static String getImageIdString(SourceIterator image) {
		String idStr = image.getValue(IMAGE_ID_PREFIX_FIELD) + ":"
				+ image.getValue(IMAGE_ID_FIELD);
		return idStr;
	}

	static final String[] viewIdFields = { "Specimen Part", "View Angle",
			"Imaging Technique", "Imaging Preparation Technique",
			"View Developmental Stage", "View Sex", "View Form", };

	// specimenPart/ViewAngle/imagingTechnique/ImagingPreparationTechnique
	// DevelopmentalStage/Sex/Form

	public static String getViewIdStr(SourceIterator view) {
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

	public static String getViewIdString(SourceIterator view) {
		String idStr = view.getValue(VIEW_ID_PREFIX_FIELD) + ":"
				+ view.getValue(VIEW_ID_FIELD);
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

	public static XmlId getSpecimenId(SourceIterator specimen) {
		String idStr = getSpecimenIdString(specimen);
		return getXmlExternalId(idStr);
	}

	public static XmlId getImageId(SourceIterator image) {
		String idStr = getImageIdString(image);
		if (idStr == null || idStr.length() == 0)
			return null;
		return getXmlExternalId(idStr);
	}

	/*
	 * 3 possibilities : there is a morphbank id for the view there is an
	 * external id there is a local id
	 */
	public static XmlId getViewId(SourceIterator view) {
		String idStr;
		if (!(idStr = view.getValue(VIEW_MORPHBANK_ID_FIELD))
				.equalsIgnoreCase("")) {
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

	public static XmlId getTaxonId(SourceIterator specimen) {
		String tsn = specimen.getValue("Determination TSN");
		XmlId taxonId = new XmlId();
		if (tsn != null && tsn.length() > 0)
			taxonId.addExternal("ITIS:" + tsn);
		String sciName = specimen.getValue("Determination Scientific Name");
		if (sciName != null && sciName.length() > 0)
			taxonId.addExternal(XmlTaxonNameUtilities.getTaxonExtId(sciName));
		return taxonId;
	}

	public static XmlId getTaxonId(String taxonName) {
		return getXmlExternalId(XmlTaxonNameUtilities.getTaxonExtId(taxonName));
	}

	static Calendar calendar = Calendar.getInstance();
	static DateFormat dateFormatSlash = DateFormat
			.getDateInstance(DateFormat.SHORT);
	static DateFormat dateFormatDash = new SimpleDateFormat("yyyy-MM-dd");

	// public static Date createDate(String dateStr) {
	// // TODO make the date format correct
	// try {
	// Date date = dateFormatSlash.parse(dateStr);
	// return date;
	// } catch (Exception e) {
	// }
	// try {
	// Date date = dateFormatDash.parse(dateStr);
	// return date;
	// } catch (Exception e) {
	// // e.printStackTrace();
	// return null;
	// }

	// }

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

	public static Extref getExternalLink(SourceIterator mapper, String objectType) {
		String urlData = mapper.getValue(objectType + " External Link URL");
		String label = mapper.getValue(objectType + " External Link Label");
		String type = mapper.getValue(objectType + " External Link Type");
		if (urlData.length() == 0)
			return null;
		return new Extref(type, label, urlData, null, null);
	}

	// convert Cell.getColumn() int into a String to match Excel column
	// numbering (AA,AB,AC..., BC,BD...)
	public static String getColumnId(int i) {
		String columnId = "";

		if (i > 26) {
			int alphaBase = i / 26 - 1;
			int asciiBase = alphaBase + 65;
			int afterZ = i % 26;
			int asciiCode = afterZ + 65;
			columnId = Character.toString((char) asciiBase)
					+ Character.toString((char) asciiCode);
		} else {
			int asciiBase = i + 65;
			columnId = Character.toString((char) asciiBase);
		}

		return columnId;
	}

	public static XmlId getStandarImageId(SourceIterator specimen) {
		if (specimen.getValue(SPECIMEN_STANDARD_IMAGE_ID_FIELD) != null
				&& specimen.getValue(SPECIMEN_STANDARD_IMAGE_ID_FIELD)
						.equalsIgnoreCase("yes")) {
			if (xmlImage != null) {
				// return new XmlId(123456);
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
