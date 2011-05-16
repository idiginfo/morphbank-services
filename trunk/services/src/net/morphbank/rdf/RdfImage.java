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
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.Image;

import com.hp.hpl.jena.rdf.model.Resource;

public class RdfImage extends RdfBaseObject implements RdfMetadata {

	static final long serialVersionUID = 1;

	Image image;

	// constructors
	public RdfImage(Image image) {
		super(image);
		this.image = image;
	}

	// constants
	public static final String IS_PUBLIC_IMAGE_SQL = "select id from baseObject where objectTypeId='XmlImage'and dateToPublish<now() and id=";

	// Metadata methods

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "XmlImage";
	}

	public void addBasicProperties(Resource res) {
		// if res has a userid, don't add properties
		super.addBasicProperties(res);
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("imageHeight"),
				image.getImageHeight());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("imageWidth"),
				image.getImageWidth());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("resolution"),
				image.getResolution());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("magnification"),
				image.getMagnification());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("imageType"), image.getImageType());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("photographer"),
				image.getPhotographer());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("rights"),
				image.getCopyrightText());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("license"),
				image.getCreativeCommons());
	}

	public void addReferenceProperties(Resource res) {
		// add related objects as properties
		// add references to the object properties specimen, view
		RdfOntologies.addReference(res, this, image.getSpecimen(), "specimen");
		RdfOntologies.addReference(res, this, image.getView(), "view");
	}

	public void addObjects(Resource res, int depth) {
		// generate RDF objects and properties for related objects
		// add resources for the related Specimen and View
		if (image.getSpecimen() != null) {// call getSpecimen to force load
			createRdfObject(image.getSpecimen()).toDetailRDF(depth - 1);
		}
		if (image.getView() != null) {
			createRdfObject(image.getView()).toDetailRDF(depth - 1);
		}
	}

}
