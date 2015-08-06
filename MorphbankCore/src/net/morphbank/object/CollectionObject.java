/**
 * 
 */
package net.morphbank.object;

import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "CollectionObjects")
public class CollectionObject { // implements IdObject {
	static final long serialVersionUID = 1;
	public static final int MAX_TITLE_LENGTH = 25;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int identifier;// autoincrement id
	@ManyToOne
	@JoinColumn(name = "objectId")
	private BaseObject object;
	private int objectOrder;
	private String objectTypeId;
	private String objectTitle;
	private String objectRole;
	@ManyToOne
	@JoinColumn(name = "collectionId")
	private BaseObject collection;

	// int collectionId;
	// int objectId;

	public CollectionObject() {
		super();
	}

	public CollectionObject(BaseObject coll, BaseObject obj) {
		this();
		setObject(obj);
		setCollection(coll);
		setObjectTypeId(obj.getObjectTypeIdStr());
		setObjectOrder(0);
	}

	public static int getLastIndex(BaseObject obj, String role) {
		StringBuffer qStr = new StringBuffer(
				"select max(c.objectOrder) from CollectionObject c where c.collection = :obj");
		if (role!=null){
			qStr.append(" and c.objectRole = :role");
		}
		Query query = MorphbankConfig.getEntityManager().createQuery(qStr.toString());
		query.setParameter("obj", obj);
		if (role!=null){
			query.setParameter("role", role);
		}
		Object result = query.getSingleResult();
		if (result == null) {
			return 0;
		}
		return 0;
	}

	public static int getLastIndex(BaseObject obj) {
		return getLastIndex(obj,null);
	}

	public void persist() {
		MorphbankConfig.getEntityManager().persist(this);
	}

	// modified id ala MB 2.8 GAR 12/19/2007

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	// | myCollectionid | objectId | objectOrder | objectTypeId | objectTitle |
	// startSubCollection | parentId |

	public int getObjectOrder() {
		return objectOrder;
	}

	public void setObjectOrder(int index) {
		this.objectOrder = index;
	}

	public String getObjectRole() {
		return objectRole;
	}

	public void setObjectRole(String objectRole) {
		this.objectRole = objectRole;
	}

	public BaseObject getCollection() {
		return collection;
	}

	public void setCollection(BaseObject collection) {
		this.collection = collection;
	}

	public BaseObject getObject() {
		return object;
	}

	public void setObject(BaseObject object) {
		this.object = object;
	}

	public String getObjectTitle() {
		return objectTitle;
	}

	public void setObjectTitle(String objectTitle) {
		if (objectTitle == null || objectTitle.length() <= MAX_TITLE_LENGTH) {
			this.objectTitle = objectTitle;
		} else {
			this.objectTitle = objectTitle.substring(0, MAX_TITLE_LENGTH - 1);
		}
	}

	public String getObjectTypeId() {
		return objectTypeId;
	}

	private void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	// Publication publication;
}
