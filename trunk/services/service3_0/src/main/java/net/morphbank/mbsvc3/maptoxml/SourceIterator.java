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
package net.morphbank.mbsvc3.maptoxml;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;

public interface SourceIterator extends Iterator<SourceObject> {
	
	//public void setValues(String line);
	
	public String getValue(int index);
	
	public String getValue(String fieldName);
	
	public Date getValueDate(String fieldName);
	
	public boolean hasNext();
	

	public String[] getHeaders() ;

	public String getValueFormula(String fieldName);


}
