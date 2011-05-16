/**
 *
 */
package net.morphbank.object;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Otu")
@DiscriminatorValue("Otu")
public class Otu extends BaseObject {
	static final long serialVersionUID = 1;

	private String label;

	public Otu(int id, String name, User user, Group group) {
		super(id, name, user, group);
	}

	public Otu() {
		super();
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<BaseObject> getTaxons() {
		// get list of taxon concepts
		return getRelatedObjectsByRole("taxon");
	}

	public void addTaxon(TaxonConcept taxon, int index) {
		addRelatedObject(taxon, index, "taxon");
	}

}
