/*******************************************************************************
 * Copyright (c) 2011 Greg Riccardi, Guillaume Jimenez.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  	Greg Riccardi - initial API and implementation
 * 	Guillaume Jimenez - initial API and implementation
 ******************************************************************************/
package net.morphbank.rdf;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.IdObject;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Provide static methods to support the RDF ontology model and resources
 * 
 * @author riccardi
 * 
 */
public class RdfOntologies {
	// utility objects for RDF transformation
	protected static OntModel darwinCoreModel = null;
	protected static OntModel morphbankModel = null;
	protected static RdfModel rdfModel = null;

	public static final Property mbankProperty(String local) {
		return ResourceFactory.createProperty(MorphbankConfig.MORPHBANK_SCHEMA_URI, local);
	}

	static {
		initializeOntModel();
	}

	public static final Property darwinProperty(String local) {
		return ResourceFactory.createProperty(MorphbankConfig.DARWIN_URI, local);
	}

	public static final Property dublinProperty(String local) {
		return ResourceFactory.createProperty(MorphbankConfig.DUBLIN_CORE_URI, local);
	}

	public static final Property foafProperty(String local) {
		return ResourceFactory.createProperty(MorphbankConfig.FOAF_URI, local);
	}

	public static void initializeOntModel() {
		rdfModel = new RdfModel();
		OntDocumentManager mgr = new OntDocumentManager("");
		mgr.setProcessImports(false);
		OntModelSpec s = new OntModelSpec(OntModelSpec.RDFS_MEM);
		// set up RDF model object
		darwinCoreModel = ModelFactory.createOntologyModel(s);
		morphbankModel = ModelFactory.createOntologyModel(s);
		InputStream morphbankStream = null;
		InputStream darwinCoreStream = null;
		try {
			darwinCoreStream = new URL(MorphbankConfig.DARWIN_CORE_SCHEMA_URL).openStream();
			darwinCoreModel.read(darwinCoreStream, MorphbankConfig.DARWIN_URI);
			darwinCoreStream.close();
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("can't find "
					+ MorphbankConfig.DARWIN_CORE_SCHEMA_URL);
			// e.printStackTrace();
		}
		// System.out.println("Darwin core model");
		// darwinCoreModel.write(System.out, "RDF/XML");
		try {
			URL remoteUrl = new URL(MorphbankConfig.MORPHBANK_SCHEMA_URL);
			URLConnection connection = remoteUrl.openConnection(MorphbankConfig.getProxy());
			morphbankStream = connection.getInputStream();
			morphbankModel.read(morphbankStream, MorphbankConfig.MORPHBANK_SCHEMA_URI);
			morphbankStream.close();
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("can't find "
					+ MorphbankConfig.MORPHBANK_SCHEMA_URL);
			// e.printStackTrace();
		}
	}

	public static Resource addProperty(Resource res, Property prop, String value) {
		if (value == null) {
			// res.addProperty(prop, "NULL");
		} else if (value.equals("")) {
		} else {
			res.addProperty(prop, value);
		}
		return res;
	}

	public static Resource addProperty(Resource res, Property prop, Double value) {
		if (value == null) {
			// res.addProperty(prop, "NULL");
		} else {
			res.addLiteral(prop, value.doubleValue());
		}
		return res;
	}

	public static Resource addProperty(Resource res, Property prop, Integer value) {
		if (value == null) {
			// res.addProperty(prop, "NULL");
		} else {
			res.addLiteral(prop, value.intValue());
		}
		return res;
	}

	public static Resource addProperty(Resource res, Property prop, Date value) {
		if (value == null) {
			// res.addProperty(prop, "NULL");
		} else {
			res.addLiteral(prop, value);
		}
		return res;
	}

	public static void addReference(Resource res, RdfBase refBase, IdObject ref, String propName) {
		if (ref != null) {
			Resource resRef = ResourceFactory.createResource(MorphbankConfig.makeURI(ref.getId()));
			res.addProperty(RdfOntologies.mbankProperty(propName), resRef);
		}
	}

	public static void addProperty(Resource res, Property prop, Collection set) {
		if (set != null) {
			java.util.Iterator iter = set.iterator();
			while (iter.hasNext()) {
				IdObject ref = (IdObject) iter.next();
				String value = Integer.toString(ref.getId());
				addProperty(res, prop, value);
			}
		}
	}

	public static void addReference(Resource res, RdfBase refBase, Collection set, String propName) {
		if (set != null) {
			java.util.Iterator iter = set.iterator();
			while (iter.hasNext()) {
				IdObject ref = (IdObject) iter.next();
				Resource resRef = ResourceFactory.createResource(MorphbankConfig.makeURI(ref
						.getId()));
				res.addProperty(RdfOntologies.mbankProperty(propName), resRef);
			}
		}
	}

	public static void addReference(Resource res, RdfBase refBase, Map map, String propName) {
		Collection set = map.entrySet();
		if (set != null) {
			java.util.Iterator iter = set.iterator();
			while (iter.hasNext()) {
				IdObject ref = (IdObject) iter.next();
				Resource resRef = ResourceFactory.createResource(MorphbankConfig.makeURI(ref
						.getId()));
				res.addProperty(RdfOntologies.mbankProperty(propName), resRef);
			}
		}
	}

	public static OntModel getDarwinCoreModel() {
		return darwinCoreModel;
	}

	public static OntModel getMorphbankModel() {
		return morphbankModel;
	}

	public static OntModel getOntModel() {
		return rdfModel.getModel();
	}

	public static RdfModel getRdfModel() {
		return rdfModel;
	}

	public static void write(PrintWriter out, String contentType) {
		getOntModel().write(out, contentType);
	}
}
