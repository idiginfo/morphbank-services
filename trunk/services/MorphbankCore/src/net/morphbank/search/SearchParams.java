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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import net.morphbank.object.Group;
import net.morphbank.object.User;
import net.morphbank.object.UserGroupKey;

public class SearchParams {
	// xml search parameters
	protected String keywords = "";
	protected int limit = 10;
	protected int offset = 0;
	// int depth = 2;
	protected int id;
	protected String[] objectTypes;
	protected String taxonName;
	protected int firstResult = 0;
	protected String keyString;
	protected String userId;
	protected String groupId;
	protected String password;
	protected Date changeDate;
	protected Date lastChangeDate;
	protected int numChangeDays = 0;
	// TODO fix geolocated
	protected boolean geolocated = false;
	protected boolean isPublic = true;
	protected boolean isExact = true;
	protected String hostServer; // host restriction for search

	// not really a parameter
	protected List<Integer> ids = new Vector<Integer>();
	// User and Group info for request
	protected User user = null;
	protected Group group = null;

	public void setUserGroup() {
		user = User.getUserByUserId(userId);
		group = Group.getGroupByGroupId(groupId);
		if (keyString != null && keyString.length() > 0) {
			UserGroupKey ugk = UserGroupKey.getUserGroupKey(keyString);
			if (ugk != null) {
				user = ugk.getUser();
				group = ugk.getGroup();
			}
		}
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		// make sure that keywords is never null
		if (keywords == null) {
			this.keywords = "";
		} else {
			this.keywords = keywords;
		}
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public int getId() {
		return id;
	}

	protected static String pattern = "\\s+|,\\s+|,";

	/**
	 * Add ids from array of strings. Each string may have multiple ids with
	 * white space or comma separators
	 * 
	 * @param idStrs
	 *            array of strings containing id values
	 */
	public void addIds(String[] idStrs) {
		if (idStrs == null) return;
		for (int i = 0; i < idStrs.length; i++) {
			String[] idSs = idStrs[i].split(pattern);
			for (int j = 0; j < idSs.length; j++) {
				try {
					ids.add(Integer.parseInt(idSs[j]));
				} catch (Exception e) {
					// bad number in id list
				}
			}
		}
	}

	public void addId(Integer id) {
		if (id != null) {
			this.id = id;
			ids.add(id);
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public String[] getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(String[] objectTypes) {
		this.objectTypes = objectTypes;
	}

	public String getTaxonName() {
		return taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public String getKeyString() {
		return keyString;
	}

	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
		setUserGroup();
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
		setUserGroup();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Date getLastChangeDate() {
		return lastChangeDate;
	}

	public void setLastChangeDate(Date lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}

	public int getNumChangeDays() {
		return numChangeDays;
	}

	public void setNumChangeDays(int numChangeDays) {
		this.numChangeDays = numChangeDays;
	}

	public boolean isGeolocated() {
		return geolocated;
	}

	public void setGeolocated(boolean geolocated) {
		this.geolocated = geolocated;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getHostServer() {
		return hostServer;
	}

	public void setHostServer(String hostServer) {
		this.hostServer = hostServer;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean isExact() {
		return isExact;
	}

	public void setExact(boolean isExact) {
		this.isExact = isExact;
	}

}
