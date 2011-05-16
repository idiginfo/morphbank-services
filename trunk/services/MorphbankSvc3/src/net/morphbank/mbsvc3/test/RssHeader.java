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

public class RssHeader {
	  private String title = "";
	  private String description = "";
	  private String link = "";
	  private String language = "";
	  private String copyright = "";
	  private String author = "";
	  private String guid = "";
	  private String pubdate ="";
	  
	  public String getTitle() {
	    return title;
	  }
	  public void setTitle(String title) {
	    this.title = title;
	  }
	  public String getDescription() {
	    return description;
	  }
	  public void setDescription(String description) {
	    this.description = description;
	  }
	  public String getLink() {
	    return link;
	  }
	  public void setLink(String link) {
	    this.link = link;
	  }
	  public String getLanguage() {
	    return language;
	  }
	  public void setLanguage(String language) {
	    this.language = language;
	  }
	  public String getCopyright() {
	    return copyright;
	  }
	  public void setCopyright(String copyright) {
	    this.copyright = copyright;
	  }
	  public String getAuthor() {
	    return author;
	  }
	  public void setAuthor(String author) {
	    this.author = author;
	  }
	  public String getGuid() {
	    return guid;
	  }
	  public void setGuid(String guid) {
	    this.guid = guid;
	  }
	  public String getPubDate() {
	    return pubdate;
	  }
	  public void setPubDate(String pubdate) {
	    this.pubdate = pubdate;
	  }

}
