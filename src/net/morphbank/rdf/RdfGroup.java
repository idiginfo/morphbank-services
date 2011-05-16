/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.Group;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public class RdfGroup extends RdfBaseObject {
	Group group;

	public RdfGroup(Group group) {
		super(group);
		this.group = group;
	}

	@Override
	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "Group";
	}

	public void addBasicProperties(Resource res) {
		// if res has a userid, don't add properties
		super.addBasicProperties(res);
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("groupName"), group.getGroupName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addReferenceProperties(com.hp.hpl.jena.rdf.model.Resource)
	 */
	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, group.getTaxon(), "taxon");
		RdfOntologies.addReference(res, this, group.getGroupManager(),
				"groupManger");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addObjects(com.hp.hpl.jena.rdf.model.Resource,
	 *      int)
	 */
	public void addObjects(Resource res, int depth) {
		if (group.getTaxon() != null) {
			createRdfObject(group.getTaxon()).toDetailRDF(depth - 1);
		}
		if (group.getGroupManager() != null) {
			createRdfObject(group.getGroupManager()).toDetailRDF(depth - 1);
		}
	}

}
