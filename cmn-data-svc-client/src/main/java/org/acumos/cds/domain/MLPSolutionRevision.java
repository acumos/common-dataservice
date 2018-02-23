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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Solution revision entity with basic mappings for solution, owner and source.
 * Inherits all simple field mappings from the abstract superclass.
 */
@Entity
@Table(name = MLPAbstractSolutionRevision.TABLE_NAME)
public class MLPSolutionRevision extends MLPAbstractSolutionRevision implements Serializable {

	private static final long serialVersionUID = 7037843597553091071L;

	@Column(name = SOL_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	private String solutionId;

	@Column(name = "OWNER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "OwnerId cannot be null")
	@Size(max = 36)
	private String ownerId;

	/**
	 * ID of the peer where this was onboarded; null indicates local. Supports
	 * federation.
	 */
	@Column(name = "SOURCE_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	private String sourceId;

	/**
	 * No-arg constructor
	 */
	public MLPSolutionRevision() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param version
	 *            User-assigned version string
	 * @param ownerId
	 *            User ID
	 */
	public MLPSolutionRevision(String solutionId, String version, String ownerId) {
		super(version);
		if (solutionId == null || ownerId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.ownerId = ownerId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + getRevisionId() + ", solutionId=" + solutionId
				+ ", ownerId=" + ownerId + ", meta=" + getMetadata() + ", version=" + getVersion() + ", created="
				+ getCreated() + ", modified=" + getModified() + "]";
	}
}
