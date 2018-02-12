package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPUserNotificationPrefernce;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserNotificationPreferenceRepository extends PagingAndSortingRepository<MLPUserNotificationPrefernce, Long>{
	/**
	 * Finds all entries for the specified userId.
	 * 
	 * @param userId
	 *            User ID
	 * @return Iterable of user notification preference objects
	 */
	@Query(value = "select m from MLPUserNotificationPrefernce m " //
			+ " where m.userId = :userId")
	Page<MLPUserNotificationPrefernce> findByUser(@Param("userId") String userId, Pageable pageable);

}
