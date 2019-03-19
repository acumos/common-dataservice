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

import org.acumos.cds.domain.MLPSolutionFOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SolutionFOMRepository
		extends JpaRepository<MLPSolutionFOM, String>, PagingAndSortingRepository<MLPSolutionFOM, String> {

	/**
	 * Gets all solutions for the specified user that appear in ANY catalog.
	 * 
	 * I added DISTINCT because this requires a join on the catalog mapping table.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Page and sort criteria
	 * @return Page of MLPSolutionFOM
	 */
	@Query(value = "SELECT DISTINCT s FROM MLPSolutionFOM s " //
			+ " WHERE s.user.userId = :userId " //
			+ " AND s.catalogs IS NOT EMPTY")
	Page<MLPSolutionFOM> findPublishedByUser(@Param("userId") String userId, Pageable pageRequest);

}
