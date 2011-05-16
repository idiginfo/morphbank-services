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

public class ChangeSearch extends Search {

	// String lastQuery;
	static final String FIRST_CHANGE = " b.dateLastModified >= ? ";
	static final String LAST_CHANGE = " and b.dateLastModified < ? ";
	static final String NUMBER_OF_CHANGES = "select count(b.id) from BaseObject b where ";
	// static final String CHANGE_IDS =
	// "select id from BaseObject where id in (select b.id from BaseObject b where ";
	static final String CHANGE_IDS = "select b.id from BaseObject b where ";
	static final String ORDER_BY = " order by id ";
	// static final String ORDER_BY = "";
	static final String CHANGE_ID1 = "select top ";
	static final String CHANGE_ID2 = " id "
			+ "from (select rownumber() OVER (ORDER BY datelastmodified desc) AS RowNumber, id from"
			+ "BaseObject b where ";
	static final String WHERE_START = ") where RowNumber > $ ";

	public ChangeSearch(SearchParams params){
		super(params);
	}
	
	@Override
	public int getNumResults() {
		boolean restrictByUser = false;
		String clause = Search.getWhereClause(params, null, restrictByUser);
		Query query = getChangeQuery(NUMBER_OF_CHANGES, params.getChangeDate(), params
				.getLastChangeDate(), clause);
		Object resultCount = query.getSingleResult();
		int numChanges = MorphbankConfig.getIntFromQuery(resultCount);
		return numChanges;
	}

	@Override
	public List<?> getResultIds() {
		String clause = Search.getWhereClause(params, null, false);
		Query query = getChangeQuery(CHANGE_IDS, params.getChangeDate(),
				params.getLastChangeDate(), clause, params.getLimit(), params.getOffset());
		List results = query.getResultList();
		return results;
	}

	public Query getChangeQuery(String baseQuery, Date firstChangeDate, Date lastChangeDate,
			String whereClause) {
		StringBuffer queryStr = new StringBuffer(baseQuery);
		queryStr.append(FIRST_CHANGE);
		if (lastChangeDate != null) queryStr.append(LAST_CHANGE);
		if (whereClause != null && whereClause.trim().length() > 0) {
			queryStr.append(" and ").append(whereClause);
		}
		String message = "Change query is: " + queryStr.toString() + "  from " + firstChangeDate;
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(queryStr.toString());
		query.setParameter(1, firstChangeDate);
		if (lastChangeDate != null) {
			query.setParameter(2, lastChangeDate);
			message = message + " to " + lastChangeDate;
		}
		MorphbankConfig.SYSTEM_LOGGER.info(message);
		return query;
	}

	public Query getChangeQuery(String baseQuery, Date firstChangeDate, Date lastChangeDate,
			String whereClause, int limit, int offset) {
		StringBuffer queryBuffer = new StringBuffer(baseQuery);
		queryBuffer.append(FIRST_CHANGE);
		if (lastChangeDate != null) queryBuffer.append(LAST_CHANGE);
		if (whereClause != null && whereClause.trim().length() > 0) {
			queryBuffer.append(" and ").append(whereClause);
		}
		queryBuffer.append(ORDER_BY);
		Search.addNativeLimitOffset(queryBuffer, limit, offset);
		String queryStr = queryBuffer.toString();
		System.out.println("Query is: " + queryStr);
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(queryStr);
		query.setParameter(1, firstChangeDate);
		if (lastChangeDate != null) {
			query.setParameter(2, lastChangeDate);
		}
		return query;
	}

	public Query getTopChangeQuery(Date firstChangeDate, Date lastChangeDate, int limit,
			int offset, String whereClause) {
		if (limit < 1 && offset < 1) {
			return getChangeQuery("", firstChangeDate, lastChangeDate, whereClause);
		}
		StringBuffer queryStr = new StringBuffer(CHANGE_ID1);
		queryStr.append(limit).append(limit);
		queryStr.append(limit).append(CHANGE_ID2);

		queryStr.append(FIRST_CHANGE);
		if (lastChangeDate != null) queryStr.append(LAST_CHANGE);
		queryStr.append(WHERE_START);
		if (whereClause != null && whereClause.trim().length() > 0) {
			queryStr.append(" and ").append(whereClause);
		}
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(queryStr.toString());
		query.setParameter(1, limit);
		query.setParameter(2, firstChangeDate);
		if (lastChangeDate != null) {
			query.setParameter(3, lastChangeDate);
			query.setParameter(4, limit);
		} else {
			query.setParameter(3, limit);
		}
		return query;
	}

}
