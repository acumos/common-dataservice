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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.acumos.cds.transport.AuthorTransport;
import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;

/**
 * Base model for a solution revision. Maps all simple columns; maps no complex
 * columns that a subclass might want to map in alternate ways. For example the
 * solution ID column is not mapped here; that is an entity ID, and could be
 * exposed as a string or as an object via Hibernate.
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
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
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
	 * The Access Type Code value set is defined by server-side configuration.
	 */
	@Column(name = "ACCESS_TYPE_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Access type code cannot be null")
	@Size(max = 2)
	private String accessTypeCode;

	/**
	 * The Validation Status Code value set is defined by server-side configuration.
	 */
	@Column(name = "VALIDATION_STATUS_CD", nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String validationStatusCode;

	/**
	 * Structured text with author (name, contact) pairs
	 */
	@Column(name = "AUTHORS", columnDefinition = "VARCHAR(1024)")
	@Size(max = 1024)
	private String authors;

	/**
	 * Free text with a company or organization name.
	 */
	@Column(name = "PUBLISHER", columnDefinition = "VARCHAR(64)")
	@Size(max = 64)
	private String publisher;

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
	 * @param accessTypeCode
	 *            Access type code
	 * @param validationStatusCode
	 *            Validation status code
	 */
	public MLPAbstractSolutionRevision(String version, String accessTypeCode, String validationStatusCode) {
		if (version == null || accessTypeCode == null || validationStatusCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.version = version;
		this.accessTypeCode = accessTypeCode;
		this.validationStatusCode = validationStatusCode;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPAbstractSolutionRevision(MLPAbstractSolutionRevision that) {
		super(that);
		this.accessTypeCode = that.accessTypeCode;
		this.authors = that.authors;
		this.description = that.description;
		this.metadata = that.metadata;
		this.origin = that.origin;
		this.publisher = that.publisher;
		this.revisionId = that.revisionId;
		this.validationStatusCode = that.validationStatusCode;
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

	public String getAccessTypeCode() {
		return accessTypeCode;
	}

	public void setAccessTypeCode(String accessTypeCode) {
		this.accessTypeCode = accessTypeCode;
	}

	public String getValidationStatusCode() {
		return validationStatusCode;
	}

	public void setValidationStatusCode(String validationStatusCode) {
		this.validationStatusCode = validationStatusCode;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/** author row separator character */
	private static final String AUTHOR_ROW_SEP = "\n";
	private static final String AUTHOR_ROW_REGEX = "[" + AUTHOR_ROW_SEP + "]";
	/** author pair separator character */
	private static final String AUTHOR_PAIR_SEP = "\t";
	private static final String AUTHOR_PAIR_REGEX = "[" + AUTHOR_PAIR_SEP + "]";

	/**
	 * Gets the authors. Converts the internal data storage format to a set of
	 * objects.
	 * 
	 * @return Array of author (name, contact) pairs
	 */
	public AuthorTransport[] getAuthors() {
		Set<AuthorTransport> set = new HashSet<>();
		if (authors != null && authors.length() > 0) {
			String[] rows = authors.split(AUTHOR_ROW_REGEX);
			for (String r : rows) {
				String[] pair = r.split(AUTHOR_PAIR_REGEX);
				if (pair[0].isEmpty() && pair[1].isEmpty())
					continue;
				AuthorTransport a = new AuthorTransport(pair[0], pair[1]);
				set.add(a);
			}
		}
		AuthorTransport[] result = new AuthorTransport[set.size()];
		set.toArray(result);
		return result;
	}

	/**
	 * Sets the authors. Converts the set of objects to the internal storage format.
	 * 
	 * @param authors
	 *            Set of author (name, contact) pairs. Must not contain the
	 *            character {@link #AUTHOR_PAIR_SEP} nor {@link #AUTHOR_ROW_SEP}.
	 */
	public void setAuthors(AuthorTransport[] authors) {
		StringBuilder sb = new StringBuilder();
		for (AuthorTransport a : authors) {
			if (a.getName().isEmpty() && a.getContact().isEmpty())
				continue;
			if (a.getName().contains(AUTHOR_ROW_SEP) || a.getName().contains(AUTHOR_PAIR_SEP))
				throw new IllegalArgumentException("Illegal character in name");
			if (a.getContact().contains(AUTHOR_ROW_SEP) || a.getContact().contains(AUTHOR_PAIR_SEP))
				throw new IllegalArgumentException("Illegal character in contact");
			if (sb.length() > 0)
				sb.append(AUTHOR_ROW_SEP);
			sb.append(a.getName() + AUTHOR_PAIR_SEP + a.getContact());
		}
		this.authors = sb.toString();
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
