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
@Table(name = "ExternalLinkType")
public class ExternalLinkType { // implements IdObject {
	static final long serialVersionUID = 1;

	@Id
	private int linkTypeId;// autoincrement id
	private String name;
	private String description;

	public ExternalLinkType() {
	}

	public static int getExternalTypeId(String linkType) {
		if (linkType == null) return -1;
		try {
			int typeId = Integer.parseInt(linkType);
			return typeId;
		} catch (Exception e) {
			// linktype is not an int
		}
		EntityManager em = MorphbankConfig.getEntityManager();
		try {
			Query query = em.createQuery("select e from ExternalLinkType e where e.name=:name");
			query.setParameter("name", linkType);
			ExternalLinkType extLinkType = (ExternalLinkType) query.getSingleResult();
			return extLinkType.linkTypeId;
		} catch (Exception e) {
			//e.printStackTrace();
			// no match for link type
		}
		return -1;
	}

	public static String getExternalType(int linkTypeId) {
		EntityManager em = MorphbankConfig.getEntityManager();
		try {
			Query query = em.createQuery("select e from ExternalLinkType e where e.linkTypeId=:id");
			query.setParameter("id", linkTypeId);
			ExternalLinkType extLinkType = (ExternalLinkType) query.getSingleResult();
			return extLinkType.name;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
