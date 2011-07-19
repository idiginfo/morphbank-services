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

//Class for extraction of data in the View spreadsheet   /
//Reads all the records(row by row) if there is a match  /
//skips the row otherwise makes new entry.Each view for  /
//which there is no Specimen provided is not entered     /
//into the database                                      /
//                                                       /
//    created by: Karolina Maneva-Jakimoska              /
//          date: Jan 27th 2006                          /
// last modified: Feb 08th 2006                          /     
//////////////////////////////////////////////////////////

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

public class View {
	final static String MYTYPE = "View";

	public static HashMap<String, Integer> viewIds = new HashMap<String, Integer>();

	private Statement statement;
	private SheetReader sheetReader;
	private int rows;
	private int viewId;
	private int viewTsn;
	private String record[][];
	private ResultSet result;
	CallableStatement insertStmt = null;
	Updater updater = null;
	public static String FOAF_URI = "http://xmlns.com/foaf/spec/";

	// constructor for the class View
	public View(SheetReader sheetReader) {
		this.statement = LoadData.getStatement();
		this.sheetReader = sheetReader;
	}

	public void processViews() {
		rows = sheetReader.GetRows(MYTYPE);
		// System.out.println("MyView has " + columns + "columns and " + rows +
		// "rows" );

		for (int row = 1; row < rows; row++) {
			if (sheetReader.getEntry(MYTYPE, 9, row).equals("View Applicable to Taxon")) continue;
			// if (!isValidView(row)) continue;

			updater = new Updater(sheetReader, MYTYPE);
			String viewRef = sheetReader.getEntry(MYTYPE, 1, row);
			if (viewRef.length() == 0) continue;
			String viewName = viewRef;
			int index = viewRef.indexOf('-');
			viewName = viewRef.substring(index + 2);

//			updater.addStringMatchColumn(2, row, "specimenPart");
//			updater.addStringMatchColumn(3, row, "viewAngle");
//			updater.addStringMatchColumn(4, row, "imagingTechnique");
//			updater.addStringMatchColumn(5, row, "imagingPreparationTechnique");
//			updater.addStringMatchColumn(6, row, "developmentalStage");
//			updater.addNamedMatchColumn(7, row, "sex", "Sex");
//			updater.addNamedMatchColumn(8, row, "form", "Form");
//			updater.addStringColumn("viewName", viewName);
			updater.addViewStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Specimen Part"), row, "specimenPart");
			updater.addViewStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "View Angle"), row, "viewAngle");
			updater.addViewStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Imaging Technique"), row, "imagingTechnique");
			updater.addViewStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Imaging Preparation Technique"), row, "imagingPreparationTechnique");
			updater.addViewStringMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Developmental Stage"), row, "developmentalStage");
			updater.addViewNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Sex"), row, "sex", "Sex");
			updater.addViewNamedMatchColumn(sheetReader.getColumnNumberByName(MYTYPE, "Form"), row, "form", "Form");
			updater.addStringColumn("viewName", viewName);
			String matchQuery = "";
			if (!updater.isMatchQueryNull()) {
				matchQuery = updater.getMatchQuery();
			}
			if (matchQuery.length() > 0) {

				String taxonName = sheetReader.getValue(MYTYPE, "View Applicable to Taxon", row);
				
				// System.out.println("Before getTsnFromName Row " + row +
				// " name: " + taxonName);
				Taxon taxon = new Taxon(taxonName);
				viewTsn = taxon.getTsn();
				if (viewTsn != 0) {
					updater.addNumericMatchColumn("viewTSN", Integer.toString(viewTsn), row);
				}
				if (sheetReader.getEntry(MYTYPE, 1, row).equals(
						"My ViewName[ Auto generated, do not change this field!]")) {
					continue;
				}
				viewId = getExistingViewId(row);
				if (viewId != 0) {
					System.out.println("View " + row + " ref '" + viewRef + "' matches " + viewId);
					viewIds.put(viewRef, viewId);
					continue;
				}
				// System.out.println("ViewName is: " + viewName);
				String description = "New view object was added using Excel file";
				try {
					String insertQuery = "{call CreateObject( 'View', ?, ?, ?, ?, ?, '')}";
					insertStmt = LoadData.getConnection().prepareCall(insertQuery);
					int i = 1;
					insertStmt.setInt(i++, sheetReader.GetUserId());
					insertStmt.setInt(i++, sheetReader.GetGroupId());
					insertStmt.setInt(i++, sheetReader.GetSubmitterId());
					insertStmt.setDate(i++, sheetReader.getReleaseDate());
					insertStmt.setString(i++, "New view from upload");
					ResultSet res = insertStmt.executeQuery();
					res.next();
					viewId = res.getInt(1);
					updater.update(viewId);
					System.out.println("View ref '" + viewRef + "" + "' row " + row
							+ " added with id " + viewId);
					LoadData.getExternalLinks().addSheetLinks(viewId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			viewIds.put(viewRef, viewId);
		}
	}

	public static int getView(String viewRef) {
		Integer viewId = viewIds.get(viewRef);
		if (viewId == null) return 0;
		return viewId;
	}

	public int getExistingViewId(int row) {
		int id = 0;
		if (updater.getMatchQuery().length() == 0) return 0;
		String checkViewSql = "SELECT id,viewTSN FROM View WHERE " + updater.getMatchQuery();
		System.out.println("Check view: " + checkViewSql);
		try {
			result = statement.executeQuery(checkViewSql);
			if (result.next()) {// record exists,read new row from the excel
				// sheet
				id = result.getInt(1);
				// System.out.println("View " + row + " matches " + id);
				// Check if it is exactly the same view or viewTSN differs,
				// update View accordingly
				int dbViewTsn = result.getInt(2);
				String viewApplTaxon = sheetReader.getValue(MYTYPE, "View Applicable to Taxon", row);
				// TODO expose this action outside of method-- hidden side
				// effect
				ViewUpdate(id, dbViewTsn, viewApplTaxon);
				return id;
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
			System.exit(1);
		}
		return 0;
	}

	/**
	 * check if the View is used for an Image
	 * 
	 * @param row
	 * @param statement
	 * @param myread
	 * @return
	 */
	public boolean isValidView(int row) {
		int imageRows = sheetReader.GetRows("Image");
		int specimenRows = sheetReader.GetRows("Specimen");
		int viewTsn = 0;
//		String viewName = sheetReader.getEntry(MYTYPE, 1, row);
		String viewName = sheetReader.getValue(MYTYPE, "My View Name", row);
		// System.out.println("view name is " + viewName);
		String specDesc = "";
		String imageViewName = "";
		String temp = "";

		// Looking for a cooresponding Image to a provided ViewName
		for (int imageRow = 1; imageRow < imageRows; imageRow++) {
//			imageViewName = sheetReader.getEntry("Image", 2, imageRow);
			imageViewName = sheetReader.getValue("Image", "My View Name", imageRow);
			if ((imageViewName.length() == 0) || (!viewName.equals(imageViewName))) {
				continue;
			}
//			specDesc = sheetReader.getEntry("Image", 1, imageRow);
			specDesc = sheetReader.getValue("Image", "Specimen Description", imageRow);
			if (specDesc.length() == 0) {
				LoadData.log("No specimen description for image " + (imageRow + 1));
				return true;// no specimen for view!
			}
//			String entry = sheetReader.getEntry(MYTYPE, 9, 0);
			String entry = sheetReader.getValue(MYTYPE, "View Applicable to Taxon", 0);
			if (entry.equals("View Applicable to Taxon")) {
//				String taxon = sheetReader.getEntry(MYTYPE, 9, row);
				String taxon = sheetReader.getValue(MYTYPE, "View Applicable to Taxon", row);
				// System.out.println("Taxon is " + taxon);
				temp = "SELECT tsn FROM Tree WHERE scientificName=\"" + taxon + "\"";
				// System.out.println("Row " + row + "query: " + temp);
				try {
					result = statement.executeQuery(temp);
					if (result.next()) {
						viewTsn = result.getInt(1);
						// System.out.println("View TSN is " + viewTsn);
						// System.out.println("SpecimenDesc " + specDesc +
						// "image has row " + row);
						return compareTsn(specDesc, specimenRows, row);
					} else {
						LoadData.log("No tsn for the highest taxon for view " + (row + 1));
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
			if (imageRow == (imageRows) && (specDesc.equals(""))) {
				LoadData.log("View row " + (row + 1) + " not used for any Specimen");
				return false;
			}
		}
		return false;
	}

	// public method that compares the tsn of the view and the Specimen applied
	// to
	public boolean compareTsn(String specimenDescription, int specimenRows, int j) {
		String temp = "";
		int viewKingdom = 0;
		int specimenKingdom = 0;
		int specimenRank = 0;
		int viewRank = 0;
		for (int specimenRow = 1; specimenRow < specimenRows; specimenRow++) {
//			String specDesc = sheetReader.getEntry("Specimen", 23, specimenRow);
			String specDesc = sheetReader.getValue("Specimen", "Specimen Description [Autogenerated -- do not change!]", specimenRow);
			if (!specimenDescription.equals(specDesc)) continue;

			// System.out.println("I found a match");
//			String name = sheetReader.getEntry("Specimen", 1, specimenRow);
			String name = sheetReader.getValue("Specimen", "Scientific Name", specimenRow);
			Taxon taxon = new Taxon(name);
			int tsn = taxon.getTsn();
			// System.out.println("TSN from Specimen is " + tsn);
			if (tsn > 0 && viewTsn > 0) {
				temp = "SELECT kingdom_id,rank_id FROM Tree WHERE tsn=" + viewTsn;
				try {
					result = statement.executeQuery(temp);
					if (result.next()) {
						viewKingdom = result.getInt(1);
						viewRank = result.getInt(2);
						// System.out.println("kingdom and rank from View are "
						// + viewKingdom + " "
						// + viewRank);
					}
				} catch (SQLException sql) {
					sql.printStackTrace();
					System.exit(1);
				}
				temp = "SELECT kingdom_id,rank_id FROM Tree WHERE tsn=" + tsn;
				// System.out.println(temp);
				try {
					result = statement.executeQuery(temp);
					if (result.next()) {
						specimenKingdom = result.getInt(1);
						specimenRank = result.getInt(2);
						// System.out.println("kingdom and rank from Specimen are"
						// + specimenKingdom
						// + specimenRank);
					}
				} catch (SQLException sql) {
					sql.printStackTrace();
					System.exit(1);
				}
				if ((specimenKingdom == viewKingdom)
						&& ((viewRank == specimenRank) || (viewRank < specimenRank))) {
					// System.out.println("View is ok I will return true");
					return true;
				}
				LoadData.log("View row " + (specimenRow + 1)
						+ "and Specimen in different kingdoms or " + "View has lower rank");
			}
		}
		return false;
	}

	// public method that updates View if the ViewTSN needs to go higher for
	// existing view
	public boolean ViewUpdate(int viewId, int dbViewTsn, String viewApplTaxon) {
		String temp = "";
		// two select statement for kingdom and rank
		// if kingdom the same put the lower value for rank(higher in the
		// hierarchy)
		temp = "SELECT tsn, kingdom_id, rank_id, parent_tsn FROM Tree where unit_name1='"
				+ viewApplTaxon + "' OR tsn=" + dbViewTsn;
		// System.out.println(temp);
		try {
			result = statement.executeQuery(temp);
			result.last();
			int rows = result.getRow();
			if (rows == 1) return true;
			if (rows > 1) {
				result.first();
				String tsn_sheet = result.getString(1).trim();
				String kingdom_sheet = result.getString(2).trim();
				int rank_sheet = Integer.parseInt(result.getString(3).trim());
				String parent_sheet = result.getString(4).trim();
				// result.next();
				String tsn_db = result.getString(1).trim();
				String kingdom_db = result.getString(2).trim();
				int rank_db = Integer.parseInt(result.getString(3).trim());
				String parent_db = result.getString(4).trim();
				if (kingdom_sheet.equals(kingdom_db)) {
					if (rank_sheet < rank_db) {
						temp = "UPDATE View set viewTSN=" + tsn_sheet + " WHERE id=" + viewId;
						statement.executeUpdate(temp);
						return true;
					}
					if (rank_sheet == rank_db) {
						if (parent_sheet.equals(parent_db)) {
							temp = "UPDATE View set viewTSN=" + parent_sheet + " WHERE id="
									+ viewId;
							statement.executeUpdate(temp);
							return true;
						}
						String new_View_tsn = FindLowestParent(parent_sheet, parent_db);
						if (!new_View_tsn.equals("")) {
							temp = "Update View set viewTSN=" + new_View_tsn + " WHERE id="
									+ viewId;
							statement.executeUpdate(temp);
							return true;
						}
						return false;
					}
				} else
					return false;
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
			System.exit(1);
		}
		return true;
	}

	/**
	 * look for Lowest Parent in the Tree
	 * 
	 * @param tsn_db
	 * @param tsn_sheet
	 * @return
	 */
	public String FindLowestParent(String tsn_db, String tsn_sheet) {
		String parent_sheet = tsn_sheet;
		String parent_db = tsn_db;
		String temp = "", temp1 = "";
		while (!parent_db.equals("0")) {
			temp = "SELECT parent_tsn FROM Tree where tsn=" + parent_db;
			temp1 = "SELECT parent_tsn FROM Tree where tsn=" + parent_sheet;
			try {
				result = statement.executeQuery(temp);
				String parent_db_q = result.getString(1).trim();
				result = statement.executeQuery(temp1);
				String parent_sheet_q = result.getString(1).trim();
				if (parent_db.equals(parent_sheet)) return parent_db_q;
				parent_db = parent_db_q;
				parent_sheet = parent_sheet_q;
				continue;
			} catch (SQLException sql) {
				sql.printStackTrace();
				System.exit(1);
			}
		}
		return "";
	}

}
