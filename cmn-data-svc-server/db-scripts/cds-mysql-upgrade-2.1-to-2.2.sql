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
