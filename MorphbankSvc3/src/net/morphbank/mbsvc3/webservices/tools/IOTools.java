package net.morphbank.mbsvc3.webservices.tools;

import java.io.File;
import java.util.Date;

import net.morphbank.MorphbankConfig;

import org.apache.commons.codec.digest.DigestUtils;

public class IOTools {

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

}
