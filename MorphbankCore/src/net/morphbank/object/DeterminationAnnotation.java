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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "DeterminationAnnotation")
@DiscriminatorValue("DeterminationAnnotation")
@PrimaryKeyJoinColumn(name = "annotationId")
public class DeterminationAnnotation extends Annotation {
	static final long serialVersionUID = 1;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specimenId")
	private Specimen specimen;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tsnId")
	private Taxon taxon;
	private String rankId;
	private String kingdomId;
	private String rankName;
	private String prefix;
	private String suffix;
	private String typeDetAnnotation;
	private String sourceOfId;
	private String materialsUsedInId;
	private String resourcesused;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collectionId")
	private BaseObject collection;
	private String altTaxonName;

	public DeterminationAnnotation() {
		super();
		setObjectTypeId("DeterminationAnnotation");
	}

	public DeterminationAnnotation(int id, String name, User user, User submitter, Group group) {
		super(id, name, user, submitter, group);
		setObjectTypeId("Annotation");
		setTypeAnnotation("Annotation");
	}

	public DeterminationAnnotation(int id, String name, User user, Group group) {
		super(id, name, user, user, group);
		setObjectTypeId("Annotation");
		setTypeAnnotation("Annotation");
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public Specimen getSpecimen() {
		return specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	public String getAltTaxonName() {
		return altTaxonName;
	}

	public void setAltTaxonName(String altTaxonName) {
		this.altTaxonName = altTaxonName;
	}

	public String getKingdomId() {
		return kingdomId;
	}

	public void setKingdomId(String kingdomId) {
		this.kingdomId = kingdomId;
	}

	public String getMaterialsUsedInId() {
		return materialsUsedInId;
	}

	public void setMaterialsUsedInId(String materialsUsedInId) {
		this.materialsUsedInId = materialsUsedInId;
	}

	public BaseObject getMyCollection() {
		return collection;
	}

	public void setMyCollection(BaseObject collection) {
		this.collection = collection;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getRankId() {
		return rankId;
	}

	public void setRankId(String rankId) {
		this.rankId = rankId;
	}

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public String getResourcesused() {
		return resourcesused;
	}

	public void setResourcesused(String resourcesused) {
		this.resourcesused = resourcesused;
	}

	public String getSourceOfId() {
		return sourceOfId;
	}

	public void setSourceOfId(String sourceOfId) {
		this.sourceOfId = sourceOfId;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTypeDetAnnotation() {
		return typeDetAnnotation;
	}

	public void setTypeDetAnnotation(String typeDetAnnotation) {
		this.typeDetAnnotation = typeDetAnnotation;
	}
}
