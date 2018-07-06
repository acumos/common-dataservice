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

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.repository.TagRepository;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

/**
 * Provides methods to create and delete tags for solutions.
 */
@Controller
@RequestMapping("/" + CCDSConstants.TAG_PATH)
public class TagController extends AbstractController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private TagRepository tagRepository;

	/**
	 * @param pageable
	 *            Sort and page criteria
	 * @return Page of tags
	 */
	@ApiOperation(value = "Gets a page of tags, optionally sorted.", response = MLPTag.class, responseContainer = "Page")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPTag> getTags(Pageable pageable) {
		Date beginDate = new Date();
		Page<MLPTag> result = tagRepository.findAll(pageable);
		logger.audit(beginDate, "getTags: {}", pageable);
		return result;
	}

	/**
	 * @param tag
	 *            Tag string
	 * @param response
	 *            HttpServletResponse
	 * @return Tag
	 */
	@ApiOperation(value = "Creates a new tag.", response = MLPTag.class)
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createTag(@RequestBody MLPTag tag, HttpServletResponse response) {
		Date beginDate = new Date();
		if (tagRepository.findOne(tag.getTag()) != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Tag exists: " + tag, null);
		}
		try {
			Object result = tagRepository.save(tag);
			logger.audit(beginDate, "createTag: tag {}", tag);
			return result;
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createTag failed: {}", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createTag failed", cve);
		}
	}

	/**
	 * @param tag
	 *            tag to delete
	 * @param response
	 *            HttpServletResponse
	 * @return Status message
	 */
	@ApiOperation(value = "Deletes a tag.", response = SuccessTransport.class)
	@RequestMapping(value = "/{tag}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteTag(@PathVariable("tag") String tag, HttpServletResponse response) {
		Date beginDate = new Date();
		try {
			tagRepository.delete(tag);
			logger.audit(beginDate, "deleteTag: tag {}", tag);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "deleteTag failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteTag failed", ex);
		}
	}

}
