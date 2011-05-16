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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

/*
 * @author Greg Riccardi
 *
 */
@Entity
@Table(name = "MbCharacter")
@DiscriminatorValue("MbCharacter")
public class MbCharacter extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	private String label;
	private String characterNumber;
	private int discrete;
	private int ordered;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publicationId")
	private Publication publication;
	private String pubComment;

	public MbCharacter() {
		super();
	}

	public MbCharacter(int id, String name, User user, Group group) {
		super(id, name, user, group);
		setLabel(name);
		addUndesignatedState();
	}

	public static final String STATE_ROLE = "state";
	public static final String UNDES_STATE_NAME = "Undesignated state";
	public static final String UNDES_STATE_VALUE = "nullState";
	public static final int UNDES_STATE_INDEX = 0;

	protected CharacterState addUndesignatedState() {
		CharacterState undes = new CharacterState(0, UNDES_STATE_VALUE,
				UNDES_STATE_NAME, getUser(), getGroup());
		CollectionObject collObj = addState(undes, UNDES_STATE_NAME);
		collObj.setObjectOrder(0);
		return undes;
	}

	// MbCharacter methods

	public List<BaseObject> getStates() {
		return getRelatedObjectsByRole(STATE_ROLE);
	}

	public CharacterState getState(String value) {
		// check related objects with role "State"
		// go directly to the states with SQL
		try {
			Query query = MorphbankConfig
					.getEntityManager()
					.createQuery(
							"from CollectionObject where objectRole=? and collectionid = ?");
			query.setParameter(1, STATE_ROLE);
			query.setParameter(2, getId());
			// java.util.List list = ;
			Iterator states = query.getResultList().iterator();
			while (states.hasNext()) {
				CollectionObject object = (CollectionObject) states.next();
				if (object.getObject() instanceof CharacterState) {
					CharacterState state = (CharacterState) object.getObject();
					if (state.getCharStateValue().equals(value)) {
						return state;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	/*
	 * public static long getSerialVersionUID() { return serialVersionUID; }
	 */

	public String getCharacterNumber() {
		return characterNumber;
	}

	public void setCharacterNumber(String characterNumber) {
		this.characterNumber = characterNumber;
	}

	public int getDiscrete() {
		return discrete;
	}

	public void setDiscrete(int discrete) {
		this.discrete = discrete;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		if (label != null && label.length() > 8) {
			this.label = label.substring(0, 7);
		} else {
			this.label = label;
		}
	}

	public int getOrdered() {
		return ordered;
	}

	public void setOrdered(int ordered) {
		this.ordered = ordered;
	}

	public String getPubComment() {
		return pubComment;
	}

	public void setPubComment(String pubComment) {
		this.pubComment = pubComment;
	}

	public Publication getPublication() {
		return publication;
	}

	public int getPublicationId() {
		return publication.getId();
	}

	public void setPublication(Publication publication) {
		this.publication = publication;
	}

	public CollectionObject addState(CharacterState state, String name) {
		CollectionObject newCollObj = new CollectionObject(this, state);
		int lastStateIndex = CollectionObject.getLastIndex(this, STATE_ROLE);
		newCollObj.setObjectRole(STATE_ROLE);
		newCollObj.setObjectTitle(name);
		newCollObj.setObjectOrder(lastStateIndex);		
		this.getObjects().add(newCollObj);
		return newCollObj;
	}

	public List<BaseObject> getAllImages() {
		// get the images (and annotations) from "this" plus from the states
		// images and annotations all have role of an "image"

		List<BaseObject> allImages = null;
		allImages = this.getImages();
		List<BaseObject> states = getCharacterStates();
		for (int i = 0; i < states.size(); i++) {
			CharacterState state = (CharacterState) states.get(i);
			List<BaseObject> stateImages = state.getImages();
			for (int j = 0; j < stateImages.size(); j++) {
				allImages.add(stateImages.get(j));
			}
		}
		return allImages;
	}

	public List<BaseObject> getImages() {
		// get images and annotations from "this"
		return getRelatedObjectsByRole("image");
	}

	public List<BaseObject> getCharacterStates() {
		// get characterStates from "this"
		return getRelatedObjectsByRole("state");
	}

	/*
	 * public CharacterState makeStates(List<BaseObject> images, String
	 * charStateValue) { List<CollectionObject> stateImages = null;
	 * 
	 * for (int i = 0; i < images.size(); i++) { CollectionObject newObj = new
	 * CollectionObject(); newObj.setObject(images.get(i));
	 * newObj.setObjectOrder(i); newObj.setObjectRole("image");
	 * stateImages.add(newObj); } CharacterState newState = new
	 * CharacterState(stateImages, charStateValue); return newState; }
	 */
	public List<CharacterState> makeStates(int[] dividers, String[] values) {
		int bar1 = 0;
		int bar2 = 0;
		List<CollectionObject> stateImages = new Vector<CollectionObject>();
		List<CharacterState> charStates = new Vector<CharacterState>();

		for (int i = 0; i < dividers.length; i++) {
			bar2 = dividers[i];
			CharacterState newState = new CharacterState();
			for (int j = bar1; j < bar2; j++) {
				CollectionObject newObj = new CollectionObject(newState,
						getImages().get(j));
				newObj.setObjectRole("image");
				newObj.setObjectOrder(j);
				stateImages.add(newObj);
			}
			bar1 = bar2;
			newState.getObjects().addAll(stateImages);
			newState.setCharStateValue(values[i]);
			charStates.add(newState);
		}
		return charStates;
	}

	public void print(java.io.PrintStream out) {
		out.print("MbCharacter ");
		// out.print(col);
		out.print(": ");
		out.print(getName());
		out.print(" id: ");
		out.print(getId());
		out.print(" num states ");
		out.println(getStates().size());
		Iterator<BaseObject> states = getStates().iterator();
		while (states.hasNext()) {
			// out.println(states.next());
			states.next().print(out);
		}
	}
}
