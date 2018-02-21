package org.acumos.cds.test;

import java.util.Date;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolRevArtMap;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFOM;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.repository.ArtifactRepository;
import org.acumos.cds.repository.SolRevArtMapRepository;
import org.acumos.cds.repository.SolutionFOMRepository;
import org.acumos.cds.repository.SolutionRepository;
import org.acumos.cds.repository.SolutionRevisionRepository;
import org.acumos.cds.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests the domain models that have complex mappings.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FOMRepositoryTest {

	private static Logger logger = LoggerFactory.getLogger(FOMRepositoryTest.class);

	// Used to create entities
	@Autowired
	private ArtifactRepository artifactRepository;
	@Autowired
	private SolutionRevisionRepository revisionRepository;
	@Autowired
	private SolutionRepository solutionRepository;
	@Autowired
	private SolRevArtMapRepository solRevArtMapRepository;
	
	// Only for querying
	@Autowired
	private SolutionFOMRepository solutionFomRepository;
	@Autowired
	private UserRepository userRepository;

	@Test
	public void testEntities() throws Exception {

		// Create entities
		
		final String loginName = "user_" + Long.toString(new Date().getTime());
		MLPUser cu = new MLPUser(loginName, true);
		cu = userRepository.save(cu);
		Assert.assertNotNull(cu.getUserId());
		logger.info("Created user " + cu);

		MLPSolution cs = new MLPSolution("sol name", cu.getUserId(), true);
		cs = solutionRepository.save(cs);
		Assert.assertNotNull("Solution ID", cs.getSolutionId());

		MLPSolutionRevision cr = new MLPSolutionRevision(cs.getSolutionId(), "version", cu.getUserId(), AccessTypeCode.PB.name());
		cr = revisionRepository.save(cr);
		Assert.assertNotNull("Revision ID", cr.getRevisionId());
		logger.info("Created solution revision " + cr.getRevisionId());
		
		MLPArtifact ca = new MLPArtifact("version", ArtifactTypeCode.BP.name(), "name", "uri", cu.getUserId(), 1);
		ca = artifactRepository.save(ca);
		Assert.assertNotNull(ca.getArtifactId());
		logger.info("Created artifact " + ca);

		MLPSolRevArtMap map = new MLPSolRevArtMap(cr.getRevisionId(), ca.getArtifactId());
		solRevArtMapRepository.save(map);

		// Search for entities
		
		MLPSolutionFOM csc = solutionFomRepository.findOne(cs.getSolutionId());
		logger.info("Found solution complete " + csc);

		// Clean up
		 
		solRevArtMapRepository.delete(map);
		artifactRepository.delete(ca);
		revisionRepository.delete(cr);
		solutionRepository.delete(cs);
		userRepository.delete(cu);
	}

}
