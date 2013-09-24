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

import net.morphbank.mbsvc3.xml.*;

public class MapFsuHerbSpecimen {

	FieldMapper specimen;
	static ObjectFactory objectFactory = new ObjectFactory();

	public MapFsuHerbSpecimen(FieldMapper specimen) {
		this.specimen = specimen;
	}

	public void setXmlSpecimenFields(XmlBaseObject xmlSpecimen) {

		xmlSpecimen.setId(MapFsuHerbSpreadsheetToXml.getSpecimenId(specimen));
		xmlSpecimen.setDetermination(MapFsuHerbSpreadsheetToXml.getTaxonId(specimen));
		xmlSpecimen.addDarwinTag(objectFactory.createRemarks(specimen
				.getValue("Comment by Determiner")));
		xmlSpecimen.addDarwinTag(objectFactory.createCollectionCode(specimen
				.getValue("Collection Code")));
		xmlSpecimen.addDarwinTag(objectFactory.createInstitutionCode(specimen
				.getValue("Institution Code")));
		xmlSpecimen.addDarwinTag(objectFactory.createCatalogNumber(specimen
				.getValue("Catalog Number")));
		xmlSpecimen.addDarwinTag(objectFactory.createBasisOfRecord(specimen
				.getValue("Basis of Record")));
		String typeStatus = specimen.getValue("Type Status");
		if (typeStatus== null || typeStatus.length()==0) typeStatus = "nontype";
		xmlSpecimen.addDarwinTag(objectFactory.createTypeStatus(typeStatus));
		xmlSpecimen
				.addDarwinTag(objectFactory.createCollector(specimen.getValue("Collector Name")));
		xmlSpecimen.addDarwinTag(objectFactory.createCollectorNumber(specimen
				.getValue("Collection Number")));
		xmlSpecimen.addDarwinTag(objectFactory.createOtherCatalogNumbers(specimen
				.getValue("Previous Catalog Number")));
		xmlSpecimen.addDarwinTag(objectFactory
				.createEarliestDateCollected(MapFsuHerbSpreadsheetToXml.createDate(specimen
						.getValue("Date Collected"))));
		xmlSpecimen.addDarwinTag(objectFactory.createLatestDateCollected(MapFsuHerbSpreadsheetToXml
				.createDate(specimen.getValue("Date Collected"))));
		// Locality properties
		xmlSpecimen.addDarwinTag(objectFactory.createContinent(specimen.getValue("Continent")));
		xmlSpecimen.addDarwinTag(objectFactory.createWaterBody(specimen.getValue("Ocean")));
		xmlSpecimen.addDarwinTag(objectFactory.createCountry(specimen.getValue("Country")));
		xmlSpecimen.addDarwinTag(objectFactory.createStateProvince(specimen
				.getValue("State Province")));
		xmlSpecimen.addDarwinTag(objectFactory.createCounty(specimen.getValue("County")));
		xmlSpecimen.addDarwinTag(objectFactory.createLocality(specimen.getValue("Locality")));
		xmlSpecimen.addDarwinTag(objectFactory.createInformationWithheld(specimen
				.getValue("Information Withheld")));
		// latitude add '-' if necessary for direction
		String latDec = specimen.getValue("Latitude");
		String latDir = specimen.getValue("latdir");
		if (latDec.length() > 0 && !latDec.startsWith("-") && ("S".equals(latDir)))
			latDec = "-" + latDec;
		xmlSpecimen.addDarwinTag(objectFactory.createDecimalLatitude(getDouble(latDec)));
		String lat = getDegMinSec(specimen.getValue("latdeg"), specimen.getValue("latmin"),
				specimen.getValue("latsec"), latDir);
		xmlSpecimen.addDarwinTag(objectFactory.createVerbatimLatitude(lat));

		// longitude add '-' if necessary for direction
		String lonDec = specimen.getValue("Longitude");
		String lonDir = specimen.getValue("londir");
		if (lonDec.length() > 0 && !latDec.startsWith("-") && !("E".equals(lonDir)))
			lonDec = "-" + lonDec;
		String lon = getDegMinSec(specimen.getValue("londeg"), specimen.getValue("lonmin"),
				specimen.getValue("lonsec"), lonDir);
		xmlSpecimen.addDarwinTag(objectFactory.createVerbatimLongitude(lon));
		xmlSpecimen.addDarwinTag(objectFactory.createDecimalLongitude(getDouble(lonDec)));
		xmlSpecimen.addDarwinTag(objectFactory.createMinimumDepthInMeters(getDouble(specimen
				.getValue("Minimum Depth"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumDepthInMeters(getDouble(specimen
				.getValue("Maximum Depth"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMinimumDepthInMeters(getDouble(specimen
				.getValue("Depth"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumDepthInMeters(getDouble(specimen
				.getValue("Depth"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumElevationInMeters(getDouble(specimen
				.getValue("Maximum Elevation"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMinimumElevationInMeters(getDouble(specimen
				.getValue("Minimum Elevation"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMaximumElevationInMeters(getDouble(specimen
				.getValue("Elevation"))));
		xmlSpecimen.addDarwinTag(objectFactory.createMinimumElevationInMeters(getDouble(specimen
				.getValue("Elevation"))));

		// user properties
		xmlSpecimen.addUserProperty("Habitat", specimen.getValue("Habitat"));
		xmlSpecimen.addUserProperty("Morphology Observations", specimen
				.getValue("Morphology Observations"));
		xmlSpecimen.addUserProperty("Notes", specimen.getValue("Notes"));

		String value = specimen.getValue("Flower Buds Present");
		if (value.equals("1")) xmlSpecimen.addUserProperty("Flower Buds Present", "true");
		value = specimen.getValue("Flowers Present");
		if (value.equals("1")) xmlSpecimen.addUserProperty("Flowers Present", "true");
		value = specimen.getValue("Fruit Present");
		if (value.equals("1")) xmlSpecimen.addUserProperty("Fruit Present", "true");

		xmlSpecimen.addExternalRef(new Extref(specimen.getValue("extLinkTypeId"), specimen
				.getValue("ExternalLinkObject_label"),
				specimen.getValue("SpecimenExtLink_urlData"), null, null));

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

}
