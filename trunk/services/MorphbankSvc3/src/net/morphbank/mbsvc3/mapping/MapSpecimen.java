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

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import net.morphbank.mbsvc3.xml.ObjectFactory;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.BasisOfRecord;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.Locality;
import net.morphbank.object.MissingLink;
import net.morphbank.object.Specimen;
import net.morphbank.object.Taxon;
import net.morphbank.object.UserProperty;

public class MapSpecimen extends MapObjectBase {

	public MapSpecimen(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapSpecimen(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapSpecimen(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// Methods to map XmlBaseObject to Specimen

	@Override
	public Specimen createObject(XmlBaseObject xmlObject, XmlId xmlId) {
		int id;
		if (xmlId != null) {
			id = xmlId.getMorphbank();
		} else {
			id = xmlObject.getMorphbankId();
		}
		MBCredentials ownerCred = xmlMapper.getOwner(xmlObject);
		MBCredentials submitterCred = xmlMapper.getSubmitter(xmlObject);
		Specimen specimen = new Specimen(id, null, ownerCred.getUser(), submitterCred.getUser(),
				ownerCred.getGroup());
		specimen.persist();
		boolean success = updateObject(specimen, xmlObject);
		if (success) return specimen;
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Specimen)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Specimen specimen = (Specimen) idObject;
		// derived field specimen.setImagesCount(xmlSpecimen.getImagesCount());
		specimen.setForm(xmlObject.getForm());

		String value = null;
		Date date = null;
		try {
			// loop through Darwin Core tags add to model as appropriate
			Iterator<Object> tags = xmlObject.getAny().iterator();
			while (tags.hasNext()) {
				JAXBElement<Object> tag = (JAXBElement<Object>) tags.next();
				String tagName = tag.getName().getLocalPart();
				Object objValue = tag.getValue();
				value = null;
				date = null;
				if (objValue instanceof String) {
					value = (String) objValue;
				} else if (objValue instanceof Date) {
					date = (Date) objValue;
				}
				if (tagName.equals("BasisOfRecord")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setBasisOfRecord(BasisOfRecord.lookupDescription(value));
					}
				} else if (tagName.equals("Sex")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setSex((String) value);
					}
				} else if (tagName.equals("LifeStage")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setDevelopmentalStage(value);
					}
				} else if (tagName.equals("Preparations")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setPreparationType(value);
					}
				} else if (tagName.equals("IndividualCount")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setIndividualCount(Integer.valueOf(value));
					}
				} else if (tagName.equals("TypeStatus")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setTypeStatus(value);
					}
				} else if (tagName.equals("DateIdentified")) {
					if (date != null) {
						specimen.setDateIdentified(date);
					}
				} else if (tagName.equals("IdentifiedBy")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setSpecimenName(value);
					}
				} else if (tagName.equals("Remarks")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setComment(value);
					}
				} else if (tagName.equals("InstitutionCode")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setInstitutionCode(value);
					}
				} else if (tagName.equals("CollectionCode")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setCollectionCode(value);
					}
				} else if (tagName.equals("CatalogNumber")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setCatalogNumber(value);
					}
				} else if (tagName.equals("OtherCatalogNumbers")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setPreviousCatalogNumber(value);
					}
				} else if (tagName.equals("RelatedCatalogItems")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setRelatedCatalogItem(value);
					}
				} else if (tagName.equals("CollectionNumber") || tagName.equals("CollectorNumber")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setCollectionNumber(value);
					}
				} else if (tagName.equals("FieldNumber") || tagName.equals("FieldNumber")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setCollectionNumber(value);
					}
				} else if (tagName.equals("LatestDateCollected")) {
					if (date != null ) {

						specimen.setLatestDateCollected(date);
						if (specimen.getDateCollected() == null){
							specimen.setDateCollected(date); //prefer the earliest date
						}
					}
				} else if (tagName.equals("EarliestDateCollected")) {
					if (date != null) {
						specimen.setDateCollected(date);
						specimen.setEarliestDateCollected(date);
					}
				} else if (tagName.equals("Collector")) {
					if (XmlUtils.notEmptyString(value)) {
						specimen.setCollectorName(value);
					}
				} else if (MapLocality.isLocalityTag(tagName)) {
					// defer to locality
				} else if (ignoreTag(tagName)) {
					// ignore tags from list ignoreList
				} else {// Darwin Core tag not in model
					xmlMapper.addUserProperty(specimen, tag);
				}
			}

			// map locality fields
			Locality localityObj = specimen.getLocality();
			MapLocality localityMapper = (MapLocality) xmlMapper.getMapper("Locality");

			if (localityObj == null) {
				// look for object from morphbank or external id
				localityObj = localityMapper.getLocalityObject(xmlObject);
			}
			if (localityObj != null) {
				localityMapper.updateObject(localityObj, xmlObject);
				specimen.setLocality(localityObj);
			} else { // no existing locality
				XmlId localityId = xmlObject.getLocality();
				// Locality is special case of sub object that must be created
				// here
				// no locality id, create new locality
				localityObj = (Locality) localityMapper.createObject(xmlObject, localityId);
				if (localityObj != null) {
					localityObj.persist();
				} else {
					if (localityId != null) {
						// add localityid to missing links
						xmlMapper.recordMissingLink(specimen, localityId, MissingLink.LOCALITY);
					}
				}
			}
			specimen.setLocality(localityObj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	String[] ignoredList = {"Class", "Family", "Genus", "Kingdom", "Order"};
	
	private boolean ignoreTag(String name) {
		for (int i = 0; i < ignoredList.length; i++) {
			if (ignoredList[i].equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	@Override
	public Specimen createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Specimen specimen = null;
		if (idObject == null) {
			specimen = (Specimen) createObject(xmlObject);
			return specimen;
		}
		if (idObject instanceof Specimen) {
			specimen = (Specimen) idObject;
			if (updateObject(specimen, xmlObject)) return specimen;
		}
		return null;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Specimen)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		Specimen specimen = (Specimen) idObject;
		Locality locality = specimen.getLocality();
		if (locality != null) {
			xmlObject.setLocality(new XmlId(locality.getId()));
			if (!objMapper.setXmlFields(xmlObject, specimen.getLocality())) return false;
		}
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (!(idObject instanceof Specimen)) return false;
		Specimen specimen = (Specimen) idObject;
		if (specimen.getStandardImage() != null) {
			xmlObject.setStandardImage(new XmlId(specimen.getStandardImage().getId()));
		}
		if (specimen.getImagesCount() != null) {
			xmlObject.setImagesCount(specimen.getImagesCount());
		}
		if (specimen.getForm() != null) {
			xmlObject.setForm(specimen.getForm());
		}
		// TODO ocr barCode labelData
		if (specimen.getStandardImage() != null) {
			xmlObject.setStandardImage(objMapper.createXmlId(specimen.getStandardImage(), null));
		}

		xmlObject.setDetermination(XmlServices.getTaxonId(specimen.getTaxon()));

		ObjectFactory objectFactory = MapObjectToResponse.getObjectFactory();
		// Add Darwin Core fields
		xmlObject.addDarwinTag(objectFactory.createSex(specimen.getSex()));
		xmlObject.addDarwinTag(objectFactory.createLifeStage(specimen.getDevelopmentalStage()));
		xmlObject.addDarwinTag(objectFactory.createCollector(specimen.getCollectorName()));
		xmlObject.addDarwinTag(objectFactory.createCollectionCode(specimen.getCollectionCode()));
		xmlObject.addDarwinTag(objectFactory.createCatalogNumber(specimen.getCatalogNumber()));
		// xmlSpecimen.addDarwinGregorianCalendarTag(ObjectFactory
		// ._EarliestDateCollected_QNAME, specimen.getDateCollected());
		JAXBElement<Date> element = objectFactory.createEarliestDateCollected(specimen
				.getDateCollected());

		xmlObject.addDarwinTag(element);

		// TODO xmlSpecimen.addDarwinTag(XmlSpecimen.DC_CUR_NAMESPACE,
		// "LatestDateCollected", specimen.getDateCollected());

		xmlObject.addDarwinTag(objectFactory.createBasisOfRecord(specimen.getBasisOfRecordDesc()));
		xmlObject.addDarwinTag(objectFactory.createInstitutionCode(specimen.getInstitutionCode()));
		// Add Darwin Core Curatorial fields
		// TODO previousCatalogNumber relatedCatalogItem relationshipType
		// collectionNumber? notes
		xmlObject.addDarwinTag(objectFactory.createPreparations(specimen.getPreparationType()));
		xmlObject.addDarwinTag(objectFactory.createIndividualCount(specimen.getIndividualCount()));
		xmlObject.addDarwinTag(objectFactory.createTypeStatus(specimen.getTypeStatus()));
		xmlObject.addDarwinTag(objectFactory.createIndividualCount(specimen.getIndividualCount()));
		xmlObject.addDarwinTag(objectFactory.createTypeStatus(specimen.getTypeStatus()));
		xmlObject.addDarwinTag(objectFactory.createDateIdentified(specimen.getDateIdentified()));
		xmlObject.addDarwinTag(objectFactory.createIdentifiedBy(specimen.getName()));
		objMapper.addTaxonomicTags(xmlObject, specimen);
		return true;
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object, XmlBaseObject responseObject) {
		if (object == null) return true;
		if (!(object instanceof Specimen)) return false;
		if (!super.linkObject(xmlObject, object, responseObject)) return false;
		Specimen specimen = (Specimen) object;

		// map standard image
		XmlId standardImageId = xmlObject.getStandardImage();
		Image standardImage = null;

		/* fix for cast problem with BaseObject that are not Image */
		BaseObject related = xmlMapper.getBaseObject(standardImageId);
		if (related == null) {
			// no specimen in database
			xmlObject
			.setStatus("specimen id not found in database, info stored in MissingLink table ");
			xmlMapper.recordMissingLink(specimen, standardImageId, MissingLink.SPECIMEN);
		}
		else {
			if (!(related instanceof Image)) {

				xmlObject.setStatus("specimen id refers to an object of type "
						+ related.getObjectTypeIdStr());
				return false;
			}
			//previous code
			//		if (standardImageId != null) {
			//			// set the standard image after the image is created!
			//			standardImage = (Image) xmlMapper.getBaseObject(standardImageId);
			//		}
			if (standardImageId != null) {
				// set the standard image after the image is created!
				standardImage = (Image) related;
			}
			/* end of fix */
		}
		//TODO use tag standard image in xml document
		if (specimen != null && specimen.getImages() != null) {
			Iterator<Image> images = specimen.getImages().iterator();
			if (images.hasNext()) {
				standardImage = images.next();
			}
		}
		if (standardImage != null) {
			specimen.setStandardImage(standardImage);
		} else {
			// missing
			xmlMapper.recordMissingLink(specimen, standardImageId,
					MissingLink.SPECIMEN_STANDARD_IMAGE);
		}
		// map determination as taxon
		XmlId determinationId = xmlObject.getDetermination();
		Taxon determination = MapTaxon.getTaxon(determinationId);
		// look in user properties to find taxon
//		Map<String, UserProperty> userProperties = specimen.getUserProperties();
//		UserProperty scientificName;
//		UserProperty author;
//		if (determination == null && (scientificName = userProperties.get("ScientificName")) != null && 
//				(author = userProperties.get("AuthorYearOfScientificName")) != null) {
//			determination = Taxon.getTaxon(scientificName.getValue(), author.getValue());
//		}
		if (determination != null) {
			specimen.setTaxon(determination);
			specimen.setTaxonomicNamesFromDetermination();
			xmlObject.addStatus("Determination for " + determinationId.getFirstExternal()
					+ " tsn: " + determination.getTsn());

		}
		else { // determination not found
			// TODO add to missinglinks
			xmlMapper.recordMissingLink(specimen, determinationId,
					MissingLink.SPECIMEN_DETERMINATION);

			specimen.setTaxon(Taxon.getTaxon(0));
			// TODO add note in status field of response
			if (responseObject != null) {
				responseObject.addStatus("No determination found for " + determinationId.getFirstExternal()
						+ ", assigned Life");
			}
			xmlMapper.reportSuccess(xmlObject, (IdObject) xmlObject.getTaxon()); 
		}
		// TODO calculate number of images
		return true;
	}
}
