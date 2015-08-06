/**
 * 
 */
package net.morphbank.object;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import net.morphbank.MorphbankConfig;

/**
 * Class used for keeping track of internal links (CollectionObjects) that are
 * unresolved. That is, the source or target object is not part of the database
 * and needs to be added before the link between source and target can be
 * entered in the CollectionObjects table
 * 
 * @author riccardi
 */
@Entity
@Table(name = "MissingLinks")
public class MissingLink {
	static final long serialVersionUID = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;// autoincrement id
	private int sourceId;
	private int targetId;
	private String linkType;
	private Integer objectOrder;
	private String objectRole;
	private String objectTitle;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateRecorded;
	private String remoteDetailUrl;

	// link types
	public static final String OBJECT = "object"; // missing object

	// References from specific object types
	public static final String SPECIMEN = "specimen"; // used for Specimen of an
	// Image
	public static final String VIEW = "view"; // used for View of an Image
	public static final String LOCALITY = "locality"; // used for Locality of a
	// Specimen
	public static final String RELATED_OBJECT = "relatedobject"; // default
	public static final String SPECIMEN_STANDARD_IMAGE = "specimenstandardimage";
	public static final String SPECIMEN_DETERMINATION = "specimendetermination";
	// View
	public static final String VIEW_STANDARD_IMAGE = "viewstandardimage";
	public static final String VIEW_DETERMINATION = "viewdetermination";
	public static final String GROUP_MANAGER = "groupManager";

	public MissingLink() {
		super();
	}

	public MissingLink(int sourceId, int targetId, String linkType) {
		this();
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.linkType = linkType;
		Calendar today = Calendar.getInstance();
		dateRecorded = today.getTime();
	}

	public static MissingLink createMissingLink(int sourceId, int targetId, String linkType,
			String remoteDetailUrl) {
		MissingLink link = createMissingLink(sourceId, targetId, linkType, null, null, null,
				remoteDetailUrl);
		return link;
	}

	public static MissingLink createMissingLink(int sourceId, Integer targetId, String linkType,
			Integer objectOrder, String objectRole, String objectTitle, String remoteDetailUrl) {
		MissingLink missingLink = new MissingLink(sourceId, targetId, linkType);
		if (objectOrder != null) missingLink.objectOrder = objectOrder;
		missingLink.objectRole = objectRole;
		missingLink.objectTitle = objectTitle;
		missingLink.remoteDetailUrl = remoteDetailUrl;

		EntityManager em = MorphbankConfig.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		boolean localTransaction = false;
		if (!tx.isActive()){
			localTransaction = true;
			tx.begin();
		}
		em.persist(missingLink);
		em.flush();
		if (localTransaction){
			tx.commit();
		}
		return missingLink;
	}

	public static List<MissingLink> getList(int sourceId) {
		EntityManager em = MorphbankConfig.getEntityManager();
		String sql = "select m from MissingLinks m where sourceId=" + sourceId;
		Query q = em.createQuery(sql);
		List<MissingLink> missingLinks = q.getResultList();
		return missingLinks;
	}

	public boolean delete() {
		EntityManager em = MorphbankConfig.getEntityManager();
		em.merge(this);
		em.remove(this);
		return true;
	}

	/**
	 * Resolve all of the missing links by linking the objects
	 * 
	 */
	public static int fixLinks() {
		int count = 0;
		return count;
	}

	public boolean fixLink() {
		BaseObject source = BaseObject.getEJB3Object(sourceId);
		if (source == null) return false;
		BaseObject target = BaseObject.getEJB3Object(targetId);
		if (target == null) return false;
		if (linkType == null || linkType.equals(RELATED_OBJECT)) {
			CollectionObject collObject = new CollectionObject(source, target);
			collObject.setObjectOrder(objectOrder);
			collObject.setObjectRole(objectRole);
			collObject.setObjectTitle(objectTitle);
			EntityManager em = MorphbankConfig.getEntityManager();
			em.persist(collObject);
		} else if (linkType.equals(SPECIMEN)) {
			if (!(source instanceof Image)) return false;
			if (!(target instanceof Specimen)) return false;
			((Image) source).setSpecimen((Specimen) target);
		} else if (linkType.equals(VIEW)) {
			if (!(source instanceof Image)) return false;
			if (!(target instanceof View)) return false;
			((Image) source).setView((View) target);
		} else if (linkType.equals(LOCALITY)) {
			if (!(source instanceof Specimen)) return false;
			if (!(target instanceof Locality)) return false;
			((Specimen) source).setLocality((Locality) target);
		} else if (linkType.equals(SPECIMEN_STANDARD_IMAGE)) {
			if (!(source instanceof Specimen)) return false;
			if (!(target instanceof Image)) return false;
			((Specimen) source).setStandardImage((Image) target);
		} else if (linkType.equals(SPECIMEN_DETERMINATION)) {
			if (!(source instanceof Specimen)) return false;
			if (!(target instanceof TaxonConcept)) return false;
			((Specimen) source).setTaxon(((TaxonConcept) target).getTaxon());
		} else if (linkType.equals(VIEW_STANDARD_IMAGE)) {
			if (!(source instanceof View)) return false;
			if (!(target instanceof Image)) return false;
			((Specimen) source).setStandardImage((Image) target);
		} else if (linkType.equals(VIEW_DETERMINATION)) {
			if (!(source instanceof View)) return false;
			if (!(target instanceof TaxonConcept)) return false;
			((View) source).setTaxon(((TaxonConcept) target).getTaxon());
		} else if (linkType.equals(GROUP_MANAGER)) {
			if (!(source instanceof Group)) return false;
			if (!(target instanceof User)) return false;
			((Group) source).setGroupManager(((User) target));		
		} else if (linkType.equals(OBJECT)) {
			// successful object load
		}
		delete();
		return true;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public int getId() {
		return id;
	}

	public int getObjectOrder() {
		return objectOrder;
	}

	public void setObjectOrder(int objectOrder) {
		this.objectOrder = objectOrder;
	}

	public String getObjectRole() {
		return objectRole;
	}

	public void setObjectRole(String objectRole) {
		this.objectRole = objectRole;
	}

	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		this.objectTitle = objectTitle;
	}

	public Date getDateRecorded() {
		return dateRecorded;
	}

	public void setDateRecorded(Date dateRecorded) {
		this.dateRecorded = dateRecorded;
	}

	public String getRemoteDetailUrl() {
		return remoteDetailUrl;
	}

	public void setRemoteDetailUrl(String remoteDetailUrl) {
		this.remoteDetailUrl = remoteDetailUrl;
	}

	public void setObjectOrder(Integer objectOrder) {
		this.objectOrder = objectOrder;
	}

}
