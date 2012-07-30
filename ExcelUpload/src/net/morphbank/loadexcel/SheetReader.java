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

//main file for reading and extracting data from Excel file    /
//First workbook is created and for each existing excel sheet  /
//a separate Sheet object is created. These object will be     /
//used for direct access of data in the worksheet               /
//                                                             /
//created by: Karolina Maneva-Jakimoska                         /
//date     : Jan 20 2006                                       /
//modified : September 29 2006                                 /
////////////////////////////////////////////////////////////////

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import jxl.Cell;
import jxl.DateCell;
import jxl.Sheet;
import jxl.StringFormulaCell;
import jxl.Workbook;

// start of public class SheetReader                               
public class SheetReader {

	private Sheet specimenSheet;
	private Sheet localitySheet;
	private Sheet imageSheet;
	private Sheet taxonSheet;
	private Sheet viewSheet;
	private Sheet supportDataSheet;
	private Sheet extLinkSheet;
	private Sheet imageCollectionSheet;
	private Sheet protectedDataSheet;
	
	private String fname;
	private GetConnection connect = null;
	private ResultSet result;
	private Statement statement;
	private ResultSetMetaData metadata;
//	private java.sql.Date releaseDate = null;
	private String releaseDate;

	protected String[] headersView = null;
	protected String[] headersImage= null;
	protected String[] headersSpecimen = null;
	protected String[] headersLocality= null;
	protected String[] headersTaxon = null;
	protected String[] headersExtLink = null;
	protected String[] headersSupportData = null;
	protected String[] headersProtectedData = null;
	int numFields;
	
	// constructor for the class SheetReader;
	// it takes the file name as a parameter
	public SheetReader(String filename, GetConnection conn) {
		connect = conn;
		fname = filename;

		result = null;
		statement = null;
		metadata = null;

		try {
			Workbook workbook = Workbook.getWorkbook(new File(fname));
			// extract the sheets from a formed workbook
			imageCollectionSheet = workbook.getSheet(0);
			imageSheet = workbook.getSheet(1);
			viewSheet = workbook.getSheet(2);
			taxonSheet = workbook.getSheet(4);
			specimenSheet = workbook.getSheet(3);
			localitySheet = workbook.getSheet(5);
			extLinkSheet = workbook.getSheet(6);
			supportDataSheet = workbook.getSheet(7);
			protectedDataSheet = workbook.getSheet(9);
			readHeaders();
			setReleaseDate();

		} catch (Exception ioexception) {
			ioexception.printStackTrace();
		}
	}// end of SheetReader constructor

	public String getEntry(String sheetName, int col, int row) {
		Sheet sheet = getSheet(sheetName);
		if (sheet == null) return "";
		Cell cell = sheet.getCell(col, row);
		if (cell.getType().toString().equalsIgnoreCase("Date")) 
		{
			DateCell datecell = (DateCell) cell;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(datecell.getDate());
		}
		if (cell.getType().toString().equalsIgnoreCase("String Formula")) {
			StringFormulaCell formulaCell = (StringFormulaCell) cell;
			return formulaCell.getContents().trim();
		}
		return sheet.getCell(col, row).getContents().trim();
	}

