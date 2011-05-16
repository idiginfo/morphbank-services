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

import javax.persistence.*;

import net.morphbank.MorphbankConfig;

@Entity
@Table(name = "TaxonConcept")
@DiscriminatorValue("TaxonConcept")
public class TaxonConcept extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	// private int tsn;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "tsn")
	Taxon taxon;
	private String nameSpace;
	private String status;

	public TaxonConcept() {
		super();
	}

	public TaxonConcept(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
	}

	public boolean validate() {
		if (!super.validate()) {
			return false;
		}
		if (getTaxon() == null){// assign Life!
			setTaxon(Taxon.getTaxon(0));
		}
		return true;
	}
		
	static final String TC_QUERY = "select t from TaxonConcept t where t.taxon.tsn = :tsn";

	public static TaxonConcept getTaxonConcept(int tsn) {
		Query query = MorphbankConfig.getEntityManager()
				.createQuery(TC_QUERY);
		query.setParameter("tsn", tsn);
		try{
		TaxonConcept tc = (TaxonConcept) query.getSingleResult();
		return tc;
		} catch (Exception e){
			return null;
		}
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
}
