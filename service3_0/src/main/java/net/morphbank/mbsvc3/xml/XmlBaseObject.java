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
package net.morphbank.mbsvc3.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;

import java.text.DateFormat;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "object", propOrder = {
		"sourceId",
		"status",
		"owner",
		"dateCreated",
		"dateLastModified",
		"dateToPublish",
		"objectTypeId",
		"name",
		"description",
		"submittedBy",
		"objectLogo",
		"thumbUrl",
		"detailPageUrl",
		"hostServer",
		"relatedObject",
		"externalRef",
		"userProperty",
		"geolocated",
		"determination",
		// Annotation
		"object",
		"typeAnnotation",
		"xLocation",
		"yLocation",
		"areaHeight",
		"areaWidth",
		"areaRadius",
		"annotationQuality",
		"title",
		"comment",
		"XMLData",
		"annotationLabel",
		// Determination Annotation
		// TODO add Determination Annotation fields
		"taxon",
		"typeDetAnnotation",
		"sourceOfId",
		"materialsUsedInId",
		"resourcesused",
		"collection",
		"altTaxonName",

		// image
		"image", "height", "width",
		"resolution",
		"magnification",
		"imageType",
		"copyrightText",
		"originalFileName",
		"creativeCommons",
		"photographer",
		"specimen",
		"eol",
		// publication
		"publicationType", "annote", "author", "publicationTitle", "chapter",
		"edition", "editor", "howPublished", "institution", "key", "month",
		"day", "note", "number", "organization",
		"pages",
		"publisher",
		"school",
		"series",
		"volume",
		"year",
		"isbn",
		"issn",
		// specimen
		"standardImage", "imagesCount",
		"locality",
		"form",
		// Taxon
		"namespace",
		"taxonStatus",
		"taxonRank",
		// view
		"view", "viewAngle", "imagingTechnique", "imagingPreparationTechnique", "specimenPart",
		"developmentalStage", "sex", "viewTSN", "viewRestrictedTo",
		// user
		"uin", "pin", "userName", "email", "affiliation", "address",
		"lastName", "firstName", "suffix", "middleInit", "street1", "street2",
		"city", "country", "state", "zipcode", "userStatus", "privilegeTSN",
		"preferredServer", "preferredGroup", "userLogo", "logoUrl",
		// group
		"groupName", "groupManager", 
		// darwin core
		"any"
})
public class XmlBaseObject {

	public static final String DC_NAMESPACE = "http://rs.tdwg.org/dwc/dwcore/";
	public static final String DC_GEO_NAMESPACE = "http://rs.tdwg.org/dwc/geospatial/";
	public static final String DC_CUR_NAMESPACE = "http://rs.tdwg.org/dwc/curatorial/";

	static javax.xml.datatype.DatatypeFactory factory;

	static {
		try {
			factory = DatatypeFactoryImpl.newInstance();
		} catch (Exception e) {
			factory = null;
		}
	}

	public XmlBaseObject() {

	}

	public XmlBaseObject(String objectType) {
		this.objectTypeId = objectType;
		this.objectType = objectType;
	}

	@XmlAttribute(name = "type")
	String objectType;
	@XmlElement(required = true)
	protected XmlId sourceId;
	protected String status;
	protected Credentials owner;
	protected Date dateCreated;
	protected Date dateLastModified;
	protected Date dateToPublish;
	protected String objectTypeId;
	protected String name;
	protected String description;
	protected Credentials submittedBy;
	protected String objectLogo;
	protected String thumbUrl;
	protected String detailPageUrl;
	protected String hostServer;
	protected List<XmlId> relatedObject;
	protected List<Extref> externalRef;
	protected List<Userprop> userProperty;
	protected Boolean geolocated;
	protected XmlId determination;
	@XmlAnyElement(lax = true)
	protected List<Object> any;

	// Annotation
	protected XmlId object;
	protected String typeAnnotation;
	protected String xLocation;
	protected String yLocation;
	protected String areaHeight;
	protected String areaWidth;
	protected String areaRadius;
	protected String annotationQuality;
	protected String title;
	protected String comment;
	protected String XMLData;
	protected String annotationLabel;

