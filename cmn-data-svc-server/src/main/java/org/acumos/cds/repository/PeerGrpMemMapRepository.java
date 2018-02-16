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

import org.acumos.cds.domain.MLPPeerGrpMemMap;
import org.acumos.cds.domain.MLPPeer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PeerGrpMemMapRepository extends PagingAndSortingRepository<MLPPeerGrpMemMap, MLPPeerGrpMemMap.PeerGrpMemMapPK> {

	/**
	 * Gets a page of peers in the specified group by joining on the 
	 * peer-group mapping table.
	 * 
	 * @param groupId
	 *            Peer group ID
	 * @param pageable
	 *            Page and sort criteria
	 * @return Page of MLPPeer
	 */
	@Query(value = "select p from MLPPeer p, MLPPeerGrpMemMap m " //
			+ " where p.peerId =  m.peerId " //
			+ " and m.groupId = :groupId")
	Page<MLPPeer> findPeersByGroupId(@Param("groupId") Long groupId, Pageable pageable);

	/**
	 * Deletes all entries for the specified group ID.
	 * 
	 * @param groupId
	 *            Group ID
	 */
	@Modifying
	@Transactional // throws exception without this
	@Query(value = "DELETE FROM MLPPeerGrpMemMap m " //
			+ " WHERE m.groupId = :groupId")
	void deleteForGroup(@Param("groupId") Long groupId);

	/**
	 * Deletes all entries for the specified peer ID.
	 * 
	 * @param peerId
	 *            Peer ID
	 */
	@Modifying
	@Transactional // throws exception without this
	@Query(value = "DELETE FROM MLPPeerGrpMemMap m " //
			+ " WHERE m.peerId = :peerId")
	void deleteForSolution(@Param("peerId") String peerId);

}
