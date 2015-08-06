/**
 * 
 */
package net.morphbank.object;

import java.io.Serializable;

import javax.persistence.Column;
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
@Table(name = "TaxonAuthors")
public class TaxonAuthor implements IdObject, Serializable {
	static final long serialVersionUID = 1;

	public String getObjectTypeIdStr(){
		return "TaxonAuthor";
	}

	@Id
	@Column(name="taxon_author_id")
	private int taxonAuthorId;
	@Column(name="taxon_author")
	private String name;
	

	public boolean persist() {
		// not needed;
		return false;
	}

	public boolean validate() {
		return true;
	}


	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTaxonAuthorId() {
		return taxonAuthorId;
	}

	public String getName() {
		return name;
	}
}
