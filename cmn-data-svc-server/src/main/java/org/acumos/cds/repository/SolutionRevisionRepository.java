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

import org.acumos.cds.domain.MLPSolutionRevision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SolutionRevisionRepository extends PagingAndSortingRepository<MLPSolutionRevision, String> {

	/**
	 * Finds solution revisions using a LIKE query on the DESCRIPTION field.
	 * 
	 * @param searchTerm
	 *            fragment to find in the descriptions
	 * @param pageRequest
	 *            Page and sort criteria
	 * @return Iterable of MLPSolutionRevision with matching text.
	 */
	@Query("SELECT s FROM MLPSolutionRevision s WHERE LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<MLPSolutionRevision> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageRequest);

	/**
	 * Finds solution revisions for the specified solution IDs.
	 * 
	 * Not defined as pageable because the number of revisions for a solution is
	 * expected to be modest.
	 * 
	 * @param solutionIds
	 *            Array of solution IDs
	 * @return Iterable of MLPSolutionRevision
	 */
	@Query("SELECT r FROM MLPSolutionRevision r WHERE r.solutionId in :solutionIds")
	Iterable<MLPSolutionRevision> findBySolution(@Param("solutionIds") String[] solutionIds);

	/**
	 * Gets all solution revisions associated with the specified artifact.
	 *
	 * This does not accept a pageable parameter because the number of revisions for
	 * a single artifact is expected to be modest.
	 * 
	 * @param artifactId
	 *            artifact ID
	 * @return Iterable of MLPSolutionRevision
	 */
	@Query(value = "select r from MLPSolutionRevision r, MLPSolRevArtMap m " //
			+ " where r.revisionId =  m.revisionId " //
			+ " and m.artifactId = :artifactId")
	Iterable<MLPSolutionRevision> findByArtifact(@Param("artifactId") String artifactId);

}
