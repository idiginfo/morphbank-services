package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.util.Date;

import net.morphbank.MorphbankConfig;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class Tools {

	private static final String[] FILE_EXTENSIONS = {".tif", ".tiff",".jpg",".jpeg",".bmp",".gif",".png"};
	
	public static String createFolder(String fileName) {
		Date date = new Date();
		byte[] bytes = DigestUtils.sha(fileName + date.getTime());
		String folder = DigestUtils.shaHex(bytes);
		boolean success = new File(MorphbankConfig.getFilepath() + folder).mkdir();
		if (!success) {
			System.err.println("Error creating a folder.");
		}
		return folder;
	}

	public static void eraseTempFile(String folderPath, String fileName, boolean eraseFolder) {
		File file = new File(folderPath + fileName);
		if (file.exists()) { 
			file.delete();
		}
		if (eraseFolder) {
			File folder = new File(folderPath);
			if (folder.exists()){
				folder.delete();
			}
		}
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
	
	public static boolean checkCellType(Cell cell,  int CellType) {
		if (cell.getCellType() != CellType) return false;
		return true;
	}
	
	/**
	 * Inforce yyyy-mm-dd format to a cell
	 * Split the date and check each part individually
	 * @param cell
	 * @return true if the format is correct
	 */
	public static boolean checkDateFormat(Cell cell) {
		String content = cell.getStringCellValue() ;
		boolean result = true;
		String[] parts = content.split("-");
		if (parts.length != 3) 
			return false;
		else {
				try {
					int year = Integer.parseInt(parts[0]);
					int month = Integer.parseInt(parts[1]);
					int day = Integer.parseInt(parts[2]);
					result &= (year > 999 && year < 2150);
					result &= (month >= 00 && month <= 12);
					result &= (day >= 00 && day <= 31);
				} catch (Exception e){
					return false;
			}
		}
		return result;
	}
	
	public static boolean isEmpty(Cell cell) {
		if( (cell == null) || (cell.getCellType()==Cell.CELL_TYPE_BLANK)) return true;
		if(cell.getCellType()==Cell.CELL_TYPE_STRING){
		return cell.getStringCellValue().equalsIgnoreCase("");
		}
		else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			if (DateUtil.isCellDateFormatted(cell)) {//returns null for blank cells
				return (cell.getDateCellValue()==null)?true:false;
			}
			else{
				return (cell.getNumericCellValue()==0)?true:false;
			}
		}
		return false;
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
}
