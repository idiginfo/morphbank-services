package net.morphbank.mbsvc3.mapdwca;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.BasicConfigurator;

import org.gbif.dwc.record.DarwinCoreRecord;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.ConceptTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.text.*;

public class UsageExample {
	
	static final String INPUT_FILE = "c:/dev/dwca-ttrs";


	public static void main(String[] args) throws IOException,
			UnsupportedArchiveException {
		
		//BasicConfigurator.configure();
		//PropertyConfigurator.configure();
		
		// opens csv files with headers or dwc-a direcotries with a meta.xml
		// descriptor
		Archive arch = ArchiveFactory.openArchive(new File(INPUT_FILE));
		

		// does scientific name exist?
		if (!arch.getCore().hasTerm(DwcTerm.scientificName)) {
			System.out
					.println("This application requires dwc-a with scientific names");
			System.exit(1);
		}
		
		Set<ArchiveFile> extensions = arch.getExtensions();
		String t = null;
		
		
		for (ArchiveFile extension: extensions){
			ArchiveField id = extension.getId();
			
			Map<ConceptTerm, ArchiveField> fields = extension.getFields();
			t = extension.getRowType();
			System.out.println("extension has row type: "+t);
		}

		// loop over core darwin core records
		Iterator<DarwinCoreRecord> iter = arch.iteratorDwc();
		DarwinCoreRecord dwc;
		while (iter.hasNext()) {
			dwc = iter.next();
			System.out.println(dwc);
		}

		// loop over star records. i.e. core with all linked extension records
		for (StarRecord starRec : arch) {
			Record rec = starRec.core();
			// print core ID + scientific name
			System.out.println(rec.id() + " - "
					+ rec.value(DwcTerm.scientificName));
			List<Record> mediaRec = starRec
					.extension("http://rs.tdwg.org/ac/terms/multimedia");
			if (rec.value(DwcTerm.decimalLongitude) != null
					&& rec.value(DwcTerm.decimalLatitude) != null) {
				System.out.println("Georeferenced: "
						+ rec.value(DwcTerm.decimalLongitude) + ","
						+ rec.value(DwcTerm.decimalLatitude));
				;
			}

			for (Record erec : mediaRec) {
				// does this extension have Long/lat?
				if (erec.value(DwcTerm.decimalLongitude) != null
						&& erec.value(DwcTerm.decimalLatitude) != null) {
					System.out.println("Georeferenced: "
							+ rec.value(DwcTerm.decimalLongitude) + ","
							+ rec.value(DwcTerm.decimalLatitude));
					;
				}

			}
		}
	}
}