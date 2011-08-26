package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.util.Date;

import jxl.Cell;
import jxl.CellType;

import net.morphbank.MorphbankConfig;

import org.apache.commons.codec.digest.DigestUtils;

public class Tools {

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
		int dot = fileName.lastIndexOf(".");
		if (dot == -1) return false;
		String extension = fileName.substring(dot);
		if (extension.equalsIgnoreCase(".tif")) return true;
		if (extension.equalsIgnoreCase(".tiff")) return true;
		if (extension.equalsIgnoreCase(".jpg")) return true;
		if (extension.equalsIgnoreCase(".jpeg")) return true;
		if (extension.equalsIgnoreCase(".bmp")) return true;
		if (extension.equalsIgnoreCase(".gif")) return true;
		if (extension.equalsIgnoreCase(".png")) return true;
		return false;
	}
	
	public static boolean checkCellType(Cell cell, CellType type) {
		if (cell.getType() != type) return false;
		return true;
	}
	
	public static boolean isEmpty(Cell cell) {
		return cell.getContents().equalsIgnoreCase("");
	}
	
	
}
