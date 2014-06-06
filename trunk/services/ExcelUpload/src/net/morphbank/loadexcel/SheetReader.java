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
//modified : September 29 2006
//Modified by: Shantanu Gautam				           			//
//date created:  November 05 2013                      			//
////////////////////////////////////////////////////////////////////////

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
	// private java.sql.Date releaseDate = null;
	private String releaseDate;

	protected String[] headersView = null;
	protected String[] headersImage = null;
	protected String[] headersSpecimen = null;
	protected String[] headersLocality = null;
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

			InputStream inp = new FileInputStream(fname);
			Workbook workbook = WorkbookFactory.create(inp);

			// extract the sheets from a formed workbook
			imageCollectionSheet = workbook.getSheetAt(0);
			imageSheet = workbook.getSheetAt(1);
			viewSheet = workbook.getSheetAt(2);
			taxonSheet = workbook.getSheetAt(4);
			specimenSheet = workbook.getSheetAt(3);
			localitySheet = workbook.getSheetAt(5);
			extLinkSheet = workbook.getSheetAt(6);
			supportDataSheet = workbook.getSheetAt(7);
			protectedDataSheet = workbook.getSheetAt(9);
			readHeaders();
			setReleaseDate();

		} catch (Exception ioexception) {
			ioexception.printStackTrace();
		}
	}// end of SheetReader constructor

	static final NumberFormat INTEGER_FORMATTER = NumberFormat
			.getIntegerInstance();
	static final NumberFormat DOUBLE_FORMATTER = new DecimalFormat("0.0##");

	public String getEntry(String sheetName, int col, int row) {
		Sheet sheet = getSheet(sheetName);
		if (sheet == null)
			return "";

		if(sheet.getRow(row).getCell(col) == null)
			return "";
		Cell cell = sheet.getRow(row).getCell(col);
		if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
			return cell.getStringCellValue();
		}
		// must be numeric
		// Date
		if (DateUtil.isCellDateFormatted(cell)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.format(cell.getDateCellValue());
		}
		double value = cell.getNumericCellValue();
		if ((value % 1) == 0) {
			// integer
			return INTEGER_FORMATTER.format(value);
		}
		// float
		return DOUBLE_FORMATTER.format(value);
	}

	public Sheet getSheet(String sheetName) {
		if ("View".equals(sheetName) || sheetName.equals(ExcelTools.VIEW_SHEET)) {
			return viewSheet;
		} else if ("Specimen".equals(sheetName)
				|| sheetName.equals(ExcelTools.SPECIMEN_SHEET)) {
			return specimenSheet;
		} else if ("Image".equals(sheetName)
				|| sheetName.equals(ExcelTools.IMAGE_SHEET)) {
			return imageSheet;
		} else if ("Locality".equals(sheetName)
				|| sheetName.equals(ExcelTools.LOCALITY_SHEET)) {
			return localitySheet;
		} else if ("SupportData".equals(sheetName)
				|| sheetName.equals(ExcelTools.SUPPORTING_DATA_SHEET)) {
			return supportDataSheet;
		} else if ("ImageCollection".equals(sheetName)
				|| sheetName.equals(ExcelTools.IMAGE_COLLECTION_SHEET)) {
			return imageCollectionSheet;
		} else if ("Taxon".equals(sheetName)
				|| sheetName.equals(ExcelTools.SPECIMEN_TAXON_DATA_SHEET)) {
			return taxonSheet;
		} else if ("ExternalLinks".equals(sheetName)
				|| sheetName.equals(ExcelTools.EXT_LINK_SHEET)) {
			return extLinkSheet;
		} else if (sheetName.equals(ExcelTools.PROTECTED_DATA_SHEET)) {
			return protectedDataSheet;
		}
		return null;
	}

	public static String[] getHeaders(Sheet sheet) {
		int numFields = sheet.getRow(0).getLastCellNum();
		String[] headers = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headers[i] = sheet.getRow(0).getCell(i).getStringCellValue()
					.toLowerCase().trim();
		}
		return headers;
	}

	public void readHeaders() {
		headersView = getHeaders(viewSheet);
		headersImage = getHeaders(imageSheet);
		headersSpecimen = getHeaders(specimenSheet);
		headersLocality = getHeaders(localitySheet);
		headersTaxon = getHeaders(taxonSheet);
		headersExtLink = getHeaders(extLinkSheet);
		headersSupportData = getHeaders(supportDataSheet);
		headersProtectedData = getHeaders(protectedDataSheet);
	}

	private String[] getHeaders(String sheet) {
		if (sheet.equalsIgnoreCase("View")
				|| sheet.equalsIgnoreCase(ExcelTools.VIEW_SHEET))
			return headersView;
		if (sheet.equalsIgnoreCase("Image")
				|| sheet.equalsIgnoreCase(ExcelTools.IMAGE_SHEET))
			return headersImage;
		if (sheet.equalsIgnoreCase("Specimen")
				|| sheet.equalsIgnoreCase(ExcelTools.SPECIMEN_SHEET))
			return headersSpecimen;
		if (sheet.equalsIgnoreCase("Locality")
				|| sheet.equalsIgnoreCase(ExcelTools.LOCALITY_SHEET))
			return headersLocality;
		if (sheet.equalsIgnoreCase("Taxon")
				|| sheet.equalsIgnoreCase(ExcelTools.SPECIMEN_TAXON_DATA_SHEET))
			return headersTaxon;
		if (sheet.equalsIgnoreCase("ExternalLinks")
				|| sheet.equalsIgnoreCase(ExcelTools.EXT_LINK_SHEET))
			return headersExtLink;
		if (sheet.equalsIgnoreCase("SupportData")
				|| sheet.equalsIgnoreCase(ExcelTools.SUPPORTING_DATA_SHEET))
			return headersSupportData;
		if (sheet.equalsIgnoreCase(ExcelTools.PROTECTED_DATA_SHEET))
			return headersProtectedData;
		else
			return null;
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

	public Cell[] getColumnCells(String sheetName, String fieldName) {
		// Sheet sheet = getSheet(sheetName);
		return getColumnCells(sheetName, getColumnNumberByName(sheetName, fieldName));
	}

	public Cell[] getColumnCells(String sheetName, int fieldNum) {
		Sheet sheet = getSheet(sheetName);
		int numRows = sheet.getLastRowNum() + 1;
		Cell[] column = new Cell[numRows];
		for (int index = 0; index < numRows; index++) {
			column[index] = sheet.getRow(index).getCell(fieldNum);
		}
		return column;
	}
	
	public Cell[] getRowCells(String sheetName, int rowNum){
		Sheet sheet = getSheet(sheetName);
		Row row = sheet.getRow(rowNum);
		Cell allCellsAtRow[] = new Cell[row.getLastCellNum()];
		for (Cell cell : row) {
			allCellsAtRow[cell.getColumnIndex()] = cell;
		}
		return allCellsAtRow;
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
		if (sheet == null)
			return 0;
		return sheet.getRow(0).getLastCellNum();
	}

	/**
	 * public method for retrieving the number of rows
	 * 
	 * @param sheet
	 * @return
	 */
	public int GetRows(String sheetName) {
		Sheet sheet = getSheet(sheetName);
		if (sheet == null)
			return 0;
		return sheet.getLastRowNum() - 1;
	}

	public String getReleaseDate() {
		// public Date getReleaseDate() {
		return releaseDate;
	}

	/**
	 * method that sets the value of release date
	 * 
	 * @return
	 */
	public String setReleaseDate() {
		if (imageCollectionSheet.getRow(6).getCell(0).toString().equals("Release date (yyyy-mm-dd):")
				&& !imageCollectionSheet.getRow(6).getCell(1).equals("")) {
			Date date = imageCollectionSheet.getRow(6).getCell(1).getDateCellValue();
			releaseDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
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
		if ((getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 0, 4)
				.equals("Contributor (morphbank username):"))
				&& (!user.equals(""))) {
			try {
				statement = connect.getConnect().createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				// System.exit(1);
			}
			String temp = "SELECT id FROM User WHERE uin='" + user + "'";
			try {
				result = statement.executeQuery(temp);
				metadata = result.getMetaData();
				int numberOfRows = 0;
				if (result.last())
					numberOfRows = result.getRow();
				if (numberOfRows != 0 && metadata.getColumnCount() == 1) {
					result.first();
					userId = result.getInt(1);
				} else {
					System.out.println(errorMessage);
					LoadData.log(errorMessage);
					// System.exit(1);
					return -1;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
				// System.exit(1);
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
					// System.exit(1);
				}
				String temp = "SELECT id FROM User WHERE name='" + user + "'";
				try {
					result = statement.executeQuery(temp);
					metadata = result.getMetaData();
					int numberOfRows = 0;
					if (result.last())
						numberOfRows = result.getRow();
					if (numberOfRows != 0 && metadata.getColumnCount() == 1) {
						result.first();
						userId = result.getInt(1);
					} else {
						System.out.println(errorMessage);
						LoadData.log(errorMessage);
						// System.exit(1);
						return -1;
					}
				} catch (SQLException sql) {
					sql.printStackTrace();
					// System.exit(1);
				}
			} else {
				System.out.println("No Contributor provided.");
				LoadData.log("No Contributor provided.");
				// System.exit(1);
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

		String group = imageCollectionSheet.getRow(7).getCell(1)
				.getStringCellValue();

		statement = LoadData.getStatement();
		if (group.length() != 0) {
			temp = "SELECT id FROM Groups WHERE groupName=?";
			try {
				PreparedStatement prepStmt = LoadData.getConnection()
						.prepareStatement(temp);
				prepStmt.setString(1, group);
				result = prepStmt.executeQuery();
				if (result.next()) {
					groupId = result.getInt(1);
					// System.out.println("Group id is: " + groupId);
				} else {
					System.out.println(existGroupMessage);
					LoadData.log(existGroupMessage);
					// System.exit(1);
					return -1;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
				// System.exit(1);
			}
			// check if the contributor belongs to the specified group
			temp = "SELECT user FROM UserGroup WHERE user=" + GetUserId()
					+ " and groups=" + groupId;
			try {
				result = statement.executeQuery(temp);
				if (!result.next()) {
					System.out.println(belongGroupMessage);
					LoadData.log(belongGroupMessage);
					return -1;
					// System.exit(1);
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
				// System.exit(1);
			}
		} else {
			// if group not specified personal group of the contributor will be
			// used
			String user = imageCollectionSheet.getRow(4).getCell(1)
					.getStringCellValue();

			temp = "SELECT id FROM Groups WHERE groupName=\"" + user
					+ "'s group" + "\"";
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
				// System.exit(1);
			}
		}
		return groupId;
	}// end of GetGroupId

	/**
	 * public get method that retrieves the submitterId
	 * 
	 * @return
	 */
	public int GetSubmitterId() {
		String errorMessage = "The submitter is not a valid user in the Morphbank database";
		int submitterId = 0;
		String submitter = getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 1, 5);
		if ((getEntry(ExcelTools.IMAGE_COLLECTION_SHEET, 0, 5)
				.equals("Submitter (first_name last_name only):"))
				&& (!submitter.equals(""))) {
			try {
				statement = connect.getConnect().createStatement();
			} catch (Exception e) {
				e.printStackTrace();
				// System.exit(1);
			}
			String temp = "SELECT id FROM User WHERE name='" + submitter + "'";
			try {
				result = statement.executeQuery(temp);
				metadata = result.getMetaData();
				int numberOfRows = 0;
				if (result.last())
					numberOfRows = result.getRow();
				if (numberOfRows != 0 && metadata.getColumnCount() == 1) {
					result.first();
					submitterId = result.getInt(1);
				} else {
					System.out.println(errorMessage);
					LoadData.log(errorMessage);
					// System.exit(1);
					return -1;
				}
			} catch (SQLException sql) {
				sql.printStackTrace();
				// System.exit(1);
			}
		}
		if (submitter.equals(""))
			submitterId = GetUserId();
		return submitterId;
	}// end of GetSubmitterId

	/**
	 * public method that provides the kingdom for the submited specimens
	 * 
	 * @return
	 */
	public int GetKingdom(int tsn) {
		int kingdomId = 0;
		String temp = "SELECT kingdom_id FROM Tree WHERE tsn=" + tsn;
		try {
			result = statement.executeQuery(temp);
			result.next();
			kingdomId = result.getInt(1);
		} catch (SQLException sql) {
			sql.printStackTrace();
			// System.exit(1);
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
		return imageCollectionSheet.getRow(9).getCell(1).getStringCellValue()
				.trim();

	}

	/**
	 * public method that retreives a Institution name if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetInstitutionName() {
		return imageCollectionSheet.getRow(8).getCell(1).getStringCellValue()
				.trim();
	}

	/**
	 * public method that retreives a Project link1 if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetProjectLink1() {
		return imageCollectionSheet.getRow(11).getCell(1).getStringCellValue()
				.trim();
	}

	/**
	 * public method that retreives a Project link1 if provided by the
	 * contributor
	 * 
	 * @return
	 */
	public String GetProjectLink2() {
		return imageCollectionSheet.getRow(13).getCell(1).getStringCellValue()
				.trim();
	}

	/**
	 * retrieve a project Name1 if provided by the contributor
	 * 
	 * @return
	 */
	public String GetProjectName1() {
		return imageCollectionSheet.getRow(10).getCell(1).getStringCellValue()
				.trim();
	}

	/**
	 * retrieve a project Name2 if provided by the contributor
	 * 
	 * @return
	 */
	public String GetProjectName2() {
		return imageCollectionSheet.getRow(12).getCell(1).getStringCellValue()
				.trim();
	}
}
