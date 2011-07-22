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

//This file loads new images in the taxon name hierarchy        /
//                                                              /
//created by: Karolina Maneva-Jakimoska                         /
//      date: March 23 2007                                     /
//                                                              /
/////////////////////////////////////////////////////////////////

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import jxl.*;

public class TaxonData {
	public static String MYTYPE = "Taxon";
	public static HashMap<String, Integer> taxonIds = new HashMap<String, Integer>();

	private Statement statement;
	private SheetReader sheetReader;
	int tsn;
	QueryParams queryParams = null;

	private ResultSet result;
	private String authorId = "";
	private String family = "";
	private String genus = "";
	private String subgenus = "";
	private String specificEpithet = "";
	private String subspecies = "";
	private String variety = "";
	private String forma = "";
	private String scientificName = "";
	private int parentTsn = 0;
	private int rankId = 0;
	private int kingdomId = 0;
	private String tradeName = null;
	private Integer taxonAuthorId = null;
	private String code = null;
	private String status = "not public";
	private String typeName = "";
	private String letter = "";
	private String publicationId = null;
	private String pages = null;
	private String nameSource = null;
	private String comments = null;
//	private Date dateToPublish = null;
	private String dateToPublish = "";
	private String parentName = null;

	private PreparedStatement getTaxaStmt = null;
	private CallableStatement insertStmt = null;

