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
package net.morphbank.mbsvc3.mapping;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.*;
import net.morphbank.mbsvc3.request.*;
import net.morphbank.object.Group;
import net.morphbank.object.User;

public class ProcessRequest {

	Request request = null;
	Responses responses = null;
	User requestUser = null;
	Group requestGroup = null;
	MapXmlToObject mapper = null;
	Credentials requestCredentials;

	public ProcessRequest(Request request) {
		this.request = request;
		if (request != null) requestCredentials = request.getSubmitter();
		responses = new Responses();
	}

	public Responses processRequest() throws IOException {
		// process queries
		// process inserts
		// process updates
		processQueries(request.getQuery());
		processInserts(request.getInsert());
		processUpdates(request.getUpdate());
		return responses;
	}

	public void processQueries(List<Query> queryList) throws IOException {
		Iterator<Query> queries = queryList.iterator();
		while (queries.hasNext()) {
			Query query = queries.next();
			Response response = processQuery(query);
			finalizeResponse(response);
			responses.getResponse().add(response);
		}
	}

	public void processInserts(List<Insert> insertList) {
		Iterator<Insert> inserts = insertList.iterator();
		while (inserts.hasNext()) {
			Insert insert = inserts.next();
			Response response = processInsert(insert);
			finalizeResponse(response);
			responses.getResponse().add(response);
		}
	}

	public void processUpdates(List<Update> updateList) {
		Iterator<Update> updates = updateList.iterator();
		while (updates.hasNext()) {
			Update update = updates.next();
			Response response = processUpdate(requestCredentials, update);
			finalizeResponse(response);
			responses.getResponse().add(response);
		}
	}

	public Response processQuery(Query query) throws IOException {
		// TODO process query
		ProcessQuery queryProcessor = new ProcessQuery();
		Object obj = queryProcessor.processQuery(requestCredentials, query);
		if (obj instanceof Response) {
			return (Response) obj;
		}
		System.err.println("improper format for request");
		return null;
	}

	public Response processInsert(ObjectList insert) {
		// EntityTransaction tx = null;
		// EntityManager em = null;
		Response response = null;
		mapper = new CreateObjectsFromXml(requestCredentials, insert, null);
		try { // add objects to database

			// em = MorphbankConfig.getEntityManager();
			// tx = em.getTransaction();
			// tx.begin();
			response = mapper.processObjects();
			// tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			mapper.reportSuccess(null, null);
			// if (tx.isActive()) {
			// tx.rollback();
			// }
		}
		return finalizeResponse(mapper.getResponse());
	}

	public Response processUpdate(Credentials requestCredentials, ObjectList update) {
		EntityTransaction tx = null;
		EntityManager em = null;
		boolean localTransaction = false;
		mapper = new UpdateObjectsFromXml(requestCredentials, update, MorphbankConfig
				.getRemoteServer());
		try { // add objects to database
			// TODO process updates one object per transaction!
//			em = MorphbankConfig.getEntityManager();
//			tx = em.getTransaction();
//			if (!tx.isActive()) {
//				localTransaction = true;
//				tx.begin();
//			}
			mapper.processObjects();
//			if (localTransaction) tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			mapper.reportSuccess(null, null);
			if (localTransaction) tx.rollback();
		}
		return finalizeResponse(mapper.getResponse());
	}

	public Response finalizeResponse(Response response) {
		// prepare status
		if (mapper != null) {
			StringBuffer status = new StringBuffer("");
			if (mapper.failure) {

				status
						.append("Some objects were not saved. Please see individual objects for details");
			} else {
				status.append("All objects were saved");
			}
			status.append("\nNumber of objects: ").append(mapper.numObjects);
			status.append("\nNumber of objects with insert problems: ")
					.append(mapper.failedObjects);
			response.setStatus(status.toString());
			response.setNumberAffected(mapper.failure ? 0 : mapper.successObjects);
			return mapper.getResponse();
		}
		// must have been a query: no mapper
		if (response != null) response.setStatus("query ok");
		return response;
	}
}
