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
package net.morphbank.mbsvc3.ocr;

import java.io.File;
import net.morphbank.*;
import net.morphbank.object.Image;
import net.morphbank.object.Specimen;

/**
 * Class to integrate OCR analysis from Herbis into the Morphbank image store
 * Methods take care of finding the appropriate image, selecting a location for
 * the temporary image files finding and using previous OCR analysis, if
 * available storing the OCR analysis results in the Specimen table
 * 
 * Use classes ContrastImage and Ocr for performing the Herbis OCR
 * 
 * See classes ContrastImage, Ocr and OcrClient for details of how to use the
 * Herbis OCR services
 * 
 * @author riccardi
 * 
 */
public class MorphbankOcr {

	final static String PATH = "/tmp/"; // directory to hold temporary images

	static String getContrastName(String imageId) {
		return imageId + "c.jpg";
	}

	static String getTiffUrl(String imageId) {
		return MorphbankConfig.getImageURL(imageId, "tiff");
	}

	/**
	 * Get an image tiff file from the database, create a contrast image file
	 * use getOcr to perform OCR analysis
	 * 
	 * @param imageId,
	 *            Morphbank id of an image to be analyzed
	 * @return Ocr object with bar code and text
	 */
	public Ocr getNewOcrFromImageId(String imageId) {
		ContrastImage contrastImage = new ContrastImage();
		String contrastName = getContrastName(imageId);
		String contrastPath = PATH + contrastName;
		String registrationId = imageId;
		Ocr ocr = null;
		// look for existing file before creating new one!
		File contrast = new File(contrastPath);
		if (!contrast.canRead()) { // contrast file is not available
			try {
				contrastImage.makeJpgFromUrl(getTiffUrl(imageId), contrastPath);
			} catch (Exception e) {
				MorphbankConfig.SYSTEM_LOGGER.info(e.toString());
				MorphbankConfig.SYSTEM_LOGGER.info("Contrast file creation failed: " + imageId);
				return null;
			}
		} else {
			MorphbankConfig.SYSTEM_LOGGER.info("contrast file exists");
		}
		// use the Ocr class to submit image for analysis
		ocr = Ocr.getOcr(registrationId, contrastPath);
		return ocr;
	}

	/**
	 * Create OCR from int image id: make String from int and call above method
	 * 
	 * @param objectId
	 * @return Ocr object with bar code and text
	 */
	public Ocr getNewOcrFromImageId(int objectId) {
		try {
			String imageId = Integer.toString(objectId);
			return getNewOcrFromImageId(imageId);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Create OCR from String object id: parse object id and call getOcrFromId
	 * with resulting integer
	 * 
	 * @param id
	 * @return
	 */
	public Ocr getOcrFromId(String id) {
		int objectId;
		try {
			objectId = Integer.parseInt(id);
		} catch (Exception e) {
			return null;
		}
		return getOcrFromId(objectId);
	}

	/**
	 * Create OCR from int object id: must first find image for the object,
	 * which may be a specimen Look in database for existing OCR results If not
	 * found, create new OCR for image
	 * 
	 * @param objectId
	 * @return
	 */
	public Ocr getOcrFromId(int objectId) {
		Specimen specimen = null;
		specimen = Specimen.getSpecimen(objectId);
		Ocr ocr = getOcr(specimen);
		if (ocr.getOcr() != null) {
			// Ocr is already in database
			return ocr;
		}
		return getNewOcrFromObjectId(objectId);
	}

	public Ocr getNewOcrFromObjectId(String id) {
		int objectId;
		try { // get image from database
			objectId = Integer.parseInt(id);
		} catch (Exception e) {
			// id is not an integer
			return null;
		}
		return getNewOcrFromObjectId(objectId);
	}

	public Ocr getNewOcrFromObjectId(int objectId) {
		Image image = null;
		int imageId = 0;
		try {
			image = Image.getImage(objectId);
			imageId = image.getId();
		} catch (Exception e) {
			return null;
		}
		return getNewOcrFromImageId(imageId);
	}

	public Ocr getOcr(int id) {
		Specimen specimen = Specimen.getSpecimen(id);
		if (specimen == null) {
			return null;
		}
		return getOcr(specimen);
	}

	public Ocr getOcr(Specimen specimen) {
		return new Ocr(specimen.getBarCode(), specimen.getOcr(), specimen
				.getLabelData());
	}

	public boolean updateSpecimen(int specimenId, Ocr ocr) {
		Specimen specimen = Specimen.getSpecimen(specimenId);
		if (specimen != null) {
			updateSpecimen(specimen, ocr);
			return true;
		}
		return false;
	}

	public void updateSpecimen(Specimen specimen, Ocr ocr) {
		specimen.setOcr(ocr.getOcr());
		specimen.setBarCode(ocr.getBarCode());
		specimen.setLabelData(ocr.getLabelData());
	}
}
