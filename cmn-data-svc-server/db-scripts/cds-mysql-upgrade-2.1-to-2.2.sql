-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
-- ===================================================================================
-- This Acumos software file is distributed by AT&T and Tech Mahindra
-- under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- This file is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ===============LICENSE_END=========================================================

-- Script to upgrade database used by the Common Data Service
-- FROM version 2.1 TO version 2.2.
-- No database name is set to allow flexible deployment.

CREATE TABLE C_NOTEBOOK (
  NOTEBOOK_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  VERSION VARCHAR(25) NOT NULL,
  NOTEBOOK_TYPE_CD CHAR(2) NOT NULL,
  KERNEL_TYPE_CD CHAR(2),
  DESCRIPTION VARCHAR(1024),
  SERVICE_STATUS_CD CHAR(2),
  REPOSITORY_URL VARCHAR(512),
  SERVICE_URL VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_NOTEBOOK_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PROJECT (
  PROJECT_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  VERSION VARCHAR(25) NOT NULL,
  DESCRIPTION VARCHAR(1024),
  SERVICE_STATUS_CD CHAR(2),
  REPOSITORY_URL VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_PROJECT_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PIPELINE (
  PIPELINE_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  VERSION VARCHAR(25) NOT NULL,
  DESCRIPTION VARCHAR(1024),
  SERVICE_STATUS_CD CHAR(2),
  REPOSITORY_URL VARCHAR(512),
  SERVICE_URL VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_PIPELINE_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PROJ_NB_MAP (
  PROJECT_ID CHAR(36) NOT NULL,
  NOTEBOOK_ID CHAR(36) NOT NULL,
  PRIMARY KEY (PROJECT_ID, NOTEBOOK_ID),
  CONSTRAINT C_PROJ_NB_MAP_C_PROJECT FOREIGN KEY (PROJECT_ID) REFERENCES C_PROJECT (PROJECT_ID),
  CONSTRAINT C_PROJ_NB_MAP_C_NOTEBOOK FOREIGN KEY (NOTEBOOK_ID) REFERENCES C_NOTEBOOK (NOTEBOOK_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PROJ_PL_MAP (
  PROJECT_ID CHAR(36) NOT NULL,
  PIPELINE_ID CHAR(36) NOT NULL,
  PRIMARY KEY (PROJECT_ID, PIPELINE_ID),
  CONSTRAINT C_PROJ_PL_MAP_C_PROJECT FOREIGN KEY (PROJECT_ID) REFERENCES C_PROJECT (PROJECT_ID),
  CONSTRAINT C_PROJ_PL_MAP_C_PIPELINE FOREIGN KEY (PIPELINE_ID) REFERENCES C_PIPELINE (PIPELINE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER_CAT_ACC_MAP (
  PEER_ID CHAR(36) NOT NULL,
  CATALOG_ID CHAR(36) NOT NULL,
  PRIMARY KEY (PEER_ID, CATALOG_ID),
  CONSTRAINT C_PEER_CAT_ACC_MAP_C_PEER FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID),
  CONSTRAINT C_PEER_CAT_ACC_MAP_C_CATALOG FOREIGN KEY (CATALOG_ID) REFERENCES C_CATALOG (CATALOG_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_USER_CAT_FAV_MAP (
  USER_ID CHAR(36) NOT NULL,
  CATALOG_ID CHAR(36) NOT NULL,
  PRIMARY KEY (USER_ID, CATALOG_ID),
  CONSTRAINT C_USER_CAT_FAV_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_USER_CAT_FAV_MAP_C_CATALOG FOREIGN KEY (CATALOG_ID) REFERENCES C_CATALOG (CATALOG_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DELETE FROM C_CAT_SOL_MAP;

DELETE from C_CATALOG;

ALTER TABLE C_CATALOG ADD COLUMN SELF_PUBLISH_YN CHAR(1) NOT NULL DEFAULT 'N';
ALTER TABLE C_CATALOG MODIFY COLUMN PUBLISHER VARCHAR(64) NOT NULL;

-- Create RESTRICTED and PUBLIC catalogs
INSERT INTO C_CATALOG (CATALOG_ID, ACCESS_TYPE_CD, NAME, PUBLISHER, URL, CREATED_DATE, MODIFIED_DATE) 
  VALUES ('12345678-abcd-90ab-cdef-1234567890ab', 'PB', 'Site Name Public Catalog', 'Site Publisher', 'http://catalog.my.org/public', NOW(), NOW());
INSERT INTO C_CATALOG (CATALOG_ID, ACCESS_TYPE_CD, NAME, PUBLISHER, URL, CREATED_DATE, MODIFIED_DATE) 
  VALUES ('1d011340-d1d2-4758-93ea-49becb833080', 'RS', 'Site Name Restricted Catalog', 'Site Publisher', 'http://catalog.my.org/restricted', NOW(), NOW());

ALTER TABLE C_PUBLISH_REQUEST ADD COLUMN CATALOG_ID CHAR(36) NOT NULL DEFAULT '12345678-abcd-90ab-cdef-1234567890ab';
ALTER TABLE C_PUBLISH_REQUEST ADD CONSTRAINT C_PUB_REQ_C_CATALOG FOREIGN KEY (CATALOG_ID) REFERENCES C_CATALOG (CATALOG_ID);
ALTER TABLE C_PEER_SUB DROP COLUMN ACCESS_TYPE;
ALTER TABLE C_PEER_SUB DROP COLUMN SCOPE_TYPE;

-- order matters here
DROP TABLE C_PEER_SOL_ACC_MAP;
DROP TABLE C_PEER_PEER_ACC_MAP;
DROP TABLE C_PEER_GRP_MEM_MAP;
DROP TABLE C_SOL_GRP_MEM_MAP;
DROP TABLE C_PEER_GROUP;
DROP TABLE C_SOLUTION_GROUP;

CREATE TABLE C_REV_CAT_DESC (
  REVISION_ID CHAR(36) NOT NULL,
  CATALOG_ID CHAR(36) NOT NULL,
  DESCRIPTION LONGTEXT NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (REVISION_ID, CATALOG_ID),
  CONSTRAINT C_REV_CAT_DESC_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_REV_CAT_DESC_C_CATALOG FOREIGN KEY (CATALOG_ID) REFERENCES C_CATALOG (CATALOG_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of revision and catalog to document requires a map (join) table
CREATE TABLE C_REV_CAT_DOC_MAP (
  REVISION_ID CHAR(36) NOT NULL,
  CATALOG_ID CHAR(36) NOT NULL,
  DOCUMENT_ID CHAR(36) NOT NULL,
  PRIMARY KEY (REVISION_ID, CATALOG_ID, DOCUMENT_ID),
  CONSTRAINT C_REV_CAT_DOC_MAP_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_REV_CAT_DOC_MAP_C_CATALOG FOREIGN KEY (CATALOG_ID) REFERENCES C_CATALOG (CATALOG_ID),
  CONSTRAINT C_REV_CAT_DOC_MAP_C_REV_DOC FOREIGN KEY (DOCUMENT_ID) REFERENCES C_DOCUMENT (DOCUMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- TODO: Migrate revision descriptions and revision document maps
-- TODO: After all migration is complete, drop the revision access-type code,
-- TODO: the description table and the document-map table.

INSERT INTO c_rev_cat_desc (CATALOG_ID, REVISION_ID, DESCRIPTION, CREATED_DATE, MODIFIED_DATE)
SELECT '1d011340-d1d2-4758-93ea-49becb833080', REVISION_ID, DESCRIPTION, CREATED_DATE, MODIFIED_DATE
FROM c_revision_desc WHERE ACCESS_TYPE_CD = 'OR'; 

INSERT INTO c_cat_sol_map (CATALOG_ID, SOLUTION_ID)
SELECT distinct '1d011340-d1d2-4758-93ea-49becb833080', solution_id FROM c_solution_rev WHERE solution_id NOT IN
(SELECT solution_id FROM c_solution_rev WHERE access_type_cd  = 'PB');

INSERT INTO c_rev_cat_desc (CATALOG_ID, REVISION_ID, DESCRIPTION, CREATED_DATE, MODIFIED_DATE)
SELECT '12345678-abcd-90ab-cdef-1234567890ab', REVISION_ID, DESCRIPTION, CREATED_DATE, MODIFIED_DATE
FROM c_revision_desc WHERE ACCESS_TYPE_CD = 'PB'; 

INSERT INTO c_cat_sol_map (CATALOG_ID, SOLUTION_ID)
SELECT distinct '12345678-abcd-90ab-cdef-1234567890ab', solution_id FROM c_solution_rev WHERE solution_id NOT IN
(SELECT solution_id FROM c_solution_rev WHERE access_type_cd  IN ('PR','OR'));

INSERT INTO C_REV_CAT_DOC_MAP (CATALOG_ID, REVISION_ID, DOCUMENT_ID)
SELECT '1d011340-d1d2-4758-93ea-49becb833080', REVISION_ID, DOCUMENT_ID
FROM C_SOL_REV_DOC_MAP WHERE ACCESS_TYPE_CD = 'OR'; 

INSERT INTO C_REV_CAT_DOC_MAP (CATALOG_ID, REVISION_ID, DOCUMENT_ID)
SELECT '12345678-abcd-90ab-cdef-1234567890ab', REVISION_ID, DOCUMENT_ID
FROM C_SOL_REV_DOC_MAP WHERE ACCESS_TYPE_CD = 'PB'; 


ALTER TABLE C_SOLUTION_REV DROP COLUMN ACCESS_TYPE_CD;
-- TODO: DROP TABLE C_SOL_REV_DOC_MAP;
-- TODO: DROP TABLE C_REVISION_DESC
