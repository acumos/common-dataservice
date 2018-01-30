package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface StepResultRepository extends PagingAndSortingRepository<MLPStepResult, Long>{
	
	/**
	 * Finds step results for the specified tracking ID.
	 * @param trackingId
	 *            tracking ID
	 * @return Iterable of MLPStepResult
	 */
	
	Iterable<MLPTag> findByTrackingId(@Param("trackingId") String trackingId);


}
