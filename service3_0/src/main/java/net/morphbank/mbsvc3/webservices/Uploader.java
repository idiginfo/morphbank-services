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
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.mbsvc3.mapping.ProcessRequest;
import net.morphbank.mbsvc3.mapsheet.MapSpreadsheetToXml;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.mbsvc3.sharing.UpdateRemote;
import net.morphbank.mbsvc3.webservices.tools.Split;
import net.morphbank.mbsvc3.webservices.tools.Tools;
import net.morphbank.mbsvc3.xml.Credentials;
import net.morphbank.mbsvc3.xml.Request;
import net.morphbank.mbsvc3.xml.Responses;
import net.morphbank.mbsvc3.xml.XmlUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



/**
 * Servlet implementation class for Servlet: convert
 * 
 */
public class Uploader extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numLines = 700;
	private ArrayList<String> listOfXmlFiles = new ArrayList<String>();
	private boolean sendToDB;
	private static boolean folderCreated = false;
	private static String folderPath;

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Uploader() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		folderPath = config.getInitParameter("filepath");
		// setup persistence unit from parameter, if available
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
			// TODO turn over to process request
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
		MorphbankConfig.SYSTEM_LOGGER.info("starting post");
		MorphbankConfig.ensureWorkingConnection();
		this.resetVariables();
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- persistence: "
				+ MorphbankConfig.getPersistenceUnit() + " -->");
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- filepath: " + folderPath
				+ " -->");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		response.setContentType("text/html");

		try {
//			String folderPath = "";
			// Process the uploaded items
			List<?> /* FileItem */items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			boolean testPassed = false;
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					processFormField(item);
				} else {
					if (testPassed = checkFilesBeforeUpload(item)) {
						String fileName = item.getName();
//						folderPath = saveTempFile(item);
						saveTempFile(item);
						InputStream stream = item.getInputStream();
						MorphbankConfig.SYSTEM_LOGGER.info("Processing file " + fileName);
						processRequest(stream, out, fileName, folderPath);
						MorphbankConfig.SYSTEM_LOGGER.info("Processing complete");

					}
				}
			}
			this.htmlPresentation(request, response, folderPath, testPassed);
			out.close();
		} catch (FileUploadException e) {
			e.printStackTrace();
			out.close();
		}
	}

	private String createZipFile() {
		String fileName = folderPath + "xml" + getNextReqFileNumber(folderPath) + ".zip";
		String list = "";
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			ZipOutputStream zout = new ZipOutputStream(fout);
			
			
			for (int i = 0; i < listOfXmlFiles.size(); i++) {
				String file = listOfXmlFiles.get(i);
				
				if (file.endsWith(".xml")) {
				list += file;
				FileInputStream fin = new FileInputStream(listOfXmlFiles.get(i));
				ZipEntry ze = new ZipEntry(listOfXmlFiles.get(i).replaceAll(folderPath, ""));
				zout.putNextEntry(ze);
			    for (int c = fin.read(); c != -1; c = fin.read()) {
			        zout.write(c);
			      }
			      fin.close();
				}
			     
			}
			 zout.close();
			 fout.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName.replaceAll(folderPath, "");
		
	}

	private void resetVariables() {
		listOfXmlFiles = new ArrayList<String>();
		sendToDB = false;
		folderCreated = false;
	}

	/**
	 * safety check on each file uploaded
	 * @param item
	 * @return
	 */
	private boolean checkFilesBeforeUpload(FileItem item) {
		boolean testPassed = true;
		if (item.getName() == null || item.getName().length() < 1) return false;
		
		if (!(item.getName().endsWith(".xls") || item.getName().endsWith(".csv"))) {
			listOfXmlFiles.add("The file extension must be .xls or .csv");
			testPassed = false;
		}
		if (testPassed) {
			listOfXmlFiles.add("<b>" + item.getName() + ":</b>");
		}
		return testPassed;
	}


	/**
	 * Process the fields in the form that are not FileItem
	 * @param item
	 */
	private void processFormField(FileItem item) {
		if (item.getFieldName().equalsIgnoreCase("numLinesPerFile")) {
			numLines = Integer.parseInt(item.getString());
			if (numLines < 100 ) {
				numLines = 100;
			}
			if (numLines > 700) {
				numLines = 700;
			}
		}
		if (item.getFieldName().equalsIgnoreCase("sendtoDB")) {
			if (item.getString().equalsIgnoreCase("send")) {
				sendToDB = true;
			}
		}
	}

	private String saveTempFile(FileItem item) {
		FileOutputStream outputStream;
		String filename = "";

		if (item.getName().endsWith(".xls")) {
			filename = folderPath + "temp.xls";
		}
		else {
			filename = folderPath + "temp.csv";
		}
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

	/**
	 * Split a custom workbook 
	 * @return list of xls files to process
	 */
	private ArrayList<String> splitXls(String folderPath) {
		Split split = new Split(folderPath + "temp.xls", numLines);
		try {
			return split.createMultiplefiles();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	private boolean processRequest(InputStream in, PrintWriter out, String fileName, String folderPath) {
		try {
			ArrayList<String> filesToProcess = new ArrayList<String>();
			//TODO check if it is a csv file
			if (fileName.endsWith(".xls")) {
				filesToProcess = splitXls(folderPath);
				if (filesToProcess == null) {
					return false;
				}
			}
			else { //it's a csv file
				filesToProcess.add(folderPath + "temp.csv");
			}
			Iterator<String> iter = filesToProcess.iterator();
			while (iter.hasNext()) {
				String next = iter.next();
				Request requestDoc = createRequest(next, folderPath);
//				if (requestDoc == null) {
//					listOfXmlFiles.add("request null");
//				}
				String xmlFileName = this.processXls(requestDoc, folderPath); //convert xls to xml
				if (sendToDB) { //upload the file to morphbank
					this.processUpload(xmlFileName, folderPath);
				}
			}
			Tools.eraseTempFile(folderPath, "temp.xls", false);
			Tools.eraseTempFile(folderPath, "temp.csv", false);
		} catch (Exception e) {
			e.printStackTrace(out);
			out.close();
		}
		return true;
	}

	/**
	 * Convert the requestDoc to Xml format
	 * @param requestDoc
	 * @return the name of the xml file
	 */
	private String processXls(Request requestDoc, String folderPath) {
		String reqFileName = saveRequestXmlAsFile(requestDoc, folderPath);
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- request file: "
				+ folderPath + reqFileName + " -->");
		listOfXmlFiles.add(folderPath + reqFileName);
		return reqFileName;
	}

	/**
	 * Upload to morphbank
	 */
	private void processUpload(String file, String folderPath) {
		Request request = null;
		String xmlOutputFile = file.replaceFirst(".xml", "-report.xml");
		file = folderPath + file;
		xmlOutputFile = folderPath + xmlOutputFile;
		listOfXmlFiles.add(xmlOutputFile);
		try {
			InputStream in = new FileInputStream(file);
			Object xmlObject = XmlUtils.getXmlObject(in);
			request = (Request) xmlObject;
			ProcessRequest process = new ProcessRequest(request);
			Responses responses = process.processRequest();
			FileWriter outFile = new FileWriter(xmlOutputFile);
			PrintWriter out = new PrintWriter(outFile);
			XmlUtils.printXml(out, responses);
			outFile.close();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Request createRequest(String fileName, String folderPath) {
		int OWNER_ID = 477719;
		int SUBMITTER_ID = 477719;
		int GROUP_ID = 2505490;
		int firstLine = 1;
		MapSpreadsheetToXml mapper = new MapSpreadsheetToXml();
		Credentials submitter = new Credentials(SUBMITTER_ID, GROUP_ID, null);
		Credentials owner = new Credentials(OWNER_ID, GROUP_ID, null);
		Request request = mapper.createRequestFromFile(fileName, submitter, owner, null,
				numLines, firstLine);
		Tools.eraseTempFile(folderPath, fileName, false);
		return request;
	}

	static String FILE_PREFIX = "req";

	private String[] getReqFileNames(String folderPath) {
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

	private int getNextReqFileNumber(String folderPath) {
		String[] children = getReqFileNames(folderPath);
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

	private String saveRequestXmlAsFile(Object xmlDoc, String folderPath) {

		String fileName = null;
		int i = getNextReqFileNumber(folderPath);
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

	private String saveResponseXmlAsFile(String reqFileName, Object xmlDoc, String folderPath) {
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


	private String createHtmlForm(String link, String fileName) {
		String table = 
		"<table border=0>" +
		"<tr>" +
			"<td>" +
				link +
			"</td>" +
			"<td>" +
				"<input type=\"submit\" value=\"UploadToMorphbank\"/>" +
			"</td>" +
		"</tr>" +
		"</table>" + 
		"<input type=\"hidden\" name=\"uploadxml\" value=\"xmlfiles/" + fileName +"\">" + 
		"<input type=\"hidden\" name=\"fileName\" value=\"" + fileName +"\">";
		
		return "<form action=\"restful\" method=\"post\" onsubmit=\"pleaseWait()\">" + table +
		"</form>";
	}

	/**
	 * Shows the list of xml files generated
	 * @param request
	 * @param response
	 */
	private void htmlPresentation(HttpServletRequest request, HttpServletResponse response, String folderPath, boolean testPassed) {
		StringBuffer listOfFiles = new StringBuffer();
		ArrayList<String> filesToZip = new ArrayList<String>();
		Iterator<String> iter = listOfXmlFiles.iterator();
		while (iter.hasNext()) {
			String next = iter.next();
			if (next.contains("Size of file") || !next.endsWith(".xml")) {
				listOfFiles.append(next + "<br />");
			}
			else {
				String nameToDisplay = next.replaceFirst(folderPath, "");
				next = next.replaceFirst(folderPath, "");
				filesToZip.add(next);
				String link = "<a href=\"" + "xmlfiles/" + next + "\">" + nameToDisplay + "</a>";
				if (!sendToDB) {
					listOfFiles.append(this.createHtmlForm(link, next));
				}
				else {
					listOfFiles.append(link + "<br />");
				}
			}
		}
		if (testPassed) {
			String zipFile = this.createZipFile();
			listOfFiles.append("<br/><b>Download all xml files: </b>");
			listOfFiles.append("<a href=\"" + "xmlfiles/" + zipFile + "\">" + zipFile + "</a>");
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
