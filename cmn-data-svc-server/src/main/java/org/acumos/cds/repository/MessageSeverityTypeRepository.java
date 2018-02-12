package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPArtifactType;
import org.acumos.cds.domain.MLPMessageSeverityType;
import org.springframework.data.repository.Repository;

public interface MessageSeverityTypeRepository extends Repository<MLPArtifactType, String>{
	/**
	 * Returns all instances of the type.
	 * 
	 * @return all entities
	 */
	Iterable<MLPMessageSeverityType> findAll();
}
