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

//This class loads the  data into the Specimen table    //
//For each Specimen which is valid and a TSN exists in  //
//the current MorphBank                                 //
//                                                      //
//created by: Karolina Maneva-Jakimoska                 //
//                                                      //
//date:  Feb 3rd 2006                                   //
//////////////////////////////////////////////////////////
import java.io.*;
import java.util.*;
import java.sql.*;
import java.awt.*;
import javax.swing.*;

public class Specimen {
	final static String MYTYPE = "Specimen";
	public static HashMap<String, Integer> specimenIds = new HashMap<String, Integer>();

	private Statement statement;
	private SheetReader sheetReader;
	private ResultSet result;
	private int specimenId;
	private String record[][];
	CallableStatement insertStmt = null;
	Updater updater = null;

	private int tsn = 0;

	public Specimen(SheetReader sheetReader) {
		this.statement = LoadData.getStatement();
		this.sheetReader = sheetReader;
	}

	public boolean processSpecimens() {
		int rows = sheetReader.GetRows(MYTYPE);
		String scientificName = "";
		int k = 0;
		for (int row = 1; row < rows; row++) {
			//			String specimenRef = sheetReader.getEntry(MYTYPE, 23, row);
			String specimenRef = sheetReader.getValue(MYTYPE, ExcelTools.COL_SPECIMEN_DESCRIPTION, row);
			updater = new Updater(sheetReader, MYTYPE);

			//			scientificName = sheetReader.getEntry(MYTYPE, 1, row);
			scientificName = sheetReader.getValue(MYTYPE, ExcelTools.COL_SCIENTIFIC_NAME, row);
			if (scientificName.equals("")) continue;

			Taxon determination = new Taxon(scientificName);
			tsn = determination.getTsn();
			if (tsn == 0) {
				System.err.println("no tsn found for " + scientificName);
				LoadData.log("no tsn found for " + scientificName);
			}



			// System.out.println("tsn id is: " + tsnId);
			//			if (tsn == 0 && sheetReader.getEntry(MYTYPE, 2, row) != "") {
			if (tsn == 0 && sheetReader.getValue(MYTYPE, ExcelTools.COL_BASIS_OF_RECORD, row) != "") {
				LoadData.log("No tsn for Specimen in row " + (row + 1));
				continue;
			}

			

			updater.addNumericMatchColumn("tsnId", Integer.toString(tsn), row);
			scientificName = findTaxName(scientificName, determination.getParentTsn());

			updater.addStringColumn("taxonomicNames", scientificName);
			//updater.addStringColumn("taxonomicNames", getTaxonomicNames());

			// if TSN is valid check if Sex and Form are valid(validity
			// explained in the manual)
			//			updater.addNamedMatchColumn(2, row, "basisOfRecordId", "BasisOfRecord");
			//			updater.addNamedMatchColumn(3, row, "sex", "Sex");
			//			updater.addNamedMatchColumn(4, row, "developmentalStage", "DevelopmentalStage");
			//			updater.addNamedMatchColumn(5, row, "form", "Form");
			//			updater.addStringMatchColumn(6, row, "preparationType");
			//			updater.addStringMatchColumn(7, row, "individualCount");
			//			updater.addNamedMatchColumn(8, row, "typeStatus", "TypeStatus");
			//			updater.addStringMatchColumn(9, row, "name");
			//			updater.addStringMatchColumn(10, row, "dateIdentified");
			//			updater.addStringMatchColumn(11, row, "comment");
			//			updater.addStringMatchColumn(12, row, "institutionCode");
			//			updater.addStringMatchColumn(13, row, "collectionCode");
			//			updater.addStringMatchColumn(14, row, "catalogNumber");
			//			updater.addStringMatchColumn(15, row, "previousCatalogNumber");
			//			updater.addStringMatchColumn(16, row, "relatedCatalogItem");
			//			updater.addStringMatchColumn(17, row, "relationshipType");
			//			updater.addStringMatchColumn(18, row, "collectionNumber");
			//			updater.addStringMatchColumn(19, row, "collectorName");
			//			updater.addStringMatchColumn(20, row, "dateCollected");
			updater.addNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_BASIS_OF_RECORD), row, "basisOfRecordId", "BasisOfRecord");
			updater.addNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_SEX), row, "sex", "Sex");
			updater.addNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_DEVELOPMENTAL_STAGE), row, "developmentalStage", "DevelopmentalStage");
			updater.addNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_FORM), row, "form", "Form");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_PREPARATION_TYPE), row, "preparationType");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_NUMBER_OF_INDIVIDUALS), row, "individualCount");
			updater.addNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_TYPE_STATUS), row, "typeStatus", "TypeStatus");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_DETERMINED_BY), row, "name");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_DATE_DETERMINED), row, "dateIdentified");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_DETERMINATION_NOTES), row, "comment");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_INSTITUTION_CODE), row, "institutionCode");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COLLECTION_CODE), row, "collectionCode");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_CATALOG_NUMBER), row, "catalogNumber");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_PREVIOUS_CATALOG_NUMBER), row, "previousCatalogNumber");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_RELATED_CATALOG_ITEM), row, "relatedCatalogItem");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_RELATIONSHIP_TYPE), row, "relationshipType");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COLLECTION_NUMBER), row, "collectionNumber");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_COLLECTOR_NAME), row, "collectorName");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_DATE_COLLECTED), row, "dateCollected");
			if (sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_EARLIEST_DATE_COLLECTED) > -1) {
				updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_EARLIEST_DATE_COLLECTED), row, "earliestDateCollected");
			}
			if (sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_LATEST_DATE_COLLECTED) > -1 ) {
				updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_LATEST_DATE_COLLECTED), row, "latestDateCollected");
			}
			//			String localityDesc = sheetReader.getValue(MYTYPE, 23, row);
			String localityDesc = sheetReader.getValue(MYTYPE, "Locality", row);
			Integer localityId = Locality.getLocality(localityDesc);
			if (localityId != null) {
				updater.addNumericColumn("localityId", Integer.toString(localityId), row);
			}
			//			updater.addStringMatchColumn(22, row, "notes");
			updater.addStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, ExcelTools.COL_NOTES), row, "notes");

			specimenId = specimenExists();
			// System.out.println("Specimen id is " + specimenId);
			if (specimenId < 1) {
				try {
					String insertQuery = "{call CreateObject( 'Specimen', ?, ?, ?, ?, ?, '')}";
					insertStmt = LoadData.getConnection().prepareCall(insertQuery);
					int i = 1;
					if (sheetReader.GetUserId() == -1) return false;
					insertStmt.setInt(i++, sheetReader.GetUserId());
					if (sheetReader.GetGroupId() == -1) return false;
					insertStmt.setInt(i++, sheetReader.GetGroupId());
					if (sheetReader.GetSubmitterId() == -1) return false;
					insertStmt.setInt(i++, sheetReader.GetSubmitterId());
					//					insertStmt.setDate(i++, sheetReader.getReleaseDate());
					insertStmt.setString(i++, sheetReader.getReleaseDate());
					insertStmt.setString(i++, "New specimen from upload");

					ResultSet res = insertStmt.executeQuery();
					res.next();
					specimenId = res.getInt(1);
					int numupdated = updater.update(specimenId);
					LoadData.getExternalLinks().addSheetLinks(specimenId);

					System.out.println("Specimen ref '" + specimenRef + "' row " + row
							+ " added with id " + specimenId);
					LoadData.log("Specimen ref '" + specimenRef + "' row " + row
							+ " added with id " + specimenId);
				} catch (SQLException sql) {
					sql.printStackTrace();
//					System.exit(1);
				}

				// System.out.println("New specimen with ObjectId" + record
				k++;
				// ExternalLinks newlink = new ExternalLinks(statement,
				// sheetReader, specimenId, 0);
			} else {
				System.out.println("Specimen ref '" + specimenRef + "' row " + row + " matches "
						+ specimenId);
				LoadData.log("Specimen ref '" + specimenRef + "' row " + row + " matches "
						+ specimenId);
			}
			specimenIds.put(specimenRef, specimenId);
		}
		return true;
	}

	/**
	 * Get the referenced specimen from the list of specimen ids
	 * 
	 * @param specimenRef
	 * @return
	 */
	public static int getSpecimen(String specimenRef) {
		Integer specimenId = specimenIds.get(specimenRef);
		if (specimenId == null) return 0;
		return specimenId;
	}

	// public method that finds all TaxonNames in a row
	public String findTaxName(String scientificName, int parentTsn) {
		String names = scientificName;
		while (parentTsn > 0) {
			try {
				String query = "SELECT parent_tsn, scientificName FROM Tree WHERE tsn=" + parentTsn;
				// System.out.println("tax name query: " + query);
				result = statement.executeQuery(query);
				if (!result.next()) {
					System.out.println("No match in tree for scientific name '" + scientificName
							+ "' tsn " + parentTsn);
					LoadData.log("No match in tree for scientific name '" + scientificName
							+ "' tsn " + parentTsn);
				} else {
					parentTsn = result.getInt(1);
					names = result.getString(2).trim() + " " + names;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
//				System.exit(1);
			}
		}
		return names;
	}

	public int specimenExists() {
		String matchQuery = "SELECT id FROM Specimen WHERE " + updater.getMatchQuery();
		// System.out.println("Specimen match query: " + matchQuery);
		try {
			result = statement.executeQuery(matchQuery);
			if (result.next()) {
				return result.getInt(1);
			}
			return 0;
		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
}
