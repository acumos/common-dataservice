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
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.MLPResponse;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.repository.CommentRepository;
import org.acumos.cds.repository.ThreadRepository;
import org.acumos.cds.repository.UserRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Provides endpoints to manage threads of comments.
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
@RequestMapping(value = "/" + CCDSConstants.THREAD_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class ThreadController extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private UserRepository userRepository;

	@ApiOperation(value = "Gets the count of threads.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getThreadCount() {
		logger.debug("getThreadCount");
		long count = threadRepository.count();
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of threads, optionally sorted. Answers empty if none are found.", //
			response = MLPThread.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(method = RequestMethod.GET)
	public Page<MLPThread> getThreads(Pageable pageable) {
		logger.debug("getThreads {}", pageable);
		return threadRepository.findAll(pageable);
	}

	@ApiOperation(value = "Gets the count of threads for the solution and revision IDs.", //
			response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}/" + CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getSolutionRevisionThreadCount(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId) {
		logger.debug("getSolutionRevisionThreadCount: solutionId {} revisionId {}", solutionId, revisionId);
		long count = threadRepository.countBySolutionIdAndRevisionId(solutionId, revisionId);
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of threads for the solution and revision IDs, optionally sorted. Answers empty if none are found.", //
			response = MLPThread.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}", method = RequestMethod.GET)
	public Page<MLPThread> getSolutionRevisionThreads(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, Pageable pageable) {
		logger.debug("getSolutionRevisionThreads: solutionId {} revisionId {}", solutionId, revisionId);
		return threadRepository.findBySolutionIdAndRevisionId(solutionId, revisionId, pageable);
	}

	@ApiOperation(value = "Gets the thread for the specified ID. Returns null if an ID is not found.", //
			response = MLPThread.class)
	@RequestMapping(value = "{threadId}", method = RequestMethod.GET)
	public MLPResponse getThread(@PathVariable("threadId") String threadId) {
		logger.debug("getThread: threadId {}", threadId);
		Optional<MLPThread> da = threadRepository.findById(threadId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new thread and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPThread.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(method = RequestMethod.POST)
	public MLPResponse createThread(@RequestBody MLPThread thread, HttpServletResponse response) {
		logger.debug("createThread: thread {}", thread);
		try {
			String id = thread.getThreadId();
			if (id != null) {
				UUID.fromString(id);
				if (threadRepository.findById(id).isPresent()) {
					logger.warn("createThread failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, ENTRY_EXISTS_WITH_ID + id);
				}
			}
			// Create a new row
			MLPThread newThread = threadRepository.save(thread);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.THREAD_PATH + "/" + newThread.getThreadId());
			return newThread;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createThread took exception {} on data {}", cve.toString(), thread.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createThread failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing thread with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "{threadId}", method = RequestMethod.PUT)
	public MLPTransportModel updateThread(@PathVariable("threadId") String threadId, @RequestBody MLPThread thread,
			HttpServletResponse response) {
		logger.debug("updateThread: threadId {}", threadId);
		// Check the existing one
		if (!threadRepository.findById(threadId).isPresent()) {
			logger.warn("updateThread failed on ID {}", threadId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + threadId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			thread.setThreadId(threadId);
			threadRepository.save(thread);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateThread took exception {} on data {}", cve.toString(), thread.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateThread failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the thread with the specified ID. Cascades to comments in the thread. Returns bad request if the ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "{threadId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteThread(@PathVariable("threadId") String threadId, HttpServletResponse response) {
		logger.debug("deleteThread: threadId {}", threadId);
		try {
			// cascade the delete
			commentRepository.deleteByThreadId(threadId);
			threadRepository.deleteById(threadId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteThread failed", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteThread failed", ex);
		}
	}

	@ApiOperation(value = "Gets the count of comments in the specified thread.", response = CountTransport.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/"
			+ CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getCommentCount(@PathVariable("threadId") String threadId) {
		logger.debug("getCommentCount: threadId {}", threadId);
		long count = commentRepository.countThreadComments(threadId);
		return new CountTransport(count);
	}

	@ApiOperation(value = "Gets a page of comments in the thread. Answers empty if none are found.", response = MLPComment.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH, method = RequestMethod.GET)
	public Page<MLPComment> getThreadComments(@PathVariable("threadId") String threadId, Pageable pageable) {
		logger.debug("getThreadComments: threadId {}", threadId);
		return commentRepository.findByThreadId(threadId, pageable);
	}

	@ApiOperation(value = "Gets the count of comments in all threads for the specified solution and revision IDs.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}/" + CCDSConstants.COMMENT_PATH + "/"
			+ CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	public CountTransport getSolutionRevisionCommentCount(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId) {
		logger.debug("getSolutionRevisionCommentCount: solutionId {} revisionId {}", solutionId, revisionId);
		long result = commentRepository.countSolutionRevisionComments(solutionId, revisionId);
		return new CountTransport(result);
	}

	@ApiOperation(value = "Gets a page of comments for the specified solution and revision IDs, optionally sorted. Answers empty if none are found.", //
			response = MLPThread.class, responseContainer = "Page")
	@ApiPageable
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH + "/{solutionId}/" + CCDSConstants.REVISION_PATH
			+ "/{revisionId}/" + CCDSConstants.COMMENT_PATH, method = RequestMethod.GET)
	public Page<MLPComment> getSolutionRevisionComments(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, Pageable pageable) {
		logger.debug("getSolutionRevisionComments: solutionId {} revisionId {}", solutionId, revisionId);
		return commentRepository.findBySolutionIdAndRevisionId(solutionId, revisionId, pageable);
	}

	@ApiOperation(value = "Gets the comment for the specified ID. Returns null if the ID is not found.", //
			response = MLPComment.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/{commentId}", method = RequestMethod.GET)
	public MLPComment getComment(@PathVariable("threadId") String threadId,
			@PathVariable("commentId") String commentId) {
		logger.debug("getComment: threadId {} commentId {}", threadId, commentId);
		Optional<MLPComment> da = commentRepository.findById(commentId);
		return da.isPresent() ? da.get() : null;
	}

	@ApiOperation(value = "Creates a new comment and generates an ID if needed. Returns bad request on constraint violation etc.", //
			response = MLPComment.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH, method = RequestMethod.POST)
	public MLPResponse createComment(@PathVariable("threadId") String threadId, @RequestBody MLPComment comment,
			HttpServletResponse response) {
		logger.debug("createComment: threadId {}", threadId);
		try {
			String id = comment.getCommentId();
			if (id != null) {
				UUID.fromString(id);
				if (commentRepository.findById(id).isPresent()) {
					logger.warn("createComment failed on ID {}", id);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, ENTRY_EXISTS_WITH_ID + id);
				}
			}
			if (comment.getParentId() != null && !commentRepository.findById(comment.getParentId()).isPresent()) {
				logger.warn("createComment failed on parent ID {}", comment.getParentId());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + comment.getParentId());
			}
			if (!threadRepository.findById(comment.getThreadId()).isPresent()) {
				logger.warn("createComment failed on thread ID {}", comment.getThreadId());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + comment.getThreadId());
			}
			if (!userRepository.findById(comment.getUserId()).isPresent()) {
				logger.warn("createComment failed on user ID {}", comment.getUserId());
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + comment.getUserId());
			}
			// Create a new row
			MLPComment newComment = commentRepository.save(comment);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.COMMENT_PATH + "/" + newComment.getCommentId());
			return newComment;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn("createComment took exception {} on data {}", cve.toString(), comment.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createComment failed", cve);
		}
	}

	@ApiOperation(value = "Updates an existing comment with the supplied data. Returns bad request on constraint violation etc.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/{commentId}", method = RequestMethod.PUT)
	public MLPTransportModel updateComment(@PathVariable("threadId") String threadId,
			@PathVariable("commentId") String commentId, @RequestBody MLPComment comment,
			HttpServletResponse response) {
		logger.debug("updateComment: threadId {} commentId {}", threadId, commentId);
		// Get the existing one
		if (!commentRepository.findById(commentId).isPresent()) {
			logger.warn("updateComment failed on comment ID {}", commentId);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + commentId, null);
		}
		if (comment.getParentId() != null && !commentRepository.findById(comment.getParentId()).isPresent()) {
			logger.warn("updateComment failed on parent ID {}", comment.getParentId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + comment.getParentId());
		}
		if (!threadRepository.findById(comment.getThreadId()).isPresent()) {
			logger.warn("updateComment failed on thread ID {}", comment.getThreadId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + comment.getThreadId());
		}
		if (!userRepository.findById(comment.getUserId()).isPresent()) {
			logger.warn("updateComment failed on user ID {}", comment.getUserId());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + comment.getUserId());
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			comment.setCommentId(commentId);
			commentRepository.save(comment);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			Exception cve = findConstraintViolationException(ex);
			logger.warn("updateComment took exception {} on data {}", cve.toString(), comment.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateComment failed", cve);
		}
	}

	@ApiOperation(value = "Deletes the comment with the specified ID. Returns bad request if an ID is not found.", //
			response = SuccessTransport.class)
	@ApiResponses({ @ApiResponse(code = 400, message = "Bad request", response = ErrorTransport.class) })
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/{commentId}", method = RequestMethod.DELETE)
	public MLPTransportModel deleteComment(@PathVariable("threadId") String threadId,
			@PathVariable("commentId") String commentId, HttpServletResponse response) {
		logger.debug("deleteComment: threadId {} commentId {}", threadId, commentId);
		try {
			commentRepository.deleteById(commentId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn("deleteComment failed: {}", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteComment failed", ex);
		}
	}
}
