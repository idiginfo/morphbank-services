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

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



public class XlsFieldMapper implements FieldMapper {

	protected String[] headers = null; // is this right? declaring as public
	protected String[] values = null; // to allow access to CsvTest.java

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

	public XlsFieldMapper(String fileName) {
		try {

			this.fileName = fileName;
			InputStream inp = new FileInputStream(fileName);
			Workbook workbook = WorkbookFactory.create(inp);
			views = workbook.getSheetAt(0);
			credentialSheet = workbook.getSheetAt(2);
			links = workbook.getSheetAt(1);
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

		Row row = views.getRow(0);
		numFields = views.getRow(0).getLastCellNum();
		if (numFields > 0) {
			headers = new String[numFields];
		} else {
			headers = new String[1];
		}
		for (Cell cell : row) {
			int index = cell.getColumnIndex();
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				headers[index] = cell.getStringCellValue().toLowerCase();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					headers[index] = cell.getDateCellValue().toString();
				} else {
					headers[index] = Integer.toString((int) cell
							.getNumericCellValue());
				}
			}
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
		String retValue = "";
		Row row =  views.getRow(currentLine);
		Cell cell = null;
		if(null!= row){
			cell = row.getCell(index);
		}
		if(null==cell){
			return retValue;
		}
		
		switch(row.getCell(index).getCellType())
		{
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					retValue = cell.getDateCellValue().toString();
				}
				else
				{
					retValue =  Integer.toString((int) cell.getNumericCellValue());
				}
				break;
				
			case Cell.CELL_TYPE_STRING:
				retValue = cell.getStringCellValue();
				break;
				
			case Cell.CELL_TYPE_FORMULA:
				retValue = cell.getCellFormula();
				break;
		}
		return retValue;
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
				
				if(views.getRow(currentLine).getCell(i).getCellType() == Cell.CELL_TYPE_FORMULA)
				{
					return views.getRow(currentLine).getCell(i).getCellFormula();
				}
			}
			
				return getValue(i);
			}
		return "";
	}

	public Cell getValueDate(String fieldName) {
		fieldName = fieldName.toLowerCase();
		for (int i = 0; i < headers.length; i++) {
			if (fieldName.equals(headers[i])) {

				Cell date = views.getRow(currentLine).getCell(i);
				if(!date.getStringCellValue().equalsIgnoreCase(""))
				{
					return date;
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
