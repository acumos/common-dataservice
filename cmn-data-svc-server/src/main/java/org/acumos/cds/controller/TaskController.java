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
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.repository.TaskRepository;
import org.acumos.cds.repository.TaskStepResultRepository;
import org.acumos.cds.service.StepResultSearchService;
import org.acumos.cds.service.TaskSearchService;
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
 * Provides endpoints to manage task and step result records.
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
@RequestMapping(value = "/" + CCDSConstants.TASK_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private TaskSearchService taskSearchService;
	@Autowired
	private TaskStepResultRepository stepResultRepository;
	@Autowired
	private StepResultSearchService stepResultSearchService;

	@ApiOperation(value = "Gets a page of tasks, optionally sorted on fields. Returns empty if none are found.", //
			response = MLPTask.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPTask> getTasks(Pageable pageable) {
		logger.debug("getTasks query {}", pageable);
		return taskRepository.findAll(pageable);
	}

	@ApiOperation(value = "Gets the task for the specified ID. Returns null if the ID is not found.", //
			response = MLPTask.class)
	@RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
	public MLPTask getTask(@PathVariable("taskId") Long taskId) {
		logger.debug("getTask taskId {}", taskId);
		Optional<MLPTask> da = taskRepository.findById(taskId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Searches for tasks with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchTasks(@ApiParam(value = "Junction", allowableValues = "a,o") //
	@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Name") //
			@RequestParam(name = NAME, required = false) String name, //
			@ApiParam(value = "Status code") //
			@RequestParam(name = STATUS_CODE, required = false) String statusCode, //
			@ApiParam(value = "Task ID") //
			@RequestParam(name = TASK_ID, required = false) Long taskId, //
			@ApiParam(value = "Tracking ID") //
			@RequestParam(name = TRACKING_ID, required = false) String trackingId, //
			@ApiParam(value = "User ID") //
			@RequestParam(name = USER_ID, required = false) String userId, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchStepResults enter");
		boolean isOr = junction != null && "o".equals(junction);
		if (name == null && statusCode == null && taskId == null && trackingId == null && userId == null) {
			logger.warn("searchStepResults missing query");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return taskSearchService.findTasks(taskId, name, statusCode, trackingId, userId, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchTasks failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchTasks failed", ex);
		}
	}

	@ApiOperation(value = "Creates a new task and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPTask.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createTask(@RequestBody MLPTask task, HttpServletResponse response) {
		logger.debug("createTask task {}", task);
		try {
			Long id = task.getTaskId();
			if (id != null) {
				if (taskRepository.findById(id).isPresent()) {
					logger.warn("createTask failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			super.validateCode(task.getStatusCode(), CodeNameType.TASK_STEP_STATUS);
			// Create a new row
			MLPTask result = taskRepository.save(task);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.TASK_PATH + "/" + task.getTaskId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createTask failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createTask failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing task with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{taskId}", method = RequestMethod.PUT)
	public MLPTransportModel updateTask(@PathVariable("taskId") Long taskId, @RequestBody MLPTask task,
			HttpServletResponse response) {
		logger.debug("updateTask: taskId {}", taskId);
		// Check the existing one
		if (!taskRepository.findById(taskId).isPresent()) {
			logger.warn("updateTask failed on ID {}", taskId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + taskId, null);
		}
		try {
			super.validateCode(task.getStatusCode(), CodeNameType.TASK_STEP_STATUS);
			// Use the path-parameter id; don't trust the one in the object
			task.setTaskId(taskId);
			// Update the existing row
			taskRepository.save(task);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateTask failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateTask failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the task with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteTask(@PathVariable("taskId") Long taskId, HttpServletResponse response) {
		logger.debug("deleteTask: taskId {}", taskId);
		try {
			taskRepository.deleteById(taskId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteTask failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteTask failed", ex);
		}
	}

	@ApiOperation(value = "Gets a task's step results. Answers empty if none are found.", //
			response = MLPTaskStepResult.class, responseContainer = "List")
	@ApiPageable
	@RequestMapping(value = "/{taskId}/" + CCDSConstants.STEP_RESULT_PATH, method = RequestMethod.GET)
	public Iterable<MLPTaskStepResult> getTaskStepResults(@PathVariable("taskId") Long taskId) {
		logger.debug("getTaskStepResults {}", taskId);
		return stepResultRepository.findByTaskId(taskId);
	}

	@ApiOperation(value = "Gets the specified step result. Returns null if the ID is not found.", //
			response = MLPTaskStepResult.class)
	@RequestMapping(value = "/{taskId}/" + CCDSConstants.STEP_RESULT_PATH
			+ "/{stepResultId}", method = RequestMethod.GET)
	public MLPTaskStepResult getTaskStepResult(@PathVariable("stepResultId") Long stepResultId) {
		logger.debug("getStepResult: stepResultId {}", stepResultId);
		Optional<MLPTaskStepResult> sr = stepResultRepository.findById(stepResultId);
		return sr.isPresent() ? sr.get() : null;
	}

	/*
	 * This method was an early attempt to provide a search feature. Originally
	 * written with a generic map request parameter to avoid binding field names,
	 * but that is not supported by Swagger web UI. Now allows use from that web UI
	 * at the cost of hard-coding many class field names.
	 */
	private static final String TASK_ID = "taskId";
	private static final String TRACKING_ID = "trackingId";
	private static final String STEP_CODE = "stepCode";
	private static final String SOLUTION_ID = "solutionId";
	private static final String REVISION_ID = "revisionId";
	private static final String ARTIFACT_ID = "artifactId";
	private static final String USER_ID = "userId";
	private static final String STATUS_CODE = "statusCode";
	private static final String NAME = "name";

	@ApiOperation(value = "Searches for step results with attributes matching the values specified as query parameters. " //
			+ "Defaults to match all (conjunction); send junction query parameter '_j=o' to match any (disjunction).", //
			response = MLPPublishRequest.class, responseContainer = "Page")
	@ApiPageable
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/" + CCDSConstants.STEP_RESULT_PATH + "/"
			+ CCDSConstants.SEARCH_PATH, method = RequestMethod.GET)
	public Object searchStepResults(@ApiParam(value = "Junction", allowableValues = "a,o") //
	@RequestParam(name = CCDSConstants.JUNCTION_QUERY_PARAM, required = false) String junction, //
			@ApiParam(value = "Task ID") //
			@RequestParam(name = TASK_ID, required = false) Long taskId, //
			@ApiParam(value = "Step code") //
			@RequestParam(name = STEP_CODE, required = false) String stepCode, //
			@ApiParam(value = "Solution ID") //
			@RequestParam(name = SOLUTION_ID, required = false) String solutionId, //
			@ApiParam(value = "Revision ID") //
			@RequestParam(name = REVISION_ID, required = false) String revisionId, //
			@ApiParam(value = "Artifact ID") //
			@RequestParam(name = ARTIFACT_ID, required = false) String artifactId, //
			@ApiParam(value = "Name") //
			@RequestParam(name = NAME, required = false) String name, //
			@ApiParam(value = "Status code") //
			@RequestParam(name = STATUS_CODE, required = false) String statusCode, //
			Pageable pageRequest, HttpServletResponse response) {
		logger.debug("searchStepResults enter");
		boolean isOr = junction != null && "o".equals(junction);
		if (taskId == null && stepCode == null && solutionId == null && revisionId == null && artifactId == null
				&& name == null && statusCode == null) {
			logger.warn("searchStepResults missing query");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Missing query", null);
		}
		try {
			return stepResultSearchService.findStepResults(taskId, stepCode, solutionId, revisionId, artifactId, name,
					statusCode, isOr, pageRequest);
		} catch (Exception ex) {
			logger.error("searchStepResults failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return new ErrorTransport(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					ex.getCause() != null ? ex.getCause().getMessage() : "searchStepResults failed", ex);
		}
	}

	@ApiOperation(value = "Adds a new step result with a generated ID to the specified task. Returns bad request on constraint violation etc.", //
			response = MLPTaskStepResult.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{taskId}/" + CCDSConstants.STEP_RESULT_PATH, method = RequestMethod.POST)
	public MLPResponse addTaskStepResult(@PathVariable("taskId") Long taskId, @RequestBody MLPTaskStepResult stepResult,
			HttpServletResponse response) {
		logger.debug("addTaskStepResult: enter");
		if (!taskRepository.findById(taskId).isPresent()) {
			logger.warn("addTaskStepResult failed on task ID {}", taskId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + taskId);
		}
		try {
			// Validate enum codes
			super.validateCode(stepResult.getStepCode(), CodeNameType.STEP_TYPE);
			super.validateCode(stepResult.getStatusCode(), CodeNameType.TASK_STEP_STATUS);
			stepResult.setTaskId(taskId);
			// Force creation of new ID
			stepResult.setStepResultId(null);
			MLPTaskStepResult result = stepResultRepository.save(stepResult);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.STEP_RESULT_PATH + "/" + result.getStepResultId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("addTaskStepResult failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "addTaskStepResult failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing step result with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{taskId}/" + CCDSConstants.STEP_RESULT_PATH
			+ "/{stepResultId}", method = RequestMethod.PUT)
	public MLPTransportModel updateTaskStepResult(@PathVariable("taskId") Long taskId,
			@PathVariable("stepResultId") Long stepResultId, @RequestBody MLPTaskStepResult stepResult,
			HttpServletResponse response) {
		logger.debug("updateTaskStepResult: stepResultId {}", stepResultId);
		// Check the existing ones
		if (!taskRepository.findById(taskId).isPresent()) {
			logger.warn("updateStepResult failed on task ID {}", taskId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + taskId);
		}
		if (!stepResultRepository.findById(stepResultId).isPresent()) {
			logger.warn("updateStepResult failed on step result ID {}", stepResultId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + stepResultId, null);
		}
		try {
			// Validate enum codes
			super.validateCode(stepResult.getStepCode(), CodeNameType.STEP_TYPE);
			super.validateCode(stepResult.getStatusCode(), CodeNameType.TASK_STEP_STATUS);
			// Use the path-parameter id; don't trust the one in the object
			stepResult.setStepResultId(stepResultId);
			// Update the existing row
			stepResultRepository.save(stepResult);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateTaskStepResult failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateTaskStepResult failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the step result with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{taskId}/" + CCDSConstants.STEP_RESULT_PATH
			+ "/{stepResultId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteTaskStepResult(@PathVariable("stepResultId") Long stepResultId,
			HttpServletResponse response) {
		logger.debug("deleteTaskStepResult: stepResultId {}", stepResultId);
		try {
			stepResultRepository.deleteById(stepResultId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteTaskStepResult failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteTaskStepResult failed", ex);
		}
	}

}
