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
package net.morphbank.mbsvc3.rssmapping;

import javax.xml.bind.JAXBElement;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;
//import net.morphbank.mbsvc3.mediarss.*;
import net.morphbank.mbsvc3.mediarss.*;
import net.morphbank.mbsvc3.rss.TRssChannel;
import net.morphbank.mbsvc3.rss.TRssItem;
//import net.morphbank.mbsvc3.rss.*;
public class RssSpecimen extends RssBaseObject {
	Specimen specimen = null;
	public static final ObjectFactory FACTORY = new ObjectFactory();

	public RssSpecimen(Specimen specimen) {
		super(specimen);
		this.specimen = specimen;
	}

	public String getTitle(String title) {
		return getTitle(0, specimen, title);
	}

	public static String getTitle(int id, Specimen specimen, String title) {
		if (title == null || title.trim().length() == 0) {
			title = "";
		}
		String taxon = null;
		if (specimen != null) {
			if (id == 0) {
				id = specimen.getId();
			}
			if (specimen.getTaxon() != null) {
				taxon = specimen.getTaxon().getScientificName();
			} else {
				taxon = specimen.getTaxonomicNames();
			}
		} else {
			taxon = "plant or animal";
		}
		title = title + taxon + " with id " + id;
		return title;
	}

	public TRssItem addMrssItem(TRssChannel channel, String title) {
		if (object != null || !(object instanceof Specimen)) {
			title = getTitle(title);
			Image standardImage = specimen.getStandardImage();
			TRssItem item = super.addMrssItem(channel, title);
			MrssContent content = new MrssContent();
			if (standardImage != null) {
				content.setUrl(MorphbankConfig.getImageURL(standardImage
						.getId(), "jpg"));
				item.getTitleOrDescriptionOrLink().add(content);
			}
			addLocality(item, object);
			return item;
		}
		return null;
	}
	
	public static boolean addLocality(TRssItem item, BaseObject obj) {
		if (obj == null) return false;
		Locality locality = obj.getLocalityObject();
		if (locality == null) return false;
		JAXBElement<String> point = FACTORY.createPoint(locality.getLatitude(),
				locality.getLongitude());
		item.getTitleOrDescriptionOrLink().add(point);
		return false;
	}
}
