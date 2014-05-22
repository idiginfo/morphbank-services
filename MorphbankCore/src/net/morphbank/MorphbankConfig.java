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
package net.morphbank;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 * Configuration parameters and standard objects for the data services library
 * 
 * Includes configuration of database and its EJB persistence configurations
 * LSID authority LSID namespaces
 * 
 * This class should be modified to use a Java properties file for the
 * configuration parameters
 * 
 * @author riccardi
 */
public class MorphbankConfig {

	protected static EntityManagerContainer entityManagerContainer = null;

	protected static ThreadLocal<EntityManager> entityManager = new ThreadLocal<EntityManager>();
	// protected static ThreadLocal<EntityManagerContainer>
	// entityManagerContainer = new ThreadLocal<EntityManagerContainer>();
	protected static EntityManagerFactory emf;

	public static final String PERSISTENCE_LOCALHOST = "localhost";
	public static final String PERSISTENCE_LOCALHOST_2 = "localhost2";
	public static final String PERSISTENCE_MBTEST = "morphbank11";
	public static final String PERSISTENCE_MBPROD = "morphbank";
	public static final String PERSISTENCE_MBDEV = "morphbank11";
	public static final String PERSISTENCE_ALA = "ala";
	protected static String persistenceUnit = PERSISTENCE_LOCALHOST;

	// constants used to create GUIDs for the Spider ATOL objects
	public static final String ID_PREFIX = "SPD";
	public static final String SPECIMEN_PREFIX = ID_PREFIX + "-S:";
	public static final String IMAGE_PREFIX = ID_PREFIX + "-I:";
	public static final String VIEW_PREFIX = ID_PREFIX + "-SV:";
	public static final String SMALLER_REGION_PREFIX = ID_PREFIX + ":";
	public static final String TAXON_PREFIX = "SCI-NAME:";
	public static final String SCI_NAME_PREFIX = "SCI-NAME:";
	public static final String SCI_NAME_AUTHOR_PREFIX = "SCI-NAME-AUTHOR:";
	public static final String SCI_NAME_AUTHOR_SEPARATOR = "|";
	public static final String SCI_NAME_AUTHOR_SPLIT = "\\|";

	// Constants for XML and RDF processing
	public static String RDF_SCHEMA_SERVER = "http://www.morphbank.net/schema/";
	public static String SERVICES = "http://services.morphbank.net/mb/request?";
	public static String servicePrefix = SERVICES;
	public static String DARWIN_CORE_SCHEMA_URL = RDF_SCHEMA_SERVER
			+ "darwin_2005_2.0.rdfs";
	public static String MORPHBANK_SCHEMA_URL = RDF_SCHEMA_SERVER
			+ "schema/rdf-schema.n3";
	// public static String DARWIN_CORE_SCHEMA_URI =
	// "http://digir2.ecoforge.net/rdf-schema/darwin/2005/2.0#";
	// public static String DARWIN_CORE_SCHEMA_URI =
	// "http://rs.tdwg.org/dwc/terms/";
	public static String MORPHBANK_SCHEMA_URI = RDF_SCHEMA_SERVER
			+ "rdf-schema#";
	// constants
	// public static String DARWIN_URI =
	// "http://digir2.ecoforge.net/rdf-schema/darwin/2005/2.0#";
	public static String DARWIN_URI = "http://rs.tdwg.org/dwc/terms/";
	public static String DUBLIN_CORE_URI = "http://purl.org/dc/terms/";
	public static String FOAF_URI = "http://xmlns.com/foaf/spec/";
	public static String MRTG_URI = "http://xmlns.com/mrtg/spec/";

	// public static String WEB_SERVER = "http://www.morphbank.net/";
	public static String WEB_SERVER = "http://www.morphbank.net/";
	public static String MORPHBANK_ID_SERVER = WEB_SERVER + "?id=";
	public static String IMAGE_SERVER = "http://images.morphbank.net/";
	// public static String REMOTE_SERVER =
	// "http://services.morphbank.net/mb3/";
	public static String REMOTE_SERVER = "http://localhost:8080/mbd/";
	public static String MB_CHANGES_REQUEST = "/request?method=changes&format=id&numChangeDays=";
	public static String MB_DETAILS_REQUEST = "/request?method=id&format=xml&id=";

