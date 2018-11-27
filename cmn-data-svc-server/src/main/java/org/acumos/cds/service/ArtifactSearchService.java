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

package org.acumos.cds.service;

import java.util.Map;

import org.acumos.cds.domain.MLPArtifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines methods to manipulate Artifact information.
 */
public interface ArtifactSearchService {

	/**
	 * Searches for instances matching all or one of the query parameters, depending
	 * on the isOr parameter.
	 * 
	 * @param queryParameters
	 *                            field-name, field-value pairs. Value may be scalar
	 *                            or array.
	 * @param isOr
	 *                            If true, the query is a disjunction ("or");
	 *                            otherwise the query is a conjunction ("and").
	 * @param pageable
	 *                            Page and sort criteria
	 * @return Page of instances, which may be empty.
	 */
	Page<MLPArtifact> findArtifacts(Map<String, ? extends Object> queryParameters, boolean isOr, Pageable pageable);

	/**
	 * Searches for instances matching all or one of the query parameters, depending
	 * on the isOr parameter.
	 * 
	 * @param artifactTypeCode
	 *                             Artifact type code; ignored if null
	 * @param name
	 *                             Artifact name; ignored if null
	 * @param uri
	 *                             Artifact URI; ignored if null
	 * @param version
	 *                             Artifact version string; ignored if null
	 * @param userId
	 *                             Associated user ID; ignored if null
	 * @param isOr
	 *                             If true, the query is a disjunction ("or");
	 *                             otherwise the query is a conjunction ("and").
	 * @param pageable
	 *                             Page and sort criteria
	 * @return Page of instances, which may be empty.
	 */
	Page<MLPArtifact> findArtifacts(String artifactTypeCode, String name, String uri, String version, String userId,
			boolean isOr, Pageable pageable);

}
