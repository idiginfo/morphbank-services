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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.*;

public class MapSheetSpecimen {

	FieldMapper specimen;
	static ObjectFactory objectFactory = new ObjectFactory();
	private String[][] userProperties = null;
	Fields fields;

	public MapSheetSpecimen(FieldMapper specimen, Fields fields) {
		this.specimen = specimen;
		this.userProperties = this.getUserProperties();
		this.fields = fields;
	}

	public void setXmlSpecimenFields(XmlBaseObject xmlSpecimen) {
		xmlSpecimen.setId(MapSpreadsheetToXml.getSpecimenId(specimen));
//		xmlSpecimen.addDescription(specimen.getValue("Specimen Description"));
		xmlSpecimen.addDescription(specimen.getValue(fields.getS_SPECIMEN_DESCRIPTION()));
//		xmlSpecimen.addUserProperty("SpecimenDescription", specimen.getValue("Specimen Description"));
		xmlSpecimen.addUserProperty("SpecimenDescription", specimen.getValue(fields.getS_SPECIMEN_DESCRIPTION()));
		xmlSpecimen.setDetermination(MapSpreadsheetToXml.getTaxonId(specimen));
		xmlSpecimen.addDarwinTag(objectFactory.createIdentifiedBy(specimen
				.getValue(fields.getS_DETERMINED_BY())));
		xmlSpecimen.addDarwinTag(objectFactory.createDateIdentified(MapSpreadsheetToXml
				.createDate(specimen.getValueDate(fields.getS_DATE_DETERMINED()))));
		xmlSpecimen.setStandardImage(MapSpreadsheetToXml.getStandarImageId(specimen));///////////////////////////////

		xmlSpecimen.addDarwinTag(objectFactory.createRemarks(specimen
				.getValue(fields.getS_COMMENT_BY_DETERMINER())));
		xmlSpecimen.setForm(specimen.getValue(fields.getS_FORM()));
		xmlSpecimen.addDarwinTag(objectFactory.createCollectionCode(specimen
				.getValue(fields.getS_COLLECTION_CODE())));
		xmlSpecimen.addDarwinTag(objectFactory.createInstitutionCode(specimen
				.getValue(fields.getS_INSTITUTION_CODE())));
		xmlSpecimen.addDarwinTag(objectFactory.createCatalogNumber(specimen
				.getValue(fields.getS_CATALOG_NUMBER())));
		xmlSpecimen.addDarwinTag(objectFactory.createOtherCatalogNumbers(specimen
				.getValue(fields.getS_PREVIOUS_CATALOG_NUMBER())));
		xmlSpecimen.addDarwinTag(objectFactory.createRelatedCatalogedItems(specimen
				.getValue(fields.getS_RELATED_CATALOG_ITEM())));
		xmlSpecimen.addDarwinTag(objectFactory.createRelationshipType(specimen
				.getValue(fields.getS_RELATIONSHIP_TYPE())));
		xmlSpecimen.addDarwinTag(objectFactory.createCollectorNumber(specimen
				.getValue(fields.getS_COLLECTION_NUMBER())));
		xmlSpecimen.addDarwinTag(objectFactory.createBasisOfRecord(specimen
				.getValue(fields.getS_BASIS_OF_RECORD())));
		xmlSpecimen.addDarwinTag(objectFactory.createSex(specimen.getValue(fields.getS_SEX())));
		xmlSpecimen.addDarwinTag(objectFactory.createTypeStatus(specimen.getValue(fields.getS_TYPE_STATUS())));
		xmlSpecimen.addDarwinTag(objectFactory.createLifeStage(specimen
				.getValue(fields.getS_DEVELOPMENTAL_STAGE())));
		xmlSpecimen.addDarwinTag(objectFactory.createPreparations(specimen
				.getValue(fields.getS_PREPARATION_TYPE())));
		xmlSpecimen
		.addDarwinTag(objectFactory.createCollector(specimen.getValue(fields.getS_COLLECTOR_NAME())));
		xmlSpecimen.addDarwinTag(objectFactory.createFieldNotes(specimen.getValue(fields.getS_NOTES())));


		xmlSpecimen.addDarwinTag(objectFactory.createEarliestDateCollected(MapSpreadsheetToXml
				.createDate(specimen.getValueDate(fields.getS_EARLIEST_DATE_COLLECTED()))));
		xmlSpecimen.addDarwinTag(objectFactory.createEarliestDateCollected(MapSpreadsheetToXml
				.createDate(specimen.getValueDate(fields.getS_LATEST_DATE_COLLECTED()))));
		



		// Locality properties
		xmlSpecimen.addDarwinTag(objectFactory.createContinent(specimen.getValue(fields.getS_CONTINENT())));
		xmlSpecimen.addDarwinTag(objectFactory.createWaterBody(specimen.getValue(fields.getS_OCEAN())));
		xmlSpecimen.addDarwinTag(objectFactory.createWaterBody(specimen.getValue(fields.getS_WATER_BODY())));
		xmlSpecimen.addDarwinTag(objectFactory.createCountry(specimen.getValue(fields.getS_COUNTRY())));
		xmlSpecimen.addDarwinTag(objectFactory.createStateProvince(specimen
				.getValue(fields.getS_STATE_OR_PROVINCE())));
		xmlSpecimen.addDarwinTag(objectFactory.createCounty(specimen.getValue(fields.getS_COUNTY())));
		xmlSpecimen.addDarwinTag(objectFactory.createLocality(specimen.getValue(fields.getS_LOCALITY())));
		xmlSpecimen.addDarwinTag(objectFactory.createInformationWithheld(specimen
				.getValue(fields.getS_INFORMATION_WITHHELD())));
		xmlSpecimen.addDarwinTag(objectFactory.createDecimalLatitude(getDouble(specimen
				.getValue(fields.getS_LATITUDE()))));
		xmlSpecimen.addDarwinTag(objectFactory.createDecimalLongitude(getDouble(specimen
				.getValue(fields.getS_LONGITUDE()))));
		// xmlSpecimen.addDarwinTag(objectFactory.createCoordinatePrecision(specimen
		// .getValue("Coordinate Precision")));
		xmlSpecimen.addDarwinTag(objectFactory.createMinimumElevationInMeters(getDouble(specimen
				.getValue(fields.getS_MINIMUM_ELEVATION()))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumElevationInMeters(getDouble(specimen
				.getValue(fields.getS_MAXIMUM_ELEVATION()))));

		xmlSpecimen.addDarwinTag(objectFactory.createMinimumDepthInMeters(getDouble(specimen
				.getValue(fields.getS_MINIMUM_DEPTH()))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumDepthInMeters(getDouble(specimen
				.getValue(fields.getS_MAXIMUM_DEPTH()))));
		xmlSpecimen.addUserProperty("Institution", specimen.getValue(fields.getS_INSTITUTION_NAME()));
		xmlSpecimen.addUserProperty("LocalityDescription", specimen.getValue(fields.getS_LOCALITY_DESCRIPTION()));
		Extref extref = MapSpreadsheetToXml.getExternalLink(specimen, "Specimen");
		if (extref != null) xmlSpecimen.addExternalRef(extref);
		if (userProperties != null && userProperties.length >= 1){
			this.addUserProperty(xmlSpecimen);
		}

	}

	//add user properties to xml document in the correct object (specimen)
	//and generates uri if present
	private void addUserProperty(XmlBaseObject xmlSpecimen) {
		for(int i = 1; i < userProperties.length; i++) {
			String property[] = userProperties[i];
			if(property[1].equalsIgnoreCase("specimen")){
				if (property[3] == null || property[3].equalsIgnoreCase("")){ //namespace uri empty
					if (property[2] == null || property[2].equalsIgnoreCase("")){ //property name empty
						xmlSpecimen.addUserProperty(property[0], specimen.getValue(property[0]));
					}
					else { //property name present
						xmlSpecimen.addUserProperty(property[2], specimen.getValue(property[0]));
					}
				}
				else { //namespace uri present
					if (property[2] == null || property[2].equalsIgnoreCase("")){ //property name empty
						xmlSpecimen.addUserProperty(property[0], specimen.getValue(property[0]), property[3]);
					}
					else { //property name present
						xmlSpecimen.addUserProperty(property[2], specimen.getValue(property[0]), property[3]);
					}
				}
			}
		}
	}


	static final String DEGREE = "� ";

	protected String getDegMinSec(String degrees, String minutes, String seconds, String direction) {
		if (degrees == null || degrees.length() < 1) return null;
		String result = degrees + DEGREE + minutes + "' " + seconds + "\" " + direction;
		return result;
	}

	protected Double getDouble(String intStr) {
		if (intStr == null) {
			return null;
		}
		try {
			Double value = Double.valueOf(intStr);
			return value;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	String getValue(int index) {
		return specimen.getValue(index);
	}

	public String[][] getUserProperties() {
		if (this.specimen instanceof XlsFieldMapper)
		{
			Sheet links = ((XlsFieldMapper) this.specimen).getLinks();
			int cols = links.getRow(0).getLastCellNum();
			if (cols != 4) {
				MorphbankConfig.SYSTEM_LOGGER.info("Wrong number of colums in Excel spreadsheet.");
				return null;
			}
			
			cols = 4;
			int rows = links.getLastRowNum();
			userProperties = new String[rows+1][cols+1];
			int i, j;
			i = j = 0;
			for (Row row : links) {
				j = 0;
				for (Cell cell : row) {
					userProperties[i][j] = cell.getStringCellValue();
					j++;
				}
				i++;
			}
			
			
			
			return userProperties;
		}
		return null;
	}


}
