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
package net.morphbank.mbsvc3.morphster.rdf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MorphsterAnnotation {

	List<Annotation> annotations;
	//List<HasPart> hasParts;
	List<CharacterAbstract> characters = new ArrayList<CharacterAbstract>();
	List<AnatomicalEntity> anatomical = new ArrayList<AnatomicalEntity>();
	List<PhenotypicQuality> phenotypic = new ArrayList<PhenotypicQuality>();


	public MorphsterAnnotation(RDF rdf) {
		super();
		this.annotations = rdf.getAnnotation();
		this.characters.addAll(rdf.getCharacter());
		this.characters.addAll(rdf.getCharacterState());
		this.annotations = this.cleanDuplicates();
		this.anatomical = rdf.getAnatomicalEntity();
		this.phenotypic = rdf.getPhenotypicQuality();
	}



	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(ArrayList<Annotation> annotations) {
		this.annotations = annotations;
	}

	public List<CharacterAbstract> getCharacters() {
		return characters;
	}

	public void setCharacters(ArrayList<CharacterAbstract> characters) {
		this.characters = characters;
	}

	//TODO fix this cleaning process
	private List<Annotation> cleanDuplicates() {
		Iterator<Annotation> itAnn = this.getAnnotations().iterator();
		Iterator<Annotation> itAnn2 = this.getAnnotations().iterator();
		AnnotatedOn annOn;
		Annotation annotation;
		Annotation annotation2;
		
		Hashtable<AnnotatedOn, Annotation> map = new Hashtable<AnnotatedOn, Annotation>();
		
		while (itAnn.hasNext()) {
			annotation = itAnn.next();

			if (!map.containsKey(annotation.getAnnotatedOn())) {//entry does not exist
				map.put(annotation.getAnnotatedOn(), annotation);
			}
			else { // entry exist -> copy all HasPart from the duplicate
				Annotation toInsert = this.transfertHasParts(annotation, map.get(annotation.getAnnotatedOn()));
				map.remove(annotation.getAnnotatedOn());
				map.put(toInsert.getAnnotatedOn(), toInsert);
			}
		}
		return new ArrayList<Annotation>(map.values());
		
		
		
	
	}

	
	private Annotation transfertHasParts(Annotation annotationToCopy, Annotation annotationToUpdate) {
		//TODO filter duplicates
		annotationToUpdate.getHasPart().addAll(annotationToCopy.getHasPart());
		return annotationToUpdate;
		
		
	}
	

	public CharacterAbstract getCharacterbyID(String id) {
		Iterator<CharacterAbstract> it = characters.iterator();
		String idTrim = id;
		if (id.charAt(0) == '#') { //local name starts with #
			idTrim = id.replaceFirst("#", "");
			//it = characters.iterator();
		}
		
		while (it.hasNext()) {
			CharacterAbstract ch = it.next();
			String chstId = ch.getID();
			if (chstId.equalsIgnoreCase(idTrim)){
				return ch;
			}
		}

		return null;
	}

	public AnatomicalEntity getAnatomicalById(String id) {
		Iterator<AnatomicalEntity> it = anatomical.iterator();
		String idTrim = id;
		if (id.charAt(0) == '#') { //local name starts with #
			idTrim = id.replaceFirst("#", "");
		}
		
		while (it.hasNext()) {
			AnatomicalEntity ae = it.next();
			String chstId = ae.getAbout();
			if (chstId.equalsIgnoreCase(idTrim)){
				return ae;
			}
		}
		return null;
	}
	
	public PhenotypicQuality getPhenotypicById(String id) {
		Iterator<PhenotypicQuality> it = phenotypic.iterator();
		String idTrim = id;
		if (id.charAt(0) == '#') { //local name starts with #
			idTrim = id.replaceFirst("#", "");
		}
		
		while (it.hasNext()) {
			PhenotypicQuality ae = it.next();
			String chstId = ae.getAbout();
			if (chstId.equalsIgnoreCase(idTrim)){
				return ae;
			}
		}
		return null;
	}
}
