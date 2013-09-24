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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.MapObjectToResponse;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.xml.RequestSummary;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.object.CollectionObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Image;
import net.morphbank.object.Specimen;

public class ResponseTest {

	static final String REQ_PKG = "net.morphbank.mbservices";
	static final String PRINT_FILE = "C:/dev/morphbank/requestDump.xml";
	static final int[] OBJ_IDS = { 464032  }; // 136061 136061, 77657 , 139915, 69632, 73700 };
	int objCount = 0;
	MapObjectToResponse mapper = null;
	Response response = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MorphbankConfig
				.setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBDEV);
		MorphbankConfig.init();
		ResponseTest responseTest = new ResponseTest();
		responseTest.run();
	}

	public void run() {
		mapper = new MapObjectToResponse("Query", "Test of specimen and it's images");
		response = mapper.getResponse();
		objCount = 0;
		BaseObject obj = null;
		RequestSummary summary = response.getRequestSummary();
		StringBuffer desc = new StringBuffer(
				"Query for information about ids: ");
		for (int i = 0; i < OBJ_IDS.length; i++) {
			desc.append(OBJ_IDS[i]).append(" ");
			obj = BaseObject.getEJB3Object(OBJ_IDS[i]);
			dumpObject(obj);
		}
		summary.setDescription(desc.toString());
		summary.setRequestType("query");
		response.setStatus("complete");		
		response.setNumberAffected(objCount);
		mapper.setSubmitter(obj);
		// prepare document for printing
		Responses responses = new Responses();
		responses.getResponse().add(response);
		XmlUtils.printXml(responses);
		try {
			File file = new File(PRINT_FILE);
			PrintWriter out = new PrintWriter(file);
			XmlUtils.printXml(out, responses);
			out.close();			
		} catch (IOException e){
			System.out.println("Can't print to file");
			e.printStackTrace();
		}
	}

	public void dumpObject(IdObject obj) {
		dumpObject(obj, true);
	}

	public void dumpObject(IdObject obj, boolean dumpRelated) {
		mapper.addObject(obj);
		objCount++;
		if (obj instanceof Image) {
			Image image = (Image) obj;
			mapper.addObject(image.getSpecimen());
			objCount++;
		} else if (obj instanceof Specimen) {
			Specimen specimen = (Specimen) obj;
			Iterator<Image> images = specimen.getImages().iterator();
			while (images.hasNext()) {
				Image specImage = images.next();
				mapper.addObject(specImage);
				objCount++;
			}
		} else if (obj instanceof Collection) {
			// TODO
			Collection collection = (Collection) obj;
			Iterator<CollectionObject> collObjects = collection.getObjects()
					.iterator();
			while (collObjects.hasNext()) {
				CollectionObject collObject = collObjects.next();
				dumpObject(collObject.getObject(), false);
			}
		}
		if (obj instanceof BaseObject && dumpRelated) {// dump related objects
			List<CollectionObject> relatedObjList = ((BaseObject) obj)
					.getCollections();
			if (relatedObjList != null) {
				Iterator<CollectionObject> relatedObjects = relatedObjList
						.iterator();
				while (relatedObjects.hasNext()) {
					CollectionObject relatedObject = relatedObjects.next();
					dumpObject(relatedObject.getObject(), false);
				}
			}
		}
	}
}
