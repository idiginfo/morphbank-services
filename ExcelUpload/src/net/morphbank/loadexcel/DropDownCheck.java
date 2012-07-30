package net.morphbank.loadexcel;

import jxl.Cell;
import jxl.Sheet;

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
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the Locality sheet. Checks that values for locality in the
	 * Specimen sheet are listed in the Locality sheet.
	 * 
	 * @return
	 */
	public boolean isSpecimenVSLocalityOk() {
		Sheet specimenSheet = sheetReader.getSheet("Specimen");
		Sheet localitySheet = sheetReader.getSheet("Locality");
		// build a list of localities in Locality
		int colLocalityName = sheetReader.getColumnNumberByName("Locality",
				"Locality Name [Auto generated--Do not change!]");
		Cell[] localityNamesLocality = localitySheet.getColumn(colLocalityName);
		// build a list of localities in Specimen
		int colSpecimenLocality = sheetReader.getColumnNumberByName("Specimen",
				"Locality");
		Cell[] localityNamesSpecimen = specimenSheet
				.getColumn(colSpecimenLocality);

		return this.testColumns(localityNamesSpecimen, localityNamesLocality,
				"Specimen", "Locality", null);
	}

	/**
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the Specimen sheet. Checks that values for specimen in the
	 * Image sheet are listed in the Specimen sheet.
	 * 
	 * @return
	 */
	public boolean isImageVSSpecimenOk() {
		Sheet specimenSheet = sheetReader.getSheet("Specimen");
		Sheet imageSheet = sheetReader.getSheet("Image");
		// build a list of specimens in Specimen
		int colSpecimenName = sheetReader.getColumnNumberByName("Specimen",
				ExcelTools.COL_SPECIMEN_DESCRIPTION);
		Cell[] specimenNamesSpecimen = specimenSheet.getColumn(colSpecimenName);
		// build a list of specimen in Images
		int colImageSpecimen = sheetReader.getColumnNumberByName("Image",
				ExcelTools.COL_IMAGE_SPECIMEN_DESCRIPTION);
		Cell[] specimenNamesImage = imageSheet.getColumn(colImageSpecimen);

		return this.testColumns(specimenNamesImage, specimenNamesSpecimen,
				"Image", "Specimen", null);
	}

	/**
	 * The selected value from the dropdown menu needs to match the list
	 * provided in the View sheet. Checks that values for view in the Image
	 * sheet are listed in the View sheet.
	 * 
	 * @return
	 */
	public boolean isImageVSViewOk() {
		Sheet viewSheet = sheetReader.getSheet("View");
		Sheet imageSheet = sheetReader.getSheet("Image");
		// build a list of views in MyView
		int colViewName = sheetReader.getColumnNumberByName("View",
				"My View Name");
		Cell[] viewNamesView = viewSheet.getColumn(colViewName);
		// build a list of views in Images
		int colImageView = sheetReader.getColumnNumberByName("Image",
				"My View Name");
		Cell[] viewNamesImage = imageSheet.getColumn(colImageView);

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
		String sheet1Name = sheet1.getName();
		String sheet2Name = sheet2.getName();
		int col1Num = sheetReader.getColumnNumberByName(sheet1Name, col);
		Cell[] col1Cells = sheet1.getColumn(col1Num);
		int col2Num = sheetReader.getColumnNumberByName(sheet2Name, col);
		Cell[] col2Cells = sheet2.getColumn(col2Num);
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
			if (!col1Sheet.equalsIgnoreCase(ExcelTools.VIEW_SHEET) 
					|| col1[i].getContents().length() > 1 && !col1[i].getContents().equalsIgnoreCase(col2Header)) {
				boolean test = false;
				for (int j = 1; j < col2.length; j++) {
					test |= col1[i].getContents().equals(col2[j].getContents());
					if (col1[i].getContents().length() < 1 || test) {
						test = true;
						break;
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
		return noErrorInColumn;
	}

}
