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

import org.acumos.cds.domain.MLPSolutionRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SolutionRatingRepository
		extends PagingAndSortingRepository<MLPSolutionRating, MLPSolutionRating.SolutionRatingPK> {

	/**
	 * Gets the count of ratings for the specified solution ID by iterating over the
	 * table that tracks user ratings, which is slightly cheaper than getting the
	 * entire list, but far more expensive than a cached statistic.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return Count of rating records
	 */
	@Query("SELECT COUNT(rating) FROM MLPSolutionRating WHERE solutionId = :solutionId")
	Long getSolutionRatingCount(@Param("solutionId") String solutionId);

	/**
	 * Gets the average of ratings for the specified solution ID by iterating over
	 * the table that tracks user ratings, which is slightly cheaper than getting
	 * the entire list, but far more expensive than a cached statistic.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return Average of rating values
	 */
	@Query("SELECT AVG(rating) FROM MLPSolutionRating WHERE solutionId = :solutionId")
	Double getSolutionRatingAverage(@Param("solutionId") String solutionId);

	/**
	 * Finds solution ratings for the specified solution ID.
	 *
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Iterable of MLPSolutionRating
	 */
	Page<MLPSolutionRating> findBySolutionId(@Param("solutionId") String solutionId, Pageable pageRequest);

	/**
	 * Deletes all ratings for the specified solution ID.
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
