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

import javax.persistence.EntityManager;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.ObjectFactory;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.Annotation;
import net.morphbank.object.BaseObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.MissingLink;
import net.morphbank.object.Publication;

public abstract class MapObjectBase implements MapObject {

	protected MapObjectToResponse objMapper;
	protected MapXmlToObject xmlMapper;
	protected ObjectFactory objectFactory = MapObjectToResponse.getObjectFactory();

	protected MapObjectBase(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		this.xmlMapper = xmlMapper;
		this.objMapper = objMapper;
	}

	@Override
	public final IdObject createObject(XmlBaseObject xmlObject) {
		return createObject(xmlObject, null);
	}

	@Override
	public XmlBaseObject createXmlObject(IdObject idObject) {
		if (idObject == null) return null;
		XmlBaseObject xmlObject = new XmlBaseObject();
		xmlObject.setObjectType(idObject.getClass().getSimpleName());
		return xmlObject;
	}

	@Override
	public XmlBaseObject createXmlObject(IdObject idObject, String localId) {
		// BaseObject fields
		XmlBaseObject xmlObject = createXmlObject(idObject);
		objMapper.setId(xmlObject, idObject, localId);
		return xmlObject;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		xmlMapper.setRelatedObject(xmlObject, idObject);
		return xmlMapper.mapToBaseObject(idObject, xmlObject);
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject, XmlId xmlId) {
		xmlMapper.setRelatedObject(xmlObject, idObject);
		return xmlMapper.mapToBaseObject(idObject, xmlObject, xmlId);
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		return setXmlFields(xmlObject, idObject);
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject baseObject) {
		if (baseObject == null) {// insert must not have worked
			MorphbankConfig.SYSTEM_LOGGER.info("Insert failed for " + xmlObject);
			xmlObject.addStatus("Insert failed");
			return false;
		}
		return xmlMapper.linkBaseObject(baseObject, xmlObject);
	}
	
	public static MissingLink createMissingLink(int sourceId, int targetId, String linkType) {
		return createMissingLink(sourceId, targetId, linkType, null, null, null);
	}

	public static MissingLink createMissingLink(int sourceId, Integer targetId, String linkType,
			Integer objectOrder, String objectRole, String objectTitle) {
		MissingLink missingLink = new MissingLink(sourceId, targetId, linkType);
		if (objectOrder != null) missingLink.setObjectOrder(objectOrder);
		missingLink.setObjectRole(objectRole);
		missingLink.setObjectTitle(objectTitle);
		EntityManager em = MorphbankConfig.getEntityManager();
		em.persist(missingLink);
		return missingLink;
	}
	
	public boolean processMissingLink(BaseObject object){
		if (object==null) return false;
		int sourceId = object.getId();

		return true;
	}

}
