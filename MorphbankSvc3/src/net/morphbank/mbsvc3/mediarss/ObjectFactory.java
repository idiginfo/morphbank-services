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
// Generated on: 2008.06.30 at 07:37:51 AM EDT 
//


package net.morphbank.mbsvc3.mediarss;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.morphbank.mrss package. 
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
	public final static String MRSS_NAMESPACE = "http://search.yahoo.com/mrss/";
	public final static String GEO_NAMESPACE = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public final static String GEORSS_NAMESPACE = "http://www.georss.org/georss";
    private final static QName _Copyright_QNAME = new QName(MRSS_NAMESPACE, "copyright");
    private final static QName _Hash_QNAME = new QName(MRSS_NAMESPACE, "hash");
    private final static QName _Restriction_QNAME = new QName(MRSS_NAMESPACE, "restriction");
    private final static QName _Player_QNAME = new QName(MRSS_NAMESPACE, "player");
    private final static QName _Keywords_QNAME = new QName(MRSS_NAMESPACE, "keywords");
    private final static QName _Text_QNAME = new QName(MRSS_NAMESPACE, "text");
    private final static QName _Credit_QNAME = new QName(MRSS_NAMESPACE, "credit");
    private final static QName _Group_QNAME = new QName(MRSS_NAMESPACE, "group");
    private final static QName _Rating_QNAME = new QName(MRSS_NAMESPACE, "rating");
    private final static QName _Description_QNAME = new QName(MRSS_NAMESPACE, "description");
    private final static QName _Content_QNAME = new QName(MRSS_NAMESPACE, "content");
    private final static QName _Thumbnail_QNAME = new QName(MRSS_NAMESPACE, "thumbnail");
    private final static QName _Title_QNAME = new QName(MRSS_NAMESPACE, "title");
    private final static QName _Category_QNAME = new QName(MRSS_NAMESPACE, "category");
    private final static QName _Point_QNAME = new QName(GEORSS_NAMESPACE, "point");
    private final static QName _Lat_QNAME = new QName(GEO_NAMESPACE, "lat");
    private final static QName _Long_QNAME = new QName(GEO_NAMESPACE, "long");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.morphbank.mrss
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MrssContent }
     * 
     */
    public MrssContent createMrssContentType() {
        return new MrssContent();
    }

    /**
     * Create an instance of {@link MrssHashType }
     * 
     */
    public MrssHashType createMrssHashType() {
        return new MrssHashType();
    }

    /**
     * Create an instance of {@link MrssDescription }
     * 
     */
    public MrssDescription createMrssDescriptionType() {
        return new MrssDescription();
    }

    /**
     * Create an instance of {@link MrssPlayerType }
     * 
     */
    public MrssPlayerType createMrssPlayerType() {
        return new MrssPlayerType();
    }

    /**
     * Create an instance of {@link MrssRatingType }
     * 
     */
    public MrssRatingType createMrssRatingType() {
        return new MrssRatingType();
    }

    /**
     * Create an instance of {@link MrssCreditType }
     * 
     */
    public MrssCreditType createMrssCreditType() {
        return new MrssCreditType();
    }

    /**
     * Create an instance of {@link MrssCopyright }
     * 
     */
    public MrssCopyright createMrssCopyrightType() {
        return new MrssCopyright();
    }

    /**
     * Create an instance of {@link MrssTitle }
     * 
     */
    public MrssTitle createMrssTitleType() {
        return new MrssTitle();
    }

    /**
     * Create an instance of {@link MrssText }
     * 
     */
    public MrssText createMrssTextType() {
        return new MrssText();
    }

    /**
     * Create an instance of {@link MrssExtension }
     * 
     */
    public MrssExtension createMrssExtension() {
        return new MrssExtension();
    }

    /**
     * Create an instance of {@link MrssGroupType }
     * 
     */
    public MrssGroupType createMrssGroupType() {
        return new MrssGroupType();
    }

    /**
     * Create an instance of {@link MrssRestrictionType }
     * 
     */
    public MrssRestrictionType createMrssRestrictionType() {
        return new MrssRestrictionType();
    }

    /**
     * Create an instance of {@link MrssCategoryType }
     * 
     */
    public MrssCategoryType createMrssCategoryType() {
        return new MrssCategoryType();
    }

    /**
     * Create an instance of {@link MrssThumbnail }
     * 
     */
    public MrssThumbnail createMrssThumbnailType() {
        return new MrssThumbnail();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssCopyright }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "copyright")
    public JAXBElement<MrssCopyright> createCopyright(MrssCopyright value) {
        return new JAXBElement<MrssCopyright>(_Copyright_QNAME, MrssCopyright.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssHashType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "hash")
    public JAXBElement<MrssHashType> createHash(MrssHashType value) {
        return new JAXBElement<MrssHashType>(_Hash_QNAME, MrssHashType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssRestrictionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "restriction")
    public JAXBElement<MrssRestrictionType> createRestriction(MrssRestrictionType value) {
        return new JAXBElement<MrssRestrictionType>(_Restriction_QNAME, MrssRestrictionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssPlayerType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "player")
    public JAXBElement<MrssPlayerType> createPlayer(MrssPlayerType value) {
        return new JAXBElement<MrssPlayerType>(_Player_QNAME, MrssPlayerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "keywords")
    public JAXBElement<String> createKeywords(String value) {
        return new JAXBElement<String>(_Keywords_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssText }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "text")
    public JAXBElement<MrssText> createText(MrssText value) {
        return new JAXBElement<MrssText>(_Text_QNAME, MrssText.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssCreditType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "credit")
    public JAXBElement<MrssCreditType> createCredit(MrssCreditType value) {
        return new JAXBElement<MrssCreditType>(_Credit_QNAME, MrssCreditType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "group")
    public JAXBElement<MrssGroupType> createGroup(MrssGroupType value) {
        return new JAXBElement<MrssGroupType>(_Group_QNAME, MrssGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssRatingType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "rating")
    public JAXBElement<MrssRatingType> createRating(MrssRatingType value) {
        return new JAXBElement<MrssRatingType>(_Rating_QNAME, MrssRatingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "description")
    public JAXBElement<MrssDescription> createDescription(MrssDescription value) {
        return new JAXBElement<MrssDescription>(_Description_QNAME, MrssDescription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssContent }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "content")
    public JAXBElement<MrssContent> createContent(MrssContent value) {
        return new JAXBElement<MrssContent>(_Content_QNAME, MrssContent.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssThumbnail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "thumbnail")
    public JAXBElement<MrssThumbnail> createThumbnail(MrssThumbnail value) {
        return new JAXBElement<MrssThumbnail>(_Thumbnail_QNAME, MrssThumbnail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssTitle }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "title")
    public JAXBElement<MrssTitle> createTitle(MrssTitle value) {
        return new JAXBElement<MrssTitle>(_Title_QNAME, MrssTitle.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssCategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = MRSS_NAMESPACE, name = "category")
    public JAXBElement<MrssCategoryType> createCategory(MrssCategoryType value) {
        return new JAXBElement<MrssCategoryType>(_Category_QNAME, MrssCategoryType.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssCategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = GEORSS_NAMESPACE, name = "point")
    public JAXBElement<String> createPoint(Double lat, Double lng) {
    	if (lat== null||lng==null)return null;
    	String value = lat.toString() + " " + lng.toString();
        return new JAXBElement<String>(_Point_QNAME, String.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssCategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = GEO_NAMESPACE, name = "lat")
    public JAXBElement<Long> createLat(Long value) {
        return new JAXBElement<Long>(_Lat_QNAME, Long.class, null, value);
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MrssCategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = GEO_NAMESPACE, name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

}
