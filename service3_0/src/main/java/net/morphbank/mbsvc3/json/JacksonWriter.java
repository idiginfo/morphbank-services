package net.morphbank.mbsvc3.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import net.morphbank.mbsvc3.xml.Response;

public class JacksonWriter {

	public static String write(Response resp) {

		ObjectMapper mapper = new ObjectMapper();

		TypeFactory typeFactory = null;
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.setAnnotationIntrospector(introspector);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		// Printing JSON
		String result = null;
		try {
			result = mapper.writeValueAsString(resp);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(result);

		return result;

	}
}
