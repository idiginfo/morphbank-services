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

import org.apache.poi.ss.usermodel.Cell;

public interface FieldMapper {
	
	//public void setValues(String line);
	
	public String getValue(int index);
	
	public String getValue(String fieldName);
	
	public Date getValueDate(String fieldName);
	
	public boolean hasNext();
	
	public void getNextLine();
	
	//public String strip(String value);

	public String[] getHeaders() ;

	public int moveToLine(int lineNumber);
	
	public String getFileName();
	
	public int getCurrentLineNumber();

	public String getValueFormula(String fieldName);

}
