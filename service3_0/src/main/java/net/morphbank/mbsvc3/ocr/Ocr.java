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

import net.morphbank.MorphbankConfig;
import net.morphbank.object.Specimen;

/**
 * 
 * @author riccardi
 * 
 * Sample class for using the OcrClient class to do analysis of image
 * 
 */
public class Ocr {

	String barCode;
	String ocr;
	String labelData;

	/**
	 * Constructor for Ocr object
	 * 
	 * @param barCode
	 * @param ocr
	 */
	public Ocr(String barCode, String ocr) {
		setBarCode(barCode);
		setOcr(ocr);
	}
	
	public Ocr (Specimen specimen){
		this(specimen.getBarCode(), specimen.getOcr(), specimen.getLabelData());
	}

	public Ocr(String barCode, String ocr, String labelData) {
		setBarCode(barCode);
		setOcr(ocr);
		setLabelData(labelData);
	}

	/**
	 * Key and marker used by the OCR analysis client in its calls
	 */
	final static String KEY = "yale_peabody";
	final static String MARKER = "_abbyy_";

	// generate name for contrast image file
	static String getContrastName(String imageId) {
		return imageId + "c.jpg";
	}

	static String getOcrRegistrationName(String fileName) {
		return KEY + MARKER + fileName;
	}

	/**
	 * Submit image to Web service for OCR analysis and retrieve the result
	 * 
	 * @param fileName
	 *            name of file to be used in OCR service registration
	 * @param fileLocation
	 *            full path of file to be used in OCR service
	 * @return
	 */
	public static Ocr getOcr(String fileName, String fileLocation) {
		String ocrResult = null;
		OcrClient ocrClient = new OcrClient();
		String barCode = null;
		String ocr = null;
		try {
			ocrResult = ocrClient.submitImageForOcr(
					getOcrRegistrationName(fileName), fileLocation);
			String[] barCodes = ocrResult.split("@|" + MARKER);
			barCode = barCodes[barCodes.length - 1];
			if (barCode.length() > 0) {
				ocr = ocrClient
						.getOcrFromService(getOcrRegistrationName(barCode));
			} else {
				return null; // didn't get a result?
			}
		} catch (Exception e) {
			//MorphbankConfig.SYSTEM_LOGGER.info(e.toString());
			e.printStackTrace();
			MorphbankConfig.SYSTEM_LOGGER.info("Service failed for file: " + getOcrRegistrationName(fileName));
			MorphbankConfig.SYSTEM_LOGGER.info("Barcode is: " + barCode);
			return null;
		}
		MorphbankConfig.SYSTEM_LOGGER.info("doOcr returns: " + ocrResult);
		return new Ocr(barCode, ocr);
	}

	public void updateSpecimen(Specimen specimen) {
		specimen.setBarCode(barCode);
		specimen.setOcr(ocr);
		specimen.setLabelData(labelData);
	}

	// Getters and Setters
	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getOcr() {
		return ocr;
	}

	public void setOcr(String ocr) {
		this.ocr = ocr;
	}

	public String getLabelData() {
		return labelData;
	}

	public void setLabelData(String labelData) {
		this.labelData = labelData;
	}

}