	public Sheet getSheet(String sheetName) {
		if ("View".equals(sheetName) || sheetName.equals(ExcelTools.VIEW_SHEET)) {
			return viewSheet;
		} else if ("Specimen".equals(sheetName) || sheetName.equals(ExcelTools.SPECIMEN_SHEET)) {
			return specimenSheet;
		} else if ("Image".equals(sheetName) || sheetName.equals(ExcelTools.IMAGE_SHEET)) {
			return imageSheet;
		} else if ("Locality".equals(sheetName) || sheetName.equals(ExcelTools.LOCALITY_SHEET)) {
			return localitySheet;
		} else if ("SupportData".equals(sheetName) || sheetName.equals(ExcelTools.SUPPORTING_DATA_SHEET)) {
			return supportDataSheet;
		} else if ("ImageCollection".equals(sheetName) || sheetName.equals(ExcelTools.IMAGE_COLLECTION_SHEET)) {
			return imageCollectionSheet;
		} else if ("Taxon".equals(sheetName) || sheetName.equals(ExcelTools.SPECIMEN_TAXON_DATA_SHEET)) {
			return taxonSheet;
		} else if ("ExternalLinks".equals(sheetName) || sheetName.equals(ExcelTools.EXT_LINK_SHEET)) {
			return extLinkSheet;
		} else if (sheetName.equals(ExcelTools.PROTECTED_DATA_SHEET)) {
			return protectedDataSheet;
		}
		return null;
	}

	
	public void readHeaders() {
		numFields = viewSheet.getColumns();
		headersView = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersView[i] = viewSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = imageSheet.getColumns();
		headersImage = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersImage[i] = imageSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = specimenSheet.getColumns();
		headersSpecimen = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersSpecimen[i] = specimenSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = localitySheet.getColumns();
		headersLocality = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersLocality[i] = localitySheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = taxonSheet.getColumns();
		headersTaxon = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersTaxon[i] = taxonSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = extLinkSheet.getColumns();
		headersExtLink = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersExtLink[i] = extLinkSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = supportDataSheet.getColumns();
		headersSupportData = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersSupportData[i] = supportDataSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = protectedDataSheet.getColumns();
		headersProtectedData = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersProtectedData[i] = protectedDataSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
	}
	
	private String[] getHeaders(String sheet) {
		if (sheet.equalsIgnoreCase("View") || sheet.equalsIgnoreCase(ExcelTools.VIEW_SHEET)) 
			return headersView;
		if (sheet.equalsIgnoreCase("Image") || sheet.equalsIgnoreCase(ExcelTools.IMAGE_SHEET)) 
			return headersImage;
		if (sheet.equalsIgnoreCase("Specimen") || sheet.equalsIgnoreCase(ExcelTools.SPECIMEN_SHEET)) 
			return headersSpecimen;
		if (sheet.equalsIgnoreCase("Locality") || sheet.equalsIgnoreCase(ExcelTools.LOCALITY_SHEET)) 
			return headersLocality;
		if (sheet.equalsIgnoreCase("Taxon") || sheet.equalsIgnoreCase(ExcelTools.SPECIMEN_TAXON_DATA_SHEET)) 
			return headersTaxon;
		if (sheet.equalsIgnoreCase("ExternalLinks") || sheet.equalsIgnoreCase(ExcelTools.EXT_LINK_SHEET)) 
			return headersExtLink;
		if (sheet.equalsIgnoreCase("SupportData") || sheet.equalsIgnoreCase(ExcelTools.SUPPORTING_DATA_SHEET)) 
			return headersSupportData;
		if (sheet.equalsIgnoreCase(ExcelTools.PROTECTED_DATA_SHEET)) 
			return headersProtectedData;
		else return null;
	}
	
	public String getValue(String sheet, String fieldName, int row) {
		fieldName = fieldName.toLowerCase().trim();
		String[] headers = getHeaders(sheet);
		
		for (int i = 0; i < headers.length; i++) {
			if (headers != null && fieldName.equals(headers[i])) {
				return getEntry(sheet, i, row);
			}
		}
		return "";
	}
	
