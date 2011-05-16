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

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.mbsvc3.rss.*;
import net.morphbank.mbsvc3.request.*;

public class RssServices {

	public static final String REQ_PKG = "net.morphbank.mbsvc3.rss";
	static String RSS_PACKAGE = "net.morphbank.mbsvc3.rss";
	static final String TEST_URL = "http://www.morphbank.net/?id=218849&imgType=jpg";
	static final String TEST_THUMB_URL = "http://www.morphbank.net/?id=218849&imgType=thumb";
	public static final ObjectFactory FACTORY = new ObjectFactory();

	public static TRss createRss(String title) {
		TRss rss = new TRss();
		return rss;
	}

	public static TRssChannel addChannel(TRss rss, String title, String description) {
		TRssChannel channel = new TRssChannel();
		rss.setChannel(channel);
		JAXBElement<String> channelTitle = FACTORY.createTRssChannelTitle(title);
		channel.getTitleOrLinkOrDescription().add(channelTitle);
		JAXBElement<String> channelAuthor = FACTORY.createTRssChannelDescription(description);
		channel.getTitleOrLinkOrDescription().add(channelAuthor);
		return channel;
	}

	public static TRssItem addItem(TRssChannel channel, String title, String url) {
		TRssItem item = new TRssItem();
		JAXBElement<String> itemTitle = FACTORY.createTRssItemTitle(title);
		JAXBElement<String> itemLink = FACTORY.createTRssItemLink(url);
		JAXBElement<String> itemAuthor = FACTORY.createTRssItemAuthor("Morphbank");
		item.getTitleOrDescriptionOrLink().add(itemLink);
		item.getTitleOrDescriptionOrLink().add(itemTitle);
		item.getTitleOrDescriptionOrLink().add(itemAuthor);
		channel.getItem().add(item);
		return item;
	}

	public static TRss doRssResponse(PrintWriter out, String title, String subtitle,
			int numResults, int firstResult, List objectIds) {
		// based on keywords, objectTypes and limit
		TRss rss = RssServices.createRss(title);
		// subtitle += " total number of results " + numResults;
		// subtitle += " showing items beginning at " + firstResult;
		TRssChannel channel = RssServices.addChannel(rss, title, subtitle);
		Iterator iter = objectIds.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			int id;
			if (obj instanceof BaseObject) {
				id = ((BaseObject) obj).getId();
			} else {
				id = MorphbankConfig.getIntFromQuery(obj);
			}
			RssBaseObject rssBaseObject = RssBaseObject.createRssObject(id);
			@SuppressWarnings("unused")
			TRssItem item = rssBaseObject.addMrssItem(channel, null);
		}
		if (out != null) RssServices.printXml(out, rss);
		return rss;
	}

	public static void doIdResponse(TRssChannel channel, int id, boolean addRelatedObjs) {
		BaseObject obj = BaseObject.getEJB3Object(id);
		doIdResponse(channel, obj, addRelatedObjs);
	}

	public static void doIdResponse(TRssChannel channel, BaseObject obj, boolean addRelatedObjs) {
		MapObjectsToRss mapper = new MapObjectsToRss(channel);
		if (obj != null) {
			int numResults = 1;
			if (obj instanceof Collection) {
				// do not add base object, add related objects
				addRelatedObjs = true;
				numResults = 0;
			} else {
				mapper.addObject(obj);
			}
			// add related objects
			if (addRelatedObjs) {
				numResults += mapper.addRelatedObjects(obj);
			}
		}
	}

	public static String getRssUrl(RequestParams params) {
		params.setFormat(RequestParams.FORMAT_RSS);
		return MorphbankConfig.servicePrefix + params.getUrlParams();

	}

	public static void printXml(Object xmlObj) {
		PrintWriter out = new PrintWriter(System.out);
		printXml(out, xmlObj);
		out.close();
	}

	public static void printXml(PrintWriter out, Object xmlDoc) {
		// JAXBContext jc = null;
		Marshaller marshaller = getMarshaller();
		try {
			marshaller.marshal(xmlDoc, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static JAXBContext jc = null;
	static Marshaller marshaller = null;
	static Unmarshaller unmarshaller = null;

	public static Marshaller getMarshaller() {
		if (marshaller != null) {
			return marshaller;
		}
		try {
			if (jc == null) {
				jc = JAXBContext.newInstance(REQ_PKG);
			}
			marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			try {
				marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
						new NamespacePrefixMapperImpl());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return marshaller;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
