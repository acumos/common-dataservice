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

package org.acumos.cds.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.DeploymentStatusCode;
import org.acumos.cds.LoginProviderCode;
import org.acumos.cds.ModelTypeCode;
import org.acumos.cds.ToolkitTypeCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.ValidationTypeCode;
import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifactType;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDeploymentStatus;
import org.acumos.cds.domain.MLPLoginProvider;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDeployment;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionValidation;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPValidationSequence;
import org.acumos.cds.domain.MLPValidationStatus;
import org.acumos.cds.domain.MLPValidationType;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.cds.transport.SuccessTransport;
import org.springframework.web.client.RestTemplate;

public class CommonDataServiceRestClientMockImpl implements ICommonDataServiceRestClient {

	public static ICommonDataServiceRestClient getInstance(String webapiUrl, String user, String pass) {
		return new CommonDataServiceRestClientMockImpl();
	}

	public static ICommonDataServiceRestClient getInstance(String webapiUrl, RestTemplate restTemplate) {
		return new CommonDataServiceRestClientMockImpl();
	}

	private CommonDataServiceRestClientMockImpl() {

	}

	@Override
	public SuccessTransport getHealth() {
		return new SuccessTransport(200, "mock health");
	}

	@Override
	public SuccessTransport getVersion() {
		return new SuccessTransport(200, "mock version");
	}

