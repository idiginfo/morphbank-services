package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.morphbank.MorphbankConfig;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ValidateCustomXls {

	private StringBuffer output = new StringBuffer(1024);
	public static String PERSISTENCE = MorphbankConfig.PERSISTENCE_LOCALHOST;
	private static final String DROP_DOWNS_SHEET_NAME = "Drop Downs";
	private static final String DATA_SHEET_NAME = "Data";
	private static final String CONTRIBUTOR_SHEET_NAME = "ContributorInfo";
	private static final String USER_PROPERTIES_SHEET_NAME = "UserProperties";
	private boolean isXlsValid = true;
	private boolean versionInfo;
	private String fileName;
	private Workbook workbook;
	private Sheet dropDownsSheet;
	private Sheet dataSheet;
	private Sheet contributorSheet;
	private Sheet userPropertiesSheet;
	private String[] headersDropDowns;
	private String[] headersData;
	private String[] headersUserProp;
	private EntityManager em;
	
	public ValidateCustomXls(String fileName, boolean versionInfo, String persistence) {
		this.fileName = fileName;
		this.workbook = this.createWorkbook();
		this.createSheets();
		this.readHeaders();
		this.versionInfo = versionInfo;
		PERSISTENCE = persistence;
	}
	
	private Workbook createWorkbook() {
		try {
			return Workbook.getWorkbook(new File(fileName));
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void createSheets() {
		dropDownsSheet = workbook.getSheet(DROP_DOWNS_SHEET_NAME);
		dataSheet = workbook.getSheet(DATA_SHEET_NAME);
		contributorSheet = workbook.getSheet(CONTRIBUTOR_SHEET_NAME);
		userPropertiesSheet = workbook.getSheet(USER_PROPERTIES_SHEET_NAME);
	}

	/**
	 * Entry point for all tests
	 * Add more tests in this method
	 * @return
	 */
	public boolean checkEverything() {
		if (versionInfo) {
			String message = "Version Info: " + getVersionNumber(); 
			System.out.println(message);
			this.messageToOuput(message);
		}
		String beginTesting = "Let's see what the file looks like...";
		System.out.println(beginTesting);
		this.messageToOuput("<b>" + beginTesting + "</b>");
		isXlsValid &= this.checkUniqueImageExtId();
		isXlsValid &= this.checkFormatDateColumns();
		isXlsValid &= this.checkOriginalFileName();
		isXlsValid &= this.checkUserProperties();
		isXlsValid &= this.checkDBMatch();
		isXlsValid &= this.checkMandatoryCellsNotEmpty();
		return isXlsValid;
	}

	private String getVersionNumber() {
		Integer col = this.getColumnNumberByName(DROP_DOWNS_SHEET_NAME, "Version Info");
		if (col == null) return "no version for this file (that's ok, this is not an error. It means there is a more recent version available online).";
		return this.getEntry(DROP_DOWNS_SHEET_NAME, col, 1);
	}

	/**
	 * Get headers for the following sheets:
	 * Drop Downs, Data, UserProperties
	 */
	private void readHeaders() {
		int numFields = dropDownsSheet.getColumns();
		headersDropDowns = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersDropDowns[i] = dropDownsSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = dataSheet.getColumns();
		headersData = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersData[i] = dataSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = userPropertiesSheet.getColumns();
		headersUserProp = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersUserProp[i] = userPropertiesSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		
	}

	private String getValue(String sheet, String fieldName, int row) {
		fieldName = fieldName.toLowerCase().trim();
		String[] headers = null;
		headers = this.getHeaders(sheet);
		
		for (int i = 0; i < headers.length; i++) {
			if (headers != null && fieldName.equals(headers[i])) {
				return getEntry(sheet, i, row);
			}
		}
		return "";
	}

	private Integer getColumnNumberByName(String sheet, String fieldName) {
		fieldName = fieldName.toLowerCase().trim();
		String[] headers = null;
		headers = this.getHeaders(sheet);
		for (int i = 0; i < headers.length; i++) {
			if (headers != null && fieldName.equalsIgnoreCase(headers[i])) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Get the appropriate header for the given sheet
	 * @param sheet
	 * @return 
	 */
	private String[] getHeaders(String sheet) {
		if (sheet.equalsIgnoreCase(DROP_DOWNS_SHEET_NAME)) return headersDropDowns;
		if (sheet.equalsIgnoreCase(DATA_SHEET_NAME)) return headersData;
		if (sheet.equalsIgnoreCase(USER_PROPERTIES_SHEET_NAME)) return headersUserProp;
		return null;
	}
	
	private String getEntry(String sheetName, int col, int row) {
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

	private Sheet getSheet(String sheetName) {
		if (DROP_DOWNS_SHEET_NAME.equals(sheetName)) {
			return dropDownsSheet;
		} 
		if (DATA_SHEET_NAME.equals(sheetName)) {
			return dataSheet;
		}
		if (USER_PROPERTIES_SHEET_NAME.equals(sheetName)) {
			return userPropertiesSheet;
		}
		return null;
	}
	
	/**
	 * No duplicate in the ImageExternalId column
	 * @return true if no duplicate have been found
	 */
	private boolean checkUniqueImageExtId() {
		HashMap<Integer, Integer> duplicates = new HashMap<Integer, Integer>();
		Cell[] cells = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Image External id"));
		String[] columnValues = new String[cells.length];
		for (int i = 0; i < cells.length -1; i++) {
			columnValues[i] = cells[i+1].getContents();
			for (int j = 0; j < i; j++) {
				if (!columnValues[i].equalsIgnoreCase("") && columnValues[j].equalsIgnoreCase(columnValues[i])) duplicates.put(j + 2, i + 2);
			}
		}
		if (!duplicates.isEmpty()) {
			String error = "In column Image External id, rows ";
			Iterator<Entry<Integer, Integer>> it = duplicates.entrySet().iterator();
			
			StringBuffer list = new StringBuffer();
			while (it.hasNext()) {
				Entry<Integer, Integer> dups = it.next();
				list.append(dups.getKey());
				list.append(" and ");
				list.append(dups.getValue());
				list.append(", ");
			}
			error += list.toString() + "have duplicate entries.";
			System.out.println(error);
			this.messageToOuput(error);
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the cell type for a date field is text
	 * Excel interprets the date entered in various formats and is not
	 * a reliable format to manipulate a date.
	 * Also checks if the format is yyyy-mm-dd 
	 * @return
	 */
	private boolean checkFormatDateColumns() {
		Integer columnNumber = this.getColumnNumberByName(DATA_SHEET_NAME, "Date Determined");
		if (columnNumber == null) {
//			String error = "No Date Determined found. It could be due to an outdated spreadsheet. Check skipped on that.";
//			System.out.println(error);
//			this.messageToOuput(error);
			return true;
		}
		boolean correctFormat = true;
		String[] dateColumns = {"Date Determined", "Earliest Date Collected", "Latest Date Collected"};
		for (String colName:dateColumns){
			Integer col = this.getColumnNumberByName(DATA_SHEET_NAME, colName);
			if (col != null) {
				Cell[] cells = dataSheet.getColumn(col);
				correctFormat &= checkFormatDate(cells, colName);
			}
		}
		return correctFormat;
	}
	
	private boolean checkFormatDate(Cell[] cells, String colName){
		boolean isCorrectFormat = true;
		boolean isRowCorrect = true;
		for (int i = 1; i < cells.length; i++) {
			if (Tools.isEmpty(cells[i])) continue;
			isCorrectFormat = Tools.checkCellType(cells[i], CellType.LABEL);
			if (!isCorrectFormat) {
				isRowCorrect = false;
				String error = "In column "+ colName +", row " + (i+1) + " should be formatted as text.";
				System.out.println(error);
				this.messageToOuput(error);
			}
			else {
				isCorrectFormat = Tools.checkDateFormat(cells[i]);
				if (!isCorrectFormat) {
					isRowCorrect = false;
					String error = "In column "+ colName +", row " + (i+1) + 
							" should be formatted as yyyy-mm-dd instead of " + cells[i].getContents() +".";
					System.out.println(error);
					this.messageToOuput(error);
				}
			}
		}
		return isRowCorrect;
	}
	
	/**
	 * Compare Taxon and Credentials to the database
	 * @return true if the data is a match
	 */
	private boolean checkDBMatch() {
		boolean matchDB = true;
		MorphbankConfig.setPersistenceUnit(PERSISTENCE);
		MorphbankConfig.init();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE);
		em = emf.createEntityManager();
		matchDB &= this.checkTSN();
		matchDB &= this.checkCredentials();
		
		return matchDB;
	}
	
	/**
	 * Compares the scientific name with the TSN to see
	 * if they match in the database
	 * @return true is the whole column of scientific name found a TSN match
	 */
	private boolean checkTSN() {
		boolean columnValid = true;
		Cell[] cellsDetermination = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Determination Scientific Name"));
		Cell[] cellsTSN = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Determination TSN"));
		String select = "select t.tsn from Taxon t where t.scientificName = :scientificName";
		Query query = em.createQuery(select);
//		query = MorphbankConfig.getEntityManager().createQuery(select);
		for (int i = 1; i < cellsDetermination.length; i++) {
			boolean matchFound = false;
			if (Tools.isEmpty(cellsDetermination[i])) continue;
			query.setParameter("scientificName", cellsDetermination[i].getContents());
			List tsns = query.getResultList();
			if (tsns == null) {
				String error = "Scientific name " + cellsDetermination[i] + " at row " + (i+1) + " is not in Morphbank.";
				System.out.println(error);
				this.messageToOuput(error);
			}
			if (Tools.isEmpty(cellsTSN[i])) continue;
			else {
				Iterator it = tsns.iterator();
				while (it.hasNext()) {
					int next = (Integer) it.next();
					int tsn = Integer.valueOf(this.safeCast(cellsTSN[i].getContents(), i+1));
					if (tsn == next)
						matchFound |= true;
					else
						matchFound |= false;
				}
				if (!matchFound) {
					String error = "Scientific name " + cellsDetermination[i].getContents() + " does not match TSN " + cellsTSN[i].getContents() + " at row " + (i+1) + ".";
					System.out.println(error);
					this.messageToOuput(error);
				}
				columnValid &= matchFound;
			}
			
		}
		return columnValid;
	}
	
	/**
	 * No extra space should be found in the TSN fields
	 * @param content
	 * @param row 
	 * @return TSN trimmed
	 */
	private String safeCast(String content, int row) {
		if (content.indexOf(' ') != -1) {
			String error = "Extra space found for TSN " + content.trim() + " at row " + row + ".<br />";
			System.out.println(error);
			this.messageToOuput(error);
			return content.trim();
		}
		return content;
	}

	/**
	 * Verifies that the data in the Contributor sheet
	 * matches the database.
	 * name and id cells cannot be both empty
	 * Checks userName /UserId, groupName/ groupId, user belongs to the group
	 * @return true if all the above is true
	 */
	private boolean checkCredentials() {
		boolean credentialsOK = true;
		boolean emptyCells = false;
		String cName = contributorSheet.getCell(1, 1).getContents();
		String cId = contributorSheet.getCell(1, 2).getContents();
		emptyCells |= areCellsBothEmpty(contributorSheet.getCell(0, 1).getContents(), cName,
				contributorSheet.getCell(0, 2).getContents(), cId);

		String sName = contributorSheet.getCell(1, 3).getContents();
		String sId = contributorSheet.getCell(1, 4).getContents();
		emptyCells |= areCellsBothEmpty(contributorSheet.getCell(0, 3).getContents(), sName,
				contributorSheet.getCell(0, 4).getContents(), sId);
		
		String gName = contributorSheet.getCell(1, 5).getContents();
		String gId = contributorSheet.getCell(1, 6).getContents();
		emptyCells |= areCellsBothEmpty(contributorSheet.getCell(0, 5).getContents(), gName,
				contributorSheet.getCell(0, 6).getContents(), gId);
		
		String date = contributorSheet.getCell(1, 7).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 7).getContents(), date);
		if(emptyCells) return false;
		
		String select = "select u.userName, u.id from User u where u.userName = :name";
		Query query = em.createQuery(select);
		query.setParameter("name", cName);
		credentialsOK &= this.compareNameId(query, cName, cId);
		query.setParameter("name", sName);
		credentialsOK &= this.compareNameId(query, sName, sId);
		select = "select g.groupName, g.id from Group g where g.groupName = :name";
		query = em.createQuery(select);
		query.setParameter("name", gName);
		credentialsOK &= this.compareNameId(query, gName, gId);
		
		String selectTest = "select g.user from UserGroup g where g.groups = " + gId;
		query = em.createNativeQuery(selectTest);
		credentialsOK &= this.compareUserGroup(query, cId, gName);
		
		return credentialsOK;
	}
	
	private boolean isCellEmpty(String label, String cell) {
		if (cell.length() < 1) {
			String error = label.replaceFirst(":", "") + " cannot be empty.";
			System.out.println(error);
			this.messageToOuput(error);
			return true;
		}
		return false;
	}

	/**
	 * Check if both cells are empty.
	 * Output a warning if id is empty. It should not stop the conversion to XML
	 * but Morphbank admin should add the id manually before upload.
	 * @param label1 title of Cell Name (ex: MB Contributor Name)
	 * @param cell1 value of cell
	 * @param label2 title of Cell ID (ex:MB Contributor id)
	 * @param cell2 value of cell
	 * @return true if either cell has content
	 */
	private boolean areCellsBothEmpty(String label1, String cell1, String label2, String cell2) {
		String label1Short = label1.replaceFirst(":", "");
		String label2Short = label2.replaceFirst(":", "");
		int size1 = cell1.length();
		int size2 = cell2.length();
		if (size2 < 1) {
			String warning = label2Short + " is empty. If you don't know the " + label2Short + " that's ok, but contact Morphbank to let them know.";
			System.out.println(warning);
			this.messageToOuput(warning);
		}
		if (size1 < 1 && size2 < 1) {
			String error = label1Short + " cannot be empty if " +
					label2Short + " is also empty.";
			System.out.println(error);
			this.messageToOuput(error);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks that the name given matches the morphbank id
	 * @param query
	 * @param name
	 * @param id
	 * @return
	 */
	private boolean compareNameId(Query query, String name, String id) {
		if(name == null || name.equals("") || id == null || id.equals("")) return true;
		List names = query.getResultList();
		if (names.isEmpty()) {
			String error = name + " is not in Morphbank.";
			System.out.println(error);
			this.messageToOuput(error);
			return false;
		}
		Iterator it = names.iterator();
		boolean matchFound = false;
		while (it.hasNext()) {
			Object[] row = (Object[]) it.next();
			int uid = Integer.valueOf(String.valueOf(row[1]));
			if (row[0].equals(name)) {
				matchFound = true;
				if (uid != Integer.valueOf(id)) {
					String error = name + " and " + id + " do not match. One of them must be misstyped.";
					System.out.println(error);
					this.messageToOuput(error);
					matchFound = false;
				}
			}
		}
		return matchFound;
	}

	/**
	 * Checks if the user belongs to the given group
	 * @param query
	 * @param id
	 * @param groupName
	 * @return
	 */
	private boolean compareUserGroup(Query query, String id, String groupName) {
		if(groupName == null || groupName.equals("") || id == null || id.equals("")) return true;
		List names = query.getResultList();
		if (names.isEmpty()) {
			String error = "Id:" + id + " is not in the group " + groupName + ".";
			System.out.println(error);
			this.messageToOuput(error);
			return false;
		}
		Iterator it = names.iterator();
		boolean matchFound = false;
		Integer user;
		while (it.hasNext()) {
			user = (Integer) it.next();
			if (user.intValue() == Integer.parseInt(id)) {
				matchFound = true;
				break;
			}
			else {
				matchFound = false;
			}
		}
		if (!matchFound) {
			String error = "Id:" + id + " is not in the group " + groupName + ".";
			System.out.println();
			this.messageToOuput(error);
		}
		return matchFound;
	}

	public StringBuffer getOutput() {
		return output;
	}
	
	/**
	 * The file name should have the correct extension
	 * and not space or more than one '.'
	 * @return
	 */
	private boolean checkOriginalFileName() {
		Cell[] cells = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Original File Name"));
		boolean isValid = true;
		String error = "In column Original File Name, row ";
		for (int i = 1; i < cells.length; i++) {
			if (Tools.isEmpty(cells[i])) continue;
			if (cells[i].getContents().indexOf(" ") > 0) {
				isValid = false;
				String message = error + (i+1) + " should not contain spaces.";
				System.out.println(message);
				this.messageToOuput(message);
			}
			if (!Tools.fileExtensionOk(cells[i].getContents())) {
				isValid = false;
				String message = error + (i+1) 
				+ " file extension should be " + Tools.outputListOfExtensions();
				System.out.println(message);
				this.messageToOuput(message);
			}
			if (!Tools.fileNameFormattedOk(cells[i].getContents())) {
				isValid = false;
				String message = error + (i+1) + " cannot use '.' in the file name.";
				System.out.println(message);
				this.messageToOuput(message);
			}
		}
		return isValid;
	}

	/**
	 * Append messages to a StringBuffer that can be used to
	 * display messages on a webpage.
	 * @param message
	 */
	private void messageToOuput(String message) {
		output.append(message);
		output.append("<br />");
	}
	
	/** check if a row with mandatory cells is either all filled or all empty
	 * @return true is test passed
	 */
	private boolean checkMandatoryCellsNotEmpty() {
		boolean isValid = true;
		//either all cells empty or all full
		int maxRows = dataSheet.getRows();
		for (int i = 1; i < maxRows; i++) {
			String[] row = this.getMandatoryRow(dataSheet.getRow(i));
			if (row == null) return printMandatoryRowsHelp(isValid);
			isValid &= this.checkMandatoryRow(row, i);
		}
		return isValid;
	}
	
	private boolean printMandatoryRowsHelp(boolean isValid) {
		if (isValid)
			return isValid;
		String listMandatoryCells = "If a row is not empty, the corresponding columns must be filled: "
				+ "Please check Image External id, Image External id Prefix, Original File Name, Creative Commons, "
				+ "Specimen External id, Specimen External id Prefix, Determination Scientific Name, "
				+ "Determination TSN, Basis of Record, Type Status, View Applicable to Taxon.";
		System.out.println(listMandatoryCells);
		this.messageToOuput(listMandatoryCells);
		return isValid;
	}
	
	/**
	 * All listed columns should have a value if one of the cell
	 * is not empty
	 * @param entireRow
	 * @return entire row
	 */
	private String[] getMandatoryRow(Cell[] entireRow){

			int colImgExtId = this.getColumnNumberByName(DATA_SHEET_NAME, "Image External id");
			int colImgExtIdPrfx = this.getColumnNumberByName(DATA_SHEET_NAME, "Image External id Prefix");
			int colOriglFileName = this.getColumnNumberByName(DATA_SHEET_NAME, "Original File Name");
			int colCreativeCommons = this.getColumnNumberByName(DATA_SHEET_NAME, "Creative Commons");
			int colSpExtId = this.getColumnNumberByName(DATA_SHEET_NAME, "Specimen External id");
			int colSpExtIdPrfx = this.getColumnNumberByName(DATA_SHEET_NAME, "Specimen External id Prefix");
			int colDetScName = this.getColumnNumberByName(DATA_SHEET_NAME, "Determination Scientific Name");
			int colDetTSN = this.getColumnNumberByName(DATA_SHEET_NAME, "Determination TSN");
			int colBasisOfRecord = this.getColumnNumberByName(DATA_SHEET_NAME, "Basis of Record");
			int colTypeStatus = this.getColumnNumberByName(DATA_SHEET_NAME, "Type Status");
			int colViewAppTaxon = this.getColumnNumberByName(DATA_SHEET_NAME, "View Applicable to Taxon");
			
			if (colViewAppTaxon > entireRow.length - 1) {
				return null;
			}
			
			String[] row = new String[11];
			row[0] = entireRow[colImgExtId].getContents(); 
			row[1] = entireRow[colImgExtIdPrfx].getContents(); 
			row[2] = entireRow[colOriglFileName].getContents(); 
			row[3] = entireRow[colCreativeCommons].getContents(); 
			row[4] = entireRow[colSpExtId].getContents();
			row[5] = entireRow[colSpExtIdPrfx].getContents(); 
			row[6] = entireRow[colDetScName].getContents(); 
			row[7] = entireRow[colDetTSN].getContents(); 
			row[8] = entireRow[colBasisOfRecord].getContents(); 
			row[9] = entireRow[colTypeStatus].getContents();
			row[10] = entireRow[colViewAppTaxon].getContents(); 
			
			return row;
	}
	
	
	private boolean checkMandatoryRow(String[] row, int rowNumber) {
		boolean hasContent = true;
		boolean isEmpty = true;
		for (String cell:row) {
			if (cell.length() > 0) {
				hasContent &= true;
				isEmpty = false;
			}
			else {
				hasContent = false;
				isEmpty &= true;
			}
		}
		//either hasContent = false and isEmpty = true;
		//or hasContent = true and isEmpty = false;
		if (!(hasContent || isEmpty)) printMandatoryCellError(rowNumber);
		return hasContent || isEmpty;
	}
	
	private void printMandatoryCellError(int row) {
		String error = "In row " + (row + 1) + ", one or more mandatory cells are empty.";
		System.out.println(error);
		this.messageToOuput(error);
	}
	
	/**
	 * Check if all user properties are in
	 * the Data sheet header.
	 * Avoid common typos, extra spaces, etc.
	 * @return true if check passed
	 */
	private boolean checkUserProperties() {
		String errorMessagePart1 = "In " + USER_PROPERTIES_SHEET_NAME + " sheet, "; //insert userProperty here
		String errorMessagePart2 = " does not match any column header in " + DATA_SHEET_NAME + " sheet. Please check for typos, extra spaces, etc.";
		boolean matchAll = true;
		boolean currentMatch = false;
		String[] headers = getHeaders(DATA_SHEET_NAME);
		Cell[] userProperties = userPropertiesSheet.getColumn(this.getColumnNumberByName(USER_PROPERTIES_SHEET_NAME, "<userProperty>"));
		for (Cell userProp:userProperties) {
			if (userProp.getContents().equals("<userProperty>")) continue;
			for (String header:headers) {
				if (userProp.getContents().toLowerCase().equals(header)) {
					currentMatch = true;
					break;
				}
			}
			if (!currentMatch) {
				String message = errorMessagePart1 + userProp.getContents() + errorMessagePart2;
				System.out.println(message);
				this.messageToOuput(message);
				matchAll = false;
			}
		}
		return matchAll;
	}
	
}
