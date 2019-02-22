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

import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPProjUserAccMap;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserProjectMapRepository extends CrudRepository<MLPProjUserAccMap, MLPProjUserAccMap.ProjUserAccMapPK> {

	/**
	 * Deletes all entries for the specified project ID.
	 * 
	 * @param projectId
	 *                      project ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteByProjectId(@Param("projectId") String projectId);

	/**
	 * Gets users granted access to the specified project. Arguably does not belong
	 * in this repository!
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return Iterable of MLPUser
	 */
	@Query(value = "SELECT u FROM MLPUser u, MLPProjUserAccMap m " //
			+ " WHERE u.userId = m.userId " //
			+ " AND m.projectId = :projectId")
	Iterable<MLPUser> getProjectUsers(@Param("projectId") String projectId);

}
