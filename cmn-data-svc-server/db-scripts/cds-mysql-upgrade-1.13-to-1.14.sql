-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
-- FROM version 1.13.x TO version 1.14.x.
-- No database is specified to allow flexible deployment!

-- 1
ALTER TABLE C_PEER
  DROP FOREIGN KEY C_PEER_C_PEER_STATUS;
-- 2
ALTER TABLE C_PEER
  DROP FOREIGN KEY C_PEER_C_VALIDATION_STATUS;
-- 3
ALTER TABLE C_PEER_SUB
  DROP FOREIGN KEY C_PEER_SUB_C_SCOPE;
-- 4
ALTER TABLE C_PEER_SUB
  DROP FOREIGN KEY C_PEER_SUB_C_ACCESS;
-- 5
ALTER TABLE C_USER_LOGIN_PROVIDER
  DROP FOREIGN KEY C_USR_LOGIN_PROVIDER_C_LOGIN_PROVIDER;
-- 6
ALTER TABLE C_SOLUTION
  DROP FOREIGN KEY C_SOLUTION_C_ACCESS_TYPE;
-- 7
ALTER TABLE C_SOLUTION
  DROP FOREIGN KEY C_SOLUTION_C_MODEL_TYPE;
-- 8
ALTER TABLE C_SOLUTION
  DROP FOREIGN KEY C_SOLUTION_C_TOOLKIT_TYPE;
-- 9
ALTER TABLE C_SOLUTION
  DROP FOREIGN KEY C_SOLUTION_C_VALIDATION_STATUS;
-- 10
ALTER TABLE C_ARTIFACT
  DROP FOREIGN KEY C_ARTIFACT_C_ARTIFACT_TYPE;
-- 11
ALTER TABLE C_SOLUTION_VALIDATION
  DROP FOREIGN KEY C_SOL_VAL_C_VAL_TYPE;
-- 12
ALTER TABLE C_SOLUTION_VALIDATION
  DROP FOREIGN KEY C_SOL_VAL_C_VAL_STATUS;
-- 13
ALTER TABLE C_SOL_VAL_SEQ
  DROP FOREIGN KEY C_SOL_VAL_SEQ_C_VAL_TYPE;
-- 14
ALTER TABLE C_SOLUTION_DEPLOYMENT 
  DROP FOREIGN KEY C_SOL_DEP_C_DEP_STATUS;
-- 15
ALTER TABLE C_STEP_RESULT
  DROP FOREIGN KEY C_STEP_RESULT_C_STEP_CD;
-- 16
ALTER TABLE C_STEP_RESULT
  DROP FOREIGN KEY C_STEP_RESULT_STATUS_CD;
