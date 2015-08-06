package net.morphbank.mbsvc3.json;

import java.io.PrintWriter;
import java.util.List;

import net.morphbank.mbsvc3.mapping.XmlServices;
import net.morphbank.mbsvc3.xml.Response;
import net.morphbank.mbsvc3.xml.XmlUtils;
import net.morphbank.object.BaseObject;

public class JsonUtils {

	public static String toJsonString(Object obj) {
		JsonWriter jsonWriter = new JsonWriter();
		String body = jsonWriter.write(obj);
		return body;
	}

	public static String doJsonResponse(PrintWriter out, List<Integer> objectIds) {

		JsonObjects objects = new JsonObjects();
		for (Object id : objectIds) {
			BaseObject obj = null;
			if (id instanceof Integer) {
				obj = BaseObject.getEJB3Object((int) id);
			} else if (id instanceof BaseObject) {
				obj = (BaseObject) id;
			}
			objects.objects.add(obj);

		}
		String body = toJsonString(objects);
		out.print(body);
		return "good";
	}

	public static String doJsonResponse(PrintWriter out, String title,
			String subtitle, int numResults, int size, int firstResult,
			List objectIds) {

		Response xmlResp = XmlServices.createResponse(title, subtitle,
				numResults, objectIds.size(), firstResult, objectIds);
		//String body = toJsonString(xmlResp);
		String body = JacksonWriter.write(xmlResp);
		if (out != null) {
			// XmlUtils.printXml(out, xmlResp);
			out.print(body);
		}

		return body;
	}
}
