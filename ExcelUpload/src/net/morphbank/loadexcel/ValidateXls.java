package net.morphbank.loadexcel;

import java.text.DateFormat;

import jxl.Cell;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.format.CellFormat;

/**
 * Set of methods to try to check the excel workbook before using its data
 * @author gjimenez
 *
 */
public class ValidateXls {
	
	private static final String[] FILE_EXTENSIONS = {".tif", ".tiff",".jpg",".jpeg",".bmp",".gif",".png"};
	SheetReader sheetReader;
	private boolean isXlsValid = true;
	private boolean versionInfo;
	StringBuffer output = new StringBuffer();

	public static void main(String[] args) {
		SheetReader sheetReader = new SheetReader("/home/gjimenez/Downloads/MatthewBuffington/mb3a_Oberthuerellinae04.xls", null);
		ValidateXls test = new ValidateXls(sheetReader, true);
		System.out.println(test.checkEverything());
	}
	
	public ValidateXls(SheetReader sheetReader, boolean versionInfo) {
		this.sheetReader = sheetReader;
		this.versionInfo = versionInfo;
	}
	
	public boolean checkEverything() {
		if (this.sheetReader == null) {
			System.out.println("Unknown error with the Excel file. Please try again.");
			output.append("Unknown error with the Excel file. Please try again.");
			return false;
		}
		if (versionInfo) {
			System.out.println("Version Info: " + getVersionNumber());
			output.append("Version Info: " + getVersionNumber() + "<br />");
		}
		isXlsValid &= checkCredentials();
		isXlsValid &= isSpecimenVSLocalityOk();
		isXlsValid &= isImageVSSpecimenOk();
		isXlsValid &= isImageVSViewOk();
		isXlsValid &= checkDateFormat();
		isXlsValid &= checkOriginalFileName();
		isXlsValid &= checkLatLong();
		isXlsValid &= isViewTSNOk();
		return isXlsValid;
	}
	
	public boolean isSpecimenVSLocalityOk() {
		Sheet specimenSheet = sheetReader.getSheet("Specimen");
		Sheet localitySheet = sheetReader.getSheet("Locality");
		//build a list of localities in Locality
		int colLocalityName = sheetReader.getColumnNumberByName("Locality", "Locality Name [Auto generated--Do not change!]");
		Cell[] localityNamesLocality = localitySheet.getColumn(colLocalityName);
		//build a list of localities in Specimen
		int colSpecimenLocality = sheetReader.getColumnNumberByName("Specimen", "Locality");
		Cell[] localityNamesSpecimen = specimenSheet.getColumn(colSpecimenLocality);
			
		return this.testColumns(localityNamesSpecimen, localityNamesLocality, "Specimen", "Locality");
	}
	
	public boolean isImageVSSpecimenOk() {
		Sheet specimenSheet = sheetReader.getSheet("Specimen");
		Sheet imageSheet = sheetReader.getSheet("Image");
		//build a list of specimens in Specimen
		int colSpecimenName = sheetReader.getColumnNumberByName("Specimen", ExcelTools.COL_SPECIMEN_DESCRIPTION);
		Cell[] specimenNamesSpecimen = specimenSheet.getColumn(colSpecimenName);
		//build a list of specimen in Images
		int colImageSpecimen = sheetReader.getColumnNumberByName("Image", ExcelTools.COL_IMAGE_SPECIMEN_DESCRIPTION);
		Cell[] specimenNamesImage = imageSheet.getColumn(colImageSpecimen);
			
		return this.testColumns(specimenNamesImage, specimenNamesSpecimen, "Image", "Specimen");
	}
	
