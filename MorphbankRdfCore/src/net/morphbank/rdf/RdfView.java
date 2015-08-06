/**
 * 
 */
package net.morphbank.rdf;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.View;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author riccardi
 * 
 */
public class RdfView extends RdfBaseObject implements RdfMetadata {
	static final long serialVersionUID = 1;

	View view;

	public RdfView(View view) {
		super(view);
		this.view = view;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "View";
	}

	public void addBasicProperties(Resource res) {
		super.addBasicProperties(res);
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("viewName"),
				view.getViewName());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("imagingTechnique"), view.getImagingTechnique());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("imagingPreparationTechnique"), view
				.getImagingPreparationTechnique());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("specimenPart"), view.getSpecimenPart());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("viewAngle"), view.getViewAngle());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("developmentalStage"), view
				.getDevelopmentalStage());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("sex"), view
				.getSex());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("form"),
				view.getForm());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("isStandardView"), view.getIsStandardView());
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("imagingTechnique"), view.getImagesCount());
	}

	public void addReferenceProperties(Resource res) {
		RdfOntologies.addReference(res, this, view.getStandardImage(),
				"standardImage");
		RdfOntologies.addReference(res, this, view.getTaxon(), "taxon");
	}

	public void addObjects(Resource res, int depth) {
		if (view.getStandardImage() != null) {// call getStandardImage to
			// force
			createRdfObject(view.getStandardImage()).toDetailRDF(depth - 1);
		}
		if (view.getTaxon() != null) {// call getStandardImage to force
			createRdfObject(view.getTaxon()).toDetailRDF(depth - 1);
		}
	}

}
