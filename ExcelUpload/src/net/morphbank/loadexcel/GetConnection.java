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

//This file makes the connection to the data base      //
//It loads the driver and using username and password  //
//connects with the data base.The credentials are      //
//obtained through GetCredentials class                // 
//                                                     //
//created by: Karolina Maneva-Jakimoska                //
//date:       January 18 2006                          //
/////////////////////////////////////////////////////////

import java.sql.Connection;
import java.sql.DriverManager;

public class GetConnection {

	// private String connString =
	// "jdbc:mysql://localhost/MB30Sample?noAccessToProcedureBodies=true";
	private Connection conn = null;

	public GetConnection() {
	}

	public Connection openConnection(String dbHost, String dbName, String userId, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//System.out.println("The Driver has been loaded");
			String connString = "jdbc:mysql://" + dbHost + ":3306/" + dbName
					+ "?noAccessToProcedureBodies=true"
					+ "&useUnicode=true&characterEncoding=UTF-8";
			// obtaining credential from authorised user
			conn = DriverManager
			// .getConnection(connString, "webuser", "namaste");
					.getConnection(connString, userId, password);
			System.out.println("Connection was established to "+dbName+" on "+dbHost);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return conn;
	}

	// public method that provides access to the connection
	public Connection getConnect() {
		return conn;
	}
}
