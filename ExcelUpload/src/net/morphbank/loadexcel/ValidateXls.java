package net.morphbank.loadexcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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
	private boolean repeatErrorMessage = true; 
	private static String propertyFile;

//	public ValidateXls(SheetReader sheetReader, boolean versionInfo) {
//		this.sheetReader = sheetReader;
//		this.versionInfo = versionInfo;
//	}
	
	public ValidateXls(SheetReader sheetReader, boolean versionInfo, String propFile) {
		this.sheetReader = sheetReader;
		this.versionInfo = versionInfo;
		propertyFile = propFile;
	}
	
	public boolean checkEverything() {
		if (this.sheetReader == null) {
			String error = "Unknown error with the Excel file. Please try again.";
			System.out.println(error);
			output.append(error);
			return false;
		}
		if (versionInfo) {
			String version = "Version Info: " + getVersionNumber();
			System.out.println(version);
			this.messageToOutput(version);
		}
		String beginTesting = "Let's see what the file looks like...";
		System.out.println(beginTesting);
		ExcelTools.messageToOutput("<b>" + beginTesting + "</b>", output);
		isXlsValid &= checkCredentials();
		DropDownCheck dropDownCheck = new DropDownCheck(sheetReader, output);
		isXlsValid &= dropDownCheck.checkAll();
		System.out.println("DropDown Specimen, Image, ImageVsViewOk, MyViewOk: " + isXlsValid);
		isXlsValid &= checkDateFormat();
		System.out.println("checkDateFormat: " + isXlsValid);
		isXlsValid &= checkOriginalFileName();
		System.out.println("checkOriginalFileName: " + isXlsValid);
		isXlsValid &= checkLatLong();
		System.out.println("checkLatLong: " + isXlsValid);
		isXlsValid &= isViewTSNOk();
		System.out.println("isViewTSNOk: " + isXlsValid);
		isXlsValid &= checkMandatoryCellsNotEmpty();
		System.out.println("checkMandatoryCellsNotEmpty: " + isXlsValid);
		checkExtraSpaces();
		System.out.println("checkExtraSpaces: " + isXlsValid);
		isXlsValid &= checkAgainstDB();
		System.out.println("checkAgainstDB: " + isXlsValid);
		return isXlsValid;
	}
	
	/**
	 * If a view name is not empty it needs to have
	 * a Taxon to be applied to.
	 * @return
	 */
	public boolean isViewTSNOk() {
		boolean isValid = true;
		Sheet viewSheet = sheetReader.getSheet("View");
		int ViewTSNCol = sheetReader.getColumnNumberByName("View", ExcelTools.VIEW_APPLICABLE_TO_TAXON);

		int numOfRows = viewSheet.getLastRowNum();
		Cell cellTSN = null, cellOne = null;
		Cell[] cells = new Cell[numOfRows];
		Cell[] viewNames = new Cell[numOfRows];
		int tsnIx=0, oneIx=0;
		boolean getCellLastTime = false;
		
		for (Row row : viewSheet) {
			cellTSN =  row.getCell(ViewTSNCol);
			if(cellTSN != null) {
				if(tsnIx==numOfRows){
					getCellLastTime = true;
				}
				else{
				cells[tsnIx++] = cellTSN;
				}
			}
			cellOne =  row.getCell(1);
			if(cellOne != null) {
				viewNames[oneIx++] = cellOne;
				if(getCellLastTime)
					break;
			}
		}
		int firstRow = findCustomView(cells) + 1;
		if (firstRow == 0) return false;
		for (int i = firstRow; i < viewNames.length; i++) {
			//if (any cell is row is not empty) then ViewTSN cannot be empty
			if (!viewNameEmpty(viewNames[i].getStringCellValue()) && isEmpty(cells[i])){
				String error = "In MyView sheet, row " + (i+1) + " cannot have " + ExcelTools.VIEW_APPLICABLE_TO_TAXON + " empty.";
				System.out.println(error);
				this.messageToOutput(error);
				isValid = false;
			}
		}
		return isValid;
	}
	
	/**
	 * Find where the Custom Views start
	 * @param cells
	 * @return the first row if found otherwise -1
	 */
	private int findCustomView(Cell[] cells) {
		for (int i = 1; i < cells.length; i++) {
			if (cells[i].getStringCellValue().equalsIgnoreCase(ExcelTools.VIEW_APPLICABLE_TO_TAXON))
				return i;
		}
		return -1;
	}
	
	private boolean viewNameEmpty(String viewName) {
		if (viewName.endsWith("//////")) return true;
		return false;
	}
	
	
	
	private String getVersionNumber() {
		Integer col = sheetReader.getColumnNumberByName("SupportData", "Version Info");
		if (col == -1) return "no version for this file. (that's ok, this is not an error. It means there is a more recent version available online.)";
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
		for (int i= 3; i <= 7; i+=2) {
			
			if (credentials.getRow(i).getCell(1) == null || credentials.getRow(i).getCell(1).getStringCellValue().equalsIgnoreCase("")){

				String error = credentials.getRow(i).getCell(0).getStringCellValue().replaceAll(":", "")  + " cannot be empty.";
				System.out.println(error);
				this.messageToOutput(error + "<br />");
				anyEmpty = true;
			}
		}
		return !anyEmpty;
	}

	private boolean checkDateFormat() {
		String date;
		boolean correctFormat = true;

		int spDescColNum = sheetReader.getColumnNumberByName("Specimen", "Specimen Description [Autogenerated -- do not change!]");
		int spDateColNum = sheetReader.getColumnNumberByName("Specimen", "Date Collected");
		Sheet shReader = sheetReader.getSheet("Specimen");
		int rowNum = shReader.getLastRowNum();
		
		Cell cellDesc = null, cellDate = null;
		Cell[] specimenDescription = new Cell[rowNum];
		Cell[] dateCollected = new Cell[rowNum];
		int descIx=0, dateIx=0;
		boolean fetchLastTime = false;
		
		for (Row row : shReader) {
			cellDesc =  row.getCell(spDescColNum);
			if(cellDesc != null) {
				if(descIx==rowNum){
					fetchLastTime = true;
				}
				else{
					specimenDescription[descIx] = cellDesc;
					descIx++;
				}
			}
			
			cellDate =  row.getCell(spDateColNum);
			if(cellDate != null) {
				if(!fetchLastTime){
					dateCollected[dateIx] = cellDate;
					dateIx++;
				}
				else{
					break;
				}
				
			}
		} 
		//This section is a hack when getColumn does not return the whole column because cells are empty
		if (dateCollected.length != specimenDescription.length) {
			Cell[] dateCollectedExtended = new Cell[specimenDescription.length];
			Arrays.fill(dateCollectedExtended, dateCollected[dateCollected.length - 1]);
			for (int i=0; i < dateCollected.length ; i++) {
				dateCollectedExtended[i] = dateCollected[i];
			}
			dateCollected = dateCollectedExtended;
		}
		//First row has the heading, so it should start reading the date values
		// from 2nd row
		for (int i = 1; i < specimenDescription.length; i++) {
			date = specimenDescription[i].getStringCellValue();
			
			boolean datePresent = true;
			if (dateCollected[i].getStringCellValue() == null || dateCollected[i].getStringCellValue().equalsIgnoreCase(""))
				datePresent = false;
			if (date.equalsIgnoreCase("#VALUE!") || !this.dateOnSpecimenDescription(date, datePresent)) {
				String error = "Date Collected row " + (i + 1)  + " does not have the format yyyy-mm-dd";
				System.out.println(error);
				this.messageToOutput(error);
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
		Sheet imageSheet = sheetReader.getSheet("Image");
		int colNum = sheetReader.getColumnNumberByName("Image", "Image file name");
		int numOfRows = imageSheet.getLastRowNum();
		Cell cellImg = null;
		Cell[] cells = new Cell[numOfRows];
		int imgIx=0;
		for (Row row : imageSheet) {
			cellImg =  row.getCell(colNum);
			if(cellImg != null) {
				if(imgIx==numOfRows){
					break;
				}
				cells[imgIx++] = cellImg;
			}
		}
		boolean isValid = true;
		String error = "In column Image File Name, row ";
		for (int i = 1; i < cells.length; i++) {
			if (isEmpty(cells[i])) continue;
			if (cells[i].getStringCellValue().indexOf(" ") > 0) {
				isValid = false;
				String message = error + (i+1) + " should not contain spaces.";
				System.out.println(message);
				this.messageToOutput(message);
			}
			if (cells[i].getStringCellValue().indexOf("'") > 0) {
				isValid = false;
				String message = error + (i+1) + " should not contain simple quotes.";
				System.out.println(message);
				this.messageToOutput(message);
			}
			if (!fileExtensionOk(cells[i].getStringCellValue())) {
				isValid = false;
				String message = error + (i+1) + " file extension should be " + outputListOfExtensions();
				System.out.println(message);
				this.messageToOutput(message);
			}
		}
		return isValid;
	}
	
	public static boolean fileNameFormattedOk(String fileName) {
		String[] split = fileName.split("\\.");
		int length = split.length;
		if (length == 2 || length == 1) return true;
		return false;
	}

	/**
	 * The file's extension belongs to the provided list
	 * @param fileName
	 * @return
	 */
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
		return cell.getStringCellValue().equalsIgnoreCase("");
	}
	
	private void messageToOutput(String message) {
		ExcelTools.messageToOutput(message, output);
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
	
	/**
	 * Both latitute and longitude need to be decimal numbers.
	 * @return true is the format is correct
	 */
	private boolean checkLatLong() {
		int latColNum = sheetReader.getColumnNumberByName(ExcelTools.LOCALITY_SHEET, ExcelTools.COL_LATITUDE);
		int lonColNum = sheetReader.getColumnNumberByName(ExcelTools.LOCALITY_SHEET, ExcelTools.COL_LONGITUDE);
		Sheet sheetLocality = sheetReader.getSheet(ExcelTools.LOCALITY_SHEET);
		
		int rowNum = sheetLocality.getLastRowNum()+1;
		
		Cell cellLat = null, cellLon = null;
		Cell[] latitude = new Cell[rowNum];
		Cell[] longitude = new Cell[rowNum];
		int latIx=0, lonIx=0;
		
		for (Row row : sheetLocality) {
			cellLat =  row.getCell(latColNum);
			if(cellLat != null) {
				latitude[latIx++] = cellLat;
			}
			
			cellLon =  row.getCell(lonColNum);
			if(cellLon != null) {
				longitude[lonIx++] = cellLon;
			}
		}
		boolean formatting = true;
		for (int i = 1; i < Math.min(latitude.length, longitude.length); i++) {
			try {
				if (latitude[i] != null && !latitude[i].getStringCellValue().equalsIgnoreCase(""))
					Double.parseDouble(latitude[i].getStringCellValue());
				if (longitude[i] != null && !longitude[i].getStringCellValue().equalsIgnoreCase(""))
					Double.parseDouble(longitude[i].getStringCellValue());
			} catch (Exception e) {
				String error = "In Locality sheet, row " + (i+1) + " longitude and latitude have to be a decimal value.";
				System.out.println(error);
				this.messageToOutput(error);
				formatting = false;
			}
		}
		return formatting;
	}
	
	/** check if a row with mandatory cells is either all filled or all empty
	 * Applicable to Images and Specimen sheets
	 * @return true is test passed
	 */
	private boolean checkMandatoryCellsNotEmpty() {
		boolean isValid = true;
		//either all cells empty or all full
		ArrayList<String> sheetsNames = new ArrayList<String>();
		sheetsNames.add(ExcelTools.IMAGE_SHEET);
		sheetsNames.add(ExcelTools.SPECIMEN_SHEET);
		sheetsNames.add(ExcelTools.SPECIMEN_TAXON_DATA_SHEET);
		
		for(String sheetName:sheetsNames) {
			Sheet sheet = sheetReader.getSheet(sheetName);
			int maxRows = sheet.getLastRowNum();
			Row row;
			for (int i = 1; i < maxRows; i++) {
			    row = sheet.getRow(i);
			    Cell allCellsAtRow[] = new Cell[row.getLastCellNum()];
				for (Cell cell : row) {
					allCellsAtRow[cell.getColumnIndex()] = cell;
				}
				String[] rowStr = this.getMandatoryRow(sheetName, allCellsAtRow);
				isValid &= this.checkMandatoryRow(rowStr, sheetName, i);
			}
		}
		
		Sheet imagesSheet = sheetReader.getSheet(ExcelTools.IMAGE_SHEET);
		int maxRows = imagesSheet.getLastRowNum();
		Row imgRow;
		for (int i = 1; i < maxRows; i++) {
			imgRow = imagesSheet.getRow(i);
			Cell allCellsAtRow[] = new Cell[imgRow.getLastCellNum()];
			for (Cell cell : imgRow) {
				allCellsAtRow[cell.getColumnIndex()] = cell;
			}
			String[] row = this.getMandatoryRow(ExcelTools.IMAGE_SHEET, allCellsAtRow);
			if (row==null) continue;
			if(! this.checkMandatoryRow(row, ExcelTools.IMAGE_SHEET, i)){
				isValid = false;
			}
		}

		Sheet specimenSheet = sheetReader.getSheet(ExcelTools.SPECIMEN_SHEET);
		maxRows = specimenSheet.getLastRowNum();
		Row spcRow;
		for (int i = 2; i < maxRows; i++) {
			spcRow = specimenSheet.getRow(i);
			Cell allCellsAtRow[] = new Cell[spcRow.getLastCellNum()];
			for (Cell cell : spcRow) {
				allCellsAtRow[cell.getColumnIndex()] = cell;
			}
			String[] row = this.getMandatoryRow(ExcelTools.SPECIMEN_SHEET, allCellsAtRow);
			if (row==null) continue;
			isValid &= this.checkMandatoryRow(row, ExcelTools.SPECIMEN_SHEET, i);
		}
		return isValid;
	}
	
	private String[] getMandatoryRow(String type, Cell[] entireRow){
		String message = "Some columns are missing. Please use an updated spreadsheet from http://www.morphbank.net.";
		if (repeatErrorMessage) {
			if ((entireRow == null || entireRow.length < 2)) {
				System.out.println(message);
				messageToOutput(message);
				repeatErrorMessage = false;
				return null;
			}
		}
		if (type.equals(ExcelTools.IMAGE_SHEET)) {
			int colISpecimenDescription= sheetReader.getColumnNumberByName(type, ExcelTools.COL_IMAGE_SPECIMEN_DESCRIPTION);
			int colIMyViewName = sheetReader.getColumnNumberByName(type, ExcelTools.COL_MY_VIEW_NAME);
			int colICopyright= sheetReader.getColumnNumberByName(type, ExcelTools.COL_COPYRIGHT_INFO);
			int colIImageFileName = sheetReader.getColumnNumberByName(type, ExcelTools.COL_IMAGE_FILE_NAME);
			int colICreativeCommons = sheetReader.getColumnNumberByName(type, ExcelTools.COL_CREATIVE_COMMONS);
			if (colISpecimenDescription < 0 || colIMyViewName < 0 || colICopyright < 0 || colIImageFileName < 0 || colICreativeCommons < 0) {
				if (repeatErrorMessage) {
					System.out.println(message);
					messageToOutput(message);
					repeatErrorMessage = false;
				}
				return null;
			}
			int[] colNumbers = {colISpecimenDescription, colIMyViewName, colICopyright, colIImageFileName, colICreativeCommons};
			if (getMaxFromTable(colNumbers) > (entireRow.length - 1)) return null;
			String[] row = new String[5];
			
			row[0] = entireRow[colISpecimenDescription].getStringCellValue(); 
			row[1] = entireRow[colIMyViewName].getStringCellValue(); 
			row[2] = entireRow[colICopyright].getStringCellValue(); 
			row[3] = entireRow[colIImageFileName].getStringCellValue();
 			if (entireRow[colICreativeCommons].getCellType() == Cell.CELL_TYPE_FORMULA) {
				row[4] = entireRow[colICreativeCommons].getStringCellValue();
			}
			else {
				row[4] = entireRow[colICreativeCommons].getStringCellValue();
			}
			return row;
		}
		if (type.equals(ExcelTools.SPECIMEN_SHEET)) {
			int colSScientificName = sheetReader.getColumnNumberByName(type, ExcelTools.COL_SCIENTIFIC_NAME);
			int colSBasisOfRecord = sheetReader.getColumnNumberByName(type, ExcelTools.COL_BASIS_OF_RECORD);
			int colSSex = sheetReader.getColumnNumberByName(type, ExcelTools.COL_SEX);
			int colSDevelopmentalStage = sheetReader.getColumnNumberByName(type, ExcelTools.COL_DEVELOPMENTAL_STAGE);
			int colSForm = sheetReader.getColumnNumberByName(type, ExcelTools.COL_FORM);
			int colSTypeStatus = sheetReader.getColumnNumberByName(type, ExcelTools.COL_TYPE_STATUS);
			int colSLocality = sheetReader.getColumnNumberByName(type, ExcelTools.COL_LOCALITY);
			int[] colNumbers = {colSScientificName, colSBasisOfRecord, colSSex, colSDevelopmentalStage, colSForm, colSTypeStatus, colSLocality};
			if (getMaxFromTable(colNumbers) > (entireRow.length - 1)) return null;
			String[] row = new String[7];
			row[0] = entireRow[colSScientificName].getStringCellValue(); 
			row[1] = entireRow[colSBasisOfRecord].getStringCellValue(); 
			row[2] = entireRow[colSSex].getStringCellValue(); 
			row[3] = entireRow[colSDevelopmentalStage].getStringCellValue(); 
			row[4] = entireRow[colSForm].getStringCellValue();
			row[5] = entireRow[colSTypeStatus].getStringCellValue(); 
			row[6] = entireRow[colSLocality].getStringCellValue();
			return row;
		}
		
		if (type.equals(ExcelTools.SPECIMEN_TAXON_DATA_SHEET)) {
			int colTaxonFamily = sheetReader.getColumnNumberByName(type, ExcelTools.COL_FAMILY);
			int colTaxonScNameString = sheetReader.getColumnNumberByName(type, ExcelTools.COL_SCIENTIFICNAMESTRING);
			if (Math.max(colTaxonFamily, colTaxonScNameString) > (entireRow.length - 1)) return null;
			String[] row = new String[2];
			row[0] = entireRow[colTaxonFamily].getStringCellValue(); 
			row[1] = entireRow[colTaxonScNameString].getStringCellValue(); 
			return row;
		}
		
		return null;
	}
	
	
	private boolean checkMandatoryRow(String[] row, String sheetName, int rowNumber) {
		if (row == null) return true;
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
		if (!(hasContent || isEmpty)) printMandatoryCellError(sheetName, rowNumber);
		return hasContent || isEmpty;
	}
	
	private void printMandatoryCellError(String sheet, int row) {
		String error = "In " + sheet + " sheet, row " + (row + 1) + ", one or more mandatory cells are empty.";
		System.out.println(error);
		this.messageToOutput(error);
	}
	
	public static int getMaxFromTable(int[] table) {
		if (table == null) return -1;
		int max = -1;
		max = table[0];
		for (int current:table) {
			if (current > max) {
				max = current;
			}
		}
		return max;
	}
	
	private boolean checkAgainstDB() {
		ValidateAgainstDatabase checkDB = new ValidateAgainstDatabase(sheetReader, output, propertyFile);
		boolean testPassed = true;
//		testPassed &= checkDB.checkTaxa();
		testPassed &= checkDB.checkCredentials();
		return testPassed;
		
	}
	
	/**
	 * Display warnings about fields that have trailing spaces which
	 * may be unintended.
	 * This should not stop the upload.
	 * @return false is extra spaces have been found
	 */
	private boolean checkExtraSpaces() {
		//columns to check: Copyright Info (Images), Catalog Number (Specimen), Collector(s) Name (Specimen), Locality Description (Locality)
		boolean noSpaceFound = true;
		HashMap<String, Cell[]> columnsAndNames = new HashMap<String, Cell[]>();
		Sheet imageSheet = sheetReader.getSheet(ExcelTools.IMAGE_SHEET);
		Sheet specimenSheet = sheetReader.getSheet(ExcelTools.SPECIMEN_SHEET);
		Sheet localitySheet = sheetReader.getSheet(ExcelTools.LOCALITY_SHEET);
		

		int colNum = sheetReader.getColumnNumberByName(ExcelTools.IMAGE_SHEET, ExcelTools.COL_COPYRIGHT_INFO);
		Cell cell = null;
		int numOfRows = imageSheet.getLastRowNum();
		Cell[] allCellAtColImg = new Cell[numOfRows];
		
		int index=0;
		for (Row row : imageSheet) {
			cell =  row.getCell(colNum);
			if(cell != null) {
				if(numOfRows==index)
					break;
				allCellAtColImg[index++] = cell;
			}
		}
		columnsAndNames.put(ExcelTools.COL_COPYRIGHT_INFO, allCellAtColImg);
		allCellAtColImg = null;
		
		colNum = sheetReader.getColumnNumberByName(ExcelTools.SPECIMEN_SHEET, ExcelTools.COL_COLLECTOR_NAME);
		cell = null;
		int spNumOfRow =  specimenSheet.getLastRowNum();
		Cell[] allCellAtColSpcName = new Cell[spNumOfRow];
		index=0;
		
		for (Row row : specimenSheet) {
			cell =  row.getCell(colNum);
			if(cell != null) {
				if(index==spNumOfRow)
					break;
				allCellAtColSpcName[index++] = cell;
			}
		}
		columnsAndNames.put(ExcelTools.COL_COLLECTOR_NAME, allCellAtColSpcName);
		allCellAtColSpcName = null;
		
		colNum = sheetReader.getColumnNumberByName(ExcelTools.SPECIMEN_SHEET, ExcelTools.COL_CATALOG_NUMBER);
		cell = null;
		Cell[] allCellAtColSpcNumber = new Cell[specimenSheet.getLastRowNum()];
		index=0;
		for (Row row : specimenSheet) {
			cell =  row.getCell(colNum);
			if(cell != null) {
				if(index==spNumOfRow)
					break;
				allCellAtColSpcNumber[index++] = cell;
			}
		}
		columnsAndNames.put(ExcelTools.COL_CATALOG_NUMBER, allCellAtColSpcNumber);
		allCellAtColSpcNumber = null;
		
		colNum = sheetReader.getColumnNumberByName(ExcelTools.LOCALITY_SHEET, ExcelTools.COL_LOCALITY_DESCRIPTION);
		cell = null;
		Cell[] allCellAtColLocDesc = new Cell[localitySheet.getLastRowNum()];
		index=0;
		for (Row row : localitySheet) {
			cell =  row.getCell(colNum);
			if(cell != null) {
				allCellAtColLocDesc[index++] = cell;
			}
		}
		columnsAndNames.put(ExcelTools.COL_LOCALITY_DESCRIPTION, allCellAtColLocDesc);
		allCellAtColLocDesc = null;
		
		Set<String> headers = columnsAndNames.keySet();
		Iterator<String> it = headers.iterator();
		while(it.hasNext()) {
			String header = it.next();
			noSpaceFound &= checkExtraSpaceByColumn(columnsAndNames.get(header), header);
		}
		if(!noSpaceFound) {
			String warning = "Please inform Morphbank if you want to keep those spaces.";
			System.out.println(warning);
			this.messageToOutput("<b>" + warning + "</b>");
		}
		return noSpaceFound;
	}  
	
	private boolean checkExtraSpaceByColumn(Cell[] col, String colName) {
		String message = "Extra space(s) found in column ";
		boolean noSpaceFound = true;
		for (int i = 0; i < col.length; i++) {
			
			if(col[i] == null)
				continue;
			switch(col[i].getCellType()) 
			{
			case Cell.CELL_TYPE_STRING:
				String content = col[i].getStringCellValue();
				if (content.startsWith(" ") || content.endsWith(" ")) {
					noSpaceFound = false;
					String error = message + colName + " row " + (i + 1) + ".";
					System.out.println(error);
					this.messageToOutput(error);
				}
				
				break;
			}
		}
		return noSpaceFound;
	}
}
