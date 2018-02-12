package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPNotificationDeliveryMechanismType;
import org.springframework.data.repository.Repository;

/**
 * Only exposes the single get-all method.
 */

public interface NotificationDeliveryMechanismTypeRepository extends Repository<MLPNotificationDeliveryMechanismType, String> {

	/**
	 * Returns all instances of the type.
	 * 
	 * @return all entities
	 */
	Iterable<MLPNotificationDeliveryMechanismType> findAll();


}
