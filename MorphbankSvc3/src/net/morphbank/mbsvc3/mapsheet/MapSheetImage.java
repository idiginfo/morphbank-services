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

import jxl.Cell;
import jxl.Sheet;
import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.ObjectFactory;
import net.morphbank.mbsvc3.xml.XmlBaseObject;

public class MapSheetImage {
	// protected MapFishSpreadsheetToXml objMapper;
	static ObjectFactory objectFactory = new ObjectFactory();
	protected XmlBaseObject xmlImage;
	FieldMapper image;
	private String[][] userProperties = null;

	public MapSheetImage(FieldMapper fieldMapper) {
		this.image = fieldMapper;
		this.userProperties = this.getUserProperties();
	}

	String getImageType(String fileName) {
		int dot = fileName.lastIndexOf('.');
		String suffix = fileName.substring(dot + 1);
		if (suffix.length() < 1 || suffix.length() > 4) {
			return "jpg";
		}
		return suffix;
	}

	public void setXmlImageFields(XmlBaseObject xmlImage) {
		// BaseObject fields
		xmlImage.setId(MapSpreadsheetToXml.getImageId(image));
		xmlImage.setSpecimen(MapSpreadsheetToXml.getSpecimenId(image));
		xmlImage.getView().add(MapSpreadsheetToXml.getViewId(image));

		xmlImage.addDescription(image.getValue("Image Description"));
		xmlImage.addUserProperty("ImageDescription", image.getValue("Image Description"));

		String originalFileName = image.getValue("Original File Name");
		xmlImage.setOriginalFileName(originalFileName);
		xmlImage.addUserProperty("imageUrl", image.getValue("imageURL"));
		xmlImage.setImageType(getImageType(originalFileName));
		xmlImage.setCreativeCommons(image.getValue("Creative Commons"));
		xmlImage.setPhotographer(image.getValue("Photographer"));
		xmlImage.setCopyrightText(image.getValue("Copyright"));
		String test = image.getValue("For Eol");
		if (image.getValue("For Eol").equalsIgnoreCase("yes")) {
			xmlImage.setEol(image.getValue("For Eol")); 
		}
		              
		// TODO add user properties and ext links as necessary
		Extref extref = MapSpreadsheetToXml.getExternalLink(image, "Image");
		if (extref != null) xmlImage.addExternalRef(extref);
		if (userProperties != null && userProperties.length >= 1){
			this.addUserProperty(xmlImage);
		}
	}

	//add user properties to xml document in the correct object (image)
	//and generates uri if present
	private void addUserProperty(XmlBaseObject xmlImage) {
		for(int i = 1; i < userProperties.length; i++) {
			String property[] = userProperties[i];
			if(property[1].equalsIgnoreCase("image")){
				if (property[3] == null || property[3].equalsIgnoreCase("")){ //namespace uri empty
					if (property[2] == null || property[2].equalsIgnoreCase("")){ //property name empty
						xmlImage.addUserProperty(property[0], image.getValue(property[0]));
					}
					else { //property name present
						xmlImage.addUserProperty(property[2], image.getValue(property[0]));
					}
				}
				else { //namespace uri present
					if (property[2] == null || property[2].equalsIgnoreCase("")){ //property name empty
						xmlImage.addUserProperty(property[0], image.getValue(property[0]), property[3]);
					}
					else { //property name present
						xmlImage.addUserProperty(property[2], image.getValue(property[0]), property[3]);
					}
				}
			}
		}
	}

	Integer CreateWidth(String width) {
		try {
			if (width.length() == 0) {
				return null;
			} else if (width.length() > 3) {
				width = width.substring(0, 1) + width.substring(2, (width.length() - 2));
			}
			return Integer.parseInt(width);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String[][] getUserProperties() {
		if (this.image instanceof XlsFieldMapper)
		{
			Sheet links = ((XlsFieldMapper) this.image).getLinks();
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