	public boolean isImageVSViewOk() {
		Sheet viewSheet = sheetReader.getSheet("View");
		Sheet imageSheet = sheetReader.getSheet("Image");
		//build a list of views in MyView
		int colViewName = sheetReader.getColumnNumberByName("View", "My View Name");
		Cell[] viewNamesView = viewSheet.getColumn(colViewName);
		//build a list of views in Images
		int colImageView = sheetReader.getColumnNumberByName("Image", "My View Name");
		Cell[] viewNamesImage = imageSheet.getColumn(colImageView);
			
		return this.testColumns(viewNamesImage, viewNamesView, "Image", "View");
	}
	
	
	public boolean isViewTSNOk() {
		boolean isValid = true;
		Sheet viewSheet = sheetReader.getSheet("View");
		int ViewTSNCol = sheetReader.getColumnNumberByName("View", ExcelTools.VIEW_APPLICABLE_TO_TAXON);
		Cell[] cells = viewSheet.getColumn(ViewTSNCol);
		Cell[] viewNames = viewSheet.getColumn(1);
		int firstRow = findCustomView(cells) + 1;
		if (firstRow == 0) return false;
		for (int i = firstRow; i < viewNames.length; i++) {
			//if (any cell is row is not empty) then ViewTSN cannot be empty
			if (!viewNameEmpty(viewNames[i].getContents()) && isEmpty(cells[i])){
				String error = "In MyView sheet, row " + (i+1) + " cannot have " + ExcelTools.VIEW_APPLICABLE_TO_TAXON + " empty.";
				System.out.println(error);
				this.messageToOuput(error);
				isValid = false;
			}
		}
		return isValid;
	}
	
	private int findCustomView(Cell[] cells) {
		for (int i = 1; i < cells.length; i++) {
			if (cells[i].getContents().equalsIgnoreCase(ExcelTools.VIEW_APPLICABLE_TO_TAXON))
				return i;
		}
		return -1;
	}
	
	private boolean viewNameEmpty(String viewName) {
		if (viewName.endsWith("//////")) return true;
		return false;
	}
	
	/**
	 * Checks if the selected item from a drop down list 
	 * actually belongs to the list. An error in the spreadsheet may happen
	 * if the user modified the drop down list entries without updating the 
	 * item selected.
	 * @param col1 column from the drop down list
	 * @param col2 column with values generating the drop down list
	 * @param col1Sheet name of the spreadsheet (used for error message)
	 * @param col2Sheet name of the spreadsheet (used for error message)
	 * @return false is there is at least one error (no match found), true otherwise
	 */
	private boolean testColumns(Cell[] col1, Cell[] col2, String col1Sheet, String col2Sheet){
		boolean noErrorInColumn = true;
		for (int i=1; i < col1.length; i++) {
			boolean test = false;
			for (int j=1; j < col2.length; j++) {
				test |= col1[i].getContents().equalsIgnoreCase(col2[j].getContents());
				if(col1[i].getContents().length() < 1 || test) {
					test = true;
					break;
				}
			}
			if (!test) {
				System.err.println("The " + col1Sheet + "'s " + col2Sheet.toLowerCase() +" row " + (i+1) +
						" does not match any " + col2Sheet.toLowerCase() +
						" in the " + col2Sheet + " spreadsheet.");
				output.append("The " + col1Sheet + "'s " + col2Sheet.toLowerCase() +" row " + (i+1) +
						" does not match any " + col2Sheet.toLowerCase() +
						" in the " + col2Sheet + " spreadsheet. <br />");
				noErrorInColumn &= false;
			}
		}
		return noErrorInColumn;
	}
	
	private String getVersionNumber() {
		Integer col = sheetReader.getColumnNumberByName("SupportData", "Version Info");
		if (col == null) return "no version for this file. (that's ok, this is not an error. It means there is a more recent version available online.)";
		return sheetReader.getEntry("SupportData", col, 1);
	}
	
	public StringBuffer getOutput() {
		return output;
	}
	
	/**
	 * Checks if any required field in ImageCollection
	 * is empty.
	 * @return false if any mandatory field is empty
	 */
	private boolean checkCredentials() {
		Sheet credentials = sheetReader.getSheet("ImageCollection");
		boolean anyEmpty = false;
		for (int i= 3; i <= 7; i++) {
			if (credentials.getCell(1, i) == null || credentials.getCell(1, i).getContents().equalsIgnoreCase("")) {
				String error = credentials.getCell(0, i).getContents().replaceAll(":", "") + " cannot be empty.";
				System.out.println(error);
				this.messageToOuput(error + "<br />");
				anyEmpty = true;
			}
				
		}
		return !anyEmpty;
	}

