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
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a user profile.
 * 
 * Passwords and tokens on the disk are hashed (data at rest). Passwords and
 * tokens are sent to the server in the clear (data in flight), then hashed for
 * comparison to a stored value.
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
	@ApiModelProperty(value = "User first name", example = "Mary")
	private String firstName;

	@Column(name = "MIDDLE_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	@ApiModelProperty(value = "User middle name", example = "Jane")
	private String middleName;

	@Column(name = "LAST_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	@ApiModelProperty(value = "User last name", example = "Doe")
	private String lastName;

	@Column(name = "ORG_NAME", columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	@ApiModelProperty(value = "Organization name", example = "The Modeling Company")
	private String orgName;

	@Column(name = "EMAIL", nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
	@NotNull(message = "Email cannot be null")
	@Size(max = 50)
	@ApiModelProperty(required = true, value = "User email address", example = "Mary.Jane.Doe@TheModelingCompany.org")
	private String email;

	@Column(name = "LOGIN_NAME", nullable = false, unique = true, columnDefinition = "VARCHAR(25)")
	@NotNull(message = "LoginName cannot be null")
	@Size(max = 25)
	@ApiModelProperty(required = true, value = "Unique short user name")
	private String loginName;

	/**
	 * This field models the password hash stored on disk. It is ALSO used to
	 * transport clear-text from client to server. The server ignores this field on
	 * requests if its null; it nulls out this field in all responses.
	 * 
	 * Optional because some installations use an external single sign-on system.
	 * 
	 * Must NOT use this annotation because it breaks serialization in the client:
	 * (at-sign)JsonIgnore
	 */
	@Column(name = "LOGIN_HASH", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	@ApiModelProperty(value = "Transports password in clear text", example = "LongKeysAreHardToCrack12345")
	private String loginHash;

	@Column(name = "LOGIN_PASS_EXPIRE_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Login password expiration date", example = "2018-12-16T12:34:56.789Z")
	private Instant loginPassExpire;

	/**
	 * Used in early versions both to maintain web session information and as an API
	 * token.
	 */
	@Column(name = "AUTH_TOKEN", columnDefinition = "VARCHAR(4096)")
	@Size(max = 4096)
	@ApiModelProperty(value = "Authentication token")
	private String authToken;

	@Column(name = "ACTIVE_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
	@NotNull(message = "Active indicator cannot be null")
	@Type(type = "yes_no")
	@ApiModelProperty(required = true, value = "Boolean indicator")
	private boolean active;

	/**
	 * Date-time of most recent successful login.
	 * 
	 * Client sends date-time as long integer, milliseconds since the Epoch.
	 * 
	 * Should have used names "LOGIN_DATE" and loginDate.
	 */
	@Column(name = "LAST_LOGIN_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Last login date", example = "2018-12-16T12:34:56.789Z")
	private Instant lastLogin;

	/**
	 * Number of login failures. Should be null after a successful login.
	 */
	@Column(name = "LOGIN_FAIL_COUNT", columnDefinition = "SMALLINT")
	@ApiModelProperty(value = "Login failure count", example = "1")
	private Short loginFailCount;

	/**
	 * Date-time of login failure. Should be null after a successful login. Used to
	 * control the delay before this user can login again with a valid password.
	 * 
	 * Client sends date-time as long integer, milliseconds since the Epoch.
	 */
	@Column(name = "LOGIN_FAIL_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Login failure date", example = "2018-12-16T12:34:56.789Z")
	private Instant loginFailDate;

	/**
	 * Derby BLOB type allows 2GB. Mysql/Mariadb BLOB type only allows 64KB, that's
	 * too small. But Derby fails to create the table if type LONGBLOB is specified
	 * here. With no columDefinition attribute Derby generates a table AND Spring
	 * validates the MariaDB schema if the column is created as LONGBLOB.
	 * 
	 * Jackson handles base64 encoding.
	 */
	@Lob
	@Column(name = "PICTURE", length = 2000000 /* DO NOT USE: columnDefinition = "BLOB" */)
	@ApiModelProperty(value = "User profile picture as byte array")
	private byte[] picture;

	/**
	 * This field models the API token stored on disk.
	 */
	@Column(name = "API_TOKEN", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	@ApiModelProperty(value = "API token clear text")
	private String apiToken;

	/**
	 * This field models the verify token hash stored on disk. It is ALSO used to
	 * transport a clear-text token from client to server.
	 */
	@Column(name = "VERIFY_TOKEN_HASH", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	@ApiModelProperty(value = "Verification token, sent as clear text, never included in response")
	private String verifyTokenHash;

	/**
	 * Client sends date-time as long integer, milliseconds since the Epoch.
	 */
	@Column(name = "VERIFY_EXPIRE_DATE", columnDefinition = "TIMESTAMP")
	@ApiModelProperty(value = "Verification token expiration date", example = "2018-12-16T12:34:56.789Z")
	private Instant verifyExpiration;

	/**
	 * Tags assigned to the user via a join table. Tags can be reused by many users,
	 * so this is a many-many (not one-many) relationship.
	 * 
	 * Unidirectional relationship - the MLPTag object is not annotated.
	 * 
	 * This does NOT use cascade; e.g., "cascade = { CascadeType.ALL }". With that
	 * annotation, use of an EXISTING tag when creating a user yields a SQL
	 * constraint-violation error, Hibernate attempts to insert a duplicate row to
	 * the join table, also see https://hibernate.atlassian.net/browse/HHH-6776
	 * 
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = MLPUserTagMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPUserTagMap.USER_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPUserTagMap.TAG_COL_NAME) })
	private Set<MLPTag> tags = new HashSet<>(0);

	/**
	 * No-arg constructor
	 */
	public MLPUser() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits user ID, which is generated on
	 * save.
	 * 
	 * @param loginName
	 *                      user name
	 * @param email
	 *                      email address
	 * @param active
	 *                      boolean flag
	 */
	public MLPUser(String loginName, String email, boolean active) {
		if (loginName == null || email == null)
			throw new IllegalArgumentException("Null not permitted");
		this.loginName = loginName;
		this.email = email;
		this.active = active;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPUser(MLPUser that) {
		super(that);
		this.active = that.active;
		this.apiToken = that.apiToken;
		this.authToken = that.authToken;
		this.email = that.email;
		this.firstName = that.firstName;
		this.lastLogin = that.lastLogin;
		this.lastName = that.lastName;
		this.loginFailCount = that.loginFailCount;
		this.loginFailDate = that.loginFailDate;
		this.loginHash = that.loginHash;
		this.loginName = that.loginName;
		this.loginPassExpire = that.loginPassExpire;
		this.middleName = that.middleName;
		this.orgName = that.orgName;
		this.picture = that.picture;
		this.userId = that.userId;
		this.verifyTokenHash = that.verifyTokenHash;
		this.verifyExpiration = that.verifyExpiration;
		this.tags = that.tags;
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
	 * Gets the login hash (or possibly a clear-text password if used for client
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
	 * Sets the login hash.
	 * 
	 * @param hash
	 *                 The hash
	 */
	public void setLoginHash(String hash) {
		this.loginHash = hash;
	}

	public Instant getLoginPassExpire() {
		return loginPassExpire;
	}

	public void setLoginPassExpire(Instant date) {
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

	public Instant getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Instant lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Short getLoginFailCount() {
		return loginFailCount;
	}

	public void setLoginFailCount(Short loginFailCount) {
		this.loginFailCount = loginFailCount;
	}

	public Instant getLoginFailDate() {
		return loginFailDate;
	}

	public void setLoginFailDate(Instant loginFailDate) {
		this.loginFailDate = loginFailDate;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getVerifyTokenHash() {
		return verifyTokenHash;
	}

	public void setVerifyTokenHash(String verifyTokenHash) {
		this.verifyTokenHash = verifyTokenHash;
	}

	public Instant getVerifyExpiration() {
		return verifyExpiration;
	}

	public void setVerifyExpiration(Instant verifyExpiration) {
		this.verifyExpiration = verifyExpiration;
	}

	/**
	 * Tags may be updated by modifying this set, but all tag objects must exist;
	 * i.e., have been created previously.
	 * 
	 * @return Set of MLPTag, which may be empty.
	 */
	public Set<MLPTag> getTags() {
		return tags;
	}

	/**
	 * Tags may be updated via this method, but all tag objects must exist; i.e.,
	 * have been created previously.
	 * 
	 * @param tags
	 *                 Set of MLPTag
	 */
	public void setTags(Set<MLPTag> tags) {
		this.tags = tags;
	}

	/**
	 * Convenience method to set all hashed values to null. Factors out this
	 * sequence for reuse and especially to avoid missing one!
	 */
	public void clearHashes() {
		setLoginHash(null);
		setVerifyTokenHash(null);
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

	/**
	 * This toString() method is safe for use by loggers because it reveals no
	 * security-related field content (apiToken, authToken, loginHash).
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + "[userId=" + userId + ", active=" + active //
				+ ", apiToken is " + (apiToken == null ? "null" : "present") //
				+ ", authToken is " + (authToken == null ? "null" : "present") //
				+ ", email=" + email + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", lastLogin = " + lastLogin //
				+ ", loginHash is " + (loginHash == null ? "null" : "present") //
				+ ", loginName=" + loginName + ", loginPassExpires=" + loginPassExpire + ", orgName=" + orgName
				+ ", created=" + getCreated() + ", modified=" + getModified() + "]";
	}

}
