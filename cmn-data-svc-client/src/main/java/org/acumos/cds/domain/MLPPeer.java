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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;

/**
 * Model for a peer - systems that communicate.
 */
@Entity
@Table(name = "C_PEER")
public class MLPPeer extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = -8132835732122031289L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "PEER_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	// Generated by DB; NotNull annotation not needed
	private String peerId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(50)")
	@NotNull(message = "name cannot be null")
	@Size(max = 50)
	@ApiModelProperty(required = true, value = "Peer name", example = "My Peer Name")
	private String name;

	/**
	 * For x.509 certificate
	 */
	@Column(name = "SUBJECT_NAME", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "subjectName cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, value = "Certificate subject name", example = "peer.company.com")
	private String subjectName;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(value = "Free-text description")
	private String description;

	@Column(name = "API_URL", nullable = false, columnDefinition = "VARCHAR(512)")
	@NotNull(message = "apiUrl cannot be null")
	@Size(max = 512)
	@ApiModelProperty(required = true, value = "URL where peer listens", example = "http://peer.company.com/api")
	private String apiUrl;

	@Column(name = "WEB_URL", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	@ApiModelProperty(required = true, value = "Web URL")
	private String webUrl;

	@Column(name = "IS_SELF", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
	@NotNull(message = "Self cannot be null")
	@Type(type = "yes_no")
	@ApiModelProperty(required = true, value = "Boolean indicator, true if this entry refers to self")
	private boolean self;

	@Column(name = "IS_LOCAL", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
	@NotNull(message = "Local cannot be null")
	@Type(type = "yes_no")
	@ApiModelProperty(required = true, value = "Boolean indicator")
	private boolean local;

	@Column(name = "CONTACT1", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "contact1 cannot be null")
	@Size(max = 100)
	@ApiModelProperty(required = true, value = "Contact information", example = "Sys Admin 212-555-1212")
	private String contact1;

	@Column(name = "STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "statusCode cannot be null")
	@Size(max = 2)
	@ApiModelProperty(required = true, value = "Peer active status code", example = "AC")
	private String statusCode;

	/**
	 * No-arg constructor.
	 */
	public MLPPeer() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits peer ID, which is generated on
	 * save.
	 * 
	 * @param name
	 *                        Peer name
	 * @param subjectName
	 *                        X.509 subject name
	 * @param apiUrl
	 *                        API URL
	 * @param self
	 *                        Is the entry this site
	 * @param local
	 *                        Is the entry local
	 * @param contact1
	 *                        Primary contact details
	 * @param statusCode
	 *                        Peer status code
	 */
	public MLPPeer(String name, String subjectName, String apiUrl, boolean self, boolean local, String contact1,
			String statusCode) {
		if (name == null || subjectName == null || apiUrl == null || contact1 == null || statusCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
		this.subjectName = subjectName;
		this.apiUrl = apiUrl;
		this.self = self;
		this.local = local;
		this.contact1 = contact1;
		this.statusCode = statusCode;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPeer(MLPPeer that) {
		super(that);
		this.apiUrl = that.apiUrl;
		this.contact1 = that.contact1;
		this.description = that.description;
		this.local = that.local;
		this.self = that.self;
		this.name = that.name;
		this.peerId = that.peerId;
		this.statusCode = that.statusCode;
		this.subjectName = that.subjectName;
		this.webUrl = that.webUrl;
	}

	/**
	 * @return the peerId
	 */
	public String getPeerId() {
		return peerId;
	}

	/**
	 * @param peerId
	 *                   the peerId to set
	 */
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *                 the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the subject name
	 */
	public String getSubjectName() {
		return subjectName;
	}

	/**
	 * @param subjectName
	 *                        the name to set
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *                        the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the apiUrl
	 */
	public String getApiUrl() {
		return apiUrl;
	}

	/**
	 * @param apiUrl
	 *                   the apiUrl to set
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * @return the webUrl
	 */
	public String getWebUrl() {
		return webUrl;
	}

	/**
	 * @param webUrl
	 *                   the webUrl to set
	 */
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	/**
	 * @return the isSelf
	 */
	public boolean isSelf() {
		return self;
	}

	/**
	 * @param isSelf
	 *                   the isSelf to set
	 */
	public void setSelf(boolean isSelf) {
		this.self = isSelf;
	}

	/**
	 * @return the isLocal
	 */
	public boolean isLocal() {
		return local;
	}

	/**
	 * @param isLocal
	 *                    the isLocal to set
	 */
	public void setLocal(boolean isLocal) {
		this.local = isLocal;
	}

	/**
	 * @return the contact1
	 */
	public String getContact1() {
		return contact1;
	}

	/**
	 * @param contact1
	 *                     the contact1 to set
	 */
	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *                       A valid peer-status code
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPPeer))
			return false;
		MLPPeer thatObj = (MLPPeer) that;
		return Objects.equals(peerId, thatObj.peerId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(peerId, name, subjectName, webUrl);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[peerId=" + peerId + ", name=" + name + ", subjectName=" + subjectName
				+ "webUrl=" + webUrl + "]";
	}
}