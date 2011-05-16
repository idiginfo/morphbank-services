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
import net.morphbank.object.IdObject;
import net.morphbank.object.Taxon;
import net.morphbank.object.User;

public class MapUser extends MapObjectBase {

	public MapUser(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapUser(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapUser(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// Methods to map XmlBaseObject to User

	@Override
	public User createObject(XmlBaseObject xmlObject, XmlId xmlId) {
		int id = xmlObject.getMorphbankId();
		String name = xmlObject.getName();
		if (name != null && name.length() == 0) name = null;
		MBCredentials ownerCred = xmlMapper.getOwner(xmlObject);
		MBCredentials submitterCred = xmlMapper.getSubmitter(xmlObject);
		String uin = xmlObject.getUin();
		String pin = xmlObject.getPin();
		String userName = xmlObject.getUserName();
		User user = new User(id, name, ownerCred.getUser(), submitterCred.getUser(), ownerCred
				.getGroup(), userName, uin, pin);
		user.persist();
		boolean success = updateObject(user, xmlObject);
		if (success) return user;
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		// public void mapToUser(User user, XmlBaseObject xmlUser, XmlId userId)
		// throws Exception {
		// TODO do not set fields unless source field is not null
		if (idObject == null) return true;
		if (!(idObject instanceof User)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		User user = (User) idObject;
		// TODO add rest of fields
		String value = null;
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getUin())) != null) {
			user.setUin(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getPin())) != null) {
			user.setPin(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getUserName())) != null) {
			user.setUserName(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getEmail())) != null) {
			user.setEmail(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getAffiliation())) != null) {
			user.setAffiliation(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getAddress())) != null) {
			user.setAddress(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getLastName())) != null) {
			user.setLastName(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getFirstName())) != null) {
			user.setFirstName(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getSuffix())) != null) {
			user.setSuffix(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getMiddleInit())) != null) {
			user.setMiddleInit(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getStreet1())) != null) {
			user.setStreet1(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getStreet2())) != null) {
			user.setStreet2(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getCity())) != null) {
			user.setCity(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getCountry())) != null) {
			user.setCountry(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getState())) != null) {
			user.setState(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getZipcode())) != null) {
			user.setZipcode(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getUserStatus())) != null) {
			user.setStatus(value);
		}
		Integer privilegeTSN = xmlObject.getPrivilegeTSN();
		if (privilegeTSN != null) {
			user.setPrivilegeTaxon(Taxon.getTaxon(privilegeTSN));
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getPreferredServer())) != null) {
			user.setPreferredServer(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getPreferredGroup())) != null) {
			user.setPreferredGroup(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getUserLogo())) != null) {
			user.setUserLogo(value);
		}
		if ((value = XmlUtils.getNonEmptyString(xmlObject.getLogoUrl())) != null) {
			user.setLogoURL(value);
		}

		// TODO add rest of user fields
		if (XmlUtils.notEmptyString(xmlObject.getUin())) {
			user.setUin(xmlObject.getUin());
		}
		return true;
	}

	@Override
	public User createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		User user;
		if (idObject == null) {
			user = (User) createObject(xmlObject);
			return (user);
		} else if (idObject instanceof User) {
			user = (User) idObject;
			boolean updateSuccess = updateObject(user, xmlObject);
			if (updateSuccess) return user;
		}
		return null;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof User)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// User user = (User) idObject;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof User)) return false;
		User user = (User) idObject;
		xmlObject.setUin(user.getUin());
		xmlObject.setPin(user.getPin());
		xmlObject.setUserName(user.getUserName());
		xmlObject.setEmail(user.getEmail());
		xmlObject.setAffiliation(user.getAffiliation());
		xmlObject.setAddress(user.getAddress());
		xmlObject.setLastName(user.getLastName());
		xmlObject.setFirstName(user.getFirstName());
		xmlObject.setSuffix(user.getSuffix());
		xmlObject.setMiddleInit(user.getMiddleInit());
		xmlObject.setStreet1(user.getStreet1());
		xmlObject.setStreet2(user.getStreet2());
		xmlObject.setState(user.getState());
		xmlObject.setZipcode(user.getZipcode());
		xmlObject.setUserStatus(user.getStatus());
		Taxon privilegeTaxon = user.getPrivilegeTaxon();
		if (privilegeTaxon!=null) xmlObject.setPrivilegeTSN(privilegeTaxon.getTsn());
		xmlObject.setPreferredServer(user.getPreferredServer());
		xmlObject.setPreferredGroup(user.getPreferredGroup());
		xmlObject.setUserLogo(user.getUserLogo());
		xmlObject.setLogoUrl(user.getLogoURL());
		return true;
	}

	public void linkUser(XmlBaseObject xmlUser) {
		User user = (User) xmlMapper.getRelatedMorphbankObject(xmlUser);
		linkUser(user, xmlUser);
	}

	public void linkUser(User user, XmlBaseObject xmlUser) {
		xmlMapper.linkBaseObject(user, xmlUser);
	}

}
