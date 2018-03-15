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

package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a notification, a message to users about a system activity. Valid
 * (active) in the date range start..end.
 * 
 * Participates in many-many relationship with users via a map table.
 */
@Entity
@Table(name = "C_NOTIFICATION")
public class MLPNotification extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -1766006565799281595L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "NOTIFICATION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String notificationId;

	@Column(name = "TITLE", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Title cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, example = "Notification subject")
	private String title;

	@Column(name = "MESSAGE", columnDefinition = "VARCHAR(2048)")
	@Size(max = 2048)
	private String message;

	@Column(name = "MSG_SEVERITY_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Message Severity Code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, example = "LO")
	private String msgSeverityCode;

	@Column(name = "URL", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	private String url;

	// No auto-update behaviors here
	// TODO: Column definition is not provided here because in Derby it's
	// created as TIMESTAMP but for Mariadb the DDL uses Datetime.
	@Column(name = "START_DATE", nullable = false)
	@ApiModelProperty(required = true)
	private Date start;

	// No auto-update behaviors here
	// TODO: Column definition is not provided here because in Derby it's
	// created as TIMESTAMP but for Mariadb the DDL uses Datetime.
	@Column(name = "END_DATE", nullable = false)
	@ApiModelProperty(required = true)
	private Date end;

	/**
	 * No-arg constructor.
	 */
	public MLPNotification() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param title
	 *            Like the subject of an email
	 * @param msgSeverityCode
	 *            severity of the notification like high, medium or low
	 * @param startDate
	 *            Earliest date of the active period
	 * @param endDate
	 *            Latest date of the active period
	 */
	public MLPNotification(String title, String msgSeverityCode, Date startDate, Date endDate) {
		if (title == null || msgSeverityCode == null || startDate == null || endDate == null)
			throw new IllegalArgumentException("Null not permitted");
		this.title = title;
		this.msgSeverityCode = msgSeverityCode;
		this.start = startDate;
		this.end = endDate;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPNotification(MLPNotification that) {
		super(that);
		this.end = that.end;
		this.message = that.message;
		this.msgSeverityCode = that.msgSeverityCode;
		this.notificationId = that.notificationId;
		this.start = that.start;
		this.title = that.title;
		this.url = that.url;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMsgSeverityCode() {
		return msgSeverityCode;
	}

	public void setMsgSeverityCode(String msgSeverityCode) {
		this.msgSeverityCode = msgSeverityCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPNotification))
			return false;
		MLPNotification thatObj = (MLPNotification) that;
		return Objects.equals(notificationId, thatObj.notificationId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(notificationId, title, url, start, end);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[title=" + title + ", URL=" + url + ", start=" + start + ", end=" + end
				+ "]";
	}

}
