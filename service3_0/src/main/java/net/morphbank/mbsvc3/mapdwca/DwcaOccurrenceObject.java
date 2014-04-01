package net.morphbank.mbsvc3.mapdwca;

import java.util.Iterator;

import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.text.StarRecord;

import net.morphbank.mbsvc3.maptoxml.Field;
import net.morphbank.mbsvc3.maptoxml.Fields;
import net.morphbank.mbsvc3.maptoxml.SourceObject;

public class DwcaOccurrenceObject implements SourceObject {

	Record record;
	Iterator<ConceptTerm> terms;

	public DwcaOccurrenceObject(String type, Record record) {

		this.record = record;
		terms = record.terms().iterator();
	}

	@Override
	public boolean hasNext() {
		return terms.hasNext();
	}

	@Override
	public Field next() {
		// TODO create a field with name and value
		ConceptTerm term = terms.next();
		String qualifiedName = term.qualifiedName();
		String termName = term.simpleName();
		return new DwcaField(qualifiedName, termName, record.value(term));
		// return null;
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
