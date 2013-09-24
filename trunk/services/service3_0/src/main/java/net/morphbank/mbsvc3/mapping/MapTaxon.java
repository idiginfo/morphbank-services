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

import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.object.BaseObject;
import net.morphbank.object.IdObject;
import net.morphbank.object.Taxon;
import net.morphbank.object.TaxonConcept;

public class MapTaxon extends MapObjectBase {

	public MapTaxon(MapXmlToObject xmlMapper, MapObjectToResponse objMapper) {
		super(xmlMapper, objMapper);
	}

	public MapTaxon(MapXmlToObject xmlMapper) {
		super(xmlMapper, null);
	}

	public MapTaxon(MapObjectToResponse objMapper) {
		super(null, objMapper);
	}

	@Override
	public Taxon createObject(XmlBaseObject xmlObject, XmlId xmlId) {
		int id = xmlObject.getMorphbankId();
		String name = xmlObject.getName();
		if (name != null && name.length() == 0) name = null;
		MBCredentials ownerCred = xmlMapper.getOwner(xmlObject);
		MBCredentials submitterCred = xmlMapper.getSubmitter(xmlObject);
		Taxon taxon = new Taxon();
		taxon.persist();
		boolean success = updateObject(taxon, xmlObject);
		if (success) return taxon;
		return null;
	}

	@Override
	public Taxon createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject) {
		Taxon taxon;
		if (idObject == null) {
			taxon = (Taxon) createObject(xmlObject);
			return taxon;
		} else if (idObject instanceof Taxon) {
			taxon = (Taxon) idObject;
			boolean updateSuccess = updateObject(taxon, xmlObject);
			if (updateSuccess) return taxon;
		}
		return null;
	}

	@Override
	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Taxon)) return false;
		if (!super.updateObject(idObject, xmlObject)) return false;
		Taxon taxon = (Taxon) idObject;
		// TODO add fields
		//taxonRank
		// namespace
        //taxonStatus
        //taxonRank
		//scientificName
		//other DWC kingdom, phylum, class, order, family, genus

		return false;
	}

	@Override
	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Taxon)) return false;
		if (!objMapper.setXmlBaseObjectFields(xmlObject, idObject)) return false;
		if (!setXmlFields(xmlObject, idObject)) return false;
		Taxon taxon = (Taxon) idObject;
		// TODO add fields
		return false;
	}

	@Override
	public boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject) {
		if (idObject == null) return true;
		if (!(idObject instanceof Taxon)) return false;
		Taxon taxon = (Taxon) idObject;
		// TODO add fields
		//taxonRank
		// namespace
        //taxonStatus
        //taxonRank
		//scientificName
		//other DWC kingdom, phylum, class, order, family, genus

		return false;
	}

	/**
	 * Use the XmlId to find a taxon
	 * 
	 * @param id
	 * @return
	 */
	public static Taxon getTaxon(XmlId id) {
		Taxon taxon;
		BaseObject obj;
		// check for a Morphbank id
		if (id.getMorphbank() != 0) {
			// taxon has morphbankId
			try {
				obj = BaseObject.getEJB3Object(id.getMorphbank());
				TaxonConcept taxonConcept = (TaxonConcept) obj;
				taxon = taxonConcept.getTaxon();
				return taxon;
			} catch (Exception e) {
				// morphbank id is not a TaxonConcept
			}
		}

		// look through the external ids
		// TODO use darwin core fields
		// TODO use name finder in XmlObjectUtilities?
		Iterator<String> extIds = id.getExternal().iterator();
		while (extIds.hasNext()) {
			String extId = extIds.next();
			if (extId.startsWith(XmlUtils.ITIS_PREFIX)) {
				String tsnStr = extId.substring(XmlUtils.ITIS_PREFIX.length());
				if (tsnStr != null) {
					try {
						taxon = Taxon.getTaxon(Integer.valueOf(tsnStr));
						if (taxon != null) {
							return taxon;
						}
					} catch (Exception e) {
						// no taxon for this extId!
					}
				}
			}
			// Check for scientific name as external id
			else if (extId.startsWith(XmlUtils.SCI_NAME_AUTHOR_PREFIX)) {
				String sciNameAuthor = extId.substring(XmlUtils.SCI_NAME_AUTHOR_PREFIX
						.length());
				String[] fields = sciNameAuthor.split(XmlUtils.SCI_NAME_AUTHOR_SPLIT);
				if (fields.length < 2) return null;
				taxon = Taxon.getTaxon(fields[0].trim(), fields[1].trim());// TODO add kingdom
				if (taxon != null) {
					return taxon;
				}else {
					taxon = Taxon.getTaxon(fields[0].trim());
					if (taxon != null) {
						return taxon;
					}
				}
			}
			else if (extId.startsWith(XmlUtils.SCI_NAME_PREFIX)) {
				String scientificName = extId.substring(XmlUtils.SCI_NAME_PREFIX.length());
				taxon = Taxon.getTaxon(scientificName);// TODO add kingdom
				if (taxon != null) {
					return taxon;
				}
			}

			else { // try to find taxon concept by external id
				obj = BaseObject.getObjectByExternalId(extId);
				if (obj != null && obj instanceof TaxonConcept) {
					taxon = ((TaxonConcept) obj).getTaxon();
					return taxon;
				}
			}
			
			
		}
		// try local ids? what about
		return null;
	}
	
	
}
