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
package net.morphbank.object;

public class ObjectValidation {

	// BaseObject validation parameters
	public static final int NAME_LEN = 64;
	public static final int DESC_LEN = 255;
	public static final int LOGO_LEN = 25;
	public static final int THUMBURL_LEN = 30;

	// Image validation parameters
	public static final int IMGTYPE_LEN = 8;
	public static final int CPWRT_LEN = 255;
	public static final int ORFN_LEN = 127;

	// Specimen validation parameters
	public static final int PREP_LEN = 255;
	public static final int INST_LEN = 128;
	public static final int COLL_LEN = 128;
	public static final int CATN_LEN = 128;
	public static final int PCATN_LEN = 128;
	public static final int RELC_LEN = 128;
	public static final int RELT_LEN = 128;
	public static final int COLN_LEN = 128;
	public static final int BARC_LEN = 45;

	// TODO complete remainder of object validation parameters and methods

	public static boolean baseObjectValidate(BaseObject object) {
		return baseObjectValidate(object, false);
	}

	public static boolean baseObjectValidate(BaseObject object,
			boolean trimToSize) {
		if (object.getName() != null && object.getName().length() > NAME_LEN) {
			if (trimToSize) {
				object.setName(object.getName().substring(0, NAME_LEN - 1));
			} else {
				return false;
			}
		}
		if (object.getName() != null && object.getName().length() > NAME_LEN) {
			if (trimToSize) {
				object.setName(object.getName().substring(0, NAME_LEN - 1));
			}
		} else {
			return false;
		}

		return true;
	}
}
