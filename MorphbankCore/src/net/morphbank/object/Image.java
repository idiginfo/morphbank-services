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
import java.util.Date;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.morphbank.MorphbankConfig;

@Entity
@Table(name = "Image")
@DiscriminatorValue("Image")
public class Image extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	// database fields
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User imageUser;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupId")
	private Group imageGroup;
	@Temporal(TemporalType.DATE)
	@Column(name = "dateToPublish")
	private Date imageDateToPublish;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specimenId")
	private Specimen specimen;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "viewId")
	private View view;
	private Integer imageHeight;
	private Integer imageWidth;
	private Integer resolution;
	private Double magnification;
	private String imageType;
	private String copyrightText;
	private String originalFileName;
	private String creativeCommons;
	private String photographer;
	private Integer eol;

	// constructors
	public Image() {
		super();
		// setObjectTypeId("Image");
	}

	public static final String CC_STD = "<a href=\"http://creativecommons.org/licenses/by-nc-sa/3.0/us/\" "
			+ "rel=\"license\"><img src=\"http://i.creativecommons.org/l/by-nc-sa/3.0/us/88x31.png\" "
			+ "style=\"border-width: 0pt;\" alt=\"Creative Commons License\"/></a>";

	public Image(int id, String name, String userId, String groupId) {
		super(id, name, userId, userId, groupId);
	}

	public Image(int id, String name, User user, Group group) {
		super(id, name, user, user, group);
		init();
	}

	public Image(int id, String name, String ownerId, String submitterId,
			String groupId) {
		super(id, name, ownerId, submitterId, groupId);
	}

	public Image(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
		init();
	}

	public void init() {
		setImageUser(super.getUser());
		setSubmittedBy(super.getSubmittedBy());
		setImageGroup(super.getGroup());
		// setSpecimen(specimen);
		// setView(view);
		setImageType("jpg");
		setCopyrightText("unknown");
		// setResolution(100);
		setImageDateToPublish(getDateToPublish());
		setCreativeCommons(CC_STD);
	}

	public Image(
			// required parameters
			int id, User user, User submittedBy, Group group,
			Specimen specimen, View view, String imageType,
			String copyrightText, int resolution) {
		this(id, "", user, group); // sets all required fields to something
		// complete the initializations
		setSubmittedBy(submittedBy);
		setSpecimen(specimen);
		setView(view);
		setImageType(imageType);
		setCopyrightText(copyrightText);
		setResolution(resolution);
	}

	/**
	 * Perform operations necessary for persistance
	 */
	public boolean persist() {
		boolean superPersist = super.persist();
		return superPersist;
	}

	public boolean validate() {
		if (!super.validate()) { return false; }
		// if (view == null) {
		// int viewNum = 136053;
		// view = (View) BaseObject.getEJB3Object(viewNum);
		// }
		if (imageType == null || imageUser == null || imageGroup == null) {
			return false;
		} else if (copyrightText == null
				|| (resolution != null && resolution < 0)
				|| imageDateToPublish == null || creativeCommons == null) { return false; }
		if (imageType.length() > 8) {
			imageType = "jpg";
		}
		return true;
	}

	/**
	 * Get an image from the database by its id If the id is for a specimen, get
	 * the standard image of the specimen
	 * 
	 * @param objectId
	 * @return
	 */
	public static Image getImage(int objectId) {
		BaseObject obj = BaseObject.getEJB3Object(objectId);
		if (obj instanceof Image) {
			return (Image) obj;
		} else if (obj instanceof Specimen) {
			return (Image) ((Specimen) obj).getStandardImage();
		} else { // imageId does not represent image or specimen
			return null;
		}
	}

	public String getCopyrightText() {
		return copyrightText;
	}

	public void setCopyrightText(String copyrightText) {
		this.copyrightText = copyrightText;
	}

	public String getCreativeCommons() {
		return creativeCommons;
	}

	public void setCreativeCommons(String creativeCommons) {
		this.creativeCommons = creativeCommons;
	}

	public Date getImageDateToPublish() {
		return imageDateToPublish;
	}

	public void setImageDateToPublish(Date imageDateToPublish) {
		this.imageDateToPublish = imageDateToPublish;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupId")
	public Group getImageGroup() {
		return imageGroup;
	}

	public void setImageGroup(Group imageGroup) {
		this.imageGroup = imageGroup;
		setGroup(imageGroup);
	}

	public Integer getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(Integer imageHeight) {
		this.imageHeight = imageHeight;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public User getImageUser() {
		return imageUser;
	}

	public void setImageUser(User imageUser) {
		this.imageUser = imageUser;
		setUser(imageUser);
	}

	public Integer getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(Integer imageWidth) {
		this.imageWidth = imageWidth;
	}

	public Double getMagnification() {
		return magnification;
	}

	public void setMagnification(Double magnification) {
		this.magnification = magnification;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public Integer getResolution() {
		return resolution;
	}

	public void setResolution(Integer resolution) {
		this.resolution = resolution;
	}

	public Specimen getSpecimen() {
		return specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public String getPhotographer() {
		return photographer;
	}

	public void setPhotographer(String photographer) {
		this.photographer = photographer;
	}

	public Integer getEol() {
		return eol;
	}

	public void setEol(String eol) {
		if (eol.equalsIgnoreCase("yes")) {
			this.eol = 1;
		} else this.eol = 0;
	}
}
