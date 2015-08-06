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
@Table(name = "FlickrUserInfo")
public class FlickrUserInfo  {
	static final long serialVersionUID = 1;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;// autoincrement id
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "ownerId")
	private BaseObject owner;

	private String apiKey;
	private String secret;
	private String token;

	public FlickrUserInfo() {
	}

	public FlickrUserInfo(BaseObject owner, String apiKey, String secret,
			String token) {
		this.owner = owner;
		if (apiKey != null) {
			this.apiKey = apiKey.trim();
		}
		if (secret != null) {
			this.secret = secret.trim();
		}
		if (token != null) {
			this.token = token.trim();
		}
	}

	public FlickrUserInfo(BaseObject obj, String apiKey, String secret) {
		this(obj, apiKey, secret, null);
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
		return true;
	}

	public int getId() {
		return id;
	}

	public BaseObject getOwner() {
		return owner;
	}

	public void setOwner(BaseObject owner) {
		this.owner = owner;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
