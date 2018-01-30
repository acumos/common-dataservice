package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPStepResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface StepResultRepository extends PagingAndSortingRepository<MLPStepResult, Long>{
	/**
	 * Finds solution downloads for the specified solution ID.
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param pageRequest
	 *            Start index, page size, sort criteria
	 * @return Iterable of MLPSolutionDownload
	 */
	@Query("SELECT d FROM MLPStepResult d WHERE d.solutionId = :solutionId AND d.revisionId = :revisionId")
	Page<MLPStepResult> findBySolutionRevision(@Param("solutionId") String solutionId, @Param("revisionId") String revisionId, Pageable pageRequest);

}
