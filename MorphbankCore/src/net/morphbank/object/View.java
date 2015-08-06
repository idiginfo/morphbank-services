/**
 * 
 */
package net.morphbank.object;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "View")
@DiscriminatorValue("View")
public class View extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	private String viewName;
	private String imagingTechnique;
	private String imagingPreparationTechnique;
	private String specimenPart;
	private String viewAngle;
	private String developmentalStage;
	private String sex;
	private String form;
	// Integer viewTsn;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "viewTsn")
	private Taxon taxon;
	private Integer isStandardView;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "standardImageId")
	private Image standardImage;
	private int imagesCount;
	@OneToMany(mappedBy = "view", fetch = FetchType.LAZY)
	private Set<Image> images;

	public View(int id, String name, String userId, String groupId) {
		super(id, name, userId, userId, groupId);
	}

	public View(int id, String name, User user, Group group) {
		super(id, name, user, user, group);
		init(name);
	}

	public View(int id, String name, String ownerId, String submitterId, String groupId) {
		super(id, name, ownerId, submitterId, groupId);
	}

	public View(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
		init(name);
	}

	public View() {
		super();
	}

	public static View getViewFromName(String extId) {
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			String queryStr = "select v from View v where v.viewName=:name";
			Query query = em.createQuery(queryStr);
			query.setParameter("name", extId);
			View obj = (View) query.getSingleResult();
			if (obj != null) {
				return obj;
			}
		} catch (PersistenceException e) {
			// e.printStackTrace();
			// no object with this viewName.
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void init(String name) {
		this.sex = "Unspecified";
		this.viewName = name;
		this.form = "Unspecified";
		this.imagingPreparationTechnique = "Unspecified";
		this.specimenPart = "Unspecified";
		this.imagingTechnique = "Unspecified";
		this.developmentalStage = "Unspecified";
		this.viewAngle = "Unspecified";
	}
	
	/**
	 * Construct view name from fields
	 */
	public void setViewName() {
		char separator = '/';
		StringBuffer name = new StringBuffer(getSpecimenPart());
		name.append(separator).append(getViewAngle());
		name.append(separator).append(getImagingTechnique());
		name.append(separator).append(getImagingPreparationTechnique());
		name.append(separator).append(getDevelopmentalStage());
		name.append(separator).append(getSex());
		name.append(separator).append(getForm());
		viewName = name.toString();
	}


	public boolean persist() {
		// TODO ensure fk correspondence for sex, form, etc.
		boolean success;
		setViewName();
		success = MorphbankConfig.secondaryTableKeyInsert("Form", getForm());
		success = MorphbankConfig.secondaryTableKeyInsert("ImagingPreparationTechnique",
				getImagingPreparationTechnique());
		success = MorphbankConfig.secondaryTableKeyInsert("Sex", getSex());
		success = MorphbankConfig.secondaryTableKeyInsert("DevelopmentalStage",
				getDevelopmentalStage());
		success = MorphbankConfig.secondaryTableKeyInsert("SpecimenPart", getSpecimenPart());
		success = MorphbankConfig.secondaryTableKeyInsert("ViewAngle", getViewAngle());
		success = MorphbankConfig
				.secondaryTableKeyInsert("ImagingTechnique", getImagingTechnique());
		success = super.persist();
		return success;
	}

	public boolean validate() {
		if (form == null) {
			form = "Unspecified";
		}
		return super.validate();
	}

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public int getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(int imagesCount) {
		this.imagesCount = imagesCount;
	}

	public Integer getIsStandardView() {
		return isStandardView;
	}

	public void setIsStandardView(Integer isStandardView) {
		this.isStandardView = isStandardView;
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
		if (form == null) {
			this.form = "Unspecified";
		} else {
			this.form = form;
		}
	}

	public String getImagingPreparationTechnique() {
		return imagingPreparationTechnique;
	}

	public void setImagingPreparationTechnique(String imagingPreparationTechnique) {
		this.imagingPreparationTechnique = imagingPreparationTechnique;
	}

	public String getImagingTechnique() {
		return imagingTechnique;
	}

	public void setImagingTechnique(String imagingTechnique) {
		this.imagingTechnique = imagingTechnique;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSpecimenPart() {
		return specimenPart;
	}

	public void setSpecimenPart(String specimenPart) {
		if (specimenPart != null && specimenPart.length() > 254) {
			specimenPart = specimenPart.substring(0, 254);
		}
		this.specimenPart = specimenPart;
	}

	public String getViewAngle() {
		return viewAngle;
	}

	public void setViewAngle(String viewAngle) {
		this.viewAngle = viewAngle;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Image getStandardImage() {
		return standardImage;
	}

	public void setStandardImage(Image standardImage) {
		this.standardImage = standardImage;
		if (standardImage != null) {
			this.setThumbURL(Integer.toString(standardImage.getId()));
		} else {
			this.setThumbURL(null);
		}
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
}
