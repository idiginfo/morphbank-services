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

import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.object.IdObject;

public class MapCollection extends MapObjectBase {

	public MapCollection(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapCollection(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapCollection(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// Methods to map XmlBaseObject to Collection

	@Override
	public Collection createObject(XmlBaseObject xmlObject, XmlId xmlId) {
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
		Collection collection = new Collection(id, name, ownerCred.getUser(), submitterCred
				.getUser(), ownerCred.getGroup());
		collection.persist();
		boolean success = updateObject(collection, xmlObject);
		if (success) return collection;
		return null;
	}

	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Collection)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		// Collection collection = (Collection) idObject;
		return true;
	}

	@Override
	public Collection createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Collection collection;
		if (idObject == null) {
			collection = (Collection) createObject(xmlObject);
			return (collection);
		} else if (idObject instanceof Collection) {
			collection = (Collection) idObject;
			boolean updateSuccess = updateObject(collection, xmlObject);
			if (updateSuccess) return collection;
		}
		return null;
	}

	// Methods to map Collection to XmlBaseObject

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Collection)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// Collection collection = (Collection) idObject;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Collection)) return false;
		// Collection collection = (Collection) idObject;
		// Collection fields
		// TODO add rest of collection fields (form?)
		return true;
	}

	// linkObject handled by super class
	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object, XmlBaseObject responseObject) {
		return super.linkObject(xmlObject, object, responseObject);
	}
}
