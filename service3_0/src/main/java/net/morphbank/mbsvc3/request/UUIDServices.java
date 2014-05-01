package net.morphbank.mbsvc3.request;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.ExternalLinkObject;

public class UUIDServices {

	public void fixAllMissingUUIDs() {
		EntityTransaction tx = null;
		boolean localTransaction = false;
		int count = 0;
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			tx = em.getTransaction();
			if (!tx.isActive()) {
				tx.begin();
				localTransaction = true;
			}

			// find and fix all missing UUIDs
			String uuidNullQuerySql = "select b from BaseObject b where b.uuidString is null";
			Query uuidNullQuery = em.createQuery(uuidNullQuerySql);
			@SuppressWarnings("unchecked")
			List<BaseObject> results = uuidNullQuery.getResultList();
			System.out.println("Number to have UUIDs added: " + results.size());
			count = 0;
			for (BaseObject obj : results) {
				fixMissingUUID(obj);
				count++;
//				if (count > 10)
//					break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localTransaction && tx.isActive()) {
				tx.commit();
			}

			System.out.println("number with new UUIDs: " + count);
		}
	}

	public void fixAllMissingIds() {
		EntityTransaction tx = null;
		boolean localTransaction = false;
		int count = 0;
		try {
			EntityManager em = MorphbankConfig.getEntityManager();
			tx = em.getTransaction();
			if (!tx.isActive()) {
				tx.begin();
				localTransaction = true;
			}

			// find and fix all missing ids
			String NoIdQuerySql = "select id from BaseObject where id not in "
					+ "(select  e.mbId from ExternalLinkObject e where e.description = '"
					+ MorphbankConfig.DCTERMS_IDENTIFIER + "')";
			Query noIdQuery = em.createNativeQuery(NoIdQuerySql);
			@SuppressWarnings("unchecked")
			List<Integer> ids = noIdQuery.getResultList();
			System.out.println("Number to have ids added: " + ids.size());
			count = 0;
			for (Integer id : ids) {
				// no identifiers: add identifier
				BaseObject obj = em.find(BaseObject.class, id);
				boolean success = ExternalLinkObject.addDctermsIdentifier(obj);
				if (success) count++;
//				if (count > 10)
//					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			if (localTransaction && tx.isActive()) {
				tx.commit();
			}
			System.out.println("number with new ids: " + count);

		}
	}

	public boolean fixMissingUUID(BaseObject obj) {
		EntityTransaction tx = null;
		boolean localTransaction = false;
		try {
			EntityManager em = MorphbankConfig.getEntityManager();

			tx = em.getTransaction();
			if (!tx.isActive()) {
				tx.begin();
				localTransaction = true;
			}
			obj.setUuidString(UUID.randomUUID().toString());
			System.out.println("Object " + obj.getId() + " uuidString "
					+ obj.getUuidString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (localTransaction && tx.isActive()) {
				tx.commit();
			}
		}
	}

}
