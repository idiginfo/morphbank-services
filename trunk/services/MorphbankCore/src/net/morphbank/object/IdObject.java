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
package net.morphbank.object;

public interface IdObject {
	public int getId();
	public String getObjectTypeIdStr();

	//public RdfBase getRdfObject();
	
	/** 
	 * Make this object persistent in the database return true if successful
	 */
	public boolean persist();
	
	/**
	 * Test to see if object is fit to be stored as an object
	 * i.e. that the field values are appropriate for the database
	 */
	public boolean validate();
}
