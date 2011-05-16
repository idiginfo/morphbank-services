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

@Entity
@Table(name = "Annotation")
@DiscriminatorValue("Annotation")
public class Annotation extends BaseObject {
	static final long serialVersionUID = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectId")
	protected BaseObject object;
	protected String objectTypeId; // redundant field, use type of object
	// String phylogeneticStateId;
	// String value;
	protected String typeAnnotation;
	protected String xLocation;
	protected String yLocation;
	protected String areaHeight;
	protected String areaWidth;
	protected String areaRadius;
	protected String annotationQuality;
	protected String title;
	// protected String keywords;
	protected String comment;
	protected String XMLData;
	protected String annotationLabel;

	public Annotation() {
		super();
		setObjectTypeId("Annotation");
	}

	public Annotation(int id, String name, User user, User submitter, Group group) {
		super(id, name, user, submitter, group);
		setObjectTypeId("Annotation");
		setTypeAnnotation("Annotation");
	}

	public Annotation(int id, String name, User user, Group group) {
		super(id, name, user, user, group);
		setObjectTypeId("Annotation");
		setTypeAnnotation("Annotation");
	}

	public static List<Annotation> getAnnotations(BaseObject obj) {
		return getAnnotations(obj.getId());
	}

	public static List<Annotation> getAnnotations(int objectId) {
		List<Annotation> list = new Vector<Annotation>();
		Iterator ids = getAnnotationIds(objectId).iterator();
		while (ids.hasNext()) {
			list.add((Annotation) BaseObject.getEJB3Object(((Integer) ids.next()).intValue()));
		}
		return list;
	}

	public static List getAnnotationIds(int objectId) {
		StringBuffer query = new StringBuffer("select a.id from Annotation a where a.object.id=");
		query.append(objectId);
		Query sessionQuery = null;
		try {
			sessionQuery = MorphbankConfig.getEntityManager().createQuery(query.toString());
			List results = sessionQuery.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}

	}

	public String getAnnotationLabel() {
		return annotationLabel;
	}

	public void setAnnotationLabel(String annotationLabel) {
		this.annotationLabel = annotationLabel;
	}

	public String getAnnotationQuality() {
		return annotationQuality;
	}

	public void setAnnotationQuality(String annotationQuality) {
		this.annotationQuality = annotationQuality;
	}

	public String getAreaHeight() {
		return areaHeight;
	}

	public void setAreaHeight(String areaHeight) {
		this.areaHeight = areaHeight;
	}

	public String getAreaRadius() {
		return areaRadius;
	}

	public void setAreaRadius(String areaRadius) {
		this.areaRadius = areaRadius;
	}

	public String getAreaWidth() {
		return areaWidth;
	}

	public void setAreaWidth(String areaWidth) {
		this.areaWidth = areaWidth;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	// public String getKeywords() {
	// return keywords;
	// }
	//
	// public void setKeywords(String keywords) {
	// this.keywords = keywords;
	// }

	public BaseObject getObject() {
		return object;
	}

	public void setObject(BaseObject object) {
		this.object = object;
	}

	public String getObjectTypeId() {
		return objectTypeId;
	}

	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	// public String getPhylogeneticStateId() {
	// return phylogeneticStateId;
	// }
	// public void setPhylogeneticStateId(String phylogeneticStateId) {
	// this.phylogeneticStateId = phylogeneticStateId;
	// }
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTypeAnnotation() {
		return typeAnnotation;
	}

	public void setTypeAnnotation(String typeAnnotation) {
		this.typeAnnotation = typeAnnotation;
	}

	// public String getValue() {
	// return value;
	// }
	// public void setValue(String value) {
	// this.value = value;
	// }
	public String getXLocation() {
		return xLocation;
	}

	public void setXLocation(String location) {
		xLocation = location;
	}

	public String getXMLData() {
		return XMLData;
	}

	public void setXMLData(String data) {
		XMLData = data;
	}

	public String getYLocation() {
		return yLocation;
	}

	public void setYLocation(String location) {
		yLocation = location;
	}

	public void print(java.io.PrintStream out) {
		out.print(getName());
		out.println("");
		out.println(getComment());
	}
}
