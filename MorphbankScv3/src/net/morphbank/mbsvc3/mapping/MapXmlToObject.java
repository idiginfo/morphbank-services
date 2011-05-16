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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.Credentials;
import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.ObjectList;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.Update;
import net.morphbank.mbsvc3.xml.Userprop;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.object.BaseObject;
import net.morphbank.object.ExternalLinkObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Locality;
import net.morphbank.object.MissingLink;
import net.morphbank.object.UserProperty;

public class MapXmlToObject {
	// TODO figure out how to find an object by an id for resolving references
	// after inserts are complete
	// 1. if morphbank id, get from database
	// 2. if external id, get from database
	// 3. if local id, get from objectlist of insert

	protected ObjectList objectList = null;

	Dictionary<String, MapObject> mappers = new Hashtable<String, MapObject>();
	protected List xmlObjectList = null;
	protected MBCredentials submitter;
	protected MBCredentials owner; // also called contributor
	protected String remoteDetailsBaseUrl = null;
	protected MapObjectToResponse responseMapper = new MapObjectToResponse("insert",
			"trying to insert");

	protected int numObjects = 0;
	protected int successObjects = 0;
	protected int failedObjects = 0;
	protected boolean failure = false;

	public MapXmlToObject(String remoteDetailsBaseUrl) {
		mappers.put("Annotation", new MapAnnotation(this));
		mappers.put("DeterminationAnnotation", getMapper("Annotation"));
		mappers.put("Collection", new MapCollection(this));
		mappers.put("Group", new MapGroup(this));
		mappers.put("Groups", getMapper("Group"));
		mappers.put("Image", new MapImage(this));
		mappers.put("Locality", new MapLocality(this));
		// mappers.put("Otu",new MapOtu(this));
		mappers.put("Publication", new MapPublication(this));
		mappers.put("Specimen", new MapSpecimen(this));
		mappers.put("Taxon", new MapTaxon(this));
		mappers.put("TaxonConcept", new MapTaxonConcept(this));
		mappers.put("View", new MapView(this));
		mappers.put("User", new MapUser(this));
		this.remoteDetailsBaseUrl = remoteDetailsBaseUrl;
	}

	public MapXmlToObject(Credentials requestCredentials, ObjectList objectList, String remoteDetailsBaseUrl) {
		this(remoteDetailsBaseUrl);
		if (objectList == null) return;
		this.objectList = objectList;
		this.xmlObjectList = objectList.getXmlObjectList();
		Credentials insertCredentials = objectList.getSubmitter();
		submitter = new MBCredentials(requestCredentials);
		if (insertCredentials != null) {
			owner = new MBCredentials(insertCredentials);
		} else {
			owner = submitter;
		}

	}

	protected Dictionary<XmlBaseObject, IdObject> relatedMorphbankObjects = new Hashtable<XmlBaseObject, IdObject>();

	public BaseObject createObject(XmlBaseObject xmlObject) throws Exception {
		return createObject(xmlObject, xmlObject.getObjectTypeId(), xmlObject.getSourceId());
	}

	public BaseObject createObject(XmlBaseObject xmlObject, String objectType, XmlId xmlId)
			throws Exception {
		BaseObject object = null;
		if (objectType == null) return null;
		MapObject mapper = getMapper(objectType);

		IdObject idObject = mapper.createObject(xmlObject, xmlId);
		if (!(idObject instanceof BaseObject)) {
			// handle Taxon
			return null;
		}
		object = (BaseObject) idObject;
		// TODO add other object types, including Collection, Annotation
		if (object != null) {
			if (!flushObject(xmlObject, object)) {
				object = null; // object did not save properly
			}
			setRelatedObject(xmlObject, object);
			reportSuccess(xmlObject, object);
			responseMapper.createResponseObject(xmlObject, object);
			xmlObject.setMorphbankId(object.getId());
		}
		MorphbankConfig.SYSTEM_LOGGER.info(objectType + " Object created: " + xmlObject
				+ " morphbank id: " + xmlObject.getMorphbankId() + " external id: "
				+ xmlObject.getSourceId().getFirstExternal());

		return object;
	}

