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

import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPProjPipelineMap;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProjPipelineMapRepository extends CrudRepository<MLPProjPipelineMap, MLPProjPipelineMap.ProjPlMapPK> {

	/**
	 * Gets the pipelines in the specified project via the mapping table.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return List of MLPPipeline
	 */
	@Query(value = "select p from MLPPipeline p, MLPProjPipelineMap m " //
			+ " where p.pipelineId =  m.pipelineId " //
			+ " and m.projectId = :projectId")
	Iterable<MLPNotebook> findPipelinesByProjectId(@Param("projectId") String projectId);

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
	 * Deletes all entries for the specified pipeline ID.
	 * 
	 * @param pipelineId
	 *                       pipeline ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteByPipelineId(@Param("pipelineId") String pipelineId);

}
