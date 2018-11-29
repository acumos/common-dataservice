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

import org.acumos.cds.domain.MLPPeerSubscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface PeerSubscriptionRepository extends PagingAndSortingRepository<MLPPeerSubscription, Long> {

	/**
	 * Gets the count of peer subscriptions.
	 * 
	 * @param peerId
	 *                   Peer ID
	 * @return Count of subscriptions
	 */
	@Query("SELECT COUNT(subId) FROM MLPPeerSubscription WHERE peerId = :peerId")
	long countPeerSubscriptions(@Param("peerId") String peerId);

	/**
	 * Finds all subscriptions for the specified peer.
	 *
	 * Generated by Spring magic.
	 * 
	 * @param peerId
	 *                   Peer ID
	 * @return Iterable of subscription objects for the peer.
	 */
	Iterable<MLPPeerSubscription> findByPeerId(@Param("peerId") String peerId);

}
