/*******************************************************************************
 * Copyright (c) 2010 Greg Riccardi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * 
 * Contributors:
 *     Greg Riccardi - initial API and implementation
 ******************************************************************************/
package net.morphbank.loadexcel;

//                                                             /
//crated by: Karolina Maneva-Jakimoska                         /
//date     : July 2 2007                                       /
//Modified by: Shantanu Gautam				          		   /
//date created:  November 05 2013                      		   /
////////////////////////////////////////////////////////////////


import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

// start of public class SheetReader                               
public class ReadExternalLinks {

	private Sheet Links;

	private GetConnection connect = null;
	private ResultSet result;
	private Statement statement;
	private ResultSetMetaData metadata;

	public ReadExternalLinks(String filename, GetConnection conn) {
		connect = conn;
		String fname = filename;

		result = null;
		statement = null;
		metadata = null;

		try {
			InputStream inp = new FileInputStream(fname);
			Workbook workbook = WorkbookFactory.create(inp);
			
			// extract the sheets from a formed workbook
			Links = workbook.getSheetAt(0);

		} catch (Exception ioexception) {
			ioexception.printStackTrace();
		}
	}// end of constructor

	// public Get methods for accesing the sheets
	public Sheet GetLinksSheet() {
		return Links;
	}

	// public method for retrieving the number of columns
	public int GetColumns(Sheet sheet) {
		Sheet temp = sheet;
		return temp.getRow(0).getLastCellNum();
	}

	// public method for retrieving the number of rows
	public int GetRows(Sheet sheet) {
		Sheet temp = sheet;
		return temp.getLastRowNum();
	}

}// end of public class ReadExternalLinks

