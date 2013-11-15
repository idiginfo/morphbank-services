/**
 * 
 */
package net.morphbank.object;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "User")
@DiscriminatorValue("User")
// @AttributeOverride(column = @Column(name = "name"), name = "name")
// @XStreamConverter(net.morphbank.object.converter.UserConverter.class)
public class User extends BaseObject implements Serializable {
	static final long serialVersionUID = 1;

	// private int privilegeTSN;
	private String uin;
	private String pin;
	@Column(name = "name")
	private String userName;
	private String email;
	private String affiliation;
	private String address;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "first_name")
	private String firstName;
	private String suffix;
	@Column(name = "middle_init")
	private String middleInit;
	private String street1;
	private String street2;
	private String city;
	private String country;
	private String state;
	private String zipcode;
	private Integer status;
	// String primarytsn;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "privilegeTSN")
	private Taxon privilegeTaxon;
	// String secondarytsn;
	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "secondarytsn")
	// private Taxon secondaryTaxon;
	private Boolean isContributor;
	private String preferredServer;
	private String preferredGroup;
	private String userLogo;
	private String logoURL;
	public static final String USER_LOGO_BASE_URL = "http://www.morphbank.net/images/userLogos/";

	@OneToMany(mappedBy = "imageUser", fetch = FetchType.LAZY)
	// @JoinColumn(name="userId", table="BaseObject")
	private Set<Image> images;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "UserGroup", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "groups"))
	private Set<Group> groups;

	// Set<Specimen> specimens;

	public User() {
		super();
	}

	public User(int id, String name, User owner, User submitter, Group group,
			String userName, String uin, String pin) {
		super(id, name, owner, submitter, group);
		pin = "";
		this.userName = userName;
		this.uin = uin;
		this.pin = pin;
	}

	public static final String USER_QUERY = "select u from User u where u.uin=:uin";
	public static final String LAST_FIRST_QUERY = "select u from User u where u.lastName=:lastName and u.firstName=:firstName";
	public static final String NAME_QUERY = "select u from User u where u.name=:name";

	public static User getUserByUserId(String userId) {
		if (userId == null || userId.length() == 0)
			return null;
		User user = getUserByUIN(userId);
		if (user != null)
			return user;
		int objectId;
		try {
			objectId = Integer.parseInt(userId);
			BaseObject obj = BaseObject.getEJB3Object(objectId);
			if (obj instanceof User)
				return (User) obj;
		} catch (Exception e) {
		}
		return null;
	}

	public static User getUserByUIN(String uin) {
		if (uin == null)
			return null;
		EntityManager em = MorphbankConfig.getEntityManager();
		Query query = em.createQuery(USER_QUERY);
		query.setParameter("uin", uin);
		try {
			Object obj =  query.getSingleResult();
			return (User) obj;
		} catch (Exception e) {
			return null;
		}
	}

	public static User getUserByFirstLast(String name) {
		if (name == null)
			return null;
		String[] names = name.split(" ");
		if (names.length != 2)
			return null;
		EntityManager em = MorphbankConfig.getEntityManager();
		
		Query query = em.createQuery(LAST_FIRST_QUERY);
		query.setParameter("lastName", names[1]);
		query.setParameter("firstName", names[0]);
		
		try {
			
			List results =  query.getResultList();
			if (results.size() == 1)
				return (User) results.get(0);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}

	public String getStatus() {
		if (status != null && status == 1)
			return "true";
		return "false";
	}

	public void setStatus(String accountstatus) {
		if ("true".equals(accountstatus))
			this.status = 1;
		else
			this.status = 0;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String first_Name) {
		this.firstName = first_Name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String last_Name) {
		this.lastName = last_Name;
	}

	public String getMiddleInit() {
		return middleInit;
	}

	public void setMiddleInit(String middleInit) {
		this.middleInit = middleInit;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	// public int getPrivilegeTSN() {
	// return privilegeTSN;
	// }
	//
	// public void setPrivilegeTSN(int privilegeTSN) {
	// this.privilegeTSN = privilegeTSN;
	// }

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public Taxon getPrivilegeTaxon() {
		return privilegeTaxon;
	}

	public void setPrivilegeTaxon(Taxon privilegeTaxon) {
		this.privilegeTaxon = privilegeTaxon;
	}

	// public Taxon getSecondaryTaxon() {
	// return secondaryTaxon;
	// }
	//
	// public void setSecondaryTaxon(Taxon secondaryTaxon) {
	// this.secondaryTaxon = secondaryTaxon;
	// }

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public Boolean getIsContributor() {
		return isContributor;
	}

	public void setIsContributor(Boolean isContributor) {
		this.isContributor = isContributor;
	}

	public String getPreferredServer() {
		return preferredServer;
	}

	public void setPreferredServer(String preferredServer) {
		this.preferredServer = preferredServer;
	}

	public String getPreferredGroup() {
		return preferredGroup;
	}

	public void setPreferredGroup(String preferredGroup) {
		this.preferredGroup = preferredGroup;
	}

	public String getUserLogo() {
		return userLogo;
	}

	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public static String getUSER_LOGO_BASE_URL() {
		return USER_LOGO_BASE_URL;
	}

	public Set<Group> getGroups() {
		return groups;
	}
}
