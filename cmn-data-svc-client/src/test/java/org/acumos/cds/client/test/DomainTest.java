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

package org.acumos.cds.client.test;

import org.acumos.cds.domain.MLPAccessType;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPArtifactType;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPDeploymentStatus;
import org.acumos.cds.domain.MLPLoginProvider;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolTagMap;
import org.acumos.cds.domain.MLPSolUserAccMap;
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
import org.acumos.cds.domain.MLPUserRoleMap;
import org.acumos.cds.domain.MLPValidationSequence;
import org.acumos.cds.domain.MLPValidationStatus;
import org.acumos.cds.domain.MLPValidationType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests getters and setters of domain (model) classes.
 */
public class DomainTest extends AbstractModelTest {

	private static Logger logger = LoggerFactory.getLogger(DomainTest.class);

	@Test
	public void testMLPAccessType() {
		MLPAccessType m = new MLPAccessType();
		m.setAccessCode(s1);
		m.setAccessName(s2);
		Assert.assertEquals(s1, m.getAccessCode());
		Assert.assertEquals(s2, m.getAccessName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPArtifact() {
		MLPArtifact m = new MLPArtifact(s1, s1, s1, s1, s1, i1);
		m = new MLPArtifact();
		m.setArtifactId(s1);
		m.setArtifactTypeCode(s2);
		m.setCreated(d1);
		m.setDescription(s3);
		m.setMetadata(s4);
		m.setModified(d2);
		m.setName(s5);
		m.setOwnerId(s6);
		m.setSize(i1);
		m.setUri(s7);
		m.setVersion(s8);
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getArtifactTypeCode());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s3, m.getDescription());
		Assert.assertEquals(s4, m.getMetadata());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s5, m.getName());
		Assert.assertEquals(s6, m.getOwnerId());
		Assert.assertEquals(i1, m.getSize());
		Assert.assertEquals(s7, m.getUri());
		Assert.assertEquals(s8, m.getVersion());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPArtifactType() {
		MLPArtifactType m = new MLPArtifactType();
		m.setTypeCode(s1);
		m.setTypeName(s2);
		Assert.assertEquals(s1, m.getTypeCode());
		Assert.assertEquals(s2, m.getTypeName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPComment() {
		MLPComment m = new MLPComment();
		m = new MLPComment(s1, s2, s3);
		m.setCommentId(s1);
		m.setParentId(s2);
		m.setText(s3);
		m.setThreadId(s4);
		m.setUserId(s6);
		Assert.assertEquals(s1, m.getCommentId());
		Assert.assertEquals(s2, m.getParentId());
		Assert.assertEquals(s3, m.getText());
		Assert.assertEquals(s4, m.getThreadId());
		Assert.assertEquals(s6, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPDeploymentStatus() {
		MLPDeploymentStatus m = new MLPDeploymentStatus();
		m.setStatusCode(s1);
		m.setStatusName(s2);
		Assert.assertEquals(s1, m.getStatusCode());
		Assert.assertEquals(s2, m.getStatusName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPLoginProvider() {
		MLPLoginProvider m = new MLPLoginProvider();
		m.setProviderCode(s1);
		m.setProviderName(s2);
		Assert.assertEquals(s1, m.getProviderCode());
		Assert.assertEquals(s2, m.getProviderName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPModelType() {
		MLPModelType m = new MLPModelType();
		m.setTypeCode(s1);
		m.setTypeName(s2);
		Assert.assertEquals(s1, m.getTypeCode());
		Assert.assertEquals(s2, m.getTypeName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPNotification() {
		MLPNotification m = new MLPNotification(s1, d1, d1);
		m = new MLPNotification();
		m.setCreated(d1);
		m.setEnd(d2);
		m.setMessage(s1);
		m.setModified(d3);
		m.setNotificationId(s2);
		m.setStart(d4);
		m.setTitle(s3);
		m.setUrl(s4);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getEnd());
		Assert.assertEquals(s1, m.getMessage());
		Assert.assertEquals(d3, m.getModified());
		Assert.assertEquals(s2, m.getNotificationId());
		Assert.assertEquals(d4, m.getStart());
		Assert.assertEquals(s3, m.getTitle());
		Assert.assertEquals(s4, m.getUrl());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPNotifUserMap() {
		MLPNotifUserMap m = new MLPNotifUserMap();
		m = new MLPNotifUserMap(s1, s2);
		m.setNotificationId(s1);
		m.setUserId(s2);
		m.setViewed(d1);
		Assert.assertEquals(s1, m.getNotificationId());
		Assert.assertEquals(s2, m.getUserId());
		Assert.assertEquals(d1, m.getViewed());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPNotifUserMap.NotifUserMapPK pk = new MLPNotifUserMap.NotifUserMapPK();
		pk = new MLPNotifUserMap.NotifUserMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPPasswordChangeRequest() {
		MLPPasswordChangeRequest m = new MLPPasswordChangeRequest(s1, s1);
		m = new MLPPasswordChangeRequest();
		m.setNewLoginPass(s1);
		m.setOldLoginPass(s2);
		Assert.assertEquals(s1, m.getNewLoginPass());
		Assert.assertEquals(s2, m.getOldLoginPass());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPPeer() {
		MLPPeer m = new MLPPeer(s1, s1, s1, s1, b1, b1, s1, s1, i1);
		m = new MLPPeer();
		m.setActive(b1);
		m.setApiUrl(s1);
		m.setContact1(s2);
		m.setContact2(s3);
		m.setCreated(d1);
		m.setDescription(s4);
		m.setModified(d2);
		m.setName(s5);
		m.setPeerId(s6);
		m.setSelf(b2);
		m.setSubjectName(s7);
		m.setWebUrl(s8);
		m.setTrustLevel(i1);
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(s1, m.getApiUrl());
		Assert.assertEquals(s2, m.getContact1());
		Assert.assertEquals(s3, m.getContact2());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s4, m.getDescription());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s5, m.getName());
		Assert.assertEquals(s6, m.getPeerId());
		Assert.assertEquals(b2, m.isSelf());
		Assert.assertEquals(s7, m.getSubjectName());
		Assert.assertEquals(s8, m.getWebUrl());
		Assert.assertEquals(i1,  m.getTrustLevel());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPPeerSubscription() {
		MLPPeerSubscription m = new MLPPeerSubscription(s1);
		m = new MLPPeerSubscription();
		m.setCreated(d1);
		m.setMaxArtifactSize(l1);
		m.setModified(d2);
		m.setOptions(s1);
		m.setPeerId(s2);
		m.setRefreshInterval(l2);
		m.setSelector(s3);
		m.setSubId(l3);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(l1, m.getMaxArtifactSize());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s1, m.getOptions());
		Assert.assertEquals(s2, m.getPeerId());
		Assert.assertEquals(l2, m.getRefreshInterval());
		Assert.assertEquals(s3, m.getSelector());
		Assert.assertEquals(l3, m.getSubId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPRole() {
		MLPRole m = new MLPRole(s1, b1);
		m = new MLPRole();
		m.setCreated(d1);
		m.setModified(d2);
		m.setName(s1);
		m.setRoleId(s2);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRoleId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPRoleFunction() {
		MLPRoleFunction m = new MLPRoleFunction(s1, s1);
		m = new MLPRoleFunction();
		m.setCreated(d1);
		m.setModified(d2);
		m.setName(s1);
		m.setRoleFunctionId(s2);
		m.setRoleId(s3);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s1, m.getName());
		Assert.assertEquals(s2, m.getRoleFunctionId());
		Assert.assertEquals(s3, m.getRoleId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPSiteConfig() {
		MLPSiteConfig m = new MLPSiteConfig(s1, s1);
		m = new MLPSiteConfig();
		m.setConfigKey(s1);
		m.setConfigValue(s2);
		m.setCreated(d1);
		m.setModified(d2);
		m.setUserId(s3);
		Assert.assertEquals(s1, m.getConfigKey());
		Assert.assertEquals(s2, m.getConfigValue());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s3, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPSolRevArtMap() {
		MLPSolRevArtMap m = new MLPSolRevArtMap(s1, s1);
		m = new MLPSolRevArtMap();
		m.setArtifactId(s1);
		m.setRevisionId(s2);
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(s2, m.getRevisionId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPSolRevArtMap.SolRevArtMapPK pk = new MLPSolRevArtMap.SolRevArtMapPK();
		pk = new MLPSolRevArtMap.SolRevArtMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPSolTagMap() {
		MLPSolTagMap m = new MLPSolTagMap(s1, s1);
		m = new MLPSolTagMap();
		m.setSolutionId(s1);
		m.setTag(s2);
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getTag());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPSolTagMap.SolTagMapPK pk = new MLPSolTagMap.SolTagMapPK();
		pk = new MLPSolTagMap.SolTagMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPSolUserAccMap() {
		MLPSolUserAccMap m = new MLPSolUserAccMap(s1, s1);
		m = new MLPSolUserAccMap();
		m.setSolutionId(s1);
		m.setUserId(s2);
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPSolUserAccMap.SolUserAccessMapPK pk = new MLPSolUserAccMap.SolUserAccessMapPK();
		pk = new MLPSolUserAccMap.SolUserAccessMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPSolution() {
		MLPSolution m = new MLPSolution(s1, s1, b1);
		m = new MLPSolution();
		m.setAccessTypeCode(s1);
		m.setActive(b1);
		m.setCreated(d1);
		m.setDescription(s2);
		m.setMetadata(s3);
		m.setModelTypeCode(s4);
		m.setModified(d2);
		m.setName(s5);
		m.setOwnerId(s6);
		m.setProvider(s7);
		m.setSolutionId(s8);
		m.setToolkitTypeCode(s9);
		m.setValidationStatusCode(s10);
		Assert.assertEquals(s1, m.getAccessTypeCode());
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getDescription());
		Assert.assertEquals(s3, m.getMetadata());
		Assert.assertEquals(s4, m.getModelTypeCode());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s5, m.getName());
		Assert.assertEquals(s6, m.getOwnerId());
		Assert.assertEquals(s7, m.getProvider());
		Assert.assertEquals(s8, m.getSolutionId());
		Assert.assertEquals(s9, m.getToolkitTypeCode());
		Assert.assertEquals(s10, m.getValidationStatusCode());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPSolutionDeployment() {
		MLPSolutionDeployment m = new MLPSolutionDeployment(s1, s1, s1, s1);
		m = new MLPSolutionDeployment();
		m.setCreated(d1);
		m.setDeploymentId(s1);
		m.setDeploymentStatusCode(s2);
		m.setDetail(s3);
		m.setModified(d2);
		m.setRevisionId(s4);
		m.setSolutionId(s5);
		m.setTarget(s6);
		m.setUserId(s7);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDeploymentId());
		Assert.assertEquals(s2, m.getDeploymentStatusCode());
		Assert.assertEquals(s3, m.getDetail());
		Assert.assertEquals(s4, m.getRevisionId());
		Assert.assertEquals(s5, m.getSolutionId());
		Assert.assertEquals(s6, m.getTarget());
		Assert.assertEquals(s7, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPSolutionDownload() {
		MLPSolutionDownload m = new MLPSolutionDownload(s1, s2, s3);
		m = new MLPSolutionDownload();
		m.setArtifactId(s1);
		m.setDownloadId(l1);
		m.setSolutionId(s2);
		m.setUserId(s3);
		m.setDownloadDate(d1);
		Assert.assertEquals(s1, m.getArtifactId());
		Assert.assertEquals(l1, m.getDownloadId());
		Assert.assertEquals(s2, m.getSolutionId());
		Assert.assertEquals(s3, m.getUserId());
		Assert.assertEquals(d1, m.getDownloadDate());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPSolutionFavorite() {
		MLPSolutionFavorite m = new MLPSolutionFavorite(s1, s1);
		m = new MLPSolutionFavorite();
		m.setSolutionId(s1);
		m.setUserId(s2);
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPSolutionFavorite.SolutionFavoritePK pk = new MLPSolutionFavorite.SolutionFavoritePK();
		pk = new MLPSolutionFavorite.SolutionFavoritePK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPSolutionRating() {
		MLPSolutionRating m = new MLPSolutionRating(s1, s1, i1);
		m = new MLPSolutionRating();
		m.setCreated(d1);
		m.setRating(i1);
		m.setSolutionId(s1);
		m.setTextReview(s2);
		m.setUserId(s3);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(i1, m.getRating());
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(s2, m.getTextReview());
		Assert.assertEquals(s3, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPSolutionRating.SolutionRatingPK pk = new MLPSolutionRating.SolutionRatingPK();
		pk = new MLPSolutionRating.SolutionRatingPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPSolutionRevision() {
		MLPSolutionRevision m = new MLPSolutionRevision(s1, s1, s1);
		m = new MLPSolutionRevision();
		m.setCreated(d1);
		m.setDescription(s1);
		m.setMetadata(s2);
		m.setModified(d2);
		m.setOwnerId(s3);
		m.setRevisionId(s4);
		m.setSolutionId(s5);
		m.setVersion(s6);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDescription());
		Assert.assertEquals(s2, m.getMetadata());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s3, m.getOwnerId());
		Assert.assertEquals(s4, m.getRevisionId());
		Assert.assertEquals(s5, m.getSolutionId());
		Assert.assertEquals(s6, m.getVersion());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPSolutionValidation() {
		MLPSolutionValidation m = new MLPSolutionValidation(s1, s1, s1, s1);
		m = new MLPSolutionValidation();
		m.setCreated(d1);
		m.setDetail(s1);
		m.setModified(d2);
		m.setRevisionId(s2);
		m.setSolutionId(s3);
		m.setTaskId(s4);
		m.setValidationStatusCode(s5);
		m.setValidationTypeCode(s6);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s1, m.getDetail());
		Assert.assertEquals(d2, m.getModified());
		Assert.assertEquals(s2, m.getRevisionId());
		Assert.assertEquals(s3, m.getSolutionId());
		Assert.assertEquals(s4, m.getTaskId());
		Assert.assertEquals(s5, m.getValidationStatusCode());
		Assert.assertEquals(s6, m.getValidationTypeCode());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPSolutionValidation.SolutionValidationPK pk = new MLPSolutionValidation.SolutionValidationPK();
		pk = new MLPSolutionValidation.SolutionValidationPK(s1, s2, s3);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPSolutionWeb() {
		MLPSolutionWeb m = new MLPSolutionWeb(s1);
		m = new MLPSolutionWeb();
		m.setDownloadCount(l1);
		m.setFeatured(b1);
		m.setLastDownload(d1);
		m.setRatingAverageTenths(l2);
		m.setRatingCount(l3);
		m.setSolutionId(s1);
		m.setViewCount(l4);
		Assert.assertEquals(l1, m.getDownloadCount());
		Assert.assertEquals(b1, m.isFeatured());
		Assert.assertEquals(d1, m.getLastDownload());
		Assert.assertEquals(l2, m.getRatingAverageTenths());
		Assert.assertEquals(l3, m.getRatingCount());
		Assert.assertEquals(s1, m.getSolutionId());
		Assert.assertEquals(l4, m.getViewCount());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPTag() {
		MLPTag m = new MLPTag(s1);
		m = new MLPTag();
		m.setTag(s1);
		Assert.assertEquals(s1, m.getTag());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPThread() {
		MLPThread m = new MLPThread();
		m.setRevisionId(s1);
		m.setSolutionId(s2);
		m.setThreadId(s3);
		m.setTitle(s4);
		Assert.assertEquals(s1, m.getRevisionId());
		Assert.assertEquals(s2, m.getSolutionId());
		Assert.assertEquals(s3, m.getThreadId());
		Assert.assertEquals(s4, m.getTitle());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPToolkitType() {
		MLPToolkitType m = new MLPToolkitType();
		m.setToolkitCode(s1);
		Assert.assertEquals(s1, m.getToolkitCode());
		m.setToolkitName(s2);
		Assert.assertEquals(s2, m.getToolkitName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPUser() {
		MLPUser m = new MLPUser(s1, b1);
		m = new MLPUser();
		m.setActive(b1);
		m.setAuthToken(s1);
		m.setCreated(d1);
		m.setEmail(s2);
		m.setFirstName(s3);
		m.setLastLogin(d2);
		m.setLastName(s4);
		m.setLoginHash(s5);
		m.setLoginName(s6);
		m.setLoginPassExpire(d3);
		m.setMiddleName(s7);
		m.setModified(d4);
		m.setOrgName(s8);
		m.setPicture(by1);
		m.setUserId(s9);
		Assert.assertEquals(b1, m.isActive());
		Assert.assertEquals(s1, m.getAuthToken());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getEmail());
		Assert.assertEquals(s3, m.getFirstName());
		Assert.assertEquals(d2, m.getLastLogin());
		Assert.assertEquals(s4, m.getLastName());
		Assert.assertEquals(s5, m.getLoginHash());
		Assert.assertEquals(s6, m.getLoginName());
		Assert.assertEquals(d3, m.getLoginPassExpire());
		Assert.assertEquals(s7, m.getMiddleName());
		Assert.assertEquals(s8, m.getOrgName());
		Assert.assertArrayEquals(by1, m.getPicture());
		Assert.assertEquals(s9, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPUserLoginProvider() {
		MLPUserLoginProvider m = new MLPUserLoginProvider(s1, s1, s1, s1, i1);
		m = new MLPUserLoginProvider();
		m.setAccessToken(s1);
		m.setCreated(d1);
		m.setDisplayName(s2);
		m.setImageUrl(s3);
		m.setModified(d2);
		m.setProfileUrl(s4);
		m.setProviderCode(s5);
		m.setProviderUserId(s6);
		m.setRank(i1);
		m.setRefreshToken(s7);
		m.setSecret(s8);
		m.setUserId(s9);
		Assert.assertEquals(s1, m.getAccessToken());
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(s2, m.getDisplayName());
		Assert.assertEquals(s3, m.getImageUrl());
		Assert.assertEquals(s4, m.getProfileUrl());
		Assert.assertEquals(s5, m.getProviderCode());
		Assert.assertEquals(s6, m.getProviderUserId());
		Assert.assertEquals(i1, m.getRank());
		Assert.assertEquals(s7, m.getRefreshToken());
		Assert.assertEquals(s8, m.getSecret());
		Assert.assertEquals(s9, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPUserLoginProvider.UserLoginProviderPK pk = new MLPUserLoginProvider.UserLoginProviderPK();
		pk = new MLPUserLoginProvider.UserLoginProviderPK(s1, s2, s3);
		MLPUserLoginProvider.UserLoginProviderPK pk2 = new MLPUserLoginProvider.UserLoginProviderPK(s1, s2, s3);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk2));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPUserNotification() {
		MLPUserNotification m = new MLPUserNotification(s1, s1, s1, s1, d1, d1, d1);
		m = new MLPUserNotification();
		m.setCreated(d1);
		m.setEnd(d2);
		m.setMessage(s1);
		m.setModified(d3);
		m.setNotificationId(s2);
		m.setStart(d4);
		m.setTitle(s3);
		m.setUrl(s4);
		m.setViewed(d5);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(d2, m.getEnd());
		Assert.assertEquals(s1, m.getMessage());
		Assert.assertEquals(d3, m.getModified());
		Assert.assertEquals(s2, m.getNotificationId());
		Assert.assertEquals(d4, m.getStart());
		Assert.assertEquals(s3, m.getTitle());
		Assert.assertEquals(s4, m.getUrl());
		Assert.assertEquals(d5, m.getViewed());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPUserRoleMap() {
		MLPUserRoleMap m = new MLPUserRoleMap(s1, s1);
		m = new MLPUserRoleMap();
		m.setRoleId(s1);
		m.setUserId(s2);
		Assert.assertEquals(s1, m.getRoleId());
		Assert.assertEquals(s2, m.getUserId());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPUserRoleMap.UserRoleMapPK pk = new MLPUserRoleMap.UserRoleMapPK();
		pk = new MLPUserRoleMap.UserRoleMapPK(s1, s2);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPValidationSequence() {
		MLPValidationSequence m = new MLPValidationSequence(i1, s1);
		m = new MLPValidationSequence();
		m.setCreated(d1);
		m.setSequence(i1);
		m.setValTypeCode(s1);
		Assert.assertEquals(d1, m.getCreated());
		Assert.assertEquals(i1, m.getSequence());
		Assert.assertEquals(s1, m.getValTypeCode());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
		MLPValidationSequence.ValidationSequencePK pk = new MLPValidationSequence.ValidationSequencePK();
		pk = new MLPValidationSequence.ValidationSequencePK(i1, s1);
		Assert.assertFalse(pk.equals(null));
		Assert.assertFalse(pk.equals(new Object()));
		Assert.assertTrue(pk.equals(pk));
		Assert.assertFalse(pk.hashCode() == 0);
		logger.info(pk.toString());
	}

	@Test
	public void testMLPValidationStatus() {
		MLPValidationStatus m = new MLPValidationStatus();
		m.setStatusCode(s1);
		m.setStatusName(s2);
		Assert.assertEquals(s1, m.getStatusCode());
		Assert.assertEquals(s2, m.getStatusName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testMLPValidationType() {
		MLPValidationType m = new MLPValidationType();
		m.setTypeCode(s1);
		m.setTypeName(s2);
		Assert.assertEquals(s1, m.getTypeCode());
		Assert.assertEquals(s2, m.getTypeName());
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new Object()));
		Assert.assertTrue(m.equals(m));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

}
