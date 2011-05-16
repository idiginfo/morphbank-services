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
package net.morphbank.rdftest;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.CollectionObject;
import net.morphbank.object.Group;
import net.morphbank.object.IdObject;
import net.morphbank.object.Locality;
import net.morphbank.object.Taxon;
import net.morphbank.object.TaxonBranchNode;
import net.morphbank.rdf.RdfBase;
import net.morphbank.rdf.RdfOntologies;

;
public class RdfTest {
	/**
	 * @param args
	 * 
	 */

	static final int DETAIL_LEVEL = 1;

	public static void main(String[] args) {
		MorphbankConfig.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
		MorphbankConfig.init();
		RdfTest test = new RdfTest();
		test.run();
	}

	public Class[] classes = { Taxon.class, TaxonBranchNode.class,
			CollectionObject.class };
	public int[] tests = {136067  };/*
									 * , 136011 // 0 image , 66008 // 1 image ,
									 * 8 // 2 group (upper rank) , 64041 // 3
									 * specimen , 999000061 // 4 taxon (local) ,
									 * 117232 // 5 taxon (ITIS) , 77590 // 6
									 * mycollection // , 78055 // 7
									 * mycollectionobject // , 78103 // 8
									 * DeterminationAnnotation , 78118 // 9
									 * Different DeterminationAnnotation
									 * 195782 taxon }
									 */;

	public void runObjectTest(IdObject obj) {
		runObjectTest(obj, DETAIL_LEVEL);
	}

	public void runObjectTest(IdObject obj, int detailLevel) {
		if (obj != null) {
			RdfBase rdfObj = RdfBase.createRdfObject(obj);
			rdfObj.toDetailRDF(detailLevel);
			RdfOntologies.getOntModel().write(System.out, "RDF/XML");
		} else {
			System.out.println("runObjectTest: null object");
		}
	}

	public void runTest(int localId) {
		runTest(localId, DETAIL_LEVEL);
	}

	@SuppressWarnings("unchecked")
	public void runTest(int localId, int detailLevel) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@runTest: "
				+ localId);
		IdObject obj;
		obj = BaseObject.getEJB3Object(localId);
		if (obj != null) {// is a base object
			runObjectTest(obj);
			return;
		}
		System.out.println("Not a base object: " + localId);
		// try some classes
		for (int i = 0; i < classes.length; i++) {
			// not a base object, try Taxon
			obj = (IdObject) MorphbankConfig.getEntityManager().find(
					classes[i], Integer.toString(localId));
			if (obj != null) { // is a taxon
				runObjectTest(obj);
				return;
			}
		}
		System.out.println("No class found for: " + localId);
	}

	public void runTestLocality() {
		String localId = "64041";
		System.out.println("run test locality for " + localId);
		Locality lcl = new Locality();
		lcl = (Locality) MorphbankConfig.getEntityManager().find(
				Locality.class, localId);
		runObjectTest(lcl);
	}

	public void runTestGroup() {
		String localId = "8";
		System.out.println("run test group for " + localId);
		Group grp;
		grp = (Group) MorphbankConfig.getEntityManager().find(Group.class,
				localId);
		runObjectTest(grp);
	}

	String[] tsns = { "999000061", "117232" };

	public void runTestTaxon() {
		for (int i = 0; i < tsns.length; i++) {
			try {
				String tsn = tsns[i];
				System.out.println("run test Taxon for " + tsn);
				Taxon taxon = new Taxon();
				taxon = (Taxon) MorphbankConfig.getEntityManager().find(
						Taxon.class, tsn);
				runObjectTest(taxon);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		// runTestGroup();
		// runTestTaxon();
		// runTestLocality();
		for (int i = 0; i < tests.length; i++) {
			runTest(tests[i]);
		}
		System.out.println("end of tests");
	}
}
