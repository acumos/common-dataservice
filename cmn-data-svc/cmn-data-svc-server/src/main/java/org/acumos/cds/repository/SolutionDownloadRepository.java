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

import org.acumos.cds.domain.MLPSolutionDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SolutionDownloadRepository extends PagingAndSortingRepository<MLPSolutionDownload, Long> {

	/**
	 * Gets the count of downloads for the specified solution ID by iterating over
	 * the table that tracks downloads, which is slightly cheaper than getting the
	 * entire list, but far more expensive than a cached statistic.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @return Count of download records
	 */
	@Query("SELECT COUNT(solutionId) FROM MLPSolutionDownload WHERE solutionId = :solutionId")
	long countSolutionDownloads(@Param("solutionId") String solutionId);

	/**
	 * Finds solution downloads for the specified solution ID.
	 *
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                        Solution ID
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Iterable of MLPSolutionDownload
	 */
	Page<MLPSolutionDownload> findBySolutionId(@Param("solutionId") String solutionId, Pageable pageRequest);

	/**
	 * Deletes all download entries for the specified solution ID.
	 * 
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteBySolutionId(@Param("solutionId") String solutionId);

	/**
	 * Deletes all download entries for the specified artifact ID.
	 * 
	 * Generated by Spring magic.
	 * 
	 * @param artifactId
	 *                       Artifact ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteByArtifactId(@Param("artifactId") String artifactId);

}
