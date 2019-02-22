/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import org.acumos.cds.domain.MLPProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends PagingAndSortingRepository<MLPProject, String> {

	/**
	 * Gets a page of projects accessible by the specified user; i.e., either the
	 * user is the creator OR has been granted access via the mapping table.
	 * 
	 * @param userId
	 *                        User ID
	 * @param pageRequest
	 *                        Start index, page size, sort criteria
	 * @return Page of MLPProject
	 */
	@Query(value = "SELECT DISTINCT p FROM MLPProject p, MLPProjUserAccMap m " //
			+ " WHERE p.userId = :userId " //
			+ " OR p.projectId =  m.projectId AND m.userId = :userId")
	Page<MLPProject> findUserAccessibleProjects(@Param("userId") String userId, Pageable pageRequest);

}
