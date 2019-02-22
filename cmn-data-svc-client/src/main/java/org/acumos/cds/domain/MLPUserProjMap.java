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

import org.acumos.cds.domain.MLPUserProjMap.UserProjMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the user-project mapping table.
 */
@Entity
@IdClass(UserProjMapPK.class)
@Table(name = "C_USER_PROJ_MAP")
public class MLPUserProjMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 5273586022262263377L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class UserProjMapPK implements Serializable {

		private static final long serialVersionUID = -198627735707587054L;
		private String userId;
		private String projId;

		public UserProjMapPK() {
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
		public UserProjMapPK(String userId, String projId) {
			this.userId = userId;
			this.projId = projId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof UserProjMapPK))
				return false;
			UserProjMapPK thatPK = (UserProjMapPK) that;
			return Objects.equals(projId, thatPK.projId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projId, userId);
		}

	}

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = "PROJ_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Project ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String projId;

	public MLPUserProjMap() {
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
	public MLPUserProjMap(String userId, String projId) {
		if (projId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.projId = projId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserProjMap(MLPUserProjMap that) {
		this.projId = that.projId;
		this.userId = that.userId;
	}

	public String getProjId() {
		return projId;
	}

	public void setProjId(String projId) {
		this.projId = projId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserProjMap))
			return false;
		MLPUserProjMap thatObj = (MLPUserProjMap) that;
		return Objects.equals(projId, thatObj.projId) && Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", projId=" + projId + "]";
	}

}
