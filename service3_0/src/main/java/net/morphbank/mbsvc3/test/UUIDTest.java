package net.morphbank.mbsvc3.test;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.request.UUIDServices;
import net.morphbank.object.BaseObject;
import net.morphbank.object.ExternalLinkObject;

public class UUIDTest {

	// public static final String PERSISTENCE =
	// MorphbankConfig.PERSISTENCE_LOCALHOST;

	// public static final String PERSISTENCE =
	// MorphbankConfig.PERSISTENCE_MBDEV;

	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBPROD;

	public static void main(String[] args) {
		UUIDTest tester = new UUIDTest();
		tester.run(args);
	}

	UUIDServices uuidServices = new UUIDServices();

	public void run(String[] args) {
		MorphbankConfig.setPersistenceUnit(PERSISTENCE);
		MorphbankConfig.init();
		// testUUID();
		// addUUIDs();
		uuidServices.fixAllMissingUUIDs();
		uuidServices.fixAllMissingIds();
	}

	public void testUUID() {
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid.toString() + " is version " + uuid.version());
	}

	public void addUUIDs() {
		EntityTransaction tx = null;
		boolean localTransaction = false;
		try {
			EntityManager em = MorphbankConfig.getEntityManager();

			String uuidNullQuerySql = "select b from BaseObject b where b.objectTypeId='specimen' and b.uuidString is null";
			Query uuidNullQuery = em.createQuery(uuidNullQuerySql);
			List<BaseObject> results = uuidNullQuery.getResultList();
			BaseObject obj = results.get(0);
			System.out.println("First result " + obj.getId()
					+ " has uuidString " + obj.getUuidString());
			uuidServices.fixMissingUUID(obj);
			fixMissingId(obj);
			List<String> ids = ExternalLinkObject.getIdentifiers(obj);
			System.out.println("Obj " + obj.getId() + " has id " + ids.get(0));
			// fix first instance
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean fixMissingId(BaseObject obj) {
		EntityTransaction tx = null;
		boolean localTransaction = false;
		ExternalLinkObject link = null;
		String externalId = null;
		try {
			EntityManager em = MorphbankConfig.getEntityManager();

			tx = em.getTransaction();
			if (!tx.isActive()) {
				tx.begin();
				localTransaction = true;
			}
			if (ExternalLinkObject.getIdentifiers(obj) != null) {
				return true;
			}
			// no identifiers: add identifier
			externalId = "urn:uuid:" + obj.getUuidString();
			link = new ExternalLinkObject(obj, externalId,
					"External Unique Reference");
			link.setDescription(MorphbankConfig.DCTERMS_IDENTIFIER);

			System.out.println("Object " + obj.getId() + " has id "
					+ link.getExternalId());
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
