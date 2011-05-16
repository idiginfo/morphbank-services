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

public class EolSearch {

	static final String fromWhere = " from BaseObject b join Image i on b.id=i.id"
			+ " where b.datetopublish<now() and i.eol>0 ";

	public int getNumEolIds() {
		String eolNumQuery = "select count(b.id) " + fromWhere;
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(eolNumQuery);
		Object resultCount = query.getSingleResult();
		int numEolImages = MorphbankConfig.getIntFromQuery(resultCount);
		return numEolImages;
	}

	public List getEolIds(int limit, int offset) {
		String eolQuery = "select b.id " + fromWhere +" order by b.id " + getLimitOffset(limit, offset);
		Query query = MorphbankConfig.getEntityManager().createNativeQuery(eolQuery);
		List results = query.getResultList();
		return results;
	}

	public String getLimitOffset(int limit, int offset) {
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
