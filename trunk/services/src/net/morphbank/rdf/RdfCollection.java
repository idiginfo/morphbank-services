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

import java.util.Iterator;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.CollectionObject;

import com.hp.hpl.jena.rdf.model.Resource;

public class RdfCollection extends RdfBaseObject implements RdfMetadata {

	BaseObject collection;

	public RdfCollection(BaseObject collection) {
		super(collection);
		this.collection = collection;
	}

	@Override
	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "Collection";
	}

	public void addBasicProperties(Resource res) {
		// if res has a userid, don't add properties
		super.addBasicProperties(res);
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("name"),
				collection.getName());
		/*
		 * RdfOntologies.addProperty(res, RdfOntologies
		 * .mbankProperty("publicationId"), collection .getPublicationId());
		 */}

	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, collection.getUser(), "owner");
		RdfOntologies.addReference(res, this, collection.getGroup(),
				"OwnerGroup");
		Iterator<CollectionObject> iter = collection.getObjects().iterator();
		while (iter.hasNext()) {
			RdfOntologies.addReference(res, this, iter.next().getObject(),
					"object");
		}
	}

	public void addObjects(Resource res, int depth) {
		if (collection.getUser() != null) {// call getSpecimen to force
			// load
			createRdfObject(collection.getUser()).toDetailRDF(depth - 1);
		}
		if (collection.getGroup() != null) {// call getSpecimen to force
			// load
			createRdfObject(collection.getGroup()).toDetailRDF(depth - 1);
		}
		Iterator<CollectionObject> iter = collection.getObjects().iterator();
		while (iter.hasNext()) {
			createRdfObject(iter.next().getObject()).toDetailRDF(depth - 1);
		}

	}

}
