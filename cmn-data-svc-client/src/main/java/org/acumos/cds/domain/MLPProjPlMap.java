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

import org.acumos.cds.domain.MLPProjPlMap.ProjPlMapPK;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a row in the project-pipeline mapping table.
 */
@Entity
@IdClass(ProjPlMapPK.class)
@Table(name = "C_PROJ_PL_MAP")
public class MLPProjPlMap implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 535005148071293009L;

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class ProjPlMapPK implements Serializable {

		private static final long serialVersionUID = -2350615411857874125L;
		private String projId;
		private String plId;

		public ProjPlMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param projId
		 *                   project ID
		 * @param plId
		 *                   pipeline ID
		 */
		public ProjPlMapPK(String projId, String plId) {
			this.projId = projId;
			this.plId = plId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof ProjPlMapPK))
				return false;
			ProjPlMapPK thatPK = (ProjPlMapPK) that;
			return Objects.equals(projId, thatPK.projId) && Objects.equals(plId, thatPK.plId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projId, plId);
		}

	}

	@Id
	@Column(name = "PROJ_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String projId;

	@Id
	@Column(name = "PL_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Role ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String plId;

	public MLPProjPlMap() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param projId
	 *                   project ID
	 * @param plId
	 *                   pipeline ID
	 */
	public MLPProjPlMap(String projId, String plId) {
		if (projId == null || plId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.projId = projId;
		this.plId = plId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPProjPlMap(MLPProjPlMap that) {
		this.projId = that.projId;
		this.plId = that.plId;
	}

	public String getProjId() {
		return projId;
	}

	public void setProjId(String projId) {
		this.projId = projId;
	}

	public String getPlId() {
		return plId;
	}

	public void setPlId(String plId) {
		this.plId = plId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPProjPlMap))
			return false;
		MLPProjPlMap thatObj = (MLPProjPlMap) that;
		return Objects.equals(projId, thatObj.projId) && Objects.equals(plId, thatObj.plId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(projId, plId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[projId=" + projId + ", plId=" + plId + "]";
	}

}
