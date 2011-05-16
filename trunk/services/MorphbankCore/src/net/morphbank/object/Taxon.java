/**
 * 
 */
package net.morphbank.object;

import java.util.*;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "Taxon")
public class Taxon implements IdObject, Serializable {
	static final long serialVersionUID = 1;

	public boolean persist() {
		return false;
	}

	public boolean validate() {
		return false;
	}

	public String getObjectTypeIdStr() {
		return "Taxon";
	}

	// fields
	@Id
	private int tsn;
	private String scientificName;
	private String rank;
	@OneToMany(mappedBy = "taxon", fetch = FetchType.LAZY)
	private Set<Specimen> specimens;
	@OneToMany(mappedBy = "taxon")
	private Set<View> views;
	@OneToMany(mappedBy = "branchChild", fetch = FetchType.LAZY)
	@MapKey(name = "rank")
	private Map<String, TaxonBranchNode> taxonNodes;
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = TaxonAuthor.class)
	@JoinColumn(name = "taxonAuthorId", insertable = false, updatable = false)
	private TaxonAuthor taxonAuthor;
	@OneToMany(mappedBy = "taxon", fetch = FetchType.LAZY)
	private List<TaxonConcept> taxonConcept;

	private static final String sciNameQueryString = "select t from Taxon t "
			+ "where t.scientificName = :sciName order by t.tsn";
	private static final String sciNameAuthorQueryString = "select t from Taxon t "
			+ "where t.scientificName = :sciName and t.taxonAuthor.name = :authorName";// order
																						// by
																						// t.tsn";

	public static Taxon getTaxon(int tsn) {
		try {
			Taxon taxon = (Taxon) MorphbankConfig.getEntityManager().find(
					Taxon.class, tsn);
			return taxon;
		} catch (Exception e) {
			return null;
		}
	}

	public static Taxon getTaxon(String scientificName) {
		try {
			Query sciNameQuery = MorphbankConfig.getEntityManager()
					.createQuery(sciNameQueryString);
			sciNameQuery.setParameter("sciName", scientificName);
			sciNameQuery.setMaxResults(1);
			List taxa = sciNameQuery.getResultList();
			if (taxa.size() > 0) {
				return (Taxon) taxa.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Taxon getTaxon(String scientificName, String authorName) {
		try {
			Query sciNameQuery = MorphbankConfig.getEntityManager()
					.createQuery(sciNameAuthorQueryString);
			sciNameQuery.setParameter("sciName", scientificName);
			sciNameQuery.setParameter("authorName", authorName);
			sciNameQuery.setMaxResults(1);
			List taxa = sciNameQuery.getResultList();
			if (taxa.size() > 0) {
				return (Taxon) taxa.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTaxonomicNames() {
		StringBuffer names = new StringBuffer();
		java.util.Collection<TaxonBranchNode> nodeColl = getTaxonNodes()
				.values();
		// int i = nodeColl.size();
		// TaxonBranchNode kingdom = getTaxonNodes().get("Kingdom");
		Iterator<TaxonBranchNode> nodes = nodeColl.iterator();
		String blank = "";
		while (nodes.hasNext()) {
			names.append(blank).append(nodes.next().getScientificName());
			blank = " ";
		}
		return names.toString();
	}

	public int getId() {
		return getTsn();
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Map<String, TaxonBranchNode> getTaxonNodes() {
		return taxonNodes;
	}

	public void setTaxonNodes(Map<String, TaxonBranchNode> taxonNodes) {
		this.taxonNodes = taxonNodes;
	}

	public Set<Specimen> getSpecimens() {
		return specimens;
	}

	public void setSpecimens(Set<Specimen> specimen) {
		this.specimens = specimen;
	}

	public int getTsn() {
		return tsn;
	}

	public void setTsn(int tsn) {
		this.tsn = tsn;
	}

	public Set<View> getViews() {
		return views;
	}

	public void setViews(Set<View> views) {
		this.views = views;
	}

	public String getScientificName() {
		return scientificName;
	}

	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	public String getTaxonAuthorName() {
		if (taxonAuthor == null)
			return null;
		return taxonAuthor.getName();
	}

	public TaxonAuthor getTaxonAuthor() {
		return taxonAuthor;
	}

	public List<TaxonConcept> getTaxonConcept() {
		return taxonConcept;
	}

	public void addTaxonConcept(TaxonConcept taxonConcept) {
		this.taxonConcept.add(taxonConcept);
	}

	public void setTaxonAuthor(TaxonAuthor taxonAuthor) {
		this.taxonAuthor = taxonAuthor;
	}
}
