/*
 * Created on May 16, 2005
 *
 */
package net.morphbank.mbsvc3.ocr;

import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;

/**
 * @author youjun adapted by Greg Riccardi June 12, 2007
 * 
 * Web service client methods for accessing the Peabody Museum OCR service
 * 
 * submitImageForOcr and getOcrFromService are web service clients.
 * 
 */
public class OcrClient {

	public static final String HERBIS_OCR_ADDRESS = "http://130.132.27.143:8080/axis/services/attachOcr";

	/**
	 * make a Call object for use in accessing the OCR service
	 * 
	 * @param operation:
	 *            name of operation ("setFile" or "getFile")
	 * @return
	 * @throws ServiceException
	 */
	protected Call createCall(String operation) throws ServiceException {
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.removeAllParameters();
		call.setTargetEndpointAddress(HERBIS_OCR_ADDRESS);
		call.setOperationName(operation);
		return call;
	}

	/**
	 * Call the setFile service operation
	 * 
	 * @param ocrRegistrationName:
	 *            name used in registering the image with the OCR service
	 * @param localFileNameWithPath:
	 *            full path of the file to be analyzed by the OCR service
	 * @return String valued returned by setFile service call
	 * @throws Exception
	 */
	public String submitImageForOcr(String ocrRegistrationName, String localFileNameWithPath)
			throws Exception {
		DataHandler dhSource = new DataHandler(new FileDataSource(localFileNameWithPath));
		QName qnameAttachment = new QName("DataHandler");
		Call call = createCall("setFile");
		call.registerTypeMapping(dhSource.getClass(), qnameAttachment,
				JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class);
		call.addParameter("filename", XMLType.XSD_STRING, ParameterMode.IN);
		call.addParameter("dh", qnameAttachment, ParameterMode.IN);
		call.setReturnType(XMLType.XSD_STRING);
		String barCodeAndFileId = (String) call
				.invoke(new Object[] { ocrRegistrationName, dhSource });
		return barCodeAndFileId;
	}

	/**
	 * Call the getFile service operation
	 * 
	 * @param ocrRegistrationName
	 *            the name of the result to be used in fetching the OCR results
	 *            from the service
	 * @return the OCR result text
	 * @throws Exception
	 */
	public String getOcrFromService(String ocrRegistrationName) throws Exception {
		// try{
		DataHandler ret = new DataHandler(new FileDataSource(ocrRegistrationName));
		QName qnameAttachment = new QName("DataHandler");

		Call call = createCall("getFile");
		call.registerTypeMapping(ret.getClass(), qnameAttachment,
				JAFDataHandlerSerializerFactory.class, JAFDataHandlerDeserializerFactory.class);

		call.addParameter("filename", XMLType.XSD_STRING, ParameterMode.IN);
		call.setReturnType(qnameAttachment);

		ret = (DataHandler) call.invoke(new Object[] { ocrRegistrationName });
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ret.writeTo(out);
		return out.toString();
	}

}