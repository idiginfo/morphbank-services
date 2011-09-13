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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Locality")
@DiscriminatorValue("Locality")
public class Locality extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	private String continentOcean;
	private String ocean;
	private String continent;
	private String country;
	@Column(name="state")
	private String stateProvince;
	private String county;
	private Integer coordinatePrecision;
	private Double latitude;
	private Double longitude;
	private Integer maximumElevation;
	private Integer minimumElevation;
	private String locality;
	private Integer minimumDepth;
	private Integer maximumDepth;
	private Integer imagesCount;
	@OneToMany(mappedBy = "locality", fetch = FetchType.LAZY)
	private Set<Specimen> specimens;
	private String informationWithheld;

	public Locality() {
		super();
	}

	private void init() {
		// make sure fields are not null
		locality = "";
		continentOcean = null;
		country = null;
	}

	public Locality(int id, String name, String userId, String groupId) {
		super(id, name, userId, userId, groupId);
		init();
	}

	public Locality(int id, String name, User user, Group group) {
		super(id, name, user, group);
		init();
	}
	public Locality(int id, String name, String ownerId, String submitterId, String groupId) {
		super(id, name,  ownerId, submitterId, groupId);
		init();
	}

	public Locality(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
		init();
	}

	public Set<Specimen> getSpecimens() {
		return specimens;
	}

	public void setSpecimens(Set<Specimen> specimens) {
		this.specimens = specimens;
	}

	public Integer getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(Integer imagesCount) {
		this.imagesCount = imagesCount;
	}

	public String getContinentOcean() {
		return continentOcean;
	}

	public void setContinentOcean(String continentOcean) {
		this.continentOcean = continentOcean;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getMaximumDepth() {
		return maximumDepth;
	}

	public void setMaximumDepth(Integer maximumDepth) {
		this.maximumDepth = maximumDepth;
	}

	public Integer getMaximumElevation() {
		return maximumElevation;
	}

	public void setMaximumElevation(Integer maximumElevation) {
		this.maximumElevation = maximumElevation;
	}

	public Integer getMinimumDepth() {
		return minimumDepth;
	}

	public void setMinimumDepth(Integer minimumDepth) {
		this.minimumDepth = minimumDepth;
	}

	public Integer getMinimumElevation() {
		return minimumElevation;
	}

	public void setMinimumElevation(Integer minimumElevation) {
		this.minimumElevation = minimumElevation;
	}

	public Integer getCoordinatePrecision() {
		return coordinatePrecision;
	}

	public void setCoordinatePrecision(Integer coordinatePrecision) {
		this.coordinatePrecision = coordinatePrecision;
	}

	public String getOcean() {
		return ocean;
	}

	public void setOcean(String ocean) {
		this.ocean = ocean;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}
	
	public String getInformationWithheld() {
		return informationWithheld;
	}

	public void setInformationWithheld(String informationWithheld) {
		this.informationWithheld = informationWithheld;
	}

}
