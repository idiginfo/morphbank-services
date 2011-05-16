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
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Group;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.MissingLink;
import net.morphbank.object.Specimen;
import net.morphbank.object.User;

public class MapGroup extends MapObjectBase {

	public MapGroup(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapGroup(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapGroup(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// Methods to map XmlBaseObject to Group
	@Override
	public Group createObject(XmlBaseObject xmlObject, XmlId groupId) {
		int id = xmlObject.getMorphbankId();
		String name = xmlObject.getGroupName();
		// TODO check for existing group before making new group
		if (name == null || name.length() == 0) name = "Group from XML upload";
		MBCredentials ownerCred = xmlMapper.getOwner(xmlObject);
		MBCredentials submitterCred = xmlMapper.getSubmitter(xmlObject);
		Group group = new Group(id, name, ownerCred.getUser(), submitterCred.getUser(), ownerCred
				.getGroup());
		group.setGroupName(name);
		group.persist();
		boolean success = updateObject(group, xmlObject);
		if (success) return group;
		return null;
	}

	@Override
	public Group createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Group group;
		if (idObject == null) {
			group = createObject(xmlObject, null);
			return group;
		} else if (idObject instanceof Group) {
			group = (Group) idObject;
			boolean updateSuccess = updateObject(group, xmlObject);
			if (updateSuccess) return group;
		}
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		// TODO do not set fields unless source field is not null
		if (idObject == null) return true;
		if (!(idObject instanceof Group)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Group group = (Group) idObject;
		if (XmlUtils.notEmptyString(xmlObject.getGroupName())) {
			group.setGroupName(xmlObject.getGroupName());
		}
		return true;
	}

	// public boolean updateObject(IdObject obj, XmlBaseObject xmlObject, XmlId
	// groupId) {
	// // TODO check this to assure no recursion
	// if (obj == null) obj = createObject(xmlObject, groupId);
	// if (!(obj instanceof Group)) return false;
	// return updateObject(obj, xmlObject, groupId);
	// }

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Group)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// Group image = (Group) idObject;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Group)) return false;
		Group group = (Group) idObject;
		xmlObject.setGroupName(group.getGroupName());
		xmlObject.setGroupManager(objMapper.createXmlId(group.getGroupManager(),null));
		return true;
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object) {
		if (object == null) return true;
		if (!(object instanceof Group)) return false;
		if (!super.linkObject(xmlObject, object)) return false;
		Group group = (Group) object;
		XmlId groupManagerId = xmlObject.getGroupManager();
		BaseObject groupManager = XmlServices.getObject(groupManagerId);
		if (groupManager == null) {
			xmlObject
					.setStatus("group manager not found in database, info stored in MissingLink table ");
			xmlMapper.recordMissingLink(group, groupManagerId, MissingLink.GROUP_MANAGER);
		} else {
			if (!(groupManager instanceof User)) {
				// TODO error! reference to non-specimen
				xmlObject.setStatus("group manager id refers to an object of type "
						+ groupManager.getObjectTypeIdStr());
			}
			User user = (User) groupManager;
			group.setGroupManager(user);
		}
		return true;
	}

}
