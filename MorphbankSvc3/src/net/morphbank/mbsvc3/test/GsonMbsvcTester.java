package net.morphbank.mbsvc3.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.persistence.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.morphbank.MorphbankConfig;

public class GsonMbsvcTester {

	public static void main(String[] args) throws Exception {
		MorphbankConfig
//			.setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBPROD);
//			.setPersistenceUnit(MorphbankConfig.PERSISTENCE_MBDEV);
//			.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
				.setPersistenceUnit(MorphbankConfig.PERSISTENCE_LOCALHOST);
		MorphbankConfig.init();
		GsonMbsvcTester tester = new GsonMbsvcTester();
		tester.run();
	}

	void run() {
		// initialize database connection
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
		String queryString = "select a from Annotation a where a.user.id=77685";
		List<Object> results;
		try {
			Query query = MorphbankConfig.getEntityManager().createQuery(queryString);
			query.setMaxResults(10);
			results = query.getResultList();
			writeJSON(results);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeJSON(Object mbobj) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.excludeFieldsWithoutExposeAnnotation().create();
		String jsonString = gson.toJson(mbobj);
		System.out.println(jsonString);
		try {
			FileWriter outfile = new FileWriter("d:/dev/mylog/mbsvc3.json");
			outfile.write(jsonString);
			outfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
