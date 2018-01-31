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
CREATE TABLE C_PEER_GROUP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT, 
  NAME VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL, 
  MODIFIED_DATE TIMESTAMP NOT NULL 
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 30
CREATE TABLE C_SOLUTION_GROUP (
  GROUP_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR(50) NOT NULL,
  DESCRIPTION VARCHAR(512),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 31
CREATE TABLE C_PEER_GRP_MEM_MAP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT,
  PEER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_PEER_GRP_MEM_MAP_C_GROUP FOREIGN KEY (GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_GRP_MEM_MAP_C_PEER FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 32
CREATE TABLE C_SOL_GRP_MEM_MAP (
  GROUP_ID INT PRIMARY KEY AUTO_INCREMENT,
  SOLUTION_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOL_GRP_MEM_MAP_C_GROUP FOREIGN KEY (GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_SOL_GRP_MEM_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 33
CREATE TABLE C_PEER_SOL_ACC_MAP (
  PEER_GROUP_ID INT NOT NULL, 
  SOL_GROUP_ID INT NOT NULL, 
  GRANTED_YN CHAR(1) DEFAULT 'N' NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (PEER_GROUP_ID, SOL_GROUP_ID),
  CONSTRAINT C_PEER_SOL_ACCESS_MAP_C_PEER FOREIGN KEY (PEER_GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_SOL_ACCESS_MAP_C_SOLUTION FOREIGN KEY (SOL_GROUP_ID) REFERENCES C_SOLUTION_GROUP (GROUP_ID)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 34
CREATE TABLE C_PEER_PEER_ACC_MAP (
  PRINCIPAL_GROUP_ID INT NOT NULL, 
  RESOURCE_GROUP_ID INT NOT NULL, 
  CREATED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (PRINCIPAL_GROUP_ID, RESOURCE_GROUP_ID),
  CONSTRAINT C_PEER_PEER_ACCESS_MAP_C_PRINCIPAL FOREIGN KEY (PRINCIPAL_GROUP_ID) REFERENCES C_PEER_GROUP (GROUP_ID),
  CONSTRAINT C_PEER_PEER_ACCESS_MAP_C_RESOURCE  FOREIGN KEY (RESOURCE_GROUP_ID)  REFERENCES C_PEER_GROUP (GROUP_ID)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
