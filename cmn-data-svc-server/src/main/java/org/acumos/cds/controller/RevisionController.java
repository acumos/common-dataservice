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

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolRevDocMap;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.DocumentRepository;
import org.acumos.cds.repository.RevisionDescriptionRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolRevDocMapRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.SuccessTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * A revision is a collection of artifacts. A revision cannot exist without a
 * solution, but an artifact can exist without a revision.
 */
@RestController
@RequestMapping(value = "/" + CCDSConstants.REVISION_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class RevisionController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private RevisionDescriptionRepository revisionDescRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private SolRevDocMapRepository solRevDocMapRepository;

	@ApiOperation(value = "Gets the artifacts for the revision.", response = MLPArtifact.class, responseContainer = "List")
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ARTIFACT_PATH, method = RequestMethod.GET)
	public Iterable<MLPArtifact> getRevisionArtifacts(@PathVariable("revisionId") String revisionId,
			HttpServletResponse response) {
		logger.debug("getSolRevArtifacts: revisionId {}", revisionId);
		return artifactRepository.findByRevision(revisionId);
	}

	@ApiOperation(value = "Adds an artifact to the revision.", response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ARTIFACT_PATH
			+ "/{artifactId}", method = RequestMethod.POST)
	public Object addRevisionArtifact(@PathVariable("revisionId") String revisionId,
			@PathVariable("artifactId") String artifactId, HttpServletResponse response) {
		logger.debug("addRevArtifact: revisionId {} artifactId {}", revisionId, artifactId);
		if (revisionRepository.findOne(revisionId) == null) {
			logger.warn("addRevArtifact failed on revID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		if (artifactRepository.findOne(artifactId) == null) {
			logger.warn("addRevArtifact failed on art ID {}", artifactId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + artifactId, null);
		}
		try {
			MLPSolRevArtMap map = new MLPSolRevArtMap(revisionId, artifactId);
			solRevArtMapRepository.save(map);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			logger.warn("addRevisionArtifact failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "addRevisionArtifact failed", ex);
		}
	}

	@ApiOperation(value = "Removes an artifact from the revision.", response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ARTIFACT_PATH
			+ "/{artifactId}", method = RequestMethod.DELETE)
	public Object dropRevisionArtifact(@PathVariable("revisionId") String revisionId,
			@PathVariable("artifactId") String artifactId, HttpServletResponse response) {
		logger.debug("dropRevArtifact: revisionId {} artifactId {}", revisionId, artifactId);
		try {
			solRevArtMapRepository.delete(new MLPSolRevArtMap.SolRevArtMapPK(revisionId, artifactId));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			logger.warn("dropRevisionArtifact failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropRevisionArtifact failed", ex);
		}
	}

	@ApiOperation(value = "Gets the revision description for the specified access type. Returns bad request if an ID is not found.", //
			response = MLPRevisionDescription.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.GET)
	public Object getRevisionDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, HttpServletResponse response) {
		logger.debug("getRevisionDescription: revisionId {} accessTypeCode {}", revisionId, accessTypeCode);
		MLPRevisionDescription da = revisionDescRepository
				.findOne(new MLPRevisionDescription.RevDescPK(revisionId, accessTypeCode));
		if (da == null) {
			logger.warn("getRevisionDescription failed on ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					NO_ENTRY_WITH_ID + revisionId + "/" + accessTypeCode, null);
		}
		return da;
	}

	@ApiOperation(value = "Creates a new description for the specified revision and access type. Returns bad request if an ID is not found.", //
			response = MLPRevisionDescription.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.POST)
	public Object createRevisionDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, @RequestBody MLPRevisionDescription description,
			HttpServletResponse response) {
		logger.debug("createRevisionDescription: revisionId {} accessTypeCode {}", revisionId, accessTypeCode);
		if (revisionRepository.findOne(revisionId) == null) {
			logger.warn("createRevisionDescription failed on ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + revisionId, null);
		}
		try {
			// Validate enum codes
			super.validateCode(description.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			// Use the validated values
			description.setRevisionId(revisionId);
			description.setAccessTypeCode(accessTypeCode);
			// Create a new row
			return revisionDescRepository.save(description);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createRevisionDescription failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createRevisionDescription failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing entity with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.PUT)
	public Object updateRevisionDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, @RequestBody MLPRevisionDescription description,
			HttpServletResponse response) {
		logger.debug("updateRevisionDescription: revisionId {} accessTypeCode {}", revisionId, accessTypeCode);
		// Get the existing one
		MLPRevisionDescription existing = revisionDescRepository
				.findOne(new MLPRevisionDescription.RevDescPK(revisionId, accessTypeCode));
		if (existing == null) {
			logger.warn("updateRevisionDescription failed on ID {}", revisionId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					NO_ENTRY_WITH_ID + revisionId + "/" + accessTypeCode, null);
		}
		try {
			super.validateCode(description.getAccessTypeCode(), CodeNameType.ACCESS_TYPE);
			// Use the validated values
			description.setRevisionId(revisionId);
			description.setAccessTypeCode(accessTypeCode);
			revisionDescRepository.save(description);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateRevisionDescription failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateRevisionDescription failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the entity with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DESCRIPTION_PATH, method = RequestMethod.DELETE)
	public Object deleteRevisionDescription(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, HttpServletResponse response) {
		logger.debug("deleteRevisionDescription: revisionId {} accessTypeCode {}", revisionId, accessTypeCode);
		try {
			revisionDescRepository.delete(new MLPRevisionDescription.RevDescPK(revisionId, accessTypeCode));
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteRevisionDescription failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteRevisionDescription failed", ex);
		}
	}

	@ApiOperation(value = "Gets the documents for the specified revision and access type.", //
			response = MLPDocument.class, responseContainer = "List")
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DOCUMENT_PATH, method = RequestMethod.GET)
	public Iterable<MLPDocument> getSolRevDocuments(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, HttpServletResponse response) {
		logger.debug("getSolRevDocuments: revisionId {} accessType {}", revisionId, accessTypeCode);
		return documentRepository.findByRevisionAccess(revisionId, accessTypeCode);
	}

	@ApiOperation(value = "Adds a user document to the specified revision and access type.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DOCUMENT_PATH + "/{documentId}", method = RequestMethod.POST)
	public SuccessTransport addRevisionDocument(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, @PathVariable("documentId") String documentId,
			HttpServletResponse response) {
		logger.debug("addRevisionDocument: revisionId {} accessType {} documentId {}", revisionId, accessTypeCode,
				documentId);
		MLPSolRevDocMap map = new MLPSolRevDocMap(revisionId, accessTypeCode, documentId);
		solRevDocMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	@ApiOperation(value = "Removes a user document from the specified revision and access type.", //
			response = SuccessTransport.class)
	@RequestMapping(value = "/{revisionId}/" + CCDSConstants.ACCESS_PATH + "/{accessTypeCode}/"
			+ CCDSConstants.DOCUMENT_PATH + "/{documentId}", method = RequestMethod.DELETE)
	public SuccessTransport dropRevisionDocument(@PathVariable("revisionId") String revisionId,
			@PathVariable("accessTypeCode") String accessTypeCode, @PathVariable("documentId") String documentId,
			HttpServletResponse response) {
		logger.debug("dropRevisionDocument: revisionId {} documentId {}", revisionId, documentId);
		solRevDocMapRepository.delete(new MLPSolRevDocMap.SolRevDocMapPK(revisionId, accessTypeCode, documentId));
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}
}