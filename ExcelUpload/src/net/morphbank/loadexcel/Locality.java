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

//This class loads Locality information into the   /
//data base. First it checks on all entries in     /
//Locality table and updates accordingly if the    / 
//entry does not exists already.                   /
//                                                 /
//created by: Karolina Maneva-Jakimoska            /
//date created:  January 25 2006                   /
//                                                 /
////////////////////////////////////////////////////

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import jxl.*;

public class Locality {

	public static HashMap<String, Integer> localityIds = new HashMap<String, Integer>();

	private Statement statement;
	Connection connection;
	private SheetReader sheetReader;
	private int rows;
	private int localityId;
	final static String MYTYPE = "Locality";
	CallableStatement insertStmt = null;
	Updater updater = null;

	// constructor for the Locality class
	public Locality(SheetReader sheetReader) {
		this.statement = LoadData.getStatement();
		this.connection = LoadData.getConnection();
		this.sheetReader = sheetReader;
	}

	public void processLocalities() {
		ResultSet result;
		rows = sheetReader.GetRows(MYTYPE);
		for (int row = 1; row < rows; row++) {
//			String localityRef = sheetReader.getEntry(MYTYPE, 11, row);
			String localityRef = sheetReader.getValue(MYTYPE, "Locality Name [Auto generated--Do not change!]", row);
			updater = new Updater(sheetReader, MYTYPE);

			// get and process field values
//			updater.addDescrMatchColumn(1, row, "continentOcean", "ContinentOcean");
//			updater.addDescrMatchColumn(2, row, "country", "Country");
//			updater.addStringMatchColumn(3, row, "locality");
//			updater.addLatLongMatchColumn(4, row, "latitude");
//			updater.addLatLongMatchColumn(5, row, "longitude");
//			updater.addNumericMatchColumn(6, row, "coordinatePrecision");
//			updater.addNumericMatchColumn(7, row, "minimumElevation");
//			updater.addNumericMatchColumn(8, row, "maximumElevation");
//			updater.addNumericMatchColumn(9, row, "minimumDepth");
//			updater.addNumericMatchColumn(10, row, "maximumDepth");
			updater.addDescrMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Continent Ocean"), row, "continentOcean", "ContinentOcean");
			updater.addDescrMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Country"), row, "country", "Country");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Locality Description"), row, "locality");
			updater.addLatLongMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Latitude"), row, "latitude");
			updater.addLatLongMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Longitude"), row, "longitude");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Coordinate Precision"), row, "coordinatePrecision");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Minimum Elevation"), row, "minimumElevation");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Maximum Elevation"), row, "maximumElevation");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Minimum Depth"), row, "minimumDepth");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Maximum Depth"), row, "maximumDepth");
			if (sheetReader.getColumnNumberByName(MYTYPE, "State or Province") != null) {
				updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "State or Province"), row, "state");
			}
			if (sheetReader.getColumnNumberByName(MYTYPE, "State or Province") != null) {
				updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "County"), row, "county");
			}
			

			// look for matching
			String matchQuery = updater.getMatchQuery();
			if (matchQuery.length() > 0) {
				try {
					String query = "select id from Locality where " + matchQuery;
					// System.out.println("Match query " + query);
					result = statement.executeQuery(query);
					if (result.next()) {// found a match
						localityId = result.getInt(1);
						System.out.println("Locality ref '" + localityRef + "' row " + row
								+ " matches id " + localityId);
					} else {// no match
						String insertQuery = "{call CreateObject( 'Locality', ?, ?, ?, now(), ?, '')}";
						insertStmt = LoadData.getConnection().prepareCall(insertQuery);
						int i = 1;
						insertStmt.setInt(i++, sheetReader.GetUserId());
						insertStmt.setInt(i++, sheetReader.GetGroupId());
						insertStmt.setInt(i++, sheetReader.GetSubmitterId());
						// insertStmt.setString(i++, "2010-09-10");
						insertStmt.setString(i++, "New locality from upload");

						ResultSet res = insertStmt.executeQuery();
						res.next();
						localityId = res.getInt(1);
						System.out.println("Locality ref '" + localityRef + "' row " + row
								+ " added with id " + localityId);
						int numupdated = updater.update(localityId);
					}
					localityIds.put(localityRef, localityId);
				} catch (SQLException sql) {
					sql.printStackTrace();
					System.exit(1);
				}
			}
		}
	}

	public static int getLocality(String localityRef) {
		Integer viewId = localityIds.get(localityRef);
		if (viewId == null) return 0;
		return viewId;
	}
}
