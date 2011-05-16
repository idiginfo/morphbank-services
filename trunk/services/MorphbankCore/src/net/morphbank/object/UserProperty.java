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
@Table(name = "UserProperty")
public class UserProperty implements IdObject {
	static final long serialVersionUID = 1;
	public static final int MAX_TITLE_LENGTH = 25;

	public String getObjectTypeIdStr(){
		return "UserProperty";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;// autoincrement id
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "objectId")
	private BaseObject object;

	private String name;
	private String value;
	//@Transient //TODO make persistent when db is updated
	private String namespaceURI;

	// int collectionId;
	// int objectId;

	public UserProperty() {
	}

	public UserProperty(BaseObject obj, String name, String value,
			String namespaceURI) {
		this.object = obj;
		if (name != null) {
			this.name = name.trim();
		}
		if (value != null) {
			this.value = value.trim();
		}
		if (namespaceURI != null) {
			this.namespaceURI = namespaceURI.trim();
		}
	}

	public UserProperty(BaseObject obj, String name, String value) {
		this(obj, name, value, null);
	}

	public UserProperty(String name, String value) {
		this(null, name, value, null);
	}

	public boolean persist() {
		if (!validate()) {
			return false;
		}
		try {
			MorphbankConfig.getEntityManager().persist(this);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validate() {
		if (name == null && name.length() == 0) {
			return false; // empty name
		} else if (this.object.getUserProperties().get(this.name) != null) {
			return false; // duplicate field name
		}
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BaseObject getObject() {
		return object;
	}

	public void setObject(BaseObject object) {
		this.object = object;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}
}
