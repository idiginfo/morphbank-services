package net.morphbank.loadexcel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jxl.Sheet;

/**
 * Use this class to add more tests against the database
 * for mb3a and mb3p workbooks
 * @author gjimenez
 *
 */
public class ValidateAgainstDatabase {

	private SheetReader sheetReader;
	private Connection conn;
	StringBuffer output;
	private String propertyFile;

	public Connection createConnection() {
		LoadData.setProperties(propertyFile);
		GetConnection newconnect = new GetConnection();
		return newconnect.openConnection(LoadData.dbHost, LoadData.dbName,
				LoadData.dbUserId, LoadData.dbPassword, LoadData.dbPort);
	}

	public ValidateAgainstDatabase(SheetReader sheetReader, StringBuffer output, String propertyFile) {
		this.sheetReader = sheetReader;
		this.output = output;
		this.propertyFile = propertyFile;
		this.conn = createConnection();
	}

	/**
	 * Check Taxa agains the database to see if all
	 * scientific names exist in the database and if not
	 * that a parent can be found for proper insert on upload
	 * @return true is no error has been found
	 */
	public boolean checkTaxa() {
		String sheetName = ExcelTools.SPECIMEN_TAXON_DATA_SHEET;
		int rows = sheetReader.GetRows(sheetName);
		int parentTsn = 0;
		boolean taxaIsOk = true;
		ArrayList<String[]> rowsToFix = new ArrayList<String[]>();
		for (int j = 1; j < rows; j++) {
			String family = sheetReader.getValue(sheetName, ExcelTools.COL_FAMILY, j);
			String genus = sheetReader.getValue(sheetName, ExcelTools.COL_GENUS, j);
			String scientificName = sheetReader.getValue(sheetName,
					ExcelTools.COL_SCIENTIFICNAMESTRING, j);
			if (scientificName.length() == 0) {
				continue;
			}
			int tsn = getFirstMatchingTaxon(scientificName, family);
			if (tsn == 0) {
				String code = sheetReader.getValue(sheetName,
						ExcelTools.COL_NOMENCLATURAL_CODE, j);
				if (code.equals("ICNCP")) { // it is a cultivar or hybrid
					if (scientificName.indexOf("'") > 0) { // cultivar
						parentTsn = findParent(j, family);
					} else {
						parentTsn = findParent(j, family);
						if (parentTsn < 1)
							continue;
					}
				} else { // regular scientificName
					parentTsn = findParent(j, family);
				}
				if (parentTsn <= 0 && !isGenusRowInSheet(j, family, genus)) {
					String noParentMessage = "No parent found for "
							+ scientificName + " family '" + family + "'.";
					rowsToFix.add(new String[]{String.valueOf(j), family, genus});
					System.out.println(noParentMessage);
					ExcelTools.messageToOutput(noParentMessage, output);
					taxaIsOk = false;
				}
			}
		}
		if (!taxaIsOk) {
			displayFixMessage(rowsToFix);
		}
		return taxaIsOk;
	}

	/**
	 * Displays a message at the end of checking to avoid
	 * duplication of the same fix
	 * @param rowsToFix
	 */
	private void displayFixMessage(ArrayList<String[]> rowsToFix) {
		ArrayList<String> genusDone = new ArrayList<String>();
		for (String[] line : rowsToFix) {
			if (!genusDone.contains(line[2])) {
				String message = "In "
						+ ExcelTools.SPECIMEN_TAXON_DATA_SHEET + " sheet, "
						+ "above row " + (Integer.parseInt(line[0]) + 1)
						+ ", insert Family: " + line[1] + " and "
						+ ExcelTools.COL_SCIENTIFICNAMESTRING + ": "
						+ line[2];
				genusDone.add(line[2]);
				System.out.println(message);
				ExcelTools.messageToOutput(message, output);
			}
		}
	}
	
	/**
	 * When a name does not exist, if the parent is on a row above it will get
	 * inserted during upload. Test if that row exists.
	 * 
	 * @return
	 */
	private boolean isGenusRowInSheet(int rowMax, String familyToCheck,
			String genusToCheck) {
		String sheetName = ExcelTools.SPECIMEN_TAXON_DATA_SHEET;
		for (int i = 0; i < rowMax; i++) {
			String family = sheetReader.getValue(sheetName,
					ExcelTools.COL_FAMILY, i);
			String scientificName = sheetReader.getValue(sheetName,
					ExcelTools.COL_SCIENTIFICNAMESTRING, i);
			String genus = sheetReader.getValue(sheetName,
					ExcelTools.COL_GENUS, i);
			if (familyToCheck.equals(family)
					&& genusToCheck.equals(scientificName)
					&& (genus == null || genus == "")) {
				return true;
			}
		}
		return false;
	}

