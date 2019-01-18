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

import javax.transaction.Transactional;

import org.acumos.cds.domain.MLPTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

public interface TaskRepository extends JpaRepository<MLPTask, Long>, JpaSpecificationExecutor<MLPTask> {

	/**
	 * Deletes all tasks for the specified solution ID.
	 * 
	 * Generated by Spring magic.
	 * 
	 * @param solutionId
	 *                       solution ID
	 */
	@Modifying
	@Transactional // throws exception without this
	void deleteBySolutionId(String solutionId);

}
