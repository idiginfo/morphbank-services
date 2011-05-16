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
package net.morphbank.mbsvc3.test;

import java.io.PrintWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.rss.TRss;
import net.morphbank.mbsvc3.rss.TRssChannel;
import net.morphbank.mbsvc3.rss.TRssItem;

public class MrssTest {
	static String RSS_PACKAGE = "net.morphbank.rss"; 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MrssTest test = new MrssTest();
		test.run(args);
	}
	
	public void run(String[] args){
		TRss rss = new TRss();
		TRssChannel channel = new TRssChannel();
		rss.setChannel(channel);
		TRssItem item = new TRssItem();
		channel.getItem().add(item);
		
		JAXBContext jc = null;
		//Unmarshaller unmarshaller = null;
		Marshaller marshaller = null;
		try {
			jc = JAXBContext.newInstance(RSS_PACKAGE);
			//unmarshaller = jc.createUnmarshaller();
			marshaller = jc.createMarshaller();

			MorphbankConfig.SYSTEM_LOGGER.info("Here's the XML");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(rss, new PrintWriter(System.out));
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

}
