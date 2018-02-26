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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * Solution revision entity with full object mappings (FOM, a lousy name I know)
 * for all complex fields including solution, owner, etc. Inherits all simple
 * field mappings from the abstract superclass.
 * 
 * Defined in the server project because it's not exposed to clients.
 * 
 * Provides a method to convert self to the client-friendly class.
 */
@Entity
@Table(name = "C_SOLUTION_REV")
@Immutable // never used for update
public class MLPSolutionRevisionFOM extends MLPAbstractSolutionRevision implements Serializable {

	private static final long serialVersionUID = -5309122724506045521L;

	/**
	 * Bidirectional mapping
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = SOL_ID_COL_NAME, nullable = false, columnDefinition = "CHAR(36)")
	private MLPSolutionFOM solution;

	/**
	 * Unidirectional mapping
	 */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER_ID", nullable = false, columnDefinition = "CHAR(36)")
	private MLPUser owner;

	/**
	 * Unidirectional mapping
	 */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_ID", nullable = false, columnDefinition = "CHAR(36)")
	private MLPPeer source;

	/**
	 * A revision may have many artifacts. A mapping table connects them.
	 * Bidirectional mapping.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = MLPSolRevArtMap.TABLE_NAME, //
			joinColumns = { @JoinColumn(name = MLPSolRevArtMap.REVISION_ID_COL_NAME) }, //
			inverseJoinColumns = { @JoinColumn(name = MLPSolRevArtMap.ARTIFACT_ID_COL_NAME) } //
	)
	private Set<MLPArtifactFOM> artifacts = new HashSet<>(0);

	public MLPSolutionFOM getSolution() {
		return solution;
	}

	public void setSolution(MLPSolutionFOM solution) {
		this.solution = solution;
	}

	public MLPUser getOwner() {
		return owner;
	}

	public void setOwner(MLPUser owner) {
		this.owner = owner;
	}

	public MLPPeer getSource() {
		return source;
	}

	public void setSource(MLPPeer source) {
		this.source = source;
	}

	public Set<MLPArtifactFOM> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<MLPArtifactFOM> artifacts) {
		this.artifacts = artifacts;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[revisionId=" + getRevisionId() + ", owner=" + owner + ", meta="
				+ getMetadata() + ", version=" + getVersion() + ", artifacts=" + artifacts + ", created=" + getCreated()
				+ ", modified=" + getModified() + "]";
	}

	/**
	 * @return MLPSolutionRevision with the information from this entity
	 */
	public MLPSolutionRevision toMLPSolutionRevision() {
		MLPSolutionRevision rev = new MLPSolutionRevision(solution.getSolutionId(), getVersion(), owner.getUserId());
		rev.setCreated(getCreated());
		rev.setDescription(getDescription());
		rev.setMetadata(getMetadata());
		rev.setModified(getModified());
		rev.setOrigin(getOrigin());
		rev.setRevisionId(getRevisionId());
		if (source != null)
			rev.setSourceId(source.getPeerId());
		rev.setVersion(getVersion());
		return rev;
	}

}