-- 17
DROP TABLE C_ACCESS_TYPE;
-- 18
DROP TABLE C_ARTIFACT_TYPE;
-- 19
DROP TABLE C_STEP_TYPE;
-- 20
DROP TABLE C_TOOLKIT_TYPE;
-- 21
DROP TABLE C_MODEL_TYPE;
-- 22
DROP TABLE C_SUB_SCOPE_TYPE;
-- 23
DROP TABLE C_VALIDATION_TYPE;
-- 24
DROP TABLE C_DEPLOYMENT_STATUS;
-- 25
DROP TABLE C_PEER_STATUS;
-- 26
DROP TABLE C_STEP_STATUS;
-- 27
DROP TABLE C_VALIDATION_STATUS;
-- 28
DROP TABLE C_LOGIN_PROVIDER;
-- 29
ALTER TABLE C_NOTIFICATION ADD COLUMN MSG_SEVERITY_CD CHAR(2) NOT NULL;
-- 30
CREATE TABLE C_NOTIF_USER_PREF (
  ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  USER_ID CHAR(36) NOT NULL,
  NOTIF_DELV_MECH_CD CHAR(2) NOT NULL,
  MSG_SEVERITY_CD CHAR(2) NOT NULL,
  CONSTRAINT C_NOTIF_USER_PREF_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 31
CREATE TABLE C_PEER_GROUP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT, 
  NAME VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL, 
  MODIFIED_DATE TIMESTAMP NOT NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 32
CREATE TABLE C_SOLUTION_GROUP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 33
CREATE TABLE C_PEER_GRP_MEM_MAP (
  GROUP_ID INT NOT NULL,
  PEER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (GROUP_ID, PEER_ID),
  CONSTRAINT C_PEER_GRP_MEM_MAP_C_GROUP FOREIGN KEY (GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_GRP_MEM_MAP_C_PEER FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 34
CREATE TABLE C_SOL_GRP_MEM_MAP (
  GROUP_ID INT NOT NULL,
  SOLUTION_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (GROUP_ID, SOLUTION_ID),
  CONSTRAINT C_SOL_GRP_MEM_MAP_C_GROUP FOREIGN KEY (GROUP_ID) REFERENCES C_SOLUTION_GROUP (GROUP_ID),
  CONSTRAINT C_SOL_GRP_MEM_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 35
CREATE TABLE C_PEER_SOL_ACC_MAP (
  PEER_GROUP_ID INT NOT NULL, 
  SOL_GROUP_ID INT NOT NULL, 
  GRANTED_YN CHAR(1) DEFAULT 'N' NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (PEER_GROUP_ID, SOL_GROUP_ID),
  CONSTRAINT C_PEER_SOL_ACCESS_MAP_C_PEER_GRP FOREIGN KEY (PEER_GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_SOL_ACCESS_MAP_C_SOL_GRP FOREIGN KEY (SOL_GROUP_ID) REFERENCES C_SOLUTION_GROUP (GROUP_ID)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 36
CREATE TABLE C_PEER_PEER_ACC_MAP (
  PRINCIPAL_GROUP_ID INT NOT NULL, 
  RESOURCE_GROUP_ID INT NOT NULL, 
  CREATED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (PRINCIPAL_GROUP_ID, RESOURCE_GROUP_ID),
  CONSTRAINT C_PEER_PEER_ACCESS_MAP_C_PRINCIPAL FOREIGN KEY (PRINCIPAL_GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_PEER_ACCESS_MAP_C_RESOURCE  FOREIGN KEY (RESOURCE_GROUP_ID)  REFERENCES C_PEER_GROUP (GROUP_ID)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- WARNING!!  THE FOLLOWING ALTER STATEMENT ADDS A UNIQUE CONSTRAINT TO C_PEER. IF THE FOLLOWING SELECT RETURNS
-- A RECORD THEN THE ALTER C_PEER WILL FAIL, FAILING THE SCRIPT. IN THAT CASE DELETE THE EXISTING RECORDS IN DATABASE WITH 
-- DUPLICATE PEER SUBJECT NAMES UNTIL THERE EXISTS AT MOST ONE RECORD WITH THAT SUBJECT NAME AND RERUN THE ALTER STATEMENT
-- 37
SELECT SUBJECT_NAME, count(*) AS c FROM C_PEER GROUP BY SUBJECT_NAME HAVING c > 1;
-- 38
ALTER TABLE C_PEER
ADD CONSTRAINT C_PEER_C_SUBJECT_NAME UNIQUE (SUBJECT_NAME);
-- 39
ALTER TABLE C_USER MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 40
ALTER TABLE C_ROLE MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 41
ALTER TABLE C_ROLE_FUNCTION MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 42
ALTER TABLE C_PEER MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 43
ALTER TABLE C_PEER_SUB MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 44
ALTER TABLE C_USER_LOGIN_PROVIDER MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 45
ALTER TABLE C_SOLUTION MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 46
ALTER TABLE C_SOLUTION_REV MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 47
ALTER TABLE C_ARTIFACT MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 48
ALTER TABLE C_SOLUTION_RATING MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 49
ALTER TABLE C_NOTIFICATION MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 50
ALTER TABLE C_SOLUTION_VALIDATION  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 51
ALTER TABLE C_SOL_VAL_SEQ  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 52
ALTER TABLE C_SOLUTION_DEPLOYMENT  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 53
ALTER TABLE C_SITE_CONFIG  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 54
ALTER TABLE C_COMMENT  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 55
ALTER TABLE C_PEER_GROUP  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 56
ALTER TABLE C_SOLUTION_GROUP  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 57
ALTER TABLE C_PEER_GRP_MEM_MAP  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 58
ALTER TABLE C_SOL_GRP_MEM_MAP  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 59
ALTER TABLE C_PEER_SOL_ACC_MAP  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';
-- 60
ALTER TABLE C_PEER_PEER_ACC_MAP  MODIFY COLUMN CREATED_DATE TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00';





