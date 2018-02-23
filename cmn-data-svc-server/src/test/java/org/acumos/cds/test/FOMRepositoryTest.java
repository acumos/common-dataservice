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

import java.util.Date;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.UserRepository;
import org.acumos.cds.service.SolutionSearchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the domain models that have complex mappings.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FOMRepositoryTest {

	private static Logger logger = LoggerFactory.getLogger(FOMRepositoryTest.class);

	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SolutionSearchService solutionSearchService;

	@Test
	public void testEntities() throws Exception {

		// Create entities

		final String loginName = "user_" + Long.toString(new Date().getTime());
		MLPUser cu = new MLPUser(loginName, true);
		cu = userRepository.save(cu);
		Assert.assertNotNull(cu.getUserId());
		logger.info("Created user " + cu);

		MLPSolution cs = new MLPSolution("sol name", cu.getUserId(), true);
		cs.setAccessTypeCode(AccessTypeCode.PR.name());
		cs.setValidationStatusCode(ValidationStatusCode.NV.name());
		cs = solutionRepository.save(cs);
		Assert.assertNotNull("Solution ID", cs.getSolutionId());

		MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "version", cu.getUserId());
		cr = revisionRepository.save(cr);
		Assert.assertNotNull("Revision ID", cr.getRevisionId());
		logger.info("Created solution revision " + cr.getRevisionId());

		MLPArtifact ca = new MLPArtifact("version", ArtifactTypeCode.BP.name(), "name", "uri", cu.getUserId(), 1);
		ca = artifactRepository.save(ca);
		Assert.assertNotNull(ca.getArtifactId());
		logger.info("Created artifact " + ca);

		MLPSolRevArtMap map = new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId());
		solRevArtMapRepository.save(map);

		// Find by modified date

		String[] accTypes = new String[] { AccessTypeCode.PR.name() };
		String[] valCodes = new String[] { ValidationStatusCode.NV.name() };
		Date modifiedDate = new Date();
		modifiedDate.setTime(modifiedDate.getTime() - 60 * 1000);
		Page<MLPSolution> solsByDate = solutionSearchService.findSolutionsByModifiedDate(true, accTypes, valCodes,
				modifiedDate, new PageRequest(0, 5, null));
		Assert.assertTrue(solsByDate != null && solsByDate.getNumberOfElements() > 0);
		logger.info("Found sols by modified date {}", solsByDate);

		// Clean up

		solRevArtMapRepository.delete(map);
		artifactRepository.delete(ca);
		revisionRepository.delete(cr);
		solutionRepository.delete(cs);
		userRepository.delete(cu);
	}

}
