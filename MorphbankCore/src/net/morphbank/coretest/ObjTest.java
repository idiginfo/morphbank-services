/*******************************************************************************
 * Copyright (c) 2011 Greg Riccardi, Guillaume Jimenez.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  	Greg Riccardi - initial API and implementation
 * 	Guillaume Jimenez - initial API and implementation
 ******************************************************************************/
package net.morphbank.coretest;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;
import java.text.*;

public class ObjTest {

	public static final String[] EXT_IDS = { "Chicago Bo51365", "XXXX", "PlantColle51372",
			"Dentary im51431" };
	public static final String KEY = "testfield2";
	public static final String VALUE = "testvalue1";

	public static void main(String[] args) {
		MorphbankConfig.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
		// .setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBPROD);
		// .setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBDEV);
		MorphbankConfig.init();
		ObjTest test = new ObjTest();
		test.run();
	}

	public void run() {
		//runLocalityTest(475363); // locality
		// runTest(1114); // user id for Fredrik
		// registerGR();
		// getGRByKey();
		List users = getUsers("Ronquist");
		// runTest(136023); // id for a taxon concept
		// runCollectionTest(140908); // id for a matrix
		// addImage();
		// printMatrix(148032);
		// printMatrix(148337);
		// printMatrix(148747);
		// printMatrix(143595); // MB 28 on .5
		// runExtIdTest();
		// runExtIdObjTest();
		// runExtIdBaseoObjectTest();
		// runTaxonTest(999008405);
		// runTaxonTest(999008406);
		System.out.println("end of tests");

	}

	public void registerGR() {
		User gr = User.getUserByUIN("griccardi");
		Group admin = Group.getGroupByName("Morphbank Administration");
		UserGroupKey ugk = new UserGroupKey(gr, admin);
		System.out.println("key for GR '" + ugk.getKey() + "'");
		boolean persist = ugk.persist();
		System.out.println("persist: " + persist);
	}

	public void getGRByKey() {
		String keyString = "5G9oBtYYb9";
		UserGroupKey ugk = UserGroupKey.getUserGroupKey(keyString);
		System.out.println("User is: " + ugk.getUser().getUin());
		System.out.println("Group is: " + ugk.getGroup().getGroupName());
	}

	public void printMatrix(int id) {
		Matrix matrix = (Matrix) BaseObject.getEJB3Object(id);
		matrix.print(System.out);
	}

	public void addImage() {
		System.out.println("add a new image");
		EntityTransaction tx = MorphbankConfig.getEntityManager().getTransaction();

		User fred = (User) BaseObject.getEJB3Object(1);
		View view = (View) BaseObject.getEJB3Object(101);
		Specimen specimen = (Specimen) BaseObject.getEJB3Object(105);
		Image image = new Image(0, fred, fred, fred.getGroup(), specimen, view, "JPG",
				"copyright@2007 Fred", 100);

		image.persist();
		// MorphbankConfig.getSession().save(image);
		System.out.println("XmlImage id: " + image.getId());

		if (tx.isActive()) {
			tx.commit();
		}
	}

	public List getUsers(String lastName) {
		List users = MorphbankConfig.getEntityManager().createQuery("Select u from User u where u.id=77420")
				.getResultList();
		// "from User where last_name='ronquist'").list();
		return users;
	}

	public void runTest(int localId) {
		System.out.println("***runTest: " + localId);
		BaseObject obj;
		obj = BaseObject.getEJB3Object(localId);

		addField(obj, KEY, VALUE);
		if (obj != null) {// is a base object
			runObjectTest(obj);
			return;
		}
	}

	public void runLocalityTest(int localId) {
		System.out.println("***runLocalityTest: " + localId);
		Locality obj;
		obj = (Locality) BaseObject.getEJB3Object(localId);
		DecimalFormat formatter = new DecimalFormat("0000.0000000");
		// System.out.println("latitude: "+formatter.format(obj.getLatitude()));
		// System.out.println("longitude: "+formatter.format(obj.getLongitude()));
		System.out.println("locality: " + obj.getLocality());
		// System.out.println(obj.getLatitude());
		User user = obj.getUser();
		System.out.println("user " + user.getId() + " name: " + user.getFirstName() + " "
				+ user.getLastName());
	}

