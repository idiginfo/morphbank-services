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
package net.morphbank.mbsvc3.mapping;

import net.morphbank.mbsvc3.xml.XmlBaseObject;
import net.morphbank.mbsvc3.xml.XmlId;
import net.morphbank.object.IdObject;

public interface MapObject {

	public IdObject createObject(XmlBaseObject xmlObject, XmlId xmlId);

	public IdObject createObject(XmlBaseObject xmlObject);

	public XmlBaseObject createXmlObject(IdObject idObj);

	public XmlBaseObject createXmlObject(IdObject idObject, String localId);
	
	public IdObject createOrUpdateObject(IdObject idObject, XmlBaseObject xmlObject);

	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject);

	public boolean updateObject(IdObject idObject, XmlBaseObject xmlObject, XmlId xmlId);

	public boolean updateXml(XmlBaseObject xmlObject, IdObject idObject);

	public boolean linkObject(XmlBaseObject xmlObject, IdObject baseObject, XmlBaseObject responseObject);

	boolean setXmlFields(XmlBaseObject xmlObject, IdObject idObject);

}
