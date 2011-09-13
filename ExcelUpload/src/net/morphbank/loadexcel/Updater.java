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

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class Updater {
	// private StringBuffer columns = new StringBuffer();
	// private StringBuffer values = new StringBuffer();
	private StringBuffer matchQuery = new StringBuffer();
	private boolean isMatchQueryNull = true;
	private SheetReader sheetReader = null;
	private Statement statement;
	private Connection connection;

	private StringBuffer query = new StringBuffer();
	// private PreparedStatement updateStatement = null;
	private List<String> columns = new Vector<String>();
	private List<Object> values = new Vector<Object>();
	private String type;

	public Updater(SheetReader sheetReader, String type) {
		this.sheetReader = sheetReader;
		this.connection = LoadData.getConnection();
		try {
			this.statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();

		}
		this.type = type;
	}

	public int update(int id) {
		StringBuffer updateQuery = new StringBuffer("update ");
		String comma = "";
		updateQuery.append(type).append(" set ");
		for (int i = 0; i < columns.size(); i++) {
			updateQuery.append(comma).append(columns.get(i)).append(" = ?");
			comma = ",";
		}
		updateQuery.append(" where id = ").append(id);
		try {
			PreparedStatement updateStatement = connection.prepareStatement(updateQuery.toString());
			for (int i = 0; i < values.size(); i++) {
				updateStatement.setObject(i + 1, values.get(i));
			}
			int numresults = updateStatement.executeUpdate();
			return numresults;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean addStringColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.length() > 0) {
			addStringColumn(colName, entry);
			return true;
		}
		return false;
	}

	public boolean addStringColumn(String columnName, String columnValue) {
		values.add(columnValue);
		columns.add(columnName);
		return true;
	}

	public boolean addNamedMatchColumn(int col, int row, String colName, String tableName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.length() > 0) { // && FormValid(entry,i)==true){
			// System.out.println(colName + " valid");
			List<String> names = LoadData.getCheckNames().getKeyFromTable(tableName, entry);
			if (names.size() == 1) {
				return addStringMatchColumn(colName, names.get(0));
			}
			// not found or not unique
		}
		return false;
	}

	/*
	 * Duplicate method from the one above to fix a
	 * conflict when some fields of the view are empty
	 * (matchquery would only check the non empty fields
	 * therefore returning more than one result
	 */
	public boolean addViewLocalityNamedMatchColumn(int col, int row, String colName, String tableName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.length() > 0) { // && FormValid(entry,i)==true){
			// System.out.println(colName + " valid");
			List<String> names = LoadData.getCheckNames().getKeyFromTable(tableName, entry);
			if (names.size() == 1) {
				isMatchQueryNull &= false;
				return addStringMatchColumn(colName, names.get(0));
			}
			// not found or not unique
		}
		else {
			isMatchQueryNull &= true;
			return addStringMatchColumn(colName, null);
		}
		return false;
	}

	public boolean addDescrMatchColumn(int col, int row, String colName, String tableName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.length() > 0) {
			try {
				String coSql = "SELECT name FROM " + tableName + " WHERE description='"
				+ entry.toUpperCase() + "'";
				// System.out.println(tableName + " query: " + coSql);
				ResultSet result = statement.executeQuery(coSql);
				result.next();
				String name = result.getString(1);
				addStringMatchColumn(colName, entry);
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
		if (entry.length() > 0) {
			return addNumericColumn(colName, entry, row);
		}
		return false;
	}

	public boolean addIntColumn(String columnName, int columnValue) {
		columns.add(columnName);
		values.add(Integer.valueOf(columnValue));
		return true;
	}

	public boolean addNumericColumn(String columnName, String columnValue, int row) {
		if (columnValue == null) {
			values.add(null);
			columns.add(columnName);
			return true;
		}
		if (columnValue.length() == 0) return false;
		try {
			Double colValue = Double.valueOf(columnValue);
			values.add(colValue);
			columns.add(columnName);
			return true;
		} catch (Exception e) {
			System.err.println("Nonumeric " + columnName + " '" + columnValue + "' in row "
					+ (row + 1));
			LoadData.log("Nonumeric " + columnName + " '" + columnValue + "' in row "
					+ (row + 1));
			return false;
		}
	}

	public boolean addStringMatchQuery(String columnName, String columnValue) {
		if (matchQuery.length() > 0) {
			matchQuery.append(" and ");
		}
		if (columnValue == null) {
			matchQuery.append(columnName).append(" is ").append(" null ");
		}
		else {
			matchQuery.append(columnName).append("=").append("'")
			.append(columnValue.replace("'", "''")).append("'");
		}
		return true;
	}

	public boolean addNumericMatchQuery(String columnName, String columnValue) {
		try {
			if (columnValue != null) {
				Double.valueOf(columnValue);
				if (matchQuery.length() > 0) {
					matchQuery.append(" and ");
				}
				matchQuery.append(columnName).append("=").append(columnValue);
			}
			else {
				if (matchQuery.length() > 0) {
					matchQuery.append(" and ");
				}
				matchQuery.append(columnName).append(" is ").append(" null ");
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addStringMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry != null && entry.length() > 0) {
			return addStringMatchColumn(colName, entry);
		}
		return false;
	}

	/*
	 * Duplicate method from the one above to fix a
	 * conflict when some fields of the view are empty
	 * (matchquery would only check the non empty fields
	 * therefore returning more than one result
	 */
	public boolean addViewLocalityStringMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry != null && entry.length() > 0) {
			isMatchQueryNull &= false;
			return addStringMatchColumn(colName, entry);
		}
		else {
			isMatchQueryNull &= true;
			return addStringMatchColumn(colName, null);
		}
		//		return false;
	}

	public boolean addStringMatchColumn(String colName, String colValue) {
		boolean result = addStringColumn(colName, colValue);
		result |= addStringMatchQuery(colName, colValue);
		return result;
	}

	public boolean addNumericMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.length() > 0) {
			isMatchQueryNull &= false;
			return addNumericMatchColumn(colName, entry, row);
		}
		else {
			isMatchQueryNull &= true;
			return addNumericMatchColumn(colName, null, row);
		}
	}

	public boolean addLocalityLatLongMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry == null) {
			isMatchQueryNull &= true;
			return addNumericMatchColumn(colName, null, row);
		}
		if (entry.endsWith("N") || entry.endsWith("E")) {
			entry = entry.substring(0, entry.length() - 1);
		} else if (entry.endsWith("S") || entry.endsWith("W")) {
			entry = "-" + entry.substring(0, entry.length() - 1);
		}
		if (entry.length() > 0) {
			isMatchQueryNull &= false;
			return addNumericMatchColumn(colName, entry, row);
		}
		return false;
	}

	/*
	 * Duplicate method from the one above to fix a
	 * conflict when some fields of the Locality are empty
	 * (matchquery would only check the non empty fields
	 * therefore returning more than one result
	 */
	public boolean addLatLongMatchColumn(int col, int row, String colName) {
		String entry = sheetReader.getEntry(type, col, row);
		if (entry.endsWith("N") || entry.endsWith("E")) {
			entry = entry.substring(0, entry.length() - 1);
		} else if (entry.endsWith("S") || entry.endsWith("W")) {
			entry = "-" + entry.substring(0, entry.length() - 1);
		}
		if (entry.length() > 0) {
			isMatchQueryNull &= false;
			return addNumericMatchColumn(colName, entry, row);
		}
		return false;
	}

	public boolean addNumericMatchColumn(String colName, String colValue, int row) {
		boolean result = addNumericColumn(colName, colValue, row);
		result |= addNumericMatchQuery(colName, colValue);
		return result;
	}

	public String getMatchQuery() {
		return matchQuery.toString();
	}

//	public void addDateColumn(String columnName, Date columnValue) {
	public void addDateColumn(String columnName, String columnValue) {
		columns.add(columnName);
		values.add(columnValue);
	}

	public boolean isMatchQueryNull() {
		return isMatchQueryNull;
	}
}
