package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Query;

import org.apache.commons.codec.digest.DigestUtils;

import net.morphbank.MorphbankConfig;
import net.morphbank.loadexcel.GetConnection;
import net.morphbank.object.Group;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ValidateCustomXls {

	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBPROD;
	private static final String DROP_DOWNS_SHEET_NAME = "Drop Downs";
	private static final String DATA_SHEET_NAME = "Data";
	private static final String CONTRIBUTOR_SHEET_NAME = "ContributorInfo";
	private boolean isXlsValid = true;
	private String fileName;
	private int numFields;
	private Workbook workbook;
	Sheet dropDownsSheet;
	Sheet dataSheet;
	Sheet contributorSheet;
	private String[] headersDropDowns;
	private String[] headersData;

	
	public ValidateCustomXls(String fileName) {
		this.fileName = fileName;
		this.workbook = this.createWorkbook();
		this.createSheets();
		this.readHeaders();
	}

	public static void main(String[] args) {
		ValidateCustomXls test = new ValidateCustomXls("/home/gjimenez/Downloads/customWorkbook-testContinentWaterBody.xls");
		boolean passed = test.checkEverything();
		System.out.println(passed);
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
	}

	public boolean checkEverything() {
		System.out.println("Version Info: " + getVersionNumber());
		isXlsValid &= this.checkUniqueImageExtId();
		isXlsValid &= this.checkFormatDateColumns();
		isXlsValid &= this.checkNoSpaceInFileName();
		isXlsValid &= this.checkDBMatch();
		isXlsValid &= this.checkCredentials();
		return isXlsValid;
	}

	private String getVersionNumber() {
		Integer col = this.getColumnNumberByName(DROP_DOWNS_SHEET_NAME, "Version Info");
		if (col == null) return "no version for this file";
		return this.getEntry(DROP_DOWNS_SHEET_NAME, col, 1);
	}

	private void readHeaders() {
		numFields = dropDownsSheet.getColumns();
		headersDropDowns = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersDropDowns[i] = dropDownsSheet.getCell(i, 0).getContents().toLowerCase().trim();
		}
		numFields = dataSheet.getColumns();
		headersData = new String[numFields];
		for (int i = 0; i < numFields; i++) {
			headersData[i] = dataSheet.getCell(i, 0).getContents().toLowerCase().trim();
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

	private String[] getHeaders(String sheet) {
		if (sheet.equalsIgnoreCase(DROP_DOWNS_SHEET_NAME)) return headersDropDowns;
		if (sheet.equalsIgnoreCase(DATA_SHEET_NAME)) return headersData;
		return null;
	}
	
	private String getEntry(String sheetName, int col, int row) {
		Sheet sheet = getSheet(sheetName);
		if (sheet == null) return "";
		Cell cell = sheet.getCell(col, row);
		String cellTest = cell.getType().toString();
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
		return null;
	}
	
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
		if (duplicates.size() > 0) {
			System.out.print("In column Image External id, rows ");
			Iterator<Entry<Integer, Integer>> it = duplicates.entrySet().iterator();
			
			StringBuffer list = new StringBuffer();
			while (it.hasNext()) {
				Entry<Integer, Integer> dups = it.next();
				list.append(dups.getKey() + " and " + dups.getValue());
				list.append(", ");
			}
			System.out.println(list.toString() + "have duplicate entries.");
			return false;
		}
		return true;
	}
	
	private boolean checkFormatDateColumns() {
		Cell[] cells = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Date Determined"));
		boolean correctFormat = true;
		for (int i = 1; i < cells.length; i++) {
			if (isEmpty(cells[i])) continue;
			correctFormat = this.checkCellType(cells[i], CellType.LABEL);
			if (!correctFormat) {
				System.out.println("In column Date Determined, row " + (i+1) + " should be formatted as text.");
			}
		}
		return correctFormat;
	}
	
	private boolean checkCellType(Cell cell, CellType type) {
		if (cell.getType() != type) return false;
		return true;
	}
	
	private boolean checkNoSpaceInFileName() {
		Cell[] cells = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Original File Name"));
		boolean isValid = true;
		for (int i = 1; i < cells.length; i++) {
			if (isEmpty(cells[i])) continue;
			if (cells[i].getContents().indexOf(" ") > 0) {
				isValid = false;
				System.out.println("In column Original File Name, row " + (i+1) + " should not contain spaces.");
			}
		}
		return isValid;
	}
	private boolean isEmpty(Cell cell) {
		return cell.getContents().equalsIgnoreCase("");
	}
	
	private boolean checkDBMatch() {
		boolean matchDB = true;
		MorphbankConfig.setPersistenceUnit(PERSISTENCE);
		MorphbankConfig.init();
		matchDB &= this.checkTSN();
		
		return matchDB;
	}
	
	private boolean checkTSN() {
		boolean columnValid = true;
		Cell[] cellsDetermination = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Determination Scientific Name"));
		Cell[] cellsTSN = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Determination TSN"));
		String select = "select t.tsn from Taxon t where t.scientificName = :scientificName";
		Query query = MorphbankConfig.getEntityManager().createQuery(select);
		for (int i = 1; i < cellsDetermination.length; i++) {
			boolean matchFound = false;
			if (isEmpty(cellsDetermination[i])) continue;
			query.setParameter("scientificName", cellsDetermination[i].getContents());
			List tsns = query.getResultList();
			if (tsns != null) {
				Iterator it = tsns.iterator();
				while (it.hasNext()) {
					int next = (Integer) it.next();
					int tsn = Integer.valueOf(cellsTSN[i].getContents());
					if (tsn != next)
						matchFound |= false;
					else
						matchFound |= true;
				}
				if (matchFound == false) {
					System.out.println("Scientific name " + cellsDetermination[i].getContents() + "does not match TSN " + cellsTSN[i].getContents() + " at row " + (i+1) + ".");
				}
				columnValid &= matchFound;
			}
			else {
				System.out.println("Scientific name " + cellsDetermination[i] + " at row " + (i+1) + " is not in Morphbank.");
			}
		}
		return columnValid;
	}
	
	private boolean checkCredentials() {
		boolean credentialsOK = true;
		String cName = contributorSheet.getCell(1, 1).getContents();
		String cId = contributorSheet.getCell(1, 2).getContents();
		String sName = contributorSheet.getCell(1, 3).getContents();
		String sId = contributorSheet.getCell(1, 4).getContents();
		String gName = contributorSheet.getCell(1, 5).getContents();
		String gId = contributorSheet.getCell(1, 6).getContents();
		String select = "select u.userName, u.id from User u where u.userName = :name";
		Query query = MorphbankConfig.getEntityManager().createQuery(select);
		query.setParameter("name", cName);
		credentialsOK &= this.compareNameId(query, cName, cId);
		query.setParameter("name", sName);
		credentialsOK &= this.compareNameId(query, sName, sId);
		select = "select g.groupName, g.id from Group g where g.groupName = :name";
		query = MorphbankConfig.getEntityManager().createQuery(select);
		query.setParameter("name", gName);
		credentialsOK &= this.compareNameId(query, gName, gId);
		
		select = "select u.groups from User u where u.id = :id";
		query = MorphbankConfig.getEntityManager().createQuery(select);
		query.setParameter("id", Integer.valueOf(cId));
		this.compareUserGroup(query, cName, cId, gName, gId);
		
		return credentialsOK;
	}
	
	private boolean compareNameId(Query query, String name, String id) {
		List names = query.getResultList();
		if (names.isEmpty()) {
			System.out.println(name + " is not in Morphbank.");
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
					System.out.println(name + " and " + id + " do not match. One of them must be misstyped.");
					matchFound = false;
				}
			}
		}
		return matchFound;
	}

	private boolean compareUserGroup(Query query, String name, String id, String groupName, String gId) {
		List names = query.getResultList();
		if (names.isEmpty()) {
			System.out.println("Id:" + id + " is not in the group " + groupName + ".");
			return false;
		}
		Iterator it = names.iterator();
		boolean matchFound = false;
		while (it.hasNext()) {
			Group group = (Group) it.next();
			int test = group.getId();
			if (group.getId() != Integer.valueOf(gId))
				matchFound = false;
			else {
				matchFound = true;
				break;
			}
		}
		if (!matchFound) {
			System.out.println("Id:" + id + " is not in the group " + groupName + ".");
		}
		return matchFound;
	}
	
	
}