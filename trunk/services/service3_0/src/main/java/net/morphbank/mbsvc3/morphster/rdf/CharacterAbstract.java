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
package net.morphbank.mbsvc3.morphster.rdf;

import java.util.ArrayList;
import java.util.List;

public abstract class CharacterAbstract{

	
	public abstract String getLabel(); 

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public abstract void setLabel(String value);

    /**
     * Gets the value of the hasEntity property.
     * 
     * @return
     *     possible object is
     *     {@link HasEntity }
     *     
     */
//    public abstract List<HasEntity> getHasEntity();
//
//    /**
//     * Sets the value of the hasEntity property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link HasEntity }
//     *     
//     */
//    public abstract void setHasEntity(List<HasEntity> value);
//
//    /**
//     * Gets the value of the hasQuality property.
//     * 
//     * @return
//     *     possible object is
//     *     {@link HasQuality }
//     *     
//     */
//    public abstract List<HasQuality> getHasQuality(); 
//
//    /**
//     * Sets the value of the hasQuality property.
//     * 
//     * @param value
//     *     allowed object is
//     *     {@link HasQuality }
//     *     
//     */
//    public abstract void setHasQuality(List<HasQuality> value);


    
    /**
     * Gets the value of the hasEntityOrHasQuality property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hasEntityOrHasQuality property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHasEntityOrHasQuality().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HasQuality }
     * {@link HasEntity }
     * 
     * 
     */
    public abstract List<HasEntityOrQuality> getHasEntityOrHasQuality();
    
    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public abstract String getID(); 

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public abstract void setID(String value);

}
