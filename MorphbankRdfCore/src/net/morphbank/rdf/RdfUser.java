/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.User;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public class RdfUser extends RdfBaseObject {
	User user;

	public RdfUser(User user) {
		super(user);
		this.user = user;
	}

	@Override
	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "User";
	}

	public void addBasicProperties(Resource res) {
		// if res has a userid, don't add properties
		super.addBasicProperties(res);
		RdfOntologies
				.addProperty(res, RdfOntologies.foafProperty("firstName"), user.getFirstName());
		RdfOntologies.addProperty(res, RdfOntologies.foafProperty("lastName"), user.getLastName());
		RdfOntologies.addProperty(res, RdfOntologies.foafProperty("name"), user.getName());
		RdfOntologies.addProperty(res, RdfOntologies.foafProperty("mbox"), user.getEmail());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("affiliation"), user
				.getAffiliation());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("accountName"), user.getUin());
		//RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("pin"), user.getPin());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("suffix"), user.getSuffix());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("middle_init"), user
				.getMiddleInit());
		// RdfOntologies.addProperty(res,
		// RdfOntologies.mbankProperty("street1"), user.getStreet1());
		// RdfOntologies.addProperty(res,
		// RdfOntologies.mbankProperty("street2"), user.getStreet2());
		// RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("city"),
		// user.getCity());
		// RdfOntologies.addProperty(res,
		// RdfOntologies.mbankProperty("country"), user.getCountry());
		// RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("state"),
		// user.getState());
		// RdfOntologies.addProperty(res,
		// RdfOntologies.mbankProperty("zipcode"), user.getZipcode());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("status"), user.getStatus());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.morphbank.rdfmetadata.RdfMetadata#addReferenceProperties(com.hp.hpl
	 * .jena.rdf.model.Resource)
	 */
	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, user.getPrivilegeTaxon(), "primaryTaxon");
		// RdfOntologies.addReference(res, this, user.getSecondaryTaxon(),
		// "secondaryTaxon");
		// RdfOntologies.addReference(res, this, user.getImages(), "images");
		// RdfOntologies.addReference(res, this, user.getSpecimens(),
		// "specimens");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.morphbank.rdfmetadata.RdfMetadata#addObjects(com.hp.hpl.jena.rdf.
	 * model.Resource, int)
	 */
	public void addObjects(Resource res, int depth) {
		if (user.getPrivilegeTaxon() != null) {
			createRdfObject(user.getPrivilegeTaxon()).toDetailRDF(depth - 1);
		}
		// if (user.getSecondaryTaxon() != null) {
		// getRdfObject(user.getSecondaryTaxon()).toDetailRDF(depth - 1);
		// }
		/*
		 * if (user.getSpecimens() != null) { Iterator<Specimen> imageIter =
		 * user.getSpecimens().iterator(); while (imageIter.hasNext()) {
		 * Specimen specimen = imageIter.next();
		 * specimen.getRdfObject().toDetailRDF((OntModel) res.getModel(), depth
		 * - 1); } }
		 */
		/*
		 * if (user.getImages() != null) { Iterator<Image> imageIter =
		 * user.getImages().iterator(); while (imageIter.hasNext()) { Image
		 * image = imageIter.next(); getRdfObject(image).toDetailRDF((OntModel)
		 * res.getModel(), depth - 1); } }
		 */
	}
}
