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
import net.morphbank.object.News;

import com.hp.hpl.jena.rdf.model.Resource;

public class RdfNews extends RdfBaseObject implements RdfMetadata {
	static final long serialVersionUID = 1;
	News news;

	public RdfNews(News news) {
		super(news);
		this.news = news;
	}

	public String getRDFDefinitionURI() {
		return MorphbankConfig.MORPHBANK_SCHEMA_URI + "Locality";
	}

	// Resourse creation: meaningful comment goes here
	@Override
	public void addBasicProperties(Resource res) {
		// super.addBasicProperties(res);
		RdfOntologies.addProperty(res, RdfOntologies
				.mbankProperty("dateCreated"), base.getDateCreated());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("title"),
				news.getTitle());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("body"),
				news.getBody());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("image"),
				news.getImage());
		RdfOntologies.addProperty(res,
				RdfOntologies.mbankProperty("imageText"), news.getImageText());
		RdfOntologies.addProperty(res, RdfOntologies.mbankProperty("status"),
				news.getStatus());
	}

	public void addReferenceProperties(Resource res) {
	}

	public void addObjects(Resource res, int depth) {
		// add related resources
		// none so far
	}

}
