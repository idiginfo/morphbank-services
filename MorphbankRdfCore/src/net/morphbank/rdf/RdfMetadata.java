/**
 * 
 */
package net.morphbank.rdf;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * This interface provides methods used in generating RDF metadata for objects.
 * 
 * @author riccardi
 * 
 */
public interface RdfMetadata {
	/**
	 * 
	 * @return the local id of the object
	 */
	public int getId();

	/**
	 * Create a new resource to hold the RDF of the object. Initialize the
	 * resource with its about=*LSID* and its rdf:type
	 * 
	 * @param model
	 *            resource is added to the model
	 * @return the new resource
	 */
	public Resource createResource();

	/**
	 * Create a new resource, as with createResource, and add its basic
	 * properties.
	 * 
	 * @param model
	 *            resource and properties are added to the model
	 * @return the new resource
	 */
	public Resource toSimpleRDF();

	/**
	 * Create a new resource, as with createResource, and add its properties
	 * including related objects and their properties.
	 * 
	 * @param model
	 *            resource and properties are added to the model
	 * @param depth,
	 *            number of levels of detail to include
	 * @return the new resource
	 */
	public Resource toDetailRDF(int depth);

	/**
	 * Add basic RDF properties of the object to the resource
	 * 
	 * @param res,
	 *            properties are added to the resource
	 */
	public void addBasicProperties(Resource res);

	public void addReferenceProperties(Resource res);

	public void addObjects(Resource res, int depth);

	/**
	 * Add RDF properties of the object to the resource, including related
	 * objects and their properties
	 * 
	 * @param res,
	 *            properties are added to the resourc
	 * @param depth,
	 *            number of levels of detail to include
	 */
	public void addDetailProperties(Resource res, int depth);

}
