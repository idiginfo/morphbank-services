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
//This class is used to generate new ObjectId value and Update//
//the database with a corresponding new ObjectId (old ObjectId +1)//
//                                                        //
//created by: Karolina Maneva-Jakimoska                   // 
//date:       January 18 2006                             //
////////////////////////////////////////////////////////////

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ObjectId {

	public static int getNewId(Statement statement) {
		ResultSet result;
		int objectId;
		try {
			result = statement
					.executeQuery("SELECT max(id) AS id FROM BaseObject");
			result.next();
			objectId = result.getInt("id");
			return objectId;
		} catch (SQLException sql) {
			sql.printStackTrace();
			return 0;
		}
	}
}
