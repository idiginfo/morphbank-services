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
import net.morphbank.object.IdObject;
import net.morphbank.object.Publication;

public class MapPublication extends MapObjectBase {

	public MapPublication(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapPublication(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapPublication(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	@Override
	public Publication createObject(XmlBaseObject xmlObject, XmlId xmlId) {
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
		Publication publication = new Publication(id, name, ownerCred.getUser(), submitterCred
				.getUser(), ownerCred.getGroup());
		publication.persist();
		boolean success = updateObject(publication, xmlObject);
		if (success) return publication;
		return null;
	}

	@Override
	public Publication createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Publication publication;
		if (idObject == null) {
			publication = (Publication) createObject(xmlObject);
			return publication;
		} else if (idObject instanceof Publication) {
			publication = (Publication) idObject;
			boolean updateSuccess = updateObject(publication, xmlObject);
			if (updateSuccess) return publication;
		}
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Publication)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Publication publication = (Publication) idObject;
		publication.setPublicationType(xmlObject.getPublicationType());
		publication.setAddress(xmlObject.getAddress());
		publication.setAnnote(xmlObject.getAnnote());
		publication.setAuthor(xmlObject.getAuthor());
		publication.setPublicationTitle(xmlObject.getPublicationTitle());
		publication.setChapter(xmlObject.getChapter());
		publication.setEdition(xmlObject.getEdition());
		publication.setEditor(xmlObject.getEditor());
		publication.setHowPublished(xmlObject.getHowPublished());
		publication.setInstitution(xmlObject.getInstitution());
		publication.setKey(xmlObject.getKey());
		//publication.setMonth(xmlObject.getMonth());
		publication.setDay(xmlObject.getDay());
		publication.setNote(xmlObject.getNote());
		publication.setNumber(xmlObject.getNumber());
		publication.setOrganization(xmlObject.getOrganization());
		publication.setPages(xmlObject.getPages());
		publication.setPublisher(xmlObject.getPublisher());
		publication.setSchool(xmlObject.getSchool());
		publication.setSeries(xmlObject.getSeries());
		publication.setTitle(xmlObject.getTitle());
		publication.setVolume(xmlObject.getVolume());
		publication.setYear(xmlObject.getYear());
		publication.setIsbn(xmlObject.getIsbn());
		publication.setIssn(xmlObject.getIssn());
		return true;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Publication)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// Publication publication = (Publication) idObject;
		// if (!objMapper.setXmlFields(xmlObject, publication.getSpecimen()))
		// return false;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Publication)) return false;
		Publication publication = (Publication) idObject;
		publication.setPublicationType(xmlObject.getPublicationType());
		xmlObject.setAddress(publication.getAddress());
		xmlObject.setAnnote(publication.getAnnote());
		xmlObject.setAuthor(publication.getAuthor());
		xmlObject.setPublicationTitle(publication.getPublicationTitle());
		xmlObject.setChapter(publication.getChapter());
		xmlObject.setEdition(publication.getEdition());
		xmlObject.setEditor(publication.getEditor());
		xmlObject.setHowPublished(publication.getHowPublished());
		xmlObject.setInstitution(publication.getInstitution());
		xmlObject.setKey(publication.getKey());
		//xmlObject.setMonth(publication.getMonth());
		xmlObject.setDay(publication.getDay());
		xmlObject.setNote(publication.getNote());
		xmlObject.setNumber(publication.getNumber());
		xmlObject.setOrganization(publication.getOrganization());
		xmlObject.setPages(publication.getPages());
		xmlObject.setPublisher(publication.getPublisher());
		xmlObject.setSchool(publication.getSchool());
		xmlObject.setSeries(publication.getSeries());
		xmlObject.setTitle(publication.getTitle());
		xmlObject.setVolume(publication.getVolume());
		xmlObject.setYear(publication.getYear());
		xmlObject.setIsbn(publication.getIsbn());
		xmlObject.setIssn(publication.getIssn());
		return true;
	}

}
