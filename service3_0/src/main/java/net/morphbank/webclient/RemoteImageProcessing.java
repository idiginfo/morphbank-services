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
package net.morphbank.webclient;

import java.io.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

public class RemoteImageProcessing {
	static String IMAGE_FILE = "C:/Users/riccardi/Application Data/Pictures/morphbankmap.jpg";
	static String ORIGINAL_FILE_NAME = "morphbankmap.jpg";
	static String URL = "http://itest.morphbank.net/Image/imageFileUpload.php";
	static String ID = "1001318";

	public static void main(String[] args) throws Exception {
		RemoteImageProcessing remoteProcessing = new RemoteImageProcessing();
		remoteProcessing.sendImage(URL, ID, ORIGINAL_FILE_NAME, IMAGE_FILE);
	}

	// $request->addPostData('id', $id);
	// $request->addPostData('fileName', $imageFileName);
	// $request->addFile('image', $imageFilePath, $imageMimeType);

	public void sendImage(String strURL, String id, String originalFileName, String imageFileName)
			throws Exception {
		File input = new File(imageFileName);
		// Prepare HTTP post
		PostMethod post = new PostMethod(strURL);

		Part[] parts = {new StringPart("id",id), new StringPart("fileName",originalFileName),
				new FilePart("image", originalFileName, input) };
		RequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
		post.setRequestEntity(entity);
		// Get HTTP client
		HttpClient httpclient = new HttpClient();
		// Execute request
		try {
			System.out.println("Trying post");
			int postStatus = httpclient.executeMethod(post);
			// Display status code
			System.out.println("Response status code: " + postStatus);
			// Display response
			System.out.print("Response body: ");
			InputStream response = post.getResponseBodyAsStream();
			for (int i = response.read(); i != -1; i = response.read()) {
				System.out.write(i);
			}
			System.out.flush();
		} finally {
			// Release current connection to the connection pool once you are
			// done
			post.releaseConnection();
		}
	}
}
