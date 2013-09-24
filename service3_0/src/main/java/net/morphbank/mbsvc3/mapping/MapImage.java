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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.BaseObject;
import net.morphbank.object.CollectionObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.MissingLink;
import net.morphbank.object.Specimen;
import net.morphbank.object.View;

public class MapImage extends MapObjectBase {

	public MapImage(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapImage(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapImage(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// Methods to map XmlBaseObject to Image

	@Override
	public Image createObject(XmlBaseObject xmlObject, XmlId xmlId) {
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
		Image image = new Image(id, name, ownerCred.getUser(), submitterCred.getUser(), ownerCred
				.getGroup());
		image.persist();
		boolean success = updateObject(image, xmlObject);
		if (success) return image;
		return null;
	}

	@Override
	public Image createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Image image;
		if (idObject == null) {
			image = (Image) createObject(xmlObject);
			return image;
		} else if (idObject instanceof Image) {
			image = (Image) idObject;
			boolean updateSuccess = updateObject(image, xmlObject);
			if (updateSuccess) return image;
		}
		return null;
	}

	/**
	 * Update an image object from an XmlBaseObject object
	 * 
	 * @param xmlImage
	 * @return
	 */
	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Image)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Image image = (Image) idObject;
		if (xmlObject.getDateToPublish() != null) {
			image.setImageDateToPublish(xmlObject.getDateToPublish());
		}

		if (xmlObject.getCopyrightText() != null)
			image.setCopyrightText(xmlObject.getCopyrightText());
		if (xmlObject.getCreativeCommons() != null)
			image.setCreativeCommons(xmlObject.getCreativeCommons());
		// private String creativeCommons;
		if (xmlObject.getOriginalFileName() != null)
			image.setOriginalFileName(xmlObject.getOriginalFileName());
		if (image.getOriginalFileName() == null || image.getOriginalFileName().length() == 0) {
			// no file name, look at URI of sourceId
			XmlId sourceId = xmlObject.getSourceId();
			if (sourceId != null) {
				String uri = sourceId.getFirstURI();
				if (uri != null && uri.length() != 0) {
					image.setOriginalFileName(uri);
				}
			}
		}
		image.setImageHeight(xmlObject.getHeight());
		image.setImageWidth(xmlObject.getWidth());
		image.setMagnification(xmlObject.getMagnification());
		if (xmlObject.getImageType() != null) image.setImageType(xmlObject.getImageType());
		if (xmlObject.getThumbUrl() != null) image.setThumbURL(Integer.toString(image.getId()));
		try {
			if (xmlObject.getMagnification() != null) {
				image.setMagnification(xmlObject.getMagnification());
			}
			if (xmlObject.getResolution() != null) {
				image.setResolution(Integer.parseInt(xmlObject.getResolution()));
			}
		} catch (NumberFormatException e) {
			// let it go!
		}
		if (xmlObject.getPhotographer() != null)
			image.setPhotographer(xmlObject.getPhotographer());
		if (xmlObject.getEol() != null)
			image.setEol(xmlObject.getEol());
		// TODO add rest of fields
		return true;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Image)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		Image image = (Image) idObject;
		if (!objMapper.setXmlFields(xmlObject, image.getSpecimen())) return false;
		if (!objMapper.setXmlFields(xmlObject, image.getView())) return false;
		if (!objMapper.setXmlFields(xmlObject, image.getSpecimen().getLocality())) return false;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Image)) return false;
		Image image = (Image) idObject;
		// Image fields
		xmlObject.setImage(new XmlId(image.getId()));
		if (image.getSpecimen() != null) {
			xmlObject.setSpecimen(objMapper.createXmlId(image.getSpecimen(), null));
		}
		xmlObject.addView(objMapper.createXmlId(image.getView(), null));
		xmlObject.setImageType(image.getImageType());
		xmlObject.setCopyrightText(image.getCopyrightText());
		if (image.getMagnification() != null) {
			xmlObject.setMagnification(image.getMagnification());
		}
		if (image.getResolution() != null) {
			xmlObject.setResolution(image.getResolution().toString());
		}
		xmlObject.setOriginalFileName(image.getOriginalFileName());
		xmlObject.setHeight(image.getImageHeight());
		xmlObject.setWidth(image.getImageWidth());
		xmlObject.setCreativeCommons(image.getCreativeCommons());
		xmlObject.setPhotographer(image.getPhotographer());
		if (image != null && image.getEol() == 1) {
			xmlObject.setEol("yes");
		}
		return true;
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object, XmlBaseObject responseObject) {
		if (object == null) return true;
		if (!(object instanceof Image)) return false;
		if (!super.linkObject(xmlObject, object, responseObject)) return false;
		Image image = (Image) object;

		XmlId specId = xmlObject.getSpecimen();
		BaseObject related = XmlServices.getObject(specId);
		// Specimen specimen = null;
		// create link to related view and specimen objects
		if (related == null) {
			// no specimen in database
			xmlObject
			.setStatus("specimen id not found in database, info stored in MissingLink table ");
			xmlMapper.recordMissingLink(image, specId, MissingLink.SPECIMEN);
		} else {
			if (!(related instanceof Specimen)) {
				// TODO error! reference to non-specimen
				xmlObject.setStatus("specimen id refers to an object of type "
						+ related.getObjectTypeIdStr());
				return false;
			}

			Specimen specimen = (Specimen) related;
			image.setSpecimen(specimen);

			if (specimen.getStandardImage() == null) {
				// first image of the specimen: make it the standard image
				specimen.setStandardImage(image);
			}

		}

		boolean updateViewResult = updateImageView(xmlObject, image);
		if (updateViewResult) {
			// TODO react to whether view update happened or not?
		}


		return true;
	}

	// TODO check the code that manages views. Look for simplification

	public boolean updateImageView(XmlBaseObject xmlImage, Image image) {
		Iterator<XmlId> viewIds = xmlImage.getView().iterator();
		View view = null;
		boolean changedView = false;
		while (viewIds.hasNext()) {
			XmlId viewId = viewIds.next();
			// add a view from the morphbank id
			int intViewId = viewId.getMorphbank();
			if (intViewId != 0) {
				view = (View) BaseObject.getEJB3Object(intViewId);
				if (view != null) {
					changedView |= setView(image, view);
				} else {
					xmlMapper
					.recordMissingLink(image, viewId, MissingLink.VIEW);
				}
			}
			// add views from the external ids
			if (viewId.getExternal() != null) {
				String result = setViewFromExtId(image, viewId);
				if (result != null && result.length() > 0) {
					xmlImage.setStatus(result);
					changedView |= true;
				}
			}
			//TODO
			// use local id
			//xmlImage.get
			if (viewId.getLocal() != null) {
				if (xmlMapper.getBaseObject(viewId) instanceof View) {
					view = (View) xmlMapper.getBaseObject(viewId);
					changedView |= setView(image, view);
				}

			}

			// use getRelatedMorphbankObject(xmlObject); from CreateObjectsFromXml --> gets MB id from a local xml id
			//also check if it's a view first
		}
		return changedView;
	}

	static final String VIEW_ROLE = "view";

	/**
	 * Set the view reference from the external references When more than one
	 * view reference exists, add the extras as related objects, if necessary
	 * 
	 * @param image
	 * @param xmlObject
	 * @return
	 */
	private String setViewFromExtId(Image image, XmlId viewId) {
		// XmlId viewId = xmlImage.getView();
		if (viewId == null || viewId.getExternal() == null) { return null; }
		View view = XmlServices.getView(viewId);
		if (view != null) {
			setView(image, view);
			return "";
		}
		String objExtId = image.getFirstExternalId();
		MorphbankConfig.SYSTEM_LOGGER.info("view ext id " + viewId.toString()
				+ "\tmorphbank id " + image.getId() + "\t external id"
				+ objExtId);
		return "Reference to view " + viewId.toString() + " unresolved";
	}

	/**
	 * if view is not already related to image, add it
	 * 
	 * @param image
	 * @param view
	 * @return
	 */
	private boolean setView(Image image, View view) {
		if (image.getView() == null) {
			image.setView(view);
			return true;
		} 
		if (image.getView() == view) return false;

		// check to see if view is already related
		CollectionObject collObj = image.getRelatedObject(view);
		if (collObj != null) {// view is in the list of related objects
			String roll = collObj.getObjectRole();
			if (VIEW_ROLE.equals(roll)) {
				// view is already in the list
				return false;
			}
		}
		// view is not related
		image.addRelatedObject(view, VIEW_ROLE);
		return true;
		// else view already includes this reference

	}

}
