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
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;

/**
 * Base model for a solution. Maps all simple columns; maps no complex columns
 * that a subclass might want to map in alternate ways. For example the owner
 * column is not mapped here; that is a user ID reference to an MLPUser entity,
 * and could be exposed as a string or as an object via Hibernate magic.
 */
@MappedSuperclass
public abstract class MLPAbstractSolution extends MLPTimestampedEntity {

	/* package */ static final String TABLE_NAME = "C_SOLUTION";
	/* package */ static final String OWNER_ID_COL_NAME = "OWNER_ID";

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "SOLUTION_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	// Generated by DB; NotNull annotation not needed
	private String solutionId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Name cannot be null")
	@Size(max = 100)
	private String name;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	private String description;

	@Column(name = "PROVIDER", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	private String provider;

	@Column(name = "METADATA", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	private String metadata;

	@Column(name = "ACTIVE_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
	@Type(type = "yes_no")
	private boolean active;

	@Column(name = "MODEL_TYPE_CD", columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String modelTypeCode;

	@Column(name = "TOOLKIT_TYPE_CD", columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String toolkitTypeCode;

	/**
	 * URI of the peer that provided this object. Supports federation.
	 */
	@Column(name = "ORIGIN", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	private String origin;

	/**
	 * Derby BLOB type allows 2GB. Mysql/Mariadb BLOB type only allows 64KB, that's
	 * too small. But Derby fails to create the table if type LONGBLOB is specified
	 * here. With no columDefinition attribute Derby generates a table AND Spring
	 * validates the MariaDB schema if the column is created as LONGBLOB.
	 * 
	 * Jackson handles base64 encoding.
	 */
	@Column(name = "PICTURE", length = 2000000 /* DO NOT USE: columnDefinition = "BLOB" */)

	@Lob
	private Byte[] picture;

	/**
	 * No-arg constructor
	 */
	public MLPAbstractSolution() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param name
	 *            Solution Name
	 * @param active
	 *            Boolean flag
	 */
	public MLPAbstractSolution(String name, boolean active) {
		if (name == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
		this.active = active;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPAbstractSolution(MLPAbstractSolution that) {
		super(that);
		this.active = that.active;
		this.description = that.description;
		this.metadata = that.metadata;
		this.modelTypeCode = that.modelTypeCode;
		this.name = that.name;
		this.origin = that.origin;
		this.picture = that.picture;
		this.provider = that.provider;
		this.solutionId = that.solutionId;
		this.toolkitTypeCode = that.toolkitTypeCode;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String meta) {
		this.metadata = meta;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getModelTypeCode() {
		return modelTypeCode;
	}

	/**
	 * @param modelTypeCode
	 *            A value obtained by calling
	 *            {@link org.acumos.cds.ModelTypeCode#toString()}.
	 */
	public void setModelTypeCode(String modelTypeCode) {
		this.modelTypeCode = modelTypeCode;
	}

	public String getToolkitTypeCode() {
		return toolkitTypeCode;
	}

	/**
	 * @param toolkitTypeCode
	 *            A value obtained by calling
	 *            {@link org.acumos.cds.ToolkitTypeCode#toString()}.
	 */
	public void setToolkitTypeCode(String toolkitTypeCode) {
		this.toolkitTypeCode = toolkitTypeCode;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Byte[] getPicture() {
		return picture;
	}

	public void setPicture(Byte[] picture) {
		this.picture = picture;
	}

	/**
	 * The ID field is primary, so defining this method here factors out code.
	 */
	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPAbstractSolution))
			return false;
		MLPAbstractSolution thatObj = (MLPAbstractSolution) that;
		return Objects.equals(solutionId, thatObj.solutionId);
	}

	/**
	 * The ID field is primary, so defining this method here factors out code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(solutionId, name, provider, description);
	}

}
