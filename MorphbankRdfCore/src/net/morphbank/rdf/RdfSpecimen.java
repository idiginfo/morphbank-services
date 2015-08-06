/**
 * 
 */
package net.morphbank.rdf;

import java.util.Collection;
import java.util.Iterator;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.Image;
import net.morphbank.object.Specimen;
import net.morphbank.object.UserProperty;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author riccardi
 * 
 */
public class RdfSpecimen extends RdfBaseObject implements RdfMetadata {
	static final long serialVersionUID = 1;
	Specimen specimen;

	public RdfSpecimen(Specimen specimen) {
		super(specimen);
		this.specimen = specimen;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.DARWIN_URI + "DarwinCoreSpecimen";
	}

	public void addBasicProperties(Resource res) {
		super.addBasicProperties(res);
		addObjectProperties(res);
	}

	public void addObjectProperties(Resource res) {

		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("BasisOfRecord"), specimen
				.getBasisOfRecordDesc());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Sex"), specimen.getSex());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("Form"), specimen.getForm());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("LifeStage"), specimen
				.getDevelopmentalStage());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Preparations"), specimen
				.getPreparationType());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("IndividualCount"), specimen
				.getIndividualCount());
		// RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("tsnId"),
		// getTsnId().toString());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("TypeStatus"), specimen
				.getTypeStatus());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("DateIdentified"), specimen
				.getDateIdentified());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("IdentifiedBy"), specimen
				.getName());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("comment"), specimen
				.getComment());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("InstitutionCode"), specimen
				.getInstitutionCode());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("CollectionCode"), specimen
				.getCollectionCode());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("CatalogNumber"), specimen
				.getCatalogNumber());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("OtherCatalogNumbers"),
				specimen.getPreviousCatalogNumber());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("RelatedCatalogItems"),
				specimen.getRelatedCatalogItem());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("relationshipType"), specimen
				.getRelationshipType());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("CollectionNumber"), specimen
				.getCollectionNumber());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Collector"), specimen
				.getCollectorName());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Notes"), specimen.getNotes());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("imagesCount"), specimen
				.getImagesCount().toString());

		// use datecollected for both earliest and latest
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("EarliestDateCollected"),
				specimen.getDateCollected());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("LatestDateCollected"),
				specimen.getDateCollected());
		// if earliest and/or latest are set, overide above values
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("EarliestDateCollected"),
				specimen.getEarliestDateCollected());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("LatestDateCollected"),
				specimen.getLatestDateCollected());
		if (specimen.getLocality() != null) {
			createRdfObject(specimen.getLocality()).addBasicProperties(res);
		}
		if (specimen.getTaxon() != null) {
			RdfTaxon.addTaxaProperties(res, specimen.getTaxon());
		}
		// add all user properties that are defined and have URIs
		Collection<UserProperty> propertyList = specimen.getUserProperties().values();
		Iterator<UserProperty> properties = propertyList.iterator();
		while (properties.hasNext()) {
			UserProperty property = properties.next();
			String namespace = property.getNamespaceURI();
			if (namespace != null && namespace.length() > 0) {
				res.addProperty(ResourceFactory.createProperty(namespace, property.getName()),
						property.getValue());
			}
		}
	}

	public void addReferenceProperties(Resource res) {
		/*
		 * if (specimen.getLocality() != null) {// call getSpecimen to force
		 * load Resource resLocality = ResourceFactory.createResource(manip
		 * .makeLSID(getLocality().getId()));
		 * res.addProperty(RdfOntologies.mbankProperty("locality"),
		 * resLocality); }
		 */
		RdfOntologies.addReference(res, this, specimen.getStandardImage(), "standardImage");
		// add image references
		RdfOntologies.addReference(res, this, specimen.getImages(), "images");
	}

	public void addObjects(Resource res, int depth) {
		// add related objects and their properties
		/*
		 * if (specimen.getLocality() != null) { locality.toDetailRDF((OntModel)
		 * res.getModel(), depth - 1); // locality.addProperties(model, res); }
		 */
		if (specimen.getStandardImage() != null) {
			createRdfObject(specimen.getStandardImage()).toDetailRDF(depth - 1);
		}
		// add image objects
		if (specimen.getImages() != null) {
			Iterator<Image> imageIter = specimen.getImages().iterator();
			while (imageIter.hasNext()) {
				Image image = imageIter.next();
				createRdfObject(image).toDetailRDF(depth - 1);
			}
		}
	}
}
