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
package net.morphbank.mbsvc3.mapping;

import java.security.CryptoPrimitive;

import net.morphbank.object.*;
import net.morphbank.mbsvc3.xml.*;

public class MBCredentials {
	User user = null;
	Group group = null;
	String password;

	public MBCredentials(ObjectList objectlist) {
		this(objectlist.getSubmitter());
	}

	private User getUser(Credentials credentials) {
		// try userid
		Object obj;
		obj = BaseObject.getEJB3Object(credentials.getUserId());
		if(null != obj)
		{
			if (obj instanceof User) {
				return (User) obj;
			}
		}
		// try uin
		User user = User.getUserByUIN(credentials.getUin());
		if (user != null)
			return user;
		// try name
		user = User.getUserByFirstLast(credentials.getUserName());
		// TODO eliminate default user and group
		if (user != null)
			return user;
		// all else failed, use default user (1 is Fredrik Ronquist)
		return (User) BaseObject.getEJB3Object(1);
	}

	private Group getGroup(Credentials credentials){
		// try groupId
		Object obj;
		obj = BaseObject.getEJB3Object(credentials.getGroupId());
		if (obj instanceof Group) {
			return (Group) obj;
		} 
		// try group name
		Group group = Group.getGroupByName(credentials.getGroupName());
		if(group != null) return group;
		// all else failed, use default group
		return (Group) BaseObject.getEJB3Object(2);
	}
	
	public MBCredentials(Credentials credentials) {
		if (credentials != null) {
			// look for user
			// try keystring first
			if (credentials.getKeyString() != null) {
				// get user and group for keyString
				UserGroupKey ugk = UserGroupKey.getUserGroupKey(credentials
						.getKeyString());
				if (ugk != null) {
					user = ugk.getUser();
					group = ugk.getGroup();
				}
			} else {
				user = getUser(credentials);
			}

			group = getGroup(credentials);
			password = credentials.getPassword();
			// TODO validate password!
		}
	}

	public MBCredentials(BaseObject object) {
		user = object.getUser();
		group = object.getGroup();
		password = null;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
