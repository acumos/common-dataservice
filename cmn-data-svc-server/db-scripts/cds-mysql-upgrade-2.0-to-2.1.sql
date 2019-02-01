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
-- FROM version 2.0 TO version 2.1.
-- This DISCARDS all existing step results.
-- No database name is set to allow flexible deployment.

CREATE TABLE C_RIGHT_TO_USE (
  RTU_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  LUM_ID CHAR(36) NOT NULL,
  SOLUTION_ID CHAR(36) NOT NULL,
  SITE_YN CHAR(1) NOT NULL DEFAULT 'N',
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_RTU_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE C_USER_RTU_MAP (
  RTU_ID INT NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  PRIMARY KEY (RTU_ID, USER_ID),
  CONSTRAINT C_USER_RTU_MAP_C_RTU FOREIGN KEY (RTU_ID) REFERENCES C_RIGHT_TO_USE (RTU_ID),
  CONSTRAINT C_USER_RTU_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
