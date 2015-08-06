package org.idiginfo.ipt.acterms;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.idiginfo.ipt.extension.Extension.Property;

public class ACTerm {

	String itemURI; // ItemURI = {{NS2 dc | type}}
	String label; // Label = Type
	String termName; // TermName = dc:type
	String definedBy; // Defined by = {{NS dcDoc | type}}
	String layer; // Layer = 1
	String repeatable; // Repeatable = No
	Boolean required; // Required = Yes
	String definition; // Definition = dc:type may take as value any type term
	String notes; // Notes = A Collection should be given type "Collection"
	String hiddenNotes; // Hidden Notes = Crosswalk: NBII = Type; KeyToNature
	String group;
	String name;

	static final String URI_REGEX = "\\{\\{NS2\\s+(\\S*)\\s*\\|\\s*(\\S*)\\s*\\}\\}";
	static final Pattern uriPattern = Pattern.compile(URI_REGEX);

	public ACTerm(Map<String, String> map, String group) {
		itemURI = map.get("ItemURI");
		label = map.get("Label");
		termName = map.get("TermName");
		definedBy = map.get("Defined by");
		layer = map.get("Layer");
		repeatable = map.get("Repeatable");
		required = "Yes".equals(map.get("Required"));
		definition = map.get("Definition");
		notes = map.get("Notes");
		this.group = group;
		name = getSimpleName();

		hiddenNotes = map.get("Hidden Notes");
	}

	public String getName() {
		return name;
	}

	public ACTerm() {
		// empty term
	}

	public String getSimpleName() {
		if (itemURI == null)
			return null;
		Matcher uri = uriPattern.matcher(itemURI);
		if (uri.matches()) {
			return uri.group(2);
		}
		// the itemURI does not match
		return itemURI;
	}

	public String getNamespaceSymbol() {
		if (itemURI == null) {
			System.out.println("empty itemURI symbol");
		}
		Matcher uri = uriPattern.matcher(itemURI);
		if (uri.matches()) {
			String namespace = uri.group(1);
			if (namespace.equals("acterms"))
				namespace = "ac";
			return namespace;
		}
		return "";
	}

	public String getNamespace() {
		if (itemURI == null) {
			System.out.println("empty itemURI namespace");
		}
		String symbol = getNamespaceSymbol();
		String namespace = Namespaces.NS_MAP.get(symbol);
		if (namespace == null || namespace.length() == 0) {
			System.out.println("*****namespace missing:" + symbol);
		}
		return namespace;
	}

	public String getUri() {
		String ns = getNamespace();
		String term = getSimpleName();
		return ns + term;
		// return null;
	}

	public String getSimpleUri() {
		String ns = getNamespaceSymbol();
		String term = getSimpleName();
		return ns + ":" + term;
	}

	public Property createExtensionProperty(String group) {
		this.group = group;
		return createExtensionProperty();
	}

	public Property createExtensionProperty() {
		Property property = new Property();
		property.setName(getName());
		property.setNamespace(getNamespace());
		// property.setRelation(Namespaces.AC_BASE + getSimpleName());
		// property.setQualName(getUri());
		// property.setRequired(required);
		// property.setGroup(group);
		// property.setDescription(definition);
		// property.setDescription(notes);
		return property;
	}

	public String getItemURI() {
		return itemURI;
	}

	public void setItemURI(String itemURI) {
		this.itemURI = itemURI;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getDefinedBy() {
		return definedBy;
	}

	public void setDefinedBy(String definedBy) {
		this.definedBy = definedBy;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(String repeatable) {
		this.repeatable = repeatable;
	}

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getHiddenNotes() {
		return hiddenNotes;
	}

	public void setHiddenNotes(String hiddenNotes) {
		this.hiddenNotes = hiddenNotes;
	}

	public void setName(String name) {
		this.name = name;

	}

}

// {{Audubon Core TermNoComments2
// | ItemURI = {{NS2 dc | type}}
// | Label = Type
// | TermName = dc:type
// | Defined by = {{NS dcDoc | type}}
// | Layer = 1
// | Repeatable = No
// | Required = Yes
// | Definition = dc:type may take as value any type term from the
// [http://dublincore.org/documents/dcmi-type-vocabulary/#H7 DCMI Type
// Vocabulary]. Recommended terms are Collection, StillImage, Sound,
// MovingImage, InteractiveResource, Text. Values may be used either in their
// literal form, or with a full namespace (e. g.
// <nowiki>http://purl.org/dc/dcmitype/StillImage)</nowiki> from a controlled
// vocabulary, but the best practice is to use the literal form when using
// dc:type and use dcterms:type when you can supply the URI from a controlled
// vocabulary and implementers may require this practice. At least one of
// dc:type and dcterms:type must be supplied but, when feasible, supplying both
// may make the metadata more widely useful. The values of each should designate
// the same type, but in case of ambiguity dcterms:type prevails.
// | Notes = A Collection should be given type "Collection" when using dc:type.
// If the resource is a Collection, this item does ''not'' identify what types
// of objects it may contain. Following the DC recommendations for
// [http://purl.org/dc/dcmitype/Text the Text type], images of text should be
// marked given as the string Text when provided as a string. {{DCMI_See_Also |
// dcterms:type}}.
// | Hidden Notes = Crosswalk: NBII = Type; KeyToNature = {{NS k2n | Type}} pro
// parte; DarwinCore = {{NS ncd | CollectionType }}; DublinCore = {{NS dcterms |
// type}}
// }}