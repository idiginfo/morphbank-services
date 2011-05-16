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
package net.morphbank.mbsvc3.test;

import java.io.PrintWriter;

import javax.persistence.EntityTransaction;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.sharing.UpdateRemote;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlUtils;

public class SharingTest {

	static String REMOTE_SERVICE = "http://services.morphbank.net/mbd";
	// static String MB_SERVICES_CHANGES =
	// "http://services.morphbank.net/mbd/request?method=search&keywords=geranium&objecttype=Specimen&format=svc&numChangeDays=";
	static String OUT_FILE = "/usr/local/dev/morphbank/changeresult.xml";
	static String REQ_FILE = "/usr/local/dev/morphbank/changes.xml";

	public static void main(String[] args) throws Exception {
		SharingTest tester = new SharingTest();
		//tester.runOne();
		tester.run(args);
		//tester.fixLinks();
	}

	public void runOne() {
		String persistence = MorphbankConfig.PERSISTENCE_LOCALHOST;
		MorphbankConfig.setPersistenceUnit(persistence);
		MorphbankConfig.init();

		UpdateRemote updateRemote = new UpdateRemote(REMOTE_SERVICE);
//		int id = 552830;
		int id = 581513;
		Response resp = updateRemote.updateObject(id);
		XmlUtils.printXml(resp);
		EntityTransaction tx = MorphbankConfig.getEntityManager().getTransaction();
		if (tx.isActive()) tx.commit();
	}
	
	public void fixLinks(){
		String persistence = MorphbankConfig.PERSISTENCE_MBDEV;
		MorphbankConfig.setPersistenceUnit(persistence);
		MorphbankConfig.init();
		UpdateRemote updateRemote = new UpdateRemote(REMOTE_SERVICE);
		int numFixed = updateRemote.fixLinks();
		System.out.println("Number of links fixed: "+numFixed);
	}

	public void run(String[] args) throws Exception {
		String persistence = MorphbankConfig.PERSISTENCE_LOCALHOST;
		if (args.length > 0) persistence = args[0];
		int numDays = 10;
		if (args.length > 1) numDays = Integer.valueOf(args[1]);
		int limit = 100;
		if (args.length > 2) limit = Integer.valueOf(args[2]);

		//extra params
		String[] objectTypes = {"Specimen", "Image"};
//		String[] objectTypes = null;
		int user = 2;
		int group = 1;
		String[] keywords = {"Alaska", "adult"};
		
		MorphbankConfig.setPersistenceUnit(persistence);
		MorphbankConfig.init();
		PrintWriter requestOut = new PrintWriter(REQ_FILE);
		UpdateRemote updateRemote = new UpdateRemote(REMOTE_SERVICE);
//		updateRemote.run(numDays, limit, requestOut);
		updateRemote.run(numDays, limit, requestOut, objectTypes, user, group, keywords);
		requestOut.close();
	}
}
