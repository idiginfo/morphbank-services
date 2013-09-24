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
import net.morphbank.mbsvc3.xml.XmlTaxonNameUtilities;
import net.morphbank.object.Annotation;
import net.morphbank.object.BaseObject;
import net.morphbank.object.DeterminationAnnotation;
import net.morphbank.object.IdObject;
import net.morphbank.object.Taxon;

public class MapAnnotation extends MapObjectBase {

	// protected Annotation annotation;

	public MapAnnotation(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapAnnotation(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapAnnotation(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	// interface methods
	@Override
	public Annotation createObject(XmlBaseObject xmlObject, XmlId xmlId) {
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
		Annotation annotation = new Annotation(id, name, ownerCred.getUser(), submitterCred
				.getUser(), ownerCred.getGroup());

		// TODO map determination annotation!
		annotation = new DeterminationAnnotation(id, name, ownerCred.getUser(), submitterCred
				.getUser(), ownerCred.getGroup());
		annotation.persist();
		boolean success = updateObject(annotation, xmlObject);
		if (success) return annotation;
		return null;
	}

	/**
	 * Create an annotation object from an XmlBaseObject object The annotation
	 * itself has not yet been processed no keywords, no access number, no
	 * annotation file
	 * 
	 * @param xmlObject
	 * @return
	 */
	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Annotation)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Annotation annotation = (Annotation) idObject;
		// TODO add rest of fields
		annotation.setTypeAnnotation(xmlObject.getTypeAnnotation());
		annotation.setXLocation(xmlObject.getXLocation());
		annotation.setYLocation(xmlObject.getYLocation());
		annotation.setAreaHeight(xmlObject.getAreaHeight());
		annotation.setAreaWidth(xmlObject.getAreaWidth());
		annotation.setAreaRadius(xmlObject.getAreaRadius());
		annotation.setAnnotationLabel(xmlObject.getAnnotationLabel());
		annotation.setAnnotationQuality(xmlObject.getAnnotationQuality());
		annotation.setTitle(xmlObject.getTitle());
		annotation.setComment(xmlObject.getComment());
		annotation.setXMLData(xmlObject.getXMLData());
		annotation.setAnnotationLabel(xmlObject.getAnnotationLabel());
		if (annotation instanceof DeterminationAnnotation) {
			DeterminationAnnotation detAnnotation = (DeterminationAnnotation) annotation;
			// TODO add Determination Annotation fields
			// private Specimen specimen;
			// private Taxon taxon;
			// private String rankName;
			// private String prefix;
			// private String suffix;
			// private String typeDetAnnotation;
			// private String sourceOfId;
			// private String materialsUsedInId;
			// private String resourcesused;
			// private BaseObject collection;
			// private String altTaxonName;
		}

		return true;
	}

	@Override
	public Annotation createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Annotation annotation;
		if (idObject == null) {
			annotation = (Annotation) createObject(xmlObject);
			return (annotation);
			// TODO } else if (idObject instanceof DeterminationAnnotation){

		} else if (idObject instanceof Annotation) {
			annotation = (Annotation) idObject;
			boolean updateSuccess = updateObject(annotation, xmlObject);
			if (updateSuccess) return annotation;
		}
		return null;
	}

	// Methods to map Annotation to XmlBaseObject
	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Annotation)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		// Annotation annotation = (Annotation) idObject;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Annotation)) return false;
		Annotation annotation = (Annotation) idObject;

		xmlObject.setObject(annotation.getObject().getId());
		xmlObject.setTypeAnnotation(annotation.getTypeAnnotation());
		xmlObject.setXLocation(annotation.getXLocation());
		xmlObject.setYLocation(annotation.getYLocation());
		xmlObject.setAreaHeight(annotation.getAreaHeight());
		xmlObject.setAreaWidth(annotation.getAreaWidth());
		xmlObject.setAreaRadius(annotation.getAreaRadius());
		xmlObject.setAnnotationLabel(annotation.getAnnotationLabel());
		xmlObject.setAnnotationQuality(annotation.getAnnotationQuality());
		xmlObject.setTitle(annotation.getTitle());
		xmlObject.setComment(annotation.getComment());
		xmlObject.setXMLData(annotation.getXMLData());
		xmlObject.setAnnotationLabel(annotation.getAnnotationLabel());
		// <typeAnnotation>Determination</typeAnnotation>
		String typeAnnotation = annotation.getTypeAnnotation();
		if (annotation instanceof DeterminationAnnotation) {
			DeterminationAnnotation detAnnotation = (DeterminationAnnotation) annotation;
			// TODO set fields
			xmlObject.setTypeDetAnnotation(detAnnotation.getTypeDetAnnotation());
			xmlObject.setSourceOfId(detAnnotation.getSourceOfId());
			xmlObject.setMaterialsUsedInId(detAnnotation.getMaterialsUsedInId());
			xmlObject.setResourcesused(detAnnotation.getResourcesused());
			xmlObject.setAltTaxonName(detAnnotation.getAltTaxonName());
			xmlObject.setTaxon(objMapper.createXmlId(detAnnotation.getTaxon(), null));
			xmlObject.setCollection(objMapper.createXmlId(detAnnotation.getMyCollection(), null));
		}
		return true;
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object, XmlBaseObject responseObject) {
		if (object == null) return true;
		if (!(object instanceof Annotation)) return false;
		if (!super.linkObject(xmlObject, object, responseObject)) return false;
		Annotation annotation = (Annotation) object;
		BaseObject related = null;
		// create link to related view and specimen objects
		XmlId objectId = xmlObject.getObject();
		// use morphbank id to get object reference
		related = XmlServices.getObject(objectId);
		annotation.setObject(related);
		if (annotation instanceof DeterminationAnnotation) {
			DeterminationAnnotation detAnnotation = (DeterminationAnnotation) annotation;
			// handle taxon as tsn or scientific name
			Taxon taxon = Taxon.getTaxon(XmlTaxonNameUtilities.getTsn(xmlObject.getTaxon()));
			if (taxon == null) {
				String sciName = XmlTaxonNameUtilities.getScientificName(xmlObject.getTaxon());
				taxon = Taxon.getTaxon(sciName);
			}
			detAnnotation.setTaxon(taxon);
			BaseObject collection = XmlServices.getObject(xmlObject.getCollection());
			detAnnotation.setMyCollection(collection);
		}
		return true;
	}

}
