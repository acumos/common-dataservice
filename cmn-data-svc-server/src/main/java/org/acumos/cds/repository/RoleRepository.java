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

import org.acumos.cds.domain.MLPRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends PagingAndSortingRepository<MLPRole, String> {

	/**
	 * Finds all roles for the specified catalogId.
	 * 
	 * @param catalogId
	 *                   Catalog ID
	 * @return Iterable of role objects
	 */
	@Query(value = "select r from MLPRole r, MLPCatRoleMap m" //
			+ " where r.roleId =  m.roleId " //
			+ " and m.catalogId = :catalogId")
	Iterable<MLPRole> findByCatalog(@Param("catalogId") String catalogId);

	/**
	 * Finds all roles for the specified userId.
	 * 
	 * @param userId
	 *                   User ID
	 * @return Iterable of role objects
	 */
	@Query(value = "select r from MLPRole r, MLPUserRoleMap m" //
			+ " where r.roleId =  m.roleId " //
			+ " and m.userId = :userId")
	Iterable<MLPRole> findByUser(@Param("userId") String userId);

}
