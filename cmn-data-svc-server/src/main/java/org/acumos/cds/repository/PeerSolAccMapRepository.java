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

package org.acumos.cds.repository;

import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PeerSolAccMapRepository
		extends PagingAndSortingRepository<MLPPeerSolAccMap, MLPPeerSolAccMap.PeerSolAccMapPK> {

	/**
	 * Deletes all entries for the specified peer group ID.
	 * 
	 * @param peerGroupId
	 *            Peer group ID
	 */
	@Modifying
	@Transactional // throws exception without this
	@Query(value = "DELETE FROM MLPPeerSolAccMap m " //
			+ " WHERE m.peerGroupId = :peerGroupId")
	void deleteForPeerGroup(@Param("peerGroupId") Long peerGroupId);

	/**
	 * Deletes all entries for the specified solution group ID.
	 * 
	 * @param solGroupId
	 *            Solution group ID
	 */
	@Modifying
	@Transactional // throws exception without this
	@Query(value = "DELETE FROM MLPPeerSolAccMap m " //
			+ " WHERE m.solutionGroupId = :solGroupId")
	void deleteForSolGroup(@Param("solGroupId") Long solGroupId);

	/**
	 * Checks if the specified peer has access to the specified solution via peer
	 * and solution groups.
	 * 
	 * @param peerId
	 *            Peer ID
	 * @param solutionId
	 *            Solution ID
	 * @return count of paths that grant the access
	 */
	@Query("SELECT count(pg.groupId) FROM MLPPeerGrpMemMap pg, MLPPeerSolAccMap ps, MLPSolGrpMemMap sg " //
			+ " WHERE pg.peerId = :peerId " //
			+ " AND pg.groupId = ps.peerGroupId " //
			+ " AND ps.granted = true " //
			+ " AND ps.solutionGroupId = sg.groupId " //
			+ " AND sg.solutionId = :solutionId")
	Long checkPeerSolutionAccess(@Param("peerId") String peerId, @Param("solutionId") String solutionId);

}