	public BaseObject createOrUpdateObject(IdObject existingObject, XmlBaseObject xmlObject,
			String objectType) throws Exception {
		XmlBaseObject responseObject = null;
		if (objectType == null) return null;
		MapObject mapper = getMapper(objectType);
		String operation = "updated";
		if (existingObject == null) operation = "created";
		IdObject idObject = mapper.createOrUpdateObject(existingObject, xmlObject);
		//TODO if idObject is null, update is blocked? add info to result
		if (idObject == null){
			responseObject = responseMapper.createResponseObject(xmlObject, idObject);
			responseObject.setId(xmlObject.getId());
			responseObject.setStatus("not updated");
		}
		if (!(idObject instanceof BaseObject)) {
			// handle Taxon
			return null;
		}
		BaseObject object = (BaseObject) idObject;
		// TODO add other object types, including Collection, Annotation
		if (object != null) {
			if (!flushObject(xmlObject, object)) {
				object = null; // object did not save properly
			}
			setRelatedObject(xmlObject, object);
			reportSuccess(xmlObject, object);
			responseMapper.createResponseObject(xmlObject, object);
			xmlObject.setMorphbankId(object.getId());
		}
		MorphbankConfig.SYSTEM_LOGGER.info(objectType + " Object "+operation+": " + xmlObject
				+ " morphbank id: " + xmlObject.getMorphbankId() + " external id: "
				+ xmlObject.getSourceId().getFirstExternal());

		return object;
	}

	public IdObject getRelatedMorphbankObject(XmlBaseObject xmlObj) {
		return relatedMorphbankObjects.get(xmlObj);
	}

	public void setRelatedObject(XmlBaseObject xmlObj, IdObject obj) {
		if (obj == null) return;
		relatedMorphbankObjects.put(xmlObj, obj);
	}

	public MapXmlToObject(List xmlObjectList) {
		this.xmlObjectList = xmlObjectList;
	}

	public MapXmlToObject(Update update) {
		this.xmlObjectList = update.getXmlObjectList();
	}

	public MBCredentials getSubmitter(XmlBaseObject xmlObject) {
		if (xmlObject == null) return getSubmitter();
		// TODO check for submitter info from the object
		MBCredentials submittedBy = null;
		if (xmlObject.getSubmittedBy() != null) {
			submittedBy = new MBCredentials(xmlObject.getSubmittedBy());
		} else {
			submittedBy = getSubmitter();
		}
		return submittedBy;
	}

	/**
	 * Get the owner of the object If the object has no owner attributes, use
	 * the credentials of the mapper
	 * 
	 * @param xmlObject
	 * @return owner for the object
	 */
	public MBCredentials getOwner(XmlBaseObject xmlObject) {
		if (xmlObject == null) {
			return getOwner();
		}
		Credentials objCredentials = xmlObject.getOwner();
		if (objCredentials != null) {
			return new MBCredentials(objCredentials);
		} else {
			return getOwner();
		}
	}

	public boolean reportSuccess(XmlBaseObject xmlObject, IdObject object) {
		if (object == null || object.getId() == 0) {
			// TODO report failure to response
			failedObjects++;
			failure = true;
			if (xmlObject != null) {
				xmlObject.setStatus("Errors found " + xmlObject.getStatus());
			}
			return false;
		} else {
			successObjects++;
			xmlObject.addStatus("No errors");
			return true;
		}
	}

	public Response processObjects() throws Exception {
		return null;
	}

	public Response processObjects(List<Object> objList, boolean createObjects,
			boolean updateObjects) throws Exception {
		return null;
	}

	public Response processObjects(List<Object> objList) throws Exception {
		return null;
	}

	public boolean flushObject(XmlBaseObject xmlObject, BaseObject object) {
		String flushResult = MorphbankConfig.flush();
		if (flushResult == null) {
			return true;
		} else {
			xmlObject.addStatus("Problem saving object: " + flushResult);
			return false;
		}
	}

	// Map fields of XML object to fields of MB object

	public void mapToBaseObject(BaseObject base, XmlBaseObject xmlObj) {
		mapToBaseObject(base, xmlObj, xmlObj.getSourceId());
	}

