/**
 * 
 */
package net.morphbank.object;

import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "Groups")
@DiscriminatorValue("Groups")
public class Group extends BaseObject {
	static final long serialVersionUID = 1;

	private String groupName;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tsn")
	private Taxon taxon;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "groupManagerId")
	private User groupManager;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "groups")
	Set<User> users;
	
	
	public Group() {
		super();
	}

	public Group(int id, String name, User owner, User submitter, Group group) {
		super(id, name, owner, submitter, group);
	}

	public static final String GROUP_QUERY = "select g from Group g where g.groupName = :groupName";

	public static Group getGroupByGroupId(String groupId) {
		if (groupId == null) return null;
		Group group = getGroupByName(groupId);
		if (group != null) return group;
		int objectId;
		try {
			objectId = Integer.parseInt(groupId);
			BaseObject obj = BaseObject.getEJB3Object(objectId);
			if (obj instanceof Group) return (Group) obj;
		} catch (Exception e) {
		}
		return null;
	}

	public static Group getGroupByName(String groupName) {
		if (groupName == null) return null;
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			Query query = em.createQuery(GROUP_QUERY);
			query.setParameter("groupName", groupName);
			return (Group) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	public User getGroupManager() {
		return groupManager;
	}

	public void setGroupManager(User groupManager) {
		this.groupManager = groupManager;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
	
	public Set<User> getUsers() {
		return users;
	}
}
