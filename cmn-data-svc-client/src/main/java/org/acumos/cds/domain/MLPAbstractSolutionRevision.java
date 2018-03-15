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

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;

/**
 * Base model for a solution revision. Maps all simple columns; maps no complex
 * columns that a subclass might want to map in alternate ways. For example the
 * owner column is not mapped here; that is a user ID reference to an MLPUser
 * entity, and could be exposed as a string or as an object via Hibernate magic.
 */
@MappedSuperclass
public abstract class MLPAbstractSolutionRevision extends MLPTimestampedEntity {

	/* package */ static final String TABLE_NAME = "C_SOLUTION_REV";
	/* package */ static final String SOL_ID_COL_NAME = "SOLUTION_ID";

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "REVISION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example="12345678-abcd-90ab-cdef-1234567890ab")
	// Generated by DB; no NotNull annotation needed.
	private String revisionId;

	@Column(name = "VERSION", nullable = false, columnDefinition = "VARCHAR(25)")
	@NotNull(message = "Version cannot be null")
	@Size(max = 25)
	private String version;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	private String description;

	@Column(name = "METADATA", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	private String metadata;

	/**
	 * URI of the peer that provided this object. Supports federation.
	 */
	@Column(name = "ORIGIN", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	private String origin;

	/**
	 * No-arg constructor
	 */
	public MLPAbstractSolutionRevision() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param version
	 *            User-assigned version string
	 */
	public MLPAbstractSolutionRevision(String version) {
		if (version == null)
			throw new IllegalArgumentException("Null not permitted");
		this.version = version;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPAbstractSolutionRevision(MLPAbstractSolutionRevision that) {
		super(that);
		this.description = that.description;
		this.metadata = that.metadata;
		this.origin = that.origin;
		this.revisionId = that.revisionId;
		this.version = that.version;
	}

	public String getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String meta) {
		this.metadata = meta;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPAbstractSolutionRevision))
			return false;
		MLPAbstractSolutionRevision thatObj = (MLPAbstractSolutionRevision) that;
		return Objects.equals(revisionId, thatObj.revisionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(revisionId, version);
	}
}
