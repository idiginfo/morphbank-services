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

import javax.persistence.Query;

import net.morphbank.MorphbankConfig;

public class ExtRefSearch {

	static final String likeQuery = "from ExternalLinkObject e " + " where e.urlData like :phrase ";
	static final String exactQuery = "from ExternalLinkObject e " + " where e.urlData = :phrase ";

	SearchParams params;
	String fromWhereClause;
	String phrase;
	boolean exact;
	int limit;
	int offset;
	
	public ExtRefSearch(SearchParams params) {
		this.params = params;
		limit = params.getLimit();
		offset = params.getOffset();
		phrase = params.getKeywords();
		
	}

	public int getNumResults() {
		String countSql = "select count(*) " + fromWhereClause;
		Query query = MorphbankConfig.getEntityManager().createQuery(countSql);
		query.setParameter("phrase", phrase);
		Object resultCount = query.getSingleResult();
		int numEolImages = MorphbankConfig.getIntFromQuery(resultCount);
		return numEolImages;
	}

	@SuppressWarnings("unchecked")
	public List getResultIds() {
		String eolQuery = "select e.mbId " + fromWhereClause + " order by e.mbId "
				+ getLimitOffset();
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(eolQuery);
		List results = query.getResultList();
		return results;
	}

	public String getLimitOffset() {
		StringBuffer queryString = new StringBuffer();
		if (limit == -1) {
			// no limit on results
		} else if (limit == 0) {
			queryString.append(" limit 10 offset ").append(offset);
		} else {
			queryString.append(" limit ").append(limit);
		}
		if (offset > 0) {
			queryString.append(" offset ").append(offset);
		}
		return queryString.toString();
	}

}
