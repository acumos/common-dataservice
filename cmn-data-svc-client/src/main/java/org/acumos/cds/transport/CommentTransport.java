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

package org.acumos.cds.transport;

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPComment;

/**
 * Model for threaded comments. Instead of a parentId field, a reply is placed
 * within a list in its parent comment.
 */
public class CommentTransport implements MLPTransportModel {

	private String commentId;
	private String threadId;
	private String userId;
	private String text;
	private String url;
	private List<CommentTransport> replies;

	/**
	 * Builds an empty object.
	 */
	public CommentTransport() {
		replies = new ArrayList<>();
	}

	/**
	 * Builds a transport object by copying all fields except parentId.
	 * 
	 * @param comment
	 *            Comment to be copied
	 */
	public CommentTransport(MLPComment comment) {
		this.commentId = comment.getCommentId();
		this.threadId = comment.getThreadId();
		this.userId = comment.getUserId();
		this.text = comment.getText();
		this.url = comment.getUrl();
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<CommentTransport> getReplies() {
		return replies;
	}

	public void setReplies(List<CommentTransport> replies) {
		this.replies = replies;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[commentId=" + commentId + ", threadId=" + threadId + ", userId=" + userId
				+ ", text=" + text + ", url=" + url + "]";
	}
}
