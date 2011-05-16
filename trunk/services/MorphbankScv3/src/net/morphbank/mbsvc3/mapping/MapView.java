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

import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.MissingLink;
import net.morphbank.object.Taxon;
import net.morphbank.object.View;

public class MapView extends MapObjectBase {

	public MapView(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapView(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapView(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// Methods to map XmlBaseObject to View
	@Override
	public IdObject createObject(XmlBaseObject xmlObject, XmlId xmlId) {
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
		View view = new View(id, name, ownerCred.getUser(), submitterCred.getUser(), ownerCred
				.getGroup());
		view.persist();
		boolean success = updateObject(view, xmlObject);
		if (success) return view;
		return null;
	}

	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		// TODO do not set fields unless source field is not null
		if (idObject == null) return true;
		if (!(idObject instanceof View)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		View view = (View) idObject;
		// TODO add rest of fields
		// TODO add rest of view fields
		if (XmlUtils.notEmptyString(xmlObject.getViewAngle())) {
			view.setViewAngle(xmlObject.getViewAngle());
		}
		if (XmlUtils.notEmptyString(xmlObject.getImagingTechnique())) {
			view.setImagingTechnique(xmlObject.getImagingTechnique());
		}
		if (XmlUtils.notEmptyString(xmlObject.getImagingPreparationTechnique())) {
			view.setImagingPreparationTechnique(xmlObject.getImagingPreparationTechnique());
		}
		if (XmlUtils.notEmptyString(xmlObject.getSpecimenPart())) {
			view.setSpecimenPart(xmlObject.getSpecimenPart());
		}
		// TODO use scientificName to find tsn

		if (xmlObject.getViewTSN() != null) {
			view.setTaxon(Taxon.getTaxon(xmlObject.getViewTSN()));
		} else {
			view.setTaxon(MapTaxon.getTaxon(xmlObject.getViewRestrictedTo()));
		}
		if (XmlUtils.notEmptyString(xmlObject.getDevelopmentalStage())) {
			view.setDevelopmentalStage(xmlObject.getDevelopmentalStage());
		}
		if (XmlUtils.notEmptyString(xmlObject.getSex())) {
			view.setSex(xmlObject.getSex());
		}
		if (XmlUtils.notEmptyString(xmlObject.getForm())) {
			view.setForm(xmlObject.getForm());
		}
		view.setViewName();

		return true;
	}

	// @Override
	public IdObject createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		View view = null;
		if (idObject == null) {
			view = (View) createObject(xmlObject);
			return view;
		}
		if (idObject instanceof View) {
			view = (View) idObject;
			if (updateObject(view, xmlObject)) return view;
		}
		return null;
	}

	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof View)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// View view = (View) idObject;
		return true;
	}

	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof View)) return false;
		View view = (View) idObject;
		// View fields
		xmlObject.setDevelopmentalStage(view.getDevelopmentalStage());
		xmlObject.setForm(view.getForm());
		xmlObject.setImagesCount(view.getImagesCount());
		xmlObject.setImagingPreparationTechnique(view.getImagingPreparationTechnique());
		xmlObject.setImagingTechnique(view.getImagingTechnique());
		xmlObject.setSex(view.getSex());
		xmlObject.setSpecimenPart(view.getSpecimenPart());
		if (view.getStandardImage() != null) {
			xmlObject.setStandardImage(objMapper.createXmlId(view.getStandardImage(), null));
		}
		xmlObject.setViewAngle(view.getViewAngle());
		if (view.getTaxon() != null) {
			//xmlObject.setViewTSN(view.getTaxon().getTsn());
			xmlObject.setViewTSN(view.getTaxon().getTsn());
		}
		return true;
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object) {
		if (object == null) return true;
		if (!(object instanceof View)) return false;
		View view = (View) object;
		// TODO standard image
		XmlId standardImageId = xmlObject.getStandardImage();
		Image standardImage = null;

		BaseObject related = xmlMapper.getBaseObject(standardImageId);
		if (related == null) {
			// no specimen in database
			xmlObject
			.setStatus("view id not found in database, info stored in MissingLink table ");
			xmlMapper.recordMissingLink(view, standardImageId, MissingLink.VIEW);
		}
		else {
			if (!(related instanceof Image)) {

				xmlObject.setStatus("view id refers to an object of type "
						+ related.getObjectTypeIdStr());
				return false;
			}
			if (standardImageId != null) {
				// set the standard image after the image is created!
				standardImage = (Image) related;
			}
			
		}
		if (view != null && view.getImages() != null) {
			Iterator<Image> images = view.getImages().iterator();
			if (images.hasNext()) {
				standardImage = images.next();
			}
		}
		if (standardImage != null) {
			view.setStandardImage(standardImage);
		} else {
			// missing
			xmlMapper.recordMissingLink(view, standardImageId,
					MissingLink.VIEW_STANDARD_IMAGE);
		}
		// TODO determination
		return true;
	}

	public void linkView(XmlBaseObject xmlView) {
		View view = (View) xmlMapper.getRelatedMorphbankObject(xmlView);
		linkView(view, xmlView);
	}

	public void linkView(View view, XmlBaseObject xmlView) {
		xmlMapper.linkBaseObject(view, xmlView);
	}

}
