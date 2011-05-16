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
package net.morphbank.mbsvc3.xml;

import java.util.Iterator;

public class XmlTaxonNameUtilities {

	public static String getTaxonExtId(String id) {
		return XmlUtils.SCI_NAME_PREFIX + id;
	}

	public static String getTaxonSciNameAuthorExtId(String sciName, String author) {
		return XmlUtils.SCI_NAME_AUTHOR_PREFIX + sciName
				+ XmlUtils.SCI_NAME_AUTHOR_SEPARATOR + author;
	}

	public static String getScientificName(String taxonExtId) {
		String[] fields = taxonExtId.split(":");
		if (fields[0].equals(XmlUtils.SCI_NAME_PREFIX)) {
			return fields[1];
		}
		return null;
	}

	public static String getScientificName(XmlId taxonId) {
		Iterator<String> externalIds = taxonId.getExternal().iterator();
		while (externalIds.hasNext()) {
			String scientificName = getScientificName(externalIds.next());
			if (scientificName != null) {
				return scientificName;
			}
		}
		return null;
	}

	public static int getTsn(String taxonExtId) {
		if (taxonExtId == null) return 0;
		if (taxonExtId.indexOf(XmlUtils.ITIS_PREFIX) != 0) return 0;
		String tsn = taxonExtId.substring(XmlUtils.SCI_NAME_PREFIX.length());
		try {
			return Integer.valueOf(tsn);
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public static int getTsn(XmlId taxonId) {
		if (taxonId == null) return 0;
		Iterator<String> externalIds = taxonId.getExternal().iterator();
		while (externalIds.hasNext()) {
			int tsn = getTsn(externalIds.next());
			if (tsn > 0) {
				return tsn;
			}
		}
		return 0;
	}

	public static XmlId getTaxonId(String scientificName, int tsn, int mbId) {
		if ((scientificName == null || scientificName.length() == 0) && tsn < 1 && mbId < 1)
			return null;
		XmlId xmlId = new XmlId();
		if (scientificName != null && scientificName.length() > 0) {
			xmlId.addExternal(XmlTaxonNameUtilities.getTaxonExtId(scientificName));
		}
		if (tsn > 0) xmlId.addExternal(XmlUtils.ITIS_PREFIX + tsn);
		if (mbId > 0) xmlId.setMorphbank(mbId);
		return xmlId;
	}
}
