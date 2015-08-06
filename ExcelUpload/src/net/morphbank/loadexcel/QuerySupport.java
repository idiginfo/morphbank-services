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

//This class checks for any changes in the Supported Data       /
//worksheet and updates the database accordingly                /
//Updates SpecimenPart, ViewAngle, Form, DevelopmentalStage,    /
//ImagingPreparationTechnique,ImagingTechnique, ContinentOcean, /
//and TypeStatus                                                /
//                                                              /
//created by: Karolina Maneva-Jakimoska                         /
//      date: Jan 22 2006                                       /
//date last Modified: Feb 10 2006                               /
/////////////////////////////////////////////////////////////////

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QuerySupport {
	final static String MYTYPE = "SupportData";

	private Statement statement;
	private SheetReader sheetReader;
	private int columns = 0;
	private int rows = 0;
	private String temp = ""; // holds the SQL statements
	private String table = "";
	private String data = "";

	// constructor, takes the connection,the statement
	// and the read from excel sheet as parameters
	public QuerySupport(SheetReader sheetReader) {
		this.statement = LoadData.getStatement();
		this.sheetReader = sheetReader;
	}

	public void loadQuerySupportData() {
		try {
			// max_rows and columns on delivered spreadsheet
			columns = sheetReader.GetColumns(MYTYPE);
			rows = sheetReader.GetRows(MYTYPE);

			for (int i = 0; i < columns; i++) {
				for (int j = 1; j < rows; j++) {
					// for all initialized rows on the excel spreadsheet that
					// are not empty
					String entry = sheetReader.getEntry(MYTYPE, i, j);
					if (!entry.equals("")) {
						// calling readFromDb to check if the entry already
						// exists
						// exchange this with ID results
						if (!readFromDb(i, entry)) // there is no such
													// entry,write in the DB
							writeToDb(i, j);
					}
				}
			}
			if (sheetReader.GetProjectLink1().length()==0) {
				List<String> names = LoadData.getCheckNames().getKeyFromTable("ExternalLinkType",
						"Project");
				if (names.size() == 0) {
					try {
						statement
								.executeUpdate("INSERT INTO ExternalLinkType(name,description) VALUES ('Project','Not provided')");
					} catch (SQLException sqle) {
						sqle.printStackTrace();
//						System.exit(1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//			System.exit(1);
		}
	}// end of constructor for class QuerySupport

	// public method that reads from the database and
	// returns true if the record exists already and false otherwise
	private boolean readFromDb(int i, String name) {
		// System.out.println("Check if the entry already exists " + name);
		ResultSet result = null;
		data = sheetReader.getEntry(MYTYPE, i, 0);
		if (data.equals("Specimen Part")) table = "SpecimenPart";
		if (data.equals("View Angle")) table = "ViewAngle";
		if (data.equals("Preparation Technique")) table = "ImagingPreparationTechnique";
		if (data.equals("Imaging Technique")) table = "ImagingTechnique";
		if (data.equals("Developmental Stage")) table = "DevelopmentalStage";
		if (data.equals("Form")) table = "Form";
		if (data.equals("Continent Ocean")) {
			table = "ContinentOcean";
			name = name.toUpperCase();
		}
		if (data.equals("Type Status")) table = "TypeStatus";
		if (data.equals("Type of External Link")) table = "ExternalLinkType";

		int col = 0;
		int row = 0;
		try {
			temp = "SELECT name FROM " + table + " WHERE name='" + name + "'";
			if (table.equals("ContinentOcean"))
				temp = "SELECT name FROM " + table + " WHERE description='" + name + "'";
			// System.out.println("readFromDb sql: " + temp);
			result = statement.executeQuery(temp);
			ResultSetMetaData metadata = result.getMetaData();
			col = metadata.getColumnCount();
			result.first();
			row = result.getRow();
			// System.out.println("From  readDB: " + row + "rows and " + col +
			// "columns");
		} catch (SQLException sql) {
			sql.printStackTrace();
//			System.exit(1);
		}
		if (col == 1 && row == 0)
			return false;
		else
			return true;
	}// end of method readFromDB

	/**
	 * public method that writes the new entry into corresponding table and
	 * calls CreateBaseObject if neccessary to create new object
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean writeToDb(int i, int j) {

		String new_entry = sheetReader.getEntry(MYTYPE, i, j);
		// TODO fix this line
		// TODO CreateBaseObject newobject = null;
		String objectTypeId = "";
		String description = "Not provided";
		// System.out.println("Currently in table: " + table);
		if ((table != "TypeStatus") && (table != "ContinentOcean") && (table != "ExternalLinkType")) {
			// ObjectId new_LSID=new ObjectId(statement);
			// String lsid=new_LSID.GetLSID();
			// System.out.println(lsid);
			try {
				temp = "INSERT INTO " + table + " (name, description) VALUES (" + "'" + new_entry
						+ "'" + "," + "'" + description + "'" + ")";
				statement.executeUpdate(temp);
				objectTypeId = table;
				description = "New " + data + "  was added to the table using Excel file";
				// TODO newobject = new CreateBaseObject(sheetReader, statement,
				// lsid,
				// objectTypeId, description);
				return true;
			} catch (SQLException sqle) {
				sqle.printStackTrace();
//				System.exit(1);
			}
		}
		String col_name = sheetReader.getEntry(MYTYPE, i, 0);
		/*
		 * if(col_name.equals("Type Status")==0 ||
		 * col_name.equals("Type of External Link")==0){ temp="INSERT INTO " +
		 * table + "(name,description) VALUES ('" + new_entry + "','" +
		 * description + "')"; try{ statement.executeUpdate(temp);
		 * }catch(SQLException sqle){ sqle.printStackTrace(); System.exit(1); }
		 * return true; }
		 */
		if (col_name.equals("Continent Ocean")) {
			// System.out.println("In the continentOcean");
			String new_code = GenerateNewCode();
			if (new_code == null) return false;
			temp = "INSERT INTO ContinentOcean (description,name) VALUES ('"
					+ new_entry.toUpperCase() + "','" + new_code + "')";
			try {
				statement.executeUpdate(temp);
			} catch (SQLException sqle) {
				sqle.printStackTrace();
//				System.exit(1);
			}
			return true;
		}
		return false;
	} // end of writeIndB

	// public method which generates a new
	// Continent/Ocean code
	public String GenerateNewCode() {
		char i = 0, j = 0;
		String code = "";
		char[] newcode = new char[2];
		for (i = 65; i <= 90; i++) {
			for (j = 65; j <= 90; j++) {
				newcode[0] = i;
				newcode[1] = j;
				code = new String(newcode);
				if (CodeExist(code.toUpperCase()))
					continue;
				else
					return code.toUpperCase();
			}
		}
		if (i == 90 && j == 90) {
			System.out.println("No more codes can be generated");
			LoadData.log("No more codes can be generated");
//			System.exit(1);
			return null;
		}
		return code.toUpperCase();
	}// end of GenerateNewCode

	// this method chech if the code already exists
	// if it does returns true
	public boolean CodeExist(String newcode) {
		ResultSet result = null;
		temp = "SELECT name FROM ContinentOcean";
		try {
			result = statement.executeQuery(temp);
			result.first();
			while (result.isAfterLast() != true) {
				if (!newcode.trim().equals(result.getString(1).trim()))
					result.next();
				else
					return true;
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
//			System.exit(1);
		}
		return false;
	}// end of CodeExist
}
