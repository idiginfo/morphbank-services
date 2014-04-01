package net.morphbank.mbsvc3.mapdwca;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.morphbank.mbsvc3.maptoxml.SourceIterator;
import net.morphbank.mbsvc3.maptoxml.SourceObject;

//import org.apache.poi.ss.usermodel.Cell;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.StarRecord;
import org.gbif.utils.file.ClosableIterator;

/**
 * Class that can open a Darwin Core Archive and iterate through the objects in it
 * 
 * @author griccardi
 *
 */
public class DwcaSourceIterator implements Iterator<SourceObject> {

	private Archive dwcaArchive;
	private ClosableIterator<StarRecord> sourceIterator;
	StarRecord currentRecord = null;
	Iterator<Record> extensionIterator;

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
				String fileName = zipDirName + "/" + zipentry.getName();
				System.out.println("Name of Extract fille : " + fileName);

				extractFile(zip.getInputStream(zipentry), new FileOutputStream(
						fileName));
			}
			zip.close();
		} catch (IOException ioe) {
			System.out.println("There is an IoException Occured :" + ioe);
			ioe.printStackTrace();
		}
	}

	// TODO make these parameters to allow for a core record that is an image,
	// extension is occurrence
	// also to allow for other extension names
	String coreType = "specimen";
	// boolean coreIsSpecimen = true;
	String extensionType = "image";
	String extensionName = "http://rs.tdwg.org/ac/terms/multimedia";

	public DwcaSourceIterator(String fileName) {
		try {
			dwcaArchive = ArchiveFactory.openArchive(new File(fileName));
			String coreRowType = dwcaArchive.getCore().getRowType();
			// is this the correct core type?
			if (!coreRowType.equals("http://rs.tdwg.org/dwc/terms/Occurrence")) {
				System.out.println("Core type is not occurrence: "
						+ coreRowType);
			}
			sourceIterator = dwcaArchive.iterator();
			// TODO detect the core type and the extension type.
			// use this info to set sourceobject type string and extension name
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	SourceObject createSourceObject(boolean isCore, Record record) {
		if (isCore)
			return new DwcaOccurrenceObject(coreType, record);
		return new DwcaExtensionObject(extensionType, record);
	}

	@Override
	public SourceObject next() {
		// if there's an available media extension object return it
		if (currentRecord != null && extensionIterator != null
				&& extensionIterator.hasNext()) {
			return createSourceObject(false, extensionIterator.next());
		}
		// get next star record
		currentRecord = sourceIterator.next();
		// set up iterator for extension records
		Map<String, List<Record>> extensions = currentRecord.extensions();
		List<Record> extensionRecords = extensions.get(extensionName);
		if (extensionRecords == null) {
			extensionIterator = null;
		} else {
			extensionIterator = extensionRecords.iterator();
		}
		return createSourceObject(true, currentRecord.core());
	}

	@Override
	public boolean hasNext() {
		if (currentRecord != null && extensionIterator != null
				&& extensionIterator.hasNext())
			return true;
		return sourceIterator.hasNext();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}


}
