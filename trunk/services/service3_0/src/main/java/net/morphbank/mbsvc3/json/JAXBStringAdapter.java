package net.morphbank.mbsvc3.json;

import java.lang.reflect.Type;

import javax.xml.bind.JAXBElement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JAXBStringAdapter implements JsonSerializer<JAXBElement<?>> {

	@Override
	public JsonElement serialize(JAXBElement<?> src, Type typeOfSrc,
			JsonSerializationContext context) {
		String simpleName = src.getName().getLocalPart();
		//Class<?> theClass = src.getDeclaredType();
		System.out.println("type of "+simpleName+" is "+src.getDeclaredType());
		String value = 	src.getValue().toString();
		JsonPrimitive jsonObject = new JsonPrimitive(value);
		//Object x = new Json
		//jsonObject.addProperty(simpleName, value);
		return jsonObject;
	}
}
