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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.06.30 at 07:38:14 AM EDT 
//


package net.morphbank.mbsvc3.rss;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.morphbank.rss package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Rss_QNAME = new QName("", "rss");
    private final static QName _TRssChannelPubDate_QNAME = new QName("", "pubDate");
    private final static QName _TRssChannelDocs_QNAME = new QName("", "docs");
    private final static QName _TRssChannelLink_QNAME = new QName("", "link");
    private final static QName _TRssChannelTextInput_QNAME = new QName("", "textInput");
    private final static QName _TRssChannelImage_QNAME = new QName("", "image");
    private final static QName _TRssChannelCopyright_QNAME = new QName("", "copyright");
    private final static QName _TRssChannelTtl_QNAME = new QName("", "ttl");
    private final static QName _TRssChannelSkipDays_QNAME = new QName("", "skipDays");
    private final static QName _TRssChannelWebMaster_QNAME = new QName("", "webMaster");
    private final static QName _TRssChannelManagingEditor_QNAME = new QName("", "managingEditor");
    private final static QName _TRssChannelCategory_QNAME = new QName("", "category");
    private final static QName _TRssChannelTitle_QNAME = new QName("", "title");
    private final static QName _TRssChannelDescription_QNAME = new QName("", "description");
    private final static QName _TRssChannelLastBuildDate_QNAME = new QName("", "lastBuildDate");
    private final static QName _TRssChannelSkipHours_QNAME = new QName("", "skipHours");
    private final static QName _TRssChannelCloud_QNAME = new QName("", "cloud");
    private final static QName _TRssChannelGenerator_QNAME = new QName("", "generator");
    private final static QName _TRssChannelLanguage_QNAME = new QName("", "language");
    private final static QName _TRssItemGuid_QNAME = new QName("", "guid");
    private final static QName _TRssItemAuthor_QNAME = new QName("", "author");
    private final static QName _TRssItemSource_QNAME = new QName("", "source");
    private final static QName _TRssItemEnclosure_QNAME = new QName("", "enclosure");
    private final static QName _TRssItemComments_QNAME = new QName("", "comments");
    //private final static QName _Content_QNAME = new QName("http://search.yahoo.com/mrss/", "content");


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.morphbank.rss
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TCloud }
     * 
     */
    public TCloud createTCloud() {
        return new TCloud();
    }

    /**
     * Create an instance of {@link TRssChannel }
     * 
     */
    public TRssChannel createTRssChannel() {
        return new TRssChannel();
    }

    /**
     * Create an instance of {@link TSkipHoursList }
     * 
     */
    public TSkipHoursList createTSkipHoursList() {
        return new TSkipHoursList();
    }

    /**
     * Create an instance of {@link TSkipDaysList }
     * 
     */
    public TSkipDaysList createTSkipDaysList() {
        return new TSkipDaysList();
    }

    /**
     * Create an instance of {@link TTextInput }
     * 
     */
    public TTextInput createTTextInput() {
        return new TTextInput();
    }

    /**
     * Create an instance of {@link TRssItem }
     * 
     */
    public TRssItem createTRssItem() {
        return new TRssItem();
    }

    /**
     * Create an instance of {@link TEnclosure }
     * 
     */
    public TEnclosure createTEnclosure() {
        return new TEnclosure();
    }

    /**
     * Create an instance of {@link TGuid }
     * 
     */
    public TGuid createTGuid() {
        return new TGuid();
    }

    /**
     * Create an instance of {@link TRss }
     * 
     */
    public TRss createTRss() {
        return new TRss();
    }

    /**
     * Create an instance of {@link TCategory }
     * 
     */
    public TCategory createTCategory() {
        return new TCategory();
    }

    /**
     * Create an instance of {@link TSource }
     * 
     */
    public TSource createTSource() {
        return new TSource();
    }

    /**
     * Create an instance of {@link TImage }
     * 
     */
    public TImage createTImage() {
        return new TImage();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TRss }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "rss")
    public JAXBElement<TRss> createRss(TRss value) {
        return new JAXBElement<TRss>(_Rss_QNAME, TRss.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "pubDate", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelPubDate(String value) {
        return new JAXBElement<String>(_TRssChannelPubDate_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "docs", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelDocs(String value) {
        return new JAXBElement<String>(_TRssChannelDocs_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "link", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelLink(String value) {
        return new JAXBElement<String>(_TRssChannelLink_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TTextInput }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "textInput", scope = TRssChannel.class)
    public JAXBElement<TTextInput> createTRssChannelTextInput(TTextInput value) {
        return new JAXBElement<TTextInput>(_TRssChannelTextInput_QNAME, TTextInput.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TImage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "image", scope = TRssChannel.class)
    public JAXBElement<TImage> createTRssChannelImage(TImage value) {
        return new JAXBElement<TImage>(_TRssChannelImage_QNAME, TImage.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "copyright", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelCopyright(String value) {
        return new JAXBElement<String>(_TRssChannelCopyright_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ttl", scope = TRssChannel.class)
    public JAXBElement<BigInteger> createTRssChannelTtl(BigInteger value) {
        return new JAXBElement<BigInteger>(_TRssChannelTtl_QNAME, BigInteger.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSkipDaysList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "skipDays", scope = TRssChannel.class)
    public JAXBElement<TSkipDaysList> createTRssChannelSkipDays(TSkipDaysList value) {
        return new JAXBElement<TSkipDaysList>(_TRssChannelSkipDays_QNAME, TSkipDaysList.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "webMaster", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelWebMaster(String value) {
        return new JAXBElement<String>(_TRssChannelWebMaster_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "managingEditor", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelManagingEditor(String value) {
        return new JAXBElement<String>(_TRssChannelManagingEditor_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "category", scope = TRssChannel.class)
    public JAXBElement<TCategory> createTRssChannelCategory(TCategory value) {
        return new JAXBElement<TCategory>(_TRssChannelCategory_QNAME, TCategory.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "title", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelTitle(String value) {
        return new JAXBElement<String>(_TRssChannelTitle_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "description", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelDescription(String value) {
        return new JAXBElement<String>(_TRssChannelDescription_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "lastBuildDate", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelLastBuildDate(String value) {
        return new JAXBElement<String>(_TRssChannelLastBuildDate_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSkipHoursList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "skipHours", scope = TRssChannel.class)
    public JAXBElement<TSkipHoursList> createTRssChannelSkipHours(TSkipHoursList value) {
        return new JAXBElement<TSkipHoursList>(_TRssChannelSkipHours_QNAME, TSkipHoursList.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCloud }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "cloud", scope = TRssChannel.class)
    public JAXBElement<TCloud> createTRssChannelCloud(TCloud value) {
        return new JAXBElement<TCloud>(_TRssChannelCloud_QNAME, TCloud.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "generator", scope = TRssChannel.class)
    public JAXBElement<String> createTRssChannelGenerator(String value) {
        return new JAXBElement<String>(_TRssChannelGenerator_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "language", scope = TRssChannel.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createTRssChannelLanguage(String value) {
        return new JAXBElement<String>(_TRssChannelLanguage_QNAME, String.class, TRssChannel.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TGuid }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "guid", scope = TRssItem.class)
    public JAXBElement<TGuid> createTRssItemGuid(TGuid value) {
        return new JAXBElement<TGuid>(_TRssItemGuid_QNAME, TGuid.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "pubDate", scope = TRssItem.class)
    public JAXBElement<String> createTRssItemPubDate(String value) {
        return new JAXBElement<String>(_TRssChannelPubDate_QNAME, String.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "author", scope = TRssItem.class)
    public JAXBElement<String> createTRssItemAuthor(String value) {
        return new JAXBElement<String>(_TRssItemAuthor_QNAME, String.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "title", scope = TRssItem.class)
    public JAXBElement<String> createTRssItemTitle(String value) {
        return new JAXBElement<String>(_TRssChannelTitle_QNAME, String.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "category", scope = TRssItem.class)
    public JAXBElement<TCategory> createTRssItemCategory(TCategory value) {
        return new JAXBElement<TCategory>(_TRssChannelCategory_QNAME, TCategory.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TSource }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "source", scope = TRssItem.class)
    public JAXBElement<TSource> createTRssItemSource(TSource value) {
        return new JAXBElement<TSource>(_TRssItemSource_QNAME, TSource.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TEnclosure }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "enclosure", scope = TRssItem.class)
    public JAXBElement<TEnclosure> createTRssItemEnclosure(TEnclosure value) {
        return new JAXBElement<TEnclosure>(_TRssItemEnclosure_QNAME, TEnclosure.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "description", scope = TRssItem.class)
    public JAXBElement<String> createTRssItemDescription(String value) {
        return new JAXBElement<String>(_TRssChannelDescription_QNAME, String.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "link", scope = TRssItem.class)
    public JAXBElement<String> createTRssItemLink(String value) {
        return new JAXBElement<String>(_TRssChannelLink_QNAME, String.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "comments", scope = TRssItem.class)
    public JAXBElement<String> createTRssItemComments(String value) {
        return new JAXBElement<String>(_TRssItemComments_QNAME, String.class, TRssItem.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssContent }{@code >}}
     * 
     */
//    @XmlElementDecl(namespace = "http://search.yahoo.com/mrss/", name = "content")
//    public JAXBElement<MrssContent> createTrssItemContent(MrssContent value) {
//        return new JAXBElement<MrssContent>(_Content_QNAME, MrssContent.class, null, value);
//    }

}
