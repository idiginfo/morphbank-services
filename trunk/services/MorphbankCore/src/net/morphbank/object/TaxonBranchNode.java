/**
 * 
 */
package net.morphbank.object;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "TaxonBranchNode")
public class TaxonBranchNode implements IdObject, Serializable {
	static final long serialVersionUID = 1;

	public String getObjectTypeIdStr(){
		return "TaxonBranchNode";
	}

	@Id
	private int child;
	@Id
	private int tsn;
	private String scientificName;
	private String kingdom;
	private String rank;
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Taxon.class)
	@JoinColumn(name = "tsn", insertable = false, updatable = false)
	private Taxon taxon;
	@ManyToOne(targetEntity = Taxon.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "child", insertable = false, updatable = false)
	private Taxon branchChild;

	public boolean persist() {
		// not needed;
		return false;
	}

	public boolean validate(){
		return true;
	}

	public String getName() {
		return scientificName;
	}

	public Taxon getBranchChild() {
		return branchChild;
	}

	public void setBranchChild(Taxon branchChild) {
		this.branchChild = branchChild;
	}

	public int getChild() {
		return child;
	}

	public void setChild(int child) {
		this.child = child;
	}

	public String getKingdom() {
		return kingdom;
	}

	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public int getTsn() {
		return tsn;
	}

	public void setTsn(int tsn) {
		this.tsn = tsn;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
