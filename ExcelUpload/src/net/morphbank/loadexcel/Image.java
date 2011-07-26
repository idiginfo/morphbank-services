/*******************************************************************************
 * Copyright (c) 2010 Greg Riccardi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * 
 * Contributors:
 *     Greg Riccardi - initial API and implementation
 ******************************************************************************/
package net.morphbank.loadexcel;

//This class retreives and saves the images in the//
//data base. The images are entered for all valid //
//entries.                                        //
//created by: Karolina Maneva-Jakimoska           //
//date: February 10 2006                          //
//modified : October 5th 2006                     //
//                                                //
////////////////////////////////////////////////////

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Image {
	final static String MYTYPE = "Image";
	public static HashMap<String, Integer> imageIds = new HashMap<String, Integer>();

	private Statement statement;
	private SheetReader sheetReader;
	private Specimen specimen;
	private ResultSet result;
	private int imageId;
	private String magnification = "";
	private String imageType = "";
	private String copyrightText = "";
	private int viewId = 0;
	private int specimenId = 0;
	private int userId = 0;
	private int groupId = 0;
//	private Date dateToPublish = null;
	private String dateToPublish = "";
	private String originalFileName = "";
	CallableStatement insertStmt = null;
	Updater updater = null;
	private int eol;
	private String photographer;
	private String creativeCommons;

	
	public Image(SheetReader sheetReader, Specimen specimen) {
		this.statement = LoadData.getStatement();
		this.sheetReader = sheetReader;
		this.specimen = specimen;
	}

	public void processImages() {
		int rows = sheetReader.GetRows(MYTYPE);
		for (int row = 1; row < rows; row++) {
//			originalFileName = sheetReader.getEntry(MYTYPE, 5, row);
			originalFileName = sheetReader.getValue(MYTYPE, "Image file name", row);
			if (originalFileName.equals("")) continue;
//			magnification = sheetReader.getEntry(MYTYPE, 3, row);// int
			magnification = sheetReader.getValue(MYTYPE, "Magnification", row);
//			copyrightText = sheetReader.getEntry(MYTYPE, 4, row);
			copyrightText = sheetReader.getValue(MYTYPE, "Copyright Info", row);
			imageType = getImageType(originalFileName);

//			String viewRef = sheetReader.getEntry(MYTYPE, 2, row);
			String viewRef = sheetReader.getValue(MYTYPE, "My View Name", row);
			viewId = View.getView(viewRef);
			if (viewId == 0) {
				System.out.println("Image row " + row
						+ " not added because it has no match for view " + viewRef);
				continue;
			}

			// Specimen new_spec=spec;
			String specimenRef = sheetReader.getEntry(MYTYPE, 1, row);
			specimenId = Specimen.getSpecimen(specimenRef);
			// System.out.println("Specimen id for this image is " +
			// specimenId);
			
			photographer = sheetReader.getValue(MYTYPE, "Photographer", row);
			if (sheetReader.getValue(MYTYPE, "eol", row).equalsIgnoreCase("yes")) eol = 1;
			else eol =0;
			creativeCommons = sheetReader.getValue(MYTYPE, "Creative Commons", row);
			
			dateToPublish = sheetReader.getReleaseDate();
			userId = sheetReader.GetUserId();
			groupId = sheetReader.GetGroupId();

			imageId = createImage(row);
			imageIds.put(originalFileName, imageId);
		}// end of for loop
	}

	private int createImage(int row) {
		try {
			String insertQuery = "{call CreateObject( 'Image', ?, ?, ?, ?, ?, '')}";
			insertStmt = LoadData.getConnection().prepareCall(insertQuery);
			int i = 1;
			insertStmt.setInt(i++, userId);
			insertStmt.setInt(i++, groupId);
			insertStmt.setInt(i++, sheetReader.GetSubmitterId());
//			insertStmt.setDate(i++, dateToPublish);
			insertStmt.setString(i++, dateToPublish);
			insertStmt.setString(i++, "New image from upload");

			ResultSet res = insertStmt.executeQuery();
			res.next();
			imageId = res.getInt(1);

			Updater updater = new Updater(sheetReader, MYTYPE);
			updater.addIntColumn("userId", userId);
			updater.addIntColumn("groupId", groupId);
			updater.addDateColumn("dateToPublish", dateToPublish);
			if (specimenId != 0) {
				updater.addIntColumn("specimenId", specimenId);
				System.out.println("No specimen found for image row " + row);
			}
			updater.addIntColumn("viewId", viewId);
			updater.addNumericColumn("magnification", magnification, row);
			updater.addStringColumn("imageType", imageType);
			updater.addStringColumn("copyrightText", copyrightText);
			updater.addStringColumn("originalFileName", originalFileName);
			updater.addStringColumn("photographer", photographer);
			updater.addIntColumn("eol", eol);
			updater.addStringColumn("creativeCommons", creativeCommons);
			int numupdated = updater.update(imageId);
			if (numupdated != 1) {
				System.out.println("Updater failed num " + numupdated);
				System.out.println("specimen id: " + specimenId);
				System.exit(0);
			}
			LoadData.getExternalLinks().addSheetLinks(imageId);
			System.out.println("Image row " + row + " added with id " + imageId);
			return imageId;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return 0;
	}

	private String getImageType(String originalFileName) {
		int dot = originalFileName.lastIndexOf(".");
		String imageType = originalFileName.substring(dot + 1, originalFileName.length())
				.toLowerCase();
		if (imageType.equals("tif")) imageType = "tiff";
		if (imageType.equals("jpeg")) imageType = "jpg";
		// System.out.println("image type: " + imageType);
		return imageType;
	}

	/**
	 * public method which gets the next AccessNum
	 * 
	 * @return
	 */
	public int GetAccessNum() {
		int access;
		String temp = "SELECT MAX(accessNum) FROM Image";
		try {
			result = statement.executeQuery(temp);
			result.next();
			access = result.getInt(1);
			return access + 1;
		} catch (SQLException sql) {
			sql.printStackTrace();
			System.exit(1);
		}
		return 0;
	}// end of GetAccessNum

	/**
	 * Get the referenced image from the list of images
	 * 
	 * @param specimenRef
	 * @return
	 */
	public static int getImage(String imageRef) {
		Integer imageId = imageIds.get(imageRef);
		if (imageId == null) return 0;
		return imageId;
	}

}
