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

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.domain.MLPEntity;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerGroup;
import org.acumos.cds.domain.MLPPeerGrpMemMap;
import org.acumos.cds.domain.MLPPeerPeerAccMap;
import org.acumos.cds.domain.MLPPeerSolAccMap;
import org.acumos.cds.domain.MLPSolGrpMemMap;
import org.acumos.cds.domain.MLPSolutionGroup;
import org.acumos.cds.repository.PeerGroupRepository;
import org.acumos.cds.repository.PeerGrpMemMapRepository;
import org.acumos.cds.repository.PeerPeerAccMapRepository;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.PeerSolAccMapRepository;
import org.acumos.cds.repository.SolGrpMemMapRepository;
import org.acumos.cds.repository.SolutionGroupRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.transport.CountTransport;
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
 * Provides methods to manage peer groups and solution groups, peer group and
 * solution group memberships, and also peer group and solution group access.
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
	private PeerGrpMemMapRepository peerGroupMemMapRepository;
	@Autowired
	private SolGrpMemMapRepository solGroupMemMapRepository;
	@Autowired
	private PeerSolAccMapRepository peerSolAccMapRepository;
	@Autowired
	private PeerPeerAccMapRepository peerPeerAccMapRepository;

	/**
	 * @param pageRequest
	 *            Page and sort criteria. Spring sets to page 0 of size 20 if client
	 *            sends nothing.
	 * @return Page of peer groups
	 */
	@ApiOperation(value = "Gets a page of peer groups, optionally sorted.", response = MLPPeerGroup.class, responseContainer = "Page")
	@RequestMapping(value = CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPPeerGroup> getPeerGroups(Pageable pageRequest) {
		Date beginDate = new Date();
		Page<MLPPeerGroup> result = peerGroupRepository.findAll(pageRequest);
		logger.audit(beginDate, "getPeerGroups {}", pageRequest);
		return result;
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
		Date beginDate = new Date();
		try {
			MLPPeerGroup result = peerGroupRepository.save(group);
			logger.audit(beginDate, "createPeerGroup: group {}", group);
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createPeerGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createPeerGroup failed", cve);
		}
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
		Date beginDate = new Date();
		// Get the existing one
		MLPPeerGroup existing = peerGroupRepository.findOne(groupId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			group.setGroupId(groupId);
			// Update the existing row
			peerGroupRepository.save(group);
			Object result = new SuccessTransport(HttpServletResponse.SC_OK, null);
			logger.audit(beginDate, "updatePeerGroup groupId {}", groupId);
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updatePeerGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updatePeerGroup failed", cve);
		}
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
		Date beginDate = new Date();
		try {
			peerGroupRepository.delete(groupId);
			logger.audit(beginDate, "deletePeerGroup groupId {}", groupId);
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
	 * @param pageRequest
	 *            Page and sort criteria. Spring sets to page 0 of size 20 if client
	 *            sends nothing.
	 * @param response
	 *            HttpServletResponse
	 * @return Page of peers
	 */
	@ApiOperation(value = "Gets a page of peer members of the specified peer group, optionally sorted.", response = MLPSolutionGroup.class, responseContainer = "Page")
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getPeersInGroup(@PathVariable("groupId") Long groupId, Pageable pageRequest,
			HttpServletResponse response) {
		Date beginDate = new Date();
		if (peerGroupRepository.findOne(groupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		Object result = peerGroupMemMapRepository.findPeersByGroupId(groupId, pageRequest);
		logger.audit(beginDate, "getPeersInGroup groupId {}", groupId);
		return result;
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
	@ApiOperation(value = "Adds the specified peer as a member of the specified peer group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH + "/{peerId}", method = RequestMethod.POST)
	@ResponseBody
	public Object addPeerToGroup(@PathVariable("groupId") Long groupId, @PathVariable("peerId") String peerId,
			@RequestBody MLPPeerGrpMemMap map, HttpServletResponse response) {
		Date beginDate = new Date();
		if (peerRepository.findOne(peerId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerId, null);
		}
		if (peerGroupRepository.findOne(groupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		// Use path parameters only
		map.setGroupId(groupId);
		map.setPeerId(peerId);
		peerGroupMemMapRepository.save(map);
		logger.audit(beginDate, "addPeerToGroup groupId {} peerId {}", groupId, peerId);
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
	@ApiOperation(value = "Drops the specified peer as a member of the specified peer group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.PEER_PATH + "/{peerId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropPeerFromGroup(@PathVariable("groupId") Long groupId, @PathVariable("peerId") String peerId,
			HttpServletResponse response) {
		Date beginDate = new Date();
		MLPPeerGrpMemMap.PeerGrpMemMapPK pk = new MLPPeerGrpMemMap.PeerGrpMemMapPK(groupId, peerId);
		if (peerGroupMemMapRepository.findOne(pk) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		peerGroupMemMapRepository.delete(pk);
		logger.audit(beginDate, "dropPeerFromGroup groupId {} peerId {}", groupId, peerId);
		return new SuccessTransport(HttpServletResponse.SC_OK, null);
	}

	/**
	 * @param pageRequest
	 *            Page and sort criteria. Spring sets to page 0 of size 20 if client
	 *            sends nothing.
	 * @return Page of peer groups
	 */
	@ApiOperation(value = "Gets a page of solution groups, optionally sorted.", response = MLPSolutionGroup.class, responseContainer = "Page")
	@RequestMapping(value = CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPSolutionGroup> getSolutionGroups(Pageable pageRequest) {
		Date beginDate = new Date();
		Page<MLPSolutionGroup> result = solutionGroupRepository.findAll(pageRequest);
		logger.audit(beginDate, "getSolutionGroups");
		return result;
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
		Date beginDate = new Date();
		try {
			Object result = solutionGroupRepository.save(group);
			logger.audit(beginDate, "createSolutionGroup groupId {}", group.getGroupId());
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "createSolutionGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "createSolutionGroup failed", cve);
		}
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
		Date beginDate = new Date();
		// Get the existing one
		MLPSolutionGroup existing = solutionGroupRepository.findOne(groupId);
		if (existing == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "NO_ENTRY_WITH_ID " + groupId, null);
		}
		try {
			// Use the path-parameter id; don't trust the one in the object
			group.setGroupId(groupId);
			// Update the existing row
			solutionGroupRepository.save(group);
			Object result = new SuccessTransport(HttpServletResponse.SC_OK, null);
			logger.audit(beginDate, "updateSolutionGroup groupId {}", groupId);
			return result;
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "updateSolutionGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "updateSolutionGroup failed", cve);
		}
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
		Date beginDate = new Date();
		logger.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionGroup: ID {}", groupId);
		try {
			solutionGroupRepository.delete(groupId);
			logger.audit(beginDate, "deleteSolutionGroup groupId {}", groupId);
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
	 * @param pageRequest
	 *            Page and sort criteria. Spring sets to page 0 of size 20 if client
	 *            sends nothing.
	 * @param response
	 *            HttpServletResponse
	 * @return Page of solutions
	 */
	@ApiOperation(value = "Gets a page of solution members in the specified solution group, optionally sorted.", response = MLPSolutionGroup.class, responseContainer = "Page")
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getSolutionsInGroup(@PathVariable("groupId") Long groupId, Pageable pageRequest,
			HttpServletResponse response) {
		Date beginDate = new Date();
		if (solutionGroupRepository.findOne(groupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		Object result = solGroupMemMapRepository.findSolutionsByGroupId(groupId, pageRequest);
		logger.audit(beginDate, "getSolutionsInGroup groupId {}", groupId);
		return result;
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
	@ApiOperation(value = "Adds the specified solution as a member of the specified solution group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH + "/{solutionId}", method = RequestMethod.POST)
	@ResponseBody
	public Object addSolutionToGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("solutionId") String solutionId, @RequestBody MLPSolGrpMemMap map,
			HttpServletResponse response) {
		Date beginDate = new Date();
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		if (solutionGroupRepository.findOne(groupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + groupId, null);
		}
		try {
			// Use path parameters only
			map.setGroupId(groupId);
			map.setSolutionId(solutionId);
			solGroupMemMapRepository.save(map);
			logger.audit(beginDate, "addSolutionToGroup groupId {} solutionId {}", groupId, solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "addSolutionToGroup", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "addSolutionToGroup failed", cve);
		}
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
	@ApiOperation(value = "Drops the specified solution as a member of the specified solution group.", response = SuccessTransport.class)
	@RequestMapping(value = "/{groupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object dropSolutionFromGroup(@PathVariable("groupId") Long groupId,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		Date beginDate = new Date();
		try {
			MLPSolGrpMemMap.SolGrpMemMapPK pk = new MLPSolGrpMemMap.SolGrpMemMapPK(groupId, solutionId);
			solGroupMemMapRepository.delete(pk);
			logger.audit(beginDate, "dropSolutionFromGroup groupId {} solutionId {}", groupId, solutionId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "dropSolutionFromGroup", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "dropSolutionFromGroup failed", ex);
		}
	}

	/**
	 * @param pageRequest
	 *            Page and sort criteria. Spring sets to page 0 of size 20 if client
	 *            sends nothing.
	 * @return Page of peer-solution map objects
	 */
	@ApiOperation(value = "Gets a page of peer-solution membership mappings, optionally sorted.", response = MLPPeerSolAccMap.class, responseContainer = "Page")
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/" + CCDSConstants.SOLUTION_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Page<MLPPeerSolAccMap> getPeerSolutionGroupMaps(Pageable pageRequest) {
		Date beginDate = new Date();
		Page<MLPPeerSolAccMap> result = peerSolAccMapRepository.findAll(pageRequest);
		logger.audit(beginDate, "getPeerSolutionGroupMaps request {}", pageRequest);
		return result;
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
	@ApiOperation(value = "Grants access for the specified peer group to the specified solution group.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerGroupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionGroupId}", method = RequestMethod.POST)
	@ResponseBody
	public Object mapPeerSolutionGroups(@PathVariable("peerGroupId") Long peerGroupId,
			@PathVariable("solutionGroupId") Long solutionGroupId, @RequestBody MLPPeerSolAccMap map,
			HttpServletResponse response) {
		Date beginDate = new Date();
		if (peerGroupRepository.findOne(peerGroupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerGroupId, null);
		}
		if (solutionGroupRepository.findOne(solutionGroupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionGroupId, null);
		}
		try {
			// Use path parameters only
			map.setPeerGroupId(peerGroupId);
			map.setSolutionGroupId(solutionGroupId);
			peerSolAccMapRepository.save(map);
			logger.audit(beginDate, "mapPeerSolutionGroups: peerGroupId {} solutionGroupId {}", peerGroupId,
					solutionGroupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "mapPeerSolutionGroups", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "mapPeerSolutionGroups failed", cve);
		}
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
	@ApiOperation(value = "Removes access for the specified peer group to the specified solution group.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerGroupId}/" + CCDSConstants.SOLUTION_PATH
			+ "/{solutionGroupId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object unmapPeerSolutionGroups(@PathVariable("peerGroupId") Long peerGroupId,
			@PathVariable("solutionGroupId") Long solutionGroupId, HttpServletResponse response) {
		Date beginDate = new Date();
		try {
			MLPPeerSolAccMap.PeerSolAccMapPK pk = new MLPPeerSolAccMap.PeerSolAccMapPK(peerGroupId, solutionGroupId);
			peerSolAccMapRepository.delete(pk);
			logger.audit(beginDate, "unmapPeerSolutionGroups: peerGroupId {} solutionGroupId {}", peerGroupId,
					solutionGroupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "unmapPeerSolutionGroups", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapPeerSolutionGroups failed", ex);
		}
	}

	/**
	 * 
	 * @param principalGroupId
	 *            Principal peer group ID
	 * @param resourceGroupId
	 *            Resource peer group ID
	 * @param map
	 *            Peer group to peer group mapping object (ignored)
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Grants access for the specified principal peer group to the specified resource peer group.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{principalGroupId}/" + CCDSConstants.PEER_PATH
			+ "/{resourceGroupId}", method = RequestMethod.POST)
	@ResponseBody
	public Object mapPeerPeerGroups(@PathVariable("principalGroupId") Long principalGroupId,
			@PathVariable("resourceGroupId") Long resourceGroupId, @RequestBody MLPPeerPeerAccMap map,
			HttpServletResponse response) {
		Date beginDate = new Date();
		if (peerGroupRepository.findOne(principalGroupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + principalGroupId, null);
		}
		if (peerGroupRepository.findOne(resourceGroupId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + resourceGroupId, null);
		}
		if (principalGroupId == resourceGroupId) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "IDs must be different", null);
		}
		try {
			// Use path parameters only
			map.setPrincipalPeerGroupId(principalGroupId);
			map.setResourcePeerGroupId(resourceGroupId);
			peerPeerAccMapRepository.save(map);
			logger.audit(beginDate, "mapPeerPeerGroups: principal {} resource {}", principalGroupId, resourceGroupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			Exception cve = findConstraintViolationException(ex);
			logger.warn(EELFLoggerDelegate.errorLogger, "mapPeerPeerGroups", cve.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "mapPeerPeerGroups failed", cve);
		}
	}

	/**
	 * 
	 * @param principalGroupId
	 *            Principal peer group ID
	 * @param resourceGroupId
	 *            Resource peer group ID
	 * @param response
	 *            HttpServletResponse
	 * @return Success or error
	 */
	@ApiOperation(value = "Removes access for the specified principal peer group to the specified resource peer group.", response = SuccessTransport.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{principalGroupId}/" + CCDSConstants.PEER_PATH
			+ "/{resourceGroupId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Object unmapPeerPeerGroups(@PathVariable("principalGroupId") Long principalGroupId,
			@PathVariable("resourceGroupId") Long resourceGroupId, HttpServletResponse response) {
		Date beginDate = new Date();
		try {
			MLPPeerPeerAccMap.PeerPeerAccMapPK pk = new MLPPeerPeerAccMap.PeerPeerAccMapPK(principalGroupId,
					resourceGroupId);
			peerPeerAccMapRepository.delete(pk);
			logger.audit(beginDate, "unmapPeerSolutionGroups: principalGroupId {} resourceGroupId {}", principalGroupId,
					resourceGroupId);
			return new SuccessTransport(HttpServletResponse.SC_OK, null);
		} catch (Exception ex) {
			// e.g., EmptyResultDataAccessException is NOT an internal server error
			logger.warn(EELFLoggerDelegate.errorLogger, "unmapPeerPeerGroups", ex.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, "unmapPeerPeerGroups failed", ex);
		}
	}

	/**
	 * 
	 * @param peerId
	 *            Peer ID
	 * @param solutionId
	 *            Solution ID
	 * @param response
	 *            HttpServletResponse
	 * @return Count of access paths; zero if none. Throws an exception if the peer
	 *         or solution IDs are bad.
	 */
	@ApiOperation(value = "Checks access for the specified peer to the specified solution.", response = MLPEntity.class)
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/" + CCDSConstants.SOLUTION_PATH + "/{solutionId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object checkPeerSolutionAccess(@PathVariable("peerId") String peerId,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		Date beginDate = new Date();
		if (peerRepository.findOne(peerId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerId, null);
		}
		if (solutionRepository.findOne(solutionId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + solutionId, null);
		}
		long count = peerSolAccMapRepository.checkPeerSolutionAccess(peerId, solutionId);
		logger.audit(beginDate, "checkPeerSolutionAccess: peerId {} solutionId {}", peerId, solutionId);
		return new CountTransport(count);
	}

	/**
	 * 
	 * @param peerId
	 *            Peer ID
	 * @param response
	 *            HttpServletResponse
	 * @return List of MLPPeer
	 */
	@ApiOperation(value = "Gets peers accessible to the specified peer.", response = MLPPeer.class, responseContainer = "List")
	@RequestMapping(value = CCDSConstants.PEER_PATH + "/{peerId}/"
			+ CCDSConstants.ACCESS_PATH, method = RequestMethod.GET)
	@ResponseBody
	public Object getPeerAccessList(@PathVariable("peerId") String peerId, HttpServletResponse response) {
		Date beginDate = new Date();
		if (peerRepository.findOne(peerId) == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return new ErrorTransport(HttpServletResponse.SC_BAD_REQUEST, NO_ENTRY_WITH_ID + peerId, null);
		}
		Object result = peerPeerAccMapRepository.findAccessPeers(peerId);
		logger.audit(beginDate, "getPeerAccessList: peerId {}", peerId);
		return result;
	}

}
