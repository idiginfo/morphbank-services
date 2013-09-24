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

import net.morphbank.mbsvc3.xml.*;

public class MapFsuHerbImage {
	// protected MapFishSpreadsheetToXml objMapper;
	static ObjectFactory objectFactory = new ObjectFactory();
	protected XmlBaseObject xmlImage;
	FieldMapper image;

	public MapFsuHerbImage(FieldMapper fieldMapper) {
		this.image = fieldMapper;
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
		xmlImage.setId(MapFsuHerbSpreadsheetToXml.getImageId(image));
		xmlImage.setSpecimen(MapFsuHerbSpreadsheetToXml.getSpecimenId(image));
		xmlImage.getView().add(MapFsuHerbSpreadsheetToXml.getViewId(image));

		String originalFileName = image.getValue("OriginalFileName");
		xmlImage.setOriginalFileName(originalFileName);
		xmlImage.addUserProperty("imageUrl", image.getValue("ImageURL"));
		xmlImage.setImageType(getImageType(originalFileName));
		xmlImage.setPhotographer(image.getValue("Photographer"));
		xmlImage.setCopyrightText(image.getValue("Copyright"));
		//TODO add user properties and ext links as necessary
	}

	Integer CreateWidth(String width) {
		try {
			if (width.length() == 0) {
				return null;
			} else if (width.length() > 3) {
				width = width.substring(0, 1)
						+ width.substring(2, (width.length() - 2));
			}
			return Integer.parseInt(width);
		} catch (Exception e) {
			return null;
		}
	}
}
