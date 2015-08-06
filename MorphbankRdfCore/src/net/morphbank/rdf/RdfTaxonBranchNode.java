/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.TaxonBranchNode;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public class RdfTaxonBranchNode extends RdfBase {
	TaxonBranchNode node;

	public RdfTaxonBranchNode(TaxonBranchNode node) {
		this.node = node;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "TaxonBranchNode";
	}

	public String getName() {
		return node.getName();
	}

	public int getId() {
		return node.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addBasicProperties(com.hp.hpl.jena.rdf.model.Resource)
	 */
	public void addBasicProperties(Resource res) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addReferenceProperties(com.hp.hpl.jena.rdf.model.Resource)
	 */
	public void addReferenceProperties(Resource res) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addObjects(com.hp.hpl.jena.rdf.model.Resource,
	 *      int)
	 */
	public void addObjects(Resource res, int depth) {

	}

}
