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
import javax.xml.namespace.QName;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.*;
import net.morphbank.mbsvc3.rss.*;

import net.morphbank.mbsvc3.mediarss.*;

public class RssBaseObject {
	BaseObject object;

	public static RssBaseObject createRssObject(int objId) {
		BaseObject obj = BaseObject.getEJB3Object(objId);
		return createRssObject(obj);
	}

	public static RssBaseObject createRssObject(BaseObject obj) {
		if (obj instanceof Image) {
			return new RssImage((Image) obj);
		} else if (obj instanceof Specimen) {
			return new RssSpecimen((Specimen) obj);
		} else {
			return new RssBaseObject(obj);
		}
	}

	public String getTitle(String title) {
		if (object == null) {
			return null;
		}
		title = "Morphbank " + object.getClassName();
		String name = object.getName();
		if (name == null || name.trim().length() == 0) {
			title += " with id " + object.getId();
		} else {
			title += ": " + name;
		}
		return title;
	}

	public RssBaseObject(BaseObject object) {
		this.object = object;
	}

	public static QName typeName = new QName(null, "type");
	public static QName groupName = new QName(null, "group");

	public TRssItem addMrssItem(TRssChannel channel, String title) {
		if (object != null) {
			if (title == null || title.trim().length() == 0) {
				title = getTitle(title);
			}
			TRssItem item = RssServices.addItem(channel, title, MorphbankConfig
					.getURL(object.getId()));
			// .addItem(channel, title,
			// MorphbankConfig.getImageURL(object.getId(),"jpg"));

			item.getOtherAttributes().put(typeName, object.getClassName());
			item.getOtherAttributes().put(groupName,
					object.getGroup().getGroupName());
			// description
			StringBuffer desc = new StringBuffer();
			desc.append(" <img src=\"").append(object.getFullThumbURL())
					.append("\"/>");
			if (object.getObjectLogo() != null) {
				desc.append(" <img src=\"").append(object.getObjectLogo())
						.append("\" width=\"50\" />");
			}
			User user = object.getUser();
			if (user.getUserLogo() != null) {
				desc.append(" <img src=\"").append(User.USER_LOGO_BASE_URL)
						.append(user.getUserLogo())
						.append("\" width=\"50\" />");
			}

			JAXBElement<String> itemDescription = RssServices.FACTORY
					.createTRssItemDescription(desc.toString());
			item.getTitleOrDescriptionOrLink().add(itemDescription);

			MrssThumbnail thumb = new MrssThumbnail();
			thumb.setUrl(object.getFullThumbURL());
			item.getTitleOrDescriptionOrLink().add(thumb);
			return item;
		}
		return null;
	}
}
