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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.repository.CommentRepository;
import org.acumos.cds.repository.ThreadRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.transport.CommentTransport;
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

/**
 * Provides methods to create and delete threads.
 */
@Controller
@RequestMapping("/" + CCDSConstants.THREAD_PATH)
public class ThreadController extends AbstractController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ThreadController.class);

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private UserRepository userRepository;

	/**
	 * @return Model that maps String to Object, for serialization as JSON
	 */
	@ApiOperation(value = "Gets the count of threads.", response = CountTransport.class)
	@RequestMapping(value = CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public CountTransport getThreadCount() {
		Long count = threadRepository.count();
		return new CountTransport(count);
	}

	/**
	 * @param pageable
	 *            Sort and page criteria
	 * @param response
	 *            HttpServletResponse
	 * @return Page of threads
	 */
	@ApiOperation(value = "Gets a page of threads, optionally sorted.", response = MLPThread.class, responseContainer = "Page")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPThread> getPageOfThreads(Pageable pageable) {
		return threadRepository.findAll(pageable);
	}

	/**
	 * @param threadId
	 *            Path parameter with row ID
	 * @param response
	 *            HttpServletResponse
	 * @return A user if found, an error otherwise.
	 */
	@ApiOperation(value = "Gets the thread for the specified ID.", response = MLPThread.class)
	@RequestMapping(value = "{threadId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getThread(@PathVariable("threadId") String threadId, HttpServletResponse response) {
		MLPThread thread = threadRepository.findOne(threadId);
		if (thread == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No entry for row ID " + threadId, null);
		}
		return thread;
	}

	/**
	 * @param thread
	 *            Thread details
	 * @param response
	 *            HttpServletResponse
	 * @return MLPThread
	 */
	@ApiOperation(value = "Creates a thread.", response = MLPThread.class)
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createThread(@RequestBody MLPThread thread, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "createThread: thread {}", thread);
		Object result;
		try {
			String id = thread.getThreadId();
			if (id != null) {
				UUID.fromString(id);
				if (threadRepository.findOne(id) != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			// Create a new row
			MLPThread newThread = threadRepository.save(thread);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.THREAD_PATH + "/" + newThread.getThreadId());
			result = newThread;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createThread", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createThread failed", cve);
		}
		return result;
	}

	/**
	 * @param threadId
	 *            Path parameter with the row ID
	 * @param thread
	 *            data to be updated
	 * @param response
	 *            HttpServletResponse
	 * @return Status message
	 */
	@ApiOperation(value = "Updates a thread.", response = SuccessTransport.class)
	@RequestMapping(value = "{threadId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateThread(@PathVariable("threadId") String threadId, @RequestBody MLPThread thread,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "updateThread: received {} ", thread);
		// Get the existing one
		MLPThread existing = threadRepository.findOne(threadId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Failed to find object with id " + threadId,
					null);
		}
		MLPTransportModel result = null;
		try {
			// Use the path-parameter id; don't trust the one in the object
			thread.setThreadId(threadId);
			// Update the existing row
			threadRepository.save(thread);
			// Answer "OK"
			result = new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updateThread", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateThread failed", cve);
		}
		return result;
	}

	/**
	 * @param threadId
	 *            Path parameter that identifies the instance
	 * @param response
	 *            HttpServletResponse
	 * @return Solution that maps String to Object, for serialization as JSON
	 */
	@ApiOperation(value = "Deletes a thread.", response = SuccessTransport.class)
	@RequestMapping(value = "{threadId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteThread(@PathVariable("threadId") String threadId, HttpServletResponse response) {
		try {
			// cascade the delete
			commentRepository.deleteThreadComments(threadId);
			threadRepository.delete(threadId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "deleteThread failed", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteThread failed", ex);
		}
	}

	/**
	 * @param threadId
	 *            Path parameter that identifies the instance
	 * @return Model that maps String to Object, for serialization as JSON
	 */
	@ApiOperation(value = "Gets the number of comments in the thread.", response = CountTransport.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/"
			+ CCDSConstants.COUNT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public CountTransport getCommentCount(@PathVariable("threadId") String threadId) {
		Long count = commentRepository.countThreadComments(threadId);
		return new CountTransport(count);
	}

	/**
	 * Returns a list of threaded comments; i.e., a reply to a comment is contained
	 * within the parent.
	 * 
	 * TODO: what about sorting?
	 * 
	 * @param threadId
	 *            Thread ID
	 * @param response
	 *            HttpServletResponse
	 * @return List of comments
	 */
	@ApiOperation(value = "Gets all comments in the thread.", response = CommentTransport.class, responseContainer = "List")
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH, method = RequestMethod.GET)
	@ResponseBody
	public List<CommentTransport> getComments(@PathVariable("threadId") String threadId) {
		List<MLPComment> comments = commentRepository.findByThreadId(threadId);
		// Build a map for easy lookup
		Map<String, CommentTransport> map = new HashMap<>();
		for (MLPComment c : comments)
			map.put(c.getCommentId(), new CommentTransport(c));
		// Build response by organizing replies
		List<CommentTransport> result = new ArrayList<>();
		Iterator<MLPComment> commentIter = comments.iterator();
		while (commentIter.hasNext()) {
			MLPComment c = commentIter.next();
			CommentTransport ct = map.get(c.getCommentId());
			if (c.getParentId() == null)
				// Top-level comment in thread
				result.add(ct);
			else {
				// A reply to another comment
				map.get(c.getParentId()).getReplies().add(ct);
			}
		}
		return result;
	}

	/**
	 * @param threadId
	 *            Thread ID
	 * @param commentId
	 *            Path parameter with row ID
	 * @param response
	 *            HttpServletResponse
	 * @return A user if found, an error otherwise.
	 */
	@ApiOperation(value = "Gets the comment for the specified ID.", response = MLPComment.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/{commentId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getComment(@PathVariable("threadId") String threadId, @PathVariable("commentId") String commentId,
			HttpServletResponse response) {
		MLPComment comment = commentRepository.findOne(commentId);
		if (comment == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No entry for row ID " + commentId, null);
		}
		return comment;
	}

	/**
	 * @param threadId
	 *            Thread ID
	 * @param comment
	 *            Comment details
	 * @param response
	 *            HttpServletResponse
	 * @return MLPComment
	 */
	@ApiOperation(value = "Creates a comment.", response = MLPComment.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object createComment(@PathVariable("threadId") String threadId, @RequestBody MLPComment comment,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "createComment: comment {}", comment);
		Object result;
		try {
			String id = comment.getCommentId();
			if (id != null) {
				UUID.fromString(id);
				if (commentRepository.findOne(id) != null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "ID exists: " + id);
				}
			}
			if (comment.getParentId() != null && commentRepository.findOne(comment.getParentId()) == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No comment: " + comment.getParentId());
			}
			if (threadRepository.findOne(comment.getThreadId()) == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No thread: " + comment.getThreadId());
			}
			if (userRepository.findOne(comment.getUserId()) == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No user: " + comment.getUserId());
			}
			// Create a new row
			MLPComment newComment = commentRepository.save(comment);
			response.setStatus(HttpServletResponse.SC_CREATED);
			// This is a hack to create the location path.
			response.setHeader(HttpHeaders.LOCATION, CCDSConstants.COMMENT_PATH + "/" + newComment.getCommentId());
			result = newComment;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createComment", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createComment failed", cve);
		}
		return result;
	}

	/**
	 * @param threadId
	 *            Thread ID
	 * @param commentId
	 *            Path parameter with the row ID
	 * @param comment
	 *            data to be updated
	 * @param response
	 *            HttpServletResponse
	 * @return Status message
	 */
	@ApiOperation(value = "Updates a comment.", response = SuccessTransport.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/{commentId}", method = RequestMethod.PUT)
	@ResponseBody
	public Object updateComment(@PathVariable("threadId") String threadId, @PathVariable("commentId") String commentId,
			@RequestBody MLPComment comment, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "updateComment: received {} ", comment);
		// Get the existing one
		MLPComment existing = commentRepository.findOne(commentId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Failed to find object with id " + commentId,
					null);
		}
		if (comment.getParentId() != null && commentRepository.findOne(comment.getParentId()) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No comment: " + comment.getParentId());
		}
		if (threadRepository.findOne(comment.getThreadId()) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No thread: " + comment.getThreadId());
		}
		if (userRepository.findOne(comment.getUserId()) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "No user: " + comment.getUserId());
		}
		MLPTransportModel result = null;
		try {
			// Use the path-parameter id; don't trust the one in the object
			comment.setCommentId(commentId);
			// Update the existing row
			commentRepository.save(comment);
			// Answer "OK"
			result = new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updateComment", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateComment failed", cve);
		}
		return result;
	}

	/**
	 * @param threadId
	 *            Thread ID
	 * @param commentId
	 *            Path parameter that identifies the instance
	 * @param response
	 *            HttpServletResponse
	 * @return Solution that maps String to Object, for serialization as JSON
	 */
	@ApiOperation(value = "Deletes a comment.", response = SuccessTransport.class)
	@RequestMapping(value = "{threadId}/" + CCDSConstants.COMMENT_PATH + "/{commentId}", method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteComment(@PathVariable("threadId") String threadId,
			@PathVariable("commentId") String commentId, HttpServletResponse response) {
		try {
			commentRepository.delete(commentId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "deleteComment failed", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteComment failed", ex);
		}
	}
}
