-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

-- DDL and DML for tables managed by the Common Data Service version 1.18.x
-- No database is created or specified to allow flexible deployment;
-- also see script cmn-data-svc-base-mysql.sql.

-- Remember: DATETIME stores a date and a time (no zone), the exact behavior depends on
-- the server's timezone.  TIMESTAMP stores an unambiguous point in time up to 2038; and
-- also remember that MySql/Mariadb have automatic behaviors on TIMESTAMP columns.

-- DDL --

CREATE TABLE C_USER (
  USER_ID CHAR(36) NOT NULL PRIMARY KEY,
  FIRST_NAME VARCHAR(50),
  MIDDLE_NAME VARCHAR(50),
  LAST_NAME VARCHAR(50),
  ORG_NAME VARCHAR(50),
  EMAIL VARCHAR(100) NOT NULL,
  LOGIN_NAME VARCHAR(25) NOT NULL,
  LOGIN_HASH VARCHAR(64),
  LOGIN_PASS_EXPIRE_DATE DATETIME NULL DEFAULT NULL,
  -- JSON web token
  AUTH_TOKEN VARCHAR(4096),
  ACTIVE_YN CHAR(1) NOT NULL DEFAULT 'Y',
  LAST_LOGIN_DATE DATETIME NULL DEFAULT NULL,
  LOGIN_FAIL_COUNT SMALLINT NULL,
  LOGIN_FAIL_DATE DATETIME NULL DEFAULT NULL,
  -- LONGBLOB is overkill but allows schema validation
  PICTURE LONGBLOB,
  API_TOKEN VARCHAR(64),
  VERIFY_TOKEN_HASH VARCHAR(64),
  VERIFY_EXPIRE_DATE DATETIME NULL DEFAULT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  UNIQUE INDEX C_USER_C_EMAIL (EMAIL),
  UNIQUE INDEX C_USER_C_LOGIN_NAME (LOGIN_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_ROLE (
  ROLE_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  UNIQUE INDEX C_ROLE_C_NAME (NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of user to role requires a map (join) table
CREATE TABLE C_USER_ROLE_MAP (
  USER_ID CHAR(36) NOT NULL,
  ROLE_ID CHAR(36) NOT NULL,
  PRIMARY KEY (USER_ID, ROLE_ID),
  CONSTRAINT FK_C_USER_ROLE_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT FK_C_USER_ROLE_MAP_C_ROLE FOREIGN KEY (ROLE_ID) REFERENCES C_ROLE (ROLE_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_ROLE_FUNCTION (
  ROLE_FUNCTION_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ROLE_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_ROLE_FUNCTION_C_ROLE FOREIGN KEY (ROLE_ID) REFERENCES C_ROLE (ROLE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER (
  PEER_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(50) NOT NULL,
  -- X.509 certificate subject name
  SUBJECT_NAME VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(512),
  API_URL VARCHAR(512) NOT NULL,
  WEB_URL VARCHAR(512),
  IS_SELF CHAR(1) NOT NULL DEFAULT 'N',
  IS_LOCAL CHAR(1) NOT NULL DEFAULT 'N',
  CONTACT1 VARCHAR(100) NOT NULL,
  STATUS_CD CHAR(2) NOT NULL,
  VALIDATION_STATUS_CD CHAR(2) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_PEER_C_SUBJECT_NAME UNIQUE (SUBJECT_NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER_SUB (
  SUB_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  PEER_ID CHAR(36) NOT NULL,
  -- MariaDB does not support JSON column type
  SELECTOR VARCHAR(1024) CHECK (SELECTOR IS NULL OR JSON_VALID(SELECTOR)),
  OPTIONS VARCHAR(1024) CHECK (OPTIONS IS NULL OR JSON_VALID(OPTIONS)),
  -- Seconds
  REFRESH_INTERVAL INT,
  -- Bytes
  MAX_ARTIFACT_SIZE INT,
  USER_ID CHAR(36) NOT NULL,
  SCOPE_TYPE CHAR(2) NOT NULL,
  ACCESS_TYPE CHAR(2) NOT NULL,
  PROCESSED_DATE TIMESTAMP NULL DEFAULT 0,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_PEER_SUB_C_PEER FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID),
  CONSTRAINT C_PEER_SUB_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_USER_LOGIN_PROVIDER (
  USER_ID CHAR(36) NOT NULL,
  PROVIDER_CD CHAR(2) NOT NULL,
  PROVIDER_USER_ID VARCHAR(255) NOT NULL,
  RANK SMALLINT NOT NULL,
  DISPLAY_NAME VARCHAR(256),
  PROFILE_URL VARCHAR(512),
  IMAGE_URL VARCHAR(512),
  SECRET VARCHAR(256),
  ACCESS_TOKEN  VARCHAR(256) NOT NULL,
  REFRESH_TOKEN VARCHAR(256),
  EXPIRE_TIME TIMESTAMP NOT NULL DEFAULT 0,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_USER_LOGIN_PROVIDER_PK PRIMARY KEY (USER_ID, PROVIDER_CD, PROVIDER_USER_ID),
  CONSTRAINT C_USER_LOGIN_PROVIDER_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION (
  SOLUTION_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(512),
  USER_ID CHAR(36) NOT NULL,
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  MODEL_TYPE_CD CHAR(2),
  TOOLKIT_TYPE_CD CHAR(2),
  -- MariaDB does not support JSON column type
  METADATA VARCHAR(1024) CHECK (METADATA IS NULL OR JSON_VALID(METADATA)),
  SOURCE_ID CHAR(36),
  ORIGIN VARCHAR(512),
  -- LONGBLOB is overkill but allows schema validation
  PICTURE LONGBLOB,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOLUTION_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_SOLUTION_C_PEER FOREIGN KEY (SOURCE_ID) REFERENCES C_PEER (PEER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_WEB (
  SOLUTION_ID CHAR(36) NOT NULL PRIMARY KEY,
  VIEW_COUNT INT,
  DOWNLOAD_COUNT INT,
  LAST_DOWNLOAD TIMESTAMP NOT NULL DEFAULT 0,
  RATING_COUNT INT,
  RATING_AVG_TENTHS INT,
  FEATURED_YN char(1),
  CONSTRAINT C_SOL_WEB_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:one mapping with solution; no need for map (join) table
CREATE TABLE C_SOLUTION_REV (
  REVISION_ID CHAR(36) NOT NULL PRIMARY KEY,
  SOLUTION_ID CHAR(36) NOT NULL,
  VERSION VARCHAR(25) NOT NULL,
  ACCESS_TYPE_CD CHAR(2) NOT NULL,
  VALIDATION_STATUS_CD CHAR(2) NOT NULL,
  DESCRIPTION VARCHAR(512),
  USER_ID CHAR(36) NOT NULL,
  -- MariaDB does not support JSON column type
  METADATA VARCHAR(1024) CHECK (METADATA IS NULL OR JSON_VALID(METADATA)),
  SOURCE_ID CHAR(36),
  ORIGIN VARCHAR(512),
  AUTHORS VARCHAR(1024),
  PUBLISHER VARCHAR(64),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOLUTION_REV_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_REV_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_SOLUTION_REV_C_PEER FOREIGN KEY (SOURCE_ID) REFERENCES C_PEER (PEER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- System-generated content stored in Nexus
CREATE TABLE C_ARTIFACT (
  ARTIFACT_ID CHAR(36) NOT NULL PRIMARY KEY,
  VERSION VARCHAR(25) NOT NULL,
  -- Value set restricted by type table
  ARTIFACT_TYPE_CD CHAR(2) NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(512),
  URI VARCHAR(512) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  SIZE INT NOT NULL,
  -- MariaDB does not support JSON column type
  METADATA VARCHAR(1024) CHECK (METADATA IS NULL OR JSON_VALID(METADATA)),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_ARTIFACT_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution_rev to artifact requires a map (join) table
CREATE TABLE C_SOL_REV_ART_MAP (
  REVISION_ID CHAR(36) NOT NULL,
  ARTIFACT_ID CHAR(36) NOT NULL,
  PRIMARY KEY (REVISION_ID, ARTIFACT_ID),
  CONSTRAINT C_SOL_REV_ART_MAP_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOL_REV_ART_MAP_C_ARTIFACT     FOREIGN KEY (ARTIFACT_ID) REFERENCES C_ARTIFACT (ARTIFACT_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution to solution (composite) requires a map (join) table
CREATE TABLE C_COMP_SOL_MAP (
  PARENT_ID CHAR(36) NOT NULL,
  CHILD_ID CHAR(36) NOT NULL,
  PRIMARY KEY (PARENT_ID, CHILD_ID),
  CONSTRAINT C_COMP_SOL_MAP_PARENT FOREIGN KEY (PARENT_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_COMP_SOL_MAP_CHILD  FOREIGN KEY (CHILD_ID)  REFERENCES C_SOLUTION (SOLUTION_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- No ID column, no created/modified columns, just a name
-- because the content is shorter than a UUID field.
CREATE TABLE C_SOLUTION_TAG (
  TAG VARCHAR(32) NOT NULL PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution to tag requires a map (join) table
CREATE TABLE C_SOL_TAG_MAP (
  SOLUTION_ID CHAR(36) NOT NULL,
  TAG VARCHAR(32) NOT NULL,
  PRIMARY KEY (SOLUTION_ID, TAG),
  CONSTRAINT C_SOL_TAG_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_TAG_MAP_C_SOLUTION_TAG FOREIGN KEY (TAG) REFERENCES C_SOLUTION_TAG (TAG)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of user to solution tag requires a map (join) table
CREATE TABLE C_USER_TAG_MAP (
  USER_ID CHAR(36) NOT NULL,
  TAG VARCHAR(32) NOT NULL,
  PRIMARY KEY (USER_ID, TAG),
  CONSTRAINT C_USER_TAG_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_USER_TAG_MAP_C_SOL_TAG FOREIGN KEY (TAG) REFERENCES C_SOLUTION_TAG (TAG)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_FAVORITE (
  SOLUTION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  PRIMARY KEY (SOLUTION_ID, USER_ID),
  CONSTRAINT C_SOLUTION_FAVORITE_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_FAVORITE_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_DOWNLOAD (
  DOWNLOAD_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  SOLUTION_ID CHAR(36) NOT NULL,
  ARTIFACT_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  DOWNLOAD_DATE TIMESTAMP NOT NULL,
  INDEX (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_DOWNLOAD_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_DOWNLOAD_C_ARTIFACT FOREIGN KEY (ARTIFACT_ID) REFERENCES C_ARTIFACT (ARTIFACT_ID),
  CONSTRAINT C_SOLUTION_DOWNLOAD_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_RATING (
  SOLUTION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  RATING SMALLINT,
  TEXT_REVIEW VARCHAR(1024),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (SOLUTION_ID, USER_ID),
  CONSTRAINT C_SOLUTION_RATING_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_RATING_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_NOTIFICATION (
  NOTIFICATION_ID CHAR(36) NOT NULL PRIMARY KEY,
  TITLE VARCHAR(100) NOT NULL,
  MESSAGE VARCHAR(2048),
  MSG_SEVERITY_CD CHAR(2) NOT NULL,
  URL VARCHAR(512),
  -- disable auto-update behavior with default values
  START_DATE DATETIME NOT NULL DEFAULT 0,
  END_DATE DATETIME NOT NULL DEFAULT 0,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Also has an attribute, not just ID columns
CREATE TABLE C_NOTIF_USER_MAP (
  NOTIFICATION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  -- disable auto-update behavior with default value
  VIEWED_DATE DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (NOTIFICATION_ID, USER_ID),
  CONSTRAINT C_NOTIF_USER_MAP_C_NOTIFICATION FOREIGN KEY (NOTIFICATION_ID) REFERENCES C_NOTIFICATION (NOTIFICATION_ID),
  CONSTRAINT C_NOTIF_USER_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Access control list is a many:many mapping of solution to user, requires a map (join) table
CREATE TABLE C_SOL_USER_ACCESS_MAP (
  SOLUTION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  PRIMARY KEY (SOLUTION_ID, USER_ID),
  CONSTRAINT C_SOL_USER_ACCESS_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_USER_ACCESS_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_VALIDATION (
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  TASK_ID CHAR(36) NOT NULL,
  VAL_TYPE_CD CHAR(2) NOT NULL,
  VAL_STATUS_CD CHAR(2),
  -- MariaDB does not support JSON column type
  DETAIL VARCHAR(8192) CHECK (DETAIL IS NULL OR JSON_VALID(DETAIL)),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (SOLUTION_ID, REVISION_ID, TASK_ID),
  CONSTRAINT C_SOL_VAL_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_VAL_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOL_VAL_SEQ (
  SEQ SMALLINT NOT NULL,
  VAL_TYPE_CD CHAR(2) NOT NULL,
  PRIMARY KEY (SEQ, VAL_TYPE_CD),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_DEPLOYMENT (
  DEPLOYMENT_ID CHAR(36) NOT NULL PRIMARY KEY,
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  DEP_STATUS_CD CHAR(2) NOT NULL,
  TARGET VARCHAR(64),
  DETAIL VARCHAR(1024) CHECK (DETAIL IS NULL OR JSON_VALID(DETAIL)),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOL_DEP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_DEP_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOL_DEP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SITE_CONFIG (
  CONFIG_KEY VARCHAR(50) NOT NULL PRIMARY KEY,
  CONFIG_VAL VARCHAR(8192) NOT NULL CHECK (JSON_VALID(CONFIG_VAL)),
  USER_ID CHAR(36) NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SITE_CONFIG_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_THREAD (
  THREAD_ID CHAR(36) NOT NULL PRIMARY KEY,
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  TITLE VARCHAR(128),
  CONSTRAINT C_THREAD_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_THREAD_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_COMMENT (
  COMMENT_ID CHAR(36) NOT NULL PRIMARY KEY,
  THREAD_ID CHAR(36) NOT NULL,
  PARENT_ID CHAR(36) NULL,
  USER_ID CHAR(36) NOT NULL,
  TEXT VARCHAR(8192) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_COMMENT_C_THREAD FOREIGN KEY (THREAD_ID) REFERENCES C_THREAD (THREAD_ID),
  CONSTRAINT C_COMMENT_C_PARENT FOREIGN KEY (PARENT_ID) REFERENCES C_COMMENT (COMMENT_ID),
  CONSTRAINT C_COMMENT_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_STEP_RESULT (
  ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  STEP_CD CHAR(2) NOT NULL,
  STATUS_CD CHAR(2) NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  TRACKING_ID CHAR(36),
  SOLUTION_ID CHAR(36),
  REVISION_ID CHAR(36),
  ARTIFACT_ID CHAR(36),
  USER_ID CHAR(36),
  RESULT VARCHAR(8192),
  START_DATE TIMESTAMP NOT NULL DEFAULT 0,
  END_DATE TIMESTAMP,
  CONSTRAINT C_STEP_RESULT_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_STEP_RESULT_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_STEP_RESULT_C_ARTIFACT FOREIGN KEY (ARTIFACT_ID) REFERENCES C_ARTIFACT (ARTIFACT_ID),
  CONSTRAINT C_STEP_RESULT_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_NOTIF_USER_PREF (
  ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  USER_ID CHAR(36) NOT NULL,
  NOTIF_DELV_MECH_CD CHAR(2) NOT NULL,
  MSG_SEVERITY_CD CHAR(2) NOT NULL,
  CONSTRAINT C_NOTIF_USER_PREF_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER_GROUP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  UNIQUE INDEX C_PEER_GROUP_C_NAME (NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOLUTION_GROUP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  UNIQUE INDEX C_SOLUTION_GROUP_C_NAME (NAME)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER_GRP_MEM_MAP (
  GROUP_ID INT NOT NULL,
  PEER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  PRIMARY KEY (GROUP_ID, PEER_ID),
  CONSTRAINT C_PEER_GRP_MEM_MAP_C_GROUP FOREIGN KEY (GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_GRP_MEM_MAP_C_PEER FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_SOL_GRP_MEM_MAP (
  GROUP_ID INT NOT NULL,
  SOLUTION_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  PRIMARY KEY (GROUP_ID, SOLUTION_ID),
  CONSTRAINT C_SOL_GRP_MEM_MAP_C_GROUP FOREIGN KEY (GROUP_ID) REFERENCES C_SOLUTION_GROUP (GROUP_ID),
  CONSTRAINT C_SOL_GRP_MEM_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER_SOL_ACC_MAP (
  PEER_GROUP_ID INT NOT NULL,
  SOL_GROUP_ID INT NOT NULL,
  GRANTED_YN CHAR(1) DEFAULT 'N' NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  PRIMARY KEY (PEER_GROUP_ID, SOL_GROUP_ID),
  CONSTRAINT C_PEER_SOL_ACCESS_MAP_C_PEER_GRP FOREIGN KEY (PEER_GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_SOL_ACCESS_MAP_C_SOL_GRP FOREIGN KEY (SOL_GROUP_ID) REFERENCES C_SOLUTION_GROUP (GROUP_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_PEER_PEER_ACC_MAP (
  PRINCIPAL_GROUP_ID INT NOT NULL,
  RESOURCE_GROUP_ID INT NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  PRIMARY KEY (PRINCIPAL_GROUP_ID, RESOURCE_GROUP_ID),
  CONSTRAINT C_PEER_PEER_ACCESS_MAP_C_PRINCIPAL FOREIGN KEY (PRINCIPAL_GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_PEER_ACCESS_MAP_C_RESOURCE  FOREIGN KEY (RESOURCE_GROUP_ID)  REFERENCES C_PEER_GROUP (GROUP_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_REVISION_DESC (
  REVISION_ID CHAR(36) NOT NULL,
  ACCESS_TYPE_CD CHAR(2) NOT NULL,
  DESCRIPTION LONGTEXT NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (REVISION_ID, ACCESS_TYPE_CD),
  CONSTRAINT C_REV_DESC_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- User-generated content stored in Nexus
CREATE TABLE C_DOCUMENT (
  DOCUMENT_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  URI VARCHAR(512) NOT NULL,
  VERSION VARCHAR(25),
  SIZE INT NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_DOCUMENT_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution_rev to document requires a map (join) table
CREATE TABLE C_SOL_REV_DOC_MAP (
  REVISION_ID CHAR(36) NOT NULL,
  ACCESS_TYPE_CD CHAR(2) NOT NULL,
  DOCUMENT_ID CHAR(36) NOT NULL,
  PRIMARY KEY (REVISION_ID, ACCESS_TYPE_CD, DOCUMENT_ID),
  CONSTRAINT C_REV_DOC_MAP_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_REV_DOC_MAP_C_REV_DOC      FOREIGN KEY (DOCUMENT_ID) REFERENCES C_DOCUMENT (DOCUMENT_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Tracks publish-to-public workflow requests
CREATE TABLE C_PUBLISH_REQUEST (
  REQUEST_ID INT PRIMARY KEY AUTO_INCREMENT,
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  REQ_USER_ID CHAR(36) NOT NULL,
  RVW_USER_ID CHAR(36),
  STATUS_CD CHAR(2) NOT NULL,
  COMMENT VARCHAR(8192),
  CONSTRAINT C_PUB_REQ_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_PUB_REQ_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_PUB_REQ_REQ_C_USER FOREIGN KEY (REQ_USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_PUB_REQ_APP_C_USER FOREIGN KEY (RVW_USER_ID) REFERENCES C_USER (USER_ID),
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- For tracking create/upgrade/downgrade; no Java entity
CREATE TABLE C_HISTORY (
  ID INT PRIMARY KEY AUTO_INCREMENT,
  COMMENT VARCHAR(100) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- DML --

# Base roles; names are case sensitive
INSERT INTO C_ROLE (ROLE_ID, NAME, ACTIVE_YN, CREATED_DATE) VALUES ('12345678-abcd-90ab-cdef-1234567890ab', 'MLP System User', 'Y', CURRENT_TIMESTAMP());
INSERT INTO C_ROLE (ROLE_ID, NAME, ACTIVE_YN, CREATED_DATE) VALUES ('8c850f07-4352-4afd-98b1-00cbceca569f', 'Admin', 'Y', CURRENT_TIMESTAMP());
INSERT INTO C_ROLE (ROLE_ID, NAME, ACTIVE_YN, CREATED_DATE) VALUES ('9d961018-5464-5b0e-a9c2-11dcdfdb67a0', 'Publisher', 'Y', CURRENT_TIMESTAMP());

-- Base admin user
INSERT INTO C_USER (USER_ID, LOGIN_NAME, LOGIN_HASH, FIRST_NAME, LAST_NAME, EMAIL, CREATED_DATE) VALUES ('12345678-abcd-90ab-cdef-1234567890ab', 'admin', '$2a$10$nogCM69/Vc0rEsZbHXlEm.nxSdGuD88Kd6NlW6fnKJz3AIz0PdOwa', 'Acumos', 'Admin', 'noreply@acumos.org', CURRENT_TIMESTAMP());

-- Grant all roles to admin user
INSERT INTO C_USER_ROLE_MAP (USER_ID, ROLE_ID) VALUES ('12345678-abcd-90ab-cdef-1234567890ab', '8c850f07-4352-4afd-98b1-00cbceca569f');
INSERT INTO C_USER_ROLE_MAP (USER_ID, ROLE_ID) VALUES ('12345678-abcd-90ab-cdef-1234567890ab', '9d961018-5464-5b0e-a9c2-11dcdfdb67a0');

# Default configuration of Portal/Marketplace features.
# Unfortunately JSON does not allow embedded newlines.
INSERT INTO C_SITE_CONFIG (CONFIG_KEY, CONFIG_VAL) VALUES (
  'site_config',
  '{"fields":[{"type":"text","name":"siteInstanceName","label":"Site Instance Name","required":"true","data":"Acumos"}, {"type":"file","name":"headerLogo","label":"Header Logo","data":{"lastModified":1510831880727,"lastModifiedDate":"2017-11-16T11:31:20.727Z","name":"acumos_logo_white.png","size":3657,"type":"image/png"}},{"type":"file","name":"footerLogo","label":"Footer Logo","data":{"lastModified":1510831874776,"lastModifiedDate":"2017-11-16T11:31:14.776Z","name":"footer_logo.png","size":3127,"type":"image/png"}},{"type":"heading","name":"ConnectionConfig","label":"Connection Configuration","required":"true","subFields":[{"type":"text","name":"socketTimeout","label":"Socket Timeout","required":"true","data":"300"},{"type":"text","name":"connectionTimeout","label":"Connection Timeout","required":"true","data":"10"}]},{"type":"select","name":"enableOnBoarding","label":"Enable On-Boarding","options":[{"name":"Enabled"},{"name":"Disabled"}],"required":true,"data":{"name":"Enabled"}},{"type":"textarea","name":"validationText","label":"Model Validation Keyword Scan Entries (CSV)","required":"false","data":"test"},{"type":"select","name":"EnableDCAE","label":"Enable DCAE","options":[{"name":"Enabled"},{"name":"Disabled"}],"required":true,"data":{"name":"Disabled"}}]}'
);