	@Override
	public List<MLPAccessType> getAccessTypes() {
		List<MLPAccessType> list = new ArrayList<>();
		for (AccessTypeCode a : AccessTypeCode.values()) {
			MLPAccessType b = new MLPAccessType();
			b.setTypeCode(a.name());
			b.setTypeName(a.getTypeName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPArtifactType> getArtifactTypes() {
		List<MLPArtifactType> list = new ArrayList<>();
		for (AccessTypeCode a : AccessTypeCode.values()) {
			MLPArtifactType b = new MLPArtifactType();
			b.setTypeCode(a.name());
			b.setTypeName(a.getTypeName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPLoginProvider> getLoginProviders() {
		List<MLPLoginProvider> list = new ArrayList<>();
		for (LoginProviderCode a : LoginProviderCode.values()) {
			MLPLoginProvider b = new MLPLoginProvider();
			b.setProviderCode(a.name());
			b.setProviderName(a.getProviderName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPModelType> getModelTypes() {
		List<MLPModelType> list = new ArrayList<>();
		for (ModelTypeCode a : ModelTypeCode.values()) {
			MLPModelType b = new MLPModelType();
			b.setTypeCode(a.name());
			b.setTypeName(a.getTypeName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPToolkitType> getToolkitTypes() {
		List<MLPToolkitType> list = new ArrayList<>();
		for (ToolkitTypeCode a : ToolkitTypeCode.values()) {
			MLPToolkitType b = new MLPToolkitType();
			b.setToolkitCode(a.name());
			b.setToolkitName(a.getTypeName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPValidationStatus> getValidationStatuses() {
		List<MLPValidationStatus> list = new ArrayList<>();
		for (ValidationStatusCode a : ValidationStatusCode.values()) {
			MLPValidationStatus b = new MLPValidationStatus();
			b.setStatusCode(a.name());
			b.setStatusName(a.getStatusName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPValidationType> getValidationTypes() {
		List<MLPValidationType> list = new ArrayList<>();
		for (ValidationTypeCode a : ValidationTypeCode.values()) {
			MLPValidationType b = new MLPValidationType();
			b.setTypeCode(a.name());
			b.setTypeName(a.getTypeName());
			list.add(b);
		}
		return list;
	}

	@Override
	public List<MLPDeploymentStatus> getDeploymentStatuses() {
		List<MLPDeploymentStatus> list = new ArrayList<>();
		for (DeploymentStatusCode a : DeploymentStatusCode.values()) {
			MLPDeploymentStatus b = new MLPDeploymentStatus();
			b.setStatusCode(a.name());
			b.setStatusName(a.getStatusName());
			list.add(b);
		}
		return list;
	}

	@Override
	public long getSolutionCount() {
		return 0L;
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutions(RestPageRequest pageRequest) {
		return null;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		return null;
	}

	@Override
	public RestPageResponse<MLPSolution> findSolutionsByTag(String tag, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPSolution getSolution(String solutionId) {

		return null;
	}

	@Override
	public MLPSolution createSolution(MLPSolution solution) {

		return null;
	}

	@Override
	public void updateSolution(MLPSolution solution) {

	}

	@Override
	public void incrementSolutionViewCount(String solutionId) {

	}

	@Override
	public void deleteSolution(String solutionId) {

	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String solutionId) {

		return null;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisions(String[] solutionIds) {
		return null;
	}

	@Override
	public MLPSolutionRevision getSolutionRevision(String solutionId, String revisionId) {
		return null;
	}

	@Override
	public List<MLPSolutionRevision> getSolutionRevisionsForArtifact(String artifactId) {

		return null;
	}

	@Override
	public MLPSolutionRevision createSolutionRevision(MLPSolutionRevision revision) {

		return null;
	}

	@Override
	public void updateSolutionRevision(MLPSolutionRevision revision) {

	}

	@Override
	public void deleteSolutionRevision(String solutionId, String revisionId) {

	}

	@Override
	public List<MLPArtifact> getSolutionRevisionArtifacts(String solutionId, String revisionId) {

		return null;
	}

	@Override
	public void addSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId) {

	}

	@Override
	public void dropSolutionRevisionArtifact(String solutionId, String revisionId, String artifactId) {

	}

	@Override
	public RestPageResponse<MLPTag> getTags(RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPTag createTag(MLPTag tag) {

		return null;
	}

	@Override
	public void deleteTag(MLPTag tag) {

	}

	@Override
	public List<MLPTag> getSolutionTags(String solutionId) {

		return null;
	}

	@Override
	public void addSolutionTag(String solutionId, String tag) {

	}

	@Override
	public void dropSolutionTag(String solutionId, String tag) {

	}

	@Override
	public long getArtifactCount() {

		return 0;
	}

	@Override
	public RestPageResponse<MLPArtifact> getArtifacts(RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public RestPageResponse<MLPArtifact> findArtifactsBySearchTerm(String searchTerm, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public List<MLPArtifact> searchArtifacts(Map<String, Object> queryParameters, boolean isOr) {

		return null;
	}

	@Override
	public MLPArtifact getArtifact(String artifactId) {

		return null;
	}

	@Override
	public MLPArtifact createArtifact(MLPArtifact artifact) {

		return null;
	}

	@Override
	public void updateArtifact(MLPArtifact artifact) {

	}

	@Override
	public void deleteArtifact(String artifactId) {

	}

	@Override
	public long getUserCount() {

		return 0;
	}

	@Override
	public RestPageResponse<MLPUser> getUsers(RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public RestPageResponse<MLPUser> findUsersBySearchTerm(String searchTerm, RestPageRequest pageRequest) {
		RestPageResponse<MLPUser> response = new RestPageResponse<>();
		return response;
	}

	@Override
	public List<MLPUser> searchUsers(Map<String, Object> queryParameters, boolean isOr) {

		List<MLPUser> mlpUserList = new ArrayList<MLPUser>();
		return mlpUserList;
	}

	@Override
	public MLPUser loginUser(String name, String pass) {

		return null;
	}

	@Override
	public MLPUser getUser(String userId) {

		return null;
	}

	@Override
	public MLPUser createUser(MLPUser user) {
		/*
		 * System.out.println("################In Mock Data Service####################"
		 * ); MLPUser mlpUser = new MLPUser();
		 * mlpUser.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		 * mlpUser.setActive(true); mlpUser.setFirstName("UserFirstName");
		 * mlpUser.setLastName("UserLastName"); mlpUser.setLoginName("User1");
		 * mlpUser.setEmail("user1@emial.com"); mlpUser.setLoginHash("User1"); return
		 * mlpUser;
		 */
		return null;
	}

	@Override
	public void updateUser(MLPUser user) {

	}

	@Override
	public void deleteUser(String userId) {

	}

	@Override
	public List<MLPRole> getUserRoles(String userId) {

		return null;
	}

	@Override
	public void addUserRole(String userId, String roleId) {

	}

	@Override
	public void updateUserRoles(String userId, List<String> roleIds) {

	}

	@Override
	public void dropUserRole(String userId, String roleId) {

	}

	@Override
	public void addUsersInRole(List<String> userIds, String roleId) {

	}

	@Override
	public void dropUsersInRole(List<String> userIds, String roleId) {

	}

	@Override
	public long getRoleUsersCount(String roleId) {

		return 0;
	}

	@Override
	public MLPUserLoginProvider getUserLoginProvider(String userId, String providerCode, String providerLogin) {

		return null;
	}

	@Override
	public List<MLPUserLoginProvider> getUserLoginProviders(String userId) {

		return null;
	}

	@Override
	public MLPUserLoginProvider createUserLoginProvider(MLPUserLoginProvider provider) {

		return null;
	}

	@Override
	public void updateUserLoginProvider(MLPUserLoginProvider provider) {

	}

	@Override
	public void deleteUserLoginProvider(MLPUserLoginProvider provider) {

	}

	@Override
	public long getRoleCount() {

		return 0;
	}

	@Override
	public List<MLPRole> searchRoles(Map<String, Object> queryParameters, boolean isOr) {
		List<MLPRole> mlpRoleList = new ArrayList<MLPRole>();
		return mlpRoleList;
	}

	@Override
	public RestPageResponse<MLPRole> getRoles(RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPRole getRole(String roleId) {

		return null;
	}

	@Override
	public MLPRole createRole(MLPRole role) {
		MLPRole mlpRole = new MLPRole();
		return mlpRole;
	}

	@Override
	public void updateRole(MLPRole role) {

	}

	@Override
	public void deleteRole(String roleId) {

	}

	@Override
	public List<MLPRoleFunction> getRoleFunctions(String roleId) {

		return null;
	}

	@Override
	public MLPRoleFunction getRoleFunction(String roleId, String roleFunctionId) {

		return null;
	}

	@Override
	public MLPRoleFunction createRoleFunction(MLPRoleFunction roleFunction) {

		return null;
	}

	@Override
	public void updateRoleFunction(MLPRoleFunction roleFunction) {

	}

	@Override
	public void deleteRoleFunction(String roleId, String roleFunctionId) {

	}

	@Override
	public RestPageResponse<MLPPeer> getPeers(RestPageRequest pageRequest) {
		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setActive(true);
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		mlpPeer.setContact2("Contact2");
		Date created = new Date();
		mlpPeer.setCreated(created);
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		RestPageResponse<MLPPeer> responseBody = new RestPageResponse<>();
		RestPageRequest restPageReq = new RestPageRequest();
		restPageReq.setPage(0);
		restPageReq.setSize(2);
		List<MLPPeer> peerList = new ArrayList<>();
		if (restPageReq.getPage() != null && restPageReq.getSize() != null) {
			peerList.add(mlpPeer);
		}
		return responseBody;
	}

	@Override
	public List<MLPPeer> searchPeers(Map<String, Object> queryParameters, boolean isOr) {

		return null;
	}

	@Override
	public MLPPeer getPeer(String peerId) {
		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setActive(true);
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		mlpPeer.setContact2("Contact2");
		Date created = new Date();
		mlpPeer.setCreated(created);
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId("62e46a5a-2c26-4dee-b320-b4e48303d24d");
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		return mlpPeer;
	}

	@Override
	public MLPPeer createPeer(MLPPeer peer) {
		MLPPeer mlpPeer = new MLPPeer();
		mlpPeer.setActive(true);
		mlpPeer.setApiUrl("http://peer-api");
		mlpPeer.setContact1("Contact1");
		mlpPeer.setContact2("Contact2");
		Date created = new Date();
		mlpPeer.setCreated(created);
		mlpPeer.setDescription("Peer description");
		mlpPeer.setName("Peer-1509357629935");
		mlpPeer.setPeerId("c17c0562-c6df-4a0c-9702-ba8175eb23fd");
		mlpPeer.setSelf(false);
		mlpPeer.setSubjectName("peer Subject name");
		mlpPeer.setWebUrl("https://web-url");
		return mlpPeer;
	}

	@Override
	public void updatePeer(MLPPeer user) {

	}

	@Override
	public void deletePeer(String peerId) {

	}

	@Override
	public List<MLPPeerSubscription> getPeerSubscriptions(String peerId) {

		return null;
	}

	@Override
	public MLPPeerSubscription getPeerSubscription(Long subscriptionId) {

		return null;
	}

	@Override
	public MLPPeerSubscription createPeerSubscription(MLPPeerSubscription peerSub) {

		return null;
	}

	@Override
	public void updatePeerSubscription(MLPPeerSubscription peerSub) {

	}

	@Override
	public void deletePeerSubscription(Long subscriptionId) {

	}

	@Override
	public RestPageResponse<MLPSolutionDownload> getSolutionDownloads(String solutionId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPSolutionDownload createSolutionDownload(MLPSolutionDownload download) {

		return null;
	}

	@Override
	public void deleteSolutionDownload(MLPSolutionDownload download) {

	}

	@Override
	public RestPageResponse<MLPSolution> getFavoriteSolutions(String userId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPSolutionFavorite createSolutionFavorite(MLPSolutionFavorite fs) {

		return null;
	}

	@Override
	public void deleteSolutionFavorite(MLPSolutionFavorite fs) {

	}

	@Override
	public RestPageResponse<MLPSolutionRating> getSolutionRatings(String solutionId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPSolutionRating createSolutionRating(MLPSolutionRating rating) {

		return null;
	}

	@Override
	public void updateSolutionRating(MLPSolutionRating rating) {

	}

	@Override
	public void deleteSolutionRating(MLPSolutionRating rating) {

	}

	@Override
	public long getNotificationCount() {
		return 1L;
	}

	@Override
	public RestPageResponse<MLPNotification> getNotifications(RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPNotification createNotification(MLPNotification notification) {
		MLPNotification mlpNotification = new MLPNotification();
		Date created = new Date();
		mlpNotification.setCreated(created);
		mlpNotification.setMessage("notification created");
		Date modified = new Date();
		mlpNotification.setModified(modified);
		mlpNotification.setNotificationId("037ad773-3ae2-472b-89d3-9e185a2cbrt");
		mlpNotification.setTitle("Notification");
		mlpNotification.setUrl("http://notify.com");
		mlpNotification.setStart(created);
		Date end = new Date();
		mlpNotification.setEnd(end);
		return mlpNotification;
	}

	@Override
	public void updateNotification(MLPNotification notification) {

	}

	@Override
	public void deleteNotification(String notificationId) {

	}

	@Override
	public RestPageResponse<MLPUserNotification> getUserNotifications(String userId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public void addUserToNotification(String notificationId, String userId) {

	}

	@Override
	public void dropUserFromNotification(String notificationId, String userId) {

	}

	@Override
	public void setUserViewedNotification(String notificationId, String userId) {

	}

	@Override
	public MLPSolutionWeb getSolutionWebMetadata(String solutionId) {

		return null;
	}

	@Override
	public List<MLPUser> getSolutionAccessUsers(String solutionId) {

		return null;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public void addSolutionUserAccess(String solutionId, String userId) {

	}

	@Override
	public void dropSolutionUserAccess(String solutionId, String userId) {

	}

	@Override
	public void updatePassword(MLPUser user, MLPPasswordChangeRequest changeRequest) {

	}

	@Override
	public List<MLPSolutionValidation> getSolutionValidations(String solutionId, String revisionId) {

		return null;
	}

	@Override
	public MLPSolutionValidation createSolutionValidation(MLPSolutionValidation validation) {

		return null;
	}

	@Override
	public void updateSolutionValidation(MLPSolutionValidation validation) {

	}

	@Override
	public void deleteSolutionValidation(MLPSolutionValidation validation) {

	}

	@Override
	public List<MLPValidationSequence> getValidationSequences() {

		return null;
	}

	@Override
	public MLPValidationSequence createValidationSequence(MLPValidationSequence sequence) {

		return null;
	}

	@Override
	public void deleteValidationSequence(MLPValidationSequence sequence) {

	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserDeployments(String userId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getSolutionDeployments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public RestPageResponse<MLPSolutionDeployment> getUserSolutionDeployments(String solutionId, String revisionId,
			String userId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPSolutionDeployment createSolutionDeployment(MLPSolutionDeployment deployment) {

		return null;
	}

	@Override
	public void updateSolutionDeployment(MLPSolutionDeployment deployment) {

	}

	@Override
	public void deleteSolutionDeployment(MLPSolutionDeployment deployment) {

	}

	@Override
	public MLPSiteConfig getSiteConfig(String configKey) {

		return null;
	}

	@Override
	public MLPSiteConfig createSiteConfig(MLPSiteConfig config) {

		return null;
	}

	@Override
	public void updateSiteConfig(MLPSiteConfig config) {

	}

	@Override
	public void deleteSiteConfig(String configKey) {

	}

	@Override
	public MLPSolutionRating getSolutionRating(String solutionId, String userId) {

		return null;
	}

	@Override
	public long getThreadCount() {

		return 0;
	}

	@Override
	public RestPageResponse<MLPThread> getThreads(RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPThread getThread(String threadId) {

		return null;
	}

	@Override
	public MLPThread createThread(MLPThread thread) {

		return null;
	}

	@Override
	public void updateThread(MLPThread thread) {

	}

	@Override
	public void deleteThread(String threadId) {

	}

	@Override
	public long getThreadCommentCount(String threadId) {

		return 0;
	}

	@Override
	public RestPageResponse<MLPComment> getThreadComments(String threadId, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public MLPComment getComment(String threadId, String commentId) {

		return null;
	}

	@Override
	public MLPComment createComment(MLPComment comment) {

		return null;
	}

	@Override
	public void updateComment(MLPComment comment) {

	}

	@Override
	public void deleteComment(String threadId, String commentId) {

	}

	@Override
	public RestPageResponse<MLPSolution> findPortalSolutions(String[] nameKeywords, String[] descriptionKeywords,
			boolean active, String[] ownerIds, String[] accessTypeCodes, String[] modelTypeCodes,
			String[] validationStatusCodes, String[] tags, RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public List<MLPSolution> searchSolutions(Map<String, Object> queryParameters, boolean isOr) {

		return null;
	}

	@Override
	public RestPageResponse<MLPThread> getSolutionRevisionThreads(String solutionId, String revisionId,
			RestPageRequest pageRequest) {

		return null;
	}

	@Override
	public RestPageResponse<MLPComment> getSolutionRevisionComments(String solutionId, String revisionId,
			RestPageRequest pageRequest) {

		return null;
	}

}