	// constructor, takes the connection,the statement
	// and the read from excel sheet as parameters
	public TaxonData(SheetReader sheetReader) {
		this.sheetReader = sheetReader;
		this.statement = LoadData.getStatement();
		String selectTreeBySciName = "SELECT tsn,rank_id FROM Tree WHERE scientificName=?";
		String insertQuery = "{call TreeInsert(?,?,?,?,?, ?,?,?,?,?,"
			+ "?,?,?,?,?, ?,?,?)}";
		try {
			getTaxaStmt = LoadData.getConnection().prepareStatement(
					selectTreeBySciName);
			insertStmt = LoadData.getConnection().prepareCall(insertQuery);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void processTaxa() {
		int rows = sheetReader.GetRows(MYTYPE);

		for (int j = 1; j < rows; j++) {
			queryParams = new QueryParams(statement, sheetReader, MYTYPE);

			// for all initialized rows on the excel spreadsheet that are not
			// empty
			//			family = sheetReader.getEntry(MYTYPE, 0, j);
			//			genus = sheetReader.getEntry(MYTYPE, 1, j);
			//			subgenus = sheetReader.getEntry(MYTYPE, 2, j);
			//			specificEpithet = sheetReader.getEntry(MYTYPE, 3, j);
			//			subspecies = sheetReader.getEntry(MYTYPE, 4, j);
			//			variety = sheetReader.getEntry(MYTYPE, 5, j);
			//			forma = sheetReader.getEntry(MYTYPE, 6, j);
			//			scientificName = sheetReader.getEntry(MYTYPE, 7, j);
			family = sheetReader.getValue(MYTYPE, "Family", j);
			genus = sheetReader.getValue(MYTYPE, "Genus", j);
			subgenus = sheetReader.getValue(MYTYPE, "Subgenus", j);
			specificEpithet = sheetReader.getValue(MYTYPE, "Specific epithet", j);
			subspecies = sheetReader.getValue(MYTYPE, "ssp.", j);
			variety = sheetReader.getValue(MYTYPE, "var.", j);
			forma = sheetReader.getValue(MYTYPE, "forma", j);
			dateToPublish = sheetReader.getReleaseDate();
			scientificName = sheetReader.getValue(MYTYPE, "ScientificNameString", j);
			if (scientificName.length() == 0) {
				continue;
			}
			tsn = getFirstMatchingTaxon(scientificName);
			if (tsn != 0) {
				System.out.println("Taxon for row " + j + " '" + scientificName
						+ "' is " + tsn);
			} else {
				letter = scientificName.substring(0, 1);
				// System.out.println("family is " + family);
				//				authorId = sheetReader.getEntry(MYTYPE, 8, j);
				//				taxonAuthorId = findAuthor(authorId);
				//				publicationId = sheetReader.getEntry(MYTYPE, 9, j);
				//				pages = sheetReader.getEntry(MYTYPE, 10, j);
				//				tradeName = sheetReader.getEntry(MYTYPE, 11, j);
				//				code = sheetReader.getEntry(MYTYPE, 12, j);
				authorId = sheetReader.getValue(MYTYPE, "Taxon Author(s),year (use if not in morphbank)", j);
				taxonAuthorId = findAuthor(authorId);
				publicationId = sheetReader.getValue(MYTYPE, "Morphbank Publication Id", j);
				pages = sheetReader.getValue(MYTYPE, "Publication Pages", j);
				tradeName = sheetReader.getValue(MYTYPE, "TradeDesignationNames", j);
				code = sheetReader.getValue(MYTYPE, "Nomenclatural Code", j);
				nameSource = sheetReader.getValue(MYTYPE, "Name Source", j);
				comments = sheetReader.getValue(MYTYPE, "Comments", j);
				if (code.equals("ICNCP")) { // it is a cultivar or hybrid
					if (scientificName.indexOf("'") > 0) { // cultivar
						typeName = "Cultivar name";
						//						usage = "public";
						this.setStatus();
						parentTsn = findParent();
						rankId = 280;
					} else {
						typeName = "Regular scientific name";
						//						usage = "public";
						this.setStatus();
						parentTsn = findParent();
						if (parentTsn < 1) continue;
						rankId = FindRank();
					}
				} else { // regular scientificName
					//					usage = "public";
					this.setStatus();
					typeName = "Regular scientific name";
					parentTsn = findParent();
					rankId = FindRank();
				}
				if (parentTsn <= 0) {
					System.out.println("No parent found for " + scientificName
							+ " family '" + family + ": processing aborted");
					System.exit(0);
				}
				kingdomId = sheetReader.GetKingdom(parentTsn);
				tsn = createTaxon();
				System.out.println("Taxon created for row " + j + " '"
						+ scientificName + "' is " + tsn);
			}
			taxonIds.put(scientificName, tsn);
		}
	}

	// public method to find the parent of the row
	public int findParent() {
		int parentTsn = 0;
		parentName = this.createParentName();
		//		String parentName = ((genus + " " + subgenus).trim() + " "
		//				+ specificEpithet + " " + subspecies).trim();
		//		parentName = ((parentName + " " + variety).trim() + " " + forma).trim();
		if (parentName.equals("")) {
			if (!family.equals("")) {
				parentName = family;
				parentTsn = getFirstMatchingTaxon(parentName);
			} else
				parentTsn = -1;
		} else
			parentTsn = getFirstMatchingTaxon(parentName);
		return parentTsn;
	}// end of method FindParent

	// public method to query the database and find the tsn of the parent
	public int getFirstMatchingTaxon(String name) {
		int tsn = 0;
		int rank = 0;
		try {
			// PreparedStatement parentStmt =
			// LoadData.getConnection().prepareStatement(select);
			getTaxaStmt.setString(1, name);
			getTaxaStmt.execute();
			ResultSet result = getTaxaStmt.getResultSet();
			while (result.next()) {
				tsn = result.getInt(1);
				rank = result.getInt(2);
				if (rank <= 140) return tsn;
				if (rank != 140) {
					if (matchFamily(tsn, family)) { return tsn; }
				}
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
			System.exit(1);
		}
		return 0;
	}

	public boolean matchFamily(int tsn, String family) {
		int levelTsn = tsn;
		String scientificName = "";
		while (true) {// go up the parents to family level
			Taxon taxon = new Taxon(levelTsn);
			if (taxon.getTsn() <= 0) return false;
			int rank = taxon.getRankId();
			scientificName = taxon.getScientificName();
			if (rank < 140) return false; // passed family!
			if (rank == 140) break; // rank is 'family'
			levelTsn = taxon.getParentTsn();
		}
		if (scientificName.equals(family)) return true;
		return false;
	}

	public boolean findFamilyInTree(int tsn) {
		ResultSet result = null;
		String familyQuery = "select tsn,scientificname from TaxonBranchNode where child="
			+ tsn + " and rank='family'";
		try {
			result = statement.executeQuery(familyQuery);
			if (result.next()) {
				int familyTsn = result.getInt(1);
				String name = result.getString(2);
				if (name.equals(family)) {
					System.out.println("Family " + name + " matches");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}// end of function FindFamily

	// public method that returns the rank for the new name
	public int FindRank() {
		int rank = 0;
		if (specificEpithet.length() != 0) {
			// below species
			if (scientificName.indexOf("f.") > -1) {
				rank = 260;
			} else if (scientificName.indexOf("var.") > -1) {
				rank = 240;
			} else {
				rank = 230;
			}
		} else if ((scientificName.indexOf("(") < 0 && genus.length() != 0)
				|| (scientificName.indexOf("(") > -1 && subgenus.length() != 0)) {
			rank = 220; // species
		} else if (scientificName.indexOf("(") > -1 && subgenus.length() == 0) {
			rank = 190; // subgenus
		} else {
			rank = 180; // genus
		}
		return rank;
	}// end of method FindRank

	public Vector<Integer> getTsns(String scientificName) {
		String temp = "SELECT tsn FROM Tree WHERE scientificName=\""
			+ scientificName
			+ "\" and `usage` not in ('not accepted','invalid')";
		Integer tsn = null;
		Vector<Integer> tsns = new Vector<Integer>();
		// System.out.println(temp);
		try {
			ResultSet result = statement.executeQuery(temp);
			while (result.next()) {
				tsns.add(result.getInt(1));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			System.exit(1);
		}
		return tsns;
	}

	public boolean nameExists() {
		Vector<Integer> tsns = getTsns(scientificName);
		if (tsns.size() != 0) {
			tsn = tsns.get(0);
			return true;
		}
		return false;
	}

	// public method that writes the new entry into corresponding tables
	public int createTaxon() {
		int tsn = 0;
		String description = "From an Excel file";
		try {
			int j = 1;
			// IN iNameSpace VARCHAR(32),
			insertStmt.setNull(j++, java.sql.Types.VARCHAR);
			// IN iStatus VARCHAR(32),
			// IN iSubBy INT,
			insertStmt.setString(j++, status);
			insertStmt.setInt(j++, sheetReader.GetSubmitterId());
			// IN iGroupId INT,
			// IN iUserId INT,
			insertStmt.setInt(j++, sheetReader.GetGroupId());
			insertStmt.setInt(j++, sheetReader.GetUserId());
			// IN iDateToPublish DATETIME,
//			insertStmt.setDate(j++, dateToPublish);
			insertStmt.setString(j++, dateToPublish);
			// IN iUnacceptReason VARCHAR(50),
			insertStmt.setNull(j++, java.sql.Types.VARCHAR);
			// IN iParentTsn BIGINT,
			insertStmt.setInt(j++, parentTsn);
			// IN iKingdomId SMALLINT,
			// IN iRankId SMALLINT,
			insertStmt.setInt(j++, kingdomId);
			insertStmt.setInt(j++, rankId);
			// IN iLetter VARCHAR(1),
			// IN iScientificName TEXT,
			insertStmt.setString(j++, letter);
			insertStmt.setString(j++, scientificName);

			// IN iTaxonAuthorId INT,
			if (taxonAuthorId == null) {
				// work around something strange in java on dev
				// Incorrect integer value: 'null' for column 'iTaxonAuthorId'
				// at row 1
				insertStmt.setInt(j++, 0);
			} else {
				insertStmt.setInt(j++, taxonAuthorId);
			}
			// IN iPublicationId INT,
			try {// if publication id is a number, use it, o/w use null
				int pubId = Integer.valueOf(publicationId);
				insertStmt.setInt(j++, pubId);
			} catch (Exception e) {
				// insertStmt.setNull(j, java.sql.Types.INTEGER);
				insertStmt.setInt(j++, 0);
			}
			// IN iPages VARCHAR(32),
			insertStmt.setString(j++, pages);
			// IN iNameType VARCHAR(32),
			insertStmt.setString(j++, typeName);
			// IN iNameSource VARCHAR(64),
			// IN iComments TEXT)
			insertStmt.setString(j++, nameSource);
			insertStmt.setString(j++, comments);
			//			insertStmt.setNull(j++, java.sql.Types.VARCHAR);
			//			insertStmt.setNull(j++, java.sql.Types.VARCHAR);



			// System.out.println(j + " parameters on call");

			boolean res = insertStmt.execute();
			ResultSet result = insertStmt.getResultSet();
			result.next();
			tsn = result.getInt(1);
			//this.updateMissingFields();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsn;
	}

	private Integer findAuthor(String authorName) {
		int author = 0;
		if (authorName == null || authorName.length() == 0) return null;
		String temp = "SELECT taxon_Author_Id FROM TaxonAuthors WHERE taxon_author='"
			+ authorName + "'";
		try {
			result = statement.executeQuery(temp);
			if (result.next()) {
				author = result.getInt(1);
			} else { // author is not in the database, create an entry
				temp = "SELECT max(taxon_Author_Id)+1 AS authorId FROM TaxonAuthors";
				result = statement.executeQuery(temp);
				if (result.next()) {
					author = result.getInt(1);

					temp = "INSERT INTO TaxonAuthors (taxon_Author_Id,taxon_author,update_date,kingdom_id)"
						+ " VALUES("
						+ author
						+ ",'"
						+ authorName
						+ "',NOW()," + kingdomId + ")";
					statement.executeUpdate(temp);
				} else {
					System.out.println("Problems querying the database");
					System.exit(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return author;
	}

	public static int getTaxon(String taxonRef) {
		Integer taxonId = taxonIds.get(taxonRef);
		if (taxonId == null) return 0;
		return taxonId;
	}

	private void setStatus() {
		java.util.Date now = new java.util.Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dateToPublishFormatted = Date.valueOf(format.format(dateToPublish));
//		releaseDate = Date.valueOf(format.format(datecell.getDate()));
		if (dateToPublishFormatted.before(new Date(now.getTime()))) {
			status = "public";
		}
	}

	private String createParentName() {
		parentName = ((genus + " " + subgenus).trim() + " "
				+ specificEpithet + " " + subspecies).trim();
		parentName = ((parentName + " " + variety).trim() + " " + forma).trim();
		return parentName;
	}

	
}
