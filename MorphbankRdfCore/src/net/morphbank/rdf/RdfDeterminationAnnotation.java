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
import net.morphbank.object.DeterminationAnnotation;

import com.hp.hpl.jena.rdf.model.Resource;

public class RdfDeterminationAnnotation extends RdfAnnotation {

	static final long serialVersionUID = 1;
	DeterminationAnnotation deterAnno;

	public RdfDeterminationAnnotation(DeterminationAnnotation deterAnno) {
		super(deterAnno);
		this.deterAnno = deterAnno;
	}

	@Override
	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "DeterminationAnnotation";
	}

	public void addBasicProperties(Resource res) {
		// if res has a userid, don't add properties
		super.addBasicProperties(res);
		if (deterAnno.getTaxon() != null) {
			RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("scientificName"),
					deterAnno.getTaxon().getScientificName());
		}
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("rankName"), deterAnno
				.getRankName());
		RdfOntologies
				.addProperty(res, RdfOntologies.mbankProperty("prefix"), deterAnno.getPrefix());
		RdfOntologies
				.addProperty(res, RdfOntologies.mbankProperty("suffix"), deterAnno.getSuffix());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("typeDetAnnotation"), deterAnno
				.getTypeDetAnnotation());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("sourceOfId"), deterAnno
				.getSourceOfId());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("materialsUsedInId"), deterAnno
				.getMaterialsUsedInId());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("resourcesused"), deterAnno
				.getResourcesused());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("altTaxonName"), deterAnno
				.getAltTaxonName());
	}

	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, deterAnno.getSpecimen(), "specimen");
		RdfOntologies.addReference(res, this, deterAnno.getTaxon(), "taxon");
		RdfOntologies.addReference(res, this, deterAnno.getMyCollection(), "collection");
	}

	public void addObjects(Resource res, int depth) {

		if (deterAnno.getSpecimen() != null) {// call getSpecimen to force
			// load
			createRdfObject(deterAnno.getSpecimen()).toDetailRDF(depth - 1);
		}
		if (deterAnno.getTaxon() != null) {// call getSpecimen to force load
			createRdfObject(deterAnno.getTaxon()).toDetailRDF(depth - 1);
		}
		if (deterAnno.getMyCollection() != null) {// call getSpecimen to force
			// load
			createRdfObject(deterAnno.getMyCollection()).toDetailRDF(depth - 1);
		}
	}
}
