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

import javax.servlet.http.HttpServletRequest;

import net.morphbank.mbsvc3.request.RequestParams;

public class FlickrRequestParams extends RequestParams {

	public static final String METHOD_UPLOAD = "upload";// upload images to flickr
	public static final String METHOD_FLICKRQUERY = "flickr";// get flickr info for images

	public FlickrRequestParams (HttpServletRequest req) {
		getSearchParams(req);
	}
}
