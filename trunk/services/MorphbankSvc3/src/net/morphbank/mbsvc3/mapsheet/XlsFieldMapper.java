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
package net.morphbank.mbsvc3.mapsheet;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.StringFormulaCell;
import jxl.Workbook;

public class XlsFieldMapper implements FieldMapper {

	protected String[] headers = null; // is this right? declaring as public
	protected String[] values = null; // to allow access to CsvTest.java
	// String[] fieldNames = null;
	private boolean stripQuotes = true;
	private static final String SPLITTER = "\t";
	private String splitter = SPLITTER;
	private Sheet views;
	private Sheet credentialSheet;
	private Sheet links;
	int numFields;
	int lastLine;
	int currentLine;
	String fileName;

	// what is the use of this method
	public XlsFieldMapper(String fileName) {
		try {
			this.fileName = fileName;
			File file = new File(fileName);
			Workbook workbook = Workbook.getWorkbook(file);
			views = workbook.getSheet(0);
			credentialSheet = workbook.getSheet(2);
			links = workbook.getSheet(1);
			readHeaders();
			lastLine = views.getRows() - 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int moveToLine(int lineNumber) {
		return currentLine = lineNumber;
	}

	public void readHeaders() {
		numFields = views.getColumns();
		headers = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headers[i] = views.getCell(i, 0).getContents().toLowerCase();
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
		return views.getCell(index, currentLine).getContents();
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
	
	@Override
	public String getValueFormula(String fieldName) {
		fieldName = fieldName.toLowerCase();
		for (int i = 0; i < headers.length; i++) {
			if (fieldName.equals(headers[i])) {
				String test = views.getCell(i, currentLine).getType().toString();
				if (views.getCell(i, currentLine).getType().toString().equalsIgnoreCase("String Formula")) {
					StringFormulaCell formulaCell = (StringFormulaCell) views.getCell(i, currentLine);
					return formulaCell.getContents();
				}
				return getValue(i);
			}
		}
		return "";
	}

	public Cell getValueDate(String fieldName) {
		fieldName = fieldName.toLowerCase();
		for (int i = 0; i < headers.length; i++) {
			if (fieldName.equals(headers[i])) {
				Cell date = views.getCell(i, currentLine);
				if (!date.getContents().equalsIgnoreCase("")) {
					return views.getCell(i, currentLine);
				}
			}
		}
		return null;
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

	public Sheet getCredentialSheet() {
		return credentialSheet;
	}

	public Sheet getLinks() {
		return links;
	}


}
