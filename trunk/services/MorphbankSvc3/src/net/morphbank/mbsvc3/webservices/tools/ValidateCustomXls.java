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
import net.morphbank.object.Group;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.StringFormulaCell;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ValidateCustomXls {

	private StringBuffer output = new StringBuffer();
	public static final String PERSISTENCE = MorphbankConfig.PERSISTENCE_MBPROD;
	private static final String DROP_DOWNS_SHEET_NAME = "Drop Downs";
	private static final String DATA_SHEET_NAME = "Data";
	private static final String CONTRIBUTOR_SHEET_NAME = "ContributorInfo";
	private boolean isXlsValid = true;
	private boolean versionInfo = false;
	private String fileName;
	private int numFields;
	private Workbook workbook;
	Sheet dropDownsSheet;
	Sheet dataSheet;
	Sheet contributorSheet;
	private String[] headersDropDowns;
	private String[] headersData;
	private EntityManager em;
	
	public ValidateCustomXls(String fileName, boolean versionInfo) {
		this.fileName = fileName;
		this.workbook = this.createWorkbook();
		this.createSheets();
		this.readHeaders();
		this.versionInfo = versionInfo;
	}

	public static void main(String[] args) {
		ValidateCustomXls test = new ValidateCustomXls("/home/gjimenez/Downloads/customWorkbook-testContinentWaterBody.xls", true);
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
		if (versionInfo) {
			System.out.println("Version Info: " + getVersionNumber());
			output.append("Version Info: " + getVersionNumber() + "<br />");
		}
		isXlsValid &= this.checkUniqueImageExtId();
		isXlsValid &= this.checkFormatDateColumns();
		isXlsValid &= this.checkNoSpaceInFileName();
		isXlsValid &= this.checkDBMatch();
		return isXlsValid;
	}

	private String getVersionNumber() {
		Integer col = this.getColumnNumberByName(DROP_DOWNS_SHEET_NAME, "Version Info");
		if (col == null) return "no version for this file (that's ok, this is not an error. It means there is a more recent version available online.)";
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
			output.append("In column Image External id, rows ");
			Iterator<Entry<Integer, Integer>> it = duplicates.entrySet().iterator();
			
			StringBuffer list = new StringBuffer();
			while (it.hasNext()) {
				Entry<Integer, Integer> dups = it.next();
				list.append(dups.getKey() + " and " + dups.getValue());
				list.append(", ");
			}
			System.out.println(list.toString() + "have duplicate entries.");
			output.append(list.toString() + "have duplicate entries.<br />");
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
				output.append("In column Date Determined, row " + (i+1) + " should be formatted as text.<br />");
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
				output.append("In column Original File Name, row " + (i+1) + " should not contain spaces.<br />");
			}
		}
		return isValid;
	}
	private boolean isEmpty(Cell cell) {
		return cell.getContents().equalsIgnoreCase("");
	}
	
	private boolean checkDBMatch() {
		boolean matchDB = true;
//		MorphbankConfig.setPersistenceUnit(PERSISTENCE);
//		MorphbankConfig.init();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE);
		em = emf.createEntityManager();
		matchDB &= this.checkTSN();
		matchDB &= this.checkCredentials();
		
		return matchDB;
	}
	
	private boolean checkTSN() {
		boolean columnValid = true;
		Cell[] cellsDetermination = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Determination Scientific Name"));
		Cell[] cellsTSN = dataSheet.getColumn(this.getColumnNumberByName(DATA_SHEET_NAME, "Determination TSN"));
		String select = "select t.tsn from Taxon t where t.scientificName = :scientificName";
		Query query = em.createQuery(select);
//		query = MorphbankConfig.getEntityManager().createQuery(select);
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
					System.out.println("Scientific name " + cellsDetermination[i].getContents() + " does not match TSN " + cellsTSN[i].getContents() + " at row " + (i+1) + ".");
					output.append("Scientific name " + cellsDetermination[i].getContents() + " does not match TSN " + cellsTSN[i].getContents() + " at row " + (i+1) + ".<br />");
				}
				columnValid &= matchFound;
			}
			else {
				System.out.println("Scientific name " + cellsDetermination[i] + " at row " + (i+1) + " is not in Morphbank.");
				output.append("Scientific name " + cellsDetermination[i] + " at row " + (i+1) + " is not in Morphbank.<br />");
			}
		}
		return columnValid;
	}
	
	private boolean checkCredentials() {
		boolean credentialsOK = true;
		boolean emptyCells = false;
		String cName = contributorSheet.getCell(1, 1).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 1).getContents(), cName);
		String cId = contributorSheet.getCell(1, 2).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 2).getContents(), cId);
		String sName = contributorSheet.getCell(1, 3).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 3).getContents(), sName);
		String sId = contributorSheet.getCell(1, 4).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 4).getContents(), sId);
		String gName = contributorSheet.getCell(1, 5).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 5).getContents(), gName);
		String gId = contributorSheet.getCell(1, 6).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 6).getContents(), gId);
		String date = contributorSheet.getCell(1, 7).getContents();
		emptyCells |= isCellEmpty(contributorSheet.getCell(0, 7).getContents(), date);
		if(emptyCells) return false;
		
		String select = "select u.userName, u.id from User u where u.userName = :name";
		Query query = em.createQuery(select);
//		query = MorphbankConfig.getEntityManager().createQuery(select);
		query.setParameter("name", cName);
		credentialsOK &= this.compareNameId(query, cName, cId);
		query.setParameter("name", sName);
		credentialsOK &= this.compareNameId(query, sName, sId);
		select = "select g.groupName, g.id from Group g where g.groupName = :name";
		query = em.createQuery(select);
//		query = MorphbankConfig.getEntityManager().createQuery(select);
		query.setParameter("name", gName);
		credentialsOK &= this.compareNameId(query, gName, gId);
		
		select = "select u.groups from User u where u.id = :id";
		query = em.createQuery(select);
//		query = MorphbankConfig.getEntityManager().createQuery(select);
		query.setParameter("id", Integer.valueOf(cId));
		credentialsOK &= this.compareUserGroup(query, cName, cId, gName, gId);
		
		return credentialsOK;
	}
	
	private boolean isCellEmpty(String label, String cell) {
		if (cell.length() < 1) {
			System.out.println(label.replaceFirst(":", "") + " cannot be empty.");
			output.append(label.replaceFirst(":", "") + " cannot be empty.<br />");
			return true;
		}
		return false;
	}
	
	private boolean compareNameId(Query query, String name, String id) {
		List names = query.getResultList();
		if (names.isEmpty()) {
			System.out.println(name + " is not in Morphbank.");
			output.append(name + " is not in Morphbank.<br />");
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
					output.append(name + " and " + id + " do not match. One of them must be misstyped.<br />");
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
			output.append("Id:" + id + " is not in the group " + groupName + ".<br />");
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
			output.append("Id:" + id + " is not in the group " + groupName + ".");
		}
		return matchFound;
	}

	public StringBuffer getOutput() {
		return output;
	}

	
	
}
