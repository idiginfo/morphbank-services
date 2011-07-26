package net.morphbank.mbsvc3.webservices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.morphbank.MorphbankConfig;
import net.morphbank.loadexcel.GetConnection;
import net.morphbank.loadexcel.SheetReader;
import net.morphbank.loadexcel.ValidateXls;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.mbsvc3.webservices.tools.RedirectSysStreams;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class Validate extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StringBuffer output = new StringBuffer();
	
	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Validate() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		
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
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- persistence: "
				+ MorphbankConfig.getPersistenceUnit() + " -->");
		MorphbankConfig.SYSTEM_LOGGER.info("<!-- filepath: " + MorphbankConfig.getFilepath()
				+ " -->");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		response.setContentType("text/html");

		try {
			PrintStream console = System.out;
			PrintStream consoleErr = System.err;
			RedirectSysStreams redirect = new RedirectSysStreams(output);
			redirect.run();
			
			// Process the uploaded items
			List<?> /* FileItem */items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					//processFormField(item);
				} else {
					//String paramName = item.getFieldName();
					//if (checkFilesBeforeUpload(item)) {
						String fileName = item.getName();
						saveTempFile(item);
						InputStream stream = item.getInputStream();
						//Reader reader = new InputStreamReader(stream);
						// if ("uploadFile".equals(paramName)) {
						MorphbankConfig.SYSTEM_LOGGER.info("Processing file " + fileName);
						processRequest(stream, out, fileName);
						MorphbankConfig.SYSTEM_LOGGER.info("Processing complete");
						this.eraseTempFile(MorphbankConfig.getFilepath() + fileName);
						// }
					//}
				}
			}
			htmlPresentation(request, response);
			redirect.close();
			out.close();
		} catch (FileUploadException e) {
			e.printStackTrace();
			out.close();
		}
	}

	
	private boolean processRequest(InputStream in, PrintWriter out, String fileName) {
		try {
			SheetReader sheetReader = new SheetReader(MorphbankConfig.getFilepath() + fileName, null);
			ValidateXls isvalid = new ValidateXls(sheetReader);
			if (!isvalid.checkEverything()) {
				output.append("Error(s) in SpreadSheet. Please check for errors in rows mentioned above.");
			}
			else {
				output.append("No error found in the document.");
			}
		} catch (Exception e) {
			e.printStackTrace(out);
			out.close();
		}
		return true;
	}
	
	private void htmlPresentation(HttpServletRequest request, HttpServletResponse response) {
		output.toString().lastIndexOf("adding a blank");
		String html = removeUnusedWarnings();
		html = html.replaceAll("\n", "<br />");
		output = new StringBuffer();
		request.setAttribute("listOfFiles", html);
		try {
			this.getServletContext().getRequestDispatcher("/showListOfFile.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveTempFile(FileItem item) {
		FileOutputStream outputStream;
		String filename = "";
			filename = MorphbankConfig.getFilepath() + item.getName();
			try {
				outputStream = new FileOutputStream(filename);
				outputStream.write(item.get());
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	private void eraseTempFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}
	
	/*
	 * Fix to solve a problem when not rebooting tomcat between two tests.
	 * Some error messages seem to appear on the second try but not on the first
	 */
	private String removeUnusedWarnings() {
		int blank = output.toString().lastIndexOf("adding a blank");
		int data = output.toString().lastIndexOf("already contains data");
		if (blank == -1 && data == -1) {
			return output.toString();
		}
		if (blank > data) {
			return output.toString().substring(blank + 15);
		}
		else {
			return output.toString().substring(data + 22);
		}
		
	}
}
