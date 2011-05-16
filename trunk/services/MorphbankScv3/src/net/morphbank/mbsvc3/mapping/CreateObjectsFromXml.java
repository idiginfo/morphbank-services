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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.*;
import net.morphbank.object.BaseObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.Specimen;
import net.morphbank.object.View;

public class CreateObjectsFromXml extends MapXmlToObject {
	// TODO figure out how to find an object by an id for resolving references
	// after inserts are complete
	// 1. if morphbank id, get from database
	// 2. if external id, get from database
	// 3. if local id, get from objectlist of insert

	public CreateObjectsFromXml(Credentials requestCredentials, ObjectList insert,
			String remoteServer) {
		super(requestCredentials, insert, remoteServer + MorphbankConfig.MB_DETAILS_REQUEST);
	}

	public Response processObjects() throws Exception {
		return processObjects(objectList.getXmlObjectList(), true, false);
	}

	/**
	 * Creation of objects in 3 passes through the Insert object list First pass
	 * creates all objects Second pass links image objects through
	 * image.specimen Third pass links specimen objects through standard image
	 * and imagesCount of specimen
	 * 
	 * @param createObjects
	 *            TODO
	 * @param updateObjects
	 *            TODO
	 * 
	 * @return A Response object that includes a report on the insertions
	 * 
	 * @throws Exception
	 */
	public Response processObjects(List<Object> objList, boolean createObjects,
			boolean updateObjects) throws Exception {
		// create specimen objects
		XmlBaseObject xmlObject = null;
		responseMapper.createResponse(getOwner(), "insert", "insert from file");
		// 1st pass: create all objects
		Iterator<Object> objects = null;
		numObjects = 0;
		successObjects = 0;
		failedObjects = 0;
		failure = false;
		boolean localTransaction = false;

		EntityTransaction tx = null;
		EntityManager em = MorphbankConfig.getEntityManager();

		IdObject object = null;
		objects = objList.iterator();
		// TODO put response objects in original order.
		// 
		while (objects.hasNext()) {
			numObjects++;
			xmlObject = ((XmlBaseObject) objects.next());
			String objectType = xmlObject.getObjectTypeId();
			// TODO fetch user and group if necessary

			// look for obect already in list of related objects
			object = getRelatedMorphbankObject(xmlObject);
			// look for object in database
			if (object == null) {
				XmlId sourceId = xmlObject.getSourceId();
				if (xmlObject.getObjectTypeId().equals("View")){
					object = XmlServices.getView(sourceId);
				} else {
					object = XmlServices.getObject(sourceId);
				}
			}

			if (object == null) {
				// create new object in its own transaction
				try {
					tx = em.getTransaction();
					if (!tx.isActive()) {
						tx.begin();
						localTransaction = true;
					}
					object = createObject(xmlObject);
					if (localTransaction) tx.commit();
				} catch (Exception e) {
					e.printStackTrace();
					xmlObject.setStatus("object created");
					if (tx.isActive()) {
						tx.rollback();
					}
				}
			} else { // object with external ref exists!
				// TODO check for consistency
				reportExistingObject(xmlObject, object);
				continue;
			}
		}
		// Second pass: link objects
		objects = objList.iterator();
		try {
			while (objects.hasNext()) {
				xmlObject = ((XmlBaseObject) objects.next());
				object = (BaseObject) getRelatedMorphbankObject(xmlObject);
				// look for object in database
				if (object == null) {
					XmlId sourceId = xmlObject.getSourceId();
					object = XmlServices.getObject(sourceId);
				}
				if (object == null) continue;
				// TODO open transaction
				tx = em.getTransaction();
				if (!tx.isActive()) {
					tx.begin();
					localTransaction = true;
				}
				MapObject mapper = getMapper(object.getObjectTypeIdStr());
				mapper.linkObject(xmlObject, object);
				if (localTransaction) tx.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			xmlObject.setStatus("exception in transaction");
			if (tx.isActive()) tx.rollback();
		}
		return responseMapper.getResponse();
	}

	public void reportExistingObject(XmlBaseObject xmlObject, IdObject existingObj) {
		xmlObject.setMorphbankId(existingObj.getId());
		xmlObject.addStatus("pre-existing object, no new object created");
		setRelatedObject(xmlObject, existingObj);
		responseMapper.createResponseObject(xmlObject, existingObj);
		String message = "Pre-existing " + existingObj.getObjectTypeIdStr();
		if (xmlObject.getSourceId().getExternal().size() > 0) {
			String extId = xmlObject.getSourceId().getExternal().get(0);
			message += " for external Id: " + extId;
		}
		MorphbankConfig.SYSTEM_LOGGER.info(message + " morphbankId: " + existingObj.getId());
	}
}
