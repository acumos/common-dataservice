package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPUserNotifPref;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserNotificationPreferenceRepository extends CrudRepository<MLPUserNotifPref, Long> {
	/**
	 * Finds all entries for the specified userId.
	 * 
	 * @param userId
	 *            User ID
	 * @return Iterable of user notification preference objects
	 */
	@Query(value = "select m from MLPUserNotifPref m " //
			+ " where m.userId = :userId")
	Iterable<MLPUserNotifPref> findByUser(@Param("userId") String userId);

}
