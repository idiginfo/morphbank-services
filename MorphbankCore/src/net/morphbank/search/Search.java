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

public abstract class Search {
	SearchParams params = null;
	String fromClause;
	String whereClause;
	String lastQuery;
	boolean restrictByUser = true;
	String limitOffsetClause;

	// public Search() {
	//
	// }

	public Search(SearchParams params) {
		this.params = params;
		fromClause = getFromClause(params);
		whereClause = getWhereClause(params, "", restrictByUser);
		limitOffsetClause = "";
	}

	protected String getFromClause(SearchParams params2) {
		StringBuffer fromClause = new StringBuffer();
		boolean hasKeywords = false;
		if (!isEmpty(params.getKeywords()) || params.getId()>0
				|| params.getIds().size()>0 ) {
			fromClause.append(" Keywords k ");
			hasKeywords = true;
		}
		//if (!isEmpty(params.get))
		if (params.getChangeDate()!=null){
			
		}

		return fromClause.toString();
	}
	
	private boolean isEmpty(String string){
		if (string == null) return true;
		if (string.length()==0) return true;
		return false;
	}

	public String getLastQuery() {
		return lastQuery;
	}

	// TODO add security to searching
	public static final String PUBLIC_CLAUSE = " (dateToPublish <= now()) ";

	/**
	 * Create security clause for OQL query Clause specifies that either the
	 * object is public or owned by group or user
	 * 
	 * @param user
	 * @param group
	 * @param restrictByUser
	 *            TODO
	 * @return
	 */
	public static String getNativePublicSecurityClause(User user, Group group,
			boolean restrictByUser) {
		// start with public objects
		StringBuffer secClause = new StringBuffer("(");
		secClause.append(PUBLIC_CLAUSE);
		// removed security for by user/group, in favor of restrict by
		// user/group
		String usergroupClause = getSecurityWhereClause(user, group, true, false);
		String connector = " or ";
		if (restrictByUser) connector = " and ";
		if (usergroupClause.length() > 0) {
			secClause.append(connector).append(usergroupClause);
		}
		secClause.append(')');
		return secClause.toString();
	}

	/**
	 * Create security clause for native query
	 * 
	 * @param user
	 * @param group
	 * @param isPublic
	 *            TODO
	 * @param restrictByUser
	 *            TODO
	 * @return
	 */
	public static String getSecurityWhereClause(User user, Group group, boolean isPublic,
			boolean restrictByUserGroup) {
		StringBuffer secClause = new StringBuffer("(");
		String connector = "";
		if (isPublic) {
			secClause.append(PUBLIC_CLAUSE);
			if (restrictByUserGroup)
				connector = " and ";
			else
				connector = "or";
		}
		if (user != null || group != null) {
			secClause.append(connector).append(" (");
			connector = "";
			if (group != null) {
				secClause.append("groupid = ").append(group.getId());
				connector = " or ";
			}
			if (user != null) {
				secClause.append(connector).append("userid = ").append(user.getId());
				connector = " or ";
			}
			secClause.append(")");
		}
		secClause.append(")");
		if (secClause.length() > 2) return secClause.toString();
		return "";
	}

