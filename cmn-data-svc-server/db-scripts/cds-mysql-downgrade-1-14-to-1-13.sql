-- Script to downgrade database used by the Common Data Service
-- FROM version 1.14.x TO version 1.13.x.
-- No database is specified to allow flexible deployment!

-- 36
DROP TABLE C_PEER_PEER_ACC_MAP;
-- 35
DROP TABLE C_PEER_SOL_ACC_MAP;
-- 34
DROP TABLE C_SOL_GRP_MEM_MAP;
-- 33
DROP TABLE C_PEER_GRP_MEM_MAP;
-- 32
DROP TABLE C_SOLUTION_GROUP;
-- 31
DROP TABLE C_PEER_GROUP;
-- 30  
DROP TABLE C_NOTIF_USER_PREF;  
-- 29
ALTER TABLE C_NOTIFICATION DROP COLUMN MSG_SEVERITY_CD;
-- 28
CREATE TABLE C_LOGIN_PROVIDER (
  PROVIDER_CD CHAR(2) NOT NULL PRIMARY KEY,
  PROVIDER_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 27
CREATE TABLE C_VALIDATION_STATUS (
  STATUS_CD CHAR(2) NOT NULL PRIMARY KEY,
  STATUS_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 26
CREATE TABLE C_STEP_STATUS (
  STATUS_CD CHAR(2) NOT NULL PRIMARY KEY,
  STATUS_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 25
CREATE TABLE C_PEER_STATUS (
  STATUS_CD CHAR(2) NOT NULL PRIMARY KEY,
  STATUS_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 24
CREATE TABLE C_DEPLOYMENT_STATUS (
  STATUS_CD CHAR(2) NOT NULL PRIMARY KEY,
  STATUS_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 23
CREATE TABLE C_VALIDATION_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 22
CREATE TABLE C_SUB_SCOPE_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 21
CREATE TABLE C_MODEL_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 20
CREATE TABLE C_TOOLKIT_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 19
CREATE TABLE C_STEP_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 18
CREATE TABLE C_ARTIFACT_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 17
CREATE TABLE C_ACCESS_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('OR', 'Organization');
INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PB', 'Public');
INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PR', 'Private');
INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('RS', 'Restricted');

INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('BP', 'BLUEPRINT FILE' );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('CD', 'CDUMP FILE'     );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DI', 'DOCKER IMAGE'   );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DS', 'DATA SOURCE'    );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MD', 'METADATA'       );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MH', 'MODEL-H2O'      );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MI', 'MODEL IMAGE'    );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MR', 'MODEL-R'        );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MS', 'MODEL-SCIKIT'   );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MT', 'MODEL-TENSORFLOW');
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TE', 'TOSCA TEMPLATE' );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TG', 'TOSCA Generator Input File');
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TS', 'TOSCA SCHEMA'   );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TT', 'TOSCA TRANSLATE');
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PJ', 'PROTOBUF FILE');

INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('FB', 'Facebook');
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('GH', 'GitHub');
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('GP', 'Google Plus');
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('LI', 'LinkedIn');

INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('CL', 'Classification');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DS', 'Data Sources');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DT', 'Data Transformer');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PR', 'Prediction');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('RG', 'Regression');

INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('CP', 'Composite Solution');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DS', 'Design Studio');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('H2', 'H2O');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PB', 'Probe');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('RC', 'R');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('SK', 'Scikit-Learn');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TF', 'TensorFlow');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TC', 'Training Client');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('BR', 'Data Broker');

INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('FA', 'Failed');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('IP', 'In Progress');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('NV', 'Not Validated');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('PS', 'Passed');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('SB', 'Submitted');

INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('SS', 'Security Scan');
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('LC', 'License Check');
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('OQ', 'OSS Quantification');
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TA', 'Text Analysis');

INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('DP', 'Deployed');
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('FA', 'Failed');
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('IP', 'In Progress');
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('ST', 'Started');

INSERT INTO C_STEP_STATUS (STATUS_CD, STATUS_NAME) VALUES ('FA', 'Failed');
INSERT INTO C_STEP_STATUS (STATUS_CD, STATUS_NAME) VALUES ('ST', 'Started');
INSERT INTO C_STEP_STATUS (STATUS_CD, STATUS_NAME) VALUES ('SU', 'Succeeded');

INSERT INTO C_STEP_TYPE (TYPE_CD, TYPE_NAME) VALUES ('OB', 'Onboarding');
INSERT INTO C_STEP_TYPE (TYPE_CD, TYPE_NAME) VALUES ('VL', 'Validation');

INSERT INTO C_PEER_STATUS (STATUS_CD, STATUS_NAME) VALUES ('AC', 'Active');
INSERT INTO C_PEER_STATUS (STATUS_CD, STATUS_NAME) VALUES ('IN', 'Inactive');
INSERT INTO C_PEER_STATUS (STATUS_CD, STATUS_NAME) VALUES ('PA', 'Pending Active');
INSERT INTO C_PEER_STATUS (STATUS_CD, STATUS_NAME) VALUES ('RM', 'Removed');
INSERT INTO C_PEER_STATUS (STATUS_CD, STATUS_NAME) VALUES ('PR', 'Pending Remove');

