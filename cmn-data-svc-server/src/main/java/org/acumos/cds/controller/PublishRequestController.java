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
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/" + CCDSConstants.PUBLISH_REQUEST_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class PublishRequestController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private PublishRequestRepository publishRequestRepository;
	@Autowired
	private PublishRequestSearchService publishRequestSearchService;

	/**
	 * 
	 * @param pageRequest
	 *            Page and sort criteria
	 * @return List of publish requests, for serialization as JSON
	 */
	@ApiOperation(value = "Gets a page of publish requests, optionally sorted on fields.", response = MLPPublishRequest.class, responseContainer = "Page")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPPublishRequest> getPublishRequests(Pageable pageRequest) {
		logger.info("getPublishRequests {}", pageRequest);
		Page<MLPPublishRequest> result = publishRequestRepository.findAll(pageRequest);
		return result;
	}

	/**
	 * @param queryParameters
	 *            Map of String (field name) to String (value) for restricting the
	 *            query. For example, all requests submitted by one user.
	 * @param pageRequest
	 *            Page and sort criteria
	 * @param response
	 *            HttpServletResponse
	 * @return List of solutions
	 */
	@ApiOperation(value = "Searches for publish requests using the field name - field value pairs specified as query parameters. Defaults to and (conjunction); send junction query parameter = o for or (disjunction).", response = MLPPublishRequest.class, responseContainer = "Page")
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object searchPublishRequests(@RequestParam MultiValueMap<String, String> queryParameters,
			HttpServletResponse response, Pageable pageRequest) {
		logger.info("searchPublishRequests {}", queryParameters);
		cleanPageableParameters(queryParameters);
		List<String> junction = queryParameters.remove(CCDSConstants.JUNCTION_QUERY_PARAM);
		boolean isOr = junction != null && junction.size() == 1 && "o".equals(junction.get(0));
		if (queryParameters.size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			Map<String, Object> convertedQryParm = convertQueryParameters(MLPPublishRequest.class, queryParameters);
			Object result = publishRequestSearchService.findPublishRequests(convertedQryParm, isOr, pageRequest);
			return result;
		} catch (Exception ex) {
			logger.warn("searchPublishRequests failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchPublishRequests failed", ex);
		}
	}

	/**
	 * @param publishRequestId
	 *            Path parameter with row ID
	 * @param response
	 *            HttpServletResponse
	 * @return PublishRequest for valid ID; an error otherwise.
	 */
	@ApiOperation(value = "Gets the publish request for the specified ID.", response = MLPPublishRequest.class)
	@RequestMapping(value = "/{publishRequestId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getPublishRequest(@PathVariable("publishRequestId") long publishRequestId,
			HttpServletResponse response) {
		logger.info("getPublishRequest: publishRequestId {}", publishRequestId);
		MLPPublishRequest sr = publishRequestRepository.findOne(publishRequestId);
		if (sr == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + publishRequestId, null);
		}
		return sr;
	}

	/**
	 * @param publishRequest
	 *            Instance to save. A new ID will be generated;
	 * @param response
	 *            HttpServletResponse
	 * @return Entity on success; error on failure.
	 */
	@ApiOperation(value = "Creates a new publish request.", response = MLPPublishRequest.class)
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createPublishRequest(@RequestBody MLPPublishRequest publishRequest, HttpServletResponse response) {
		logger.info("createPublishRequest: enter");
		try {
			// Validate enum codes
			super.validateCode(publishRequest.getStatusCode(), CodeNameType.PUBLISH_REQUEST_STATUS);
			// Create a new row
			MLPPublishRequest result = publishRequestRepository.save(publishRequest);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.PUBLISH_REQUEST_PATH + "/" + result.getRequestId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPublishRequest failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPublishRequest failed", cve);
		}
	}

	/**
	 * @param requestId
	 *            Path parameter with the row ID
	 * @param publishRequest
	 *            data to be updated
	 * @param response
	 *            HttpServletResponse
	 * @return Success or failure indicator
	 */
	@ApiOperation(value = "Updates a publish request.", response = SuccessTransport.class)
	@RequestMapping(value = "/{requestId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updatePublishRequest(@PathVariable("publishRequest") long requestId,
			@RequestBody MLPPublishRequest publishRequest, HttpServletResponse response) {
		logger.info("updatePublishRequest: requestId {}", requestId);
		// Get the existing one
		MLPPublishRequest existing = publishRequestRepository.findOne(requestId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + requestId, null);
		}
		try {
			// Validate enum codes
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

	/**
	 * 
	 * @param requestId
	 *            Path parameter with the row ID
	 * @param response
	 *            HttpServletResponse
	 * @return Transport model with success or failure
	 */
	@ApiOperation(value = "Deletes the publish request with the specified ID.", response = SuccessTransport.class)
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
