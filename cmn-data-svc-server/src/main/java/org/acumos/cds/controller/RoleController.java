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
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.repository.RoleFunctionRepository;
import org.acumos.cds.repository.RoleRepository;
import org.acumos.cds.service.RoleSearchService;
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

/**
 * Answers REST requests to get, add, update and delete roles.
 * 
 * https://stackoverflow.com/questions/942951/rest-api-error-return-good-practices
 */
@Controller
@RequestMapping(value = "/" + CCDSConstants.ROLE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private RoleSearchService roleSearchService;
	@Autowired
	private RoleFunctionRepository roleFunctionRepository;

	@ApiOperation(value = "Gets the count of roles.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public CountTransport getRoleCount() {
		logger.info("getRoleCount");
		Long count = roleRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of roles, optionally sorted on fields.", //
			response = MLPRole.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPRole> getRoles(Pageable pageable) {
		logger.info("getRoles query {}", pageable);
		return roleRepository.findAll(pageable);
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many class field names.
	 */
	private static final String nameField = "name";
	private static final String activeField = "active";

	@ApiOperation(value = "Searches for roles with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPRole.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object searchRoles(//
			@ApiParam(value = "Junction", allowableValues = "a,o") //
			@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Name") //
			@RequestParam(name = nameField, required = false) String name, //
			@ApiParam(value = "Active") //
			@RequestParam(name = activeField, required = false) Boolean active, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.info("searchRoles enter");
		boolean isOr = junction != null && "o".equals(junction);
		Map<String, Object> queryParameters = new HashMap<>();
		if (name != null)
			queryParameters.put(nameField, name);
		if (active != null)
			queryParameters.put(activeField, active);
		if (queryParameters.size() == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return roleSearchService.findRoles(queryParameters, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchRoles failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchRoles failed", ex);
		}
	}

	@ApiOperation(value = "Gets the entity for the specified ID. Returns bad request if the ID is not found.", //
			response = MLPRole.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getRole(@PathVariable("roleId") String roleId, HttpServletResponse response) {
		logger.info("getRole roleId {}", roleId);
		MLPRole da = roleRepository.findOne(roleId);
		if (da == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + roleId, null);
		}
		return da;
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPRole.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createRole(@RequestBody MLPRole role, HttpServletResponse response) {
		logger.info("createRole role {}", role);
		try {
			String id = role.getRoleId();
			if (id != null) {
				UUID.fromString(id);
				if (roleRepository.findOne(id) != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Create a new row
			Object result = roleRepository.save(role);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.ROLE_PATH + "/" + role.getRoleId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createRole failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createRole failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateRole(@PathVariable("roleId") String roleId, @RequestBody MLPRole role,
			HttpServletResponse response) {
		logger.info("updateRole roleId {}", roleId);
		// Get the existing one
		MLPRole existing = roleRepository.findOne(roleId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + roleId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			role.setRoleId(roleId);
			// Update the existing row
			roleRepository.save(role);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateRole failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateRole failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteRole(@PathVariable("roleId") String roleId, HttpServletResponse response) {
		logger.info("deleteRole roleId {}", roleId);
		try {
			Iterable<MLPRoleFunction> fns = roleFunctionRepository.findByRoleId(roleId);
			if (fns != null)
				roleFunctionRepository.delete(fns);
			roleRepository.delete(roleId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteRole failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRole failed", ex);
		}
	}

	@ApiOperation(value = "Gets the functions for the specified role. Returns bad request if the ID is not found.", //
			response = MLPRoleFunction.class, responseContainer = "List")
	@RequestMapping(value = "/{roleId}/" + CCDSConstants.FUNCTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getListOfRoleFunc(@PathVariable("roleId") String roleId, HttpServletResponse response) {
		logger.info("getListOfRoleFunc roleId {}", roleId);
		if (roleRepository.findOne(roleId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + roleId, null);
		}
		return roleFunctionRepository.findByRoleId(roleId);
	}

	@ApiOperation(value = "Gets the role function for the specified role and function IDs. Returns bad request if an ID is not found.", //
			response = MLPRoleFunction.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}/" + CCDSConstants.FUNCTION_PATH + "/{functionId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getRoleFunc(@PathVariable("roleId") String roleId, @PathVariable("functionId") String functionId,
			HttpServletResponse response) {
		logger.info("getRoleFunc roleId {} functionId {}", roleId, functionId);
		MLPRoleFunction rf = roleFunctionRepository.findOne(functionId);
		if (rf == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + functionId, null);
		}
		return rf;
	}

	@ApiOperation(value = "Creates a new entity and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPRoleFunction.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}/" + CCDSConstants.FUNCTION_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object createRoleFunc(@PathVariable("roleId") String roleId, @RequestBody MLPRoleFunction roleFunction,
			HttpServletResponse response) {
		logger.info("createRoleFunc: function {}", roleFunction);
		if (roleRepository.findOne(roleId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + roleId, null);
		}
		Object result;
		try {
			// Null out any existing ID to get an auto-generated ID
			roleFunction.setRoleFunctionId(null);
			// Add the solution, which the client cannot provide
			roleFunction.setRoleId(roleId);
			// Create a new row
			result = roleFunctionRepository.save(roleFunction);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createRoleFunc failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createRoleFunc failed", cve);
		}
		return result;
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}/" + CCDSConstants.FUNCTION_PATH + "/{functionId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateRoleFunc(@PathVariable("roleId") String roleId, @PathVariable("functionId") String functionId,
			@RequestBody MLPRoleFunction roleFunction, HttpServletResponse response) {
		logger.info("updateRoleFunc roleId {} functionId {}", roleId, functionId);
		if (roleRepository.findOne(roleId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + roleId, null);
		}
		if (roleFunctionRepository.findOne(functionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + functionId, null);
		}
		try {
			// Use the validated values
			roleFunction.setRoleFunctionId(functionId);
			roleFunction.setRoleId(roleId);
			// Update
			roleFunctionRepository.save(roleFunction);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateRoleFunc failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateRoleFunc failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{roleId}/" + CCDSConstants.FUNCTION_PATH + "/{functionId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteRoleFunc(@PathVariable("roleId") String roleId,
			@PathVariable("functionId") String functionId, HttpServletResponse response) {
		logger.info("deleteRoleFunc roleId {} funcId {}", roleId, functionId);
		try {
			roleFunctionRepository.delete(functionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteRoleFunc failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRoleFunc failed", ex);
		}
	}

}
