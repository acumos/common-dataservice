/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPSolutionDeployment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SolutionDeploymentRepository extends PagingAndSortingRepository<MLPSolutionDeployment, String> {

	/**
	 * Gets the count of deployments for the specified solution and revision IDs by
	 * iterating over the table.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param revisionId
	 *                       Revision ID
	 * @return Count of deployment records
	 */
	@Query("SELECT COUNT(solutionId) FROM MLPSolutionDeployment "
			+ " WHERE solutionId = :solutionId AND revisionId = :revisionId")
	long countSolutionDeployments(@Param("solutionId") String solutionId, @Param("revisionId") String revisionId);

	/**
	 * Finds solution deployments for the specified user ID.
	 *
	 * Generated by Spring magic.
	 * 
	 * @param userId
	 *                        user ID
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Page of MLPSolutionDeployment
	 */
	Page<MLPSolutionDeployment> findByUserId(@Param("userId") String userId, Pageable pageRequest);

	/**
	 * Finds solution deployments for the specified solution ID and revision ID.
	 *
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Page of MLPSolutionDeployment
	 */
	Page<MLPSolutionDeployment> findBySolutionIdAndRevisionId(@Param("solutionId") String solutionId,
			@Param("revisionId") String revisionId, Pageable pageRequest);

	/**
	 * Finds solution deployments for the specified solution ID, revision ID and
	 * user ID.
	 *
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param revisionId
	 *                        Revision ID
	 * @param userId
	 *                        user ID
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Page of MLPSolutionDeployment
	 */
	Page<MLPSolutionDeployment> findBySolutionIdAndRevisionIdAndUserId(@Param("solutionId") String solutionId,
			@Param("revisionId") String revisionId, @Param("userId") String userId, Pageable pageRequest);

	/**
	 * Deletes all entries for the specified solution ID
	 * 
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteBySolutionId(@Param("solutionId") String solutionId);

}