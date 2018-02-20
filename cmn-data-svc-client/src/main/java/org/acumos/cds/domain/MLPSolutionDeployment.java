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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

/**
 * Model for solution deployment.
 */
@Entity
@Table(name = "C_SOLUTION_DEPLOYMENT")
public class MLPSolutionDeployment extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -399547002926585992L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "DEPLOYMENT_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Generated by DB; NotNull annotation not needed
	private String deploymentId;

	@Column(name = "SOLUTION_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "SolutionID cannot be null")
	@Size(max = 36)
	private String solutionId;

	@Column(name = "REVISION_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "RevisionID cannot be null")
	@Size(max = 36)
	private String revisionId;

	@Column(name = "USER_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "UserID cannot be null")
	@Size(max = 36)
	private String userId;

	/**
	 * This code is defined by {@link org.acumos.cds.DeploymentStatusCode}
	 */
	@Column(name = "DEP_STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String deploymentStatusCode;

	@Column(name = "TARGET", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	private String target;

	/** JSON */
	@Column(name = "DETAIL", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	private String detail;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionDeployment() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param userId
	 *            User ID
	 * @param deploymentStatusCode
	 *            Value from the table
	 */
	public MLPSolutionDeployment(String solutionId, String revisionId, String userId, String deploymentStatusCode) {
		if (solutionId == null || revisionId == null || userId == null || deploymentStatusCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.userId = userId;
		this.deploymentStatusCode = deploymentStatusCode;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeploymentStatusCode() {
		return deploymentStatusCode;
	}

	public void setDeploymentStatusCode(String deploymentStatusCode) {
		this.deploymentStatusCode = deploymentStatusCode;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPSolutionDeployment))
			return false;
		MLPSolutionDeployment thatObj = (MLPSolutionDeployment) that;
		return Objects.equals(deploymentId, thatObj.deploymentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(deploymentId, solutionId, revisionId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[deploymentId=" + deploymentId + ", solutionId=" + solutionId
				+ ", revisionId=" + revisionId + ", status=" + deploymentStatusCode + "]";
	}

}
