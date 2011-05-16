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

public abstract class HasEntityOrQuality {

	
	protected String resource;

    /**
     * Gets the value of the resource property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public abstract String getResource();


    /**
     * Sets the value of the resource property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public abstract void setResource(String value);

	
	
}
