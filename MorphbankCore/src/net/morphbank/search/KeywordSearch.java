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

import java.util.*;

import javax.persistence.Query;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;

public class KeywordSearch extends Search {

	public KeywordSearch(SearchParams params) {
		super(params);
	}

	/**
	 * Create the list of keywords for the "match(keywords) against" clause in
	 * the SQL. A + is prepended to the keyword so that it is required A * is
	 * appended to the keyword so that it matches all words that have the it as
	 * prefix
	 * 
	 * @param keywords
	 * @return
	 */

	@Override
	public int getNumResults() {
		String clause = getNativeWhereClause(params);
		return getNumResults(params.getKeywords(), clause);

	}
	@Override
	public List getResultIds() {
		String clause = getNativeWhereClause(params);
		return getResultIds(params, clause);
	}

	public static String makeRequiredKeywordPrefixes(String keywords) {
		// tokenize keywords add '+' to each word and "IN BOOLEAN MODE"
		String[] words = keywords.split("\\s");
		StringBuffer newKeywords = new StringBuffer();
		for (int i = 0; i < words.length; i++) {
			newKeywords.append('+').append(words[i]).append("* ");
		}
		return newKeywords.toString();
	}

	public static String getKeywordsClause(String keywords) {
		StringBuffer query = new StringBuffer("match (keywords) against ( ?   in boolean mode)");
		// if no keywords are present, create a clause that matches everything
		// This change is in lieu of any explicit search for everything for a
		// user, group, etc.
		if (keywords.length() < 1) query.insert(0, "not ");
		return query.toString();
	}

	public static String getCountKeywordsQuery(String keywords) {
		// StringBuffer query = new StringBuffer("select count(if(");
		// query.append(getKeywordsClause(keywords));
		// query.append(", 1, NULL)) FROM Keywords");
		// return query.toString();
		StringBuffer query = new StringBuffer("select count(*) from Keywords where ");
		query.append(getKeywordsClause(keywords));
		return query.toString();
	}


	public static String getWhereClause(
			// boolean nativeQuery,
			String[] objectTypes, String whereClause, User user, Group group, boolean geolocated,
			boolean isPublic, boolean restrictByUser) {
		String types = getTypesWhereClause(objectTypes);
		StringBuffer clause = new StringBuffer();
		if (types != null && whereClause != null) {
			clause.append(types).append(" and ").append(whereClause);
		} else if (types != null) {
			clause.append(types);
		} else if (whereClause != null) {
			clause.append(whereClause);
		} else clause.append(" true ");
		String security = null;
		// if (nativeQuery) {
		security = getSecurityWhereClause(user, group, isPublic, restrictByUser);
		// } else {
		// security = getSecurityClause(user, group);
		// }
		if (security.length() > 0) {
			if (clause.length() > 0) clause.append(" and ");
			clause.append(security);
		}
		if (geolocated) {
			if (clause.length() > 0) clause.append(" and ");
			clause.append(" geolocated ");
		}
		return clause.toString();
	}

	/**
	 * Create the JPA Query and set parameters so that it is ready for execution
	 * 
	 * @param queryString
	 * @param keywords
	 * @return
	 */
	public Query createKeywordsQuery(String queryString, String keywords) {
		String keys = makeRequiredKeywordPrefixes(keywords);
		System.out.println("Query: " + queryString);
		System.out.println("Keywords: " + keys);
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(queryString.toString());
		query.setParameter(1, keys);
		return query;
	}

	public int getNumResults(String keywords, String whereClause) {
		StringBuffer query = new StringBuffer(getCountKeywordsQuery(keywords));
		if (whereClause != null && whereClause.length()>0) {
			query.append(" and ").append(whereClause);
		}
		try {
			Query sessionQuery = createKeywordsQuery(query.toString(), keywords);
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

	public List getResultIds(SearchParams params, String whereClause) {
		// public List searchByKeyword(String keywords, String whereClause, int
		// limit, int offset) {
		StringBuffer query = new StringBuffer("select id from Keywords where ");
		query.append(getKeywordsClause(params.getKeywords()));
		if (whereClause != null && whereClause.length()>0) {
			query.append(" and ").append(whereClause);
		}
		if (params.getLimit() == -1) {
			// no limit on results
		} else if (params.getLimit() == 0) {
			query.append(" limit 10");
		} else {
			query.append(" limit ").append(params.getLimit());
		}
		if (params.getOffset() > 0) {
			query.append(" offset ").append(params.getOffset());
		}
		Query sessionQuery = null;
		String queryString = query.toString();
		lastQuery = queryString;
		try {
			sessionQuery = createKeywordsQuery(query.toString(), params.getKeywords());
			List results = sessionQuery.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
	}
}
