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
package net.morphbank.mbsvc3.mapsheet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.XmlBaseObject;

public class MapSheetView {

	protected XmlBaseObject xmlView;
	FieldMapper view;
	private String[][] userProperties = null;
	Fields fields;

	public MapSheetView(FieldMapper fieldMapper, Fields fields) {
		this.view = fieldMapper;
		this.userProperties = this.getUserProperties();
		this.fields = fields;
	}

	public String createViewDescription() {

		return MapSpreadsheetToXml.getViewIdStr(view);
	}

	// Methods to map View to XmlView
	public void setXmlViewFields(XmlBaseObject xmlView) {
		// TODO add rest of view fields
		xmlView.setSourceId(MapSpreadsheetToXml.getViewId(view));
		xmlView.setDescription(view.getValue(fields.getV_VIEW_DESCRIPTION()));
		xmlView.addUserProperty("ViewDescription",
				view.getValue(fields.getV_VIEW_DESCRIPTION()));

		xmlView.setName(MapSpreadsheetToXml.getViewIdStr(view));
		xmlView.setViewAngle(view.getValue(fields.getV_VIEW_ANGLE()));
		xmlView.setImagingTechnique(view.getValue(fields.getV_IMAGING_TECHNIQUE()));
		xmlView.setImagingPreparationTechnique(view
				.getValue(fields.getV_IMAGING_PREPARATION_TECHNIQUE()));
		xmlView.setSex(view.getValue(fields.getV_VIEW_SEX()));
		xmlView.setForm(view.getValue(fields.getV_VIEW_FORM()));
		xmlView.setDevelopmentalStage(view.getValue(fields.getV_VIEW_DEVELOPMENTAL_STAGE()));
		xmlView.setSpecimenPart(view.getValue(fields.getV_SPECIMEN_PART()));
		xmlView.setViewRestrictedTo(MapSpreadsheetToXml.getTaxonId(view
				.getValue(fields.getV_VIEW_APPLICABLE_TO_TAXON())));
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
			int cols = links.getRow(0).getLastCellNum();
			int rows = links.getLastRowNum();
			userProperties = new String[rows+1][cols+1];
			int i = 0;
			int j  = 0;
			  for (Row row : links) {
				  j = 0;
			      for (Cell cell : row) {
			    	  userProperties[i][j] = cell.getStringCellValue();
			    	  j++;
			      }
				  i++;
			  }
			return userProperties;
		}
		return null;
	}
}
