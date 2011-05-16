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

import javax.persistence.*;

import net.morphbank.MorphbankConfig;

@Entity
@Table(name = "BasisOfRecord")
public class BasisOfRecord {
	@Id
	String name;
	String description;
	
	public static BasisOfRecord lookupSymbol(String name){
		EntityManager em = MorphbankConfig.getEntityManager();
		BasisOfRecord basis = (BasisOfRecord) em.find(BasisOfRecord.class, name);
		return basis;
	}
	public static BasisOfRecord lookupDescription(String desc){
		EntityManager em = MorphbankConfig.getEntityManager();
		Query query = em.createQuery("select b from BasisOfRecord b where b.description=:desc");
		query.setParameter("desc", desc);
		BasisOfRecord basis = (BasisOfRecord) query.getSingleResult();
		return basis;
	}
	
	public BasisOfRecord(){
		
	}
	
	public BasisOfRecord(String name){
		setName(name);
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
