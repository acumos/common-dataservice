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

package org.acumos.cds.test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.DeploymentStatusCode;
import org.acumos.cds.LoginProviderCode;
import org.acumos.cds.ModelTypeCode;
import org.acumos.cds.PeerStatusCode;
import org.acumos.cds.StepStatusCode;
import org.acumos.cds.StepTypeCode;
import org.acumos.cds.SubscriptionScopeTypeCode;
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
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerStatus;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRating.SolutionRatingPK;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.cds.domain.MLPSubscriptionScopeType;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.domain.MLPUserRoleMap;
import org.acumos.cds.domain.MLPValidationStatus;
import org.acumos.cds.domain.MLPValidationType;
import org.acumos.cds.repository.AccessTypeRepository;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.ArtifactTypeRepository;
import org.acumos.cds.repository.CommentRepository;
import org.acumos.cds.repository.DeploymentStatusRepository;
import org.acumos.cds.repository.LoginProviderRepository;
import org.acumos.cds.repository.ModelTypeRepository;
import org.acumos.cds.repository.NotifUserMapRepository;
import org.acumos.cds.repository.NotificationRepository;
import org.acumos.cds.repository.PeerRepository;
import org.acumos.cds.repository.PeerStatusRepository;
import org.acumos.cds.repository.PeerSubscriptionRepository;
import org.acumos.cds.repository.RoleFunctionRepository;
import org.acumos.cds.repository.RoleRepository;
import org.acumos.cds.repository.SiteConfigRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolTagMapRepository;
import org.acumos.cds.repository.SolUserAccMapRepository;
import org.acumos.cds.repository.SolutionDownloadRepository;
import org.acumos.cds.repository.SolutionRatingRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.SolutionWebRepository;
import org.acumos.cds.repository.StepResultRepository;
import org.acumos.cds.repository.StepStatusRepository;
import org.acumos.cds.repository.StepTypeRepository;
import org.acumos.cds.repository.SubscriptionScopeRepository;
import org.acumos.cds.repository.TagRepository;
import org.acumos.cds.repository.ThreadRepository;
import org.acumos.cds.repository.ToolkitTypeRepository;
import org.acumos.cds.repository.UserLoginProviderRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.repository.UserRoleMapRepository;
import org.acumos.cds.repository.ValidationStatusRepository;
import org.acumos.cds.repository.ValidationTypeRepository;
import org.acumos.cds.service.ArtifactSearchService;
import org.acumos.cds.service.PeerSearchService;
import org.acumos.cds.service.RoleSearchService;
import org.acumos.cds.service.SolutionSearchService;
import org.acumos.cds.service.StepResultSearchService;
import org.acumos.cds.service.UserSearchService;
import org.acumos.cds.util.EELFLoggerDelegate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionSystemException;

