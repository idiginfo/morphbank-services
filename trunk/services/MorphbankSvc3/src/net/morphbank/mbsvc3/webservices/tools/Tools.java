package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.util.Date;

import jxl.Cell;
import jxl.CellType;

import net.morphbank.MorphbankConfig;

import org.apache.commons.codec.digest.DigestUtils;

public class Tools {

	private static final String[] FILE_EXTENSIONS = {".tif", ".tiff",".jpg",".jpeg",".bmp",".gif",".png"};
	
	public static String createFolder(String fileName) {
		Date date = new Date();
		byte[] bytes = DigestUtils.sha(fileName + date.getTime());
		String folder = DigestUtils.sha256Hex(bytes);
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

	public static boolean fileExtensionOk(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot == -1) return false;
		String extension = fileName.substring(dot);
		for (int i = 0; i < FILE_EXTENSIONS.length; i++) {
			if (extension.equalsIgnoreCase(FILE_EXTENSIONS[i])) return true;
		}
		return false;
	}
	
	public static boolean checkCellType(Cell cell, CellType type) {
		if (cell.getType() != type) return false;
		return true;
	}
	
	public static boolean isEmpty(Cell cell) {
		return cell.getContents().equalsIgnoreCase("");
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
