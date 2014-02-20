package net.morphbank.mbsvc3.mapdwca;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.morphbank.mbsvc3.mapsheet.FieldMapper;

//import org.apache.poi.ss.usermodel.Cell;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.StarRecord;

public class DwcaFieldMapper implements FieldMapper {

	private Archive dwcaArchive;
	private ArrayList<Record> records = new ArrayList<Record>();
	int lastLine;
	int currentLine;

	public static void extractFile(InputStream inStream, OutputStream outStream)
			throws IOException {
		byte[] buf = new byte[1024];
		int l;
		while ((l = inStream.read(buf)) >= 0) {
			outStream.write(buf, 0, l);
		}
		inStream.close();
		outStream.close();
	}

	public static void extractZippedFiles(String zipFileName, String zipDirName) {
		new File(zipDirName).mkdir();
		try {
			ZipFile zip = new ZipFile(zipFileName);
			Enumeration<? extends ZipEntry> enumEntries = zip.entries();
			while (enumEntries.hasMoreElements()) {
				ZipEntry zipentry = (ZipEntry) enumEntries.nextElement();
				if (zipentry.isDirectory()) {
					System.out.println("Name of Extract directory : "
							+ zipentry.getName());
					(new File(zipentry.getName())).mkdir();
					continue;
				}
				String fileName = zipDirName+"/"+zipentry.getName();
				System.out.println("Name of Extract fille : "
						+ fileName);

				extractFile(zip.getInputStream(zipentry), new FileOutputStream(
						fileName));
			}
			zip.close();
		} catch (IOException ioe) {
			System.out.println("There is an IoException Occured :" + ioe);
			ioe.printStackTrace();
		}
	}

	public DwcaFieldMapper(String fileName) {
		try {
			dwcaArchive = ArchiveFactory.openArchive(new File(fileName));
			//lastLine = dwcaArchive.
			for (StarRecord record : dwcaArchive) {
				records.add(record.core());
			}
			lastLine = records.size();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}


	@Override
	public String getValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Date getValueDate(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasNext() {
		return currentLine < lastLine;
	}

	@Override
	public void getNextLine() {
		if (hasNext()) {
			currentLine++;
		}
	}

	@Override
	public String[] getHeaders() {
		return null;
	}

	@Override
	public int moveToLine(int lineNumber) {
		return currentLine = lineNumber;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentLineNumber() {
		return currentLine;
	}

	@Override
	public String getValueFormula(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getUserId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSubmitterName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSubmitterId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getGroupName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int groupId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getValue(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

}