/**
 * Tests the repository and service classes that provide access to the database.
 * Relies on the provided application.properties with Derby configuration.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CdsRepositoryServiceTest {

	private final static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CdsRepositoryServiceTest.class);

	@Autowired
	private AccessTypeRepository accessTypeRepository;
	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private ArtifactTypeRepository artifactTypeRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private DeploymentStatusRepository deploymentStatusRepository;
	@Autowired
	private LoginProviderRepository loginProviderRepository;
	@Autowired
	private ModelTypeRepository modelTypeRepository;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private NotifUserMapRepository notifUserMapRepository;
	@Autowired
	private PeerRepository peerRepository;
	@Autowired
	private PeerStatusRepository peerStatusRepository;
	@Autowired
	private PeerSubscriptionRepository peerSubscriptionRepository;
	@Autowired
	private RoleFunctionRepository roleFunctionRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private SiteConfigRepository siteConfigRepository;
	@Autowired
	private SolutionDownloadRepository solutionDownloadRepository;
	@Autowired
	private SolutionRatingRepository solutionRatingRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolutionWebRepository solutionWebRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private SolTagMapRepository solTagMapRepository;
	@Autowired
	private SolUserAccMapRepository solUserAccMapRepository;
	@Autowired
	private StepStatusRepository stepStatusRepository;
	@Autowired
	private StepTypeRepository stepTypeRepository;
	@Autowired
	private SubscriptionScopeRepository subscriptionScopeRepository;
	@Autowired
	private TagRepository solutionTagRepository;
	@Autowired
	private ThreadRepository threadRepository;
	@Autowired
	private ToolkitTypeRepository toolkitTypeRepository;
	@Autowired
	private UserLoginProviderRepository userLoginProviderRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleMapRepository userRoleMapRepository;
	@Autowired
	private ArtifactSearchService artifactSearchService;
	@Autowired
	private PeerSearchService peerSearchService;
	@Autowired
	private RoleSearchService roleSearchService;
	@Autowired
	private SolutionSearchService solutionSearchService;
	@Autowired
	private UserSearchService userService;
	@Autowired
	private StepResultRepository stepResultRepository;
	@Autowired
	private StepResultSearchService stepResultSearchService;
	@Autowired
	private ValidationStatusRepository validationStatusRepository;
	@Autowired
	private ValidationTypeRepository validationTypeRepository;

	@Test
	public void testEnums() throws Exception {

		int count;

		Iterable<MLPAccessType> accessTypeList = accessTypeRepository.findAll();
		logger.info("Access types {}", accessTypeList);
		Assert.assertTrue(accessTypeList.iterator().hasNext());
		Iterator<MLPAccessType> atIter = accessTypeList.iterator();
		count = 0;
		while (atIter.hasNext()) {
			++count;
			MLPAccessType act = atIter.next();
			AccessTypeCode.valueOf(act.getTypeCode());
		}
		Assert.assertEquals(count, AccessTypeCode.values().length);

		Iterable<MLPArtifactType> artifactTypeList = artifactTypeRepository.findAll();
		Assert.assertTrue(artifactTypeList.iterator().hasNext());
		logger.info("Artifact types {}", accessTypeList);
		Iterator<MLPArtifactType> artIter = artifactTypeList.iterator();
		count = 0;
		while (artIter.hasNext()) {
			++count;
			MLPArtifactType art = artIter.next();
			ArtifactTypeCode.valueOf(art.getTypeCode());
		}
		Assert.assertEquals(count, ArtifactTypeCode.values().length);

		Iterable<MLPDeploymentStatus> deploymentStatusList = deploymentStatusRepository.findAll();
		Assert.assertTrue(deploymentStatusList.iterator().hasNext());
		logger.info("Deployment statuses {}", deploymentStatusList);
		Iterator<MLPDeploymentStatus> dsIter = deploymentStatusList.iterator();
		count = 0;
		while (dsIter.hasNext()) {
			++count;
			MLPDeploymentStatus ds = dsIter.next();
			DeploymentStatusCode.valueOf(ds.getStatusCode());
		}
		Assert.assertEquals(count, DeploymentStatusCode.values().length);

		Iterable<MLPLoginProvider> loginProviderList = loginProviderRepository.findAll();
		Assert.assertTrue(loginProviderList.iterator().hasNext());
		logger.info("Login providers {}", loginProviderList);
		Iterator<MLPLoginProvider> lpIter = loginProviderList.iterator();
		count = 0;
		while (lpIter.hasNext()) {
			++count;
			MLPLoginProvider lp = lpIter.next();
			LoginProviderCode.valueOf(lp.getProviderCode());
		}
		Assert.assertEquals(count, LoginProviderCode.values().length);

		Iterable<MLPModelType> modelTypeList = modelTypeRepository.findAll();
		Assert.assertTrue(modelTypeList.iterator().hasNext());
		logger.info("Model types {}", modelTypeList);
		Iterator<MLPModelType> mtIter = modelTypeList.iterator();
		count = 0;
		while (mtIter.hasNext()) {
			++count;
			MLPModelType lp = mtIter.next();
			ModelTypeCode.valueOf(lp.getTypeCode());
		}
		Assert.assertEquals(count, ModelTypeCode.values().length);

		Iterable<MLPPeerStatus> peerStatusList = peerStatusRepository.findAll();
		Assert.assertTrue(peerStatusList.iterator().hasNext());
		logger.info("Peer statuses {}", peerStatusList);
		Iterator<MLPPeerStatus> psIter = peerStatusList.iterator();
		count = 0;
		while (psIter.hasNext()) {
			++count;
			MLPPeerStatus ps = psIter.next();
			PeerStatusCode.valueOf(ps.getStatusCode());
		}
		Assert.assertEquals(count, PeerStatusCode.values().length);

		Iterable<MLPStepStatus> stepStatusList = stepStatusRepository.findAll();
		Assert.assertTrue(stepStatusList.iterator().hasNext());
		logger.info("Step statuses {}", stepStatusList);
		Iterator<MLPStepStatus> ssIter = stepStatusList.iterator();
		count = 0;
		while (ssIter.hasNext()) {
			++count;
			MLPStepStatus ss = ssIter.next();
			StepStatusCode.valueOf(ss.getStatusCode());
		}
		Assert.assertEquals(count, StepStatusCode.values().length);

		Iterable<MLPStepType> stepTypeList = stepTypeRepository.findAll();
		Assert.assertTrue(stepTypeList.iterator().hasNext());
		logger.info("Step types {}", stepTypeList);
		Iterator<MLPStepType> stIter = stepTypeList.iterator();
		count = 0;
		while (stIter.hasNext()) {
			++count;
			MLPStepType st = stIter.next();
			StepTypeCode.valueOf(st.getTypeCode());
		}
		Assert.assertEquals(count, StepTypeCode.values().length);

		Iterable<MLPSubscriptionScopeType> sstList = subscriptionScopeRepository.findAll();
		Assert.assertTrue(sstList.iterator().hasNext());
		logger.info("SubscriptionScope type list {}", sstList);
		Iterator<MLPSubscriptionScopeType> sstIter = sstList.iterator();
		count = 0;
		while (sstIter.hasNext()) {
			++count;
			MLPSubscriptionScopeType sst = sstIter.next();
			SubscriptionScopeTypeCode.valueOf(sst.getTypeCode());
		}
		Assert.assertEquals(count, SubscriptionScopeTypeCode.values().length);

		Iterable<MLPToolkitType> toolkitTypeList = toolkitTypeRepository.findAll();
		Assert.assertTrue(toolkitTypeList.iterator().hasNext());
		logger.info("Toolkit type list {}", toolkitTypeList);
		Iterator<MLPToolkitType> ttIter = toolkitTypeList.iterator();
		count = 0;
		while (ttIter.hasNext()) {
			++count;
			MLPToolkitType tt = ttIter.next();
			ToolkitTypeCode.valueOf(tt.getTypeCode());
		}
		Assert.assertEquals(count, ToolkitTypeCode.values().length);

		Iterable<MLPValidationStatus> validStatusList = validationStatusRepository.findAll();
		Assert.assertTrue(validStatusList.iterator().hasNext());
		logger.info("Validation status code list {}", validStatusList);
		Iterator<MLPValidationStatus> vsIter = validStatusList.iterator();
		count = 0;
		while (vsIter.hasNext()) {
			++count;
			MLPValidationStatus vs = vsIter.next();
			ValidationStatusCode.valueOf(vs.getStatusCode());
		}
		Assert.assertEquals(count, ValidationStatusCode.values().length);

		Iterable<MLPValidationType> validTypeList = validationTypeRepository.findAll();
		Assert.assertTrue(validTypeList.iterator().hasNext());
		logger.info("Validation type list {}", validTypeList);
		Iterator<MLPValidationType> vtIter = validTypeList.iterator();
		count = 0;
		while (vtIter.hasNext()) {
			++count;
			MLPValidationType vt = vtIter.next();
			ValidationTypeCode.valueOf(vt.getTypeCode());
		}
		Assert.assertEquals(count, ValidationTypeCode.values().length);

	}

	@Test
	public void testingRepositories() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			final String firstName = "First_" + Long.toString(new Date().getTime());
			final String lastName = "TestLast";
			final String loginName = "test_user3";
			final String loginPass = "test_pass3";
			cu.setFirstName(firstName);
			cu.setLastName(lastName);
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setAuthToken("JWT is Greek to me");
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			logger.info("Created user " + cu.getUserId());

			// Fetch it back
			HashMap<String, Object> restr = new HashMap<>();
			restr.put("firstName", firstName);
			restr.put("lastName", lastName);
			Page<MLPUser> userPage = userService.findUsers(restr, true, new PageRequest(0,5,null));
			Assert.assertTrue(userPage.getNumberOfElements() > 0);
			MLPUser testUser = userPage.iterator().next();
			logger.info("testUser is " + testUser);
			logger.info("cu.getUserID is " + cu.getUserId());

			MLPNotification notif = null;
			notif = new MLPNotification();
			notif.setTitle("Notification title");
			notif.setMessage("Notification message");
			notif.setUrl("http://www.yahoo.com");
			notif.setStart(new Date());
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // Now use today date.
			c.add(Calendar.DATE, 5); // Adding 5 days
			notif.setEnd(c.getTime());
			notif = notificationRepository.save(notif);
			Assert.assertNotNull(notif.getNotificationId());
			Assert.assertNotNull(notif.getCreated());
			logger.info("\t\tNotification: " + notif);

			// put it in the map NotifUserMapRepository
			MLPNotifUserMap notifMap = null;
			notifMap = new MLPNotifUserMap();
			notifMap.setNotificationId(notif.getNotificationId());
			notifMap.setUserId(cu.getUserId());
			notifMap = notifUserMapRepository.save(notifMap);
			logger.info("\t\tNotification Map: " + notifMap);

			logger.info("NotificationRepository Info");
			Iterable<MLPUserNotification> nList = notificationRepository.findActiveByUser(cu.getUserId(), null);
			logger.info("User notifications for user {}: {}", cu.getUserId(), nList);

			MLPUserLoginProvider ulp = new MLPUserLoginProvider();
			ulp.setUserId(cu.getUserId());
			ulp.setProviderCode("GH");
			ulp.setProviderUserId("something");
			ulp.setAccessToken("bogus");
			ulp.setRank(0);
			ulp = userLoginProviderRepository.save(ulp);
			Assert.assertNotNull(ulp.getCreated());

			logger.info("UserLoginProviderRepository Info");
			Iterable<MLPUserLoginProvider> ulpList = userLoginProviderRepository.findByUser(cu.getUserId());
			logger.info("User Login provider list {}", ulpList);

			// Create Peer
			MLPPeer pr = new MLPPeer();
			pr.setName("Peer-" + Long.toString(new Date().getTime()));
			pr.setSubjectName("x.509");
			pr.setApiUrl("http://peer-api");
			pr.setContact1("Tyrion Lannister");
			pr.setStatusCode(PeerStatusCode.AC.name());
			pr.setValidationStatusCode(ValidationStatusCode.FA.name());
			pr = peerRepository.save(pr);
			Assert.assertNotNull(pr.getPeerId());
			Assert.assertNotNull(pr.getCreated());

			// Fetch back
			Map<String, String> peerParms = new HashMap<>();
			peerParms.put("name", pr.getName());
			Page<MLPPeer> searchPeers = peerSearchService.findPeers(peerParms, false, new PageRequest(0,5,null));
			Assert.assertTrue(searchPeers.getNumberOfElements() == 1);

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(),
					SubscriptionScopeTypeCode.FL.name(), AccessTypeCode.PB.name());
			ps = peerSubscriptionRepository.save(ps);
			logger.info("Peer subscription {}", ps);

			logger.info("Fetching PeerSubscriptions");
			Iterable<MLPPeerSubscription> peerSubscriptionList = peerSubscriptionRepository.findByPeer(pr.getPeerId());
			Assert.assertTrue(peerSubscriptionList.iterator().hasNext());
			logger.info("Peer subscription list {}", peerSubscriptionList);

			logger.info("Creating test role");
			MLPRole cr2 = new MLPRole();
			cr2.setName("MLP System User4");
			cr2 = roleRepository.save(cr2);
			Assert.assertNotNull(cr2.getRoleId());

			long count = roleRepository.count();
			Assert.assertTrue(count > 0);
			logger.info("Role count: {}", count);

			Map<String, String> roleParms = new HashMap<>();
			roleParms.put("name", cr2.getName());
			Page<MLPRole> searchRoles = roleSearchService.findRoles(roleParms, false, new PageRequest(0,5,null));
			Assert.assertTrue(searchRoles.getNumberOfElements() == 1);

			MLPRoleFunction crf = new MLPRoleFunction();
			final String roleFuncName = "My test role function";
			crf.setName(roleFuncName);
			crf.setRoleId(cr2.getRoleId());
			crf = roleFunctionRepository.save(crf);
			Assert.assertNotNull(crf.getRoleFunctionId());

			logger.info("User Info {}", cu);
			userRoleMapRepository.save(new MLPUserRoleMap(cu.getUserId(), cr2.getRoleId()));

			long usersInRole = userRoleMapRepository.getRoleUsersCount(cr2.getRoleId());
			Assert.assertTrue(usersInRole > 0);
			logger.info("Count of users in role: {}", usersInRole);

			logger.info("Checking role content");
			MLPRole res = roleRepository.findOne(cr2.getRoleId());
			Assert.assertNotNull(res.getRoleId());

			Iterable<MLPRoleFunction> resrf = roleFunctionRepository.findByRole(cr2.getRoleId());
			Assert.assertTrue(resrf.iterator().hasNext());
			MLPRoleFunction roleFuncOne = resrf.iterator().next();
			Assert.assertEquals(roleFuncName, roleFuncOne.getName());

			logger.info("RoleRepository Info");
			final String UserID = cu.getUserId();
			Iterable<MLPRole> roleList = roleRepository.findByUser(UserID);
			logger.info("Role list {}", roleList);

			userRoleMapRepository.delete(new MLPUserRoleMap.UserRoleMapPK(cu.getUserId(), cr2.getRoleId()));

			logger.info("Deleting test role function");
			roleFunctionRepository.delete(roleFuncOne);

			logger.info("Deleting test role");
			roleRepository.delete(cr2.getRoleId());

			logger.info("Creating artifact");
			MLPArtifact ca = new MLPArtifact();
			ca.setVersion("1.0A");
			ca.setName("test artifact name");
			ca.setUri("http://nexus/artifact");
			ca.setArtifactTypeCode(ArtifactTypeCode.DI.name());
			ca.setOwnerId(cu.getUserId());
			ca.setSize(123);
			ca = artifactRepository.save(ca);
			Assert.assertTrue(artifactRepository.count() > 0);

			// Fetch artifact back
			Map<String, String> artParms = new HashMap<>();
			artParms.put("name", ca.getName());
			Page<MLPArtifact> searchArts = artifactSearchService.findArtifacts(artParms, false, new PageRequest(0,5,null));
			Assert.assertTrue(searchArts.getNumberOfElements() > 0);

			// This tag is existing
			MLPTag solTag1 = new MLPTag("soltag1");
			solTag1 = solutionTagRepository.save(solTag1);

			MLPSolution cs = new MLPSolution();
			final String solName = "solution name";
			final String solDesc = "description";
			cs.setName(solName);
			cs.setDescription(solDesc);
			cs.setActive(true);
			cs.setOwnerId(cu.getUserId());
			cs.setProvider("Big Data Org");
			cs.setAccessTypeCode(AccessTypeCode.PB.name());
			cs.setModelTypeCode(ModelTypeCode.CL.name());
			cs.setToolkitTypeCode(ToolkitTypeCode.SK.name());
			cs.setValidationStatusCode(ValidationStatusCode.SB.name());
			// tags must exist; they are not created here
			cs.getTags().add(solTag1);
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			Assert.assertTrue(cs.getTags().size() == 1);
			logger.info("Created solution " + cs.getSolutionId());

			// Search for a single value
			Map<String, String> solParms = new HashMap<>();
			solParms.put("name", cs.getName());
			Page<MLPSolution> searchSols = solutionSearchService.findSolutions(solParms, false, new PageRequest(0, 5, null));
			Assert.assertTrue(searchSols.getNumberOfElements() == 1);

			// Search for a list
			Map<String, Object> solListParms = new HashMap<>();
			solListParms.put("name", new String[] { cs.getName() });
			Page<MLPSolution> searchPageSols = solutionSearchService.findSolutions(solListParms, false, new PageRequest(0,5,null));
			Assert.assertTrue(searchPageSols.getNumberOfElements() == 1);

			logger.info("Finding portal solutions");
			String[] solKw = { solName };
			String[] descKw = { solDesc };
			boolean active = true;
			String[] ownerIds = { cu.getUserId() };
			String[] accessTypeCodes = { null, AccessTypeCode.PB.name() };
			String[] modelTypeCodes = { ModelTypeCode.CL.name() };
			String[] valStatusCodes = { ValidationStatusCode.SB.name() };
			String[] searchTags = { solTag1.getTag() };
			Page<MLPSolution> portalSearchResult = solutionSearchService.findPortalSolutions(solKw, descKw, active,
					ownerIds, accessTypeCodes, modelTypeCodes, valStatusCodes, searchTags,
					new PageRequest(0, 2, Direction.ASC, "name"));
			Assert.assertTrue(portalSearchResult != null && portalSearchResult.getNumberOfElements() > 0);
			logger.info("Found portal solution total " + portalSearchResult.getTotalElements());

			MLPSolutionRevision cr = new MLPSolutionRevision();
			cr.setSolutionId(cs.getSolutionId());
			cr.setVersion("1.0R");
			cr.setDescription("Some description 2");
			cr.setOwnerId(cu.getUserId());
			cr = revisionRepository.save(cr);
			Assert.assertNotNull("Revision ID", cr.getRevisionId());
			logger.info("Created solution revision " + cr.getRevisionId());

			logger.info("Adding artifact to revision");
			solRevArtMapRepository.save(new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId()));

			logger.info("Added" + cr.getRevisionId() + " and " + ca.getArtifactId());

			logger.info("Querying for artifact by partial match");
			Iterable<MLPArtifact> al = artifactRepository.findBySearchTerm("name", new PageRequest(0, 5, null));
			Assert.assertTrue(al != null && al.iterator().hasNext());
			logger.info("Artifact list {}", al);

			logger.info("Querying for solution by id");
			MLPSolution si = solutionRepository.findOne(cs.getSolutionId());
			Assert.assertTrue(si != null);
			logger.info("Found solution: " + si.toString());

			logger.info("Querying for solution by partial match");
			Iterable<MLPSolution> sl = solutionRepository.findBySearchTerm("name", new PageRequest(0, 5, null));
			Assert.assertTrue(sl != null && sl.iterator().hasNext());

			logger.info("Querying for revisions by solution");
			Iterable<MLPSolutionRevision> revs = revisionRepository
					.findBySolution(new String[] { si.getSolutionId(), cs.getSolutionId() });
			Assert.assertTrue(revs.iterator().hasNext());
			for (MLPSolutionRevision r : revs) {
				logger.info("\tRevision: " + r.toString());
				Iterable<MLPArtifact> arts = artifactRepository.findByRevision(r.getRevisionId());
				Assert.assertTrue(arts.iterator().hasNext());
				for (MLPArtifact a : arts)
					logger.info("\t\tArtifact: " + a.toString());
			}

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = solutionDownloadRepository.save(sd);
			Assert.assertNotNull(sd.getDownloadId());
			Assert.assertNotNull(sd.getDownloadDate());
			logger.info("Created solution download: " + sd.toString());

			// Fetch the download count
			Long downloadCount = solutionDownloadRepository.getSolutionDownloadCount(cs.getSolutionId());
			Assert.assertNotNull("Solution download count", downloadCount);
			logger.info("Solution download count: " + downloadCount);

			logger.info("Querying for solution downloads for the specified solution ID");
			Iterable<MLPSolutionDownload> soldown = solutionDownloadRepository.findBySolutionId(cs.getSolutionId(),
					new PageRequest(0, 5, null));
			logger.info("solutionDownloadRepository list {}", soldown);

			MLPSolutionRating solrate = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 2);
			solrate.setTextReview("Review text");
			solrate.setCreated(new Date());
			solrate = solutionRatingRepository.save(solrate);
			Assert.assertNotNull(solrate.getSolutionId());
			logger.info("Created Solution Rating " + solrate.getSolutionId() + " Rating is " + solrate.getRating());

			logger.info("Querying for solution rating for the specified solution ID");
			Iterable<MLPSolutionRating> solrating = solutionRatingRepository.findBySolutionId(cs.getSolutionId(),
					new PageRequest(0, 5, null));
			logger.info("SolutionRatingRepository list: {}", solrating);

			logger.info("Creating solution tag");
			MLPTag tag1 = new MLPTag("Java");
			tag1 = solutionTagRepository.save(tag1);
			Iterable<MLPTag> tags = solutionTagRepository.findAll();
			Assert.assertTrue(tags.iterator().hasNext());
			logger.info("First tag fetched back is " + tags.iterator().next());

			MLPSolTagMap solTagMap1 = new MLPSolTagMap(cs.getSolutionId(), tag1.getTag());
			solTagMapRepository.save(solTagMap1);

			MLPSolTagMap soltag = null;
			soltag = new MLPSolTagMap();
			soltag.setSolutionId(cs.getSolutionId());
			soltag.setTag("Java");
			Assert.assertNotNull(soltag.getSolutionId());
			logger.info("Created Solution Tag " + soltag.getSolutionId() + " Tag is " + soltag.getTag());
			soltag = solTagMapRepository.save(soltag);

			Iterable<MLPTag> soltag2 = solutionTagRepository.findBySolution(soltag.getSolutionId());
			logger.info("Solution tag: {}", soltag2);
			logger.info("Solution Tag list above");

			logger.info("Querying for revisions by artifact");
			Iterable<MLPSolutionRevision> revsByArt = revisionRepository.findByArtifact(ca.getArtifactId());
			Assert.assertTrue(revsByArt != null && revsByArt.iterator().hasNext());
			for (MLPSolutionRevision r : revsByArt)
				logger.info("\tRevision for artifact: " + r.toString());

			logger.info("Querying for revisions by search term");
			Iterable<MLPSolutionRevision> rl = revisionRepository.findBySearchTerm("Some", new PageRequest(0, 5, null));
			Assert.assertTrue(rl != null && rl.iterator().hasNext());
			logger.info("Revision list: {}", rl);
			logger.info("revisionRepository list above");

			logger.info("Querying for user by partial match");
			Iterable<MLPUser> sul = userRepository.findBySearchTerm("Test", new PageRequest(0, 5, null));
			Assert.assertTrue(sul != null && sul.iterator().hasNext());
			logger.info("User list: {}", sul);

			Iterable<MLPSolution> solByTag = solutionRepository.findByTag("Java", new PageRequest(0, 5, null));
			logger.info("Solutions by tag: {}", solByTag);
			Assert.assertTrue(solByTag != null && solByTag.iterator().hasNext());

			Date anHourAgo = new java.util.Date();
			anHourAgo.setTime(new Date().getTime() - (1000L * 60 * 60));
			Iterable<MLPSolution> solByDate = solutionRepository.findModifiedAfter(anHourAgo,
					new PageRequest(0, 5, null));
			logger.info("Solutions by date: {}", solByDate);
			Assert.assertTrue(solByDate != null && solByDate.iterator().hasNext());

			MLPUser founduser = userRepository.findByLoginOrEmail("test_user7");
			logger.info("Found user: {}", founduser);

			logger.info("Dropping artifact from revision");
			solRevArtMapRepository.delete(new MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(), ca.getArtifactId()));
			logger.info("Dropped" + cr.getRevisionId() + " and " + ca.getArtifactId());

			MLPSiteConfig cc = new MLPSiteConfig("myKey", " { 'json' : 'block' }");
			cc = siteConfigRepository.save(cc);
			Assert.assertNotNull(cc);
			logger.info("Created site config {}", cc);
			siteConfigRepository.delete(cc);
			
			MLPThread thread = threadRepository.save(new MLPThread(cs.getSolutionId(), cr.getRevisionId()));
			Assert.assertTrue(thread.getThreadId() != null);
			logger.info("Created thread {}", thread);

			Page<MLPThread> threads = threadRepository.findBySolutionIdAndRevisionId(cs.getSolutionId(),
					cr.getRevisionId(), new PageRequest(0, 5, null));
			Assert.assertTrue(threads != null && threads.getNumberOfElements() > 0);

			MLPComment mc = commentRepository.save(new MLPComment(thread.getThreadId(), cu.getUserId(), "c"));
			long crc = commentRepository.count();
			Assert.assertTrue(crc > 0);
			long tcc = commentRepository.countThreadComments(thread.getThreadId());
			Assert.assertTrue(tcc > 0);
			Page<MLPComment> commentList = commentRepository.findByThreadId(thread.getThreadId(),
					new PageRequest(0, 5));
			Assert.assertTrue(commentList != null && commentList.hasContent());
			Page<MLPComment> solRevComments = commentRepository.findBySolutionIdAndRevisionId(cs.getSolutionId(),
					cr.getRevisionId(), new PageRequest(0, 5, null));
			Assert.assertTrue(solRevComments != null && solRevComments.hasContent());

			commentRepository.delete(mc.getCommentId());
			threadRepository.delete(thread.getThreadId());

			if (cleanup) {
				logger.info("Removing newly added entities");
				// Dropping the revision above; don't need delete here
				// solRevArtMapRepository.delete(new
				// MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(),
				// ca.getArtifactId()));
				revisionRepository.delete(cr);
				solutionRatingRepository.delete(solrate);
				MLPSolTagMap.SolTagMapPK solTagMapKey = new MLPSolTagMap.SolTagMapPK(cs.getSolutionId(), tag1.getTag());
				solTagMapRepository.delete(solTagMapKey);
				solutionDownloadRepository.delete(sd);
				solutionRepository.delete(cs.getSolutionId());
				artifactRepository.delete(ca);
				peerSubscriptionRepository.delete(ps);
				peerRepository.delete(pr);
				notifUserMapRepository.delete(notifMap);
				notificationRepository.delete(notif);
				userLoginProviderRepository.delete(ulp);
				userRepository.delete(cu.getUserId());

				if (solutionRepository.findOne(cs.getSolutionId()) != null)
					throw new Exception("Found a deleted solution: " + cs.getSolutionId());
				if (artifactRepository.findOne(ca.getArtifactId()) != null)
					throw new Exception("Found a deleted artifact: " + ca.getArtifactId());
				if (peerRepository.findOne(pr.getPeerId()) != null)
					throw new Exception("Found a deleted peer: " + pr.getPeerId());
				if (peerSubscriptionRepository.findOne(ps.getSubId()) != null)
					throw new Exception("Found a deleted peer sub: " + ps.getSubId());
				if (userRepository.findOne(cu.getUserId()) != null)
					throw new Exception("Found a deleted user: " + cu.getUserId());
			}

		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}
	}

	@Test
	public void testValidationConstraints2() throws Exception {
		MLPUser cu = new MLPUser();
		try {
			userRepository.save(cu);
			throw new Exception("Validation failed to catch null field");
		} catch (Exception ex) {
			ConstraintViolationException cve = findConstraintViolationException(ex);
			if (cve == null)
				logger.info("Unexpected exception: " + ex.toString());
			else
				logger.info("Caught expected exception on create user: " + ex.getMessage());
		}
		try {
			cu.setLoginName("illegal extremely long string value should trigger constraint validation annotation");
			userRepository.save(cu);
			throw new Exception("Validation failed to catch long field value");
		} catch (TransactionSystemException ex) {
			logger.info("Caught expected constraint violation exception: " + ex.getMessage());
		}
	}

	@Test
	public void createSolutionWithArtifacts() throws Exception {
		/** Delete data added in test? */
		final boolean cleanup = true;
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			// Want a unique first name for query below
			final String firstName = "First" + Long.toString(new Date().getTime());
			final String lastName = "TestLast";
			final String loginName = "test_user" + Long.toString(new Date().getTime());
			final String loginPass = "test_pass";
			cu.setFirstName(firstName);
			cu.setLastName(lastName);
			cu.setLoginName(loginName);
			cu.setLoginHash(loginPass);
			cu.setAuthToken("JWT is Greek to me");
			Byte[] bytes = { 0, 1, 2, 3, 4, 5 };
			cu.setPicture(bytes);
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());
			Assert.assertNotNull(cu.getCreated());
			Assert.assertNotNull(cu.getModified());
			logger.info("Created user " + cu.getUserId());

			// Search by partial match
			logger.info("Searching for user by partial match");
			Iterable<MLPUser> userLikeList = userRepository.findBySearchTerm("First", new PageRequest(0, 5));
			Assert.assertTrue(userLikeList.iterator().hasNext());

			// Fetch it back
			logger.info("Searching for user by exact match");
			HashMap<String, Object> restr = new HashMap<>();
			// restr.put("active", true);
			restr.put("firstName", firstName);
			restr.put("lastName", lastName);
			Page<MLPUser> userPage = userService.findUsers(restr, false, new PageRequest(0,5));
			Assert.assertTrue(userPage.getNumberOfElements() == 1);

			// social login
			Iterable<MLPLoginProvider> provs = loginProviderRepository.findAll();
			// Derby has no data preloaded
			Assert.assertTrue(provs != null);// && provs.iterator().hasNext());
			logger.info("Login providers: " + provs);

			MLPUserLoginProvider ulp = new MLPUserLoginProvider();
			ulp.setUserId(cu.getUserId());
			ulp.setProviderCode("GH");
			ulp.setProviderUserId("something");
			ulp.setAccessToken("bogus");
			ulp.setRank(0);
			ulp = userLoginProviderRepository.save(ulp);

			// Create Peer
			final String peerName = "Peer-" + Long.toString(new Date().getTime());
			MLPPeer pr = new MLPPeer(peerName, "x.509", "http://peer-api", true, true, "", PeerStatusCode.AC.name(),
					ValidationStatusCode.IP.name());
			pr = peerRepository.save(pr);
			Assert.assertNotNull(pr.getPeerId());
			Assert.assertNotNull(pr.getCreated());

			MLPPeerSubscription ps = new MLPPeerSubscription(pr.getPeerId(), cu.getUserId(),
					SubscriptionScopeTypeCode.FL.toString(), AccessTypeCode.PB.toString());
			ps = peerSubscriptionRepository.save(ps);
			Assert.assertNotNull(ps.getSubId());

			logger.info("Creating artifact with new ID");
			MLPArtifact ca = new MLPArtifact();
			ca.setVersion("1.0A");
			ca.setName("test artifact name");
			ca.setUri("http://nexus/artifact");
			ca.setArtifactTypeCode(ArtifactTypeCode.DI.name());
			ca.setOwnerId(cu.getUserId());
			ca.setSize(123);
			ca = artifactRepository.save(ca);
			Assert.assertNotNull(ca.getArtifactId());
			Assert.assertNotNull(ca.getCreated());
			Assert.assertTrue(artifactRepository.count() > 0);

			final String artId = "e007ce63-086f-4f33-84c6-cac270874d81";
			MLPArtifact ca2 = new MLPArtifact();
			ca2.setArtifactId(artId);
			ca2.setVersion("2.0A");
			ca2.setName("replicated artifact ");
			ca2.setUri("http://other.foo");
			ca2.setArtifactTypeCode(ArtifactTypeCode.CD.toString());
			ca2.setOwnerId(cu.getUserId());
			ca2.setSize(456);
			ca2 = artifactRepository.save(ca2);
			Assert.assertEquals(artId, ca2.getArtifactId());
			logger.info("Created artifact with preset ID: " + artId);
			artifactRepository.delete(ca2);

			logger.info("Creating solution tags");
			final String tagName1 = "tag1-" + Long.toString(new Date().getTime());
			MLPTag tag1 = new MLPTag(tagName1);
			solutionTagRepository.save(tag1);
			Iterable<MLPTag> tags = solutionTagRepository.findAll();
			Assert.assertTrue(tags.iterator().hasNext());
			logger.info("First tag fetched back is " + tags.iterator().next());

			MLPSolution cs = new MLPSolution();
			cs.setName("solution name");
			cs.setActive(true);
			cs.setOwnerId(cu.getUserId());
			cs.setProvider("Big Data Org");
			cs.setAccessTypeCode(AccessTypeCode.PB.name());
			cs.setModelTypeCode(ModelTypeCode.CL.name());
			cs.setToolkitTypeCode(ToolkitTypeCode.SK.name());
			cs.setValidationStatusCode(ValidationStatusCode.SB.name());
			cs = solutionRepository.save(cs);
			Assert.assertNotNull("Solution ID", cs.getSolutionId());
			Assert.assertNotNull("Solution create time", cs.getCreated());
			logger.info("Created solution " + cs.getSolutionId());

			MLPSolutionWeb stats = new MLPSolutionWeb();
			stats.setSolutionId(cs.getSolutionId());
			stats = solutionWebRepository.save(stats);
			Assert.assertNotNull(stats);
			final Long countBefore = stats.getViewCount();
			logger.info("Solution view count before: " + countBefore);
			solutionWebRepository.incrementViewCount(cs.getSolutionId());
			stats = solutionWebRepository.findOne(stats.getSolutionId());
			final Long countAfter = stats.getViewCount();
			logger.info("Solution view count after: " + countAfter);
			Assert.assertNotEquals(countBefore, countAfter);

			// add tag
			MLPSolTagMap solTagMap1 = new MLPSolTagMap(cs.getSolutionId(), tag1.getTag());
			solTagMapRepository.save(solTagMap1);

			// Get the solution by ID
			MLPSolution solById = solutionRepository.findOne(cs.getSolutionId());
			Assert.assertTrue(solById != null && !solById.getTags().isEmpty());
			logger.info("Fetched solution: " + solById);

			// Query for tags on the solution
			Iterable<MLPTag> solTags = solutionTagRepository.findBySolution(cs.getSolutionId());
			Assert.assertTrue(solTags.iterator().hasNext());
			logger.info("Found tag on solution: " + solTags.iterator().next());
			// Find solution by tag
			Page<MLPSolution> taggedSolutions = solutionRepository.findByTag(tag1.getTag(), null);
			Assert.assertTrue(taggedSolutions.getNumberOfElements() > 0);

			// add user to access control list
			MLPSolUserAccMap solUserAccMap = new MLPSolUserAccMap(cs.getSolutionId(), cu.getUserId());
			solUserAccMapRepository.save(solUserAccMap);
			Iterable<MLPUser> usersWithAccess = solUserAccMapRepository.getUsersForSolution(cs.getSolutionId());
			Assert.assertTrue(usersWithAccess.iterator().hasNext());

			MLPSolution cs2 = new MLPSolution();
			cs2.setName("solution name");
			cs2.setActive(true);
			cs2.setOwnerId(cu.getUserId());
			cs2.setProvider("Big Data Org");
			cs2.setAccessTypeCode(AccessTypeCode.PB.toString());
			cs2.setModelTypeCode(ModelTypeCode.CL.toString());
			cs2.setToolkitTypeCode(ToolkitTypeCode.SK.toString());
			cs2.setValidationStatusCode(ValidationStatusCode.SB.name());
			cs2 = solutionRepository.save(cs2);
			Assert.assertNotNull("Solution 2 ID", cs2.getSolutionId());
			logger.info("Created solution 2 " + cs2.getSolutionId());

			MLPSolutionRevision cr = new MLPSolutionRevision();
			cr.setSolutionId(cs.getSolutionId());
			cr.setVersion("1.0R");
			cr.setDescription("Some description");
			cr.setOwnerId(cu.getUserId());
			cr = revisionRepository.save(cr);
			Assert.assertNotNull("Revision ID", cr.getRevisionId());
			logger.info("Adding artifact to revision");
			solRevArtMapRepository.save(new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId()));

			// Create Solution download
			MLPSolutionDownload sd = new MLPSolutionDownload(cs.getSolutionId(), ca.getArtifactId(), cu.getUserId());
			sd = solutionDownloadRepository.save(sd);
			Assert.assertNotNull(sd.getDownloadId());
			Assert.assertNotNull(sd.getDownloadDate());
			logger.info("Created solution download: " + sd.toString());

			// Fetch the download count
			Long downloadCount = solutionDownloadRepository.getSolutionDownloadCount(cs.getSolutionId());
			Assert.assertNotNull("Solution download count", downloadCount);
			logger.info("Solution download count: " + downloadCount);

			// Create Solution Rating
			MLPSolutionRating ur = new MLPSolutionRating(cs.getSolutionId(), cu.getUserId(), 1);
			ur.setTextReview("Awesome");
			ur = solutionRatingRepository.save(ur);
			Assert.assertNotNull("Solution create time", ur.getCreated());
			logger.info("Created solution rating: " + ur);

			// Fetch average rating
			Double avgRating = solutionRatingRepository.getSolutionRatingAverage(cs.getSolutionId());
			Assert.assertNotNull("Solution rating average", avgRating);
			logger.info("Solution rating average: " + avgRating);

			logger.info("Querying for solution by id");
			MLPSolution si = solutionRepository.findOne(cs.getSolutionId());
			Assert.assertTrue(si != null);
			logger.info("Found solution: " + si.toString());

			logger.info("Querying for solution by partial match");
			Iterable<MLPSolution> sl = solutionRepository.findBySearchTerm("name", new PageRequest(0, 5, null));
			Assert.assertTrue(sl != null && sl.iterator().hasNext());

			logger.info("Querying for revisions by solution");
			Iterable<MLPSolutionRevision> revs = revisionRepository
					.findBySolution(new String[] { si.getSolutionId(), cs2.getSolutionId() });
			Assert.assertTrue(revs.iterator().hasNext());
			for (MLPSolutionRevision r : revs) {
				logger.info("\tRevision: " + r.toString());
				Iterable<MLPArtifact> arts = artifactRepository.findByRevision(r.getRevisionId());
				Assert.assertTrue(arts.iterator().hasNext());
				for (MLPArtifact a : arts)
					logger.info("\t\tArtifact: " + a.toString());
			}

			logger.info("Querying for revisions by artifact");
			Iterable<MLPSolutionRevision> revsByArt = revisionRepository.findByArtifact(ca.getArtifactId());
			Assert.assertTrue(revsByArt != null && revsByArt.iterator().hasNext());
			for (MLPSolutionRevision r : revsByArt)
				logger.info("\tRevision for artifact: " + r.toString());

			if (cleanup) {
				logger.info("Removing newly added entities");
				MLPSolTagMap.SolTagMapPK solTagMapKey = new MLPSolTagMap.SolTagMapPK(cs.getSolutionId(), tag1.getTag());
				solutionWebRepository.delete(cs.getSolutionId());
				solUserAccMapRepository.delete(solUserAccMap);
				solTagMapRepository.delete(solTagMapKey);
				solutionRatingRepository.delete(ur);
				solutionDownloadRepository.delete(sd);
				solRevArtMapRepository
						.delete(new MLPSolRevArtMap.SolRevArtMapPK(cr.getRevisionId(), ca.getArtifactId()));
				revisionRepository.delete(cr);
				solutionRepository.delete(cs.getSolutionId());
				solutionRepository.delete(cs2.getSolutionId());
				artifactRepository.delete(ca);
				peerSubscriptionRepository.delete(ps);
				peerRepository.delete(pr);
				userLoginProviderRepository.delete(ulp);
				userRepository.delete(cu.getUserId());
				solutionTagRepository.delete(tag1);

				if (solutionRepository.findOne(cs.getSolutionId()) != null)
					throw new Exception("Found a deleted solution: " + cs.getSolutionId());
				if (artifactRepository.findOne(ca.getArtifactId()) != null)
					throw new Exception("Found a deleted artifact: " + ca.getArtifactId());
				if (peerRepository.findOne(pr.getPeerId()) != null)
					throw new Exception("Found a deleted peer: " + pr.getPeerId());
				if (peerSubscriptionRepository.findOne(ps.getSubId()) != null)
					throw new Exception("Found a deleted peer sub: " + ps.getSubId());
				if (userRepository.findOne(cu.getUserId()) != null)
					throw new Exception("Found a deleted user: " + cu.getUserId());

				SolutionRatingPK ratingPK = new SolutionRatingPK(ur.getSolutionId(), ur.getUserId());
				if (solutionRatingRepository.findOne(ratingPK) != null)
					throw new Exception("Found a deleted rating: " + ratingPK.toString());

				if (solutionDownloadRepository.findOne(sd.getDownloadId()) != null)
					throw new Exception("Found a deleted download: " + sd.toString());
			}

		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}
	}

	/**
	 * Searches the exception-cause stack for a constraint-violation exceptions.
	 * 
	 * @param t
	 *            Throwable
	 * @return ConstraintViolationException if found; otherwise null.
	 */
	private ConstraintViolationException findConstraintViolationException(Throwable t) {
		while (t != null) {
			if (t instanceof ConstraintViolationException)
				return (ConstraintViolationException) t;
			t = t.getCause();
		}
		return null;
	}

	@Test
	public void testValidationConstraints() throws Exception {
		MLPUser cu = new MLPUser();
		try {
			userRepository.save(cu);
			throw new Exception("Validation failed to catch null field");
		} catch (Exception ex) {
			ConstraintViolationException cve = findConstraintViolationException(ex);
			if (cve == null)
				logger.info("Unexpected exception: " + ex.toString());
			else
				logger.info("Caught expected exception: " + ex.getMessage());
		}
		try {
			cu.setLoginName("illegal extremely long string value should trigger constraint validation annotation");
			userRepository.save(cu);
			throw new Exception("Validation failed to catch long field value");
		} catch (TransactionSystemException ex) {
			logger.info("Caught expected constraint violation exception: " + ex.getMessage());
		}
	}

	@Test
	public void testRoleAndFunctions() throws Exception {
		try {
			MLPUser cu = null;
			cu = new MLPUser();
			cu.setActive(true);
			final String loginName = "test_user_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			logger.info("Creating test role");
			MLPRole cr = new MLPRole();
			cr.setName("My test role");
			cr = roleRepository.save(cr);
			Assert.assertNotNull(cr.getRoleId());

			logger.info("Assigning role to user");
			userRoleMapRepository.save(new MLPUserRoleMap(cu.getUserId(), cr.getRoleId()));

			Iterable<MLPRole> roles = roleRepository.findByUser(cu.getUserId());
			Assert.assertTrue(roles.iterator().hasNext());

			MLPRoleFunction crf = new MLPRoleFunction();
			final String roleFuncName = "My test role function";
			crf.setName(roleFuncName);
			crf.setRoleId(cr.getRoleId());
			roleFunctionRepository.save(crf);
			Assert.assertNotNull(crf.getRoleFunctionId());

			logger.info("Checking role content");
			MLPRole res = roleRepository.findOne(cr.getRoleId());
			Assert.assertNotNull(res.getRoleId());

			Iterable<MLPRoleFunction> resrf = roleFunctionRepository.findByRole(cr.getRoleId());
			Assert.assertTrue(resrf.iterator().hasNext());
			MLPRoleFunction roleFuncOne = resrf.iterator().next();
			Assert.assertEquals(roleFuncName, roleFuncOne.getName());

			logger.info("Removing user from role");
			userRoleMapRepository.delete(new MLPUserRoleMap.UserRoleMapPK(cu.getUserId(), cr.getRoleId()));

			logger.info("Deleting test user");
			userRepository.delete(cu.getUserId());

			logger.info("Deleting test role function");
			roleFunctionRepository.delete(roleFuncOne);

			logger.info("Deleting test role");
			roleRepository.delete(cr.getRoleId());
		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}

	}

	@Test
	public void testNotifications() throws Exception {
		try {
			MLPUser cu = new MLPUser();
			final String loginName = "notif_" + Long.toString(new Date().getTime());
			cu.setLoginName(loginName);
			cu = userRepository.save(cu);
			Assert.assertNotNull(cu.getUserId());

			MLPNotification no = new MLPNotification();
			no.setTitle("notif title");
			no.setMessage("notif msg");
			no.setUrl("http://notify.me");
			Date now = new Date();
			no.setStart(new Date(now.getTime() - 1000));
			no.setEnd(new Date(now.getTime() + 60 * 60 * 1000));
			no = notificationRepository.save(no);
			Assert.assertNotNull(no.getNotificationId());

			MLPNotifUserMap nm = new MLPNotifUserMap();
			nm.setNotificationId(no.getNotificationId());
			nm.setUserId(cu.getUserId());
			nm = notifUserMapRepository.save(nm);

			Iterable<MLPUserNotification> notifs = notificationRepository.findActiveByUser(cu.getUserId(), null);
			Assert.assertTrue(notifs.iterator().hasNext());

			// This next step mimics what a controller will do
			nm.setViewed(new Date());
			notifUserMapRepository.save(nm);

			// Notif has been viewed; item should have viewed-on date
			notifs = notificationRepository.findActiveByUser(cu.getUserId(), null);
			Assert.assertNotNull(notifs.iterator().next().getViewed());

			notifUserMapRepository.delete(nm);
			notificationRepository.delete(no);
			userRepository.delete(cu);
		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}

	}

	@Test
	public void testStepResults() throws Exception {
		try {
			MLPStepResult sr = new MLPStepResult();
			sr.setStepCode(String.valueOf(StepTypeCode.OB));
			sr.setName("Solution ID creation");
			sr.setStatusCode(StepStatusCode.FA.name());
			Date now = new Date();
			sr.setStartDate(new Date(now.getTime() - 60 * 1000));
			sr = stepResultRepository.save(sr);
			Assert.assertNotNull(sr.getStepResultId());

			long srCountTrans = stepResultRepository.count();
			Assert.assertTrue(srCountTrans > 0);
		
			MLPStepResult result = stepResultRepository.findOne(sr.getStepResultId()); 
			Assert.assertNotNull(result);
			logger.info("First step result {}", result);
			
			HashMap<String,Object> queryParameters = new HashMap<>();
			queryParameters.put("statusCode", StepStatusCode.FA.toString());
			Page<MLPStepResult> page = stepResultSearchService.findStepResults(queryParameters, false, new PageRequest(0, 5, null));
			Assert.assertTrue(page.getNumberOfElements() > 0);

			sr.setResult("New stack trace");
			stepResultRepository.save(sr);

			Assert.assertNotNull(stepResultRepository.findOne(sr.getStepResultId()).getResult());
			stepResultRepository.delete(sr.getStepResultId());
		} catch (Exception ex) {
			logger.error("Failed", ex);
			throw ex;
		}

	}

	@Test
	public void testLogger() {
		logger.init();
		logger.trace(EELFLoggerDelegate.applicationLogger, "A trace message");
		logger.trace(EELFLoggerDelegate.applicationLogger, "A trace {}", "message");
		logger.trace(EELFLoggerDelegate.applicationLogger, "A trace message", new Exception());
		logger.debug(EELFLoggerDelegate.applicationLogger, "A debug message");
		logger.debug(EELFLoggerDelegate.applicationLogger, "A debug {}", "message");
		logger.debug(EELFLoggerDelegate.applicationLogger, "A debug message", new Exception());
		logger.info(EELFLoggerDelegate.applicationLogger, "An info message");
		logger.info(EELFLoggerDelegate.applicationLogger, "An info {}", "message");
		logger.info(EELFLoggerDelegate.applicationLogger, "An info message", new Exception());
		logger.warn(EELFLoggerDelegate.applicationLogger, "A warning message");
		logger.warn(EELFLoggerDelegate.applicationLogger, "A warning {}", "message");
		logger.warn(EELFLoggerDelegate.applicationLogger, "A warning message", new Exception());
		logger.error(EELFLoggerDelegate.applicationLogger, "An error message");
		logger.error(EELFLoggerDelegate.applicationLogger, "An error {}", "message");
		logger.error(EELFLoggerDelegate.applicationLogger, "An error message", new Exception());
	}

}
