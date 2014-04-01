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
package net.morphbank.mbsvc3.maptoxml;

import net.morphbank.mbsvc3.xml.Extref;
import net.morphbank.mbsvc3.xml.ObjectFactory;
import net.morphbank.mbsvc3.xml.XmlBaseObject;

public class MapImage {
	// protected MapFishSpreadsheetToXml objMapper;
	static ObjectFactory objectFactory = new ObjectFactory();
	protected XmlBaseObject xmlImage;
	SourceIterator image;
	private String[][] userProperties = null;
	Fields fields;

	public MapImage(SourceIterator fieldMapper, Fields fields) {
		this.image = fieldMapper;
		this.userProperties = this.getUserProperties();
		this.fields = fields;
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
		xmlImage.setId(MapSourceToXml.getImageId(image));
		xmlImage.setSpecimen(MapSourceToXml.getSpecimenId(image));
		xmlImage.getView().add(MapSourceToXml.getViewId(image));

		xmlImage.addDescription(image.getValue(fields.getI_IMAGE_DESCRIPTION()));
		xmlImage.addUserProperty("ImageDescription",
				image.getValue(fields.getI_IMAGE_DESCRIPTION()));

		String originalFileName = image.getValue(fields
				.getI_ORIGINAL_FILE_NAME());
		xmlImage.setOriginalFileName(originalFileName);
		xmlImage.addUserProperty("imageUrl",
				image.getValue(fields.getI_IMAGEURL()));
		xmlImage.setImageType(getImageType(originalFileName));
		xmlImage.setCreativeCommons(image.getValue(fields
				.getI_CREATIVE_COMMONS()));
		xmlImage.setPhotographer(image.getValue(fields.getI_PHOTOGRAPHER()));
		xmlImage.setCopyrightText(image.getValue(fields.getI_COPYRIGHT()));
		if (image.getValue(fields.getI_ENCYCLOPEDIA_OF_LIFE())
				.equalsIgnoreCase("yes")) {
			xmlImage.setEol(image.getValue(fields.getI_ENCYCLOPEDIA_OF_LIFE()));
		}

		// TODO add user properties and ext links as necessary
		Extref extref = MapSourceToXml.getExternalLink(image, "Image");
		if (extref != null)
			xmlImage.addExternalRef(extref);
		if (userProperties != null && userProperties.length >= 1) {
			this.addUserProperty(xmlImage);
		}
	}

	// add user properties to xml document in the correct object (image)
	// and generates uri if present
	private void addUserProperty(XmlBaseObject xmlImage) {
		for (int i = 1; i < userProperties.length; i++) {
			String property[] = userProperties[i];
			if (property[1].equalsIgnoreCase("image")) {
				if (property[3] == null || property[3].equalsIgnoreCase("")) { // namespace
																				// uri
																				// empty
					if (property[2] == null || property[2].equalsIgnoreCase("")) { // property
																					// name
																					// empty
						xmlImage.addUserProperty(property[0],
								image.getValue(property[0]));
					} else { // property name present
						xmlImage.addUserProperty(property[2],
								image.getValue(property[0]));
					}
				} else { // namespace uri present
					if (property[2] == null || property[2].equalsIgnoreCase("")) { // property
																					// name
																					// empty
						xmlImage.addUserProperty(property[0],
								image.getValue(property[0]), property[3]);
					} else { // property name present
						xmlImage.addUserProperty(property[2],
								image.getValue(property[0]), property[3]);
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
				width = width.substring(0, 1)
						+ width.substring(2, (width.length() - 2));
			}
			return Integer.parseInt(width);
		} catch (Exception e) {
			return null;
		}
	}

	public String[][] getUserProperties() {
		// TODO add body
		return null;
	}
}
