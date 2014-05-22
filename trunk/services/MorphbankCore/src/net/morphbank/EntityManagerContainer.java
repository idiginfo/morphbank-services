package net.morphbank;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerContainer {
	
	@PersistenceContext
	EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}


}
