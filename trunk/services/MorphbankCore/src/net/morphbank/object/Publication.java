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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author riccardi
 * 
 */
@Entity
@Table(name = "Publication")
@DiscriminatorValue("Publication")
public class Publication extends BaseObject {
	static final long serialVersionUID = 1;

	//public enum months {Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec};
	private String publicationType;
	private String address;
	private String annote;
	private String author;
	private String publicationTitle;
	private String chapter;
	private String edition;
	private String editor;
	private String howPublished;
	private String institution;
	@Column(name="`key`")
	private String key;
	//@Enumerated(EnumType.STRING)
	//@Transient
	//private months month;
	@Column(name="`day`")
	private Integer day;
	private String note;
	private String number;
	private String organization;
	private String pages;
	private String publisher;
	private String school;
	private String series;
	private String title;
	private String volume;
	private String year;
	private String isbn;
	private String issn;

	public Publication() {
		super();
	}

	public Publication(int id, String name, User user, User user2, Group group) {
		super(id, name, user, user, group);
		init();
	}

	public Publication(int id, String name, String ownerId, String submitterId, String groupId) {
		super(id, name, ownerId, submitterId, groupId);
	}

	public void init() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAnnote() {
		return annote;
	}

	public void setAnnote(String annote) {
		this.annote = annote;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublicationTitle() {
		return publicationTitle;
	}

	public void setPublicationTitle(String publicationTitle) {
		this.publicationTitle = publicationTitle;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getHowPublished() {
		return howPublished;
	}

	public void setHowPublished(String howPublished) {
		this.howPublished = howPublished;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	// public String getMonth() {
	// return month.toString();
	// }
	//
	// public void setMonth(String month) {
	// this.month = months.valueOf(month);
	// }

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}
}
