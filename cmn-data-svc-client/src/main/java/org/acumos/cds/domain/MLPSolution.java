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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Solution entity with complex mappings for tags and web stats. Inherits all
 * simple field mappings from the abstract superclass.
 */
@Entity
@Table(name = MLPAbstractSolution.TABLE_NAME)
public class MLPSolution extends MLPAbstractSolution implements Serializable {

	private static final long serialVersionUID = 745945642089325612L;

	@Column(name = OWNER_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "OwnerId cannot be null")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "User ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String ownerId;

	/**
	 * ID of the peer where this was onboarded; null indicates local. Supports
	 * federation.
	 */
	@Column(name = "SOURCE_ID", columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, value = "Peer ID", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String sourceId;

	/**
	 * Tags assigned to the solution via a join table. Tags can be reused by many
	 * solutions, so this is a many-many (not one-many) relationship.
	 * 
	 * Unidirectional relationship - the MLPTag object is not annotated.
	 * 
	 * This does NOT use cascade; e.g., "cascade = { CascadeType.ALL }". With that
	 * annotation, use of an EXISTING tag when creating a solution yields a SQL
	 * constraint-violation error, Hibernate attempts to insert a duplicate row to
	 * the join table, also see https://hibernate.atlassian.net/browse/HHH-6776
	 * 
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = MLPSolTagMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPSolTagMap.SOL_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPSolTagMap.TAG_COL_NAME) })
	private Set<MLPTag> tags = new HashSet<>(0);

	/**
	 * Statistics about downloads, ratings etc. Should always exist, but don't mark
	 * as required.
	 * 
	 * Unidirectional relationship - the MLPSolutionWeb object is not annotated.
	 * 
	 * This is optional (the default) because of the unidirectional relationship.
	 * Without annotation and a setter on the MLPSolutionWeb object there's no way
	 * to create a solution.
	 * 
	 * This does NOT use cascade; e.g., "cascade = { CascadeType.ALL }". Tests WITH
	 * that annotation revealed no problems, but the controller does not accept
	 * updates to the web stats via the solution object, so there is no need.
	 */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = MLPSolutionWeb.SOL_ID_COL_NAME)
	// Swagger fails to recognize readOnly on complex objects
	@ApiModelProperty(readOnly = true, value = "Stats are read-only")
	private MLPSolutionWeb webStats;

	/**
	 * No-arg constructor
	 */
	public MLPSolution() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param name
	 *            Solution Name
	 * @param ownerId
	 *            User ID of owner
	 * @param active
	 *            Boolean flag
	 */
	public MLPSolution(String name, String ownerId, boolean active) {
		super(name, active);
		this.ownerId = ownerId;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPSolution(MLPSolution that) {
		super(that);
		this.ownerId = that.ownerId;
		this.sourceId = that.sourceId;
		this.tags = that.tags;
		this.webStats = that.webStats;
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

	/**
	 * Solution tags may be updated by modifying this set, but all tag objects must
	 * exist; i.e., have been created previously.
	 * 
	 * @return Set of MLPTag, which may be empty.
	 */
	public Set<MLPTag> getTags() {
		return tags;
	}

	/**
	 * Solution tags may be updated via this method, but all tag objects must exist;
	 * i.e., have been created previously.
	 * 
	 * @param tags
	 *            Set of MLPTag
	 */
	public void setTags(Set<MLPTag> tags) {
		this.tags = tags;
	}

	/**
	 * Provides counts of user activity such as downloads. These counts CANNOT be
	 * updated via this object; all changes made here are discarded.
	 * 
	 * @return MLPSolutionWeb object
	 */
	public MLPSolutionWeb getWebStats() {
		return webStats;
	}

	/**
	 * User activity counts CANNOT be updated via this object; all changes made here
	 * are discarded.
	 * 
	 * @param webStats
	 *            MLPSolutionWeb object
	 */
	public void setWebStats(MLPSolutionWeb webStats) {
		this.webStats = webStats;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[solutionId=" + getSolutionId() + ", name=" + getName() + ", owner="
				+ ownerId + ", desc=" + getDescription() + ", active=" + isActive() + ", modelTypeCode="
				+ getModelTypeCode() + ", validationStatusCode=" + getValidationStatusCode() + ", provider="
				+ getProvider() + ", created=" + getCreated() + ", modified=" + getModified() + "]";
	}

}
