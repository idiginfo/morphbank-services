package net.morphbank.loadexcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class DropDownCheck {

	SheetReader sheetReader;
	StringBuffer output = new StringBuffer();

	public DropDownCheck(SheetReader sheetReader, StringBuffer output) {
		super();
		this.sheetReader = sheetReader;
		this.output = output;
	}

	/**
	 * Convient method to call all tests instead of doing it individually
	 * 
	 * @return true if all tests are passed
	 */
	public boolean checkAll() {
		boolean isXlsValid = true;
		isXlsValid &= this.isSpecimenVSLocalityOk();
		isXlsValid &= this.isImageVSSpecimenOk();
		isXlsValid &= this.isImageVSViewOk();
		isXlsValid &= this.isMyViewOk();
		return isXlsValid;
	}
	
	
	/**
	 * Convenient method to get all columns of a particular 
	 * 	column number from a specified sheet 
	 * 
	 * @return array of cell
	 */
	private Cell[] getColumn(Sheet sheet, int columnNum){
		int numOfRows = sheet.getLastRowNum();
		Cell cell = null;
		Cell[] destCell = new Cell[numOfRows];
		int locIx=0;
		for (Row row : sheet) {
			cell = null;
			cell =  row.getCell(columnNum);
			if(cell != null) {
				if(locIx==numOfRows){
					break;
				}
				destCell[locIx++] = cell;
			}
		}
		return destCell;
	}

	/**
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the Locality sheet. Checks that values for locality in the
	 * Specimen sheet are listed in the Locality sheet.
	 * 
	 * @return
	 */
	public boolean isSpecimenVSLocalityOk() {
				
		// build a list of localities in Locality
		Cell[] localityNamesLocality = sheetReader.getColumnCells(ExcelTools.LOCALITY_SHEET, ExcelTools.COL_LOCALITY_NAME);
		
		// build a list of localities in Specimen
		Cell[] localityNamesSpecimen = sheetReader.getColumnCells(ExcelTools.SPECIMEN_SHEET,ExcelTools.COL_LOCALITY);
		
		return this.testColumns(localityNamesSpecimen, localityNamesLocality,
				ExcelTools.SPECIMEN_SHEET, ExcelTools.LOCALITY_SHEET, null);
	}

	/**
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the Specimen sheet. Checks that values for specimen in the
	 * Image sheet are listed in the Specimen sheet.
	 * 
	 * @return
	 */
	public boolean isImageVSSpecimenOk() {
		
		// build a list of specimen description in Specimen
		Cell[] specimenNamesSpecimen = sheetReader.getColumnCells(ExcelTools.SPECIMEN_SHEET, ExcelTools.COL_SPECIMEN_DESCRIPTION);
				
		// build a list of specimen in Images
		Cell[] specimenNamesImage = sheetReader.getColumnCells(ExcelTools.IMAGE_SHEET,ExcelTools.COL_IMAGE_SPECIMEN_DESCRIPTION); 
				
		return this.testColumns(specimenNamesImage, specimenNamesSpecimen,
				ExcelTools.IMAGE_SHEET, ExcelTools.SPECIMEN_SHEET, null);
	}

	/**
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the View sheet. Checks that values for view in the Image
	 * sheet are listed in the View sheet.
	 * 
	 * @return
	 */
	public boolean isImageVSViewOk() {
		
		// build a list of views in MyView
		Cell[] viewNamesView = sheetReader.getColumnCells(ExcelTools.VIEW_SHEET,ExcelTools.COL_MY_VIEW_NAME);
				
		// build a list of views in Images
		Cell[] viewNamesImage = sheetReader.getColumnCells(ExcelTools.IMAGE_SHEET,ExcelTools.COL_MY_VIEW_NAME);
		
		return this.testColumns(viewNamesImage, viewNamesView, "Image", "View", null);
	}

	/**
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the View sheet. Checks that values for in the View sheet are
	 * listed in the SupportingData sheet.
	 * 
	 * @return
	 */
	public boolean isMyViewOk() {
		boolean allColOk = true;
		Sheet viewSheet = sheetReader.getSheet(ExcelTools.VIEW_SHEET);
		Sheet supportDataSheet = sheetReader.getSheet("SupportData");
		Sheet protectedDataSheet = sheetReader.getSheet(ExcelTools.PROTECTED_DATA_SHEET);
		String[] colToCheck = { "Specimen Part", "View Angle",
				"Imaging Technique", "Imaging Preparation Technique",
				"Developmental Stage", "Form" };
		for (String currentColumn:colToCheck){
			allColOk &= this.checkColumnsData(viewSheet, supportDataSheet, currentColumn);
		}
		allColOk &= this.checkColumnsData(viewSheet, protectedDataSheet, "Sex");
		return allColOk;
	}

	private boolean checkColumnsData(Sheet sheet1, Sheet sheet2, String col) {
		String sheet1Name = sheet1.getSheetName();
		String sheet2Name = sheet2.getSheetName();
		Cell[] col1Cells = sheetReader.getColumnCells(sheet1Name, col);
				
		Cell[] col2Cells = sheetReader.getColumnCells(sheet2Name, col);

		return this.testColumns(col1Cells, col2Cells, sheet1Name, sheet2Name, col);
	}

	/**
	 * Checks if the selected item from a drop down list actually belongs to the
	 * list. An error in the spreadsheet may happen if the user modified the
	 * drop down list entries without updating the item selected.
	 * 
	 * @param col1
	 *            column from the drop down list
	 * @param col2
	 *            column with values generating the drop down list
	 * @param col1Sheet
	 *            name of the spreadsheet (used for error message)
	 * @param col2Sheet
	 *            name of the spreadsheet (used for error message)
	 * @return false is there is at least one error (no match found), true
	 *         otherwise
	 */
	private boolean testColumns(Cell[] col1, Cell[] col2, String col1Sheet,
			String col2Sheet, String col2Header) {
		boolean noErrorInColumn = true;
		if (col2Header == null) { //used when the column's name does not equals the sheet's name
			col2Header = col2Sheet.toLowerCase();
		}
		for (int i = 1; i < col1.length; i++) {
			if(col1[i] != null)
			{	
				if (!col1Sheet.equalsIgnoreCase(ExcelTools.VIEW_SHEET) 
						|| col1[i].getStringCellValue().length() > 1 && !col1[i].getStringCellValue().equalsIgnoreCase(col2Header)) {
					boolean test = false;
					for (int j = 1; j < col2.length; j++) {
						if(col2[j] != null)
						{	
							test |= col1[i].getStringCellValue().equals(col2[j].getStringCellValue());
							if (col1[i].getStringCellValue().length() < 1 || test) {
								test = true;
								break;
							}
						}
					}
					if (!test) {
						String error = "The " + col1Sheet + "'s " + col2Header
								+ " row " + (i + 1) + " does not match any "
								+ col2Header + " in the " + col2Sheet
								+ " spreadsheet.";
						System.out.println(error);
						ExcelTools.messageToOutput(error, output);
						noErrorInColumn &= false;
					}
				}
			}
		}
		return noErrorInColumn;
	}

}