	public Integer getColumnNumberByName(String sheet, String fieldName) {
		fieldName = fieldName.toLowerCase().trim();
		String[] headers = getHeaders(sheet);
		for (int i = 0; i < headers.length; i++) {
			if (headers != null && fieldName.equalsIgnoreCase(headers[i])) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * method for retrieving the number of columns
	 * 
	 * @param sheet
	 * @return
	 */
	public int GetColumns(String sheetName) {
		Sheet sheet = getSheet(sheetName);
		if (sheet == null) return 0;
		return sheet.getColumns();
	}

	/**
	 * public method for retrieving the number of rows
	 * 
	 * @param sheet
	 * @return
	 */
	public int GetRows(String sheetName) {
		Sheet sheet = getSheet(sheetName);
		if (sheet == null) return 0;
		return sheet.getRows();
	}

	public String getReleaseDate() {
//	public Date getReleaseDate() {
		return releaseDate;
	}

	/**
	 * method that sets the value of release date
	 * 
	 * @return
	 */
	public String setReleaseDate() {
//	public Date setReleaseDate() {
		if (imageCollectionSheet.getCell(0, 6).getContents().equals("Release date (yyyy-mm-dd):")
				&& !imageCollectionSheet.getCell(1, 6).equals("")) {
//			DateCell datecell = (DateCell) imageCollectionSheet.getCell(1, 6);
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			String releaseDateStr = imageCollectionSheet.getCell(1, 6).getContents().trim().replaceAll(" ", "-").substring(0,10);
//			releaseDate = Date.valueOf(format.format(datecell.getDate()));
			releaseDate = imageCollectionSheet.getCell(1, 6).getContents();
		} 
		return releaseDate;
	}

	/**
	 * public method that retreives the value of the contributer - userId
	 * 
	 * @return
	 */
	public int GetUserId() {
		String errorMessage = "The contributor is not a valid user in the Morphbank database";
		int userId = 0;
		String user = getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 1, 4);
		if ((getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 0, 4).equals("Contributor (morphbank username):"))
				&& (!user.equals(""))) {
			try {
				statement = connect.getConnect().createStatement();
			} catch (Exception e) {
				e.printStackTrace();
//				System.exit(1);
			}
			String temp = "SELECT id FROM User WHERE uin='" + user + "'";
			try {
				result = statement.executeQuery(temp);
				metadata = result.getMetaData();
				int numberOfRows = 0;
				if (result.last()) numberOfRows = result.getRow();
				if (numberOfRows != 0 && metadata.getColumnCount() == 1) {
					result.first();
					userId = result.getInt(1);
				} else {
					System.out
							.println(errorMessage);
					LoadData.log(errorMessage);
//					System.exit(1);
					return -1;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
//				System.exit(1);
			}
		} else {
			user = getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 1, 3);
			if ((getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 0, 3)
					.equals("Contributor (first_name last_name only):"))
					&& (!user.equals(""))) {
				try {
					statement = connect.getConnect().createStatement();
				} catch (Exception e) {
					e.printStackTrace();
//					System.exit(1);
				}
				String temp = "SELECT id FROM User WHERE name='" + user + "'";
				try {
					result = statement.executeQuery(temp);
					metadata = result.getMetaData();
					int numberOfRows = 0;
					if (result.last()) numberOfRows = result.getRow();
					if (numberOfRows != 0 && metadata.getColumnCount() == 1) {
						result.first();
						userId = result.getInt(1);
					} else {
						System.out
								.println(errorMessage);
						LoadData.log(errorMessage);
//						System.exit(1);
						return -1;
					}
				} catch (SQLException sql) {
					sql.printStackTrace();
//					System.exit(1);
				}
			} else {
				System.out.println("No Contributor provided.");
				LoadData.log("No Contributor provided.");
//				System.exit(1);
				return -1;
			}
		}
		return userId;
	}

	/**
	 * public method that retrieves the value of groupId
	 * 
	 * @return
	 */
	public int GetGroupId() {
		String existGroupMessage = "The group specified by the contributor does not exist in the database";
		String belongGroupMessage = "The contributor does not belong to the specified group.";
		String personalGroupMessage = "The contributor does not have a personal group";
		int groupId = 0;
		String temp = "";
		String group = imageCollectionSheet.getCell(1, 7).getContents();
		statement = LoadData.getStatement();
		if (group.length() != 0) {
			temp = "SELECT id FROM Groups WHERE groupName=?";
			try {
				PreparedStatement prepStmt = LoadData.getConnection().prepareStatement(temp);
				prepStmt.setString(1, group);
				result = prepStmt.executeQuery();
				if (result.next()) {
					groupId = result.getInt(1);
					// System.out.println("Group id is: " + groupId);
				} else {
					System.out
							.println(existGroupMessage);
					LoadData.log(existGroupMessage);
//					System.exit(1);
					return -1;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
//				System.exit(1);
			}
			// check if the contributor belongs to the specified group
			temp = "SELECT user FROM UserGroup WHERE user=" + GetUserId() + " and groups="
					+ groupId;
			try {
				result = statement.executeQuery(temp);
				if (!result.next()) {
					System.out.println(belongGroupMessage);
					LoadData.log(belongGroupMessage);
					return -1;
//					System.exit(1);
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
//				System.exit(1);
			}
		} else {
			// if group not specified personal group of the contributor will be
			// used
			String user = imageCollectionSheet.getCell(1, 4).getContents();
			temp = "SELECT id FROM Groups WHERE groupName=\"" + user + "'s group" + "\"";
			// System.out.println(temp);
			try {
				// System.out.println(temp);
				ResultSet newResult = statement.executeQuery(temp);
				if (!result.next()) {
					System.out.println(personalGroupMessage);
					LoadData.log(personalGroupMessage);
					// System.exit(1);
					groupId = 2;
				} else {
					groupId = newResult.getInt(1);
				}

			} catch (SQLException sql) {
				sql.printStackTrace();
//				System.exit(1);
			}
		}
		return groupId;
	}// end of GetGroupId

	/**
	 * public get method that retreives the submitterId
	 * 
	 * @return
	 */
	public int GetSubmitterId() {
		String errorMessage = "The submitter is not a valid user in the Morphbank database";
		int submitterId = 0;
		String submitter = getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 1, 5);
		if ((getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 0, 5).equals("Submitter (first_name last_name only):"))
				&& (!submitter.equals(""))) {
			try {
				statement = connect.getConnect().createStatement();
			} catch (Exception e) {
				e.printStackTrace();
//				System.exit(1);
			}
			String temp = "SELECT id FROM User WHERE name='" + submitter + "'";
			try {
				result = statement.executeQuery(temp);
				metadata = result.getMetaData();
				int numberOfRows = 0;
				if (result.last()) numberOfRows = result.getRow();
				if (numberOfRows != 0 && metadata.getColumnCount() == 1) {
					result.first();
					submitterId = result.getInt(1);
				} else {
					System.out
							.println(errorMessage);
					LoadData.log(errorMessage);
//					System.exit(1);
					return -1;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
//				System.exit(1);
			}
		}
		if (submitter.equals("")) submitterId = GetUserId();
		return submitterId;
	}// end of GetSubmitterId

	/**
	 * public method that provides the kingdom for the submited specimens
	 * 
	 * @return
	 */
	public int GetKingdom(int tsn) {
		int kingdomId = 0;
		String temp = "SELECT kingdom_id FROM Tree WHERE tsn="
				+ tsn;
		try {
			result = statement.executeQuery(temp);
			result.next();
			kingdomId = result.getInt(1);
		} catch (SQLException sql) {
			sql.printStackTrace();
//			System.exit(1);
		}
		return kingdomId;
	}

	/**
	 * public method that retreives a Institution link if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetInstitutionLink() {
		return imageCollectionSheet.getCell(1, 9).getContents().trim();
	}

	/**
	 * public method that retreives a Institution name if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetInstitutionName() {
		return imageCollectionSheet.getCell(1, 8).getContents().trim();
	}

	/**
	 * public method that retreives a Project link1 if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetProjectLink1() {

		return imageCollectionSheet.getCell(1, 11).getContents().trim();
	}

	/**
	 * public method that retreives a Project link1 if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetProjectLink2() {
		return imageCollectionSheet.getCell(1, 13).getContents().trim();
	}

	/**
	 * retrieve a project Name1 if provided by the contributor
	 * 
	 * @return
	 */
	public String GetProjectName1() {
		return imageCollectionSheet.getCell(1, 10).getContents().trim();
	}

	/**
	 * retrieve a project Name2 if provided by the contributor
	 * 
	 * @return
	 */
	public String GetProjectName2() {
		return imageCollectionSheet.getCell(1, 12).getContents().trim();
	}
}
