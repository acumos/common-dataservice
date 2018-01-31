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
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Model for a row in the peer group - peer mapping table. 
 */
@Entity
@IdClass(MLPGrpPeerMap.GrpPeerMapPK.class)
@Table(name = MLPGrpPeerMap.TABLE_NAME)
public class MLPGrpPeerMap implements MLPEntity, Serializable {

	private static final long serialVersionUID = -4275921877383738725L;

	// Define constants so names can be reused in many-many annotation.
	/* package */ static final String TABLE_NAME = "C_GRP_PEER_MAP";
	/* package */ static final String GRP_ID_COL_NAME = "GROUP_ID";
	/* package */ static final String PEER_ID_COL_NAME = "PEER_ID";

	/**
	 * Embedded key for Hibernate
	 */
	@Embeddable
	public static class GrpPeerMapPK implements Serializable {

		private static final long serialVersionUID = 8686964092888375139L;
		private Long groupId;
		private String peerId;

		public GrpPeerMapPK() {
			// no-arg constructor
		}

		/**
		 * Convenience constructor
		 * 
		 * @param groupId
		 *            group ID
		 * @param peerId
		 *            Peer ID
		 */
		public GrpPeerMapPK(Long groupId, String peerId) {
			this.groupId = groupId;
			this.peerId = peerId;
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (!(that instanceof GrpPeerMapPK))
				return false;
			GrpPeerMapPK thatPK = (GrpPeerMapPK) that;
			return Objects.equals(groupId, thatPK.groupId) && Objects.equals(peerId, thatPK.peerId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(groupId, peerId);
		}

		@Override
		public String toString() {
			return this.getClass().getName() + "[groupId=" + groupId + ", peerId=" + peerId + "]";
		}

	}

	@Id
	@Column(name = MLPGrpPeerMap.GRP_ID_COL_NAME, updatable = false, nullable = false, columnDefinition = "INT")
	private Long groupId;

	@Id
	@Column(name = MLPGrpPeerMap.PEER_ID_COL_NAME, updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	private String peerId;

	@CreationTimestamp
	@Column(name = "CREATED_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	private Date created;

	/**
	 * No-arg constructor
	 */
	public MLPGrpPeerMap() {
		// no-arg constructor
	}

	/**
	 * Convenience constructor
	 *
	 * @param groupId
	 *            group ID
	 * @param peerId
	 *            Peerution ID
	 */
	public MLPGrpPeerMap(Long groupId, String peerId) {
		if (groupId == null || peerId == null)
			throw new IllegalArgumentException("Null not permitted");
		this.groupId = groupId;
		this.peerId = peerId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPGrpPeerMap))
			return false;
		MLPGrpPeerMap thatObj = (MLPGrpPeerMap) that;
		return Objects.equals(groupId, thatObj.groupId) && Objects.equals(peerId, thatObj.peerId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId, peerId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[groupId=" + groupId + ", peerId=" + peerId + "]";
	}

}
