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
package net.morphbank.mbsvc3.flickr;

public class FlickrService {
	public static final String FLICKR_KEY = "62c59ff4531ca180a611f2e5fbeb1f05";
	public static final String FLICKR_SECRET = "967fd21b0d093228";
	public static final String FLICKR_TOKEN = "72157617650647387-dd0396fcb913ab18";

	public String getKey() {
		return FLICKR_KEY;
	}

	public String getSecret() {
		return FLICKR_SECRET;
	}

	public String getToken() {
		return FLICKR_TOKEN;
	}
}
