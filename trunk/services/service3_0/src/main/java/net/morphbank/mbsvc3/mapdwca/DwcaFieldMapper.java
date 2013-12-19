package net.morphbank.mbsvc3.mapdwca;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.morphbank.mbsvc3.mapsheet.FieldMapper;

import org.apache.poi.ss.usermodel.Cell;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.text.Archive;
import org.gbif.dwc.text.ArchiveFactory;
import org.gbif.dwc.text.StarRecord;
import org.gbif.dwc.text.UnsupportedArchiveException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class DwcaFieldMapper implements FieldMapper {
	
	private Archive dwcaArchive;
	private ArrayList<StarRecord> records = new ArrayList<StarRecord>();
	protected String[] headers = null;
	int lastLine;
	int currentLine;

	public DwcaFieldMapper(String fileName) {
		try {
			dwcaArchive = ArchiveFactory.openArchive(new File(fileName));
			readHeaders();
			for (StarRecord record: dwcaArchive) {
				records.add(record);
			}
			lastLine = records.size();
		} catch (UnsupportedArchiveException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readHeaders() {
		this.readMetadata(dwcaArchive);
		currentLine = 0;
	}
	
	/**
	 * Parse meta.xml and fill out headers
	 * @param arch
	 */
	private void readMetadata(Archive arch) {
		try {
			String metaLocation = arch.getLocation() + "/meta.xml";
			MetaXmlHandler handler = new MetaXmlHandler();
			try {
				XMLReader xr = XMLReaderFactory.createXMLReader();
				xr.setContentHandler(handler);
				xr.setErrorHandler(handler);
			    xr.parse(new InputSource(new FileReader(metaLocation)));		    
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			headers = handler.getHeader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String fieldName) {
		String value = records.get(currentLine).core().value(fieldName);
		if (value != null) return value;
		return getValueFromExtension(records.get(currentLine).extensions(), fieldName);
	}

	public String getValueFromExtension(Map<String, List<Record>> extensions, String fieldName) {
		Set<String> keys = extensions.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String extensionName = it.next();
			List<Record> extRecords = extensions.get(extensionName);
			for (Record record : extRecords) {
				String value = record.value(fieldName);
				if (value != null) return value;
			}
		}
		return null;
	}
	
	public String getValueFromExtension(String fieldName, String extensionName) {
		Map<String, List<Record>> extensions = records.get(currentLine).extensions();
		List<Record> extRecords = extensions.get(extensionName);
		for (Record record : extRecords) {
			String value = record.value(fieldName);
			if (value != null) return value;
		}
		return null;
	}

	@Override
	public Cell getValueDate(String fieldName) {
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
		return headers;
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
	

}
