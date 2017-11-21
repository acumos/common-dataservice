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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPSolutionValidation.SolutionValidationPK;

/**
 * Model for a solution validation detail.
 */
@Entity
@IdClass(SolutionValidationPK.class)
@Table(name = "C_SOLUTION_VALIDATION")
public class MLPSolutionValidation extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -5211530596207550260L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class SolutionValidationPK implements Serializable {

		private static final long serialVersionUID = -7312821900754322126L;
		private String solutionId;
		private String revisionId;
		private String taskId;

		public SolutionValidationPK() {
			// no-arg constructor
		}

		public SolutionValidationPK(String solutionId, String revisionId, String taskId) {
			this.solutionId = solutionId;
			this.revisionId = revisionId;
			this.taskId = taskId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof SolutionValidationPK))
				return false;
			SolutionValidationPK thatPK = (SolutionValidationPK) that;
			return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(revisionId, thatPK.revisionId)
					&& Objects.equals(taskId, thatPK.taskId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(solutionId, revisionId, taskId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[solutionId=" + solutionId + ", revisionId=" + revisionId + ", taskId="
					+ taskId + "]";
		}
	}

	@Id
	@Column(name = "SOLUTION_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "SolutionID cannot be null")
	@Size(max = 36)
	private String solutionId;

	@Id
	@Column(name = "REVISION_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "RevisionID cannot be null")
	@Size(max = 36)
	private String revisionId;

	@Id
	@Column(name = "TASK_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "TaskID cannot be null")
	@Size(max = 36)
	private String taskId;

	/**
	 * Validation type; e.g., security scan
	 */
	@Column(name = "VAL_TYPE_CD", nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String validationTypeCode;

	/**
	 * Validation status; e.g., pass. Can be null.
	 * 
	 * This exposes the database code for simplicity. Alternately this column could
	 * be mapped using @ManyToOne and @JoinColumn as an MLPValidationStatus object.
	 */
	@Column(name = "VAL_STATUS_CD", columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String validationStatusCode;

	/**
	 * JSON expected
	 */
	@Column(name = "DETAIL", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	private String detail;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionValidation() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            RevisionID
	 * @param taskId
	 *            Task ID
	 * @param validationTypeCode
	 *            Validation type code
	 */
	public MLPSolutionValidation(String solutionId, String revisionId, String taskId, String validationTypeCode) {
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.taskId = taskId;
		this.validationTypeCode = validationTypeCode;
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

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getValidationTypeCode() {
		return validationTypeCode;
	}

	public void setValidationTypeCode(String typeCode) {
		this.validationTypeCode = typeCode;
	}

	public String getValidationStatusCode() {
		return validationStatusCode;
	}

	public void setValidationStatusCode(String statusCode) {
		this.validationStatusCode = statusCode;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String details) {
		this.detail = details;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolutionValidation))
			return false;
		MLPSolutionValidation thatPK = (MLPSolutionValidation) that;
		return Objects.equals(solutionId, thatPK.solutionId) && Objects.equals(revisionId, thatPK.revisionId)
				&& Objects.equals(taskId, thatPK.taskId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(solutionId, revisionId, taskId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + solutionId + ", revisionId=" + revisionId + ", taskId="
				+ taskId + ", typeCode=" + validationTypeCode + ", statusCode =" + validationStatusCode + ", created="
				+ getCreated() + "]";
	}

}
