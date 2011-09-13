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

//File wich contains Main class. The external links load in the /
//database is done through this class. Creates objects from all /
//classes that read from a particular sheet and populates       /
//specified table.                                              /
//                                                              /
//created by: Karolina Maneva-Jakimoska                         /
//date      : July 2 2007                                       /
//                                                              /
/////////////////////////////////////////////////////////////////

import java.io.*;
import java.sql.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;

public class CreateExternalLinks {

	final static String MYTYPE = "ExternalLinks";

	private static Connection conn;
	private static BufferedWriter out;
	private static Statement statement;

	public static void main(String args[]) {
		try {
			System.out.println("Before conection");
			LoadData.log("Before conection");
			GetConnection newconnect = new GetConnection();
			conn = newconnect.getConnect();
			statement = conn.createStatement();

			ResultSet result;
			String filename;
			String image_directory_path;
			filename = "./AlaskaGUID.xls";

			System.out.println("excel path is: " + filename);
			LoadData.log("excel path is: " + filename);

			// reading from the excel sheet
			ReadExternalLinks newread = new ReadExternalLinks(filename, newconnect);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
//				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}// end of class LoadData
