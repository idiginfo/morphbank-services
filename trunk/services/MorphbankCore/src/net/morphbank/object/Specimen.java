/**
 * 
 */
package net.morphbank.object;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "Specimen")
@DiscriminatorValue("Specimen")
public class Specimen extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "basisOfRecordId")
	private BasisOfRecord basisOfRecord;
	private String sex;
	private String form;
	private String developmentalStage;
	private String preparationType;
	private Integer individualCount;
	// Integer tsnId;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "tsnId")
	Taxon taxon;
	private String typeStatus;
	@Column(name = "name")
	private String specimenName;
	@Temporal(TemporalType.DATE)
	private Date dateIdentified;
	private String comment;
	private String institutionCode;
	private String collectionCode;
	private String catalogNumber;
	private String previousCatalogNumber;
	private String relatedCatalogItem;
	private String relationshipType;
	private String collectionNumber;
	private String collectorName;
	// @Temporal(TemporalType.DATE)
	// private Date earliestDateCollected;
	// @Temporal(TemporalType.DATE)
	// private Date latestDateCollected;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCollected;
	@Temporal(TemporalType.TIMESTAMP)
	private Date latestDateCollected;
	@Temporal(TemporalType.TIMESTAMP)
	private Date earliestDateCollected;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "localityId")
	private Locality locality;
	private String notes;
	private String taxonomicNames;
	private Integer imagesCount;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "standardImageId")
	private Image standardImage;
	@OneToMany(mappedBy = "specimen", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Set<Image> images;
	private String barCode;
	private String ocr;
	private String labelData;

	public Specimen() {
		super();
		// setObjectTypeId("Specimen");
	}

	public Specimen(int id, String name, String ownerId, String submitterId, String groupId) {
		super(id, name, ownerId, submitterId, groupId);
	}

	public Specimen(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
		init();
	}
	public Specimen(int id, String name, String ownerId, String groupId) {
		super(id, name, ownerId, ownerId, groupId);
	}

	public Specimen(int id, String name, User owner, Group group) {
		super(id, name, owner, owner, group);
		init();
	}
	
	public void init() {
		setSpecimenName(getName());
		//setTypeStatus("nonType");
		// setLocality((Locality) BaseObject.getEJB3Object(103));
		setBasisOfRecord(BasisOfRecord.lookupSymbol("S"));
		//setTypeStatus("nonType");
		//setSex("unknown");
		//setForm("unknown");
		//setDevelopmentalStage("unknown");
		//setTypeStatus("Not Provided");
		setCollectorName(null);
		setDateCollected(null);
		setDateIdentified(null);
		setTaxon(null);
	}

	/**
	 * Test to see if object is fit to be stored as an object i.e. that the
	 * field values are appropriate for the database
	 */
	public boolean validate() {
		// TODO validate linked fields (sex, form, etc.) against allowed values
		if (!super.validate()) {
			return false;
			// } else if (typeStatus == null || locality == null ||
			// basisOfRecord == null || sex == null
			// || form == null || developmentalStage == null || typeStatus ==
			// null
			// || collectorName == null || dateIdentified==null || taxon==null)
			// {
			// return false;
		}
		return true;
	}

	/**
	 * Get the specimen object that matches the collection information
	 * 
	 * @param institutionCode
	 * @param collectionCode
	 * @param catalogNumber
	 * @return
	 */
	static Specimen getSpecimen(String institutionCode, String collectionCode,
			String catalogNumber) {
		try {
			Query query = MorphbankConfig
					.getEntityManager()
					.createQuery(
							"from Specimen where institutionCode=? and collectionCode = ? and catalogNumber=?");
			query.setParameter(0, institutionCode);
			query.setParameter(1, collectionCode);
			query.setParameter(2, catalogNumber);
			java.util.List list = query.getResultList();
			return (Specimen) list.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	public static Specimen getSpecimen(int objectId) {
		BaseObject obj = BaseObject.getEJB3Object(objectId);
		if (obj instanceof Specimen) {
			return (Specimen) obj;
		} else if (obj instanceof Image) {
			return (Specimen) ((Image) obj).getSpecimen();
		} else { // imageId does not represent image or specimen
			return null;
		}
	}

	public void setTaxonomicNamesFromDetermination() {
		if (getTaxon() != null) {
			setTaxonomicNames(getTaxon().getTaxonomicNames());
		}
	}

	public String getBasisOfRecordDesc() {
		if (basisOfRecord != null) {
			return basisOfRecord.getDescription();
		} else
			return null;
	}

	public BasisOfRecord getBasisOfRecord() {
		return basisOfRecord;
	}

	public void setBasisOfRecord(BasisOfRecord basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}

	public String getCatalogNumber() {
		return catalogNumber;
	}

	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}

	public String getCollectionCode() {
		return collectionCode;
	}

	public void setCollectionCode(String collectionCode) {
		this.collectionCode = collectionCode;
	}

	public String getCollectionNumber() {
		return collectionNumber;
	}

	public void setCollectionNumber(String collectionNumber) {
		this.collectionNumber = collectionNumber;
	}

	public String getCollectorName() {
		return collectorName;
	}

	public void setCollectorName(String collectorName) {
		this.collectorName = collectorName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDateIdentified() {
		return dateIdentified;
	}

	public void setDateIdentified(Date dateIdentified) {
		try {
			this.dateIdentified = dateIdentified;
		} catch (Exception e) {
			this.dateIdentified = null;
			e.printStackTrace();
		}
	}

	public String getDevelopmentalStage() {
		return developmentalStage;
	}

	public void setDevelopmentalStage(String developmentalStage) {
		this.developmentalStage = developmentalStage;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public Integer getIndividualCount() {
		return individualCount;
	}

	public void setIndividualCount(Integer individualCount) {
		this.individualCount = individualCount;
	}

	public String getInstitutionCode() {
		return institutionCode;
	}

	public void setInstitutionCode(String institutionCode) {
		this.institutionCode = institutionCode;
	}

	public String getLabelData() {
		return labelData;
	}

	public void setLabelData(String labelData) {
		this.labelData = labelData;
	}

	public String getSpecimenName() {
		return specimenName;
	}

	public void setSpecimenName(String name) {
		this.specimenName = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getOcr() {
		return ocr;
	}

	public void setOcr(String ocr) {
		this.ocr = ocr;
	}

	public String getPreparationType() {
		return preparationType;
	}

	public void setPreparationType(String preparationType) {
		this.preparationType = preparationType;
	}

	public String getPreviousCatalogNumber() {
		return previousCatalogNumber;
	}

	public void setPreviousCatalogNumber(String previousCatalogNumber) {
		this.previousCatalogNumber = previousCatalogNumber;
	}

	public String getRelatedCatalogItem() {
		return relatedCatalogItem;
	}

	public void setRelatedCatalogItem(String relatedCatalogItem) {
		this.relatedCatalogItem = relatedCatalogItem;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTaxonomicNames() {
		return taxonomicNames;
	}

	public void setTaxonomicNames(String taxonomicNames) {
		this.taxonomicNames = taxonomicNames;
	}

	public String getTypeStatus() {
		return typeStatus;
	}

	public void setTypeStatus(String typeStatus) {
		this.typeStatus = typeStatus;
	}

	public Date getDateCollected() {
		return dateCollected;
	}

	public void setDateCollected(Date dateCollected) {
		this.dateCollected = dateCollected;
	}

	public Integer getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(Integer imagesCount) {
		this.imagesCount = imagesCount;
	}

	public Image getStandardImage() {
		return standardImage;
	}

	public void setStandardImage(Image standardImage) {
		this.standardImage = standardImage;
		if (standardImage!=null){
			this.setThumbURL(Integer.toString(standardImage.getId()));
		} else {
			this.setThumbURL(null);
		}
	}

	public Locality getLocality() {
		return locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public Date getLatestDateCollected() {
		return latestDateCollected;
	}

	public void setLatestDateCollected(Date latestDateCollected) {
		this.latestDateCollected = latestDateCollected;
	}

	public Date getEarliestDateCollected() {
		return earliestDateCollected;
	}

	public void setEarliestDateCollected(Date earliestDateCollected) {
		this.earliestDateCollected = earliestDateCollected;
	}
}
