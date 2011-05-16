/**
 * 
 */
package net.morphbank.mbsvc3.request;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;

/**
 * @author riccardi
 * 
 *         Main class for Simple XML Search services for Morphbank
 */
public class RequestServlet extends HttpServlet {
	static final long serialVersionUID = 1;

	boolean isChangeSearchPublic = true;

	/**
	 * 
	 */
	public RequestServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		RequestParams.initService(config);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		RequestProcessor requestProcessor = new RequestProcessor(isChangeSearchPublic);
		try {
			requestProcessor.doGet(req, resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MorphbankConfig.SYSTEM_LOGGER.info("IO Error in doGet: " + e.toString());
		}
		// MorphbankConfig.getEntityManager().close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		RequestProcessor requestProcessor = new RequestProcessor(isChangeSearchPublic);
		try {
			requestProcessor.doPost(req, resp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MorphbankConfig.SYSTEM_LOGGER.info("IO Error in doPost: " + e.toString());
		}
	}
}
