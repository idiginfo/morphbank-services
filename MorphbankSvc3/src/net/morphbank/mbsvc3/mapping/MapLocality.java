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
package net.morphbank.mbsvc3.mapping;

import java.util.HashSet;
import java.util.Set;

import net.morphbank.mbsvc3.xml.ObjectFactory;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Locality;

public class MapLocality extends MapObjectBase {

	public MapLocality(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapLocality(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapLocality(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// copies of fields included for convenience in processing
	protected XmlId localityId = null;
	protected String continentOcean;
	protected String continent;
	protected String ocean;
	protected String country;
	protected String stateProvince;
	protected String county;
	protected Integer coordinatePrecision;
	protected Double latitude;
	protected Double longitude;
	protected String verbatimLatitude;
	protected String verbatimLongitude;
	protected Integer maximumElevation;
	protected Integer minimumElevation;
	protected String locality;
	protected Integer minimumDepth;
	protected Integer maximumDepth;
	protected String informationWithheld;

	protected static final String AND = " and ";
	protected String and = "";

	public void init(XmlBaseObject xmlObject) {
		localityId = xmlObject.getLocality();
		continentOcean = xmlObject.getFirstTagValue("ContinentOcean");
		continent = xmlObject.getFirstTagValue("Continent");
		ocean = xmlObject.getFirstTagValue("WaterBody");
		country = xmlObject.getFirstTagValue("Country");
		county = xmlObject.getFirstTagValue("County");
		stateProvince = xmlObject.getFirstTagValue("StateProvince");
		coordinatePrecision = getInteger(xmlObject
				.getFirstTagValue("CoordinateUncertaintyInMeters"));
		// TODO process Decimal Verbatim latitude and longitude
		latitude = getDouble(xmlObject.getFirstTagValue("DecimalLatitude"));
		longitude = getDouble(xmlObject.getFirstTagValue("DecimalLongitude"));
		verbatimLatitude = xmlObject.getFirstTagValue("VerbatimLatitude");
		if (verbatimLatitude != null && latitude == null) {
			try {
				Double lat = LatLonConverter.decimalDegrees(verbatimLatitude);
				latitude = lat;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		verbatimLongitude = xmlObject.getFirstTagValue("VerbatimLongitude");
		if (verbatimLongitude != null && longitude == null) {
			try {
				Double lon = LatLonConverter.decimalDegrees(verbatimLongitude);
				longitude = lon;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		maximumElevation = getInteger(xmlObject.getFirstTagValue("MaximumElevationInMeters"));
		minimumElevation = getInteger(xmlObject.getFirstTagValue("MinimumElevationInMeters"));
		locality = xmlObject.getFirstTagValue("Locality");
		minimumDepth = getInteger(xmlObject.getFirstTagValue("MinimumDepthInMeters"));
		maximumDepth = getInteger(xmlObject.getFirstTagValue("MaximumDepthInMeters"));
		informationWithheld = xmlObject.getFirstTagValue("InformationWithheld");
	}

	public void init(Locality localityObj) {
		continentOcean = localityObj.getContinentOcean();
		continent = localityObj.getContinent();
		ocean = localityObj.getOcean();
		stateProvince = localityObj.getStateProvince();
		country = localityObj.getCountry();
		county = localityObj.getCounty();
		coordinatePrecision = localityObj.getCoordinatePrecision();
		latitude = localityObj.getLatitude();
		longitude = localityObj.getLongitude();
		verbatimLatitude = localityObj.getFirstUserProperty("VerbatimLatitude");
		verbatimLongitude = localityObj.getFirstUserProperty("VerbatimLongitude");
		maximumElevation = localityObj.getMaximumElevation();
		minimumElevation = localityObj.getMinimumElevation();
		locality = localityObj.getLocality();
		minimumDepth = localityObj.getMinimumDepth();
		maximumDepth = localityObj.getMaximumDepth();
		informationWithheld = localityObj.getInformationWithheld();
	}

	@Override
	public Locality createObject(XmlBaseObject xmlObject, XmlId xmlId) {
		int id;
		if (xmlId != null) {
			id = xmlId.getMorphbank();
		} else {
			id = xmlObject.getMorphbankId();
		}
		String name = xmlObject.getName();
		if (name != null && name.length() == 0) name = null;
		MBCredentials ownerCred = xmlMapper.getOwner(xmlObject);
		MBCredentials submitterCred = xmlMapper.getSubmitter(xmlObject);
		Locality locality = new Locality(id, name, ownerCred.getUser(), submitterCred.getUser(),
				ownerCred.getGroup());
		init(xmlObject);
		boolean success = updateObject(locality, xmlObject);
		if (isEmpty()) return null;
		locality.persist();
		if (success) return locality;
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Locality)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Locality localityObj = (Locality) idObject;
		init(xmlObject);
		boolean update = false;
		try {
			if (this.locality != null) {
				localityObj.setLocality(locality);
				update = true;
			}
			if (continentOcean != null) {
				localityObj.setContinentOcean(continentOcean);
				update = true;
			}
			if (continent != null) {
				localityObj.setContinent(continent);
				update = true;
			}
			if (ocean != null) {
				localityObj.setOcean(ocean);
				update = true;
			}
			if (country != null) {
				localityObj.setCountry(country);
				update = true;
			}
			if (stateProvince != null) {
				localityObj.setStateProvince(stateProvince);
				update = true;
			}
			if (county != null) {
				localityObj.setCounty(county);
				update = true;
			}
			if (coordinatePrecision != null) {
				localityObj.setCoordinatePrecision(coordinatePrecision);
				update = true;
			}
			if (latitude != null) {
				localityObj.setLatitude(latitude);
				update = true;
			}
			if (longitude != null) {
				localityObj.setLongitude(longitude);
				update = true;
			}
			if (maximumElevation != null) {
				localityObj.setMaximumElevation(maximumElevation);
				update = true;
			}
			if (minimumElevation != null) {
				localityObj.setMinimumElevation(minimumElevation);
				update = true;
			}
			if (maximumDepth != null) {
				localityObj.setMaximumDepth(maximumDepth);
				update = true;
			}
			if (minimumDepth != null) {
				localityObj.setMinimumDepth(minimumDepth);
				update = true;
			}
			if (verbatimLatitude != null) {
				localityObj.addUserProperty("VerbatimLatitude", verbatimLatitude,
						ObjectFactory._VerbatimLatitude_QNAME.getNamespaceURI());
				update = true;
			}
			if (verbatimLongitude != null) {
				localityObj.addUserProperty("VerbatimLongitude", verbatimLongitude,
						ObjectFactory._VerbatimLongitude_QNAME.getNamespaceURI());
				update = true;
			}
			if (this.informationWithheld != null) {
				localityObj.setInformationWithheld(informationWithheld);
				update = true;
			}
			if (update) {
				localityObj.updateDateLastModified();
			}

		} catch (Exception e) {
			// allow the error of type conversion
		}
		return true;
	}

	@Override
	public Locality createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Locality locality;
		if (idObject == null) {
			locality = (Locality) createObject(xmlObject);
			return locality;
		} else if (idObject instanceof Locality) {
			locality = (Locality) idObject;
			boolean updateSuccess = updateObject(locality, xmlObject);
			if (updateSuccess) return locality;
		}
		return null;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Locality)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// Locality locality = (Locality) idObject;
		return true;
	}

	/**
	 * set the locality fields of xmlSpecimen
	 */
	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Locality)) return false;
		Locality locality = (Locality) idObject;
		init(locality);
		xmlObject.addDarwinTag(objectFactory.createContinent(continent));
		xmlObject.addDarwinTag(objectFactory.createWaterBody(ocean));
		xmlObject.addDarwinTag(objectFactory.createCountry(country));
		xmlObject.addDarwinTag(objectFactory.createStateProvince(stateProvince));
		xmlObject.addDarwinTag(objectFactory.createCounty(county));
		xmlObject.addDarwinTag(objectFactory
				.createCoordinateUncertaintyInMeters(coordinatePrecision));
		xmlObject.addDarwinTag(objectFactory.createDecimalLatitude(latitude));
		xmlObject.addDarwinTag(objectFactory.createDecimalLongitude(longitude));
		if (maximumElevation != null) {
			xmlObject.addDarwinTag(objectFactory.createMaximumElevationInMeters(Double
					.valueOf(maximumElevation.doubleValue())));
		}
		if (minimumElevation != null) {
			xmlObject.addDarwinTag(objectFactory.createMinimumElevationInMeters(Double
					.valueOf(minimumElevation.doubleValue())));
		}
		xmlObject.addDarwinTag(objectFactory.createLocality(this.locality));
		if (maximumDepth != null) {
			xmlObject.addDarwinTag(objectFactory.createMaximumDepthInMeters(Double
					.valueOf(maximumDepth.doubleValue())));
		}
		if (minimumDepth != null) {
			xmlObject.addDarwinTag(objectFactory.createMinimumDepthInMeters(Double
					.valueOf(minimumDepth.doubleValue())));
		}
		if (verbatimLatitude != null) {
			xmlObject.addDarwinTag(objectFactory.createVerbatimLatitude(verbatimLatitude));
		}
		if (verbatimLongitude != null) {
			xmlObject.addDarwinTag(objectFactory.createVerbatimLongitude(verbatimLongitude));
		}
		if (informationWithheld != null) {
			xmlObject.addDarwinTag(objectFactory.createInformationWithheld(informationWithheld));
		}
		return true;
	}

	protected Integer getInteger(String intStr) {
		if (intStr == null) {
			return null;
		}
		try {
			Integer value = Integer.valueOf(intStr);
			return value;
		} catch (NumberFormatException e) {

		}
		try {
			double dValue = Double.valueOf(intStr);
			int intVal = (int) dValue;
			return intVal;
		} catch (Exception e) {

		}
		return null;
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

	public boolean isEmpty() {
		if (null != continentOcean) return false;
		if (null != continent) return false;
		if (null != ocean) return false;
		if (null != country) return false;
		if (null != stateProvince) return false;
		if (null != county) return false;
		if (null != coordinatePrecision) return false;
		if (null != latitude) return false;
		if (null != longitude) return false;
		if (null != verbatimLatitude) return false;
		if (null != verbatimLongitude) return false;
		if (null != maximumElevation) return false;
		if (null != minimumElevation) return false;
		if (null != locality) return false;
		if (null != minimumDepth) return false;
		if (null != maximumDepth) return false;
		if (null != informationWithheld) return false;
		return true;
	}

	protected static final String[] LOCALITY_TAG_STRINGS = { "Country", "StateProvince", "County",
		"Continent", "WaterBody", "CoordinateUncertaintyInMeters", "DecimalLatitude",
		"DecimalLongitude", "MaximumElevationInMeters", "MinimumElevationInMeters", "Locality",
		"MinimumDepthInMeters", "MaximumDepthInMeters", "VerbatimLatitude", "VerbatimLongitude", "InformationWithheld" };
	protected static Set<String> LOCALITY_TAGS = new HashSet<String>();

	static {
		for (int i = 0; i < LOCALITY_TAG_STRINGS.length; i++)
			LOCALITY_TAGS.add(LOCALITY_TAG_STRINGS[i]);
	}

	/**
	 * Return true if tagName is a Darwin Core tag that is part of the Locality
	 * object
	 * 
	 * @param tagName
	 * @return
	 */
	public static boolean isLocalityTag(String tagName) {
		return LOCALITY_TAGS.contains(tagName);
	}

	/**
	 * Get a related locality object, if one can be found put object into field
	 * localityObj
	 * 
	 * @param xmlSpecimen
	 * @return id of object
	 */
	public Locality getLocalityObject(XmlBaseObject xmlSpecimen) {
		XmlId xmlId = xmlSpecimen.getLocality();
		int localityId = 0;
		if (xmlId != null) localityId = xmlId.getMorphbank();
		if (localityId > 0) {
			BaseObject obj = BaseObject.getEJB3Object(localityId);
			return (Locality) obj;
		}
		return null;
	}
}
