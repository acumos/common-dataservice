package org.acumos.cds.controller;


import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.StepResultRepository;
import org.acumos.cds.transport.CountTransport;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/" + CCDSConstants.STEP_RESULT_PATH, produces = CCDSConstants.APPLICATION_JSON)
public class StepResultController extends AbstractController {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(StepResultController.class);

	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolutionRevisionRepository solutionRevisionRepository;
	@Autowired
	private StepResultRepository stepResultRepository;

	/**
	 * @return SuccessTransport object
	 */
	@ApiOperation(value = "Gets the count of step results.", response = CountTransport.class)
	@RequestMapping(value = "/" + CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public CountTransport getCount() {
		Long count = stepResultRepository.count();
		return new CountTransport(count);
	}

	/**
	 * 
	 * @param pageRequest
	 *            Page and sort criteria
	 * @return List of step results, for serialization as JSON
	 */
	@ApiOperation(value = "Gets a page of step results, optionally sorted on fields.", response = MLPStepResult.class, responseContainer = "Page")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPStepResult> getPage(Pageable pageRequest) {
		return stepResultRepository.findAll(pageRequest);
	}

	/**
	 * @param artifactId
	 *            Path parameter with row ID
	 * @param response
	 *            HttpServletResponse
	 * @return An artifact if found, an error otherwise.
	 */
	@ApiOperation(value = "Gets the step results that use the specified solution ID and revision ID", response = MLPStepResult.class, responseContainer = "List")
	@RequestMapping(value = "/{solutionId}" + CCDSConstants.REVISION_PATH + "/{revisionId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionRevisionForStep(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, HttpServletResponse response, Pageable pageRequest) {
		MLPSolution sol = solutionRepository.findOne(solutionId);
		MLPSolutionRevision solrev = solutionRevisionRepository.findOne(revisionId);
		
		if (sol == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No entry for ID " + solutionId, null);
		}
		
		if (solrev == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No entry for ID " + revisionId, null);
		}
		
		return stepResultRepository.findBySolutionRevision(solutionId, revisionId, pageRequest);
	}

	/**
	 * @param stepResult
	 *            stepResult to save. A new one will be generated; 
	 * @param response
	 *            HttpServletResponse
	 * @return Entity on success; error on failure.
	 */
	@ApiOperation(value = "Creates a new step result.", response = MLPStepResult.class)
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createStepResult(@RequestBody MLPStepResult stepResult, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "createStepResult: received {} ", stepResult);
		Object result;
		try {
			// Create a new row
			result = stepResultRepository.save(stepResult);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.STEP_RESULT_PATH + "/" + stepResult.getStepResultId());
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createStepResult", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createStepResult failed", cve);
		}
		return result;
	}

	/**
	 * @param stepResultId
	 *            Path parameter with the row ID
	 * @param stepResult
	 *            stepResult data to be updated
	 * @param response
	 *            HttpServletResponse
	 * @return StepResult that maps String to Object, for serialization as JSON
	 */
	@ApiOperation(value = "Updates a step result", response = SuccessTransport.class)
	@RequestMapping(value = "/{stepResultId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateStepResult(@PathVariable("stepResultId") Long stepResultId, @RequestBody MLPStepResult stepResult,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "update: received {} ", stepResult);
		// Get the existing one
		MLPStepResult existing = stepResultRepository.findOne(stepResultId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Failed to find object with id " + stepResultId,
					null);
		}
		MLPTransportModel result = null;
		try {
			// Use the path-parameter id; don't trust the one in the object
			stepResult.setStepResultId(stepResultId);
			// Update the existing row
			stepResultRepository.save(stepResult);
			result = new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updateStepResult", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateStepResult failed", cve);
		}
		return result;
	}

	/**
	 * 
	 * @param stepResultId
	 *            Path parameter that identifies the instance
	 * @param response
	 *            HttpServletResponse
	 * @return stepResult that maps String to Object, for serialization as JSON
	 */
	@ApiOperation(value = "Deletes the stepResult with the specified ID.", response = SuccessTransport.class)
	@RequestMapping(value = "/{stepResultId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteStepResult(@PathVariable("stepResultId") Long stepResultId,
			HttpServletResponse response) {
		try {
			stepResultRepository.delete(stepResultId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "deleteStepResult", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteStepResult failed", ex);
		}
	}
	
}
