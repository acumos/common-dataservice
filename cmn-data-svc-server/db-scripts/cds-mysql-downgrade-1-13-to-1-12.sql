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

-- Script to downgrade database used by the Common Data Service
-- FROM version 1.13.x TO version 1.12.x.
-- No database is created or specified to allow flexible deployment!

-- 34
DROP TABLE C_STEP_RESULT;
-- 33
ALTER TABLE C_SOLUTION_REV
  DROP FOREIGN KEY C_SOLUTION_REV_C_PEER,
  DROP COLUMN SOURCE_ID;
-- 32   
ALTER TABLE C_SOLUTION_REV
  DROP COLUMN ORIGIN;
-- 31
ALTER TABLE C_SOLUTION
  DROP COLUMN ORIGIN; 
-- 30
ALTER TABLE C_PEER_SUB
  DROP FOREIGN KEY C_PEER_SUB_C_ACCESS,
  DROP COLUMN ACCESS_TYPE;
-- 29
ALTER TABLE C_PEER_SUB
  DROP COLUMN PROCESSED_DATE;
-- 28
ALTER TABLE C_PEER_SUB
  DROP FOREIGN KEY C_PEER_SUB_C_SCOPE,
  DROP COLUMN SCOPE_TYPE;
-- 27
ALTER TABLE C_PEER
  DROP FOREIGN KEY C_PEER_C_VALIDATION_STATUS,
  DROP COLUMN VALIDATION_STATUS_CD;
-- 26
ALTER TABLE C_PEER
  DROP FOREIGN KEY C_PEER_C_PEER_STATUS,
  DROP COLUMN STATUS_CD;
-- 25
ALTER TABLE C_PEER
  DROP COLUMN IS_LOCAL;
-- 24
ALTER TABLE C_PEER 
  MODIFY COLUMN WEB_URL VARCHAR(512) NOT NULL;
-- 23
ALTER TABLE C_PEER 
  ADD COLUMN IS_ACTIVE CHAR(1) NOT NULL DEFAULT 'Y';
-- 22
ALTER TABLE C_PEER 
  ADD COLUMN TRUST_LEVEL SMALLINT NOT NULL DEFAULT 0;
-- 21
ALTER TABLE C_PEER 
  ADD COLUMN CONTACT2 VARCHAR(100) NOT NULL DEFAULT '';
-- 20..15
DROP TABLE C_PEER_STATUS;
-- 14..11
DROP TABLE C_STEP_STATUS;
-- 10..8
DROP TABLE C_SUB_SCOPE_TYPE;
-- 7..5
DROP TABLE C_STEP_TYPE;
-- 4
DELETE FROM C_TOOLKIT_TYPE WHERE TYPE_CD = 'BR';
-- 3
DELETE FROM C_TOOLKIT_TYPE WHERE TYPE_CD = 'TC';
-- 2
DELETE FROM C_TOOLKIT_TYPE WHERE TYPE_CD = 'PB';
-- 1
DELETE FROM C_ACCESS_TYPE WHERE TYPE_CD = 'RS';
