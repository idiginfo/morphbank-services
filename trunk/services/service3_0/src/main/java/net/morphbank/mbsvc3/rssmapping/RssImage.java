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
import net.morphbank.mbsvc3.mediarss.MrssContent;
import net.morphbank.mbsvc3.rss.TRssChannel;
import net.morphbank.mbsvc3.rss.TRssItem;
import net.morphbank.object.Image;
import net.morphbank.object.Specimen;

public class RssImage extends RssBaseObject {
	Image image;

	public RssImage(Image image) {
		super(image);
		this.image = image;
	}

	public String getTitle(String title) {
		if (title != null && title.trim().length() != 0) {
			return title;
		}
		//String taxon = null;
		Specimen specimen = image.getSpecimen();
		if (specimen != null) {
			title = RssSpecimen
					.getTitle(image.getId(), specimen, "Image of a ");
		} else {
			title = "Image with id " + object.getId();
		}
		return title;
	}

	public TRssItem addMrssItem(TRssChannel channel, String title) {
		if (object != null || !(object instanceof Image)) {
			title = getTitle(title);
			TRssItem item = super.addMrssItem(channel, title);
			MrssContent content = new MrssContent();
			content.setUrl(MorphbankConfig.getImageURL(object.getId(), "jpeg"));
			item.getTitleOrDescriptionOrLink().add(content);
			RssSpecimen.addLocality(item, object);
			return item;
		}
		return null;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
