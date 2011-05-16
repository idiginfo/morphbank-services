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

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RdfModel {

	OntModel model = null;

	public RdfModel() {
		createModel();
	}

	public OntModel getModel() {
		return model;
	}

	public OntModel createOntModel() {
		return createModel();
	}

	public OntModel createModel() {
		model = ModelFactory.createOntologyModel();
		model.setNsPrefix("mbank", MorphbankConfig.MORPHBANK_SCHEMA_URI);
		model.setNsPrefix("darwin", MorphbankConfig.DARWIN_URI);
		model.setNsPrefix("dc", MorphbankConfig.DUBLIN_CORE_URI);
		model.setNsPrefix("foaf", MorphbankConfig.FOAF_URI);
		model.setNsPrefix("mrtg", MorphbankConfig.MRTG_URI);

		return model;
	}

	public Resource createResource(int id, String uri) {
		return model.createIndividual(MorphbankConfig.makeURI(id),
				RdfOntologies.darwinCoreModel.getResource(uri));
	}
}
