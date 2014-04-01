package net.morphbank.mbsvc3.mapdwca;

import net.morphbank.mbsvc3.maptoxml.Field;
import net.morphbank.mbsvc3.xml.XmlBaseObject;

public class DwcaField extends Field{

	public DwcaField(String nameSpace, String fieldName, String value) {
		super(nameSpace, fieldName, value);
		
	}

	@Override
	public boolean setXmlValue(XmlBaseObject xmlObject) {
		// TODO Auto-generated method stub
		return false;
	}

	
	//look for xml field, user property.

}
