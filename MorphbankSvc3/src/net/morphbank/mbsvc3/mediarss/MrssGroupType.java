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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mrssGroupType", propOrder = {
    "content",
    "rating",
    "title",
    "description",
    "keywords",
    "thumbnail",
    "category",
    "hash",
    "player",
    "credit",
    "restriction",
    "copyright",
    "text"
})
public class MrssGroupType {

    protected List<MrssContent> content;
    protected MrssRatingType rating;
    protected MrssTitle title;
    protected MrssDescription description;
    protected String keywords;
    protected MrssThumbnail thumbnail;
    protected List<MrssCategoryType> category;
    protected MrssHashType hash;
    protected MrssPlayerType player;
    protected List<MrssCreditType> credit;
    protected MrssRestrictionType restriction;
    protected MrssCopyright copyright;
    protected MrssText text;

    /**
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MrssContent }
     * 
     * 
     */
    public List<MrssContent> getContent() {
        if (content == null) {
            content = new ArrayList<MrssContent>();
        }
        return this.content;
    }

    /**
     * Gets the value of the rating property.
     * 
     * @return
     *     possible object is
     *     {@link MrssRatingType }
     *     
     */
    public MrssRatingType getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssRatingType }
     *     
     */
    public void setRating(MrssRatingType value) {
        this.rating = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link MrssTitle }
     *     
     */
    public MrssTitle getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssTitle }
     *     
     */
    public void setTitle(MrssTitle value) {
        this.title = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link MrssDescription }
     *     
     */
    public MrssDescription getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssDescription }
     *     
     */
    public void setDescription(MrssDescription value) {
        this.description = value;
    }

    /**
     * Gets the value of the keywords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Sets the value of the keywords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeywords(String value) {
        this.keywords = value;
    }

    /**
     * Gets the value of the thumbnail property.
     * 
     * @return
     *     possible object is
     *     {@link MrssThumbnail }
     *     
     */
    public MrssThumbnail getThumbnail() {
        return thumbnail;
    }

    /**
     * Sets the value of the thumbnail property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssThumbnail }
     *     
     */
    public void setThumbnail(MrssThumbnail value) {
        this.thumbnail = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the category property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MrssCategoryType }
     * 
     * 
     */
    public List<MrssCategoryType> getCategory() {
        if (category == null) {
            category = new ArrayList<MrssCategoryType>();
        }
        return this.category;
    }

    /**
     * Gets the value of the hash property.
     * 
     * @return
     *     possible object is
     *     {@link MrssHashType }
     *     
     */
    public MrssHashType getHash() {
        return hash;
    }

    /**
     * Sets the value of the hash property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssHashType }
     *     
     */
    public void setHash(MrssHashType value) {
        this.hash = value;
    }

    /**
     * Gets the value of the player property.
     * 
     * @return
     *     possible object is
     *     {@link MrssPlayerType }
     *     
     */
    public MrssPlayerType getPlayer() {
        return player;
    }

    /**
     * Sets the value of the player property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssPlayerType }
     *     
     */
    public void setPlayer(MrssPlayerType value) {
        this.player = value;
    }

    /**
     * Gets the value of the credit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MrssCreditType }
     * 
     * 
     */
    public List<MrssCreditType> getCredit() {
        if (credit == null) {
            credit = new ArrayList<MrssCreditType>();
        }
        return this.credit;
    }

    /**
     * Gets the value of the restriction property.
     * 
     * @return
     *     possible object is
     *     {@link MrssRestrictionType }
     *     
     */
    public MrssRestrictionType getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssRestrictionType }
     *     
     */
    public void setRestriction(MrssRestrictionType value) {
        this.restriction = value;
    }

    /**
     * Gets the value of the copyright property.
     * 
     * @return
     *     possible object is
     *     {@link MrssCopyright }
     *     
     */
    public MrssCopyright getCopyright() {
        return copyright;
    }

    /**
     * Sets the value of the copyright property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssCopyright }
     *     
     */
    public void setCopyright(MrssCopyright value) {
        this.copyright = value;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link MrssText }
     *     
     */
    public MrssText getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link MrssText }
     *     
     */
    public void setText(MrssText value) {
        this.text = value;
    }

}
