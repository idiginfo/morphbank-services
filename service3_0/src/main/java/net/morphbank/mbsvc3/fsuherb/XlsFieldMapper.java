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
 *  Shantanu Gautam - Modified to use org.apache.poi.ss.usermodel
 *  Date - Nov 6, 2013
 ******************************************************************************/
package net.morphbank.mbsvc3.fsuherb;

import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class XlsFieldMapper implements FieldMapper {

	protected String[] headers = null; // is this right? declaring as public
	protected String[] values = null; // to allow access to CsvTest.java
	// String[] fieldNames = null;
	private boolean stripQuotes = true;
	private static final String SPLITTER = "\t";
	private String splitter = SPLITTER;
	private Sheet views;
	int numFields;
	int lastLine;
	int currentLine;
	String fileName;

	// what is the use of this method
	public XlsFieldMapper(String fileName) {
		try {
			this.fileName = fileName;
			InputStream inp = new FileInputStream(fileName);
			Workbook workbook = WorkbookFactory.create(inp);
			views = workbook.getSheetAt(0);
			readHeaders();
			lastLine = views.getLastRowNum() - 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int moveToLine(int lineNumber) {
		return currentLine = lineNumber;
	}


	public void readHeaders() {
		numFields = views.getRow(0).getLastCellNum();
		headers = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headers[i] = views.getRow(0).getCell(i).getStringCellValue().toLowerCase();
		}
		currentLine = 0;
	}

	public boolean hasNext() {
		return currentLine < lastLine;
	}
	public void getNextLine() {
		// split and retain line
		if (hasNext()) {
			currentLine++;
		}
	}

	public String getValue(int index) {
		return views.getRow(currentLine).getCell(index).getStringCellValue();
	}

	public String getValue(String fieldName) {
		fieldName = fieldName.toLowerCase();
		for (int i = 0; i < headers.length; i++) {
			if (fieldName.equals(headers[i])) {
				return getValue(i);
			}
		}
		return "";
	}

	public String[] getHeaders() {
		return headers;
	}

	@Override
	public int getCurrentLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFileName() {
		return fileName;
	}
}
