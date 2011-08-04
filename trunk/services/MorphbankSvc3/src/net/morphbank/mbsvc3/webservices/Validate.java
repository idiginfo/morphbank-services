package net.morphbank.mbsvc3.webservices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.read.biff.BiffException;

import net.morphbank.MorphbankConfig;
import net.morphbank.loadexcel.GetConnection;
import net.morphbank.loadexcel.SheetReader;
import net.morphbank.loadexcel.ValidateXls;
import net.morphbank.mbsvc3.request.RequestParams;
import net.morphbank.mbsvc3.webservices.tools.IOTools;
import net.morphbank.mbsvc3.webservices.tools.RedirectSysStreams;
import net.morphbank.mbsvc3.webservices.tools.ValidateCustomXls;

import org.apache.commons.codec.digest.DigestUtils;
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
	private static String MB3AP_BOOK = "mb3ap";
	private static String CUSTOM_BOOK = "custom";
	private static String folderPath = "";

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
			RedirectSysStreams redirect = new RedirectSysStreams(output);
			redirect.run();
			String fileType = "";

			// Process the uploaded items
			List<?> /* FileItem */items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					fileType = processFormField(item);
				} else {
					//String paramName = item.getFieldName();
					if (checkFilesBeforeUpload(item)) {
						String fileName = item.getName();
						saveTempFile(item);
						InputStream stream = item.getInputStream();
						MorphbankConfig.SYSTEM_LOGGER.info("Processing file " + fileName);
						processRequest(stream, out, fileName, fileType);
						MorphbankConfig.SYSTEM_LOGGER.info("Processing complete");
						IOTools.eraseTempFile(folderPath, fileName, true);
					 }
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

	/**
	 * safety check on each file uploaded
	 * @param item
	 * @return
	 */
	private boolean checkFilesBeforeUpload(FileItem item) {
		boolean testPassed = true;
		if (item.getName() == null || item.getName().length() < 1) return false;
//		if ((item.getSize()) > maxSize) {
//			listOfXmlFiles.add("Size of file " + item.getName() + " is too big. Max is " + maxSize / 1000 + "KB.");
//			testPassed = false;
//		}
		if (!(item.getName().endsWith(".xls") || item.getName().endsWith(".csv"))) {
			output.append("The file extension must be .xls");
			testPassed = false;
		}
		return testPassed;
	}
	
	/**
	 * Process the fields in the form that are not FileItem
	 * @param item
	 */
	private String processFormField(FileItem item) {
		return "";
	}


	private boolean processRequest(InputStream in, PrintWriter out, String fileName, String fileType) {
		fileType = this.excelFileType(fileName);
		try {
			if (fileType.equalsIgnoreCase(MB3AP_BOOK)){ //Animalia or Plantae
				SheetReader sheetReader = new SheetReader(folderPath + fileName, null);
				ValidateXls isvalid = new ValidateXls(sheetReader);
				if (!isvalid.checkEverything()) {
					output.append("Error(s) in SpreadSheet. Please check for errors in rows mentioned above.<br />");
					output.append("<br/><i>\"does not match\" errors are typically from modifying rows in the Locality, Specimen or View sheet.<br />");
					output.append("Excel does not automatically update the item selected from the drop down list after a change.<br />");
					output.append("You need to go to the row with an error and select the right Locality, Specimen or View from the list again.</i><br />");
				}
				else {
					output.append("No error found in the document. Congratulations!<br />");
				}
			}
			else { //TODO Custom Workbook
				ValidateCustomXls isvalid = new ValidateCustomXls(folderPath + fileName);
				if (!isvalid.checkEverything()){
					output.append("Error(s) in SpreadSheet. Please check for errors in rows mentioned above.<br />");
					output.append("<br/><i>\"duplicates\" errors exit if a column should only have unique values per row and the same value is found<br />");
					output.append("more than once.</i>");
				}
				else {
					output.append("No error found in the document. Congratulations!<br />");
				}
			}

		} catch (Exception e) {
			e.printStackTrace(out);
			out.close();
		}
		return true;
	}

	private String excelFileType(String fileName) {
		try {
			Workbook workbook = Workbook.getWorkbook(new File(folderPath + fileName));
			String[] sheets = workbook.getSheetNames();
			if (sheets.length < 8) return CUSTOM_BOOK;
			return MB3AP_BOOK;
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void htmlPresentation(HttpServletRequest request, HttpServletResponse response) {
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
		folderPath = MorphbankConfig.getFilepath() + IOTools.createFolder(item.getName()) + "/";
		filename =  item.getName();
		try {
			outputStream = new FileOutputStream(folderPath + filename);
			outputStream.write(item.get());
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Fix to solve a problem when not rebooting tomcat between two tests.
	 * Some error messages seem to appear on the second try but not on the first
	 * Also used to removed unwanted diagnotic messages that users don't need to see.
	 */
	private String removeUnusedWarnings() {
		int blank = output.toString().lastIndexOf("adding a blank");
		int data = output.toString().lastIndexOf("already contains data");
		int empty = output.toString().lastIndexOf("setting to empty");
		int ELInfo = output.toString().lastIndexOf("login successul");
		if (blank == -1 && data == -1 && empty == -1 && ELInfo == -1) {
			return output.toString();
		}
		if (ELInfo != -1) {
			output.delete(output.toString().indexOf("[EL Info]"), ELInfo + 15);
		}
		if (blank > data && blank > empty) {
			return output.toString().substring(blank + 15);
		}
		else if (data > blank && data > empty){
			return output.toString().substring(data + 22);
		}
		else {
			return output.toString().substring(empty + 17);
		}

	}
}
