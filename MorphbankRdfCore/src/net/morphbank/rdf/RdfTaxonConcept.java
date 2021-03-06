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
import net.morphbank.object.TaxonConcept;

import com.hp.hpl.jena.rdf.model.Resource;

public class RdfTaxonConcept extends RdfBaseObject implements RdfMetadata {
	static final long serialVersionUID = 1;

	TaxonConcept taxonConcept;

	public RdfTaxonConcept(TaxonConcept taxonConcept) {
		super(taxonConcept);
		this.taxonConcept = taxonConcept;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "TaxonConcept";
	}

	// Resourse creation: meaningful comment goes here
	@Override
	public void addBasicProperties(Resource res) {
		// super.addBasicProperties(res);
		// RdfOntologies.addProperty(res, RdfOntologies
		// .darwinProperty("maximumDepth"), taxonConcept.getMaximumDepth());
	}

	public void addReferenceProperties(Resource res) {
	}

	public void addObjects(Resource res, int depth) {
		// add related resources
		// none so far
	}

}
