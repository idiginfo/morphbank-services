package net.morphbank;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerContainer {
	
	@PersistenceContext
	EntityManager em;

	public EntityManager getEm() {
		return em;
	}


}
