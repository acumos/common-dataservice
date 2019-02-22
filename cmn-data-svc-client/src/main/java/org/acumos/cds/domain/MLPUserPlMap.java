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

import org.acumos.cds.domain.MLPUserPlMap.UserPlMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the user-pipeline mapping table.
 */
@Entity
@IdClass(UserPlMapPK.class)
@Table(name = "C_USER_PL_MAP")
public class MLPUserPlMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 4947528879523481131L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class UserPlMapPK implements Serializable {

		private static final long serialVersionUID = 6509512562296542562L;
		private String userId;
		private String pipelineId;

		public UserPlMapPK() {
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
		public UserPlMapPK(String userId, String projId) {
			this.userId = userId;
			this.pipelineId = projId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof UserPlMapPK))
				return false;
			UserPlMapPK thatPK = (UserPlMapPK) that;
			return Objects.equals(pipelineId, thatPK.pipelineId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(pipelineId, userId);
		}

	}

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = "PIPELINE_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Pipeline ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String pipelineId;

	public MLPUserPlMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param userId
	 *                   user ID
	 * @param plId
	 *                   pipeline ID
	 */
	public MLPUserPlMap(String userId, String plId) {
		if (plId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.pipelineId = plId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserPlMap(MLPUserPlMap that) {
		this.pipelineId = that.pipelineId;
		this.userId = that.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String theId) {
		this.userId = theId;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String theId) {
		this.pipelineId = theId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserPlMap))
			return false;
		MLPUserPlMap thatObj = (MLPUserPlMap) that;
		return Objects.equals(pipelineId, thatObj.pipelineId) && Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pipelineId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", pipelineId=" + pipelineId + "]";
	}

}
