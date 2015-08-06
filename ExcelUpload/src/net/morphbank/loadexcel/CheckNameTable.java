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

//class for extracting id's from different tables
//based on the name provided

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class CheckNameTable {
	private String name;
	private ResultSet result;
	private boolean isUnique = false;
	Statement statement;

	public CheckNameTable() {
		statement = LoadData.getStatement();
	}

//	public CheckNameTable(String table, String colValue) {
//		this();
//		getNameFromTable(table, colValue);
//	}

	public List<String> getKeyFromTable(String table, String colValue) {
		String sql = "";
		List<String> names = new Vector<String>();
		if (table.equals("ExternalLinkType")) {
			sql = "SELECT linkTypeId FROM ExternalLinkType WHERE name='" + colValue + "'";
		} else if (table.equals("Locality")) {
			System.out.println("Cannot call CheckNameTable with table=Locality");
			LoadData.log("Cannot call CheckNameTable with table=Locality");
		} else if (table.equals("BasisOfRecord")) {
			sql = "SELECT name FROM BasisOfRecord WHERE description=\"" + colValue + "\"";
		} else {
			sql = "SELECT name FROM " + table + " WHERE name='" + colValue + "'";
		}
		try {
			result = statement.executeQuery(sql);
			while (result.next()) {
				names.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
//			System.exit(1);
		}
		return names;
	}

	public String getResult() {
		return name;
	}// end of get method

	public boolean isUnique() {
		return isUnique;
	}
}
