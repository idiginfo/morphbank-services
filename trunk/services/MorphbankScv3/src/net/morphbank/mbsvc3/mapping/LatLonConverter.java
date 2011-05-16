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
package net.morphbank.mbsvc3.mapping;

import net.morphbank.MorphbankConfig;


public class LatLonConverter {

	// sample string N20�25'26.8"
	static final String[] DEGREE = { "�", "�"};
	static final String[] MINUTE = { "�", "'" };
	static final String[] SECOND = { "�", "\"" };
	static final char[] SECONDCHAR = { '�', '"'};
	static final String DIRECTION = "NSEWnsew";

	public static Double decimalDegrees(String str) {
		if (str == null || str.length()==0) {
			return null;
		}
		//TODO try to convert directly as double
		Double degrees = 0.0;
		Double minutes = 0.0;
		Double seconds = 0.0;

		try {
			str = str.trim();
			int i;
			int degreePos = str.indexOf(DEGREE[0]);
			for (i = 1; degreePos < 0 & i < DEGREE.length; i++) {
				degreePos = str.indexOf(DEGREE[i]);
			}
			int minutePos = str.indexOf(MINUTE[0]);
			for (i = 1; minutePos < 0 & i < MINUTE.length; i++) {
				minutePos = str.indexOf(MINUTE[i]);
			}
			int secondPos = str.indexOf(SECOND[0]);
			for (i = 1; secondPos < 0 & i < SECOND.length; i++) {
				secondPos = str.indexOf(SECOND[i]);
			}
			int dirPos = 0;
			char direction = str.charAt(dirPos);
			if (DIRECTION.indexOf(direction, 0) < 0) {
				// direction not first character
				direction = str.charAt(str.length() - 1);
				dirPos = -1;
				if (DIRECTION.indexOf(direction, 0) < 0) {
					direction = ' ';
				}
			}
			if (degreePos >= 0) {
				String degreeStr = str.substring(dirPos + 1, degreePos);
				degrees = Double.valueOf(degreeStr);
				if (minutePos >= 0) {
					String minuteStr = str.substring(degreePos + 1, minutePos);
					minuteStr = minuteStr.replace(',', '.');
					minutes = Double.valueOf(minuteStr);
					if (secondPos >= 0) {
						String secondStr = str.substring(minutePos + 1,
								secondPos);
						secondStr = secondStr.replace(',', '.');
						seconds = Double.valueOf(secondStr);
					}
				}
				Double value = degrees + minutes / 60 + seconds / 3600;
				if (direction == 'S' || direction == 'W') {
					value = -value;
				}
				return value;
			}
			return null;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		Double deg = decimalDegrees("16� 05' S ");
		MorphbankConfig.SYSTEM_LOGGER.info(deg.toString());
		MorphbankConfig.SYSTEM_LOGGER.info((SECONDCHAR[0]==SECONDCHAR[2]?"true":"false"));
		//Character c0 = Character.valueOf(SECONDCHAR[0]);
		//Character c2 = Character.valueOf(SECONDCHAR[2]);
		//MorphbankConfig.SYSTEM_LOGGER.info(c2.);
		//MorphbankConfig.SYSTEM_LOGGER.info(Byte.valueOf(SECONDCHAR[2]));
	}
}
