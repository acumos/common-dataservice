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
package org.acumos.cds.migrate;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.migrate.client.CMSReaderClient;
import org.acumos.cds.migrate.client.CMSWorkspace;
import org.acumos.cds.migrate.domain.CMSNameList;
import org.acumos.cds.migrate.domain.CMSRevisionDescription;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Migrates the following data from CMS to CDS and Nexus:
 * <OL>
 * <LI>Solution image
 * <LI>Revision descriptions, org and public are separate
 * <LI>Revision documents, org and public are separate
 * </OL>
 * Configured by properties file in current directory
 */
public class MigrateCmsToCdsApp {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws Exception {
		MigrateProperties props = new MigrateProperties();

		final String cdsUrl = props.getProperty(MigrateProperties.CDS_URL);
		final String cmsUrl = props.getProperty(MigrateProperties.CMS_URL);
		ICommonDataServiceRestClient cdsClient = CommonDataServiceRestClientImpl.getInstance(cdsUrl,
				props.getProperty(MigrateProperties.CDS_USER), props.getProperty(MigrateProperties.CDS_PASS));
		CMSReaderClient cmsClient = new CMSReaderClient(cmsUrl, props.getProperty(MigrateProperties.CMS_USER),
				props.getProperty(MigrateProperties.CMS_PASS));

		logger.info("Migrate data FROM CMS {}", cmsUrl);
		logger.info("Migrate data TO CDS {}", cdsUrl);

		Long count = cdsClient.getSolutionCount();
		logger.info("Total solution count {}", count);

		final int pageSize = 100;
		for (int page = 0;; ++page) {
			RestPageRequest request = new RestPageRequest(page, pageSize);
			RestPageResponse<MLPSolution> sols = cdsClient.getSolutions(request);
			logger.info("Solution page {] has {} elements", page, sols.getNumberOfElements());

			for (MLPSolution s : sols.getContent()) {
				logger.info("Processing solution ID {} name {}", s.getSolutionId(), s.getName());

				// Item 1: solution image
				CMSNameList solNames = cmsClient.getSolutionImageName(s.getSolutionId());
				if (!solNames.getResponse_body().isEmpty()) {
					String imgName = solNames.getResponse_body().get(0);
					if (s.getPicture() != null) {
						// CDS already has it
						logger.info("Solution image already migrated: {}", imgName);
					} else {
						logger.info("Migrating solution image: {}", imgName);
						byte[] image = cmsClient.getSolutionImage(s.getSolutionId(), imgName);
						// s.setPicture(image);
					}
				}

				List<MLPSolutionRevision> revs = cdsClient.getSolutionRevisions(s.getSolutionId());
				for (MLPSolutionRevision r : revs) {
					logger.info("Processing revision ID {} version {}", r.getRevisionId(), r.getVersion());
					for (CMSWorkspace ws : CMSWorkspace.values()) {
						logger.info("Processing CMS workspace {} -> CDS access type {}", ws.getCmsKey(),
								ws.getCdsKey());

						// Item 2: revision descriptions, 0..1 per workspace value
						MLPRevisionDescription cdsRevDesc = null;
						try {
							cdsRevDesc = cdsClient.getRevisionDescription(r.getRevisionId(), ws.getCdsKey());
							logger.info("Revision description already migrated");
						} catch (HttpStatusCodeException ex) {
							CMSRevisionDescription cmsRevDesc = cmsClient.getRevisionDescription(s.getSolutionId(),
									r.getRevisionId(), ws.getCmsKey());
							if (cmsRevDesc != null && cmsRevDesc.getDescription() != null
									&& cmsRevDesc.getDescription().length() > 0) {
								logger.info("Migrating revision description");
								cdsRevDesc = new MLPRevisionDescription(r.getRevisionId(), ws.getCdsKey(),
										cmsRevDesc.getDescription());
								cdsClient.createRevisionDescription(cdsRevDesc);
							}
						}

						// Item 3: revision documents, 0..many per workspace value
						List<MLPDocument> cdsRevDocs = cdsClient.getSolutionRevisionDocuments(r.getRevisionId(),
								ws.getCdsKey());
						CMSNameList cmsRevDocs = cmsClient.getRevisionDocumentNames(s.getSolutionId(),
								r.getRevisionId(), ws.getCmsKey());
						for (String docName : cmsRevDocs.getResponse_body()) {
							if (findDocNameInCdsList(docName, cdsRevDocs)) {
								logger.info("Revision document already migrated: name {}", docName);
							} else {
								logger.info("Migrating revision document: name {}", docName);
								byte[] cmsDoc = cmsClient.getRevisionDocument(s.getSolutionId(), r.getRevisionId(),
										ws.getCmsKey(), docName);
								String uri = null; // TODO: PUSH TO NEXUS!
								MLPDocument cdsDoc = new MLPDocument(null, docName, uri, cmsDoc.length, r.getUserId());
								cdsClient.createDocument(cdsDoc);
							}
						} // for doc

					} // for workspace
					
				} // for revision

			} // for solution in page

			// Stop after the last page
			if (sols.getNumberOfElements() < pageSize)
				break;

		} // for page

	}

	private static boolean findDocNameInCdsList(String name, List<MLPDocument> docs) {
		for (MLPDocument d : docs)
			if (name.equals(d.getName()))
				return true;
		return false;
	}
}
