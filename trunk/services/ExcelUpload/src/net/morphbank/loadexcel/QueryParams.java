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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QueryParams {
	private StringBuffer columns = new StringBuffer();
	private StringBuffer values = new StringBuffer();
	private StringBuffer matchQuery = new StringBuffer();
	private SheetReader sheetReader = null;
	private Statement statement;
	private String type;

	public QueryParams(Statement statement, SheetReader sheetReader, String type) {
		this.sheetReader = sheetReader;
		this.statement = statement;
		this.type = type;
	}

	public boolean addStringColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (!entry.equals("")) {
			addStringColumn(colName, entry);
			return true;
		}
		return false;
	}

	public boolean addStringColumn(String columnName, String columnValue) {
		if (columns.length() > 0) {
			values.append(",");
			columns.append(",");
		}
		values.append("'").append(columnValue.replace("'", "''")).append("'");
		columns.append(columnName);
		return true;
	}

	public boolean addNamedMatchColumn(int col, int row, String colName, String tableName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (!entry.equals("")) { // && FormValid(entry,i)==true){
			// System.out.println(colName + " valid");
			List<String> names = LoadData.getCheckNames().getKeyFromTable(tableName, entry);
			if (names.size() == 1) {
				return addStringMatchColumn(colName, names.get(0));
			}
			// not found or not unique
		}
		return false;
	}

	public boolean addDescrMatchColumn(int col, int row, String colName, String tableName) { // get
																								// and
																								// process
																								// field
																								// values
		String entry = sheetReader.getEntry(type, col, row);// continent ocean
		if (!entry.equals("")) {
			try {
				String coSql = "SELECT name FROM " + tableName + " WHERE description='"
						+ entry.toUpperCase() + "'";
				System.out.println(tableName + " query: " + coSql);
				LoadData.log(tableName + " query: " + coSql);
				ResultSet result = statement.executeQuery(coSql);
				result.next();
				String continentOcean = result.getString(1);
				addStringMatchColumn(colName, continentOcean);
				return true;
			} catch (SQLException sql) {
				System.err.println("No " + colName + " match " + entry);
				LoadData.log("No " + colName + " match " + entry);
			}
		}
		return false;
	}

	public boolean addNumericColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (!entry.equals("")) {
			return addNumericColumn(colName, entry, row);
		}
		return false;
	}

	public boolean addNumericColumn(String columnName, String columnValue, int row) {
		try {
			Double.valueOf(columnValue);
			if (columns.length() > 0) {
				values.append(",");
				columns.append(",");
			}
			values.append(columnValue);
			columns.append(columnName);
			return true;
		} catch (Exception e) {
			System.err.println("Nonumeric " + columnName + " in row " + (row + 1));
			LoadData.log("Nonumeric " + columnName + " in row " + (row + 1));
			return false;
		}
	}

	public boolean addStringMatchQuery(String columnName, String columnValue) {
		if (matchQuery.length() > 0) {
			matchQuery.append(" and ");
		}
		matchQuery.append(columnName).append("=").append("'")
				.append(columnValue.replace("'", "''")).append("'");
		return true;
	}

	public boolean addNumericMatchQuery(String columnName, String columnValue) {
		try {
			Double.valueOf(columnValue);
			if (matchQuery.length() > 0) {
				matchQuery.append(" and ");
			}
			matchQuery.append(columnName).append("=").append(columnValue);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addStringMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (!entry.equals("")) {
			return addStringMatchColumn(colName, entry);
		}
		return false;
	}

	public boolean addStringMatchColumn(String colName, String colValue) {
		boolean result = addStringColumn(colName, colValue);
		result |= addStringMatchQuery(colName, colValue);
		return result;
	}

	public boolean addNumericMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (!entry.equals("")) {
			return addNumericMatchColumn(colName, entry, row);
		}
		return false;
	}

	public boolean addLatLongMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.endsWith("N") || entry.endsWith("E")) {
			entry = entry.substring(0, entry.length() - 1);
		} else if (entry.endsWith("S") || entry.endsWith("W")) {
			entry = "-" + entry.substring(0, entry.length() - 1);
		}
		if (!entry.equals("")) {
			return addNumericMatchColumn(colName, entry, row);
		}
		return false;
	}

	public boolean addNumericMatchColumn(String colName, String colValue, int row) {
		boolean result = addNumericColumn(colName, colValue, row);
		result |= addNumericMatchQuery(colName, colValue);
		return result;
	}

	public String getValues() {
		return values.toString();
	}

	public String getMatchQuery() {
		return matchQuery.toString();
	}

	public String getColumns() {
		return columns.toString();
	}

}
