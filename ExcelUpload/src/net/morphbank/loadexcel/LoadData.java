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

///File wich contains Main class. The whole load in the database /
//is done through this class. Creates objects from all classes  /
//that read from a particular sheet and populates specified     /
//table.                                                        /
//                                                              /
//created by: Karolina Maneva-Jakimoska                         /
//date      : January 21 2006                                   /
//modified  : September 29 2006                                 /
/////////////////////////////////////////////////////////////////

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

public class LoadData {
	private static String logFile = "report.txt";
	private static boolean debug = true;
	private static Connection conn;
	private static Statement statement;
	private static BufferedWriter out;
	private static SheetReader sheetReader;
	private static CheckNameTable checkNames;
	private static ExternalLinks externalLinks;
	private static String dbHost;
	private static String dbName;
	private static String dbUserId;
	private static String dbPassword;
	private static String imageDirectoryPath;

	public static void main(String args[]) {

		System.setProperty("file.encoding", "Cp1252");
		// System.setProperty("file.encoding", "UTF-8");
		Charset charSet = Charset.defaultCharset();
		System.out.println("default charset: " + charSet.displayName());
		String filename = "";
		
//		String dbHost = "dev.morphbank.net";
		// filename = "C:/dev/java31/lib/Morphbank_MS2_p1Deb.xls";
		// filename = "C:/dev/java2008/testutf8a.xls";
//		filename = "/usr/local/dev/morphbank/upload/mb3a_current.xls";
		filename = "/home/gjimenez/Downloads/MatthewBuffington/mb3a_Oberthuerellinae04.xls";
//		filename = "/home/gjimenez/Downloads/mb3a_MattBuffington_forQC.xls";
//		imageDirectoryPath = "../Camellia/";
		if (args.length == 0) {
			System.out.println("usage: java net.morphbank.loadexcel.LoadData.java filename"
					+ " [dbhost] [dbname] [imagepath] [debug]");
			// System.exit(-1);
		}
		
		setProperties();
		
		if (args.length > 0) filename = args[0];
		if (args.length > 1) dbHost = args[1];
		if (args.length > 2) dbName = args[2];
		if (args.length > 3) imageDirectoryPath = args[3];
		if (args.length > 4) debug = "debug".equals(args[4]);

		
		System.out.println("excel file is: " + filename);
		System.out.println("log file is : " + logFile);
		System.out.println("db host is: " + dbHost);
		System.out.println("db name is: " + dbName);

		// System.out.println("Before conection");
		GetConnection newconnect = new GetConnection();

		
		
		conn = newconnect.openConnection(dbHost,dbName, dbUserId, dbPassword);
		try {
			statement = conn.createStatement();

			// reading from the excel sheet
			sheetReader = new SheetReader(filename, newconnect);
			ValidateXls isvalid = new ValidateXls(sheetReader, true);
			if (!isvalid.checkEverything()) {
				System.err.println("Error(s) in SpreadSheet. Program interrupted.");
				System.exit(0);
			}
			System.out.println("No Error found. Let's get this baby runnin'!");
			checkNames = new CheckNameTable();
			externalLinks = new ExternalLinks();

			// Printing statement for checking purpose
			System.out.print("Release date: " + sheetReader.getReleaseDate());
			System.out.print(" UserId: " + sheetReader.GetUserId());
			System.out.print(" group: " + sheetReader.GetGroupId());
			System.out.print(" submitted by: " + sheetReader.GetSubmitterId());
			System.out.print(" institution: " + sheetReader.GetInstitutionLink());
			System.out.println(" project: " + sheetReader.GetProjectLink1());

			// starting a report for the new set
			log("Report for set of images contributed by "
					+ sheetReader.getEntry("ImageCollection", 1, 4));
			// TODO use transaction instead of locks, except while debugging
			// locking all the tables used in the program. most of them for
			// Writing
			if (debug) { // lock tables
				String temp = "LOCK TABLES SpecimenPart WRITE, Form WRITE, DevelopmentalStage WRITE, View WRITE,"
						+ " ViewAngle WRITE, ImagingPreparationTechnique WRITE, ImagingTechnique WRITE, ContinentOcean WRITE, Sex WRITE,"
						+ " TypeStatus WRITE, BaseObject WRITE, User READ, UserGroup READ, Locality WRITE, Country READ,"
						+ " BasisOfRecord READ, Specimen WRITE, Tree WRITE, Image WRITE, Groups READ, ExternalLinkType WRITE,"
						+ " ExternalLinkObject WRITE, TaxonUnitTypes READ, TaxonConcept WRITE, TaxonAuthors WRITE, CurrentIds READ"
						+ ", Kingdoms READ, Taxa WRITE";

				statement.executeQuery(temp);
			} else {
				// transaction
				conn.setAutoCommit(false);
			}

			System.out.println("Uploading Taxon Data ...");
			TaxonData taxon = new TaxonData(sheetReader);
			taxon.processTaxa();
			System.out.println("Done.");

			System.out.println("Uploading Supporting Data ...");
			QuerySupport support = new QuerySupport(sheetReader);
			support.loadQuerySupportData();
			System.out.println("Done.");

			System.out.println("Uploading Locality ...");
			Locality locality = new Locality(sheetReader);
			locality.processLocalities();
			System.out.println("Done.");

			System.out.println("Uploading View ...");
			View view = new View(sheetReader);
			view.processViews();
			System.out.println("Done.");

			System.out.println("Uploading Specimen ...");
			Specimen specimen = new Specimen(sheetReader);
			specimen.processSpecimens();
			System.out.println("Done.");

			System.out.println("Uploading Image ...");
			Image image = new Image(sheetReader, specimen);
			image.processImages();
			System.out.println("Done.");

			System.out.println("Uploading External Links ...");
			externalLinks.processLinks();
			System.out.println("Done.");

			if (debug) {
				statement.executeQuery("UNLOCK TABLES");
			} else {
				conn.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String entry) {
		try {
			out = new BufferedWriter(new FileWriter(logFile, true));
			out.write(entry + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static int getId(String objectType, String objectRef) {
		if (objectType.equals("Image")) return Image.getImage(objectRef);
		if (objectType.equals("View")) return View.getView(objectRef);
		if (objectType.equals("Specimen")) return Specimen.getSpecimen(objectRef);
		if (objectType.equals("Locality")) return Locality.getLocality(objectRef);
		if (objectType.equals("Taxon")) return TaxonData.getTaxon(objectRef);
		return 0;
	}

	public static Connection getConnection() {
		return conn;
	}

	public static Statement getStatement() {
		return statement;
	}

	public static SheetReader getSheetReader() {
		return sheetReader;
	}

	public static CheckNameTable getCheckNames() {
		return checkNames;
	}

	public static ExternalLinks getExternalLinks() {
		return externalLinks;
	}
	
	public static void setProperties() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("loadData.properties"));
			dbHost = prop.getProperty("dbhost");
			dbName = prop.getProperty("dbname");
			dbUserId = prop.getProperty("login");
			dbPassword = prop.getProperty("password");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
