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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.morphbank.mbsvc3.xml.*;
import net.morphbank.object.*;

public class MapObjectToResponse {

	Dictionary<String, MapObject> mappers = new Hashtable<String, MapObject>();

	// protected MapToResponse mapResponse;
	protected Credentials submitter;
	protected MBCredentials credentials;
	protected Response response;

	static String ITIS_PREFIX = "ITIS:";

	protected static ObjectFactory objectFactory = new ObjectFactory();

	public MapObjectToResponse() {
		mappers.put("Annotation", new MapAnnotation(this));
		mappers.put("DeterminationAnnotation", getMapper("Annotation"));
		mappers.put("Collection", new MapCollection(this));
		mappers.put("Group", new MapGroup(this));
		mappers.put("Image", new MapImage(this));
		mappers.put("Locality", new MapLocality(this));
		// mappers.put("Otu",new MapOtu(this));
		mappers.put("Publication", new MapPublication(this));
		mappers.put("Specimen", new MapSpecimen(this));
		mappers.put("Taxon", new MapTaxon(this));
		mappers.put("TaxonConcept", new MapTaxonConcept(this));
		mappers.put("View", new MapView(this));
		mappers.put("User", new MapUser(this));

	}

	public MapObjectToResponse(Response resp) {
		this();
		this.response = resp;
	}

	public MapObjectToResponse(String requestType, String description) {
		this();
		this.response = createResponse((MBCredentials) null, requestType, description);
	}

	public MapObjectToResponse(BaseObject obj, String requestType, String description) {
		this();
		this.response = createResponse(obj, requestType, description);
	}

	public Response createResponse(MBCredentials credentials, String requestType, String description) {
		response = new Response();
		RequestSummary reqSummary = new RequestSummary();
		response.setRequestSummary(reqSummary);
		if (credentials != null) {
			submitter = new Credentials();
			if (credentials.getUser() != null) {
				submitter.setUserId(credentials.getUser().getId());
				submitter.
				setGroupId(
						credentials.
						getGroup().
						getGroupId());
			}
			reqSummary.setSubmitter(submitter);
		}
		reqSummary.setRequestType(requestType);
		reqSummary.setDescription(description);
		return response;
	}

	public Response createResponse(BaseObject obj, String requestType, String description) {
		return createResponse(new MBCredentials(obj), requestType, description);
	}

