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
package net.morphbank.mbsvc3.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class for Servlet: RestService
 * 
 */
public class RestfulService extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	static final String CONFIG = MorphbankConfig.PERSISTENCE_MBPROD;
	static final String CONFIGx = MorphbankConfig.PERSISTENCE_MBDEV;
	static final String FILEPATH = "c:/dev/morphbank/websvcfiles/";
	static final String SERVICE = MorphbankConfig.SERVICES;
	static final String CONFIG_PARAM = "persistence";
	static final String FILEPATH_PARAM = "filepath";
	static final String SERVICE_PARAM = "serviceprefix";

	String filepath = null;
	String persistence = null;
	String servicePrefix = null;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RestfulService() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		persistence = config.getInitParameter(CONFIG_PARAM);
		filepath = config.getInitParameter(FILEPATH_PARAM);
		servicePrefix = config.getInitParameter(SERVICE_PARAM);
		initObject();
	}

	public void initObject() {
		// setup persistence unit from parameter, if available
		if (persistence != null) {
			MorphbankConfig.setPersistenceUnit(persistence);
		} else {
			MorphbankConfig.setPersistenceUnit(CONFIG);
		}
		if (filepath == null) {
			filepath = FILEPATH;
		}
		if (servicePrefix != null) {
			MorphbankConfig.setServicePrefix(servicePrefix);
		} else {
			MorphbankConfig.setServicePrefix(SERVICE);
		}
		MorphbankConfig.renewEntityManagerFactory();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Turn over work to RequestProcessor!
		response.setContentType("text/xml");
		PrintStream out = new PrintStream(response.getOutputStream());
		out.println("<html><body>Here I am</body></html>");
		out.close();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		MorphbankConfig.SYSTEM_LOGGER.info("starting post");
		response.setContentType("text/xml");
		System.err.println("<!-- persistence: " + MorphbankConfig.getPersistenceUnit() + " -->");
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- filepath: " + filepath + " -->");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		// response.setContentType("text/html");

		try {
			// Process the uploaded items
			List<?> /* FileItem */items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					MorphbankConfig.SYSTEM_LOGGER.info("Form field " + item.getFieldName());
					// processFormField(item);
				} else {
					// processUploadedFile(item);
					String paramName = item.getFieldName();
					String fileName = item.getName();
					InputStream stream = item.getInputStream();
					// Reader reader = new InputStreamReader(stream);
					if ("uploadFileXml".equals(paramName)) {
						MorphbankConfig.SYSTEM_LOGGER.info("Processing file " + fileName);
						processRequest(stream, out, fileName);
						MorphbankConfig.SYSTEM_LOGGER.info("Processing complete");
					} else {
						// Process the input stream
						MorphbankConfig.SYSTEM_LOGGER.info("Upload field name "
								+ item.getFieldName() + " ignored!");
					}
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	public boolean processRequest(InputStream in, PrintWriter out, String fileName) {
		try {
			Request requestDoc = XmlUtils.getRequest(in);
			String reqFileName = saveRequestXmlAsFile(requestDoc);
			MorphbankConfig.SYSTEM_LOGGER.info("<!-- request file: " + filepath + reqFileName
					+ " -->");
			ProcessRequest process = new ProcessRequest(requestDoc);
			Responses responses = process.processRequest();
			responses.setStatus("request file " + fileName + " stored as " + reqFileName);
			String respFileName = saveResponseXmlAsFile(reqFileName, responses);
			responses.setStatus(responses.getStatus() + " response stored as " + respFileName);
			XmlUtils.printXml(out, responses);
			out.println("<!-- response: " + filepath + respFileName + " -->");
			MorphbankConfig.SYSTEM_LOGGER
					.info("<!-- response: " + filepath + respFileName + " -->");
			out.close();
		} catch (Exception e) {
			e.printStackTrace(out);
			out.close();
		}
		return true;
	}

	static String FILE_PREFIX = "req";

	public String[] getReqFileNames() {
		File dir = new File(filepath);

		// It is also possible to filter the list of returned files.
		// This example does not return any files that start with `.'.
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(FILE_PREFIX);
			}
		};
		String[] children = dir.list(filter);
		return children;
	}

	public int getNextReqFileNumber() {
		String[] children = getReqFileNames();
		int max = 0;
		for (int i = 0; i < children.length; i++) {
			String num = children[i].substring(FILE_PREFIX.length(), children[i].indexOf(".xml"));
			try {
				int val = Integer.valueOf(num);
				if (max < val) max = val;
			} catch (Exception e) {

			}
		}
		return max + 1;
	}

	public String saveRequestXmlAsFile(Object xmlDoc) {

		String fileName = null;
		try {
			int i = getNextReqFileNumber();
			fileName = FILE_PREFIX + i + ".xml";
			String filePath = filepath + fileName;
			FileOutputStream outFileStr = new FileOutputStream(filePath);
			PrintWriter out = new PrintWriter(outFileStr);
			XmlUtils.printXml(out, xmlDoc);
			out.close();
			outFileStr.close();
		} catch (Exception e) {

		}
		return fileName;
	}

	protected String saveResponseXmlAsFile(String reqFileName, Object xmlDoc) {
		String fileName = reqFileName.replaceFirst("req", "resp");
		// open a file
		try {
			FileOutputStream outFile = new FileOutputStream(filepath + fileName);
			PrintWriter out = new PrintWriter(outFile);
			XmlUtils.printXml(out, xmlDoc);
			out.close();
			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;

	}

}
