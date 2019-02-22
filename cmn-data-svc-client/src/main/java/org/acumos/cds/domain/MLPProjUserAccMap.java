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

package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPProjUserAccMap.ProjUserAccMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the user-project mapping table.
 */
@Entity
@IdClass(ProjUserAccMapPK.class)
@Table(name = "C_PROJ_USER_ACC_MAP")
public class MLPProjUserAccMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 5273586022262263377L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class ProjUserAccMapPK implements Serializable {

		private static final long serialVersionUID = -198627735707587054L;
		private String userId;
		private String projectId;

		public ProjUserAccMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param userId
		 *                   user ID
		 * @param projId
		 *                   project ID
		 */
		public ProjUserAccMapPK(String userId, String projId) {
			this.userId = userId;
			this.projectId = projId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof ProjUserAccMapPK))
				return false;
			ProjUserAccMapPK thatPK = (ProjUserAccMapPK) that;
			return Objects.equals(projectId, thatPK.projectId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projectId, userId);
		}

	}

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = "PROJECT_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Project ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String projectId;

	public MLPProjUserAccMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param userId
	 *                   user ID
	 * @param projId
	 *                   project ID
	 */
	public MLPProjUserAccMap(String userId, String projId) {
		if (projId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.projectId = projId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPProjUserAccMap(MLPProjUserAccMap that) {
		this.projectId = that.projectId;
		this.userId = that.userId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String theId) {
		this.projectId = theId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String theId) {
		this.userId = theId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPProjUserAccMap))
			return false;
		MLPProjUserAccMap thatObj = (MLPProjUserAccMap) that;
		return Objects.equals(projectId, thatObj.projectId) && Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projectId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", projectId=" + projectId + "]";
	}

}
