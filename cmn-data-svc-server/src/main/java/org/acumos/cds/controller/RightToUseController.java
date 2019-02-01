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

package org.acumos.cds.controller;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRightToUse_;
import org.acumos.cds.domain.MLPUserRtuMap;
import org.acumos.cds.repository.RightToUseRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.repository.UserRtuMapRepository;
import org.acumos.cds.service.RightToUseSearchService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Answers REST requests to get, add, update and delete right-to-use records.
 * <P>
 * Validation design decisions:
 * <OL>
 * <LI>Keep queries fast, so check nothing on read.</LI>
 * <LI>Provide useful messages on failure, so check everything on write.</LI>
 * <LI>Also see:
 * https://stackoverflow.com/questions/942951/rest-api-error-return-good-practices
 * </LI>
 * </OL>
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.RTU_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class RightToUseController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private RightToUseRepository rtuRepository;
	@Autowired
	private RightToUseSearchService rtuSearchService;
	@Autowired
	private UserRtuMapRepository rtuMapRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets the right-to-use record for the specified ID. Returns null if the ID is not found.", //
			response = MLPRightToUse.class)
	@RequestMapping(value = "/{rtuId}", method = RequestMethod.GET)
	public MLPRightToUse getRightToUse(@PathVariable("rtuId") Long rtuId) {
		logger.debug("getRightToUse rtuId {}", rtuId);
		Optional<MLPRightToUse> da = rtuRepository.findById(rtuId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Gets a page of right-to-use records, optionally sorted on fields. Returns empty if none are found.", //
			response = MLPRightToUse.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPRightToUse> getRightToUses(Pageable pageable) {
		logger.debug("getRightToUses query {}", pageable);
		return rtuRepository.findAll(pageable);
	}

	@ApiOperation(value = "Gets a list of right-to-use records for the specified solution and user. Returns empty if none are found.", //
			response = MLPRightToUse.class, responseContainer = "List")
	@RequestMapping(value = "/" + CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.USER_PATH
			+ "/{userId}", method = RequestMethod.GET)
	public Iterable<MLPRightToUse> getRightToUsesForSolAndUser(@PathVariable("solutionId") String solutionId,
			@PathVariable("userId") String userId) {
		logger.debug("getRightToUsesForSolAndUser solutionId {} userId {}", solutionId, userId);
		return rtuRepository.findBySolutionIdUserId(solutionId, userId);
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI.
	 */
	@ApiOperation(value = "Searches for right-to-use records with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPRightToUse.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchRightToUses(//
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@RequestParam(name = MLPRightToUse_.LUM_ID, required = false) String lumId, //
			@RequestParam(name = MLPRightToUse_.SOLUTION_ID, required = false) String solutionId, //
			@RequestParam(name = MLPRightToUse_.SITE, required = false) Boolean site, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchRightToUses enter");
		boolean isOr = junction != null && "o".equals(junction);

		if (lumId == null && solutionId == null && site == null) {
			logger.warn("searchRightToUses missing query");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return rtuSearchService.findRtus(lumId, solutionId, site, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchRightToUses failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchRightToUses failed", ex);
		}
	}

	@ApiOperation(value = "Creates a new entity and generates an ID. Returns bad request on constraint violation etc.", //
			response = MLPRightToUse.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createRightToUse(@RequestBody MLPRightToUse rtu, HttpServletResponse response) {
		logger.debug("createRightToUse rtu {}", rtu);
		try {
			// Create a new row
			rtu.setRtuId(null);
			MLPRightToUse result = rtuRepository.save(rtu);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.RTU_PATH + "/" + rtu.getRtuId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createRightToUse took exception {} on data {}", cve.toString(), rtu.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createRightToUse failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{rtuId}", method = RequestMethod.PUT)
	public MLPTransportModel updateRightToUse(@PathVariable("rtuId") Long rtuId, @RequestBody MLPRightToUse rtu,
			HttpServletResponse response) {
		logger.debug("updateRightToUse rtuId {}", rtuId);
		// Check the existing one
		if (!rtuRepository.findById(rtuId).isPresent()) {
			logger.warn("updateRightToUse failed on ID {}", rtuId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + rtuId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			rtu.setRtuId(rtuId);
			// Update the existing row
			rtuRepository.save(rtu);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateRightToUse took exception {} on data {}", cve.toString(), rtu.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateRightToUse failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{rtuId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteRightToUse(@PathVariable("rtuId") Long rtuId, HttpServletResponse response) {
		logger.debug("deleteRightToUse rtuId {}", rtuId);
		try {
			rtuRepository.deleteById(rtuId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteRightToUse failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRightToUse failed", ex);
		}
	}

	@ApiOperation(value = "Adds the specified user to the specified RTU. Answers bad request if an ID is invalid.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{rtuId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.POST)
	public MLPResponse addUserToRtu(@PathVariable("rtuId") Long rtuId, @PathVariable("userId") String userId,
			@RequestBody MLPUserRtuMap map, HttpServletResponse response) {
		logger.debug("addUserToRtu rtuId {} userId {}", rtuId, userId);
		if (!rtuRepository.findById(rtuId).isPresent()) {
			logger.warn("addUserToRtu: failed on RTU ID {}", rtuId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + rtuId, null);
		}
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("addUserToRtu: failed on user ID {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		// Use path parameters only
		map.setRightToUseId(rtuId);
		map.setUserId(userId);
		rtuMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Removes the specified user from the specified RTU.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{rtuId}/" + CCDSConstants.USER_PATH + "/{userId}", method = RequestMethod.DELETE)
	public MLPTransportModel dropUserFromRtu(@PathVariable("rtuId") Long rtuId, @PathVariable("userId") String userId,
			HttpServletResponse response) {
		logger.debug("dropUserFromRtu rtuId {} userId {}", rtuId, userId);
		try {
			MLPUserRtuMap.UserRtuMapPK pk = new MLPUserRtuMap.UserRtuMapPK(userId, rtuId);
			rtuMapRepository.deleteById(pk);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropUserFromRtu failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropUserFromRtu failed", ex);
		}
	}

}
