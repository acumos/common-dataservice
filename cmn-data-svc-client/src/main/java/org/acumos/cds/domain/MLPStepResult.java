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
import java.time.Instant;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for a step result
 */
@Entity
@Table(name = "C_STEP_RESULT")
public class MLPStepResult implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = -595148641870461125L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	// The "native" annotations work in Hibernate 5.3 with Mariadb 10.2.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Generated")
	private Long stepResultId;

	@Column(name = "TRACKING_ID", updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String trackingId;

	@Column(name = "STEP_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Step code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, example = "OB")
	private String stepCode;

	@Column(name = "SOLUTION_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "REVISION_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String revisionId;

	@Column(name = "ARTIFACT_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String artifactId;

	@Column(name = "USER_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Step name cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, example = "Step name")
	private String name;

	@Column(name = "STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Status code cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, example = "SU")
	private String statusCode;

	@Column(name = "RESULT", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(value = "Step result details")
	private String result;

	@Column(name = "START_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	@ApiModelProperty(required = true, value = "Start date", example = "2018-12-16T12:34:56.789Z")
	private Instant startDate;

	@Column(name = "END_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "End date", example = "2018-12-16T12:34:56.789Z")
	private Instant endDate;

	/**
	 * No-arg constructor
	 */
	public MLPStepResult() {
		// no-arg constructor
		startDate = Instant.now();
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits step result ID, which is
	 * generated on save.
	 * 
	 * @param stepTypeCode
	 *                         Step type code
	 * @param name
	 *                         Step name
	 * @param statusCode
	 *                         Step status code
	 * @param startDate
	 *                         Start date
	 */
	public MLPStepResult(String stepTypeCode, String name, String statusCode, Instant startDate) {
		if (stepTypeCode == null || name == null || statusCode == null || startDate == null)
			throw new IllegalArgumentException("Null not permitted");
		this.stepCode = stepTypeCode;
		this.name = name;
		this.statusCode = statusCode;
		this.startDate = startDate;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPStepResult(MLPStepResult that) {
		this.artifactId = that.artifactId;
		this.endDate = that.endDate;
		this.name = that.name;
		this.result = that.result;
		this.revisionId = that.revisionId;
		this.solutionId = that.solutionId;
		this.startDate = that.startDate;
		this.statusCode = that.statusCode;
		this.stepCode = that.stepCode;
		this.stepResultId = that.stepResultId;
		this.trackingId = that.trackingId;
		this.userId = that.userId;
	}

	public Long getStepResultId() {
		return stepResultId;
	}

	public void setStepResultId(Long stepResultId) {
		this.stepResultId = stepResultId;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPStepResult))
			return false;
		MLPStepResult thatObj = (MLPStepResult) that;
		return Objects.equals(stepResultId, thatObj.stepResultId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stepResultId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[stepResultId=" + stepResultId + ", trackingId=" + trackingId + ", "
				+ "stepCode=" + stepCode + "solutionId=" + solutionId + "artifactId=" + artifactId + ", userId="
				+ userId + "name=" + name + "statusCode=" + statusCode + "result=" + result + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}

}