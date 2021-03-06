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
package net.morphbank.mbsvc3.mapdwca;

import jxl.Cell;
import jxl.Sheet;
import net.morphbank.mbsvc3.mapsheet.FieldMapper;
import net.morphbank.mbsvc3.mapsheet.MapSpreadsheetToXml;
import net.morphbank.mbsvc3.mapsheet.XlsFieldMapper;
import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.XmlBaseObject;

public class MapDwcaView {

	protected XmlBaseObject xmlView;
	FieldMapper view;
	private String[][] userProperties = null;

	public MapDwcaView(FieldMapper fieldMapper) {
		this.view = fieldMapper;
		this.userProperties = this.getUserProperties();
	}

	public String createViewDescription() {

		return MapSpreadsheetToXml.getViewIdStr(view);
	}

	// Methods to map View to XmlView
	public void setXmlViewFields(XmlBaseObject xmlView) {
		// TODO add rest of view fields
		xmlView.setSourceId(MapSpreadsheetToXml.getViewId(view));
		xmlView.setDescription(view.getValue("View Description"));
		xmlView.addUserProperty("ViewDescription",
				view.getValue("View Description"));

		xmlView.setName(MapSpreadsheetToXml.getViewIdStr(view));
		xmlView.setViewAngle(view.getValue("View Angle"));
		xmlView.setImagingTechnique(view.getValue("Imaging Technique"));
		xmlView.setImagingPreparationTechnique(view
				.getValue("Imaging Preparation Technique"));
		xmlView.setSex(view.getValue("View Sex"));
		xmlView.setForm(view.getValue("View Form"));
		xmlView.setDevelopmentalStage(view.getValue("View Developmental Stage"));
		xmlView.setSpecimenPart(view.getValue("Specimen Part"));
		xmlView.setViewRestrictedTo(MapSpreadsheetToXml.getTaxonId(view
				.getValue("View Applicable to Taxon")));
		xmlView.addDescription(createViewDescription());
		Extref extref = MapSpreadsheetToXml.getExternalLink(view, "View");
		if (extref != null) xmlView.addExternalRef(extref);
		if (userProperties != null && userProperties.length >= 1){
			this.addUserProperty(xmlView);
		}

	}

	//add user properties to xml document in the correct object (view)
	//and generate uri if present
	private void addUserProperty(XmlBaseObject xmlView) {
		for(int i = 1; i < userProperties.length; i++) {
			String property[] = userProperties[i];
			if(property[1].equalsIgnoreCase("view")){
				if (property[3] == null || property[3].equalsIgnoreCase("")){ //namespace uri empty
					if (property[2] == null || property[2].equalsIgnoreCase("")){ //property name empty
						xmlView.addUserProperty(property[0], view.getValue(property[0]));
					}
					else { //property name present
						xmlView.addUserProperty(property[2], view.getValue(property[0]));
					}
				}
				else { //namespace uri present
					if (property[2] == null || property[2].equalsIgnoreCase("")){ //property name empty
						xmlView.addUserProperty(property[0], view.getValue(property[0]), property[3]);
					}
					else { //property name present
						xmlView.addUserProperty(property[2], view.getValue(property[0]), property[3]);
					}
				}
			}
		}
	}
	
	public String[][] getUserProperties() {
		if (this.view instanceof XlsFieldMapper)
		{
			Sheet links = ((XlsFieldMapper) this.view).getLinks();
			int c = links.getColumns();
			int r = links.getRows();
			userProperties = new String[r][c];
			for (int i = 0; i < links.getColumns(); i++){
				Cell[] cells = links.getColumn(i);
				for (int j = 0; j < cells.length; j++){
					userProperties[j][i] = cells[j].getContents();
				}
			}
			return userProperties;
		}
		return null;
	}
}
