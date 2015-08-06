/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.Taxon;
import net.morphbank.object.TaxonBranchNode;
import net.morphbank.object.TaxonConcept;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public class RdfTaxon extends RdfBase {
	static final long serialVersionUID = 1;

	Taxon taxon;
	TaxonConcept taxonConcept;

	public RdfTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "Taxon";
	}

	public String getScientificName() {
		return taxon.getScientificName();
	}

	public int getId() {
		return taxon.getId();
	}

	public void addBasicProperties(Resource res) {
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("tsn"),
				taxon.getTsn());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("scientificName"), taxon.getScientificName());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("rank"),
				taxon.getRank());
		addTaxa(res);
	}

	public static void addRankProperty(Taxon taxon, Resource res, String rankName,
			String propertyName) {
		TaxonBranchNode node;
		node = taxon.getTaxonNodes().get(rankName);
		if (node != null) {
			RdfOntologies.addProperty(res, RdfOntologies
					.darwinProperty(propertyName), node.getName());
		}
	}

	public void addTaxa(Resource res) {
		// add tree nodes as properties
		addTaxaProperties(res, taxon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addReferenceProperties(com.hp.hpl.jena.rdf.model.Resource)
	 */
	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, taxon.getSpecimens(), "specimen");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addObjects(com.hp.hpl.jena.rdf.model.Resource,
	 *      int)
	 */
	public void addObjects(Resource res, int depth) {

	}

	public static void addTaxaProperties(Resource res, Taxon taxon) {
		// TODO Auto-generated method stub
		addRankProperty(taxon, res, "Kingdom", "kingdom");
		addRankProperty(taxon, res, "Phylum", "phylum");
		addRankProperty(taxon, res, "Class", "class");
		addRankProperty(taxon, res, "Order", "order");
		addRankProperty(taxon, res, "Family", "family");
		addRankProperty(taxon, res, "Genus", "genus");
		addRankProperty(taxon, res, "Species", "species");
		addRankProperty(taxon, res, "Subspecies", "subspecies");
	}
}