	// Determination Annotation
	protected XmlId taxon;
	protected String typeDetAnnotation;
	protected String sourceOfId;
	protected String materialsUsedInId;
	protected String resourcesused;
	protected XmlId collection;
	protected String altTaxonName;

	// Image
	protected XmlId image;
	protected Integer height;
	protected Integer width;
	protected String resolution;
	protected Double magnification;
	protected String imageType;
	protected String copyrightText;
	protected String originalFileName;
	protected String creativeCommons;
	protected String photographer;
	protected String eol;

	// Publication
	protected String publicationType;
	// protected String address;
	protected String annote;
	protected String author;
	protected String publicationTitle;
	protected String chapter;
	protected String edition;
	protected String editor;
	protected String howPublished;
	protected String institution;
	protected String key;
	protected String month;
	protected Integer day;
	protected String note;
	protected String number;
	protected String organization;
	protected String pages;
	protected String publisher;
	protected String school;
	protected String series;
	// protected String title;
	protected String volume;
	protected String year;
	protected String isbn;
	protected String issn;

	// Specimen
	protected XmlId specimen;
	protected XmlId standardImage;
	protected Integer imagesCount;
	protected XmlId locality;
	protected String form;

	// Taxon "namespace", "taxonStatus"
	protected String namespace;
	protected String taxonStatus;
	protected String taxonRank;
	// View
	protected List<XmlId> view;
	protected String viewAngle;
	protected String imagingTechnique;
	protected String imagingPreparationTechnique;
	protected String specimenPart;
	protected String developmentalStage;
	protected String sex;
	protected Integer viewTSN;
	protected XmlId viewRestrictedTo;

	// user
	protected String uin;
	protected String pin;
	protected String userName;
	protected String email;
	protected String affiliation;
	protected String address;
	protected String lastName;
	protected String firstName;
	protected String suffix;
	protected String middleInit;
	protected String street1;
	protected String street2;
	protected String city;
	protected String country;
	protected String state;
	protected String zipcode;
	protected String userStatus;
	protected Integer privilegeTSN;
	protected String preferredServer;
	protected String preferredGroup;
	protected String userLogo;
	protected String logoUrl;
	// group
	protected String groupName;
	protected XmlId groupManager;

	// Methods for creating HTML presentation of properties

	public String getHtmlDesc(ObjectList list) {
		StringBuffer out = new StringBuffer();
		int id = getMorphbankId();
		if (id > 0) {
			out.append(getHtmlItem(getObjectTypeId() + " Id",
					Integer.toString(id)));
			out.append(getHtmlId(getSourceId(), ""));
			out.append(getHtmlImageFields(list));
			out.append(getHtmlViewFields(list));
			out.append(getHtmlSpecimenFields(list));
		}
		return out.toString();
	}

	public String getHtmlItem(String field, String value) {
		if (value == null || value.length() < 1 || "unknown".equals(value))
			return "";
		StringBuffer out = new StringBuffer();
		out.append("<b>").append(StringEscapeUtils.escapeHtml(field))
		.append(":</b> ").append(StringEscapeUtils.escapeHtml(value))
		.append("<br/>");
		return out.toString();
	}

	private Object getHtmlItem(String field, Integer value) {
		StringBuffer out = new StringBuffer();
		out.append("<b>").append(StringEscapeUtils.escapeHtml(field))
		.append(":</b> ").append(value).append("<br/>");
		return out.toString();
	}

	private Object getHtmlItem(String field, Double value) {
		StringBuffer out = new StringBuffer();
		out.append("<b>").append(StringEscapeUtils.escapeHtml(field))
		.append(":</b> ").append(value).append("<br/>");
		return out.toString();
	}

