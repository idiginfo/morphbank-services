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
import net.morphbank.object.Annotation;
import net.morphbank.object.CharacterState;
import net.morphbank.object.Collection;
import net.morphbank.object.DeterminationAnnotation;
import net.morphbank.object.Group;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.Locality;
import net.morphbank.object.MbCharacter;
import net.morphbank.object.News;
import net.morphbank.object.Publication;
import net.morphbank.object.Specimen;
import net.morphbank.object.Taxon;
import net.morphbank.object.TaxonBranchNode;
import net.morphbank.object.TaxonConcept;
import net.morphbank.object.User;
import net.morphbank.object.View;

import com.hp.hpl.jena.rdf.model.Resource;

public abstract class RdfBase implements RdfMetadata {

	public static RdfBase createRdfObject(IdObject obj) {
		if (obj instanceof DeterminationAnnotation) {
			return new RdfDeterminationAnnotation((DeterminationAnnotation) obj);
		} else if (obj instanceof Annotation) {
			return new RdfAnnotation((Annotation) obj);
		} else if (obj instanceof CharacterState) {
			return new RdfCharacterState((CharacterState) obj);
		} else if (obj instanceof Collection) {
			return new RdfCollection((Collection) obj);
		} else if (obj instanceof Group) {
			return new RdfGroup((Group) obj);
		} else if (obj instanceof Image) {
			return new RdfImage((Image) obj);
		} else if (obj instanceof Locality) {
			return new RdfLocality((Locality) obj);
			// } else if (obj instanceof Matrix) {
			// return new RdfMatrix((Matrix) obj);
		} else if (obj instanceof MbCharacter) {
			return new RdfMbCharacter((MbCharacter) obj);
		} else if (obj instanceof News) {
			return new RdfNews((News) obj);
			// } else if (obj instanceof Otu) {
			// return new RdfOtu((Otu) obj);
		} else if (obj instanceof Publication) {
			return new RdfPublication((Publication) obj);
		} else if (obj instanceof Specimen) {
			return new RdfSpecimen((Specimen) obj);
		} else if (obj instanceof Taxon) {
			return new RdfTaxon((Taxon) obj);
		} else if (obj instanceof TaxonBranchNode) {
			return new RdfTaxonBranchNode((TaxonBranchNode) obj);
		} else if (obj instanceof TaxonConcept) {
			return new RdfTaxonConcept((TaxonConcept) obj);
		} else if (obj instanceof User) {
			return new RdfUser((User) obj);
		} else if (obj instanceof View) {
			return new RdfView((View) obj);
		} else {
			return null;
		}
	}

	public void addDetailProperties(Resource res, int depth) {
		addBasicProperties(res);
		addReferenceProperties(res);
		if (depth > 1) {
			addObjects(res, depth);
		}
	}

	public abstract String getRDFDefinitionURI();

	public Resource createResource() {
		return RdfOntologies.getOntModel().createIndividual(
				MorphbankConfig.makeURI(getId()),
				RdfOntologies.darwinCoreModel
						.getResource(getRDFDefinitionURI()));
	}

	public Resource toDetailRDF(int depth) {
		Resource res = createResource();
		addDetailProperties(res, depth);
		return res;
	}

	public Resource toSimpleRDF() {
		Resource res = RdfOntologies.getRdfModel().createResource(getId(),
				getRDFDefinitionURI());
		addBasicProperties(res);
		addReferenceProperties(res);
		return res;
	}

	public static String makeURI(int id) {
		return null;
	}

}
