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
@Table(name = "UserGroupKey")
public class UserGroupKey { // implements IdObject {
	static final long serialVersionUID = 1;
	@Id
	private String keyString;
	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;
	@ManyToOne
	@JoinColumn(name = "groupId")
	private Group group;

	public UserGroupKey(User user, Group group) {
		this.user = user;
		this.group = group;
		this.keyString = newKey(user, group);
	}

	public static UserGroupKey getUserGroupKey(String key) {
		EntityManager em = MorphbankConfig.getEntityManager();
		UserGroupKey ugk = (UserGroupKey) em.find(UserGroupKey.class, key);
		return ugk;
	}

	public boolean persist() {
		boolean createTransaction = false;
		try {
			EntityTransaction tx = MorphbankConfig.getEntityManager()
					.getTransaction();
			if (tx == null) {// no transaction available, unknown reason
				return false;
			} else if (!tx.isActive()) {
				createTransaction = true;
				tx.begin();
			}
			MorphbankConfig.getEntityManager().persist(this);
			MorphbankConfig.getEntityManager().flush();
			if (createTransaction) {// method created its own transaction
				tx.commit();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static String getPassword(int n) {
		char[] pw = new char[n];
		int c = 'A';
		int r1 = 0;
		for (int i = 0; i < n; i++) {
			r1 = (int) (Math.random() * 3);
			switch (r1) {
			case 0:
				c = '0' + (int) (Math.random() * 10);
				break;
			case 1:
				c = 'a' + (int) (Math.random() * 26);
				break;
			case 2:
				c = 'A' + (int) (Math.random() * 26);
				break;
			}
			pw[i] = (char) c;
		}
		return new String(pw);
	}

	static String newKey(User user, Group group) {
		return getPassword(10);
	}

	public UserGroupKey() {
		super();
	}

	public String getKey() {
		return keyString;
	}

	public void setKey(String keyString) {
		this.keyString = keyString;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