	public static String getSecurityClause(User user, Group group) {
		StringBuffer secClause = new StringBuffer();
		String connector = "";
		if (group != null) {
			secClause.append(connector).append("b.group.id = ").append(group.getId());
			connector = " or ";
		}
		if (user != null) {
			secClause.append(connector).append("b.group.id = ").append(user.getId());
			connector = " or ";
		}
		return secClause.toString();
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
	public static String makeRequiredKeywordPrefixes(String keywords) {
		// tokenize keywords add '+' to each word and "IN BOOLEAN MODE"
		String[] words = keywords.split("\\s");
		StringBuffer newKeywords = new StringBuffer();
		for (int i = 0; i < words.length; i++) {
			newKeywords.append('+').append(words[i]).append("* ");
		}
		return newKeywords.toString();
	}

	public static String getNativeTypesWhereClause(String[] objectTypes) {
		if (objectTypes == null || objectTypes.length == 0) {
			return null;
		}
		// use nested query to avoid filesort
		StringBuffer types = new StringBuffer("id in (select id from Keywords where ");

		boolean firstTime = true;
		for (int i = 0; i < objectTypes.length; i++) {
			if (!firstTime) {
				types.append(" or ");
			} else {
				firstTime = false;
				types.append(" (");
			}
			types.append("objectTypeId='");
			types.append(objectTypes[i]);
			types.append("'");
		}
		types.append(")) ");
		return types.toString();
	}

	// This does not work in Toplink at present.
	public static String getTypesWhereClause(String[] objectTypes) {
		if (objectTypes == null || objectTypes.length == 0) {
			return null;
		}
		StringBuffer types = new StringBuffer();
		boolean firstTime = true;
		for (int i = 0; i < objectTypes.length; i++) {
			if (!firstTime) {
				types.append(" or ");
			} else {
				firstTime = false;
				types.append(" (");
			}
			types.append("b.objectTypeId='");
			types.append(objectTypes[i]);
			types.append("'");
		}
		types.append(") ");
		// BaseObject b= null;b.getObjectTypeId() ;
		return types.toString();
	}

	public static void addLimitOffset(Query query, int limit, int offset) {
		if (limit == -1) {
			// no limit on results
		} else if (limit == 0) {
			query.setMaxResults(10);
		} else {
			query.setMaxResults(limit);
		}
		if (offset > 0) {
			query.setFirstResult(offset);
		}
	}

	public static void addNativeLimitOffset(StringBuffer queryString, int limit, int offset) {
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
	}

	public static String getWhereClause(SearchParams params, String whereClause,
			boolean restrictByUser) {
		String types = getTypesWhereClause(params.getObjectTypes());
		StringBuffer clause = new StringBuffer();
		if (types != null && whereClause != null) {
			clause.append(types).append(" and ").append(whereClause);
		} else if (types != null) {
			clause.append(types);
		} else if (whereClause != null) {
			clause.append(whereClause);
		}
		String security = null;
		// if (nativeQuery) {
		security = getSecurityWhereClause(params.getUser(), params.getGroup(), params.isPublic(),
				restrictByUser);
		// } else {
		// security = getSecurityClause(user, group);
		// }
		if (security.length() > 0) {
			if (clause.length() > 0) clause.append(" and ");
			clause.append(security);
		}
		if (params.isGeolocated()) {
			if (clause.length() > 0) clause.append(" and ");
			clause.append(" geolocated ");
		}
		String hostServer = params.getHostServer();
		if (hostServer != null && hostServer.length() > 0) {
			if (clause.length() > 0) clause.append(" and ");
			clause.append(" hostServer='").append(hostServer).append("' ");
		}
		// external id
		// external link
		// eol
		return clause.toString();
	}

	public abstract int getNumResults();

	public abstract List<?> getResultIds();

	public String getNativeWhereClause(SearchParams params) {
		return getNativeWhereClause(params.getObjectTypes(), "", params.getUser(), params
				.getGroup(), params.isGeolocated(), params.isPublic());
	}

	public String getNativeWhereClause(String[] objectTypes, String whereClause, User user,
			Group group, boolean geolocated, boolean isPublic) {
		String types = getNativeTypesWhereClause(objectTypes);
		StringBuffer clause = new StringBuffer();
		if (types != null && whereClause != null && whereClause.length() > 0) {
			clause.append(types).append(" and ").append(whereClause);
		} else if (types != null) {
			clause.append(types);
		} else if (whereClause != null && whereClause.length() > 0) {
			clause.append(whereClause);
		}
		String security = null;
		security = getSecurityWhereClause(user, group, isPublic, false);

		if (clause.length() > 0 && security.length() > 0) {
			clause.append(" and ").append(security);
		} else {
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

}
