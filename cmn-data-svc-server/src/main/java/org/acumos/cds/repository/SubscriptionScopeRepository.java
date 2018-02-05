package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPSubscriptionScopeType;
import org.springframework.data.repository.Repository;

public interface SubscriptionScopeRepository extends Repository<MLPSubscriptionScopeType, String> {
	/**
	 * Returns all instances of the type.
	 * 
	 * @return all entities
	 */
	Iterable<MLPSubscriptionScopeType> findAll();
}
