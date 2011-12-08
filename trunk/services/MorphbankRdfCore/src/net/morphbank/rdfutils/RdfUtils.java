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
package net.morphbank.rdfutils;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Specimen;
import net.morphbank.rdf.RdfBase;
import net.morphbank.rdf.RdfOntologies;
import net.morphbank.search.KeywordSearch;
import net.morphbank.search.Search;
import net.morphbank.search.SearchParams;

public class RdfUtils {

	SearchParams params;
	KeywordSearch search;
	
	public RdfUtils(SearchParams params) {
		this.params = params;
		 search = new KeywordSearch(params);
	}

	public Search getSearch() {
		return search;
	}

	public void printNumResults(PrintWriter out, int numResults,
			int numResultsReturned, int firstResult) {
		out.println("\t<numResults>" + numResults + "</numResults>");
		out.println("\t<numResultsReturned>" + numResultsReturned
				+ "</numResultsReturned>");
		out.println("\t<firstResult>" + firstResult + "</firstResult>");
	}

	public void getRdfObjects(String keywords, int limit, int firstResult,
			int depth, PrintWriter out) {
		SearchParams params = new SearchParams();
		params.setKeywords(keywords);
		params.setLimit(limit);
		params.setFirstResult(firstResult);
		int numResults = search.getNumResults();
		List objectIds = search.getResultIds();
		printNumResults(out, numResults, objectIds.size(), firstResult);
		Iterator iter = objectIds.iterator();
		while (iter.hasNext()) {
			int id = MorphbankConfig.getIntFromQuery(iter.next());
			getRdfObjectById(id, depth);
		}
		RdfOntologies.getOntModel().write(out, "RDF/XML");
	}

	public int getSpecimenId(String imageId) {
		int objectId = Integer.parseInt(imageId);
		Specimen specimen = Specimen.getSpecimen(objectId);
		return specimen.getId();
	}

	public void getRdfObjectById(int id, int depth) {
		BaseObject obj = BaseObject.getEJB3Object(id);
		getRdfObjectById(obj, depth);
	}

	public RdfBase getRdfObjectById(BaseObject obj, int depth) {
		RdfBase rdfObj = RdfBase.createRdfObject(obj);
		if (rdfObj != null) {
			rdfObj.toDetailRDF(depth);
		}
		return rdfObj;
	}

	public void getRdfObjects(int numResults, int firstResult, List objectIds,
			int depth, PrintWriter out) {
		//printNumResults(out, numResults, objectIds.size(), firstResult);
        // start with a new rdf model
        RdfOntologies.initializeOntModel(); 
		Iterator iter = objectIds.iterator();
		// int i = 0;
		while (iter.hasNext()) {
			Object obj = iter.next();
			int id;
			if (obj instanceof BaseObject) {
				id = ((BaseObject) obj).getId();
			} else {
				id = MorphbankConfig.getIntFromQuery(obj);
			}
			getRdfObjectById(id, depth);
		}
		RdfOntologies.getOntModel().write(out, "RDF/XML");
	}
}
