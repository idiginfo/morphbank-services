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

//class to load data into the Specimen table
//This file finds the tsn for Given family       /                                                                                          
//genus or species provided for a given Specimen /                                                                                           //                                               /                                                                                           //created by: Karolina Maneva-Jakimoska          /                                                                                           // date:      March 5th 2006                     /
//upadated:   April 2nd 2007                     /                                                                                           //////////////////////////////////////////////////                                                                                            
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Taxon {
	private int tsn = 0;
	private int parentTsn = 0;
	private int kingdomId = 0;
	private int rankId = 0;
	String scientificName = "";
	private Connection conn;

	// constructor for FindTSN
	public Taxon(String scientificName) {
		this.scientificName = scientificName;
		if (findInTaxonData(scientificName)) return;
		findInDatabase(scientificName);
	}

	public Taxon(int tsn) {
		loadFromDatabase(tsn);
	}
	public Taxon(int tsn, Connection conn) {
		this.conn = conn;
		loadFromDatabase(tsn);
	}

	public boolean findInTaxonData(String scientificName) {
		int tsn = TaxonData.getTaxon(scientificName);
		if (tsn == 0) return false;
		return loadFromDatabase(tsn);
	}

	public boolean findInDatabase(String scientificName) {
		String escaped = scientificName.replaceAll("'", "\\'");
		String whereClause = " scientificName='" + escaped + "'";
		if (!init(whereClause)) return false;
		// add tsn to taxonIds
		TaxonData.taxonIds.put(scientificName, tsn);
		return true;
	}

	public boolean loadFromDatabase(int tsn) {
		String whereClause = " tsn=" + tsn;
		if (!init(whereClause)) return false;
		// add tsn to taxonIds
		TaxonData.taxonIds.put(this.scientificName, tsn);
		return true;
	}

	private boolean init(String whereClause) {
		try {
			Statement statement = LoadData.getStatement();
			if (statement == null) { //comes from validation instead of upload
				statement = conn.createStatement();
			}
			String query = "SELECT tsn,parent_tsn,kingdom_id,rank_id, scientificName FROM Tree WHERE "
					+ whereClause;
			ResultSet result = statement.executeQuery(query);
			if (!result.next()) return false;
			this.tsn = result.getInt(1);
			this.parentTsn = result.getInt("parent_tsn");
			this.kingdomId = result.getInt(3);
			this.rankId = result.getInt(4);
			this.scientificName = result.getString(5);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getTsn() {
		return tsn;
	}

	public int getParentTsn() {
		return parentTsn;
	}

	public int getRankId() {
		return rankId;
	}

	public int getKingdomId() {
		return kingdomId;
	}

	public String getScientificName() {
		return scientificName;
	}
}
