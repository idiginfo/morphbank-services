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

import java.io.*;

import jxl.Cell;

public class TextFieldMapper implements FieldMapper {

	protected String[] headers = null; // is this right? declaring as public
	protected String[] values = null; //                to allow access to CsvTest.java
	//String[] fieldNames = null;
	private boolean stripQuotes = true;
	private static final String SPLITTER = "\t";
	BufferedReader in = null;
	Reader fr = null;
	String fileName = null;
	int currentLine = 0;

	public TextFieldMapper(Reader reader) {
		this.fileName = fileName;
		int numLines = 0;
		try {
			//Reader reader = new InputStreamReader(inStream);
			in = new BufferedReader(reader);
			String line = null;
			do {
				line = in.readLine(); // reading and initializing header
				numLines ++;
				if (line==null) return;
			} while (!setHeaders(line));
		} catch (IOException e) {
			System.out.println("read "+numLines+" from stream");
			e.printStackTrace();
		}

	}
	public TextFieldMapper(String fileName) {
		this.fileName = fileName;
		try {
			fr = new FileReader(fileName);
			in = new BufferedReader(fr);

			String line = in.readLine(); // reading and initializing header
			setHeaders(line);
		} catch (IOException e) {

		}

	}

	public boolean setHeaders(String headerline) {
		//System.out.println("Header line: " + headerline);
		//if (!headerline.startsWith("File")) return false;
		getFieldNames(headerline);
		return true;
		// split and set headers
	}

	public void setValues(String line) {
		// split and retain line
		if (line != null) values = line.split(SPLITTER);
	}

	public String getValue(int index) {
		return strip(values[index]);
	}

	public String getValue(String fieldName) {
		int length = (values.length <= headers.length
				? values.length
				: headers.length);
		for (int i = 0; i < length; i++) {
			String test = headers[i];
			if (fieldName.equals(headers[i])) {
				return strip(values[i]);
			}
		}
		return "";
	}

	private void getFieldNames(String line) {
		headers = line.split(SPLITTER);
		for (int k = 0; k < headers.length; k++) {
			headers[k] = strip(headers[k]);
		}
		//		for (int i = 0; i < headers.length; i++)
		//			System.out.println("header[" + i + "]:" + headers[i] + ":");
	}

	public String strip(String value) {
		if (!stripQuotes) return value;
		if (value.length() > 2 && value.charAt(0) == '\"'){
			value = value.substring(1, value.length() - 1);
		}
		// removed doubled double quotes
		value = value.replace("\"\"", "\"");
		return value;
	}

	public String[] getHeaders() {
		return headers;
	}

	@Override
	public int moveToLine(int lineNumber) {
		for (int i = currentLine; i < lineNumber; i++) {
			getNextLine();
			if (i == currentLine) return -1;
		}
		return currentLine;
	}

	@Override
	public void getNextLine() {
		String line = null;
		do {// skip blank lines
			try {
				line = in.readLine();
				//System.out.println("line " + currentLine + ":" + line + ":");
				setValues(line);
				//				for (int i = 0; i < values.length; i++)
				//					System.out.println("values[" + i + "]:" + values[i] + ":");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} while (line != null && line.length() == 0);
		currentLine++;
	}

	@Override
	public boolean hasNext() {
		if (in == null) return false;
		try {
			return in.ready();
		} catch (IOException e) {
			return false;
		}
	}

	public String getFileName() {
		return fileName;
	}

	public int getCurrentLineNumber() {
		return currentLine;
	}
	
	@Override
	public Cell getValueDate(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getValueFormula(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}
}