	public boolean mapToBaseObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (xmlObject == null) return false;
		XmlId xmlId = xmlObject.getSourceId();
		return mapToBaseObject(idObject, xmlObject, xmlId);
	}

	/**
	 * set base object fields from the insert include user and group Prepare
	 * object for insertion into database no processing of fields thumbURL,
	 * submitted by, etc.
	 * 
	 * Method used for create and update
	 * 
	 * @param base
	 * @param objectList
	 */
	public boolean mapToBaseObject(IdObject idObject, XmlBaseObject xmlObj, XmlId xmlId) {
		if (!(idObject instanceof BaseObject)) {
			// TODO handle Taxon
		}
		BaseObject base = (BaseObject) idObject;
		// TODO make sure update works properly
		Date objDLM = base.getDateLastModified();
		Date xmlObjDLM = xmlObj.getDateLastModified();
		if (xmlObjDLM != null && objDLM != null && !xmlObjDLM.after(objDLM)) {
			// attempt to update object that with older object
			MorphbankConfig.SYSTEM_LOGGER.info("Update blocked for old "
					+ base.getObjectTypeIdStr() + ": " + idObject.getId() + " date " + objDLM
					+ " update date " + xmlObjDLM);
			return false;
		}
		base.updateDateLastModified(xmlObj.getDateLastModified());

		if (xmlObj.getDateCreated() != null) {
			base.setDateCreated(xmlObj.getDateCreated());
		}
		if (xmlObj.getDateToPublish() != null) {
			base.setDateToPublish(xmlObj.getDateToPublish());
		}
		// Special case of Locality created from specimen info, do not add user
		// props and exts
		if (base instanceof Locality && xmlObj.getObjectTypeId().equals("Specimen")) return true;

		// map other base object fields
		if (xmlObj.getDescription() != null) base.setDescription(xmlObj.getDescription());
		// user and group mapped by constructor
		// TODO store URIs of sourceId
		if (xmlObj.getHostServer()!=null) base.setHostServer(xmlObj.getHostServer());

		// create an external link object for each external id in the sourceId
		Iterator<String> externalIds = xmlId.getExternal().iterator();
		while (externalIds.hasNext()) {
			String externalId = externalIds.next();
			ExternalLinkObject linkObj = ExternalLinkObject.getExternalLinkObject(externalId);
			if (linkObj != null) {
				if (linkObj.getObject() == null) {
					// some weird problem!
					String message = "External link with no related object: " + externalId
							+ " linkObj " + linkObj.getLinkId() + " object: " + base.getId();
					MorphbankConfig.SYSTEM_LOGGER.info(message);
					xmlObj.addStatus(message);

				} else if (linkObj.getObject().getId() != base.getId()) {
					// externalId refers to a different object
					// fail on lack of uniqueness of external id
					MorphbankConfig.SYSTEM_LOGGER.info("External link not unique: " + externalId
							+ " object: " + base.getId());
					xmlObj.addStatus("External link not unique: " + externalId + " object: "
							+ base.getId());
				} else {
					// external link is already present
				}
			} else {
				ExternalLinkObject link = new ExternalLinkObject(base, externalId,
						"External Unique Reference");
				link.persist();
			}
		}
		// create UserProperty objects
		Iterator<Userprop> userProps = xmlObj.getUserProperty().iterator();
		while (userProps.hasNext()) {
			Userprop userprop = userProps.next();
			UserProperty userProperty = base.addUserProperty(userprop.getProperty(), userprop
					.getValue(), userprop.getNamespaceURI());
			userProperty.persist();
		}
		// create ExternalLinkObject objects
		Iterator<Extref> externalLinks = xmlObj.getExternalRef().iterator();
		while (externalLinks.hasNext()) {
			Extref extref = externalLinks.next();
			// ExternalLinkObject linkObj =
			// ExternalLinkObject.getExternalLinkObject(extref.getExternalId());
			ExternalLinkObject externalLink = base.addExternalLink(extref.getType(), extref
					.getExternalId(), extref.getDescription(), extref.getLabel(), extref
					.getUrlData());
			if (externalLink.validate()) {// check for presence of some info
				// in link
				externalLink.persist();
			}
		}
		return true;
	}

	public static XmlBaseObject getXmlObject(ObjectList req, XmlId refId) {
		XmlBaseObject ref = req.getObject(refId);
		return ref;
	}

	/**
	 * Create all the necessary links to related object for the BaseObject
	 * relationships.
	 * 
	 * @param xmlObj
	 *            The mapped object to be linked
	 * @return BaseObject of the xmlObj
	 */
	public boolean linkBaseObject(XmlBaseObject xmlObj) {
		// get the BaseObject for xmlObj
		BaseObject base = (BaseObject) getRelatedMorphbankObject(xmlObj);
		return linkBaseObject(base, xmlObj);
	}

	public boolean linkBaseObject(IdObject idObject, XmlBaseObject xmlObj) {
		if (!(idObject instanceof BaseObject)) return true;
		BaseObject base = (BaseObject) idObject;
		BaseObject relatedObject = null;
		// create RelatedObject objects as CollectionObjects of base
		Iterator<XmlId> relatedObjectIds = xmlObj.getRelatedObject().iterator();
		while (relatedObjectIds.hasNext()) {

			XmlId relatedObjId = relatedObjectIds.next();
			relatedObject = getBaseObject(relatedObjId);
			try {
				Integer indexInt = relatedObjId.getIndex();
				int index = -1;
				if (indexInt != null) index = indexInt;
				String role = relatedObjId.getRole();
				String title = relatedObjId.getTitle();
				if (relatedObject == null) {
					// TODO deal with lack of related object!
					recordMissingLink(base, relatedObjId, "specimen", index, role, title);

					MorphbankConfig.SYSTEM_LOGGER.info("No related object for: " + base.getId()
							+ " related id: " + relatedObjId.toString());
				} else {
					// add the relationship between base and relatedObject
					// Store the relationship in the correct direction
					// relationship is created as
					// [source].addObject([target]...)
					if (relatedObjId.getSource() == null || !relatedObjId.getSource()) {
						// base is source, relatedObject is target
						base.addRelatedObject(relatedObject, index, role, title);
					} else {// source is true
						// relatedObject is source, base is target
						relatedObject.addRelatedObject(base, index, role, title);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}

	public boolean recordMissingLink(BaseObject object, XmlId target, String linkType,
			Integer objectOrder, String objectRole, String objectTitle) {
		if (object == null || target == null) return false;
		int sourceId = object.getId();
		int targetId = target.getMorphbank();
		if (targetId < 1) return false; // no morphbank id in the target
		MissingLink missingLink = MissingLink.createMissingLink(sourceId, targetId, linkType,
				objectOrder, objectRole, objectTitle, remoteDetailsBaseUrl+targetId);
		MorphbankConfig.SYSTEM_LOGGER.info("missing link created for "+sourceId+" type "+linkType+ 
				" to "+targetId+" result is id "+missingLink.getId());
		return true;
	}

	public boolean recordMissingLink(BaseObject object, XmlId target, String linkType) {
		return recordMissingLink(object, target, linkType, null, null, null);
	}

	public  void recordMissingLink(int sourceId, int targetId, String linkType) {
		MissingLink.createMissingLink(sourceId, targetId, linkType, remoteDetailsBaseUrl+targetId);
	}

	public BaseObject getBaseObject(XmlId xmlId) {
		BaseObject obj = null;
		// try to use the morphbank id or external id to get object
		obj = XmlServices.getObject(xmlId);
		if (obj == null) {// try to use the local id to get object
			XmlBaseObject relatedXmlObj = getXmlObject(objectList, xmlId);
			if (relatedXmlObj != null) {
				IdObject idObj = getRelatedMorphbankObject(relatedXmlObj);
				if (idObj instanceof BaseObject) {
					obj = (BaseObject) idObj;
				}
			}
		}
		return obj;
	}

	public boolean addUserProperty(BaseObject obj, JAXBElement tag) {
		String value = (String) tag.getValue().toString();
		if (XmlUtils.notEmptyString(value)) {
			QName qName = tag.getName();
			UserProperty userProperty = new UserProperty(obj, qName.getLocalPart(), value, qName
					.getNamespaceURI());
			// TODO check for update of existing property
			obj.getUserProperties().put(userProperty.getName(), userProperty);
			userProperty.persist();
			return true;
		}
		return false;
	}

	public boolean addUserProperty(BaseObject obj, XmlBaseObject object, String tagName) {
		JAXBElement tag = object.getFirstTag(tagName);
		return addUserProperty(obj, tag);
	}

	public JAXBElement<String> getJAXBElement(BaseObject obj, String tagName) {
		UserProperty userProp = obj.getUserProperties().get(tagName);
		if (userProp == null || userProp.getValue() == null || userProp.getNamespaceURI() == null
				|| userProp.getValue().length() == 0) {
			return null;
		}
		QName qname = new QName(userProp.getNamespaceURI(), tagName);
		JAXBElement<String> tag = new JAXBElement<String>(qname, String.class, null, userProp
				.getValue());
		return tag;
	}

	public ObjectList getObjectList() {
		return objectList;
	}

	public void setObjectList(ObjectList objectList) {
		this.objectList = objectList;
	}

	public Dictionary<XmlBaseObject, IdObject> getRelatedObjects() {
		return relatedMorphbankObjects;
	}

	public MapObjectToResponse getResponseMapper() {
		return responseMapper;
	}

	public Response getResponse() {
		return responseMapper.getResponse();
	}

	public MBCredentials getSubmitter() {
		return submitter;
	}

	public MBCredentials getOwner() {
		return owner;
	}

	public MapObject getMapper(String className) {
		return mappers.get(className);
	}

	public MapObject getMapper(IdObject idObject) {
		return mappers.get(idObject.getClass().getSimpleName());
	}

}
