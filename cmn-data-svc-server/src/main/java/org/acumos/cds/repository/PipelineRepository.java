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

import org.acumos.cds.domain.MLPPipeline;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface PipelineRepository extends PagingAndSortingRepository<MLPPipeline, String> {

	/**
	 * Gets all pipelines mapped to the specified project. Size is expected to be
	 * modest, so this does not use page.
	 * 
	 * @param projectId
	 *                      Project ID
	 * @return Iterable of MLPPipeline
	 */
	@Query(value = "SELECT p FROM MLPPipeline p, MLPProjPipelineMap m " //
			+ " WHERE p.pipelineId =  m.pipelineId AND m.projectId = :projectId")
	Iterable<MLPPipeline> findProjectPipelines(@Param("projectId") String projectId);

}
