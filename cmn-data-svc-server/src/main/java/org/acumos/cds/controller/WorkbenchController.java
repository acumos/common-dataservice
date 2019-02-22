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
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPNotebook;
import org.acumos.cds.domain.MLPPipeline;
import org.acumos.cds.domain.MLPProjNotebookMap;
import org.acumos.cds.domain.MLPProjPipelineMap;
import org.acumos.cds.domain.MLPProject;
import org.acumos.cds.domain.MLPUserNotebookMap;
import org.acumos.cds.domain.MLPUserPipelineMap;
import org.acumos.cds.domain.MLPUserProjMap;
import org.acumos.cds.repository.NotebookRepository;
import org.acumos.cds.repository.PipelineRepository;
import org.acumos.cds.repository.ProjNotebookMapRepository;
import org.acumos.cds.repository.ProjPipelineMapRepository;
import org.acumos.cds.repository.ProjectRepository;
import org.acumos.cds.repository.UserNotebookMapRepository;
import org.acumos.cds.repository.UserPipelineMapRepository;
import org.acumos.cds.repository.UserProjectMapRepository;
import org.acumos.cds.repository.UserRepository;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Provides REST endpoints for managing projects, notebooks and pipelines.
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
@RequestMapping(value = "/" + CCDSConstants.WORKBENCH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkbenchController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private NotebookRepository notebookRepository;
	@Autowired
	private PipelineRepository pipelineRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProjNotebookMapRepository projNbMapRepository;
	@Autowired
	private ProjPipelineMapRepository projPlMapRepository;
	@Autowired
	private UserProjectMapRepository userProjMapRepository;
	@Autowired
	private UserNotebookMapRepository userNbMapRepository;
	@Autowired
	private UserPipelineMapRepository userPlMapRepository;

	@ApiOperation(value = "Gets a page of projects, optionally sorted; empty if none are found.", //
			response = MLPProject.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.PROJECT_PATH, method = RequestMethod.GET)
	public Page<MLPProject> getProjects(Pageable pageRequest) {
		logger.debug("getProjects {}", pageRequest);
		return projectRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the project for the specified ID. Returns null if the ID is not found.", //
			response = MLPProject.class)
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}", method = RequestMethod.GET)
	public MLPProject getProject(@PathVariable("projectId") String projectId) {
		logger.debug("getProject ID {}", projectId);
		Optional<MLPProject> da = projectRepository.findById(projectId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new project and generates an ID if needed. Returns bad request on bad URI, constraint violation etc.", //
			response = MLPProject.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PROJECT_PATH, method = RequestMethod.POST)
	public MLPResponse createProject(@RequestBody MLPProject project, HttpServletResponse response) {
		logger.debug("createProject project {}", project);
		try {
			String id = project.getProjectId();
			if (id != null) {
				UUID.fromString(id);
				if (projectRepository.findById(id).isPresent()) {
					logger.warn("createProject failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Validate the URLs
			if (project.getRepositoryUrl() != null)
				new URL(project.getRepositoryUrl());
			if (project.getServiceUrl() != null)
				new URL(project.getServiceUrl());
			// Create a new row
			MLPProject result = projectRepository.save(project);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.WORKBENCH_PATH + "/" + CCDSConstants.PROJECT_PATH + "/" + project.getProjectId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createProject took exception {} on data {}", cve.toString(), project.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createProject failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing project with the supplied data. Returns bad request on bad URI, constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}", method = RequestMethod.PUT)
	public MLPResponse updateProject(@PathVariable("projectId") String projectId, @RequestBody MLPProject project,
			HttpServletResponse response) {
		logger.debug("updateProject ID {}", projectId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		Optional<MLPProject> existing = projectRepository.findById(projectId);
		if (!existing.isPresent()) {
			logger.warn("updateProject failed on ID {}", projectId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + projectId, null);
		}
		try {
			// Validate the URI
			// Validate the URLs
			if (project.getRepositoryUrl() != null)
				new URL(project.getRepositoryUrl());
			if (project.getServiceUrl() != null)
				new URL(project.getServiceUrl());
			// Use the path-parameter id; don't trust the one in the object
			project.setProjectId(projectId);
			// Update the existing row
			projectRepository.save(project);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateProject took exception {} on data {}", cve.toString(), project.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateProject failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the project with the specified ID. Cascades delete to related records.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteProject(@PathVariable("projectId") String projectId, HttpServletResponse response) {
		logger.debug("deleteProject ID {}", projectId);
		try {
			projectRepository.deleteById(projectId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteProject failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteProject failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of notebooks, optionally sorted; empty if none are found.", //
			response = MLPNotebook.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.NOTEBOOK_PATH, method = RequestMethod.GET)
	public Page<MLPNotebook> getNotebooks(Pageable pageRequest) {
		logger.debug("getNotebooks {}", pageRequest);
		return notebookRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the notebook for the specified ID. Returns null if the ID is not found.", //
			response = MLPNotebook.class)
	@RequestMapping(value = CCDSConstants.NOTEBOOK_PATH + "/{notebookId}", method = RequestMethod.GET)
	public MLPNotebook getNotebook(@PathVariable("notebookId") String notebookId) {
		logger.debug("getNotebook ID {}", notebookId);
		Optional<MLPNotebook> da = notebookRepository.findById(notebookId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new notebook and generates an ID if needed. Returns bad request on bad URI, constraint violation etc.", //
			response = MLPNotebook.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.NOTEBOOK_PATH, method = RequestMethod.POST)
	public MLPResponse createNotebook(@RequestBody MLPNotebook notebook, HttpServletResponse response) {
		logger.debug("createNotebook notebook {}", notebook);
		try {
			String id = notebook.getNotebookId();
			if (id != null) {
				UUID.fromString(id);
				if (notebookRepository.findById(id).isPresent()) {
					logger.warn("createNotebook failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Validate the URLs
			if (notebook.getRepositoryUrl() != null)
				new URL(notebook.getRepositoryUrl());
			if (notebook.getServiceUrl() != null)
				new URL(notebook.getServiceUrl());
			// Create a new row
			MLPNotebook result = notebookRepository.save(notebook);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.WORKBENCH_PATH + "/" + CCDSConstants.NOTEBOOK_PATH + "/" + notebook.getNotebookId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createNotebook took exception {} on data {}", cve.toString(), notebook.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createNotebook failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing notebook with the supplied data. Returns bad request on bad URI, constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.NOTEBOOK_PATH + "/{notebookId}", method = RequestMethod.PUT)
	public MLPResponse updateNotebook(@PathVariable("notebookId") String notebookId, @RequestBody MLPNotebook notebook,
			HttpServletResponse response) {
		logger.debug("updateNotebook ID {}", notebookId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		Optional<MLPNotebook> existing = notebookRepository.findById(notebookId);
		if (!existing.isPresent()) {
			logger.warn("updateNotebook failed on ID {}", notebookId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + notebookId, null);
		}
		try {
			// Validate the URI
			// Validate the URLs
			if (notebook.getRepositoryUrl() != null)
				new URL(notebook.getRepositoryUrl());
			if (notebook.getServiceUrl() != null)
				new URL(notebook.getServiceUrl());
			// Use the path-parameter id; don't trust the one in the object
			notebook.setNotebookId(notebookId);
			// Update the existing row
			notebookRepository.save(notebook);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateNotebook took exception {} on data {}", cve.toString(), notebook.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateNotebook failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the notebook with the specified ID. Cascades delete to related records.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.NOTEBOOK_PATH + "/{notebookId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteNotebook(@PathVariable("notebookId") String notebookId,
			HttpServletResponse response) {
		logger.debug("deleteNotebook ID {}", notebookId);
		try {
			notebookRepository.deleteById(notebookId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteNotebook failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteNotebook failed", ex);
		}
	}

	@ApiOperation(value = "Maps the specified notebook to the specified project.", //
			response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}" + CCDSConstants.NOTEBOOK_PATH
			+ "/{notebookId}", method = RequestMethod.POST)
	public MLPResponse addNotebookToProject(@PathVariable("projectId") String projectId,
			@PathVariable("notebookId") String notebookId, HttpServletResponse response) {
		logger.debug("addNotebookToProject projectId {} notebookId {}", projectId, notebookId);
		if (!projectRepository.findById(projectId).isPresent()) {
			logger.warn("addNotebookToProject: failed on project ID {}", projectId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + projectId, null);
		}
		if (!notebookRepository.findById(notebookId).isPresent()) {
			logger.warn("addNotebookToProject: failed on group ID {}", notebookId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + notebookId, null);
		}
		projNbMapRepository.save(new MLPProjNotebookMap(projectId, notebookId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Unmaps the specified notebook from the specified project.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}" + CCDSConstants.NOTEBOOK_PATH
			+ "/{notebookId}", method = RequestMethod.DELETE)
	public MLPResponse dropNotebookFromProject(@PathVariable("projectId") String projectId,
			@PathVariable("notebookId") String notebookId, HttpServletResponse response) {
		logger.debug("dropNotebookFromProject projectId {} notebookId {}", projectId, notebookId);
		try {
			projNbMapRepository.deleteById(new MLPProjNotebookMap.ProjNbMapPK(projectId, notebookId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropNotebookFromProject failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropNotebookFromProject failed", ex);
		}
	}

	@ApiOperation(value = "Gets a page of pipelines, optionally sorted; empty if none are found.", //
			response = MLPPipeline.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.PIPELINE_PATH, method = RequestMethod.GET)
	public Page<MLPPipeline> getPipelines(Pageable pageRequest) {
		logger.debug("getPipelines {}", pageRequest);
		return pipelineRepository.findAll(pageRequest);
	}

	@ApiOperation(value = "Gets the pipeline for the specified ID. Returns null if the ID is not found.", //
			response = MLPPipeline.class)
	@RequestMapping(value = CCDSConstants.PIPELINE_PATH + "/{pipelineId}", method = RequestMethod.GET)
	public MLPPipeline getPipeline(@PathVariable("pipelineId") String pipelineId) {
		logger.debug("getPipeline ID {}", pipelineId);
		Optional<MLPPipeline> da = pipelineRepository.findById(pipelineId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new pipeline and generates an ID if needed. Returns bad request on bad URI, constraint violation etc.", //
			response = MLPPipeline.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PIPELINE_PATH, method = RequestMethod.POST)
	public MLPResponse createPipeline(@RequestBody MLPPipeline pipeline, HttpServletResponse response) {
		logger.debug("createPipeline pipeline {}", pipeline);
		try {
			String id = pipeline.getPipelineId();
			if (id != null) {
				UUID.fromString(id);
				if (pipelineRepository.findById(id).isPresent()) {
					logger.warn("createPipeline failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Validate the URLs
			if (pipeline.getRepositoryUrl() != null)
				new URL(pipeline.getRepositoryUrl());
			if (pipeline.getServiceUrl() != null)
				new URL(pipeline.getServiceUrl());
			// Create a new row
			MLPPipeline result = pipelineRepository.save(pipeline);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION,
					CCDSConstants.WORKBENCH_PATH + "/" + CCDSConstants.PIPELINE_PATH + "/" + pipeline.getPipelineId());
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createPipeline took exception {} on data {}", cve.toString(), pipeline.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPipeline failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing pipeline with the supplied data. Returns bad request on bad URI, constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PIPELINE_PATH + "/{pipelineId}", method = RequestMethod.PUT)
	public MLPResponse updatePipeline(@PathVariable("pipelineId") String pipelineId, @RequestBody MLPPipeline pipeline,
			HttpServletResponse response) {
		logger.debug("updatePipeline ID {}", pipelineId);
		// Check for existing because the Hibernate save() method doesn't distinguish
		Optional<MLPPipeline> existing = pipelineRepository.findById(pipelineId);
		if (!existing.isPresent()) {
			logger.warn("updatePipeline failed on ID {}", pipelineId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + pipelineId, null);
		}
		try {
			// Validate the URI
			// Validate the URLs
			if (pipeline.getRepositoryUrl() != null)
				new URL(pipeline.getRepositoryUrl());
			if (pipeline.getServiceUrl() != null)
				new URL(pipeline.getServiceUrl());
			// Use the path-parameter id; don't trust the one in the object
			pipeline.setPipelineId(pipelineId);
			// Update the existing row
			pipelineRepository.save(pipeline);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updatePipeline took exception {} on data {}", cve.toString(), pipeline.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePipeline failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the pipeline with the specified ID. Cascades delete to related records.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PIPELINE_PATH + "/{pipelineId}", method = RequestMethod.DELETE)
	public MLPTransportModel deletePipeline(@PathVariable("pipelineId") String pipelineId,
			HttpServletResponse response) {
		logger.debug("deletePipeline ID {}", pipelineId);
		try {
			pipelineRepository.deleteById(pipelineId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deletePipeline failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePipeline failed", ex);
		}
	}

	@ApiOperation(value = "Maps the specified pipeline to the specified project.", //
			response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}/" + CCDSConstants.PIPELINE_PATH
			+ "/{pipelineId}", method = RequestMethod.POST)
	public MLPResponse addPipelineToProject(@PathVariable("projectId") String projectId,
			@PathVariable("pipelineId") String pipelineId, HttpServletResponse response) {
		logger.debug("addPipelineToProject projectId {} pipelineId {}", projectId, pipelineId);
		if (!projectRepository.findById(projectId).isPresent()) {
			logger.warn("addPipelineToProject: failed on project ID {}", projectId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + projectId, null);
		}
		if (!pipelineRepository.findById(pipelineId).isPresent()) {
			logger.warn("addPipelineToProject: failed on group ID {}", pipelineId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + pipelineId, null);
		}
		projPlMapRepository.save(new MLPProjPipelineMap(projectId, pipelineId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Umaps the specified pipeline from the specified project.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.PROJECT_PATH + "/{projectId}/" + CCDSConstants.PIPELINE_PATH
			+ "/{pipelineId}", method = RequestMethod.DELETE)
	public MLPResponse dropPipelineFromProject(@PathVariable("projectId") String projectId,
			@PathVariable("pipelineId") String pipelineId, HttpServletResponse response) {
		logger.debug("dropPipelineFromProject projectId {} pipelineId {}", projectId, pipelineId);
		try {
			projPlMapRepository.deleteById(new MLPProjPipelineMap.ProjPlMapPK(projectId, pipelineId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("dropPipelineFromProject failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropPipelineFromProject failed", ex);
		}
	}

	@ApiOperation(value = "Maps the specified user to the specified project.", //
			response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.PROJECT_PATH
			+ "/{projectId}", method = RequestMethod.POST)
	public MLPResponse mapUserToProject(@PathVariable("userId") String userId,
			@PathVariable("projectId") String projectId, HttpServletResponse response) {
		logger.debug("mapUserToProject userId {} projectId {}", userId, projectId);
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("mapUserToProject: failed on userId {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		if (!projectRepository.findById(projectId).isPresent()) {
			logger.warn("mapUserToProject: failed on projectId {}", projectId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + projectId, null);
		}
		userProjMapRepository.save(new MLPUserProjMap(userId, projectId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Umaps the specified user from the specified project.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.PROJECT_PATH + "/{projectId}/"
			+ CCDSConstants.PIPELINE_PATH + "/{pipelineId}", method = RequestMethod.DELETE)
	public MLPResponse unmapUserFromProject(@PathVariable("userId") String userId,
			@PathVariable("projectId") String projectId, HttpServletResponse response) {
		logger.debug("unmapUserFromProject userId {} projectId {}", userId, projectId);
		try {
			userProjMapRepository.deleteById(new MLPUserProjMap.UserProjMapPK(userId, projectId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("unmapUserFromProject failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapUserFromProject failed", ex);
		}
	}

	@ApiOperation(value = "Maps the specified user to the specified notebook.", //
			response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.NOTEBOOK_PATH
			+ "/{notebookId}", method = RequestMethod.POST)
	public MLPResponse mapUserToNotebook(@PathVariable("userId") String userId,
			@PathVariable("notebookId") String notebookId, HttpServletResponse response) {
		logger.debug("mapUserToNotebook userId {} notebookId {}", userId, notebookId);
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("mapUserToNotebook: failed on userId {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		if (!notebookRepository.findById(notebookId).isPresent()) {
			logger.warn("mapUserToNotebook: failed on notebookId {}", notebookId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + notebookId, null);
		}
		userNbMapRepository.save(new MLPUserNotebookMap(userId, notebookId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Umaps the specified user from the specified notebook.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.NOTEBOOK_PATH
			+ "/{notebookId}", method = RequestMethod.DELETE)
	public MLPResponse unmapUserFromNotebook(@PathVariable("userId") String userId,
			@PathVariable("notebookId") String notebookId, HttpServletResponse response) {
		logger.debug("unmapUserFromNotebook userId {} projectId {}", userId, notebookId);
		try {
			userNbMapRepository.deleteById(new MLPUserNotebookMap.UserNbMapPK(userId, notebookId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("unmapUserFromNotebook failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapUserFromNotebook failed", ex);
		}
	}

	@ApiOperation(value = "Maps the specified user to the specified pipeline.", //
			response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.PIPELINE_PATH
			+ "/{pipelineId}", method = RequestMethod.POST)
	public MLPResponse mapUserToPipeline(@PathVariable("userId") String userId,
			@PathVariable("pipelineId") String pipelineId, HttpServletResponse response) {
		logger.debug("mapUserToPipeline userId {} pipelineId {}", userId, pipelineId);
		if (!userRepository.findById(userId).isPresent()) {
			logger.warn("mapUserToPipeline: failed on userId {}", userId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + userId, null);
		}
		if (!pipelineRepository.findById(pipelineId).isPresent()) {
			logger.warn("mapUserToPipeline: failed on pipelineId {}", pipelineId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + pipelineId, null);
		}
		userPlMapRepository.save(new MLPUserPipelineMap(userId, pipelineId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Umaps the specified user from the specified pipeline.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.USER_PATH + "/{userId}/" + CCDSConstants.PIPELINE_PATH
			+ "/{pipelineId}", method = RequestMethod.DELETE)
	public MLPResponse unmapUserFromPipeline(@PathVariable("userId") String userId,
			@PathVariable("pipelineId") String pipelineId, HttpServletResponse response) {
		logger.debug("unmapUserFromPipeline userId {} projectId {}", userId, pipelineId);
		try {
			userPlMapRepository.deleteById(new MLPUserPipelineMap.UserPlMapPK(userId, pipelineId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("unmapUserFromPipeline failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapUserFromPipeline failed", ex);
		}
	}

}
