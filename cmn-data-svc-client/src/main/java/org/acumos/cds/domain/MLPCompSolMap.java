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
import javax.validation.constraints.Size;

import org.acumos.cds.domain.MLPCompSolMap.CompSolMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the composite solution mapping table. This is in lieu of
 * many-to-one annotations.
 */
@Entity
@IdClass(CompSolMapPK.class)
@Table(name = MLPCompSolMap.TABLE_NAME)
public class MLPCompSolMap implements MLPEntity, Serializable {

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_COMP_SOL_MAP";
	/* package */ static final String PARENT_ID_COL_NAME = "PARENT_ID";
	/* package */ static final String CHILD_ID_COL_NAME = "CHILD_ID";
	private static final long serialVersionUID = 5998210814745640634L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class CompSolMapPK implements Serializable {

		private static final long serialVersionUID = -8994409294830976174L;
		private String parentId;
		private String childId;

		public CompSolMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param parentId
		 *            solution ID
		 * @param childId
		 *            solution ID
		 */
		public CompSolMapPK(String parentId, String childId) {
			this.parentId = parentId;
			this.childId = childId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof CompSolMapPK))
				return false;
			CompSolMapPK thatPK = (CompSolMapPK) that;
			return Objects.equals(parentId, thatPK.parentId) && Objects.equals(childId, thatPK.childId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(parentId, childId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[parentId=" + parentId + ", childId=" + childId + "]";
		}

	}

	@Id
	@Column(name = MLPCompSolMap.PARENT_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String parentId;

	@Id
	@Column(name = MLPCompSolMap.CHILD_ID_COL_NAME, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String childId;

	/**
	 * No-arg constructor
	 */
	public MLPCompSolMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 * 
	 * @param parentId
	 *            solution ID
	 * @param childId
	 *            child ID
	 */
	public MLPCompSolMap(String parentId, String childId) {
		if (parentId == null || childId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.parentId = parentId;
		this.childId = childId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPCompSolMap(MLPCompSolMap that) {
		this.parentId = that.parentId;
		this.childId = that.childId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getChildId() {
		return childId;
	}

	public void setChildId(String childId) {
		this.childId = childId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPCompSolMap))
			return false;
		MLPCompSolMap thatObj = (MLPCompSolMap) that;
		return Objects.equals(parentId, thatObj.parentId) && Objects.equals(childId, thatObj.childId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parentId, childId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[parentId=" + parentId + ", childId=" + childId + "]";
	}

}
