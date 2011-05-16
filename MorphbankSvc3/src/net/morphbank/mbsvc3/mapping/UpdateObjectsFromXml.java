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

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.Credentials;
import net.morphbank.mbsvc3.xml.ObjectList;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.IdObject;

public class UpdateObjectsFromXml extends MapXmlToObject {
	// TODO figure out how to find an object by an id for resolving references
	// after inserts are complete
	// 1. if morphbank id, get from database
	// 2. if external id, get from database
	// 3. if local id, get from objectlist of insert

	public UpdateObjectsFromXml(Credentials requestCredentials, ObjectList objectlist,
			String remoteServer) {
		super(requestCredentials, objectlist, remoteServer + MorphbankConfig.MB_DETAILS_REQUEST);
	}

	public Response processObjects() throws Exception {
		return processObjects(objectList.getXmlObjectList());
	}

	/**
	 * Creation of objects in 3 passes through the Insert object list First pass
	 * updates all objects Second pass links image objects through
	 * image.specimen Third pass links specimen objects through standard image
	 * and imagesCount of specimen
	 * 
	 * @return A Response object that includes a report on the insertions
	 * 
	 * @throws Exception
	 */
	public Response processObjects(List objList) throws Exception {
		// update specimen objects
		boolean localTransaction = false;
		XmlBaseObject xmlObject = null;
		responseMapper.createResponse(submitter, "update", "update from request");

		// 1st pass: update all objects
		Iterator objects = null;
		numObjects = 0;
		successObjects = 0;
		failedObjects = 0;
		failure = false;
		EntityTransaction tx = null;
		EntityManager em = MorphbankConfig.getEntityManager();

		BaseObject object = null;
		objects = objList.iterator();
		while (objects.hasNext()) {// update taxa
			// First loop assigns local index to each object
			numObjects++;
			xmlObject = ((XmlBaseObject) objects.next());
			String objectType = xmlObject.getObjectTypeId();
			xmlObject.setLocalId(Integer.toString(numObjects));
			// get existing MB object
			IdObject existingObj = null;
			Integer mbId = xmlObject.getSourceId().getMorphbank();
			if (mbId != null && mbId != 0) {
				existingObj = BaseObject.getEJB3Object(mbId);
			} else {
				List<String> objectIdentifiers = xmlObject.getSourceId().getExternal();
				existingObj = BaseObject.getObjectByExternalId(objectIdentifiers);
			}

			if (existingObj == null) {
				// TODO insert object, may need to add insert if not found
				// option
				xmlObject.addStatus("No matching object create new object");
				// objectUpdateSuccess = reportSuccess(xmlObject, null);
			} else {
				xmlObject.addStatus("updating object");
			}
			try { // create or update within new transaction
				tx = em.getTransaction();
				if (!tx.isActive()) {
					localTransaction = true;
					tx.begin();
				}
				createOrUpdateObject(existingObj, xmlObject, objectType);
				if (localTransaction) tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
				xmlObject.setStatus("object created");
				if (tx.isActive()) {
					tx.rollback();
				}
			}

		}
		// Second pass: link objects
		objects = objList.iterator();
		while (objects.hasNext()) {
			xmlObject = ((XmlBaseObject) objects.next());
			object = (BaseObject) getRelatedMorphbankObject(xmlObject);
			// look for object in database
			if (object == null) {
				XmlId sourceId = xmlObject.getSourceId();
				object = XmlServices.getObject(sourceId);
			}
			if (object == null) continue;
			tx = em.getTransaction();
			if (!tx.isActive()) {
				tx.begin();
				localTransaction = true;
			}
			MapObject mapper = getMapper(object.getObjectTypeIdStr());
			mapper.linkObject(xmlObject, object, getResponseObject(xmlObject));
			if (localTransaction) tx.commit();
		}
		return responseMapper.getResponse();
	}

	public MapObject getMapper(String className) {
		return mappers.get(className);
	}

	public MapObject getMapper(IdObject idObject) {
		return mappers.get(idObject.getClass().getSimpleName());
	}

}
