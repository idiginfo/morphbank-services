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

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;


public class MbDescriptorCustomizer implements DescriptorCustomizer {
	static final String QUERYNAME  = "objectTypeId"; // name for field in query
	static final String DATABASENAME  = "objectTypeId"; // name for field in database

	public void customize(ClassDescriptor descriptor) throws Exception {
		descriptor.addDirectQueryKey(QUERYNAME , DATABASENAME );
		System.out.println("customizer("+QUERYNAME +","+DATABASENAME +")");
	}
}