	public void addField(BaseObject obj, String name, String value) {
		// open transaction
		EntityManager em = MorphbankConfig.getEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		UserProperty field = new UserProperty(obj, KEY, VALUE);
		obj.addUserProperty(field);
		if (!field.validate()) {
			System.out.println("can't save field " + KEY);
			et.rollback();
		} else {
			em.persist(field);
			et.commit();
		}
		System.out.println("Testing fields: value of " + KEY + " is: "
				+ obj.getUserProperties().get(KEY).getValue());
		// close transaction
	}

	public void runCollectionTest(int localId) {
		BaseObject obj;
		obj = BaseObject.getEJB3Object(localId);
		if (obj != null) {
			System.out.println("object: " + obj.toString());
			List objects = obj.getObjects();
			if (objects != null) {
				System.out.println("number related objects: " + objects.size());
			}
		}
	}

	public void runObjectTest(BaseObject obj) {
		if (obj != null) {
			System.out.println(obj);
			System.out.println("Id: " + obj.getId());
			System.out.println("Name: " + obj.getName());
			UserProperty field = obj.getUserProperties().get(KEY);
			if (obj instanceof Specimen) {
				Specimen specimen = (Specimen) obj;
				Locality locality = specimen.getLocality();
				System.out.println("Country: " + locality.getCountry());
				System.out.println("Continent: " + locality.getContinent());
				System.out.println("Ocean: " + locality.getOcean());
				System.out.println("StateProvince: " + locality.getStateProvince());

			}
			System.out.println("KEY Field " + field.getName() + " has value " + field.getValue());
			Iterator<Map.Entry<String, UserProperty>> fields = obj.getUserProperties().entrySet()
					.iterator();
			UserProperty field2 = obj.getUserProperties().get(KEY + "X");
			System.out.println("NON_KEY Field " + KEY + "X is:" + field2);

			while (fields.hasNext()) {
				Map.Entry<String, UserProperty> entry = fields.next();
				System.out.println("Field " + entry.getKey() + " has value "
						+ entry.getValue().getValue());
			}
		} else {
			System.out.println("runObjectTest: null object");
		}
	}

	public void runExtIdTest() {
		for (int i = 0; i < EXT_IDS.length; i++) {
			int id = ExternalLinkObject.searchExternalId(EXT_IDS[i]);
			System.out.print("The object with external id '" + EXT_IDS[i]);
			if (id == 0) { // not found
				System.out.println("' not found");
			} else {
				System.out.println("' is " + id);
			}
		}
	}

	public void runExtIdObjTest() {
		for (int i = 0; i < EXT_IDS.length; i++) {
			ExternalLinkObject obj = ExternalLinkObject.getExternalLinkObject(EXT_IDS[i]);
			System.out.print("The object with external id '" + EXT_IDS[i]);
			if (obj == null) {
				System.out.println("' not found");
			} else {
				System.out.print("' is " + obj.getObject().getId());
				System.out.println(" with linkId " + obj.getLinkId());
			}
		}
	}

	public void runExtIdBaseoObjectTest() {
		for (int i = 0; i < EXT_IDS.length; i++) {
			BaseObject obj = BaseObject.getObjectByExternalId(EXT_IDS[i]);
			System.out.print("The base object with external id '" + EXT_IDS[i]);
			if (obj == null) {
				System.out.println("' not found");
			} else {
				System.out.print("' is " + obj.getId());
				System.out.println(" with linkId " + obj.getExternalLinks().get(0).getLinkId());
			}
		}
	}

	public void runTaxonTest(int taxonId) {
		EntityManager em = MorphbankConfig.getEntityManager();
		Taxon taxon = em.find(Taxon.class, taxonId);
		System.out.println("Taxon sci name: " + taxon.getScientificName());
		System.out.println("Taxon author name: " + taxon.getTaxonAuthorName());
	}
}
