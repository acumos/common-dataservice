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

package org.acumos.cds.controller;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPGrpPeerMap;
import org.acumos.cds.domain.MLPGrpPeerSolMap;
import org.acumos.cds.domain.MLPGrpSolMap;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionGroup;
import org.acumos.cds.repository.GrpPeerMapRepository;
import org.acumos.cds.repository.GrpPeerSolMapRepository;
import org.acumos.cds.repository.GrpSolMapRepository;
import org.acumos.cds.repository.PeerGroupRepository;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.SolutionGroupRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.cds.transport.MLPTransportModel;
import org.acumos.cds.transport.SuccessTransport;
import org.acumos.cds.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

/**
 * Provides methods to manage peer groups and solution groups.
 */
@Controller
@RequestMapping("/" + CCDSConstants.GROUP_PATH)
public class GroupPeerSolutionController extends AbstractController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(GroupPeerSolutionController.class);

	@Autowired
	private PeerRepository peerRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private PeerGroupRepository peerGroupRepository;
	@Autowired
	private SolutionGroupRepository solutionGroupRepository;
	@Autowired
	private GrpPeerMapRepository groupPeerMapRepository;
	@Autowired
	private GrpSolMapRepository groupSolMapRepository;
	@Autowired
	private GrpPeerSolMapRepository groupPeerSolMapRepository;

	// Silence Sonar complaints
	private static final String NO_GROUP_WITH_ID = "No group with ID ";
	private static final String NO_PEER_WITH_ID = "No peer with ID ";
	private static final String NO_SOLUTION_WITH_ID = "No solution with ID ";
	private static final String NO_MAP_WITH_IDS = "No map with IDs ";

	/**
	 * @param pageable
	 *            Sort and page criteria
	 * @return Page of peer groups
	 */
	@ApiOperation(value = "Gets a page of peer groups, optionally sorted.", response = MLPPeerGroup.class, responseContainer = "Page")
	@RequestMapping(value = CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPPeerGroup> getPeerGroups(Pageable pageable) {
		return peerGroupRepository.findAll(pageable);
	}

	/**
	 * @param group
	 *            Peer group
	 * @param response
	 *            HttpServletResponse
	 * @return Tag
	 */
	@ApiOperation(value = "Creates a new peer group.", response = MLPPeerGroup.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object createPeerGroup(@RequestBody MLPPeerGroup group, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "createPeerGroup: group {}", group);
		Object result;
		try {
			result = peerGroupRepository.save(group);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createPeerGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPeerGroup failed", cve);
		}
		return result;
	}

	/**
	 * @param groupId
	 *            Path parameter with the row ID
	 * @param group
	 *            Peer group to be updated
	 * @param response
	 *            HttpServletResponse
	 * @return Transport model with success or failure
	 */
	@ApiOperation(value = "Updates a peer group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.PUT)
	@ResponseBody
	public Object updatePeerGroup(@PathVariable("groupId") Long groupId, @RequestBody MLPPeerGroup group,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "updatePeerGroup: received object {} ", group);
		// Get the existing one
		MLPPeerGroup existing = peerGroupRepository.findOne(groupId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Failed to find object with id " + groupId,
					null);
		}
		MLPTransportModel result = null;
		try {
			// Use the path-parameter id; don't trust the one in the object
			group.setGroupId(groupId);
			// Update the existing row
			peerGroupRepository.save(group);
			result = new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updatePeerGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePeerGroup failed", cve);
		}
		return result;
	}

	/**
	 * @param groupId
	 *            ID of group to delete
	 * @param response
	 *            HttpServletResponse
	 * @return Status message
	 */
	@ApiOperation(value = "Deletes a peer group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deletePeerGroup(@PathVariable("groupId") Long groupId, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "deletePeerGroup: ID {}", groupId);
		try {
			peerGroupRepository.delete(groupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "deletePeerGroup", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deletePeerGroup failed", ex);
		}
	}

	/**
	 * @param groupId
	 *            Peer group ID
	 * @param pageable
	 *            Sort and page criteria
	 * @return Page of peers
	 */
	@ApiOperation(value = "Gets a page of peers in the specified peer group, optionally sorted.", response = MLPSolutionGroup.class, responseContainer = "Page")
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPPeer> getPeersInGroup(@PathVariable("groupId") Long groupId, Pageable pageable) {
		return groupPeerMapRepository.findPeersByGroupId(groupId, pageable);
	}

	/**
	 * @param groupId
	 *            Peer group ID
	 * @param peerId
	 *            Peer ID
	 * @param map
	 *            Peer to peer group mapping object (ignored)
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Adds the specified peer to the specified peer group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH + "/{peerId}", method = RequestMethod.POST)
	@ResponseBody
	public Object addPeerToGroup(@PathVariable("groupId") Long groupId, @PathVariable("peerId") String peerId,
			@RequestBody MLPGrpPeerMap map, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "addPeerToGroup: groupId {} peerId {}", groupId, peerId);
		if (peerRepository.findOne(peerId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_PEER_WITH_ID + peerId, null);
		}
		if (peerGroupRepository.findOne(groupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_GROUP_WITH_ID + groupId, null);
		}
		// Use path parameters only
		map.setGroupId(groupId);
		map.setPeerId(peerId);
		groupPeerMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	/**
	 * 
	 * @param groupId
	 *            Peer group ID
	 * @param peerId
	 *            Peer ID
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Drops the specified peer from the specified peer group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH + "/{peerId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropPeerFromGroup(@PathVariable("groupId") Long groupId, @PathVariable("peerId") String peerId,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "dropPeerFromGroup: groupId {} peerId {}", groupId, peerId);
		MLPGrpPeerMap.GrpPeerMapPK pk = new MLPGrpPeerMap.GrpPeerMapPK(groupId, peerId);
		if (groupPeerMapRepository.findOne(pk) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_MAP_WITH_IDS + groupId, null);
		}
		groupPeerMapRepository.delete(pk);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	/**
	 * @param pageable
	 *            Sort and page criteria
	 * @return Page of peer groups
	 */
	@ApiOperation(value = "Gets a page of solution groups, optionally sorted.", response = MLPSolutionGroup.class, responseContainer = "Page")
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPSolutionGroup> getSolutionGroups(Pageable pageable) {
		return solutionGroupRepository.findAll(pageable);
	}

	/**
	 * @param group
	 *            Solution group
	 * @param response
	 *            HttpServletResponse
	 * @return Tag
	 */
	@ApiOperation(value = "Creates a new solution group.", response = MLPSolutionGroup.class)
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH, method = RequestMethod.POST)
	@ResponseBody
	public Object createSolutionGroup(@RequestBody MLPSolutionGroup group, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "createSolutionGroup: group {}", group);
		Object result;
		try {
			result = solutionGroupRepository.save(group);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createSolutionGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolutionGroup failed", cve);
		}
		return result;
	}

	/**
	 * @param groupId
	 *            Path parameter with the row ID
	 * @param group
	 *            Solution group to be updated
	 * @param response
	 *            HttpServletResponse
	 * @return Transport model with success or failure
	 */
	@ApiOperation(value = "Updates a solution group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.PUT)
	@ResponseBody
	public Object updateSolutionGroup(@PathVariable("groupId") Long groupId, @RequestBody MLPSolutionGroup group,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "updateSolutionGroup: received object {} ", group);
		// Get the existing one
		MLPSolutionGroup existing = solutionGroupRepository.findOne(groupId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "Failed to find object with id " + groupId,
					null);
		}
		MLPTransportModel result = null;
		try {
			// Use the path-parameter id; don't trust the one in the object
			group.setGroupId(groupId);
			// Update the existing row
			solutionGroupRepository.save(group);
			result = new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updateSolutionGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			result = new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionGroup failed", cve);
		}
		return result;
	}

	/**
	 * @param groupId
	 *            ID of group to delete
	 * @param response
	 *            HttpServletResponse
	 * @return Status message
	 */
	@ApiOperation(value = "Deletes a solution group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.DELETE)
	@ResponseBody
	public MLPTransportModel deleteSolutionGroup(@PathVariable("groupId") Long groupId, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionGroup: ID {}", groupId);
		try {
			solutionGroupRepository.delete(groupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "deleteSolutionGroup", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "deleteSolutionGroup failed", ex);
		}
	}

	/**
	 * @param groupId
	 *            Solution group ID
	 * @param pageable
	 *            Sort and page criteria
	 * @return Page of solutions
	 */
	@ApiOperation(value = "Gets a page of solutions in the specified solution group, optionally sorted.", response = MLPSolutionGroup.class, responseContainer = "Page")
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPSolution> getSolutionsInGroup(@PathVariable("groupId") Long groupId, Pageable pageable) {
		return groupSolMapRepository.findSolutionsByGroupId(groupId, pageable);
	}

	/**
	 * @param groupId
	 *            Solution group ID
	 * @param solutionId
	 *            Solution ID
	 * @param map
	 *            Solution to solution group mapping object (ignored)
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Adds the specified solution to the specified solution group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH + "/{solutionId}", method = RequestMethod.POST)
	@ResponseBody
	public Object addSolutionToGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("solutionId") String solutionId, @RequestBody MLPGrpSolMap map,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "addSolutionToGroup: map {}", map);
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_SOLUTION_WITH_ID + solutionId, null);
		}
		if (solutionGroupRepository.findOne(groupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_GROUP_WITH_ID + groupId, null);
		}
		// Use path parameters only
		map.setGroupId(groupId);
		map.setSolutionId(solutionId);
		groupSolMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	/**
	 * 
	 * @param groupId
	 *            Solution group ID
	 * @param solutionId
	 *            Solution ID
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Drops the specified solution from the specified solution group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropSolutionFromGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "dropSolutionFromGroup: groupId {} solutionId {}", groupId,
				solutionId);
		MLPGrpSolMap.GrpSolMapPK pk = new MLPGrpSolMap.GrpSolMapPK(groupId, solutionId);
		if (groupSolMapRepository.findOne(pk) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_MAP_WITH_IDS + groupId, null);
		}
		groupSolMapRepository.delete(pk);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	/**
	 * @param pageable
	 *            Sort and page criteria
	 * @return Page of peer-solution map objects
	 */
	@ApiOperation(value = "Gets a page of peer-solution mappings, optionally sorted.", response = MLPGrpPeerSolMap.class, responseContainer = "Page")
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPGrpPeerSolMap> getPeerSolutionGroupMaps(Pageable pageable) {
		return groupPeerSolMapRepository.findAll(pageable);
	}

	/**
	 * 
	 * @param peerGroupId
	 *            Peer group ID
	 * @param solutionGroupId
	 *            Solution group ID
	 * @param map
	 *            Peer group to solution group mapping object (ignored)
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Maps the specified peer group and solution group together.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerGroupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionGroupId}", method = RequestMethod.POST)
	@ResponseBody
	public Object mapPeerSolutionGroups(@PathVariable("peerGroupId") Long peerGroupId,
			@PathVariable("solutionGroupId") Long solutionGroupId, @RequestBody MLPGrpPeerSolMap map,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "mapPeerSolutionGroups: peerGroupId {} solutionGroupId {}",
				peerGroupId, solutionGroupId);
		if (peerGroupRepository.findOne(peerGroupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_GROUP_WITH_ID + peerGroupId, null);
		}
		if (solutionGroupRepository.findOne(solutionGroupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_GROUP_WITH_ID + solutionGroupId, null);
		}
		// Use path parameters only
		map.setPeerGroupId(peerGroupId);
		map.setSolutionGroupId(solutionGroupId);
		groupPeerSolMapRepository.save(map);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	/**
	 * 
	 * @param peerGroupId
	 *            Peer group ID
	 * @param solutionGroupId
	 *            Solution group ID
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Disassociates the specified peer group and solution group.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerGroupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionGroupId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object unmapPeerSolutionGroups(@PathVariable("peerGroupId") Long peerGroupId,
			@PathVariable("solutionGroupId") Long solutionGroupId, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "unmapPeerSolutionGroups: peerGroupId {} solutionGroupId {}",
				peerGroupId, solutionGroupId);

		MLPGrpPeerSolMap.GrpPeerSolMapPK pk = new MLPGrpPeerSolMap.GrpPeerSolMapPK(peerGroupId, solutionGroupId);
		if (groupPeerSolMapRepository.findOne(pk) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST,
					NO_MAP_WITH_IDS + peerGroupId + "," + solutionGroupId, null);
		}
		groupPeerSolMapRepository.delete(pk);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

}
