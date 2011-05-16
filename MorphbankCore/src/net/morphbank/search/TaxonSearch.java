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
package net.morphbank.search;

import java.util.List;
import java.util.Vector;

import javax.persistence.Query;

import net.morphbank.MorphbankConfig;

public class TaxonSearch {

	// TODO add restriction to public images
	static final String TAXON_PARAM = "taxonName";
	static final String TAXON_CLAUSE_BASE = "taxonomicNames like :"
			+ TAXON_PARAM;
	static final String COUNT_IMAGE = "select count(i) from Image i where i.specimen."
			+ TAXON_CLAUSE_BASE;
	static final String SELECT_IMAGE = "select i from Image i where i.specimen."
			+ TAXON_CLAUSE_BASE;
	static final String COUNT_SPECIMEN = "select count(s) from Specimen s where s."
			+ TAXON_CLAUSE_BASE;
	static final String SELECT_SPECIMEN = "select s from Specimen s where s."
			+ TAXON_CLAUSE_BASE;

	public String getTaxonClause() {
		return TAXON_CLAUSE_BASE;
	}

	private String like(String name) {
		return "%" + name + "%";
	}

	public Query createTaxonQuery(String queryString, String taxonName) {
		Query query = MorphbankConfig.getEntityManager().createQuery(
				queryString);
		query.setParameter(TAXON_PARAM, like(taxonName));
		return query;
	}

	public int getNumImages(String taxonName) {
		if (taxonName== null || taxonName.equals("")) return 0;
		Query sessionQuery = createTaxonQuery(COUNT_IMAGE, taxonName);
		Object countObj = sessionQuery.getSingleResult();
		return Integer.parseInt(countObj.toString());
	}

	public List getImages(String taxonName, int limit, int firstResult) {
		if (taxonName== null || taxonName.equals("")) return new Vector();
		Query sessionQuery = createTaxonQuery(SELECT_IMAGE, taxonName);
		if (limit == 0) limit = 10;
		if (limit > 0) sessionQuery.setMaxResults(limit);
		if (firstResult > 0) sessionQuery.setFirstResult(firstResult);
		return sessionQuery.getResultList();
	}

	public int getNumSpecimens(String taxonName) {
		if (taxonName== null || taxonName.equals("")) return 0;
		Query sessionQuery = createTaxonQuery(COUNT_SPECIMEN, taxonName);
		Object countObj = sessionQuery.getSingleResult();
		return Integer.parseInt(countObj.toString());
	}

	public List getSpecimens(String taxonName, int limit, int firstResult) {
		if (taxonName== null || taxonName.equals("")) return new Vector();
		Query sessionQuery = createTaxonQuery(SELECT_SPECIMEN, taxonName);
		if (limit > 0) sessionQuery.setMaxResults(limit);
		if (firstResult > 0) sessionQuery.setFirstResult(firstResult);
		return sessionQuery.getResultList();
	}
}
