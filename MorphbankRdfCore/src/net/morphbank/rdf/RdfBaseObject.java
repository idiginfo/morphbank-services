/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public abstract class RdfBaseObject extends RdfBase implements RdfMetadata {

	BaseObject base;

	public RdfBaseObject(BaseObject base) {
		this.base = base;
	}

	public void addBasicProperties(Resource res) {
		// res.addProperty(Ontologies.property("id"), getId());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("identifier"), MorphbankConfig
				.getURL(base.getId()));
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("publisher"), MorphbankConfig
				.getPublisher());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("userId"), base.getUserId());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("contributor"), base.getUser()
				.getName());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("creator"), base
				.getSubmittedBy().getName());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("groupId"), base.getGroupId());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("title"), "joe");
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("dateCreated"), base
				.getDateCreated());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("created"), base
				.getDateCreated());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("dateLastModified"), base
				.getDateLastModified());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("dateToPublish"), base
				.getDateToPublish());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("description"), base
				.getDescription());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("objectLogo"), base
				.getObjectLogo());
		// RdfOntologies.addProperty(res, RdfOntologies
		// .mbankProperty("keywords"), base.getKeywords());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("summaryHTML"), base
				.getSummaryHTML());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("thumbURL"), base
				.getFullThumbURL());
	}

	public String getURI() {
		return MorphbankConfig.getURL(getId());
	}

	public int getId() {
		return base.getId();
	}
}
