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

import org.acumos.cds.domain.MLPUserNbMap.UserNbMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the user-notebook mapping table.
 */
@Entity
@IdClass(UserNbMapPK.class)
@Table(name = "C_USER_NB_MAP")
public class MLPUserNbMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 1904731283050715397L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class UserNbMapPK implements Serializable {

		private static final long serialVersionUID = -2047742184842023828L;
		private String userId;
		private String nbId;

		public UserNbMapPK() {
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
		public UserNbMapPK(String userId, String projId) {
			this.userId = userId;
			this.nbId = projId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof UserNbMapPK))
				return false;
			UserNbMapPK thatPK = (UserNbMapPK) that;
			return Objects.equals(nbId, thatPK.nbId) && Objects.equals(userId, thatPK.userId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(nbId, userId);
		}

	}

	@Id
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	@Id
	@Column(name = "NB_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Notebook ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String nbId;

	public MLPUserNbMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param userId
	 *                   user ID
	 * @param nbId
	 *                   notebook ID
	 */
	public MLPUserNbMap(String userId, String nbId) {
		if (nbId == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.nbId = nbId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUserNbMap(MLPUserNbMap that) {
		this.nbId = that.nbId;
		this.userId = that.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPlId() {
		return nbId;
	}

	public void setPlId(String nbId) {
		this.nbId = nbId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserNbMap))
			return false;
		MLPUserNbMap thatObj = (MLPUserNbMap) that;
		return Objects.equals(nbId, thatObj.nbId) && Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nbId, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", nbId=" + nbId + "]";
	}

}
