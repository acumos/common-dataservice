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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

/**
 * Model for licensing right to use in the Boreas release, which mocks up some
 * features of an external License User Manager system.
 */
@Entity
@Table(name = "C_RIGHT_TO_USE")
public class MLPRightToUse extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 5160126100103971423L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	// The "native" annotations work in Hibernate 5.3 with Mariadb 10.2.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "RTU_ID", nullable = false, updatable = false, columnDefinition = "INT")
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY, value = "Generated", example = "12345")
	// Generated by DB; NotNull annotation not needed
	private Long rtuId;

	@Column(name = "SOLUTION_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Solution ID cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "GUID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String solutionId;

	@Column(name = "SITE_YN", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
	@NotNull(message = "Site indicator cannot be null")
	@Type(type = "yes_no")
	@ApiModelProperty(required = true, value = "Boolean indicator")
	private boolean site;

	/**
	 * Reference IDs (GUIDs) assigned to the Right-to-Use record via a join table.
	 * This is a many-many (not one-many) relationship.
	 * 
	 * Unidirectional relationship - there is no reference object.
	 * 
	 * This does NOT use cascade; e.g., "cascade = { CascadeType.ALL }".
	 * 
	 * Eager fetch type ensures that references are present when an entity is
	 * fetched by ID via a Spring-generated repository method.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = MLPRtuRefMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPRtuRefMap.RTU_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPRtuRefMap.REF_ID_COL_NAME) })
	private Set<MLPRtuReference> rtuReferences = new HashSet<>(0);

	/**
	 * No-arg constructor
	 */
	public MLPRightToUse() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits role ID, which is generated on
	 * save.
	 * 
	 * @param solutionId
	 *                       Solution ID
	 * @param site
	 *                       True if site-wide
	 */
	public MLPRightToUse(String solutionId, boolean site) {
		if (solutionId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.solutionId = solutionId;
		this.site = site;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRightToUse(MLPRightToUse that) {
		super(that);
		this.rtuId = that.rtuId;
		this.site = that.site;
		this.solutionId = that.solutionId;
		this.rtuReferences = that.rtuReferences;
	}

	public Long getRtuId() {
		return rtuId;
	}

	public void setRtuId(Long rtuId) {
		this.rtuId = rtuId;
	}

	public String getSolutionId() {
		return solutionId;
	}

	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}

	public boolean isSite() {
		return site;
	}

	public void setSite(boolean site) {
		this.site = site;
	}

	public Set<MLPRtuReference> getRtuReferences() {
		return rtuReferences;
	}

	public void setRtuReferences(Set<MLPRtuReference> rtuReferences) {
		this.rtuReferences = rtuReferences;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rtuId, solutionId);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRightToUse))
			return false;
		MLPRightToUse thatObj = (MLPRightToUse) that;
		return Objects.equals(rtuId, thatObj.rtuId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[rtuId=" + rtuId + ", solutionId=" + solutionId + ", rtuReferences="
				+ rtuReferences + ", created=" + getCreated() + ", modified=" + getModified() + "]";
	}

}