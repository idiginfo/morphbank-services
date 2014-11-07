package net.morphbank.mbsvc3.json;

import javax.xml.bind.JAXBElement;

import net.morphbank.mbsvc3.xml.ObjectFactory;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Class to implement ObjectWriter for JSON output of Citagora content
 * 
 * @author griccardi
 * 
 */

public class JsonWriter  {
	JsonParser parser = new JsonParser();
	ExclusionStrategy excludeFields = new MyExclusionStrategy();
	Gson gson = null;
	
	JsonWriter() {
		JAXBElement<String> element = new JAXBElement<String>(ObjectFactory._Continent_QNAME, String.class, null, null);
		Class<?> theClass = element.getClass();
		gson = new GsonBuilder().setExclusionStrategies(excludeFields)
				.registerTypeAdapter(theClass, new JAXBStringAdapter())
				.setPrettyPrinting().create();
	}

	public String write(Object objects) {
		String formattedContent = gson.toJson(objects);
		return formattedContent;
	}

	public String format(String content) {
		String formattedContent;
		if (content == null)
			return null;
		try {
			JsonElement tree = parser.parse(content);
			formattedContent = gson.toJson(tree);
			return formattedContent;
		} catch (JsonParseException e) {
		}
		return null;
	}

}
