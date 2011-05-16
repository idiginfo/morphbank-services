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
package net.morphbank.object;

import java.util.List;
import java.util.Vector;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "Collection")
@DiscriminatorValue("Collection")
public class Collection extends BaseObject {

	static final long serialVersionUID = 1;

	public Collection() {
		super();
	}

	public Collection(int id, String name, User user, Group group) {
		super(id, user, group);
		/*
		 * setName(name); setOwner(user); setOwnerGroup(group);
		 */
	}
	public Collection(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
		/*
		 * setName(name); setOwner(user); setOwnerGroup(group);
		 */
	}

	public void addRelatedObject(BaseObject obj) {
		CollectionObject collobj = new CollectionObject(this, obj);
		getObjects().add(collobj);
		// session.save(collobj);
	}

	public void addRelatedObject(BaseObject obj, int index) {
		CollectionObject collobj = new CollectionObject(this, obj);
		collobj.setObjectOrder(index);
		collobj.setCollection(this);
		// session.save(collobj);
		updateRelatedObjectOrder();
	}

	public void addRelatedObject(BaseObject obj, String role) {
		CollectionObject collobj = new CollectionObject(this, obj);
		collobj.setObjectRole(role);
		if (getObjects() != null) {
			collobj.setObjectOrder(getObjects().size() + 1);
		} else {
			collobj.setObjectOrder(1);
		}
		collobj.setCollection(this);
		// session.save(collobj);
	}

	public void moveRelatedObject(int index, int newIndex) {

		getObjects().add(newIndex, getObjects().get(index));
		if (newIndex < index)
			index++;
		removeRelatedObject(index);
		// session.update(this);
	}

	public void moveObject(int index, int newIndex, Collection newCollection) {

		newCollection.addRelatedObject(getObjects().get(index).getObject(), newIndex);
		removeRelatedObject(index);
		// session.update(this);
		// session.save(newCollection);
	}

	public void moveObject(int index, int newIndex, Collection newCollection,
			String newRole) {

		moveObject(index, newIndex, newCollection);
		newCollection.getObjects().get(newIndex).setObjectRole(newRole);
		// session.update(this);
		// session.save(newCollection);
	}

	public void updateRelatedObjectOrder() {
		for (int i = 0; i < getObjects().size(); i++) {
			getObjects().get(i).setObjectOrder(i);
			// session.update(getObjects().get(i));
		}
	}

	public void removeRelatedObject(int index) {

		getObjects().remove(index);
		updateRelatedObjectOrder();
	}

	public void removeRelatedObject(BaseObject object) {
		int i = 0;
		for (i = 0; i < getObjects().size(); i++) {
			if (getObjects().get(i).getObject().equals(object))
				break;
		}
		if (i < getObjects().size())
			removeRelatedObject(i);
	}

	public List<BaseObject> getRelatedObjectsByRole(String role) {
		List<BaseObject> listByRole = new Vector<BaseObject>();
		for (int i = 0; i < getObjects().size(); i++) {
			if (this.getObjects().get(i).getObjectRole().compareTo(role) == 0)
				listByRole.add(this.getObjects().get(i).getObject());
		}
		return listByRole;
	}

	public List<BaseObject> getRelatedObjectsByType(String objectTypeId) {
		List<BaseObject> listByType = new Vector<BaseObject>();
		for (int i = 0; i < getObjects().size(); i++) {
			if (this.getObjects().get(i).getObjectTypeId().compareTo(
					objectTypeId) == 0)
				listByType.add(this.getObjects().get(i).getObject());
		}
		return listByType;
	}
}
