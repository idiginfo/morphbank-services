package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.SwingWorker;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class Split extends SwingWorker<Object, Object>
{
	Workbook workbook;
	String originalFile;
	int limit = 100;

	public Split(String originalFile, int limit) {
		this.originalFile = originalFile;
		this.limit = limit;
		try {
			init();
		}catch (InvalidFormatException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
 
	}

	private void init() throws IOException, InvalidFormatException
	{
		this.workbook = WorkbookFactory.create(new File(this.originalFile));
	}

	private void createNewFile(String newFileName, int current)
			throws InvalidFormatException, IOException {

		Workbook newWorkbook = WorkbookFactory.create(new File(
				this.originalFile));
		Sheet sheet0 = newWorkbook.getSheetAt(0);
		removeRows(sheet0, current);
		FileOutputStream newFile = new FileOutputStream(newFileName);
		newWorkbook.write(newFile);
	}


	private void removeRows(Sheet sheet, int current){ 
		int rows = sheet.getLastRowNum();
		for (int i = 1; i <= current * this.limit; i++) {
			sheet.removeRow(sheet.getRow(1));
		}
		for (int i = 1; i <= rows - this.limit; i++)
			sheet.removeRow(sheet.getRow(this.limit + 1));
	}

	public ArrayList<String> createMultiplefiles() 
	{
		ArrayList<String> fileList = new ArrayList<String>();
		int files = this.countRows() / this.limit + 1;
		for (int i = 0; i < files; i++) {
			Date before = new Date();
			String newFile = this.originalFile.substring(0, this.originalFile.length() - 4) + String.valueOf(i) + ".xls";
			System.out.println("Creating file :" + newFile);
			fileList.add(newFile);
			try {
				createNewFile(newFile, i);
			} catch (InvalidFormatException | IOException e) {
				e.printStackTrace();
			}
			Date now = new Date();
			System.out.println("Duration: " + (now.getTime() - before.getTime()) + "ms");
		}
		return fileList;
	}
	
	private boolean isCellEmpty(Cell cell) {
		if (cell == null || cell.getStringCellValue().equals(""))
			return true;
		return false;
	}
	
	/**
	 * Convenient method to get all columns of a particular 
	 * 	column number from a specified sheet 
	 * 
	 * @return array of cell
	 */
	private Cell[] getColumns(Sheet sheet, int columnNum){
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
	
	private int countRows() {
		Cell[] cells = getColumns(this.workbook.getSheetAt(0), 3);
		int size = 0;
		for (int i = 0; i < cells.length; i++) {
			if (!isCellEmpty(cells[i])) {
				size++;
			}
		}
		return size;
	}

	protected Object doInBackground() throws Exception
	{
		ArrayList<String> fileList = new ArrayList<String>();
		System.out.println("Spliting file: " + this.originalFile);
		createMultiplefiles();
		System.out.println("done");
		return null;
	}
	
}
