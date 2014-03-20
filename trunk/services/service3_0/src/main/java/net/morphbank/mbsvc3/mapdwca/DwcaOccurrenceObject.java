package net.morphbank.mbsvc3.mapdwca;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.text.StarRecord;

import net.morphbank.mbsvc3.maptoxml.Field;
import net.morphbank.mbsvc3.maptoxml.Fields;
import net.morphbank.mbsvc3.maptoxml.SourceObject;

public class DwcaOccurrenceObject implements SourceObject {

	Record record;

	public DwcaOccurrenceObject(String type, Record record2) {

		this.record = record2;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Field next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSourceType() {
		// TODO Auto-generated method stub
		return null;
	}

}
