package net.morphbank.mbsvc3.test;

import java.io.File;
import java.io.IOException;
import java.util.*;


import javax.persistence.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.morphbank.MorphbankConfig;

public class JacksonMbsvcTester {

	public static void main(String[] args) throws Exception {
		MorphbankConfig
//			.setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBPROD);
//			.setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBDEV);
//			.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
				.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
		MorphbankConfig.init();
		JacksonMbsvcTester tester = new JacksonMbsvcTester();
		tester.run();
	}

	void run() {
		//initialize database connection
		EntityManager manager = MorphbankConfig.getEntityManager();
		if (!manager.isOpen()) {
			try {
				manager = MorphbankConfig.getEntityManager();
			} catch (Exception e) {
				MorphbankConfig.SYSTEM_LOGGER.info("error in connect to db: " + e.toString());
				System.out.print(e.getMessage());
				return;
			}
		}
		String queryString = "select a from Image a";
		List<Object> results ;
		try {
			Query query = MorphbankConfig.getEntityManager().createQuery(queryString);
			query .setMaxResults(10);
			results = query.getResultList();
			writeJSON(results);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeJSON(Object mbobj) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File("d:/dev/mylog/mbsvc.json"), mbobj);
	}

}
