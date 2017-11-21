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

import org.acumos.cds.domain.MLPUserRoleMap;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRoleMapRepository extends CrudRepository<MLPUserRoleMap, MLPUserRoleMap.UserRoleMapPK> {

	/**
	 * Counts the users assigned to the specified role
	 * 
	 * @param roleId
	 *            Role ID
	 * @return Count of users in the role
	 */
	@Query(value = "select count(m) from MLPUserRoleMap m where m.roleId = :roleId")
	Long getRoleUsersCount(@Param("roleId") String roleId);

	/**
	 * Finds all map entries for the specified userId.
	 * 
	 * @param userId
	 *            User ID
	 * @return Iterable of map objects
	 */
	@Query(value = "select m from MLPUserRoleMap m where m.userId = :userId")
	Iterable<MLPUserRoleMap> findByUser(@Param("userId") String userId);

}
