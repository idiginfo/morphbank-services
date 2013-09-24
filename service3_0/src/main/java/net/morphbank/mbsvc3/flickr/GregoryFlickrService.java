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

public class GregoryFlickrService extends FlickrService {
	public static final String FLICKR_KEY = "f9fec3064318b592d3a104601ae639f7";
	public static final String FLICKR_SECRET = "d5a614757a45bbd5";
	public static final String FLICKR_TOKEN = "72157617745756731-4452c40f475e387b";

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
