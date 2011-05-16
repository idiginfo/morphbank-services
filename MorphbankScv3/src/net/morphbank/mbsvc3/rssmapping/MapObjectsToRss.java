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

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;

import java.util.*;
import net.morphbank.mbsvc3.rss.*;
import net.morphbank.mbsvc3.mediarss.*;

public class MapObjectsToRss {
	TRssChannel channel;

	MapObjectsToRss(TRssChannel channel) {
		this.channel = channel;
	}

	public TRss createRss(List<Integer> ids) {
		return null;
	}

	public TRssItem addObject(BaseObject obj) {
		if (obj == null) return null;
		RssBaseObject rssBaseObject = RssBaseObject.createRssObject(obj);
		TRssItem item = rssBaseObject.addMrssItem(channel, null);
		return item;
	}

	public int addRelatedObjects(BaseObject obj) {
		// RssBaseObject rssObj = null;
		if (obj == null) return 0;
		int numRelated = 0;
		if (obj instanceof Image) {
			// add specimen and view
			addObject(((Image) obj).getSpecimen());
			addObject(((Image) obj).getView());
			// TODO add other related objects including extra views
		} else if (obj instanceof Specimen) {
			// add standard image
			addObject(((Specimen) obj).getStandardImage());
			// TODO add other related objects including parent specimen
			// } else if (obj instanceof Taxon) {
			// no related objects
		} else if (obj instanceof View) {
			// add standard image
			addObject(((View) obj).getStandardImage());
			// TODO add other related objects including related views (eg spider
			// smaller region)
		}
		// TODO add collection objects
		List idList = obj.getRelatedIdList();
		Iterator ids = idList.iterator();
		while (ids.hasNext()) {
			Object idObj = ids.next();
			int id = MorphbankConfig.getIntFromQuery(idObj);
			BaseObject relatedObj = BaseObject.getEJB3Object(id);
			addObject(relatedObj);
			numRelated++;
		}
		return numRelated;
	}
}
