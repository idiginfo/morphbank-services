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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.mbsvc3.sharing.UpdateRemote;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.Response;
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
public class RestService extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	
	private static String folderPath;
	private static ArrayList<String> ipAddresses;
	
	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RestService() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// setup persistence unit from parameter, if available
		folderPath = config.getInitParameter("filepath");
		RequestParams.initService(config);
		setProperties();
		
	}

	private void setProperties() {
		Properties properties = new Properties();
		try {
			//TODO add capability to handle ip list as properties. this is not it!
			String propertyFile = MorphbankConfig.getIpAllowed(); 
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propertyFile);  
			properties.load(inputStream);
			String[] ips = properties.getProperty("IP").replaceAll(" ", "").split(",");
			ipAddresses = new ArrayList<String>();
			for (String ip : ips) {
				ipAddresses.add(ip);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 
		String method = request.getParameter(RequestParams.PARAM_METHOD);
		if ("remoteupdate".equals(method)) {
			UpdateRemote.update(request, response);
		} else if (RequestParams.METHOD_FIX_UUID.equals(method)){
			//TODO  call fix uuid and fix id
		} else {
			// TODO turn over to processrequest
			response.setContentType("text/xml");
			PrintStream out = new PrintStream(response.getOutputStream());
			out.println("<html><body>Here I am</body></html>");
			out.close();
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isIPAllowed(request.getRemoteAddr())) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "This IP is not allowed. Current IP used:" + request.getRemoteAddr());
			return;
		}
		PrintWriter out = response.getWriter();
		MorphbankConfig.SYSTEM_LOGGER.info("starting post from ip:" + request.getRemoteAddr());
		MorphbankConfig.ensureWorkingConnection();
		response.setContentType("text/xml");
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- persistence: "
				+ MorphbankConfig.getPersistenceUnit() + " -->");
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- filepath: " + folderPath
				+ " -->");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
//		response.setContentType("text/html");

		String parameter = request.getParameter("uploadxml");
		if(parameter != null) {
			ServletContext context = getServletContext();
			InputStream fis = context.getResourceAsStream(parameter);
			processRequest(fis, out, request.getParameter("fileName"));
		}
		else {
			try {
				// Process the uploaded items
				List<?> /* FileItem */items = upload.parseRequest(request);
				Iterator<?> iter = items.iterator();
				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();

					if (item.isFormField()) {
						// processFormField(item);
					} else {
						// processUploadedFile(item);
						String paramName = item.getFieldName();
						String fileName = item.getName();
						InputStream stream = item.getInputStream();
						MorphbankConfig.SYSTEM_LOGGER.info("Processing file " + fileName);
						processRequest(stream, out, fileName);
						MorphbankConfig.SYSTEM_LOGGER.info("Processing complete");
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isIPAllowed(String ip) {
		if (ipAddresses.contains(ip)) {
			return true;
		}
		String[] ipParts = ip.split("\\.");
		for (String ipInList : ipAddresses) {
			boolean authorized = true;
			if (ipInList.contains("*")) {
				String[] ipInListParts = ipInList.split("\\.");
				for (int i = 0; i < 4; i++) {
					authorized &= ipPartMatch(ipInListParts[i], 
							ipParts[i]);
				}
				if (authorized) return true;
			}
		}
		return false;
	}
	
	private boolean ipPartMatch(String inList, String ipToTest) {
		if (inList.equals("*") || inList.equals(ipToTest))
			return true;
		return false;
	}

	public boolean processRequest(InputStream in, PrintWriter out, String fileName) {
		try {
			Request requestDoc = XmlUtils.getRequest(in);
			String reqFileName = saveRequestXmlAsFile(requestDoc);
			MorphbankConfig.SYSTEM_LOGGER.info("<!-- request file: "
					+ folderPath + reqFileName + " -->");
			ProcessRequest process = new ProcessRequest(requestDoc);
			Responses responses = process.processRequest();
			responses.setStatus("request file " + fileName + " stored as " + reqFileName);
			String respFileName = saveResponseXmlAsFile(reqFileName, responses);
			responses.setStatus(responses.getStatus() + " response stored as " + respFileName);
			XmlUtils.printXml(out, responses);
			out.println("<!-- response: " + folderPath + respFileName + " -->");
			MorphbankConfig.SYSTEM_LOGGER.info("<!-- response: " + folderPath
					+ respFileName + " -->");
			out.close();
		} catch (Exception e) {
			e.printStackTrace(out);
			out.close();
		}
		return true;
	}

	static String FILE_PREFIX = "req";

	public String[] getReqFileNames() {
		File dir = new File(folderPath);

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
		if (children == null) return 0;
		for (int i = 0; i < children.length; i++) {
			String num = children[i].substring(FILE_PREFIX.length(), children[i].indexOf(".xml"));
			try {
				int val = Integer.valueOf(num);
				if (max < val) max = val;
			} catch (Exception e) {
				MorphbankConfig.SYSTEM_LOGGER.info("unable to convert file number " + num
						+ " to int");
			}
		}
		return max + 1;
	}

	public String saveRequestXmlAsFile(Object xmlDoc) {

		String fileName = null;
		int i = getNextReqFileNumber();
		fileName = FILE_PREFIX + i + ".xml";
		String filePath = folderPath + fileName;
		try {
			FileOutputStream outFileStr = new FileOutputStream(filePath);
			PrintWriter out = new PrintWriter(outFileStr);
			XmlUtils.printXml(out, xmlDoc);
			out.close();
			outFileStr.close();
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("unable to create/write xml file as " + filePath);
		}
		return fileName;
	}

	protected String saveResponseXmlAsFile(String reqFileName, Object xmlDoc) {
		String fileName = reqFileName.replaceFirst("req", "resp");
		if (fileName == null) {
			fileName = "resp"+reqFileName;
		}
		String filePath = folderPath + fileName;
		// open a file
		try {
			FileOutputStream outFile = new FileOutputStream(folderPath + fileName);
			PrintWriter out = new PrintWriter(outFile);
			XmlUtils.printXml(out, xmlDoc);
			out.close();
			outFile.close();
		} catch (Exception e) {
			MorphbankConfig.SYSTEM_LOGGER.info("unable to create/write xml file as " + filePath);
			e.printStackTrace();
		}
		return fileName;

	}

}