	public XmlBaseObject addObject(IdObject obj) {
		if (obj == null) return null;
		XmlBaseObject xmlObj = null;
		String objectType = obj.getClass().getSimpleName();
		MapObject mapper = mappers.get(objectType);
		if (mapper == null) return null;
		xmlObj = mapper.createXmlObject((BaseObject) obj);
		try {
			mapper.updateXml(xmlObj, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getXmlObjectList().add(xmlObj);
		return xmlObj;
	}

	/**
	 * add appropriate related objects to response
	 * 
	 * @param obj
	 * @return
	 */
	public int addRelatedObjects(IdObject obj) {
		XmlBaseObject xmlObj = null;
		if (obj == null) return 0;
		int numRelated = 0;
		if (obj instanceof Image) {
			// add specimen and view
			xmlObj = mappers.get("Specimen").createXmlObject(((Image) obj).getSpecimen());
			if (xmlObj != null) {
				response.getXmlObjectList().add(xmlObj);
				numRelated++;
			}
			xmlObj = mappers.get("View").createXmlObject(((Image) obj).getView());
			if (xmlObj != null) {
				response.getXmlObjectList().add(xmlObj);
				numRelated++;
			}
			// TODO add other related objects including extra views
		} else if (obj instanceof Specimen) {
			// add standard image
			xmlObj = mappers.get("Image").createXmlObject(((Specimen) obj).getStandardImage());
			if (xmlObj != null) {
				response.getXmlObjectList().add(xmlObj);
				numRelated++;
				// TODO add other related objects including parent specimen
			}
		} else if (obj instanceof Taxon) {
			// no related objects
		} else if (obj instanceof View) {
			// add standard image
			xmlObj = mappers.get("Image").createXmlObject(((View) obj).getStandardImage());
			if (xmlObj != null) {
				response.getXmlObjectList().add(xmlObj);
				numRelated++;
			}
			// TODO add other related objects including extra views
		}
		// TODO add collection objects
		if (obj instanceof BaseObject) {
			BaseObject object = (BaseObject) obj;
			Iterator<CollectionObject> collObjects = object.getObjects().iterator();
			while (collObjects.hasNext()) {
				CollectionObject collObj = collObjects.next();
				BaseObject relatedObj = collObj.getObject();
				XmlBaseObject relatedXmlObj = addObject(relatedObj);
				// TODO produce XML object for related object
				response.getXmlObjectList().add(relatedXmlObj);
				numRelated++;
			}
		}
		return numRelated;
	}

	/**
	 * Create the response a request to insert an object
	 * 
	 * @param sourceXmlObject
	 *            the object to be inserted
	 * @param obj
	 *            the Database object
	 * @return
	 */
	public XmlBaseObject createResponseObject(XmlBaseObject sourceXmlObject, IdObject obj) {
		XmlBaseObject xmlObj = null;
		if (sourceXmlObject == null) return new XmlBaseObject();
		String objectType = sourceXmlObject.getObjectTypeId();
		xmlObj = new XmlBaseObject(objectType);
		setId(xmlObj, obj, sourceXmlObject.getLocalId());

		// add status and localid
		xmlObj.setStatus(sourceXmlObject.getStatus());
		response.getXmlObjectList().add(xmlObj);
		return xmlObj;
	}

	public void setId(XmlBaseObject xmlObj, IdObject obj, String localId) {
		XmlId xmlId = createXmlId(obj, localId);
		if (obj != null & obj instanceof BaseObject) {
			xmlId.setMorphbank(obj.getId());
		} else if (obj != null && obj instanceof Taxon) {
			xmlId.addExternal(ITIS_PREFIX + obj.getId());
		}
		xmlObj.setSourceId(xmlId);
		// xmlId.setMorphbank(obj.getId());
		return;
	}

	public XmlId createXmlId(IdObject obj, String localId) {
		// initialize Id object
		XmlId xmlId = null;
		if (obj instanceof Taxon) { // create a good taxon id with concept and
			// scientific name
			Taxon taxon = (Taxon) obj;
			List<TaxonConcept> concept = taxon.getTaxonConcept();
			int mbId = 0;
			if (concept != null && concept.size() > 0) mbId = concept.get(0).getId();
			xmlId = XmlTaxonNameUtilities.getTaxonId(taxon.getScientificName(), taxon.getTsn(),
					mbId);
			xmlId.addExternal(ITIS_PREFIX + obj.getId());
		} else {
			xmlId = createXmlId(obj, localId, null, null);
		}
		return xmlId;
	}

	private XmlId createXmlId(IdObject obj, String localId, String objectRole, String objectTypeId) {
		// TODO Auto-generated method stub
		XmlId xmlId = new XmlId();

		if (obj != null) xmlId.setMorphbank(obj.getId());
		// add external links to Id object
		if (obj instanceof BaseObject) {
			Iterator<ExternalLinkObject> links = ((BaseObject) obj).getExternalLinks().iterator();
			while (links.hasNext()) {
				ExternalLinkObject link = links.next();
				if (link.getExternalId() != null) {
					xmlId.addExternal(link.getExternalId());
				}
			}
		}
		xmlId.setLocal(localId);
		xmlId.setRole(objectRole);
		xmlId.setObjectType(objectTypeId);
		return xmlId;
	}

	public boolean setXmlBaseObjectFields(XmlBaseObject xmlObj, IdObject idObject) {
		if (!(idObject instanceof BaseObject) || idObject == null) {
			// TODO handle Taxon
			return false;
		}
		BaseObject obj = (BaseObject) idObject;
		setId(xmlObj, obj, null);
		xmlObj.setObjectTypeId(obj.getObjectTypeIdStr());
		xmlObj.setOwner(new Credentials(obj.getUser().getId(), obj.getUser().getUin(), obj
				.getGroup().getId(), obj.getGroup().getGroupName()));
		xmlObj.setDateCreated(obj.getDateCreated());
		xmlObj.setDateLastModified(obj.getDateLastModified());
		xmlObj.setDateToPublish(obj.getDateToPublish());
		xmlObj.setName(obj.getName());
		xmlObj.setDescription(obj.getDescription());
		xmlObj.setHostServer(obj.getHostServer());
		User sub = obj.getUser();
		String userName = sub.getUserName();
		xmlObj.setSubmittedBy(new Credentials(obj.getUser().getId(), obj.getUser().getUin()));
		xmlObj.setObjectLogo(obj.getObjectLogo());
		xmlObj.setThumbUrl(obj.getFullThumbURL());
		xmlObj.setDetailPageUrl(obj.getUrl());

		Iterator<CollectionObject> relatedObjects = obj.getObjects().iterator();
		while (relatedObjects.hasNext()) {
			CollectionObject collObj = relatedObjects.next();
			XmlId xmlId = createXmlId(collObj.getObject(), null, collObj.getObjectRole(), collObj
					.getObjectTypeId());
			xmlObj.addRelatedObject(xmlId);
		}
		// add external link objects
		Iterator<ExternalLinkObject> extLinks = obj.getExternalLinks().iterator();
		while (extLinks.hasNext()) {
			XmlServices.addExternalRef(xmlObj, extLinks.next());
		}
		// add user defined properties
		Iterator<Map.Entry<String, UserProperty>> fields = obj.getUserProperties().entrySet()
				.iterator();
		while (fields.hasNext()) {
			// TODO handle fields that are date valued!
			UserProperty field = fields.next().getValue();
			if (MapLocality.isLocalityTag(field.getName())) {
				// skip these
				continue;
			}
			String namespaceURI = field.getNamespaceURI();
			if (namespaceURI != null) {
				QName qname = new QName(namespaceURI, field.getName());
				JAXBElement tag = null;
				if (isSpecialField(DATE_FIELD_NAMES, field.getName())) {
					try {
						Date dateValue = FORMATTER.parse(field.getValue());
						tag = new JAXBElement<Date>(qname, Date.class, dateValue);
					} catch (Exception e) {
						tag = null;
					}
				} else if (isSpecialField(DOUBLE_FIELD_NAMES, field.getName())) {
					try {
						Double value = Double.valueOf(field.getValue());
						tag = new JAXBElement<Double>(qname, Double.class, value);
					} catch (Exception e) {
						tag = null;
					}
				} else {
					tag = new JAXBElement<String>(qname, String.class, field.getValue());
				}
				xmlObj.getAny().add(tag);
			} else {
				xmlObj.addUserProperty(field.getName(), field.getValue());
			}
		}
		return true;
	}

	public void addTaxonomicTags(XmlBaseObject xmlObj, Specimen specimen) {
		if (specimen==null) return;
		Taxon taxon = specimen.getTaxon();
		addTaxonomicTags(xmlObj, taxon);
	}

	public void addTaxonomicTags(XmlBaseObject xmlObj, Taxon taxon) {
		// add tree nodes as properties
		if (taxon == null) return;
		// add Darwin Core ranks above species
		addRankProperty(xmlObj, taxon, "Kingdom");
		addRankProperty(xmlObj, taxon, "Phylum");
		addRankProperty(xmlObj, taxon, "Class");
		addRankProperty(xmlObj, taxon, "Order");
		addRankProperty(xmlObj, taxon, "Family");
		addRankProperty(xmlObj, taxon, "Genus");
		// extract and add specific epithet from species name
		TaxonBranchNode node = (TaxonBranchNode) (taxon.getTaxonNodes().get("Species"));
		if (node != null) {
			String speciesName = node.getName();
			int blank = speciesName.indexOf(' ');
			if (blank < speciesName.length() - 1) {
				String specificEpithet = speciesName.substring(blank + 1);
				xmlObj.addDarwinTag(objectFactory.createSpecificEpithet(specificEpithet));
			}
		}
		// add scientific name
		xmlObj.addDarwinTag(objectFactory.createScientificName(taxon.getScientificName()));
	}

	/**
	 * Add Darwin core taxonomic names property a specific rank
	 * 
	 * @param res
	 * @param rankName
	 * @param propertyName
	 */
	public void addRankProperty(XmlBaseObject xmlObj, Taxon taxon, String rankName) {
		if (taxon == null) return;
		TaxonBranchNode node;
		if (taxon.getTaxonNodes() == null) return;
		try {
			node = (TaxonBranchNode) (taxon.getTaxonNodes().get(rankName));
			if (node != null) {
				QName rankQName = new QName("http://rs.tdwg.org/dwc/dwcore/", rankName);
				JAXBElement<String> tag = new JAXBElement<String>(rankQName, String.class, null,
						node.getName());
				xmlObj.addDarwinTag(tag);
			}
		} catch (Exception e) {
			e.printStackTrace();// ?
		}
	}

	// Darwin Core Fields that have Date values
	static final String[] DATE_FIELD_NAMES = { "EarliestDateCollected", "LatestDateCollected",
			"DateLastModified", "DateIdentified" };
	// Darwin Core Fields that have Double values
	static final String[] DOUBLE_FIELD_NAMES = { "MinimumElevationInMeters",
			"MaximumElevationInMeters", "MinimumDepthInMeters", "DecimalLongitude",
			"CatalogNumberNumeric", "MaximumDepthInMeters", "DecimalLatitude" };

	static final Calendar TODAY = Calendar.getInstance();
	static final DateFormat FORMATTER = DateFormat.getDateInstance();

	static boolean isSpecialField(String[] fieldNames, String fieldName) {
		if (fieldName == null || fieldName.length() == 0) {
			return false;
		}
		for (int i = 0; i < fieldNames.length; i++)
			if (fieldNames[i].equals(fieldName)) {
				return true;
			}
		return false;
	}

	// public XmlView createXmlView(View view) {
	// XmlView xmlView = new XmlView();
	// return xmlView;
	// }

	public Credentials getSubmitter() {
		return submitter;
	}

	public void setSubmitter(Credentials submitter) {
		this.submitter = submitter;
	}

	public void setSubmitter(BaseObject obj) {
		if (obj != null) {
			submitter = new Credentials();
			submitter.setUserId(obj.getUser().getId());
			submitter.setGroupId(obj.getGroup().getGroupId());
			response.getRequestSummary().setSubmitter(submitter);
		}
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public static ObjectFactory getObjectFactory() {
		return objectFactory;
	}

	public MapObject getMapper(String className) {
		return mappers.get(className);
	}

	public MapObject getMapper(IdObject idObject) {
		return mappers.get(idObject.getClass().getSimpleName());
	}

	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		String objectType = idObject.getClass().getSimpleName();
		MapObject mapper = mappers.get(objectType);
		if (mapper != null) {
			return mapper.setXmlFields(xmlObject, idObject);
		}
		return false;
	}
}