	public String getHtmlImageFields(ObjectList list) {
		StringBuffer out = new StringBuffer();
		if (!"Image".equals(getObjectTypeId())) {
			XmlId imageId = getStandardImage();
			out.append(getHtmlId(imageId, "Image"));
		}
		out.append(getHtmlItem("Image type", getImageType()));
		out.append(getHtmlItem("Copyright text", getCopyrightText()));
		XmlBaseObject xmlObj = list.getObject(getSpecimen());
		out.append(getHtmlItem("Height", getHeight()));
		out.append(getHtmlItem("Width", getWidth()));
		out.append(getHtmlItem("Resolution", getResolution()));
		out.append(getHtmlItem("Magnification", getMagnification()));
		out.append(getHtmlItem("Photographer", getPhotographer()));
		out.append(getHtmlItem("For Eol", getEol()));

		// <xs:element name="originalFileName" type="xs:string" minOccurs="0"/>
		// <xs:element name="creativeCommons" type="xs:string" minOccurs="0"/>
		return out.toString();
	}

	public String getHtmlSpecimenFields(ObjectList list) {
		StringBuffer out = new StringBuffer();
		if (!"Specimen".equals(getObjectTypeId())) {
			XmlId specimenId = getSpecimen();
			out.append(getHtmlId(specimenId, "Specimen"));
		}

		XmlId taxon = getDetermination();
		String taxonName = XmlTaxonNameUtilities.getScientificName(taxon);
		out.append(getHtmlItem("Taxon", taxonName));
		out.append(getHtmlItem("Sex", getFirstTagValue("Sex")));
		out.append(getHtmlItem("Form", getFirstTagValue("Form")));
		out.append(getHtmlItem("Developmental Stage",
				getFirstTagValue("LifeStage")));
		out.append(getHtmlItem("Type status", getFirstTagValue("TypeStatus")));
		out.append(getHtmlItem("Institution",
				getFirstTagValue("InstitutionCode")));
		out.append(getHtmlItem("Collection Code",
				getFirstTagValue("CollectionCode")));
		// out.append(getHtmlItem("Date Collected",
		// getFirstTagValue("EarliestDateCollected")));
		out.append(getHtmlItem("Locality", getFirstTagValue("Locality")));
		return out.toString();
	}

	public String getHtmlViewFields(ObjectList list) {
		StringBuffer out = new StringBuffer();
		if (!"View".equals(getObjectTypeId())) {
			List<XmlId> views = getView();
			if (views.size() > 0) {
				XmlId viewId = getView().get(0);
				out.append(getHtmlId(viewId, "View"));
			}
		}

		out.append(getHtmlItem("ViewAngle", getViewAngle()));
		out.append(getHtmlItem("Imaging Technique", getImagingTechnique()));
		out.append(getHtmlItem("Imaging Preparation",
				getImagingPreparationTechnique()));
		out.append(getHtmlItem("Specimen Part", getSpecimenPart()));
		out.append(getHtmlItem("Developmental Stage", getDevelopmentalStage()));

		// <xs:element name="viewTSN" type="xs:int" minOccurs="0"/>
		// <xs:element name="viewRestrictedTo" type="xmlId" minOccurs="0"/>

		return out.toString();
	}

	public String getHtmlId(XmlId xmlId, String label) {
		if (xmlId == null) return "";
		StringBuffer out = new StringBuffer();
		// morphbank id
		if (xmlId.getMorphbank() > 0) {
			out.append(getHtmlItem(label + " Id", xmlId.getMorphbank()));
		}
		Iterator<String> extIds = xmlId.getExternal().iterator();
		while (extIds.hasNext()) {
			String extId = extIds.next();
			out.append(getHtmlItem(label + " external id", extId));
		}
		// TODO local identification
		return out.toString();
	}

	// end of HTML properties methods

	public Boolean getGeolocated() {
		return geolocated;
	}

	public void setGeolocated(Boolean geolocated) {
		this.geolocated = geolocated;
	}

	public boolean hasId(XmlId id) {
		return getSourceId().matches(id);
	}

	public void setId(XmlId id) {
		setSourceId(id);
	}

	public XmlId getId() {
		return getSourceId();
	}

	public void addExternal(String id) {
		getSourceId().addExternal(id);
	}

	public void setMorphbankId(int id) {
		getSourceId().setMorphbank(new Integer(id));
	}

