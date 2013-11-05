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

import java.sql.*;
import java.util.*;

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

	public boolean processLocalities() {
		ResultSet result;
		rows = sheetReader.GetRows(MYTYPE);
		for (int row = 1; row < rows; row++) {
			//			String localityRef = sheetReader.getEntry(MYTYPE, 11, row);
			String localityRef = sheetReader.getValue(MYTYPE, ExcelTools.COL_LOCALITY_NAME, row);
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
			Integer col = sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_CONTINENT_OCEAN);
			if(col != null && col != -1) {
				updater.addStringMatchColumn(col, row, "continentOcean");
			}
			col = sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_CONTINENT);
			if(col != null  && col != -1) {
				updater.addStringMatchColumn(col, row, "continent");
			}
			col = sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_WATER_BODY);
			if(col != null  && col != -1) {
				updater.addStringMatchColumn(col, row, "ocean");
			}
			updater.addViewLocalityStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COUNTRY), row, "country");
			updater.addViewLocalityStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_LOCALITY_DESCRIPTION), row, "locality");
			updater.addLocalityLatLongMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_LATITUDE), row, "latitude");
			updater.addLocalityLatLongMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_LONGITUDE), row, "longitude");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COORDINATE_PRECISION), row, "coordinatePrecision");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_MINIMUM_ELEVATION), row, "minimumElevation");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_MAXIMUM_ELEVATION), row, "maximumElevation");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_MINIMUM_DEPTH), row, "minimumDepth");
			updater.addNumericMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_MAXIMUM_DEPTH), row, "maximumDepth");
			if (sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_STATE_OR_PROVINCE) != null) {
				updater.addViewLocalityStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_STATE_OR_PROVINCE), row, "state");
			}
			if (sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COUNTY) != null) {
				updater.addViewLocalityStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COUNTY), row, "county");
			}
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_INFORMATION_WITHHELD), row, "informationWithheld");
			String matchQuery = "";
			if (!updater.isMatchQueryNull()) {
				matchQuery = updater.getMatchQuery();


				// look for matching
				//matchQuery = updater.getMatchQuery();
				if (matchQuery.length() > 0) {
					try {
						String query = "select id from Locality where " + matchQuery;
						// System.out.println("Match query " + query);
						result = statement.executeQuery(query);
						if (result.next()) {// found a match
							localityId = result.getInt(1);
							System.out.println("Locality ref '" + localityRef + "' row " + row
									+ " matches id " + localityId);
							LoadData.log("Locality ref '" + localityRef + "' row " + row
									+ " matches id " + localityId);
						} else {// no match
							String insertQuery = "{call CreateObject( 'Locality', ?, ?, ?, now(), ?, '')}";
							insertStmt = LoadData.getConnection().prepareCall(insertQuery);
							int i = 1;
							if (sheetReader.GetUserId() == -1) return false;
							insertStmt.setInt(i++, sheetReader.GetUserId());
							if (sheetReader.GetGroupId() == -1) return false;
							insertStmt.setInt(i++, sheetReader.GetGroupId());
							if (sheetReader.GetSubmitterId() == -1) return false;
							insertStmt.setInt(i++, sheetReader.GetSubmitterId());
							// insertStmt.setString(i++, "2010-09-10");
							insertStmt.setString(i++, "New locality from upload");

							ResultSet res = insertStmt.executeQuery();
							res.next();
							localityId = res.getInt(1);
							System.out.println("Locality ref '" + localityRef + "' row " + row
									+ " added with id " + localityId);
							LoadData.log("Locality ref '" + localityRef + "' row " + row
									+ " added with id " + localityId);
							int numupdated = updater.update(localityId);
						}
						localityIds.put(localityRef, localityId);
					} catch (SQLException sql) {
						sql.printStackTrace();
//						System.exit(1);
					}
				}
			}
		}
		return true;
	}

	public static Integer getLocality(String localityRef) {
		Integer viewId = localityIds.get(localityRef);
		if (viewId == null) return null;
		return viewId;
	}
}
