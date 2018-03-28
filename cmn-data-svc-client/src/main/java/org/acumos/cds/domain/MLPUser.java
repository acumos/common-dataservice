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
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a user profile. Passwords on the disk are encrypted (data at rest).
 * Passwords entered by a user are sent to the server in the clear (data in
 * flight). This model is used for both purposes.
 */
@Entity
@Table(name = "C_USER")
public class MLPUser extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 6443219375733216340L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	// Generated by DB; NotNull annotation not needed
	private String userId;

	@Column(name = "FIRST_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	private String firstName;

	@Column(name = "MIDDLE_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	private String middleName;

	@Column(name = "LAST_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	private String lastName;

	@Column(name = "ORG_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	private String orgName;

	@Column(name = "EMAIL", nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	private String email;

	@Column(name = "LOGIN_NAME", nullable = false, unique = true, columnDefinition = "VARCHAR(25)")
	@NotNull(message = "LoginName cannot be null")
	@Size(max = 25)
	@ApiModelProperty(required = true)
	private String loginName;

	/**
	 * This field models the hash stored on disk. This model is ALSO used to
	 * transport a clear-text password from client to server. The server blocks this
	 * field in all responses so the client never sees the hash.
	 * 
	 * Must NOT use this annotation because it breaks serialization in the client:
	 * (at-sign)JsonIgnore
	 */
	@Column(name = "LOGIN_HASH", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	@ApiModelProperty(value = "Transports password in clear text", example = "LongKeysAreHardToCrack12345")
	private String loginHash;

	@Column(name = "LOGIN_PASS_EXPIRE_DATE")
	@ApiModelProperty(value = "Millisec since the Epoch", example = "1521202458867")
	private Date loginPassExpire;

	@Column(name = "AUTH_TOKEN", columnDefinition = "VARCHAR(4096)")
	@Size(max = 4096)
	private String authToken;

	@Column(name = "ACTIVE_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
	@Type(type = "yes_no")
	@ApiModelProperty(required = true)
	private boolean active;

	@Column(name = "LAST_LOGIN_DATE")
	@ApiModelProperty(value = "Millisec since the Epoch", example = "1521202458867")
	private Date lastLogin;

	/* Jackson handles base64 encoding. MEDIUMBLOB in MariaDB. */
	@Column(name = "PICTURE", length = 2000000, columnDefinition = "BLOB")
	@Lob
	private Byte[] picture;

	/**
	 * No-arg constructor
	 */
	public MLPUser() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param loginName
	 *            user name
	 * @param active
	 *            boolean flag
	 */
	public MLPUser(String loginName, String email,  boolean active) {
		if (loginName == null)
			throw new IllegalArgumentException("Null not permitted");
		if (email == null)
			throw new IllegalArgumentException("Null not permitted");
		this.loginName = loginName;
		this.email = email;
		this.active = active;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPUser(MLPUser that) {
		super(that);
		this.active = that.active;
		this.authToken = that.authToken;
		this.email = that.email;
		this.firstName = that.firstName;
		this.lastLogin = that.lastLogin;
		this.lastName = that.lastName;
		this.loginHash = that.loginHash;
		this.loginName = that.loginName;
		this.loginPassExpire = that.loginPassExpire;
		this.middleName = that.middleName;
		this.orgName = that.orgName;
		this.picture = that.picture;
		this.userId = that.userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * Gets the hash (or possibly a clear-text password if used for client
	 * transport).
	 * 
	 * Must NOT use this annotation because it breaks the path from client to
	 * server: (at-sign)JsonProperty(access = Access.WRITE_ONLY)
	 * 
	 * @return The hash
	 */
	public String getLoginHash() {
		return loginHash;
	}

	/**
	 * Sets the hash.
	 * 
	 * @param hash
	 *            The hash
	 */
	public void setLoginHash(String hash) {
		this.loginHash = hash;
	}

	public Date getLoginPassExpire() {
		return loginPassExpire;
	}

	public void setLoginPassExpire(Date date) {
		this.loginPassExpire = date;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Byte[] getPicture() {
		return picture;
	}

	public void setPicture(Byte[] picture) {
		this.picture = picture;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUser))
			return false;
		MLPUser thatObj = (MLPUser) that;
		return Objects.equals(userId, thatObj.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, loginName, loginHash);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", loginName=" + loginName + ", email=" + email
				+ ", lastName=" + lastName + ", middleName=" + middleName + ", firstName=" + firstName + "orgName="
				+ orgName + ", lastLogin = " + lastLogin + "loginPassExpires=" + loginPassExpire + ", created="
				+ getCreated() + ", modified=" + getModified() + "]";
	}

}
