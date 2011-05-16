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

import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import net.morphbank.MorphbankConfig;

public class NativeTaxonSearch extends Search {

	public NativeTaxonSearch(SearchParams params) {
		super(params);
	}

	public String getTaxonClause() {
		// StringBuffer query = new StringBuffer("match (keywords) against ('");
		// query.append(makeRequiredKeywords(taxonName));
		// query.append("' in boolean mode)");
		StringBuffer query = new StringBuffer("taxonomicNames like $");
		return query.toString();
	}

	public String getCountTaxonQuery() {
		// StringBuffer query = new StringBuffer("select count(if(");
		StringBuffer query = new StringBuffer("select count(id) from Specimen where ");
		query.append(getTaxonClause());
		// query.append(", 1, NULL)) FROM Keywords");
		return query.toString();
	}

	public Query createTaxonQuery(String queryString, String taxonName) {
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(queryString.toString());
		query.setParameter(1, taxonName);
		return query;
	}

	public List searchByTaxon(String taxonName, String[] objectTypes, String whereClause,
			int limit, int firstResult) {
		String where = null; // search.getTypesWhereClause(objectTypes);
		if (where == null) {
			where = whereClause;
		} else if (whereClause != null) {
			where = where + " and " + whereClause;
		} else {
			// where is already set properly
		}
		return searchByTaxon(taxonName, where, limit, firstResult);
	}

	public List searchByTaxon(String taxonName, String whereClause) {
		return searchByTaxon(taxonName, whereClause, -1, 0);
	}

	public List searchByNativeQuery(String queryString, String taxonName, int limit, int offset) {
		Query sessionQuery = null;
		StringBuffer queryBuffer = new StringBuffer(queryString);
		Search.addNativeLimitOffset(queryBuffer, limit, offset);
		queryString = queryBuffer.toString();
		lastQuery = queryString;
		try {
			sessionQuery = createTaxonQuery(queryString, taxonName);
			List results = sessionQuery.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
	}

	public List searchByTaxon(String taxonName, String whereClause, int limit, int firstResult) {
		StringBuffer query = new StringBuffer("select id from Specimen where ");
		query.append(getTaxonClause());
		if (whereClause != null) {
			query.append(" and ").append(whereClause);
		}
		return searchByNativeQuery(query.toString(), taxonName, limit, firstResult);
	}

	public int getNumTaxonResults(String taxonName, String[] objectTypes, String whereClause) {
		String types = null; // search.getTypesWhereClause(objectTypes);
		String where = null;
		if (types != null & whereClause != null) {
			where = types + " and " + whereClause;
		} else if (types != null) {
			where = types;
		} else {
			where = whereClause;
		}
		return getNumTaxonResults(taxonName, where);
	}

	public int getNumTaxonResults(String taxonName, String whereClause) {
		StringBuffer query = new StringBuffer(getCountTaxonQuery());
		if (whereClause != null) {
			query.append(" and ").append(whereClause);
		}
		try {
			// TODO check parameter substitution
			Query sessionQuery = createTaxonQuery(query.toString(), taxonName);
			lastQuery = query.toString();
			Iterator iter = sessionQuery.getResultList().iterator();
			return MorphbankConfig.getIntFromQuery(iter.next());
		} catch (Exception e) {
			System.err.println("exception");
			System.err.println("Query " + query.toString());
			e.printStackTrace(System.err);
			return 0;
		}
	}

	@Override
	public int getNumResults() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<?> getResultIds() {
		// TODO Auto-generated method stub
		return null;
	}

}
