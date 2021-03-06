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

import java.io.FileOutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RssWriter {
  private static String XML_BLOCK = "\n";
  private static String XML_INDENT = "\t";
  
  public static void write(RssFeed rssfeed, String xmlfile) throws Exception {
    XMLOutputFactory output = XMLOutputFactory.newInstance();
    XMLEventWriter writer = output.createXMLEventWriter
      (new FileOutputStream(xmlfile));
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent endSection = eventFactory.createDTD(XML_BLOCK);

    StartDocument startDocument = eventFactory.createStartDocument();
    writer.add(startDocument);
    writer.add(endSection);
    StartElement rssStart = eventFactory.createStartElement("", "", "rss");
    writer.add(rssStart);
    writer.add(eventFactory.createAttribute("version", "2.0"));
    writer.add(endSection);

    writer.add(eventFactory.createStartElement("", "", "channel"));
    writer.add(endSection);

    RssHeader header = rssfeed.getHeader();
    createNode(writer, "title", header.getTitle());
    createNode(writer, "link", header.getLink());
    createNode(writer, "description", header.getDescription());
    createNode(writer, "language", header.getLanguage());
    createNode(writer, "copyright", header.getCopyright());
    createNode(writer, "pubDate", header.getPubDate());
    Iterator<RssEntry> iterator = rssfeed.getEntries().iterator();
    while (iterator.hasNext()) {
      RssEntry entry = iterator.next();
      writer.add(eventFactory.createStartElement("", "", "item"));
      writer.add(endSection);
      createNode(writer, "title", entry.getTitle());
      createNode(writer, "description", entry.getDescription());
      createNode(writer, "link", entry.getLink());
      createNode(writer, "guid", entry.getGuid());
      createNode(writer, "pubDate", entry.getPubDate());
      writer.add(eventFactory.createEndElement("", "", "item"));
      writer.add(endSection);
    }

    writer.add(endSection);
    writer.add(eventFactory.createEndElement("", "", "channel"));
    writer.add(endSection);
    writer.add(eventFactory.createEndElement("", "", "rss"));

    writer.add(endSection);
    writer.add(eventFactory.createEndDocument());
    writer.close();
  }

  private static void createNode
    (XMLEventWriter eventWriter, String name, String value) 
     throws XMLStreamException {
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
    XMLEvent endSection = eventFactory.createDTD(XML_BLOCK);
    XMLEvent tabSection = eventFactory.createDTD(XML_INDENT);

    StartElement sElement = eventFactory.createStartElement("", "", name);
    eventWriter.add(tabSection);
    eventWriter.add(sElement);

    Characters characters = eventFactory.createCharacters(value);
    eventWriter.add(characters);

    EndElement eElement = eventFactory.createEndElement("", "", name);
    eventWriter.add(eElement);
    eventWriter.add(endSection);
  }
}
