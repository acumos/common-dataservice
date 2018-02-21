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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Solution entity with full object mappings (FOM, a lousy name I know) for all
 * complex fields including tags, web stats and revisions. Inherits all simple
 * field mappings from the abstract superclass. Only used for query, never used
 * for update.
 * 
 * Defined in the server package to support queries; not exposed to clients.
 */
@Entity
@Table(name = "C_SOLUTION")
@Immutable
public class MLPSolutionFOM extends MLPAbstractSolution implements Serializable {

	private static final long serialVersionUID = -6075523082529564585L;

	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="OWNER_ID", nullable = false, columnDefinition = "CHAR(36)")
	private MLPUser owner;

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
	private MLPSolutionWeb webStats;

	/**
	 * A solution may have many solution revisions. The solution revision entity has
	 * the solutionId field.
	 */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = MLPSolutionRevision.SOL_ID_COL_NAME) // in MLPSolutionRevision
	private Set<MLPSolutionRevision> revisions = new HashSet<>(0);

	/**
	 * No-arg constructor
	 */
	public MLPSolutionFOM() {
		// no-arg constructor
	}

	public MLPUser getOwner() {
		return owner;
	}

	public void setOwner(MLPUser owner) {
		this.owner = owner;
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
				+ owner + ", desc=" + getDescription() + ", active=" + isActive() + ", accessTypeCode="
				+ getAccessTypeCode() + ", modelTypeCode=" + getModelTypeCode() + ", validationStatusCode="
				+ getValidationStatusCode() + ", provider=" + getProvider() + ", revision count=" + revisions.size()
				+ ", created=" + getCreated() + ", modified=" + getModified() + "]";
	}

	/**
	 * @return MLPSolution with the information from this entity
	 */
	public MLPSolution toMLPSolution() {
		MLPSolution sol = new MLPSolution(getName(), owner.getUserId(), isActive());
		sol.setAccessTypeCode(getAccessTypeCode());
		sol.setCreated(getCreated());
		sol.setDescription(getDescription());
		sol.setMetadata(getMetadata());
		sol.setModelTypeCode(getModelTypeCode());
		sol.setModified(getModified());
		sol.setName(getName());
		sol.setOrigin(getOrigin());
		sol.setProvider(getProvider());
		sol.setSolutionId(getSolutionId());
		sol.setSourceId(getSourceId());
		sol.setTags(getTags());
		sol.setToolkitTypeCode(getToolkitTypeCode());
		sol.setValidationStatusCode(getValidationStatusCode());
		sol.setWebStats(getWebStats());
		return sol;
	}
}
