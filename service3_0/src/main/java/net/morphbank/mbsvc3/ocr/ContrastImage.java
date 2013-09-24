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

import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

import net.morphbank.MorphbankConfig;

import com.sun.media.jai.codec.FileCacheSeekableStream;

/**
 * Class to create higher contrast image for Herbis OCR analysis Adapted from
 * code from Youjun
 * 
 * @author riccardi
 * 
 */
public class ContrastImage {

	private LookupTableJAI lookupTable;
	private byte[][] tableData;

	public ContrastImage() {
		initTableData();
	}

	/**
	 * Fetch tiff file from URL and make a high contrast image suitable for OCR
	 * analysis
	 * 
	 * @param url
	 *            address of tiff image
	 * @param contrastFileLocation
	 *            full path name of file to hold the high contrast image
	 * @throws Exception
	 */
	public void makeJpgFromUrl(String urlText, String contrastFileLocation)
			throws IOException {
		URL url = new URL(urlText);
		URLConnection connection = url.openConnection(MorphbankConfig.getProxy());
		InputStream in = connection.getInputStream();
		makeContrastImage(in, contrastFileLocation);
		in.close();

		// ParameterBlock params = new ParameterBlock();
		// params.add(url);
		// RenderedOp image = JAI.create("url", url);
		// RenderedOp contrast = doContrast(image);
		// File outcJpg = new File(contrastFileLocation);
		// ImageIO.write(contrast, "JPEG", outcJpg);
	}

	/**
	 * Fetch tiff file file system and make a high contrast image suitable for
	 * OCR analysis
	 * 
	 * @param imageId
	 *            base name of image files imageId+".tif" is original tiff file
	 *            name imageId+"c.jpg" is resulting high contrast image file
	 *            name
	 * @param imagePath
	 *            path of directory containing tiff file and contrast file
	 * @throws IOException
	 */
	public void makeJpg(String imageId, String imagePath) throws IOException {
		String originalFile = imageId + ".tif";
		String contrastFile = imageId + "c.jpg";
		String fileLocation = imagePath + originalFile;
		FileInputStream in = new FileInputStream(fileLocation);
		makeContrastImage(in, imagePath + contrastFile);
	}

	/**
	 * Make a high contrast image from the InputStream in using javax.imagio
	 * 
	 * @param in
	 *            the InputStream that contains the original tiff image
	 * @param outputFile
	 *            the path of the file to hold the result image
	 * @throws IOException
	 */
	public void makeContrastImage(InputStream in, String outputFile)
			throws IOException {
		// TIFFDecodeParam decodeParam = new TIFFDecodeParam();
		// decodeParam.setDecodePaletteAsShorts(true);
		FileCacheSeekableStream strm = new FileCacheSeekableStream(in);
		ParameterBlock params = new ParameterBlock();
		params.add(strm);
		// RenderedOp image = JAI.create("tiff", params);
		RenderedOp image = JAI.create("tiff", params);
		RenderedOp contrast = doContrast(image);
		File outcJpg = new File(outputFile);
		ImageIO.write(contrast, "JPEG", outcJpg);
		strm.close();
	}

	/*
	 * Create the contrast adjustment table (Youjun supplied the table
	 */
	private void initTableData() {
		tableData = new byte[3][256];
		for (int i = 0; i < 256; i++) {
			double c = 0;
			if (i > 224)
				c = 255 / i;
			if (i > 192 && i < 225)
				c = 255 / 224;
			if (i > 160 && i < 193)
				c = 224 / 192;
			if (i > 128 && i < 161)
				c = 192 / 160;
			if (i > 96 && i < 129)
				c = 64 / 96;
			if (i > 64 && i < 97)
				c = 32 / 64;
			if (i > 32 && i < 65)
				c = 1 / 32;
			if (i < 33)
				c = 1 / 255;
			tableData[0][i] = (byte) (c * i); // this may be different
			tableData[1][i] = (byte) (c * i); // for each band
			tableData[2][i] = (byte) (c * i);
		}
		lookupTable = new LookupTableJAI(tableData);
	}

	protected RenderedOp doContrast(RenderableOp image) {
		return JAI.create("lookup", image, lookupTable);
	}

	protected RenderedOp doContrast(PlanarImage image) {
		return JAI.create("lookup", image, lookupTable);
	}

}