	public static Proxy PROXY = Proxy.NO_PROXY;
	public final static String FILEPATH = "xmlfiles/";
	public static String filepath = FILEPATH;

	public static final String DCTERMS_IDENTIFIER = "dcterms:identifier";

	// logging objects
	private static final String SYSTEM_LOGGER_NAME = "ServicesLogger";
	public static final Logger SYSTEM_LOGGER = Logger
			.getLogger(SYSTEM_LOGGER_NAME);
	public static String SYSTEM_LOG_FILE_NAME = "/data/log/tomcat6/service.log";

	static {
		System.setProperty("file.encoding", "UTF-8");
	}

	public static void createLogHandler(String logname) {
		try {
			String logFileName = logname;
			if (logname == null || logname.length() == 0) {
				logFileName = SYSTEM_LOG_FILE_NAME;
			}
			SYSTEM_LOGGER.addHandler(new FileHandler(logFileName, true));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot create logger file");
		}
	}

	public static void init() {
		try {
			getEntityManager();
			// renewEntityManager(); // Retrieve an application managed
			// entity manager
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static EntityManager getEntityManager() {
		// Use the Spring PersistenceContext to get the entity manager
		if (entityManagerContainer == null) {
			entityManagerContainer = new EntityManagerContainer();
		}
		return entityManagerContainer.getEntityManager();
	}

	public static void closeEntityManager() {
		EntityManager em = entityManager.get();
		entityManager.set(null);
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

	public static void closeEntityManagerFactory() {
		if (emf != null) {
			emf.close();
		}
	}

	public static EntityManagerFactory renewEntityManagerFactory() {
		if (emf == null || !emf.isOpen()) {
			emf = Persistence.createEntityManagerFactory(persistenceUnit);
		}
		return emf;
	}

	public static boolean ensureWorkingConnection() {
		boolean working = testConnection();
		if (working)
			return true;
		closeEntityManager();
		getEntityManager();
		return testConnection();
	}

	public static boolean testConnection() {
		try {
			String testSql = "select min(id) from BaseObject";
			EntityManager em = entityManager.get();
			Query q = em.createNativeQuery(testSql);
			Object result = q.getSingleResult();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Add keyValue to the table if it is not already included
	 * 
	 * @param tableName
	 * @param keyValue
	 * @return
	 */
	public static boolean secondaryTableKeyInsert(String tableName,
			String keyValue) {
		if (keyValue == null || keyValue.length() == 0) {
			return false;
		}
		EntityManager em = getEntityManager();
		String selectStr = "select count(*) from " + tableName
				+ " where name = ?";
		Query selectQuery = em.createNativeQuery(selectStr);
		selectQuery.setParameter(1, keyValue);
		Object result = selectQuery.getSingleResult();
		int count = getIntFromQuery(result);
		if (count > 0)
			return false;
		boolean createTransaction = false;
		String insertQuery = "insert into " + tableName + " (name) values (?)";
		// attempt insert into table
		try {
			EntityTransaction tx = em.getTransaction();
			if (tx == null) {// no transaction available, unknown reason
				return false;
			} else if (!tx.isActive()) {// new transaction
				createTransaction = true;
				tx.begin();
			}
			Query insert = em.createNativeQuery(insertQuery);
			insert.setParameter(1, keyValue);
			int numresults = insert.executeUpdate();
			if (numresults > 0 && createTransaction) {
				tx.commit();
				return true;
			} else if (createTransaction) { // insert failed, keyValue already
				// present
				tx.rollback();
				return false;
			}
			return false;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	// method compensates for lack of consistency in value returned from native
	// query
	// Toplink persistence returns vector from "select id from", hibernate
	// returns Integer or Long
	public static int getIntFromQuery(Object obj) {
		if (obj instanceof List) {
			obj = ((List) obj).get(0);
		}
		if (obj instanceof Integer) {
			return ((Integer) obj).intValue();
		} else if (obj instanceof Long) {
			return ((Long) obj).intValue();
		} else {
			return -1;
		}
	}

	public static boolean setupProxy(String proxyServer, String proxyPort) {
		if (proxyServer == null)
			return false;
		try {
			int port = Integer.parseInt(proxyPort);
			MorphbankConfig.setProxy(proxyServer, port);
			MorphbankConfig.SYSTEM_LOGGER.info("Proxy configuration: "
					+ proxyServer + " port '" + proxyPort + "' enabled ");
			return true;
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("Proxy port '" + proxyPort
					+ "' is not integer ");
			return false;
		}

	}

	public static void setWebServer(String webServer) {
		WEB_SERVER = webServer;
		MORPHBANK_ID_SERVER = WEB_SERVER + "?id=";
	}

	public static void setImageServer(String imageServer) {
		IMAGE_SERVER = imageServer;
	}

	public static String getURL(int localId) {
		return WEB_SERVER + localId;
	}

	public static String getURL(String localId) {
		return WEB_SERVER + localId;
	}

	public static String getImageURL(int localId) {
		return getImageURL(localId, "thumb");
	}

	public static String getImageURL(int localId, String imageType) {
		return getImageURL(Integer.toString(localId), imageType);
	}

	public static String getImageURL(String localId, String imageType) {
		// return MORPHBANK_SERVER + imageType + "/" + localId + ".jpg";
		return IMAGE_SERVER + "?id=" + localId + "&imgType=" + imageType;
	}

	public static String getImageLink(int localId) {
		return "Reference this image at <a href=\"" + getURL(localId)
				+ "\">MorphBank</a>";
	}

	public static final String AUTHORITY = "services.morphbank.net";

	public static final String NAMESPACE = "morphbank";

	/**
	 * Make an LSID for the object
	 * 
	 * @param localId
	 *            the MorphBank identifier of the object
	 * @return an LSID of the object
	 */
	public static String makeURI(int localId) {
		// TODO add reference to hostServer?
		return makeUrlId(localId);
	}

	public static int getLocalId(String uri) {
		return 0;
	}

	public static String getAuthority() {
		return AUTHORITY;
	}

	public static String getNamespace() {
		return NAMESPACE;
	}

	public static String makeUrlId(int localId) {
		String out = WEB_SERVER + localId;
		return out;
	}

	public static String makeLSID(int localId) {
		StringBuffer out = new StringBuffer();
		out.append("urn:lsid:").append(getAuthority()).append(":")
				.append(getNamespace()).append(":").append(localId);
		return out.toString();
	}

	public static int getLocalIdFromLSID(String lsid) {
		// TODO write this code!
		return 0;
	}

	public static String getPersistenceUnit() {
		return persistenceUnit;
	}

	public static void setPersistenceUnit(String persistenceUnit) {
		MorphbankConfig.persistenceUnit = persistenceUnit;
	}

	public static String flush() {
		try {
			EntityManager em = getEntityManager();
			em.flush();
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String getServicePrefix() {
		return servicePrefix;
	}

	public static void setServicePrefix(String servicePrefix) {
		MorphbankConfig.servicePrefix = servicePrefix;
	}

	public static String getRemoteServer() {
		return REMOTE_SERVER;
	}

	public static void setRemoteServer(String remoteServer) {
		REMOTE_SERVER = remoteServer;
	}

	public static Proxy getProxy() {
		return PROXY;
	}

	public static void setProxy(String proxyServer, int port) {
		PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer,
				port));
	}

	/**
	 * Return a string to be used in Dublin Core publisher term
	 * 
	 * @return
	 */
	public static String getPublisher() {

		return "Morphbank Image Repository http://www.morphbank.net";
	}

	public static String getFilepath() {
		return filepath;
	}

	public static void setFILEPATH(String fILEPATH) {
		filepath = fILEPATH;
	}

	public static String getIpAllowed() {
		// TODO Auto-generated method stub
		return null;
	}
}
