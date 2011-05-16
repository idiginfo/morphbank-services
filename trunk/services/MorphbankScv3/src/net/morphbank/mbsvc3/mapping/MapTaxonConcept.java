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

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;
import net.morphbank.mbsvc3.xml.*;

import java.util.*;

public class MapTaxonConcept extends MapObjectBase {

	public MapTaxonConcept(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapTaxonConcept(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapTaxonConcept(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	@Override
	public TaxonConcept createObject(XmlBaseObject xmlObject, XmlId xmlId) {
		int id;
		if (xmlId != null) {
			id = xmlId.getMorphbank();
		} else {
			id = xmlObject.getMorphbankId();
		}
		String name = xmlObject.getName();
		if (name != null && name.length() == 0) name = null;
		MBCredentials ownerCred = xmlMapper.getOwner(xmlObject);
		MBCredentials submitterCred = xmlMapper.getSubmitter(xmlObject);
		TaxonConcept taxonConcept = new TaxonConcept(id, name, ownerCred.getUser(), submitterCred
				.getUser(), ownerCred.getGroup());
		boolean madePersistent = taxonConcept.persist();
		if (!madePersistent) return null;
		boolean success = updateObject(taxonConcept, xmlObject);
		if (success) return taxonConcept;
		return null;
	}

	@Override
	public TaxonConcept createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		TaxonConcept taxonConcept;
		if (idObject == null) {
			taxonConcept = (TaxonConcept) createObject(xmlObject);
			return taxonConcept;
		} else if (idObject instanceof TaxonConcept) {
			taxonConcept = (TaxonConcept) idObject;
			boolean updateSuccess = updateObject(taxonConcept, xmlObject);
			if (updateSuccess) return taxonConcept;
		}
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof TaxonConcept)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		TaxonConcept taxonConcept = (TaxonConcept) idObject;
		taxonConcept.setNameSpace(xmlObject.getNamespace());
		taxonConcept.setStatus(xmlObject.getTaxonStatus());
		// TODO add fields
		return true;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof TaxonConcept)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		//TaxonConcept taxonConcept = (TaxonConcept) idObject;
		return true;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof TaxonConcept)) return false;
		TaxonConcept taxonConcept = (TaxonConcept) idObject;
		Taxon taxon = taxonConcept.getTaxon();
		xmlObject.setNamespace(taxonConcept.getNameSpace());
		xmlObject.setTaxonStatus(taxonConcept.getStatus());
		xmlObject.setTaxon(objMapper.createXmlId(taxon, null));
		// TODO add taxon info for creating new Taxon in Tree
		objMapper.addTaxonomicTags(xmlObject, taxon);
		// TODO add fields
		return true;
	}

	@Override
	public boolean linkObject(XmlBaseObject xmlObject, IdObject object) {
		if (object == null) return true;
		if (!(object instanceof TaxonConcept)) return false;
		if (!super.linkObject(xmlObject, object)) return false;
		TaxonConcept taxonConcept = (TaxonConcept) object;
		XmlId taxonId = xmlObject.getTaxon();
		int tsn = XmlTaxonNameUtilities.getTsn(taxonId);
		Taxon taxon = Taxon.getTaxon(tsn);
		if (taxon == null && tsn > 0) {
			// TODO create Taxon! probably by calling php service! or as a separate activity
		}
		if (taxon == null && tsn > 0) {
			// no specimen in database
			xmlMapper.recordMissingLink(taxonConcept, taxonId, "taxon");
			return true;
		} 
		taxonConcept.setTaxon(taxon);
		return true;
	}

}
