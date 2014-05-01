/**
 * 
 */
package net.morphbank.object;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "BaseObject")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "objectTypeId", discriminatorType = DiscriminatorType.STRING)
public class BaseObject implements IdObject, java.io.Serializable {
	static final long serialVersionUID = 1;

	static final String PACKAGE_NAME = "net.morphbank.object.";

	// object fields
	@Transient
	String url = null;
	@Id
	private int id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "userId")
	private User user;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "submittedBy")
	private User submittedBy;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "groupId")
	private Group group;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLastModified;
	@Temporal(TemporalType.DATE)
	private Date dateToPublish;
	@Transient
	// private String objectTypeId;
	private String description;
	private String objectLogo;
	private String keywords;
	private String summaryHTML;
	private String thumbURL;
	private String name;
	@OneToMany(mappedBy = "collection", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<CollectionObject> objects = new Vector<CollectionObject>();
	@OneToMany(mappedBy = "object", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<CollectionObject> collections = new Vector<CollectionObject>();
	@OneToMany(mappedBy = "object", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ExternalLinkObject> externalLinks = new Vector<ExternalLinkObject>();
	// //@OneToMany(mappedBy = "object", fetch = FetchType.LAZY)
	// @Transient
	// private List<Field> fields = new Vector<Field>();
	@OneToMany(mappedBy = "object", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@MapKey(name = "name")
	private Map<String, UserProperty> userProperties = new HashMap<String, UserProperty>();
	private Boolean geolocated;
	private String imageAltText;
	private String xmlKeywords;
	private String hostServer;
	private String uuidString;

	// Constructors
	public BaseObject() {
	}

	public BaseObject(int id, User user, Group group) {
		this(id, "", user, group);
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param ownerId
	 *            id or uin
	 * @param submitterId
	 *            id or uin
	 * @param groupId
	 *            id or groupName
	 */
	public BaseObject(int id, String name, String ownerId, String submitterId, String groupId) {
		User owner;
		User submitter;
		Group group;
		try {
			int uid = Integer.parseInt(ownerId);
			owner = (User) getEJB3Object(uid);
		} catch (Exception e) {
			owner = User.getUserByUIN(ownerId);
		}
		try {
			int uid = Integer.parseInt(submitterId);
			submitter = (User) getEJB3Object(uid);
		} catch (Exception e) {
			submitter = User.getUserByUIN(ownerId);
		}
		try {
			int gid = Integer.parseInt(groupId);
			group = (Group) getEJB3Object(gid);
		} catch (Exception e) {
			group = Group.getGroupByName(groupId);
		}
		init(0, name, owner, submitter, group);
	}

	/**
	 * Assure that method can be made persistent
	 * 
	 * @param id
	 * 
	 * @param name
	 * @param owner
	 * @param group
	 */
	public BaseObject(int id, String name, User owner, Group group) {
		init(id, name, owner, owner, group);
	}

	public BaseObject(int id, String name, User owner, User submitter, Group group) {
		init(id, name, owner, submitter, group);
	}

	public void init(int id, String name, User owner, User submitter, Group group) {
		setId(id);
		setName(name);
		setUser(owner);
		setGroup(group);
		setSubmittedBy(submitter);
		Calendar today = Calendar.getInstance();
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);
		setDateCreated(today.getTime());
		//setDateLastModified(today.getTime());
		setDateToPublish(nextYear.getTime());
	}

	public void updateDateLastModified() {
		Calendar today = Calendar.getInstance();
		setDateLastModified(today.getTime());
	}

	// validation criteria
	// TODO add necessary criteria

	// Utility methods
	public boolean persist() {
		if (!validate()) {
			return false;
		}
		int id = createObject();
		return id > 0;
	}

	/**
	 * Test to see if object is fit to be stored as an object i.e. that the
	 * field values are appropriate for the database
	 */
	public boolean validate() {
		if (name == null) {// watch out, Specimen has 2 name fields
			name = getName();
			// if (name == null) {
			// return false;
			// }
		}
		if (dateCreated == null) {
			return false;
		} 
		if (dateLastModified == null) {
			//ok to be null
		} 
		if (dateToPublish == null) {
			return false;
		} 
		if (user == null || group == null || submittedBy == null) {
			return false;
		}
		return true;
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof BaseObject)) return false;
		final BaseObject obj = (BaseObject) other;
		if (obj.getId() != getId()) return false;
		return true;
	}

	public int hashCode() {
		int result;
		result = Integer.valueOf(getId()).hashCode();
		return result;
	}

	public void setModified() {
		setDateLastModified(Calendar.getInstance().getTime());
	}

	public static String className(String name) {
		// make sure the 1st mbCharacter is upper case
		char c = name.charAt(0);
		return Character.toUpperCase(c) + name.substring(1);
	}

	public static String fullClassName(String name) {
		return PACKAGE_NAME + className(name);
	}

	public String getClassName() {
		return getClass().getSimpleName();
	}

	public String getFullClassName() {
		return fullClassName(getClassName());
	}

	public static BaseObject getObject(String idStr) {
		int id;
		try {
			id = Integer.parseInt(idStr);
			return getEJB3Object(id);
		} catch (Exception e) {
			// not an integer
		}
		// maybe external id
		BaseObject obj = getObjectByExternalId(idStr);
		if (obj != null) return obj;
		// otherwise?
		return null;
	}

	/**
	 * Load the correct type for a BaseObject id
	 * 
	 * @param localId
	 * @return
	 */
	public static BaseObject getEJB3Object(int localId) {
		BaseObject base;
		int numtries = 0;
		EntityManager em = MorphbankConfig.getEntityManager();
		if (em == null) {
			return null;
		}
		while (numtries < 2) {
			try {
				base = (BaseObject) em.find(BaseObject.class, localId);
				return base;
			} catch (Exception e) {
				// em is not active, try again
				e.printStackTrace();
				MorphbankConfig.closeEntityManager();
				em = MorphbankConfig.getEntityManager();
				numtries++;
			}
		}
		return null;
	}

	/**
	 * Find the first object identified the by one of the external ids
	 * 
	 * @param localId
	 * @return
	 */
	public static BaseObject getObjectByExternalId(List<String> extIdList) {
		if (extIdList == null) {
			return null;
		}
		Iterator<String> extIds = extIdList.iterator();
		while (extIds.hasNext()) {
			BaseObject obj = getObjectByExternalId(extIds.next());
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

	/**
	 * Find the object identified the by the external id
	 * 
	 * @param localId
	 * @return
	 */
	public static BaseObject getObjectByExternalId(String externalId) {
		ExternalLinkObject obj = ExternalLinkObject.getExternalLinkObject(externalId);
		if (obj != null) {
			return obj.getObject();
		}
		return null;
	}

	// public ExternalLinkObject getExternalLink(String externalId) {
	// Iterator<ExternalLinkObject> externs = getExternalLinks().iterator();
	// while (externs.hasNext()) {
	// ExternalLinkObject extern = externs.next();
	// if (extern.getExternalId() != null &&
	// extern.getExternalId().equals(externalId)) {
	// return extern;
	// }
	// }
	// return null;
	// }

	// Collection management methods

	// native query for related ids
	public List getRelatedIdList() {
		String idQueryStr = "select objectId from CollectionObjects where collectionId=?";
		Query idQuery = MorphbankConfig.getEntityManager().createNativeQuery(idQueryStr);
		idQuery.setParameter(1, this.getId());
		return idQuery.getResultList();
	}

	public List<Integer> getRelatedIds() {
		List<Integer> list = new Vector<Integer>();
		Iterator ids = getRelatedIdList().iterator();
		while (ids.hasNext()) {
			int id = MorphbankConfig.getIntFromQuery(ids.next());
			list.add(id);
		}
		return null;
	}

	public BaseObject getRelatedObject(int index) {
		CollectionObject obj = getObjects().get(index);
		return obj.getObject();
	}

	public CollectionObject getRelatedObject(BaseObject obj) {
		if (obj == null) return null;
		Iterator<CollectionObject> collObjects = getObjects().iterator();
		while (collObjects.hasNext()) {
			CollectionObject collObj = collObjects.next();
			if (collObj.getObject() == obj) return collObj;
		}
		return null;
	}

	public void addRelatedObject(BaseObject obj) {
		CollectionObject collobj = new CollectionObject(this, obj);
		getObjects().add(collobj);
		// session.save(collobj);
	}

	public void addRelatedObject(BaseObject obj, int index) {
		addRelatedObject(obj, index, null);
	}

	public void addRelatedObject(BaseObject obj, int index, String role) {
		addRelatedObject(obj, index, role, "");
	}

	public void addRelatedObject(BaseObject obj, String role) {
		addRelatedObject(obj, -1, role, "");
	}

	public void addRelatedObject(BaseObject obj, int index, String role, String title) {
		// TODO check for existing object
		if (obj == null) {
			return; // no object provided
		}
		CollectionObject collobj = new CollectionObject(this, obj);
		collobj.setObjectRole(role);
		if (index >= 0) {
			collobj.setObjectOrder(index);
		}
		collobj.setObjectTitle(title);
		// collobj.setCollection(this);
		getObjects().add(collobj);
		collobj.persist();
	}

	public void moveRelatedObject(int index, int newIndex) {
		getObjects().add(newIndex, getObjects().get(index));
		if (newIndex < index) index++;
		removeRelatedObject(index);
		// session.update(this);
	}

	public void moveRelatedObject(int index, int newIndex, BaseObject newCollection) {

		newCollection.addRelatedObject(getObjects().get(index).getObject(), newIndex);
		removeRelatedObject(index);
		// session.update(this);
		// session.save(newCollection);
	}

	public void moveRelatedObject(int index, int newIndex, BaseObject newCollection, String newRole) {

		moveRelatedObject(index, newIndex, newCollection);
		newCollection.getObjects().get(newIndex).setObjectRole(newRole);
		// session.update(this);
		// session.save(newCollection);
	}

	public void updateRelatedObjectOrder() {
		for (int i = 0; i < getObjects().size(); i++) {
			getObjects().get(i).setObjectOrder(i);
			// session.update(getObjects().get(i));
		}
	}

	public void removeRelatedObject(int index) {

		getObjects().remove(index);
		updateRelatedObjectOrder();
	}

	public void removeRelatedObject(BaseObject object) {
		int i = 0;
		for (i = 0; i < getObjects().size(); i++) {
			if (getObjects().get(i).getObject().equals(object)) break;
		}
		if (i < getObjects().size()) removeRelatedObject(i);
	}

	public List<CollectionObject> getCollections() {
		return collections;
	}

	public List<BaseObject> getRelatedObjectsByRole(String role) {
		List<BaseObject> listByRole = new Vector<BaseObject>();
		for (int i = 0; i < getObjects().size(); i++) {
			CollectionObject cobj = getObjects().get(i);
			if (cobj != null && cobj.getObjectRole().compareTo(role) == 0)
				listByRole.add(this.getObjects().get(i).getObject());
		}
		return listByRole;
	}

	public List getRelatedObjectIdsByRoleIndex(String role, int index) {
		StringBuffer query = new StringBuffer();
		query.append("select c.object.id from CollectionObject c ").append(" where c.objectRole='")
				.append(role);
		query.append("' and c.collection.id=").append(getId());
		query.append(" and c.objectOrder=").append(index);
		Query sessionQuery = null;
		try {
			sessionQuery = MorphbankConfig.getEntityManager().createQuery(query.toString());
			List results = sessionQuery.getResultList();
			return results;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return null;
		}
	}

	public BaseObject getFirstRelatedObjectByRoleIndex(String role, int index) {
		List results = getRelatedObjectIdsByRoleIndex(role, index);
		if (results != null) {
			try {
				Integer objectId = null;
				Object result = results.get(0);
				if (result instanceof List) {
					objectId = ((Integer) ((List) result).get(0));
				} else {
					objectId = (Integer) result;
				}
				int id = objectId.intValue();
				return getEJB3Object(id);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public List<BaseObject> getRelatedObjectsByType(String objectTypeId) {
		List<BaseObject> listByType = new Vector<BaseObject>();
		for (int i = 0; i < getObjects().size(); i++) {
			if (this.getObjects().get(i).getObjectTypeId().compareTo(objectTypeId) == 0)
				listByType.add(this.getObjects().get(i).getObject());
		}
		return listByType;
	}

	// external link methods

	public ExternalLinkObject addExternalLink(String linkTypeName, String externalId, String desc,
			String label, String urlData) {
		ExternalLinkObject externalLink = null;
		if (externalId != null) {
			externalLink = ExternalLinkObject.getExternalLinkObject(externalId);
			// externalLink = getExternalLink(externalId);
			if (externalLink != null) {
				// external link already present, OK to update below
			} else { // link has not yet been created
				externalLink = new ExternalLinkObject(this, externalId, linkTypeName);
			}
		} else {// no external link in sourceId of xmlObj
			externalLink = new ExternalLinkObject(this, null, linkTypeName);
		}
		externalLink.addToDescription(desc);
		externalLink.setLabel(label);
		externalLink.setUrlData(urlData);
		return externalLink;
	}

	// user property methods

	public void addUserProperty(UserProperty prop) {
		getUserProperties().put(prop.getName(), prop);
	}

	public UserProperty addUserProperty(String name, String value, String namespaceURI) {
		UserProperty prop = getUserProperties().get(name);
		if (prop != null) {
			// update value of property
			prop.setNamespaceURI(namespaceURI);
			prop.setValue(value);
		} else {
			prop = new UserProperty(this, name, value, namespaceURI);
			getUserProperties().put(prop.getName(), prop);
		}
		return prop;
	}

	/**
	 * Get value of first user property with name
	 * 
	 * @param name
	 * @return
	 */
	public String getFirstUserProperty(String name) {
		UserProperty prop = getUserProperties().get(name);
		if (prop != null) {
			return prop.getValue();
		}
		return null;
	}

	/**
	 * get the locality of an object
	 * 
	 * @return
	 */
	public Locality getLocalityObject() {
		// defined for Image, Specimen and Locality
		Locality locality = null;
		Specimen specimen = null;
		if (this instanceof Locality) return (Locality) this;
		if (this instanceof Image) specimen = ((Image) this).getSpecimen();
		if (this instanceof Specimen) specimen = (Specimen) this;
		if (specimen != null) locality = specimen.getLocality();
		return locality;
	}

	// getters and setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null && name.length() > 64) {
			this.name = name.substring(0, 63);
		} else {
			this.name = name;
		}
	}

	public List<CollectionObject> getObjects() {
		return objects;
	}

	public String getUrl() {
		if (url == null) {
			setUrl(MorphbankConfig.getURL(id));
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastModified() {
		return dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public Date getDateToPublish() {
		return dateToPublish;
	}

	public void setDateToPublish(Date dateToPublish) {
		this.dateToPublish = dateToPublish;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, UserProperty> getUserProperties() {
		return userProperties;
	}

	public int getGroupId() {
		return group.getId();
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getObjectTypeIdStr() {
		if (this instanceof Group) {
			return "Groups";
		}
		return getClassName();
	}

	// public void setObjectTypeId(String objectTypeId) {
	// this.objectTypeId = objectTypeId;
	// }

	public User getUser() {
		return user;
	}

	public int getUserId() {
		return user.getId();
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getObjectLogo() {
		return objectLogo;
	}

	public void setObjectLogo(String objectLogo) {
		this.objectLogo = objectLogo;
	}

	public String getSummaryHTML() {
		return summaryHTML;
	}

	public void setSummaryHTML(String summaryHTML) {
		this.summaryHTML = summaryHTML;
	}

	/**
	 * Convert integer value of thumbURL to correct URL, if necessary
	 * 
	 * @return URL of thumbnail for this object
	 */
	public String getFullThumbURL() {
		// if simple integer, generate thumbnail URL
		try {
			Integer.parseInt(thumbURL);
			return MorphbankConfig.getImageURL(thumbURL, "thumb");
		} catch (Exception e) {
			return thumbURL;
		}
	}

	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}

	public List<ExternalLinkObject> getExternalLinks() {
		return externalLinks;
	}

	public void setExternalLinks(List<ExternalLinkObject> externalLinks) {
		this.externalLinks = externalLinks;
	}

	/**
	 * Make the object persistent: assign an id and save into the database Use
	 * the existing transaction if open, create and commit a new transaction
	 * otherwise
	 * 
	 * @return id of the new persistent object, return 0 if save fails
	 */
	public int createObject() {
		// if (getId() != 0) { // not new object
		// System.out.println("attempt to create new object with non-zero id");
		// return getId();
		// }
		boolean createTransaction = false;
		EntityTransaction tx = MorphbankConfig.getEntityManager().getTransaction();
		if (tx == null) {// no transaction available, unknown reason
			return 0;
		} else if (!tx.isActive()) {
			createTransaction = true;
			tx.begin();
		}
		int id = getId();
		int prevId = id;
		int numAttempts = 0;
		while (numAttempts < 2) {
			try {
				if (id == 0) {
					Query sessionQuery = MorphbankConfig.getEntityManager().createNativeQuery(
							"select GetNewObjectId()");
					Object newId = sessionQuery.getSingleResult();
					if (newId instanceof Vector) {
						newId = ((Vector) newId).get(0);
					}
					if (newId instanceof Integer) {
						id = ((Integer) newId).intValue();
					} else {
						id = Integer.parseInt(newId.toString());
					}
					if (id == prevId) {
						// the previous insert did not fail because of bad id
						// System.out.println("previous instance failed in
						// createObject");
						return 0;
					}
					setId(id);
				}
				// MorphbankConfig.getEntityManager().merge(this);
				MorphbankConfig.getEntityManager().persist(this);
				MorphbankConfig.getEntityManager().flush();
				if (createTransaction) {// method created its own transaction
					tx.commit();
				}
				return id;
			} catch (Exception e) {
				numAttempts++;
				if (numAttempts >= 2) {
				}
				prevId = id;
				System.out.println("object save failed for some reason");
				e.printStackTrace();
				// System.out.println("Exception in createObject");
			}
		}
		// create object failed, reset id of object and rollback the tx, if new
		// tx created
		setId(0);
		if (createTransaction) {
			tx.rollback();
		}
		return 0;
	}

	public void print(java.io.PrintStream out) {
		out.print(toString());
	}

	public Boolean getGeolocated() {
		return geolocated;
	}

	public boolean isGeolocated() {
		if (geolocated == null)
			return false;
		else
			return geolocated.booleanValue();
	}

	public void setGeolocated(Boolean geolocated) {
		this.geolocated = geolocated;
	}

	public String getImageAltText() {
		return imageAltText;
	}

	public void setImageAltText(String imageAltText) {
		this.imageAltText = imageAltText;
	}

	public String getXmlKeywords() {
		return xmlKeywords;
	}

	public void setXmlKeywords(String xmlKeywords) {
		this.xmlKeywords = xmlKeywords;
	}

	public void updateDateLastModified(Date dateLastModified) {
		if (dateLastModified == null) {
			updateDateLastModified();
		} else if (this.dateLastModified == null || this.dateLastModified.before(dateLastModified)) {
			this.dateLastModified = dateLastModified;
		}
		// otherwise, the date last modifieds are inconsistent!
		// this.dateLastModified = dateLastModified;
	}
	
	public String getFirstExternalId(){
		Iterator<ExternalLinkObject> links = getExternalLinks().iterator();
		while (links.hasNext()){
			ExternalLinkObject link = links.next();
			String extId = link.getExternalId();
			if (extId!=null && extId.length()>0) return extId;
		}
		return null;
	}

	public String getHostServer() {
		return hostServer;
	}

	public void setHostServer(String hostServer) {
		this.hostServer = hostServer;
	}

	public String getUuidString() {
		return uuidString;
	}

	public void setUuidString(String uuidString) {
		this.uuidString = uuidString;
	}
}
