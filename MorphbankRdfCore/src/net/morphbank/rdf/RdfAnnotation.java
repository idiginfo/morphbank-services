/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.Annotation;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public class RdfAnnotation extends RdfBaseObject {
	Annotation annotation;

	public RdfAnnotation(Annotation annotation) {
		super(annotation);
		this.annotation = annotation;
	}

	@Override
	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "Annotation";
	}

	public void addBasicProperties(Resource res) {
		// if res has a userid, don't add properties
		super.addBasicProperties(res);
		// RdfOntologies.addProperty(res, RdfOntologies
		// .mbankProperty("phylogeneticStateId"), annotation
		// .getPhylogeneticStateId());
		// RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("value"),
		// annotation.getValue());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("typeAnnotation"), annotation
				.getTypeAnnotation());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("xLocation"), annotation
						.getXLocation());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("yLocation"), annotation
						.getYLocation());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("areaHeight"), annotation.getAreaHeight());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("areaWidth"), annotation
						.getAreaWidth());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("areaRadius"), annotation.getAreaRadius());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("annotationQuality"), annotation
				.getAnnotationQuality());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("title"),
				annotation.getTitle());
		RdfOntologies.addProperty(res, RdfOntologies.dublinProperty("subject"),
				annotation.getKeywords());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("comment"),
				annotation.getComment());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("XMLData"),
				annotation.getXMLData());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("annotationLabel"), annotation
				.getAnnotationLabel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addReferenceProperties(com.hp.hpl.jena.rdf.model.Resource)
	 */
	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, annotation.getObject(), "object");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.morphbank.rdfmetadata.RdfMetadata#addObjects(com.hp.hpl.jena.rdf.model.Resource,
	 *      int)
	 */
	public void addObjects(Resource res, int depth) {
		if (annotation.getObject() != null) {
			RdfBase.createRdfObject(annotation.getObject()).toDetailRDF(depth - 1);
		}
	}

}