INSERT INTO C_SUB_SCOPE_TYPE (TYPE_CD, TYPE_NAME) VALUES ('RF', 'Reference');
INSERT INTO C_SUB_SCOPE_TYPE (TYPE_CD, TYPE_NAME) VALUES ('FL', 'Full');

-- 16
ALTER TABLE C_STEP_RESULT
  ADD CONSTRAINT C_STEP_RESULT_STATUS_CD FOREIGN KEY (STATUS_CD) REFERENCES C_STEP_STATUS (STATUS_CD);
-- 15
ALTER TABLE C_STEP_RESULT
  ADD CONSTRAINT C_STEP_RESULT_C_STEP_CD FOREIGN KEY (STEP_CD) REFERENCES C_STEP_TYPE (TYPE_CD);
-- 14
ALTER TABLE C_SOLUTION_DEPLOYMENT 
  ADD CONSTRAINT C_SOL_DEP_C_DEP_STATUS FOREIGN KEY (DEP_STATUS_CD) REFERENCES C_DEPLOYMENT_STATUS (STATUS_CD);
-- 13
ALTER TABLE C_SOL_VAL_SEQ
  ADD CONSTRAINT C_SOL_VAL_SEQ_C_VAL_TYPE FOREIGN KEY (VAL_TYPE_CD) REFERENCES C_VALIDATION_TYPE (TYPE_CD);
-- 12
ALTER TABLE C_SOLUTION_VALIDATION
  ADD CONSTRAINT C_SOL_VAL_C_VAL_STATUS FOREIGN KEY (VAL_STATUS_CD) REFERENCES C_VALIDATION_STATUS (STATUS_CD);
-- 11
ALTER TABLE C_SOLUTION_VALIDATION
  ADD CONSTRAINT C_SOL_VAL_C_VAL_TYPE FOREIGN KEY (VAL_TYPE_CD) REFERENCES C_VALIDATION_TYPE (TYPE_CD);
-- 10
ALTER TABLE C_ARTIFACT
  ADD CONSTRAINT C_ARTIFACT_C_ARTIFACT_TYPE FOREIGN KEY (ARTIFACT_TYPE_CD) REFERENCES C_ARTIFACT_TYPE (TYPE_CD);
-- 9
ALTER TABLE C_SOLUTION
  ADD CONSTRAINT C_SOLUTION_C_VALIDATION_STATUS FOREIGN KEY (VALIDATION_STATUS_CD) REFERENCES C_VALIDATION_STATUS (STATUS_CD);
-- 8
ALTER TABLE C_SOLUTION
  ADD CONSTRAINT C_SOLUTION_C_TOOLKIT_TYPE FOREIGN KEY (TOOLKIT_TYPE_CD) REFERENCES C_TOOLKIT_TYPE (TYPE_CD);
-- 7
ALTER TABLE C_SOLUTION
  ADD CONSTRAINT C_SOLUTION_C_MODEL_TYPE FOREIGN KEY (MODEL_TYPE_CD) REFERENCES C_MODEL_TYPE (TYPE_CD);
-- 6
ALTER TABLE C_SOLUTION
  ADD CONSTRAINT C_SOLUTION_C_ACCESS_TYPE FOREIGN KEY (ACCESS_TYPE_CD) REFERENCES C_ACCESS_TYPE (TYPE_CD);
-- 5
ALTER TABLE C_USER_LOGIN_PROVIDER
  ADD CONSTRAINT C_USR_LOGIN_PROVIDER_C_LOGIN_PROVIDER FOREIGN KEY (PROVIDER_CD) REFERENCES C_LOGIN_PROVIDER (PROVIDER_CD);
-- 4
ALTER TABLE C_PEER_SUB
  ADD CONSTRAINT C_PEER_SUB_C_ACCESS FOREIGN KEY (ACCESS_TYPE) REFERENCES C_ACCESS_TYPE (TYPE_CD);
-- 3
ALTER TABLE C_PEER_SUB
  ADD CONSTRAINT C_PEER_SUB_C_SCOPE FOREIGN KEY (SCOPE_TYPE) REFERENCES C_SUB_SCOPE_TYPE (TYPE_CD);
-- 2
ALTER TABLE C_PEER
  ADD  CONSTRAINT C_PEER_C_VALIDATION_STATUS FOREIGN KEY (VALIDATION_STATUS_CD) REFERENCES C_VALIDATION_STATUS (STATUS_CD);
-- 1
ALTER TABLE C_PEER
  ADD CONSTRAINT C_PEER_C_PEER_STATUS FOREIGN KEY (STATUS_CD) REFERENCES C_PEER_STATUS (STATUS_CD);
