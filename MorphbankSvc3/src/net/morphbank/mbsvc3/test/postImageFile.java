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
package net.morphbank.mbsvc3.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

class postImageFile {
	private boolean postImageFileTest() {
		boolean result = false;
		try {
			Object morphBankImageId;
			PostMethod post = null;// MorphBankTest.getImagePostRequest(morphBankImageId.toString(),
				//	getImageFileName());
			HttpClient httpclient = new HttpClient();
			int postStatus = httpclient.executeMethod(post);
			System.out.println(post.getResponseBody());
			if (postStatus == 200) {
				result = true;
			}
		} catch (Exception ex) {
			Exception killer = ex;
		}
		if (!result) {
			Object statusText = "";
		}
		return result;
	}

	private String getImageFileName() {
		// TODO Auto-generated method stub
		return null;
	}
}
