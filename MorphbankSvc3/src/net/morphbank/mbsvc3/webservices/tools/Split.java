package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.SwingWorker;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

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
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() throws BiffException, IOException
	{
		this.workbook = Workbook.getWorkbook(new File(this.originalFile));
	}

	private void createNewFile(String newFileName, int current) throws IOException, WriteException, BiffException {
		File newFile = new File(newFileName);
		WritableWorkbook newWorkbook = Workbook.createWorkbook(newFile, Workbook.getWorkbook(new File(this.originalFile)));
		WritableSheet sheet0 = newWorkbook.getSheet(0);
		removeRows(sheet0, current);
		newWorkbook.write();
		newWorkbook.close();
	}

	private void removeRows(WritableSheet sheet, int current) throws RowsExceededException, WriteException {
		int rows = sheet.getRows();
		for (int i = 1; i <= current * this.limit; i++) {
			sheet.removeRow(1);
		}
		for (int i = 1; i <= rows - this.limit; i++)
			sheet.removeRow(this.limit + 1);
	}

	public ArrayList<String> createMultiplefiles() throws WriteException, IOException, BiffException
	{
		ArrayList<String> fileList = new ArrayList<String>();
		int files = this.countRows() / this.limit + 1;
		for (int i = 0; i < files; i++) {
			Date before = new Date();
			String newFile = this.originalFile.substring(0, this.originalFile.length() - 4) + String.valueOf(i) + ".xls";
			System.out.println("Creating file :" + newFile);
			fileList.add(newFile);
			createNewFile(newFile, i);
			Date now = new Date();
			System.out.println("Duration: " + (now.getTime() - before.getTime()) + "ms");
		}
		return fileList;
	}
	
	private boolean isCellEmpty(Cell cell) {
		if (cell.getContents() == null || cell.getContents().equals(""))
			return true;
		return false;
	}
	
	private int countRows() {
		Cell[] cells = this.workbook.getSheet(0).getColumn(3);
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
