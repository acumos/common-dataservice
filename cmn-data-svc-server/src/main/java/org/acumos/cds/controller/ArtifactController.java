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

package org.acumos.cds.controller;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.service.ArtifactSearchService;
import org.acumos.cds.transport.CountTransport;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.util.ApiPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(value = "/" + CCDSConstants.ARTIFACT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtifactController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private ArtifactSearchService artifactService;
	@Autowired
	private SolutionRevisionRepository solutionRevisionRepository;

	@ApiOperation(value = "Gets the count of artifacts.", response = CountTransport.class)
	@RequestMapping(value = "/" + CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public CountTransport getArtifactCount() {
		logger.info("getArtifactCount");
		Long count = artifactRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of artifacts, optionally sorted.", //
			response = MLPArtifact.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPArtifact> getArtifacts(Pageable pageRequest) {
		logger.info("getArtifacts {}", pageRequest);
		Page<MLPArtifact> page = artifactRepository.findAll(pageRequest);
		return page;
	}

	@ApiOperation(value = "Gets the entity for the specified ID. Returns bad request if the ID is not found.", //
			response = MLPArtifact.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{artifactId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getArtifact(@PathVariable("artifactId") String artifactId, HttpServletResponse response) {
		logger.info("getArtifact ID {}", artifactId);
		MLPArtifact da = artifactRepository.findOne(artifactId);
		if (da == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + artifactId, null);
		}
		return da;
	}

	@ApiOperation(value = "Searches for entities with names or descriptions that contain the search term using the like operator.", //
			response = MLPArtifact.class, responseContainer = "List")
	@ApiPageable
	@RequestMapping(value = "/" + CCDSConstants.LIKE_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Iterable<MLPArtifact> like(@RequestParam(CCDSConstants.TERM_PATH) String term, Pageable pageRequest) {
		logger.info("like pageRequest {}", pageRequest);
		Iterable<MLPArtifact> i = artifactRepository.findBySearchTerm(term, pageRequest);
		return i;
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many field names from the MLPArtifact class.
	 */
	@ApiOperation(value = "Searches for artifacts with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPArtifact.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object searchArtifacts(//
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Artifact ID") //
			@RequestParam(name = "artifactId", required = false) String artifactId, //
			@ApiParam(value = "Artifact type code") //
			@RequestParam(name = "artifactTypeCode", required = false) String artifactTypeCode, //
			@ApiParam(value = "Created date, in ms since the Epoch") //
			@RequestParam(name = "created", required = false) Long created, //
			@ApiParam(value = "Modified date, in ms since the Epoch") //
			@RequestParam(name = "modified", required = false) Long modified, //
			@ApiParam(value = "Name") //
			@RequestParam(name = "name", required = false) String name, //
			@ApiParam(value = "URI") //
			@RequestParam(name = "uri", required = false) String uri, //
			@ApiParam(value = "Version") //
			@RequestParam(name = "version", required = false) String version, //
			@ApiParam(value = "User ID") //
			@RequestParam(name = "userId", required = false) String userId, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("searchArtifacts enter");
		boolean isOr = junction != null && "o".equals(junction);
		Map<String, Object> queryParameters = new HashMap<>();
		if (artifactId != null)
			queryParameters.put("artifactId", artifactId);
		if (artifactTypeCode != null)
			queryParameters.put("artifactTypeCode", artifactTypeCode);
		if (created != null)
			queryParameters.put("created", new Date(created));
		if (modified != null)
			queryParameters.put("modified", new Date(modified));
		if (name != null)
			queryParameters.put("name", name);
		if (uri != null)
			queryParameters.put("uri", uri);
		if (version != null)
			queryParameters.put("version", version);
		if (userId != null)
			queryParameters.put("userId", userId);
		if (queryParameters.size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			Object result = artifactService.findArtifacts(queryParameters, isOr, pageRequest);
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("searchArtifacts failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchArtifacts failed", ex);
		}
	}

	@ApiOperation(value = "Gets the solution revisions that use the specified artifact ID.", //
			response = MLPSolutionRevision.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{artifactId}/" + CCDSConstants.REVISION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getRevisionsForArtifact(@PathVariable("artifactId") String artifactId, HttpServletResponse response) {
		logger.info("getRevisionsForArtifact ID {}", artifactId);
		// Validate the artifact ID because an empty result is ambiguous.
		MLPArtifact da = artifactRepository.findOne(artifactId);
		if (da == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + artifactId, null);
		}
		Object result = solutionRevisionRepository.findByArtifactId(artifactId);
		return result;
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPArtifact.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createArtifact(@RequestBody MLPArtifact artifact, HttpServletResponse response) {
		logger.info("createArtifact artifact {}", artifact);
		try {
			String id = artifact.getArtifactId();
			if (id != null) {
				UUID.fromString(id);
				if (artifactRepository.findOne(id) != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Create a new row
			Object result = artifactRepository.save(artifact);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.ARTIFACT_PATH + "/" + artifact.getArtifactId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createArtifact failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createArtifact failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{artifactId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateArtifact(@PathVariable("artifactId") String artifactId, @RequestBody MLPArtifact artifact,
			HttpServletResponse response) {
		logger.info("updateArtifact ID {}", artifactId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		MLPArtifact existing = artifactRepository.findOne(artifactId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + artifactId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			artifact.setArtifactId(artifactId);
			// Update the existing row
			artifactRepository.save(artifact);
			Object result = new SuccessTransport(HttpServletResponse.SC_OK, null);
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateArtifact failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateArtifact failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{artifactId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteArtifact(@PathVariable("artifactId") String artifactId,
			HttpServletResponse response) {
		logger.info("deleteArtifact ID {}", artifactId);
		try {
			artifactRepository.delete(artifactId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteArtifact failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteArtifact failed", ex);
		}
	}

}
