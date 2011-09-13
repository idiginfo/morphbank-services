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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.loadexcel.LoadData;
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
public class RestServiceExcelUpload extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	
	private static String folderPath;
	private ArrayList<String> listOfXmlFiles = new ArrayList<String>();
	private static String propertyFile;
	
	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public RestServiceExcelUpload() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// setup persistence unit from parameter, if available
		folderPath = config.getInitParameter("filepath");
		propertyFile = config.getInitParameter("propertyFile");
		RequestParams.initService(config);
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
		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			// Process the uploaded items
			List<?> /* FileItem */items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				if (item.isFormField()) {
					// processFormField(item);
				} else {
					String report = this.processUploadedFile(item);
					this.outputReport(report);
					htmlPresentation(request, response, null);
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

	}

	private void outputReport(String report) {
		File file = new File(folderPath + report);
		FileInputStream input;
		BufferedReader reader;
	    DataInputStream dis;
	    String line;
		try {
			input = new FileInputStream(file);
			dis = new DataInputStream(input);
			reader = new BufferedReader(new InputStreamReader(dis));
			while((line = reader.readLine()) != null) {
				System.out.println(line);
				listOfXmlFiles.add(line);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String processUploadedFile(FileItem item) {
		this.saveTempFile(item);
		LoadData excelupload = new LoadData(folderPath, item.getName(), propertyFile);
		return excelupload.run();	
		
	}

	private String saveTempFile(FileItem item) {
		FileOutputStream outputStream;
		String filename = "";
			filename = folderPath + item.getName();
		try {
			outputStream = new FileOutputStream(filename);
			outputStream.write(item.get());
			outputStream.close();
			return folderPath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void htmlPresentation(HttpServletRequest request, HttpServletResponse response, String folderPath) {
		StringBuffer listOfFiles = new StringBuffer();
		Iterator<String> iter = listOfXmlFiles.iterator();
		while (iter.hasNext()) {
			String next = iter.next();
			listOfFiles.append(next + "<br />");
			
		}
		if (listOfFiles.length() == 0) {
			listOfFiles.append("No file selected.");
		}
		request.setAttribute("listOfFiles", listOfFiles.toString());
		try {
			this.getServletContext().getRequestDispatcher("/showListOfFile.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
