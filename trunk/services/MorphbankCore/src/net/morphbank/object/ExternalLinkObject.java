/**
 * 
 */
package net.morphbank.object;

import javax.persistence.*;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "ExternalLinkObject")
public class ExternalLinkObject { // implements IdObject {
	static final long serialVersionUID = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int linkId;// autoincrement id
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "mbId")
	private BaseObject object; // | objectId
	private int extLinkTypeId;// TODO include related table
	private String label;
	private String urlData;
	private String description;
	private String externalId;

	public ExternalLinkObject() {
		this.extLinkTypeId = OTHER_LINK_TYPE;
	}

	static final int OTHER_LINK_TYPE = 5;

	public ExternalLinkObject(BaseObject object, String externalId,
			String linkType) {
		this();
		this.object = object;
		if (object != null && object.getExternalLinks() != null) {
			object.getExternalLinks().add(this);
		}
		boolean success = setExternalId(externalId);
		if (success) {
			this.description = "External identification";
		} else {
			this.description = "Duplicate of external identification: would have been '"
					+ externalId + "'";
		}
		setExtLinkTypeId(linkType);
	}

	/**
	 * Find the id of the object that matches the external Id
	 * 
	 * @param externalId
	 * @return
	 */
	public static int searchExternalId(String externalId) {
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			String queryStr = "select e.object.id from ExternalLinkObject e where e.externalId= :extId";
			Query query = em.createQuery(queryStr);
			query.setParameter("extId", externalId);
			Integer id = (Integer) query.getSingleResult();
			if (id != null) {
				return id.intValue();
			}
		} catch (PersistenceException e) {

		}
		return 0;
	}

	public static ExternalLinkObject getExternalLinkObject(String externalId) {
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			String queryStr = "select e from ExternalLinkObject e where e.externalId= :extId";
			Query query = em.createQuery(queryStr);
			query.setParameter("extId", externalId);
			ExternalLinkObject obj = (ExternalLinkObject) query
					.getSingleResult();
			if (obj != null) {
				return obj;
			}
		} catch (PersistenceException e) {
			//no object with this id. 
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean validate() {
		if (getLabel() != null || getUrlData() != null
				| getExternalId() != null) {
			return true;
		} else {
			// no information
			return false;
		}
	}

	public boolean persist() {
		if (validate()) {
			MorphbankConfig.getEntityManager().merge(this);
			return true;
		} else {
			// no information, don't keep object
			return false;
		}
	}

	public void addToDescription(String desc) {
		if (getDescription() == null) {
			setDescription(desc);
		} else {
			setDescription(getDescription() + " " + desc);
		}
	}

	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}

	public BaseObject getObject() {
		return object;
	}

	public void setObject(BaseObject object) {
		this.object = object;
	}

	public int getExtLinkTypeId() {
		return extLinkTypeId;
	}

	public void setExtLinkTypeId(int extLinkTypeId) {
		this.extLinkTypeId = extLinkTypeId;
	}

	public void setExtLinkTypeId(String linkType) {
		extLinkTypeId = ExternalLinkType.getExternalTypeId(linkType);
		if (extLinkTypeId < 0) extLinkTypeId = OTHER_LINK_TYPE;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrlData() {
		return urlData;
	}

	public void setUrlData(String urlData) {
		this.urlData = urlData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExternalId() {
		return externalId;
	}

	public boolean setExternalId(String externalId) {
		BaseObject obj = BaseObject.getObjectByExternalId(externalId);
		if (obj == null) {// external Id not in use
			this.externalId = externalId;
			return true;
		} else if (obj == getObject()) {// external id already marks this object
			return true;
		}
		return false;
	}
}