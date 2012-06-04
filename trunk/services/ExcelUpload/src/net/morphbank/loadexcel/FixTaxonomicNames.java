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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Fix taxonomicNames in Taxa tables being null after an upload
 * @author gjimenez
 *
 */
public class FixTaxonomicNames {
	private static Connection conn;
	private static Statement statement;
	private static String dbHost;
	private static String dbName;
	private static String dbUserId;
	private static String dbPassword;
	private static String propertyFile;
	private static String sqlUpdate = "Update Taxa t set t.taxonomicNames=? where t.tsn=?";
	private static String sqlSelect = "select t.tsn from Taxa t where t.taxonomicNames is null and t.tsn > 0 order by t.tsn";

	public FixTaxonomicNames() {
		super();
	}
	
	public void run() {
		GetConnection newconnect = new GetConnection();
		
		conn = newconnect.openConnection(dbHost,dbName, dbUserId, dbPassword);
		try {
			statement = conn.createStatement();

			conn.setAutoCommit(false);
			ArrayList<Integer> taxNamesNull = getAllNamesNull();
			this.fixNames(taxNamesNull);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		propertyFile = "loadData.properties";
		FixTaxonomicNames fixNames = new FixTaxonomicNames();
		setProperties();
		System.out.println("db host is: " + dbHost);
		System.out.println("db name is: " + dbName);
		fixNames.run();
		System.out.println("Done");
		
	}

	public static Connection getConnection() {
		return conn;
	}

	public static Statement getStatement() {
		return statement;
	}

	
	public static void setProperties() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(propertyFile));
			dbHost = prop.getProperty("dbhost");
			dbName = prop.getProperty("dbname");
			dbUserId = prop.getProperty("login");
			dbPassword = prop.getProperty("password");
		} catch (FileNotFoundException e) {
			System.out.println(propertyFile);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @return all the tsn from rows with taxonomicNames null
	 */
	private ArrayList<Integer> getAllNamesNull() {
		ArrayList<Integer> taxNamesNull = new ArrayList<Integer>();
		try {
			ResultSet results = statement.executeQuery(sqlSelect);
			while (results.next()){
				taxNamesNull.add(results.getInt("tsn"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return taxNamesNull;
	}
	
	private void fixNames(ArrayList<Integer> taxNamesNull) {
		Iterator<Integer> it = taxNamesNull.iterator();
		PreparedStatement updateStmt;
		try {
			updateStmt = conn.prepareStatement(sqlUpdate);
			while (it.hasNext()) {
				int tsn = it.next();
				String taxonomicNames = getTaxonomicNamesFromBranch(tsn);
				System.out.println("Updating tsn: " + tsn + " with taxonomicName: " + taxonomicNames);
				updateStmt.setString(1, taxonomicNames);
				updateStmt.setInt(2, tsn);
				updateStmt.executeUpdate();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Output the names in reverse order
	 * @return
	 */
	private String getTaxonomicNamesFromBranch(int tsn) {
		String taxonomicNames = "";
		LinkedList<String> branches;
		try {
			branches = getTaxonBranchFromParent(tsn);
			while (!branches.isEmpty()) {
				taxonomicNames += branches.removeLast() + " ";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return taxonomicNames;
	}

	/**
	 * Get all the branches up to tsn = 0 as a Queue
	 * @return
	 * @throws SQLException
	 */
	
	private LinkedList<String> getTaxonBranchFromParent(int tsn) throws SQLException{
		LinkedList<String> taxonomicNames = new LinkedList<String>();
		String sql = "select parent_tsn, scientificName from Tree tr where tsn=?";
		String lock = "LOCK tables Tree tr WRITE";
		PreparedStatement branchstmt = conn.prepareStatement(sql);
		branchstmt.setInt(1, tsn);
		statement.executeQuery(lock);
		int currentTsn = tsn; 
		while (currentTsn != 0){
			ResultSet result = branchstmt.executeQuery();
			if (result.next()){
				int parentTsn = result.getInt("parent_tsn");
				String scientificName = result.getString("scientificName");
				taxonomicNames.add(scientificName); 
				currentTsn = parentTsn;
				branchstmt.setInt(1, currentTsn);
			}
		}
		statement.executeQuery("UNLOCK TABLES");
		return taxonomicNames;
	}

}
