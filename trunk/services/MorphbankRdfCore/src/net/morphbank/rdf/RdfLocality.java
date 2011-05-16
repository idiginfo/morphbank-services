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
import net.morphbank.object.Locality;
import net.morphbank.object.UserProperty;

import com.hp.hpl.jena.rdf.model.Resource;

public class RdfLocality extends RdfBaseObject implements RdfMetadata {
	static final long serialVersionUID = 1;
	Locality locality;

	public RdfLocality(Locality locality) {
		super(locality);
		this.locality = locality;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "Locality";
	}
	
	public void addObjectProperties(Resource res){
	//TODO allow difference between adding full object and adding properties
		super.addBasicProperties(res);
		addBasicProperties(res);
	}

	// Resourse creation: meaningful comment goes here
	@Override
	public void addBasicProperties(Resource res) {
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("ContinentOcean"), locality
				.getContinentOcean());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Continent"), locality
				.getContinent());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("WaterBody"), locality
				.getOcean());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Country"), locality
				.getCountry());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("StateProvince"), locality
				.getStateProvince());
		RdfOntologies
				.addProperty(res, RdfOntologies.darwinProperty("CoordinateUncertaintyInMeters"),
						locality.getCoordinatePrecision());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("DecimalLatitude"), locality
				.getLatitude());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("DecimalLongitude"), locality
				.getLongitude());
		UserProperty verbatimLatitude = locality.getUserProperties().get("VerbatimLatitude");
		if (verbatimLatitude != null) {
			RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("VerbatimLatitude"),
					verbatimLatitude.getValue());
		}
		UserProperty verbatimLongitude = locality.getUserProperties().get("VerbatimLongitude");
		if (verbatimLatitude != null) {
			RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("VerbatimLongitude"),
					verbatimLongitude.getValue());
		}
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("Locality"), locality
				.getLocality());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("MaximumElevation"), locality
				.getMaximumElevation());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("MinimumElevation"), locality
				.getMinimumElevation());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("MinimumDepth"), locality
				.getMinimumDepth());
		RdfOntologies.addProperty(res, RdfOntologies.darwinProperty("MaximumDepth"), locality
				.getMaximumDepth());
	}

	public void addReferenceProperties(Resource res) {
	}

	public void addObjects(Resource res, int depth) {
		// add related resources
		// none so far
	}

}