	public int getMorphbankId() {
		Integer morphbankId = getSourceId().getMorphbank();
		if (morphbankId == null) { return 0; }
		return morphbankId.intValue();
	}

	public void setLocalId(String id) {
		getSourceId().setLocal(id);
	}

	public String getLocalId() {
		return getSourceId().getLocal();
	}

	public XmlId getSourceId() {
		if (sourceId == null) {
			sourceId = new XmlId();
		}
		return sourceId;
	}

	public void setSourceId(XmlId sourceId) {
		this.sourceId = sourceId;
	}

	public Credentials getOwner() {
		return owner;
	}

	public void setOwner(Credentials owner) {
		this.owner = owner;
	}

	public void setObjectTypeId(String objectTypeId) {
		this.objectTypeId = objectTypeId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastModified() {
		return dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public Date getDateToPublish() {
		return dateToPublish;
	}

	public void setDateToPublish(Date dateToPublish) {
		this.dateToPublish = dateToPublish;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Credentials getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(Credentials submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getObjectLogo() {
		return objectLogo;
	}

	public void setObjectLogo(String objectLogo) {
		this.objectLogo = objectLogo;
	}

	public String getThumbUrl() {
		try {
			int id = Integer.parseInt(thumbUrl);
			this.thumbUrl = XmlUtils.getImageURL(id, "thumb");
		} catch (Exception e) {
		}
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public void setThumbUrl(int id) {
		// TODO check Xml classes for proper treatment of thumburl and
		// detailPageURL
		String url = XmlUtils.getImageURL(id, "thumb");
	}

	public String getDetailPageUrl() {
		return detailPageUrl;
	}

	public void setDetailPageUrl(String detailPageUrl) {
		this.detailPageUrl = detailPageUrl;
	}

	public List<XmlId> getRelatedObject() {
		if (relatedObject == null) {
			relatedObject = new ArrayList<XmlId>();
		}
		return relatedObject;
	}

	public void addRelatedObject(int morphbankId, String extId) {
		XmlId xmlId = new XmlId();
		xmlId.addExternal(extId);
		xmlId.setMorphbank(morphbankId);
		addRelatedObject(xmlId);
	}

	public void addRelatedObjectExt(String extId) {
		XmlId xmlId = new XmlId();
		xmlId.addExternal(extId);
		addRelatedObject(xmlId);
	}

	public void addRelatedObjectMB(int id) {
		XmlId xmlId = new XmlId();
		xmlId.setMorphbank(id);
		addRelatedObject(xmlId);
	}

	public void addRelatedObject(XmlId id) {
		if (id != null) {
			getRelatedObject().add(id);
		}
	}

	public List<Extref> getExternalRef() {
		if (externalRef == null) {
			externalRef = new ArrayList<Extref>();
		}
		return externalRef;
	}

	public void addExternalRef(Extref extref) {
		getExternalRef().add(extref);
	}

	public void addUserProperty(String name, String value) {
		if (value == null || value.length() == 0) { return; }
		Userprop userProp = new Userprop(name, value);
		getUserProperty().add(userProp);
	}

	public void addUserProperty(String name, String value, String namespaceURI) {
		if (value == null || value.length() == 0) { return; }
		Userprop userProp = new Userprop(name, value, namespaceURI);
		getUserProperty().add(userProp);
	}

	public List<Userprop> getUserProperty() {
		if (userProperty == null) {
			userProperty = new ArrayList<Userprop>();
		}
		return userProperty;
	}

	/**
	 * add status message to the end of the status field
	 * @param status
	 */
	public void addStatus(String status) {
		if (this.status == null) {
			this.status = status;
		} else {
			this.status = this.status + "\n" + status;
		}
	}

	public String getStatus() {
		return status;
	}

	/**
	 * Add new status message to the front of the status field
	 * @param status
	 */
	public void setStatus(String status) {
		if (this.status == null) {
			this.status = status;
		} else {
			this.status = status + "\n" + this.status;
		}
	}

	public void addDescription(String desc) {
		if (this.description == null) {
			description = desc;
		} else {
			description = description + " " + desc;
		}
	}

	public String getObjectTypeId() {
		if (objectTypeId != null) return objectTypeId;
		if (objectType != null) return objectType;
		return "";
	}

	/**
	 * Add the Any element to the specimen, unless the value is null or an empty
	 * string
	 * 
	 * @param element
	 */
	public void addDarwinTag(JAXBElement element) {
		if (element == null) return;
		Object value = element.getValue();
		if (value == null) return;
		if (value instanceof String) {
			if (((String) value).length() == 0) return;
		}
		getAny().add(element);
	}

	/**
	 * Method to add a Darwin core field of type string to the xsi:any tag of
	 * the specimen
	 * 
	 * @param namespace
	 * @param tagName
	 * @param value
	 */
	public void addDarwinTag(QName tag, String value) {
		// TODO resolve namespace for various fields
		// strategy for adding a field that represents a Darwin Core attribute
		if (value != null && value.length() > 0) {
			JAXBElement<String> node = new JAXBElement<String>(tag,
					String.class, value);
			getAny().add(node);
		}
	}

	/**
	 * Method to add a Darwin core field of type double to the xsi:any tag of
	 * the specimen
	 * 
	 * @param namespace
	 * @param tagName
	 * @param value
	 */
	public void addDarwinTag(QName tag, Double value) {
		// TODO resolve namespace for various fields
		// strategy for adding a field that represents a Darwin Core attribute
		if (value != null) {
			JAXBElement<Double> node = new JAXBElement<Double>(tag,
					Double.class, value);
			getAny().add(node);
		}
	}

	static DateFormat DC_DATE = DateFormat.getDateInstance();

	/**
	 * Method to add a Darwin core field of type Date to the xsi:any tag of the
	 * specimen
	 * 
	 * @param namespace
	 * @param tagName
	 * @param value
	 */
	public void addDarwinGregorianCalendarTag(QName tag, Date value) {

		// TODO resolve namespace for various fields
		// strategy for adding a field that represents a Darwin Core attribute
		if (value != null) {
			String dateString = DC_DATE.format(value);
			System.out.println("earliest date: " + dateString);
			GregorianCalendar dateCal = new GregorianCalendar();
			dateCal.setTime(value);
			XMLGregorianCalendar date = factory
			.newXMLGregorianCalendar(dateCal);
			JAXBElement<XMLGregorianCalendar> node = new JAXBElement<XMLGregorianCalendar>(
					tag, XMLGregorianCalendar.class, date);
			getAny().add(node);
		}
	}

	public JAXBElement getFirstTag(String tagName) {
		Iterator<Object> tags = getAny().iterator();
		while (tags.hasNext()) {
			JAXBElement obj = (JAXBElement) tags.next();
			QName qname = obj.getName();
			String localPart = qname.getLocalPart();
			if (localPart.equals(tagName)) { return obj; }
		}
		return null;
	}

	public String getFirstTagValue(String tagName) {
		JAXBElement obj = getFirstTag(tagName);
		if (obj != null) {
			return obj.getValue().toString();

		} else {
			return null;
		}
	}

	public Date getFirstTagDateValue(String tagName) {
		Iterator<Object> tags = getAny().iterator();
		while (tags.hasNext()) {
			JAXBElement obj = (JAXBElement) tags.next();
			QName qname = obj.getName();
			if (qname.getLocalPart().equals(tagName)) {
				Object value = obj.getValue();
				if (value instanceof Date) { return (Date) value; }
			}
		}
		return null;
	}

	public List<Object> getAny() {
		if (any == null) {
			any = new ArrayList<Object>();
		}
		return this.any;
	}

	public XmlId getDetermination() {
		return determination;
	}

	public void setDetermination(XmlId determination) {
		this.determination = determination;
	}

	public XmlId getObject() {
		return object;
	}

	public void setObject(int id) {
		if (object == null) {
			object = new XmlId();
		}
		object.setMorphbank(new Integer(id));
	}

	public void setObject(XmlId object) {
		this.object = object;
	}

	public String getTypeAnnotation() {
		return typeAnnotation;
	}

	public void setTypeAnnotation(String typeAnnotation) {
		this.typeAnnotation = typeAnnotation;
	}

	public String getXLocation() {
		return xLocation;
	}

	public void setXLocation(String xLocation) {
		this.xLocation = xLocation;
	}

	public String getYLocation() {
		return yLocation;
	}

	public void setYLocation(String yLocation) {
		this.yLocation = yLocation;
	}

	public String getAreaHeight() {
		return areaHeight;
	}

	public void setAreaHeight(String areaHeight) {
		this.areaHeight = areaHeight;
	}

	public String getAreaWidth() {
		return areaWidth;
	}

	public void setAreaWidth(String areaWidth) {
		this.areaWidth = areaWidth;
	}

	public String getAreaRadius() {
		return areaRadius;
	}

	public void setAreaRadius(String areaRadius) {
		this.areaRadius = areaRadius;
	}

	public String getAnnotationQuality() {
		return annotationQuality;
	}

	public void setAnnotationQuality(String annotationQuality) {
		this.annotationQuality = annotationQuality;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getXMLData() {
		return XMLData;
	}

	public void setXMLData(String xMLData) {
		XMLData = xMLData;
	}

	public String getAnnotationLabel() {
		return annotationLabel;
	}

	public void setAnnotationLabel(String annotationLabel) {
		this.annotationLabel = annotationLabel;
	}

	public void setExternalRef(List<Extref> externalRef) {
		this.externalRef = externalRef;
	}

	public XmlId getSpecimen() {
		return specimen;
	}

	public void setSpecimen(XmlId specimen) {
		this.specimen = specimen;
	}

	public List<XmlId> getView() {
		if (view == null || view.get(0) == null) {
			view = new ArrayList<XmlId>();
		}
		return this.view;
	}

	public void setView(List<XmlId> view) {
		this.view = view;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public Double getMagnification() {
		return magnification;
	}

	public void setMagnification(Double magnification) {
		this.magnification = magnification;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getCopyrightText() {
		return copyrightText;
	}

	public void setCopyrightText(String copyrightText) {
		this.copyrightText = copyrightText;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getCreativeCommons() {
		return creativeCommons;
	}

	public void setCreativeCommons(String creativeCommons) {
		this.creativeCommons = creativeCommons;
	}

	public String getPhotographer() {
		return photographer;
	}

	public void setPhotographer(String photographer) {
		this.photographer = photographer;
	}

	public void setSpecimenId(int id) {
		if (specimen == null) {
			specimen = new XmlId();
		}
		specimen.setMorphbank(new Integer(id));
	}

	public void setViewId(int id) {
		if (getView().size() == 0) {
			view.add(new XmlId());
		}
		getView().get(0).setMorphbank(new Integer(id));
	}

	public void addView(XmlId viewId) {
		getView().add(viewId);
	}

	public void addViews(List<XmlId> viewIdList) {
		Iterator<XmlId> viewIds = viewIdList.iterator();
		while (viewIds.hasNext()) {
			getView().add(viewIds.next());
		}
	}

	public boolean setViewId(int index, int id) {
		if (getView().size() < index + 1) { return false; }
		getView().get(index).setMorphbank(new Integer(id));
		return true;
	}

	public XmlId getStandardImage() {
		return standardImage;
	}

	public void setStandardImage(XmlId standardImage) {
		this.standardImage = standardImage;
	}

	public void setEol(String value) {
		this.eol = value;
	}

	public String getEol() {
		return eol;
	}

	public Integer getImagesCount() {
		return imagesCount;
	}

	public void setImagesCount(Integer imagesCount) {
		this.imagesCount = imagesCount;
	}

	public XmlId getLocality() {
		return locality;
	}

	public void setLocality(XmlId locality) {
		this.locality = locality;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getViewAngle() {
		return viewAngle;
	}

	public void setViewAngle(String viewAngle) {
		this.viewAngle = viewAngle;
	}

	public String getImagingTechnique() {
		return imagingTechnique;
	}

	public void setImagingTechnique(String imagingTechnique) {
		this.imagingTechnique = imagingTechnique;
	}

	public String getImagingPreparationTechnique() {
		return imagingPreparationTechnique;
	}

	public void setImagingPreparationTechnique(
			String imagingPreparationTechnique) {
		this.imagingPreparationTechnique = imagingPreparationTechnique;
	}

	public String getSpecimenPart() {
		return specimenPart;
	}

	public void setSpecimenPart(String specimenPart) {
		this.specimenPart = specimenPart;
	}

	public String getDevelopmentalStage() {
		return developmentalStage;
	}

	public void setDevelopmentalStage(String developmentalStage) {
		this.developmentalStage = developmentalStage;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getViewTSN() {
		return viewTSN;
	}

	public void setViewTSN(Integer viewTSN) {
		this.viewTSN = viewTSN;
	}

	public XmlId getViewRestrictedTo() {
		return viewRestrictedTo;
	}

	public void setViewRestrictedTo(XmlId viewRestrictedTo) {
		this.viewRestrictedTo = viewRestrictedTo;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getxLocation() {
		return xLocation;
	}

	public void setxLocation(String xLocation) {
		this.xLocation = xLocation;
	}

	public String getyLocation() {
		return yLocation;
	}

	public void setyLocation(String yLocation) {
		this.yLocation = yLocation;
	}

	public XmlId getImage() {
		return image;
	}

	public void setImage(XmlId image) {
		this.image = image;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getMiddleInit() {
		return middleInit;
	}

	public void setMiddleInit(String middleInit) {
		this.middleInit = middleInit;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPreferredServer() {
		return preferredServer;
	}

	public void setPreferredServer(String preferredServer) {
		this.preferredServer = preferredServer;
	}

	public String getPreferredGroup() {
		return preferredGroup;
	}

	public void setPreferredGroup(String preferredGroup) {
		this.preferredGroup = preferredGroup;
	}

	public String getUserLogo() {
		return userLogo;
	}

	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getPrivilegeTSN() {
		return privilegeTSN;
	}

	public void setPrivilegeTSN(Integer privilegeTSN) {
		this.privilegeTSN = privilegeTSN;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
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

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

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

	public String getAnnote() {
		return annote;
	}

	public void setAnnote(String annote) {
		this.annote = annote;
	}

	public XmlId getTaxon() {
		return taxon;
	}

	public void setTaxon(XmlId taxon) {
		this.taxon = taxon;
	}

	public String getTypeDetAnnotation() {
		return typeDetAnnotation;
	}

	public void setTypeDetAnnotation(String typeDetAnnotation) {
		this.typeDetAnnotation = typeDetAnnotation;
	}

	public String getSourceOfId() {
		return sourceOfId;
	}

	public void setSourceOfId(String sourceOfId) {
		this.sourceOfId = sourceOfId;
	}

	public String getMaterialsUsedInId() {
		return materialsUsedInId;
	}

	public void setMaterialsUsedInId(String materialsUsedInId) {
		this.materialsUsedInId = materialsUsedInId;
	}

	public String getResourcesused() {
		return resourcesused;
	}

	public void setResourcesused(String resourcesused) {
		this.resourcesused = resourcesused;
	}

	public XmlId getCollection() {
		return collection;
	}

	public void setCollection(XmlId collection) {
		this.collection = collection;
	}

	public String getAltTaxonName() {
		return altTaxonName;
	}

	public void setAltTaxonName(String altTaxonName) {
		this.altTaxonName = altTaxonName;
	}

	public void setRelatedObject(List<XmlId> relatedObject) {
		this.relatedObject = relatedObject;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getTaxonStatus() {
		return taxonStatus;
	}

	public void setTaxonStatus(String taxonStatus) {
		this.taxonStatus = taxonStatus;
	}

	public String getTaxonRank() {
		return taxonRank;
	}

	public void setTaxonRank(String taxonRank) {
		this.taxonRank = taxonRank;
	}

	public String getHostServer() {
		return hostServer;
	}

	public void setHostServer(String hostServer) {
		this.hostServer = hostServer;
	}

	public XmlId getGroupManager() {
		return groupManager;
	}

	public void setGroupManager(XmlId groupManager) {
		this.groupManager = groupManager;
	}

}
