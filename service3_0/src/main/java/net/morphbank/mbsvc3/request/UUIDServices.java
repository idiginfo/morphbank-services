package net.morphbank.mbsvc3.request;

import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.ExternalLinkObject;

public class UUIDServices {
	PrintStream out = null;
	public UUIDServices()
	{
		this.out = System.out; 
	}
	public UUIDServices(PrintStream out)
	{
		this.out = out; 
	}
	public int fixAllMissingUUIDs() {
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
			String uuidNullQuerySql = "select b from BaseObject b where b.uuidString is null or b.uuidString=''";
			Query uuidNullQuery = em.createQuery(uuidNullQuerySql);
			@SuppressWarnings("unchecked")
			List<BaseObject> results = uuidNullQuery.getResultList();
			out.println("<div id=\"missingUUID\"><h3>Number to have UUIDs added: " + results.size()+"</h1>");
			boolean bfixed = false;
			for (BaseObject obj : results) {
				bfixed = fixMissingUUID(obj);
				if (bfixed)
					count++;
				// if (count > 10)
				// break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localTransaction && tx.isActive()) {
				tx.commit();
			}

			out.println("<h3>number with new UUIDs: " + count+"</h3></div>");
		}
		return count;
	}
	
	

	public int fixAllMissingIds() {
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
			out.println("<div id=\"missingID\"><h3>Number to have ids added: " + ids.size()+"</h3><p>");
			count = 0;
			for (Integer id : ids) {
				// no identifiers: add identifier
				BaseObject obj = em.find(BaseObject.class, id);
				boolean success = ExternalLinkObject.addDctermsIdentifier(obj,out);
				if (success) count++;
				// if (count > 10)
				// break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (localTransaction && tx.isActive()) {
				tx.commit();
			}
			out.println("</p><h3>number with new ids: " + count+"</h3></div>");
		}
		return count;
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
			out.println("Object " + obj.getId() + " uuidString "
					+ obj.getUuidString()+"<br>");
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
