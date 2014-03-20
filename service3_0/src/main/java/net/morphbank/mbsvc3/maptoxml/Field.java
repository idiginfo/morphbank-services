package net.morphbank.mbsvc3.maptoxml;

import net.morphbank.mbsvc3.xml.XmlBaseObject;

public abstract class Field {
	
	String nameSpace;
	String fieldName;
	String value;

	public abstract boolean setXmlValue(XmlBaseObject xmlObject);
	
	public boolean addUserProperty(XmlBaseObject xmlObject){
		xmlObject.addUserProperty(fieldName, value, nameSpace);
		return true;
	}

}
