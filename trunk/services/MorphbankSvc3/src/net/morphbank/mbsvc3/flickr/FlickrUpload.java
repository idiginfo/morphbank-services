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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Vector;

import net.morphbank.MorphbankConfig;
import net.morphbank.object.BaseObject;
import net.morphbank.object.Collection;
import net.morphbank.object.Image;
import net.morphbank.object.Specimen;
import net.morphbank.object.Taxon;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.activity.ActivityInterface;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.AuthInterface;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.aetrion.flickr.uploader.Uploader;
import com.aetrion.flickr.util.IOUtilities;

public class FlickrUpload {

	Auth auth = null;
	AuthInterface authInterface = null;
	RequestContext requestContext = null;
	ActivityInterface iface = null;
	Flickr flickr = null;
	Uploader uploader = null;
	StringBuffer messages = new StringBuffer();

	public FlickrUpload() {
		init(new FlickrService());
	}

	public FlickrUpload(FlickrService flickrService) {
		init(flickrService);
	}

	void init(FlickrService flickrService) {
		try {
			flickr = new Flickr(flickrService.getKey(), flickrService
					.getSecret(), new REST());
			requestContext = RequestContext.getRequestContext();
			auth = new Auth();
			auth.setPermission(Permission.WRITE);
			auth.setToken(flickrService.getToken());
			requestContext.setAuth(auth);
			Flickr.debugRequest = false;
			Flickr.debugStream = false;
			authInterface = flickr.getAuthInterface();
			uploader = flickr.getUploader();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean upload(Collection collection) {
		return false;
	}

	public boolean upload(int id) {
		InputStream in = null;
		String imageUrl = null;
		BaseObject object = null;
		Image image = null;
		try {
			object = BaseObject.getEJB3Object(id);
			if (object == null || !(object instanceof Image)) {
				messages.append(" non-image for id ").append(id);
				return false;
			}
			image = (Image) object;
			if (image.getSpecimen() == null) {
				messages.append(" no specimen for image ").append(id);
				return false;
			}
			if (image.getSpecimen().getTaxon() == null) {
				messages.append(" no taxon for image ").append(id);
				return false;
			}
			Specimen specimen = image.getSpecimen();
			Taxon taxon = specimen.getTaxon();
			//TODO convert image metadata to tags
			URL url = new URL(imageUrl);
			URLConnection connection = url.openConnection(MorphbankConfig.getProxy());
			in = connection.getInputStream();
			UploadMetaData metaData = new UploadMetaData();
			// check correct handling of escaped value
			metaData.setTitle(getTitle(image));
			ArrayList<String> tags = new ArrayList<String>();
			tags.add(image.getImageAltText()); // general tags 
			addTaxonomicInfo(tags, taxon);
			addMrtgInfo(tags, image);
			metaData.setTags(tags);
			String photoId = uploader.upload(in, metaData);
			messages.append(" New photo for id: ").append(id).append(
					" photo id: ").append(photoId);
			updateCopyright(image, photoId);
			IOUtilities.close(in);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void updateCopyright(Image id, String photoId) {
		String copyright = id.getCopyrightText();
		//TODO finish
	}

	String getTitle(Image image) {
		if (image.getName() != null) return image.getName();
		return "Image of a "
				+ image.getSpecimen().getTaxon().getScientificName();
	}

	static final String TAXON_NAMESPACE = "taxonomic";
	void addTaxonomicInfo(ArrayList tags, Taxon taxon) {
		//TODO make consistent with EOL
		tags.add("namespace:taxonomic=");
	}
	
	void addMrtgInfo(ArrayList tags, Image image){
		//consistent with MRTG schema
	}
}
