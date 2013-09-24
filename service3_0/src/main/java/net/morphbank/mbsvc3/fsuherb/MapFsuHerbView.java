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
package net.morphbank.mbsvc3.fsuherb;

import net.morphbank.mbsvc3.xml.XmlBaseObject;

public class MapFsuHerbView {

	protected XmlBaseObject xmlView;
	FieldMapper view;
	public static final int FISH_TSN = 161030;
	public static final String EXT_LABEL_BASE = "Teleost Anatomy Ontology ";
	public static final String EXT_URL_BASE = "http://bioportal.bioontology.org/virtual/1110/";
	public static final String EXT_URL_TYPE = "Ontology"; // database id for

	// "ontology"

	// public XmlBaseObject createXmlViewId(FieldMapper view, String localId) {
	// // BaseObject fields
	// XmlBaseObject xmlView = new XmlView();
	// // objMapper.setId(xmlView, specimen, localId);
	// return xmlView;
	// }
	public MapFsuHerbView(FieldMapper fieldMapper) {
		this.view = fieldMapper;
	}

	public String createViewDescription() {

		return MapFsuHerbSpreadsheetToXml.getViewIdStr(view);
	}

	// Methods to map View to XmlView
	public void setXmlViewFields(XmlBaseObject xmlView) {
		// TODO add rest of view fields
		xmlView.setSourceId(MapFsuHerbSpreadsheetToXml.getViewId(view));
		xmlView.setName(MapFsuHerbSpreadsheetToXml.getViewIdStr(view));
		xmlView.setViewAngle(view.getValue("View Angle"));
		xmlView.setImagingTechnique(view.getValue("Imaging Technique"));
		xmlView.setImagingPreparationTechnique(view
				.getValue("Image Preparation Technique"));
		xmlView.setSex(view.getValue("Sex"));
		xmlView.setForm(view.getValue("Form"));
		xmlView.setDevelopmentalStage(view.getValue("Developmental Stage"));
		xmlView.setSpecimenPart(view.getValue("Specimen Part"));
		xmlView.setViewRestrictedTo(MapFsuHerbSpreadsheetToXml
				.getTaxonId(view.getValue("View Applicable to Taxon")));
		xmlView.addDescription(createViewDescription());

	}
}
