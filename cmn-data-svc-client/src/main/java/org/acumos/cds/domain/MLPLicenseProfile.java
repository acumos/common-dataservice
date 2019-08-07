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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for license profile, a lightly decorated key-value store for JSON
 * content.
 */
@Entity
@Table(name = "C_LICENSE_PROFILE")
public class MLPLicenseProfile extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 6260070847006499966L;

	@Id
	@Column(name = "LICENSE_ID", nullable = false, updatable = false, columnDefinition = "VARCHAR(50)")
	@NotNull(message = "License ID cannot be null")
	@Size(max = 50)
	@ApiModelProperty(required = true, value = "Unique key", example = "license_profile_key_1")
	private String licenseId;

	// JSON
	@Column(name = "LICENSE", nullable = false, columnDefinition = "VARCHAR(8192)")
	@NotNull(message = "License cannot be null")
	@ApiModelProperty(required = true, value = "JSON", example = "{ \"tag\" : \"value\" }")
	@Size(max = 8192)
	private String license;

	@Column(name = "PRIORITY", nullable = false, columnDefinition = "INT")
	@NotNull(message = "Priority cannot be null")
	@Min(value = 0)
	@ApiModelProperty(required = true, value = "Priority", example = "1")
	private long priority;

	@Column(name = "USER_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "User ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(value = "UUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String userId;

	/**
	 * No-arg constructor.
	 */
	public MLPLicenseProfile() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param licenseId
	 *                      Row ID
	 * @param license
	 *                      License block, validated as JSON
	 * @param priority
	 *                      Priority
	 * @param userId
	 *                      User ID
	 * 
	 */
	public MLPLicenseProfile(String licenseId, String license, int priority, String userId) {
		if (licenseId == null || license == null || userId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.licenseId = licenseId;
		this.license = license;
		this.userId = userId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPLicenseProfile(MLPLicenseProfile that) {
		super(that);
		this.licenseId = that.licenseId;
		this.license = that.license;
		this.priority = that.priority;
		this.userId = that.userId;
	}

	public String getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(String id) {
		this.licenseId = id;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
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
		if (!(that instanceof MLPLicenseProfile))
			return false;
		MLPLicenseProfile thatObj = (MLPLicenseProfile) that;
		return Objects.equals(licenseId, thatObj.licenseId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(licenseId, license, userId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[id=" + licenseId + ", value=" + license + ", prio=" + priority + ", user="
				+ userId + ", created=" + getCreated() + ", ...]";
	}

}
