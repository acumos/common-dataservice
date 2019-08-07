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
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.repository.LicenseTemplateRepository;
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
 * Answers REST requests to manage license templates.
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
@RequestMapping(value = "/" + CCDSConstants.LICENSE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class LicenseController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private LicenseTemplateRepository licenseRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets a page of license templates, optionally sorted on fields. Returns empty if none are found.", //
			response = MLPLicenseProfileTemplate.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.TEMPlATE_PATH, method = RequestMethod.GET)
	public Page<MLPLicenseProfileTemplate> getLicenseTemplates(Pageable pageable) {
		logger.debug("getLicenseTemplates query {}", pageable);
		return licenseRepository.findAll(pageable);
	}

	@ApiOperation(value = "Gets the license template for the specified ID. Returns null if not found.", response = MLPLicenseProfileTemplate.class)
	@RequestMapping(value = CCDSConstants.TEMPlATE_PATH + "/{licenseId}", method = RequestMethod.GET)
	public MLPLicenseProfileTemplate getLicenseTemplate(@PathVariable("licenseId") Long licenseId) {
		logger.debug("getLicenseTemplate key {}", licenseId);
		Optional<MLPLicenseProfileTemplate> da = licenseRepository.findById(licenseId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new license template object. Returns bad request on constraint violation etc.", response = MLPLicenseProfileTemplate.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.TEMPlATE_PATH, method = RequestMethod.POST)
	public Object createLicenseTemplate(@RequestBody MLPLicenseProfileTemplate licenseTemplate, HttpServletResponse response) {
		logger.debug("createLicenseTemplate: key {}", licenseTemplate.getTemplateId());
		if (licenseRepository.findById(licenseTemplate.getTemplateId()).isPresent()) {
			logger.warn("createLicenseTemplate failed on key {}", licenseTemplate.getTemplateId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					"Key exists: " + licenseTemplate.getTemplateId(), null);
		}
		if (licenseTemplate.getUserId() == null || !userRepository.findById(licenseTemplate.getUserId()).isPresent()) {
			logger.warn("createLicenseTemplate failed on user {}", licenseTemplate.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + licenseTemplate.getUserId(),
					null);
		}
		try {
			Object result = licenseRepository.save(licenseTemplate);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.LICENSE_PATH + "/" + CCDSConstants.TEMPlATE_PATH + "/"
					+ licenseTemplate.getTemplateId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createLicenseTemplate took exception {} on data {}", cve.toString(), licenseTemplate.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createLicenseTemplate failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing license template with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.TEMPlATE_PATH + "/{licenseId}", method = RequestMethod.PUT)
	public Object updateLicenseTemplate(@PathVariable("licenseId") Long licenseId,
			@RequestBody MLPLicenseProfileTemplate licenseTemplate, HttpServletResponse response) {
		logger.debug("updateLicenseTemplate key {}", licenseId);
		// Check for an existing one
		if (!licenseRepository.findById(licenseId).isPresent()) {
			logger.warn("updateLicenseTemplate failed on ID {}", licenseId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + licenseId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			licenseTemplate.setTemplateId(licenseId);
			licenseRepository.save(licenseTemplate);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateLicenseTemplate took exception {} on data {}", cve.toString(), licenseTemplate.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateLicenseTemplate failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the license template with the specified ID. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = CCDSConstants.TEMPlATE_PATH + "/{licenseId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteLicenseTemplate(@PathVariable("licenseId") Long licenseId,
			HttpServletResponse response) {
		logger.debug("deleteLicenseTemplate key {}", licenseId);
		try {
			licenseRepository.deleteById(licenseId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteLicenseTemplate failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteLicenseTemplate failed", ex);
		}
	}

}