	private boolean checkDateFormat() {
		String date;
		boolean correctFormat = true;
		Cell[] specimenDescription = sheetReader.getSheet("Specimen").getColumn(sheetReader.getColumnNumberByName("Specimen", "Specimen Description [Autogenerated -- do not change!]"));
		Cell[] dateCollected = sheetReader.getSheet("Specimen").getColumn(sheetReader.getColumnNumberByName("Specimen", "Date Collected"));
		for (int i = 1; i < specimenDescription.length; i++) {
			date = specimenDescription[i].getContents();
			boolean datePresent = true;
			if (dateCollected[i].getContents() == null || dateCollected[i].getContents().equalsIgnoreCase(""))
				datePresent = false;
			if (date.equalsIgnoreCase("#VALUE!") || !this.dateOnSpecimenDescription(date, datePresent)) {
				String error = "Date Collected row " + (i + 1)  + " does not have the format yyyy-mm-dd";
				System.out.println(error);
				this.messageToOuput(error);
				correctFormat = false;
			}
			
		}
		return correctFormat;
	}
	
	private boolean dateOnSpecimenDescription(String description, boolean isDatePresent) {
		if (!isDatePresent) return true;
		String date = description.substring(description.lastIndexOf("/") + 1);
		String[] split = date.split("-");
		if (split == null || split.length != 3 || split[0].length() != 4 || split[1].length() != 2 || split[2].length() != 2) return false;
			try {
				Integer.valueOf(split[0]);
				Integer.valueOf(split[1]);
				Integer.valueOf(split[2]);
			} catch (NumberFormatException e) {
				return false;
			}
		
		return true;
	}
	
	
	private boolean checkOriginalFileName() {
		Cell[] cells = sheetReader.getSheet("Image").getColumn(sheetReader.getColumnNumberByName("Image", "Image file name"));
		boolean isValid = true;
		String error = "In column Image File Name, row ";
		for (int i = 1; i < cells.length; i++) {
			if (isEmpty(cells[i])) continue;
			if (cells[i].getContents().indexOf(" ") > 0) {
				isValid = false;
				String message = error + (i+1) + " should not contain spaces.";
				System.out.println(message);
				this.messageToOuput(message);
			}
			if (!fileExtensionOk(cells[i].getContents())) {
				isValid = false;
				String message = error + (i+1) + " file extension should be " + outputListOfExtensions();
				System.out.println(message);
				this.messageToOuput(message);
			}
//			if (!fileNameFormattedOk(cells[i].getContents())) {
//				isValid = false;
//				String message = error + (i+1) + " cannot use '.' in the file name.";
//				System.out.println(message);
//				this.messageToOuput(message);
//			}
		}
		return isValid;
	}
	
	public static boolean fileNameFormattedOk(String fileName) {
		String[] split = fileName.split("\\.");
		int length = split.length;
		if (length == 2 || length == 1) return true;
		return false;
	}

	public static boolean fileExtensionOk(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot == -1) return false;
		String extension = fileName.substring(dot);
		for (int i = 0; i < FILE_EXTENSIONS.length; i++) {
			if (extension.equalsIgnoreCase(FILE_EXTENSIONS[i])) return true;
		}
		return false;
	}
	
	public static boolean isEmpty(Cell cell) {
		return cell.getContents().equalsIgnoreCase("");
	}
	
	private void messageToOuput(String message) {
		output.append(message);
		output.append("<br />");
	}
	
	public static String outputListOfExtensions() {
		StringBuffer list = new StringBuffer(64);
		for (int i = 0; i < (FILE_EXTENSIONS.length - 1); i++) {
			list.append(FILE_EXTENSIONS[i].substring(1));
			list.append(", ");
		}
		list.append("or ");
		list.append(FILE_EXTENSIONS[FILE_EXTENSIONS.length -1].substring(1));
		list.append(".");
		return list.toString();
		
	}
	
	private boolean checkLatLong() {
		Cell[] latitude = sheetReader.getSheet("Locality").getColumn(sheetReader.getColumnNumberByName("Locality", "Latitude"));
		Cell[] longitude = sheetReader.getSheet("Locality").getColumn(sheetReader.getColumnNumberByName("Locality", "Longitude"));
		boolean formatting = true;
		for (int i = 1; i < Math.min(latitude.length, longitude.length); i++) {
			try {
				if (latitude[i] != null && !latitude[i].getContents().equalsIgnoreCase(""))
					Double.parseDouble(latitude[i].getContents());
				if (longitude[i] != null && !longitude[i].getContents().equalsIgnoreCase(""))
					Double.parseDouble(longitude[i].getContents());
			} catch (Exception e) {
				String error = "In Locality sheet, row " + (i+1) + " longitude and latitude have to be a decimal value.";
				System.out.println(error);
				this.messageToOuput(error);
				formatting = false;
			}
		}
		return formatting;
	}
}