	// public method to query the database and find the tsn of the parent
	public int getFirstMatchingTaxon(String name, String family) {
		int tsn = 0;
		int rank = 0;
		try {
			String selectTreeBySciName = "SELECT tsn,rank_id FROM Tree WHERE scientificName=?";
			PreparedStatement getTaxaStmt = conn
					.prepareStatement(selectTreeBySciName);
			getTaxaStmt.setString(1, name);
			getTaxaStmt.execute();
			ResultSet result = getTaxaStmt.getResultSet();
			while (result.next()) {
				tsn = result.getInt(1);
				rank = result.getInt(2);
				if (rank <= 140)
					return tsn;
				if (rank != 140) {
					if (matchFamily(tsn, family)) {
						return tsn;
					}
				}
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		return 0;
	}

	// public method to find the parent of the row
	public int findParent(int row, String family) {
		int parentTsn = 0;
		String parentName = this.createParentName(row);
		if (parentName.equals("")) {
			if (!family.equals("")) {
				parentName = family;
				parentTsn = getFirstMatchingTaxon(parentName, family);
			} else
				parentTsn = -1;
		} else
			parentTsn = getFirstMatchingTaxon(parentName, family);
		return parentTsn;
	}

	private String createParentName(int row) {
		String sheetName = ExcelTools.SPECIMEN_TAXON_DATA_SHEET;
		String genus = sheetReader.getValue(sheetName, ExcelTools.COL_GENUS,
				row);
		String subgenus = sheetReader.getValue(sheetName,
				ExcelTools.COL_SUBGENUS, row);
		String specificEpithet = sheetReader.getValue(sheetName,
				ExcelTools.COL_SPECIFIC_EPITHET, row);
		String subspecies = sheetReader.getValue(sheetName, ExcelTools.COL_SSP,
				row);
		String variety = sheetReader.getValue(sheetName, ExcelTools.COL_VAR,
				row);
		String forma = sheetReader.getValue(sheetName, ExcelTools.COL_FORMA,
				row);
		String parentName = ((genus + " " + subgenus).trim() + " "
				+ specificEpithet + " " + subspecies).trim();
		parentName = ((parentName + " " + variety).trim() + " " + forma).trim();
		return parentName;
	}

	public boolean matchFamily(int tsn, String family) {
		int levelTsn = tsn;
		String scientificName = "";
		while (true) {// go up the parents to family level
			Taxon taxon = new Taxon(levelTsn, conn);
			if (taxon.getTsn() <= 0)
				return false;
			int rank = taxon.getRankId();
			scientificName = taxon.getScientificName();
			if (rank < 140)
				return false; // passed family!
			if (rank == 140)
				break; // rank is 'family'
			levelTsn = taxon.getParentTsn();
		}
		if (scientificName.equals(family))
			return true;
		return false;
	}
	
	/**
	 * Checks if the group name and user name are in the database
	 * @return false is any of the above is not true
	 */
	public boolean checkCredentials(){
		boolean credentialsOK = true;
		Sheet imageCollection = sheetReader.getSheet(ExcelTools.IMAGE_COLLECTION_SHEET);
		String groupName = imageCollection.getCell(1, 7).getContents();
		String userName = imageCollection.getCell(1, 4).getContents();
		String grpStmt = "SELECT id FROM Groups WHERE groupName=?";
		String userStmt = "SELECT id FROM User WHERE uin=?";
		credentialsOK &= execStmt(grpStmt, "group name", groupName);
		credentialsOK &= execStmt(userStmt, "Contributor (morphbank username)", userName);
		return credentialsOK;
	}
	
	/**
	 * Create a statement and execute it. 
	 * If a row is returned, the value in the spreadsheet is correct
	 * @param stmt
	 * @param field the title of the cell in the spreadsheet (userd for error message)
	 * @param value corresponding value in the spreadsheet
	 * @return false is the value is not in the corresponding table
	 */
	public boolean execStmt(String stmt, String field, String value) {
		try {
			PreparedStatement getStmt = conn
					.prepareStatement(stmt);
			getStmt.setString(1, value);
			getStmt.execute();
			ResultSet result = getStmt.getResultSet();
			if (!result.next()) {
				String message = "In ImageCollection sheet, Morphbank " + field + " "
						+ value + " is not in the database.";
				if (field.equalsIgnoreCase("group name")) {
					message += "Please contact Morphbank if you want to add a new group.";
				}
				System.out.println(message);
				ExcelTools.messageToOutput(message, output);
				return false;
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
		return true;
	}

}
