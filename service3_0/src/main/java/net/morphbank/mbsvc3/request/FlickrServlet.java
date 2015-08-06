/**
 * 
 */
package net.morphbank.mbsvc3.request;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.flickr.FlickrRequestProcessor;


/**
 * @author riccardi
 * 
 * Main class for Simple XML Search services for Morphbank
 */
public class FlickrServlet extends HttpServlet {
	static final long serialVersionUID = 1;

	static final String CONFIG = MorphbankConfig.PERSISTENCE_MBDEV;
	static final String CONFIG_PARAM = "persistence";
	static final String SERVICE = MorphbankConfig.SERVICES;
	static final String SERVICE_PARAM = "serviceprefix";

	/**
	 * 
	 */
	public FlickrServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// setup persistence unit from parameter, if available
		String persistence = config.getInitParameter(CONFIG_PARAM);
		if (persistence != null) {
			MorphbankConfig.setPersistenceUnit(persistence);
		} else {
			MorphbankConfig.setPersistenceUnit(CONFIG);
		}
		String servicePrefix = config.getInitParameter(SERVICE_PARAM);
		if (servicePrefix!=null){
			MorphbankConfig.setServicePrefix(servicePrefix);
		} else {
			MorphbankConfig.setServicePrefix(SERVICE);
		}

		MorphbankConfig.renewEntityManagerFactory();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		FlickrRequestProcessor requestProcessor = new FlickrRequestProcessor(req);
		requestProcessor.doGet(resp);
		//MorphbankConfig.getEntityManager().close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		FlickrRequestProcessor requestProcessor = new FlickrRequestProcessor(req);
		requestProcessor.doPost(resp);
	}
}
