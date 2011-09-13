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

//This class loads ExternalLinks information into the   /
//data base.                                            /
//                                                      /
//created by: Karolina Maneva-Jakimoska                 /
//date created:  October 03 2006                        /
//                                                      /
/////////////////////////////////////////////////////////

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import jxl.*;

public class ExternalLinks {
	final static String MYTYPE = "ExternalLinks";

	// private Statement statement;
	private SheetReader myread;
	private ResultSet result;
	private int columns;
	private int rows;
	private ExternalLink project1;
	private ExternalLink project2;
	private ExternalLink institution;

	// constructor for the ExternalLinks class
	public ExternalLinks() {
		Statement statement = LoadData.getStatement();
		myread = LoadData.getSheetReader();
		columns = myread.GetColumns(MYTYPE);
		rows = myread.GetRows(MYTYPE);
		if (myread.GetInstitutionName().length() != 0 && myread.GetInstitutionLink().length() != 0) {
			institution = new ExternalLink("Institution", myread.GetInstitutionName(), myread
					.GetInstitutionLink());
		}

		if (myread.GetProjectName1().length() != 0 && myread.GetProjectLink1().length() != 0) {
			project1 = new ExternalLink("Project", myread.GetProjectName1(), myread
					.GetProjectLink1());
		}

		if (myread.GetProjectName2().length() != 0 && myread.GetProjectLink2().length() != 0) {
			project2 = new ExternalLink("Project", myread.GetProjectName2(), myread
					.GetProjectLink2());
		}
	}

	private class ExternalLink {
		int row;
		String objectType = "";
		String objectRef = "";
		String extLinkTypeId = "";
		String extLinkType = "";
		String urlData = "";
		String label = "";
		String description = "";
		int id = 0;

		ExternalLink(int row) {
			this.row = row;
//			objectType = myread.getEntry(MYTYPE, 0, row);
//			objectRef = myread.getEntry(MYTYPE, 1, row);
//			extLinkType = myread.getEntry(MYTYPE, 2, row);
//			label = myread.getEntry(MYTYPE, 3, row);
//			urlData = myread.getEntry(MYTYPE, 4, row);
//			description = myread.getEntry(MYTYPE, 5, row);
			objectType = myread.getValue(MYTYPE, "ObjectType", row);
			objectRef = myread.getValue(MYTYPE, "Type Description for External Link", row);
			extLinkType = myread.getValue(MYTYPE, "Type of External Link", row);
			label = myread.getValue(MYTYPE, "Label for External Link", row);
			urlData = myread.getValue(MYTYPE, "External Link", row);
			description = myread.getValue(MYTYPE, "Notes", row);
			id = LoadData.getId(objectType, objectRef);
		}

		ExternalLink(String extLinkType, String label, String urlData) {
			this.extLinkType = extLinkType;
			this.label = label;
			this.urlData = urlData;
		}
	}

	public boolean addSheetLinks(int id) {
		boolean addedInst = addLink(id, institution);
		boolean addedProj1 = addLink(id, project1);
		boolean addedProj2 = addLink(id, project2);
		return addedInst || addedProj1 || addedProj2;
	}

	public boolean processLinks() {
		for (int row = 1; row < rows; row++) {
			ExternalLink link = new ExternalLink(row);
			if (link.id != 0) {
				addLink(link);
			}
		}
		return false;
	}

	/**
	 * function to add external link record to database, if not already present
	 * 
	 * @param id
	 */
	private boolean addLink(int id, ExternalLink link) {
		if (link == null) return false;
		link.id = id;
		return addLink(link);
	}

	private boolean addLink(ExternalLink link) {
		if (link == null) return false;
		List<String> names = LoadData.getCheckNames().getKeyFromTable("ExternalLinkType",
				link.extLinkType);
		if (names.size() != 1) {
			System.out.println("External link type '" + link.extLinkType + "' not defined");
			LoadData.log("External link type '" + link.extLinkType + "' not defined");
			return false;
		}
		link.extLinkTypeId = names.get(0);
		if (checkForLink(link)) {
			System.out.println("Extern link row " + link + " matches");
			LoadData.log("Extern link row " + link + " matches");
			return true;
		}
		if (insertLink(link)) {
			System.out.println("Extern link row " + link + " added");
			LoadData.log("Extern link row " + link + " added");
			return true;
		}
		System.out.println("Extern link row " + link + " not added ");
		LoadData.log("Extern link row " + link + " not added ");
		return false;
	}

	private boolean insertLink(ExternalLink link) {
		try {
			Connection connection = LoadData.getConnection();

			String insertSql = "INSERT INTO ExternalLinkObject (mbId, extlinkTypeId,"
					+ "urlData,description, label) VALUES (?,?,?,?,?)";
			// System.out.println(insertSql);
			PreparedStatement stmt = prepare(link, insertSql);
			stmt.setString(5, link.label);
			int res = stmt.executeUpdate();
			if (res == 0) {
				System.out.println("Problems updating ExternalLinksObject table");
				LoadData.log("Problems updating ExternalLinksObject table");
//				System.exit(1);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkForLink(ExternalLink link) {
		Connection connection = LoadData.getConnection();
		try {
			String sql = "SELECT linkId FROM ExternalLinkObject WHERE mbId=? "
					+ "AND extLinkTypeId=? AND urlData=? AND description=?";
			// System.out.println(sql);
			PreparedStatement stmt = prepare(link, sql);
			ResultSet result = stmt.executeQuery();
			return result.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Prepare for executing the sql statement create prepared statement set 4
	 * arguments mbid,extlinktypeid,urldata and description
	 * 
	 * @param link
	 * 
	 * @param sql
	 * @return
	 */
	private PreparedStatement prepare(ExternalLink link, String sql) {
		Connection connection = LoadData.getConnection();
		try {
			// System.out.println(sql);
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, link.id);
			stmt.setString(2, link.extLinkTypeId);
			stmt.setString(3, link.urlData);
			stmt.setString(4, link.description);
			return stmt;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
