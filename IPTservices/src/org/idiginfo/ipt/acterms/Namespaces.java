package org.idiginfo.ipt.acterms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Namespaces {
	// Namespaces
	static final String DCTERMS_NS = "http://purl.org/dc/terms/";
	static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	static final String DWC_NS = "http://rs.tdwg.org/dwc/terms/";
	static final String ACTERMS_NS = "http://rs.tdwg.org/ac/terms/";
	static final String XMP_NS = "http://ns.adobe.com/xap/1.0/";
	static final String EXIFTERMS_NS = "http://ns.adobe.com/exif/1.0/";
	static final String MIX2TERMS_NS = "http://www.loc.gov/mix/v20/";
	static final String IPTC4XMLEXT_NS = "http://iptc.org/std/Iptc4xmpExt/2008-02-29/";
	static final String XMPRIGHTS_NS = "http://ns.adobe.com/xap/1.0/rights/";
	static final String PHOTOSHOP_NS = "http://ns.adobe.com/photoshop/1.0/";

	static final String AC_BASE = "http://species-id.net/wiki/Audubon_Core_Term_List#";

	static final Map<String, String> NS_MAP;
	static final Map<String, String> PREFIX_MAP;
	static {
		NS_MAP = new HashMap<String, String>();
		PREFIX_MAP = new HashMap<String, String>();
		NS_MAP.put("dcterms", DCTERMS_NS);
		NS_MAP.put("dwc", DWC_NS);
		NS_MAP.put("dc", DC_NS);
		NS_MAP.put("ac", ACTERMS_NS);
		//NS_MAP.put("acterms", ACTERMS_NS);// note: this namespace called by 2 names: ac and acterms
		NS_MAP.put("xmp", XMP_NS);
		NS_MAP.put("exifTerms", EXIFTERMS_NS);
		NS_MAP.put("mix2terms", MIX2TERMS_NS);
		NS_MAP.put("Iptc4xmpExt", IPTC4XMLEXT_NS);
		NS_MAP.put("xmpRights", XMPRIGHTS_NS);
		NS_MAP.put("photoshop", PHOTOSHOP_NS);
		for (Entry<String, String> e : NS_MAP.entrySet()) {
			PREFIX_MAP.put(e.getValue(), e.getKey());
		}
		PREFIX_MAP.put("http://rs.gbif.org/extension/",""); // default namespace
	}

}
