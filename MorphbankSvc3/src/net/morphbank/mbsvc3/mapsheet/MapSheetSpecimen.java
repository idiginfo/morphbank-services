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

import jxl.Cell;
import jxl.Sheet;
import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.fsuherb.MapFsuHerbSpreadsheetToXml;
import net.morphbank.mbsvc3.xml.*;

public class MapSheetSpecimen {

	FieldMapper specimen;
	static ObjectFactory objectFactory = new ObjectFactory();
	private String[][] userProperties = null;

	public MapSheetSpecimen(FieldMapper specimen) {
		this.specimen = specimen;
		this.userProperties = this.getUserProperties();
	}

	public void setXmlSpecimenFields(XmlBaseObject xmlSpecimen) {
		xmlSpecimen.setId(MapSpreadsheetToXml.getSpecimenId(specimen));
		xmlSpecimen.addDescription(specimen.getValue("Specimen Description"));
		xmlSpecimen.addUserProperty("SpecimenDescription", specimen.getValue("Specimen Description"));

		xmlSpecimen.setDetermination(MapSpreadsheetToXml.getTaxonId(specimen));
		xmlSpecimen.addDarwinTag(objectFactory.createIdentifiedBy(specimen
				.getValue("Determined By")));
		xmlSpecimen.addDarwinTag(objectFactory.createDateIdentified(MapSpreadsheetToXml
				.createDate(specimen.getValueDate("Date Determined"))));
		xmlSpecimen.setStandardImage(MapSpreadsheetToXml.getStandarImageId(specimen));///////////////////////////////

		xmlSpecimen.addDarwinTag(objectFactory.createRemarks(specimen
				.getValue("Comment by Determiner")));
		xmlSpecimen.setForm(specimen.getValue("Form"));
		xmlSpecimen.addDarwinTag(objectFactory.createCollectionCode(specimen
				.getValue("Collection Code")));
		xmlSpecimen.addDarwinTag(objectFactory.createInstitutionCode(specimen
				.getValue("Institution Code")));
		xmlSpecimen.addDarwinTag(objectFactory.createCatalogNumber(specimen
				.getValue("Catalog Number")));
		xmlSpecimen.addDarwinTag(objectFactory.createOtherCatalogNumbers(specimen
				.getValue("Previous Catalog Number")));
		xmlSpecimen.addDarwinTag(objectFactory.createRelatedCatalogedItems(specimen
				.getValue("Related Catalog Item")));
		xmlSpecimen.addDarwinTag(objectFactory.createRelationshipType(specimen
				.getValue("Relationship Type")));
		xmlSpecimen.addDarwinTag(objectFactory.createCollectorNumber(specimen
				.getValue("Collection Number")));
		xmlSpecimen.addDarwinTag(objectFactory.createBasisOfRecord(specimen
				.getValue("Basis of Record")));
		xmlSpecimen.addDarwinTag(objectFactory.createSex(specimen.getValue("Sex")));
		xmlSpecimen.addDarwinTag(objectFactory.createTypeStatus(specimen.getValue("Type Status")));
		xmlSpecimen.addDarwinTag(objectFactory.createLifeStage(specimen
				.getValue("Developmental Stage")));
		xmlSpecimen.addDarwinTag(objectFactory.createPreparations(specimen
				.getValue("Preparation Type")));
		xmlSpecimen
		.addDarwinTag(objectFactory.createCollector(specimen.getValue("Collector Name")));
		xmlSpecimen.addDarwinTag(objectFactory.createFieldNotes(specimen.getValue("Notes")));


		xmlSpecimen.addDarwinTag(objectFactory.createEarliestDateCollected(MapSpreadsheetToXml
				.createDate(specimen.getValueDate("Earliest Date Collected"))));
		xmlSpecimen.addDarwinTag(objectFactory.createEarliestDateCollected(MapSpreadsheetToXml
				.createDate(specimen.getValueDate("Latest Date Collected"))));
		



		// Locality properties
		xmlSpecimen.addDarwinTag(objectFactory.createContinent(specimen.getValue("Continent")));
		xmlSpecimen.addDarwinTag(objectFactory.createWaterBody(specimen.getValue("Ocean")));
		xmlSpecimen.addDarwinTag(objectFactory.createWaterBody(specimen.getValue("Water Body")));
		xmlSpecimen.addDarwinTag(objectFactory.createCountry(specimen.getValue("Country")));
		xmlSpecimen.addDarwinTag(objectFactory.createStateProvince(specimen
				.getValue("State Province")));
		xmlSpecimen.addDarwinTag(objectFactory.createCounty(specimen.getValue("County")));
		xmlSpecimen.addDarwinTag(objectFactory.createLocality(specimen.getValue("Locality")));
		xmlSpecimen.addDarwinTag(objectFactory.createInformationWithheld(specimen
				.getValue("Information Withheld")));
		xmlSpecimen.addDarwinTag(objectFactory.createDecimalLatitude(getDouble(specimen
				.getValue("Latitude"))));
		xmlSpecimen.addDarwinTag(objectFactory.createDecimalLongitude(getDouble(specimen
				.getValue("Longitude"))));
		// xmlSpecimen.addDarwinTag(objectFactory.createCoordinatePrecision(specimen
		// .getValue("Coordinate Precision")));
		xmlSpecimen.addDarwinTag(objectFactory.createMinimumElevationInMeters(getDouble(specimen
				.getValue("Minimum Elevation"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumElevationInMeters(getDouble(specimen
				.getValue("Maximum Elevation"))));

		xmlSpecimen.addDarwinTag(objectFactory.createMinimumDepthInMeters(getDouble(specimen
				.getValue("Minimum Depth"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumDepthInMeters(getDouble(specimen
				.getValue("Maximum Depth"))));
		xmlSpecimen.addUserProperty("Institution", specimen.getValue("Institution Name"));
		xmlSpecimen.addUserProperty("LocalityDescription", specimen.getValue("Locality Description"));
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
			int c = links.getColumns();
			if (c != 4) {
				MorphbankConfig.SYSTEM_LOGGER.info("Wrong number of colums in Excel spreadsheet.");
				return null;
			}
			c = 4;
			int r = links.getRows();
			userProperties = new String[r][c];
			for (int i = 0; i < links.getColumns(); i++){
				Cell[] cells = links.getColumn(i);
				for (int j = 0; j < cells.length; j++){
					userProperties[j][i] = cells[j].getContents();
				}
			}
			return userProperties;
		}
		return null;
	}


}
