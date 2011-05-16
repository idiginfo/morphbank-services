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

import java.util.Iterator;
import java.util.List;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.mbsvc3.xml.XmlTaxonNameUtilities;
import net.morphbank.object.BaseObject;
import net.morphbank.object.ExternalLinkObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Taxon;
import net.morphbank.object.TaxonConcept;
import net.morphbank.object.View;

public class XmlServices {

	public static Response createIdResponse(String requestType, String description, int id,
			boolean addRelatedObjs) {
		BaseObject obj = BaseObject.getEJB3Object(id);
		return createIdResponse(requestType, description, obj, addRelatedObjs);
	}

	public static Response createIdResponse(String requestType, String description, BaseObject obj,
			boolean addRelatedObjs) {
		MapObjectToResponse mapper = new MapObjectToResponse(requestType, description);
		Response resp = mapper.getResponse();
		mapper.addObject(obj);
		// add related objects
		int numResults = 1;
		if (addRelatedObjs) {
			numResults += mapper.addRelatedObjects(obj);
		}
		resp.setNumberAffected(resp.getNumberAffected() + numResults);
		return resp;
	}

	public static Response addIdResponse(MapObjectToResponse mapper, String requestType,
			String description, BaseObject obj, boolean addRelatedObjs) {
		Response resp = mapper.getResponse();
		mapper.addObject(obj);
		// add related objects
		int numResults = 1;
		if (addRelatedObjs) {
			numResults += mapper.addRelatedObjects(obj);
		}
		resp.setNumberAffected(numResults);
		return resp;
	}

	public static Response createResponse(String requestType, String description, int numResults,
			int numReturned, int firstResult, List objectIds) {
		MapObjectToResponse mapper = new MapObjectToResponse(requestType, description);
		Response resp = mapper.getResponse();
		resp.setNumberAffected(numResults);
		resp.setNumMatches(numResults);
		resp.setFirstReturned(firstResult);
		resp.setNumReturned(objectIds.size());
		if (numResults == 0) {
			return resp;
		}
		if (objectIds == null) {
			return resp;
		}
		Iterator iter = objectIds.iterator();
		while (iter.hasNext()) {
			Object idObj = iter.next();
			int id;
			if (idObj instanceof BaseObject) {
				id = ((BaseObject) idObj).getId();
			} else {
				id = MorphbankConfig.getIntFromQuery(idObj);
			}
			BaseObject obj = BaseObject.getEJB3Object(id);
			mapper.addObject(obj);
		}
		return resp;
	}

	public static void addExternalRef(XmlBaseObject xmlObj, ExternalLinkObject extLink) {
		Extref extref = new Extref(extLink.getExternalId(), extLink.getExtLinkTypeId());
		extref.setLabel(extLink.getLabel());
		extref.setDescription(extLink.getDescription());
		extref.setUrlData(extLink.getUrlData());
		xmlObj.getExternalRef().add(extref);
	}

	/**
	 * Find the object referenced by id if the Morphbank id is not null, return
	 * the object with that id if any external id refers to an object, return it
	 * otherwise, return null
	 * 
	 * @param id
	 *            XmlId id to use
	 * @return
	 */
	public static BaseObject getObject(XmlId id) {
		// try using morphbank id for BaseObject
		if (id == null) {
			return null;
		}
		Integer mbId = id.getMorphbank();
		// Check for valid morphbank id
		if (mbId != null && mbId != 0) {
			return BaseObject.getEJB3Object(mbId.intValue());
		}
		// try using external id for BaseObject
		Iterator<String> extIds = id.getExternal().iterator();
		while (extIds.hasNext()) {
			String extId = extIds.next();
			BaseObject obj = BaseObject.getObjectByExternalId(extId);
			if (obj != null) {
				return obj;
			}
		}
		return null; // no BaseObject for id
	}

	/**
	 * Create an XmlId to represent a Taxon
	 * 
	 * @param taxon
	 * @return
	 */
	public static XmlId getTaxonId(Taxon taxon) {
		if (taxon == null) {
			return null;
		}
		int tsn = taxon.getTsn();
		String scientificName = taxon.getScientificName();
		TaxonConcept tc = TaxonConcept.getTaxonConcept(tsn);
		int mbId = 0;
		if (tc != null) {
			mbId = tc.getId();
		}
		return XmlTaxonNameUtilities.getTaxonId(scientificName, tsn, mbId);
	}

	public static Taxon getTaxon(XmlId id) {
		// look for TaxonConcept ided by a morphbank id
		if (id.getMorphbank() != 0) {
			try {
				TaxonConcept tc = (TaxonConcept) BaseObject.getEJB3Object(id.getMorphbank());
				return tc.getTaxon();
			} catch (Exception e) {
				// go to next option!
			}
		}
		// look for an ITIS id in the external ids
		int tsn = XmlTaxonNameUtilities.getTsn(id);
		if (tsn > 0) return Taxon.getTaxon(tsn);
		// TODO look for a TaxonConcept by external Id.
		String scientificName = XmlTaxonNameUtilities.getScientificName(id);
		return Taxon.getTaxon(scientificName);
	}

	public static View getView(XmlId viewId) {
		Iterator<String> extIds = viewId.getExternal().iterator();
		while (extIds.hasNext()) {
			String extId = extIds.next();
			// try extid as viewname
			View view = View.getViewFromName(extId);
			if (view != null) return view;
			BaseObject baseObj = BaseObject.getObjectByExternalId(extId);
			if (baseObj instanceof View) return (View) baseObj;
		}
		// TODO Auto-generated method stub
		return null;
	}

}
