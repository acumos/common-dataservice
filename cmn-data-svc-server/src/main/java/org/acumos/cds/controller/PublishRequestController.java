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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.repository.PublishRequestRepository;
import org.acumos.cds.service.PublishRequestSearchService;
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
@RequestMapping(value = "/" + CCDSConstants.PUBLISH_REQUEST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class PublishRequestController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private PublishRequestRepository publishRequestRepository;
	@Autowired
	private PublishRequestSearchService publishRequestSearchService;

	@ApiOperation(value = "Gets a page of publish requests, optionally sorted on fields.", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPPublishRequest> getPublishRequests(Pageable pageRequest) {
		logger.info("getPublishRequests {}", pageRequest);
		return publishRequestRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the request for the specified ID. Returns bad request if the ID is not found.", //
			response = MLPPublishRequest.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{requestId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getPublishRequest(@PathVariable("requestId") long requestId, HttpServletResponse response) {
		logger.info("getPublishRequest: requestId {}", requestId);
		MLPPublishRequest sr = publishRequestRepository.findOne(requestId);
		if (sr == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + requestId, null);
		}
		return sr;
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many field names from the MLPArtifact class.
	 */
	private static final String solutionIdField = "solutionId";
	private static final String revisionIdField = "revisionId";
	private static final String requestUserIdField = "requestUserId";
	private static final String reviewUserIdField = "reviewUserId";
	private static final String statusCodeField = "statusCode";

	@ApiOperation(value = "Searches for requests with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object searchPublishRequests(@ApiParam(value = "Junction", allowableValues = "a,o") //
	@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Solution ID") //
			@RequestParam(name = solutionIdField, required = false) String solutionId, //
			@ApiParam(value = "Revision ID") //
			@RequestParam(name = revisionIdField, required = false) String revisionId, //
			@ApiParam(value = "Request user ID") //
			@RequestParam(name = requestUserIdField, required = false) String requestUserId, //
			@ApiParam(value = "Review user ID") //
			@RequestParam(name = reviewUserIdField, required = false) String reviewUserId, //
			@ApiParam(value = "Status code") //
			@RequestParam(name = statusCodeField, required = false) String statusCode, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("searchArtifacts enter");
		boolean isOr = junction != null && "o".equals(junction);
		Map<String, Object> queryParameters = new HashMap<>();
		if (solutionId != null)
			queryParameters.put(solutionIdField, solutionId);
		if (revisionId != null)
			queryParameters.put(revisionIdField, revisionId);
		if (requestUserId != null)
			queryParameters.put(requestUserIdField, requestUserId);
		if (reviewUserId != null)
			queryParameters.put(reviewUserIdField, reviewUserId);
		if (statusCode != null)
			queryParameters.put(statusCodeField, statusCode);
		if (queryParameters.size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return publishRequestSearchService.findPublishRequests(queryParameters, isOr, pageRequest);
		} catch (Exception ex) {
			logger.warn("searchPublishRequests failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchPublishRequests failed", ex);
		}
	}

	@ApiOperation(value = "Creates a new request with a generated ID. Returns bad request on constraint violation etc.", //
			response = MLPPublishRequest.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createPublishRequest(@RequestBody MLPPublishRequest publishRequest, HttpServletResponse response) {
		logger.info("createPublishRequest: enter");
		try {
			// Validate enum codes
			super.validateCode(publishRequest.getStatusCode(), CodeNameType.PUBLISH_REQUEST_STATUS);
			// Force creation of new ID
			publishRequest.setRequestId(null);
			MLPPublishRequest result = publishRequestRepository.save(publishRequest);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.PUBLISH_REQUEST_PATH + "/" + result.getRequestId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPublishRequest failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPublishRequest failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing request with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{requestId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updatePublishRequest(@PathVariable("requestId") long requestId,
			@RequestBody MLPPublishRequest publishRequest, HttpServletResponse response) {
		logger.info("updatePublishRequest: requestId {}", requestId);
		// Get the existing one
		MLPPublishRequest existing = publishRequestRepository.findOne(requestId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + requestId, null);
		}
		try {
			super.validateCode(publishRequest.getStatusCode(), CodeNameType.PUBLISH_REQUEST_STATUS);
			// Use the path-parameter id; don't trust the one in the object
			publishRequest.setRequestId(requestId);
			// Update the existing row
			publishRequestRepository.save(publishRequest);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updatePublishRequest failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePublishRequest failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the request with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{requestId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deletePublishRequest(@PathVariable("requestId") long requestId,
			HttpServletResponse response) {
		logger.info("deletePublishRequest: requestId {}", requestId);
		try {
			publishRequestRepository.delete(requestId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deletePublishRequest failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePublishRequest failed", ex);
		}
	}

}
