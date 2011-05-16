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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CharacterState")
@DiscriminatorValue("CharacterState")
public class CharacterState extends BaseObject {
	static final long serialVersionUID = 1;

	public CharacterState() {
		super();
	}

	public static final int MAX_VALUE_LENGTH = 32;

	private String charStateValue;

	public CharacterState(int id, String value, String name, User user, Group group) {
		super(id, name, user, group);
		setCharStateValue(value);
	}

	/*
	 * public CharacterState(List<CollectionObject> colObj, String
	 * charStateValue) { this(); setObjectTypeId("PhyloCharState");
	 * setObjects(colObj); setCharStateValue(charStateValue); }
	 */

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCharStateValue() {
		return charStateValue;
	}

	public void setCharStateValue(String charStateValue) {
		if (charStateValue == null
				| charStateValue.length() <= MAX_VALUE_LENGTH) {
			this.charStateValue = charStateValue;
		} else {
			this.charStateValue = charStateValue.substring(MAX_VALUE_LENGTH);
		}
	}

	/**
	 * return all images and annotation for the state
	 */
	public List<BaseObject> getImages() {
		// get images and annotations from this
		return getRelatedObjectsByRole("image");
	}

	public void print(java.io.PrintStream out) {
		out.print("MbCharacter State: ");
		out.print(getCharStateValue());
		out.print(" ");
		out.println(getName());
	}
}
